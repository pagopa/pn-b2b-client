Feature: Nella generazione del token sono aggiunte le seguenti informazioni: header typ del jwt generato, claim cnf del
  jwt generato (quando presente), claim digest del jwt generato (quando presente), claim digest della client assertion
  (quando presente).

  Scenario Outline: [AUTH_TOKEN_INFO_1] Generazione con successo di un voucher bearer con digest, con verifica della corretta
  struttura dei dati nel voucher e dei relativi log di audit salvati nel bucket S3

    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When l'utente richiede la generazione del voucher con digest
    And si ottiene la corretta generazione del voucher di tipo "Bearer" contenente le seguenti informazioni:
      | position | element      |
      | header   | typ          |
      | payload  | jti          |
      | payload  | digest.value |
      | payload  | client_id    |
    Then verifica che le informazioni di audit sul bucket S3 <bucketRole> contengano i seguenti dati per il voucher generato:
      | position | element                      | context      |
      | header   | typ                          | typ          |
      | payload  | jwtId                        | jti          |
      | payload  | clientId                     | client_id    |
      | payload  | digest.value                 | digest.value |
      | payload  | clientAssertion.digest.value | digest.value |

    Examples:
      | bucketRole |
      | STANDARD   |
      | WORM       |

  Scenario Outline: [AUTH_TOKEN_INFO_2] Generazione con successo di un voucher DPoP con digest, con verifica della corretta
  struttura dei dati nel voucher e dei relativi log di audit salvati nel bucket S3

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
    Then si ottiene lo status code 200
    And si ottiene la corretta generazione del voucher di tipo "DPoP" contenente le seguenti informazioni:
      | position | element |
      | header   | typ     |
      | payload  | jti     |
      | payload  | cnf.jkt |
    Then verifica che le informazioni di audit sul bucket S3 <bucketRole> contengano i seguenti dati per il voucher generato:
      | position | element | context |
      | header   | typ     | typ     |
      | payload  | jwtId   | jti     |
      | payload  | cnf.jkt | cnf.jkt |

    Examples:
      | keyType | bucketRole |
      | EC      | STANDARD   |
      | EC      | WORM       |
      | RSA     | STANDARD   |
      | RSA     | WORM       |
