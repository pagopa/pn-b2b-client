Feature: Resa al mittente di una notifica

  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_1] Invio notifica 890 mono-destinatario verso PF dichiarato deceduto con controllo costo, retention dei documenti e stato RETURNED_TO_SENDER
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED


    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V26"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V26"


    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "RETURNED_TO_SENDER"
    And esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "NotNull" per l'utente 0
    #And viene effettuato un controllo sulla durata della retention di " "


  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_2] Invio notifica AR mono-destinatario verso PF visualizzata precedentemente alla notifica di deceduto con controllo costo, retention dei documenti
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "RETURNED_TO_SENDER"
    And esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "null" per l'utente 0
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
      | details          | NOT_NULL |
      | details_recIndex | 0        |


  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_3] Invio notifica AR mono-destinatario verso PF cancellata dopo la notifica di decesso con controllo costo, retention dei documenti e stato CANCELLED
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And la notifica può essere annullata dal sistema tramite codice IUN
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "CANCELLED"
    And esiste l'elemento di timeline della notifica "NOTIFICATION_CANCELLED" abbia notificationCost ugauale a "null" per l'utente 0
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
      | details          | NOT_NULL |
      | details_recIndex | 0        |

  @returnedToSender @webhook1  @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_4] Invio notifica 890 mono-destinatario verso PF deceduto e in seguito visualizzata con controllo costo, retention dei documenti macro stato mostrato RETURN_TO_SENDER
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    And "CucumberSpa" legge la notifica
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "RETURNED_TO_SENDER"
    And esiste l'elemento di timeline della notifica "NOTIFICATION_VIEWED" abbia notificationCost ugauale a "null" per l'utente 0
    Then viene effettuato un controllo sulla durata della retention di "ATTACHMENTS" per l'elemento di timeline "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
      | details          | NOT_NULL |
      | details_recIndex | 0        |


  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_5] Invio notifica AR mono-destinatario verso PF cancellata e susccessivamente arrivo notifica di deceduto con controllo costo e macro stato mostrato CANCELLED
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "CANCELLED"
    And esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "Null" per l'utente 0



 #  MULTIDEST


  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_6] Invio notifica AR multi-destinatario aventi stati Inviata, Irreperibile, Deceduto e macro stato mostrato DELIVERED e controllo costo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                   |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V26"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V26"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERED"
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "NotNull" per l'utente 0

  @returnedToSender @webhook1 @cleanWebhook @precondition
  Scenario: [RETURNED-TO-SENDER_7_TEST] Invio notifica AR multi-destinatario aventi stati Irreperibile, Deceduto e macro stato mostrato UNREACHABLE
   Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V26"
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                   |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V26"

    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream

    And si crea il nuovo stream per il "Comune_Multi" con versione "V26"
    And  si invoca l'api Webhook versione "V26" per ottenere gli elementi di timeline di tale notifica
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "COMPLETELY_UNREACHABLE" con versione V26
   # And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "NotNull" per l'utente 0

  @returnedToSender @webhook1 @cleanWebhook @precondition
  Scenario: [RETURNED-TO-SENDER_8_TEST] Invio notifica 890 multi-destinatario entrambi deceduti e macro stato mostrato RETURNED_TO_SENDER
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
  #  And destinatario CucumberSpa e:
   #   | physicalAddress_address | @FAIL_DECEDUTO_890 |
   #   | digitalDomicile         | NULL                   |

    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V26"
    And Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_Multi" con versione "V26"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    #Then si invoca l'api Webhook versione "V26" per ottenere gli elementi di timeline di tale notifica
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "ACCEPTED" con versione V26

  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_9] Invio notifica 890 multi-destinatario aventi stati Inviata e Deceduto e macro stato mostrato DELIVERED
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    And destinatario
      | denomination            | Leonardo da Vinci |
      | taxId                   | DVNLRD52D15M059P  |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | Via@OK_890        |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERED"

  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_10] Invio notifica AR multi-destinatario aventi stati Inviata e Irreperibile e macro stato mostrato DELIVERED
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | Leonardo da Vinci |
      | taxId                   | DVNLRD52D15M059P  |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | Via@OK_AR         |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERED"

  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_11] Invio notifica AR multi-destinatario aventi stati Visualizzata e Deceduto e stato mostrato VIEWED con controllo costo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                   |
    And destinatario GherkinSrl
    And "GherkinSrl" legge la notifica
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    #And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "NotNull" per l'utente 0
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "VIEWED"


  Scenario: [RETURNED-TO-SENDER_12] Invio notifica AR multi-destinatario  aventi stati Inviata e Irreperibile e macro stato mostrato DELIVERED
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | Leonardo da Vinci |
      | taxId                   | DVNLRD52D15M059P  |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | Via@OK_AR         |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | 05722930657              |
      | digitalDomicile         | NULL                     |
      | recipientType           | PG                       |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERED"




#----multidest 2

  #prima visualizza, poi deceduto
  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_13] Invio notifica AR multi-destinatario aventi stati Inviata e prima Visualizzata e poi Deceduto e macro stato mostrato DELIVERED con controllo costo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERED"
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "null" per l'utente 0

  #prima deceduto, poi visualizza
  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_14] Invio notifica 890 multi-destinatario aventi stati Inviata, Irreperibile e Deceduto che poi Visualizza con macro stato mostrato DELIVERED e controllo costo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario
      | denomination            | Leonardo da Vinci |
      | taxId                   | DVNLRD52D15M059P  |
      | digitalDomicile         | NULL              |
      | physicalAddress_address | Via@OK_890        |
      | digitalDomicile         | NULL                   |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "NotNull" per l'utente 2
    And "Mario Cucumber" legge la notifica
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERED"

  #prima visualizza, poi deceduto
  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_15] Invio notifica AR multi-destinatario aventi stati Irreperibile e Visualizzata e successivamente Deceduto con macro stato mostrato UNREACHABLE
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                   |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | NNTNRZ80A01H501D         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "UNREACHABLE"

  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_16] Invio notifica 890 multi-destinatario aventi stati Irreperibile e Deceduto. A seguito della Cancellazione e macro stato mostrato CANCELLED con controllo costo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario
      | denomination            | Test AR Fail 2            |
      | taxId                   | NNTNRZ80A01H501D          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "UNREACHABLE"
    When la notifica può essere annullata dal sistema tramite codice IUN
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "CANCELLED"

  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_17] Invio notifica 890 multi-destinatario aventi stati Inviata, Irreperibile e Visualizzata che poi sarà Deceduto con macro stato mostrato DELIVERED
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | digitalDomicile         | NULL                   |
    And destinatario
      | denomination            | Test AR Fail 2            |
      | taxId                   | NNTNRZ80A01H501D          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERED"
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "null" per l'utente 2


  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_18] Invio notifica AR multi-destinatario entrambi deceduti con in seguito una visualizzazione e macro stato mostrato RETURNED_TO_SENDER
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                   |
    And destinatario CucumberSpa e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" per l'utente 0
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "NotNull" per l'utente 0
    And "Mario Cucumber" legge la notifica
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "RETURNED_TO_SENDER"

  @returnedToSender @webhook1 @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_19] Invio notifica 890 multi-destinatario aventi stati Deceduto e Visualizzato che poi sarà Deceduto e macro stato mostrato RETURNED_TO_SENDER
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario CucumberSpa e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_890 |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "null" per l'utente 1
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "RETURNED_TO_SENDER"


# ----------VersioningModeFlag-------------
  @returnedToSender
  Scenario: [RETURNED-TO-SENDER_20] Invio notifica AR mono-destinatario Deceduto con VersioningModeFlag=true, atteso stato DELIVERING
    #v25
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED

    And vengono letti gli eventi fino all'elemento di timeline della notifica "<timeline_element>"
    When si invoca l'api B2B versione "<version>" per ottenere gli elementi di timeline di tale notifica
    Then gli elementi di timeline restituiti da B2B contengono i campi attesi in accordo alla versione "<version>"
   # Examples:
      | timeline_element        | version |
      | ANALOG_SUCCESS_WORKFLOW | V24     |
      | ANALOG_SUCCESS_WORKFLOW | V23     |


  @returnedToSender
  Scenario: [RETURNED-TO-SENDER_21] Invio notifica AR multi-destinatario di cui un Deceduto con VersioningModeFlag=true, atteso stato DELIVERING
    #v25
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED

  @returnedToSender
  Scenario: [RETURNED-TO-SENDER_22] Invio notifica 890 mono-destinatario Deceduto con VersioningModeFlag=false, si attende errore 400
    #v25
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | digitalDomicile         | NULL                   |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    When la notifica viene inviata dal "Comune_Multi"
    #When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'operazione ha prodotto un errore con status code "400"


  @returnedToSender
  Scenario: [RETURNED-TO-SENDER_23] Invio notifica 890 multi-destinatario di cui un Deceduto con VersioningModeFlag=false, si attende errore 400
    #v25
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata dal "Comune_Multi"
    #When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'operazione ha prodotto un errore con status code "400"

    #----------activationDeceasedWorfklowDate-----------------
  @returnedToSender
  Scenario: [RETURNED-TO-SENDER_24] Invio notifica AR mono-destinatario Deceduto con activationDeceasedWorfklowDate=false, atteso stato EFFECTIVE_DATE
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL |
      | details_deliveryFailureCause | M020     |
    And viene verificato che l'elemento di timeline "ANALOG_WORKFLOW_RECIPIENT_DECEASED" non esista
      | details                 | NOT_NULL |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |


  @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_25] Invio notifica 890 multi-destinatario con stato Inviato e Deceduto con activationDeceasedWorfklowDate=false
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |

    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERED"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" per l'utente 0
    And esiste l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0


  @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_26] Invio notifica 890 multi-destinatario con stato Inviato, Irreperibile e Deceduto con activationDeceasedWorfklowDate=false
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | 05722930657              |
      | digitalDomicile         | NULL                     |
      | recipientType           | PG                       |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "DELIVERED"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" per l'utente 0
    And esiste l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0

  @cleanWebhook
  Scenario: [RETURNED-TO-SENDER_27] Invio notifica AR multi-destinatario con stato Irreperibile e Deceduto con activationDeceasedWorfklowDate=false
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_AR |
      | digitalDomicile         | NULL                   |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | 05722930657              |
      | digitalDomicile         | NULL                     |
      | recipientType           | PG                       |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And si crea il nuovo stream per il "Comune_Multi" con versione "V10"
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino allo stato "UNREACHABLE"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" per l'utente 0
    And esiste l'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW" per l'utente 0







  # timeline destinatario--------
  @returnedToSend
  Scenario: [RETURNED-TO-SENDER_MONO] Invio notifica AR mono-destinatario che Visualizza e poi dichiarato Deceduto con stato atteso RETURNED_TO_SENDER e corretta visualizzazione della timeline del destinatario
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"
    And  esiste l'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" abbia notificationCost ugauale a "NotNull" per l'utente 0


  @returnedToSend
  Scenario: [RETURNED-TO-SENDER_28] Invio notifica AR mono-destinatario che Visualizza e poi dichiarato Deceduto con stato atteso RETURNED_TO_SENDER e corretta visualizzazione della timeline del destinatario
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"

  @returnedToSend
  Scenario: [RETURNED-TO-SENDER_29] Invio notifica 890 mono-destinatario Deceduto che poi Visualizza con stato atteso RETURNED_TO_SENDER e corretta visualizzazione della timeline del destinatario
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" per l'utente 0
    And "Mario Cucumber" legge la notifica
    Then viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" esista
      | details                      | NOT_NULL |
      | details_deliveryFailureCause | M020     |
    And vengono letti gli eventi fino allo stato della notifica "RETURNED_TO_SENDER"


  @returnedToSend
  Scenario: [RETURNED-TO-SENDER_30] Invio notifica AR mono-destinatario Deceduto e successivamente notifica cancellata con stato atteso CANCELLED e corretta visualizzazione della timeline del destinatario
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"

  @returnedToSend
  Scenario: [RETURNED-TO-SENDER_31] Invio notifica 890 mono-destinatario Cancellata e successivamene notifica di Decesso con stato atteso CANCELLED e corretta visualizzazione della timeline del destinatario
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_890 |
      | digitalDomicile         | NULL                   |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED"
    Then vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    #dovrebbe arrivare la notifica di deceduto



  #--EFFECTIVE_DATE
  @returnedToSend
  Scenario: [RETURNED-TO-SENDER_32] Invio notifica 890 multi-destinatario una Perfezionata dopo Delivered e un Visualizzato preceduto dal Deceduto, stato atteso EFFECTIVE_DATE
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_890 |
      | digitalDomicile         | NULL                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" per l'utente 0
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"


  @returnedToSend
  Scenario: [RETURNED-TO-SENDER_33] Invio notifica AR multi-destinatario una Perfezionata dopo Delivered e un Deceduto, stato atteso EFFECTIVE_DATE
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_SLOW_AR |
      | digitalDomicile         | NULL                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" per l'utente 0
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"


  @returnedToSend
  Scenario: [RETURNED-TO-SENDER_34] Invio notifica 890 multi-destinatario una Perfezionata dopo Delivered e un Deceduto preceduto dalla Visualizzazione, stato atteso EFFECTIVE_DATE
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL                   |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_WORKFLOW_RECIPIENT_DECEASED" per l'utente 0


#----RS-----
  @returnedToSend
  Scenario: [RETURNED-TO-SENDER_35] Invio notifica RS mono-destinatario
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | ***RS***                    |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | @FAIL_DECEDUTO_RS |
      | digitalDomicile         | NULL                   |
    And destinatario
      | denomination            | Test AR Fail 2   |
      | taxId                   | NNTNRZ80A01H501D |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_RS        |

    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"

