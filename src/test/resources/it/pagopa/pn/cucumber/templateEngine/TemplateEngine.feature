Feature: Template engine

  @templateEngine #18 19 20 21 /templates-engine-private/v1/templates/notification-received-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_1] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "attestazione opponibile a terzi di notifica presa in carico" di tipo "<notificationType>" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    And controllo che per il template "attestazione opponibile a terzi di notifica presa in carico" il file "pdf" sia in lingua "<language>"
    And controllo che la notifica "<notificationType>" abbia i giusti campi valorizzati
    Examples:
      | language | notificationType  |
      | italiana | multidestinatario |
      | tedesca  | monodestinatario  |
      | slovena  | singolo allegato  |
      | francese | piu allegati      |

  @templateEngine #22 - 23 /templates-engine-private/v1/templates/notification-received-legal-fact
  Scenario: [TEMPLATE-ENGINE_2] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - lingua errata - lingua vuota
    When recupero il template per "attestazione opponibile a terzi di notifica presa in carico" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #86 /templates-engine-private/v1/templates/notification-received-legal-fact
  Scenario: [TEMPLATE-ENGINE_2_1] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - body vuoto
    When recupero il template per "attestazione opponibile a terzi di notifica presa in carico" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #106 /templates-engine-private/v1/templates/notification-received-legal-fact
  Scenario: [TEMPLATE-ENGINE_2_2] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - body errato
    When recupero il template per "attestazione opponibile a terzi di notifica presa in carico" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #119 /templates-engine-private/v1/templates/notification-received-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_2_3] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - not required fields
    When recupero il template per "attestazione opponibile a terzi di notifica presa in carico" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che il template è in formato ".pdf"
    And controllo che nel file "pdf" contenga il campo "<fieldToFind>" valorizzato a "<fieldValueToFind>"
    Examples:
      | fieldName                                       | fieldValue | fieldToFind        | fieldValueToFind         |
      | notification_recipient_physicalAddress          | null       | Indirizzo fisico   | non presente             |
      | notification_recipient_digitalDomicile_address  | null       | Domicilio digitale | non fornito dalla PA     |
      | notification_recipient_digitalDomicile          | null       | Domicilio digitale | non fornito dalla PA     |

  @templateEngine #24 25 26 27 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_3] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "attestazione opponibile a terzi di notifica digitale" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    And controllo che per il template "attestazione opponibile a terzi di notifica digitale" il file "pdf" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #28 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario: [TEMPLATE-ENGINE_4] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - lingua errata
    When recupero il template per "attestazione opponibile a terzi di notifica digitale" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #87 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario: [TEMPLATE-ENGINE_4_1] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - body vuoto
    When recupero il template per "attestazione opponibile a terzi di notifica digitale" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #107 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario: [TEMPLATE-ENGINE_4_2] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - body errato
    When recupero il template per "attestazione opponibile a terzi di notifica digitale" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #120 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_4_3] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - not required fields
    When recupero il template per "attestazione opponibile a terzi di notifica digitale" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che il template è in formato ".pdf"
    And controllo che nel file "pdf" contenga il campo "<fieldToFind>" valorizzato a "<fieldValueToFind>"
    Examples:
      | fieldName                  | fieldValue | fieldToFind                       | fieldValueToFind         |
      | delivery_addressSource     | null       | Tipologia di domicilio digitale   | non presente             |
      | delivery_address           | null       | Domicilio digitale                | non presente             |

  @templateEngine #121 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_4_4] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - required fields
    When recupero il template per "attestazione opponibile a terzi di notifica digitale" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che la chiamata sia andata in "500" error
    Examples:
      | fieldName                  | fieldValue |
      | context_endWorkflowDate    | null       |
      | delivery_type              | null       |

  @templateEngine #122 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_4_5] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - if context_endWorkflowStatus = SUCCESS
    When recupero il template per "attestazione opponibile a terzi di notifica digitale" con i valori nel request body:
      | context_endWorkflowStatus    | SUCCESS            |
      | context_endWorkflowDate      | <endWorkflowDate>  |
    Then verifico che il template è in formato ".pdf"
    And controllo che nel file "pdf" contenga il testo "finale" valorizzato con "il relativo avviso di avvenuta ricezione in formato elettronico è stato consegnato in data string al domicilio digitale indicato immediatamente sopra la presente data. Firmato digitalmente da PagoPA S.p.A."
    Examples:
      | endWorkflowDate  |
      | null             |
      | string           |

  @templateEngine #123 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_4_6] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - if delivery_address = null
    When recupero il template per "attestazione opponibile a terzi di notifica digitale" con i valori nel request body:
      | delivery_address | null      |
      | delivery_type    | <type>    |
    Then verifico che il template è in formato ".pdf"
    And controllo che nel file "pdf" contenga il campo "Domicilio digitale" valorizzato a "non presente"
    And controllo che nel file "pdf" contenga il testo "finale" valorizzato con "In data string il gestore della piattaforma ha reso disponibile l’avviso di mancato recapito del messaggio ai sensi dell’ art. 26, comma 6 del D.L. 76 del 16 luglio 2020. Firmato digitalmente da PagoPA S.p.A."
    Examples:
      | type          |
      | null          |
      | string        |

  @templateEngine #29 30 31 32 /templates-engine-private/v1/templates/notification-viewed-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_5] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di avvenuto accesso - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "attestazione opponibile a terzi di avvenuto accesso" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    And controllo che per il template "attestazione opponibile a terzi di avvenuto accesso" il file "pdf" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #33 /templates-engine-private/v1/templates/notification-viewed-legal-fact
  Scenario: [TEMPLATE-ENGINE_6] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di avvenuto accesso - lingua errata
    When recupero il template per "attestazione opponibile a terzi di avvenuto accesso" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #88 /templates-engine-private/v1/templates/notification-viewed-legal-fact
  Scenario: [TEMPLATE-ENGINE_6_1] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di avvenuto accesso - body vuoto
    When recupero il template per "attestazione opponibile a terzi di avvenuto accesso" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #108 /templates-engine-private/v1/templates/notification-viewed-legal-fact
  Scenario: [TEMPLATE-ENGINE_6_2] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di avvenuto accesso - body errato
    When recupero il template per "attestazione opponibile a terzi di avvenuto accesso" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #124 /templates-engine-private/v1/templates/notification-viewed-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_6_3] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di avvenuto accesso - field not required
    When recupero il template per "attestazione opponibile a terzi di avvenuto accesso" con i valori nel request body:
      | <fieldName> | <fieldValue> |
    Then verifico che il template è in formato ".pdf"
    And controllo che nel file "pdf" contenga il testo "delegato" valorizzato con "<fieldExpectedValue>"
    Examples:
      | fieldName                          | fieldValue | fieldExpectedValue   |
      | context_delegate                   | null       | destinatario         |
      | context_delegate                   | string     | delegato             |

  @templateEngine #34 35 36 37 /templates-engine-private/v1/templates/legal-fact-malfunction
  Scenario Outline: [TEMPLATE-ENGINE_7] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di malfunzionamento e ripristino - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "attestazione opponibile a terzi di malfunzionamento e ripristino" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    And controllo che per il template "attestazione opponibile a terzi di malfunzionamento e ripristino" il file "pdf" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #38 /templates-engine-private/v1/templates/legal-fact-malfunction
  Scenario: [TEMPLATE-ENGINE_8] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di malfunzionamento e ripristino - lingua errata
    When recupero il template per "attestazione opponibile a terzi di malfunzionamento e ripristino" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #89 /templates-engine-private/v1/templates/legal-fact-malfunction
  Scenario: [TEMPLATE-ENGINE_8_1] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di malfunzionamento e ripristino - body vuoto
    When recupero il template per "attestazione opponibile a terzi di malfunzionamento e ripristino" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #109 /templates-engine-private/v1/templates/legal-fact-malfunction
  Scenario: [TEMPLATE-ENGINE_8_2] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di malfunzionamento e ripristino - body errato
    When recupero il template per "attestazione opponibile a terzi di malfunzionamento e ripristino" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #39 40 41 42 /templates-engine-private/v1/templates/notification-cancelled-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_9] Richiamare l’API per il recupero del template della dichiarazione di annullamento notifica - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "dichiarazione di annullamento notifica" di tipo "<notificationType>" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    And controllo che per il template "dichiarazione di annullamento notifica" il file "pdf" sia in lingua "<language>"
    And controllo che la notifica "<notificationType>" abbia i giusti campi valorizzati
    Examples:
      | language | notificationType  |
      | italiana | multidestinatario |
      | tedesca  | multidestinatario  |
      | slovena  | monodestinatario  |
      | francese | monodestinatario  |
      | inglese  | monodestinatario  |

  @templateEngine #43 /templates-engine-private/v1/templates/notification-cancelled-legal-fact
  Scenario: [TEMPLATE-ENGINE_10] Richiamare l’API per il recupero del template della dichiarazione di annullamento notifica - lingua errata
    When recupero il template per "dichiarazione di annullamento notifica" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #90 /templates-engine-private/v1/templates/notification-cancelled-legal-fact
  Scenario: [TEMPLATE-ENGINE_10_1] Richiamare l’API per il recupero del template della dichiarazione di annullamento notifica - body vuoto
    When recupero il template per "dichiarazione di annullamento notifica" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #110 /templates-engine-private/v1/templates/notification-cancelled-legal-fact
  Scenario: [TEMPLATE-ENGINE_10_2] Richiamare l’API per il recupero del template della dichiarazione di annullamento notifica - body errato
    When recupero il template per "dichiarazione di annullamento notifica" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #44 45 46 47 /templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_11] Richiamare l’API per il recupero del template del deposito di avvenuta ricezione - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "deposito di avvenuta ricezione" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    And controllo che per il template "deposito di avvenuta ricezione" il file "pdf" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #48 /templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact
  Scenario: [TEMPLATE-ENGINE_12] Richiamare l’API per il recupero del template del deposito di avvenuta ricezione - lingua errata
    When recupero il template per "deposito di avvenuta ricezione" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #91 /templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact
  Scenario: [TEMPLATE-ENGINE_12_1] Richiamare l’API per il recupero del template del deposito di avvenuta ricezione - body vuoto
    When recupero il template per "deposito di avvenuta ricezione" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #111 /templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact
  Scenario: [TEMPLATE-ENGINE_12_2] Richiamare l’API per il recupero del template del deposito di avvenuta ricezione - body errato
    When recupero il template per "deposito di avvenuta ricezione" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #49 50 51 52 /templates-engine-private/v1/templates/notification-aar
  Scenario Outline: [TEMPLATE-ENGINE_13] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di avvenuta ricezione" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    And controllo che per il template "avviso di avvenuta ricezione" il file "pdf" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #53 /templates-engine-private/v1/templates/notification-aar
  Scenario: [TEMPLATE-ENGINE_14] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione - lingua errata
    When recupero il template per "avviso di avvenuta ricezione" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #92 /templates-engine-private/v1/templates/notification-aar
  Scenario: [TEMPLATE-ENGINE_14_1] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione - body vuoto
    When recupero il template per "avviso di avvenuta ricezione" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #112 /templates-engine-private/v1/templates/notification-aar
  Scenario: [TEMPLATE-ENGINE_14_2] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione - body errato
    When recupero il template per "avviso di avvenuta ricezione" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #54 55 56 57 /templates-engine-private/v1/templates/notification-aar-radd-alt
  Scenario Outline: [TEMPLATE-ENGINE_15] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione RADD
    When recupero il template per "avviso di avvenuta ricezione RADD" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    And controllo che per il template "avviso di avvenuta ricezione RADD" il file "pdf" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #58 /templates-engine-private/v1/templates/notification-aar-radd-alt
  Scenario: [TEMPLATE-ENGINE_16] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione RADD - lingua errata
    When recupero il template per "avviso di avvenuta ricezione RADD" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #93 /templates-engine-private/v1/templates/notification-aar-radd-alt
  Scenario: [TEMPLATE-ENGINE_16_1] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione RADD - body vuoto
    When recupero il template per "avviso di avvenuta ricezione RADD" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #113 /templates-engine-private/v1/templates/notification-aar-radd-alt
  Scenario: [TEMPLATE-ENGINE_16_2] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione RADD - body errato
    When recupero il template per "avviso di avvenuta ricezione RADD" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #59 60 61 62 templates-engine-private/v1/templates/notification-aar-for-email
  Scenario Outline: [TEMPLATE-ENGINE_17] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia EMAIL" in lingua "<language>"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia EMAIL" il file "html" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #63 /templates-engine-private/v1/templates/notificationAARForEMAIL
  Scenario: [TEMPLATE-ENGINE_18] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua errata
    When recupero il template per "avviso di cortesia EMAIL" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #94 /templates-engine-private/v1/templates/notificationAARForEMAIL
  Scenario: [TEMPLATE-ENGINE_18_1] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body vuoto
    When recupero il template per "avviso di cortesia EMAIL" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #114 /templates-engine-private/v1/templates/notificationAARForEMAIL
  Scenario: [TEMPLATE-ENGINE_18_2] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body errato
    When recupero il template per "avviso di cortesia EMAIL" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #64 65 66 67 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario Outline: [TEMPLATE-ENGINE_19] Richiamare l’API per il recupero del template di avviso di cortesia PEC - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia PEC" in lingua "<language>"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia PEC" il file "html" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |


  @templateEngine #68 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario: [TEMPLATE-ENGINE_20] Richiamare l’API per il recupero del template di avviso di cortesia PEC - lingua errata
    When recupero il template per "avviso di cortesia PEC" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #95 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario Outline: [TEMPLATE-ENGINE_20_1] Richiamare l’API per il recupero del template di avviso di cortesia PEC - body vuoto
    When recupero il template per "avviso di cortesia PEC" in lingua "<language>" con il body "null"
    Then verifico che la chiamata sia andata in "400" error
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #115 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario: [TEMPLATE-ENGINE_20_2] Richiamare l’API per il recupero del template di avviso di cortesia PEC - body errato
    When recupero il template per "avviso di cortesia PEC" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #69 /templates-engine-private/v1/templates/emailbody
  Scenario Outline: [TEMPLATE-ENGINE_21] Richiamare l’API per il recupero del template di OTP di conferma email - lingua italiana
    When recupero il template per "OTP di conferma email" in lingua "<language>"
    Then verifico che il template è in formato "html"
    And controllo che per il template "OTP di conferma email" il file "html" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #96 /templates-engine-private/v1/templates/emailbody
  Scenario Outline: [TEMPLATE-ENGINE_22_1] Richiamare l’API per il recupero del template di OTP di conferma email - body vuoto
    When recupero il template per "OTP di conferma email" in lingua "<language>" con il body "null"
    Then verifico che la chiamata sia andata in "400" error
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #116 /templates-engine-private/v1/templates/emailbody
  Scenario: [TEMPLATE-ENGINE_22_2] Richiamare l’API per il recupero del template di OTP di conferma email - body errato
    When recupero il template per "OTP di conferma email" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #71 /templates-engine-private/v1/templates/pec-verification-code-body
  Scenario Outline: [TEMPLATE-ENGINE_23] Richiamare l’API per il recupero del template di OTP di conferma pec - lingua italiana
    When recupero il template per "OTP di conferma pec" in lingua "<language>"
    Then verifico che il template è in formato "html"
    And controllo che per il template "OTP di conferma pec" il file "html" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #97 /templates-engine-private/v1/templates/pec-verification-code-body
  Scenario Outline: [TEMPLATE-ENGINE_24_1] Richiamare l’API per il recupero del template di OTP di conferma pec - body vuoto
    When recupero il template per "OTP di conferma pec" in lingua "<language>" con il body "null"
    Then verifico che la chiamata sia andata in "400" error
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #117 /templates-engine-private/v1/templates/pec-verification-code-body
  Scenario: [TEMPLATE-ENGINE_24_2] Richiamare l’API per il recupero del template di OTP di conferma pec - body errato
    When recupero il template per "OTP di conferma pec" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #73 /templates-engine-private/v1/templates/pecbodyconfirm
  Scenario Outline: [TEMPLATE-ENGINE_25] Richiamare l’API per il recupero del template di PEC valida - lingua italiana
    When recupero il template per "PEC valida" in lingua "<language>"
    Then verifico che il template è in formato "html"
    And controllo che per il template "PEC valida" il file "html" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #75 /templates-engine-private/v1/templates/pecbodyreject
  Scenario Outline: [TEMPLATE-ENGINE_27] Richiamare l’API per il recupero del template di PEC non valida - lingua italiana
    When recupero il template per "PEC non valida" in lingua "<language>"
    Then verifico che il template è in formato "html"
    And controllo che per il template "PEC non valida" il file "hmtl" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #77 templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_29] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana
    When recupero il template per "avviso di cortesia SMS" in lingua "italiana"
    Then verifico che il template è in formato "text"
    And controllo che per il template "avviso di cortesia SMS" il file "text" sia in lingua "italiana"

  @templateEngine #78 /templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_30] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua errata
    When recupero il template per "avviso di cortesia SMS" in lingua "tedesca"
    Then verifico che il template è in formato "text"
    And controllo che per il template "avviso di cortesia SMS" il file "text" sia in lingua "italiana"

  @templateEngine #99 /templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_30_1] Richiamare l’API per il recupero del template di avviso di cortesia SMS - body vuoto
    When recupero il template per "avviso di cortesia SMS" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #119 /templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_30_2] Richiamare l’API per il recupero del template di avviso di cortesia SMS - body errato
    When recupero il template per "avviso di cortesia SMS" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #79 /templates-engine-private/v1/templates/smsbody
  Scenario Outline: [TEMPLATE-ENGINE_31] Richiamare l’API per il recupero del template di OTP di conferma sms - lingua italiana
    When recupero il template per "OTP di conferma sms" in lingua "<language>"
    Then verifico che il template è in formato "text"
    And controllo che per il template "OTP di conferma sms" il file "text" sia in lingua "italiana"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #80 /templates-engine-private/v1/templates/smsbody
  Scenario: [TEMPLATE-ENGINE_32] Richiamare l’API per il recupero del template di OTP di conferma sms - lingua null
    When recupero il template per "OTP di conferma sms" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #80 /templates-engine-private/v1/templates/smsbody
  Scenario: [TEMPLATE-ENGINE_32_1] Richiamare l’API per il recupero del template di OTP di conferma sms - lingua errata
    When recupero il template per "OTP di conferma sms" in lingua "slovena"
    Then verifico che il template è in formato "text"
    And controllo che per il template "OTP di conferma sms" il file "text" sia in lingua "italiana"

  @templateEngine #81 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_33] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS
    When recupero l'oggetto per "avviso di cortesia per email object" in lingua "italiana"
    Then verifico che il template è in formato "text"
    And controllo che per il template "avviso di cortesia per email object" il file "text" sia in lingua "italiana"

  @templateEngine #100 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_33_1] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS - lingua errata
    When recupero l'oggetto per "avviso di cortesia per email object" in lingua "francese"
    Then verifico che il template è in formato "text"
    And controllo che per il template "avviso di cortesia per email object" il file "text" sia in lingua "francese"

  @templateEngine #100 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_33_2] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS - body vuoto
    When recupero l'oggetto per "avviso di cortesia per email object" in lingua "italiana" con il body "vuoto"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #120 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_33_3] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS - body errato
    When recupero l'oggetto per "avviso di cortesia per email object" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #82 /templates-engine-private/v1/templates/emailsubject
  Scenario Outline: [TEMPLATE-ENGINE_34] Richiamare l’API per il recupero dell’oggetto relativo all’OTP di conferma email
    When recupero l'oggetto per "OTP di conferma email object" in lingua "<language>"
    Then verifico che il template è in formato "text"
    And controllo che per il template "OTP di conferma email object" il file "text" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #83 /templates-engine-private/v1/templates/pecsubject
  Scenario Outline: [TEMPLATE-ENGINE_35] Richiamare l’API per il recupero dell’oggetto relativo all’OTP di conferma pec
    When recupero l'oggetto per "OTP di conferma pec object" in lingua "<language>"
    Then verifico che il template è in formato "text"
    And controllo che per il template "OTP di conferma pec object" il file "text" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #84 /templates-engine-private/v1/templates/pecsubjectconfirm
  Scenario Outline: [TEMPLATE-ENGINE_36] Richiamare l’API per il recupero dell’oggetto relativo alla PEC valida
    When recupero l'oggetto per "PEC valida object" in lingua "<language>"
    Then verifico che il template è in formato "text"
    And controllo che per il template "PEC valida object" il file "text" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

  @templateEngine #85 /templates-engine-private/v1/templates/pecsubjectreject
  Scenario Outline: [TEMPLATE-ENGINE_37] Richiamare l’API per il recupero dell’oggetto relativo alla PEC non valida
    When recupero l'oggetto per "PEC non valida object" in lingua "<language>"
    Then verifico che il template è in formato "text"
    And controllo che per il template "PEC non valida object" il file "text" sia in lingua "<language>"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |
      | inglese  |

    # COURTESY MESSAGE

  #/templates-engine-private/v1/templates/notification-aar-for-email-digital
  #/templates-engine-private/v1/templates/notification-aar-for-sms-digital
  #/templates-engine-private/v1/templates/notification-aar-for-email
  #/templates-engine-private/v1/templates/notification-aar-for-sms

#OFF as is @courtesyMessageFlagOFF
# /templates-engine-private/v1/templates/notification-aar-for-email
# /templates-engine-private/v1/templates/notification-aar-for-sms


  @templateEngine @templateEngineCM # con flag courtesy message:ON - templates-engine-private/v1/templates/notification-aar-for-sms-digital
  Scenario: [TEMPLATE-ENGINE_38] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana
    When recupero il template per "AVVISO DI CORTESIA SMS DIGITALE" in lingua "tedesca" con recipient Type "PG"
    Then verifico che il template è in formato "text"
    And controllo che per il template "AVVISO DI CORTESIA SMS DIGITALE" il file "text" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "accedi a SEND"

  @templateEngine @templateEngineCM # con flag courtesy message:ON - templates-engine-private/v1/templates/notification-aar-for-email-digital
  Scenario: [TEMPLATE-ENGINE_39] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia EMAIL digitale" in lingua "italiana" con recipient Type "PG"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia EMAIL digitale" il file "html" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "una comunicazione a valore legale da parte di"

  @templateEngine @templateEngineCM #Per flusso Analogico o con flag courtesy message:OFF - templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_40] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana (PG)
    When recupero il template per "avviso di cortesia SMS" in lingua "italiana" con recipient Type "PG"
    Then verifico che il template è in formato "text"
    And controllo che per il template "avviso di cortesia SMS" il file "text" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "accedi con SPID"

  @templateEngine @templateEngineCM #Per flusso Analogico o con flag courtesy message:OFF - templates-engine-private/v1/templates/notification-aar-for-email
  Scenario: [TEMPLATE-ENGINE_41] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia EMAIL" in lingua "italiana" con recipient Type "PG"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia EMAIL" il file "html" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "una notifica da parte di"

  @templateEngine @templateEngineCM
  Scenario: [TEMPLATE-ENGINE_42] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana
    When recupero il template per "AVVISO DI CORTESIA SMS DIGITALE" in lingua "slovena" con recipient Type "PG"
    Then verifico che il template è in formato "text"
    And controllo che per il template "AVVISO DI CORTESIA SMS DIGITALE" il file "text" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "accedi a SEND"

  @templateEngine @templateEngineCM
  Scenario: [TEMPLATE-ENGINE_43] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana
    When recupero il template per "AVVISO DI CORTESIA SMS DIGITALE" in lingua "francese" con recipient Type "PG"
    Then verifico che il template è in formato "text"
    And controllo che per il template "AVVISO DI CORTESIA SMS DIGITALE" il file "text" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "accedi a SEND"



# ...................SERCQ Fase 2 step 2..............



  @templateEngine @templateEngineCM #templates-engine-private/v1/templates/notification-aar-for-sms-digital
  Scenario: [TEMPLATE-ENGINE_38] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana
    When recupero il template per "AVVISO DI CORTESIA SMS DIGITALE" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"
    And controllo che per il template "AVVISO DI CORTESIA SMS DIGITALE" il file "text" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "accedi a SEND"

  @templateEngine @templateEngineCM #templates-engine-private/v1/templates/notification-aar-for-email-digital
  Scenario: [TEMPLATE-ENGINE_39] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia EMAIL digitale" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia EMAIL digitale" il file "html" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "una comunicazione a valore legale da parte di"

  @templateEngine @templateEngineCM #templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_40] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana
    When recupero il template per "avviso di cortesia SMS" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"
    And controllo che per il template "avviso di cortesia SMS" il file "text" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "accedi con SPID"

  @templateEngine @templateEngineCM #Per flusso Analogico o con flag courtesy message:OFF - templates-engine-private/v1/templates/notification-aar-for-email
  Scenario: [TEMPLATE-ENGINE_41] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese (PF)
    When recupero il template per "avviso di cortesia EMAIL" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia EMAIL" il file "html" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "una notifica da parte di"

  @templateEngine
  Scenario Outline: [TEMPLATE-ENGINE_42] Verifica dell'intero template AAR-NO-RADD per PF e PG per le lingue IT,DE,FR,SL
  When recupero il template per "avviso di avvenuta ricezione" in lingua "<language>" con recipient Type "<recipientType>"
  Then verifico che il template è in formato ".pdf"
  And controllo che per il template "avviso di avvenuta ricezione" il file "pdf" sia in lingua "<language>"
  Examples:
  | language | recipientType |
  | italiana | PF |
  | italiana | PG |
  | francese | PF |
  | francese | PG |
  | tedesca | PF |
  | tedesca | PG |
  | slovena | PF |
  | slovena | PG |
  | inglese | PF |
  | inglese | PG |

  @templateEngine
  Scenario Outline: [TEMPLATE-ENGINE_43B] Verifica dell'intero template AAR-RADD per PF e PG per le lingue IT,DE,FR,SL,EN
  When recupero il template per "avviso di avvenuta ricezione RADD" in lingua "<language>" con recipient Type "<recipientType>"
  Then verifico che il template è in formato ".pdf"
  And controllo che per il template "avviso di avvenuta ricezione RADD" il file "pdf" sia in lingua "<language>"
  Examples:
    | language | recipientType |
    | italiana | PF |
    | italiana | PG |
    | francese | PF |
    | francese | PG |
    | tedesca | PF |
    | tedesca | PG |
    | slovena | PF |
    | slovena | PG |
    | inglese | PF |
    | inglese | PG |




  @templateEngine @templateEngineCM #templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_44] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua inglese
    When recupero il template per "avviso di cortesia SMS" in lingua "inglese" con recipient Type "PF"
    Then verifico che il template è in formato "text"
    And controllo che per il template "avviso di cortesia SMS" il file "text" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "accedi con SPID"

  @templateEngine @templateEngineCM #Per flusso Analogico o con flag courtesy message:OFF - templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_45] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua inglese (PG)
    When recupero il template per "avviso di cortesia SMS" in lingua "inglese" con recipient Type "PG"
    Then verifico che il template è in formato "text"
    And controllo che per il template "avviso di cortesia SMS" il file "text" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "accedi con SPID"

  @templateEngine @templateEngineCM # con flag courtesy message:ON - templates-engine-private/v1/templates/notification-aar-for-email-digital
  Scenario: [TEMPLATE-ENGINE_46] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia EMAIL digitale" in lingua "inglese" con recipient Type "PG"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia EMAIL digitale" il file "html" sia in lingua "inglese"
    And il corpo del messaggio contiene il testo "una comunicazione a valore legale da parte di"

  @templateEngine @templateEngineCM #Per flusso Analogico o con flag courtesy message:OFF - templates-engine-private/v1/templates/notification-aar-for-email
  Scenario: [TEMPLATE-ENGINE_47] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia EMAIL" in lingua "inglese" con recipient Type "PG"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia EMAIL" il file "html" sia in lingua "inglese"
    And il corpo del messaggio contiene il testo "una notifica da parte di"

  @templateEngine @templateEngineCM #templates-engine-private/v1/templates/notification-aar-for-email-digital
  Scenario: [TEMPLATE-ENGINE_48] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia EMAIL digitale" in lingua "inglese" con recipient Type "PF"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia EMAIL digitale" il file "html" sia in lingua "inglese"
    And il corpo del messaggio contiene il testo "una comunicazione a valore legale da parte di"

  @templateEngine @templateEngineCM #Per flusso Analogico o con flag courtesy message:OFF - templates-engine-private/v1/templates/notification-aar-for-email
  Scenario: [TEMPLATE-ENGINE_49] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese (PF)
    When recupero il template per "avviso di cortesia EMAIL" in lingua "inglese" con recipient Type "PF"
    Then verifico che il template è in formato "html"
    And controllo che per il template "avviso di cortesia EMAIL" il file "html" sia in lingua "inglese"
    And il corpo del messaggio contiene il testo "una notifica da parte di"

  @templateEngine @templateEngineCM
  Scenario: [TEMPLATE-ENGINE_50] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana
    When recupero il template per "AVVISO DI CORTESIA SMS DIGITALE" in lingua "inglese" con recipient Type "PG"
    Then verifico che il template è in formato "text"
    And controllo che per il template "AVVISO DI CORTESIA SMS DIGITALE" il file "text" sia in lingua "italiana"
    And il corpo del messaggio contiene il testo "accedi a SEND"


  @templateEngine #81 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_51] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS
    When recupero l'oggetto per "avviso di cortesia per email object" in lingua "inglese"
    Then verifico che il template è in formato "text"
    And controllo che per il template "avviso di cortesia per email object" il file "text" sia in lingua "inglese"

  @templateEngine #100 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_52] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS - body vuoto
    When recupero l'oggetto per "avviso di cortesia per email object" in lingua "inglese" con il body "vuoto"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #94 /templates-engine-private/v1/templates/notificationAARForEMAIL
  Scenario: [TEMPLATE-ENGINE_53] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body vuoto
    When recupero il template per "avviso di cortesia EMAIL" in lingua "inglese" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  ##############################
  # COMUNICAZIONE BONARIA
  #############################
  # Endpoint definiti in: https://github.com/pagopa/pn-templates-engine/blob/1d71146851778765cd54fa866cf5252fed85762e/README.md

  @templateEngine # /templates-engine-private/v1/templates/informal/analog-communication
  Scenario: [TEMPLATE-ENGINE_54] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body vuoto
    When recupero il template per "comunicazione bonaria posta cartacea" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "pdf"

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-body
  Scenario: [TEMPLATE-ENGINE_54] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body vuoto
    When recupero il template per "email body comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "html"

  @templateEngine # /templates-engine-private/v1/templates/informal/email-communication-subject
  Scenario: [TEMPLATE-ENGINE_54] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body vuoto
    When recupero il template per "email subject comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-body
  Scenario: [TEMPLATE-ENGINE_54] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body vuoto
    When recupero il template per "pec body comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "html"

  @templateEngine # /templates-engine-private/v1/templates/informal/pec-communication-subject
  Scenario: [TEMPLATE-ENGINE_54] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body vuoto
    When recupero il template per "pec subject comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"

  @templateEngine # /templates-engine-private/v1/templates/informal/io-communication
  Scenario: [TEMPLATE-ENGINE_54] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body vuoto
    When recupero il template per "IO comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"

  @templateEngine # /templates-engine-private/v1/templates/informal/sms-communication
  Scenario: [TEMPLATE-ENGINE_54] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - body vuoto
    When recupero il template per "sms comunicazione bonaria" in lingua "italiana" con recipient Type "PF"
    Then verifico che il template è in formato "text"