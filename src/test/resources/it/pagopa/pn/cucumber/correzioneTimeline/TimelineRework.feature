Feature: Test relativi al SRS di correzione timeline

  @timelineRework
  Scenario: [TIMELINE_REWORK_1] Viene creata correttamente una richiesta di rework.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And si verifica che in fase di rework non ci sono richieste appese in stato diverso da DONE o ERROR
    Then viene invocata una richiesta di rework per la notifica appena creata
    And si verifica che la richiesta di rework effettuata sia in stato "CREATED"

  @timelineRework
  Scenario Outline: [TIMELINE_REWORK_2] Viene invocata con errori la chiamata di rework
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun    | attemptId    | pcRetry   | recIndex  | expectedStatusCode  | expectedDeliveryFailureCause  | reason   |
      | <iun>  | <attemptId>  |<pcRetry>  |<recIndex> |<expectedStatusCode> |<expectedDeliveryFailureCause> | <reason> |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 400
    Examples: :
      | iun                                | attemptId             | pcRetry         | recIndex         | expectedStatusCode | expectedDeliveryFailureCause  | reason         |
      | IUN-NOT_VALID                      | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | reason         |
      | ZJUJ-AAAA-AAAA-202512-X-1          | EMPTY_STRING          | PCRETRY_0       | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | reason         |
      | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | EMPTY_STRING    | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | reason         |
      | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | RETRY_0         | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | reason         |
      | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | INDEX_0          | RECRI003C          | EMPTY_STRING                  | reason         |
      | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | EMPTY_STRING       | EMPTY_STRING                  | reason         |
      | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | RECRI003C          | EMPTY_STRING                  | EMPTY_STRING   |

  @timelineRework
  Scenario: [TIMELINE_REWORK_3] Viene verificata che la richiesta di rework viene rifiutata quando è associata ad uno IUN non valido
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun    |
      | ZJUJ-AAAA-AAAA-202512-X-1    |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 404


  @timelineRework
  Scenario: [TIMELINE_REWORK_4] Verifica che non sia possibile creare una nuova richiesta di rework quando ne esiste già una associata alla stessa notifica in stato diverso da DONE o ERROR
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER     |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | Via@OK_AR |
      | digitalDomicile         | NULL              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then viene invocata una richiesta di rework per la notifica appena creata
    Then viene invocata una richiesta di rework per la notifica appena creata
    And si verifica che la chiamata sia andata in errore con il seguente status code: 409

  @timelineRework
  Scenario Outline: [TIMELINE_REWORK_5] Viene invocata l'API di rework passando degli statusCode non validi e si ci aspetta uno status code di tipo 404
    Then viene invocata una richiesta di rework per la notifica appena creata con i seguenti parametri:
      | iun    | attemptId    | pcRetry   | recIndex  | expectedStatusCode  | expectedDeliveryFailureCause  | reason   |
      | <iun>  | <attemptId>  |<pcRetry>  |<recIndex> |<expectedStatusCode> |<expectedDeliveryFailureCause> | <reason> |
    And si verifica che la chiamata sia andata in errore con il seguente status code: 404
    Examples: :
  | iun                                | attemptId             | pcRetry         | recIndex         | expectedStatusCode | expectedDeliveryFailureCause  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | NOT_VALID          | EMPTY_STRING                  | reason         |
  | ZJUJ-AAAA-AAAA-202512-X-1          | ATTEMPT_0             | PCRETRY_0       | RECINDEX_0       | RECRI003C          | NOT_VALID                     | reason         |
