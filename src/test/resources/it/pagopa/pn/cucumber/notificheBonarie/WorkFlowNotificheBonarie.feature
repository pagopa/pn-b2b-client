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
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine

  #----------- STEP --------------------------------------------

    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"

    And si attende che venga prodotto l'elemento "TODO" della notifica bonaria
    And viene verificato che l'elemento di timeline "TODO" della notifica bonaria esista e sia correttamente compilato


    And  si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_REACHED" della notifica bonaria esista e sia correttamente compilato

    And  si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_UNREACHED" della notifica bonaria esista e sia correttamente compilato

    And  si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_REACHED" della notifica bonaria esista e sia correttamente compilato

    And  si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato

    And  si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato


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
      | digitalAddress | todo              |
      | pec_address    | todo              |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"

    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "PROCESSING"


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_A] Come ente mittente invio una notifica bonaria, ottengo il feedback desiderato sulla pec
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
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"

    And  si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_REACHED" della notifica bonaria esista e sia correttamente compilato





  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_B] Come ente mittente invio una notifica bonaria verso PF ottengo errore sul email
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
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And  si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_C] Come ente mittente invio una notifica bonaria verso PF ottengo errore sul email
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
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And  si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_2_D] Come ente mittente invio una notifica bonaria verso PF ottengo errore sul email
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
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And  si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato








  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_3_A] Come ente mittente invio una notifica bonaria verso PG ottengo feedback desiderato sulla PEC
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd    |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PG                |
      | taxId          | todo  |
      | denomination   | todo |
      | digitalAddress | todo              |
      | pec_address    | todo              |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And  si verifica che la notifica bonaria sia in stato "COMPLETED_REACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_DONE_REACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_DONE_REACHED" della notifica bonaria esista e sia correttamente compilato


# todo corretto ended? si perchè il fb è recived
  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_3_B] Come ente mittente invio una notifica bonaria verso PG ottengo errore sulla PEC
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd    |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PG                |
      | taxId          | todo  |
      | denomination   | todo |
      | digitalAddress | todo              |
      | pec_address    | todo              |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And  si verifica che la notifica bonaria sia in stato "COMPLETED_UNREACHED"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNREACHED" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNREACHED" della notifica bonaria esista e sia correttamente compilato





  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_4_A] Come ente mittente invio una notifica bonaria...
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
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And  si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_4_B] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalFattOrd    |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PG                |
      | taxId          | todo  |
      | denomination   | todo |
      | digitalAddress | todo              |
      | pec_address    | todo              |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And  si verifica che la notifica bonaria sia in stato "UNDELIVERABLE"
    And si attende che venga prodotto l'elemento "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria
    And viene verificato che l'elemento di timeline "WORKFLOW_ENDED_UNDELIVERABLE" della notifica bonaria esista e sia correttamente compilato









  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_5_A] Come ente mittente invio una notifica bonaria...
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
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_5_B] Come ente mittente invio una notifica bonaria...
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
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_01_6] Come ente mittente invio una notifica bonaria...
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
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"











# ***********************************************
# **** Reminder
# **** SoricalReminder
# ***********************************************


  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_1] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | SoricalReminder   |
      | messageId      | ${IT}             |
      | subject        | Test workflow     |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | digitalAddress | todo              |
      | pec_address    | todo              |
      | phone_number   | todo              |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_A] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_B] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_C] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_2_D] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_3_A] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_3_B] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_4_A] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_4_B] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_5_A] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_5_B] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_6_0] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_7_A] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_02_7_B] Come ente mittente invio una notifica bonaria...

    # ***********************************************
# **** Messa in mora
# **** SoricalMessaMora
# ***********************************************

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_2_A] Come ente mittente invio una notifica bonaria...
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | SoricalMessaMora   |
      | messageId     | ${IT}             |
      | subject       | Test workflow     |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | pec_address   | todo              |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_2_B] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_00_0_0] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_3_A] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_3_B] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_4] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_5_A] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_5_B] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_6_A] Come ente mittente invio una notifica bonaria...

  @informalNotificationsWorkFlow
  Scenario: [NOTIFICHE_BONARIE_WORKFLOW_03_6_B] Come ente mittente invio una notifica bonaria...


  # SKIP *************

  # AUDIT LOG ********