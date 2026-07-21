@bff-notification
Feature: API Notifiche - verifica notifiche in-app messaggio e deep link (generato da excel)

  # PASSA
  Scenario: [Notifica nuova richiesta fruizione] Ricezione nuova richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    When "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    Hai ricevuto una nuova richiesta di fruizione per l'e-service $CONTEXT(eServiceName) formulata
    da parte di $CONTEXT(consumerName).
    """

  # PASSA
  Scenario: [Notifica richiesta fruizione accettata] Accettazione automatica di una richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    È stata accettata una richiesta di fruizione per l'e-service $CONTEXT(eServiceName) formulata
    da parte di $CONTEXT(consumerName).
    """

  # PASSA
  Scenario: [Notifica richiesta fruizione aggiornata] Il fruitore aggiorna la richiesta di fruizione per la nuova versione dell'e-service pubblicata dall'erogatore
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And "PA2" ha già pubblicato una nuova versione per quell'e-service
    When "PA1" ha già aggiornato la richiesta di fruizione all'ultima versione dell'eservice
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha aggiornato la propria richiesta di fruizione per l'e-service
    $CONTEXT(eServiceName) alla versione più recente.
    """

  # PASSA
  Scenario: [Notifica richiesta fruizione sospesa e riattivata] Il fruitore sospende e riattiva la richiesta di fruizione per l'e-service dell'erogatore

    # Scenario: [Notifica richiesta fruizione sospesa] Il fruitore sospende la richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "SUSPENDED" per quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha sospeso la propria richiesta di fruizione per il suo e-service
    $CONTEXT(eServiceName).
    """

    # PASSA
    # Scenario: [Notifica richiesta fruizione riattivata] Il fruitore riattiva la richiesta di fruizione per l'e-service dell'erogatore
    When "PA2" ha già attivato nuovamente quella richiesta di fruizione come CONSUMER
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha riattivato la propria richiesta di fruizione per il tuo e-service
    $CONTEXT(eServiceName), precedentemente sospesa.
    """

  # PASSA
  Scenario: [Notifica richiesta fruizione sospesa dalla Piattaforma] La Piattaforma PDND sospende la richiesta di fruizione del fruitore causa perdita dei requisiti
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    And "PA2" ha già approvato quella richiesta di fruizione
    And l'utente è un "admin" di "PA2"
    When l'utente revoca l'attributo precedentemente verificato
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    La Piattaforma PDND ha sospeso la richiesta di fruizione del fruitore $CONTEXT(consumerName) per il tuo
    e-service $CONTEXT(eServiceName), in quanto l'ente fruitore non dispone più dei requisiti per poter fruire
    di questi dati.
    """

  # PASSA
  Scenario: [Notifica richiesta fruizione riattivata dalla Piattaforma] La Piattaforma PDND riattiva la richiesta di fruizione del fruitore per riottenimento dei requisiti
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    And "PA2" ha già approvato quella richiesta di fruizione
    And l'utente è un "admin" di "PA2"
    And l'utente revoca l'attributo precedentemente verificato
    When "PA2" ha già verificato l'attributo verificato a "PA1"
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    La Piattaforma PDND ha riattivato la richiesta di fruizione del fruitore $CONTEXT(consumerName) per
    il tuo e-service $CONTEXT(eServiceName), precedentemente sospesa.
    """

  # PASSA
  Scenario: [Notifica richiesta fruizione archiviata] Il fruitore archivia la richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "ARCHIVED" per quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    Ti informiamo che il fruitore $CONTEXT(consumerName) ha archiviato la sua richiesta di fruizione per
    il tuo e-service $CONTEXT(eServiceName).
    """

  # PASSA
  Scenario: [Notifica client associato a una finalità] Il fruitore associa un proprio client ad una finalità dell'e-service dell'erogatore
    Given "GSP" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato una nuova finalità attiva per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" ha già associato quella nuova finalità a quel client
    Then admin di "GSP" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha associato un proprio client alla finalità "$CONTEXT(purposeTitle)"
    per il tuo e-service $CONTEXT(eServiceName).
    """

  # PASSA
  Scenario: [Notifica client disassociato da una finalità] Il fruitore disassocia un proprio client ad una finalità dell'e-service dell'erogatore
    Given "GSP" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato una nuova finalità attiva per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And "PA1" ha già associato quella nuova finalità a quel client
    When l'utente "admin" di "PA1" richiede la disassociazione della finalità dal client con successo
    Then admin di "GSP" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha disassociato un proprio client dalla finalità "$CONTEXT(purposeTitle)"
    per il tuo e-service $CONTEXT(eServiceName).
    """

  # PASSA
  Scenario: [Notifica finalità sospesa dal fruitore] Il fruitore sospende la finalità associata all'e-service dell'erogatore
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    Ti informiamo che l'ente $CONTEXT(consumerName) ha sospeso la finalità "$CONTEXT(purposeTitle)",
    associata al tuo e-service $CONTEXT(eServiceName).
    """

  # PASSA
  Scenario: [Notifica finalità riattivata dal fruitore] Il fruitore riattiva la finalità associata all'e-service dell'erogatore
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "SUSPENDED" per quell'eservice
    When l'utente "admin" di "PA2" riattiva la finalità in stato "SUSPENDED" per quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    Ti informiamo che l'ente $CONTEXT(consumerName) ha riattivato la finalità "$CONTEXT(purposeTitle)",
    associata al tuo e-service $CONTEXT(eServiceName).
    """

  # PASSA
  Scenario: [Notifica finalità archiviata dal fruitore] Il fruitore archivia la finalità associata all'e-service dell'erogatore
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When "PA2" ha già archiviato quella finalità
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    Ti informiamo che l'ente $CONTEXT(consumerName) ha archiviato la finalità "$CONTEXT(purposeTitle)",
    associata al tuo e-service $CONTEXT(eServiceName).
    """

  # PASSA
  Scenario: [Notifica richiesta adeguamento piano di carico] Il fruitore chiede un adeguamento del piano di carico della finalità associata all'e-service
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When "PA1" ha già richiesto l'aggiornamento della stima di carico superando i limiti di quell'e-service
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha richiesto un adeguamento del piano di carico per la finalità
    "$CONTEXT(purposeTitle)", associata al tuo e-service $CONTEXT(eServiceName).
    """

  # PASSA
  Scenario: [Notifica e-service template sospeso] Il creatore sospende il proprio e-service template e l'erogatore viene notificato
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service - OK"
    And l'utente è un "admin" di "PA2"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "OK_2" a partire dal template indicando tutte le specifiche
    And l'utente è un "admin" di "PA1"
    When l'utente effettua la sospensione dell'e-service template
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link TEMPLATE_E_SERVICE_EROGAZIONE
    """
    È stato sospeso il tuo template "$CONTEXT(eServiceTemplateName)".
    """

  # PASSA
  Scenario: [Notifica versione e-service nuova poi sospesa e riattivata] L'erogatore pubblica nuova versione dell'e-service a cui il fruitore è iscritto, poi la sospende e poi la riattiva

    # Scenario: [Notifica nuova versione e-service] L'erogatore pubblica nuova versione dell'e-service a cui il fruitore è iscritto
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA2" ha già pubblicato una nuova versione per quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    È disponibile una nuova versione (2) per l'e-service "$CONTEXT(eServiceName)",
    pubblicato da $CONTEXT(producerName).
    """

    # PASSA
    # Scenario: [Notifica versione e-service sospesa] L'erogatore sospende la versione dell'e-service a cui il fruitore è iscritto
    When "PA2" ha già sospeso la vecchia versione di quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    L'ente erogatore $CONTEXT(producerName) ha sospeso la versione 1 dell'e-service $CONTEXT(eServiceName),
    a cui sei iscritto.
    """

    # PASSA
    # Scenario: [Notifica versione e-service riattivata] L'erogatore riattiva la versione dell'e-service precedentemente sospesa
    When "PA2" ha già attivato nuovamente la vecchia versione quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    L'ente erogatore $CONTEXT(producerName) ha riattivato la versione 1 dell'e-service $CONTEXT(eServiceName),
    precedentemente sospesa.
    """

  # PASSA
  Scenario: [Notifica modifica carico a versione e-service] L'erogatore modifica le soglie di carico della versione e-service a cui il fruitore è iscritto
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente aggiorna la durata del voucher e le soglie di carico di quel descrittore
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $CONTEXT(producerName) ha apportato delle modifiche alle soglie di carico della
    versione 1 dell'e-service $CONTEXT(eServiceName) a cui sei iscritto.
    """

  # PASSA
  Scenario: [Notifica aggiunta documento a versione e-service] L'erogatore aggiunge un documento nella versione dell'e-service a cui il fruitore è iscritto
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e un documento già caricato
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente ha già aggiunto un documento al descrittore
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $CONTEXT(producerName) ha aggiunto un documento nella versione 1
    dell'e-service $CONTEXT(eServiceName) a cui sei iscritto.
    """

  # PASSA
  Scenario: [Notifica e-service rinominato] L'erogatore rinomina l'e-service a cui il fruitore è iscritto
    Given "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA2"
    When l'utente aggiorna il nome dell'e-service con un valore di lunghezza 60 caratteri
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    Ti informiamo che l'e-service $CONTEXT(oldEServiceName) è stato rinominato in
    $CONTEXT(eServiceName) dall'ente erogatore. La tua richiesta di fruizione rimane attiva e
    non sono richieste azioni da parte tua.
    """

  # PASSA
  Scenario: [Notifica modifica descrizione a versione e-service] L'erogatore modifica la descrizione nella versione dell'e-service a cui il fruitore è iscritto
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente è un "admin" di "PA1"
    When l'utente aggiorna la descrizione di quell'e-service
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $CONTEXT(producerName) ha modificato la descrizione nella versione 1
    dell'e-service $CONTEXT(eServiceName) a cui sei iscritto.
    """

  # PASSA
  Scenario: [Notifica documento aggiornato a versione e-service] L'erogatore ha aggiornato un documento nella versione dell'e-service a cui il fruitore è iscritto
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e un documento già caricato
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    When l'utente aggiorna il nome di quel documento
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $CONTEXT(producerName) ha aggiornato un documento $CONTEXT(documentName) della
    versione 1 dell'e-service $CONTEXT(eServiceName), a cui sei iscritto.
    """

  # PASSA
  Scenario: [Notifica nuovo livello di sicurezza per e-service] L'erogatore aggiunge un nuovo livello di sicurezza all'e-service a cui il fruitore è iscritto
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    When l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    Ti informiamo che l'ente erogatore $CONTEXT(producerName) ha aggiunto un nuovo livello di sicurezza
    (portachiavi) all'e-service $CONTEXT(eServiceName).
    """

  # PASSA
  Scenario: [Notifica richiesta fruizione accettata] L'erogatore accetta al fruitore la richiesta di fruizione di un e-service
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA1" ha già approvato quella richiesta di fruizione
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha accettato la richiesta di fruizione formulata dal tuo ente
    per l'e-service $CONTEXT(eServiceName). Puoi ora procedere alla creazione dei voucher per iniziare a
    interrogare le API.
    """

  # PASSA
  Scenario: [Notifica richiesta fruizione rifiutata] L'erogatore rifiuta al fruitore la richiesta di fruizione di un e-service
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA1" ha già rifiutato quella richiesta di fruizione
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La richiesta di fruizione per l'e-service $CONTEXT(eServiceName) è stata rifiutata dall'ente erogatore.
    """

  # PASSA
  Scenario: [Notifica richiesta fruizione sospesa e riattivata] L'erogatore sospende e riattiva al fruitore la richiesta di fruizione di un e-service

    # Scenario: [Notifica richiesta fruizione sospesa] L'erogatore sospende al fruitore la richiesta di fruizione di un e-service
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA1" ha già approvato quella richiesta di fruizione
    When "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha sospeso la richiesta di fruizione formulata dal tuo ente
    per l'e-service $CONTEXT(eServiceName). Non potrai utilizzare i voucher associati fino alla riattivazione.
    """

    # PASSA
    # Scenario: [Notifica richiesta fruizione riattivata] L'erogatore riattiva al fruitore la richiesta di fruizione di un e-service
    When "PA1" ha già attivato nuovamente quella richiesta di fruizione come PRODUCER
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha riattivato la richiesta di fruizione formulata dal tuo ente
    per l'e-service $CONTEXT(eServiceName), precedentemente sospesa. Puoi nuovamente utilizzare i voucher
    associati.
    """

  # PASSA
  Scenario: [Notifica sospensione e riattivazione fruizione da PDND] La Piattaforma PDND sospende e riattiva la richiesta di fruizione per un e-service

    # Scenario: [Notifica riattivazione fruizione da PDND] La Piattaforma PDND sospende la richiesta di fruizione per un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha creato un attributo certificato e lo ha assegnato a "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quell'attributo certificato con approvazione automatica
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA2" ha già revocato quell'attributo "CERTIFIED" a "PA1"
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La Piattaforma PDND ha sospeso la richiesta di fruizione formulata dal tuo ente per l'e-service
    $CONTEXT(eServiceName), in quanto non risultano più soddisfatti i requisiti necessari.
    """

    # PASSA
    # Scenario: [Notifica riattivazione fruizione da PDND] La Piattaforma PDND riattiva la richiesta di fruizione per un e-service
    When "PA2" ha già assegnato nuovamente quell'attributo "CERTIFIED" a "PA1"
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La Piattaforma PDND ha riattivato la richiesta di fruizione formulata dal tuo ente per l'e-service
    "$CONTEXT(eServiceName)", precedentemente sospesa.
    """

  # PASSA
  Scenario: [Notifica stima di carico superata] La stima di carico complessiva per le finalità associate all'e-service vengono superate dal fruitore
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When "PA1" ha già richiesto l'aggiornamento della stima di carico superando i limiti di quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    La stima di carico complessiva per le finalità associate all'e-service "$CONTEXT(eServiceName)" ha superato
    la soglia massima consentita dall'erogatore pari a 50 chiamate API giornaliere.
    """

  # PASSA
  Scenario: [Notifica richiesta di adeguamento piano rifiutata] L'erogatore rifiuta la richiesta di adeguamento del piano di carico al fruitore
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And "PA1" ha già richiesto l'aggiornamento della stima di carico superando i limiti di quell'e-service
    When "PA2" ha già rifiutato la richiesta di aggiornamento della stima di carico
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha rifiutato la richiesta di adeguamento del piano di carico
    formulata dal tuo ente per la finalità "$CONTEXT(purposeTitle)", associata all'e-service
    "$CONTEXT(eServiceName)".
    """

  # PASSA
  Scenario: [Notifica finalità approvata] L'erogatore approva la finalità richiesta dal fruitore per un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di approvare purpose
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha approvato la finalità "$CONTEXT(purposeTitle)" che hai
    richiesto per l'e-service $CONTEXT(eServiceName).
    """

  # FALLISCE per una questione di virgolette assenti rispetto all'Excel
  Scenario: [Notifica finalità rifiutata] L'erogatore rifiuta la finalità richiesta dal fruitore per un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "WAITING_FOR_APPROVAL" per quell'eservice
    When l'utente rifiuta la finalità aggiungendo una motivazione
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha rifiutato la finalità "$CONTEXT(purposeTitle)" che il tuo
    ente ha inoltrato per l'e-service "$CONTEXT(eServiceName)".
    """

  # PASSA
  Scenario: [Notifica finalità sospesa] L'erogatore sospende la finalità richiesta dal fruitore per un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente sospende quella finalità in stato "ACTIVE"
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha sospeso la finalità "$CONTEXT(purposeTitle)", associata
    all'e-service $CONTEXT(eServiceName).
    """

  # FALLISCE per una questione di virgolette assenti rispetto all'Excel
  Scenario: [Notifica finalità riattivata] L'erogatore riattiva la finalità richiesta dal fruitore per un e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And l'utente sospende quella finalità in stato "ACTIVE"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente tenta di riattivare purpose
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha riattivato la finalità "$CONTEXT(purposeTitle)", associata
    all'e-service "$CONTEXT(eServiceName)".
    """

  # PASSA
  Scenario: [Notifica nuova versione template] L'erogatore pubblica una nuova versione di e-service template
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED a partire dal template con successo indicando solo le specifiche strettamente necessarie
    And l'utente è un "admin" di "PA1"
    When l'utente aggiunge all'e-service template una versione in stato PUBLISHED con successo
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'ente $CONTEXT(templateProducerName) ha pubblicato una nuova versione 2
    del template "$CONTEXT(eServiceTemplateName)".
    """

  # FALLISCE: la notifica riporta il nome del template vecchio e nuovo, e non dell'e-service nome vecchio e nuovo
  Scenario: [Notifica aggiornamento nome template] L'e-service viene rinominato in quanto il suo template è stato rinominato
    Given l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "istanza" a partire dal template con successo indicando tutte le specifiche
    And l'utente è un "admin" di "PA2"
    When l'utente tenta la modifica del nome dell'e-service template
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    Ti informiamo che il tuo e-service $CONTEXT(eServiceTemplateName) - istanza è stato rinominato in
    $CONTEXT(newEServiceTemplateName) - istanza in quanto è stato modificato il template e-service
    da cui lo hai generato.
    """

  # PASSA
  Scenario: [Notifica sospensione template] L'ente sospende il template e-service da cui un secondo ente ha generato un e-service
    Given l'utente è un "admin" di "PA2"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED
    And l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un nuovo e-service in stato PUBLISHED con suffisso "istanza" a partire dal template con successo indicando tutte le specifiche
    And l'utente è un "admin" di "PA2"
    When l'utente effettua la sospensione dell'e-service template
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'ente $CONTEXT(templateProducerName) ha sospeso il template "$CONTEXT(eServiceTemplateName)",
    da cui il tuo ente ha generato l'e-service.
    """

  # PASSA
  Scenario: [Notifica richiesta delega accettata] Il delegato accetta la delega alla fruizione di un e-service delegabile
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    When l'ente delegato accetta la delega in fruizione
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $CONTEXT(delegateName) ha accettato la delega alla fruizione che il
    tuo ente gli ha conferito per l'e-service "$CONTEXT(eServiceName)". La delega è ora attiva.
    """

  # PASSA
  Scenario: [Notifica richiesta delega rifiutata] Il delegato rifiuta la delega alla fruizione di un e-service delegabile
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    And l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    And l'utente è un "admin" dell'ente delegato
    When l'ente delegato rifiuta la delega in fruizione
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $CONTEXT(delegateName) ha rifiutato la delega alla fruizione che il
    tuo ente gli ha conferito per l'e-service "$CONTEXT(eServiceName)".
    """

  # FALLISCE per una questione di virgolette assenti rispetto all'Excel
  Scenario: [Notifica richiesta approvazione nuova versione e-service] L'ente delegato richiede l'approvazione per pubblicare una nuova versione di e-service
    Given l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente crea una nuova versione dell'e-service
    And "PA2" ha già caricato un'interfaccia per quel descrittore
    And l'utente aggiorna alcuni parametri di quel descrittore con:
      | agreementApprovalPolicy | MANUAL    |
      | audience                | pagopa.it |
      | dailyCallsPerConsumer   | 100       |
      | dailyCallsTotal         | 1000      |
      | description             | Descrizione versione 2 |
      | voucherLifespan         | 80        |
    When l'utente pubblica quel descrittore
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegato $CONTEXT(delegateName) richiede la tua approvazione per pubblicare
    una nuova versione dell'e-service "$CONTEXT(eServiceName)".
    """

  # FALLISCE per una questione di virgolette assenti rispetto all'Excel
  Scenario: [Notifica approvazione nuova versione e-service] L'ente delegante approva la pubblicazione della nuova versione dell'e-service
    Given l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente crea una nuova versione dell'e-service
    And "PA2" ha già caricato un'interfaccia per quel descrittore
    And l'utente aggiorna alcuni parametri di quel descrittore con:
      | agreementApprovalPolicy | MANUAL    |
      | audience                | pagopa.it |
      | dailyCallsPerConsumer   | 100       |
      | dailyCallsTotal         | 1000      |
      | description             | Descrizione versione 2 |
      | voucherLifespan         | 80        |
    And l'utente pubblica quel descrittore
    And l'utente è un "admin" di "PA1"
    When l'utente approva la pubblicazione dell'e-service
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegante $CONTEXT(producerName) ha approvato la pubblicazione della nuova versione
    dell'e-service "$CONTEXT(eServiceName)" che gestisci tramite delega.
    """

  # FALLISCE per una questione di virgolette assenti rispetto all'Excel
  Scenario: [Notifica rifiuto nuova versione e-service] L'ente delegante rifiuta la pubblicazione della nuova versione dell'e-service
    Given l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
    And "PA1" ha già creato e pubblicato 1 e-service
    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
    And l'ente "PA2" accetta la delega in erogazione con successo
    And l'utente è un "admin" di "PA2"
    And l'utente crea una nuova versione dell'e-service
    And "PA2" ha già caricato un'interfaccia per quel descrittore
    And l'utente aggiorna alcuni parametri di quel descrittore con:
      | agreementApprovalPolicy | MANUAL    |
      | audience                | pagopa.it |
      | dailyCallsPerConsumer   | 100       |
      | dailyCallsTotal         | 1000      |
      | description             | Descrizione versione 2 |
      | voucherLifespan         | 80        |
    And l'utente pubblica quel descrittore
    And l'utente è un "admin" di "PA1"
    When l'utente rifiuta la pubblicazione dell'e-service
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegante $CONTEXT(producerName) ha rifiutato la pubblicazione della nuova versione
    dell'e-service "$CONTEXT(eServiceName)" che gestisci tramite delega.
    """

  # PASSA
  Scenario: [Notifica ricezione richiesta di delega] L'ente riceve una richiesta di delega all'erogazione per un e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    When l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Hai ricevuto una richiesta di delega all'erogazione dall'ente $CONTEXT(producerName) per
    l'e-service $CONTEXT(eServiceName).
    """

  # PASSA
  Scenario: [Notifica delega in erogazione revocata] Viene revocata la delega all'erogazione ad un ente per un e-service
    Given l'ente delegante "PA1"
    And l'ente delegato "PA2"
    And l'ente "PA2" concede la disponibilità a ricevere deleghe
    And "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'ente delegante ha inoltrato una richiesta di delega all'ente delegato con successo
    When l'ente "PA1" con ruolo "admin" revoca la delega in erogazione con successo
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $CONTEXT(producerName) ha revocato la delega all'erogazione per
    l'e-service $CONTEXT(eServiceName) che ti aveva conferito.
    """

  # PASSA
  Scenario: [Notifica attributo certificato ricevuto] L'ente certificatore conferisce l'attributo certificato a un ente
    Given l'utente è un "admin" di "PA2"
    And PA2 ha già creato 1 attributo CERTIFIED
    When l'utente assegna a "PA1" l'attributo certificato precedentemente creato
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    L'ente certificatore $CONTEXT(certifierName) ha conferito al tuo ente l'attributo certificato
    "$CONTEXT(attributeName)". Puoi ora utilizzarlo nelle richieste di fruizione.
    """

  # Due forme di notifica tra riga 101 e riga 102 del foglio Mappatura notifiche: ma perché differiscono?
  # Questa notifica risulta NON IMPLEMENTATA
#  Scenario: [Notifica aderente attributo certificato ricevuto] L'ente certificatore conferisce l'attributo certificato a un ente aderente
#    Given l'utente è un "admin" di "PA1"
#    And PA1 ha già creato 1 attributo CERTIFIED
#    When l'utente assegna a "PA1" l'attributo certificato precedentemente creato
#    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
#    """
#    Al tuo ente è stato conferito l'attributo certificato "$CONTEXT(attributeName)".
#    Puoi ora utilizzarlo nelle richieste di fruizione.
#    """

  # PASSA
  Scenario: [Notifica attributo certificato revocato] L'ente certificatore revoca l'attributo certificato a un ente
    Given "PA2" ha creato un attributo certificato e lo ha assegnato a "PA1"
    And l'utente è un "admin" di "PA2"
    And PA2 ha già creato 1 attributo CERTIFIED
    And l'utente assegna a "PA1" l'attributo certificato precedentemente creato
    When "PA2" ha già revocato quell'attributo "CERTIFIED" a "PA1"
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    L'ente certificatore $CONTEXT(certifierName) ha revocato l'attributo certificato "$CONTEXT(attributeName)".
    Tutte le richieste di fruizione con questo attributo saranno sospese e in futuro non potrai più utilizzare
    questo attributo per le richieste di fruizione.
    """

  # Due forme di notifica tra riga 103 e riga 104 del foglio Mappatura notifiche: ma perché differiscono?
  # Questa notifica risulta NON IMPLEMENTATA
#  Scenario: [Notifica aderente attributo certificato revocato] L'ente certificatore revoca l'attributo certificato a un ente aderente
#    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
#    """
#    Al tuo ente è stato revocato l'attributo certificato "$CONTEXT(attributeName)".
#    Tutte le richieste di fruizione con questo attributo saranno sospese e in futuro
#    non potrai più utilizzare questo attributo per le richieste di fruizione.
#    """

  # PASSA
  Scenario: [Notifica attributo verificato ricevuto] L'ente certificatore conferisce l'attributo verificato a un ente
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA2" ha già verificato l'attributo verificato a "PA1"
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    L'ente certificatore $CONTEXT(producerName) ha conferito al tuo ente l'attributo verificato
    "$CONTEXT(attributeName)". Puoi ora utilizzarlo nelle richieste di fruizione.
    """

  # FALLISCE: la notifica è diversa nella forma
  Scenario: [Notifica attributo verificato revocato] L'ente certificatore revoca l'attributo verificato a un ente
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    When "PA2" ha già revocato quell'attributo "VERIFIED" a "PA1"
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    Ti informiamo che l'ente certificatore $CONTEXT(producerName) ha revocato l'attributo verificato
    "$CONTEXT(attributeName)". Tutte le richieste di fruizione che utilizzano tale attributo subiranno una sospensione.
    Non potrai più utilizzare questo attributo per le future richieste di fruizione.
    """

  # FALLISCE: la notifica è diversa nella forma
  Scenario: [Notifica chiave rimossa da client e-service] Viene rimossa una chiave da un client e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" rimuove quella chiave dal client
    Then security di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    L'utente $CONTEXT(producerName) ha rimosso una chiave di e-service dal client "CONTEXT(clientName)".
    Assicurati che l'operatività non sia compromessa.
    """

  # PASSA
  Scenario: [Notifica chiave client non più sicura] L'operatore che ha caricato una chiave al client non è più attivo
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    And un "security" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" ha già rimosso l'utente con ruolo "security" dai membri di quel client
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    Una chiave associata al client $CONTEXT(clientName) non è più considerata sicura, in quanto l'operatore che l'ha
    caricata non è più attivo. La chiave deve essere sostituita per garantire la sicurezza e l'operatività.
    """

  # FALLISCE: si aggiunge una chiave al client e-service, ma la notifica non menziona 'e-service'
  # Inoltre le doppie virgolette attorno al nome del client sono attese ma non presenti
  Scenario: [Notifica chiave aggiunta a client e-service] Viene aggiunta una nuova chiave ad un client e-service
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    When un "admin" di "PA1" ha caricato una chiave pubblica nel client
    Then security di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    Ti informiamo che è stata aggiunta una nuova chiave e-service al client "$CONTEXT(clientName)".
    """

  # FALLISCE: si aggiunge una chiave al client interop, ma la notifica non menziona 'interop'
  # Inoltre le doppie virgolette attorno al nome del client sono attese ma non presenti
  Scenario: [Notifica chiave aggiunta al client interop] Viene aggiunta una nuova chiave al client interop
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "API"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    When un "admin" di "PA1" ha caricato una chiave pubblica nel client
    Then security di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    Ti informiamo che è stata aggiunta una nuova chiave al client interop "$CONTEXT(clientName)".
    """

  # PASSA
  Scenario: [Notifica chiave rimossa dal portachiavi] L'utente rimuove una chiave dal portachiavi erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    When l'utente "admin" di "PA1" rimuove tutte le chiavi dal portachiavi erogatore
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link PORTACHIAVI_EROGAZIONE
    """
    La chiave $CONTEXT(deletedKeyId) è stata rimossa dal portachiavi erogatore $CONTEXT(keychainName).
    Assicurati che l'operatività non sia compromessa.
    """

  # PASSA
  Scenario: [Notifica chiave portachiavi non più sicura] L'operatore che ha caricato una chiave al portachiavi non è più attivo
    Given l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    And l'utente "security" di "PA1" aggiunge una chiave al portachiavi erogatore
    When l'utente "admin" di "PA1" rimuove l'utente "security" dal portachiavi erogatore
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link PORTACHIAVI_EROGAZIONE
    """
    Una chiave associata al portachiavi erogatore $CONTEXT(keychainName) non è più considerata sicura,
    in quanto l'operatore che l'ha caricata non è più attivo. La chiave deve essere sostituita per garantire la
    sicurezza e l'operatività.
    """

  # PASSA
  Scenario: [Notifica chiave aggiunta al portachiavi] Viene aggiunta una nuova chiave al portachiavi erogatore
    Given l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    When l'utente "security" di "PA1" aggiunge una chiave al portachiavi erogatore
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link PORTACHIAVI_EROGAZIONE
    """
    Ti informiamo che è stata aggiunta una nuova chiave al portachiavi erogatore $CONTEXT(keychainName).
    """

  # PASSA
  Scenario: [Notifica chiave client rimossa] L'utente rimuove una chiave pubblica dal client
    Given l'utente è un "admin" di "PA1"
    And "GSP" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And "PA1" ha già associato la finalità a quel client
    And un "admin" di "PA1" ha aggiunto una nuova chiave pubblica al client
    When "PA1" rimuove quella nuova chiave dal client
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    La chiave $CONTEXT(newKeyId) è stata rimossa dal client $CONTEXT(clientName).
    Assicurati che l'operatività non sia compromessa.
    """
