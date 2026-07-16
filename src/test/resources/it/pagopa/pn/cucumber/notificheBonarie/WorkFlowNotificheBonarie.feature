Feature: Workflow di una notifica bonaria.


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_00_0_0] Come ente mittente invio una notifica bonaria
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine

  #----------- STEP --------------------------------------------

    And si verifica che la notifica bonaria sia in stato "ACCEPTED"

    And si attende che venga prodotto l'elemento "TODO" della notifica bonaria
    And viene verificato che l'elemento di timeline "TODO" della notifica bonaria esista e sia correttamente compilato


    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_REACHED" della notifica bonaria esista e sia correttamente compilato

    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_UNREACHED" della notifica bonaria esista e sia correttamente compilato

    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_REACHED" della notifica bonaria esista e sia correttamente compilato

    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato

    And si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato


 #example@pecSuccess.it
#testpagopa3@pec.pagopa.it
#testpagopa1@pec.pagopa.it
# example@OK-pecSuccess.it

  #example@FAIL-pecFirstKO.it



# ***********************************************
# **** Fatturazione Ordinaria
# **** SoricalFattOrd
# ***********************************************

  #TODO il controllo può essere inglobato in altro scenario
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_1] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd    |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | todo ok           |
      | pec_address    | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"

    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria
    And si verifica che la notifica bonaria sia in stato "PROCESSING"


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_A] Come ente mittente invio una notifica bonaria, ottengo il feedback desiderato sull email ricevuta
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd    |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | todo  ok          |
      | pec_address    | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"

    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_REACHED" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_B] Come ente mittente invio una notifica bonaria verso PF ottengo errore SPAM sul email e solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd                    |
      | messageId      | ${IT}                             |
      | subject        | Test workflow                     |
      | recipientType  | PF                                |
      | taxId          | FRMTTR76M06B715E                  |
      | denomination   | Ettore Fieramosca                 |
      | digitalAddress | complaint@simulator.amazonses.com |
      | pec_address    | NULL                              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_C] Come ente mittente invio una notifica bonaria verso PF ottengo errore BOUCED sul email e solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd                          |
      | messageId      | ${IT}                                   |
      | subject        | Test workflow                           |
      | recipientType  | PF                                      |
      | taxId          | FRMTTR76M06B715E                        |
      | denomination   | Ettore Fieramosca                       |
      | digitalAddress | suppressionlist@simulator.amazonses.com |
      | pec_address    | NULL                                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_D] Come ente mittente invio una notifica bonaria verso PF ottengo errore BOUCED sul email e solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd                 |
      | messageId      | ${IT}                          |
      | subject        | Test workflow                  |
      | recipientType  | PF                             |
      | taxId          | FRMTTR76M06B715E               |
      | denomination   | Ettore Fieramosca              |
      | digitalAddress | bounce@simulator.amazonses.com |
      | pec_address    | NULL                           |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_3_A] Come ente mittente invio una notifica bonaria verso PG ottengo feedback desiderato sulla PEC ricevuta
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd        |
      | messageId      | ${IT}                 |
      | subject        | Test workflow         |
      | recipientType  | PG                    |
      | taxId          | 20517490320           |
      | denomination   | Acme spa              |
      | digitalAddress | NULL                  |
      | pec_address    | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_REACHED" della notifica bonaria esista e sia correttamente compilato


# todo corretto ended? si perchè il fb è recived
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_3_B] Come ente mittente invio una notifica bonaria verso PG ottengo errore sulla PEC e solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd             |
      | messageId      | ${IT}                      |
      | subject        | Test workflow              |
      | recipientType  | PG                         |
      | taxId          | 20517490320                |
      | denomination   | Acme spa                   |
      | digitalAddress | NULL                       |
      | pec_address    | example@FAIL-pecFirstKO.it |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_4_A] Come ente mittente invio una notifica bonaria che termoina senza aver trovato alcun recapito
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd    |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | NULL              |
      | pec_address    | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_4_B] Come ente mittente invio una notifica bonaria che termina senza aver trovato nessun recapito
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd |
      | messageId      | ${IT}          |
      | subject        | Test workflow  |
      | recipientType  | PG             |
      | taxId          | 20517490320    |
      | denomination   | Acme spa       |
      | digitalAddress | NULL           |
      | pec_address    | NULL           |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato








#todo da verificare se possibile in quanto il desiderato è già recived quindi non potrà arrivare feedb. negativo? in Asyncrono?
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_5_A] Come ente mittente invio una notifica bonaria verso PF ricevo feedback desiderato ma non..
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd    |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | todo              |
      | pec_address    | todo              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_UNREACHED" della notifica bonaria esista e sia correttamente compilato

  #todo da verificare se possibile in quanto il desiderato è già recived quindi non potrà arrivare feedb. in quanto pec
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_5_B] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd |
      | messageId      | ${IT}          |
      | subject        | Test workflow  |
      | recipientType  | PG             |
      | taxId          | 20517490320    |
      | denomination   | Acme spa       |
      | digitalAddress | todo           |
      | pec_address    | todo           |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_UNREACHED" della notifica bonaria esista e sia correttamente compilato




  #todo visualizzazione..
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_6] Come ente mittente invio una notifica bonaria e solo feddback negativie e in seguito viene visualizzata
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd                    |
      | messageId      | ${IT}                             |
      | subject        | Test workflow                     |
      | recipientType  | PF                                |
      | taxId          | FRMTTR76M06B715E                  |
      | denomination   | Ettore Fieramosca                 |
      | digitalAddress | complaint@simulator.amazonses.com |
      | pec_address    | NULL                              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato
    # visualizza
    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"








# ***********************************************
# **** Reminder
# **** SoricalReminder
# ***********************************************

#todo da integrare con altri scenari
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_1] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder   |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | todo ok           |
      | pec_address    | NULL              |
      | phone_number   | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria
    And si verifica che la notifica bonaria sia in stato "PROCESSING"

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_A] Come ente mittente invio una notifica bonaria verso PF e ottengo il desiderato ricevuto dell' email
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder   |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | todo ok           |
      | pec_address    | NULL              |
      | phone_number   | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_REACHED" della notifica bonaria esista e sia correttamente compilato

  #todo fallimento analogico
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_B] Come ente mittente invio una notifica bonaria verso PF ricevo solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder                   |
      | messageId      | ${IT}                             |
      | subject        | Test workflow                     |
      | recipientType  | PF                                |
      | taxId          | FRMTTR76M06B715E                  |
      | denomination   | Ettore Fieramosca                 |
      | digitalAddress | complaint@simulator.amazonses.com |
      | pec_address    | NULL                              |
      | phone_number   | NULL                              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_C] Come ente mittente invio una notifica bonaria verso PF

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_D] Come ente mittente invio una notifica bonaria verso PF


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_3_A] Come ente mittente invio una notifica bonaria verso PG ottengo feedback desideraro
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder |
      | messageId      | ${IT}           |
      | subject        | Test workflow   |
      | recipientType  | PG              |
      | taxId          | 20517490320     |
      | denomination   | Acme spa        |
      | digitalAddress | NULL            |
      | pec_address    | example@OK-pecSuccess.it        |
      | phone_number   | NULL            |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_REACHED" della notifica bonaria esista e sia correttamente compilato

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_3_B] Come ente mittente invio una notifica bonaria non ottengo il feedback desiderato pec ricevuta
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder |
      | messageId      | ${IT}           |
      | subject        | Test workflow   |
      | recipientType  | PG              |
      | taxId          | 20517490320     |
      | denomination   | Acme spa        |
      | digitalAddress | todo            |
      | pec_address    | todo            |
      | phone_number   | todo            |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    #todo rivedi
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_FEEDBACK" della notifica bonaria
    And si verifica che la notifica bonaria sia in stato "PROCESSING"
    #  esito finale dipende dall sms todo




# todo rivedi prog
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_4_A] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_4_B] Come ente mittente invio una notifica bonaria...


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_5_A] Come ente mittente invio una notifica bonaria verso PF senza alcun recapito trovato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder   |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | NULL              |
      | pec_address    | NULL              |
      | phone_number   | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_5_B] Come ente mittente invio una notifica bonaria verso PG senza alcun recapito trovato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder |
      | messageId      | ${IT}           |
      | subject        | Test workflow   |
      | recipientType  | PG              |
      | taxId          | 20517490320     |
      | denomination   | Acme spa        |
      | digitalAddress | NULL            |
      | pec_address    | NULL            |
      | phone_number   | NULL            |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_6] Come ente mittente invio una notifica bonaria ricevendo solo feddback negativi e visualizzazione
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder             |
      | messageId      | ${IT}                      |
      | subject        | Test workflow              |
      | recipientType  | PG                         |
      | taxId          | 20517490320                |
      | denomination   | Acme spa                   |
      | digitalAddress | NULL                       |
      | pec_address    | example@FAIL-pecFirstKO.it |
      | phone_number   | NULL                       |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato
    # visualizza
    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"



#todo con feedback recived possiamo avere il non recapitato?
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_7_A] Come ente mittente invio una notifica bonaria verso PF ricevo feedback desiderato ma la notifica non è recapitata

    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_UNREACHED" della notifica bonaria esista e sia correttamente compilato

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_7_B] Come ente mittente invio una notifica bonaria verso PG ricevo feedback desiderato ma la notifica non è recapitata

    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_UNREACHED" della notifica bonaria esista e sia correttamente compilato



    # ***********************************************
# **** Messa in mora
# **** SoricalMessaMora
# ***********************************************

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_2_A] Come ente mittente invio una notifica bonaria verso PG e ricevo feedback desiderato sulla ricezionen della pec
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalMessaMora |
      | messageId      | ${IT}            |
      | subject        | Test workflow    |
      | recipientType  | PG               |
      | taxId          | 20517490320      |
      | denomination   | Acme spa         |
      | digitalAddress | NULL             |
      | pec_address    | todo  ok         |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_REACHED" della notifica bonaria esista e sia correttamente compilato

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_2_B] Come ente mittente invio una notifica bonaria verso PG ricevo solo feedback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | SoricalMessaMora           |
      | messageId        | ${IT}                      |
      | subject          | Test workflow              |
      | recipientType    | PG                         |
      | taxId            | 20517490320                |
      | denomination     | Acme spa                   |
      | digitalAddress   | NULL                       |
      | pec_address      | example@FAIL-pecFirstKO.it |
      #| taxId            | XVRSFN76E31L781N         |
      #| denomination     | xavier                   |
      | physical_address | ${PHYSICAL_ADDRESS_NULL}   |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_3_A] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | SoricalMessaMora                         |
      | messageId        | ${IT}                                   |
      | subject          | Test workflow                           |
      | recipientType    | PF                                      |
      | taxId            | FRMTTR76M06B715E                        |
      | denomination     | Ettore Fieramosca                       |
      | digitalAddress   | suppressionlist@simulator.amazonses.com |
      | pec_address      | NULL                                    |
      | physical_address | Via@ok_RS                               |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_REACHED" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_3_B] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | SoricalMessaMora           |
      | messageId        | ${IT}                      |
      | subject          | Test workflow              |
      | recipientType    | PG                         |
      | taxId            | 20517490320                |
      | denomination     | Acme spa                   |
      | digitalAddress   | NULL                       |
      | pec_address      | example@FAIL-pecFirstKO.it |
      | physical_address | Via@ok_RS                  |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_REACHED" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_4] Come ente mittente invio una notifica bonaria verso PG ottenendo solo feedback negativi, in seguito visualizza
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | SoricalMessaMora           |
      | messageId        | ${IT}                      |
      | subject          | Test workflow              |
      | recipientType    | PG                         |
      | taxId            | 20517490320                |
      | denomination     | Acme spa                   |
      | digitalAddress   | NULL                       |
      | pec_address      | example@FAIL-pecFirstKO.it |
      | physical_address | todo ko                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato
    #todo visualizza
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_5] Come ente mittente invio una notifica bonaria verso PG ottenendo solo feedback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | SoricalMessaMora           |
      | messageId        | ${IT}                      |
      | subject          | Test workflow              |
      | recipientType    | PG                         |
      | taxId            | 20517490320                |
      | denomination     | Acme spa                   |
      | digitalAddress   | NULL                       |
      | pec_address      | example@FAIL-pecFirstKO.it |
      | physical_address | todo ko                    |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_6_A] Come ente mittente invio una notifica bonaria verso PF e nessun recapito trovato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalMessaMora   |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | todo errore       |
      | pec_address    | NULL              |
      | phone_number   | NULL              |
      | physical_address | ${PHYSICAL_ADDRESS_NULL}   |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_6_B] Come ente mittente invio una notifica bonaria verso PG con nessun recapiuto trovato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalMessaMora |
      | messageId      | ${IT}            |
      | subject        | Test workflow    |
      | recipientType  | PG               |
      | taxId          | 20517490320      |
      | denomination   | Acme spa         |
      | pec_address    | NULL              |
      | phone_number   | NULL              |
      | physical_address | ${PHYSICAL_ADDRESS_NULL}   |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato



  # SKIP *************

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_04_1_A] Come ente mittente invio una notifica bonaria verso PF e verifico i canali skippati
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder   |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | NULL       |
      | pec_address    | NULL              |
      | phone_number   | NULL              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_04_1_B] Come ente mittente invio una notifica bonaria verso PG e verifico i canali skippati
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalMessaMora |
      | messageId      | ${IT}            |
      | subject        | Test workflow    |
      | recipientType  | PG               |
      | taxId          | 20517490320      |
      | denomination   | Acme spa         |
      | pec_address    | NULL              |
      | phone_number   | NULL              |
      | physical_address | ${PHYSICAL_ADDRESS_NULL}   |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria esista e sia correttamente compilato






  # AUDIT LOG ********

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_05_1_A] Come ente mittente invio una notifica bonaria verso PG con pec e analogico e verifico gli auditlog
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalMessaMora |
      | messageId      | ${IT}            |
      | subject        | Test workflow    |
      | recipientType  | PG               |
      | taxId          | 20517490320      |
      | denomination   | Acme spa         |
      | digitalAddress | todo             |
      | pec_address    | todo             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    # pec e analogico

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_05_1_B] Come ente mittente invio una notifica bonaria verso PF con email e telefono e verifico gli auditlog
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder   |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | todo errore       |
      | pec_address    | todo              |
      | phone_number   | todo              |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    # email sms



  #todo dettaglio dest/mittente + download doc/attach