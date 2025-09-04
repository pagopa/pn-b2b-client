Feature: Gestione evolutiva del Call Center Evoluto per consentire ai destinatari di notifiche RADD, impossibilitati a recarsi in un CAF o ad accedere online, di prenotare un appuntamento in Virtual Room e ricevere copia digitale degli atti.

  @ignore
  Scenario: [UTILS_TEST_MANUALE_1] Verifica allegati di una notifica perfezionata da oltre 120 giorni (Scenario 13)
    Given imposto lo iun di SharedSteps a "LNWV-GRMV-KPWV-202503-W-1" e la pa a "Comune_Multi"
    When viene popolata una richiesta di creazione Act operation con i seguenti dati
      | ticketId          | auto                        |
      | iun               | LNWV-GRMV-KPWV-202503-W-1   |
      | ticketOperationId | auto                        |
      | taxId             | CLMCST42R12D969Z            |
      | addressType       | EMAIL                       |
      | addressValue      | stefano.netti@grupposcai.it |
      | ticketDate        | auto                        |
      | vrDate            | auto                        |
    When viene invocata l'api "CREATE_ACT_OPERATION"
    And il servizio risponde con 201
    And viene atteso lo stato "CREATING" dell'operazione
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    And viene atteso lo stato "OK" dell'operazione

  @CallCenterEvolutoViaMail
    #BUG: https://pagopa.atlassian.net/browse/PN-16242
  Scenario Outline: [CCE_MAIL_CREATE_ACT_OPERATION] Chiamata createActOperation (Scenario 2, 3)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation con i seguenti dati
      | ticketId          | <ticketId>     |
      | iun               | <iun>          |
      | ticketOperationId | auto           |
      | taxId             | <taxId>        |
      | addressType       | <addressType>  |
      | addressValue      | <addressValue> |
      | ticketDate        | <ticketDate>   |
      | vrDate            | <vrDate>       |
    And viene invocata l'api "CREATE_ACT_OPERATION"
    Then il servizio risponde con <statusCode>
    Examples:
      | ticketId | iun  | taxId            | addressType | addressValue | ticketDate | vrDate | statusCode |
      | auto     | auto | CLMCST42R12D969Z | EMAIL       | test@test.it | auto       | auto   | 201        |
      | auto     | auto | CLMCST42R12D969Z | COURTESY    | test@test.it | auto       | auto   | 400        |
      | auto     | null | null             | null        | null         | null       | null   | 400        |
      | null     | auto | null             | null        | null         | null       | null   | 400        |
      | null     | null | CLMCST42R12D969Z | null        | null         | null       | null   | 400        |
      | null     | null | null             | EMAIL       | test@test.it | null       | null   | 400        |
      | null     | null | null             | null        | null         | auto       | auto   | 400        |

  @CallCenterEvolutoViaMail
  Scenario: [CCE_MAIL_GET_STATUS_1] Verifica stato operazione (Scenario 4)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation con i seguenti dati
      | ticketId          | auto             |
      | iun               | auto             |
      | ticketOperationId | auto             |
      | taxId             | CLMCST42R12D969Z |
      | addressType       | EMAIL            |
      | addressValue      | test@test.it     |
      | ticketDate        | auto             |
      | vrDate            | auto             |
    And viene invocata l'api "CREATE_ACT_OPERATION"
    Then il servizio risponde con 201
    And viene invocata l'api "GET_ACT_OPERATION_STATUS"
    And il servizio risponde con 200
    Then l'operazione è in stato "CREATING"

  @CallCenterEvolutoViaMail
  Scenario: [CCE_MAIL_GET_STATUS_2] Verifica stato operazione con operationId inesistente (Scenario 5)
    Given viene settato l'operationId a "valore_inesistente"
    Then viene invocata l'api "GET_ACT_OPERATION_STATUS"
    And il servizio risponde con 404

  @CallCenterEvolutoViaMail
  Scenario: [CCE_MAIL_AUTH_1] Verifica stato operazione con api-key invalida (Scenario 6)
    Given imposto lo iun di SharedSteps a "XDPT-VYGV-QAKG-202509-E-1" e la pa a "Comune_Multi"
    When viene popolata una richiesta di creazione Act operation con i seguenti dati
      | ticketId          | auto                      |
      | iun               | XDPT-VYGV-QAKG-202509-E-1 |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | test@test.it              |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
    Then viene invocata l'api "GET_ACT_OPERATION_STATUS_INVALID_API_KEY"
    And il servizio risponde con 401

  @CallCenterEvolutoViaMail
  Scenario Outline: [CCE_MAIL_1] Invio corretto della documentazione digitale (Scenario 7, 11)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation con i seguenti dati
      | ticketId          | auto             |
      | iun               | auto             |
      | ticketOperationId | auto             |
      | taxId             | CLMCST42R12D969Z |
      | addressType       | EMAIL            |
      | addressValue      | <email>          |
      | ticketDate        | auto             |
      | vrDate            | auto             |
    When viene invocata l'api "CREATE_ACT_OPERATION"
    And il servizio risponde con 201
    And viene atteso lo stato "CREATING" dell'operazione
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    And viene atteso lo stato "OK" dell'operazione

    Examples:
      | email                       |
      | stefano.netti@grupposcai.it |

  @CallCenterEvolutoViaMail @onlyDev
    #BUG: https://pagopa.atlassian.net/browse/PN-16237
  Scenario Outline: [CCE_MAIL_1_MOCK_DEV] Invio corretto della documentazione digitale (Scenario 7, 11)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation con i seguenti dati
      | ticketId          | auto             |
      | iun               | auto             |
      | ticketOperationId | auto             |
      | taxId             | CLMCST42R12D969Z |
      | addressType       | EMAIL            |
      | addressValue      | <email>          |
      | ticketDate        | auto             |
      | vrDate            | auto             |
    When viene invocata l'api "CREATE_ACT_OPERATION"
    And il servizio risponde con 201
    And viene atteso lo stato "CREATING" dell'operazione
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    And viene atteso lo stato "OK" dell'operazione

    Examples:
      | email        |
      | test@test.it |

  @CallCenterEvolutoViaMail
  Scenario: [CCE_MAIL_2] Flusso avviato con CF differente rispetto la notifica di riferimento (Scenario 12)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation con i seguenti dati
      | ticketId          | auto             |
      | iun               | auto             |
      | ticketOperationId | auto             |
      | taxId             | CLMCST42R12D969X |
      | addressType       | EMAIL            |
      | addressValue      | test@test.it     |
      | ticketDate        | auto             |
      | vrDate            | auto             |
    When viene invocata l'api "CREATE_ACT_OPERATION"
    And il servizio risponde con 400

  @CallCenterEvolutoViaMail
  Scenario Outline: [CCE_MAIL_UPLOAD_VIDEO_2] Verifica stato operazione con operationId inesistente (Scenario 8, 9)
    Given viene settato l'operationId a <operationId>
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con <statusCode>
    Examples:
      | operationId                 | statusCode |
      | "ZXPLQH7W9RTM2VYJ8KN3CSBFD" | 404        |
      | "null"                      | 400        |

  @CallCenterEvolutoViaMail
    #BUG: https://pagopa.atlassian.net/browse/PN-16241
  Scenario: [CCE_MAIL_UPLOAD_VIDEO_3] Upload secondo video riconoscimento utente (Scenario 10)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation con i seguenti dati
      | ticketId          | auto                        |
      | iun               | auto                        |
      | ticketOperationId | auto                        |
      | taxId             | CLMCST42R12D969Z            |
      | addressType       | EMAIL                       |
      | addressValue      | stefano.netti@grupposcai.it |
      | ticketDate        | auto                        |
      | vrDate            | auto                        |
    When viene invocata l'api "CREATE_ACT_OPERATION"
    And il servizio risponde con 201
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    And viene atteso lo stato "OK" dell'operazione
    Then viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 409


