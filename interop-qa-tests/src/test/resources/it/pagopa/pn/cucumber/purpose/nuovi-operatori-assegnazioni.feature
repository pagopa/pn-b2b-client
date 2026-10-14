# NOTA: considerare di compattare gli scenari di questo file in uno Scenario Outline
@purpose @nuovi-operatori
Feature: Consultazione delle assegnazioni e modifica della finalità per i Nuovi Operatori

  # PST: Scenario 31 - Caso 31.1
  @happy-path
  Scenario: [AWRS_MODIFICA_31_1_SUBMITTED] Modifica parti della finalità con workflow di revisione in stato SUBMITTED
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico
    And l'utente invia il submit dell'analisi del rischio della finalità con successo
    When l'utente è un "admin" di "PA2"
    And l'utente aggiorna il titolo della finalità
    Then si ottiene status code 200
    And il titolo della finalità è stato aggiornato correttamente
    And lo stato della compilazione dell'analisi del rischio è "SUBMITTED"

  # PST: Scenario 31 - Caso 31.1
  @happy-path
  Scenario: [AWRS_MODIFICA_31_1_SIGNED] Modifica parti della finalità con workflow di revisione in stato SIGNED
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico con successo
    And l'utente invia il submit dell'analisi del rischio della finalità con successo
    And il valutatore assegnato convalida l'analisi del rischio della finalità con successo
    When l'utente è un "admin" di "PA2"
    And l'utente aggiorna il titolo della finalità
    Then si ottiene status code 200
    And il titolo della finalità è stato aggiornato correttamente
    And lo stato della compilazione dell'analisi del rischio è "SIGNED"

  # PST: Scenario 32 - Caso 32.1
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_NESSUN_FILTRO] Lettura assegnazioni del valutatore senza filtri
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico con successo
    And l'utente invia il submit dell'analisi del rischio della finalità con successo
    When l'utente è un "reviewer" di "PA2"
    And tenta di interrogare l'endpoint delle assegnazioni del valutatore senza filtri
    Then si ottiene status code 200
    And vengono restituite 2 finalità attese nelle assegnazioni

  # PST: Scenario 32 - Caso 32.1 (con filtro stato)
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_FILTRO_STATO] Lettura assegnazioni del valutatore con filtro stato=ASSIGNED
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico con successo
    And l'utente invia il submit dell'analisi del rischio della finalità con successo
    When l'utente è un "reviewer" di "PA2"
    And interroga l'endpoint delle assegnazioni del valutatore con filtro stato "ASSIGNED"
    Then si ottiene status code 200
    And viene restituita una sola finalità in stato "ASSIGNED" nelle assegnazioni

  # PST: Scenario 32 - Caso 32.1 (con parametro offset)
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_OFFSET] Lettura assegnazioni del valutatore con parametro offset=1
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico con successo
    And l'utente invia il submit dell'analisi del rischio della finalità con successo
    When l'utente è un "reviewer" di "PA2"
    And interroga l'endpoint delle assegnazioni del valutatore con parametro offset 1
    Then si ottiene status code 200
    And viene restituita una sola finalità nelle assegnazioni

  # PST: Scenario 32 - Caso 32.1 (con parametro limit)
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_LIMIT] Lettura assegnazioni del valutatore con parametro limit=1
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico con successo
    And l'utente invia il submit dell'analisi del rischio della finalità con successo
    When l'utente è un "reviewer" di "PA2"
    And interroga l'endpoint delle assegnazioni del valutatore con parametro limit 1
    Then si ottiene status code 200
    And viene restituita una sola finalità nelle assegnazioni

  # PST: Scenario 32 - Caso 32.1 (con filtro e-service)
  @happy-path
  Scenario: [AWRS_LETTURA_32_1_FILTRO_ESERVICE] Lettura assegnazioni del valutatore con filtro per e-service specifico
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "ReviewerWritesReviewerSigns" con successo
	# Seconda finalità: nuovo e-service + nuovo agreement + workflow SUBMITTED
    And "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And l'utente assegna un valutatore alla finalità in modalità "AdminWritesReviewerSigns" con successo
    And compila l'analisi del rischio tramite endpoint generico con successo
    And l'utente invia il submit dell'analisi del rischio della finalità con successo
    When l'utente è un "reviewer" di "PA2"
    And interroga l'endpoint delle assegnazioni del valutatore filtrando per il primo e-service creato
    Then si ottiene status code 200
    And viene restituita una sola finalità associata al primo e-service creato nelle assegnazioni

  # PST: Scenario 32 - Caso 32.2
  # NOTA 22/06/2026: test difficile da automatizzare in quanto richiede un utente valutatore non assegnato
  # a nessuna finalità, il che è fuori dal controllo della suite di TA (un precedente test può avere lasciato
  # delle assegnazioni, oppure potrebbe averlo fatto un test UI).
  # Si minimizza il problema utilizzando un ente poco utilizzato (PA4).
  @happy-path
  Scenario: [AWRS_LETTURA_32_2_NESSUNA_ASSEGNAZIONE] Lettura assegnazioni quando valutatore non è assegnato a nessuna finalità
    When l'utente è un "reviewer" di "PA4"
    And tenta di interrogare l'endpoint delle assegnazioni del valutatore senza filtri
    Then si ottiene status code 200
    And la risposta contiene 0 risultati nelle assegnazioni

  # PST: Scenario 32 - Caso 32.3
  @sad-path
  Scenario Outline: [AWRS_LETTURA_32_3_ACCESSO_NEGATO] Accesso negato per utenti non valutatori all'endpoint delle assegnazioni
    When l'utente è un "<ruolo>" di "PA2"
    And tenta di interrogare l'endpoint delle assegnazioni del valutatore senza filtri
    Then si ottiene status code 403
    Examples:
      | ruolo    |
      | api      |
      | admin    |
      | support  |
      | security |

  # PST: https://pagopa.atlassian.net/wiki/x/KAH_x
  # CDT 1.1: variazioni dei valutatori e della modalità.
  # CDT 7.2 (Examples M2M): aggiunta a un'assegnazione esistente, 1 -> 1,2
  # e 1,2 -> 1,2,3,4, mantenendo tutti i valutatori già assegnati.
  # Prime assegnazioni singole e multiple in M2M_RA_REVIEW_01.
  # CDT 7.3 (Examples M2M): rimozione singola 1,2 -> 1,
  # rimozione multipla 1,2,3,4 -> 1,2 e sostituzione completa 1,2 -> 3,4.
  Scenario Outline: [ASSIGN_MORE_REVIEWERS_01] <apiSet> - <caso> da <modeIniziale>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "GSP"
    And si ha una compilazione dell'analisi del rischio in modalità <modeIniziale> in stato "<statoIniziale>"
    And l'utente è un amministratore del profilo API BFF dell'ente "GSP"
    And l'utente assegna con successo i valutatori <reviewerIniziali> alla finalità in modalità <modeIniziale>
    When l'utente è un amministratore del profilo API <apiSet> dell'ente "GSP"
    And l'utente tenta di assegnare i valutatori <reviewerFinali> alla finalità in modalità <modeFinale>
    Then si ottiene status code 204
    And l'utente è un "admin" di "GSP"
    And alla finalità sono associati esclusivamente i valutatori <reviewerFinali> in modalità <modeFinale>
    And ai valutatori assegnati risulta correttamente assegnata la finalità
    And i valutatori rimossi nel passaggio da <reviewerIniziali> a <reviewerFinali> non vedono più la finalità nelle assegnazioni

    # Con quattro reviewer, i cambi di cardinalità singolo/multipli restano distinti
    # dalle aggiunte/rimozioni multiple che mantengono un gruppo e dalle sostituzioni di gruppo.
    Examples: BFF
      | apiSet | caso                                    | modeIniziale                | statoIniziale | reviewerIniziali | reviewerFinali | modeFinale                  |
      | BFF    | Aggiunta - singolo a multipli           | AdminWritesReviewerSigns    | DRAFT         | 1                | 1,2            | AdminWritesReviewerSigns    |
      | BFF    | Rimozione - multipli a singolo          | AdminWritesReviewerSigns    | DRAFT         | 1,2              | 1              | AdminWritesReviewerSigns    |
      | BFF    | Sostituzione completa                   | AdminWritesReviewerSigns    | DRAFT         | 1                | 2              | AdminWritesReviewerSigns    |
      | BFF    | Cambio contemporaneo reviewer e mode    | AdminWritesReviewerSigns    | DRAFT         | 1                | 2              | ReviewerWritesReviewerSigns |
      | BFF    | Aggiunta - singolo a multipli           | ReviewerWritesReviewerSigns | ASSIGNED      | 1                | 1,2            | ReviewerWritesReviewerSigns |
      | BFF    | Rimozione - multipli a singolo          | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2              | 1              | ReviewerWritesReviewerSigns |
      | BFF    | Sostituzione completa                   | ReviewerWritesReviewerSigns | ASSIGNED      | 1                | 2              | ReviewerWritesReviewerSigns |
      | BFF    | Cambio contemporaneo reviewer e mode    | ReviewerWritesReviewerSigns | ASSIGNED      | 1                | 2              | AdminWritesReviewerSigns    |
      # CDT 1.1: casi di gruppo con quattro reviewer.
      | BFF    | Aggiunta multipla a gruppo esistente    | AdminWritesReviewerSigns    | DRAFT         | 1,2              | 1,2,3,4        | AdminWritesReviewerSigns    |
      | BFF    | Aggiunta multipla a gruppo esistente    | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2              | 1,2,3,4        | ReviewerWritesReviewerSigns |
      | BFF    | Rimozione multipla da gruppo            | AdminWritesReviewerSigns    | DRAFT         | 1,2,3,4          | 1,2            | AdminWritesReviewerSigns    |
      | BFF    | Rimozione multipla da gruppo            | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2,3,4          | 1,2            | ReviewerWritesReviewerSigns |
      | BFF    | Sostituzione completa del gruppo        | AdminWritesReviewerSigns    | DRAFT         | 1,2              | 3,4            | AdminWritesReviewerSigns    |
      | BFF    | Sostituzione completa del gruppo        | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2              | 3,4            | ReviewerWritesReviewerSigns |
      | BFF    | Cambio contemporaneo gruppo e mode     | AdminWritesReviewerSigns    | DRAFT         | 1,2              | 3,4            | ReviewerWritesReviewerSigns |
      | BFF    | Cambio contemporaneo gruppo e mode     | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2              | 3,4            | AdminWritesReviewerSigns    |

    @m2m-ra-review
    Examples: M2M
      | apiSet | caso                                    | modeIniziale                | statoIniziale | reviewerIniziali | reviewerFinali | modeFinale                  |
      | M2M    | Aggiunta - singolo a multipli           | AdminWritesReviewerSigns    | DRAFT         | 1                | 1,2            | AdminWritesReviewerSigns    |
      | M2M    | Rimozione - multipli a singolo          | AdminWritesReviewerSigns    | DRAFT         | 1,2              | 1              | AdminWritesReviewerSigns    |
      # CDT 7.4: modifica prima della firma in mode 2, stato DRAFT.
      | M2M    | Sostituzione completa                   | AdminWritesReviewerSigns    | DRAFT         | 1                | 2              | AdminWritesReviewerSigns    |
      | M2M    | Cambio contemporaneo reviewer e mode    | AdminWritesReviewerSigns    | DRAFT         | 1                | 2              | ReviewerWritesReviewerSigns |
      | M2M    | Aggiunta - singolo a multipli           | ReviewerWritesReviewerSigns | ASSIGNED      | 1                | 1,2            | ReviewerWritesReviewerSigns |
      | M2M    | Rimozione - multipli a singolo          | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2              | 1              | ReviewerWritesReviewerSigns |
      # CDT 7.4: modifica prima della firma in mode 3, stato ASSIGNED.
      # Verificare che mode 2 (AdminWritesReviewerSigns) non preveda lo stato ASSIGNED.
      | M2M    | Sostituzione completa                   | ReviewerWritesReviewerSigns | ASSIGNED      | 1                | 2              | ReviewerWritesReviewerSigns |
      | M2M    | Cambio contemporaneo reviewer e mode    | ReviewerWritesReviewerSigns | ASSIGNED      | 1                | 2              | AdminWritesReviewerSigns    |
      # CDT 7.2: più valutatori già assegnati nel Given; aggiunta di 3 e 4 senza rimuovere 1 e 2.
      | M2M    | Aggiunta multipla a gruppo esistente    | AdminWritesReviewerSigns    | DRAFT         | 1,2              | 1,2,3,4        | AdminWritesReviewerSigns    |
      | M2M    | Aggiunta multipla a gruppo esistente    | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2              | 1,2,3,4        | ReviewerWritesReviewerSigns |
      # CDT 7.3: rimozione contemporanea di 3 e 4, mantenendo 1 e 2.
      | M2M    | Rimozione multipla da gruppo            | AdminWritesReviewerSigns    | DRAFT         | 1,2,3,4          | 1,2            | AdminWritesReviewerSigns    |
      | M2M    | Rimozione multipla da gruppo            | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2,3,4          | 1,2            | ReviewerWritesReviewerSigns |
      # CDT 7.3: esclusione di tutti i reviewer iniziali e assegnazione di un nuovo gruppo.
      | M2M    | Sostituzione completa del gruppo        | AdminWritesReviewerSigns    | DRAFT         | 1,2              | 3,4            | AdminWritesReviewerSigns    |
      | M2M    | Sostituzione completa del gruppo        | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2              | 3,4            | ReviewerWritesReviewerSigns |
      | M2M    | Cambio contemporaneo gruppo e mode     | AdminWritesReviewerSigns    | DRAFT         | 1,2              | 3,4            | ReviewerWritesReviewerSigns |
      | M2M    | Cambio contemporaneo gruppo e mode     | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2              | 3,4            | AdminWritesReviewerSigns    |

  # CDT 7.2: prima assegnazione singola e multipla via M2M.
  # CDT 7.4: mode 1 non pubblicata e mode 2 in SUBMITTED/REJECTED.
  # Mode 2 DRAFT e mode 3 ASSIGNED sono coperti dagli Examples M2M di ASSIGN_MORE_REVIEWERS_01.
  @m2m-ra-review
  Scenario Outline: [M2M_RA_REVIEW_01] Modifica assegnazione prima di firma o pubblicazione - <caso>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "GSP"
    And si ha un'analisi del rischio non firmata in modalità <modeIniziale> in stato "<statoIniziale>"
    When l'utente è un amministratore del profilo API M2M dell'ente "GSP"
    And l'utente tenta di assegnare i valutatori <reviewerFinali> alla finalità in modalità <modeFinale>
    Then si ottiene status code 204
    And l'utente è un "admin" di "GSP"
    And alla finalità sono associati esclusivamente i valutatori <reviewerFinali> in modalità <modeFinale>
    And ai valutatori assegnati risulta correttamente assegnata la finalità

    Examples:
      | caso                                           | modeIniziale                | statoIniziale | reviewerFinali | modeFinale                  |
      | Mode 1 non pubblicata verso mode 2              | AdminWritesAdminSigns       | DRAFT         | 2              | AdminWritesReviewerSigns    |
      | Mode 1 non pubblicata verso mode 3              | AdminWritesAdminSigns       | DRAFT         | 2              | ReviewerWritesReviewerSigns |
      | Prima assegnazione multipla verso mode 2        | AdminWritesAdminSigns       | DRAFT         | 1,2            | AdminWritesReviewerSigns    |
      | Prima assegnazione multipla verso mode 3        | AdminWritesAdminSigns       | DRAFT         | 1,2            | ReviewerWritesReviewerSigns |
      | Mode 2 SUBMITTED - sostituzione reviewer        | AdminWritesReviewerSigns    | SUBMITTED     | 2              | AdminWritesReviewerSigns    |
      | Mode 2 REJECTED - sostituzione reviewer         | AdminWritesReviewerSigns    | REJECTED      | 2              | AdminWritesReviewerSigns    |
