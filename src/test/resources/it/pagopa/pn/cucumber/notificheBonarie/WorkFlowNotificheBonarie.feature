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


  And viene verificato che l'elemento di timeline "TODO" della notifica bonaria esista e sia correttamente compilato
And si attende che venga prodotto l'elemento "TODO" della notifica bonaria

# ***********************************************
# **** Fatturazione Ordinaria
# **** SoricalFattOrd
# ***********************************************

# ***********************************************
# **** Reminder
# **** SoricalReminder
# ***********************************************

# ***********************************************
# **** Messa in mora
# **** SoricalMessaMora
# ***********************************************