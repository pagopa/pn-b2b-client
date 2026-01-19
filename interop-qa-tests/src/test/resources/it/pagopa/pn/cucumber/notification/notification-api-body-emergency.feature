@bff-notification-parallel
@disable-notifications-hooks # FIXME usato per velocizzare l'esecuzione dei test in locale, rimuovere

# TODO fragilità: la regex di molti deeplink termina con .+, che catturerebbe anche indirizzi con più di un path variabile, quando ce ne si aspetterebbe soltanto 1. Correggere regex escludendo carattere '/'.
Feature: API Notifiche - verifica bodies (generato da excel)
  #Background:
  # eliminazione di ogni notifica presente per ogni ente ed utente (applicato con Cucumber Hook @Before)
  # abilitazione delle notifiche in-app per ogni ente ed utente (applicato con Cucumber Hook @Before)
  # disabilitazione delle notifiche in-app per ogni ente ed utente (applicato con Cucumber Hook @After)

  Scenario: [Nuova richiesta di fruizione per un tuo e-service] - Hai ricevuto una nuova richiesta di fruizione per l'e-service formulata da part…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Hai ricevuto una nuova richiesta di fruizione per l'e-service .+ formulata da parte di .+\." e "/erogazione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Richiesta di fruizione accettata automaticamente] - È stata accettata una richiesta di fruizione per l'e-service formulata da parte…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "È stata accettata una richiesta di fruizione per l'e-service .+ formulata da parte di .+\." e "/erogazione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Richiesta di fruizione aggiornata per un tuo e-service] - L'ente ha aggiornato la propria richiesta di fruizione per l'e-service alla ver…
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha aggiornato la propria richiesta di fruizione per l'e-service .+ alla versione più recente\." e "/erogazione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Sospensione richiesta di fruizione da parte del fruitore] - L'ente ha sospeso la propria richiesta di fruizione per il suo e-service .
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha sospeso la propria richiesta di fruizione per il suo e-service .+\." e "/erogazione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Sospensione richiesta di fruizione da parte della Piattaforma] - La Piattaforma PDND ha sospeso la richiesta di fruizione del fruitore per il tu…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "La Piattaforma PDND ha sospeso la richiesta di fruizione del fruitore .+ per il tuo e-service .+, in quanto l'ente fruitore non dispone più dei requisiti per poter fruire di questi dati\." e "/erogazione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Riattivazione richiesta di fruizione da parte del fruitore] - L'ente ha riattivato la propria richiesta di fruizione per il tuo e-service , p…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha riattivato la propria richiesta di fruizione per il tuo e-service .+, precedentemente sospesa\." e "/erogazione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Riattivazione richiesta di fruizione da parte della Piattaforma] - La Piattaforma PDND ha riattivato la richiesta di fruizione del fruitore per il…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "La Piattaforma PDND ha riattivato la richiesta di fruizione del fruitore .+ per il tuo e-service .+, precedentemente sospesa\." e "/erogazione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Richiesta di fruizione archiviata dal fruitore] - Ti informiamo che il fruitore ha archiviato la sua richiesta di fruizione per i…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che il fruitore .+ ha archiviato la sua richiesta di fruizione per il tuo e-service .+\." e "/erogazione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Nuovo client associato a una finalità] - L'ente ha associato un proprio client alla finalità "" per il tuo e-service .
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha associato un proprio client alla finalità .+ per il tuo e-service .+" e "/erogazione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Client disassociato da una finalità] - L'ente ha disassociato un proprio client dalla finalità "" per il tuo e-service…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha disassociato un proprio client dalla finalità .+ per il tuo e-service .+\." e "/erogazione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Richiesta di adeguamento piano di carico] - L'ente ha richiesto un adeguamento del piano di carico per la finalità "", asso…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha richiesto un adeguamento del piano di carico per la finalità .+, associata al tuo e-service .+\." e "/erogazione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [NOTIF_001] - L'ente ha inviato la finalità "", che prevede un piano di carico superiore alla…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha inviato la finalità .+, che prevede un piano di carico superiore alla tua soglia, associata al tuo e-service .+\." e "/erogazione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Finalità sospesa dal fruitore] - Ti informiamo che l'ente ha sospeso la finalità "", associata al tuo e-service .
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente .+ ha sospeso la finalità .+, associata al tuo e-service .+\." e "/erogazione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Finalità riattivata dal fruitore] - Ti informiamo che l'ente ha riattivato la finalità "", associata al tuo e-servi…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente .+ ha riattivato la finalità .+, associata al tuo e-service .+\." e "/erogazione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Finalità archiviata dal fruitore] - Ti informiamo che il fruitore ha archiviato la finalità "", associata al tuo e-…
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente .+ ha archiviato la finalità .+, associata al tuo e-service .+\." e "/erogazione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  #Tickets: https://pagopa.atlassian.net/browse/PIN-8964, https://pagopa.atlassian.net/browse/PIN-8968
  Scenario: [Hai sospeso un tuo template e-service] - È stato sospeso il tuo template "".
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "È stato sospeso il tuo template .+\." e "/erogazione/template-eservice/.+"

  Scenario: [Una versione di "<Nome E-service>" è stata sospesa] - L'ente erogatore ha sospeso la versione dell'e-service "", a cui sei iscritto.
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha sospeso la versione .+ dell'e-service .+\, a cui sei iscritto\." e "/catalogo-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Una versione di "<Nome E-service>" è stata riattivata] - L'ente erogatore ha riattivato la versione dell'e-service "", precedentemente s…
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha riattivato la versione .+ dell'e-service .+, precedentemente sospesa\." e "/catalogo-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Nuova versione disponibile per "<Nome E-service>"] - È disponibile una nuova versione () per l'e-service "", pubblicato da .
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "È disponibile una nuova versione \(.+\) per l'e-service .+, pubblicato da .+\." e "/catalogo-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Modifiche alla versione di "<Nome E-service>"] - L'ente erogatore ha apportato delle modifiche alle soglie di carico della versi…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha apportato delle modifiche alle soglie di carico della versione .+ dell'e-service .+ a cui sei iscritto\." e "/catalogo-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Modifiche alla versione di "<Nome E-service>"] - L'ente erogatore ha aggiunto un documento nella versione dell'e-service "" a cu…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha aggiunto un documento nella versione .+ dell'e-service .+ a cui sei iscritto\." e "/catalogo-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Modifiche alla versione di "<Nome E-service>"] - L'ente erogatore ha modificato la descrizione nella versione dell'e-service ""…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha modificato la descrizione nella versione .+ dell'e-service .+ a cui sei iscritto\." e "/catalogo-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Modifiche alla versione di "<Nome E-service>"] - L'ente erogatore ha aggiornato un documento nella versione dell'e-service "" a…
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e un documento già caricato
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente aggiorna il nome di quel documento
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha aggiornato un documento .+ della versione [0-9]+ dell'e-service .+, a cui sei iscritto\." e "/catalogo-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Nuovo livello di sicurezza per "<Nome E-service>"] - Ti informiamo che l'ente erogatore ha aggiunto un nuovo livello di sicurezza (p…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    When l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente erogatore .+ ha aggiunto un nuovo livello di sicurezza \(portachiavi\) all'e-service .+" e "/catalogo-e-service/.+/.+"

  Scenario: [L'e-service "<Vecchio Nome E-service>" è stato rinominato] - Ti informiamo che l'e-service "" è stato rinominato in "" dall'ente erogatore.…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'e-service .+ è stato rinominato in .+ dall'ente erogatore\. La tua richiesta di fruizione rimane attiva e non sono richieste azioni da parte tua\." e "/catalogo-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [La tua richiesta per "<Nome E-service>" è stata accettata] - L'ente erogatore ha accettato la richiesta di fruizione formulata dal tuo ente…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha accettato la richiesta di fruizione formulata dal tuo ente per l'e-service .+\. Puoi ora procedere alla creazione dei voucher per iniziare a interrogare le API\." e "/fruizione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [La tua richiesta per "<Nome E-service>" è stata rifiutata] - La richiesta di fruizione per l'e-service "" è stata rifiutata dall'ente erogat…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "La richiesta di fruizione per l'e-service .+ è stata rifiutata dall'ente erogatore\." e "/fruizione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Sospensione richiesta di fruizione per "<Nome E-service>"] - L'ente erogatore ha sospeso la richiesta di fruizione formulata dal tuo ente pe…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha sospeso la richiesta di fruizione formulata dal tuo ente per l'e-service .+\. Non potrai utilizzare i voucher associati fino alla riattivazione\." e "/fruizione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [NOTIFICATION_AGREEMENTS_5] - Sospensione richiesta di fruizione da parte della Piattaforma
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "La Piattaforma PDND ha sospeso la richiesta di fruizione formulata dal tuo ente per l'e-service .+, in quanto non risultano più soddisfatti i requisiti necessari\. " e "/fruizione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [La tua richiesta per "<Nome E-service>" è stata riattivata] - L'ente erogatore ha riattivato la richiesta di fruizione formulata dal tuo ente…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha riattivato la richiesta di fruizione formulata dal tuo ente per l'e-service .+, precedentemente sospesa\. Puoi nuovamente utilizzare i voucher associati\." e "/fruizione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Riattivazione richiesta da parte della Piattaforma] - La Piattaforma PDND ha riattivato la richiesta di fruizione formulata dal tuo e…
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "La Piattaforma PDND ha riattivato la richiesta di fruizione formulata dal tuo ente per l'e-service .+, precedentemente sospesa\." e "/fruizione/richieste/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Avviso: soglia di carico per finalità superata] - La stima di carico complessiva per le finalità associate all'e-service "" ha su…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "La stima di carico complessiva per le finalità associate all'e-service .+ ha superato la soglia massima consentita dall'erogatore pari a .+ chiamate API giornaliere\." e "/fruizione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  # Fallito, in quanto questa specifica notifica non è stata implementata (segnata come non-implementata nello sheet di riferimento). Test da togliere eventualmente.
  #Scenario: [Richiesta di adeguamento piano accettata] - L'ente erogatore ha accettato la richiesta di adeguamento del piano di carico f…
  #  Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha accettato la richiesta di adeguamento del piano di carico formulata dal tuo ente per la finalità .+, associata all'e-service .+\." e "/fruizione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Richiesta di adeguamento piano rifiutata] - L'ente erogatore ha rifiutato la richiesta di adeguamento del piano di carico f…
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha rifiutato la richiesta di adeguamento del piano di carico formulata dal tuo ente per la finalità .+, associata all'e-service .+\." e "/fruizione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [La tua finalità "<nome finalità>" è stata approvata] - L'ente erogatore ha approvato la finalità "" che hai richiesto per l'e-service…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha approvato la finalità .+ che hai richiesto per l'e-service .+\." e "/fruizione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [La tua finalità "<nome finalità>" è stata rifiutata] - L'ente erogatore ha rifiutato la finalità "" che il tuo ente ha inoltrato per l…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha rifiutato la finalità .+ che il tuo ente ha inoltrato per l'e-service .+\." e "/fruizione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Sospensione della finalità "<nome finalità>"] - L'ente erogatore ha sospeso la finalità "", associata all'e-service "".
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha sospeso la finalità .+, associata all'e-service .+\." e "/fruizione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Riattivazione della finalità "<nome finalità>"] - L'ente erogatore ha riattivato la finalità "", associata all'e-service ""
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente erogatore .+ ha riattivato la finalità .+, associata all'e-service .+" e "/fruizione/finalita/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Nuova versione del template "<Nome Template>"] - L'ente ha pubblicato una nuova versione () del template ""
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha pubblicato una nuova versione .+ del template .+" e "/erogazione/e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Aggiornamento nome del template "<Vecchio Nome Template>"] - Ti informiamo che il tuo e-service è stato rinominato in in quanto è stato modi…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che il tuo e-service .+ è stato rinominato in .+ in quanto è stato modificato il template e-service da cui lo hai generato\." e "/erogazione/e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Sospensione del template "<Nome Template>"] - L'ente ha sospeso un template e-service da cui il tuo ente ha generato l'e-service
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente .+ ha sospeso il template \".+\", da cui il tuo ente ha generato l'e-service\." e "/erogazione/template-eservice/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [La tua richiesta di delega è stata accettata] - Ti informiamo che l'ente ha accettato la delega che il tuo ente gli ha conferit…
    #Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente .+ ha accettato la delega .+ che il tuo ente gli ha conferito per l'e-service .+\. La delega è ora attiva\." e "/aderente/deleghe/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente .+ ha approvato la delega .+ che il tuo ente gli ha conferito per l'e-service .+\. La delega è ora attiva\." e "/aderente/deleghe/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [La tua richiesta di delega è stata rifiutata] - Ti informiamo che l'ente ha rifiutato la delega che il tuo ente gli ha conferit…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente .+ ha rifiutato la delega .+ che il tuo ente gli ha conferito per l'e-service .+" e "/aderente/deleghe/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  # Ticket https://pagopa.atlassian.net/browse/PIN-8982
  Scenario: [Richiesta di approvazione per una nuova versione] - L'ente delegato richiede la tua approvazione per pubblicare una nuova versione…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente delegato .+ richiede la tua approvazione per pubblicare una nuova versione dell'e-service .+" e "/aderente/deleghe/.+"

  # Ticket https://pagopa.atlassian.net/browse/PIN-8982
  Scenario: [Approvata la pubblicazione della nuova versione] - L'ente delegante ha approvato la pubblicazione della nuova versione dell'e-serv…
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente delegante .+ ha approvato la pubblicazione della nuova versione dell'e-service .+ che gestisci tramite delega\." e "/aderente/deleghe/.+"

  # Ticket https://pagopa.atlassian.net/browse/PIN-8982
  Scenario: [Rifiutata la pubblicazione della nuova versione] - L'ente delegante ha rifiutato la pubblicazione della nuova versione dell'e-serv…
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente delegante .+ ha rifiutato la pubblicazione della nuova versione dell'e-service .+ che gestisci tramite delega\." e "/aderente/deleghe/.+"

  Scenario: [Hai ricevuto una richiesta di delega] - Hai ricevuto una richiesta di delega per "" dall'ente per l'e-service
    # originale
    # Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Hai ricevuto una richiesta di delega per .+ dall'ente .+ per l'e-service .+" e "/aderente/deleghe/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
    # adattato al messaggio ottenuto
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Hai ricevuto una richiesta di delega .+ dall'ente .+ per l'e-service .+" e "/aderente/deleghe/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Una delega che gestivi è stata revocata] - Ti informiamo che l'ente ha revocato la delega per l'e-service "" che ti aveva…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente .+ ha revocato la delega .+ per l'e-service .+ che ti aveva conferito\." e "/aderente/deleghe/.+"

  # Ticket: https://pagopa.atlassian.net/browse/PIN-8983 (deepLink comunque adattato nel test)
  Scenario: [Hai ricevuto un nuovo attributo certificato] - L'ente certificatore ha conferito al tuo ente l'attributo certificato "". Puoi…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente certificatore .+ ha conferito al tuo ente l'attributo certificato .+\. Puoi ora utilizzarlo nelle richieste di fruizione\." e "/aderente/anagrafica/.+"

  # Ticket: https://pagopa.atlassian.net/browse/PIN-8983 (deepLink comunque adattato nel test)
  Scenario: [Un tuo attributo certificato è stato revocato] - Ti informiamo che l'ente certificatore ha revocato l'attributo certificato "".…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente certificatore .+ ha revocato l'attributo certificato .+\. Tutte le richieste di fruizione che utilizzano tale attributo subiranno una sospensione\. Non potrai più utilizzare questo attributo per le future richieste di fruizione\." e "/aderente/anagrafica.+"

  # Ticket: https://pagopa.atlassian.net/browse/PIN-8983 (deepLink comunque adattato nel test)
  Scenario: [Hai ricevuto un nuovo attributo verificato] - L'ente certificatore ha conferito al tuo ente l'attributo verificato "". Puoi o…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'ente certificatore .+ ha conferito al tuo ente l'attributo verificato .+\. Puoi ora utilizzarlo nelle richieste di fruizione\." e "/aderente/anagrafica/.+"

  # Ticket: https://pagopa.atlassian.net/browse/PIN-8983 (deepLink comunque adattato nel test)
  Scenario: [Un tuo attributo verificato è stato revocato] - Ti informiamo che l'ente certificatore ha revocato l'attributo verificato "". T…
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che l'ente certificatore .+ ha revocato l'attributo verificato .+\. Tutte le richieste di fruizione che utilizzano tale attributo subiranno una sospensione\. Non potrai più utilizzare questo attributo per le future richieste di fruizione\." e "/aderente/anagrafica/.+"

  # trigger: it/pagopa/pn/cucumber/authorization/client-key-delete.feature:7
  Scenario: [Una chiave di e-service è stata rimossa] - L'utente ha rimosso una chiave di e-service dal client "". Assicurati che l'ope…
    #Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "L'utente .+ ha rimosso una chiave di e-service dal client .+\. Assicurati che l'operatività non sia compromessa\." e "/gestione-client/api-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "La chiave .+ è stata rimossa dal client .+\. Assicurati che l'operatività non sia compromessa\." e "/gestione-client/api-e-service/.+"

  Scenario: [Attenzione: una chiave non è più sicura] - Una chiave associata al client "" non è più considerata sicura, in quanto l'ope…
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    And un "security" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" ha già rimosso l'utente con ruolo "security" dai membri di quel client
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Una chiave associata al client .+ non è più considerata sicura, in quanto l'operatore che l'ha caricata non è più attivo\. La chiave deve essere sostituita per garantire la sicurezza e l'operatività\." e "/gestione-client/api-e-service/.+"

  Scenario: [Nuova chiave aggiunta al client "<nome client>"] - Ti informiamo che è stata aggiunta una nuova chiave e-service al client "".
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che è stata aggiunta una nuova chiave e-service al client .+\." e "/gestione-client/api-e-service/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  Scenario: [Una chiave di e-service è stata rimossa] - L'utente ha rimosso una chiave dal portachiavi erogatore "". Assicurati che l'o…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    When l'utente "admin" di "PA1" rimuove tutte le chiavi dal portachiavi erogatore
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "La chiave .+ è stata rimossa dal portachiavi erogatore .+\. Assicurati che l'operatività non sia compromessa\." e "/erogazione/portachiavi/.+"

  Scenario: [Attenzione: una chiave non è più sicura] - Una chiave associata al portachiavi erogatore "" non è più considerata sicura,…
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    And l'utente "security" di "PA1" aggiunge una chiave al portachiavi erogatore
    When l'utente "admin" di "PA1" rimuove l'utente "security" dal portachiavi erogatore
    Then per l'utente "admin" di "PA1" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Una chiave associata al portachiavi erogatore .+ non è più considerata sicura, in quanto l'operatore che l'ha caricata non è più attivo\. La chiave deve essere sostituita per garantire la sicurezza e l'operatività\." e "/erogazione/portachiavi/.+"

  Scenario: [Nuova chiave aggiunta al portachiavi erogatore "<Nome Portachiavi>"] - Ti informiamo che è stata aggiunta una nuova chiave al portachiavi erogatore .
    Then per l'utente "admin" di "PA2" è presente una notifica in-app in cui messaggio e deepLink aderiscono rispettivamente ai pattern "Ti informiamo che è stata aggiunta una nuova chiave al portachiavi erogatore .+\." e "/erogazione/portachiavi/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"

  # TODO comodità per test manuali, rimuovere
  Scenario: genera tokens
    Given l'utente è un "admin" di "PA1"
    Given l'utente è un "admin" di "PA2"
    Given l'utente è un "admin" di "GSP"