Feature: invio notifiche b2b con altre PA, multi-destinatario e senza pagamento

  Scenario: [B2B-MULTI-PA-SEND_1] Invio notifica digitale_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica multi cucumber |
      | senderDenomination | Comune di verona |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si tenta il recupero dal sistema tramite codice IUN dalla PA "Comune_1"
    And l'operazione ha generato un errore con status code "404"

  @SmokeTest
  Scenario: [B2B-PA-GA-SEND_1] Invio notifica digitale senza pagamento_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"

  @SmokeTest
  Scenario: [B2B-PA-GA-SEND_2] Invio notifica multi destinatario senza pagamento_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | CLMCST42R12D969Z@pnpagopa.postecert.local |
      | payment | NULL |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | FRMTTR76M06B715E@pnpagopa.postecert.local |
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"

  @SmokeTest
  Scenario: [B2B-PA-GA-SEND_3] Invio notifica multi destinatario con pagamento_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"

  @SmokeTest
  Scenario: [B2B-PA-GA-SEND_4] Invio notifica multi destinatario PA non abilitata_scenario negativa
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'invio ha prodotto un errore con status code "400"

  @SmokeTest
  Scenario: [B2B-PA-GA-SEND_5] Invio notifica multi destinatario uguale codice avviso_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario "Mario Cucumber" con uguale codice avviso del destinario numero 1
      | digitalDomicile_address | FRMTTR76M06B715E@pnpagopa.postecert.local |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio ha prodotto un errore con status code "500"


  @SmokeTest
  Scenario: [B2B-PA-GA-SEND_6] Invio notifica multi destinatario destinatario duplicato_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    And destinatario Mario Gherkin
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio ha prodotto un errore con status code "400"
