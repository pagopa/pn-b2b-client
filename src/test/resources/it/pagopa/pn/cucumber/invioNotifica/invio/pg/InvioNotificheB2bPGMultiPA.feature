Feature: invio notifiche b2b con altre PA, multi-destinatario e senza pagamento per persona giuridica

  Scenario: [B2B-PG-MULTI-PA_01] Invio notifica digitale_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica multi cucumber |
      | senderDenomination | Comune di verona |
    And destinatario Gherkin spa
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si tenta il recupero dal sistema tramite codice IUN dalla PA "Comune_1"
    And l'operazione ha generato un errore con status code "404"

  Scenario: [B2B-PG-MULTI-PA_02] Invio notifica digitale senza pagamento_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"

  Scenario: [B2B-PG-MULTI-PA_03] Invio notifica multi destinatario senza pagamento_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
      | payment | NULL |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | FRMTTR76M06B715E@pec.pagopa.it |
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"

  Scenario: [B2B-PG-MULTI-PA_04] Invio notifica multi destinatario con pagamento_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"

  Scenario: [B2B-PG-MULTI-PA_05] Invio notifica multi destinatario PA non abilitata_scenario negativa
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'invio ha prodotto un errore con status code "400"

  Scenario: [B2B-PG-MULTI-PA_06] Invio notifica multi destinatario uguale codice avviso_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa
    And destinatario "Mario Cucumber" con uguale codice avviso del destinario numero 1
      | digitalDomicile_address | FRMTTR76M06B715E@pec.pagopa.it |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'invio ha prodotto un errore con status code "500"

  Scenario: [B2B-PG-MULTI-PA_07] Invio notifica multi destinatario senza pagamento_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa e:
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
      | payment                 | NULL                      |
    And destinatario Cucumber srl e:
      |     payment   |     NULL     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"

  Scenario: [B2B-PG-MULTI-PA_08] Invio notifica multi destinatario con pagamento_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla PA "Comune_Multi"

  Scenario: [B2B-PG-MULTI-PA_09] Invio notifica multi destinatario PA non abilitata_scenario negativa
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa
    And destinatario Cucumber srl
    When la notifica viene inviata dal "Comune_1"
    Then l'invio ha prodotto un errore con status code "400"
