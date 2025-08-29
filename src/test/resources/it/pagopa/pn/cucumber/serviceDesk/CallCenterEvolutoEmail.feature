Feature: Gestione evolutiva del Call Center Evoluto per consentire ai destinatari di notifiche RADD, impossibilitati a recarsi in un CAF o ad accedere online, di prenotare un appuntamento in Virtual Room e ricevere copia digitale degli atti.

@CallCenterEvolutoViaMail
Scenario Outline: [E2E_] Chiamata createActOperation (Scenario 2, 3)
Given viene popolata una richiesta di creazione Act operation con i seguenti dati
| ticketId          | <ticketId>     |
| iun               | <iun>          |
| ticketOperationId | auto           |
| taxId             | <taxId>        |
| addressType       | <addressType>  |
| addressValue      | <addressValue> |
| ticketDate        | <ticketDate>   |
| vrDate            | <vrDate>       |
Then viene invocata l'api "CREATE_ACT_OPERATION"
And il servizio risponde con <statusCode>
Examples:
| ticketId | iun  | taxId            | addressType  | addressValue | ticketDate | vrDate | statusCode |
| auto     | auto | TMTTMS92M57G793P | test@test.it | COURTESY     | auto       | auto   | 201        |
| auto     | null | null             | null         | null         | null       | null   | 400        |
| null     | auto | null             | null         | null         | null       | null   | 400        |
| null     | null | TMTTMS92M57G793P | null         | null         | null       | null   | 400        |
| null     | null | null             | test@test.it | COURTESY     | null       | null   | 400        |
| null     | null | null             | null         | null         | auto       | auto   | 400        |

@CallCenterEvolutoViaMail
Scenario Outline: [E2E_] Verifica stato operazione (Scenario 4)
Given viene popolata una richiesta di creazione Act operation con i seguenti dati
| ticketId          | <ticketId>     |
| ticketOperationId |                |
| taxId             | <taxId>        |
| iun               |                |
| ticketDate        |                |
| vrDate            |                |
| addressType       | <addressType>  |
| addressValue      | <addressValue> |
Then viene invocata l'api "GET_ACT_OPERATION_STATUS"
And il servizio risponde con <statusCode>
And l'operazione è in stato "PENDING"
Examples:
| ticketId | taxId | addressType | addressValue | statusCode |
| valid    | valid | valid       | valid        | 200        |

@CallCenterEvolutoViaMail
Scenario: [E2E_] Verifica stato operazione con operationId inesistente (Scenario 5)
Given viene settato l'operationId a "valore_inesistente"
Then viene invocata l'api "GET_ACT_OPERATION_STATUS"
And il servizio risponde con 404

@CallCenterEvolutoViaMail
Scenario Outline: [E2E_] Verifica stato operazione con api-key invalida (Scenario 6)
Given viene popolata una richiesta di creazione Act operation con i seguenti dati
| ticketId          | <ticketId>     |
| ticketOperationId |                |
| taxId             | <taxId>        |
| iun               |                |
| ticketDate        |                |
| vrDate            |                |
| addressType       | <addressType>  |
| addressValue      | <addressValue> |
Then viene invocata l'api "GET_ACT_OPERATION_STATUS_INVALID_API_KEY"
And il servizio risponde con <statusCode>
Examples:
| ticketId | taxId | addressType | addressValue | statusCode |
| valid    | valid | valid       | valid        | 403        |

@CallCenterEvolutoViaMail
Scenario Outline: [E2E_] Upload video riconoscimento utente (Scenario 7, 11)
Given viene popolata una richiesta di creazione Act operation con i seguenti dati
| ticketId          | <ticketId>     |
| ticketOperationId |                |
| taxId             | <taxId>        |
| iun               |                |
| ticketDate        |                |
| vrDate            |                |
| addressType       | <addressType>  |
| addressValue      | <addressValue> |
When viene invocata l'api "CREATE_ACT_OPERATION"
And il servizio risponde con 201
And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
And viene invocata l'api "UPLOAD_VIDEO"
And il servizio risponde con 201
And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
And il video viene caricato su SafeStorage
Then viene invocata l'api "GET_ACT_OPERATION_STATUS"
And il servizio risponde con 200
And l'operazione è in stato "OK"
Examples:
| ticketId | taxId | addressType | addressValue |
| valid    | valid | valid       | valid        |

@CallCenterEvolutoViaMail
Scenario Outline: [E2E_] Verifica stato operazione con operationId inesistente (Scenario 8, 9)
Given viene settato l'operationId a <operationId>
And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
And viene invocata l'api "UPLOAD_VIDEO"
And il servizio risponde con <statusCode>
Examples:
| operationId          | statusCode |
| "valore_inesistente" | 404        |
| "valore_nullo"       | 400        |

@CallCenterEvolutoViaMail
Scenario Outline: [E2E_] Upload secondo video riconoscimento utente (Scenario 10)
Given viene popolata una richiesta di creazione Act operation con i seguenti dati
| ticketId          | <ticketId>     |
| ticketOperationId |                |
| taxId             | <taxId>        |
| iun               |                |
| ticketDate        |                |
| vrDate            |                |
| addressType       | <addressType>  |
| addressValue      | <addressValue> |
When viene invocata l'api "CREATE_ACT_OPERATION"
And il servizio risponde con 201
And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
And viene invocata l'api "UPLOAD_VIDEO"
And il servizio risponde con 201
And la risposta del servizio UPLOAD VIDEO risponde con esito positivo
And il video viene caricato su SafeStorage
And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
And viene invocata l'api "UPLOAD_VIDEO"
And il servizio risponde con 400
Examples:
| ticketId | taxId | addressType | addressValue |
| valid    | valid | valid       | valid        |