Feature: Sottomissione di una notifica bonaria.



# *******************************************************************
#  Lingua gestitia al livello del recipient
# *******************************************************************

  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_LANGUAGES_01_1_A] Come ente mittente invio una notifica bonaria allegando
  un messaggio multilingue e specificando la seconda lingia come lingua addizionale.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd    |
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_LANGUAGES_01_1_B] Come ente mittente invio una notifica bonaria allegando
  un messaggio multilingue e NON specificando nessuna addizionale.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd            |
      | recipientType       | PG                        |
      | taxId               | 20517490320               |
      | denomination        | Cucumber srl              |
      | messageId           | ${NEW-IT-FR}              |
      | additionalLanguages | NULL                      |
      | digitalDomicile     | testpagopa1@pec.pagopa.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalSyncValidation
  Scenario Outline: [NOTIFICHE_LANGUAGES_01_3_A] Come ente mittente ricevo errore nel tentativo di inviare
  una notifica bonaria con allegato un messaggio e indicando una lingua non conforme
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd        |
      | recipientType       | PF                    |
      | taxId               | FRMTTR76M06B715E      |
      | denomination        | Ettore Fieramosca     |
      | messageId           | ${NEW-IT}             |
      | additionalLanguages | <additionalLanguages> |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_DELIVERY_ADDITIONAL_LANG_UNSUPPORTED_VALUE"
    Examples:
      | additionalLanguages |
      | AA                  |
      | I T                 |
      | ITT                 |


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_LANGUAGES_01_3_B] Come ente mittente ricevo errore nel tentativo di inviare
  una notifica bonaria con allegato un messaggio e indicando una lingua non presente nel messaggio
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd |
      | recipientType       | PG             |
      | taxId               | 20517490320    |
      | denomination        | Cucumber srl   |
      | messageId           | ${NEW-IT-FR}   |
      | additionalLanguages | DE             |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "MESSAGE_LANGUAGE_MISMATCH"



# *******************************************************************
#  Introduzione attributo amount e dueDate
# *******************************************************************

  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_AMOUNT_02_1_A] Come ente mittente invio una notifica bonaria definendo il campo amount e dueDate correttamente
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | FattOrd    |
      | recipientType    | PF                |
      | taxId            | FRMTTR76M06B715E  |
      | denomination     | Ettore Fieramosca |
      | messageId        | ${NEW-IT}         |
      | payment_amount   | 1500              |
      | payment_due_date | 2028-12-31        |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_AMOUNT_02_1_B] Come ente mittente invio una notifica bonaria definendo il campo amount e non definendo il campo dueDate
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | FattOrd    |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | messageId      | ${NEW-IT}         |
      | payment_amount | 1500              |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_AMOUNT_02_1_C] Come ente mittente ricevo un errore nel tentativo di invio una notifica bonaria senza definire il campo amount
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd    |
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
      | payment_amount      | NULL              |
      | payment_due_date    | 2026-12-31        |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


  @informalNotificationsValidation @informalSyncValidation     #validazione ripresa dalle legal + obbligo di rispettarer la firma del API
  Scenario Outline: [NOTIFICHE_AMOUNT_02_1_D] Come ente mittente ricevo un errore nel tentativo di invio una notifica bonaria definendo il campo amount con valori non conformi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId     | FattOrd    |
      | recipientType  | PF                |
      | taxId          | FRMTTR76M06B715E  |
      | denomination   | Ettore Fieramosca |
      | messageId      | ${NEW-IT}         |
      | payment_amount | <amount>          |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"
    Examples:
      | amount |
      | -1     |


  #@informalNotificationsValidation @informalAsyncValidation #obbligo di rispettare la firma del API
  Scenario Outline: [NOTIFICHE_AMOUNT_02_1_E] Come ente mittente invio una notifica bonaria....
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | FattOrd    |
      | recipientType    | PF                |
      | taxId            | FRMTTR76M06B715E  |
      | denomination     | Ettore Fieramosca |
      | messageId        | ${NEW-IT}         |
      | payment_amount   | 1500              |
      | payment_due_date | <dueDate>         |
    Examples:
      | dueDate    |
      | 2000-1A-31 |
      | 2026/10/10 |
      | 1000/10/10 |



# *******************************************************************
#  Gestione stato della Campagna
# *******************************************************************

  @informalNotificationsValidation @informalAsyncValidation
  Scenario Outline: [NOTIFICHE_BONARIE_CAMPAIGN_03_1_A] Come ente mittente invio una notifica bonaria con campagna con stato in progress.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId           | <campaignId>     |
      | recipientType        | PF               |
      | taxId                | FRMTTR76M06B715E |
      | payment_multy_number | 1                |
      | messageId            | ${NEW-IT}        |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Examples:
      | campaignId     |
      | FattOrd |


  @informalNotificationsValidation @informalAsyncValidation @informalNotMVP
  Scenario Outline: [NOTIFICHE_BONARIE_CAMPAIGN_03_1_B] Come ente mittente invio una notifica bonaria indicando campagne con stati non validi, la nottifca viene rifiutata.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId           | <campaignId>     |
      | recipientType        | PF               |
      | taxId                | FRMTTR76M06B715E |
      | payment_multy_number | 1                |
      | messageId            | ${NEW-IT}        |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "<ERROR>"
    Examples:
      | campaignId    | ERROR                   |
      | CampDraft     | CAMPAIGN_INVALID_STATUS |
      | CampConcluded | CAMPAIGN_INVALID_STATUS |
      | CampCancelled | CAMPAIGN_INVALID_STATUS |


# *******************************************************************
#  UAT
# *******************************************************************

  @informalAsyncValidation @informalIgnoreUAT
  Scenario: [NOTIFICHE_BONARIE_VAS_SM_01_1_F] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo analogico che varrà recuperato dal VAS.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | MessaMora           |
      | denomination     | PG Censito VAS             |
      | recipientType    | PG                         |
      | taxId            | 01113570442                |
      | digitalDomicile  | example@FAIL-pecFirstKO.it |
      | physical_address | ${PHYSICAL_ADDRESS_NULL}   |
      | messageId        | ${NEW-IT}                  |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine
    And si attende che venga prodotto l'elemento "PUBLIC_REGISTRY_VALIDATION_CALL" della notifica bonaria
    And si attende che venga prodotto l'elemento "PUBLIC_REGISTRY_VALIDATION_RESPONSE" della notifica bonaria
    And si attende che venga prodotto l'elemento "VALIDATE_NORMALIZE_ADDRESSES_REQUEST" della notifica bonaria
#    And verifico che su DynamoDB è presente in timeline l'elemento "PUBLIC_REGISTRY_VALIDATION_CALL"
#    And verifico che su DynamoDB è presente in timeline l'elemento "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
#    And verifico che su DynamoDB è presente in timeline l'elemento "VALIDATE_NORMALIZE_ADDRESSES_REQUEST"


  @informalAsyncValidation @informalUAT
  Scenario: [NOTIFICHE_BONARIE_VAS_SM_01_1_UAT] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo analogico che varrà recuperato dal VAS.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | MessaMora         |
      | denomination     | PF censito vas           |
      | recipientType    | PF                       |
      | taxId            | DVNLRD52D15M059P         |
      | digitalDomicile  | NULL                     |
      | physical_address | ${PHYSICAL_ADDRESS_NULL} |
      | messageId        | ${NEW-IT}                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine
    And si attende che venga prodotto l'elemento "PUBLIC_REGISTRY_VALIDATION_CALL" della notifica bonaria
    And si attende che venga prodotto l'elemento "PUBLIC_REGISTRY_VALIDATION_RESPONSE" della notifica bonaria
    And si attende che venga prodotto l'elemento "VALIDATE_NORMALIZE_ADDRESSES_REQUEST" della notifica bonaria
#    And verifico che su DynamoDB è presente in timeline l'elemento "PUBLIC_REGISTRY_VALIDATION_CALL"
#    And verifico che su DynamoDB è presente in timeline l'elemento "PUBLIC_REGISTRY_VALIDATION_RESPONSE"
#    And verifico che su DynamoDB è presente in timeline l'elemento "VALIDATE_NORMALIZE_ADDRESSES_REQUEST"

  @informalAsyncValidation @informalIgnoreUAT
  Scenario: [NOTIFICHE_BONARIE_ASYNC_ANALOG_01_6_B] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo analogico che NON varrà recuperato dal VAS.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | MessaMora         |
      | denomination     | Leonardo Da Vinci no vas |
      | taxId            | DVNLRD52D15M059P         |
      | digitalDomicile  | NULL                     |
      | physical_address | ${PHYSICAL_ADDRESS_NULL} |
      | messageId        | ${NEW-IT}                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "ADDRESS_NOT_FOUND"


  @informalAsyncValidation @informalUAT
  Scenario: [NOTIFICHE_BONARIE_ASYNC_ANALOG_01_6_B_UAT] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo analogico che NON varrà recuperato dal VAS.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | MessaMora          |
      | recipientType    | PG                        |
      | denomination     | Leonardo Da Vinci no vas  |
      | taxId            | 38868390881               |
      | digitalDomicile  | testpagopa1@pec.pagopa.it |
      | physical_address | ${PHYSICAL_ADDRESS_NULL}  |
      | messageId        | ${NEW-IT}                 |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "ADDRESS_NOT_FOUND"


  @informalAsyncValidation @informalIgnoreUAT
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_C] Come ente mittente invio una notifica bonaria senza indirizzo analogico e quello dei RN non postalizabile, la notifca viene rifiutata.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | MessaMora         |
      | denomination     | Matteo Rossi             |
      | taxId            | XVRSFN76E31L781N         |
      | recipientType    | PF                       |
      | digitalDomicile  | NULL                     |
      | physical_address | ${PHYSICAL_ADDRESS_NULL} |
      | messageId        | ${NEW-IT}                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "NOT_VALID_ADDRESS"


  @informalAsyncValidation @informalUAT
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_UAT] Come ente mittente invio una notifica bonaria senza indirizzo analogico e quello dei RN non postalizabile, la notifca viene rifiutata.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | MessaMora         |
      | denomination     | Matteo Rossi             |
      | taxId            | KRSJSM88S03H501A         |
      | recipientType    | PF                       |
      | digitalDomicile  | NULL                     |
      | physical_address | ${PHYSICAL_ADDRESS_NULL} |
      | messageId        | ${NEW-IT}                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "NOT_VALID_ADDRESS"



# *******************************************************************
#  Preload AUDIT-LOG
# *******************************************************************

  @informalNotificationsValidation @informalAsyncValidation @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_AUDITLOG_04_1] Come ente mittente invio una notifica bonaria e vengono generati i corretti auditlog in pn-commons
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 5 minuti riportante i seguenti dati nel messaggio
      | param1 | AUD_COM_PRELOAD              |
      | param2 | PN_COMMUNICATIONS_ATTACHMENT |
      | param3 | SUCCESS                      |



# *******************************************************************
#  Presenza Codice Tassonomico
# *******************************************************************

  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_TAXONIMIC_06_1] Come ente mittente invio una notifica bonaria verso PF ottengo errore SPAM sul email e solo feddback negativi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd    |
      | messageId       | ${NEW-IT}         |
      | subject         | Test workflow     |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | email           | NULL              |
      | digitalDomicile | NULL              |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then si verifica che il "taxonomyCode" sia correttamente compilato in pn-Notifications
    Then si verifica che il "campaignId" sia correttamente compilato in pn-Notifications



# *******************************************************************
#  Sottomissione di una notifica bonaria Mancata validazione Sincrona
# *******************************************************************

  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_GROUP_DELETED] Come ente mittente invio una notifica bonaria con gruppo non attivo. Ricevo un errore
    Given mittente della notifica bonaria: "Comune_Multi"
    And imposto un gruppo non attivo per "Comune_Multi"
    And viene creata una nuova notifica bonaria con i seguenti parametri
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_DELIVERY_INVALIDPARAMETER_GROUP"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_03_1b] Come ente mittente invio una notifica bonaria con gruppo non esistente
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd          |
      | group         | 63f359bc72337440a40f111 |
      | recipientType | PF                      |
      | taxId         | FRMTTR76M06B715E        |
      | denomination  | Ettore Fieramosca       |
      | messageId     | ${NEW-IT}               |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_DELIVERY_INVALIDPARAMETER_GROUP"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_A] Come Ente mittente disattivo in whitelist invio una notifica bonaria e ricevo un errore.
    Given l'ente mittente "Comune_2" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 403 "PN_DELIVERY_SEND_IS_DISABLED"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_A2] Come Ente mittente non censito in whitelist invio una notifica bonaria e ricevo un errore.
    Given l'ente mittente "Comune_Root" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 403 "PN_DELIVERY_SEND_IS_DISABLED"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_B] come ente mittente invio una notifica bonaria con un numero di allegati di pagamento superiore al massimo configurato e ricevo un errore,
  Nome Parametro: PN_DELIVERY_INFORMALNOTIFICATIONMAXPAYMENTS
  Tipo parametro: Configurazione dichiarata nel file application-<env>.env
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | recipientType        | PF                |
      | taxId                | FRMTTR76M06B715E  |
      | denomination         | Ettore Fieramosca |
      | payment_multy_number | 11                |
      | messageId            | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_DELIVERY_MAX_PAYMENT_NUMBER_PASSED"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_E] Invio bonaria con lingua secondaria non supportata 1
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd    |
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT}         |
      | additionalLanguages | XX                |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_DELIVERY_ADDITIONAL_LANG_UNSUPPORTED_VALUE"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_H] Verso PG con CF non conforme
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | recipientType   | PG                        |
      | taxId           | ABCDEF12345               |
      | messageId       | ${NEW-IT}                 |
      | digitalDomicile | testpagopa1@pec.pagopa.it |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_I] Verso PF con CF non conforme
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | recipientType | PF          |
      | taxId         | ABCDEF12345 |
      | messageId     | ${NEW-IT}   |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_M] Invio con fileKey non coerente con contentType diverso da pdf
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | recipientType          | PF                |
      | taxId                  | FRMTTR76M06B715E  |
      | denomination           | Ettore Fieramosca |
      | attachment_contentType | application/txt   |
      | messageId              | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


  @informalNotificationsValidation @informalSyncValidation # la validazione è presa da quella esistente per le legal aperto bug che sarà gestito nelle prossime release:PN-20249
  #Invio con recapito fisico (indirizzo, civico, località, CAP, provincia, nazione) NON conforme agli standard di postalizzazione,
  #"PhysicalAddressValidationCharsValue": "\\u0020-\\u007E\\u00A0-\\u00FF",,"PhysicalAddressValidationValue": "true","PhysicalAddressValidationLength": "500",
  Scenario Outline: [NOTIFICHE_BONARIE_SM_04_2_N] Validazione indirizzo fisico - errori formali
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId                    | FattOrd    |
      | recipientType                 | PF                |
      | taxId                         | FRMTTR76M06B715E  |
      | denomination                  | Ettore Fieramosca |
      | physicalAddress               | SI                |
      | physical_address_address      | <address>         |
      | physical_address_details      | <details>         |
      | physical_address_zip          | <zip>             |
      | physical_address_municipality | <city>            |
      | physical_address_province     | RM                |
      | physical_address_state        | ITALIA            |
      | messageId                     | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "<error>"
    Examples:
      | address     | details   | zip   | city | error                                           |
      | Via Roma 😃 | scala A   | 00100 | Roma | PN_DELIVERY_PHYSICAL_ADDRESS_INVALID_CHARACTERS |
            # caratteri non validi
      | Via Roma    | scala 😃  | 00100 | Roma | PN_DELIVERY_PHYSICAL_ADDRESS_INVALID_CHARACTERS |
            # caratteri speciali city
      | 501_CHAR    | dettaglio | 00100 | Roma | PN_DELIVERY_PHYSICAL_ADDRESS_LENGTH_EXCEEDED    |
            # lunghezza > 500


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_O] Invio con indirizzo nazionale senza provincia
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId                | FattOrd    |
      | recipientType             | PF                |
      | taxId                     | FRMTTR76M06B715E  |
      | denomination              | Ettore Fieramosca |
      | physicalAddress           | SI                |
      | physical_address_state    | ITALIA            |
      | physical_address_province | NULL              |
      | messageId                 | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_DELIVERY_PROVINCE_REQUIRED"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_OA] Come ente mittente tento l'invio di una notifica bonaria con taxi id mancante, ricevo un errore.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId                | FattOrd    |
      | recipientType             | PF                |
      | taxId                     | NULL              |
      | denomination              | Ettore Fieramosca |
      | physicalAddress           | SI                |
      | physical_address_state    | ITALIA            |
      | physical_address_province | BN                |
      | messageId                 | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2T] Come ente mittente tento l'invio di una notifica bonaria con recipient type mancante, ricevo un errore.
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType             | NULL              |
      | taxId                     | FRMTTR76M06B715E  |
      | denomination              | Ettore Fieramosca |
      | physicalAddress           | SI                |
      | physical_address_state    | ITALIA            |
      | physical_address_province | NULL              |
      | messageId                 | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


  @informalNotificationsValidation @informalSyncValidation
  Scenario Outline: [NOTIFICHE_BONARIE_SM_04_2_Q] Validazione denominazione e presso con caratteri esclusi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId                | FattOrd   |
      | recipientType             | PF               |
      | taxId                     | FRMTTR76M06B715E |
      | denomination              | <denomination>   |
      | physicalAddress           | SI               |
      | physical_address_at       | <at>             |
      | physical_address_state    | ITALIA           |
      | physical_address_province | RM               |
      | messageId                 | ${NEW-IT}        |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400
    Examples:
      | denomination       | at                      |
      | Test😊             | Presso ufficio          |
      | Mario Rossi        | Test😊                  |
      | 你好                 | Presso Ufficio          |
      | Rossi Mario        | 你好                      |
      | €                  | Presso Ufficio          |
      | Mario Rossi        | €                       |
      | 你好                 | Presso Ufficio          |
      | “virgolette smart” | Presso ufficio          |
      | Riga1 Riga2        | ‘apostrofo tipografico’ |
      | Nome \n test       | Valido                  |
      | Valido             | Riga \n test            |


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_R] Validazione denominazione troppo lunga
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId                | FattOrd   |
      | recipientType             | PF               |
      | taxId                     | FRMTTR76M06B715E |
      | denomination              | 89_CHAR          |
      | physicalAddress           | SI               |
      | physical_address_at       | Presso           |
      | physical_address_state    | ITALIA           |
      | physical_address_province | RM               |
      | messageId                 | ${NEW-IT}        |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_DELIVERY_DENOMINATION_LENGTH_EXCEEDED"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_S] Invio bonaria con pagamento senza allegato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | document             | DOC_1_PG          |
      | campaignId           | FattOrd    |
      | recipientType        | PF                |
      | taxId                | NULL              |
      | denomination         | Ettore Fieramosca |
      | payment_multy_number | 1                 |
      | messageId            | ${NEW-IT}         |
    Then viene inviata una nuova notifica bonaria con sha non valido
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


  @informalSyncValidation #bug validation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_S2] Invio bonaria con pagamento senza allegato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | document             | DOC_1_PG          |
      | campaignId           | FattOrd    |
      | recipientType        | PF                |
      | taxId                | FRMTTR76M06B715E  |
      | denomination         | Ettore Fieramosca |
      | payment_multy_number | 1                 |
      | messageId            | ${NEW-IT}         |
    Then viene inviata una nuova notifica bonaria con nome attachment non valido
    Then si riceve errore 400 "PN_DELIVERY_INVALID_DOCUMENT_KEY"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_06_1_B3] Come ente mittente creo una notifica bonaria e tento la visualizzazione da ente diverso , ricevendo un errore.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Given mittente della notifica bonaria: "Comune_1"
    When si verifica lo stato della richiesta della notifica bonaria con errore
    Then si riceve errore 404


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_06] Verifica stato con requestId inesistente
    Given mittente della notifica bonaria: "Comune_Multi"
    When si verifica lo stato della richiesta di notifica bonaria con notification id "qqq"
    Then si riceve errore 400


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_DUP_KEY] Invio con fileKey duplicata tra allegati
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | document      | DOC_1_PG;DOC_1_PG |
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria con fileKey duplicata
    Then si riceve errore 400 "PN_DELIVERY_DUPLICATED_ATTACHMENTS"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_MAX_ATTACHMENTS] Invio con numero documenti superiore al limite
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | document      | DOC_1_PG;DOC_1_PG;DOC_1_PG;DOC_1_PG;DOC_1_PG;DOC_1_PG;DOC_1_PG;DOC_1_PG;DOC_1_PG;DOC_1_PG;DOC_1_PG;DOC_1_PG |
      | campaignId    | FattOrd                                                                                              |
      | recipientType | PF                                                                                                          |
      | taxId         | FRMTTR76M06B715E                                                                                            |
      | denomination  | Ettore Fieramosca                                                                                           |
      | messageId     | ${NEW-IT}                                                                                                   |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_DELIVERY_MAX_ATTACHMENT_NUMBER_PASSED"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_MULTI_LANG] Invio con più lingue aggiuntive
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd    |
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR, IT            |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_NO_MESSAGE_ID] Invio senza messageId in one-to-one
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | NULL              |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_DOC_CONTENT_TYPE] Invio documento principale con contentType errato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria con content type non valido
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"


   #@informalNotificationsValidation @informalSyncValidation errore dato nella prepare non oggetto delle bonarie
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_PAYMENT_NO_ATTACHMENT] Invio pagamento senza attachment
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId           | FattOrd    |
      | recipientType        | PF                |
      | taxId                | FRMTTR76M06B715E  |
      | denomination         | Ettore Fieramosca |
      | payment_multy_number | 1                 |
      | attachment_key       | NULL              |
      | messageId            | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400


  #@informalNotificationsValidation @informalSyncValidation gestito in future release
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_GROUP_REQUIRED] Invio senza gruppo quando obbligatorio
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | group         | NULL              |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400


  #@informalNotificationsValidation @informalSyncValidation non considerato errore
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_DUP_SHA] Invio con SHA duplicato tra documenti
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | document      | DOC_1_PG;DOC_1_PG |
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400


    # il sender taxid viene sempre recuperto da db e non più passato
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_C] Invio bonaria con senderTaxId custom
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId         | FattOrd    |
      | senderTaxId        | 20517490320       |
      | senderDenomination | Comune di Test    |
      | recipientType      | PF                |
      | taxId              | FRMTTR76M06B715E  |
      | denomination       | Ettore Fieramosca |
      | messageId          | ${NEW-IT}         |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400


  @informalNotificationsValidation @informalSyncValidation
  Scenario Outline: [NOTIFICHE_BONARIE_SM_06_C1] Invio notifica bonaria con email non valida
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
      | email         | <email>           |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"
    Examples:
      | email         |
      | test          |
      | test@         |
      | test@.it      |
      | test          |
      | test@@ciao.it |
      | test@.itt     |

   #@informalNotificationsValidation @informalSyncValidation coperto da [NOTIFICHE_BONARIE_SM_01_1_D]
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_D3] Invio bonaria verso 2 destinatari con stesso message id
  Nome Parametro Max numero destinatari: PN_DELIVERY_INFORMALNOTIFICATIONMAXRECIPIENTS.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId           | FattOrd    |
      | recipientType        | PF                |
      | taxId                | FRMTTR76M06B715E  |
      | denomination         | Ettore Fieramosca |
      | messageId            | ${NEW-IT}         |
      | payment_multy_number | 1                 |
    And destinatario della notifica bonaria
      | recipientType        | PF               |
      | taxId                | GLLGLL64B15G702I |
      | denomination         | Galileo Galilei  |
      | messageId            | ${NEW-IT}        |
      | payment_multy_number | 1                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalSyncValidation
  Scenario Outline: [NOTIFICHE_BONARIE_SM_06_B] Invio notifica bonaria con telefono non valido (<descrizione>)
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
      | email         | test@test.it      |
      | phone_number  | <phone>           |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_GENERIC_INVALIDPARAMETER"
    Examples:
      | descrizione                   | phone         |
      | telefono senza prefisso       | 3396778788    |
      | telefono con lettere          | +39ABC123456  |
      | telefono con simboli invalidi | +39#3396778!! |


  @informalNotificationsValidation @informalSyncValidation
  Scenario Outline: [NOTIFICHE_BONARIE_SM_06_C] Invio notifica bonaria con telefono valido (<descrizione>)
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
      | email         | test@test.it      |
      | phone_number  | <phone>           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine
    Examples:
      | descrizione         | phone           |
      | telefono valido +39 | +393396778788   |
      | telefono valido 00  | 00393396778788  |
      | telefono con spazi  | +39 339 6778788 |


# ***********************************************
# **** VALIDAZIONE ASYNCRONA
# ***********************************************

  #scenario testato con altri test nella suite
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_1] come ente mittente invio una notitfca bonaria con parametri corretti, ottenendo la validazione della notifica.

  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_03_1] Come ente mittente invio una notifica bonaria con gruppo.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_06_A] Come ente mittente invio una notifica bonaria con telefono corretto.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT-FR}      |
      | email         | test@test.it      |
      | phone_number  | +393396778788     |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_G3] Come mittente associato alla campagna tento l'invio di una notifica bonaria.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd            |
      | recipientType   | PF                        |
      | taxId           | FRMTTR76M06B715E          |
      | denomination    | Ettore Fieramosca         |
      | messageId       | ${NEW-IT}                 |
      | digitalDomicile | testpagopa1@pec.pagopa.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_01_1_A] Come ente mittente invio una notifica bonaria con modalità one-to-many quindi con id della campagna valorizzato
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine
    And si attende che venga prodotto l'elemento "REQUEST_ACCEPTED" della notifica bonaria


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_01_1_B] Come ente mittente invio una notifica bonaria con modalità one-to-one quindi con id messaggio, e seconda lingua specificata
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd    |
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine


  @informalNotificationsValidation @informalAsyncValidation #bug fix PN-20248
  Scenario: [NOTIFICHE_BONARIE_SM_01_1_C] Come ente mittente invio una notifica bonaria con messaggio bilingue,  seconda lingua NON specificata
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd    |
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | NULL              |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine


  #@informalNotificationsValidation @informalAsyncValidation #Multidestinatario disabilitato per il momento
  Scenario: [NOTIFICHE_BONARIE_SM_01_1_D] Come ente mittenste invio delle notifiche bonarie verso più destinatari con più pagamenti
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId           | FattOrd    |
      | recipientType        | PF                |
      | taxId                | FRMTTR76M06B715E  |
      | denomination         | Ettore Fieramosca |
      | payment_multy_number | 2                 |
      | messageId            | ${NEW-IT}         |
    And destinatario della notifica bonaria
      | denomination         | Vita Nova Sas             |
      | recipientType        | PG                        |
      | taxId                | 12666810299               |
      | payment_multy_number | 1                         |
      | messageId            | ${NEW-IT}                 |
      | digitalDomicile      | testpagopa1@pec.pagopa.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_01_1_E] Come ente mittenste invio una notifica bonaria con un documento e senza pagamneti e allegati
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | document             | DOC_INFORMAL      |
      | campaignId           | FattOrd    |
      | recipientType        | PF                |
      | taxId                | FRMTTR76M06B715E  |
      | denomination         | Ettore Fieramosca |
      | payment_multy_number | 0                 |
      | attachment_key       | NULL              |
      | messageId            | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_5] Come ente mittente invio una notifica bonaria con indirizzo digitale.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd |
      | recipientType   | PG             |
      | taxId           | 20517490320    |
      | denomination    | ACME SPA       |
      | digitalDomicile | tu@gmail.com   |
      | messageId       | ${NEW-IT}      |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalAsyncValidation @informalNotMVP
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_D] Come ente mittente tento invio una notifica bonaria senza indirizzo digitale per un campagna con canale digitale verso PF.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | QADigital         |
      | recipientType   | PF                |
      | taxId           | FRMTTR76M06B715E  |
      | denomination    | Ettore Fieramosca |
      | messageId       | ${NEW-IT}         |
      | digitalDomicile | NULL              |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalAsyncValidation @informalNotMVP
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_D_PG2] Come ente mittente invio una notifica bonaria senza indirizzo digitale per un campagna NON digitale.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | CampAnalogic |
      | recipientType   | PG           |
      | taxId           | 20517490320  |
      | denomination    | Acme Spa     |
      | messageId       | ${NEW-IT}    |
      | digitalDomicile | NULL         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_L2] Invio con allegato pdf
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId             | FattOrd        |
      | recipientType          | PF                    |
      | taxId                  | FRMTTR76M06B715E      |
      | denomination           | Ettore Fieramosca     |
      | attachment_key         | classpath:/sample.pdf |
      | attachment_contentType | application/pdf       |
      | messageId              | ${NEW-IT}             |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_G] Lingua secondaria non presente nella campagna
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd            |
      | recipientType       | PF                        |
      | taxId               | FRMTTR76M06B715E          |
      | denomination        | Ettore Fieramosca         |
      | messageId           | ${NEW-IT}                 |
      | additionalLanguages | FR                        |
      | digitalDomicile     | testpagopa1@pec.pagopa.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "MESSAGE_LANGUAGE_MISMATCH"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_G2] Come mittente non associato alla campagna tento l'invio di una notifica bonaria.
    Given l'ente mittente "Comune_1" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd            |
      | recipientType   | PF                        |
      | taxId           | FRMTTR76M06B715E          |
      | denomination    | Ettore Fieramosca         |
      | messageId       | ${NEW-IT}                 |
      | digitalDomicile | testpagopa1@pec.pagopa.it |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 404 "PN_DELIVERY_CAMPAIGN_NOT_FOUND"


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_L] Invio con fileKey non coerente con contentType del pagamento
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId             | FattOrd        |
      | recipientType          | PF                    |
      | taxId                  | FRMTTR76M06B715E      |
      | denomination           | Ettore Fieramosca     |
      | attachment_key         | classpath:/sample.txt |
      | attachment_contentType | application/pdf       |
      | messageId              | ${NEW-IT}             |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "FILE_PDF_INVALID_ERROR"


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_4] Come ente mittente invio una notifica bonaria con messaggi id non esistente, la nottifca viene rifiutata.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId           | FattOrd                       |
      | recipientType        | PF                                   |
      | taxId                | FRMTTR76M06B715E                     |
      | payment_multy_number | 1                                    |
      | messageId            | 11111111-2222-3333-4444-555555555555 |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "MESSAGE_NOT_FOUND"


  @informalNotificationsValidation @informalSyncValidation
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_4B] Come ente mittente invio una notifica bonaria con campagna id non esistente, la nottifca viene rifiutata.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId           | campaign-0       |
      | recipientType        | PF               |
      | taxId                | FRMTTR76M06B715E |
      | payment_multy_number | 1                |
      | messageId            | ${NEW-IT}        |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 404 "PN_DELIVERY_CAMPAIGN_NOT_FOUND"


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_D_PG] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo digitale per un campagna con canale digitale verso PG.Ottengo stato refused
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd |
      | recipientType   | PG             |
      | taxId           | 20517490320    |
      | denomination    | Acme Spa       |
      | messageId       | ${NEW-IT}      |
      | digitalDomicile | NULL           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "DIGITAL_ADDRESS_MISSING"


  @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_ASYNC_ANALOG_01_6] Come ente mittente tento l'invio di una notifica bonaria senza indirizzo analogico per un campagna con canale analogico.Ottengo stato refused.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | MessaMora         |
      | recipientType    | PF                       |
      | taxId            | XVRSFN76E31L781N         |
      | denomination     | xavier                   |
      | physical_address | ${PHYSICAL_ADDRESS_NULL} |
      | messageId        | ${NEW-IT}                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"


  @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_ASYNC_01_6_A] Come ente mittente invio una notifica bonaria con indirizzo analogico non normaliozzabile, la notifca viene rifiutata
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId                    | MessaMora         |
      | denomination                  | Leonardo Da Vinci no vas |
      | taxId                         | DVNLRD52D15M059P         |
      | physical_address_zip          | 80100                    |
      | physical_address_municipality | Bologn                   |
      | physical_address_province     | RM                       |
      | physical_address_state        | Becok                    |
      | physical_address_address      | Q1                       |
      | physical_address_details      | NULL                     |
      | messageId                     | ${NEW-IT}                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "NOT_VALID_ADDRESS"


    #@informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_01_1_E2] Come ente mittenste invio una notifica bonaria con un documento oltre il limite di grandezza massimo.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | document      | DOC_110MB         |
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "FILE_PDF_TOOBIG_ERROR"
    And verifico che su DynamoDB è presente in timeline l'elemento "REQUEST_REFUSED"



# ************************************************
#  *****SCENARIO 6  - Stato della notifica bonaria
# ***********************************************

  #non necessario, api invocata in altri scenari
  Scenario: [NOTIFICHE_BONARIE_06_1_A] Verifica stato richiesta bonaria - OK
    Given l'ente mittente "Comune_1" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    And si verifica lo stato della richiesta della notifica bonaria con errore
    Then l'operazione non ha generato errori


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_06_1_B] Come ente mittente creo una notifica bonaria e tento la visualizzazione da ente diverso , ricevendo un errore.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Given mittente della notifica bonaria: "Comune_2"
    When si verifica lo stato della richiesta della notifica bonaria con errore
    Then si riceve errore 404



# ************************************************
#  *****SCENARIO   - AUDIT-LOG
# ***********************************************

  @informalNotificationsValidation @informalAsyncValidation @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_AUDITLOG_02_1] Come ente mittente invio una notifica bonaria e vengono generati i corretti auditlog in pn-delivery
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery-push-validator-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto          |
      | param1 | AUD_COM_VALID |
      | param2 | SUCCESS       |
    And verifico la presenza di un audit log su "/aws/ecs/pn-timeline-service" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_TIMELINE |
      | param2 | SUCCESS          |
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto          |
      | param1 | AUD_COM_CHECK |
      | param2 | SUCCESS       |


  @informalNotificationsValidation @informalAsyncValidation @informalAuditlog
  Scenario: [NOTIFICHE_BONARIE_WF_1_B] Come ente mittente invio una notifica bonaria e vengono generati i corretti log in pn-workflow-manager
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-manager" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto                          |
      | param1 | workflowManagerActionConsumer |

    #@informalNotificationsValidation @informalAsyncValidation non presente nella specifica
  Scenario: [NOTIFICHE_BONARIE_WF_1] Come ente mittente invio una notifica bonaria e vengono generati i corretti auditlog in pn-workflow-manager
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
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


  #SCENARIO  - Sottomissione di una notifica bonaria.
  #CASO DI TEST .1 Validazione della richiesta di invio notifica bonaria.
  Scenario: [NOTIFICHE_BONARIE_SOTTOMISSIONE_14A] Come ente mittente creo una nuova notifica bonaria utilizzando valori di default.
  Con whitelist vuota ricevo un errore 403 Forbidden.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd    |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
    Then l'invio della notifica bonaria fallisce
    Then si riceve errore 403



# ************************************************
#  *****SCENARIO 7  - Terminazione della notifica
# ***********************************************

  # Api ancora non implementata, da testare con gli sviluppi futuri
  Scenario:[NOTIFICHE_BONARIE_TERMINAZIONE_07_A] Come ente mittente creo una notifica bonarie e successivamente ne chiedo la Terminazione.
    Given mittente della notifica bonaria: "Comune_Multi"
    When si tenta la terminazione della notifica bonaria
    Then la terminazione della notifica bonaria è accettata

  # Api ancora non implementata, da testare con gli sviluppi futuri
  Scenario:[NOTIFICHE_BONARIE_TERMINAZIONE_07_B] Come ente mittente creo una notifica bonarie e successivamente ne chiedo la Terminazione due volte ricevendo un errore.
    Given mittente della notifica bonaria: "Comune_Multi"
    When si tenta la terminazione della notifica bonaria
    And si tenta la terminazione della notifica bonaria
    Then la notifica bonaria risulta già terminata



# ********************************************************
#  *****SCENARI  - Multidestinatario Ancora non abilitato
# *******************************************************

  #@informalNotificationsValidation @informalSyncValidation #Multidestinatario disabilitato per il momento
  Scenario: [NOTIFICHE_BONARIE_SM_04_2_D] Invio bonaria verso 11 destinatari - errore 400
  Nome Parametro Max numero destinatari: PN_DELIVERY_INFORMALNOTIFICATIONMAXRECIPIENTS.
    Given mittente della notifica bonaria: "Comune_Multi"
    And viene creata una nuova notifica bonaria con valori di default
    And destinatario della notifica bonaria
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    And destinatario della notifica bonaria
      | denomination         | Vita Nova Sas |
      | recipientType        | PG            |
      | taxId                | 12666810299   |
      | payment_multy_number | 1             |
      | messageId            | ${NEW-IT}     |
    And destinatario della notifica bonaria
      | recipientType | PF               |
      | taxId         | GLLGLL64B15G702I |
      | denomination  | Galileo Galilei  |
      | messageId     | ${NEW-IT-FR}     |
    And destinatario della notifica bonaria
      | recipientType | PG          |
      | denomination  | CucumberSpa |
      | taxId         | 20517490320 |
      | messageId     | ${NEW-IT}   |
    And destinatario della notifica bonaria
      | recipientType        | PF                 |
      | denomination         | Alessandro Manzoni |
      | taxId                | MNZLSN99E05F205J   |
      | payment_multy_number | 1                  |
      | messageId            | ${NEW-IT}          |
    And destinatario della notifica bonaria
      | recipientType | PF               |
      | denomination  | utenza radd      |
      | taxId         | STTSGT90A01H501J |
      | messageId     | ${NEW-IT-FR}     |
    And destinatario della notifica bonaria
      | recipientType | PF               |
      | taxId         | LVLDAA85T50G702B |
      | denomination  | Livilo Daia      |
      | messageId     | ${NEW-IT}        |
    And destinatario della notifica bonaria
      | recipientType        | PG            |
      | senderDenomination   | PagoPa S.p.A. |
      | senderTaxId          | 15376371009   |
      | payment_multy_number | 1             |
      | messageId            | ${NEW-IT}     |
    And destinatario della notifica bonaria
      | denomination | Leonardo da Vinci |
      | taxId        | DVNLRD52D15M059P  |
      | messageId    | ${NEW-IT-FR}      |
    And destinatario della notifica bonaria
      | denomination | Giovanna D'Arco  |
      | taxId        | DRCGNN12A46A326K |
      | messageId    | ${NEW-IT}        |
    And destinatario della notifica bonaria
      | recipientType        | PF               |
      | denomination         | Test AR Fail 2   |
      | taxId                | NNTNRZ80A01H501D |
      | payment_multy_number | 1                |
      | messageId            | ${NEW-IT}        |
    When l'invio della notifica bonaria fallisce
    Then si riceve errore 400 "PN_DELIVERY_MAX_RECIPIENT_NUMBER_PASSED"

   #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_1_C] Come ente mittente invio una notifica bonaria allegando
  un messaggio multilingue e specificando Italiano come lingua addizionale.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | IT                |
    And destinatario della notifica bonaria
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT-FR} |
      | additionalLanguages | NULL         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_2_A] Come ente mittente invio una notifica bonaria multidestinatario
  allegando un messaggio multilingue indicando la lingua addizionale per un destinatario e italiana per l'altro
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
    And destinatario della notifica bonaria
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT-FR} |
      | additionalLanguages | IT           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"

  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_2_B] Come ente mittente invio una notifica bonaria multidestinatario
  allegando un messaggio multilingue indicando la lingua addizionale per entrambi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
    And destinatario della notifica bonaria
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT-FR} |
      | additionalLanguages | FR           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_2_C] Come ente mittente invio una notifica bonaria multidestinatario
  allegando due diversi messaggi multilingue indicando la lingua addizionale per entrambi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
    And destinatario della notifica bonaria
      | recipientType       | PG             |
      | taxId               | 20517490320    |
      | denomination        | Cucumber srl   |
      | messageId           | ${SAVED-IT-FR} |
      | additionalLanguages | FR             |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_2_D] Come ente mittente invio una notifica bonaria multidestinatario
  allegando un messaggio monolingua indicando la lingua italiana per uno e nessuna lingua per l'altro
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT}         |
      | additionalLanguages | IT                |
    And destinatario della notifica bonaria
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT}    |
      | additionalLanguages | NULL         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"

  #non sarà più possibile l'invio multidestinatario
  Scenario: [NOTIFICHE_LANGUAGES_01_1_C] Come ente mittente invio una notifica bonaria allegando
  un messaggio multilingue e specificando Italiano come lingua addizionale.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | IT                |
    And destinatario della notifica bonaria
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT-FR} |
      | additionalLanguages | NULL         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_2_A] Come ente mittente invio una notifica bonaria multidestinatario
  allegando un messaggio multilingue indicando la lingua addizionale per un destinatario e italiana per l'altro
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
    And destinatario della notifica bonaria
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT-FR} |
      | additionalLanguages | IT           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_2_B] Come ente mittente invio una notifica bonaria multidestinatario
  allegando un messaggio multilingue indicando la lingua addizionale per entrambi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
    And destinatario della notifica bonaria
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT-FR} |
      | additionalLanguages | FR           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_2_C] Come ente mittente invio una notifica bonaria multidestinatario
  allegando due diversi messaggi multilingue indicando la lingua addizionale per entrambi
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | FR                |
    And destinatario della notifica bonaria
      | recipientType       | PG             |
      | taxId               | 20517490320    |
      | denomination        | Cucumber srl   |
      | messageId           | ${SAVED-IT-FR} |
      | additionalLanguages | FR             |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_2_D] Come ente mittente invio una notifica bonaria multidestinatario
  allegando un messaggio monolingua indicando la lingua italiana per uno e nessuna lingua per l'altro
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT}         |
      | additionalLanguages | IT                |
    And destinatario della notifica bonaria
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT}    |
      | additionalLanguages | NULL         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_3_C] Come ente mittente invio una notifica bonaria multidestinatario
  allegando due messaggi multilingua diversi, indicando una lingua non presente per uno di loro
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | DE                |
    And destinatario della notifica bonaria
      | recipientType       | PG           |
      | taxId               | 20517490320  |
      | denomination        | Cucumber srl |
      | messageId           | ${NEW-IT-FR} |
      | additionalLanguages | FR           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "MESSAGE_LANGUAGE_MISMATCH"


  #Al momento l'invio multidestinatario non è previsto
  Scenario: [NOTIFICHE_LANGUAGES_01_3_D] Come ente mittente invio una notifica bonaria multidestinatario
  allegando un messaggio multilingua, indicando una lingua non presente per uno di loro
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId | FattOrd |
    And destinatario della notifica bonaria
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-FR}      |
      | additionalLanguages | DE                |
    And destinatario della notifica bonaria
      | recipientType       | PG             |
      | taxId               | 20517490320    |
      | denomination        | Cucumber srl   |
      | messageId           | ${SAVED-IT-FR} |
      | additionalLanguages | FR             |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "MESSAGE_LANGUAGE_MISMATCH"

  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_LANGUAGES_DE_1_A] Come ente mittente invio una notifica bonaria allegando
  un messaggio multilingue e specificando la seconda lingia come lingua addizionale.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd    |
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-DE}      |
      | additionalLanguages | DE                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"

  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_LANGUAGES_SL_1_A] Come ente mittente invio una notifica bonaria allegando
  un messaggio multilingue e specificando la seconda lingia come lingua addizionale.
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId          | FattOrd    |
      | recipientType       | PF                |
      | taxId               | FRMTTR76M06B715E  |
      | denomination        | Ettore Fieramosca |
      | messageId           | ${NEW-IT-SL}      |
      | additionalLanguages | SL                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"


  @informalNotificationsValidation @informalAsyncValidation
  Scenario: [NOTIFICHE_BONARIE_SM_01_1_H] Come ente mittenste invio una notifica bonaria senza documento e senza pagamneti e allegati
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | document             | NULL              |
      | campaignId           | FattOrd    |
      | recipientType        | PF                |
      | taxId                | FRMTTR76M06B715E  |
      | denomination         | Ettore Fieramosca |
      | payment_multy_number | 0                 |
      | attachment_key       | NULL              |
      | messageId            | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then la sottomissione della notifica bonaria è andata a buon fine