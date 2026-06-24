@tenant
Feature: Revoca di un attributo dichiarato posseduto da uno specifico aderente
  Tutti gli utenti autorizzati degli enti erogatori possono revocare uno degli attributi dichiarati che si sono precedentemente assegnati

  @nrt-minimal
  @tenant_revoke_declared_attribute1 @no-parallel
  Scenario Outline: [TENANT_REVOKE_DECLARED_ATTRIBUTE_1] Per un attributo precedentemente dichiarato dall’aderente stesso, alla richiesta di revoca da parte di un utente con sufficienti permessi (admin) appartenente a quell'ente, va a buon fine
    Given l'utente è un "<ruolo>" di "<ente>"
    Given "<ente>" ha già dichiarato un attributo
    When l'utente revoca l'attributo precedentemente dichiarato
    Then si ottiene status code <statusCode>

    Examples:
      | ente | ruolo        | statusCode |
      | PA1  | admin        |        204 |
      | PA1  | api          |        403 |
      | PA1  | security     |        403 |
      | PA1  | support      |        403 |
      | PA1  | api,security |        403 |

    @nuovi-operatori-update
    Examples:
      | ente | ruolo        | statusCode |
      | PA2  | reviewer     |        403 |
      | PA2  | viewer       |        403 |
