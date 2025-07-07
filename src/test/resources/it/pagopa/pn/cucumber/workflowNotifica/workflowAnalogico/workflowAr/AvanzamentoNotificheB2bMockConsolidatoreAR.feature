Feature: avanzamento b2b notifica controllo timestamp mock da consolidatore AR



  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_9] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano diversi tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    Then verifica che i DeliveryDetailCode "RECRN001A" "RECRN001B" "RECRN001C" abbiano timestamp "diversi"

    Examples:
      | SEQUENCE  |
      | via @OK_AR_INVALID_DATETIME |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_7] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    Then verifica che i DeliveryDetailCode "RECRN002D" "RECRN002E" "RECRN002F" e "RECRN002D" "RECRN002E" "RECRN002F" abbiano timestamp uguali

    Examples:
      | SEQUENCE                           |
      | via @FAIL-DiscoveryIrreperibile_AR |

  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_7B] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    Then verifica che i DeliveryDetailCode "RECRN002D" "RECRN002E" "RECRN002F" e "RECRN001A" "RECRN001B" "RECRN001C" abbiano timestamp uguali

    Examples:
      | SEQUENCE                           |
      | via @FAIL-Discovery_AR             |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_8] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005B"
    Then verifica che i DeliveryDetailCode "RECRN005A" "RECRN005B" "" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE                          |
      | via @FAIL_CompiutaGiacenza_AR_ERR |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_1A] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    Then verifica che i DeliveryDetailCode "RECRN001A" "RECRN001B" "RECRN001C" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE                      |
      | via @OK_AR                    |
      | via @OK-Retry_AR              |
      | via @OK-NonRendicontabile_AR  |
      | via @OK-CausaForzaMaggiore_AR |
      | via @FAIL-Discovery_AR        |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_1B] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    Then verifica che i DeliveryDetailCode "RECRN001A" "RECRN001B" "RECRN001C" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE                |
      | via @OK-M_AR            |
      | via @OK_AR-CON020-7Z1P  |
      | via @OK_AR-CON020-ZIP1P |
      | via @OK_AR-CON020-7Z2P  |
      | via @OK_AR-CON020-ZIP2P |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_1C] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C"
    Then verifica che i DeliveryDetailCode "RECRN001A" "RECRN001B" "RECRN001C" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE                |
      | via @OK_AR-CON020-7Z3P  |
      | via @OK_AR-CON020-ZIP3P |
      #| via @OK-AR-ENP          |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_2] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002C"
    Then verifica che i DeliveryDetailCode "RECRN002A" "RECRN002B" "RECRN002C" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE                         |
      | via @FAIL_AR                     |
      | via @FAIL_IndirizzoInesistenteAR |
      | via @FAIL_DECEDUTO_SLOW_AR       |
      | via @FAIL_DECEDUTO_AR            |
      | via @FAIL-WO_AR                  |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_3] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN002F"
    Then verifica che i DeliveryDetailCode "RECRN002D" "RECRN002E" "RECRN002F" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE                           |
      | via @FAIL-Irreperibile_AR          |
      | via @FAIL-DiscoveryIrreperibile_AR |
      | via @FAIL-Irreperibile_AR_SLOW     |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_4] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN003C"
    Then verifica che i DeliveryDetailCode "RECRN003A" "RECRN003B" "RECRN003C" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE               |
      | via @OK-WO-Giacenza_AR |
      | via @OK-Giacenza_AR    |

  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_4B] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN003C"
    Then verifica che i DeliveryDetailCode "RECRN003A" "RECRN003B" "RECRN003C" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE                 |
      | via @OK-Giacenza-gt10_AR |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_5] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN004C"
    Then verifica che i DeliveryDetailCode "RECRN004A" "RECRN004B" "RECRN004C" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE              |
      | via @FAIL-Giacenza_AR |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_5B] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN004C"
    Then verifica che i DeliveryDetailCode "RECRN004A" "RECRN004B" "RECRN004C" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE                   |
      | via @FAIL-Giacenza-gt10_AR |


  @mockConsolidatore
  Scenario Outline: [B2B_MOCK_CONSOLIDATORE_6] Si verifica che i timestamp degli elementi con DeliveryDetailCode forniti siano uguali tra loro
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | <SEQUENCE> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN005C"
    Then verifica che i DeliveryDetailCode "RECRN005A" "RECRN005B" "RECRN005C" abbiano timestamp "uguali"

    Examples:
      | SEQUENCE                          |
      | via @FAIL_CompiutaGiacenza_AR_ERR |
      | via @FAIL-CompiutaGiacenza_AR     |
