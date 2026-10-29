@purpose @nuovi-operatori @evol-nuovi-operatori @sad-path
Feature: Diniego delle modifiche di assegnazione dell'analisi del rischio

  # Ticket aperto: https://pagopa.atlassian.net/browse/PIN-11237
  # CDT 1.4, 1.5, 7.5 e sola scrittura di 7.7.
  # La preparazione usa l'admin BFF GSP e una richiesta valida per isolare il motivo del diniego.
  # "reviewer": 1 -> 2, mode invariata. "mode": reviewer 1 invariato, mode 2 <-> 3.
  # Per mode 1 pubblicata si provano solo cambi mode validi verso 2/3 con reviewer 1.
  # Codici concordati: 409 per firma/pubblicazione, 403 per autorizzazione.
  Scenario Outline: [RA_ASSIGNMENT_DENIED_01] Diniego modifica assegnazione per stato non modificabile o ruolo non autorizzato (<apiSet> - <caso> - <modeIniziale> - <ruolo> di <enteAttore>)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un amministratore del profilo API BFF dell'ente "GSP"
    And si ha una compilazione dell'analisi del rischio in modalità <modeIniziale> in stato "<statoRA>"
    And "GSP" ha già portato la finalità in stato "<statoFinalita>"
    And si memorizzano l'assegnazione e i dati dell'analisi del rischio della finalità
    When l'utente ha ruolo "<ruolo>" nel profilo API <apiSet> dell'ente "<enteAttore>"
    And l'utente tenta di assegnare alla finalità i seguenti valutatori: <reviewerFinali>, in modalità <modeFinale>
    Then si ottiene status code <status>
    And l'utente è un "admin" di "GSP"
    And l'assegnazione e i dati dell'analisi del rischio sono rimasti invariati

    @bff-ra-review
    Examples: CDT 1.4 - BFF RA firmata
      | apiSet | caso            | modeIniziale                | statoRA | statoFinalita | ruolo | enteAttore | reviewerFinali | modeFinale                  | status |
      | BFF    | SIGNED reviewer | AdminWritesReviewerSigns    | SIGNED  | DRAFT         | admin | GSP        | 2              | AdminWritesReviewerSigns    | 409    |
      | BFF    | SIGNED mode     | AdminWritesReviewerSigns    | SIGNED  | DRAFT         | admin | GSP        | 1              | ReviewerWritesReviewerSigns | 409    |
      | BFF    | SIGNED reviewer | ReviewerWritesReviewerSigns | SIGNED  | DRAFT         | admin | GSP        | 2              | ReviewerWritesReviewerSigns | 409    |
      | BFF    | SIGNED mode     | ReviewerWritesReviewerSigns | SIGNED  | DRAFT         | admin | GSP        | 1              | AdminWritesReviewerSigns    | 409    |

    # CDT 1.5: RA modificabile per isolare il diniego di ruolo (non il blocco SIGNED).
    @bff-ra-review
    Examples: CDT 1.5 - BFF ruoli non autorizzati
      | apiSet | caso     | modeIniziale                | statoRA  | statoFinalita | ruolo        | enteAttore | reviewerFinali | modeFinale                  | status |
      | BFF    | reviewer | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | support      | GSP        | 2              | AdminWritesReviewerSigns    | 403    |
      | BFF    | mode     | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | support      | GSP        | 1              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | reviewer | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | api          | GSP        | 2              | AdminWritesReviewerSigns    | 403    |
      | BFF    | mode     | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | api          | GSP        | 1              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | reviewer | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | security     | GSP        | 2              | AdminWritesReviewerSigns    | 403    |
      | BFF    | mode     | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | security     | GSP        | 1              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | reviewer | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | api,security | GSP        | 2              | AdminWritesReviewerSigns    | 403    |
      | BFF    | mode     | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | api,security | GSP        | 1              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | reviewer | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | reviewer     | GSP        | 2              | AdminWritesReviewerSigns    | 403    |
      | BFF    | mode     | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | reviewer     | GSP        | 1              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | reviewer | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | viewer       | GSP        | 2              | AdminWritesReviewerSigns    | 403    |
      | BFF    | mode     | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | viewer       | GSP        | 1              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | reviewer | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | support      | GSP        | 2              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | mode     | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | support      | GSP        | 1              | AdminWritesReviewerSigns    | 403    |
      | BFF    | reviewer | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | api          | GSP        | 2              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | mode     | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | api          | GSP        | 1              | AdminWritesReviewerSigns    | 403    |
      | BFF    | reviewer | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | security     | GSP        | 2              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | mode     | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | security     | GSP        | 1              | AdminWritesReviewerSigns    | 403    |
      | BFF    | reviewer | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | api,security | GSP        | 2              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | mode     | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | api,security | GSP        | 1              | AdminWritesReviewerSigns    | 403    |
      | BFF    | reviewer | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | reviewer     | GSP        | 2              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | mode     | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | reviewer     | GSP        | 1              | AdminWritesReviewerSigns    | 403    |
      | BFF    | reviewer | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | viewer       | GSP        | 2              | ReviewerWritesReviewerSigns | 403    |
      | BFF    | mode     | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | viewer       | GSP        | 1              | AdminWritesReviewerSigns    | 403    |

    @m2m-ra-review
    Examples: CDT 7.5 - M2M RA firmata o finalità pubblicata
      | apiSet | caso                | modeIniziale                | statoRA | statoFinalita | ruolo     | enteAttore | reviewerFinali | modeFinale                  | status |
      | M2M    | Pubblicata mode 1-2 | AdminWritesAdminSigns       | DRAFT   | ACTIVE        | m2m-admin | GSP        | 1              | AdminWritesReviewerSigns    | 409    |
      | M2M    | Pubblicata mode 1-3 | AdminWritesAdminSigns       | DRAFT   | ACTIVE        | m2m-admin | GSP        | 1              | ReviewerWritesReviewerSigns | 409    |
      | M2M    | SIGNED reviewer     | AdminWritesReviewerSigns    | SIGNED  | DRAFT         | m2m-admin | GSP        | 2              | AdminWritesReviewerSigns    | 409    |
      | M2M    | SIGNED mode         | AdminWritesReviewerSigns    | SIGNED  | DRAFT         | m2m-admin | GSP        | 1              | ReviewerWritesReviewerSigns | 409    |
      | M2M    | SIGNED reviewer     | ReviewerWritesReviewerSigns | SIGNED  | DRAFT         | m2m-admin | GSP        | 2              | ReviewerWritesReviewerSigns | 409    |
      | M2M    | SIGNED mode         | ReviewerWritesReviewerSigns | SIGNED  | DRAFT         | m2m-admin | GSP        | 1              | AdminWritesReviewerSigns    | 409    |

    # CDT 7.7: sola scrittura. Client m2m-admin proprietario coperto dagli Outline positivi.
    # Le verifiche di lettura per m2m proprietario/altro ente restano da implementare separatamente.
    # PA2 autentica l'attore; gli UUID reviewer nel payload sono sempre quelli del proprietario GSP.
    @m2m-ra-review
    Examples: CDT 7.7 - M2M dinieghi per ruolo o ente
      | apiSet | caso     | modeIniziale                | statoRA  | statoFinalita | ruolo     | enteAttore | reviewerFinali | modeFinale                  | status |
      | M2M    | reviewer | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | m2m-admin | PA2        | 2              | AdminWritesReviewerSigns    | 403    |
      | M2M    | mode     | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | m2m-admin | PA2        | 1              | ReviewerWritesReviewerSigns | 403    |
      | M2M    | reviewer | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | m2m-admin | PA2        | 2              | ReviewerWritesReviewerSigns | 403    |
      | M2M    | mode     | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | m2m-admin | PA2        | 1              | AdminWritesReviewerSigns    | 403    |
      | M2M    | reviewer | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | m2m       | GSP        | 2              | AdminWritesReviewerSigns    | 403    |
      | M2M    | mode     | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | m2m       | GSP        | 1              | ReviewerWritesReviewerSigns | 403    |
      | M2M    | reviewer | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | m2m       | GSP        | 2              | ReviewerWritesReviewerSigns | 403    |
      | M2M    | mode     | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | m2m       | GSP        | 1              | AdminWritesReviewerSigns    | 403    |
      | M2M    | reviewer | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | m2m       | PA2        | 2              | AdminWritesReviewerSigns    | 403    |
      | M2M    | mode     | AdminWritesReviewerSigns    | DRAFT    | DRAFT         | m2m       | PA2        | 1              | ReviewerWritesReviewerSigns | 403    |
      | M2M    | reviewer | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | m2m       | PA2        | 2              | ReviewerWritesReviewerSigns | 403    |
      | M2M    | mode     | ReviewerWritesReviewerSigns | ASSIGNED | DRAFT         | m2m       | PA2        | 1              | AdminWritesReviewerSigns    | 403    |



