Feature: invio notifiche e2e web PA

  @WebPAtest @ignore
  Scenario: [WEB_PA-SEND_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN web PA_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN web PA



  @WebPAtest  @ignore
  Scenario: [WEB_PA-SEND_2] Invio notifica digitale senza pagamento e recupero tramite codice IUN web PA_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata dal sistema tramite codice IUN dalla web PA "Comune_Multi"


  @WebPAtest  @ignore
  Scenario: [WEB_PA-SEND_3] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "ACCEPTED"
    Then la notifica può essere correttamente recuperata dal sistema tramite Stato "ACCEPTED" dalla web PA "Comune_1"

  @SmokeTest  @WebPAtest @testLite
  Scenario: [WEB_PA-SEND_4] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_PA_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca da web PA "Comune_1"
      | startDate | 01/01/2022 |
      | endDate | 01/10/2030 |
      | iunMatch | ACTUAL |
      | subjectRegExp | cucumber |

  @WebPAtest
  Scenario: [WEB_PA-SEND_5] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_PA_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca da web PA "Comune_1"
      |||

  @WebPAtest
  Scenario: [WEB_PA-SEND_6] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_PA_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca da web PA "Comune_1"
      | subjectRegExp | cucumber |

  @WebPAtest
  Scenario: [WEB_PA-SEND_7] Invio notifica digitale mono destinatario e recupero tramite ricerca API WEB_PA_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata con una ricerca da web PA "Comune_1"
      | startDate | 01/01/2022 |
      | subjectRegExp | cucumber |