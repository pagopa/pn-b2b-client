Feature: Suite di test per verificare la compatibilità e la corretta gestione delle notifiche inviate con versioni precedenti all'ultima implementata.

  @version
  Scenario Outline: [SEND_NEW_NOTIFICATION_OLD_VERSION] Invio e lettura notifica con una versione precedente all'ultima implementata
    Given viene generata una nuova notifica con la versione "<version>"
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi e verifico che l'utente 1 non abbia associato un evento "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" con la versione "<version>"
    Examples:
      | version |
      | V23     |
      | V24     |
      | V25     |

  @version
  Scenario Outline: [SEND_NEW_NOTIFICATION_CROSS_VERSION] Invio notifica con una versione e lettura con una versione diversa
    Given viene generata una nuova notifica con la versione "<createVersion>"
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Cucumber Society
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi e verifico che l'utente 1 non abbia associato un evento "ANALOG_SUCCESS_WORKFLOW"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" con la versione "<readVersion>"
    Examples:
      | createVersion | readVersion |
      | V23           | V24         |
      | V24           | V25         |
      | V25           | V23         |
