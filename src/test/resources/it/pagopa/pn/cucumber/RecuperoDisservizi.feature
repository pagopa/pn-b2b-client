Feature: Recupero Disservizi

  #RECUPERO_DISSERVIZI_10
  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_1] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi e verificare la presenza del nuovo campo fileAvailableTimestamp correttamente valorizzato
    Given si chiama l'api di recupero elenco disservizi nell'anno e mese corrente
    Then viene restituito l'elenco dei disservizi del mese 11 dell'anno 2024

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_2] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi senza i query params
    Given si chiama l'api di recupero elenco disservizi con mese e anno vuoti
    Then viene restituito l'elenco dei disservizi del mese e dell'anno corrente

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_3] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi specificando un periodo antecedente a gennaio 2023
    Given si chiama l'api di recupero elenco disservizi nell'anno 2022 e mese 12
    Then si controlla che l'api restituisce un codice di errore 400

  @recuperoDisservizi
  Scenario Outline: [RECUPERO_DISSERVIZI_4] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi successivo alla data in cui avviene la chiamata oppure una data compresa tra gennaio 2023 e giugno 2023
    Given si chiama l'api di recupero elenco disservizi nell'anno <year> e mese <month>
    Then viene restituito un elenco di disservizi vuoto
    Examples:
      | year | month |
      | 2023 | 1     |
      | 2065 | 10    |

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_5] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi specificando un mese errato
    Given si chiama l'api di recupero elenco disservizi nell'anno 2023 e mese 13
    Then si controlla che l'api restituisce un codice di errore 400

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_6] Richiamare l’API per il download dell'atto opponibile ai terzi di malfunzionamento e ripristino
    Given viene chiamata l’API per il download dell'atto opponibile ai terzi con id "CORRETTO"
    Then viene scaricato l'atto opponibile ai terzi di malfunzionamento e ripristino

  @recuperoDisservizi
  Scenario Outline: [RECUPERO_DISSERVIZI_7] Richiamare l’API per il download dell'atto opponibile ai terzi di malfunzionamento e ripristino specificando un id inesistente o errato
    Given viene chiamata l’API per il download dell'atto opponibile ai terzi con id "<idType>"
    Then si controlla che l'api restituisce un codice di errore 400
    Examples:
    | idType |
    | null   |
    | ERRATO |
    |        |

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_9] Richiamare l’API per il download dell'atto opponibile ai terzi di malfunzionamento e ripristino trascorsi 365 giorni
    Given viene chiamata l’API per il download dell'atto opponibile prodotto piu di 365 giorni precedenti
    Then la chiamata va con successo e la risposta contiene il campo retryAfter popolato
