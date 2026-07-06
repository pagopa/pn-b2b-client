#@bff-notification
#@disable-notifications-hooks # FIXME usato per velocizzare l'esecuzione dei test in locale, rimuovere

Feature: API Notifiche - verifica bodies (generato da excel)

  # PASSA
  Scenario: [Notifica nuova richiesta di fruizione] Ricezione nuova richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    When "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    Hai ricevuto una nuova richiesta di fruizione per l'e-service $CONTEXT(eServiceName) formulata
    da parte di $CONTEXT(consumerName).
    """

  # PASSA
  Scenario: [Notifica richiesta di fruizione accettata] Accettazione automatica di una richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    È stata accettata una richiesta di fruizione per l'e-service $CONTEXT(eServiceName) formulata
    da parte di $CONTEXT(consumerName).
    """

  # PASSA
  Scenario: [Notifica richiesta di fruizione aggiornata] Il fruitore aggiorna la richiesta di fruizione per la nuova versione dell'e-service pubblicata dall'erogatore
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
  Scenario: [Notifica richiesta di fruizione sospesa e riattivata] Il fruitore sospende e riattiva la richiesta di fruizione per l'e-service dell'erogatore

    # Scenario: [Notifica richiesta di fruizione sospesa] Il fruitore sospende la richiesta di fruizione per l'e-service dell'erogatore
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When "PA2" ha una richiesta di fruizione in stato "SUSPENDED" per quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha sospeso la propria richiesta di fruizione per il suo e-service
    $CONTEXT(eServiceName).
    """

    # Scenario: [Notifica richiesta di fruizione riattivata] Il fruitore riattiva la richiesta di fruizione per l'e-service dell'erogatore
    When "PA2" ha già attivato nuovamente quella richiesta di fruizione come CONSUMER
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha riattivato la propria richiesta di fruizione per il tuo e-service
    $CONTEXT(eServiceName), precedentemente sospesa.
    """

  # PASSA
  Scenario: [Notifica richiesta di fruizione sospesa dalla Piattaforma] La Piattaforma PDND sospende la richiesta di fruizione del fruitore causa perdita dei requisiti
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    And "PA2" ha già approvato quella richiesta di fruizione
    When l'utente è un "admin" di "PA2"
    And l'utente revoca l'attributo precedentemente verificato
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    La Piattaforma PDND ha sospeso la richiesta di fruizione del fruitore $CONTEXT(consumerName) per il tuo
    e-service $CONTEXT(eServiceName), in quanto l'ente fruitore non dispone più dei requisiti per poter fruire
    di questi dati.
    """

  # PASSA
  Scenario: [Notifica richiesta di fruizione riattivata dalla Piattaforma] La Piattaforma PDND riattiva la richiesta di fruizione del fruitore per riottenimento dei requisiti
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    And "PA2" ha già approvato quella richiesta di fruizione
    When l'utente è un "admin" di "PA2"
    And l'utente revoca l'attributo precedentemente verificato
    And "PA2" ha già verificato l'attributo verificato a "PA1"
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_EROGAZIONE
    """
    La Piattaforma PDND ha riattivato la richiesta di fruizione del fruitore $CONTEXT(consumerName) per
    il tuo e-service $CONTEXT(eServiceName), precedentemente sospesa.
    """

  # PASSA
  Scenario: [Notifica richiesta di fruizione archiviata] Il fruitore archivia la richiesta di fruizione per l'e-service dell'erogatore
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
    When "PA1" ha già associato quella nuova finalità a quel client
    When l'utente "admin" di "PA1" richiede la disassociazione della finalità dal client con successo
    Then admin di "GSP" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha disassociato un proprio client dalla finalità "$CONTEXT(purposeTitle)"
    per il tuo e-service $CONTEXT(eServiceName).
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

  # FALLISCE
  # Ottengo un messaggio un po' diverso: L'ente Comune di Milano ha richiesto un adeguamento del piano di carico per
  # la finalità "purpose title - QA - 1543411954 - 1015894717", associata al tuo e-service eservice-0-1543411954-
  # 1244196506.
  # Probabilmente bisogna correggere gli step.
  Scenario: [Notifica piano di carico sopra soglia] Il fruitore chiede un adeguamento del piano di carico della finalità superiore alla soglia dell'e-service
    Given "GSP" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA1" ha già creato una nuova finalità attiva per quell'eservice
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica nel client
    And l'utente è un "admin" di "PA1"
    When l'utente aggiorna la stima di carico per quella finalità superando la soglia
    And "PA1" ha già associato quella nuova finalità a quel client
    Then admin di "GSP" ha ricevuto la notifica in-app contenente il link FINALITA_EROGAZIONE
    """
    L'ente $CONTEXT(consumerName) ha inviato la finalità "$CONTEXT(purposeTitle)", che prevede un piano
    di carico superiore alla tua soglia, associata al tuo e-service $CONTEXT(eServiceName).
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

  # FALLISCE sbaglia solo il deep link arriva E_SERVICE_EROGAZIONE ma dovrebbe essere TEMPLATE_E_SERVICE_EROGAZIONE
  # FALLISCE non arriva più la notifica per niente... è cambiato qualcosa o mi sono sbagliato?
  Scenario: [Notifica e-service template sospeso] Il creatore sospende il proprio e-service template e l'erogatore viene notificato
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di PUBLISHED con nome "E-Service - OK"
    Given l'utente è un "admin" di "PA2"
    And l'utente tenta la creazione di un nuovo e-service con suffisso "OK_2" a partire dal template indicando tutte le specifiche
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la sospensione dell'e-service template
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link TEMPLATE_E_SERVICE_EROGAZIONE
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

    # Scenario: [Notifica versione e-service sospesa] L'erogatore sospende la versione dell'e-service a cui il fruitore è iscritto
    When "PA2" ha già sospeso la vecchia versione di quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    L'ente erogatore $CONTEXT(producerName) ha sospeso la versione 1 dell'e-service $CONTEXT(eServiceName),
    a cui sei iscritto.
    """

    # Scenario: [Notifica versione e-service riattivata] L'erogatore riattiva la versione dell'e-service precedentemente sospesa
    When "PA2" ha già attivato nuovamente la vecchia versione quell'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE_PRIMO_DESCRITTORE
    """
    L'ente erogatore $CONTEXT(producerName) ha riattivato la versione 1 dell'e-service $CONTEXT(eServiceName),
    precedentemente sospesa.
    """

  # FALLISCE non arriva affatto la notifica: step corretti?
  Scenario: [Modifica carico a versione e-service] L'erogatore modifica le soglie di carico della versione e-service precedentemente
    Given l'utente è un "admin" di "PA1"
    Given "PA2" ha già creato e pubblicato 1 e-service
    #Given "PA2" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    Given "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA1" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    When l'utente aggiorna la stima di carico per quella finalità restando entro la soglia
    #When l'utente aggiorna la durata del voucher e le soglie di carico di quel descrittore
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $CONTEXT(producerName) ha apportato delle modifiche alle soglie di carico della
    versione 1 dell'e-service $CONTEXT(eServiceName) a cui sei iscritto.
    """

  # TODO come aggiungere un documento ad un e-service?
  Scenario: [Aggiunta documento a versione e-service] L'erogatore aggiunge un documento nella versione dell'e-service a cui il fruitore è iscritto
    Given l'utente è un "admin" di "PA1"
    #Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED" e tecnologia "REST"
    Given "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    Given l'utente è un "admin" di "PA1"
    When l'utente carica un documento di interfaccia di tipo "yaml"
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $CONTEXT(producerName) ha aggiunto un documento nella versione 1
    dell'e-service $CONTEXT(eServiceName) a cui sei iscritto.
    """

  # PASSA
  Scenario: [Modifica descrizione a versione e-service] L'erogatore modifica la descrizione nella versione dell'e-service a cui il fruitore è iscritto
    Given "PA1" ha già creato un e-service con un descrittore in stato "PUBLISHED"
    Given "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given "PA2" ha già creato 1 finalità in stato "ACTIVE" per quell'eservice
    Given l'utente è un "admin" di "PA1"
    When l'utente aggiorna la descrizione di quell'e-service
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    L'ente erogatore $CONTEXT(producerName) ha modificato la descrizione nella versione 1
    dell'e-service $CONTEXT(eServiceName) a cui sei iscritto.
    """

  # PASSA
  Scenario: [Documento aggiornato a versione e-service] L'erogatore ha aggiornato un documento nella versione dell'e-service a cui il fruitore è iscritto
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
  Scenario: [Nuovo livello di sicurezza per e-service] L'erogatore aggiunge un nuovo livello di sicurezza all'e-service a cui il fruitore è iscritto
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
  Scenario: [Rinominazione e-service] L'erogatore rinomina l'e-service a cui il fruitore è iscritto
    And "PA2" ha già creato e pubblicato 1 e-service
    And "PA1" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    Given l'utente è un "admin" di "PA2"
    When l'utente aggiorna il nome dell'e-service con un valore di lunghezza 60 caratteri
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link CATALOGO_E_SERVICE
    """
    Ti informiamo che l'e-service $CONTEXT(oldEServiceName) è stato rinominato in
    $CONTEXT(eServiceName) dall'ente erogatore. La tua richiesta di fruizione rimane attiva e
    non sono richieste azioni da parte tua.
    """

  # PASSA
  Scenario: [Richiesta fruizione accettata] L'erogatore accetta al fruitore la richiesta di fruizione di un e-service
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
  Scenario: [Richiesta fruizione rifiutata] L'erogatore rifiuta al fruitore la richiesta di fruizione di un e-service
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA1" ha già rifiutato quella richiesta di fruizione
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La richiesta di fruizione per l'e-service $CONTEXT(eServiceName) è stata rifiutata dall'ente erogatore.
    """

  # PASSA
  Scenario: [Richiesta fruizione sospesa] L'erogatore sospende al fruitore la richiesta di fruizione di un e-service
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    And "PA1" ha già approvato quella richiesta di fruizione
    When "PA1" ha già sospeso quella richiesta di fruizione come PRODUCER
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha sospeso la richiesta di fruizione formulata dal tuo ente
    per l'e-service $CONTEXT(eServiceName). Non potrai utilizzare i voucher associati fino alla riattivazione.
    """

## TODO Da qui in avanti scrivere meglio ID e titoli degli scenari ##

  Scenario: [NOTIFICATION_AGREEMENTS_5] Sospensione richiesta di fruizione da parte della Piattaforma
    Given l'utente è un "admin" di "PA1"
    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" ne possiede uno per gruppo
    And "PA1" ha già creato un e-service in stato "DRAFT" che richiede quegli attributi con approvazione "AUTOMATIC" con dailyCallsPerConsumer uguale a 10 e dailyCallsTotal uguale a 11
    When l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo e il gruppo 0-esimo creato
    #And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "10"
    And l'utente tenta di aggiungere una soglia differenziata di 10 per l'attributo CERTIFIED 0-esimo e il gruppo 1-esimo creato
    #And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 1-esimo è uguale a "10"
    And "PA1" ha già caricato un'interfaccia per quel descrittore
    And l'utente pubblica l'e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And l'utente è un "admin" di "PA1"
    And la soglia differenziata per l'attributo CERTIFIED 0-esimo creato nel gruppo 0-esimo è uguale a "1"

#    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
#    And l'utente è un "admin" di "PA1"
#    And l'utente crea 4 attributi certificati con successo
#    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
#    And "PA1" porta il descrittore dell'e-service in stato "PUBLISHED"
#    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And l'utente tenta di aggiungere gli attributi certificati numeri da 2 a 3 al gruppo dell'e-service
#    And due gruppi di due attributi certificati da "PA1", dei quali "PA2" li possiede tutti
#    And "PA2" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
#    And "PA1" ha già approvato quella richiesta di fruizione
#    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
#    And l'utente tenta di rimuovere l'attributo certificato numero 2 dal gruppo dell'e-service

    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La Piattaforma PDND ha sospeso la richiesta di fruizione formulata dal tuo ente per l'e-service
    $CONTEXT(eServiceName), in quanto non risultano più soddisfatti i requisiti necessari.
    """

  Scenario: [La tua richiesta per e-service è stata riattivata] L'ente erogatore ha riattivato la richiesta di fruizione formulata dal tuo ente…
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha riattivato la richiesta di fruizione formulata dal tuo ente
    per l'e-service $CONTEXT(eServiceName), precedentemente sospesa. Puoi nuovamente utilizzare i voucher
    associati.
    """

  Scenario: [Riattivazione richiesta da parte della Piattaforma] La Piattaforma PDND ha riattivato la richiesta di fruizione formulata dal tuo e…
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link RICHIESTA_FRUIZIONE
    """
    La Piattaforma PDND ha riattivato la richiesta di fruizione formulata dal tuo ente per l'e-service
    $CONTEXT(eServiceName), precedentemente sospesa.
    """

  Scenario: [Avviso: soglia di carico per finalità superata] La stima di carico complessiva per le finalità associate all'e-service ha su…
    # TODO per l'ammontare delle chiamata API massimo, si può decidere a monte un numero basso e noto e poi usarlo
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    La stima di carico complessiva per le finalità associate all'e-service $CONTEXT(eServiceName) ha superato
    la soglia massima consentita dall'erogatore pari a $CONTEXT(ammontare_chiamate_api) chiamate API giornaliere.
    """

  # Fallito, in quanto questa specifica notifica non è stata implementata (segnata come non-implementata nello sheet di riferimento). Test da togliere eventualmente.
  #Scenario: [Richiesta di adeguamento piano accettata] - L'ente erogatore ha accettato la richiesta di adeguamento del piano di carico f…
  #  Then admin di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
  #  """
  #  L'ente erogatore $CONTEXT(producerName) ha accettato la richiesta di adeguamento del piano di carico
  #  formulata dal tuo ente per la finalità $CONTEXT(purposeId), associata all'e-service
  #  $CONTEXT(eServiceName).
  #  """

  Scenario: [Richiesta di adeguamento piano rifiutata] L'ente erogatore ha rifiutato la richiesta di adeguamento del piano di carico f…
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha rifiutato la richiesta di adeguamento del piano di carico
    formulata dal tuo ente per la finalità $CONTEXT(purposeId), associata all'e-service
    $CONTEXT(eServiceName).
    """

  Scenario: [La tua finalità è stata approvata] L'ente erogatore ha approvato la finalità che hai richiesto per l'e-service…
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha approvato la finalità $CONTEXT(purposeId) che hai
    richiesto per l'e-service $CONTEXT(eServiceName).
    """

  Scenario: [La tua finalità è stata rifiutata] L'ente erogatore ha rifiutato la finalità che il tuo ente ha inoltrato per l…
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha rifiutato la finalità $CONTEXT(purposeId) che il tuo
    ente ha inoltrato per l'e-service $CONTEXT(eServiceName).
    """

  Scenario: [Sospensione della finalità] L'ente erogatore ha sospeso la finalità, associata all'e-service.
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha sospeso la finalità $CONTEXT(purposeId), associata
    all'e-service $CONTEXT(eServiceName).
    """

  Scenario: [Riattivazione della finalità] L'ente erogatore ha riattivato la finalità, associata all'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link FINALITA_FRUIZIONE
    """
    L'ente erogatore $CONTEXT(producerName) ha riattivato la finalità $CONTEXT(purposeId), associata
    all'e-service $CONTEXT(eServiceName).
    """

  Scenario: [Nuova versione del template] L'ente ha pubblicato una nuova versione del template
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    L'ente $CONTEXT(producerName) ha pubblicato una nuova versione $CONTEXT(versione_template) del
    template $CONTEXT(nome_template_e_service).
    """

  Scenario: [Aggiornamento nome del template] Ti informiamo che il tuo e-service è stato rinominato in in quanto è stato modi…
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link E_SERVICE_EROGAZIONE
    """
    Ti informiamo che il tuo e-service $CONTEXT(precedente_eServiceName) è stato rinominato in
    $CONTEXT(eServiceName) in quanto è stato modificato il template e-service da cui lo hai generato.
    """

  Scenario: [Sospensione del template] L'ente ha sospeso un template e-service da cui il tuo ente ha generato l'e-service
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link TEMPLATE_E_SERVICE_EROGAZIONE
    """
    L'ente $CONTEXT(producerName) ha sospeso il template "$CONTEXT(nome_template)", da cui il tuo ente
    ha generato l'e-service.
    """

  Scenario: [La tua richiesta di delega è stata accettata] Ti informiamo che l'ente ha accettato la delega che il tuo ente gli ha conferit…
#    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
#    And l'ente "PA2" concede la disponibilità a ricevere deleghe in erogazione
#    And l'ente "PA1" richiede la creazione di una delega in erogazione per l'ente "PA2" con successo
#    And l'ente "PA2" accetta la delega in erogazione con successo
    # FALLISCE
    Given "PA3" ha già creato e pubblicato 1 e-service delegabile in fruizione
    Given l'ente delegato "PA2"
    And l'utente è un "admin" dell'ente delegato
    And l'ente delegato concede la disponibilità a ricevere deleghe in fruizione
    And l'ente delegante "PA1"
    And l'utente è un "admin" dell'ente delegante
    And l'ente delegante ha inoltrato una richiesta di delega in fruizione all'ente delegato
    When l'utente è un "admin" dell'ente delegato
    And l'ente delegato accetta la delega in fruizione
    # THEN precedentemente commentato:
    #Then admin di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    #"""
    #Ti informiamo che l'ente $CONTEXT(producerName) ha accettato la delega $CONTEXT(id_delega) che il
    #tuo ente gli ha conferito per l'e-service $CONTEXT(eServiceName). La delega è ora attiva.
    #"""
    # FALLISCE
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $CONTEXT(producerName) ha approvato la delega $CONTEXT(id_delega) che il
    tuo ente gli ha conferito per l'e-service $CONTEXT(eServiceName). La delega è ora attiva.
    """

  Scenario: [La tua richiesta di delega è stata rifiutata] Ti informiamo che l'ente ha rifiutato la delega che il tuo ente gli ha conferito…
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $CONTEXT(nome_ente_delegato) ha rifiutato la delega $CONTEXT(id_delega) che il
    tuo ente gli ha conferito per l'e-service $CONTEXT(eServiceName).
    """

  Scenario: [Richiesta di approvazione per una nuova versione] L'ente delegato richiede la tua approvazione per pubblicare una nuova versione…
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegato $CONTEXT(nome_ente_delegato) richiede la tua approvazione per pubblicare una nuova versione
    dell'e-service $CONTEXT(eServiceName).
    """

  Scenario: [Approvata la pubblicazione della nuova versione] L'ente delegante ha approvato la pubblicazione della nuova versione dell'e-serv…
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegante $CONTEXT(nome_ente_delegante) ha approvato la pubblicazione della nuova versione dell'e-service
    $CONTEXT(eServiceName) che gestisci tramite delega.
    """

  Scenario: [Rifiutata la pubblicazione della nuova versione] L'ente delegante ha rifiutato la pubblicazione della nuova versione dell'e-serv…
    Then admin di "PA2" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    L'ente delegante $CONTEXT(nome_ente_delegante) ha rifiutato la pubblicazione della nuova versione dell'e-service
    $CONTEXT(eServiceName) che gestisci tramite delega.
    """

  Scenario: [Hai ricevuto una richiesta di delega] Hai ricevuto una richiesta di delega per dall'ente per l'e-service
    # originale
    # Then admin di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    # """
    # Hai ricevuto una richiesta di delega per $CONTEXT(id_delega) dall'ente $CONTEXT(nome_ente_delegante) per
    # l'e-service $CONTEXT(eServiceName).
    # """
    # adattato al messaggio ottenuto
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Hai ricevuto una richiesta di delega $CONTEXT(id_delega) dall'ente $CONTEXT(nome_ente_delegante) per
    l'e-service $CONTEXT(eServiceName).
    """

  Scenario: [Una delega che gestivi è stata revocata] Ti informiamo che l'ente ha revocato la delega per l'e-service che ti aveva…
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link DELEGA_ADERENTE
    """
    Ti informiamo che l'ente $CONTEXT(nome_ente_delegante) ha revocato la delega $CONTEXT(id_delega) per
    l'e-service $CONTEXT(eServiceName) che ti aveva conferito.
    """

  # RUNNING
  Scenario: [Hai ricevuto un nuovo attributo certificato] L'ente certificatore ha conferito al tuo ente l'attributo certificato. Puoi…
    Given "PA2" ha già creato un attributo verificato
    And "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    When "PA2" ha già verificato l'attributo verificato a "PA1"
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    L'ente certificatore $CONTEXT(nome_ente) ha conferito al tuo ente l'attributo certificato
    $CONTEXT(id_attributo). Puoi ora utilizzarlo nelle richieste di fruizione.
    """

  Scenario: [Un tuo attributo certificato è stato revocato] Ti informiamo che l'ente certificatore ha revocato l'attributo certificato…
    Given "PA2" ha già creato un attributo verificato
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA1" ha una richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "PA2" ha già verificato l'attributo verificato a "PA1"
    Given "GSP" ha già creato un e-service in stato "PUBLISHED" che richiede quegli attributi con approvazione "MANUAL"
    Given "PA1" ha un'altra richiesta di fruizione in stato "PENDING" per quell'e-service
    Given "GSP" ha già verificato l'attributo verificato a "PA1" sull'altra richiesta di fruizione
    Given l'utente è un "admin" di "PA2"
    When l'utente revoca l'attributo precedentemente verificato
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    Ti informiamo che l'ente certificatore $CONTEXT(nome_ente) ha revocato l'attributo certificato
    $CONTEXT(id_attributo). Tutte le richieste di fruizione che utilizzano tale attributo subiranno una sospensione.
    Non potrai più utilizzare questo attributo per le future richieste di fruizione.
    """

  Scenario: [Hai ricevuto un nuovo attributo verificato] L'ente certificatore ha conferito al tuo ente l'attributo verificato "". Puoi o…
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    L'ente certificatore $CONTEXT(nome_ente) ha conferito al tuo ente l'attributo verificato
    $CONTEXT(id_attributo). Puoi ora utilizzarlo nelle richieste di fruizione.
    """

  Scenario: [Un tuo attributo verificato è stato revocato] Ti informiamo che l'ente certificatore ha revocato l'attributo verificato "". T…
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link ANAGRAFICA_ADERENTE
    """
    Ti informiamo che l'ente certificatore $CONTEXT(nome_ente) ha revocato l'attributo verificato
    $CONTEXT(id_attributo). Tutte le richieste di fruizione che utilizzano tale attributo subiranno una sospensione.
    Non potrai più utilizzare questo attributo per le future richieste di fruizione.
    """

  # trigger: it/pagopa/pn/cucumber/authorization/client-key-delete.feature:7
  Scenario: [Una chiave di e-service è stata rimossa] L'utente ha rimosso una chiave di e-service dal client. Assicurati che l'ope…
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
    # FIXME Perché seppure la notifica è presente non viene vista? Quale delle due versioni di Then è quella giusta per il test?
    #Then admin di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    #"""
    #L'utente $CONTEXT(nome_ruolo) ha rimosso una chiave di e-service dal client $CONTEXT(id_client). Assicurati
    #che l'operatività non sia compromessa.
    #"""
    Then admin di "GSP" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    La chiave $CONTEXT(id_chiave) è stata rimossa dal client $CONTEXT(id_client). Assicurati che l'operatività
    non sia compromessa.
    """

  Scenario: [Attenzione: una chiave non è più sicura] Una chiave associata al client non è più considerata sicura, in quanto l'ope…
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    And un "security" di "PA1" ha caricato una chiave pubblica nel client
    When "PA1" ha già rimosso l'utente con ruolo "security" dai membri di quel client
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    Una chiave associata al client $CONTEXT(id_client) non è più considerata sicura, in quanto l'operatore che l'ha
    caricata non è più attivo. La chiave deve essere sostituita per garantire la sicurezza e l'operatività.
    """

  Scenario: [Nuova chiave aggiunta al client] Ti informiamo che è stata aggiunta una nuova chiave e-service al client
    Given l'utente è un "admin" di "PA1"
    And "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    When un "security" di "PA1" ha caricato una chiave pubblica nel client
    # FALLISCE
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    Ti informiamo che è stata aggiunta una nuova chiave e-service al client $CONTEXT(id_client).
    """

  Scenario: [Una chiave di e-service è stata rimossa] L'utente ha rimosso una chiave dal portachiavi erogatore. Assicurati che l'o…
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    And l'utente "admin" di "PA1" aggiunge una chiave al portachiavi erogatore
    And l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    When l'utente "admin" di "PA1" rimuove tutte le chiavi dal portachiavi erogatore
    # FALLISCE
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link PORTACHIAVI_EROGAZIONE
    """
    La chiave $CONTEXT(id_chiave) è stata rimossa dal portachiavi erogatore $CONTEXT(nome_portachiavi).
    Assicurati che l'operatività non sia compromessa.
    """

  Scenario: [Attenzione: una chiave non è più sicura] Una chiave associata al portachiavi erogatore non è più considerata sicura…
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    And l'utente "security" di "PA1" aggiunge una chiave al portachiavi erogatore
    When l'utente "admin" di "PA1" rimuove l'utente "security" dal portachiavi erogatore
    # FALLISCE: c'è solo il messaggio di chiave aggiunta al portachiavi
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link PORTACHIAVI_EROGAZIONE
    """
    Una chiave associata al portachiavi erogatore $CONTEXT(nome_portachiavi) non è più considerata sicura,
    in quanto l'operatore che l'ha caricata non è più attivo. La chiave deve essere sostituita per garantire la
    sicurezza e l'operatività.
    """

  Scenario: [Nuova chiave aggiunta al portachiavi erogatore] Ti informiamo che è stata aggiunta una nuova chiave al portachiavi erogatore
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    And l'utente "admin" di "PA1" aggiunge l'utente "security" di "PA1" al portachiavi erogatore
    When l'utente "security" di "PA1" aggiunge una chiave al portachiavi erogatore
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link PORTACHIAVI_EROGAZIONE
    """
    Ti informiamo che è stata aggiunta una nuova chiave al portachiavi erogatore $CONTEXT(nome_portachiavi).
    """
