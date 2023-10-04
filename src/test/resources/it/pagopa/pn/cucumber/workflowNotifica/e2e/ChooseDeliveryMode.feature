Feature: Scelta canale di invio (Digitale o analogico)

  @e2e @platformDependent
  Scenario: [E2E-CHOOSE-DELIVERY-MODE-1] Invio notifica mono destinatario. L’utente ha configurato l’indirizzo di piattaforma
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |

  @e2e @platformDependent
  Scenario: [E2E-CHOOSE-DELIVERY-MODE-2] Invio notifica mono destinatario. L’utente NON ha configurato l’indirizzo di piattaforma MA ha valorizzato l’indirizzo Speciale
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then  viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |

  @e2e @OnlyEnvTest @platformDependent
  Scenario: [E2E-CHOOSE-DELIVERY-MODE-3] Invio notifica mono destinatario. L’utente NON ha configurato l’indirizzo di piattaforma,
  NON ha valorizzato l’indirizzo Speciale MA ha valorizzato l’indirizzo GENERALE
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Louis Armstrong |
      | taxId | RMSLSO31M04Z404R |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |

  @e2e
  Scenario: [E2E-CHOOSE-DELIVERY-MODE-4] Invio notifica mono destinatario. L’utente non ha configurato nessuno degli indirizzi digitali
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination | Leonardo da Vinci |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SCHEDULE_ANALOG_WORKFLOW" esista
      | loadTimeline | true |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |
    And viene verificato che l'elemento di timeline "GET_ADDRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
      | details_isAvailable | false |

