Feature: Template engine

  @templateEngine #18 19 20 21 /templates-engine-private/v1/templates/notification-received-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_1] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "attestazione opponibile a terzi di notifica presa in carico" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
    | language |
    | italiana |
    | tedesca  |
    | slovena  |
    | francese |

  @templateEngine #22 - 23 /templates-engine-private/v1/templates/notification-received-legal-fact
  Scenario: [TEMPLATE-ENGINE_2] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica presa in carico - lingua errata - lingua vuota
    #essendo un enum non posso mandare una lingua sbagliata, posso mandare solo null
    When scarico il template "attestazione opponibile a terzi di notifica presa in carico" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #24 25 26 27 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_3] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "attestazione opponibile a terzi di notifica digitale" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #28 /templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact
  Scenario: [TEMPLATE-ENGINE_4] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di notifica digitale - lingua errata
    When scarico il template "attestazione opponibile a terzi di notifica digitale" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #29 30 31 32 /templates-engine-private/v1/templates/notification-viewed-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_5] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di avvenuto accesso - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "attestazione opponibile a terzi di avvenuto accesso" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #33 /templates-engine-private/v1/templates/notification-viewed-legal-fact
  Scenario: [TEMPLATE-ENGINE_6] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di avvenuto accesso - lingua errata
    When scarico il template "attestazione opponibile a terzi di avvenuto accesso" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #34 35 36 37 /templates-engine-private/v1/templates/legal-fact-malfuntion
  Scenario Outline: [TEMPLATE-ENGINE_7] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di malfunzionamento e ripristino - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "attestazione opponibile a terzi di malfunzionamento e ripristino" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #38 /templates-engine-private/v1/templates/legal-fact-malfuntion
  Scenario: [TEMPLATE-ENGINE_8] Richiamare l’API per il recupero del template dell’attestazione opponibile a terzi di malfunzionamento e ripristino - lingua errata
    When scarico il template "attestazione opponibile a terzi di malfunzionamento e ripristino" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #39 40 41 42 /templates-engine-private/v1/templates/notification-cancelled-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_9] Richiamare l’API per il recupero del template della dichiarazione di annullamento notifica - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "dichiarazione di annullamento notifica" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #43 /templates-engine-private/v1/templates/notification-cancelled-legal-fact
  Scenario: [TEMPLATE-ENGINE_10] Richiamare l’API per il recupero del template della dichiarazione di annullamento notifica - lingua errata
    When scarico il template "dichiarazione di annullamento notifica" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #44 45 46 47 /templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact
  Scenario Outline: [TEMPLATE-ENGINE_11] Richiamare l’API per il recupero del template del deposito di avvenuta ricezione - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "deposito di avvenuta ricezione" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #48 /templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact
  Scenario: [TEMPLATE-ENGINE_12] Richiamare l’API per il recupero del template del deposito di avvenuta ricezione - lingua errata
    When scarico il template "deposito di avvenuta ricezione" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #49 50 51 52 /templates-engine-private/v1/templates/notification-aar
  Scenario Outline: [TEMPLATE-ENGINE_13] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "avviso di avvenuta ricezione" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #53 /templates-engine-private/v1/templates/notification-aar
  Scenario: [TEMPLATE-ENGINE_14] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione - lingua errata
    When scarico il template "avviso di avvenuta ricezione" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #54 55 56 57 /templates-engine-private/v1/templates/notification-aar-radd-alt
  Scenario Outline: [TEMPLATE-ENGINE_15] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione RADD - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "avviso di avvenuta ricezione RADD" in lingua "<language>"
    Then verifico che il template è in formato ".pdf"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #58 /templates-engine-private/v1/templates/notification-aar-radd-alt
  Scenario: [TEMPLATE-ENGINE_16] Richiamare l’API per il recupero del template di avviso di avvenuta ricezione RADD - lingua errata
    When scarico il template "avviso di avvenuta ricezione RADD" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #59 60 61 62 templates-engine-private/v1/templates/notification-aar-for-email
  Scenario Outline: [TEMPLATE-ENGINE_17] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "avviso di cortesia EMAIL" in lingua "<language>"
    Then verifico che il template è in formato "html"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #63 /templates-engine-private/v1/templates/notificationAARForEMAIL
  Scenario: [TEMPLATE-ENGINE_18] Richiamare l’API per il recupero del template di avviso di cortesia EMAIL - lingua errata
    When scarico il template "avviso di cortesia EMAIL" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #64 65 66 67 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario Outline: [TEMPLATE-ENGINE_19] Richiamare l’API per il recupero del template di avviso di cortesia PEC - lingua italiana - lingua italiana e tedesca - lingua italiana e slovena - lingua italiana e francese
    When scarico il template "avviso di cortesia PEC" in lingua "<language>"
    Then verifico che il template è in formato "html"
    Examples:
      | language |
      | italiana |
      | tedesca  |
      | slovena  |
      | francese |

  @templateEngine #68 /templates-engine-private/v1/templates/notification-aar-for-pec
  Scenario: [TEMPLATE-ENGINE_19] Richiamare l’API per il recupero del template di avviso di cortesia PEC - lingua errata
    When scarico il template "avviso di cortesia PEC" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #69 /templates-engine-private/v1/templates/emailbody
  Scenario: [TEMPLATE-ENGINE_20] Richiamare l’API per il recupero del template di OTP di conferma email - lingua italiana
    When scarico il template "OTP di conferma email" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #70 /templates-engine-private/v1/templates/emailbody
  Scenario: [TEMPLATE-ENGINE_20] Richiamare l’API per il recupero del template di OTP di conferma email - lingua errata
    When scarico il template "OTP di conferma email" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #71 /templates-engine-private/v1/templates/pecbody
  Scenario: [TEMPLATE-ENGINE_21] Richiamare l’API per il recupero del template di OTP di conferma pec - lingua italiana
    When scarico il template "OTP di conferma pec" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #72 /templates-engine-private/v1/templates/pecbody
  Scenario: [TEMPLATE-ENGINE_22] Richiamare l’API per il recupero del template di OTP di conferma pec - lingua errata
    When scarico il template "OTP di conferma pec" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #73 /templates-engine-private/v1/templates/pecbodyconfirm
  Scenario: [TEMPLATE-ENGINE_23] Richiamare l’API per il recupero del template di PEC valida - lingua italiana
    When scarico il template "PEC valida" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #74 /templates-engine-private/v1/templates/pecbodyconfirm
  Scenario: [TEMPLATE-ENGINE_24] Richiamare l’API per il recupero del template di PEC valida - lingua errata
    When scarico il template "PEC valida" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #75 /templates-engine-private/v1/templates/pecbodyreject
  Scenario: [TEMPLATE-ENGINE_25] Richiamare l’API per il recupero del template di PEC non valida - lingua italiana
    When scarico il template "PEC non valida" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #76 /templates-engine-private/v1/templates/pecbodyreject
  Scenario: [TEMPLATE-ENGINE_26] Richiamare l’API per il recupero del template di PEC non valida - lingua errata
    When scarico il template "PEC non valida" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #77 templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_27] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua italiana
    When scarico il template "avviso di cortesia SMS" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #78 /templates-engine-private/v1/templates/notification-aar-for-sms
  Scenario: [TEMPLATE-ENGINE_28] Richiamare l’API per il recupero del template di avviso di cortesia SMS - lingua errata
    When scarico il template "avviso di cortesia SMS" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #79 /templates-engine-private/v1/templates/smsbody
  Scenario: [TEMPLATE-ENGINE_29] Richiamare l’API per il recupero del template di OTP di conferma sms - lingua italiana
    When scarico il template "OTP di conferma sms" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #80 /templates-engine-private/v1/templates/smsbody
  Scenario: [TEMPLATE-ENGINE_30] Richiamare l’API per il recupero del template di OTP di conferma sms - lingua errata
    When scarico il template "OTP di conferma sms" in lingua "null"
    Then verifico che la chiamata sia andata in "400" error

  @templateEngine #81 /templates-engine-private/v1/templates/notification-aar-subject
  Scenario: [TEMPLATE-ENGINE_31] Richiamare l’API per il recupero dell’oggetto relativo all’avviso di cortesia per l’SMS
    When scarico il template "avviso di cortesia per l’SMS" in lingua "italiana"
    Then verifico che il template è in formato "html"

  @templateEngine #82 /templates-engine-private/v1/templates/emailsubject
  Scenario: [TEMPLATE-ENGINE_32] Richiamare l’API per il recupero dell’oggetto relativo all’OTP di conferma email

  @templateEngine #83 /templates-engine-private/v1/templates/pecsubject
  Scenario: [TEMPLATE-ENGINE_33] Richiamare l’API per il recupero dell’oggetto relativo all’OTP di conferma pec

  @templateEngine #84 /templates-engine-private/v1/templates/pecsubjectreject
  Scenario: [TEMPLATE-ENGINE_34] Richiamare l’API per il recupero dell’oggetto relativo alla PEC valida

  @templateEngine #85 /templates-engine-private/v1/templates/pecsubjectreject
  Scenario: [TEMPLATE-ENGINE_35] Richiamare l’API per il recupero dell’oggetto relativo alla PEC non valida
