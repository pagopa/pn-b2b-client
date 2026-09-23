# PST: PST Disponibilita documenti (versione allineata all'SRS del 21/09/2026)
#
# NOTA: questa feature sostituisce la versione precedente, scritta per una versione dell'SRS
# in cui una data di fine disponibilita gia trascorsa veniva accettata (200) e produceva un
# 410 solo in lettura. Nella versione attuale dell'SRS quella data viene invece rifiutata
# (400) al momento dell'impostazione: gli scenari sono stati riscritti di conseguenza.
#
# NOTA SUL CAMPO: il campo "availableUntil" (WI 1 - PN-21557) non era ancora presente nel
# client SafeStorage generato al momento in cui questa feature e' stata scritta (il Work Item
# risultava "Ready To DEV"). Gli step Java che la usano assumono che, una volta rilasciato,
# esponga un builder .availableUntil(OffsetDateTime) su UpdateFileMetadataRequest, analogo a
# quello gia' esistente per retentionUntil: da verificare e correggere quando pn-ss pubblica
# il campo nello spec pn-safestorage-v1.1-api.yaml e il client viene rigenerato.
Feature: Disponibilita dei documenti SafeStorage

  # Perimetro automatico definito dalla PST: casi 1.1, 4.1, 4.2, 5.1, 5.2 e 5.3.
  # Cessazione alla fine del giorno indicato, reversibilita, applicazione uniforme a piu
  # chiamanti, link di download gia rilasciati e recupero da archiviazione a freddo restano
  # verifiche manuali (richiedono di attraversare il cambio di giornata, o non sono
  # automatizzabili con i soli dati predisposti dal test) e non sono duplicati in questa feature.
  #
  # NOTA SUI CASI 4.1 e 4.2: la risposta pubblica di lettura riporta un'unica data di scadenza
  # (la fine disponibilita, quando impostata, altrimenti la conservazione garantita - caso
  # 5.3). Una volta impostata una fine disponibilita, questa feature non ha modo di leggere la
  # conservazione garantita "vera" separatamente da essa: l'unica verifica automatizzabile e'
  # che la data restituita coincida con la fine disponibilita appena impostata. Che la
  # conservazione registrata internamente sia stata davvero allungata (o non anticipata) andra
  # confermato con un controllo separato (es. sullo storage), come gia annotato nella PST.

  @e2e @documentAvailability
  Scenario Outline: [SS-DOCUMENT-AVAILABILITY-1.1] Accettazione di una fine disponibilita non ancora trascorsa
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento 1 viene impostata a una data "<combinazioneData>"
    Then l'impostazione della fine disponibilita restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200

    Examples:
      | combinazioneData  |
      | DOMANI             |
      | PRECEDENTE_FUTURA  |

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-1.1] Sostituzione di una fine disponibilita gia indicata
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento 1 viene impostata a una data "DOMANI"
    Then l'impostazione della fine disponibilita restituisce status code 200
    When la fine disponibilita del documento 1 viene impostata a una data "DOPODOMANI"
    Then l'impostazione della fine disponibilita restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200
    And la data di scadenza riportata coincide con la fine disponibilita indicata

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-1.1] Rifiuto di una fine disponibilita gia trascorsa
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento 1 viene impostata a una data "IERI"
    Then l'impostazione della fine disponibilita restituisce status code 400
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200

  @e2e @documentAvailability
  Scenario Outline: [SS-DOCUMENT-AVAILABILITY-4.1] Rapporto tra fine disponibilita e conservazione garantita
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento 1 viene impostata a una data "<combinazioneData>"
    Then l'impostazione della fine disponibilita restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200
    And la data di scadenza riportata coincide con la fine disponibilita indicata

    Examples:
      | combinazioneData  |
      | SUCCESSIVA         |
      | PRECEDENTE_FUTURA  |
      | PARI               |

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-4.2] La conservazione non viene mai anticipata
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento 1 viene impostata a una data "SUCCESSIVA"
    Then l'impostazione della fine disponibilita restituisce status code 200
    When la fine disponibilita del documento 1 viene impostata a una data "PRECEDENTE_FUTURA"
    Then l'impostazione della fine disponibilita restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200
    And la data di scadenza riportata coincide con la fine disponibilita indicata

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.1] Posticipo della conservazione senza indicare la fine disponibilita
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la retention del documento 1 viene aggiornata con una data "DOMANI"
    Then l'aggiornamento della retention restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200
    And la data di scadenza riportata coincide con la conservazione indicata

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.1] Rifiuto del tentativo di anticipo della conservazione
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la retention del documento 1 viene aggiornata con una data "IERI"
    Then l'aggiornamento della retention restituisce status code 400
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.2] Documento senza fine disponibilita impostata
    # Equivalente funzionale di un documento gia in archivio prima della funzionalità: un
    # documento su cui la fine disponibilita non e' mai stata impostata percorre lo stesso
    # ramo di codice (indicatore assente).
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.3] Data di scadenza riportata quando la fine disponibilita e impostata
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento 1 viene impostata a una data "DOMANI"
    Then l'impostazione della fine disponibilita restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200
    And la data di scadenza riportata coincide con la fine disponibilita indicata

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.3] Data di scadenza riportata quando la fine disponibilita non e impostata
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    And si registra la conservazione garantita corrente del documento 1
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200
    And la data di scadenza riportata coincide con la conservazione garantita del documento
