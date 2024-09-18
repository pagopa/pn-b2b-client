Feature: Public key legal Person Authentication

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_1] Un Amministratore PG censisce una chiave pubblica per la Persona Giuridica dopo averla cancellata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "CANCELLATA"
    When l'utente "AMMINISTRATORE" "RICREA" la chiave pubblica per la PG
    Then la chiave pubblica viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_2] Un Amministratore PG censisce una chiave pubblica per la Persona Giuridica dopo averla bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCCATA"
    When l'utente "AMMINISTRATORE" "RICREA" la chiave pubblica per la PG
    Then la chiave pubblica viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_3] Un Amministratore PG riattiva una chiave pubblica per la Persona Giuridica dopo averla bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCCATA"
    When l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG
    Then la chiave pubblica viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_4] Un Amministratore PG cancella una chiave pubblica per la Persona Giuridica dopo averla ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "RUOTATA"
    When l'utente "AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG
    Then la chiave pubblica non è più presente nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_5] Un Amministratore PG blocca una chiave pubblica per la Persona Giuridica dopo averla ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "RUOTATA"
    When l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG
    Then la chiave pubblica non è più presente nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_5] Un Amministratore PG blocca una chiave pubblica per la Persona Giuridica dopo averla ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG
    Then la chiave pubblica non è più presente nell'elenco delle chiavi pubbliche per la PG

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_6] Un Amministratore PG con un gruppo associato censisce una chiave pubblica per la Persona Giuridica
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" "CREA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_7] Un utente PG censisce una chiave pubblica per la Persona Giuridica
    When l'utente "NON AMMINISTRATORE" "CREA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_8] Un Amministratore PG censisce una chiave pubblica duplicata per la Persona Giuridica
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'utente "AMMINISTRATORE" "RICREA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_9] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica senza una chiave attiva con una chiave bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCCATA"
    When l'utente "AMMINISTRATORE" "RUOTA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_10] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica con una chiave associata cancellata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "CANCELLATA"
    When l'utente "AMMINISTRATORE" "RUOTA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_11] Un utente PG ruota la chiave pubblica per la Persona Giuridica
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'utente "NON AMMINISTRATORE" "RUOTA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_12] Un Amministratore PG con un gruppo associato ruota la chiave pubblica per la Persona Giuridica
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" "RUOTA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_13] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica già ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "RUOTATA"
    When l'utente "AMMINISTRATORE" "RUOTA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_14] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica con una chiave ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "RUOTATA"
    When l'utente "AMMINISTRATORE" crea una seconda chiava pubblica e prova a cambiarne lo stato in "RUOTATA"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_15] Un Amministratore PG ruota la chiave pubblica per la Persona Giuridica senza una chiave associata
    When l'utente "AMMINISTRATORE" "RUOTA" una chiave pubblica per la PG che non esiste
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario Outline: [LEGAL_PERSON_AUTH_16] Un utente (PG / Amministratore con gruppo associato) recupera la lista delle chiavi pubbliche
    When l'utente "<user>" recupera la lista delle chiavi pubbliche
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "TODO"
    Examples:
      | user                                |
      | NON AMMINISTRATORE                  |
      | AMMINISTRATORE CON GRUPPO ASSOCIATO |

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_17] Un Amministratore PG blocca la chiave pubblica della PG, la quale è già bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCCATA"
    When l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_18] Un Amministratore PG blocca la chiave pubblica della PG con chiave pubblica cancellata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "CANCELLATA"
    When l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_19] Un Amministratore PG con un gruppo associato blocca la chiave pubblica della PG
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" "BLOCCA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_20] Un Amministratore PG blocca la chiave pubblica per la Persona Giuridica con una chiave bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCCATA"
    When l'utente "AMMINISTRATORE" crea una seconda chiava pubblica e prova a cambiarne lo stato in "RUOTATA"
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_21] Un utente PG blocca la chiave pubblica della PG
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'utente "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_22] Un Amministratore PG riattiva la chiave pubblica della PG, la quale risulta essere già stata riattivata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_23] Un Amministratore PG riattiva la chiave pubblica della PG, la quale risulta essere già stata cancellata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "CANCELLATA"
    When l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_24] Un Amministratore PG riattiva la chiave pubblica della PG, la quale risulta essere stata ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "RUOTATA"
    When l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_25] Un Amministratore PG riattiva la chiave pubblica della PG, la quale risulta essere scaduta
    #TODO
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "SCADUTA"
    When l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario Outline: [LEGAL_PERSON_AUTH_26] Un Amministratore PG con un gruppo associato riattiva la chiave pubblica della PG
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "<status>"
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" "RIATTIVA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "<message>"
    Examples:
      | status   | message |
      | BLOCCATA | TODO    |
      | RUOTATA  | TODO    |

  @publicKeyCreation
  Scenario Outline: [LEGAL_PERSON_AUTH_27] Un utente PG riattiva la chiave pubblica della PG
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "<status>"
    When l'utente "NON AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "<message>"
    Examples:
      | status   | message |
      | BLOCCATA | TODO    |
      | RUOTATA  | TODO    |

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_28] Un Amministratore PG riattiva la chiave pubblica della PG dopo averne censita un’altra che risulta attiva
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCCATA"
    When l'utente "AMMINISTRATORE" crea una seconda chiava pubblica
    And l'utente "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_29] Un Amministratore PG riattiva la chiave pubblica della PG dopo averne censita un’altra che risulta attiva
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'utente "AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_30] Un utente PG cancella la chiave pubblica della PG
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'utente "NON AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_31] Un Amministratore PG cancella la chiave pubblica della PG scaduta
    #TODO
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "SCADUTA"
    When l'utente "AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 409 riportante il messaggio "TODO"

  @publicKeyCreation
  Scenario: [LEGAL_PERSON_AUTH_32] Un Amministratore PG con un gruppo associato cancella la chiave pubblica della PG ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "RUOTATA"
    When l'utente "AMMINISTRATORE CON GRUPPO ASSOCIATO" "CANCELLA" la chiave pubblica per la PG
    Then la chiamata restituisce un errore con status code 403 riportante il messaggio "TODO"




















