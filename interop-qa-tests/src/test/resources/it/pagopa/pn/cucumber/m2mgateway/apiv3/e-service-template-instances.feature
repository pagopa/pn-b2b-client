@m2m-apiv3-eservice-template-instance-creation
Feature: (M2M v3) Gestione delle istanze di e-service template

  @happy-path
  Scenario Outline: [M2M_V3_ESERVICE_TEMPLATE_INSTANCE_CREATION_1] La creazione di un nuovo e-service a partire da un template attivo può essere effettuata da un ente con ruolo M2M-ADMIN
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità <mode> in stato di PUBLISHED
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When l'utente m2m v3 tenta la creazione di un nuovo e-service a partire dal template indicando solo le specifiche strettamente necessarie
    Then si ottiene response status code 200
    And il nuovo e-service è stato creato correttamente in stato DRAFT

    Examples:
      | mode       |
      | erogazione |
      | ricezione  |
