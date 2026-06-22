@m2m-apiv3-purposes-threshold-discr-cert-attr
Feature: Soglie differenziate in attributi certificati discreti con API M2M V3

  Scenario: [CERT_DISCRETE_ATTR_PURPOSE_THRESHOLD_1B] Attivazione con successo di una finalità e verifica della corretta decurtazione
  delle chiamate giornaliere residue in base alle soglie differenziate.
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 100 e con i seguenti attributi:
      | kind               | group | comparator | value                                | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | $ATTR_CERT_DISCR_THRESHOLD(PA1,-100) | 99                    |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente è un "admin" di "PA1"
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 11
    And si ottiene status code 200
    And l'utente tenta di attivare la finalità
    Then si ottiene status code 200 e la finalità in stato "ACTIVE"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And i residui relativi alle dailyCalls associati alla finalità sono pari a per m2m:
      | remainingDailyCallsPerConsumer | 88 |
      | remainingDailyCallsTotal       | 89 |

  Scenario: [CERT_DISCRETE_ATTR_PURPOSE_THRESHOLD_2B] Attivazione con successo di una finalità, il fruitore non soddisfa l'attributo
  certificato discreto e la sua soglia di fruizione è uguale a quella di default
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 100 e con i seguenti attributi:
      | kind               | group | comparator | value                               | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | $ATTR_CERT_DISCR_THRESHOLD(PA1,100) | 99                    |
      | CERTIFIED          | 0     |            |                                     |                       |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    And l'utente assegna a "PA1" l'attributo certificato precedentemente creato
    And l'utente è un "admin" di "PA1"
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 11
    And si ottiene status code 200
    And l'utente tenta di attivare la finalità
    Then si ottiene status code 200 e la finalità in stato "WAITING_FOR_APPROVAL"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And i residui relativi alle dailyCalls associati alla finalità sono pari a per m2m:
      | remainingDailyCallsPerConsumer | 10  |
      | remainingDailyCallsTotal       | 100 |
