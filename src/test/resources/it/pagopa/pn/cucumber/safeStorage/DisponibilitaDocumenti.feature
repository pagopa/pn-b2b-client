# PST: PST Disponibilita documenti
Feature: Disponibilita dei documenti SafeStorage

  # Una data di fine disponibilita gia trascorsa viene sempre rifiutata dall'API (vedi
  # scenario 1.1): non e' quindi possibile ottenere per via automatica un documento la cui
  # fine disponibilita sia gia scaduta, se non attraversando il cambio di giornata. Per
  # questo cessazione alla fine del giorno indicato, reversibilita, applicazione uniforme a
  # piu chiamanti, link di download gia rilasciati e recupero da archiviazione a freddo
  # restano verifiche manuali, e non sono duplicati in questa feature.
  #
  # La risposta pubblica di lettura riporta un'unica data di scadenza: la fine disponibilita
  # quando impostata, altrimenti la conservazione garantita. Non e' quindi possibile
  # osservare le due date separatamente una volta che la fine disponibilita e' impostata: le
  # verifiche che la coinvolgono si limitano a confrontare la data restituita con quella
  # appena impostata, normalizzata dal servizio alla fine del giorno indicato (ora italiana).
  #
  # La conservazione puo' essere solo posticipata: gli aggiornamenti di retention attesi con
  # successo usano quindi una data successiva alla conservazione garantita corrente.

  @e2e @documentAvailability
  Scenario Outline: [SS-DOCUMENT-AVAILABILITY-1.1.1] Accettazione di una fine disponibilita non ancora trascorsa
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento viene impostata a una data "<combinazioneData>"
    Then l'impostazione della fine disponibilita viene completata con successo
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile

    Examples:
      | combinazioneData  |
      | DOMANI             |
      | PRECEDENTE_FUTURA  |

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-1.1.2] Sostituzione di una fine disponibilita gia indicata
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento viene impostata a una data "DOMANI"
    Then l'impostazione della fine disponibilita viene completata con successo
    When la fine disponibilita del documento viene impostata a una data "DOPODOMANI"
    Then l'impostazione della fine disponibilita viene completata con successo
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile
    And la data di scadenza riportata coincide con la fine disponibilita indicata

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-1.1.3] Rifiuto di una fine disponibilita gia trascorsa
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento viene impostata a una data "IERI"
    Then l'impostazione della fine disponibilita viene rifiutata
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile

  @e2e @documentAvailability
  Scenario Outline: [SS-DOCUMENT-AVAILABILITY-4.1] Rapporto tra fine disponibilita e conservazione garantita
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento viene impostata a una data "<combinazioneData>"
    Then l'impostazione della fine disponibilita viene completata con successo
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile
    And la data di scadenza riportata coincide con la fine disponibilita indicata

    Examples:
      | combinazioneData  |
      | SUCCESSIVA         |
      | PRECEDENTE_FUTURA  |
      | PARI               |

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-4.2] La conservazione non viene mai anticipata
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento viene impostata a una data "SUCCESSIVA"
    Then l'impostazione della fine disponibilita viene completata con successo
    When la fine disponibilita del documento viene impostata a una data "PRECEDENTE_FUTURA"
    Then l'impostazione della fine disponibilita viene completata con successo
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile
    And la data di scadenza riportata coincide con la fine disponibilita indicata

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.1.1] Posticipo della conservazione senza indicare la fine disponibilita
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la retention del documento viene aggiornata con una data "SUCCESSIVA"
    Then l'aggiornamento della retention viene completato con successo
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile
    And la data di scadenza riportata coincide con la conservazione indicata

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.1.2] Rifiuto del tentativo di anticipo della conservazione
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la retention del documento viene aggiornata con una data "IERI"
    Then l'aggiornamento della retention viene rifiutato
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.2] Documento senza fine disponibilita impostata
    # Equivalente funzionale di un documento gia in archivio prima della funzionalità: un
    # documento su cui la fine disponibilita non e' mai stata impostata percorre lo stesso
    # ramo di codice (indicatore assente).
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.3.1] Data di scadenza riportata quando la fine disponibilita e impostata
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la fine disponibilita del documento viene impostata a una data "DOMANI"
    Then l'impostazione della fine disponibilita viene completata con successo
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile
    And la data di scadenza riportata coincide con la fine disponibilita indicata

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.3.2] Data di scadenza riportata quando la fine disponibilita non e impostata
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    And si registra la conservazione garantita corrente del documento
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile
    And la data di scadenza riportata coincide con la conservazione garantita del documento

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-5.4] Posticipo della sola conservazione non modifica una fine disponibilita non ancora trascorsa
    # Copre solo il caso con fine disponibilita non ancora trascorsa: l'altra combinazione
    # (fine disponibilita gia' trascorsa) richiederebbe di impostare direttamente una data
    # passata, che l'API rifiuta sempre, e resta quindi una verifica manuale.
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    And si registra la conservazione garantita corrente del documento
    When la fine disponibilita del documento viene impostata a una data "DOMANI"
    Then l'impostazione della fine disponibilita viene completata con successo
    When la retention del documento viene aggiornata con una data "SUCCESSIVA"
    Then l'aggiornamento della retention viene completato con successo
    When viene richiesto il contenuto del documento
    Then il documento risulta ancora scaricabile
    And la data di scadenza riportata coincide con la fine disponibilita indicata
