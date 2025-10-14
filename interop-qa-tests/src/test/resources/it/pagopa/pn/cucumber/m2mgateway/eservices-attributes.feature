@m2mEservices
Feature: Gestione degli attributi degli e-services attraverso APIs M2M V2

  @m2m-parte2-ottobre
  Scenario Outline: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_01_A] Un utente con ruolo M2M-ADMIN può aggiungere degli attributi certificati a una versione di un e-service (Parte2#Scenario intorno a 197)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea 4 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And "PA1" porta il descrittore dell'e-service in stato "<stato>"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    When l'utente tenta di aggiungere gli attributi certificati numeri da 2 a 3 al gruppo dell'e-service
    Then si ottiene lo status code 200

    # Verifica che il risultato sia coerente e che non sia stato modificato l'attributo caricato in fase di creazione
    And gli attributi certificati restituiti dell'e-service sono coerenti con quelli aggiunti
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati sono stati aggiunti correttamente al gruppo dell'e-service
    And i precedenti attributi certificati del gruppo dell'e-service sono rimasti invariati
    When l'utente tenta di aggiungere l'attributo certificato numero 4 al gruppo dell'e-service
    Then si ottiene lo status code 200

    # Verifica che il risultato sia coerente e che non siano stati modificati gli attributi aggiunti in precedenza
    And gli attributi certificati restituiti dell'e-service sono coerenti con quelli aggiunti
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati sono stati aggiunti correttamente al gruppo dell'e-service
    And i precedenti attributi certificati del gruppo dell'e-service sono rimasti invariati
    Examples:
      | stato       |
      | DRAFT       |
      | PUBLISHED   |
      | SUSPENDED   |
      | DEPRECATED  |

  @m2m-parte2-ottobre
  Scenario: [M2M_ESERVICES_CERTIFIED_ATTRIBUTES_01_B] Un utente con ruolo M2M-ADMIN può aggiungere degli attributi certificati a una versione di un e-service in stato WAITING_FOR_APPROVAL (Parte2#Scenario intorno a 197)
    Given "PA1" ha già creato un e-service con un descrittore in stato "DRAFT"
    And l'utente crea 4 attributi certificati con successo
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente crea un gruppo di attributi contenente 1 attributo certificato con successo
    And "PA1" porta il descrittore dell'e-service in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente è un "admin" di "PA2" con ruolo M2M m2m-admin
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    When l'utente tenta di aggiungere gli attributi certificati numeri da 2 a 3 al gruppo dell'e-service
    Then si ottiene lo status code 200

    # Verifica che il risultato sia coerente e che non sia stato modificato l'attributo caricato in fase di creazione
    And gli attributi certificati restituiti dell'e-service sono coerenti con quelli aggiunti
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati sono stati aggiunti correttamente al gruppo dell'e-service
    And i precedenti attributi certificati del gruppo dell'e-service sono rimasti invariati
    When l'utente tenta di aggiungere l'attributo certificato numero 4 al gruppo dell'e-service
    Then si ottiene lo status code 200

    # Verifica che il risultato sia coerente e che non siano stati modificati gli attributi aggiunti in precedenza
    And gli attributi certificati restituiti dell'e-service sono coerenti con quelli aggiunti
    And [si prende nota dello stato degli attributi certificati del gruppo dell'e-service]
    And gli attributi certificati sono stati aggiunti correttamente al gruppo dell'e-service
    And i precedenti attributi certificati del gruppo dell'e-service sono rimasti invariati