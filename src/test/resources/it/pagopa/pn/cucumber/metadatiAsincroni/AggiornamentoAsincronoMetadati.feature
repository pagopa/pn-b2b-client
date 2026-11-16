#PST: <link PST da confermare>
#Jira: <riferimento da confermare>
Feature: Aggiornamento asincrono dei metadati della notifica

  # ============================== FASE 1 ==============================
  # Gestione del lock distribuito, pagamento tramite portale senza path critico,
  # API costi notifica e logging delle API di pn-external-registries su CloudWatch

  # TODO: sostituire <messaggio lock non disponibile> con il testo del log di pn-delivery-push emesso quando il lock non è acquisibile
  @aggiornamentoMetadatiFase1 @mockPec
  Scenario: [ASYNC-META_1] Notifica multi destinatario (5) su PEC slow: eventi non elaborati per lock non disponibile vengono ritentati e completati
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@OK-pecSlowSuccess.it |
    And destinatario Mario Cucumber e:
      | digitalDomicile_address | test@OK-pecSlowSuccess.it |
    And destinatario Ettore Fieramosca e:
      | digitalDomicile_address | test@OK-pecSlowSuccess.it |
    And destinatario Cristoforo Colombo e:
      | digitalDomicile_address | test@OK-pecSlowSuccess.it |
    And destinatario Leonardo da Vinci e:
      | digitalDomicile_address | test@OK-pecSlowSuccess.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 1
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 2
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 3
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 4
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery-push" negli ultimi 30 minuti riportante i seguenti dati nel messaggio
      | iun     | auto                             |
      | message | <messaggio lock non disponibile> |
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And verifico che su DynamoDB non è presente il lock della notifica nella tabella pn-PnDeliveryPushShedLock

  @aggiornamentoMetadatiFase1
  Scenario: [ASYNC-META_2] Notifica con 2 avvisi pagoPA e 2 F24: pagamento di un avviso da portale senza lock residuo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm   | SI                   |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | title_payment        | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa    | SI                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 2                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    Then l'avviso pagopa 0 viene pagato correttamente dall'utente 0
    And si attende il corretto pagamento della notifica con l' avviso 0 dal destinatario 0
    And verifico che su DynamoDB non è presente il lock della notifica nella tabella pn-PnDeliveryPushShedLock

  @aggiornamentoMetadatiFase1
  Scenario: [ASYNC-META_3] Recupero costo notifica v2.3 prima dello stato ACCEPTED: 404 PN_DELIVERY_PUSH_NOTIFICATION_NOT_ACCEPTED
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI   |
      | payment_f24        | NULL |
      | apply_cost_pagopa  | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" senza aspettare che diventi accepted
    And viene richiesto il costo della notifica tramite api v2.3 per l'avviso 0 del destinatario 0
    Then l'operazione ha prodotto un errore con status code "404" con messaggio di errore "PN_DELIVERY_PUSH_NOTIFICATION_NOT_ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 10 minuti riportante i seguenti dati nel messaggio
      | iun    | auto                      |
      | api    | notification-process-cost |
      | status | 404                       |

  @aggiornamentoMetadatiFase1
  Scenario: [ASYNC-META_4] Calcolo costo tramite API notification-process-cost: risposta 200
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI   |
      | payment_f24        | NULL |
      | apply_cost_pagopa  | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then viene invocata l'API di calcolo costo della notifica per il destinatario 0 che risponde correttamente

  @aggiornamentoMetadatiFase1
  Scenario: [ASYNC-META_5] Recupero enti attivi su PN: risposta 200 e log su pn-external-registries
    When viene invocata l'API di recupero degli enti attivi su PN
    Then l'operazione non ha prodotto errori
    And verifico la presenza di un audit log su "/aws/ecs/pn-external-registries" negli ultimi 5 minuti riportante i seguenti dati nel messaggio
      | api | /ext-registry/pa/v1/activated-on-pn |

  @aggiornamentoMetadatiFase1
  Scenario: [ASYNC-META_6] Recupero gruppi utente PG: risposta 200 e log su pn-external-registries
    When viene invocata l'API di recupero dei gruppi utente della PG per l'utente amministratore
    Then l'operazione non ha prodotto errori
    And verifico la presenza di un audit log su "/aws/ecs/pn-external-registries" negli ultimi 5 minuti riportante i seguenti dati nel messaggio
      | api | /ext-registry/pg/v1/user-groups |

  @aggiornamentoMetadatiFase1 @mockCheckout
  Scenario: [ASYNC-META_7] Creazione carrello checkout: log su pn-external-registries-checkout
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@ok_890  |
      | payment_creditorTaxId   | 77777777777 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then si richiama checkout con dati:
      | amount      | 10                                       |
      | description | PIPPO                                    |
      | companyName | PLUTO                                    |
      | returnUrl   | https://www.pagopa.gov.it/it/assistenza/ |
    And verifico la presenza di un audit log su "/aws/ecs/pn-external-registries-checkout" negli ultimi 5 minuti riportante i seguenti dati nel messaggio
      | api | /ext-registry/pagopa/v1/checkout-cart |

  @aggiornamentoMetadatiFase1 @mockCheckout
  Scenario: [ASYNC-META_8] Recupero informazioni di pagamento v2.1: risposta 200 e log su pn-external-registries-checkout
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | payment_creditorTaxId | 77777777777        |
      | payment_noticeCode    | 302191689948151964 |
    Then verifica stato pagamento di una notifica con status "REQUIRED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-external-registries-checkout" negli ultimi 5 minuti riportante i seguenti dati nel messaggio
      | api        | /ext-registry/pagopa/v2.1/paymentinfo |
      | noticeCode | 302191689948151964                    |

  # ============================== FASE 2 ==============================
  # Scrittura dei metadati su DynamoDB, rimozione dei pagamenti dalla tabella costi in caso di rifiuto,
  # deprecazione dell'API /delivery-private/notifications/update-status

  @aggiornamentoMetadatiFase2
  Scenario: [ASYNC-META_9] Notifica digitale a PF con PG delegata: scrittura metadati per destinatario e delegato
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    Then si verifica sulla tabella pn-NotificationsMetadata che per lo IUN ":actualIun" e il destinatario con taxId ":recipientId" di tipo "PF" gli attributi siano:
      | communicationType | LEGAL |
    And verifico che su DynamoDB è presente il record di delega della notifica per il destinatario 0 e il delegato "GherkinSrl" nella tabella pn-NotificationDelegationMetadata

  @aggiornamentoMetadatiFase2
  Scenario: [ASYNC-META_10] Notifica con 2 avvisi pagoPA e 2 F24 rifiutata per indirizzo fisico errato: rimozione pagamenti da pn-NotificationsCost
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | physicalAddress_address      | VIA LAMARMORA 40/A   |
      | physicalAddress_zip          | 20122                |
      | physicalAddress_municipality | NON IDENTIFICATO     |
      | physicalAddress_province     | XX                   |
      | payment_pagoPaForm           | SI                   |
      | payment_f24                  | PAYMENT_F24_STANDARD |
      | title_payment                | F24_STANDARD_GHERKIN |
      | apply_cost_pagopa            | SI                   |
      | apply_cost_f24               | SI                   |
      | payment_multy_number         | 2                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "REFUSED"
    Then verifico che su DynamoDB non siano presenti i pagamenti del destinatario 0 nella tabella pn-NotificationsCost

  @aggiornamentoMetadatiFase2
  Scenario: [ASYNC-META_11] Notifica digitale perfezionata senza invocazione di /delivery-private/notifications/update-status
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Milano            |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"
    And verifico l'assenza di log su "/aws/ecs/pn-delivery-push" negli ultimi 30 minuti riportanti i seguenti dati nel messaggio
      | iun | auto                                          |
      | api | /delivery-private/notifications/update-status |

  @aggiornamentoMetadatiFase2
  Scenario: [ASYNC-META_12] Invocazione API deprecata /delivery-private/notifications/update-status: errore 405
    When viene invocata l'API deprecata di aggiornamento stato della notifica
    Then l'operazione ha prodotto un errore con status code "405"
