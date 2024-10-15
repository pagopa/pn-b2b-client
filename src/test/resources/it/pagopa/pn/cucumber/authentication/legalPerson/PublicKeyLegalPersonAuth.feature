Feature: Public key legal Person Authentication

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_1] Un Amministratore PG censisce una chiave pubblica per la Persona Giuridica dopo averla cancellata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "CANCELLED"
    When l'utente "AMMINISTRATORE" "RICREA" la chiave pubblica per la PG che si trova in stato "CANCELLED"
    Then la chiave pubblica in stato "ACTIVE" viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_2] Un Amministratore PG censisce una chiave pubblica per la Persona Giuridica dopo averla bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCKED"
    When l'utente "AMMINISTRATORE" "RICREA" la chiave pubblica per la PG che si trova in stato "BLOCKED"
    Then la chiave pubblica in stato "ACTIVE" viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_3] Un Amministratore PG riattiva una chiave pubblica per la Persona Giuridica dopo averla bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCKED"
    When l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG che si trova in stato "BLOCKED"
    Then la chiave pubblica in stato "ACTIVE" viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_4] Un Amministratore PG cancella una chiave pubblica per la Persona Giuridica dopo averla ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ROTATED"
    When l'utente "AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG che si trova in stato "ROTATED"
    Then la chiave pubblica in stato "CANCELLED" non è più presente nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_5] Un Amministratore PG blocca una chiave pubblica per la Persona Giuridica dopo averla ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ROTATED"
    When l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "ROTATED"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_6] Un Amministratore PG con un gruppo associato censisce una chiave pubblica per la Persona Giuridica
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" crea una chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_7] Un utente PG censisce una chiave pubblica per la Persona Giuridica
    When l'utente "NON AMMINISTRATORE" crea una chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_8] Un Amministratore PG censisce una chiave pubblica duplicata per la Persona Giuridica
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ACTIVE"
    When l'utente "AMMINISTRATORE" "RICREA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_9] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica senza una chiave attiva con una chiave bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCKED"
    When l'utente "AMMINISTRATORE" "RUOTA" la chiave pubblica per la PG che si trova in stato "BLOCKED"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_10] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica con una chiave associata cancellata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "CANCELLED"
    When l'utente "AMMINISTRATORE" "RUOTA" la chiave pubblica per la PG che si trova in stato "CANCELLED"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_11] Un utente PG ruota la chiave pubblica per la Persona Giuridica
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ACTIVE"
    When l'utente "NON AMMINISTRATORE" "RUOTA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_12] Un Amministratore PG con un gruppo associato ruota la chiave pubblica per la Persona Giuridica
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ACTIVE"
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" "RUOTA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_13] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica già ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ROTATED"
    When l'utente "AMMINISTRATORE" "RUOTA" la chiave pubblica per la PG che si trova in stato "ROTATED"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_14] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica con una chiave ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ROTATED"
    When l'utente "AMMINISTRATORE" "RUOTA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_15] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica senza una chiave associata
    When l'utente "AMMINISTRATORE" "RUOTA" una chiave pubblica per la PG che non esiste
    Then la chiamata restituisce un errore con status code 404 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario Outline: [LEGAL_PERSON_AUTH_16] Un utente (PG / Amministratore con gruppo associato) recupera la lista delle chiavi pubbliche
    When l'utente "<user>" recupera la lista delle chiavi pubbliche
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "GENERIC_ERROR"
    Examples:
      | user                                |
      | NON AMMINISTRATORE                  |
      | AMMINISTRATORE CON GRUPPO ASSOCIATO |

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_17] Un Amministratore PG blocca la chiave pubblica della PG, la quale è già bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCKED"
    When l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "BLOCKED"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_18] Un Amministratore PG blocca la chiave pubblica della PG con chiave pubblica cancellata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "CANCELLED"
    When l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "CANCELLED"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_19] Un Amministratore PG con un gruppo associato blocca la chiave pubblica della PG
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ACTIVE"
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" "BLOCCA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_20] Un Amministratore PG blocca la chiave pubblica per la Persona Giuridica con una chiave bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCKED"
    When l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_21] Un utente PG blocca la chiave pubblica della PG
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ACTIVE"
    When l'utente "NON AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_22] Un Amministratore PG riattiva la chiave pubblica della PG, la quale risulta essere già stata riattivata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ACTIVE"
    When l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_23] Un Amministratore PG riattiva la chiave pubblica della PG, la quale risulta essere già stata cancellata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "CANCELLED"
    When l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG che si trova in stato "CANCELLED"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_24] Un Amministratore PG riattiva la chiave pubblica della PG, la quale risulta essere stata ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ROTATED"
    When l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG che si trova in stato "ROTATED"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario Outline: [LEGAL_PERSON_AUTH_26] Un Amministratore PG con un gruppo associato riattiva la chiave pubblica di una PG di cui non fa parte
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "<status>"
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" "RIATTIVA" la chiave pubblica per la PG che si trova in stato "<status>"
    Then la chiamata restituisce un errore con status code 403
    Examples:
      | status   |
      | BLOCKED  |
      | ROTATED  |

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario Outline: [LEGAL_PERSON_AUTH_27] Un utente PG riattiva la chiave pubblica della PG
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "<status>"
    When l'utente "NON AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG che si trova in stato "<status>"
    Then la chiamata restituisce un errore con status code 403
    Examples:
      | status   |
      | BLOCKED  |
      | ROTATED  |

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_28] Un Amministratore PG riattiva la chiave pubblica della PG dopo averne censita un’altra che risulta attiva
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCKED"
    When l'utente "AMMINISTRATORE" crea una chiave pubblica per la PG
    And l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG che si trova in stato "BLOCKED"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_29] Un Amministratore PG cancella la chiave pubblica della PG che è in stato attivo
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ACTIVE"
    When l'utente "AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_30] Un utente PG cancella la chiave pubblica della PG
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ACTIVE"
    When l'utente "NON AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG che si trova in stato "ACTIVE"
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "GENERIC_ERROR"

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_32] Un Amministratore PG con un gruppo associato cancella la chiave pubblica della PG ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ROTATED"
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" "CANCELLA" la chiave pubblica per la PG che si trova in stato "ROTATED"
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "GENERIC_ERROR"

  @pgAuthentication @legalPersonCuncurrency
  Scenario Outline: [LEGAL_PERSON_AUTH_33] Un Amministratore PG / Utente PG recupera i dati di un utente tramite uno userId
    When un utente tenta di recuperare i dati dell'utente "<userToSearch>"
    Then i dati utente vengono correttamente recuperati
    Examples:
      | userToSearch     |
      | Nilde Iotti      |
      | Alda Merini      |
      | Maria Montessori |

  @pgAuthentication @legalPersonCuncurrency @ignore
  Scenario Outline: [LEGAL_PERSON_AUTH_34] Un Amministratore PG / Utente PG recupera i dati di un utente tramite uno userId o organizzationId vuoto
    When un utente tenta di recuperare i dati dell'utente "<userToSearch>" della pg "<pg>"
    Then la chiamata restituisce un errore con status code 400
    Examples:
      | userToSearch | pg             |
      | vuoto        | corretta       |
      | vuoto        | corretta       |
      | Nilde Iotti  | non corretta   |
      | Alda Merini  | non corretta   |
      | vuoto        | non corretta   |

  @pgAuthentication @legalPersonCuncurrency @ignore
  Scenario: [LEGAL_PERSON_AUTH_36] Un Amministratore PG / Utente PG recupera i dati di un utente tramite uno userId inesistente
    When un utente tenta di recuperare i dati dell'utente "Unknown"
    Then la chiamata va in status 200 e restituisce una lista utenti vuota

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario: [LEGAL_PERSON_AUTH_37] Un Amministratore PG blocca la chiave pubblica della PG passando kid vuoto
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ACTIVE"
    When l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG che si trova in stato "INESISTENTE"
    Then la chiamata restituisce un errore con status code 404

  @publicKeyCreation @pgAuthentication @legalPersonCuncurrency
  Scenario Outline: [LEGAL_PERSON_AUTH_38] Un Amministratore PG blocca la chiave pubblica della PG passando kid non appartenente alla sua pg
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "<status>"
    When l'utente "DI UNA PG DIVERSA" "<operation>" la chiave pubblica per la PG che si trova in stato "<status>"
    Then la chiamata restituisce un errore con status code 404
    Examples:
    | status      | operation |
    | ACTIVE      | RUOTA     |
    | ACTIVE      | BLOCCA    |

