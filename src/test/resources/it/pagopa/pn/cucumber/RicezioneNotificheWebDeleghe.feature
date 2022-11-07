Feature: Ricezione notifiche destinate al delegante

  Background:
    Given Cristoforo colombo rifiuta se presente la delega ricevuta da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"

  #@SmokeTest
  Scenario: [WEB-PF-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta dal delegato

  #@SmokeTest
  Scenario: [WEB-PF-MANDATE_2] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere correttamente recuperato dal delegato

  Scenario: [WEB-PF-MANDATE_3] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato dal delegato


  Scenario: [WEB-PF-MANDATE_4] Invio notifica digitale mono destinatario e recupero allegato F24_FLAT_scenario positivo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | SI |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato dal delegato

  Scenario: [WEB-PF-MANDATE_5] Invio notifica digitale mono destinatario e recupero allegato F24_STANDARD_scenario positivo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
      | payment_pagoPaForm | SI |
      | payment_f24flatRate | NULL |
      | payment_f24standard | SI |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then l'allegato "F24" può essere correttamente recuperato dal delegato

  Scenario: [WEB-PF-MANDATE_6] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E" revoca la delega a Cristoforo Colombo
    Then si tenta la lettura della notifica da parte del delegato che produce un errore con status code "404"

  Scenario: [WEB-PF-MANDATE_7] Invio notifica digitale altro destinatario e recupero allegato pagopa_scenario negativo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo rifiuta la delega da "FRMTTR76M06B715E"
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then si tenta la lettura della notifica da parte del delegato che produce un errore con status code "404"


  Scenario: [WEB-PF-MANDATE_8] Delega a se stesso _scenario negativo
    Given Cristoforo Colombo viene delegato da "Cristoforo" "Colombo" con cf "CLMCST42R12D969Z"
    Then l'operazione di delega ha prodotto un errore con status code "409"

  Scenario: [WEB-PF-MANDATE_9] delega duplicata_scenario negativo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    And Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    Then l'operazione di delega ha prodotto un errore con status code "409"

  Scenario: [WEB-PF-MANDATE_10] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta dal delegato
    And la notifica può essere correttamente letta dal destinatario "FRMTTR76M06B715E"

  Scenario: [WEB-PF-MANDATE_11] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    When viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta dal destinatario "FRMTTR76M06B715E"
    And la notifica può essere correttamente letta dal delegato

  Scenario: [WEB-PF-MULTI-MANDATE_1] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given Cristoforo Colombo viene delegato da "Ettore" "Fieramosca" con cf "FRMTTR76M06B715E"
    And Cristoforo Colombo accetta la delega da "FRMTTR76M06B715E"
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
      | senderTaxId | 80016350821 |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
    And destinatario
      | denomination | Mario Gherkin |
      | taxId | FRMTTR76M06B715E |
      | digitalDomicile_address | FRMTTR76M06B715E@pnpagopa.postecert.local |
    When la notifica viene inviata tramite api b2b dalla PA "GA" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta dal destinatario "FRMTTR76M06B715E"
    And la notifica può essere correttamente letta dal delegato


