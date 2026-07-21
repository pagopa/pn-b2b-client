Feature: Ricerca delle notifiche legali e bonarie ricevute lato mittente

#  @informalNotificationsMessageAttachment
#  Scenario: [NOTIFICHE_BONARIE_05_1] Come ente mittente Recupero i documenti di una notifica bonaria
#    Given mittente della notifica bonaria: "Comune_Multi"
#    And viene creata una nuova notifica bonaria con i seguenti parametri
#      | campaignId | campaign-1 |
#    And destinatario della notifica bonaria
#      | recipientType | PF                |
#      | taxId         | FRMTTR76M06B715E  |
#      | denomination  | Ettore Fieramosca |
#      | messageId     | ${IT}             |
#    When viene inviata una nuova notifica bonaria
#    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
#    And si tenta il recupero documento della notifica bonaria
#    Then il download risulta correttamente effettuato



  #CASO DI TEST 2.1 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  @letturaDestinatario
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_1_A] Come mittente recupero le notifiche inviate filtrando per tutti i campi obbligatori e opzionali
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | startDate         | 2026-07-07 |
      | endDate           | 2026-07-09 |
      | communicationType | INFORMAL   |
      | size              | 50         |
      | senderId          | :senderId  |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt   | 2026-07-07, 2026-07-09 |
      | senderId | :senderId              |
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | sentAt            | 2026-07-07, 2026-07-09 |
      | communicationType | INFORMAL               |
      | size              | 50                     |
      | senderId          | :senderId              |
      | recipientId       | :recipientId           |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt      | 2026-07-07, 2026-07-09 |
      | senderId    | :senderId              |
      | recipientId | :recipientId           |
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | sentAt            | 2026-07-07, 2026-07-09 |
      | communicationType | INFORMAL               |
      | size              | 50                     |
      | senderId          | :senderId              |
      | iunMatch          | :actualIun             |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt | 2026-07-07, 2026-07-09 |
      | sender | :senderId              |
      | iun    | :actualIun             |
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | sentAt            | 2026-07-07, 2026-07-09 |
      | communicationType | INFORMAL               |
      | size              | 50                     |
      | senderId          | :senderId              |
      | status            | EFFECTIVE_DATE         |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt             | 2026-07-07, 2026-07-09 |
      | sender             | :senderId              |
      | notificationStatus | EFFECTIVE_DATE         |


  @letturaDestinatario
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_1_B] Viene inviata una notifica legale con gruppo e si recuperano le notifiche inviate dal mittente filtrando per gruppo
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Palermo          |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | startDate         | 2026-07-07 |
      | endDate           | 2026-07-09 |
      | communicationType | INFORMAL   |
      | size              | 50         |
      | senderId          | :senderId  |
      | xPagopaPnCxGroups | :group     |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt            | 2026-07-07, 2026-07-09 |
      | xPagopaPnCxGroups | :group                 |


  @letturaDestinatario
  Scenario Outline: [MITTENTE_RICERCA_NOTIFICHE_1_C] Si tenta il recupero delle notifiche inviate dal mittente quando manca un campo obbligatorio
    And vengono recuperate le notifiche inviate dal mittente "Comune_Multi"
      | <campo> | $NULL |
    Then si verifica che sia stato restituito un errore di tipo "BAD REQUEST"
    Examples:
      | campo           |
      | xPagopaPnUid    |
      | xPagopaPnCxType |
      | xPagopaPnCxId   |
      | startDate       |
      | endDate         |


    #######################
    # Comunicazioni Bonarie
    #######################

  @letturaDestinatario
  Scenario: [MITTENTE_RICERCA_NOTIFICHE_BONARIE_2.A] Vengono inviate due notifiche bonarie con esiti differenti
  e si recuperano le notifiche inviate dal mittente filtrando per specifici criteri
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType | PF               |
      | taxId         | FRMTTR76M06B715E |
      | denomination  | Mario Cucumber   |
      | messageId     | ${IT}            |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"

    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-4 |
    And destinatario della notifica bonaria
      | recipientType | PF               |
      | taxId         | FRMTTR76M06B715E |
      | denomination  | Mario Cucumber   |
      | messageId     | ${IT}            |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "REFUSED"

    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate  | 2026-07-07         |
      | endDate    | 2026-07-09         |
      | campaignId | campaign-1         |
      | senderId   | :informal_senderId |
      | size       | 50                 |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt     | 2026-07-07, 2026-07-09 |
      | campaignId | campaign-1             |
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate | 2026-07-07         |
      | endDate   | 2026-07-09         |
      | status    | REFUSED            |
      | senderId  | :informal_senderId |
      | size      | 50                 |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt             | 2026-07-07, 2026-07-09 |
      | notificationStatus | REFUSED                |
#    ricerca per specifico esito
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-09 |
      | delivered | true       |
      | size      | 50         |
    And l'elenco delle notifiche recuperate dalla PA rispettare i seguenti criteri:
      | sentAt    | 2026-07-07, 2026-07-09 |
      | delivered | true                   |

  @letturaDestinatario
  Scenario Outline: [MITTENTE_RICERCA_NOTIFICHE_BONARIE_2.B] Si tenta il recupero delle notifiche bonarie inviate dal mittente quando manca un campo obbligatorio
    And vengono recuperate le notifiche bonarie inviate dal mittente "Comune_Multi"
      | <campo> | $NULL |
    Then si verifica che sia stato restituito un errore di tipo "BAD REQUEST"
    Examples:
      | campo           |
      | xPagopaPnUid    |
      | xPagopaPnCxType |
      | xPagopaPnCxId   |
      | startDate       |
      | endDate         |
      | campaignId      |

