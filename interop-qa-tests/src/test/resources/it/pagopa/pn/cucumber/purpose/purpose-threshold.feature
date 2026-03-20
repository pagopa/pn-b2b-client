@purpose
Feature: Verifica soglie differenziate
  Tutti gli utenti autorizzati possono usufruire della soglia differenziata specificata nell'attributo certificato

  @dailyCallsThreshold
  Scenario: [PURPOSE_THRESHOLD_1] Per la creazione di una finalità il sistema attribuisce la soglia di default se il fruitore non possiede nessun attributo certificato associato ad una soglia
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 2 attributi CERTIFIED
    And l'utente assegna a "PA1" l'attributo certificato precedentemente creato
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 100
    And l'utente tenta di aggiungere una soglia differenziata di 100 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 11
    And si ottiene status code 200
    Then la finalità è in stato WAITING_FOR_APPROVAL

  @dailyCallsThreshold
  Scenario: [PURPOSE_THRESHOLD_2] Per la creazione di una finalità il sistema attribuisce la soglia maggiore degli attributi certificati
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 2 attributi CERTIFIED
    And l'utente assegna a "PA1" gli attributi certificati precedentemente creati
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 100 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And l'utente tenta di aggiungere una soglia differenziata di 1000 per l'attributo CERTIFIED 1-esimo creato
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 1000
    And si ottiene status code 200
    Then la finalità è in stato ACTIVE

  @dailyCallsThreshold
  Scenario: [PURPOSE_THRESHOLD_3] Per la creazione di una finalità il sistema calcola correttamente il campo remainingDailyCallsPerConsumer, scalandolo per le purpose provenienti dallo stesso fruitore per lo stesso eservice e non scalandolo per le purpose provenienti da altri fruitori per lo stesso eservice, e remainingDailyCallsTotal scalandolo per ogni purpose attiva
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA1" gli attributi certificati precedentemente creati
    And l'utente assegna a "GSP" gli attributi certificati precedentemente creati
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 1 e dailyCallsTotal uguale a 15
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 5
    And si ottiene status code 200
    And la finalità è in stato ACTIVE
    And i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 5  |
      | remainingDailyCallsTotal       | 10 |
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 5
    And si ottiene status code 200
    And la finalità è in stato ACTIVE
    And i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 0 |
      | remainingDailyCallsTotal       | 5 |
    Then l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 5
    And si ottiene status code 200
    And la finalità è in stato WAITING_FOR_APPROVAL
    And l'utente è un "admin" di "GSP"
    Then l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 5
    And si ottiene status code 200
    And la finalità è in stato ACTIVE
    And i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 5 |
      | remainingDailyCallsTotal       | 0 |

  @dailyCallsThreshold
  Scenario: [PURPOSE_THRESHOLD_4] Per la creazione di una finalità il sistema demanda allo stato WAITING_FOR_APROVAL se il limite di chiamate giornaliere per fruitore supera il limite fornito dall'erogatore
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA1" gli attributi certificati precedentemente creati
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 1 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 11
    And si ottiene status code 200
    Then la finalità è in stato WAITING_FOR_APPROVAL

  @dailyCallsThreshold
  Scenario: [PURPOSE_THRESHOLD_5] Una finalità in stato ACTIVE è indifferente ai nuovi cambiamenti delle soglie associate agli attributi certificati posseduti per la fruizione di un certo eservice
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA1" gli attributi certificati precedentemente creati
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 1 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 5
    And si ottiene status code 200
    And la finalità è in stato ACTIVE
    And i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 5   |
      | remainingDailyCallsTotal       | 995 |
    And l'utente è un "admin" di "PA2"
    When l'utente tenta di aggiungere una soglia differenziata di 1 per l'attributo CERTIFIED 0-esimo creato
    And l'utente è un "admin" di "PA1"
    Then i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 5   |
      | remainingDailyCallsTotal       | 995 |
    And i residui relativi alle dailyCalls associati alla finalità sono pari a per m2m:
      | remainingDailyCallsPerConsumer | 5   |
      | remainingDailyCallsTotal       | 995 |

  @dailyCallsThreshold
  Scenario: [PURPOSE_THRESHOLD_6] Una finalità in stato ACTIVE è alla revoca di un attributo certificato non essenziale per la fruizione ma associato ad una certa soglia
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 2 attributo CERTIFIED
    And l'utente assegna a "PA1" l'attributo certificato precedentemente creato
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 100 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 11
    And si ottiene status code 200
    Then la finalità è in stato ACTIVE
    Then i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 95  |
      | remainingDailyCallsTotal       | 989 |
    Given l'utente è un "admin" di "PA2"
    And l'utente revoca l'attributo precedentemente creato e assegnato
    And si ottiene status code 200
    Then la finalità è in stato ACTIVE
    And i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 95  |
      | remainingDailyCallsTotal       | 989 |

  @dailyCallsThreshold
  Scenario: [PURPOSE_THRESHOLD_7] Per la creazione di una finalità in stato ACTIVE il sistema attribuisce la soglia maggiore degli attributi certificati definiti in gruppi differenti
    Given l'utente è un "admin" di "PA2"
    And due gruppi di due attributi certificati da "PA2", dei quali "PA1" ne possiede uno per gruppo
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 100 per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo
    And si ottiene status code 200
    And l'utente tenta di aggiungere una soglia differenziata di 1000 per l'attributo CERTIFIED 1-esimo creato nel gruppo 1-esimo
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 100
    And si ottiene status code 200
    Then la finalità è in stato ACTIVE
    And i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 900 |
      | remainingDailyCallsTotal       | 900 |

  @dailyCallsThreshold
  Scenario: [PURPOSE_THRESHOLD_8] Per la creazione di una finalità con una soglia superiore a quelle impostate negli attributi certificati contenuti in gruppi differenti, se la richiesta contiene un limite di soglia superiore, il sistema imposta lo stato della finalità a WAITING_FOR_APPROVAL e la soglia a quella richiesta
    Given l'utente è un "admin" di "PA2"
    And due gruppi di due attributi certificati da "PA2", dei quali "PA1" ne possiede uno per gruppo
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 100 per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo
    And si ottiene status code 200
    And l'utente tenta di aggiungere una soglia differenziata di 200 per l'attributo CERTIFIED 1-esimo creato nel gruppo 1-esimo
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 300
    And si ottiene status code 200
    Then la finalità è in stato WAITING_FOR_APPROVAL
    And i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 200  |
      | remainingDailyCallsTotal       | 1000 |
    And si ottiene status code 400

  @dailyCallsThreshold
  @security
  Scenario Outline: [PURPOSE_THRESHOLD_9] Una richiesta con API BFF per recuperare le soglie rimanenti effettuata con un ruolo non autorizzato fallisce
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA1" gli attributi certificati precedentemente creati
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 100 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 1
    And si ottiene status code 200
    And la finalità è in stato ACTIVE
    When l'utente è un "<ruolo>" di "PA2"
    And i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 9   |
      | remainingDailyCallsTotal       | 999 |
    And si ottiene status code <statusCode>

    Examples:
      | ruolo        | statusCode |
      | admin        | 200        |
      | api          | 403        |
      | security     | 403        |
      | support      | 403        |
      | api,security | 403        |

  @dailyCallsThreshold
  @security
  Scenario Outline: [PURPOSE_THRESHOLD_9b] Una richiesta con API M2M V3 per recuperare le soglie rimanenti effettuata con un ruolo non autorizzato fallisce
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA1" gli attributi certificati precedentemente creati
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000
    And l'utente tenta di aggiungere una soglia differenziata di 100 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente crea una nuova finalità per quell'e-service con tutti i campi richiesti correttamente formattati e con dailyCalls uguale a 1
    And si ottiene status code 200
    And la finalità è in stato ACTIVE
    When l'utente è un "<ruolo>" di "PA2" con ruolo M2M m2m-admin
    And i residui relativi alle dailyCalls associati alla finalità sono pari a per m2m:
      | remainingDailyCallsPerConsumer | 9   |
      | remainingDailyCallsTotal       | 999 |
    And si ottiene status code <statusCode>

    Examples:
      | ruolo        | statusCode |
      | admin        | 200        |
      | api          | 403        |
      | security     | 403        |
      | support      | 403        |
      | api,security | 403        |

  @dailyCallsThreshold
  @security
  Scenario Outline: [PURPOSE_THRESHOLD_10] Una richiesta con API BFF per recuperare le soglie rimanenti specificando una finalità non valida o inesistente fallisce
    Given l'utente è un "admin" di "PA2"
    When l'utente cerca di recuperare le soglie rimanenti per la finalità con ID "<purposeId>"
    Then si ottiene status code <statusCode>

    Examples:
      | purposeId | statusCode |
      | %random   | 404        |
      | %null     | 404        |

  @dailyCallsThreshold
  @security
  Scenario Outline: [PURPOSE_THRESHOLD_10b] Una richiesta con API M2M V3 per recuperare le soglie rimanenti specificando una finalità non valida o inesistente fallisce
    Given l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    When l'utente cerca di recuperare le soglie rimanenti per la finalità con ID "<purposeId>" per m2m
    Then si ottiene status code <statusCode>

    Examples:
      | purposeId | statusCode |
      | %random   | 404        |
      | %null     | 404        |

  @dailyCallsThreshold
  @security
  Scenario: [PURPOSE_THRESHOLD_11] Una richiesta con API BFF per recuperare le soglie rimanenti effettuata senza autenticazione fallisce
    When l'utente cerca di recuperare le soglie rimanenti per la finalità con ID "%random"
    Then si ottiene status code 401

  @dailyCallsThreshold
  @security
  Scenario: [PURPOSE_THRESHOLD_11b] Una richiesta con API M2M V3 per recuperare le soglie rimanenti effettuata senza autenticazione fallisce
    When l'utente cerca di recuperare le soglie rimanenti per la finalità con ID "%random" per m2m
    Then si ottiene status code 401
