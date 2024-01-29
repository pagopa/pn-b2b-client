Feature: Deleghe Cruscotto Assitenza
  Background:
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"

  @deleghe1  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.9_64] Invocazione del servizio e verifica risposta
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Mario Gherkin" e recipientType  "PF"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "Mario Cucumber" recipientType  "PF" e con searchPageSize "1" searchNextPagesKey "NULL" startDate "NULL" endDate "NULL" searchMandateId "NO_SET" searchInternalId "NO_SET"
    And Il servizio risponde correttamente



  @deleghe2  @cruscottoAssistenza @ignore
  Scenario: [API-SERVICE-PG-CA_CE02.9_64] Invocazione del servizio e verifica risposta
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "CucumberSpa" e recipientType  "PG"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "GherkinSrl" recipientType  "PG" e con searchPageSize "1" searchNextPagesKey "NULL" startDate "NULL" endDate "NULL" searchMandateId "NO_SET" searchInternalId "NO_SET"
    And Il servizio risponde correttamente



  @deleghe1  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.9_65] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica ma senza searchMandateId
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Mario Gherkin" e recipientType  "PF"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "Mario Cucumber" recipientType  "PF" e con searchPageSize "1" searchNextPagesKey "NULL" startDate "NULL" endDate "NULL" searchMandateId "NULL" searchInternalId "NO_SET"
    And il servizio risponde con errore "400"

  @deleghe1  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.9_66] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica, con searchMandateId ma senza searchDelegateInternalId
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Mario Gherkin" e recipientType  "PF"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "Mario Cucumber" recipientType  "PF" e con searchPageSize "1" searchNextPagesKey "NULL" startDate "NULL" endDate "NULL" searchMandateId "NO_SET" searchInternalId "NULL"
    And il servizio risponde con errore "400"

   #TODO Verificare il comportamento corretto...
  @deleghe1  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.9_67] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica, ma con searchMandateId non coerente con il searchDelegateInternalId
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Mario Cucumber" e recipientType  "PF"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegante" di una persona fisica o di una persona giuridica con taxId "Mario Gherkin" recipientType  "PF" e con searchPageSize "10" searchNextPagesKey "NULL" startDate "NULL" endDate "NULL" searchMandateId "z7942f2e-1037-4ed9-8ca6-" searchInternalId "NO_SET"
    And il servizio risponde con errore "400"

    # Response 400 INTERNAL_SERVER_ERROR

  #CE02.10 Come operatore devo accedere alla lista delle Notifiche per le quali l'utente risulta destinatario ma sono state "trattate" da altro utente da lui "delegato"

  @deleghe1  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.10_74] Invocazione del servizio e verifica risposta
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "Mario Cucumber" e recipientType  "PF"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegante" di una persona fisica o di una persona giuridica con taxId "Mario Gherkin" recipientType  "PF" e con searchPageSize "1" searchNextPagesKey "NULL" startDate "NULL" endDate "NULL" searchMandateId "NO_SET" searchInternalId "NO_SET"
    And Il servizio risponde correttamente
