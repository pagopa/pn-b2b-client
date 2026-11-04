Feature: Elenco degli Enti PA che hanno delegato un Partner Tecnologico

  #################### PAGINAZIONE ####################

  # Precondizione: l'Ente PT "PT_A" ha più di 10 deleghe
  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_1] Il numero di Enti PA per pagina non è indicato.
  SEND restituisce al massimo 10 Enti, con il riferimento alla pagina successiva
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    ... ripetere per almeno 11 Enti PA diversi
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti
    Then la risposta contiene esattamente 10 Enti
    And la risposta contiene il riferimento alla pagina successiva

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_2] Il numero di Enti PA per pagina è al minimo ammesso (1).
  SEND restituisce un solo Ente PA, con il riferimento alla pagina successiva
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" crea una delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | limit | 1 |
    Then la risposta contiene esattamente 1 Enti
    And la risposta contiene il riferimento alla pagina successiva

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_3] Il numero di Enti PA per pagina è al massimo ammesso (50).
  SEND restituisce al massimo 50 Enti
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    ... ripetere per almeno 11 Enti PA diversi
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | limit | 50 |
    Then la risposta contiene al massimo 50 Enti

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_4] Il numero di Enti per pagina è intermedio (5).
  SEND restituisce al massimo 5 Enti per pagina
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    ... ripetere per almeno 11 Enti PA diversi
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | limit | 5 |
    Then la risposta contiene al massimo 5 Enti
    When l'Ente PT "PT_A" richiede la pagina successiva
    Then la risposta contiene al massimo 5 Enti

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_5] L'Ente PT legge tutte le pagine in sequenza.
  Ogni Ente compare una sola volta, senza duplicati né Enti mancanti
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    ... ripetere per almeno 11 Enti PA diversi
    When l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 3
    Then ogni Ente compare una sola volta in tutte le pagine lette

  # Precondizione: il numero di deleghe dell'Ente PT "PT_A" è pari a <numeroDeleghe> (placeholder da allineare ai dati di ambiente)
  @ptEntiDeleganti
  Scenario Outline: [PT-ENTI-DELEGANTI_6] Il numero di Enti PA dell'Ente PT è pari esattamente al numero di Enti per pagina.
  Ogni Ente compare una sola volta, senza duplicati né Enti mancanti
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    ... ripetere fino a <numeroDeleghe> Enti PA diversi
    When l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit <numeroDeleghe>
    Then sono stati letti in totale <numeroDeleghe> Enti
    And ogni Ente compare una sola volta in tutte le pagine lette
    Examples:
      | numeroDeleghe |
      | 11            |

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_7] L'Ente PT legge l'ultima pagina.
  SEND non restituisce alcun riferimento a una pagina successiva
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    ... ripetere per almeno 11 Enti PA diversi
    When l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 5
    Then la risposta non contiene il riferimento alla pagina successiva

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_8] L'Ente PT passa alla pagina successiva dopo un filtro per stato.
  I risultati sono coerenti tra le pagine e rispettano il filtro
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_Multi" revoca la delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | status | ACTIVE |
      | limit  | 1      |
    And l'Ente PT "PT_A" richiede la pagina successiva
    Then tutti gli Enti restituiti hanno stato delega "ACTIVE"
    And ogni Ente compare una sola volta in tutte le pagine lette

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_9] L'Ente PT passa alla pagina successiva dopo una ricerca.
  I risultati sono coerenti tra le pagine e rispettano la ricerca
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" crea una delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | search | Comune |
      | limit  | 1      |
    And l'Ente PT "PT_A" richiede la pagina successiva
    Then tutti gli Enti restituiti hanno ragione sociale contenente "Comune" senza distinzione tra maiuscole e minuscole
    And ogni Ente compare una sola volta in tutte le pagine lette

  #################### CDT 3.3 - VALORI NON VALIDI ####################

  @ptEntiDeleganti
  Scenario Outline: [PT-ENTI-DELEGANTI_10] L'Ente PT richiede l'elenco degli Enti deleganti usando un valore non valido (<caso>).
  Il servizio rifiuta la richiesta perché non valida
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri non validi:
      | <parametro> | <valore> |
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | caso                                     | parametro | valore         |
      | numero di Enti per pagina pari a 0       | limit     | 0              |
      | numero di Enti per pagina superiore a 50 | limit     | 51             |
      | numero di Enti per pagina non numerico   | limit     | abc            |
      | ragione sociale vuota                    | search    | @vuoto         |
      | ragione sociale più lunga di 250         | search    | @lunghezza_251 |
      | ragione sociale più corta di 3           | search    | ab             |
      | codice fiscale vuoto                     | search    | @vuoto         |
      | stato diverso da attiva e revocata       | status    | PENDING        |
      # non previsto dalla spec pn-pt-internal-v1: codice fiscale e ragione sociale condividono il parametro search (max 250)
#      | codice fiscale più lungo di 32           | search    | @lunghezza_33  |

  #################### CDT 3.4 - PT SENZA DELEGHE ####################

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_11] L'Ente PT senza alcuna delega richiede l'elenco degli Enti PA che lo hanno delegato.
  L'elenco è vuoto, senza errori
    When l'Ente PT "PT_SENZA_DELEGHE" richiede l'elenco degli Enti deleganti
    Then l'elenco degli Enti deleganti è vuoto
    And la risposta non contiene il riferimento alla pagina successiva

  #################### CDT 3.5 - ISOLAMENTO TRA PT ####################

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_12] La richiesta è effettuata dall'Ente PT A.
  Il servizio restituisce solo gli Enti PA dell'Ente PT A e non quelli dell'Ente PT B
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" crea una delega per l'Ente PT "PT_B"
    When l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 50
    Then nessuno degli Enti restituiti è presente nell'elenco dell'Ente PT "PT_B"

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_13] La richiesta non indica quale Ente PT la effettua.
  Il servizio rifiuta la richiesta perché non valida
    When viene richiesto l'elenco degli Enti deleganti senza indicare l'Ente PT
    Then l'operazione ha prodotto un errore con status code "400"

  #################### CDT 3.6 - FILTRI E RICERCHE ####################

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_14] Nessun filtro: l'Ente PT ha Enti con delega attiva e revocata.
  L'elenco comprende tutti gli Enti, ciascuno con ragione sociale, codice fiscale, identificativo della delega, stato e data
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" revoca la delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 50
    Then ogni Ente restituito ha valorizzati ragione sociale, codice fiscale, id delega, stato e data
    And l'elenco degli Enti deleganti contiene almeno un Ente con stato delega "ACTIVE"
    And l'elenco degli Enti deleganti contiene almeno un Ente con stato delega "REVOKED"

  # Il codice fiscale è un placeholder: va allineato al codice fiscale dell'Ente PA che ha modificato più volte la delega
  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_15] Nessun filtro: un Ente ha modificato più volte la delega.
  L'elenco comprende l'Ente PA una sola volta, con la situazione più recente
#    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_Multi" revoca la delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 50
    Then l'Ente con codice fiscale "PLACEHOLDER_CF_ENTE_MODIFICATO" compare una sola volta con stato "ACTIVE"

  # Il codice fiscale è un placeholder: va allineato al codice fiscale dell'Ente PA che ha revocato la delega
  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_16] Nessun filtro: un Ente ha revocato la delega.
  L'Ente PA compare nell'elenco con stato revocata e data della revoca
#    Given l'Ente PA "Comune_2" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" revoca la delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 50
    Then l'Ente con codice fiscale "PLACEHOLDER_CF_ENTE_REVOCATO" compare una sola volta con stato "REVOKED"

  @ptEntiDeleganti
  Scenario Outline: [PT-ENTI-DELEGANTI_17] Filtro per stato <stato>.
  L'elenco comprende solo gli Enti con delega nello stato indicato
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" revoca la delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | status | <stato> |
    Then tutti gli Enti restituiti hanno stato delega "<stato>"
    Examples:
      | stato   |
      | ACTIVE  |
      | REVOKED |

  @ptEntiDeleganti
  Scenario Outline: [PT-ENTI-DELEGANTI_18] Ricerca per ragione sociale (<caso>).
  L'elenco comprende gli Enti la cui ragione sociale contiene il testo cercato, senza distinguere maiuscole e minuscole
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti
    And si memorizzano ragione sociale e codice fiscale del primo Ente restituito
    And l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | search | <ricerca> |
    Then tutti gli Enti restituiti hanno ragione sociale contenente "<ricerca>" senza distinzione tra maiuscole e minuscole
    Examples:
      | caso                                         | ricerca                             |
      | nome completo                                | @ragioneSocialeSalvata              |
      | parte del nome                               | @parteRagioneSocialeSalvata         |
      | maiuscole e minuscole diverse dall'originale | @ragioneSocialeSalvataCaseInvertito |

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_19] Ricerca per codice fiscale di un Ente delegante.
  L'elenco comprende solo l'Ente PA con quel codice fiscale
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti
    And si memorizzano ragione sociale e codice fiscale del primo Ente restituito
    And l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | search | @codiceFiscaleSalvato |
    Then la risposta contiene solo l'Ente con codice fiscale "@codiceFiscaleSalvato"

  # Precondizione: l'Ente PT "PT_A" ha solo deleghe attive
  @ptEntiDeleganti
  Scenario Outline: [PT-ENTI-DELEGANTI_20] Filtro o ricerca senza risultati (<caso>).
  L'elenco è vuoto, senza errori
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | <parametro> | <valore> |
    Then l'elenco degli Enti deleganti è vuoto
    Examples:
      | caso                                               | parametro | valore           |
      | filtro per uno stato senza Enti corrispondenti     | status    | REVOKED          |
      | ragione sociale senza risultati                    | search    | zzzNonEsiste     |
      | codice fiscale non presente tra gli Enti deleganti | search    | 99999999999      |

  @ptEntiDeleganti
  Scenario: [PT-ENTI-DELEGANTI_21] Ricerca per ragione sociale combinata con il filtro per stato.
  L'elenco comprende solo gli Enti che soddisfano entrambe le condizioni
#    Given l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" crea una delega per l'Ente PT "PT_A"
#    And l'Ente PA "Comune_2" revoca la delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | search | Comune |
      | status | ACTIVE |
    Then tutti gli Enti restituiti hanno ragione sociale contenente "Comune" senza distinzione tra maiuscole e minuscole
    And tutti gli Enti restituiti hanno stato delega "ACTIVE"
