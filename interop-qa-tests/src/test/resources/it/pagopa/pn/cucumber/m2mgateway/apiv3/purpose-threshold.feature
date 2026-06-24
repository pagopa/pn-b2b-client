@m2m-apiv3-purposes-threshold
Feature: Soglie differenziate con API M2M V3

  Scenario: [PURPOSE_THRESHOLD_5] Una finalità in stato ACTIVE è indifferente ai nuovi cambiamenti delle soglie associate agli attributi certificati posseduti per la fruizione di un certo eservice
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA2" gli attributi certificati precedentemente creati
    And si ottiene status code 200
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 1 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "10"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA2"
    And l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 5
    And si ottiene status code 200
    And l'utente tenta di attivare la finalità
    And si ottiene status code 200 e la finalità in stato "ACTIVE"
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And i residui relativi alle dailyCalls associati alla finalità sono pari a per m2m:
      | remainingDailyCallsPerConsumer | 5   |
      | remainingDailyCallsTotal       | 995 |

  Scenario Outline: [PURPOSE_THRESHOLD_9b] Una richiesta con API M2M V3 per recuperare le soglie rimanenti effettuata con un ruolo non autorizzato fallisce
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA2" gli attributi certificati precedentemente creati
    And si ottiene status code 200
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 100 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "100"
    And l'utente è un "admin" di "PA2"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 1
    And si ottiene status code 200
    And l'utente tenta di attivare la finalità
    And si ottiene status code 200 e la finalità in stato "ACTIVE"
    When l'utente è un "<role>" di "PA2" con ruolo M2M <m2mRole>
    And l'utente cerca di recuperare le soglie rimanenti per la finalità con ID "<purposeId>" per m2m e si ottiene uno status code <statusCode>

    @happy-path
    Examples:
      | role         | m2mRole   | purposeId | statusCode |
      | admin        | m2m-admin | %actual   | 200        |
      | api          | m2m-admin | %actual   | 200        |
      | security     | m2m-admin | %actual   | 200        |
      | support      | m2m-admin | %actual   | 200        |
      | api,security | m2m-admin | %actual   | 200        |

    @sad-path
    Examples:
      | role         | m2mRole   | purposeId | statusCode |
      | admin        | m2m       | %actual   | 403        |
      | api          | m2m       | %actual   | 403        |
      | security     | m2m       | %actual   | 403        |
      | support      | m2m       | %actual   | 403        |
      | api,security | m2m       | %actual   | 403        |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | role         | m2mRole   | purposeId | statusCode |
      | reviewer     | m2m-admin | %actual   | 403        |

  Scenario Outline: [PURPOSE_THRESHOLD_10b] Una richiesta con API M2M V3 per recuperare le soglie rimanenti specificando una finalità non valida o inesistente fallisce
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente cerca di recuperare le soglie rimanenti per la finalità con ID "<purposeId>" per m2m e si ottiene uno status code <statusCode>

    Examples:
      | purposeId | statusCode |
      | %random   | 404        |
      | %null     | 400        |

  Scenario: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_17] Per un e-service in stato PUBLISHED è possibile modificare dailyCallsPerConsumer all'interno degli attributi certificati utilizzando le API M2M v3
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di aggiungere una soglia differenziata di 70 per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo con m2m
    And si ottiene status code 200
    Then la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "70"
