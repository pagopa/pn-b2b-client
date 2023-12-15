Feature: Api Service Cruscotto Assitenza

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.1_1] Invocazione del servizio e verifica restituzione array di oggetti con nome e id della PA
    Given l'operatore richiede l'elenco di tutte le PA che hanno effettuato on boarding
    Then Il servizio risponde con esito positivo

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_2] Invocazione del servizio con taxId vuoto
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con cf "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | CF   |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | NULL |         PF   |   NULL             |       NULL            |   NULL   |  NULL  |

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_3] Invocazione del servizio con taxId non formalmente corretto
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con cf "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | CF                                |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | CPNTMS85T15H703WCPNTMS85T15H703W! |         PF   |   NULL             |       NULL            |   NULL   |  NULL  |

  @cruscottoAssistenza
  Scenario: [API-SERVICE-CA_CE02.2_4] Invocazione del servizio con recipientType vuoto
    Given l'operatore richiede l'elenco di tutti i messaggi di cortesia inviati con recipientType vuoto
    Then il servizio risponde con errore "400"

  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_5] Invocazione del servizio con taxId valorizzato correttamente ma non corrispondente al recipientType inserito
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con cf "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | CF               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | CLMCST42R12D969Z |         PG   |   NULL             |       NULL            |   NULL   |  NULL  |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_6] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con cf "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | CF               |RECIPIENT_TYPE  |SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | CLMCST42R12D969Z |         NULL   |   NULL             |       NULL            |   NULL   |  NULL  |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_7] Invocazione del servizio con recipientType valorizzato correttamente ma senza taxId
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con cf "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then il servizio risponde con errore "400"

    Examples:
      | CF   |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | NULL |         PF   |   NULL             |       NULL            |   NULL   |  NULL  |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_8] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con cf "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde con esito positivo

    Examples:
      | CF               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE    |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | CLMCST42R12D969Z |         PF   |   NULL             |       NULL            |   NULL   |  NULL  |


  @cruscottoAssistenza
  Scenario Outline: [API-SERVICE-CA_CE02.2_9] Invocazione del servizio con taxId e recipientType corretti e  1 <searchPageSize> 50
    Given l'operatore richiede elenco di tutti i messaggi di cortesia inviati con cf "<CF>" recipientType  "<RECIPIENT_TYPE>" e con searchPageSize "<SEARCH_PAGE_SIZE>" searchNextPagesKey "<SEARCH_NEXT_PAGE_KEY>" startDate "<START_DATE>" endDate "<END_DATE>"
    Then Il servizio risponde con esito positivo

    Examples:
      | CF               |RECIPIENT_TYPE|SEARCH_PAGE_SIZE |SEARCH_NEXT_PAGE_KEY   |START_DATE|END_DATE|
      | CLMCST42R12D969Z |   PF         |   1             |       NULL            |   NULL   |  NULL  |

  Scenario: [API-SERVICE-CA_CE02.2_10] Invocazione del servizio con taxId e recipientType corretti e  searchPageSize = 0

  Scenario: [API-SERVICE-CA_CE02.2_11] Invocazione del servizio con taxId e recipientType corretti e  searchPageSize = 51

  Scenario: [API-SERVICE-CA_CE02.2_12] Invocazione del servizio con taxId e recipientType corretti e  searchNextPagesKey = 50

  Scenario: [API-SERVICE-CA_CE02.2_13] Invocazione del servizio con taxId e recipientType corretti e  searchNextPagesKey = 51

  Scenario: [API-SERVICE-CA_CE02.2_14] Invocazione del servizio con taxId e recipientType corretti ma con endDate > startDate

  Scenario: [API-SERVICE-CA_CE02.2_15] Invocazione del servizio con taxId e recipientType corretti, con endDate valorizzata ma senza startDate

  Scenario: [API-SERVICE-CA_CE02.2_16] Invocazione del servizio con taxId e recipientType corretti, con startDate valorizzata ma senza endDate

  Scenario: [API-SERVICE-CA_CE02.2_16_1] Invocazione del servizio con taxId e recipientType corretti, con startDate  ed endDate  valorizzati correttamente

  Scenario: [API-SERVICE-CA_CE02.3_17] Invocazione del servizio con taxId vuoto

  Scenario: [API-SERVICE-CA_CE02.3_18] Invocazione del servizio con taxId non formalmente corretto

  Scenario: [API-SERVICE-CA_CE02.3_19] Invocazione del servizio con recipientType vuoto

  Scenario: [API-SERVICE-CA_CE02.3_20] Invocazione del servizio con taxId valorizzato correttamente ma non corrispondente al recipientType inserito

  Scenario: [API-SERVICE-CA_CE02.3_21] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType

  Scenario: [API-SERVICE-CA_CE02.3_22] Invocazione del servizio con recipientTypevalorizzato correttamente ma senza taxId

  Scenario: [API-SERVICE-CA_CE02.3_23] Invocazione del servizio con taxId e recipientType corretti e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.4_24] Invocazione del servizio con taxId vuoto


  Scenario: [API-SERVICE-CA_CE02.4_25] Invocazione del servizio con taxId non formalmente corretto

  Scenario: [API-SERVICE-CA_CE02.4_26] Invocazione del servizio con recipientType vuoto

  Scenario: [API-SERVICE-CA_CE02.4_27] Invocazione del servizio con taxId valorizzato correttamente ma non corrispondente al recipientType inserito

  Scenario: [API-SERVICE-CA_CE02.4_28] Invocazione del servizio con taxId valorizzato correttamente ma senza recipientType

  Scenario: [API-SERVICE-CA_CE02.4_29] Invocazione del servizio con recipientTypevalorizzato correttamente ma senza taxId

  Scenario: [API-SERVICE-CA_CE02.4_30] Invocazione del servizio solo con taxId e recipientType corretti e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.5_39] Invocazione del servizio con IUN vuoto

  Scenario: [API-SERVICE-CA_CE02.5_40] Invocazione del servizio con IUN inesistente

  Scenario: [API-SERVICE-CA_CE02.5_42] Invocazione del servizio con IUN corretto e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.6_43] Invocazione del servizio con taxId valorizzato ma IUN vuoto

  Scenario: [API-SERVICE-CA_CE02.6_44] Invocazione del servizio con taxId valorizzato ma IUN inesistente

  Scenario: [API-SERVICE-CA_CE02.6_46] Invocazione del servizio con IUN corretto ma taxId vuoto

  Scenario: [API-SERVICE-CA_CE02.6_47] Invocazione del servizio con IUN corretto ma taxId non formalmente corretto

  Scenario: [API-SERVICE-CA_CE02.6_48] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica

  Scenario: [API-SERVICE-CA_CE02.6_49] Invocazione del servizio con IUN (notifica mono destinatario)  corretto e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.7_50] Invocazione del servizio con IUN vuoto

  Scenario: [API-SERVICE-CA_CE02.7_51] Invocazione del servizio con IUN inesistente

  Scenario: [API-SERVICE-CA_CE02.7_53] Invocazione del servizio con IUN corretto ma taxId vuoto

  Scenario: [API-SERVICE-CA_CE02.7_54] Invocazione del servizio con IUN corretto ma taxId non formalmente corretto

  Scenario: [API-SERVICE-CA_CE02.7_55] Invocazione del servizio con IUN corretto ma taxId non corrispondente al destinatario della notifica

  Scenario: [API-SERVICE-CA_CE02.7_56] Invocazione del servizio con IUN (notifica multi destinatario) corretto e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.8_57] Invocazione del servizio con IUN vuoto

  Scenario: [API-SERVICE-CA_CE02.8_58] Invocazione del servizio con IUN inesistente

  Scenario: [API-SERVICE-CA_CE02.8_60] Invocazione del servizio con IUN esistente (notifica emessa < 120 gg) e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.8_61] Invocazione del servizio con IUN esistente (notifica emessa > 120 gg) e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.8_62] Invocazione del servizio con IUN esistente, recipientType corretto ma con recipientTaxId non corrispondente al destinatario della notifica

  Scenario: [API-SERVICE-CA_CE02.8_63] Invocazione del servizio con IUN esistente, ma recipientType non coerente rispetto al recipientTaxId della notifica

  Scenario: [API-SERVICE-CA_CE02.9_64] Invocazione del servizio e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.9_65] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica ma senza searchMandateId

  Scenario: [API-SERVICE-CA_CE02.9_66] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica, con searchMandateId ma senza searchDelegateInternalId

  Scenario: [API-SERVICE-CA_CE02.9_67] Invocazione del servizio con IUN esistente, recipientType corretto, recipientTaxId corrispondente al destinatario della notifica, ma consearchMandateId non coerente con ilsearchDelegateInternalId

  Scenario: [API-SERVICE-CA_CE02.10_74] Invocazione del servizio e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.11_79] Invocazione del servizio con paId vuoto

  Scenario: [API-SERVICE-CA_CE02.11_80] Invocazione del servizio con paId inesistente

  Scenario: [API-SERVICE-CA_CE02.11_84] Invocazione del servizio con paId correttamente valorizzato e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.12_90] Invocazione del servizio con IUN vuoto

  Scenario: [API-SERVICE-CA_CE02.12_91] Invocazione del servizio con IUN inesistente

  Scenario: [API-SERVICE-CA_CE02.12_93] Invocazione del servizio con IUN corretto e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.13_94] Invocazione del servizio con IUN vuoto

  Scenario: [API-SERVICE-CA_CE02.13_95] Invocazione del servizio con IUN inesistente

  Scenario: [API-SERVICE-CA_CE02.13_97] Invocazione del servizio con IUN corretto e verifica risposta

  Scenario: [API-SERVICE-CA_CE02.14_98] Invocazione del servizio con paId vuoto

  Scenario: [API-SERVICE-CA_CE02.14_99] Invocazione del servizio con paId inesistente

  Scenario: [API-SERVICE-CA_CE02.14_100] Invocazione del servizio con paId correttamente valorizzato e verifica risposta