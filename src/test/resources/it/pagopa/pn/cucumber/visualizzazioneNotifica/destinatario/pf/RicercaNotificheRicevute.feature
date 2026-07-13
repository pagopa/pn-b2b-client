Feature: Ricerca delle notifiche ricevute lato destinatario

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



  Background:
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType | PF               |
      | taxId         | FRMTTR76M06B715E |
      | denomination  | Mario Cucumber   |
      | messageId     | ${IT}            |

  #CASO DI TEST 4.1 - tutti i campi (obbligatori e opzionali) valorizzati correttamente
  @letturaDestinatario @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_1] Come destinatario <tipo> ricerco le notifiche ricevute con tutti i filtri valorizzati
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | 2026-07-07 |
      | endDate           | 2026-07-09 |
      | communicationType | ALL        |
      | size              | 50         |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL, INFORMAL |
    Examples:
      | tipo | destinatario   |
      | PF   | Mario Cucumber |
      | PG   | CucumberSpa    |

  #CASO DI TEST 4.1 - communicationType = INFORMAL -> la notifica bonaria è presente
  @letturaDestinatario @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_4] Come destinatario <tipo> ricerco le notifiche ricevute di tipo INFORMAL e trovo la notifica bonaria
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | 2026-07-07 |
      | endDate           | 2026-07-09 |
      | communicationType | INFORMAL   |
      | size              | 50         |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | INFORMAL |
    Examples:
      | tipo | destinatario   |
      | PF   | Mario Cucumber |
      | PG   | CucumberSpa    |


  #CASO DI TEST 4.1 - communicationType = LEGAL -> la notifica bonaria non è presente
  @letturaDestinatario @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_5] Come destinatario <tipo> ricerco le notifiche ricevute di tipo LEGAL e non trovo la notifica bonaria
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate         | 2026-07-07 |
      | endDate           | 2026-07-09 |
      | communicationType | LEGAL      |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL |
    Examples:
      | tipo | destinatario   |
      | PF   | Mario Cucumber |
      | PG   | CucumberSpa    |

  #CASO DI TEST 4.1 - communicationType assente -> di default vengono cercate solo le LEGAL
  @letturaDestinatario
  Scenario Outline: [RICERCA_RICEVUTE_6] Come destinatario <tipo> ricerco le notifiche ricevute senza specificare communicationType e di default non trovo la notifica bonaria
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Given "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-09 |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL |
    Examples:
      | tipo | destinatario   |
      | PF   | Mario Cucumber |
      | PG   | CucumberSpa    |

  #CASO DI TEST 4.1 - solo campi obbligatori valorizzati correttamente
  @letturaDestinatario
  Scenario Outline: [RICERCA_RICEVUTE_2] Come destinatario <tipo> ricerco le notifiche ricevute con i soli filtri obbligatori
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And "<destinatario>" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-09 |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
      | communicationType | LEGAL |
    Examples:
      | tipo | destinatario   |
      | PF   | Mario Cucumber |
      | PG   | CucumberSpa    |

  #CASO DI TEST 4.1 - campo obbligatorio non valorizzato -> 400 KO
  @letturaDestinatario @useB2B
  Scenario Outline: [RICERCA_RICEVUTE_3] Come destinatario non riesco a ricercare le notifiche ricevute se manca un campo obbligatorio
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | <campo> | NULL |
    Then si verifica che venga ritornato un errore di tipo "BAD REQUEST"
    Examples:
      | campo           |
      | xPagopaPnUid    |
      | xPagopaPnCxType |
      | xPagopaPnCxId   |
      | startDate       |
      | endDate         |

  @deleghe1 @useB2B
  Scenario: [B2B-AOO-UO_MANDATE_2] Un delegato riceve una notifica legale e la ricerca delle notifiche ricevute lato delegato restituisce le notifiche attese dei criteri di ricerca
    Given "Mario Cucumber" rifiuta se presente la delega ricevuta "Mario Gherkin"
    And "Mario Cucumber" viene delegato da "Mario Gherkin" per comune "Comune_Multi"
    And "Mario Cucumber" accetta la delega "Mario Gherkin"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo         |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-10 |
      | iunMatch  | :actualIun |
    And Si verifica che il numero di notifiche restituite nella pagina sia 1
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
#      | communicationType | LEGAL      |
      | iun | :actualIun |
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-10 |
      | iunMatch  | :actualIun |
      | senderId  | :senderId  |
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
#      | communicationType | LEGAL     |
      | sender | :sender |


  @deleghe1 @useB2B
  Scenario: [B2B-AOO-UO_MANDATE_2AA] Un delegato tenta di recuperare le notifiche ricevute di un delegante per il quale ha accettato la delega e si
    aspetta di ricevere le notifiche attese dei criteri di ricerca
    Given "Mario Cucumber" rifiuta se presente la delega ricevuta "Mario Gherkin"
    And "Mario Cucumber" viene delegato da "Mario Gherkin" per comune "Comune_Multi"
    And "Mario Cucumber" accetta la delega "Mario Gherkin"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di Aglientu         |
    And destinatario Mario Gherkin e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And imposto lo iun di SharedSteps a "PQHX-ZUQV-QDQT-202607-K-1" e la pa a "Comune_Multi"
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-10 |
      | iunMatch  | :actualIun |
      | mandateId | :mandateId |
    # POSSIBILE BUG: FA QUERY SUL MANDATEID MA NELLA RISPOSTA VA IN ERRORE PERCHé IL MANDATEID NON è PRESENTE NELLA RISPOSTA
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
#      | communicationType | LEGAL      |
      | mandateId         | :mandateId |
    And "Mario Cucumber" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate | 2026-07-07 |
      | endDate   | 2026-07-10 |
      | iunMatch  | :actualIun |
      | mandateId | :mandateId |
    And Si verifica che il numero di notifiche restituite nella pagina sia 1
    And l'elenco delle notifiche recuperate devono rispettare i seguenti criteri:
#      | communicationType | LEGAL      |
      | mandateId         | :mandateId |
      | iun               | :actualIun |







