Feature: Gestione notifiche tramite algoritmo del microservizio ritardatore e Lambda di test

  @delayer
  Scenario: [DELEYER-TC01] Le notifiche sono elaborate secondo priorità
    Given il CSV "tc01_priorita.csv" è importato nella tabella di test tramite lambda "arn:aws:lambda:eu-south-1:830192246553:function:pn-testDelayerLambda"
    When viene eseguito l'algoritmo tramite lambda
    Then le notifiche del requestId "TC01-BATCH-PRIORITY" sono elaborate in ordine di priorità:
      | categoria           | ordinamentoCampo      |
      | RS                  | prepareRequestDate    |
      | SECONDO_TENTATIVO   | prepareRequestDate    |
      | ALTRO               | notificationSentAt    |


  @delayer
  Scenario: [DELEYER-TC02] Rispetto dei limiti settimanali del mittente
    Given il CSV "tc02_limite_pa.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then i risultati per requestId "TC02-LIMIT-PA" contengono esattamente 3000 notifiche
    And le restanti notifiche non sono presenti nella tabella "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELEYER-TC03] Mittente non censito nei limiti settimanali
    Given il CSV "tc03_mittente_non_censito.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then i risultati per requestId "TC03-BATCH-UNCENSORED" contengono solo notifiche elaborate dopo esaurimento capacità delle PA censite

  @delayer
  Scenario: [DELEYER-TC04] Capacità settimanale esaurita – posticipo a W+1
    Given il CSV "tc04_capacita_esaurita.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC04-BATCH-CAPACITY-FULL" sono presenti in "pn-PaperDeliveryHighPriority"
    And nessuna notifica è presente nella tabella "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELEYER-TC05] Stampa giornaliera piena – nessun invio
    Given il CSV "tc05_stampa_piena.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC05-BATCH-STAMPA" rimangono nella tabella "pn-PaperDeliveryHighPriority"
    And nessuna notifica è trasferita in "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELEYER-TC06] RS e secondi tentativi ignorano i limiti mittente
    Given il CSV "tc06_rs_secondi_tentativi.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC06-RS-ATTEMPT1" sono presenti nella tabella "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELEYER-TC07] Scarto di notifiche errate senza blocco
    Given il CSV "tc07_dati_non_validi.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then le notifiche non valide del requestId "TC07-DATI-KO" non sono presenti in "pn-PaperDeliveryReadyToSend"
    And l'elaborazione è andata a buon fine per le notifiche valide

  @delayer
  Scenario: [DELEYER-TC08] Capacità aggregata tra recapiti
    Given il CSV "tc08_multi_recapito.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then la capacità usata per "Sailpost~80100" il giorno "2025-07-10" è pari a 1500
    And tutte le notifiche del requestId "TC08-BATCH" sono presenti in "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELEYER-TC09] Nessuna condizione soddisfatta – tutto posticipato
    Given il CSV "tc09_tutti_vincoli_negati.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC09-FAIL-ALL" sono presenti solo in "pn-PaperDeliveryHighPriority"

  @delayer
  Scenario: [DELEYER-TC10] Provincia non mappata a regione – fallback
    Given il CSV "tc10_provincia_senza_regione.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then le notifiche del requestId "TC10-NOREGION" sono trattate come eccedenti
    And rimangono nella tabella "pn-PaperDeliveryHighPriority"

  @delayer
  Scenario: [DELEYER-TC11] Limite applicato per coppia PA + prodotto
    Given il CSV "tc11_pa_prodotti_distinti.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then il requestId "TC11-PER-PA-PRODOTTO" contiene 6000 notifiche elaborate in "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELEYER-TC12] Recupero notifiche accantonate post capienza residua
    Given il CSV "tc12_notifiche_accantonate.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    And la lambda slave viene invocata per capacità residua
    Then le notifiche accantonate del requestId "TC12-RIPRESA" sono presenti in "pn-PaperDeliveryReadyToSend"

  @delayer
  Scenario: [DELEYER-TC14] Selezione ordinata in caso di overbooking stampa
    Given il CSV "tc14_overbooking_stampa.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then le notifiche selezionate del requestId "TC14-BATCH" rispettano l’ordine di priorità e data

  @delayer
  Scenario: [DELEYER-TC15] Assenza tabella limiti mittente – fallback attivo
    Given il CSV "tc15_limiti_mittente_assenti.csv" è importato nella tabella di test tramite lambda
    When viene eseguito l'algoritmo tramite lambda
    Then tutte le notifiche del requestId "TC15-FALLBACK-LIMIT" sono trattate come senza limite
    And l’algoritmo non va in errore
