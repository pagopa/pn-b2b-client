Feature: Virtual key legal Person Authentication

  Scenario Outline: [LEGAL-PERSON-AUTH_1] Un Amministratore Persona Giuridica / Un utente Persona Giuridica blocca e riattiva la propria virtual key
    #6-7
    Given l'utente "<USER>" crea una chiave pubblica per la PG
    And l'utente "<USER>" "ACCETTA" i tos
    And l'utente "<USER>" censisce una virtual key per sè stesso
    And l'utente "<USER>" controlla l'accettazione dei tos "POSITIVA"
    And l'utente "<USER>" censisce una virtual key per sè stesso
    And l'utente "<USER>" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "<USER>" "RIATTIVA" una virtual key in stato "BLOCK" per sè stesso
    Then controllo che l'utente "<USER>" veda "<CONDITION>" virtual key nella PG
    Examples:
      | USER           | CONDITION  |
      | AMMINISTRATORE | TUTTE LE   |
      | PG             | LE PROPRIE |

  Scenario: [LEGAL-PERSON-AUTH_2] Un Amministratore Persona Giuridica blocca e riattiva la virtual key di l'utente della PG
    #8
    # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "PG" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    When l'utente "Amministratore" "BLOCCA" una virtual key in stato "ENABLE" per l'utente "PG"
    When l'utente "Amministratore" "RIATTIVA" una virtual key in stato "BLOCK" per l'utente "PG"
    Then controllo che la chiave sia in stato "ATTIVA" per l'utente "PG"

  Scenario: [LEGAL-PERSON-AUTH_3] Un utente Persona Giuridica censisce una virtual key dopo averne cancellata una in precedenza
    #9
    # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "PG" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "PG" "CANCELLA" una virtual key in stato "BLOCK" per sè stesso
    When l'utente "PG" censisce una virtual key per sè stesso
    Then controllo che l'utente "PG" veda "LE PROPRIE" virtual key nella PG

  Scenario: [LEGAL-PERSON-AUTH_4] Un utente Persona Giuridica censisce una virtual key dopo averne ruotata e bloccata una in precedenza
    #10
    # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "PG" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "PG" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "PG"
    And l'utente "PG" "BLOCCA" una virtual key in stato "ROTATE" per sè stesso
    When l'utente "PG" censisce una virtual key per sè stesso
    Then controllo che l'utente "PG" veda "LE PROPRIE" virtual key nella PG

  Scenario: [LEGAL-PERSON-AUTH_5] Un Amministratore Persona Giuridica cancella la virtual key dopo averne ruotata una in precedenza
    #11
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "AMMINISTRATORE"
    When l'utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "ROTATE" per sè stesso
    Then controllo che l'utente "AMMINISTRATORE" veda "TUTTE LE" virtual key nella PG

  Scenario: [LEGAL-PERSON-AUTH_6] Un Amministratore Persona Giuridica prima ruota e poi cancella la virtual key di un altro utente
    #12
     # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "PG" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per l'utente "PG"
    And controllo che la rotazione è stata effettuata con successo per l'utente "PG"
    When l'utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "ENABLE" per l'utente "PG"
    Then controllo che l'utente "AMMINISTRATORE" veda "TUTTE LE" virtual key nella PG

  Scenario: [LEGAL-PERSON-AUTH_7] Un Amministratore Persona Giuridica censisce la virtual key per sè stesso, senza aver accettato i TOS
    #45
    # c'è da cpire serve un amministratore con gruppo associato con la stessa PG dell amministratore
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "NON ACCETTA" i tos
    And l'utente "AMMINISTRATORE" controlla l'accettazione dei tos "NEGATIVA"
    When l'utente "AMMINISTRATORE CON GRUPPO" censisce una virtual key per sè stesso senza successo con l'errore 403

  Scenario: [LEGAL-PERSON-AUTH_8] Un Amministratore Persona Giuridica censisce la virtual key per sè stesso, senza la presenza di una chiave pubblica attiva per la PG
    #46
    Given l'utente "AMMINISTRATORE" "ACCETTA" i tos
    When l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso senza successo con l'errore 403

  Scenario: [LEGAL-PERSON-AUTH_9] Un Amministratore Persona Giuridica censisce un’altra virtual key per sè stesso, anche se è già presente una attiva
    #47
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso senza successo con l'errore 409

  Scenario: [LEGAL-PERSON-AUTH_10] Un Amministratore Persona Giuridica blocca la virtual key per sè stesso, nonostante sia stata già bloccata
    #48
    # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "PG" "BLOCCA" una virtual key in stato "BLOCK" per sè stesso e riceve errore 409

  Scenario Outline: [LEGAL-PERSON-AUTH_11] Un Amministratore Persona Giuridica prova delle operazioni sulla virtual key cancellata
    #49 - 54 - 60 - 67
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "BLOCCATO" per sè stesso
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "CANCELLATO" per sè stesso e riceve errore 409
    Examples:
      | OPERATION |
      | BLOCCA    |
      | RIATTIVA  |
      | RUOTA     |
      | CANCELLA  |

  Scenario Outline: [LEGAL-PERSON-AUTH_12] Un utente Persona Giuridica e Amministratore Persona Giuridica appartenente ad un gruppo blocca/ruota la virtual key attiva di un altro utente
    #50 - 62
    # c'è da cpire serve un amministratore con la stessa PG dello user e con l amministratore con gruppi
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "<USER1>" "ACCETTA" i tos
    And l'utente "<USER2>" "ACCETTA" i tos
    And l'utente "<USER1>" censisce una virtual key per sè stesso
    And l'utente "<USER2>" censisce una virtual key per sè stesso
    When l'utente "<USER1>" "<OPERATION>" una virtual key in stato "ENABLE" per l'utente "<USER2>" e riceve errore 409
    Examples:
      | USER1                     | USER2           | OPERATION |
      | PG                        | AMMINISTRATORE  | BLOCCA    |
      | AMMINISTRATORE CON GRUPPI | PG              | RUOTA     |

  Scenario Outline: [LEGAL-PERSON-AUTH_13] Un Amministratore Persona Giuridica blocca/ruota la virtual key per un altro utente, senza aver accettato i TOS
    #51 - 63
    # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "PG" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "NON ACCETTA" i tos
    And l'utente "AMMINISTRATORE" controlla l'accettazione dei tos "NEGATIVA"
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "ENABLE" per l'utente "PG" e riceve errore 403
    Examples:
      | OPERATION |
      | BLOCCA    |
      | RUOTA     |

  Scenario Outline: [LEGAL-PERSON-AUTH_14] Un Amministratore Persona Giuridica blocca/ruota la virtual key attiva, senza la presenza di una chiave pubblica attiva per la PG
    #52 - 64
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "ATTIVA"
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "ENABLE" per sè stesso e riceve errore 403
    Examples:
      | OPERATION |
      | BLOCCA    |
      | RUOTA     |

  Scenario: [LEGAL-PERSON-AUTH_15] Un Amministratore Persona Giuridica blocca la virtual key per sè stesso, nonostante esiste una già bloccata
    #53
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_16] Un utente Persona Giuridica riattiva la virtual key bloccata di un altro utente
    #55
    # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "PG" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "PG" "RIATTIVA" una virtual key in stato "BLOCK" per l'utente "AMMINISTRATORE" e riceve errore 403

  Scenario Outline: [LEGAL-PERSON-AUTH_17] Un Amministratore Persona Giuridica che non ha accettato i TOS, cancella/riattiva la virtual key bloccata di un altro utente
    # 56 - 71
    # c'è da cpire serve un amministratore con la stessa PG dello amministratore con gruppi
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE CON GRUPPI" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE CON GRUPPI" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "DECLINA" i tos
    And l'utente "AMMINISTRATORE" controlla l'accettazione dei tos "NEGATIVA"
    And l'utente "AMMINISTRATORE CON GRUPPI" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "BLOCCATO" per l'utente "AMMINISTRATORE CON GRUPPI" e riceve errore 403
    Examples:
      | OPERATION   |
      | CANCELLA    |
      | RIATTIVA    |

  Scenario Outline: [LEGAL-PERSON-AUTH_18] Un Amministratore Persona Giuridica riattiva/cancella la propria virtual key bloccata, senza esistenza di una chiave pubblica attiva
    #57 - 72
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "ATTIVA"
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "BLOCK" per sè stesso e riceve errore 403
    Examples:
      | OPERATION |
      | CANCELLA  |
      | RIATTIVA  |

  Scenario Outline: [LEGAL-PERSON-AUTH_19] Un Amministratore/utente PG riattiva/blocca/ruota la propria virtual key ruotata
    #58 - 61 - 77
    # c'è da capire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "<USER>" "ACCETTA" i tos
    And l'utente "<USER>" censisce una virtual key per sè stesso
    And l'utente "<USER>" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "<USER>"
    When l'utente "<USER>" "<OPERATION>" una virtual key in stato "RUOTATO" per sè stesso e riceve errore 409
    Examples:
      | USER           | OPERATION |
      | AMMINISTRATORE | BLOCCA    |
      | AMMINISTRATORE | RIATTIVA  |
      | PG             | RUOTA     |

  Scenario: [LEGAL-PERSON-AUTH_20] Un Utente Persona Giuridica riattiva la propria virtual key, nonostante sia presente una già attiva
    #59
    # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "PG " "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" "RIATTIVA" una virtual key in stato "BLOCK" per sè stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_21] Un Amministratore Persona Giuridica ruota la propria virtual key bloccata
    #65
    # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "PG" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "PG" "RUOTA" una virtual key in stato "BLOCK" per sè stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_22] Un Amministratore Persona Giuridica ruota la virtual key per sè stesso, nonostante esiste una già ruotata
    #66
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "AMMINISTRATORE"
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per sè stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_23] Un Amministratore Persona Giuridica cancella la propria virtual key non esistente
    #68
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "UNKNOWN" per sè stesso e riceve errore 404

  Scenario: [LEGAL-PERSON-AUTH_24] Un Utente Persona Giuridica cancella la propria virtual key attiva
    #69
 # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "PG" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    When l'utente "PG" "CANCELLA" una virtual key in stato "ENABLE" per sè stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_25] Un Amministratore Persona Giuridica appartenente ad un gruppo cancella la virtual key ruotata di un altro utente
    #70
   # c'è da cpire serve un amministratore con la stessa PG dello user
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE CON GRUPPI" "ACCETTA" i tos
    And l'utente "PG" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE CON GRUPPI" censisce una virtual key per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "PG" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "PG"
    When l'utente "AMMINISTRATORE CON GRUPPI" "CANCELLA" una virtual key in stato "ROTATE" per l'utente "PG" e riceve errore 409
    Then controllo che l'utente "AMMINISTRATORE CON GRUPPI" veda "LE PROPRIE" virtual key nella PG
