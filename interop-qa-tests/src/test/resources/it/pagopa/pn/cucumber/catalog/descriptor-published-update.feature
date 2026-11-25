@descriptor
Feature: Aggiornamento di un descrittore già pubblicato
  Tutti gli utenti autorizzati di enti erogatori possono aggiornare la durata voucher e le soglie di carico di un descrittore già punlicato.

  @sad-path
  @nrt-minimal
  @descriptor_published_update1
  Scenario Outline: [DESCRIPTOR_PUBLISHED_UPDATE_1] Per un e-service che ha un solo descrittore, il quale è in stato PUBLISHED, all'aggiornamento da parte di un utente autorizzato della durata del voucher e delle soglie di carico del descrittore, la bozza viene aggiornata correttamente
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente aggiorna la durata del voucher e le soglie di carico di quel descrittore
    Then si ottiene status code <risultato>

    Examples: 
      | ente | ruolo        | risultato |
      | GSP  | admin        |       400 |
      | GSP  | api          |       400 |
      | GSP  | security     |       403 |
      | GSP  | api,security |       400 |
      | GSP  | support      |       403 |
      | PA1  | admin        |       400 |
      | PA1  | api          |       400 |
      | PA1  | security     |       403 |
      | PA1  | api,security |       400 |
      | PA1  | support      |       403 |

    @nuovi-operatori-update
    Examples: 
      | ente | ruolo        | risultato |
      | GSP  | reviewer     |       403 |
      | GSP  | viewer       |       403 |
      | PA2  | reviewer     |       403 |
      | PA2  | viewer       |       403 |

  @nrt-minimal
  @descriptor_published_update2
  Scenario Outline: [DESCRIPTOR_PUBLISHED_UPDATE_2] Per un e-service che ha un solo descrittore, il quale è in stato PUBLISHED, SUSPENDED o DEPRECATED, all'aggiornamento della durata del voucher e delle soglie di carico, l'operazione va a buon fine
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoVersione>"
    When l'utente aggiorna la durata del voucher e le soglie di carico di quel descrittore
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | statoVersione | risultato |
      | PUBLISHED     |       200 |
      | SUSPENDED     |       200 |
      | DEPRECATED    |       200 |

    @sad-path
    Examples:
      | statoVersione | risultato |
      | ARCHIVED      |       400 |
      | DRAFT         |       400 |

  @dailyCallsThreshold
  Scenario Outline: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_1] Per un e-service in stato PUBLISHED è possibile modificare dailyCallsPerConsumer all'interno degli attributi certificati
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000000000
    And si ottiene status code 200
    When l'utente tenta di aggiungere una soglia differenziata di <dailyCallsPerConsumer> per l'attributo CERTIFIED 0-esimo creato
    Then si ottiene status code <statusCode>
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "<expectedDailyCallsPerConsumer>"

    @happy-path
    Examples:
      | dailyCallsPerConsumer | statusCode | expectedDailyCallsPerConsumer |
      | 100                   | 200        | 100                           |
      | 1000000000            | 200        | 1000000000                    |

    @sad-path
    Examples:
      | dailyCallsPerConsumer | statusCode | expectedDailyCallsPerConsumer |
      | 0                     | 400        | %null                         |
      | 1000000001            | 400        | %null                         |

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_2] Per un e-service in stato PUBLISHED non è possibile indicare soglie differenti per il medesimo attributo certificato
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC"
    When l'utente tenta di dichiarare due volte lo stesso attributo certificato ognuno con un dailyCallsPerConsumer differente
    Then si ottiene status code 400
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "%null"

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_3] Per un e-service in stato PUBLISHED non è possibile indicare una soglia il cui limite è superiore al limite di chiamate totali giornaliero
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 11
    And PA1 ha già creato 1 attributo CERTIFIED
    And l'utente è un "admin" di "PA1"
    And l'utente associa l'attributo CERTIFIED 1-esimo creato all'eservice
    When l'utente tenta di aggiungere una soglia differenziata di 12 per l'attributo CERTIFIED 1-esimo creato
    Then si ottiene status code 400
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "%null"
    And la soglia differenziata per l'attributo CERTIFIED 1-esimo creato nel gruppo 0-esimo è uguale a "%null"

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_4] Per un e-service in stato PUBLISHED non è possibile indicare una soglia associata ad un attributo verificato
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And PA1 ha già creato 1 attributo VERIFIED
    And l'utente assegna a "PA2" l'attributo certificato precedentemente creato
    And si ottiene status code 200
    And l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA1" ha già verificato l'attributo verificato a "PA2"
    And "PA1" ha già approvato quella richiesta di fruizione
    And si ottiene status code 200
    When l'utente tenta di aggiungere una soglia differenziata di 11 per l'attributo VERIFIED 0-esimo creato
    Then si ottiene status code 400

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_5] Per un e-service in stato PUBLISHED non è possibile indicare una soglia associata ad un attributo dichiarato
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And PA1 ha già creato 1 attributo DECLARED
    And si ottiene status code 200
    And l'utente assegna a "PA2" l'attributo certificato precedentemente creato
    And si ottiene status code 200
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC"
    When l'utente tenta di aggiungere una soglia differenziata di 11 per l'attributo DECLARED 0-esimo creato
    Then si ottiene status code 400

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_6] Per un e-service in stato PUBLISHED è possibile indicare N soglie la cui somma dei limiti è superiore al limite di chiamate totali giornaliero
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 3 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000
    When l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "10"
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 1-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 1-esimo creato nel gruppo 0-esimo è uguale a "10"
    Then l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 2-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 2-esimo creato nel gruppo 0-esimo è uguale a "10"

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_7] Per un e-service in stato PUBLISHED non è possibile indicare N soglie di cui almeno una esplicita un limite superiore al limite di chiamate totali giornaliero
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 3 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 100
    When l'utente tenta di aggiungere una soglia differenziata di 11 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "11"
    And l'utente tenta di aggiungere una soglia differenziata di 11 per l'attributo CERTIFIED 1-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 1-esimo creato nel gruppo 0-esimo è uguale a "11"
    Then l'utente tenta di aggiungere una soglia differenziata di 11 per l'attributo CERTIFIED 2-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 2-esimo creato nel gruppo 0-esimo è uguale a "11"

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_15] Per un e-service in stato PUBLISHED non è possibile avere dailyCallPerConsumer superiore a dailyCallTotals
    Given l'utente è un "admin" di "PA1"
    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" ne possiede uno per gruppo
    When "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 11 e dailyCallsTotal uguale a 10
    Then si ottiene status code 400

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_PUBLISHED_UPDATE_THRESHOLD_16] Per un e-service in stato PUBLISHED è possibile impostare dailyCallsPerConsumer uguale a dailyCallsTotal
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    When "PA1" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 10
    And si ottiene status code 200
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo creato
    Then l'e-service ha questa configurazione:
      | dailyCallsPerConsumer | 10 |
      | dailyCallsTotal       | 10 |
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "10"
