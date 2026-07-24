Feature: Comunicazioni bonarie

  ##############################
  # COMUNICAZIONE BONARIA
  #############################
  # Endpoint definiti in: https://github.com/pagopa/pn-templates-engine/blob/1d71146851778765cd54fa866cf5252fed85762e/README.md

  @templateEngine # /templates-engine-private/v1/templates/informal/analog-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_1] Richiamare l’API per il recupero del template di avviso di cortesia posta cartacea
    When recupero il template per "comunicazione bonaria posta cartacea" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "pdf"

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_2] Richiamare l’API per il recupero del template relativo al body di avviso di cortesia EMAIL
    When recupero il template per "email body comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "html"

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-subject
  Scenario: [COMBO_TEMPLATE_ENGINE_3] Richiamare l’API per il recupero del template relativo all'oggetto di avviso di cortesia EMAIL
    When recupero il template per "email subject comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_4] Richiamare l’API per il recupero del template relativo al body di avviso di cortesia della PEC
    When recupero il template per "pec body comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "html"

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-subject
  Scenario: [COMBO_TEMPLATE_ENGINE_5] Richiamare l’API per il recupero del template relativo all'oggetto di avviso di cortesia della PEC
    When recupero il template per "pec subject comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_6] Richiamare l’API per il recupero del template relativo al body di avviso di cortesia dell'IO
    When recupero il template per "IO comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"

  @templateEngine # /templates-engine-private/v1/templates/informal/sms-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_7] Richiamare l’API per il recupero del template relativo al body di avviso di cortesia dell'SMS
    When recupero il template per "sms comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"

  # ---------- Comunicazione bonaria posta cartacea (schema InformalCommunication) ----------

  @templateEngine # /templates-engine-private/v1/templates/informal/analog-communication
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_8] Verifica dell'intero template comunicazione bonaria posta cartacea per PF e PG per le lingue IT,DE,FR,SL,EN
    When recupero il template per "comunicazione bonaria posta cartacea" in lingua "<language>" con recipient Type "<recipientType>"
    Then verifico che il template è in formato ".pdf"
    And controllo che per il template "comunicazione bonaria posta cartacea" il file "pdf" sia in lingua "<language>"
    Examples:
      | language | recipientType |
      | italiana | PF            |
      | italiana | PG            |
      | francese | PF            |
      | francese | PG            |
      | tedesca  | PF            |
      | tedesca  | PG            |
      | slovena  | PF            |
      | slovena  | PG            |
      | inglese  | PF            |
      | inglese  | PG            |

  @templateEngine # /templates-engine-private/v1/templates/informal/analog-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_8_1] Richiamare l’API per il recupero del template di comunicazione bonaria posta cartacea - lingua non valorizzata
    When recupero il template per "comunicazione bonaria posta cartacea" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/analog-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_8_2] Richiamare l’API per il recupero del template di comunicazione bonaria posta cartacea - body vuoto
    When recupero il template per "comunicazione bonaria posta cartacea" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/analog-communication
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_8_3] Richiamare l’API per il recupero del template di comunicazione bonaria posta cartacea - campi obbligatori non valorizzati
    When recupero il template per "comunicazione bonaria posta cartacea" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che la chiamata sia andata in "400" error
    Examples:
      | fieldName               | fieldValue |
      | iun                     | null       |
      | subject                 | null       |
      | body_primaryContent     | null       |
      | sender_denomination     | null       |
      | sender_id               | null       |
      | sender_service          | null       |
      | recipient_denomination  | null       |
      | recipient_taxId         | null       |
      | recipient_recipientType | null       |
      | hasAttachment           | null       |
      | hasPayment              | null       |

  @templateEngine # /templates-engine-private/v1/templates/informal/analog-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_8_5] Richiamare l’API per il recupero del template di comunicazione bonaria posta cartacea - body con primaryContent e secondaryContent
    When recupero il template per "comunicazione bonaria posta cartacea" con i valori nel request body:
      | body_primaryContent   | string |
      | body_secondaryContent | string |
    Then verifico che il template è in formato ".pdf"
    And il corpo del messaggio contiene il testo "COMUNICAZIONE DA PARTE DI [sender_denomination] Comunicazione inviata tramite Identificativo Univoco Notifica: UTGP-ZRHR-XDNQ-202505-Q-1 Codice fiscale: recipient_taxId notifichedigitali.it recipient_denomination Hai ricevuto una comunicazione da [sender_denomination] con oggetto: [subject] Ciao recipient_denomination, string string CON L'APP IO È ANCORA PIÙ SEMPLICE! Ricevi le prossime comunicazioni dagli enti pubblici, aggiungi i tuoi documenti personali, ottieni bonus e sconti. E in più, paghi e firmi in digitale. Scarica gratis l'app IO!"

  @templateEngine # /templates-engine-private/v1/templates/informal/analog-communication
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_8_6] Richiamare l’API per il recupero del template di comunicazione bonaria posta cartacea - hasAttachment true/false
    When recupero il template per "comunicazione bonaria posta cartacea" con i valori nel request body:
      | hasAttachment | <hasAttachment> |
    Then verifico che il template è in formato ".pdf"
    And il corpo del messaggio contiene il testo "COMUNICAZIONE DA PARTE DI [sender_denomination] Comunicazione inviata tramite Identificativo Univoco Notifica: UTGP-ZRHR-XDNQ-202505-Q-1 Codice fiscale: recipient_taxId notifichedigitali.it recipient_denomination Hai ricevuto una comunicazione da [sender_denomination] con oggetto: [subject] Ciao recipient_denomination, body_primaryContent CON L'APP IO È ANCORA PIÙ SEMPLICE! Ricevi le prossime comunicazioni dagli enti pubblici, aggiungi i tuoi documenti personali, ottieni bonus e sconti. E in più, paghi e firmi in digitale. Scarica gratis l'app IO!"
    Examples:
      | hasAttachment |
      | true          |
      | false         |

  @templateEngine # /templates-engine-private/v1/templates/informal/analog-communication
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_8_7] Richiamare l’API per il recupero del template di comunicazione bonaria posta cartacea - hasPayment true con checkoutUrl / false senza checkoutUrl
    When recupero il template per "comunicazione bonaria posta cartacea" con i valori nel request body:
      | hasPayment  | <hasPayment>  |
      | checkoutUrl | <checkoutUrl> |
    Then verifico che il template è in formato ".pdf"
    And il corpo del messaggio contiene il testo "COMUNICAZIONE DA PARTE DI [sender_denomination] Comunicazione inviata tramite Identificativo Univoco Notifica: UTGP-ZRHR-XDNQ-202505-Q-1 Codice fiscale: recipient_taxId notifichedigitali.it recipient_denomination Hai ricevuto una comunicazione da [sender_denomination] con oggetto: [subject] Ciao recipient_denomination, body_primaryContent CON L'APP IO È ANCORA PIÙ SEMPLICE! Ricevi le prossime comunicazioni dagli enti pubblici, aggiungi i tuoi documenti personali, ottieni bonus e sconti. E in più, paghi e firmi in digitale. Scarica gratis l'app IO!"
    Examples:
      | hasPayment | checkoutUrl |
      | true       | string      |
      | false      | null        |

  # ---------- Email body comunicazione bonaria (schema InformalCommunication) ----------

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_9] Verifica dell'intero template email body comunicazione bonaria per PF e PG per le lingue IT,DE,FR,SL,EN
    When recupero il template per "email body comunicazione bonaria" in lingua "<language>" con recipient Type "<recipientType>"
    Then verifico che il template è in formato "html"
    And controllo che per il template "email body comunicazione bonaria" il file "html" sia in lingua "<language>"
    Examples:
      | language | recipientType |
      | italiana | PF            |
      | italiana | PG            |
      | francese | PF            |
      | francese | PG            |
      | tedesca  | PF            |
      | tedesca  | PG            |
      | slovena  | PF            |
      | slovena  | PG            |
      | inglese  | PF            |
      | inglese  | PG            |

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_9_1] Richiamare l’API per il recupero del template di email body comunicazione bonaria - lingua non valorizzata
    When recupero il template per "email body comunicazione bonaria" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_9_2] Richiamare l’API per il recupero del template di email body comunicazione bonaria - body vuoto
    When recupero il template per "email body comunicazione bonaria" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_9_3] Richiamare l’API per il recupero del template di email body comunicazione bonaria - campi obbligatori non valorizzati
    When recupero il template per "email body comunicazione bonaria" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che la chiamata sia andata in "400" error
    Examples:
      | fieldName               | fieldValue |
      | iun                     | null       |
      | subject                 | null       |
      | body_primaryContent     | null       |
      | sender_denomination     | null       |
      | sender_id               | null       |
      | sender_service          | null       |
      | recipient_denomination  | null       |
      | recipient_taxId         | null       |
      | recipient_recipientType | null       |
      | hasAttachment           | null       |
      | hasPayment              | null       |

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_9_5] Richiamare l’API per il recupero del template di email body comunicazione bonaria - body con primaryContent e secondaryContent
    When recupero il template per "email body comunicazione bonaria" con i valori nel request body:
      | body_primaryContent   | testo_primary_content   |
      | body_secondaryContent | testo_secondary_content |
    Then verifico che il template è in formato "html"
    And il corpo del messaggio contiene il testo "testo_primary_content testo_secondary_content Per avere maggiori informazioni prendi visione degli allegati"

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_9_6] Richiamare l’API per il recupero del template di email body comunicazione bonaria - hasAttachment true/false
    When recupero il template per "email body comunicazione bonaria" con i valori nel request body:
      | hasAttachment | <hasAttachment> |
    Then verifico che il template è in formato "html"
    And il corpo del messaggio contiene il testo "<messageContent>"
    Examples:
      | hasAttachment | messageContent                                                                                                                                                               |
      | true          | subject Ciao recipient_denomination, body_primaryContent Per avere maggiori informazioni prendi visione degli allegati                                                       |
      | false         | subject Ciao recipient_denomination, body_primaryContent In ogni caso, qualora avessi bisogno di assistenza, contatta sender_denomination attraverso i suoi canali ufficiali |

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_9_7] Richiamare l’API per il recupero del template di email body comunicazione bonaria - hasPayment true con checkoutUrl popolato e si verifica che nel template siano presenti i messaggi di pagamento nel corpo del messaggio
    When recupero il template per "email body comunicazione bonaria" con i valori nel request body:
      | hasPayment  | true       |
      | checkoutUrl | paymentUrl |
    Then verifico che il template è in formato "html"
    And il corpo del messaggio contiene il testo "Ciao recipient_denomination"
    And il corpo del messaggio contiene il testo "body_primaryContent"
    And il corpo del messaggio contiene il testo "Puoi effettuare il pagamento direttamente tramite l&#39;app IO o SEND"
    And il corpo del messaggio contiene il testo "In ogni caso, qualora avessi bisogno di assistenza, contatta sender_denomination attraverso i suoi canali ufficiali."
    And il corpo del messaggio contiene il testo "Paga ora"

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_9_8] Richiamare l’API per il recupero del template di email body comunicazione bonaria - hasPayment false con checkoutUrl null e si verifica che non siano presenti i messaggi di pagamento nel corpo del messaggio
    When recupero il template per "email body comunicazione bonaria" con i valori nel request body:
      | hasPayment  | false |
      | checkoutUrl | null  |
    Then verifico che il template è in formato "html"
    And il corpo del messaggio contiene il testo "Ciao recipient_denomination"
    And il corpo del messaggio contiene il testo "body_primaryContent"
    And il corpo del messaggio non contiene il testo "Puoi effettuare il pagamento direttamente tramite l&#39;app IO o SEND"
    And il corpo del messaggio contiene il testo "In ogni caso, qualora avessi bisogno di assistenza, contatta sender_denomination attraverso i suoi canali ufficiali."
    And il corpo del messaggio non contiene il testo "Paga ora"

  # ---------- Pec body comunicazione bonaria (schema InformalCommunication) ----------

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_10] Verifica dell'intero template pec body comunicazione bonaria per PF e PG per le lingue IT,DE,FR,SL,EN
    When recupero il template per "pec body comunicazione bonaria" in lingua "<language>" con recipient Type "<recipientType>"
    Then verifico che il template è in formato "html"
    And controllo che per il template "pec body comunicazione bonaria" il file "html" sia in lingua "<language>"
    Examples:
      | language | recipientType |
      | italiana | PF            |
      | italiana | PG            |
      | francese | PF            |
      | francese | PG            |
      | tedesca  | PF            |
      | tedesca  | PG            |
      | slovena  | PF            |
      | slovena  | PG            |
      | inglese  | PF            |
      | inglese  | PG            |

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_10_1] Richiamare l’API per il recupero del template di pec body comunicazione bonaria - lingua non valorizzata
    When recupero il template per "pec body comunicazione bonaria" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_10_2] Richiamare l’API per il recupero del template di pec body comunicazione bonaria - body vuoto
    When recupero il template per "pec body comunicazione bonaria" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_10_3] Richiamare l’API per il recupero del template di pec body comunicazione bonaria - campi obbligatori non valorizzati
    When recupero il template per "pec body comunicazione bonaria" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che la chiamata sia andata in "400" error
    Examples:
      | fieldName               | fieldValue |
      | iun                     | null       |
      | subject                 | null       |
      | body_primaryContent     | null       |
      | sender_denomination     | null       |
      | sender_id               | null       |
      | sender_service          | null       |
      | recipient_denomination  | null       |
      | recipient_taxId         | null       |
      | recipient_recipientType | null       |
      | hasAttachment           | null       |
      | hasPayment              | null       |

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_10_5] Richiamare l’API per il recupero del template di pec body comunicazione bonaria - body con primaryContent e secondaryContent
    When recupero il template per "pec body comunicazione bonaria" con i valori nel request body:
      | body_primaryContent   | body_primaryContent   |
      | body_secondaryContent | body_secondaryContent |
    Then verifico che il template è in formato "html"
    And il corpo del messaggio contiene il testo "All&#39;attenzione di recipient_denomination"
    And il corpo del messaggio contiene il testo "body_primaryContent"
    And il corpo del messaggio contiene il testo "body_secondaryContent"

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_10_6] Richiamare l’API per il recupero del template di pec body comunicazione bonaria - hasAttachment true/false
    When recupero il template per "pec body comunicazione bonaria" con i valori nel request body:
      | hasAttachment | <hasAttachment> |
    Then verifico che il template è in formato "html"
    And il corpo del messaggio contiene il testo "<message>"
    Examples:
      | hasAttachment | message                                                                                                                                                                                                       |
      | true          | All&#39;attenzione di recipient_denomination, body_primaryContent Per avere maggiori informazioni si consiglia di prendere visione degli allegati , che possono fornire dettagli importanti                   |
      | false         | All&#39;attenzione di recipient_denomination, body_primaryContent In ogni caso, qualora si avesse bisogno di assistenza, &egrave; possibile contattare sender_denomination attraverso i suoi canali ufficiali |

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_10_7] Richiamare l’API per il recupero del template di pec body comunicazione bonaria - hasPayment true con checkoutUrl
    When recupero il template per "pec body comunicazione bonaria" con i valori nel request body:
      | hasPayment  | true       |
      | checkoutUrl | paymentUrl |
    Then verifico che il template è in formato "html"
    And il corpo del messaggio contiene il testo "&Egrave; possibile effettuare il pagamento direttamente tramite SEND , il servizio di notifiche digitali della pubblica amministrazione"
    And il corpo del messaggio contiene il testo "Paga su SEND"

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario: [COMBO_TEMPLATE_ENGINE_10_8] Richiamare l’API per il recupero del template di pec body comunicazione bonaria - hasPayment false senza checkoutUrl
    When recupero il template per "pec body comunicazione bonaria" con i valori nel request body:
      | hasPayment  | false |
      | checkoutUrl | null  |
    Then verifico che il template è in formato "html"
    And il corpo del messaggio contiene il testo "Comunicazione inviata tramite &nbsp; L&#39;indirizzo PEC della tua impresa &egrave; stato registrato come canale di contatto da sender_denomination"
    And il corpo del messaggio non contiene il testo "&Egrave; possibile effettuare il pagamento direttamente tramite SEND , il servizio di notifiche digitali della pubblica amministrazione"
    And il corpo del messaggio non contiene il testo "Paga su SEND"

  # ---------- Email subject comunicazione bonaria (schema InformalEmailCommunicationSubject) ----------

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-subject
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_11] Verifica dell'intero template email subject comunicazione bonaria per le lingue IT,DE,FR,SL,EN
    When recupero il template per "email subject comunicazione bonaria" in lingua "<language>"
    Then verifico che il template è in formato "text"
    And controllo che per il template "email subject comunicazione bonaria" il file "text" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-subject
  Scenario: [COMBO_TEMPLATE_ENGINE_11_1] Richiamare l’API per il recupero del template di email subject comunicazione bonaria - lingua non valorizzata
    When recupero il template per "email subject comunicazione bonaria" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-subject
  Scenario: [COMBO_TEMPLATE_ENGINE_11_2] Richiamare l’API per il recupero del template di email subject comunicazione bonaria - body vuoto
    When recupero il template per "email subject comunicazione bonaria" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-subject
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_11_3] Richiamare l’API per il recupero del template di email subject comunicazione bonaria - campi obbligatori non valorizzati
    When recupero il template per "email subject comunicazione bonaria" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che la chiamata sia andata in "400" error
    Examples:
      | fieldName             | fieldValue |
      | senderDenomination    | null       |
      | recipientDenomination | null       |
      | subject               | null       |

  # ---------- Pec subject comunicazione bonaria (schema InformalEmailCommunicationSubject) ----------

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-subject
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_12] Verifica dell'intero template pec subject comunicazione bonaria per le lingue IT,DE,FR,SL,EN
    When recupero il template per "pec subject comunicazione bonaria" in lingua "<language>"
    Then verifico che il template è in formato "text"
    And controllo che per il template "pec subject comunicazione bonaria" il file "text" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-subject
  Scenario: [COMBO_TEMPLATE_ENGINE_12_1] Richiamare l’API per il recupero del template di pec subject comunicazione bonaria - lingua non valorizzata
    When recupero il template per "pec subject comunicazione bonaria" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-subject
  Scenario: [COMBO_TEMPLATE_ENGINE_12_2] Richiamare l’API per il recupero del template di pec subject comunicazione bonaria - body vuoto
    When recupero il template per "pec subject comunicazione bonaria" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-subject
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_12_3] Richiamare l’API per il recupero del template di pec subject comunicazione bonaria - campi obbligatori non valorizzati
    When recupero il template per "pec subject comunicazione bonaria" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che la chiamata sia andata in "400" error
    Examples:
      | fieldName             | fieldValue |
      | senderDenomination    | null       |
      | recipientDenomination | null       |
      | subject               | null       |

  # ---------- IO comunicazione bonaria (schema InformalCommunication) ----------

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_13] Verifica dell'intero template IO comunicazione bonaria per PF e PG per le lingue IT,DE,FR,SL,EN
    When recupero il template per "IO comunicazione bonaria" in lingua "<language>" con recipient Type "<recipientType>"
    Then verifico che il template è in formato "text"
    And controllo che per il template "IO comunicazione bonaria" il file "text" sia in lingua "<language>"
    Examples:
      | language | recipientType |
      | italiana | PF            |
      | italiana | PG            |
      | francese | PF            |
      | francese | PG            |
      | tedesca  | PF            |
      | tedesca  | PG            |
      | slovena  | PF            |
      | slovena  | PG            |
      | inglese  | PF            |
      | inglese  | PG            |

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_13_1] Richiamare l’API per il recupero del template di IO comunicazione bonaria - lingua non valorizzata
    When recupero il template per "IO comunicazione bonaria" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_13_2] Richiamare l’API per il recupero del template di IO comunicazione bonaria - body vuoto
    When recupero il template per "IO comunicazione bonaria" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_13_3] Richiamare l’API per il recupero del template di IO comunicazione bonaria - campi obbligatori non valorizzati
    When recupero il template per "IO comunicazione bonaria" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che la chiamata sia andata in "400" error
    Examples:
      | fieldName               | fieldValue |
      | iun                     | null       |
      | subject                 | null       |
      | body_primaryContent     | null       |
      | sender_denomination     | null       |
      | sender_id               | null       |
      | sender_service          | null       |
      | recipient_denomination  | null       |
      | recipient_taxId         | null       |
      | recipient_recipientType | null       |
      | hasAttachment           | null       |
      | hasPayment              | null       |

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_13_5] Richiamare l’API per il recupero del template di IO comunicazione bonaria - body con primaryContent e secondaryContent
    When recupero il template per "IO comunicazione bonaria" con i valori nel request body:
      | body_primaryContent   | string |
      | body_secondaryContent | string |
    Then verifico che il template è in formato "text"
    And il corpo del messaggio contiene il testo "Ciao recipient_denomination, string Per avere maggiori informazioni **prendi visione degli allegati**, che possono fornirti dettagli importanti. Ma ricorda: saranno disponibili online per un periodo limitato, quindi salvali sul tuo dispositivo. In ogni caso, qualora avessi bisogno di assistenza, **contatta sender_denomination attraverso i suoi canali ufficiali**."

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_13_6] Richiamare l’API per il recupero del template di IO comunicazione bonaria - hasAttachment true/false
    When recupero il template per "IO comunicazione bonaria" con i valori nel request body:
      | hasAttachment | <hasAttachment> |
    Then verifico che il template è in formato "text"
    And il corpo del messaggio contiene il testo "<messageContent>"
    Examples:
      | hasAttachment | messageContent                                                                                                                                                                                                                                                                                                                                                                             |
      | true          | Ciao recipient_denomination, body_primaryContent Per avere maggiori informazioni **prendi visione degli allegati**, che possono fornirti dettagli importanti. Ma ricorda: saranno disponibili online per un periodo limitato, quindi salvali sul tuo dispositivo. In ogni caso, qualora avessi bisogno di assistenza, **contatta sender_denomination attraverso i suoi canali ufficiali**. |
      | false         | Ciao recipient_denomination, body_primaryContent In ogni caso, qualora avessi bisogno di assistenza, **contatta sender_denomination attraverso i suoi canali ufficiali**.                                                                                                                                                                                                                  |

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_13_7] Richiamare l’API per il recupero del template di IO comunicazione bonaria - hasPayment true con checkoutUrl
    When recupero il template per "IO comunicazione bonaria" con i valori nel request body:
      | hasPayment  | true        |
      | checkoutUrl | checkoutUrl |
    Then verifico che il template è in formato "text"
    And il corpo del messaggio contiene il testo "Ciao recipient_denomination, body_primaryContent Per avere maggiori informazioni **prendi visione degli allegati**, che possono fornirti dettagli importanti. Ma ricorda: saranno disponibili online per un periodo limitato, quindi salvali sul tuo dispositivo. Puoi effettuare il pagamento direttamente sull'app IO premendo **Paga**. In alternativa, puoi utilizzare l'**avviso allegato** per saldare l'importo tramite tutti i canali abilitati a pagoPA. In ogni caso, qualora avessi bisogno di assistenza, **contatta sender_denomination attraverso i suoi canali ufficiali**."

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_13_8] Richiamare l’API per il recupero del template di IO comunicazione bonaria - hasPayment false senza checkoutUrl
    When recupero il template per "IO comunicazione bonaria" con i valori nel request body:
      | hasPayment  | false |
      | checkoutUrl | null  |
    Then verifico che il template è in formato "text"
    And il corpo del messaggio contiene il testo "Ciao recipient_denomination, body_primaryContent Per avere maggiori informazioni **prendi visione degli allegati**, che possono fornirti dettagli importanti. Ma ricorda: saranno disponibili online per un periodo limitato, quindi salvali sul tuo dispositivo. In ogni caso, qualora avessi bisogno di assistenza, **contatta sender_denomination attraverso i suoi canali ufficiali**."

  # ---------- SMS comunicazione bonaria (schema InformalSmsCommunication) ----------

  @templateEngine # /templates-engine-private/v1/templates/informal/sms-communication
  Scenario Outline: [COMBO_TEMPLATE_ENGINE_14] Verifica dell'intero template sms comunicazione bonaria per le lingue IT,DE,FR,SL,EN
    When recupero il template per "sms comunicazione bonaria" in lingua "<language>"
    Then verifico che il template è in formato "text"
    And controllo che per il template "sms comunicazione bonaria" il file "text" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine # /templates-engine-private/v1/templates/informal/sms-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_14_1] Richiamare l’API per il recupero del template di sms comunicazione bonaria - lingua non valorizzata
    When recupero il template per "sms comunicazione bonaria" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/sms-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_14_2] Richiamare l’API per il recupero del template di sms comunicazione bonaria - body vuoto
    When recupero il template per "sms comunicazione bonaria" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/sms-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_14_3] Richiamare l’API per il recupero del template di sms comunicazione bonaria - campo obbligatorio senderPaDenomination non valorizzato
    When recupero il template per "sms comunicazione bonaria" con i valori nel request body:
      | senderPaDenomination | null |
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine # /templates-engine-private/v1/templates/informal/sms-communication
  Scenario: [COMBO_TEMPLATE_ENGINE_14_4] Richiamare l’API per il recupero del template di sms comunicazione bonaria - campo obbligatorio recipientType non valorizzato
    When recupero il template per "sms comunicazione bonaria" con i valori nel request body:
      | recipientType | null |
    Then verifico che la chiamata sia andata in "400" error


