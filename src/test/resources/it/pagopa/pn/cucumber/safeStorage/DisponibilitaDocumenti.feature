# PST: PST Disponibilita documenti
Feature: Disponibilita dei documenti SafeStorage

  # Perimetro automatico definito dalla PST: casi 1.1, 1.2, 3.1, 5.1, 6.1 e 8.1.
  # Tolleranza, delete marker, pregresso, passaggio reale della mezzanotte, GLACIER
  # e confronto OpenAPI restano verifiche manuali e non sono duplicati in questa feature.

  @e2e @documentAvailability
  Scenario Outline: [SS-DOCUMENT-AVAILABILITY-1.1] Aggiornamento con data non ancora trascorsa
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la retention del documento 1 viene aggiornata con una data "<combinazioneData>"
    Then l'aggiornamento della retention restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200

    Examples:
      | combinazioneData  |
      | SUCCESSIVA        |
      | PRECEDENTE_FUTURA |

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-1.2] Aggiornamento con data gia trascorsa
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la retention del documento 1 viene aggiornata con una data "IERI"
    Then l'aggiornamento della retention restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 410

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-3.1] Riattivazione prima della cancellazione
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When la retention del documento 1 viene aggiornata con una data "IERI"
    Then l'aggiornamento della retention restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 410
    When la retention del documento 1 viene aggiornata con una data "DOMANI"
    Then l'aggiornamento della retention restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 200

  @e2e @documentAvailability
  Scenario Outline: [SS-DOCUMENT-AVAILABILITY-5.1] Applicazione uniforme a client e documenti differenti
    Given il client SafeStorage "<clientId>" carica un nuovo documento di tipo "<documentType>"
    When la retention del documento 1 viene aggiornata con una data "IERI"
    Then l'aggiornamento della retention restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 410

    Examples:
      | clientId    | documentType                |
      | pn-test     | PN_NOTIFICATION_ATTACHMENTS |
      | pn-delivery | PN_NOTIFICATION_ATTACHMENTS |
      | pn-test     | PN_AAR                      |

  @e2e @documentAvailability
  Scenario: [SS-DOCUMENT-AVAILABILITY-6.1] Utilizzo di una URL ottenuta prima della fine disponibilita
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    And viene acquisita una presigned-url di download per il documento 1
    When la retention del documento 1 viene aggiornata con una data "IERI"
    Then l'aggiornamento della retention restituisce status code 200
    When viene richiesto il contenuto del documento 1
    Then la lettura del contenuto restituisce status code 410
    When viene effettuato il download tramite la presigned-url acquisita
    Then il download tramite la presigned-url acquisita restituisce status code 200

  @e2e @documentAvailability
  Scenario Outline: [SS-DOCUMENT-AVAILABILITY-8.1] Consultazione di metadati e tag dopo la scadenza
    Given il client SafeStorage "pn-test" carica un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con il tag "global_multivalue"
    And viene acquisito il comportamento corrente per i "<informazioni>" del documento 1
    When la retention del documento 1 viene aggiornata con una data "IERI"
    Then l'aggiornamento della retention restituisce status code 200
    When vengono richiesti i "<informazioni>" del documento 1
    Then la consultazione delle informazioni mantiene il comportamento precedente

    Examples:
      | informazioni |
      | METADATI     |
      | TAG          |
