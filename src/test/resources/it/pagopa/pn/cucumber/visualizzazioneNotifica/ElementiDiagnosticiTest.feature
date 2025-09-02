Feature: controlli su elementi diagnostici

#  elementi diagnostici:
#  VALIDATED_F24,
#  VALIDATE_F24_REQUEST,
#  GENERATED_F24,
#  GENERATE_F24_REQUEST,
#  NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST

  Scenario: [ELEMENTI_DIAGNOSTICI_1] - Controllo che non siano presenti elementi diagnostici tra i relatedTimelineElements della notifica
    Given viene generata una nuova notifica
      | subject            | notifica digitale |
      | senderDenomination | Comune di palermo |
      | feePolicy          | DELIVERY_MODE     |
      | document           | DOC_1_PG;         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address      | test@pecOk.it        |
      | physicalAddress_municipality | BARI                 |
      | physicalAddress_province     | BA                   |
      | physicalAddress_zip          | 70129                |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_f24               | SI                   |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica annullata "NOTIFICATION_CANCELLATION_REQUEST"
    And recuperando la fullSentNotification con la versione b2b "più recente" controllo che non sia presente l'elementoto "GENERATED_F24" nei relatedTimelineElements
    And recuperando la fullSentNotification con la versione b2b "più recente" controllo che non sia presente l'elementoto "GENERATE_F24_REQUEST" nei relatedTimelineElements
    And recuperando la fullSentNotification con la versione b2b "più recente" controllo che non sia presente l'elementoto "VALIDATED_F24" nei relatedTimelineElements
    And recuperando la fullSentNotification con la versione b2b "più recente" controllo che non sia presente l'elementoto "VALIDATE_F24_REQUEST" nei relatedTimelineElements
    And recuperando la fullSentNotification con la versione b2b "più recente" controllo che non sia presente l'elementoto "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST" nei relatedTimelineElements

  Scenario: [ELEMENTI_DIAGNOSTICI_2] - Controllo che non siano presenti elementi diagnostici di TimeOut RIR
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | feePolicy             | DELIVERY_MODE                   |
      | document              | DOC_5_PG;                       |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_State   | MESSICO    |
      | physicalAddress_zip     | ZONE_2     |
      | physicalAddress_address | via@OK_RIR_NO_DEMAT |
      | payment_pagoPaForm      | NOALLEGATO |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" al tentativo "ATTEMPT_0"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And recuperando la fullSentNotification con la versione b2b "più recente" controllo che non sia presente l'elementoto "SEND_ANALOG_TIMEOUT" nei relatedTimelineElements
    And recuperando la fullSentNotification con la versione b2b "più recente" controllo che non sia presente l'elementoto "SEND_ANALOG_TIMEOUT_CREATION_REQUEST" nei relatedTimelineElements
