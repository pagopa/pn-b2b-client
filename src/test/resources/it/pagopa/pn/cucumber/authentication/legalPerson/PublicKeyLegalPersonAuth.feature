Feature: Public key legal Person Authentication

  Scenario: [LEGAL_PERSON_AUTH_1] Amministratore PG censisce una chiave pubblica per la Persona Giuridica dopo averla cancellata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "CANCELLATA"
    When l'amministratore "AMMINISTRATORE" "RICREA" la chiave pubblica per la PG
    Then la chiave pubblica viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG

  Scenario: [LEGAL_PERSON_AUTH_2] Amministratore PG censisce una chiave pubblica per la Persona Giuridica dopo averla bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCCATA"
    When l'amministratore "AMMINISTRATORE" "RICREA" la chiave pubblica per la PG
    Then la chiave pubblica viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG

  Scenario: [LEGAL_PERSON_AUTH_3] Amministratore PG riattiva una chiave pubblica per la Persona Giuridica dopo averla bloccata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "BLOCCATA"
    When l'amministratore "AMMINISTRATORE" "RIATTIVA" la chiave pubblica per la PG
    Then la chiave pubblica viene correttamente visualizzata nell'elenco delle chiavi pubbliche per la PG

  Scenario: [LEGAL_PERSON_AUTH_4] Amministratore PG cancella una chiave pubblica per la Persona Giuridica dopo averla ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "RUOTATA"
    When l'amministratore "AMMINISTRATORE" "CANCELLA" la chiave pubblica per la PG
    Then //TODO verificare non sia presente

  Scenario: [LEGAL_PERSON_AUTH_5] Amministratore PG blocca una chiave pubblica per la Persona Giuridica dopo averla ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "RUOTATA"
    When l'amministratore "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG
    Then //TODO verificare non sia presente

  Scenario: [LEGAL_PERSON_AUTH_5] Amministratore PG blocca una chiave pubblica per la Persona Giuridica dopo averla ruotata
    Given esiste una chiave pubblica creata da "AMMINISTRATORE" in stato "ATTIVA"
    When l'amministratore "AMMINISTRATORE" "BLOCCA" la chiave pubblica per la PG
    Then //TODO verificare non sia presente

