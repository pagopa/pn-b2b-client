Feature: Scelta canale di invio (Digitale o analogico)

  @e2e @ignore
  Scenario: [E2E-CHOOSE-DELIVERY-MODE-1] Invio notifica mono destinatario. L’utente ha configurato l’indirizzo di piattaforma
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Cristoforo Colombo
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica GET_ADDRESS con il campo digitalAddressSource a "PLATFORM" e isAvailable a "true"

  @e2e @ignore
  Scenario: [E2E-CHOOSE-DELIVERY-MODE-2] Invio notifica mono destinatario. L’utente NON ha configurato l’indirizzo di piattaforma MA ha valorizzato l’indirizzo Speciale
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica GET_ADDRESS con il campo digitalAddressSource a "PLATFORM" e isAvailable a "false"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica GET_ADDRESS con il campo digitalAddressSource a "SPECIAL" e isAvailable a "true"

  @e2e @ignore
  Scenario: [E2E-CHOOSE-DELIVERY-MODE-3] Invio notifica mono destinatario. L’utente NON ha configurato l’indirizzo di piattaforma,
  NON ha valorizzato l’indirizzo Speciale MA ha valorizzato l’indirizzo GENERALE
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Louis Armstrong
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica GET_ADDRESS con il campo digitalAddressSource a "PLATFORM" e isAvailable a "false"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica GET_ADDRESS con il campo digitalAddressSource a "SPECIAL" e isAvailable a "false"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica GET_ADDRESS con il campo digitalAddressSource a "GENERAL" e isAvailable a "true"

  @e2e @ignore
  Scenario: [E2E-CHOOSE-DELIVERY-MODE-4] Invio notifica mono destinatario. L’utente non ha configurato nessuno degli indirizzi digitali
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario Dino De Sauro
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica GET_ADDRESS con il campo digitalAddressSource a "PLATFORM" e isAvailable a "false"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica GET_ADDRESS con il campo digitalAddressSource a "SPECIAL" e isAvailable a "false"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica GET_ADDRESS con il campo digitalAddressSource a "GENERAL" e isAvailable a "false"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"

