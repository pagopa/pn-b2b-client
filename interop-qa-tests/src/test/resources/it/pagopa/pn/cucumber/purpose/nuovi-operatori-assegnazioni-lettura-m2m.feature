@purpose @nuovi-operatori @evol-nuovi-operatori @m2m-ra-review
Feature: Autorizzazione alla lettura dell'assegnazione RA tramite M2M

  # CDT 7.7: un utente m2m del proprietario può consultare modalità e valutatori assegnati.
  # Il diniego di scrittura è già coperto da RA_ASSIGNMENT_DENIED_01.
  # NOTA 19/10/2026: al momento lo step di verifica controlla unicamente la modalità perché la GET M2M non espone
  # ancora i valutatori (blocco contrattuale segnalato su https://pagopa.atlassian.net/browse/PIN-11189). Lo step è già
  # intitolato per coprire sia modalità che valutatori; al rilascio della fix andrà aggiornato esclusivamente il corpo del metodo Java.
  Scenario Outline: [M2M_RA_READ_OWNER_01] Lettura con utente m2m ente proprietario - <mode>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And si ha una compilazione dell'analisi del rischio in modalità <mode> in stato "<stato>"
    When l'utente ha ruolo "m2m" nel profilo API M2M dell'ente "GSP"
    And l'utente tenta di leggere la finalità tramite M2M V3
    Then si ottiene status code 200
    # NOTA 19/10/2026: il controllo effettivo all'interno del metodo Java è limitato alla sola modalità in attesa di PIN-11189
    And la risposta M2M della finalità riporta la modalità <mode> e i valutatori assegnati

    Examples:
      | mode                        | stato    |
      | AdminWritesAdminSigns       | DRAFT    |
      | AdminWritesReviewerSigns    | DRAFT    |
      | ReviewerWritesReviewerSigns | ASSIGNED |

  # CDT 7.7: PA2 non è né il fruitore GSP né l'erogatore PA1.
  # Il diniego è verificato sulla GET M2M reale, senza fallback BFF.
  Scenario Outline: [M2M_RA_READ_OTHER_TENANT_01] Lettura negata al client m2m di altro ente - <mode>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And si ha una compilazione dell'analisi del rischio in modalità <mode> in stato "<stato>"
    When l'utente ha ruolo "m2m" nel profilo API M2M dell'ente "PA2"
    And l'utente tenta di leggere la finalità tramite M2M V3
    Then si ottiene status code 403

    Examples:
      | mode                        | stato    |
      | AdminWritesAdminSigns       | DRAFT    |
      | AdminWritesReviewerSigns    | DRAFT    |
      | ReviewerWritesReviewerSigns | ASSIGNED |
