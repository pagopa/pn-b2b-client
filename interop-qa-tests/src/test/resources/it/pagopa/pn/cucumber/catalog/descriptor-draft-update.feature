@descriptor
Feature: Aggiornamento di un descrittore in bozza
  Tutti gli utenti autorizzati di enti erogatori possono aggiornare tutti i parametri di un descrittore in bozza.

  @nrt-minimal
  @descriptor_draft_update1
  Scenario Outline: [DESCRIPTOR_DRAFT_UPDATE_1] Per un e-service che ha un solo descrittore, il quale è in stato DRAFT, all’aggiornamento da parte di un utente autorizzato di alcuni parametri del descrittore, ben formattati, la bozza viene aggiornata correttamente
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già creato un e-service con un descrittore in stato "DRAFT"
    When l'utente aggiorna alcuni parametri di quel descrittore
    Then si ottiene status code <risultato>

    @happy-path
    Examples:
      | ente | ruolo        | risultato |
      | GSP  | admin        | 200       |
      | GSP  | api          | 200       |
      | GSP  | api,security | 200       |
      | PA1  | admin        | 200       |
      | PA1  | api          | 200       |
      | PA1  | api,security | 200       |

    @sad-path
    Examples:
      | ente | ruolo    | risultato |
      | GSP  | security | 403       |
      | GSP  | support  | 403       |
      | PA1  | security | 403       |
      | PA1  | support  | 403       |

  @sad-path
  @nrt-minimal
  @descriptor_draft_update2
  Scenario Outline: [DESCRIPTOR_DRAFT_UPDATE_2] Per un e-service che ha un solo descrittore, il quale è in stato NON DRAFT (PUBLISHED, SUSPENDED, DEPRECATED, ARCHIVED), all’aggiornamento di alcuni parametri del descrittore, ben formattati, l’aggiornamento della bozza restituisce errore
    Given l'utente è un "admin" di "PA1"
    Given "PA1" ha già creato un e-service con un descrittore in stato "<statoVersione>"
    When l'utente aggiorna alcuni parametri di quel descrittore
    Then si ottiene status code 400

    Examples:
      | statoVersione |
      | PUBLISHED     |
      | SUSPENDED     |
      | DEPRECATED    |
      | ARCHIVED      |

  @dailyCallsThreshold
  Scenario Outline: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_1] Per un e-service in stato DRAFT è possibile modificare dailyCallsPerConsumer all'interno degli attributi certificati
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 999999999 e dailyCallsTotal uguale a 1000000000
    When l'utente tenta di aggiungere una soglia differenziata di <dailyCallsPerConsumer> per l'attributo CERTIFIED 0-esimo creato
    Then si ottiene status code <statusCode>
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "<expectedDailyCallsPerConsumer>"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

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
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_2] Per un e-service in stato DRAFT non è possibile indicare soglie differenti per il medesimo attributo certificato
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC"
    When l'utente tenta di dichiarare due volte lo stesso attributo certificato ognuno con un dailyCallsPerConsumer differente
    Then si ottiene status code 400
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_3] Per un e-service in stato DRAFT non è possibile indicare una soglia il cui limite è superiore al limite di chiamate totali giornaliero
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 11
    When l'utente tenta di aggiungere una soglia differenziata di 12 per l'attributo CERTIFIED 0-esimo creato
    Then si ottiene status code 400
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "%null"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_4] Per un e-service in stato DRAFT non è possibile indicare una soglia associata ad un attributo verificato
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    And PA1 ha già creato 1 attributo VERIFIED
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC"
    When l'utente tenta di aggiungere una soglia differenziata di 11 per l'attributo VERIFIED 0-esimo creato
    Then si ottiene status code 400
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_5] Per un e-service in stato DRAFT non è possibile indicare una soglia associata ad un attributo dichiarato
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo DECLARED
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC"
    When l'utente tenta di aggiungere una soglia differenziata di 11 per l'attributo DECLARED 0-esimo creato
    Then si ottiene status code 400
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_6] Per un e-service in stato DRAFT è possibile indicare N soglie la cui somma dei limiti è superiore al limite di chiamate totali giornaliero
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 3 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 20
    When l'utente tenta di aggiungere una soglia differenziata di 8 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "8"
    And l'utente tenta di aggiungere una soglia differenziata di 9 per l'attributo CERTIFIED 1-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 1-esimo creato nel gruppo 0-esimo è uguale a "9"
    Then l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 2-esimo creato
    And la soglia differenziata per l'attributo CERTIFIED 2-esimo creato nel gruppo 0-esimo è uguale a "10"
    And si ottiene status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_7] Per un e-service in stato DRAFT non è possibile indicare N soglie di cui almeno una esplicita un limite superiore al limite di chiamate totali giornaliero
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 3 attributo CERTIFIED
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 11
    When l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "10"
    And l'utente tenta di aggiungere una soglia differenziata di 12 per l'attributo CERTIFIED 1-esimo creato
    Then si ottiene status code 400
    And la soglia differenziata per l'attributo CERTIFIED 1-esimo creato nel gruppo 0-esimo è uguale a "%null"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_8] Per un e-service in stato DRAFT non è possibile specificare due gruppi di attributi certificati dove in almeno uno è presente un valore invalido di dailyCallPerConsumer
    Given l'utente è un "admin" di "PA1"
    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" ne possiede uno per gruppo
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 11
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo e il gruppo 0-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "10"
    When l'utente tenta di aggiungere una soglia differenziata di 0 per l'attributo CERTIFIED 0-esimo e il gruppo 1-esimo creato
    Then si ottiene status code 400
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 1-esimo è uguale a "%null"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_9] Per un e-service in stato DRAFT è possibile specificare due gruppi di attributi certificati dove in ognuno è presente un valore valido di dailyCallPerConsumer
    Given l'utente è un "admin" di "PA1"
    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" ne possiede uno per gruppo
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 11
    When l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo e il gruppo 0-esimo creato
    And si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "10"
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo e il gruppo 1-esimo creato
    Then si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 1-esimo è uguale a "10"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_10] Per un e-service in stato DRAFT non è possibile specificare due gruppi di attributi certificati dove in almeno uno è presente un valore invalido di dailyCallPerConsumer
    Given l'utente è un "admin" di "PA1"
    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" ne possiede uno per gruppo
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 11
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo e il gruppo 0-esimo creato
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "10"
    When l'utente tenta di duplicare l'attributo CERTIFIED 0-esimo nel gruppo 0-esimo
    Then si ottiene status code 400
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_11] Per un e-service in stato DRAFT non è possibile avere due attributi certificati in due gruppi differenti di cui uno con un valore invalido di dailyCallPerConsumer
    Given l'utente è un "admin" di "PA1"
    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" ne possiede uno per gruppo
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC"
    And l'utente tenta di aggiungere una soglia differenziata di 1 per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "1"
    When l'utente tenta di aggiungere una soglia differenziata di 0 per l'attributo CERTIFIED 0-esimo creato nel gruppo 1-esimo
    Then si ottiene status code 400
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 1-esimo è uguale a "%null"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_12] Per un e-service in stato DRAFT è possibile avere lo stesso attributo certificato in due gruppi differenti con un valore valido di dailyCallPerConsumer
    Given l'utente è un "admin" di "PA1"
    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" ne possiede uno per gruppo
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC"
    And si ottiene status code 200
    When l'utente tenta di duplicare l'attributo CERTIFIED 0-esimo contenuto nel gruppo 0-esimo nel gruppo 1-esimo
    Then si ottiene status code 200
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_13] Per un e-service in stato DRAFT è possibile avere due attributi certificati in due gruppi differenti tutti e due con un valore valido di dailyCallPerConsumer
    Given l'utente è un "admin" di "PA1"
    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" ne possiede uno per gruppo
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC"
    When l'utente tenta di aggiungere una soglia differenziata di 1 per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "1"
    And l'utente tenta di aggiungere una soglia differenziata di 2 per l'attributo CERTIFIED 0-esimo creato nel gruppo 1-esimo
    Then si ottiene status code 200
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 1-esimo è uguale a "2"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And si ottiene status code 200

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_14] Per un e-service in stato DRAFT non è possibile avere dailyCallPerConsumer superiore a dailyCallTotals
    Given l'utente è un "admin" di "PA1"
    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" ne possiede uno per gruppo
    When "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 11 e dailyCallsTotal uguale a 10
    Then si ottiene status code 400

  @dailyCallsThreshold
  Scenario: [DESCRIPTOR_DRAFT_UPDATE_THRESHOLD_15] Per un e-service in stato DRAFT è possibile impostare dailyCallsPerConsumer uguale a dailyCallsTotal
    Given l'utente è un "admin" di "PA1"
    And PA1 ha già creato 1 attributo CERTIFIED
    When "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 10
    And si ottiene status code 200
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo creato
    Then l'e-service ha questa configurazione:
      | dailyCallsPerConsumer | 10 |
      | dailyCallsTotal       | 10 |
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "10"

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_ADD_1] L'erogatore di un e-service in stato DRAFT può assegnare diversi attributi
  certificati discreti, impostando le relative soglie e i comparatori (sono incluse le logiche OR e AND).

    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    When "PA2" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 100                   |
      | CERTIFIED          | 0     |            |         | 200                   |
      | CERTIFIED          | 1     |            |         |                       |
      | CERTIFIED_DISCRETE | 1     | GTE        | 500000  |                       |
      | CERTIFIED          | 1     |            |         |                       |
      | DECLARED           | 0     |            |         |                       |
    And si ottiene response status code 200
    Then l'e-service ha questa configurazione:
      | dailyCallsPerConsumer | 10   |
      | dailyCallsTotal       | 1000 |
    And la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 0-esimo è uguale a "100", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 1000000
    And la soglia differenziata per l'attributo CERTIFIED 1-esimo creato nel gruppo 0-esimo è uguale a "200"
    And la soglia differenziata per l'attributo CERTIFIED_DISCRETE 1-esimo creato nel gruppo 1-esimo è uguale a "%null", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 500000

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_ADD_2] L'erogatore di un e-service in stato DRAFT può modificare diversi attributi
  certificati discreti, impostando le relative soglie e comparatori (sono incluse le logiche OR e AND).

    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    When "PA2" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 100                   |
      | CERTIFIED          | 0     |            |         | 200                   |
      | CERTIFIED          | 1     |            |         |                       |
      | CERTIFIED_DISCRETE | 1     | GTE        | 500000  |                       |
      | CERTIFIED          | 1     |            |         |                       |
      | DECLARED           | 0     |            |         |                       |
    And si ottiene response status code 200
    When l'utente aggiorna il descrittore dell'e-service con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 2500000 | 150                   |
      | CERTIFIED          | 0     |            |         | 190                   |
      | CERTIFIED          | 1     |            |         |                       |
      | CERTIFIED_DISCRETE | 1     | LTE        | 900000  |                       |
      | CERTIFIED          | 1     |            |         |                       |
      | DECLARED           | 0     |            |         |                       |
    And si ottiene response status code 200
    Then la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 0-esimo è uguale a "150", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 2500000
    And la soglia differenziata per l'attributo CERTIFIED 1-esimo creato nel gruppo 0-esimo è uguale a "190"
    And la soglia differenziata per l'attributo CERTIFIED_DISCRETE 1-esimo creato nel gruppo 1-esimo è uguale a "%null", mentre il discrete comparator è "LTE" e il discrete threshhold è uguale a 900000

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_UPD_1] L'erogatore di un e-service in stato PUBLISHED può modificare diversi attributi
  certificati discreti, impostando le relative soglie e comparatori (sono incluse le logiche OR e AND).

    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    When "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 100                   |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    When l'utente pubblica il descrittore dell'e-service con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 2500000 | 150                   |
    And si ottiene response status code 200
    Then la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 0-esimo è uguale a "150", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 2500000

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_UPD_2] L'erogatore di un e-service in stato PUBLISHED non può modificare la configurazione
  di un attributo certificato discreto.

    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 100                   |
    And si ottiene response status code 200
    And l'utente pubblica l'e-service
    And la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 0-esimo è uguale a "100", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 1000000
    When l'utente tenta di aggiornare l'attributo certificato discreto 0-esimo del gruppo 0-esimo con discrete comparator "GT" e il discrete threshhold 25000
    Then si ottiene response status code 400

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_UPD_3] L'erogatore di un e-service in stato DRAFT può modificare la configurazione
  di un attributo certificato discreto.

    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    And "PA2" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 100                   |
    And si ottiene response status code 200
    And l'utente pubblica l'e-service
    And la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 0-esimo è uguale a "100", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 1000000
    When l'utente tenta di aggiornare l'attributo certificato discreto 0-esimo del gruppo 0-esimo con discrete comparator "GT" e il discrete threshhold 25000
    Then si ottiene response status code 200
    And la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 0-esimo è uguale a "100", mentre il discrete comparator è "GT" e il discrete threshhold è uguale a 25000

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_NO_DUPLICATED] Un e-service in stato DRAFT non può avere lo stesso attributo certificato
  discreto nello stesso gruppo (logiche OR non consentite).

    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    When "PA2" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 100                   |
      | CERTIFIED_DISCRETE | 0     | GTE        | 500000  |                       |
    Then si ottiene response status code 400

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_THRESHOLD_1] Per un e-service in stato DRAFT non è possibile indicare una dailyCallsPerConsumer
  nell'attributo certificato discreto superiore al limite di chiamate totali giornaliero
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    When "PA2" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 10000                 |
    Then si ottiene response status code 400

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_THRESHOLD_2] Per un e-service in stato DRAFT è possibile indicare più attributi certificati
    discreti la cui somma delle dailyCallsPerConsumer è superiore al limite di chiamate totali giornaliero dell'e-service.
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    When "PA2" ha già creato un e-service in stato "DRAFT" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 600                   |
      | CERTIFIED_DISCRETE | 1     | GTE        | 125000  | 700                   |
      | CERTIFIED          | 1     | GTE        | 95000   | 900                   |
    And si ottiene response status code 200
    Then la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 0-esimo è uguale a "600", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 1000000
    And la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 1-esimo è uguale a "700", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 125000
    And la soglia differenziata per l'attributo CERTIFIED 1-esimo creato nel gruppo 1-esimo è uguale a "900"

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_THRESHOLD_3] Per un e-service in stato PUBLISHED non è possibile indicare una dailyCallsPerConsumer
  nell'attributo certificato discreto superiore al limite di chiamate totali giornaliero
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    When "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 10000                 |
    Then si ottiene response status code 400

  @certifiedDiscreteAttribute
  @certifiedDiscreteAttributeFlagOn
  Scenario: [CERT_DISCRETE_ATTR_ESERVICE_THRESHOLD_4] Per un e-service in stato PUBLISHED è possibile indicare più attributi certificati
  discreti la cui somma delle dailyCallsPerConsumer è superiore al limite di chiamate totali giornaliero dell'e-service.
    Given l'utente è un "admin" di "PA2"
    And l'utente richiede una operazione di listing degli attributi certificati discreti disponibili
    And l'utente "PA1" possiede almeno un attributo certificato discreto
    When "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 1000 e con i seguenti attributi:
      | kind               | group | comparator | value   | dailyCallsPerConsumer |
      | CERTIFIED_DISCRETE | 0     | GTE        | 1000000 | 600                   |
      | CERTIFIED_DISCRETE | 1     | GTE        | 125000  | 700                   |
      | CERTIFIED          | 1     | GTE        | 95000   | 900                   |
    And si ottiene response status code 200
    And l'e-service è in stato "PUBLISHED"
    Then la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 0-esimo è uguale a "600", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 1000000
    And la soglia differenziata per l'attributo CERTIFIED_DISCRETE 0-esimo creato nel gruppo 1-esimo è uguale a "700", mentre il discrete comparator è "GTE" e il discrete threshhold è uguale a 125000
    And la soglia differenziata per l'attributo CERTIFIED 1-esimo creato nel gruppo 1-esimo è uguale a "900"
