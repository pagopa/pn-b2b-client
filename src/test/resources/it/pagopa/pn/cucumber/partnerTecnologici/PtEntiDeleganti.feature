Feature: Elenco degli Enti PA che hanno delegato un Partner Tecnologico

  #################### CDT 1.1 - PERCORSO DI UNA DELEGA ####################

  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_1.1_B] Un nuovo Ente PA conferisce la delega a un Ente PT che ha già altri Enti PA deleganti.
  L'elenco degli Enti deleganti dell'Ente PT comprende il nuovo Ente PA con delega ACTIVE e continua a mostrare, invariati, gli Enti PA già presenti
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    And l'Ente PA "Comune_1" non ha una delega attiva per l'Ente PT "PT_A"
    When l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
    And l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 10
    Then l'Ente PA "Comune_1" compare una sola volta con stato "ACTIVE" e l'identificativo della delega creata
    And l'Ente PA "Comune_Multi" compare una sola volta con stato "ACTIVE" e l'identificativo della delega creata

  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_1.1_D] L'Ente PA revoca la delega conferita all'Ente PT.
  Nell'elenco degli Enti deleganti l'Ente PA compare una sola volta con delega REVOKED e non compare più tra gli Enti con delega ACTIVE
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    When l'Ente PA "Comune_Multi" revoca la delega per l'Ente PT "PT_A"
    And l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 50
    Then l'Ente PA "Comune_Multi" compare una sola volta con stato "REVOKED" e l'identificativo della delega creata
    And l'Ente PA "Comune_Multi" non compare tra gli Enti con delega "ACTIVE" dell'Ente PT "PT_A"

  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_1.1_E] L'Ente PA, dopo aver revocato la delega, conferisce una nuova delega allo stesso Ente PT.
  Nell'elenco degli Enti deleganti l'Ente PA compare una sola volta, con l'identificativo e lo stato ACTIVE della nuova delega al posto di quella revocata
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    And l'Ente PA "Comune_Multi" revoca la delega per l'Ente PT "PT_A"
    And l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 50
    And l'Ente PA "Comune_Multi" compare una sola volta con stato "REVOKED" e l'identificativo della delega creata
    When l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    And l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 50
    Then l'Ente PA "Comune_Multi" compare una sola volta con stato "ACTIVE" e l'identificativo della delega creata

  #################### CDT 1.2 - ENTE PA NON ADERENTE ####################

  # Precondizione: "Comune di Castagnero" esiste su SelfCare ma non ha aderito a SEND
  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_1.2] Un Ente PA presente su SelfCare ma non aderente a SEND conferisce la delega all'Ente PT.
  La delega viene scartata: anche dopo l'attesa della propagazione, l'Ente PA non compare nell'elenco degli Enti deleganti dell'Ente PT
    When l'Ente PA "Comune di Castagnero" conferisce la delega all'Ente PT "PT_A"
    Then dopo 1 minuti l'Ente PA "Comune di Castagnero" non compare nell'elenco degli Enti deleganti dell'Ente PT "PT_A"

  #################### CDT 3.3 - VALORI NON VALIDI ####################

  @ptEntiDeleganti @work-item:QA-18530
  Scenario Outline: [PT-ENTI-DELEGANTI_10] L'Ente PT richiede l'elenco degli Enti deleganti passando un parametro non valido (<caso>).
  Il servizio rifiuta la richiesta con errore 400 Bad Request
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
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

  #################### CDT 3.4 - PT SENZA DELEGHE ####################

  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_11] Un Ente PT che non ha ricevuto deleghe da alcun Ente PA richiede l'elenco degli Enti deleganti.
  Il servizio risponde con successo restituendo un elenco vuoto, senza riferimento alla pagina successiva
    When l'Ente PT "PT_SENZA_DELEGHE" richiede l'elenco degli Enti deleganti
    Then l'elenco degli Enti deleganti è vuoto
    And la risposta non contiene il riferimento alla pagina successiva

  #################### CDT 3.5 - ISOLAMENTO TRA PT ####################

  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_12] L'Ente PT A richiede l'elenco dei propri Enti deleganti, mentre altri Enti PA hanno delegato l'Ente PT B.
  Il servizio restituisce solo gli Enti PA che hanno delegato l'Ente PT A e nessuno di quelli che hanno delegato l'Ente PT B
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    And l'Ente PA "Comune_2" crea una delega per l'Ente PT "PT_B"
    When l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 50
    Then nessuno degli Enti restituiti è presente nell'elenco dell'Ente PT "PT_B"

  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_13] La richiesta dell'elenco degli Enti deleganti non indica quale Ente PT la effettua.
  Il servizio rifiuta la richiesta con errore 400 Bad Request
    When viene richiesto l'elenco degli Enti deleganti senza indicare l'Ente PT
    Then l'operazione ha prodotto un errore con status code "400"

  #################### CDT 3.6 - FILTRI E RICERCHE ####################

  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_14] L'Ente PT, delegato da Enti PA con delega sia attiva sia revocata, richiede l'elenco senza filtri.
  L'elenco comprende sia gli Enti con delega ACTIVE sia quelli con delega REVOKED, ciascuno con ragione sociale, codice fiscale, identificativo della delega, stato e data valorizzati
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    And l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
    And l'Ente PA "Comune_1" revoca la delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" legge tutte le pagine dell'elenco degli Enti deleganti con limit 50
    Then ogni Ente restituito ha valorizzati ragione sociale, codice fiscale, id delega, stato e data
    And l'elenco degli Enti deleganti contiene almeno un Ente con stato delega "ACTIVE"
    And l'elenco degli Enti deleganti contiene almeno un Ente con stato delega "REVOKED"

  @ptEntiDeleganti @work-item:QA-18530
  Scenario Outline: [PT-ENTI-DELEGANTI_17] L'Ente PT richiede l'elenco degli Enti deleganti filtrando per stato della delega <stato>.
  L'elenco comprende solo gli Enti PA la cui delega è nello stato <stato>, escludendo quelli nell'altro stato
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    And l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
    And l'Ente PA "Comune_1" revoca la delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | status | <stato> |
    Then tutti gli Enti restituiti hanno stato delega "<stato>"
    Examples:
      | stato   |
      | ACTIVE  |
      | REVOKED |

  @ptEntiDeleganti @work-item:QA-18530
  Scenario Outline: [PT-ENTI-DELEGANTI_18] L'Ente PT cerca tra i propri Enti deleganti per ragione sociale (<caso>).
  L'elenco comprende solo gli Enti PA la cui ragione sociale contiene il testo cercato, senza distinzione tra maiuscole e minuscole
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
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

  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_19] L'Ente PT cerca tra i propri Enti deleganti per codice fiscale di un Ente PA delegante.
  L'elenco comprende solo l'Ente PA con il codice fiscale cercato
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti
    And si memorizzano ragione sociale e codice fiscale del primo Ente restituito
    And l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | search | @codiceFiscaleSalvato |
    Then la risposta contiene solo l'Ente con codice fiscale "@codiceFiscaleSalvato"

  @ptEntiDeleganti @work-item:QA-18530
  Scenario Outline: [PT-ENTI-DELEGANTI_20] L'Ente PT effettua una ricerca che non corrisponde a nessuno dei propri Enti deleganti (<caso>).
  Il servizio risponde con successo restituendo un elenco vuoto
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | <parametro> | <valore> |
    Then l'elenco degli Enti deleganti è vuoto
    Examples:
      | caso                                               | parametro | valore           |
      | ragione sociale senza risultati                    | search    | zzzNonEsiste     |
      | codice fiscale non presente tra gli Enti deleganti | search    | 99999999999      |

  @ptEntiDeleganti @work-item:QA-18530
  Scenario: [PT-ENTI-DELEGANTI_21] L'Ente PT combina la ricerca per ragione sociale con il filtro per stato della delega ACTIVE.
  L'elenco comprende solo gli Enti PA la cui ragione sociale contiene il testo cercato e la cui delega è ACTIVE
    Given l'Ente PA "Comune_Multi" crea una delega per l'Ente PT "PT_A"
    And l'Ente PA "Comune_1" crea una delega per l'Ente PT "PT_A"
    And l'Ente PA "Comune_1" revoca la delega per l'Ente PT "PT_A"
    When l'Ente PT "PT_A" richiede l'elenco degli Enti deleganti con i seguenti parametri:
      | search | Comune |
      | status | ACTIVE |
    Then tutti gli Enti restituiti hanno ragione sociale contenente "Comune" senza distinzione tra maiuscole e minuscole
    And tutti gli Enti restituiti hanno stato delega "ACTIVE"
