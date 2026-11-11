@purpose @nuovi-operatori @evol-nuovi-operatori @m2m-ra-review
Feature: Validazione dei dati di assegnazione dell'analisi del rischio via M2M

  # Background CDT 7.6
  Background:
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And si ha una compilazione dell'analisi del rischio in modalità AdminWritesReviewerSigns in stato "DRAFT"
    And si memorizzano l'assegnazione e i dati dell'analisi del rischio della finalità

  # CDT 7.6: nessun valutatore nelle mode AWRS/RWRS; valutatori presenti nella mode 1.
  # Nessuna validazione locale della coerenza mode/lista: la richiesta deve raggiungere M2M.
  @sad-path
  Scenario Outline: [M2M_RA_ASSIGNMENT_INVALID_REVIEWERS_01] Elenco valutatori non coerente con <mode>
    When l'utente ha ruolo "m2m-admin" nel profilo API M2M dell'ente "GSP"
    And l'utente tenta di assegnare alla finalità i seguenti valutatori: <valutatori>, in modalità <mode>
    Then si ottiene status code 400
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And l'assegnazione e i dati dell'analisi del rischio sono rimasti invariati

    Examples:
      | mode                        | valutatori |
      | AdminWritesReviewerSigns    | nessuno    |
      | ReviewerWritesReviewerSigns | nessuno    |
      | AdminWritesAdminSigns       | 1          |
      | AdminWritesAdminSigns       | 1,2        |

  # CDT 7.6: l'UUID indicato è di un utente admin, non di un reviewer.
  # Il chiamante resta m2m-admin: è il ruolo del destinatario nel body a essere non valido.
  @sad-path
  Scenario Outline: [M2M_RA_ASSIGNMENT_INVALID_ROLE_01] Utente privo del ruolo Valutatore in <mode>
    When l'utente ha ruolo "m2m-admin" nel profilo API M2M dell'ente "GSP"
    And l'utente assegna un valutatore alla finalità in modalità "<mode>" specificando un utente con ruolo "admin"
    Then si ottiene status code 400
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And l'assegnazione e i dati dell'analisi del rischio sono rimasti invariati

    Examples:
      | mode                        |
      | AdminWritesReviewerSigns    |
      | ReviewerWritesReviewerSigns |

  # SKIPPED 20/10/2026 - CDT 7.6, combinazione "Valutatore duplicato nell'elenco".
  # RiskAnalysisAssignmentSeed M2M usa Set<UUID>: la conversione List -> LinkedHashSet
  # rimuove i duplicati prima dell'invio. Con i soli metodi tipizzati del client generato
  # non è possibile inviare il body richiesto: il caso NON è coperto. Riesaminare se cambia il contratto.
