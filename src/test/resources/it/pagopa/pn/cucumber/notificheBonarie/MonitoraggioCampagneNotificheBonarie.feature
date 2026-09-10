Feature: Monitoraggio delle campagne per le notifiche bonarie.



 # STEP :
Given vengono salvati i dati statistici attuali della campagna "TEST_CAMPAIGN"

Then il contatore "totalCount" della campagna "TEST_CAMPAIGN" risulta incrementato di 1




  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_A] Come ente mittente sottometto una notifica bonaria che verrà INVIATA su canale ANALOGICO, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
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
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | OK |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    # call monitor +1 INVIO
    # call monitor +1 INVIO ANALOGICO


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_B] Come ente mittente sottometto una notifica bonaria che verrà INVIATA su canale DIGITALE, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | MessaMora             |
      | messageId       | ${NEW-IT}             |
      | subject         | Test workflow         |
      | recipientType   | PG                    |
      | taxId           | 20517490320           |
      | denomination    | Acme spa              |
      | email           | NULL                  |
      | digitalDomicile | example@pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | OK  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
     # call monitor +2



  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_C] Come ente mittente sottometto una notifica bonaria che verrà INVIATA su canale ANALOGICO e DIGITALE, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | MessaMora                  |
      | messageId                | ${NEW-IT}                  |
      | subject                  | Test workflow              |
      | recipientType            | PG                         |
      | taxId                    | 20517490320                |
      | denomination             | Acme spa                   |
      | email                    | NULL                       |
      | digitalDomicile          | example@FAIL-pecFirstKO.it |
      | physical_address_address | Via@OK_RIS                 |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | OK |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
     # call monitor +1
    # call monitor +1 Ricevuta RS



  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_2] Come ente mittente sottometto una notifica bonaria che sarà ACCETTATA, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | MessaMora             |
      | messageId       | ${NEW-IT}             |
      | subject         | Test workflow         |
      | recipientType   | PG                    |
      | taxId           | 20517490320           |
      | denomination    | Acme spa              |
      | email           | NULL                  |
      | digitalDomicile | example@pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    # call monitor +1


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_3_A] Come ente mittente sottometto una notifica bonaria che sarà RIFIUTATA, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd      |
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT-FR} |
      | additionalLanguages | DE           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    # call monitor +1



  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_3_B] Come ente mittente sottometto una notifica bonaria che sarà RIFIUTATA, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId                    | MessaMora                |
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


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_4_CDE] Come ente mittente sottometto una notifica bonaria che sarà INVIATA tramite EMAIL e SMS, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
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
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | SMS |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | SMS |
      | details_responseStatus | OK  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
# call monitor email sms +2

  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_4_ADE] Come ente mittente sottometto una notifica bonaria che sarà INVIATA tramite PEC e SMS, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | Reminder                   |
      | messageId       | ${NEW-IT}                  |
      | subject         | Test workflow              |
      | recipientType   | PG                         |
      | taxId           | 20517490320                |
      | denomination    | Acme spa                   |
      | email           | NULL                       |
      | digitalDomicile | example@FAIL-pecFirstKO.it |
      | phone_number    | +3900000                   |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | KO  |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | SMS |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | SMS |
      | details_responseStatus | OK  |
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
# call monitor pec sms +2



  # implementato con [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_A]
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_5] Come ente mittente sottometto una notifica bonaria che sarà INVIATA con ANALOGICO, il contatore del monitoraggio si incrementa correttamente.


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6_B] Come ente mittente sottometto una notifica bonaria RICEVUTA tramite EMAIL, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
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


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6] Come ente mittente sottometto una notifica bonaria RICEVUTA tramite PEC, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | MessaMora                |
      | messageId       | ${NEW-IT}                |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | OK  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria



  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6_D] Come ente mittente sottometto una notifica bonaria RICEVUTA tramite SMS, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | Reminder                   |
      | messageId       | ${NEW-IT}                  |
      | subject         | Test workflow              |
      | recipientType   | PG                         |
      | taxId           | 20517490320                |
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
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria



  # implementato con [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_1_C]
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6_E] Come ente mittente sottometto una notifica bonaria RICEVUTA tramite EMAIL, il contatore del monitoraggio si incrementa correttamente.


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_6_F] Come ente mittente sottometto una notifica bonaria RICEVUTA su più canali digitali, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | QADigitalNoFeed todo     |
      | messageId       | ${NEW-IT}                |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | tullio.test@virgilio.it  |
      | digitalDomicile | example@OK-pecSuccess.it |
      | phone_number    | +3900000                 |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | OK  |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M004  |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | SMS |
      | details_responseStatus | OK  |



  @informalNotificationsMonitorCampaign @informalNotMVP
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_7_A] Come ente mittente sottometto una notifica bonaria che SODDISFI il feedback ed è RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
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


  @informalNotificationsMonitorCampaign @informalNotMVP
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_7_B] Come ente mittente sottometto una notifica bonaria che NON soddisfi il feedback ma è RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
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


  @informalNotificationsMonitorCampaign  #todo
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_7_C] Come ente mittente sottometto una notifica bonaria che NON soddisfi il feedback ma è RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    # call monitor






  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_8] Come ente mittente sottometto una notifica bonaria per destinatario NON REPERIBILE, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
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


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_9_A] Come ente mittente sottometto una notifica bonaria che SODDISFA il feedback ed è RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | MessaMora                |
      | messageId       | ${NEW-IT}                |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | OK  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria


  @informalNotificationsMonitorCampaign @informalNotMVP
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_9_B] Come ente mittente sottometto una notifica bonaria che SODDISFA il feedback ma NON RECAPITATA, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
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


  @informalNotificationsMonitorCampaign
  Scenario: [NOTIFICHE_BONARIE_MONITOR_CAMPAGNA_01_11] Come ente mittente sottometto una notifica bonaria che sarà VISUALIZZATA, il contatore del monitoraggio si incrementa correttamente.
    # call monitor
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
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"











































    #l