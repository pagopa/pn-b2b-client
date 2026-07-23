Feature: Messaggi e allegati di una notifica bonaria.


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

  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_02_1_A] Come ente mittente creo un nuovo messaggio con valori di default.
  Il messaggio creato è utilizzabile in una campagna per le notifiche bonarie.
    Given mittente della notifica bonaria: "Comune_Multi"
    When si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject                       |
      | Nuovo messaggio per notifiche bonarie |
    And l'operazione è andata a buon fine
    Then tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie
    And l'operazione è andata a buon fine

  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_02_1_B] Come ente mittente creo un nuovo messaggio con valori di default con seconda lingua non specificata
    Given mittente della notifica bonaria: "Comune_Multi"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | additional_language |
      | Messaggio bonario | NULL                |
    Then l'operazione è andata a buon fine

  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_02_1_C] Come ente mittente creo un nuovo messaggio con valori di default con seconda lingua specificata
    Given mittente della notifica bonaria: "Comune_Multi"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | additional_language |
      | Messaggio bonario | FR                  |
    Then l'operazione è andata a buon fine

  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_02_1_D] Come ente mittente creo un nuovo messaggio con valori di default con seconda lingua non specificata
    Given mittente della notifica bonaria: "Comune_Multi"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | additional_language | additional_short_body | additional_long_body | additional_subject |
      | Messaggio bonario | FR                  | Testo short add       | testo long add       | subj add           |
    Then tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie
    Then l'operazione è andata a buon fine

  @informalNotificationsMessageAttachment
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_1_D2] Come ente mittente creo un nuovo messaggio con campi addizionali a null, ricevo errore.
    Given mittente della notifica bonaria: "Comune_Multi"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | additional_subject   | additional_long_body   | additional_short_body   |
      | <additional_subject> | <additional_long_body> | <additional_short_body> |
    Then si riceve errore 400
    Examples:
      | additional_subject | additional_long_body | additional_short_body |
      | NULL               | Add long             | Add short             |
      | Add sub            | NULL                 | Add short             |
      | Add sub            | Add long             | NULL                  |


#            CASO DI TEST 2.2  Errore sulla Creazione di un messaggio.


#  @informalNotificationsMessageAttachment tutte le pa sono abilitate
#  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_02_1_A1] Come ente mittente non abilitato alla creazione di un messaggio tento di crearlo con valori di default.
#    Given mittente della notifica bonaria: "Comune_2"
#    When si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
#      | primary_subject                       |
#      | Nuovo messaggio per notifiche bonarie |
#    Then si riceve errore 403

  @informalNotificationsMessageAttachment
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_2_B] Come ente mittente ricevo un errore sulla creazione di un nuovo messaggio non valorizzando campi obbligatori.
    Given mittente della notifica bonaria: "Comune_Multi"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | primary_long_body   | primary_short_body   |
      | <primary_subject> | <primary_long_body> | <primary_short_body> |
    Then si riceve errore 400
    Examples:
      | primary_subject | primary_long_body | primary_short_body |
      | NULL            |                   |                    |
      |                 | NULL              |                    |


  @informalNotificationsMessageAttachment
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_2_C] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi in maniera non conforme.
    Given mittente della notifica bonaria: "Comune_Multi"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | primary_long_body   | primary_short_body   |
      | <primary_subject> | <primary_long_body> | <primary_short_body> |
    Then si riceve errore 400
    Examples:
      | primary_subject | primary_long_body | primary_short_body |
      | 257_CHAR        |                   |                    |
      |                 | 10001_CHAR        |                    |
      |                 |                   | 161_CHAR           |

  @informalNotificationsMessageAttachment
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_2_D] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi addizionali in maniera non conforme.
    Given mittente della notifica bonaria: "Comune_Multi"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | additional_subject   | additional_long_body   | additional_short_body   |
      | <additional_subject> | <additional_long_body> | <additional_short_body> |
    Then si riceve errore 400
    Examples:
      | additional_subject | additional_long_body | additional_short_body |
      | 257_CHAR           |                      |                       |
      |                    | 10001_CHAR           |                       |
      |                    |                      | 161_CHAR              |

  @informalNotificationsMessageAttachment
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_2_E] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi relativi alla lingua in maniera non conforme.
    Given mittente della notifica bonaria: "Comune_Multi"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_language   |
      | <primary_language> |
    Then si riceve errore 400
    Examples:
      | primary_language |
      | NULL             |
      | fr               |
      | xx               |
      | FRA              |

  @informalNotificationsMessageAttachment
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_02_2_EA] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi relativi alla lingua in maniera non conforme.
    Given mittente della notifica bonaria: "Comune_Multi"
    Then si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | additional_subject | additional_long_body | additional_short_body | additional_language   |
      | Add Sub            | Add LB               | Add SB                | <additional_language> |
    Then si riceve errore 400
    Examples:
      | additional_language |
      | NULL                |
      | FRA                 |
      | xx                  |
      | @                   |
      | IT                  |



#************************************************
#  *****SCENARIO 3 - Lettura di un Messaggio.
# ***********************************************


#               CASO DI TEST 3.1  Recupero di un messaggio.
#               implementato con [NOTIFICHE_BONARIE_MESSAGGI_02_1_A]




                #  CASO DI TEST 3.2 Errore nel recupero di un messaggio.

  #@informalNotificationsMessageAttachment il client dell api accetta solo uuid quindi non si può passare malformato
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_03_2_A] Come ente mittente ricevo un Errore nel recuperare un messaggio con un id non valido.
    Given mittente della notifica bonaria: "Comune_Multi"
    Then tento il recupero del messaggio per le comunicazioni bonarie con message id "<messageId>"
    Then si riceve errore 400
    Examples:
      | messageId |
      | @         |

  @informalNotificationsMessageAttachment
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_03_2_A2] Come ente mittente ricevo un Errore nel recuperare un messaggio con un id non censito.
    Given mittente della notifica bonaria: "Comune_Multi"
    Then tento il recupero del messaggio per le comunicazioni bonarie con message id "<messageId>"
    Then si riceve errore 404
    Examples:
      | messageId                            |
      | d9d7545c-fa98-4e0e-8900-b4d3e6923015 |

  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_03_2_B] Come ente mittente creo un nuovo messaggio e tento il recupero tramite diverso ente non attivo.
    Given mittente della notifica bonaria: "Comune_Multi"
    When si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject                       |
      | Nuovo messaggio per notifiche bonarie |
    And l'operazione è andata a buon fine
    Then tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie
    And l'operazione è andata a buon fine
    Then mittente della notifica bonaria: "Comune_Root"
    And il recupero del messaggio per le comunicazioni bonarie fallisce con errore "403"

  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_03_2_B2] Come ente mittente creo un nuovo messaggio e tento il recupero tramite diverso ente attivo.
    Given mittente della notifica bonaria: "Comune_Multi"
    When si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject                       |
      | Nuovo messaggio per notifiche bonarie |
    And l'operazione è andata a buon fine
    Then tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie
    And l'operazione è andata a buon fine
    Then mittente della notifica bonaria: "Comune_1"
    And il recupero del messaggio per le comunicazioni bonarie fallisce con errore "403"



# ************************************************
#  *****SCENARIO 5  - Download dei documenti.
# ***********************************************


#  CASO DI TEST 5.1 Corretto Download dei documenti notificati.

  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_05_1] Come ente mittente Recupero i documenti di una notifica bonaria
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si tenta il recupero documento della notifica bonaria
    Then il download risulta correttamente effettuato


    #  CASO DI TEST 5.2 Corretto Download degli allegati di pagamento di una notifica.
  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_05_4_] Come ente mittente Recupero l'allegato di pagamento di una notifica bonaria
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And si tenta il recupero allegato pagamento della notifica bonaria
    Then il download risulta correttamente effettuato




#  CASO DI TEST 5.3 Errore Download dei documenti.
  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_05_3_A] come ente mittente tento il recupero del documento di una notifica non inviata da me ricevendo un errore
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Given mittente della notifica bonaria: "Comune_1"
    And si tenta il recupero documento della notifica bonaria
    Then si riceve errore 404

  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_05_3_A1] come ente mittente tento il recupero del documento di una notifica non inviata da me ricevendo un errore
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Given mittente della notifica bonaria: "Comune_Root"
    And si tenta il recupero documento della notifica bonaria
    Then si riceve errore 404

  #@informalNotificationsMessageAttachment errore già noto con bug PN-20078
  Scenario: [NOTIFICHE_BONARIE_05_3_B] Come ente mittente tento il Recupero del documento con indice non valido ricevendo errore
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    When si tenta il recupero documento con indice 5
    Then si riceve errore 404

  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_05_3_C] Come ente mittente tento il Recupero del documento con IUN non valido ricevendo errore
    Given mittente della notifica bonaria: "Comune_Multi"
    When si tenta il recupero documento con IUN "fake"
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER_SIZE"




      #  CASO DI TEST 5.4 Errore Download degli allegati di pagamento.


  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_05_4_A] Come ente mittente tento il Recupero del allegato di pagamento con iun non valido ricevendo errore
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    When si tenta il recupero allegato pagamento con IUN "fake"
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER_SIZE"

  #@informalNotificationsMessageAttachment errore già noto con bug PN-20078
  Scenario: [NOTIFICHE_BONARIE_05_4_B] Come ente mittente tento il Recupero del allegato di pagamento con indice non valido ricevendo errore
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    When si tenta il recupero allegato pagamento con recipient 0 e attachment 5
    Then si riceve errore 404


  @informalNotificationsMessageAttachment
  Scenario: [NOTIFICHE_BONARIE_05_4_C] Come ente mittente tento il Recupero del allegato di pagamento con indice non valido ricevendo errore
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    When si tenta il recupero allegato pagamento con recipient 5 e attachment 0
    Then si riceve errore 404



    # DOWNLOAD ALLEGATI LATO DESTINATARIO

  @informalNotificationsMessageAttachment @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_DL_DESTINATARIO_1] Come ente mittente Recupero l'allegato di pagamento di una notifica bonaria
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And il destinatario tenta il recupero dell'allegato pagamento della notifica bonaria
    Then il download del destinatario risulta correttamente effettuato
    And verifico la presenza di un audit log su "/aws/ecs/pn-commons" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_ATCHOPEN_RCP |

  @informalNotificationsMessageAttachment @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_DL_DESTINATARIO_2] Come ente mittente Recupero l'allegato di pagamento di una notifica bonaria
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${IT}             |
    When viene inviata una nuova notifica bonaria
    And si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And il destinatario tenta il recupero del documento della notifica bonaria
    Then il download del destinatario risulta correttamente effettuato
    And verifico la presenza di un audit log su "/aws/ecs/pn-commons" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_DOCOPEN_RCP |