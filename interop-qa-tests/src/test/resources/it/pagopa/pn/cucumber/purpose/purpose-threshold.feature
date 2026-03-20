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
    And la finalità è in stato WAITING_FOR_APPROVAL

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
    Then i residui relativi alle dailyCalls associati alla finalità sono pari a:
      | remainingDailyCallsPerConsumer | 95  |
      | remainingDailyCallsTotal       | 989 |
