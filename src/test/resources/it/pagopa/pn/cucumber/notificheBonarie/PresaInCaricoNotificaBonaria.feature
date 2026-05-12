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


#  ***   SCENARIO 1 - Preload del documento.



#  CASO DI TEST 1.1  Invio dei documenti allegati al destinatario.

  Scenario:[NOTIFICHE_BONARIE_PRELOAD_1] Come ente mittente creo un nuovo messaggio con valori di default.
  Includo nella notifica allegati di pagamento e documenti.
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | document | DOC_1_PG;DOC_2_PG;DOC_3_PG |
    And destinatario della notifica bonaria
      | recipient_type       | PF                    |
      | payment_multy_number | 3                     |
      | attachment_key       | classpath:/pagopa.pdf |
    When viene inviata una nuova notifica bonaria
    Then l'operazione non ha generato errori




#  *****SCENARIO 2 - Inserimento di un Messaggio.


#  CASO DI TEST 2.1  Creazione di nuovo un messaggio.

  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_06_1] Come ente mittente creo un nuovo messaggio con valori di default.
  Il messaggio creato è utilizzabile in una campagna per le notifiche bonarie.
    Given mittente della notifica bonaria: "COMUNE_1"
    When si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject                       |
      | Nuovo messaggio per notifiche bonarie |
    And l'operazione non ha generato errori
    Then tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie
    And l'operazione non ha generato errori


  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_06_2] Come ente mittente creo un nuovo messaggio con valori di default con seconda lingua non specificata
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | additional_language |
      | Messaggio bonario | NULL                |
    Then l'operazione non ha generato errori


  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_06_3] Come ente mittente creo un nuovo messaggio con valori di default con seconda lingua specificata
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | additional_language |
      | Messaggio bonario | FR                  |
    Then l'operazione non ha generato errori



#  CASO DI TEST 2.2  Errore sulla Creazione di un messaggio.



#  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_07_1] Come ente mittente non abilitato alla creazione di un messaggio ricevo un errore sulla creazione di un nuovo messaggio.
#    #todo t bonarie ente non abilitato



  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_07_2] Come ente mittente ricevo un errore sulla creazione di un nuovo messaggio non valorizzando campi obbligatori.
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | primary_long_body   | primary_short_body   |
      | <primary_subject> | <primary_long_body> | <primary_short_body> |
    Then si riceve errore 400
    Examples:
      | primary_subject | primary_long_body | primary_short_body |
      | NULL            |                   |                    |
      |                 | NULL              |                    |
      |                 |                   | NULL               |


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_07_3] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi in maniera non conforme.
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | primary_long_body   | primary_short_body   |
      | <primary_subject> | <primary_long_body> | <primary_short_body> |
    Then si riceve errore 400
    Examples:
      | primary_subject | primary_long_body | primary_short_body |
      | 257_CHAR        |                   |                    |
      |                 | 10001_CHAR        |                    |
      |                 |                   | 161_CHAR           |


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_07_4] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi addizionali in maniera non conforme.
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | additional_subject   | additional_long_body   | additional_short_body   |
      | <additional_subject> | <additional_long_body> | <additional_short_body> |
    Then si riceve errore 400
    Examples:
      | additional_subject | additional_long_body | additional_short_body |
      | 257_CHAR           |                      |                       |
      |                    | 10001_CHAR           |                       |
      |                    |                      | 161_CHAR              |


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_07_5] Come ente mittente ricevo un Errore sulla creazione di un nuovo messaggio compilando i campi relativi alla lingua in maniera non conforme.
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
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


#  *****  SCENARIO 3 - Lettura di un Messaggio.
#  CASO DI TEST 3.1  Recupero di un messaggio.
# implementato con [NOTIFICHE_BONARIE_MESSAGGI_06_1]


#  CASO DI TEST 3.2 Errore nel recupero di un messaggio.
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_09] Come ente mittente ricevo un Errore nel recuperare un messaggio con un id non valido.
    Given tento il recupero del messaggio per le comunicazioni bonarie con message id "<messageId>"
    Then si riceve errore 400
    Examples:
      | messageId |
      | ***Todo   |


#  CASO DI TEST 3.2 Errore nel recupero di un messaggio. Per assenza di permessi

#  CASO DI TEST 3.2 Errore nel recupero di un messaggio. Campi obbligatiro - soggetti a firma nel client




#  SCENARIO  - Sottomissione di una notifica bonaria.


#  CASO DI TEST .1 Validazione della richiesta di invio notifica bonaria.
  @informalNotifications
  Scenario: [NOTIFICHE_BONARIE_SM_01] Come ente mittente invio una notifica bonaria con modalità one-to-many quindi con id della campagna valorizzato
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    Then l'operazione non ha generato errori


  Scenario: [NOTIFICHE_BONARIE_SM_02] Come ente mittente invio una notifica bonaria con modalità one-to-one quindi con id messaggio, e seconda lingua specificata
    Given mittente della notifica bonaria: "COMUNE_1"
    #And viene creato un nuovo messaggio
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | messageId           | <ID_MSG> |
      | additional_language | FR       |
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    Then l'operazione non ha generato errori


  Scenario: [NOTIFICHE_BONARIE_SM_03] Come ente mittente invio una notifica bonaria con modalità one-to-one quindi con id messaggio, e seconda lingua NON specificata
    Given mittente della notifica bonaria: "COMUNE_1"
    #And viene creato un nuovo messaggio
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | messageId           | <ID_MSG_MONOLINGUA> |
      | additionalLanguages | NULL                |
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    Then l'operazione non ha generato errori


  Scenario: [NOTIFICHE_BONARIE_04] Come ente mittenste invio delle notifiche bonarie verso più destinatari con più pagamenti
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType        | PF                |
      | taxId                | FRMTTR76M06B715E  |
      | denomination         | Ettore Fieramosca |
      | payment_multy_number | 2                 |
    And destinatario della notifica bonaria
      | recipientType        | PG   |
      | taxId                | todo |
      | denomination         | todo |
      | payment_multy_number | 1    |
    When viene inviata una nuova notifica bonaria
    Then l'operazione non ha generato errori


  Scenario: [NOTIFICHE_BONARIE_SM_05] Come ente mittenste invio una notifica bonaria con un documento e più pagamenti
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | document | DOC_1_PG |
    And destinatario della notifica bonaria
      | recipientType        | PF                    |
      | taxId                | FRMTTR76M06B715E      |
      | denomination         | Ettore Fieramosca     |
      | payment_multy_number | 3                     |
      | attachment_key       | classpath:/pagopa.pdf |
    When viene inviata una nuova notifica bonaria
    Then l'operazione non ha generato errori



# ADD vas e lingue PG todo




#  CASO DI TEST .1 Mancata validazione della richiesta di invio notifica bonaria.



  Scenario: [NOTIFICHE_BONARIE_E01] Come Ente mittente non censito in whitelist invio una notifica bonaria e ricevo un errore.
    Given mittente della notifica bonaria: "COMUNE_2"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type         | PF                |
      | recipient_tax_id       | FRMTTR76M06B715E  |
      | recipient_denomination | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 403


  Scenario: [NOTIFICHE_BONARIE_E02] come ente mittente invio una notifica bonaria con un numero di allegati di pagamento superiore al massimo configurato e ricevo un errore,
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type         | PF                |
      | recipient_tax_id       | FRMTTR76M06B715E  |
      | recipient_denomination | Ettore Fieramosca |
      | payment_multy_number   | 3                 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_00] Invio bonaria con senderTaxId custom
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | senderTaxId        | 20517490320    |
      | senderDenomination | Comune di Test |
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_00] Invio bonaria verso tre destinatari - errore 400
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    And destinatario della notifica bonaria
      | recipient_type         | PF  |
      | recipient_tax_id       | *** |
      | recipient_denomination | *** |
    And destinatario della notifica bonaria
      | recipient_type         | PF  |
      | recipient_tax_id       | *** |
      | recipient_denomination | *** |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_00] Invio bonaria con lingua secondaria non supportata 1
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | additionalLanguages | XX |
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_00] Invio bonaria con 2 lingue secondarie 2
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | additionalLanguages | XX |
    And destinatario della notifica bonaria
      | recipient_type         | PF               |
      | recipient_tax_id       | FRMTTR76M06B715E |
      | recipient_denomination | Mario Cucumber   |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_00] Lingua secondaria non presente nella campagna
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId          | campaign-it-only |
      | additionalLanguages | FR               |
    And destinatario della notifica bonaria
      | recipient_type | PF |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_00] Verso PG con CF non conforme
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type   | PG          |
      | recipient_tax_id | ABCDEF12345 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_00] Verso PF con CF non conforme
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type   | PF     |
      | recipient_tax_id | ABC123 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


#  ADD :Invio con numero di pagamenti inclusi superiore al limite configurato.


#  Invio di una tipologia di file dichiarata (Content-Type) NON coerente con l'estensione o il formato del file effettivamente caricato sul sistema di storage (FileKey).

  Scenario: [NOTIFICHE_BONARIE_00] Invio con fileKey non coerente con contentType del pagamento
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type         | PF                |
      | recipient_tax_id       | FRMTTR76M06B715E  |
      | recipient_denomination | Ettore Fieramosca |
      | attachment_key         | FILE_NON_PDF.txt  |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400

  Scenario: [NOTIFICHE_BONARIE_00] Invio con fileKey non coerente con contentType del pagamento
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type         | PF                |
      | recipient_tax_id       | FRMTTR76M06B715E  |
      | recipient_denomination | Ettore Fieramosca |

      | attachment_contentType | application/xml   |
      | attachment_key         | FILE.pdf          |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


#  Invio con recapito fisico (indirizzo, civico, località, CAP, provincia, nazione) NON conforme agli standard di postalizzazione,
  #"PhysicalAddressValidationCharsValue": "\\u0020-\\u007E\\u00A0-\\u00FF",,"PhysicalAddressValidationValue": "true","PhysicalAddressValidationLength": "500",
  Scenario Outline: Validazione indirizzo fisico - errori formali
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type                | PF                |
      | recipient_tax_id              | FRMTTR76M06B715E  |
      | recipient_denomination        | Ettore Fieramosca |
      | physicalAddress               | SI                |
      | physical_address_address      | <address>         |
      | physical_address_details      | <details>         |
      | physical_address_zip          | <zip>             |
      | physical_address_municipality | <city>            |
      | physical_address_province     | RM                |
      | physical_address_state        | ITALIA            |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400
    Examples:
      | address      | details   | zip   | city |
      | Via Roma ### | scala A   | 00100 | Roma |
            # caratteri non validi
      | Via Roma     | scala 😃  | 00100 | Roma |
      | Via Roma     | dettaglio | ABCDE | Roma |
            # CAP non numerico
      | Via Roma     | dettaglio | 00100 | Rom@ |
            # caratteri speciali city
      | 501_CHAR     | dettaglio | 00100 | Roma |
            # lunghezza > 500





#  Invio di un indirizzo nazionale con il campo provincia mancante o nullo.
  Scenario: [NOTIFICHE_BONARIE_00] Invio con indirizzo nazionale senza provincia
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type            | PF                |
      | recipient_tax_id          | FRMTTR76M06B715E  |
      | recipient_denomination    | Ettore Fieramosca |
      | physicalAddress           | SI                |
      | physical_address_state    | ITALIA            |
      | physical_address_province | NULL              |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


#  Invio richiesta con campi Denominazione e Presso(AT) non conformi.

  Scenario Outline: Validazione denominazione e presso tramite regex
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type            | PF               |
      | recipient_tax_id          | FRMTTR76M06B715E |
      | recipient_denomination    | <denomination>   |
      | physicalAddress           | SI               |
      | physical_address_at       | <at>             |
      | physical_address_state    | ITALIA           |
      | physical_address_province | RM               |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400
    Examples:
      | denomination | at     |
      | TODO         | Valido |
      | Valido       | TODO   |
      | TODO         | TODO   |

  Scenario Outline: Validazione denominazione e presso con caratteri esclusi
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type            | PF               |
      | recipient_tax_id          | FRMTTR76M06B715E |
      | recipient_denomination    | <denomination>   |
      | physicalAddress           | SI               |
      | physical_address_at       | <at>             |
      | physical_address_state    | ITALIA           |
      | physical_address_province | RM               |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400
    Examples:
      | denomination   | at                |
      | Mario \| Rossi | Presso ufficio    |
      | Mario Rossi    | Presso \| Ufficio |
      | Mario\|Rossi   | Presso \| Ufficio |

  Scenario: Validazione denominazione troppo lunga
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipient_type            | PF               |
      | recipient_tax_id          | FRMTTR76M06B715E |
      | recipient_denomination    | 89_CHAR          |
      | physicalAddress           | SI               |
      | physical_address_at       | Presso           |
      | physical_address_state    | ITALIA           |
      | physical_address_province | RM               |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400

#  Invio di risorsa (documento principale e avvisi di pagamento) duplicata.

#  Campi obbligatori mancanti.




  Scenario: [NOTIFICHE_BONARIE_00] Invio bonaria con pagamento senza allegato
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType        | PF                 |
      | taxId                | FRMTTR76M06B715E   |
      | denomination         | Ettore Fieramosca  |
      | payment_noticeCode   | 302000000000000001 |
      | payment_multy_number | 2                  |
      | attachment_sha256    | NULL               |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400




#  SCENARIO 5 - Download dei documenti.

#  CASO DI TEST 5.1 Corretto Download dei documenti notificati.
  Scenario: [NOTIFICHE_BONARIE_00] Recupero allegato pagamento - OK
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    And si tenta il recupero allegato pagamento della notifica bonaria
    Then l'operazione non ha generato errori


#  CASO DI TEST 5.3 Errore Download dei documenti.

  Scenario: [NOTIFICHE_BONARIE_00] Recupero allegato pagamento PA diversa
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    Given mittente della notifica bonaria: "COMUNE_2"
    When si tenta il recupero allegato pagamento della notifica bonaria
    Then si riceve errore 403



    When si tenta il recupero allegato pagamento con IUN "fake"
    Then si riceve errore 404


    When si tenta il recupero allegato pagamento con recipient 0 e attachment 5
    Then si riceve errore 404


    When si tenta il recupero allegato pagamento con recipient 5 e attachment 0
    Then si riceve errore 404




#  CASO DI TEST 5.2 Corretto Download degli allegati di pagamento di una notifica.


  Scenario: [NOTIFICHE_BONARIE_00] Recupero documento bonaria - OK
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    And si tenta il recupero documento della notifica bonaria
    Then l'operazione non ha generato errori


#  CASO DI TEST 5.4 Errore Download degli allegati di pagamento.



  Scenario: [NOTIFICHE_BONARIE_00] Recupero documento con indice non valido
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    When si tenta il recupero documento con indice 5
    Then si riceve errore 404


  Scenario: [NOTIFICHE_BONARIE_00] Recupero documento con IUN inesistente
    Given mittente della notifica bonaria: "COMUNE_1"
    When si tenta il recupero documento con IUN "fake"
    Then si riceve errore 404


  Scenario: [NOTIFICHE_BONARIE_00] Recupero documento con PA non autorizzata
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria

    Given mittente della notifica bonaria: "COMUNE_2"
    When si tenta il recupero documento della notifica bonaria
    Then si riceve errore 403


 #  SCENARIO 6 - Stato della notifica.


  Scenario: [NOTIFICHE_BONARIE_00] Verifica stato richiesta bonaria - OK
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    And si verifica lo stato della richiesta di notifica bonaria
    Then l'operazione non ha generato errori


  Scenario: [NOTIFICHE_BONARIE_00] Verifica stato con parametri incoerenti
    Given mittente della notifica bonaria: "COMUNE_1"
    When si verifica lo stato con requestId e protocollo insieme
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_00] Verifica stato con PA non autorizzata
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene inviata una notifica bonaria valida

    Given mittente della notifica bonaria: "COMUNE_2"
    When si verifica lo stato della richiesta di notifica bonaria
    Then si riceve errore 403


  Scenario: [NOTIFICHE_BONARIE_00] Verifica stato con requestId inesistente
    Given mittente della notifica bonaria: "COMUNE_1"
    When si verifica lo stato della richiesta di notifica bonaria con id "fake"
    Then si riceve errore 404


