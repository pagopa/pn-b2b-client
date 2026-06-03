Feature: Sottomissione di una notifica bonaria.


  Scenario: [NOTIFICHE_BONARIE_TEST_01] Solo per Testing -> Creazione di un messaggio tutti i campi compilati
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject             | primary_long_body                       | primary_short_body                      | primary_language | additional_subject | additional_long_body                    |
      | Sollecito di pagamento 2023 | Gentile cittadino, la informiamo che... | Sollecito: hai una nuova comunicazione. | it               | Sollecito.         | Gentile cittadino, la informiamo che... |


  Scenario: [NOTIFICHE_BONARIE_TEST_02] Solo per Testing -> Invio di una notifica bonaria tutti i campi compilati
    Given viene creata una nuova notifica bonaria con i seguenti parametri
      | senderDenomination | senderTaxId | paProtocolNumber | idempotenceToken | campaignId | messageId                            | subject        | additional_language | recipientType | taxId       | denomination | pec_address       | pagopa_notice_code | pagopa_creditor_tax_id | document_title      | document_docidx | group      |
      | Comune di Milano   | 77777777777 | PROT-123         | TOKEN-ABC        | CAMP-001   | 3fa85f64-5717-4562-b3fc-2c963f66afa6 | Sollecito Tari | fr                  | PF            | 57143494439 | Mario Rossi  | account@domain.it | 302000100000019421 | 77777777777            | Avviso di pagamento | 1               | TEST-GROUP |
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


#***************************************************
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_CHECK    |
      | param2 | recIndex=0       |
      | pippo  | phase=VALIDATION |
 # Se nell'audit log è presente lo IUN, lascia iun | auto
#  le chiavi successive le puoi chiamare come ti pare, è del tutto ininfluente.
#  sono i valori che contano.
#  Ognuno di quelli corrisponde a una stringa che imposti nel filtro di ricerca degli audit log


    And verifico che su DynamoDB è presente in timeline l'elemento "NOTIFICATION_COST_VALIDATION_RESPONSE"

#  QueryResponse queryResponse = dynamoDbService.call(DynamoTableName.TIMELINE, Map.of(
#  ":v_iun", AttributeValue.builder().s(sharedSteps.getNotificationIun()).build(),
#  ":v_category", AttributeValue.builder().s(timelineElement).build()
#  ));
#**************************************************************************************************************

# ************************************************
#  *****SCENARIO   - Sottomissione di una notifica bonaria..
# ***********************************************


#  CASO DI TEST .1 Validazione della richiesta di invio notifica bonaria.

  Scenario: [NOTIFICHE_BONARIE_SM_01_1_A] Come ente mittente invio una notifica bonaria con modalità one-to-many quindi con id della campagna valorizzato
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | FRMTTR76M06B715E                     |
      | denomination  | Ettore Fieramosca                    |
      | messageId     | d9d7545c-fa98-4e0e-8900-b4d3e6923015 |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine
    And verifico che su DynamoDB è presente in timeline l'elemento "REQUEST_ACCEPTED"


  Scenario: [NOTIFICHE_BONARIE_SM_01_1_B] Come ente mittente invio una notifica bonaria con modalità one-to-one quindi con id messaggio, e seconda lingua specificata
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | FRMTTR76M06B715E                     |
      | denomination  | Ettore Fieramosca                    |
      | messageId     | 543638e2-7fc5-4cf8-b1d4-52921c8c398b |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine


  Scenario: [NOTIFICHE_BONARIE_SM_01_1_C] Come ente mittente invio una notifica bonaria con modalità one-to-one quindi con id messaggio, e seconda lingua NON specificata
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId          | campaign-1 |
      | additionalLanguages | NULL       |
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | FRMTTR76M06B715E                     |
      | denomination  | Ettore Fieramosca                    |
      | messageId     | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine


  Scenario: [NOTIFICHE_BONARIE_SM_01_1_D] Come ente mittenste invio delle notifiche bonarie verso più destinatari con più pagamenti
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType        | PF                                   |
      | taxId                | FRMTTR76M06B715E                     |
      | denomination         | Ettore Fieramosca                    |
      | payment_multy_number | 2                                    |
      | messageId            | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    And destinatario della notifica bonaria
      | denomination         | Vita Nova Sas                        |
      | recipientType        | PG                                   |
      | taxId                | 12666810299                          |
      | payment_multy_number | 1                                    |
      | messageId            | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine


  Scenario: [NOTIFICHE_BONARIE_SM_01_1_E] Come ente mittenste invio una notifica bonaria con un documento e più pagamenti
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | document   | DOC_1_PG   |
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType        | PF                    |
      | taxId                | FRMTTR76M06B715E      |
      | denomination         | Ettore Fieramosca     |
      | payment_multy_number | 3                     |
      | attachment_key       | classpath:/pagopa.pdf |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine



# ADD vas e lingue PG todo




#  CASO DI TEST .1 Mancata validazione della richiesta di invio notifica bonaria.



  Scenario: [NOTIFICHE_BONARIE_SM_04_2_A] Come Ente mittente non censito in whitelist invio una notifica bonaria e ricevo un errore.
    Given mittente della notifica bonaria: "COMUNE_2"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | FRMTTR76M06B715E                     |
      | denomination  | Ettore Fieramosca                    |
      | messageId     | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 403


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_B] come ente mittente invio una notifica bonaria con un numero di allegati di pagamento superiore al massimo configurato e ricevo un errore,
  Nome Parametro: PN_DELIVERY_INFORMALNOTIFICATIONMAXPAYMENTS
  Tipo parametro: Configurazione dichiarata nel file application-<env>.env
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType        | PF                                   |
      | taxId                | FRMTTR76M06B715E                     |
      | denomination         | Ettore Fieramosca                    |
      | payment_multy_number | 3                                    |
      | messageId            | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_C] Invio bonaria con senderTaxId custom
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | senderTaxId        | 20517490320    |
      | senderDenomination | Comune di Test |
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | FRMTTR76M06B715E                     |
      | denomination  | Ettore Fieramosca                    |
      | messageId     | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_D] Invio bonaria verso tre destinatari - errore 400
  Nome Parametro Max numero destinatari: PN_DELIVERY_INFORMALNOTIFICATIONMAXRECIPIENTS.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | FRMTTR76M06B715E                     |
      | denomination  | Ettore Fieramosca                    |
      | messageId     | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    And destinatario della notifica bonaria
      | denomination         | Vita Nova Sas                        |
      | recipientType        | PG                                   |
      | taxId                | 12666810299                          |
      | payment_multy_number | 1                                    |
      | messageId            | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | GLLGLL64B15G702I                     |
      | denomination  | Galileo Galilei                      |
      | messageId     | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_E] Invio bonaria con lingua secondaria non supportata 1
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | additionalLanguages | XXX |
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | FRMTTR76M06B715E                     |
      | denomination  | Ettore Fieramosca                    |
      | messageId     | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_F] Invio bonaria con 2 lingue secondarie 2
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | additionalLanguages | XX |
    And destinatario della notifica bonaria
      | recipientType | PF               |
      | taxId         | FRMTTR76M06B715E |
      | denomination  | Mario Cucumber   |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_G] Lingua secondaria non presente nella campagna
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId          | campaign-pec |
      | additionalLanguages | FR           |
    And destinatario della notifica bonaria
      | recipientType   | PF                                   |
      | taxId           | FRMTTR76M06B715E                     |
      | denomination    | Ettore Fieramosca                    |
      | messageId       | 439cd04a-f903-4cc7-b869-3eca50b68887 |
      | digitalDomicile | testpagopa1@pec.pagopa.it            |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_H] Verso PG con CF non conforme
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PG                                   |
      | taxId         | ABCDEF12345                          |
      | messageId     | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_I] Verso PF con CF non conforme
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | ABCDEF12345                          |
      | messageId     | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_L] Invio con fileKey non coerente con contentType del pagamento
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType          | PF                                   |
      | taxId                  | FRMTTR76M06B715E                     |
      | denomination           | Ettore Fieramosca                    |
      | attachment_key         | FILE_NON_PDF.txt                     |
      | attachment_contentType | application/pdf                      |
      | messageId              | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400

  Scenario: [NOTIFICHE_BONARIE_SM_04_2_M] Invio con fileKey non coerente con contentType del pagamento
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType          | PF                                   |
      | taxId                  | FRMTTR76M06B715E                     |
      | denomination           | Ettore Fieramosca                    |
      | attachment_key         | classpath:/sample.txt                |
      | attachment_contentType | application/pdf                      |
      | messageId              | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


#  Invio con recapito fisico (indirizzo, civico, località, CAP, provincia, nazione) NON conforme agli standard di postalizzazione,
  #"PhysicalAddressValidationCharsValue": "\\u0020-\\u007E\\u00A0-\\u00FF",,"PhysicalAddressValidationValue": "true","PhysicalAddressValidationLength": "500",
  Scenario Outline: [NOTIFICHE_BONARIE_SM_04_2_N] Validazione indirizzo fisico - errori formali
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType             | PF                                   |
      | taxId                     | FRMTTR76M06B715E                     |
      | denomination              | Ettore Fieramosca                    |
      | physicalAddress           | SI                                   |
      | physical_address_address  | <address>                            |
      | physical_address_details  | <details>                            |
      | physical_address_zip      | <zip>                                |
      | physical_address_city     | <city>                               |
      | physical_address_province | RM                                   |
      | physical_address_state    | ITALIA                               |
      | messageId                 | 0327e9fc-d571-4401-97b5-175b70be01a1 |
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



  Scenario: [NOTIFICHE_BONARIE_SM_04_2_O] Invio con indirizzo nazionale senza provincia
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType             | PF                                   |
      | taxId                     | FRMTTR76M06B715E                     |
      | denomination              | Ettore Fieramosca                    |
      | physicalAddress           | SI                                   |
      | physical_address_state    | ITALIA                               |
      | physical_address_province | NULL                                 |
      | messageId                 | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400


#  Invio richiesta con campi Denominazione e Presso(AT) non conformi.

  Scenario Outline: [NOTIFICHE_BONARIE_SM_04_2_P]Validazione denominazione e presso tramite regex
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType             | PF                                   |
      | taxId                     | FRMTTR76M06B715E                     |
      | denomination              | <denomination>                       |
      | physicalAddress           | SI                                   |
      | physical_address_at       | <at>                                 |
      | physical_address_state    | ITALIA                               |
      | physical_address_province | RM                                   |
      | messageId                 | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400
    Examples:
      | denomination | at     |
      | TODO         | Valido |
      | Valido       | TODO   |
      | TODO         | TODO   |

  Scenario Outline: [NOTIFICHE_BONARIE_SM_04_2_Q] Validazione denominazione e presso con caratteri esclusi
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType             | PF                                   |
      | taxId                     | FRMTTR76M06B715E                     |
      | denomination              | <denomination>                       |
      | physicalAddress           | SI                                   |
      | physical_address_at       | <at>                                 |
      | physical_address_state    | ITALIA                               |
      | physical_address_province | RM                                   |
      | messageId                 | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400
    Examples:
      | denomination   | at                |
      | Mario \| Rossi | Presso ufficio    |
      | Mario Rossi    | Presso \| Ufficio |
      | Mario\|Rossi   | Presso \| Ufficio |

  Scenario: [NOTIFICHE_BONARIE_SM_04_2_R] Validazione denominazione troppo lunga
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType             | PF                                   |
      | taxId                     | FRMTTR76M06B715E                     |
      | denomination              | 89_CHAR                              |
      | physicalAddress           | SI                                   |
      | physical_address_at       | Presso                               |
      | physical_address_state    | ITALIA                               |
      | physical_address_province | RM                                   |
      | messageId                 | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400

#  Invio di risorsa (documento principale e avvisi di pagamento) duplicata.

#  Campi obbligatori mancanti.


  Scenario: [NOTIFICHE_BONARIE_SM_04_2_S] Invio bonaria con pagamento senza allegato
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType        | PF                                   |
      | taxId                | FRMTTR76M06B715E                     |
      | denomination         | Ettore Fieramosca                    |
      | payment_noticeCode   | 302000000000000001                   |
      | payment_multy_number | 2                                    |
      | attachment_sha256    | NULL                                 |
      | messageId            | 0327e9fc-d571-4401-97b5-175b70be01a1 |
    When viene inviata una nuova notifica bonaria
    Then si riceve errore 400



# ************************************************
#  *****SCENARIO 6  - Stato della notifica bonaria
# ***********************************************

  Scenario: [NOTIFICHE_BONARIE_06_1_A] Verifica stato richiesta bonaria - OK
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    And si verifica lo stato della richiesta di notifica bonaria
    Then l'operazione non ha generato errori


  Scenario: [NOTIFICHE_BONARIE_06_1_B] Verifica stato con PA non autorizzata
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    When viene inviata una nuova notifica bonaria
    And si verifica lo stato della richiesta di notifica bonaria
    Then l'operazione non ha generato errori
    Given mittente della notifica bonaria: "COMUNE_2"
    When si verifica lo stato della richiesta di notifica bonaria
    Then si riceve errore 403


  Scenario: [NOTIFICHE_BONARIE_06] Verifica stato con requestId inesistente
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    #When si verifica lo stato della richiesta di notifica bonaria con id "fake"
    Then si riceve errore 404

  Scenario: [NOTIFICHE_BONARIE_06] Verifica stato con parametri incoerenti
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    #When si verifica lo stato con requestId e protocollo insieme
    Then si riceve errore 400



# ***********************************************
# **** VALIDAZIONE ASYNCRONA
# ***********************************************

  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_1] come ente mittente invio una notitfca bonaria con parametri corretti, ottenendo la validazione della notifica.
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType        | PF                 |
      | taxId                | FRMTTR76M06B715E   |
      | denomination         | Ettore Fieramosca  |
      | phone_number         | 3331234567         |
      | email                | ettore@test.it     |
      | payment_noticeCode   | 302000000000000001 |
      | payment_multy_number | 1                  |
    When viene inviata una nuova notifica bonaria
    And la sottomissione della notifica bonaria è andata a buon fine
    #And la notifica bonaria è validata correttamente

  Scenario Outline: [NOTIFICHE_BONARIE_ASYNC_01_2] Come ente mittente tento di inviare una notifica bonaria con documento non conforme, la notifica viene respinta.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType        | PF               |
      | taxId                | FRMTTR76M06B715E |
      | payment_multy_number | 1                |
    And documento non valido: "<errore>"
    When viene inviata una nuova notifica bonaria
    #Then la validazione della notifica bonaria fallisce
    Examples:
      | errore                 |
      | SHA NON INTEGRO        |
      | FORMATO NON CONFORME   |
      | ALLEGATO TROPPO GRANDE |


  Scenario Outline: [NOTIFICHE_BONARIE_ASYNC_01_3] Come ente mittente invio una notifica bonaria con campagne non conformi, la nottifca viene rifiutata.
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | <campaignId> |
    And destinatario della notifica bonaria
      | recipientType        | PF               |
      | taxId                | FRMTTR76M06B715E |
      | payment_multy_number | 1                |
    When viene inviata una nuova notifica bonaria
    #Then la validazione della notifica bonaria fallisce
    Examples:
      | campaignId       |
      | CAMPAGNA_SCADUTA |
      | CAMPAGNA_CHIUSA  |
      | CAMPAGNA_FAKE    |
#todo t bonarie censire le campagne


  Scenario Outline: [NOTIFICHE_BONARIE_ASYNC_01_4] Come ente mittente invio una notifica bonaria con messaggi id non conformi, la nottifca viene rifiutata.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | messageId | <messageId> |
    And destinatario della notifica bonaria
      | recipientType        | PF               |
      | taxId                | FRMTTR76M06B715E |
      | payment_multy_number | 1                |
    When viene inviata una nuova notifica bonaria
    #Then la validazione della notifica bonaria fallisce
    Examples:
      | messageId                       |
      | MESS NON PRESENTE A DB          |
      | MESS CON LINGUA NON DISPONIBILE |
  #todo t bonarie censire I MESSAGGI


# Al momento sarà previsto sempre un invio con indirizzo digitale.
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_5] Come ente mittente invio una notifica bonaria senza indirizzo digitale, la nottifca viene rifiutata.
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType        | PG          |
      | taxId                | 20517490320 |
      | denomination         | ACME SPA    |
      | digital_domicile     | NULL        |
      | payment_multy_number | 1           |
    When viene inviata una nuova notifica bonaria
    #Then la validazione della notifica bonaria fallisce




  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo analogico per un campagna con canale analogico.Ottengo stato refused.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-analog-workflow |
    And destinatario della notifica bonaria
      | recipientType                 | PF                |
      | taxId                         | FRMTTR76M06B715E  |
      | denomination                  | Ettore Fieramosca |
      | physical_address_address      | NULL              |
      | physical_address_details      | NULL              |
      | physical_address_zip          | NULL              |
      | physical_address_municipality | NULL              |
      | physical_address_province     | NULL              |
      | physical_address_state        | NULL              |
      | messageId                     | NULL              |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "REFUSED"


  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_D] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo digitale per un campagna con canale digitale.Ottengo stato refused
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-pec |
    And destinatario della notifica bonaria
      | recipientType   | PF                                   |
      | taxId           | FRMTTR76M06B715E                     |
      | denomination    | Ettore Fieramosca                    |
      | messageId       | 439cd04a-f903-4cc7-b869-3eca50b68887 |
      | digitalDomicile | NULL                                 |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "REFUSED"


  Scenario: [NOTIFICHE_BONARIE_VAS_SM_01_1_F] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo analogico che varrà recuperato dal VAS.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-analog-workflow |
    And destinatario della notifica bonaria
      | denomination                  | PG Censito VAS |
      | recipientType                 | PG             |
      | taxId                         | 01113570442    |
      | digitalDomicile               | NULL           |
      | physical_address_address      | NULL           |
      | physical_address_details      | NULL           |
      | physical_address_zip          | NULL           |
      | physical_address_municipality | NULL           |
      | physical_address_province     | NULL           |
      | physical_address_state        | NULL           |
      | messageId                     | NULL           |
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine
    And verifico che su DynamoDB è presente in timeline l'elemento "NATIONAL_REGISTRY_VALIDATION_RESPONSE"
    And verifico che su DynamoDB è presente in timeline l'elemento "NATIONAL_REGISTRY_VALIDATION_CALL"


  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_B] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo analogico che NON varrà recuperato dal VAS.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-analog-workflow |
    And destinatario della notifica bonaria
      | taxId                         | 01113570442              |
      | denomination                  | Leonardo Da Vinci no vas |
      | taxId                         | DVNLRD52D15M059P         |
      | digitalDomicile               | NULL                     |
      | physical_address_address      | NULL                     |
      | physical_address_details      | NULL                     |
      | physical_address_zip          | NULL                     |
      | physical_address_municipality | NULL                     |
      | physical_address_province     | NULL                     |
      | physical_address_state        | NULL                     |
      | messageId                     | NULL                     |
    And  si verifica che la notifica bonaria sia in stato "REFUSED"


  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_A] Come ente mittente invio una notifica bonaria con indirizzo analogico non normaliozzabile, la notifca viene rifiutata
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-analog-workflow |
    And destinatario della notifica bonaria
      | taxId                         | 01113570442              |
      | denomination                  | Leonardo Da Vinci no vas |
      | taxId                         | DVNLRD52D15M059P         |
      | digitalDomicile               | NULL                     |
      | physical_address_zip          | 801005                   |
      | physical_address_municipality | NULL                     |
      | physical_address_province     | ZZ                       |
      | physical_address_state        | NULL                     |
      | messageId                     | NULL                     |
    And  si verifica che la notifica bonaria sia in stato "REFUSED"


  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_C] Come ente mittente invio una notifica bonaria senza indirizzo analogico e quello dei RN non postalizabile, la notifca viene rifiutata.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-analog-workflow |
    And destinatario della notifica bonaria
      | denomination                  | Matteo Rossi     |
      | taxId                         | XVRSFN76E31L781N |
      | recipientType                 | PF               |
      | digitalDomicile               | NULL             |
      | physical_address_address      | NULL             |
      | physical_address_details      | NULL             |
      | physical_address_zip          | NULL             |
      | physical_address_municipality | NULL             |
      | physical_address_province     | NULL             |
      | physical_address_state        | NULL             |
      | messageId                     | NULL             |
    And  si verifica che la notifica bonaria sia in stato "REFUSED"




  #SCENARIO 3 - Persistenza elementi di Timeline.


  Scenario:[NOTIFICHE_BONARIE_ASYNC_00] Creazione messaggio senza shortBody
    When si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_short_body | NULL |
    Then si riceve errore 400



# **
  Scenario: Recupero notifica bonaria tramite IUN
    Given mittente della notifica bonaria: "COMUNE_1"
    And viene inviata una nuova notifica bonaria
    When si tenta il recupero della notifica bonaria tramite IUN
    Then la notifica bonaria è recuperabile tramite IUN

    And destinatario della notifica bonaria
      | recipientType        | PF                |
      | taxId                | FRMTTR76M06B715E  |
      | denomination         | Ettore Fieramosca |
      | phone_number         | 3331234567        |
      | email                | ettore@test.it    |
      | payment_multy_number | 1                 |



    # ************************************************
#  *****SCENARIO   - AUDIT-LOG
# ***********************************************

  Scenario: [NOTIFICHE_BONARIE_AUDITLOG_02_1] Come ente mittente invio una notifica bonaria e vengono generati i corretti auditlog in pn-delivery
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | FRMTTR76M06B715E                     |
      | denomination  | Ettore Fieramosca                    |
      | messageId     | d9d7545c-fa98-4e0e-8900-b4d3e6923015 |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery-push-validator-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto          |
      | param1 | AUD_COM_VALID |
      | param2 | SUCCESS       |
    And verifico la presenza di un audit log su "/aws/ecs/pn-timeline-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_TIMELINE |
      | param2 | SUCCESS          |
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto                                   |
      | param1 | AUD_COM_CHECK                          |
      | param2 | SUCCESS                                |



  Scenario: [NOTIFICHE_BONARIE_WF_1] Come ente mittente invio una notifica bonaria e vengono generati i corretti auditlog in pn-workflow-manager
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | campaign-1 |
    And destinatario della notifica bonaria
      | recipientType | PF                                   |
      | taxId         | FRMTTR76M06B715E                     |
      | denomination  | Ettore Fieramosca                    |
      | messageId     | 4bd8502e-3aa2-49b1-8bc6-3ed5b4fb6398 |
    When viene inviata una nuova notifica bonaria
    And  si verifica che la notifica bonaria sia in stato "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto                   |
      | param1 | AUD_INFORMAL_NT_INSERT |
      | param2 | recIndex=0             |
      | pippo  | phase=VALIDATION       |
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto                    |
      | param1 | AUD_INFORMAL_MSG_INSERT |
      | param2 | recIndex=0              |
      | pippo  | phase=VALIDATION        |






    # ************************************************
#  *****SCENARIO 7  - Terminazione della notifica
# ***********************************************

  # Api ancora non implementata, da testare con gli sviluppi futuri
  Scenario:[NOTIFICHE_BONARIE_TERMINAZIONE_07_A] Come ente mittente creo una notifica bonarie e successivamente ne chiedo la Terminazione.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene inviata una nuova notifica bonaria
    When si tenta la terminazione della notifica bonaria
    Then la terminazione della notifica bonaria è accettata

  # Api ancora non implementata, da testare con gli sviluppi futuri
  Scenario:[NOTIFICHE_BONARIE_TERMINAZIONE_07_B] Come ente mittente creo una notifica bonarie e successivamente ne chiedo la Terminazione due volte ricevendo un errore.
    Given mittente della notifica bonaria: "COMUNE_MULTI"
    And viene inviata una nuova notifica bonaria
    When si tenta la terminazione della notifica bonaria
    And si tenta la terminazione della notifica bonaria
    Then la notifica bonaria risulta già terminata