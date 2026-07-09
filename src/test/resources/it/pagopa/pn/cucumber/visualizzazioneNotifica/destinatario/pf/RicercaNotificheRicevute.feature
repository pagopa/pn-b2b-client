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
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Mario Cucumber |
      | messageId     | ${IT}             |

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
#      | PG   | CucumberSpa    |

  #CASO DI TEST 4.1 - communicationType = INFORMAL -> la notifica bonaria è presente
  @letturaDestinatario
  Scenario: [RICERCA_RICEVUTE_4] Come destinatario ricerco le notifiche ricevute di tipo INFORMAL e trovo la notifica bonaria
    Given destinatario della notifica bonaria: Mario Cucumber
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And destinatario della ricerca: Mario Cucumber
    And ricerca delle notifiche ricevute utilizzando i seguenti filtri:
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(+1D) |
      | communicationType | INFORMAL       |
    Then la ricerca delle notifiche ricevute ha esito positivo
    And l'elenco restituito contiene la notifica ricevuta


  #CASO DI TEST 4.1 - communicationType = LEGAL -> la notifica bonaria non è presente
  @letturaDestinatario
  Scenario: [RICERCA_RICEVUTE_5] Come destinatario ricerco le notifiche ricevute di tipo LEGAL e non trovo la notifica bonaria
    Given destinatario della notifica bonaria: Mario Cucumber
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And destinatario della ricerca: Mario Cucumber
    And ricerca delle notifiche ricevute utilizzando i seguenti filtri:
      | startDate         | $DATE_ADD(-1D) |
      | endDate           | $DATE_ADD(+1D) |
      | communicationType | LEGAL          |
    Then la ricerca delle notifiche ricevute ha esito positivo
    And l'elenco restituito non contiene la notifica ricevuta

  #CASO DI TEST 4.1 - communicationType assente -> di default vengono cercate solo le LEGAL
  @letturaDestinatario
  Scenario: [RICERCA_RICEVUTE_6] Come destinatario ricerco le notifiche ricevute senza specificare communicationType e di default non trovo la notifica bonaria
    Given destinatario della notifica bonaria: Mario Cucumber
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And destinatario della ricerca: Mario Cucumber
    And ricerca delle notifiche ricevute utilizzando i seguenti filtri:
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(+1D) |
    Then la ricerca delle notifiche ricevute ha esito positivo
    And l'elenco restituito non contiene la notifica ricevuta

  #CASO DI TEST 4.1 - solo campi obbligatori valorizzati correttamente
  @letturaDestinatario
  Scenario Outline: [RICERCA_RICEVUTE_2] Come destinatario <tipo> ricerco le notifiche ricevute con i soli filtri obbligatori
#    Given destinatario della notifica bonaria: <destinatario>
#    When viene inviata una nuova notifica bonaria
#    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And destinatario della ricerca: <destinatario>
    And la notifica può essere correttamente recuperata con una ricerca da "<destinatario>"
      | startDate | $DATE_ADD(-1D) |
      | endDate   | $DATE_ADD(+1D) |
    Then la ricerca delle notifiche ricevute ha esito positivo

    Examples:
      | tipo | destinatario   |
      | PF   | Mario Cucumber |
      | PG   | CucumberSpa    |

  #CASO DI TEST 4.1 - campo obbligatorio non valorizzato -> 400 KO
  @letturaDestinatario
  Scenario Outline: [RICERCA_RICEVUTE_3] Come destinatario non riesco a ricercare le notifiche ricevute se manca un campo obbligatorio
    And destinatario della ricerca: Mario Cucumber
    And l'utenza "Mario Cucumber" intraprende il flusso B2B
    And la notifica può essere correttamente recuperata con una ricerca da "Mario Cucumber"
      | <campo> | NULL |
    Then si riceve errore 400 nella ricerca delle notifiche ricevute
    Examples:
      | campo           |
      | xPagopaPnUid    |
      | xPagopaPnCxType |
      | xPagopaPnCxId   |
      | startDate       |
      | endDate         |






