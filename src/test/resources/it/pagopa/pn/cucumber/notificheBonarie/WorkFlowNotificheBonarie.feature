Feature: Workflow di una notifica bonaria.


  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_test] Come ente mittente invio una notifica bonaria verso PF ottengo errore SPAM sul email e solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd    |
      | messageId       | ${IT}             |
      | subject         | Test workflow     |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | email           | NULL              |
      | digitalDomicile | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"

#    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
#    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria



# ***********************************************
# **** Fatturazione Ordinaria
# **** SoricalFattOrd
# ***********************************************

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_1] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd          |
      | messageId       | ${IT}                   |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_recIndex | 0 |
    And si attende che la notifica bonaria passi in stato "PROCESSING"


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_A] Come ente mittente invio una notifica bonaria, ottengo il feedback desiderato sull email ricevuta
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd          |
      | messageId       | ${IT}                   |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M004  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_B] Come ente mittente invio una notifica bonaria verso PF ottengo errore SPAM sul email quindi non ko subito ma successivo
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd                    |
      | messageId       | ${IT}                             |
      | subject         | Test workflow                     |
      | recipientType   | PF                                |
      | taxId           | FRMTTR76M06B715E                  |
      | denomination    | Ettore Fieramosca                 |
      | email           | complaint@simulator.amazonses.com |
      | digitalDomicile | NULL                              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M006  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_REACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_C] Come ente mittente invio una notifica bonaria verso PF ottengo errore BOUCED sul email e solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd                          |
      | messageId       | ${IT}                                   |
      | subject         | Test workflow                           |
      | recipientType   | PF                                      |
      | taxId           | FRMTTR76M06B715E                        |
      | denomination    | Ettore Fieramosca                       |
      | email           | suppressionlist@simulator.amazonses.com |
      | digitalDomicile | NULL                                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M003  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_D] Come ente mittente invio una notifica bonaria verso PF ottengo errore BOUCED sul email e solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd                 |
      | messageId       | ${IT}                          |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M005  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_3_A] Come ente mittente invio una notifica bonaria verso PG ottengo feedback desiderato sulla PEC ricevuta
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd           |
      | messageId       | ${IT}                    |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | OK  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_3_B] Come ente mittente invio una notifica bonaria verso PG ottengo errore sulla PEC e solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd             |
      | messageId       | ${IT}                      |
      | subject         | Test workflow              |
      | recipientType   | PG                         |
      | taxId           | 20517490320                |
      | denomination    | Acme spa                   |
      | email           | NULL                       |
      | digitalDomicile | example@FAIL-pecFirstKO.it |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | KO  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_4_A] Come ente mittente invio una notifica bonaria che termina senza aver trovato alcun recapito
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd    |
      | messageId       | ${IT}             |
      | subject         | Test workflow     |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | email           | NULL              |
      | digitalDomicile | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And si attende che la notifica bonaria passi in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria



#todo da verificare se possibile in quanto il desiderato è già recived quindi non potrà arrivare feedb. negativo? in Asyncrono?
  #@informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_5_A] Come ente mittente invio una notifica bonaria verso PF ricevo feedback desiderato ma non..
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd    |
      | messageId       | ${IT}             |
      | subject         | Test workflow     |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | email           | todo              |
      | digitalDomicile | todo              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria

  #todo da verificare se possibile in quanto il desiderato è già recived quindi non potrà arrivare feedb. in quanto pec
  #@informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_5_B] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd |
      | messageId       | ${IT}          |
      | subject         | Test workflow  |
      | recipientType   | PG             |
      | taxId           | 20517490320    |
      | denomination    | Acme spa       |
      | email           | todo           |
      | digitalDomicile | todo           |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_6] Come ente mittente invio una notifica bonaria e solo feddback negativi e in seguito viene visualizzata
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd                 |
      | messageId       | ${IT}                          |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And il destinatario legge la notifica bonaria
    And si attende che venga prodotto l'elemento "INFORMAL_NOTIFICATION_VIEWED" della notifica bonaria
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"


# ***********************************************
# **** Reminder
# **** SoricalReminder
# ***********************************************

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_1] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder         |
      | messageId       | ${IT}                   |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
      | phone_number    | NULL                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria
    And si attende che la notifica bonaria passi in stato "PROCESSING"

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_A] Come ente mittente invio una notifica bonaria verso PF e ottengo il desiderato ricevuto dell' email
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder         |
      | messageId       | ${IT}                   |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
      | phone_number    | NULL                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M004  |
    And si attende che venga prodotto l'elemento "DELIVERED" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_B] Come ente mittente invio una notifica bonaria verso PF ricevo solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder                |
      | messageId       | ${IT}                          |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
      | phone_number    | +39001                         |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | KO    |
      | details_channel        | EMAIL |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | KO  |
      | details_channel        | SMS |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_C] Come ente mittente invio una notifica bonaria verso PF

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_D] Come ente mittente invio una notifica bonaria verso PF


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_3_A] Come ente mittente invio una notifica bonaria verso PG ottengo feedback desideraro
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder          |
      | messageId       | ${IT}                    |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
      | phone_number    | NULL                     |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | OK  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_3_B] Come ente mittente invio una notifica bonaria non ottengo il feedback desiderato pec ricevuta
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder            |
      | messageId       | ${IT}                      |
      | subject         | Test workflow              |
      | recipientType   | PG                         |
      | taxId           | 20517490320                |
      | denomination    | Acme spa                   |
      | email           | NULL                       |
      | digitalDomicile | example@FAIL-pecFirstKO.it |
      | phone_number    | +3900000                   |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
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


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_4_A] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder                |
      | messageId       | ${IT}                          |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
      | phone_number    | +3900000                       |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel | SMS |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | SMS |
      | details_responseStatus | OK  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_4_B] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder            |
      | messageId       | ${IT}                      |
      | subject         | Test workflow              |
      | recipientType   | PG                         |
      | taxId           | 20517490320                |
      | denomination    | Acme spa                   |
      | email           | NULL                       |
      | digitalDomicile | example@FAIL-pecFirstKO.it |
      | phone_number    | +3900000                   |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
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
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_5_A] Come ente mittente invio una notifica bonaria verso PF senza alcun recapito trovato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder   |
      | messageId       | ${IT}             |
      | subject         | Test workflow     |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | email           | NULL              |
      | digitalDomicile | NULL              |
      | phone_number    | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | SMS |
    And si attende che la notifica bonaria passi in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_6] Come ente mittente invio una notifica bonaria ricevendo solo feddback negativi e visualizzazione
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder            |
      | messageId       | ${IT}                      |
      | subject         | Test workflow              |
      | recipientType   | PG                         |
      | taxId           | 20517490320                |
      | denomination    | Acme spa                   |
      | email           | NULL                       |
      | digitalDomicile | example@FAIL-pecFirstKO.it |
      | phone_number    | +39001                     |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | KO  |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | SMS |
      | details_responseStatus | KO  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And il destinatario legge la notifica bonaria
    And si attende che venga prodotto l'elemento "INFORMAL_NOTIFICATION_VIEWED" della notifica bonaria
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"



#todo con feedback recived possiamo avere il non recapitato?
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_7_A] Come ente mittente invio una notifica bonaria verso PF ricevo feedback desiderato ma la notifica non è recapitata

    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_7_B] Come ente mittente invio una notifica bonaria verso PG ricevo feedback desiderato ma la notifica non è recapitata

    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria



# ***********************************************
# **** Messa in mora
# **** SoricalMessaMora
# ***********************************************

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_2_A] Come ente mittente invio una notifica bonaria verso PG e ricevo feedback desiderato sulla ricezionen della pec
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalMessaMora         |
      | messageId       | ${IT}                    |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | OK  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria


  #@informalNotificationsWorkFlow #La campagna analogica prevede sempre indirizzo analogico per PF
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_2_B] Come ente mittente invio una notifica bonaria verso PG ricevo solo feedback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | SoricalMessaMora           |
      | messageId        | ${IT}                      |
      | subject          | Test workflow              |
      | recipientType    | PG                         |
      | taxId            | 15376371009                |
      | denomination     | PagoPa spa                 |
      | email            | NULL                       |
      | digitalDomicile  | example@FAIL-pecFirstKO.it |
      | physical_address | ${PHYSICAL_ADDRESS_NULL}   |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | KO  |
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | KO |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_3_A] Come ente mittente invio una notifica bonaria non ottengo il feedback desiderato ma positivo su analogico
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | SoricalMessaMora                        |
      | messageId                | ${IT}                                   |
      | subject                  | Test workflow                           |
      | recipientType            | PF                                      |
      | taxId                    | FRMTTR76M06B715E                        |
      | denomination             | Ettore Fieramosca                       |
      | email                    | suppressionlist@simulator.amazonses.com |
      | digitalDomicile          | NULL                                    |
      | physical_address_address | Via@OK_RIS                              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | OK |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_3_B] Come ente mittente invio una notifica bonaria ottengo il feedback desiderato su analogico
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | SoricalMessaMora           |
      | messageId                | ${IT}                      |
      | subject                  | Test workflow              |
      | recipientType            | PG                         |
      | taxId                    | 20517490320                |
      | denomination             | Acme spa                   |
      | email                    | NULL                       |
      | digitalDomicile          | example@FAIL-pecFirstKO.it |
      | physical_address_address | Via@OK_RIS                 |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "COVERPAGE_CREATION_REQUEST" della notifica bonaria
    And si attende che venga prodotto l'elemento "PREPARE_ANALOG_DELIVERY" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | OK |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_4] Come ente mittente invio una notifica bonaria verso PG ottenendo solo feedback negativi, in seguito visualizza
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | SoricalMessaMora           |
      | messageId                | ${IT}                      |
      | subject                  | Test workflow              |
      | recipientType            | PG                         |
      | taxId                    | 20517490320                |
      | denomination             | Acme spa                   |
      | email                    | NULL                       |
      | digitalDomicile          | example@FAIL-pecFirstKO.it |
      | physical_address_address | via@FAIL_RS                |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And il destinatario legge la notifica bonaria
    And si attende che venga prodotto l'elemento "INFORMAL_NOTIFICATION_VIEWED" della notifica bonaria
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_5] Come ente mittente invio una notifica bonaria verso PG ottenendo solo feedback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | SoricalMessaMora           |
      | messageId                | ${IT}                      |
      | subject                  | Test workflow              |
      | recipientType            | PG                         |
      | taxId                    | 20517490320                |
      | denomination             | Acme spa                   |
      | email                    | NULL                       |
      | digitalDomicile          | example@FAIL-pecFirstKO.it |
      | physical_address_address | via@FAIL_RS                |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | KO  |
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | KO |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria


  #@informalNotificationsWorkFlow #La campagna analogica prevede indirzzo analogico per PF
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_6_A] Come ente mittente invio una notifica bonaria verso PF e nessun recapito trovato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | SoricalMessaMora         |
      | messageId        | ${IT}                    |
      | subject          | Test workflow            |
      | recipientType    | PF                       |
      | taxId            | DVNLRD52D15M059P         |
      | denomination     | Leonardo da Vinci        |
      | email            | NULL                     |
      | digitalDomicile  | NULL                     |
      | phone_number     | NULL                     |
      | physical_address | ${PHYSICAL_ADDRESS_NULL} |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria


  #@informalNotificationsWorkFlow #pec obbligatpria per PG+Canale digitale
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_6_B] Come ente mittente invio una notifica bonaria verso PG con nessun recapiuto trovato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | SoricalMessaMora         |
      | messageId        | ${IT}                    |
      | subject          | Test workflow            |
      | recipientType    | PG                       |
      | taxId            | 15376371009              |
      | denomination     | PagoPa spa               |
      | digitalDomicile  | NULL                     |
      | phone_number     | NULL                     |
      | physical_address | ${PHYSICAL_ADDRESS_NULL} |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria


# ***********************************************
# ****
# ****
# ***********************************************


  @informalNotificationsWorkFlow @informalNotMVP #SENT in EMAIL
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_5_A] Come ente mittente invio una notifica bonaria verso PF ricevo feedback desiderato ma non..
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | todo                                    |
      | messageId       | ${IT}                                   |
      | subject         | Test workflow                           |
      | recipientType   | PF                                      |
      | taxId           | FRMTTR76M06B715E                        |
      | denomination    | Ettore Fieramosca                       |
      #| email           | bounce@simulator.amazonses.com |
      | email           | suppressionlist@simulator.amazonses.com |
      | digitalDomicile | NULL                                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria


  @informalNotificationsWorkFlow @informalNotMVP #SENT in EMAIL
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_5_B] Come ente mittente invio una notifica bonaria verso PF ricevo feedback desiderato ma non..
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | todo                     |
      | messageId       | ${IT}                    |
      | subject         | Test workflow            |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | Acme spa                 |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_REACHED" della notifica bonaria



# ***********************************************
# *** AUDIT LOG ********
# ***********************************************


  @informalNotificationsWorkFlow @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_05_1_A] Come ente mittente invio una notifica bonaria verso PG con pec e analogico e verifico gli auditlog
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | SoricalMessaMora           |
      | messageId                | ${IT}                      |
      | subject                  | Test workflow              |
      | recipientType            | PG                         |
      | taxId                    | 20517490320                |
      | denomination             | Acme spa                   |
      | email                    | NULL                       |
      | digitalDomicile          | example@FAIL-pecFirstKO.it |
      | physical_address_address | Via@OK_RIS                 |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | OK |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-manager" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_SEND_PEC |
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-manager" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto               |
      | param1 | AUD_COM_PD_PREPARE |
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-manager" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto               |
      | param1 | AUD_COM_PD_EXECUTE |
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-manager" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto                       |
      | param1 | AUD_COM_PD_PREPARE_RECEIVE |
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-manager" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto                       |
      | param1 | AUD_COM_PD_EXECUTE_RECEIVE |


  @informalNotificationsWorkFlow @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_05_1_A2] Come ente mittente invio una notifica bonaria verso PG con pec e analogico e verifico gli auditlog
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalMessaMora      |
      | messageId       | ${IT}                 |
      | subject         | Test workflow         |
      | recipientType   | PG                    |
      | taxId           | 20517490320           |
      | denomination    | Acme spa              |
      | email           | NULL                  |
      | digitalDomicile | example@pecSuccess.it |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | OK  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-manager" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_SEND_PEC |


  @informalNotificationsWorkFlow @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_05_1_B] Come ente mittente invio una notifica bonaria verso PF con email e telefono e verifico gli auditlog
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder         |
      | messageId       | ${IT}                   |
      | subject         | Test workflow           |
      | recipientType   | PF                      |
      | taxId           | FRMTTR76M06B715E        |
      | denomination    | Ettore Fieramosca       |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
      | phone_number    | NULL                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    #And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-manager" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto               |
      | param1 | AUD_COM_SEND_EMAIL |


  @informalNotificationsWorkFlow @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_05_1_B2] Come ente mittente invio una notifica bonaria verso PF con email e telefono e verifico gli auditlog
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalReminder   |
      | messageId       | ${IT}             |
      | subject         | Test workflow     |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | email           | NULL              |
      | digitalDomicile | NULL              |
      | phone_number    | +3900000          |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | SMS |
      | details_responseStatus | KO  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-manager" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_SEND_SMS |


  @informalNotificationsWorkFlow @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_todo] Come ente mittente invio una notifica bonaria e solo feddback negativi e in seguito viene visualizzata controllo gli audit log
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | SoricalFattOrd                 |
      | messageId       | ${IT}                          |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che la notifica bonaria passi in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And il destinatario legge la notifica bonaria
    And si attende che venga prodotto l'elemento "INFORMAL_NOTIFICATION_VIEWED" della notifica bonaria
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-commons" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_VIEW_RCP |
    And verifico la presenza di un audit log su "/aws/ecs/pn-commons" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_VIEW_SND |

