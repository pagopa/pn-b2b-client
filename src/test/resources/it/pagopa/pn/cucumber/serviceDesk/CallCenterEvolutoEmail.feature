Feature: Gestione evolutiva del Call Center Evoluto per consentire ai destinatari di notifiche RADD, impossibilitati a recarsi in un CAF o ad accedere online, di prenotare un appuntamento in Virtual Room e ricevere copia digitale degli atti.

  @ignore
    # BUG: https://pagopa.atlassian.net/browse/PN-16308
  Scenario: [UTILS_TEST_MANUALE_1] Verifica allegati di una notifica perfezionata da oltre 120 giorni (Scenario 13)
    Given imposto lo iun di SharedSteps a "UTGP-ZRHR-XDNQ-202505-Q-1" e la pa a "Comune_Multi"
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | UTGP-ZRHR-XDNQ-202505-Q-1 |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
    When viene invocata l'api "CREATE_ACT_OPERATION"
    And il servizio risponde con 201
    And viene atteso lo stato "CREATING" dell'operazione
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
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
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
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
#      | auto     | auto | CLMCST42R12D969Z | COURTESY    | test@test.it | auto       | auto   | 400        | ora accetta un enum, non si può più passare COURTESY
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
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
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
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
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
  Scenario: [CCE_MAIL_1] Invio corretto della documentazione digitale con workflow analogico (Scenario 7, 11)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | auto                      |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
    When viene invocata l'api "CREATE_ACT_OPERATION"
    And il servizio risponde con 201
    And viene atteso lo stato "CREATING" dell'operazione
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    And viene atteso lo stato "OK" dell'operazione

  @CallCenterEvolutoViaMail
  Scenario: [CCE_MAIL_1.2] Invio corretto della documentazione digitale con workflow digitale (Scenario 7, 11)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | auto                      |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
    When viene invocata l'api "CREATE_ACT_OPERATION"
    And il servizio risponde con 201
    And viene atteso lo stato "CREATING" dell'operazione
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    And viene atteso lo stato "OK" dell'operazione


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
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
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
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
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
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
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
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
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
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | auto                      |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
    When viene invocata l'api "CREATE_ACT_OPERATION"
    And il servizio risponde con 201
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    And viene atteso lo stato "OK" dell'operazione
    Then viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 409

  @CallCenterEvolutoViaMail @CallCenterEvolutoV2
  Scenario Outline: [ACT_OPERATION_V2] Creazione di un'act operation. Nei casi di POST invocata con successo, verifica tramite GET della corretta valorizzazione dell'operationStatus
    Given vengono inviate <notificationNumber> nuove notifiche tramite api b2b dal "Comune_Multi" con destinatario Mario Gherkin e si aspetta che raggiungano l'elemento di timeline "REQUEST_ACCEPTED"
      #campi notifica
      | subject                 | notifica analogica con cucumber |
      | senderDenomination      | Comune di palermo               |
      #campi destinatario
      | digitalDomicile         | NULL                            |
      | physicalAddress_address | Via@ok_890                      |
    And viene popolata una richiesta di creazione Act operation "V2" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | auto                      |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
      | iunListType       | <iunListType>             |
    When viene invocata l'api "CREATE_ACT_OPERATION V2"
    Then il servizio risponde con <statusCodePost>
    And se la chiamata al servizio ha avuto successo
    When viene invocata l'API v2 GET operations passando "VALID OP. ID"
    Then il servizio risponde con 200
    And il campo operationStatus della response è valorizzato con "<operationStatusPreVideoUpload>"
    When viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    When viene invocata l'API v2 GET operations passando "<getOperationIdType>"
    Then il servizio risponde con <statusCodeGet>
    And se la chiamata al servizio ha avuto successo
    Then il campo operationStatus della response è valorizzato con "<operationStatus>"
    When viene creata una nuova richiesta per invocare il servizio SEARCH per il "CLMCST42R12D969Z"
    And viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo
    And il campo "iuns" risulta popolato correttamente, e il campo senderPaDescription è "Comune di palermo"
    Examples:
      | notificationNumber | iunListType         | statusCodePost | operationStatusPreVideoUpload | getOperationIdType | statusCodeGet | operationStatus |
      | 1                  | DATI VALIDI         | 200            | CREATING                      | VALID OP. ID       | 200           | OK              |
      | 5                  | DATI VALIDI         | 200            | CREATING                      | VALID OP. ID       | 200           | OK              |
      | 2                  | UNO IUN INESISTENTE | 200            | CREATING                      | VALID OP. ID       | 200           | WARNING         |
      | 1                  | DATI VALIDI         | 200            | CREATING                      | INEXISTENT OP. ID  | 404           | NULL            |
      | 1                  | DATI VALIDI         | 200            | CREATING                      | INVALID OP. ID     | 400           | NULL            |
      | 6                  | DATI VALIDI         | 400            | NULL                          | NULL               | NULL          | NULL            |
      | 1                  | LISTA IUN VUOTA     | 400            | NULL                          | NULL               | NULL          | NULL            |
      | 2                  | IUN RIPETUTO        | 400            | NULL                          | NULL               | NULL          | NULL            |

  @CallCenterEvolutoViaMail @CallCenterEvolutoV2
  Scenario: [ACT_OPERATION_BUG_VALIDATION_18333] Creazione di un'act operation per una notifica senza allegati di pagamento e successiva invocazione della GET v2
    Given vengono inviate 1 nuove notifiche tramite api b2b dal "Comune_Multi" con destinatario Mario Gherkin e si aspetta che raggiungano l'elemento di timeline "REQUEST_ACCEPTED"
      #campi notifica
      | subject                 | notifica analogica con cucumber |
      | senderDenomination      | Comune di palermo               |
      #campi destinatario
      | digitalDomicile         | NULL                            |
      | physicalAddress_address | Via@ok_890                      |
      | payment                 | NULL                            |
    And viene popolata una richiesta di creazione Act operation "V2" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | auto                      |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
      | iunListType       | DATI VALIDI               |
    When viene invocata l'api "CREATE_ACT_OPERATION V2"
    Then il servizio risponde con 200
    And se la chiamata al servizio ha avuto successo
    When viene invocata l'API v2 GET operations passando "VALID OP. ID"
    Then il servizio risponde con 200
    And il campo operationStatus della response è valorizzato con "CREATING"
    When viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    When viene invocata l'API v2 GET operations passando "VALID OP. ID"
    Then il servizio risponde con 200
    And se la chiamata al servizio ha avuto successo
    Then il campo operationStatus della response è valorizzato con "OK"
    When viene creata una nuova richiesta per invocare il servizio SEARCH per il "CLMCST42R12D969Z"
    And viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo
    And il campo "iuns" risulta popolato correttamente, e il campo senderPaDescription è "Comune di palermo"

  @CallCenterEvolutoViaMail @CallCenterEvolutoV2
  Scenario: [ACT_OPERATION_V2_ONLY_INEXISTENT_IUNS] Creazione di un'act operation con soli IUN inesistenti. Tramite GET, verifica che abbia lo status impostato su KO
    Given viene popolata una richiesta di creazione Act operation "V2" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | auto                      |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
      | iunListType       | TUTTI IUN INESISTENTI     |
    When viene invocata l'api "CREATE_ACT_OPERATION V2"
    Then il servizio risponde con 200
    And se la chiamata al servizio ha avuto successo
    When viene invocata l'API v2 GET operations passando "VALID OP. ID"
    Then il servizio risponde con 200
    And se la chiamata al servizio ha avuto successo
    Then il campo operationStatus della response è valorizzato con "KO"
    When viene creata una nuova richiesta per invocare il servizio SEARCH per il "CLMCST42R12D969Z"
    And viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo

  @CallCenterEvolutoViaMail @CallCenterEvolutoV2
  Scenario: [GET_ACT_OPERATION_V2_WITH_SUBOPERATION_IUN] Creazione di un'act operation e successiva invocazione della get V2 passando un'idOperation contenente lo IUN di una specifica subOperation
    Given vengono inviate 1 nuove notifiche tramite api b2b dal "Comune_Multi" con destinatario Mario Gherkin e si aspetta che raggiungano l'elemento di timeline "REQUEST_ACCEPTED"
      #campi notifica
      | subject                 | notifica analogica con cucumber |
      | senderDenomination      | Comune di palermo               |
      #campi destinatario
      | digitalDomicile         | NULL                            |
      | physicalAddress_address | Via@ok_890                      |
    And viene popolata una richiesta di creazione Act operation "V2" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | auto                      |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
      | iunListType       | DATI VALIDI               |
    When viene invocata l'api "CREATE_ACT_OPERATION V2"
    Then il servizio risponde con 200
    And se la chiamata al servizio ha avuto successo
    When viene invocata l'API v2 GET operations passando "VALID OP. ID"
    Then il servizio risponde con 200
    And il campo operationStatus della response è valorizzato con "CREATING"
    When viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    When viene invocata l'API v2 GET operations passando "OP. ID WITH IUN"
    Then il servizio risponde con 500
    When viene creata una nuova richiesta per invocare il servizio SEARCH per il "CLMCST42R12D969Z"
    And viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo

  @CallCenterEvolutoViaMail @CallCenterEvolutoV2
  Scenario Outline: [CREATE_ACT_OPERATION_V2_KO] Chiamata createActOperation V2 (controllo validazione campi)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation "V2" con i seguenti dati
      | ticketId          | <ticketId>     |
      | ticketOperationId | auto           |
      | taxId             | <taxId>        |
      | addressType       | <addressType>  |
      | addressValue      | <addressValue> |
      | ticketDate        | <ticketDate>   |
      | vrDate            | <vrDate>       |
      | iunListType       | DATI VALIDI    |
    And viene invocata l'api "CREATE_ACT_OPERATION V2"
    Then il servizio risponde con <statusCode>
    Examples:
      | ticketId | taxId            | addressType | addressValue              | ticketDate | vrDate | statusCode |
      | null     | CLMCST42R12D969Z | EMAIL       | matteo.sperati@dgsspa.com | auto       | auto   | 400        |
      | auto     | null             | EMAIL       | matteo.sperati@dgsspa.com | auto       | auto   | 400        |
      | auto     | CLMCST42R12D969Z | null        | matteo.sperati@dgsspa.com | auto       | auto   | 400        |
      | auto     | CLMCST42R12D969Z | EMAIL       | null                      | auto       | auto   | 400        |
      | auto     | CLMCST42R12D969Z | EMAIL       | matteo.sperati@dgsspa.com | null       | auto   | 400        |
      | auto     | CLMCST42R12D969Z | EMAIL       | matteo.sperati@dgsspa.com | auto       | null   | 400        |

  @CallCenterEvolutoViaMail @CallCenterEvolutoV2
  Scenario: [ACT_OPERATION_V2_LEGACY] Creazione di un actOperation con API v1 ed esecuzione della GET tramite api V2
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    When viene popolata una richiesta di creazione Act operation "V1" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | auto                      |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
    And viene invocata l'api "CREATE_ACT_OPERATION"
    Then il servizio risponde con 201
    When viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    When viene invocata l'API v2 GET operations passando "VALID OP. ID V1"
    Then il servizio risponde con 200
    And il campo operationStatus della response è valorizzato con "OK"
    When viene creata una nuova richiesta per invocare il servizio SEARCH per il "CLMCST42R12D969Z"
    And viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo

  @CallCenterEvolutoV2
  Scenario Outline: [ACT_OPERATION_BUG_VALIDATION_14813] Creazione di un actOperation relativa a una notifica i cui documenti risultano non disponibili e verifica della popolazione del campo uncompletedIuns
    Given "Comune_Multi" recupera lato web PA una notifica perfezionata inviata tra 180 e 150 giorni fa con destinatario Mario Gherkin
    When viene popolata una richiesta di creazione Act operation "<actOperationVersion>" con i seguenti dati
      | ticketId          | auto                      |
      | iun               | auto                      |
      | ticketOperationId | auto                      |
      | taxId             | CLMCST42R12D969Z          |
      | addressType       | EMAIL                     |
      | addressValue      | matteo.sperati@dgsspa.com |
      | ticketDate        | auto                      |
      | vrDate            | auto                      |
      | iunListType       | DATI VALIDI               |
    And viene invocata l'api "<api>"
    Then il servizio risponde con <creationStatusCode>
    When viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO per il video "video_vuoto.mp4"
    And viene invocata l'api "UPLOAD_VIDEO"
    And il servizio risponde con 200
    And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage
    When viene invocata l'API v2 GET operations passando "VALID OP. ID V1"
    Then il servizio risponde con 200
    When viene creata una nuova richiesta per invocare il servizio SEARCH per il "CLMCST42R12D969Z"
    And viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo
    And il campo "uncompletedIuns" risulta popolato correttamente, e il campo senderPaDescription è "NOT PagoPA"
    Examples:
      | api                     | creationStatusCode | actOperationVersion |
      | CREATE_ACT_OPERATION    | 201                | V1                  |
      | CREATE_ACT_OPERATION V2 | 200                | V2                  |

  @CallCenterEvolutoV2
  Scenario: [OPERATION_BUG_VALIDATION_14813] Creazione di un'operation per invio analogico e verifica della popolazione dei campi iuns e uncompletedIuns
    Given viene comunicato il nuovo indirizzo con "Cristoforo Colombo" "SIGN." "Via@ok_RS" "INTERNO 2" "80100" "NAPOLI" "XXX" "NA" "ITALIA"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "CLMCST42R12D969Z"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    When viene creata una nuova richiesta per invocare il servizio SEARCH per il "CLMCST42R12D969Z"
    And viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo