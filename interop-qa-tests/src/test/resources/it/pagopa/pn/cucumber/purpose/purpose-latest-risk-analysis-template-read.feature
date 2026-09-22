@purpose_latest_risk_analysis
Feature: Lettura del template di analisi del rischio più recente
  Tutti gli utenti possono leggere il template dell'analisi del rischio, specifico per il proprio ente, di una propria finalità

  @happy-path
  @nrt-minimal
  @purpose_latest_risk_analysis_template_read1 @fixed_in_node
  Scenario Outline: [LETTURA_TEMPLATE_RISK_ANALYSIS_1] Per una richiesta di lettura del template di analisi del rischio da parte di un ente, alla richiesta di lettura, ottiene l'ultima versione di analisi del rischio dedicata a quel tipo specifico di ente
    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente richiede il template dell'analisi del rischio
    Then si ottiene status code 200 e il template in versione "<versione>"

    Examples:
      | ente    | ruolo        | versione |
      | PA1     | admin        |      3.2 |
      | PA1     | api          |      3.2 |
      | PA1     | security     |      3.2 |
      | PA1     | api,security |      3.2 |
      | PA1     | support      |      3.2 |
      | GSP     | admin        |      2.1 |
      | GSP     | api          |      2.1 |
      | GSP     | security     |      2.1 |
      | GSP     | api,security |      2.1 |
      | GSP     | support      |      2.1 |
      | Privato | admin        |      2.1 |
      | Privato | api          |      2.1 |
      | Privato | security     |      2.1 |
      | Privato | api,security |      2.1 |
      | Privato | support      |      2.1 |
