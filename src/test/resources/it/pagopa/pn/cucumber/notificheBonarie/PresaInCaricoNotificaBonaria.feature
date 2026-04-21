Feature: Sottomissione di una notifica bonaria.


  Scenario: [NOTIFICHE_BONARIE_TEST_01] Creazione di un messaggio tutti i campi compilati

    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject             | primary_long_body                       | primary_short_body                      | primary_language | additional_subject | additional_long_body                    |
      | Sollecito di pagamento 2023 | Gentile cittadino, la informiamo che... | Sollecito: hai una nuova comunicazione. | it               | Sollecito.         | Gentile cittadino, la informiamo che... |


  Scenario: [NOTIFICHE_BONARIE_TEST_02] Invio di una notifica bonaria tutti i campi compilati
    Given viene creata una nuova notifica bonaria
      | senderDenomination | senderTaxId | paProtocolNumber | idempotenceToken | campaignId | messageId                            | subject        | additional_language | recipient_type | recipient_tax_id | recipient_denomination | pec_address       | pagopa_notice_code | pagopa_creditor_tax_id | document_title      | document_docidx | group      |
      | Comune di Milano   | 77777777777 | PROT-123         | TOKEN-ABC        | CAMP-001   | 3fa85f64-5717-4562-b3fc-2c963f66afa6 | Sollecito Tari | fr                  | PF             | 57143494439      | Mario Rossi            | account@domain.it | 302000100000019421 | 77777777777            | Avviso di pagamento | 1               | TEST-GROUP |
    Then viene inviata una nuova notifica bonaria

  Scenario: [RETURNED-TO-SENDER_9] Invio notifica 890 multi-destinatario aventi stati Inviata e Deceduto e macro stato mostrato DELIVERED
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Cucumber
      | physicalAddress_address | @FAIL_DECEDUTO_890 |
      | digitalDomicile         | NULL               |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"


# test progettati..


#  SCENARIO 2 - Inserimento di un Messaggio.
#  CASO DI TEST 2.1  Creazione di nuovo un messaggio.

  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_06_1] Creazione di un nuovo messaggio con valori di default.
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject                       |
      | Nuovo messaggio per notifiche bonarie |
    And l'operazione non ha generato errori
    Then tento il recupero del messaggio precedentemente creato per le comunicazioni bonarie
    And l'operazione non ha generato errori


  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_06_2] Creazione di un nuovo messaggio con valori di default con seconda lingua non specificata
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | additional_language |
      | Messaggio bonario | NULL                |
    Then l'operazione non ha generato errori


#  CASO DI TEST 2.2  Errore sulla Creazione di un messaggio.
  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_07_1] Errore sulla creazione di un nuovo messaggio per ente non abilitato
    #todo t bonarie


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_07_2] Errore sulla creazione di un nuovo messaggio per campi obbligatori non valorizzati
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | primary_long_body   | primary_short_body   |
      | <primary_subject> | <primary_long_body> | <primary_short_body> |
    Then si riceve errore 400
    Examples:
      | primary_subject | primary_long_body | primary_short_body |
      | NULL            |                   |                    |
      |                 | NULL              |                    |
      |                 |                   | NULL               |


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_07_3] Errore sulla creazione di un nuovo messaggio per campi relativi al messaggio principale non conformi
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | primary_subject   | primary_long_body   | primary_short_body   |
      | <primary_subject> | <primary_long_body> | <primary_short_body> |
    Then si riceve errore 400
    Examples:
      | primary_subject | primary_long_body | primary_short_body |
      | 257_CHAR        |                   |                    |
      |                 | 10001_CHAR        |                    |
      |                 |                   | 161_CHAR           |


  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_07_4] Errore sulla creazione di un nuovo messaggio per campi relativi al messaggio secondario non conformi
    Given si tenta la creazione di un nuovo messaggio per le comunicazioni bonarie
      | additional_subject   | additional_long_body   | additional_short_body   |
      | <additional_subject> | <additional_long_body> | <additional_short_body> |
    Then si riceve errore 400
    Examples:
      | additional_subject | additional_long_body | additional_short_body |
      | 257_CHAR           |                      |                       |
      |                    | 10001_CHAR           |                       |
      |                    |                      | 161_CHAR              |

  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_07_4] Errore sulla creazione di un nuovo messaggio per campi relativi alla lingua non conformi
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


#  SCENARIO 3 - Lettura di un Messaggio.
#  CASO DI TEST 3.1  Recupero di un messaggio.
# implementato con [NOTIFICHE_BONARIE_MESSAGGI_06_1]


#  CASO DI TEST 3.2 Errore nel recupero di un messaggio.
  Scenario Outline: [NOTIFICHE_BONARIE_MESSAGGI_09] Errore nel recuperare un messaggio per id non valido.
    Given tento il recupero del messaggio per le comunicazioni bonarie con message id "<messageId>"
    Then si riceve errore 400
    Examples:
      | messageId |



#  SCENARIO  - Sottomissione di una notifica bonaria.
#  CASO DI TEST .1 Validazione della richiesta di invio notifica bonaria.
  Scenario: [NOTIFICHE_BONARIE_SOTTOMISSIONE_14] Invio di una notifica bonaria con campi di default
    Given viene creata una nuova notifica bonaria
      | senderDenomination |
      | Comune di Milano   |
    Then viene inviata una nuova notifica bonaria







#  SCENARIO 1 - Preload del documento.
#  CASO DI TEST 1.1  Invio dei documenti allegati al destinatario.
  Scenario: [NOTIFICHE_BONARIE_MESSAGGI_05] Creazione di un nuovo messaggio con valori di default.

#  CASO DI TEST .1 Mancata validazione della richiesta di invio notifica bonaria.
  Scenario: [NOTIFICHE_BONARIE_15] Invio di una notifica bonaria con messaggio predefinito di campagna

#  SCENARIO 5 - Download dei documenti.
#  CASO DI TEST 5.1 Corretto Download dei documenti notificati.
  Scenario: [NOTIFICHE_BONARIE_10] Invio di una notifica bonaria con messaggio predefinito di campagna

#  CASO DI TEST 5.2 Corretto Download degli allegati di pagamento di una notifica.
  Scenario: [NOTIFICHE_BONARIE_11] Invio di una notifica bonaria con messaggio predefinito di campagna

#  CASO DI TEST 5.3 Errore Download dei documenti.
  Scenario: [NOTIFICHE_BONARIE_12] Invio di una notifica bonaria con messaggio predefinito di campagna

#  CASO DI TEST 5.4 Errore Download degli allegati di pagamento.
  Scenario: [NOTIFICHE_BONARIE_13] Invio di una notifica bonaria con messaggio predefinito di campagna





