Feature: connettore app IO per invio messaggi di cortesia per comunicazioni bonarie

  #----- SCENARIO 3 -------------------------------------

  @comunicazione-orchestratore-io
  Scenario: [IO_CONNECTOR_3.1.1] Richiesta valida presa in carico messaggio di cortesia verso app IO
    Given viene generata una richiesta valida per la presa in carico del messaggio
    When come orchestratore SEND richiedo l'invio del messaggio verso IO
    Then verifico che si ottenga una response di "OK"
    Then verifico che la risposta contenga tutti i campi obbligatori valorizzati
    Then verifico che in tabella pn-IOConnectorRequests esista un record per requestId

  @comunicazione-orchestratore-io
  Scenario: [IO_CONNECTOR_3.1.2] Richiesta valida presa in carico inviata due volte
    Given viene generata una richiesta valida per la presa in carico del messaggio
    When come orchestratore SEND richiedo l'invio del messaggio verso IO
    Then verifico che si ottenga una response di "OK"
    When come orchestratore SEND richiedo l'invio del messaggio verso IO
    Then verifico che si ottenga una response di "NO CONTENT"

  @comunicazione-orchestratore-io
  Scenario: [IO_CONNECTOR_3.1.3] Richiesta valida presa in carico inviata nuovamente con body diverso
    Given viene generata una richiesta valida per la presa in carico del messaggio
    When come orchestratore SEND richiedo l'invio del messaggio verso IO
    Then verifico che si ottenga una response di "OK"
    When modifico il payload della request ma non il request Id
    When come orchestratore SEND richiedo l'invio del messaggio verso IO
    Then verifico che si ottenga una response di "CONFLICT"

  @comunicazione-orchestratore-io
  Scenario Outline: [IO_CONNECTOR_3.1.4] Richiesta non valida presa in carico
    Given viene generata una richiesta valida per la presa in carico del messaggio
    And sostituisco un valore non valido nel campo "<field>"
    When come orchestratore SEND richiedo l'invio del messaggio verso IO
    Then verifico che si ottenga una response di "BAD REQUEST"
    Examples:
      | field          |
      | requestId      |
      | recipientTaxId |
      | subject        |
      | markdown       |
      | TUTTI          |

  @comunicazione-orchestratore-io @eventbridge
  Scenario: [IO_CONNECTOR_3.1.5] Errore di validazione a valle lato IO e pubblicazione evento su EventBridge
    Given viene generata una richiesta per la presa in carico con markdown che eccede la lunghezza massima consentita
    When come orchestratore SEND richiedo l'invio del messaggio verso IO
    Then verifico che si ottenga una response di "OK"
    And verifico che su DynamoDB la richiesta evolva nello stato "FAILED"
    And verifico la presenza nei log di "/aws/ecs/pn-io-connector" negli ultimi 2 minuti dell'evento EventBridge con causale "SEND_FAILED"

  @comunicazione-orchestratore-io @multi-service
  Scenario: [IO_CONNECTOR_3.1.6] Invio messaggio con nuovo serviceId censito nel secret e presenza di allegati
    Given viene generata una richiesta valida con senderServiceId: "02NEWSERVICEIDBONARIE0001"
    And alla richiesta viene associato un allegato PDF valido
    When come orchestratore SEND richiedo l'invio del messaggio verso IO
    Then verifico che si ottenga una response di "OK"
    And verifico che in tabella pn-IOConnectorRequests esista un record per requestId con il senderServiceId "02NEWSERVICEIDBONARIE0001"

  @comunicazione-orchestratore-io @multi-service
  Scenario: [IO_CONNECTOR_3.1.7] Rifiuto invio messaggio con serviceId non censito nel secret
    Given viene generata una richiesta valida con senderServiceId: "SERVICE_NOT_CONFIGURED"
    When come orchestratore SEND richiedo l'invio del messaggio verso IO
    Then verifico che si ottenga una response di "BAD REQUEST"



  #----- SCENARIO 4 --------------------------------------

  @comunicazione-orchestratore-io
  Scenario: [IO_CONNECTOR_4.1.1] Verifica raggiungibilità profilo IO da orchestratore con utente censito su IO
    Given come orchestratore SEND tento la verifica raggiungibilità profilo con senderServiceId valido e CF destinatario: "PF-b7e52cf2-95d4-4dfc-ad47-5d6f7073d6e2"
    Then verifico che si ottenga una response di "OK"
    Then verifico che la response contenga l'informazione sulla raggiungibilità del profilo

  @comunicazione-orchestratore-io
  Scenario Outline: [IO_CONNECTOR_4.1.2] Verifica raggiungibilità profilo IO da orchestratore con request malformata
    Given come orchestratore SEND tento la verifica raggiungibilità profilo con senderServiceId: "<senderServiceId>" e recipientTaxId: "<recipientTaxId>"
    Then verifico che si ottenga una response di "BAD REQUEST"
    Examples:
      | senderServiceId | recipientTaxId |
      | $NULL           | $NULL          |
      | $EMPTY          | $NULL          |
      | $NULL           | $EMPTY         |
      | $EMPTY          | $EMPTY         |



  #----- SCENARIO 5 --------------------------------

  @comunicazione-orchestratore-io
  Scenario: [IO_CONNECTOR_5.1.1] Recupero dettagli messaggio di cortesia per comunicazione bonaria da app IO OK
    Given come app IO tento il recupero dettagli messaggio con requestID valido e CF destinatario: "PF-b7e52cf2-95d4-4dfc-ad47-5d6f7073d6e2"
    Then verifico che si ottenga una response di "OK"
    Then verifico che la lista dettagli allegati sia non vuota

  @comunicazione-orchestratore-io
  Scenario: [IO_CONNECTOR_5.1.2] Recupero dettagli messaggio di cortesia da app IO con CF destinatario errato
    Given come app IO tento il recupero dettagli messaggio con requestID valido e CF destinatario: "non-valid-taxId"
    Then verifico che si ottenga una response di "NOT FOUND"

  @comunicazione-orchestratore-io
  Scenario Outline: [IO_CONNECTOR_5.1.3] Recupero dettagli messaggio di cortesia da app IO con request malformata
    Given come app IO tento il recupero dettagli messaggio con requestID: "<requestId>" e CF destinatario: "<recipientTaxId>"
    Then verifico che si ottenga una response di "BAD REQUEST"
    Examples:
      | requestId                           | recipientTaxId                          |
      | $NULL                               | $NULL                                   |
      | $NULL                               | PF-36b40f4c-f792-40ca-9c52-a94a32b5fc28 |
      | TEST-POLLING_REQ-20260603-PAYMENT_2 | $NULL                                   |
      | $NULL                               | $EMPTY                                  |
      | $EMPTY                              | $NULL                                   |

  @comunicazione-orchestratore-io @multi-service @attachments
  Scenario: [IO_CONNECTOR_5.1.4] Recupero dettagli e apertura allegato da app IO per messaggio con nuovo serviceId
    Given come app IO tento il recupero dettagli del messaggio inviato con nuovo serviceId e CF destinatario: "PF-ef4f3181-c2a9-4924-9307-d107af8f0c34"
    Then verifico che si ottenga una response di "OK"
    And verifico che la lista dettagli allegati sia non vuota
    And verifico che il link dell'allegato permetta il download del documento PDF


