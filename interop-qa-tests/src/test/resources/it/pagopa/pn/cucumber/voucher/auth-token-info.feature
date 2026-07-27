Feature: Nella generazione del token sono aggiunte le seguenti informazioni: header typ del jwt generato, claim cnf del
  jwt generato (quando presente), claim digest del jwt generato (quando presente), claim digest della client assertion
  (quando presente).

  Scenario: [AUTH_TOKEN_INFO_1] Generazione con successo di un voucher bearer con digest e validazione dei relativi log
  di audit salvati nel bucket S3.

    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede la generazione del voucher con digest
    And si ottiene la corretta generazione del voucher di tipo "Bearer"
    Then verifica che le informazioni di audit sul bucket S3 "persistenza" contengano i seguenti dati per il voucher generato:
      | s3-element                   | ctx-source | ctx-group | ctx-item     |
      | typ                          | voucher    | header    | typ          |
      | jwtId                        | voucher    | payload   | jti          |
      | clientId                     | voucher    | payload   | client_id    |
      | digest.value                 | voucher    | payload   | digest.value |
      | clientAssertion.digest.value | voucher    | payload   | digest.value |
    Then verifica che le informazioni di audit sul bucket S3 "signed" contengano i seguenti dati per il voucher generato:
      | s3-element                   | ctx-source | ctx-group | ctx-item     |
      | typ                          | voucher    | header    | typ          |
      | jwtId                        | voucher    | payload   | jti          |
      | clientId                     | voucher    | payload   | client_id    |
      | digest.value                 | voucher    | payload   | digest.value |
      | clientAssertion.digest.value | voucher    | payload   | digest.value |

  Scenario Outline: [AUTH_TOKEN_INFO_2] Generazione con successo di un voucher DPoP con digest e validazione dei relativi
  log di audit salvati nel bucket S3.

    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave "RSA" pubblica nel client
    When "PA1" genera una dpop proof con una chiave "<keyType>" e verifica i campi HTU,HTM
    And "PA1" cerca di ottenere un access token per il client "CONSUMER" usando il dpop proof creato
    And si ottiene lo status code 200
    And si ottiene la corretta generazione del voucher di tipo "DPoP"
    Then verifica che le informazioni di audit sul bucket S3 "persistenza" contengano i seguenti dati per il voucher generato:
      | s3-element | ctx-source | ctx-group | ctx-item |
      | typ        | voucher    | header    | typ      |
      | jwtId      | voucher    | payload   | jti      |
      | cnf.jkt    | voucher    | payload   | cnf.jkt  |
    And verifica che le informazioni di audit sul bucket S3 "signed" contengano i seguenti dati per il voucher generato:
      | s3-element | ctx-source | ctx-group | ctx-item |
      | typ        | voucher    | header    | typ      |
      | jwtId      | voucher    | payload   | jti      |
      | cnf.jkt    | voucher    | payload   | cnf.jkt  |

    Examples:
      | keyType |
      | EC      |
      | RSA     |

  Scenario: [AUTH_TOKEN_INFO_3] Generazione con successo di un voucher M2M e validazione dei relativi log di audit salvati
  nel bucket S3.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And l'utente tenta la modifica dell'amministratore del client indicando se stesso
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede la generazione del voucher M2M
    And si ottiene la corretta generazione del voucher di tipo "Bearer"
    Then verifica che le informazioni di audit sul bucket S3 "persistenza m2m" contengano i seguenti dati per il voucher generato:
      | s3-element     | ctx-source | ctx-group | ctx-item       |
      | algorithm      | voucher    | header    | alg            |
      | keyId          | voucher    | header    | kid            |
      | typ            | voucher    | header    | typ            |
      | jwtId          | voucher    | payload   | jti            |
      | issuedAt       | voucher    | payload   | iat            |
      | clientId       | voucher    | payload   | client_id      |
      | organizationId | voucher    | payload   | organizationId |
      | adminId        | voucher    | payload   | adminId        |
      | notBefore      | voucher    | payload   | nbf            |
      | expirationTime | voucher    | payload   | exp            |
      | issuer         | voucher    | payload   | iss            |
      | audience       | voucher    | payload   | aud            |
      | subject        | voucher    | payload   | sub            |
    And verifica che le informazioni di audit sul bucket S3 "signed m2m" contengano i seguenti dati per il voucher generato:
      | s3-element     | ctx-source | ctx-group | ctx-item       |
      | algorithm      | voucher    | header    | alg            |
      | keyId          | voucher    | header    | kid            |
      | typ            | voucher    | header    | typ            |
      | jwtId          | voucher    | payload   | jti            |
      | issuedAt       | voucher    | payload   | iat            |
      | clientId       | voucher    | payload   | client_id      |
      | organizationId | voucher    | payload   | organizationId |
      | adminId        | voucher    | payload   | adminId        |
      | notBefore      | voucher    | payload   | nbf            |
      | expirationTime | voucher    | payload   | exp            |
      | issuer         | voucher    | payload   | iss            |
      | audience       | voucher    | payload   | aud            |
      | subject        | voucher    | payload   | sub            |

  Scenario: [AUTH_TOKEN_INFO_4] Generazione con successo di un voucher M2M con DPoP e validazione dei relativi log di audit
  salvati nel bucket S3 con dpop e clientAssertion.

    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And l'utente tenta la modifica dell'amministratore del client indicando se stesso
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" genera una dpop proof con una chiave "RSA" e verifica i campi HTU,HTM
    And "PA1" cerca di ottenere un access token per il client "API" usando il dpop proof creato
    Then verifica che le informazioni di audit sul bucket S3 "persistenza m2m" contengano i seguenti dati per il voucher generato:
      | s3-element                     | ctx-source       | ctx-group | ctx-item       |
      | algorithm                      | voucher          | header    | alg            |
      | keyId                          | voucher          | header    | kid            |
      | typ                            | voucher          | header    | typ            |
      | jwtId                          | voucher          | payload   | jti            |
      | issuedAt                       | voucher          | payload   | iat            |
      | clientId                       | voucher          | payload   | client_id      |
      | organizationId                 | voucher          | payload   | organizationId |
      | adminId                        | voucher          | payload   | adminId        |
      | notBefore                      | voucher          | payload   | nbf            |
      | expirationTime                 | voucher          | payload   | exp            |
      | issuer                         | voucher          | payload   | iss            |
      | audience                       | voucher          | payload   | aud            |
      | subject                        | voucher          | payload   | sub            |
      | cnf.jkt                        | voucher          | payload   | cnf.jkt        |
      | clientAssertion.algorithm      | client-assertion | header    | alg            |
      | clientAssertion.keyId          | client-assertion | header    | kid            |
      | clientAssertion.jwtId          | client-assertion | payload   | jti            |
      | clientAssertion.issuedAt       | client-assertion | payload   | iat            |
      | clientAssertion.issuer         | client-assertion | payload   | iss            |
      | clientAssertion.subject        | client-assertion | payload   | sub            |
      | clientAssertion.expirationTime | client-assertion | payload   | exp            |
      | dpop.typ                       | dpop-proof       | header    | typ            |
      | dpop.alg                       | dpop-proof       | header    | alg            |
      | dpop.jwk.kty                   | dpop-proof       | header    | jwk.kty        |
      | dpop.jwk.n                     | dpop-proof       | header    | jwk.n          |
      | dpop.jwk.e                     | dpop-proof       | header    | jwk.e          |
      | dpop.htm                       | dpop-proof       | payload   | htm            |
      | dpop.htu                       | dpop-proof       | payload   | htu            |
      | dpop.iat                       | dpop-proof       | payload   | iat            |
      | dpop.jti                       | dpop-proof       | payload   | jti            |
    And verifica che le informazioni di audit sul bucket S3 "signed m2m" contengano i seguenti dati per il voucher generato:
      | s3-element                     | ctx-source       | ctx-group | ctx-item       |
      | algorithm                      | voucher          | header    | alg            |
      | keyId                          | voucher          | header    | kid            |
      | typ                            | voucher          | header    | typ            |
      | jwtId                          | voucher          | payload   | jti            |
      | issuedAt                       | voucher          | payload   | iat            |
      | clientId                       | voucher          | payload   | client_id      |
      | organizationId                 | voucher          | payload   | organizationId |
      | adminId                        | voucher          | payload   | adminId        |
      | notBefore                      | voucher          | payload   | nbf            |
      | expirationTime                 | voucher          | payload   | exp            |
      | issuer                         | voucher          | payload   | iss            |
      | audience                       | voucher          | payload   | aud            |
      | subject                        | voucher          | payload   | sub            |
      | cnf.jkt                        | voucher          | payload   | cnf.jkt        |
      | clientAssertion.algorithm      | client-assertion | header    | alg            |
      | clientAssertion.keyId          | client-assertion | header    | kid            |
      | clientAssertion.jwtId          | client-assertion | payload   | jti            |
      | clientAssertion.issuedAt       | client-assertion | payload   | iat            |
      | clientAssertion.issuer         | client-assertion | payload   | iss            |
      | clientAssertion.subject        | client-assertion | payload   | sub            |
      | clientAssertion.expirationTime | client-assertion | payload   | exp            |
      | dpop.typ                       | dpop-proof       | header    | typ            |
      | dpop.alg                       | dpop-proof       | header    | alg            |
      | dpop.jwk.kty                   | dpop-proof       | header    | jwk.kty        |
      | dpop.jwk.n                     | dpop-proof       | header    | jwk.n          |
      | dpop.jwk.e                     | dpop-proof       | header    | jwk.e          |
      | dpop.htm                       | dpop-proof       | payload   | htm            |
      | dpop.htu                       | dpop-proof       | payload   | htu            |
      | dpop.iat                       | dpop-proof       | payload   | iat            |
      | dpop.jti                       | dpop-proof       | payload   | jti            |
