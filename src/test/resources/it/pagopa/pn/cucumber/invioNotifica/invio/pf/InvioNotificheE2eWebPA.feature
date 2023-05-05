Feature: invio notifiche e2e web PA

  @WebPAtest @testLite
  Scenario: [WEB_PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN web PA_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN web PA



  @WebPAtest @testLite
  Scenario: [WEB_PA-SEND_2] Invio notifica digitale senza pagamento e recupero tramite codice IUN web PA_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla web PA "Comune_Multi"


  @WebPAtest @testLite
  Scenario: [WEB_PA-SEND_3] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
    Then la notifica può essere correttamente recuperata dal sistema tramite Stato "ACCEPTED" dalla web PA "Comune_1"
