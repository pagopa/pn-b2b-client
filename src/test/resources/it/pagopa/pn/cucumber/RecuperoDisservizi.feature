Feature: Recupero Disservizi

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_1] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi
    Given si chiama l'api di recupero elenco disseervizi nell'anno "anno" e mese "mese"
    Then viene restituito l'elenco dei disservizi del mese {mese} dell'anno "anno"


  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_2] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi senza i query params
    Given si chiama l'api di recupero elenco disseervizi nell'anno null e mese null
    Then viene restituito l'elenco dei disservizi del mese corrente

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_3] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi specificando un periodo antecedente a gennaio 2023
    Given si chiama l'api di recupero elenco disseervizi nell'anno 2022 e mese 12
    Then viene restituito un codice di errore 400

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_4] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi successivo alla data in cui avviene la chiamata oppure una data compresa tra gennaio 2023 e giugno 2023
    Given si chiama l'api di recupero elenco disseervizi nell'anno 2023 e mese 1
    Then viene restituito un elenco di disservizi vuoto

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_5] Richiamare l’API per il recupero dell’elenco dei disservizi conclusi specificando un mese errato
    Given si chiama l'api di recupero elenco disseervizi nell'anno 2023 e mese 13
    Then viene restituito un codice di errore 400

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_6] Richiamare l’API per il download dell'atto opponibile ai terzi di malfunzionamento e ripristino
    Given viene chiamata l’API per il download dell'atto opponibile ai terzi con id "ID"
    Then viene scaricato l'atto opponibile ai terzi di malfunzionamento e ripristino

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_7] Richiamare l’API per il download dell'atto opponibile ai terzi di malfunzionamento e ripristino specificando un id inesistente o errato
    Given viene chiamata l’API per il download dell'atto opponibile ai terzi con id "idErrato"
    Then viene restituito un codice di errore 400

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_8] Richiamare l’API per il download dell'atto opponibile ai terzi di malfunzionamento e ripristino  omettendo il parametro legalFactId
    Given viene chiamata l’API per il download dell'atto opponibile ai terzi con id null
    Then viene restituito un codice di errore 200 con response contenente il campo xxxxx valorizzato a "retryAfter"

  @recuperoDisservizi
  Scenario: [RECUPERO_DISSERVIZI_9] Richiamare l’API per il download dell'atto opponibile ai terzi di malfunzionamento e ripristino trascorsi 120 giorni
    Given viene chiamata l’API per il download dell'atto opponibile ai terzi con id "id"