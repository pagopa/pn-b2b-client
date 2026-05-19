Feature: Sottomissione di una notifica bonaria.


  Scenario: [NOTIFICHE_BONARIE_TEST_01] Solo per Testing -> Creazione di un messaggio tutti i campi compilati
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject             | primary_long_body                       | primary_short_body                      | primary_language | additional_subject | additional_long_body                    |
      | Sollecito di pagamento 2023 | Gentile cittadino, la informiamo che... | Sollecito: hai una nuova comunicazione. | it               | Sollecito.         | Gentile cittadino, la informiamo che... |


  Scenario: [NOTIFICHE_BONARIE_TEST_02] Solo per Testing -> Invio di una notifica bonaria tutti i campi compilati
    Given viene creata una nuova notifica bonaria con i seguenti parametri
      | senderDenomination | senderTaxId | paProtocolNumber | idempotenceToken | campaignId | messageId                            | subject        | additional_language | recipient_type | recipient_tax_id | recipient_denomination | pec_address       | pagopa_notice_code | pagopa_creditor_tax_id | document_title      | document_docidx | group      |
      | Comune di Milano   | 77777777777 | PROT-123         | TOKEN-ABC        | CAMP-001   | 3fa85f64-5717-4562-b3fc-2c963f66afa6 | Sollecito Tari | fr                  | PF             | 57143494439      | Mario Rossi            | account@domain.it | 302000100000019421 | 77777777777            | Avviso di pagamento | 1               | TEST-GROUP |
    Then viene inviata una nuova notifica bonaria


#  SCENARIO  - Sottomissione di una notifica bonaria.
#  CASO DI TEST .1 Validazione della richiesta di invio notifica bonaria.
  @informalNotifications
  Scenario: [NOTIFICHE_BONARIE_SOTTOMISSIONE_14A] Come ente mittente creo una nuova notifica bonaria utilizzando valori di default.
  Con whitelist vuota ricevo un errore 403 Forbidden.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    When viene creata una nuova notifica bonaria con i seguenti parametri
      | additionalLanguages | campaignId     |
      | NULL                | campaign-1-uat |
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    Then viene inviata una nuova notifica bonaria
    Then si riceve errore 403




#//////////////////// INIZIO TEST PROGETTTI////////////////
#
# ////////////////////////////////////////////////////////////




#************************************************
#  *****SCENARIO 1 - Preload del documento. OK
# ***********************************************

#  CASO DI TEST 1.1  Invio dei documenti allegati al destinatario.

  Scenario:[NOTIFICHE_BONARIE_PRELOAD_1] Come ente mittente effettuo il preload dei documenti.
  Includo nella notifica allegati di pagamento e documenti.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | document | DOC_1_PG;DOC_2_PG;DOC_3_PG |
    And destinatario della notifica bonaria
      | recipient_type       | PF                    |
      | payment_multy_number | 3                     |
      | attachment_key       | classpath:/pagopa.pdf |
    When viene inviata una nuova notifica bonaria
    Then l'operazione non ha generato errori



#************************************************
#  *****SCENARIO 2 - Creazione di un Messaggio.
# ***********************************************


#              CASO DI TEST 2.1  Creazione di nuovo un messaggio.


  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_02_1_A] Come ente mittente creo un nuovo messaggio con valori di default.
  Il messaggio creato è utilizzabile in una campagna per le notifiche bonarie.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    When si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject                       |
      | Nuovo messaggio per notifiche bonarie |
    And l'operazione sul messaggio utile per le bonarie è andata a buon fine
    Then tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie
    And l'operazione sul messaggio utile per le bonarie è andata a buon fine


  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_02_1_B] Come ente mittente creo un nuovo messaggio con valori di default con seconda lingua non specificata
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | additional_language |
      | Messaggio bonario | NULL                |
    Then l'operazione sul messaggio utile per le bonarie è andata a buon fine


  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_02_1_C] Come ente mittente creo un nuovo messaggio con valori di default con seconda lingua specificata
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | additional_language |
      | Messaggio bonario | FR                  |
    Then l'operazione sul messaggio utile per le bonarie è andata a buon fine



#            CASO DI TEST 2.2  Errore sulla Creazione di un messaggio.



#  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_07_1] Come ente mittente non abilitato alla creazione di un messaggio ricevo un errore sulla creazione di un nuovo messaggio.
  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_02_1_A] Come ente mittente non abilitato alla creazione di un messaggio tento di crearlo con valori di default.
    Given mittente della notifica bonaria: "COMUNE_2"
    When si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject                       |
      | Nuovo messaggio per notifiche bonarie |
    Then si riceve errore 403


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_2_B] Come ente mittente ricevo un errore sulla creazione di un nuovo messaggio non valorizzando campi obbligatori.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | primary_long_body   | primary_short_body   |
      | <primary_subject> | <primary_long_body> | <primary_short_body> |
    Then si riceve errore 400
    Examples:
      | primary_subject | primary_long_body | primary_short_body |
      | NULL            |                   |                    |
      |                 | NULL              |                    |
      |                 |                   | NULL               |


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_2_C] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi in maniera non conforme.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | primary_long_body   | primary_short_body   |
      | <primary_subject> | <primary_long_body> | <primary_short_body> |
    Then si riceve errore 400
    Examples:
      | primary_subject | primary_long_body | primary_short_body |
      | 257_CHAR        |                   |                    |
      |                 | 10001_CHAR        |                    |
      |                 |                   | 161_CHAR           |


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_2_D] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi addizionali in maniera non conforme.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | additional_subject   | additional_long_body   | additional_short_body   |
      | <additional_subject> | <additional_long_body> | <additional_short_body> |
    Then si riceve errore 400
    Examples:
      | additional_subject | additional_long_body | additional_short_body |
      | 257_CHAR           |                      |                       |
      |                    | 10001_CHAR           |                       |
      |                    |                      | 161_CHAR              |


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_2_E] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi relativi alla lingua in maniera non conforme.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_language   | additional_language   |
      | <primary_language> | <additional_language> |
    Then si riceve errore 400
    Examples:
      | primary_language | additional_language |
      | NULL             |                     |
      | fr               |                     |
      |                  | xx                  |
      | xx               |                     |
      | it               | it                  |



#************************************************
#  *****SCENARIO 3 - Lettura di un Messaggio.
# ***********************************************


#               CASO DI TEST 3.1  Recupero di un messaggio.
#               implementato con [NOTIFICHE_BONARIE_MESSAGGI_02_1_A]




                #  CASO DI TEST 3.2 Errore nel recupero di un messaggio.


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_03_2_A] Come ente mittente ricevo un Errore nel recuperare un messaggio con un id non valido.
    Given tento il recupero del messaggio per le comunicazioni bonarie con message id "<messageId>"
    Then si riceve errore 400
    Examples:
      | messageId |
      | ***Todo   |


  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_03_2_B] Come ente mittente creo un nuovo messaggio e tento il recupero tramite diverso ente.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    When si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject                       |
      | Nuovo messaggio per notifiche bonarie |
    And l'operazione sul messaggio utile per le bonarie è andata a buon fine
    Then tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie
    And l'operazione sul messaggio utile per le bonarie è andata a buon fine
    Then mittente della notifica bonaria: "COMUNE_2"
    And tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie
    Then si riceve errore 403



#  CASO DI TEST 3.2 Errore nel recupero di un messaggio. Campi obbligatiro - soggetti a firma nel client



# ************************************************
#  *****SCENARIO 5  - Download dei documenti.
# ***********************************************


#  CASO DI TEST 5.1 Corretto Download dei documenti notificati.


  Scenario: [NOTIFICHE_BONARIE_05_1] Come ente mittente Recupero i documenti di una notifica bonaria
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    And si tenta il recupero documento della notifica bonaria
    Then il download risulta correttamente effettuato


    #  CASO DI TEST 5.2 Corretto Download degli allegati di pagamento di una notifica.

  Scenario: [NOTIFICHE_BONARIE_05_2] Come ente mittente Recupero l'allegato di pagamento di una notifica bonaria
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    And si tenta il recupero allegato pagamento della notifica bonaria
    Then il download risulta correttamente effettuato




#  CASO DI TEST 5.3 Errore Download dei documenti.

  Scenario: [NOTIFICHE_BONARIE_05_2_A] Rcome ente mittente tento il recupero del documento di una notifica non inviata da me ricevendo un errore
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    Given mittente della notifica bonaria: "COMUNE_2"
    And si tenta il recupero documento della notifica bonaria
    Then si riceve errore 403


  Scenario: [NOTIFICHE_BONARIE_05_2_B] Come ente mittente tento il Recupero del documento con indice non valido ricevendo errore
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    When si tenta il recupero documento con indice 5
    Then si riceve errore 404


  Scenario: [NOTIFICHE_BONARIE_05_2_C] Come ente mittente tento il Recupero del documento con IUN non valido ricevendo errore
    Given mittente della notifica bonaria: "COMUNE_1"
    When si tenta il recupero documento con IUN "fake"
    Then si riceve errore 404




      #  CASO DI TEST 5.4 Errore Download degli allegati di pagamento.



  Scenario: [NOTIFICHE_BONARIE_05_3_A] Come ente mittente tento il Recupero del allegato di pagamento con iun non valido ricevendo errore
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    When si tenta il recupero allegato pagamento con IUN "fake"
    Then si riceve errore 404


  Scenario: [NOTIFICHE_BONARIE_05_3_B] Come ente mittente tento il Recupero del allegato di pagamento con indice non valido ricevendo errore
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    When si tenta il recupero allegato pagamento con recipient 0 e attachment 5
    Then si riceve errore 404



  Scenario: [NOTIFICHE_BONARIE_05_3_C] Come ente mittente tento il Recupero del allegato di pagamento con indice non valido ricevendo errore
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    When si tenta il recupero allegato pagamento con recipient 5 e attachment 0
    Then si riceve errore 404

