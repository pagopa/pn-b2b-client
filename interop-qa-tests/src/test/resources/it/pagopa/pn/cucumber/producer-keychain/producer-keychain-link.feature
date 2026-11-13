@producer-keychain
@producer-keychain-link
Feature: Associazione del producer-keychain agli e-services

  # PST: https://pagopa.atlassian.net/wiki/x/HYCyzg
  # CASO DI TEST 1.1
  @work-item:QA-17417
  Scenario Outline: [PRODUCER_KEYCHAIN_LINK_1] - Associazione del producer-keychain a un e-service in stato legittimo
    Given "PA1" ha già creato un e-service in stato <stato>
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    When l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    Then si ottiene status code 200
    And all'utente "admin" di "PA1" risulta che il portachiavi erogatore sia associato all'e-service
    Examples:
      | stato               |
      | DRAFT               |
      | PUBLISHED           |
      | DEPRECATED          |
      | SUSPENDED           |
      | ARCHIVING           |
      | ARCHIVING_SUSPENDED |

  # PST: https://pagopa.atlassian.net/wiki/x/HYCyzg
  # CASO DI TEST 1.2
  @work-item:QA-17417 @deleghe2
  Scenario: [PRODUCER_KEYCHAIN_LINK_2] - Associazione del producer-keychain a un e-service in stato WAITING_FOR_APPROVAL
    Given "PA1" ha già creato un e-service con un descrittore in stato WAITING_FOR_APPROVAL usando "PA2" come delegato
    And l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    When l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service
    Then si ottiene status code 200
    And all'utente "admin" di "PA1" risulta che il portachiavi erogatore sia associato all'e-service


  # PST: https://pagopa.atlassian.net/wiki/x/HYCyzg
  # CASO DI TEST 1.3
  #
  # DEV. NOTE 10/11/2026: in ambiente QA non è al momento possibile effettuare una transizione di stato di un
  # e-service verso lo stato ARCHIVED in tempi ragionevoli. E' possibile solo riferirsi ad e-service in stato
  # ARCHIVED pre-creati attraverso lo step di data preparation.
  @work-item:QA-17417
  Scenario: [PRODUCER_KEYCHAIN_LINK_3] - Associazione del producer-keychain a un e-service in stato illegittimo
    Given l'utente "admin" di "PA1" crea un portachiavi erogatore con successo
    When l'utente "admin" di "PA1" associa il portachiavi erogatore all'e-service con id "45d822b9-066c-4a77-b4a1-331135496907"
    Then si ottiene status code 409
    And all'utente "admin" di "PA1" risulta che il portachiavi erogatore non sia associato all'e-service