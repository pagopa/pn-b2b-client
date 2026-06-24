@agreementApprovalPolicy-edit
Feature: Test modificabilità campo "agreementApprovalPolicy"

  @app-edit-ff-on
  Scenario Outline: [MOD_APP_ON_1] Il campo "agreementApprovalPolicy" di un e-service in stato PUBLISHED, SUSPENDED, DEPRECATED può essere modificato da un utente con ruolo ADMIN o API
    Given "PA1" ha già creato un e-service in stato "<stato>" con approvazione "<valore_iniziale>"
    When l'utente è un "<ruolo>" di "PA1"
    And l'utente tenta la modifica di agreementApprovalPolicy in "<valore_finale>"
    Then si ottiene lo status code 200
    And il valore di agreementApprovalPolicy dell'e-service è adesso "<valore_finale>"
    Examples:
      | stato                 | ruolo | valore_iniziale | valore_finale |
      | PUBLISHED             | admin | MANUAL          | MANUAL        |
      | PUBLISHED             | admin | MANUAL          | AUTOMATIC     |
      | PUBLISHED             | admin | AUTOMATIC       | AUTOMATIC     |
      | PUBLISHED             | admin | AUTOMATIC       | MANUAL        |
      | PUBLISHED             | api   | MANUAL          | MANUAL        |
      | PUBLISHED             | api   | MANUAL          | AUTOMATIC     |
      | PUBLISHED             | api   | AUTOMATIC       | AUTOMATIC     |
      | PUBLISHED             | api   | AUTOMATIC       | MANUAL        |
      | SUSPENDED             | admin | MANUAL          | MANUAL        |
      | SUSPENDED             | admin | MANUAL          | AUTOMATIC     |
      | SUSPENDED             | admin | AUTOMATIC       | AUTOMATIC     |
      | SUSPENDED             | admin | AUTOMATIC       | MANUAL        |
      | SUSPENDED             | api   | MANUAL          | MANUAL        |
      | SUSPENDED             | api   | MANUAL          | AUTOMATIC     |
      | SUSPENDED             | api   | AUTOMATIC       | AUTOMATIC     |
      | SUSPENDED             | api   | AUTOMATIC       | MANUAL        |
      | DEPRECATED            | admin | MANUAL          | MANUAL        |
      | DEPRECATED            | admin | MANUAL          | AUTOMATIC     |
      | DEPRECATED            | admin | AUTOMATIC       | AUTOMATIC     |
      | DEPRECATED            | admin | AUTOMATIC       | MANUAL        |
      | DEPRECATED            | api   | MANUAL          | MANUAL        |
      | DEPRECATED            | api   | MANUAL          | AUTOMATIC     |
      | DEPRECATED            | api   | AUTOMATIC       | AUTOMATIC     |
      | DEPRECATED            | api   | AUTOMATIC       | MANUAL        |

  @app-edit-ff-on
  Scenario Outline: [MOD_APP_ON_2_A] Il campo "agreementApprovalPolicy" di un e-service in stato ARCHIVED o DRAFT non può essere modificato
    Given "PA1" ha già creato un e-service in stato "<stato>" con approvazione "AUTOMATIC"
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la modifica di agreementApprovalPolicy in "MANUAL"
    Then si ottiene lo status code 400
    Examples:
      | stato     |
      | ARCHIVED  |
      | DRAFT     |

  @app-edit-ff-on @deleghe2
  Scenario: [MOD_APP_ON_2_B] Il campo "agreementApprovalPolicy" di un e-service in stato WAITING_FOR_APPROVAL non può essere modificato
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2"
    When l'utente tenta la modifica di agreementApprovalPolicy in "AUTOMATIC"
    Then si ottiene lo status code 400

  @app-edit-ff-on
  Scenario: [MOD_APP_ON_3] Il campo "agreementApprovalPolicy" di un e-service inesistente non può essere modificato
    Given l'utente è un "admin" di "PA1"
    When l'utente tenta la modifica di agreementApprovalPolicy di un e-service inesistente
    Then si ottiene lo status code 404

  @app-edit-ff-on
  Scenario: [MOD_APP_ON_4] Il campo "agreementApprovalPolicy" di un e-service non può essere modificato specificando un valore vuoto
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la modifica di agreementApprovalPolicy specificando un valore vuoto
    Then si ottiene lo status code 400

  @app-edit-ff-on
  Scenario: [MOD_APP_ON_5] Il campo "agreementApprovalPolicy" di un e-service non può essere modificato da un ente differente dal creatore dell'e-service
    Given "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When l'utente è un "admin" di "PA2"
    And l'utente tenta la modifica di agreementApprovalPolicy in "MANUAL"
    Then si ottiene lo status code 403

  @app-edit-ff-on
  Scenario Outline: [MOD_APP_ON_6] Il campo "agreementApprovalPolicy" di un e-service non può essere modificato da un ente con ruolo diverso da ADMIN o API
    Given "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "AUTOMATIC"
    When l'utente è un "<ruolo>" di "PA2"
    And l'utente tenta la modifica di agreementApprovalPolicy in "MANUAL"
    Then si ottiene lo status code 403
    Examples:
      | ruolo     |
      | security  |
      | support   |

    @nuovi-operatori-update
    Examples:
      | ruolo     |
      | reviewer  |
      | viewer    |

  @app-edit-ff-off
  Scenario Outline: [MOD_APP_OFF_1_A] Il campo "agreementApprovalPolicy" di un e-service in stato PUBLISHED, SUSPENDED, DEPRECATED non può essere modificato
    Given "PA1" ha già creato un e-service in stato "<stato>" con approvazione "MANUAL"
    When l'utente è un "admin" di "PA1"
    And l'utente tenta la modifica di agreementApprovalPolicy in "AUTOMATIC"
    Then si ottiene lo status code 403
    Examples:
      | stato                 |
      | PUBLISHED             |
      | SUSPENDED             |
      | DEPRECATED            |

  @app-edit-ff-off
  Scenario: [MOD_APP_OFF_1_B] Il campo "agreementApprovalPolicy" di un e-service in stato WAITING_FOR_APPROVAL non può essere modificato
    Given l'ente "PA2" concede la disponibilità a ricevere deleghe
    And l'utente è un "admin" di "PA1"
    And "PA1" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And l'utente richiede la creazione di una delega per l'ente "PA2"
    And l'ente "PA2" accetta la delega
    And l'utente è un "admin" di "PA2"
    And l'utente crea una nuova versione dell'e-service
    And l'utente delegato pubblica la versione dell'e-service
    When l'utente tenta la modifica di agreementApprovalPolicy in "AUTOMATIC"
    Then si ottiene lo status code 403