Feature: Virtual key legal Person Authentication

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario Outline: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_1] Un Amministratore Persona Giuridica / Un utente Persona Giuridica blocca e riattiva la propria virtual key
    #6-7
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "<USER>" controlla l'accettazione dei tos "POSITIVA"
    And l'utente "<USER>" censisce una virtual key per sè stesso
    And l'utente "<USER>" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "<USER>" "RIATTIVA" una virtual key in stato "BLOCKED" per sè stesso
    Examples:
      | USER           |
      | AMMINISTRATORE |
      | PG             |

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_2] Un Amministratore Persona Giuridica blocca e riattiva la virtual key di l'utente della PG
    #8
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    # bug riferito
    #And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per l'utente "PG"
    When l'utente "AMMINISTRATORE" "RIATTIVA" una virtual key in stato "BLOCKED" per l'utente "PG"
    Then controllo che la chiave sia in stato "REACTIVE" per l'utente "PG"

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_3] Un utente Persona Giuridica censisce una virtual key dopo averne cancellata una in precedenza
    #9
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "PG" "CANCELLA" una virtual key in stato "BLOCKED" per sè stesso
    When l'utente "PG" censisce una virtual key per sè stesso
    Then controllo che l'utente "PG" veda le proprie virtual key nella PG

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_4] Un utente Persona Giuridica censisce una virtual key dopo averne ruotata e bloccata una in precedenza
    #10
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "PG" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "PG"
    And l'utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "PG" censisce una virtual key per sè stesso
    Then controllo che l'utente "PG" veda le proprie virtual key nella PG

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_5] Un Amministratore Persona Giuridica cancella la virtual key dopo averne ruotata una in precedenza
    #11
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "AMMINISTRATORE"
    When l'utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "ROTATED" per sè stesso
    Then controllo che l'utente "AMMINISTRATORE" veda tutte le virtual key nella PG

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_6] Un Amministratore Persona Giuridica prima ruota e poi cancella la virtual key di un altro utente
    # 12 sostituito come caso di errore
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per l'utente "PG" e riceve errore 403

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_7] Un Amministratore Persona Giuridica censisce la virtual key per sè stesso, senza aver accettato i TOS
    #45
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "DECLINA" i tos
    And l'utente "AMMINISTRATORE" controlla l'accettazione dei tos "NEGATIVA"
    When l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso senza successo con l'errore 403

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_8] Un Amministratore Persona Giuridica censisce la virtual key per sè stesso, senza la presenza di una chiave pubblica attiva per la PG
    #46
    Given l'utente "AMMINISTRATORE" "ACCETTA" i tos
    When l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso senza successo con l'errore 403

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_9] Un Amministratore Persona Giuridica censisce un’altra virtual key per sè stesso, anche se è già presente una attiva
    #47
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso senza successo con l'errore 409

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_10] Un Amministratore Persona Giuridica blocca la virtual key per sè stesso, nonostante sia stata già bloccata
    #48
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "BLOCKED" per sè stesso e riceve errore 409

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario Outline: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_11] Un Amministratore Persona Giuridica prova delle operazioni sulla virtual key cancellata
    #49 - 54 - 60 - 67
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "BLOCKED" per sè stesso
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "CANCELLED" per sè stesso e riceve errore 409
    Examples:
      | OPERATION |
      | BLOCCA    |
      | RIATTIVA  |
      | RUOTA     |
      | CANCELLA  |

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario Outline: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_12] Un utente Persona Giuridica e Amministratore Persona Giuridica appartenente ad un gruppo blocca/ruota la virtual key attiva di un altro utente
    #50 - 62 il secondo caso va in verde perchè nessuno puo ruotare chiavi di terze persone / vanno aggiunti i gruppi alla pg 2 Maria montessori
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "<USER1>" censisce una virtual key per sè stesso
    And l'utente "<USER2>" censisce una virtual key per sè stesso
    When l'utente "<USER1>" "<OPERATION>" una virtual key in stato "ENABLE" per l'utente "<USER2>" e riceve errore 403
    Examples:
      | USER1                     | USER2           | OPERATION |
      | PG                        | AMMINISTRATORE  | BLOCCA    |
      | AMMINISTRATORE CON GRUPPI | PG              | BLOCCA    |
      | AMMINISTRATORE            | PG              | RUOTA     |

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario Outline: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_13] Un Amministratore Persona Giuridica blocca/ruota la virtual key per un altro utente, senza aver accettato i TOS
    #51 - 63
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "NON ACCETTA" i tos
    And l'utente "AMMINISTRATORE" controlla l'accettazione dei tos "NEGATIVA"
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "ENABLE" per l'utente "PG" e riceve errore 403
    Examples:
      | OPERATION |
      | BLOCCA    |
      | RUOTA     |

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario Outline: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_14] Un Amministratore Persona Giuridica blocca/ruota la virtual key attiva, senza la presenza di una chiave pubblica attiva per la PG
    #52 - 64
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    And l'utente "AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG che si trova in stato "BLOCKED"
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "ENABLE" per sè stesso e riceve errore 403
    Examples:
      | OPERATION |
      | BLOCCA    |
      | RUOTA     |

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_15] Un Amministratore Persona Giuridica blocca la virtual key per sè stesso, nonostante esiste una già bloccata
    #53
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso e riceve errore 409

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_16] Un utente Persona Giuridica riattiva la virtual key bloccata di un altro utente
    #55 questo va in errore e riceve 409
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "PG" "RIATTIVA" una virtual key in stato "BLOCKED" per l'utente "AMMINISTRATORE" e riceve errore 403

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario Outline: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_17] Un Amministratore Persona Giuridica che non ha accettato i TOS, cancella/riattiva la virtual key bloccata di un altro utente
    # 56 - 71
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE CON GRUPPI" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE CON GRUPPI" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE CON GRUPPI" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "AMMINISTRATORE" "DECLINA" i tos
    And l'utente "AMMINISTRATORE" controlla l'accettazione dei tos "NEGATIVA"
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "BLOCKED" per l'utente "AMMINISTRATORE CON GRUPPI" e riceve errore 403
    Examples:
      | OPERATION   |
      | CANCELLA    |
      | RIATTIVA    |

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario Outline: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_18] Un Amministratore Persona Giuridica riattiva/cancella la propria virtual key bloccata, senza esistenza di una chiave pubblica attiva
    #57 - 72
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    When l'utente "AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG che si trova in stato "BLOCKED"
    When l'utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "BLOCKED" per sè stesso e riceve errore 403
    Examples:
      | OPERATION |
      | CANCELLA  |
      | RIATTIVA  |

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario Outline: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_19] Un Amministratore/utente PG riattiva/blocca/ruota la propria virtual key ruotata
    #58 - 61 - 78
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "<USER>" censisce una virtual key per sè stesso
    And l'utente "<USER>" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "<USER>"
    When l'utente "<USER>" "<OPERATION>" una virtual key in stato "ROTATED" per sè stesso e riceve errore 409
    Examples:
      | USER           | OPERATION |
      | AMMINISTRATORE | BLOCCA    |
      | AMMINISTRATORE | RIATTIVA  |
      | PG             | RUOTA     |

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_20] Un Utente Persona Giuridica riattiva la propria virtual key, nonostante sia presente una già attiva
    #59
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    When l'utente "PG" "RIATTIVA" una virtual key in stato "BLOCKED" per sè stesso e riceve errore 409

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_21] Un Amministratore Persona Giuridica ruota la propria virtual key bloccata
    #65
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per sè stesso
    When l'utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "BLOCKED" per sè stesso e riceve errore 409

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_22] Un Amministratore Persona Giuridica ruota la virtual key per sè stesso, nonostante esiste una già ruotata
    #66
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    And l'utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "AMMINISTRATORE"
    When l'utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per sè stesso e riceve errore 409

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_23] Un Amministratore Persona Giuridica cancella la propria virtual key non esistente
    #68
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE" censisce una virtual key per sè stesso
    When l'utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "UNKNOWN" per sè stesso e riceve errore 404

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_24] Un Utente Persona Giuridica cancella la propria virtual key attiva
    #69
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "PG" censisce una virtual key per sè stesso
    When l'utente "PG" "CANCELLA" una virtual key in stato "ENABLE" per sè stesso e riceve errore 409

  @removeAllVirtualKey @publicKeyCreation @pgAuthentication
  Scenario: [LEGAL-PERSON-AUTH-VIRTUAL-KEY_25] Un Amministratore Persona Giuridica appartenente ad un gruppo cancella la virtual key ruotata di un altro utente
    #70 c'è da aggiungere i gruppi all user pg4 Maria montessori
    Given l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "ACCETTA" i tos
    And l'utente "AMMINISTRATORE CON GRUPPI" censisce una virtual key per sè stesso
    And l'utente "PG" censisce una virtual key per sè stesso
    And l'utente "PG" "RUOTA" una virtual key in stato "ENABLE" per sè stesso
    And controllo che la rotazione è stata effettuata con successo per l'utente "PG"
    When l'utente "AMMINISTRATORE CON GRUPPI" "CANCELLA" una virtual key in stato "ROTATED" per l'utente "PG" e riceve errore 409
    Then controllo che l'utente "AMMINISTRATORE CON GRUPPI" veda le proprie virtual key nella PG
