@m2m-keys-apiv3
Feature: Gestione chiavi producer con API M2M V3

  Scenario Outline: [M2M_V3_GET_PRODUCER_KEY] Recupero chiave producer in formato JWK tramite kid
    Given l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene creato un producer keychain
    And viene associato l'utente chiamante al producer keychain
    And viene creata una chiave producer con kid "KID" associata al producer keychain
    When l'utente tenta di ottenere la chiave producer con kid "<kid>"
    Then si ottiene status code <statusCode>

    Examples:
      | kid             | statusCode |
      | kidAttuale      |        200 |
      | null            |        400 |
      | kidNonAssociato |        404 |
