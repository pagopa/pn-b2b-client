Feature: Monitoraggio delle campagne per le notifiche bonarie.

#  @addressBook1 -> MessaMora
#  @addressBook2 -> FattOrd
#  @addressBook3 -> Reminder
#  @addressBook4 -> QADigital
#  @addressBook4 -> CampaignOnlyPEC
#   N.i.         -> CampAnalogic
#   N.i.         -> BonarieAllChannels


  @informalNotificationsMonitorCampaign @addressBook1
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_A] Come ente mittente sottometto una notifica bonaria che verrà INVIATA su canale ANALOGICO, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "MessaMora" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | MessaMora                               |
      | messageId                | ${NEW-IT}                               |
      | subject                  | Test workflow                           |
      | recipientType            | PF                                      |
      | taxId                    | FRMTTR76M06B715E                        |
      | denomination             | Ettore Fieramosca                       |
      | email                    | suppressionlist@simulator.amazonses.com |
      | digitalDomicile          | NULL                                    |
      | physical_address_address | Via@OK_RIS                              |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | OK |
    Then il contatore "NOTIFICHE_TOTALI" della campagna "MessaMora" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_SU_CANALE" della campagna "MessaMora" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_RS" della campagna "MessaMora" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE_RS" della campagna "MessaMora" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook4
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_B] Come ente mittente sottometto una notifica bonaria che verrà INVIATA su canale DIGITALE, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "CampaignOnlyPEC" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | CampaignOnlyPEC       |
      | messageId       | ${NEW-IT}             |
      | subject         | Test workflow         |
      | recipientType   | PG                    |
      | taxId           | 20517490320           |
      | denomination    | Acme spa              |
      | email           | NULL                  |
      | digitalDomicile | example@pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria
    Then il contatore "NOTIFICHE_TOTALI" della campagna "CampaignOnlyPEC" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_SU_CANALE" della campagna "CampaignOnlyPEC" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_PEC" della campagna "CampaignOnlyPEC" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE_PEC" della campagna "CampaignOnlyPEC" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook1
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_C] Come ente mittente sottometto una notifica bonaria che verrà INVIATA su canale ANALOGICO e DIGITALE, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "MessaMora" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | MessaMora                  |
      | messageId                | ${NEW-IT}                  |
      | subject                  | Test workflow              |
      | recipientType            | PG                         |
      | taxId                    | 15376371009                |
      | denomination             | Acme spa                   |
      | email                    | NULL                       |
      | digitalDomicile          | example@FAIL-pecFirstKO.it |
      | physical_address_address | Via@OK_RIS                 |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | OK |
    Then il contatore "NOTIFICHE_TOTALI" della campagna "MessaMora" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_SU_CANALE" della campagna "MessaMora" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_PEC" della campagna "MessaMora" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_RS" della campagna "MessaMora" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE_RS" della campagna "MessaMora" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook2
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_3_A] Come ente mittente sottometto una notifica bonaria che sarà RIFIUTATA, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "FattOrd" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd      |
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT-FR} |
      | additionalLanguages | DE           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then il contatore "NOTIFICHE_RIFIUTATE" della campagna "FattOrd" risulta incrementato di 1


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_3_B] Come ente mittente sottometto una notifica bonaria che sarà RIFIUTATA, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "CampAnalogic" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId                    | CampAnalogic             |
      | denomination                  | Leonardo Da Vinci no vas |
      | taxId                         | DVNLRD52D15M059P         |
      | physical_address_zip          | 80100                    |
      | physical_address_municipality | Bologn                   |
      | physical_address_province     | RM                       |
      | physical_address_state        | Becok                    |
      | physical_address_address      | Q1                       |
      | physical_address_details      | NULL                     |
      | messageId                     | ${NEW-IT}                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then il contatore "NOTIFICHE_RIFIUTATE" della campagna "CampAnalogic" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook3
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_4_BCD] Come ente mittente sottometto una notifica bonaria che sarà INVIATA tramite EMAIL e SMS, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "Reminder" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | Reminder                       |
      | messageId       | ${NEW-IT}                      |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
      | phone_number    | +3900000                       |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | EMAIL |
      | details_responseStatus | KO    |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | SMS |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | SMS |
      | details_responseStatus | OK  |
    Then il contatore "NOTIFICHE_TOTALI" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_EMAIL" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_SMS" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE_EMAIL" della campagna "Reminder" risulta incrementato di 0
    Then il contatore "NOTIFICHE_INVIATE_SU_CANALE" della campagna "Reminder" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook3
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_4_ACD] Come ente mittente sottometto una notifica bonaria che sarà INVIATA tramite PEC e SMS, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "Reminder" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | Reminder                   |
      | messageId       | ${NEW-IT}                  |
      | subject         | Test workflow              |
      | recipientType   | PG                         |
      | taxId           | 15376371009                |
      | denomination    | Acme spa                   |
      | email           | NULL                       |
      | digitalDomicile | example@FAIL-pecFirstKO.it |
      | phone_number    | +3900000                   |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | SMS |
    Then il contatore "NOTIFICHE_TOTALI" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_PEC" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_SMS" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE_PEC" della campagna "Reminder" risulta incrementato di 0
    Then il contatore "NOTIFICHE_INVIATE_SU_CANALE" della campagna "Reminder" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook2
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6_A] Come ente mittente sottometto una notifica bonaria RICEVUTA tramite EMAIL, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "FattOrd" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                 |
      | messageId       | ${NEW-IT}               |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "DELIVERED" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    Then il contatore "NOTIFICHE_INVIATE_EMAIL" della campagna "FattOrd" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE_EMAIL" della campagna "FattOrd" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook4
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6_B] Come ente mittente sottometto una notifica bonaria RICEVUTA tramite PEC, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "CampaignOnlyPEC" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | CampaignOnlyPEC          |
      | messageId       | ${NEW-IT}                |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "DELIVERED" della notifica bonaria con dettagli
      | details_channel | PEC |
    Then il contatore "NOTIFICHE_INVIATE_PEC" della campagna "CampaignOnlyPEC" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE_PEC" della campagna "CampaignOnlyPEC" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook3
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6_C] Come ente mittente sottometto una notifica bonaria RICEVUTA tramite SMS, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "Reminder" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | Reminder                   |
      | messageId       | ${NEW-IT}                  |
      | subject         | Test workflow              |
      | recipientType   | PG                         |
      | taxId           | 15376371009                |
      | denomination    | Acme spa                   |
      | email           | NULL                       |
      | digitalDomicile | example@FAIL-pecFirstKO.it |
      | phone_number    | +3900000                   |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | KO  |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | SMS |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | SMS |
      | details_responseStatus | OK  |
    Then il contatore "NOTIFICHE_INVIATE_SMS" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_PEC" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE_PEC" della campagna "Reminder" risulta incrementato di 0


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6_E] Come ente mittente sottometto una notifica bonaria RICEVUTA su più canali digitali, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "BonarieAllChannels" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | BonarieAllChannels       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | tullio.test@virgilio.it  |
      | digitalDomicile | example@OK-pecSuccess.it |
      | phone_number    | +3900000                 |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | OK  |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel | SMS |
    Then il contatore "NOTIFICHE_CONSEGNATE_EMAIL" della campagna "BonarieAllChannels" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE_PEC" della campagna "BonarieAllChannels" risulta incrementato di 1
    Then il contatore "NOTIFICHE_INVIATE_SMS" della campagna "BonarieAllChannels" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @informalNotMVP @addressBook2
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_7_A] Come ente mittente sottometto una notifica bonaria che SODDISFI il feedback ed è RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "FattOrd" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                 |
      | messageId       | ${NEW-IT}               |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M004  |
    And si attende che venga prodotto l'elemento "DELIVERED" della notifica bonaria
    Then il contatore "NOTIFICHE_CONSEGNATE" della campagna "FattOrd" risulta incrementato di 1
    Then il contatore "WORKFLOW_COMPLETATI" della campagna "FattOrd" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @informalNotMVP @addressBook4
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_7_B] Come ente mittente sottometto una notifica bonaria che NON soddisfi il feedback ma è RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "QADigital" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | QADigital                |
      | messageId       | ${NEW-IT}                |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_REACHED" della notifica bonaria
    Then il contatore "NOTIFICHE_CONSEGNATE" della campagna "QADigital" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook2
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_7_C] Come ente mittente sottometto una notifica bonaria che NON soddisfi il feedback ma è RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "FattOrd" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                        |
      | messageId       | ${NEW-IT}                      |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And il destinatario Ettore Fieramosca legge la notifica bonaria
    And si attende che venga prodotto l'elemento "INFORMAL_NOTIFICATION_VIEWED" della notifica bonaria
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    Then il contatore "NOTIFICHE_VISUALIZZATE" della campagna "FattOrd" risulta incrementato di 1
    Then il contatore "NOTIFICHE_VISUALIZZATE_SEND" della campagna "FattOrd" risulta incrementato di 1
    Then il contatore "PRIMA_VISUALIZZAZIONE" della campagna "FattOrd" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE" della campagna "FattOrd" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook4
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_7_C2] Come ente mittente sottometto una notifica bonaria RECAPITATA in seguito visualizzata , il contatore RECSPITATA del monitoraggio si incrementa una sola volta.
    Given vengono salvati i dati statistici attuali della campagna "QADigital" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | QADigital                |
      | messageId       | ${NEW-IT}                |
      | subject         | Test workflow            |
      | recipientType   | PF                       |
      | taxId           | FRMTTR76M06B715E         |
      | denomination    | Ettore Fieramosca        |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_REACHED" della notifica bonaria
    Then il contatore "NOTIFICHE_CONSEGNATE" della campagna "QADigital" risulta incrementato di 1
    And il destinatario Ettore Fieramosca legge la notifica bonaria
    And si attende che venga prodotto l'elemento "INFORMAL_NOTIFICATION_VIEWED" della notifica bonaria
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    Then il contatore "NOTIFICHE_VISUALIZZATE" della campagna "QADigital" risulta incrementato di 1
    Then il contatore "NOTIFICHE_VISUALIZZATE_SEND" della campagna "QADigital" risulta incrementato di 1
    Then il contatore "PRIMA_VISUALIZZAZIONE" della campagna "QADigital" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE" della campagna "QADigital" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook2
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_8] Come ente mittente sottometto una notifica bonaria per destinatario NON REPERIBILE, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "FattOrd" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd           |
      | messageId       | ${NEW-IT}         |
      | subject         | Test workflow     |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | email           | NULL              |
      | digitalDomicile | NULL              |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And si attende che la notifica bonaria passi in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    Then il contatore "NOTIFICHE_NON_CONSEGNABILI" della campagna "FattOrd" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook2
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_9_A] Come ente mittente sottometto una notifica bonaria che SODDISFA il feedback ed è RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "FattOrd" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                 |
      | messageId       | ${NEW-IT}               |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M004  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    Then il contatore "WORKFLOW_COMPLETATI" della campagna "FattOrd" risulta incrementato di 1
    Then il contatore "NOTIFICHE_CONSEGNATE" della campagna "FattOrd" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @informalNotMVP @addressBook4
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_9_B] Come ente mittente sottometto una notifica bonaria che SODDISFA il feedback ma NON RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "QADigital" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | QADigital                               |
      | messageId       | ${NEW-IT}                               |
      | subject         | Test workflow                           |
      | recipientType   | PF                                      |
      | taxId           | FRMTTR76M06B715E                        |
      | denomination    | Ettore Fieramosca                       |
      | email           | suppressionlist@simulator.amazonses.com |
      | digitalDomicile | NULL                                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria
    Then il contatore "WORKFLOW_COMPLETATI" della campagna "QADigital" risulta incrementato di 1


  @informalNotificationsMonitorCampaign @addressBook3
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_10] Come ente mittente sottometto una notifica bonaria che sarà VISUALIZZATA, il contatore del monitoraggio si incrementa correttamente.
    Given vengono salvati i dati statistici attuali della campagna "Reminder" associata all' ente "Comune_Multi"
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | Reminder                   |
      | messageId       | ${NEW-IT}                  |
      | subject         | Test workflow              |
      | recipientType   | PG                         |
      | taxId           | 20517490320                |
      | denomination    | Acme spa                   |
      | email           | NULL                       |
      | digitalDomicile | example@FAIL-pecFirstKO.it |
      | phone_number    | +39001                     |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And il destinatario CucumberSpa legge la notifica bonaria
    And si attende che venga prodotto l'elemento "INFORMAL_NOTIFICATION_VIEWED" della notifica bonaria
    Then il contatore "NOTIFICHE_VISUALIZZATE" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "NOTIFICHE_VISUALIZZATE_SEND" della campagna "Reminder" risulta incrementato di 1
    Then il contatore "PRIMA_VISUALIZZAZIONE" della campagna "Reminder" risulta incrementato di 1


  # Testato con [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_A]
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_5] Come ente mittente sottometto una notifica bonaria che sarà INVIATA con ANALOGICO, il contatore del monitoraggio si incrementa correttamente.

  # Testato con [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1]
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_2] Come ente mittente sottometto una notifica bonaria che sarà ACCETTATA, il contatore del monitoraggio si incrementa correttamente.

  # Testato con [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_C]
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6_D] Come ente mittente sottometto una notifica bonaria RICEVUTA tramite EMAIL, il contatore del monitoraggio si incrementa correttamente.


  @informalNotificationsMonitorCampaign
  Scenario Outline: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_02_1]
    Given il recupero dei dati statistici della campagna "<id_campagna>" fallisce con errore 400
    Examples:
      | id_campagna |
      | 123         |
      | abc         |
      | @           |
      |             |






























    #l