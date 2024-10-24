Feature: test per la visualizzazione della copia conforme



  #@copiaConformeCon020
  Scenario: [COPIA-CONFORME-CON020-1] Creazione notifica analogica mono destinatario AR di una pagina da 7ZIP verso PF con SEND_ANALOG_PROGRESS per PF e PA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via @OK_AR-CON020-7Z1P |
    Then si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"


  #@copiaConformeCon020
  Scenario: [COPIA-CONFORME-CON020-2] Creazione notifica 890 analogica mono destinatario verso PF con SEND_ANALOG_PROGRESS per PA e PF
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL               |
      | physicalAddress_address | Via @OK_890-CON020 |
    Then si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"


   #@copiaConformeCon020
  Scenario: [COPIA-CONFORME-CON020-3] Creazione notifica RS analogica mono destinatario verso PF con SEND_SIMPLE_REGISTERED_LETTER_PROGRESS per PA e PF
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it      |
      | physicalAddress_address | Via @OK_RS-CON020 |
    Then si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"



       #@copiaConformeCon020
  Scenario Outline: [COPIA-CONFORME-CON020-PAG] Creazione notifica analogica mono destinatario AR verso PF e download del pdf di n pagine
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL               |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And ricerca ed effettua download del legalFact con la categoria "SEND_ANALOG_PROGRESS" con DetailCode "CON020"
    #And la PA richiede il download dell'attestazione opponibile "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020"
    And verifica che il file contenga massimo 2 pagine
    Examples:
      | SEQUENCE              |
      #|via @OK_AR-CON020-7Z1P |
      |via @OK_AR-CON020-ZIP1P|
      #|via @OK_AR-CON020-7Z2P |
      #|via @OK_AR-CON020-ZIP2P|
      #|via @OK_AR-CON020-7Z3P |
      #|via @OK_AR-CON020-ZIP3P|




       #@copiaConformeCon020
  Scenario: [COPIA-CONFORME-CON020-TEST-MULTI] Creazione notifica RS analogica da ZIP mono destinatario verso PF con SEND_SIMPLE_REGISTERED_LETTER_PROGRESS per PA e PF
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario GherkinSrl e:
      | digitalDomicile         | NULL               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL               |
      | physicalAddress_address | Via @OK_AR-CON020-ZIP3P |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON020" per l'utente 1























      |

