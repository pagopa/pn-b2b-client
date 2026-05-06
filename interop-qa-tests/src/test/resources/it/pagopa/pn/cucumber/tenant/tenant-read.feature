@tenant
Feature: Lettura di un singolo aderente
  Tutti gli utenti autenticati possono leggere un singolo aderente

  @nrt-minimal
  @tenant_read1
  Scenario Outline: [TENANT_READ_1] Per un aderente della piattaforma, alla richiesta di lettura da parte di qualsiasi livello di permesso associato a qualsiasi tipologia di ente, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente richiede la lettura dell'aderente "PA2"
    Then si ottiene status code 200 

    Examples:
      | ente    | ruolo        |
      | GSP     | admin        |
      | GSP     | api          |
      | GSP     | security     |
      | GSP     | api,security |
      | GSP     | support      |
      | PA1     | api          |
      | PA1     | admin        |
      | PA1     | security     |
      | PA1     | support      |
      | PA1     | api,security |
      | Privato | admin        |
      | Privato | api          |
      | Privato | security     |
      | Privato | support      |
      | Privato | api,security |

  @hotfix_QA-13870
  Scenario Outline: [TENANT_DELEGATIONS_ALLOWED] Un ente della piattaforma può ottenere informazioni sul proprio stato di essere considerato Pubblica Amministrazione
    Given l'utente è un "<ruolo>" di "<ente>"
    When l'utente visualizza se all'utente d'appartenenza è permesso partecipare a processi di delega
    Then l'utente ottiene responso <responso> dal sistema sul poter partecipare a processi di delega

    Examples:
      | ente    | ruolo        | responso |
      | GSP     | admin        | negativo |
      | GSP     | api          | negativo |
      | GSP     | security     | negativo |
      | GSP     | api,security | negativo |
      | GSP     | support      | negativo |
      | PA1     | api          | positivo |
      | PA1     | admin        | positivo |
      | PA1     | security     | positivo |
      | PA1     | support      | positivo |
      | PA1     | api,security | positivo |
      | Privato | admin        | negativo |
      | Privato | api          | negativo |
      | Privato | security     | negativo |
      | Privato | support      | negativo |
      | Privato | api,security | negativo |