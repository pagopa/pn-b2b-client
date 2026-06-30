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

