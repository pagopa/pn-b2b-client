Feature: Template engine

  @templateEngine #18 19 20 21 /templates-engine-private/v1/templates/notification-received-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_1] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "attestazione opponibile a terzi di notifica presa in carico" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
    | language |
    | italiana |
    | tedesca  |
    | slovena  |
    | francese |

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

  @templateEngine #106 /templates-engine-private/v1/templates/notification-received-legal-fact
  Scenario: [TEMPLATE-ENGINE_2_3] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - "context_physicalAddressAndDenomination" null
    When recupero il template per "attestazione opponibile a terzi di notifica presa in carico" con i valori nel request body:
      | context_physicalAddressAndDenomination | null |
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #24 25 26 27 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_3] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "attestazione opponibile a terzi di notifica digitale" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

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

  @templateEngine #29 30 31 32 /templates-engine-private/v1/templates/notification-viewed-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_5] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di avvenuto accesso - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "attestazione opponibile a terzi di avvenuto accesso" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

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
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #34 35 36 37 /templates-engine-private/v1/templates/legal-fact-malfunction
  Scenario Outline: [TEMPLATE-ENGINE_7] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di malfunzionamento e ripristino - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "attestazione opponibile a terzi di malfunzionamento e ripristino" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
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
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #39 40 41 42 /templates-engine-private/v1/templates/notification-cancelled-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_9] Richiamare l’API per il recupero del template della dichiarazione di annullamento notifica - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "dichiarazione di annullamento notifica" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

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
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #44 45 46 47 /templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_11] Richiamare l’API per il recupero del template del deposito di avvenuta ricezione - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "deposito di avvenuta ricezione" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

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
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #49 50 51 52 /templates-engine-private/v1/templates/notification-aar
  Scenario Outline: [TEMPLATE-ENGINE_13] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di avvenuta ricezione" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

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
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #54 55 56 57 /templates-engine-private/v1/templates/notification-aar-radd-alt
  Scenario Outline: [TEMPLATE-ENGINE_15] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione RADD - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di avvenuta ricezione RADD" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

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
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #59 60 61 62 templates-engine-private/v1/templates/notification-aar-for-email
  Scenario Outline: [TEMPLATE-ENGINE_17] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia EMAIL" in lingua "<language>"
    Then verifico che il template è in formato "html"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

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
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #64 65 66 67 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario Outline: [TEMPLATE-ENGINE_19] Richiamare l’API per il recupero del template di avviso di cortesia PEC - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When recupero il template per "avviso di cortesia PEC" in lingua "<language>"
    Then verifico che il template è in formato "html"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #68 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario: [TEMPLATE-ENGINE_20] Richiamare l’API per il recupero del template di avviso di cortesia PEC - lingua errata
    When recupero il template per "avviso di cortesia PEC" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #95 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario: [TEMPLATE-ENGINE_20_1] Richiamare l’API per il recupero del template di avviso di cortesia PEC - body vuoto
    When recupero il template per "avviso di cortesia PEC" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #115 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario: [TEMPLATE-ENGINE_20_2] Richiamare l’API per il recupero del template di avviso di cortesia PEC - body errato
    When recupero il template per "avviso di cortesia PEC" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #69 /templates-engine-private/v1/templates/emailbody
  Scenario: [TEMPLATE-ENGINE_21] Richiamare l’API per il recupero del template di OTP di conferma email - lingua italiana
    When recupero il template per "OTP di conferma email" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #70 /templates-engine-private/v1/templates/emailbody
  Scenario: [TEMPLATE-ENGINE_22] Richiamare l’API per il recupero del template di OTP di conferma email - lingua errata
    When recupero il template per "OTP di conferma email" in lingua "francese"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #96 /templates-engine-private/v1/templates/emailbody
  Scenario: [TEMPLATE-ENGINE_22_1] Richiamare l’API per il recupero del template di OTP di conferma email - body vuoto
    When recupero il template per "OTP di conferma email" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #116 /templates-engine-private/v1/templates/emailbody
  Scenario: [TEMPLATE-ENGINE_22_2] Richiamare l’API per il recupero del template di OTP di conferma email - body errato
    When recupero il template per "OTP di conferma email" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #71 /templates-engine-private/v1/templates/pec-verification-code-body
  Scenario: [TEMPLATE-ENGINE_23] Richiamare l’API per il recupero del template di OTP di conferma pec - lingua italiana
    When recupero il template per "OTP di conferma pec" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #72 /templates-engine-private/v1/templates/pec-verification-code-body
  Scenario: [TEMPLATE-ENGINE_24] Richiamare l’API per il recupero del template di OTP di conferma pec - lingua errata
    When recupero il template per "OTP di conferma pec" in lingua "slovena"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #97 /templates-engine-private/v1/templates/pec-verification-code-body
  Scenario: [TEMPLATE-ENGINE_24_1] Richiamare l’API per il recupero del template di OTP di conferma pec - body vuoto
    When recupero il template per "OTP di conferma pec" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #117 /templates-engine-private/v1/templates/pec-verification-code-body
  Scenario: [TEMPLATE-ENGINE_24_2] Richiamare l’API per il recupero del template di OTP di conferma pec - body errato
    When recupero il template per "OTP di conferma pec" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "400" error e che nessuna abbia ricevuto una risposta

  @templateEngine #73 /templates-engine-private/v1/templates/pecbodyconfirm
  Scenario: [TEMPLATE-ENGINE_25] Richiamare l’API per il recupero del template di PEC valida - lingua italiana
    When recupero il template per "PEC valida" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #74 /templates-engine-private/v1/templates/pecbodyconfirm
  Scenario: [TEMPLATE-ENGINE_26] Richiamare l’API per il recupero del template di PEC valida - lingua errata
    When recupero il template per "PEC valida" in lingua "tedesca"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #98 /templates-engine-private/v1/templates/pecbodyconfirm
  Scenario: [TEMPLATE-ENGINE_26_1] Richiamare l’API per il recupero del template di PEC valida - body vuoto
    When recupero il template per "PEC valida" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #118 /templates-engine-private/v1/templates/pecbodyconfirm
  Scenario: [TEMPLATE-ENGINE_26_2] Richiamare l’API per il recupero del template di PEC valida - body errato
    When recupero il template per "PEC valida" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #75 /templates-engine-private/v1/templates/pecbodyreject
  Scenario: [TEMPLATE-ENGINE_27] Richiamare l’API per il recupero del template di PEC non valida - lingua italiana
    When recupero il template per "PEC non valida" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #76 /templates-engine-private/v1/templates/pecbodyreject
  Scenario: [TEMPLATE-ENGINE_28] Richiamare l’API per il recupero del template di PEC non valida - lingua errata
    When recupero il template per "PEC non valida" in lingua "francese"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #77 templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_29] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana
    When recupero il template per "avviso di cortesia SMS" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #78 /templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_30] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua errata
    When recupero il template per "avviso di cortesia SMS" in lingua "tedesca"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #99 /templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_30_1] Richiamare l’API per il recupero del template di avviso di cortesia SMS - body vuoto
    When recupero il template per "avviso di cortesia SMS" in lingua "italiana" con il body "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #119 /templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_30_2] Richiamare l’API per il recupero del template di avviso di cortesia SMS - body errato
    When recupero il template per "avviso di cortesia SMS" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #79 /templates-engine-private/v1/templates/smsbody
  Scenario: [TEMPLATE-ENGINE_31] Richiamare l’API per il recupero del template di OTP di conferma sms - lingua italiana
    When recupero il template per "OTP di conferma sms" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #80 /templates-engine-private/v1/templates/smsbody
  Scenario: [TEMPLATE-ENGINE_32] Richiamare l’API per il recupero del template di OTP di conferma sms - lingua errata
    When recupero il template per "OTP di conferma sms" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #81 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_33] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS
    When recupero l'oggetto per "avviso di cortesia per l’SMS object" in lingua "italiana"
    Then verifico che il template è in formato "text"

  @templateEngine #100 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_33_1] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS - lingua errata
    When recupero l'oggetto per "avviso di cortesia per l’SMS object" in lingua "francese"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #100 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_33_2] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS - body vuoto
    When recupero l'oggetto per "avviso di cortesia per l’SMS object" in lingua "italiana" con il body "vuoto"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #120 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_33_3] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS - body errato
    When recupero l'oggetto per "avviso di cortesia per l’SMS object" con i valori nel request body errati
    Then verifico che tutte le chiamate siano andate in "500" error e che nessuna abbia ricevuto una risposta

  @templateEngine #82 #83 #84 #85 /templates-engine-private/v1/templates/emailsubject
  Scenario: [TEMPLATE-ENGINE_34] Richiamare l’API per il recupero dell’oggetto relativo all’OTP di conferma email
    When recupero l'oggetto per "OTP di conferma email object" in lingua "italiana"
    Then verifico che il template è in formato "text"

  @templateEngine #102 /templates-engine-private/v1/templates/emailsubject
  Scenario: [TEMPLATE-ENGINE_34_1] Richiamare l’API per il recupero dell’oggetto relativo all’OTP di conferma email - lingua errata
    When recupero l'oggetto per "OTP di conferma email object" in lingua "slovena"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #83 /templates-engine-private/v1/templates/pecsubject
  Scenario: [TEMPLATE-ENGINE_35] Richiamare l’API per il recupero dell’oggetto relativo all’OTP di conferma pec
    When recupero l'oggetto per "OTP di conferma pec object" in lingua "italiana"
    Then verifico che il template è in formato "text"

  @templateEngine #103 /templates-engine-private/v1/templates/pecsubject
  Scenario: [TEMPLATE-ENGINE_35_1] Richiamare l’API per il recupero dell’oggetto relativo all’OTP di conferma pec - lingua errata
    When recupero l'oggetto per "OTP di conferma pec object" in lingua "tedesca"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #84 /templates-engine-private/v1/templates/pecsubjectconfirm
  Scenario: [TEMPLATE-ENGINE_36] Richiamare l’API per il recupero dell’oggetto relativo alla PEC valida
    When recupero l'oggetto per "PEC valida object" in lingua "italiana"
    Then verifico che il template è in formato "text"

  @templateEngine #104 /templates-engine-private/v1/templates/pecsubjectconfirm
  Scenario: [TEMPLATE-ENGINE_36_1] Richiamare l’API per il recupero dell’oggetto relativo alla PEC valida - lingua errata
    When recupero l'oggetto per "PEC valida object" in lingua "francese"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #85 /templates-engine-private/v1/templates/pecsubjectreject
  Scenario: [TEMPLATE-ENGINE_37] Richiamare l’API per il recupero dell’oggetto relativo alla PEC non valida
    When recupero l'oggetto per "PEC non valida object" in lingua "italiana"
    Then verifico che il template è in formato "text"

  @templateEngine #105 /templates-engine-private/v1/templates/pecsubjectreject
  Scenario: [TEMPLATE-ENGINE_37_1] Richiamare l’API per il recupero dell’oggetto relativo alla PEC non valida - lingua errata
    When recupero l'oggetto per "PEC non valida object" in lingua "slovena"
    Then verifico che la chiamata sia andata in "400" error
