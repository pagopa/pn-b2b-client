Feature: Virtual key legal Person Authentication

  Scenario Outline: [LEGAL-PERSON-AUTH_1] Un Amministratore Persona Giuridica / Un utente Persona Giuridica blocca e riattiva la propria virtual key
    #6-7
    #aggiungi GIVEN censimento chiave pubblica per la PG
    And un utente "<USER>" "ACCETTA" i tos
    And un utente "<USER>" censisce una virtual key per se stesso
    And un utente "<USER>" controlla l'accettazione dei tos "POSITIVA"
    And un utente "<USER>" censisce una virtual key per se stesso
    And un utente "<USER>" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    When un utente "<USER>" "RIATTIVA" una virtual key in stato "BLOCK" per se stesso
    And controllo che l utente "<USER>" abbia tutte le virtual su cui ha operato con lo stato giusto
    Examples:
      | USER           |
      | AMMINISTRATORE |
      | PG             |

  Scenario: [LEGAL-PERSON-AUTH_2] Un Amministratore Persona Giuridica blocca e riattiva la virtual key di un utente della PG
    #8
    # aggiungi GIVEN censimento chiave pubblica per la PG
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "PG" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "PG" censisce una virtual key per se stesso
    When un utente "Amministratore" "BLOCCA" una virtual key in stato "ENABLE" per l'utente "PG"
    And un utente "Amministratore" "RIATTIVA" una virtual key in stato "BLOCK" per l'utente "PG"

  Scenario: [LEGAL-PERSON-AUTH_3] Un utente Persona Giuridica censisce una virtual key dopo averne cancellata una in precedenza
    #9
    # aggiungi GIVEN censimento chiave pubblica per la PG
    And un utente "PG" "ACCETTA" i tos
    And un utente "PG" censisce una virtual key per se stesso
    When un utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    When un utente "PG" "CANCELLA" una virtual key in stato "BLOCK" per se stesso
    And un utente "PG" censisce una virtual key per se stesso
    And controllo che l utente "PG" abbia tutte le virtual su cui ha operato con lo stato giusto

  Scenario: [LEGAL-PERSON-AUTH_4] Un utente Persona Giuridica censisce una virtual key dopo averne ruotata e bloccata una in precedenza
    #10
    # aggiungi GIVEN censimento chiave pubblica per la PG
    And un utente "PG" "ACCETTA" i tos
    And un utente "PG" censisce una virtual key per se stesso
    When un utente "PG" "RUOTA" una virtual key in stato "ENABLE" per se stesso
    When un utente "PG" "BLOCCA" una virtual key in stato "ROTATE" per se stesso
    And un utente "PG" censisce una virtual key per se stesso
    And controllo che l utente "PG" abbia tutte le virtual su cui ha operato con lo stato giusto

  Scenario: [LEGAL-PERSON-AUTH_5] Un Amministratore Persona Giuridica cancella la virtual key dopo averne ruotata una in precedenza
    #11
    # aggiungi GIVEN censimento chiave pubblica per la PG
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    When un utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per se stesso
    When un utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "ROTATE" per se stesso
    And controllo che l utente "AMMINISTRATORE" abbia tutte le virtual su cui ha operato con lo stato giusto

  Scenario: [LEGAL-PERSON-AUTH_6] Un Amministratore Persona Giuridica ruota e cancella la virtual key di un altro utente
    #12
    # aggiungi GIVEN censimento chiave pubblica per la PG
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "PG" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "PG" censisce una virtual key per se stesso
    When un utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per l'utente "PG"
    When un utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "ENABLE" per l'utente "PG"
    And controllo che l utente "PG" abbia tutte le virtual su cui ha operato con lo stato giusto
    And controllo che l utente "AMMINISTRATORE" abbia tutte le virtual su cui ha operato con lo stato giusto

  Scenario: [LEGAL-PERSON-AUTH_7] Un Amministratore Persona Giuridica censisce la virtual key per se stesso, senza aver accettato i TOS
    #45
    # aggiungi GIVEN censimento chiave pubblica per la PG
    And un utente "AMMINISTRATORE" "NON ACCETTA" i tos
    And un utente "AMMINISTRATORE" controlla l'accettazione dei tos "NEGATIVA"
    When un utente "AMMINISTRATORE CON GRUPPO" censisce una virtual key per se stesso senza successo con l'errore 403

  Scenario: [LEGAL-PERSON-AUTH_8] Un Amministratore Persona Giuridica censisce la virtual key per se stesso, senza la presenza di una chiave pubblica attiva per la PG
    #46
    # aggiungi GIVEN chiave pubblica cancellata o bloccata
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    When un utente "AMMINISTRATORE" censisce una virtual key per se stesso senza successo con l'errore 403

  Scenario: [LEGAL-PERSON-AUTH_9] Un Amministratore Persona Giuridica censisce un’altra virtual key per se stesso, anche se è già presente una attiva
    #47
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    When un utente "AMMINISTRATORE" censisce una virtual key per se stesso senza successo con l'errore 409

  Scenario: [LEGAL-PERSON-AUTH_10] Un Amministratore Persona Giuridica blocca la virtual key per se stesso, nonostante sia stata già bloccata
    #48
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    When un utente "PG" "BLOCCA" una virtual key in stato "BLOCK" per se stesso e riceve errore 409

  Scenario Outline: [LEGAL-PERSON-AUTH_11] Un Amministratore Persona Giuridica prova delle operazioni sulla virtual key cancellata
    #49 - 54 - 60 - 67
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    And un utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "BLOCCATO" per se stesso
    When un utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "CANCELLATO" per se stesso e riceve errore 409
    Examples:
      | OPERATION |
      | BLOCCA    |
      | RIATTIVA  |
      | RUOTA     |
      | CANCELLA  |

  Scenario Outline: [LEGAL-PERSON-AUTH_12] Un utente Persona Giuridica e Amministratore Persona Giuridica appartenente ad un gruppo blocca/ruota la virtual key attiva di un altro utente
    #50 -64
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "<USER1>" "ACCETTA" i tos
    And un utente "<USER2>" "ACCETTA" i tos
    And un utente "<USER1>" censisce una virtual key per se stesso
    And un utente "<USER2>" censisce una virtual key per se stesso
    And un utente "<USER1>" "<OPERATION>" una virtual key in stato "ENABLE" per l'utente "<USER2>" e riceve errore 409
    Examples:
      | USER1                     | USER2           | OPERATION |
      | PG                        | AMMINISTRATORE  | BLOCCA    |
      | AMMINISTRATORE CON GRUPPI | PG              | RUOTA     |

  Scenario Outline: [LEGAL-PERSON-AUTH_13] Un Amministratore Persona Giuridica blocca/ruota la virtual key per un altro utente, senza aver accettato i TOS
    #51 - 63
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "PG" "ACCETTA" i tos
    And un utente "PG" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "NON ACCETTA" i tos
    And un utente "AMMINISTRATORE" controlla l'accettazione dei tos "NEGATIVA"
    And un utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "ENABLE" per l'utente "PG" e riceve errore 403
    Examples:
      | OPERATION |
      | BLOCCA    |
      | RUOTA     |

  Scenario Outline: [LEGAL-PERSON-AUTH_14] Un Amministratore Persona Giuridica blocca/ruota la virtual key attiva, senza la presenza di una chiave pubblica attiva per la PG
    #52 - 64
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    #chiave pubblica bloccata cancellata
    And un utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "ENABLE" per se stesso e riceve errore 403
    Examples:
      | OPERATION |
      | BLOCCA    |
      | RUOTA     |

  Scenario: [LEGAL-PERSON-AUTH_15] Un Amministratore Persona Giuridica blocca la virtual key per se stesso, nonostante esiste una già bloccata
    #53
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per se stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_16] Un utente Persona Giuridica riattiva la virtual key bloccata di un altro utente
    #55
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "PG" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "PG" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    And un utente "PG" "RIATTIVA" una virtual key in stato "BLOCK" per l'utente "AMMINISTRATORE" e riceve errore 403

  Scenario Outline: [LEGAL-PERSON-AUTH_17] Un Amministratore Persona Giuridica che non ha accettato i TOS, cancella/riattiva la virtual key bloccata di un altro utente
    # 56 - 71
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "AMMINISTRATORE CON GRUPPI" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE CON GRUPPI" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "DECLINA" i tos
    And un utente "AMMINISTRATORE" controlla l'accettazione dei tos "NEGATIVA"
    And un utente "AMMINISTRATORE CON GRUPPI" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    And un utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "BLOCCATO" per l'utente "AMMINISTRATORE CON GRUPPI" e riceve errore 403
    Examples:
      | OPERATION   |
      | CANCELLA    |
      | RIATTIVA    |

  Scenario Outline: [LEGAL-PERSON-AUTH_18] Un Amministratore Persona Giuridica riattiva/cancella la propria virtual key bloccata, senza esistenza di una chiave pubblica attiva
    #57 - 72
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE " "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    # blocca chiave pubblica
    And un utente "AMMINISTRATORE" "<OPERATION>" una virtual key in stato "BLOCK" per se stesso e riceve errore 403
    Examples:
      | OPERATION |
      | CANCELLA  |
      | RIATTIVA  |

  Scenario Outline: [LEGAL-PERSON-AUTH_19] Un Amministratore/utente PG riattiva/blocca/ruota la propria virtual key ruotata
    #58 - 61 - caso ancora da definire in srs
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "<USER>" "ACCETTA" i tos
    And un utente "<USER>" censisce una virtual key per se stesso
    And un utente "<USER>" "RUOTA" una virtual key in stato "ENABLE" per se stesso
    And un utente "<USER>" "<OPERATION>" una virtual key in stato "RUOTATO" per se stesso e riceve errore 409
    Examples:
      | USER           | OPERATION |
      | AMMINISTRATORE | BLOCCA    |
      | AMMINISTRATORE | RIATTIVA  |
      | PG             | RUOTA     |

  Scenario: [LEGAL-PERSON-AUTH_20] Un Utente Persona Giuridica riattiva la propria virtual key, nonostante sia presente una già attiva
    #59
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "PG " "ACCETTA" i tos
    And un utente "PG" censisce una virtual key per se stesso
    And un utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    And un utente "PG" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "RIATTIVA" una virtual key in stato "BLOCK" per se stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_21] Un Amministratore Persona Giuridica ruota la propria virtual key bloccata
    #65
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "PG" "ACCETTA" i tos
    And un utente "PG" censisce una virtual key per se stesso
    And un utente "PG" "BLOCCA" una virtual key in stato "ENABLE" per se stesso
    And un utente "PG" "RUOTA" una virtual key in stato "BLOCK" per se stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_22] Un Amministratore Persona Giuridica ruota la virtual key per se stesso, nonostante esiste una già ruotata
    #66
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per se stesso
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "RUOTA" una virtual key in stato "ENABLE" per se stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_23] Un Amministratore Persona Giuridica cancella la propria virtual key non esistente
    #68
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE" "ACCETTA" i tos
    And un utente "AMMINISTRATORE" censisce una virtual key per se stesso
    And un utente "AMMINISTRATORE" "CANCELLA" una virtual key in stato "UNKNOWN" per se stesso e riceve errore 404

  Scenario: [LEGAL-PERSON-AUTH_24] Un Utente Persona Giuridica cancella la propria virtual key attiva
    #69
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "PG" "ACCETTA" i tos
    And un utente "PG" censisce una virtual key per se stesso
    And un utente "PG" "CANCELLA" una virtual key in stato "ENABLE" per se stesso e riceve errore 409

  Scenario: [LEGAL-PERSON-AUTH_25] Un Amministratore Persona Giuridica appartenente ad un gruppo cancella la virtual key ruotata di un altro utente
    #70
    # aggiungi GIVEN chiave pubblica attiva
    And un utente "AMMINISTRATORE CON GRUPPI" "ACCETTA" i tos
    And un utente "PG" "ACCETTA" i tos
    And un utente "AMMINISTRATORE CON GRUPPI" censisce una virtual key per se stesso
    And un utente "PG" censisce una virtual key per se stesso
    And un utente "PG" "RUOTA" una virtual key in stato "ENABLE" per se stesso
    And un utente "AMMINISTRATORE CON GRUPPI" "CANCELLA" una virtual key in stato "ROTATE" per l'utente "PG" e riceve errore 409
    #controllare verifica lettura chiavi pubbliche
