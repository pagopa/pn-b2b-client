Feature: deleghe test new feature

  @Annullamento @deleghe1
  Scenario:  [B2B-PF-ANNULLAMENTO_26] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @Annullamento @deleghe1
  Scenario:  [B2B-PF-ANNULLAMENTO_27] PA mittente: annullamento notifica in cui è presente un delegato e verifica dell’annullamento sia da parte del destinatario che del delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then la notifica può essere correttamente recuperata da "Mario Cucumber"
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega


  @Annullamento @deleghe1 @ignore
  Scenario: [B2B-PA-ANNULLAMENTO_32] Invio notifica digitale mono destinatario e recupero documento notificato_scenario negativo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    When vengono letti gli eventi fino allo stato della notifica "CANCELLED"
    Then il documento notificato non può essere correttamente recuperato da "Mario Gherkin" con delega restituendo un errore "404"

  @deleghe1 @AOO_UO
  Scenario: [WEB-PF-MANDATE_17] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [WEB-PF-MANDATE_18] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    And l'allegato "PAGOPA" può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [WEB-PF-MANDATE_19] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
      | feePolicy          | DELIVERY_MODE              |
      | paFee              | 0                          |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NULL               |
      | payment_f24flatRate  | NULL               |
      | payment_f24standard  | SI                 |
      | title_payment        | F24_STANDARD_MARIO |
      | apply_cost_pagopa    | NO                 |
      | apply_cost_f24       | SI                 |
      | payment_multy_number | 1                  |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    And l'allegato "F24" può essere correttamente recuperato da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [WEB-PF-MANDATE_20] Invio notifica da parte di ente padre e lettura da delegato
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Root"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu                |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @deleghe1 @AOO_UO
  Scenario: [WEB-PF-MANDATE_21] Invio notifica digitale altro destinatario e recupero_scenario positivo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    Given "Mario Gherkin" viene delegato da "Mario Cucumber" per comune "Comune_Son"
    Then l'operazione ha generato un errore con status code "422"

  @Annullamento @deleghe1
  Scenario: [B2B-PA-ANNULLAMENTO_33] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario negativo
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    Then il documento notificato non può essere correttamente recuperato da "Mario Gherkin" con delega restituendo un errore "404"

  @deleghe2 @AOO_UO
  Scenario: [WEB-PG-MANDATE_19] Invio notifica digitale altro destinatario e recupero_scenario positivo da parte di ente radice
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega

  @deleghe2 @AOO_UO
  Scenario: [WEB-PG-MANDATE_20] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo da parte di ente radice
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Ufficio per la transizione al Digitale         |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | SI   |
      | payment_f24standard | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    And l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega

  @deleghe2 @AOO_UO
  Scenario: [WEB-PG-MANDATE_21] Invio notifica digitale altro destinatario e recupero AAR e Attestazione Opponibile positivo da parte di ente radice
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Ufficio per la transizione al Digitale          |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm   | NULL                 |
      | payment_f24flatRate  | NULL                 |
      | payment_f24standard  | SI                   |
      | title_payment        | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa    | NO                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    And l'allegato "F24" può essere correttamente recuperato da "CucumberSpa" con delega

  @deleghe2 @AOO_UO
  Scenario: [WEB-PG-MANDATE_22] Invio notifica digitale altro destinatario per ente figlio e fallimento invio
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_Root"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Ufficio per la transizione al Digitale         |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega

  @pagamentiMultipli @deleghe2
  Scenario: [B2B-PA-PAY_MULTI_PG_69] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante e Delegato scaricano correttamenta l'avviso pagoPA e F24
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | SI |
      | title_payment | F24_STANDARD_GHERKING_SRL |
      | apply_cost_pagopa | SI |
      | apply_cost_f24 | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega
    And "GherkinSrl" tenta il recupero dell'allegato "PAGOPA"
    And il download non ha prodotto errori
    And l'allegato "F24" può essere correttamente recuperato da "CucumberSpa" con delega
    And "GherkinSrl" tenta il recupero dell'allegato "F24"
    And il download non ha prodotto errori


  #70 Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante paga avviso1 e delegato paga avviso1 (Stesso avviso - pagoPA)
  @pagamentiMultipli @deleghe2
  Scenario: [B2B-PA-PAY_MULTI_PG_70] Notifica con delega e presenza contemporanea di avviso pagoPA e F24: Delegante e Delegato scaricano correttamenta l'avviso pagoPA
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
      | feePolicy | DELIVERY_MODE |
      | paFee | 0 |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm  | SI   |
      | payment_f24flatRate | NULL   |
      | payment_f24standard | NULL |
      | apply_cost_pagopa | SI |
      | payment_multy_number | 1 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega
    And "GherkinSrl" tenta il recupero dell'allegato "PAGOPA"
    And il download non ha prodotto errori

    #CE02.9 Come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come “delegato” di una persona fisica o di una persona giuridica.

  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.9_64] Invocazione del servizio e verifica risposta
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And Il servizio risponde correttamente

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE | MANDATE_ID | INTERNAL_ID |
      | Mario Gherkin | PF             | 1                | NULL                 | NULL       | NULL     | NO_SET     | NO_SET      |
    #Mario Gherkin - DELEGATO - CLMCST42R12D969Z
    #Mario Cucumber -DELEGANTE - FRMTTR76M06B715E


  @deleghe2  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-PG-CA_CE02.9_64] Invocazione del servizio e verifica risposta
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And Il servizio risponde correttamente

    Examples:
      | TAXIID      | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE | MANDATE_ID | INTERNAL_ID |
      | CucumberSpa | PG             | 1                | NULL                 | NULL       | NULL     | NO_SET     | NO_SET      |


  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.9_65] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica ma senza searchMandateId
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE | MANDATE_ID | INTERNAL_ID |
      | Mario Gherkin | PF             | 1                | NULL                 | NULL       | NULL     | NULL       | NO_SET      |
    #Errore: 400 BAD_REQUEST 400 Missing the required parameter 'mandateId' when calling searchNotificationsAsDelegateFromInternalId null

  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.9_66] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica, con searchMandateId ma senza searchDelegateInternalId
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegato" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID        | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE | MANDATE_ID | INTERNAL_ID |
      | Mario Gherkin | PF             | 1                | NULL                 | NULL       | NULL     | NO_SET     | NULL        |
    # Errore: 400 BAD_REQUEST 400 Missing the required parameter 'delegateInternalId' when calling searchNotificationsAsDelegateFromInternalId null

   #TODO Verificare il comportamento corretto...
  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.9_67] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica, ma con searchMandateId non coerente con il searchDelegateInternalId
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegante" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And il servizio risponde con errore "400"

    Examples:
      | TAXIID         | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE | MANDATE_ID               | INTERNAL_ID |
      | Mario Cucumber | PF             | 10               | NULL                 | NULL       | NULL     | z7942f2e-1037-4ed9-8ca6- | NO_SET      |

    # Response 400 INTERNAL_SERVER_ERROR

  #CE02.10 Come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario ma sono state “trattate” da altro utente da lui “delegato”

  @deleghe1  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.10_74] Invocazione del servizio e verifica risposta
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    And come operatore devo accedere ai dati del profilo di un utente (PF e PG) di Piattaforma Notifiche con taxId "<TAXIID>" e recipientType  "<RECIPIENT_TYPE>"
    And Il servizio risponde correttamente
    Then come operatore devo accedere alla lista delle Notifiche per le quali l’utente risulta destinatario come "delegante" di una persona fisica o di una persona giuridica con taxId "<TAXIID>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>" searchMandateId "<MANDATE_ID>" searchInternalId "<INTERNAL_ID>"
    And Il servizio risponde correttamente

    Examples:
      | TAXIID         | RECIPIENT_TYPE | SEARCH_PAGE_SIZE | SEARCH_NEXT_PAGE_KEY | START_DATE | END_DATE | MANDATE_ID | INTERNAL_ID |
      | Mario Cucumber | PF             | 1                | NULL                 | NULL       | NULL     | NO_SET     | NO_SET      |

