Feature: Gestione notifiche tramite algoritmo del microservizio ritardatore e Lambda di test

  @delayer
  Scenario: [DELAYER-TC01] Le notifiche sono elaborate secondo priorità
    Given il CSV "tc02_limite_pa.csv" contiene 30 notifiche appartenenti alle categorie RS, SECONDO TENTATIVO, ALTRO
    Given il CSV "tc01_priorita.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "{string}" contengono esattamente 30 notifiche
    And le notifiche sono elaborate in ordine di priorità:
      | categoria           | ordinamentoCampo      |
      | RS                  | prepareRequestDate    |
      | SECONDO_TENTATIVO   | prepareRequestDate    |
      | ALTRO               | notificationSentAt    |

  @delayer
  Scenario: [DELAYER-TC02] Rispetto del limite settimanale mittente e ordinamento cronologico
    Given il CSV "tc02_limite_pa.csv" contiene 30 notifiche appartenenti alla stessa categoria
    And il CSV "tc02_limite_pa.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "{string}" contengono esattamente 30 notifiche
    And le prime 20 notifiche sono pianificate secondo ordine cronologico per il campo "prepareRequestDate"
    And le restanti 10 notifiche non sono ancora pianificate

  @delayer
  Scenario: [DELAYER-TC03] Mittente non censito nei limiti settimanali
    Given il CSV "tc03_mittente_non_censito.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "TC03-BATCH-UNCENSORED" contengono solo notifiche elaborate dopo esaurimento capacità delle PA censite

  @delayer
  Scenario: [DELAYER-TC04] Capacità settimanale esaurita – posticipo a W+1
    Given il CSV "tc04_capacita_esaurita.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC04-BATCH-CAPACITY-FULL" sono presenti in "pn-PaperDeliveryHighPriority"
    And nessuna notifica è presente nella tabella "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELAYER-TC05] Stampa giornaliera piena – nessun invio
    Given il CSV "tc05_stampa_piena.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC05-BATCH-STAMPA" rimangono nella tabella "pn-PaperDeliveryHighPriority"
    And nessuna notifica è trasferita in "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELAYER-TC06] RS e secondi tentativi ignorano i limiti mittente
    Given il CSV "tc06_rs_secondi_tentativi.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC06-RS-ATTEMPT1" sono presenti nella tabella "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELAYER-TC07] Scarto di notifiche errate senza blocco
    Given il CSV "tc07_dati_non_validi.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then le notifiche non valide del requestId "TC07-DATI-KO" non sono presenti in "pn-PaperDeliveryReadyToSend"
    And l'elaborazione è andata a buon fine per le notifiche valide

  @delayer
  Scenario: [DELAYER-TC08] Capacità aggregata tra recapiti
    Given il CSV "tc08_multi_recapito.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then la capacità usata per "Sailpost~80100" il giorno "2025-07-10" è pari a 1500
    And tutte le notifiche del requestId "TC08-BATCH" sono presenti in "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELAYER-TC09] Nessuna condizione soddisfatta – tutto posticipato
    Given il CSV "tc09_tutti_vincoli_negati.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC09-FAIL-ALL" sono presenti solo in "pn-PaperDeliveryHighPriority"

  @delayer
  Scenario: [DELAYER-TC10] Provincia non mappata a regione – fallback
    Given il CSV "tc10_provincia_senza_regione.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then le notifiche del requestId "TC10-NOREGION" sono trattate come eccedenti
    And rimangono nella tabella "pn-PaperDeliveryHighPriority"

  @delayer
  Scenario: [DELAYER-TC11] Limite applicato per coppia PA + prodotto
    Given il CSV "tc11_pa_prodotti_distinti.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then il requestId "TC11-PER-PA-PRODOTTO" contiene 6000 notifiche elaborate in "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELAYER-TC12] Recupero notifiche accantonate post capienza residua
    Given il CSV "tc12_notifiche_accantonate.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    And la lambda slave viene invocata per capacità residua
    Then le notifiche accantonate del requestId "TC12-RIPRESA" sono presenti in "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELAYER-TC14] Selezione ordinata in caso di overbooking stampa
    Given il CSV "tc14_overbooking_stampa.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then le notifiche selezionate del requestId "TC14-BATCH" rispettano l’ordine di priorità e data

  @delayer
  Scenario: [DELAYER-TC15] Assenza tabella limiti mittente – fallback attivo
    Given il CSV "tc15_limiti_mittente_assenti.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC15-FALLBACK-LIMIT" sono trattate come senza limite
    And l’algoritmo non va in errore
