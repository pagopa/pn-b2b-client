Feature: Gestione notifiche tramite algoritmo del microservizio ritardatore e Lambda di test

  @delayer
  Scenario: [DELAYER-TC01] Le notifiche sono pianificate secondo priorità
    Given il CSV "tc01_priorita.csv" contiene 30 notifiche appartenenti alle categorie RS, SECONDO TENTATIVO, ALTRO
    And il CSV "tc01_priorita.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    And i risultati per requestId "tc01r1" e workflow step "EVALUATE_SENDER_LIMIT" contengono esattamente 30 notifiche
    And la capacità disponibile per il driver "Poste" su provincia "RM" e per ogni deliveryDate attesa è almeno 30
    And i risultati per requestId "tc01r1" e workflow step "EVALUATE_DRIVER_CAPACITY" contengono esattamente 30 notifiche
    And i risultati per requestId "tc01r1" e workflow step "EVALUATE_PRINT_CAPACITY" contengono esattamente 30 notifiche
    And i risultati per requestId "tc01r1" e workflow step "SENT_TO_PREPARE_PHASE_2" contengono esattamente 30 notifiche
    And la deliveryDate delle notifiche coincide con la deliveryDate attesa
    Then le prime 30 notifiche per il workflow step "SENT_TO_PREPARE_PHASE_2" sono selezionate secondo l’ordine di priorità:
      | categoria         | ordinamentoCampo   |
      | RS                | prepareRequestDate |
      | SECONDO_TENTATIVO | prepareRequestDate |
      | ALTRO             | notificationSentAt |

  @delayer
    #TODO: impostare il limite mittente a 20
    #Domanda: se lato mittente ci sono dei limiti, le notifiche eccedenti e non prioritarie non passano al workflow successivo, giusto ?
  Scenario: [DELAYER-TC02] Rispetto del limite settimanale mittente e ordinamento cronologico
    Given il CSV "tc02_limite_mitt.csv" contiene 30 notifiche appartenenti alla stessa categoria
    And il CSV "tc02_limite_mitt.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    And i risultati per requestId "tc02r1" e workflow step "EVALUATE_SENDER_LIMIT" contengono esattamente 30 notifiche
    And la capacità disponibile per il driver "Poste" su provincia "RM" e per ogni deliveryDate attesa è almeno 20
    And i risultati per requestId "tc01r1" e workflow step "EVALUATE_DRIVER_CAPACITY" contengono esattamente 20 notifiche
    And i risultati per requestId "tc02r1" e workflow step "EVALUATE_PRINT_CAPACITY" contengono esattamente 20 notifiche
    And i risultati per requestId "tc02r1" e workflow step "SENT_TO_PREPARE_PHASE_2" contengono esattamente 20 notifiche
    And la deliveryDate delle notifiche coincide con la deliveryDate attesa
    Then le prime 20 notifiche sono pianificate secondo ordine cronologico per il campo "prepareRequestDate"

  @delayer
  # Si necessita di impostare a monte la capacità per il recapito nella provincia in esame
  Scenario Outline: [DELAYER-TC03] Le notifiche dei mittenti non censiti sono elaborate solo in base alla capacità residua
    Given il CSV "tc03_mittente_non_censito.csv" contiene 40 notifiche: 30 da mittenti censiti e 10 da mittenti non censiti
    And la capacità disponibile per il driver "<recapitista>" su provincia "<provincia>" è configurata a <capacity>
    And il CSV "tc03_mittente_non_censito.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "{string}" contengono esattamente <elaborateTotali> notifiche
    And sono state elaborate esattamente <elaborateNonCensiti> notifiche dei mittenti non censiti
    And sono state accantonate esattamente <accantonateNonCensiti> notifiche dei mittenti non censiti
    And la capacità usata per il driver "<recapitista>", provincia "<provincia>", data "<deliveryDate>" è pari a <elaborateTotali> su una capacità totale di <capacity>

    Examples:
      | provincia | capacity | elaborateTotali | elaborateNonCensiti | accantonateNonCensiti | recapitista | deliveryDate        |
      | RM        | 30       | 30              | 0                   | 10                    | Poste       | 2025-07-14T00:00:00Z |
      | RM        | 35       | 35              | 5                   | 5                     | Poste       | 2025-07-14T00:00:00Z |
      | RM        | 40       | 40              | 10                  | 0                     | Poste       | 2025-07-14T00:00:00Z |

  @delayer
  Scenario: [DELAYER-TC04] Le notifiche vengono posticipate alla settimana successiva quando la capacità settimanale è esaurita
    Given il CSV "tc04_capacita_esaurita.csv" contiene 20 notifiche appartenenti alla stessa categoria
    And la capacità disponibile per il driver "{recapitista}" su provincia "{provincia}" è configurata a 0
    And il CSV "tc04_capacita_esaurita.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "{requestId}" contengono esattamente 20 notifiche
    And tutte le 20 notifiche sono posticipate: deliveryDate corrisponde al lunedì della settimana successiva

  @delayer
  Scenario: [DELAYER-TC05] Le notifiche non sono pianificate per la spedizione quando la capacità di stampa giornaliera è esaurita
    Given il CSV "tc05_stampa_piena.csv" contiene 25 notifiche appartenenti alla stessa categoria
    And la capacità disponibile per la stampa nella data prevista di delivery è configurata a 0
    And il CSV "tc05_stampa_piena.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "{requestId}" contengono esattamente 25 notifiche
    Then tutte le 20 notifiche restano in attesa perché la capacità di stampa giornaliera è esaurita
    And nessuna notifica è stata ancora pianificata per la spedizione

  @delayer
    #TODO: mittente non ha capacità per la deliveryDate
  Scenario: [DELAYER-TC06] Le notifiche RS e i secondi tentativi vengono elaborate anche oltre i limiti settimanali del mittente
    Given il CSV "tc06_rs_secondi_tentativi.csv" contiene 20 notifiche: 10 RS e 10 con attempt = 1
    And il CSV "tc06_rs_secondi_tentativi.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "TC06-RS-ATTEMPT1" contengono esattamente 20 notifiche
    And tutte le 20 notifiche sono state pianificate (deliveryDate valorizzato)
    And le prime 20 notifiche sono pianificate secondo ordine cronologico per il campo "prepareRequestDate"

  @delayer
  Scenario: [DELAYER-TC07] Le notifiche con dati errati vengono scartate senza bloccare l'elaborazione
    Given il CSV "tc07_dati_non_validi.csv" contiene 20 notifiche: 10 valide e 10 non valide
    And il CSV "tc07_dati_non_validi.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "{string}" contengono esattamente 20 notifiche
    And le prime 10 notifiche sono pianificate secondo ordine cronologico per il campo "prepareRequestDate"
    And le restanti 10 notifiche non sono ancora pianificate

  @delayer
  Scenario: [DELAYER-TC08] Le notifiche vengono distribuite tra più recapiti in base alla capacità aggregata disponibile
    Given il CSV "tc08_multi_recapito.csv" contiene 20 notifiche appartenenti alla stessa categoria
    And la capacità disponibile per il driver "{recapitista1}" su provincia "{provincia}" è configurata a 10
    And la capacità disponibile per il driver "{recapitista2}" su provincia "{provincia}" è configurata a 10
    And il CSV "tc08_multi_recapito.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "{requestId}" contengono esattamente 20 notifiche
    And la capacità usata per il driver "{recapitista1}", provincia "<provincia>", data "<deliveryDate>" è pari a 10 su una capacità totale di 10
    And la capacità usata per il driver "{recapitista2}", provincia "<provincia>", data "<deliveryDate>" è pari a 10 su una capacità totale di 10
    And tutte le 20 notifiche sono state pianificate (deliveryDate valorizzato)

  @delayer
     #TODO: mittente non ha capacità per la deliveryDate
  Scenario: [DELAYER-TC09] Nessuna condizione soddisfatta – tutte le notifiche vengono posticipate
    Given il CSV "tc09_tutti_vincoli_negati.csv" contiene 20 notifiche non prioritarie da mittenti non censiti
    And il CSV "tc09_tutti_vincoli_negati.csv" è importato da S3 nella tabella di test tramite lambda
    And la capacità usata per il driver "{recapitista1}", provincia "<provincia>", data "<deliveryDate>" è pari a 0 su una capacità totale di 0
    And la capacità disponibile per la stampa nella data prevista di delivery è configurata a 0
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "{string}" contengono esattamente 20 notifiche
    And le restanti 20 notifiche non sono ancora pianificate

  @delayer
  Scenario Outline: [DELAYER-TC10] Le notifiche con provincia non mappata a una regione vengono trattate come eccedenti
    Given il CSV "tc10_provincia_senza_regione.csv" contiene 10 notifiche da mittenti validi ma con provincia non mappata
    And la capacità disponibile per il driver "<recapitista>" su provincia "<provincia>" è configurata a <capacita>
    And il CSV "tc10_provincia_senza_regione.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "TC10-NOREGION" contengono esattamente 10 notifiche
    And sono state <stato> tutte le 10 notifiche (verifica basata su deliveryDate)

    Examples:
      | provincia | recapitista | capacita | stato         |
      | XX        | Poste       | 0        | accantonate   |
      | XX        | Poste       | 10       | pianificate   |

  @delayer
  Scenario: [DELAYER-TC11] Il limite settimanale è applicato separatamente per ciascun mittente e prodotto
    Given il CSV "tc11_pa_prodotti_distinti.csv" contiene 10 notifiche: 5 da "idMittente1" e 5 da "idMittente2" con productType "AR"
    And il CSV "tc11_pa_prodotti_distinti.csv" è importato da S3 nella tabella di test tramite lambda
    #And la capacità settimanale disponibile per il mittente "idMittente1" e prodotto "AR" è configurata a 5 per la deliveryDate indicata nel CSV
    #And la capacità settimanale disponibile per il mittente "idMittente2" e prodotto "AR" è configurata a 5 per la deliveryDate indicata nel CSV
    And la capacità disponibile per il driver "{recapitista}", provincia "{provincia}", prodotto "{productType}" è configurata a 10
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "TC11-PER-PA-PRODOTTO" contengono esattamente 10 notifiche
    And tutte le 10 notifiche sono state pianificate (deliveryDate valorizzato)

  @delayer
  # ===================================================
  # SCENARIO: Recupero delle notifiche accantonate tramite capacità residua
  #
  # OBIETTIVO:
  # Verificare che, una volta esaurita la capacità nel primo batch (batch master),
  # il sistema sia in grado di riprendere in automatico le notifiche accantonate
  # qualora venga rilevata capacità residua nella stessa settimana.
  #
  # STRATEGIA:
  # - Primo CSV (accantonate): 30 notifiche da mittenti non censiti → vengono accantonate
  # - Secondo CSV (trigger): 1 notifica RS → viene elaborata
  #     ⇒ questo consuma parte della capacità e innesca la slave
  #     ⇒ la slave recupera le 30 notifiche accantonate
  #
  # NOTA:
  # La slave si attiva **solo dopo l’elaborazione di almeno una notifica** che impegni capacità.
  # Senza questo "trigger", le accantonate non vengono mai riesaminate.
  # ===================================================
  @delayer
  Scenario: [DELAYER-TC12] Le notifiche accantonate vengono riprese grazie alla capacità residua
    Given il CSV "tc12_notifiche_accantonate.csv" contiene 10 notifiche non prioritarie da mittenti non censiti
    And il CSV "tc12_notifiche_accantonate.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "TC12-RIPRESA" contengono esattamente 10 notifiche
    And sono state accantonate tutte le 10 notifiche (verifica basata su deliveryDate)

  # Attivazione della slave con capacità residua
    Given il CSV "tc12_notifiche_trigger.csv" contiene 1 notifica prioritaria (es. RS)
    And il CSV "tc12_notifiche_trigger.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato nuovamente l'algoritmo tramite lambda
    Then i risultati per requestId "TC12-RIPRESA" contengono esattamente 11 notifiche
    And sono state pianificate tutte le 11 notifiche (verifica basata su deliveryDate)

  @delayer
  Scenario: [DELAYER-TC14] Le notifiche vengono selezionate in modo ordinato in caso di overbooking della stampa
    Given il CSV "tc14_overbooking_stampa.csv" contiene 30 notifiche appartenenti alle categorie RS, SECONDO_TENTATIVO, ALTRO
    And il CSV "tc14_overbooking_stampa.csv" è importato da S3 nella tabella di test tramite lambda
    And la capacità disponibile per la stampa nella data prevista di delivery è configurata a 20
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "TC14-BATCH" contengono esattamente 20 notifiche
    And le prime 20 notifiche sono selezionate secondo l’ordine di priorità:
      | categoria           | ordinamentoCampo      |
      | RS                  | prepareRequestDate    |
      | SECONDO_TENTATIVO   | prepareRequestDate    |
      | ALTRO               | notificationSentAt    |

  @delayer
  Scenario: [DELAYER-TC15] Le notifiche sono elaborate normalmente se la tabella limiti mittente è assente
    Given il CSV "tc15_limiti_mittente_assenti.csv" contiene 20 notifiche non prioritarie da un mittente non censito
    And il CSV "tc15_limiti_mittente_assenti.csv" è importato da S3 nella tabella di test tramite lambda
    When viene avviato l'algoritmo tramite lambda
    Then i risultati per requestId "TC15-FALLBACK-LIMIT" contengono esattamente 20 notifiche
    And tutte le 20 notifiche sono state pianificate (deliveryDate valorizzato)


