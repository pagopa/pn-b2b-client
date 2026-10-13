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

    # Con due reviewer, aggiunta e singolo -> multipli coincidono;
    # analogamente, rimozione e multipli -> singolo coincidono.
    Examples: BFF
      | apiSet | caso                                 | modeIniziale                | statoIniziale | reviewerIniziali | reviewerFinali | modeFinale                  |
      | BFF    | Aggiunta - singolo a multipli         | AdminWritesReviewerSigns    | DRAFT         | 1               | 1,2            | AdminWritesReviewerSigns    |
      | BFF    | Rimozione - multipli a singolo        | AdminWritesReviewerSigns    | DRAFT         | 1,2             | 1              | AdminWritesReviewerSigns    |
      | BFF    | Sostituzione completa                | AdminWritesReviewerSigns    | DRAFT         | 1               | 2              | AdminWritesReviewerSigns    |
      | BFF    | Cambio contemporaneo reviewer e mode | AdminWritesReviewerSigns    | DRAFT         | 1               | 2              | ReviewerWritesReviewerSigns |
      | BFF    | Aggiunta - singolo a multipli         | ReviewerWritesReviewerSigns | ASSIGNED      | 1               | 1,2            | ReviewerWritesReviewerSigns |
      | BFF    | Rimozione - multipli a singolo        | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2             | 1              | ReviewerWritesReviewerSigns |
      | BFF    | Sostituzione completa                | ReviewerWritesReviewerSigns | ASSIGNED      | 1               | 2              | ReviewerWritesReviewerSigns |
      | BFF    | Cambio contemporaneo reviewer e mode | ReviewerWritesReviewerSigns | ASSIGNED      | 1               | 2              | AdminWritesReviewerSigns    |

    @m2m-ra-review
    Examples: M2M
      | apiSet | caso                                 | modeIniziale                | statoIniziale | reviewerIniziali | reviewerFinali | modeFinale                  |
      | M2M    | Aggiunta - singolo a multipli         | AdminWritesReviewerSigns    | DRAFT         | 1               | 1,2            | AdminWritesReviewerSigns    |
      | M2M    | Rimozione - multipli a singolo        | AdminWritesReviewerSigns    | DRAFT         | 1,2             | 1              | AdminWritesReviewerSigns    |
      | M2M    | Sostituzione completa                | AdminWritesReviewerSigns    | DRAFT         | 1               | 2              | AdminWritesReviewerSigns    |
      | M2M    | Cambio contemporaneo reviewer e mode | AdminWritesReviewerSigns    | DRAFT         | 1               | 2              | ReviewerWritesReviewerSigns |
      | M2M    | Aggiunta - singolo a multipli         | ReviewerWritesReviewerSigns | ASSIGNED      | 1               | 1,2            | ReviewerWritesReviewerSigns |
      | M2M    | Rimozione - multipli a singolo        | ReviewerWritesReviewerSigns | ASSIGNED      | 1,2             | 1              | ReviewerWritesReviewerSigns |
      | M2M    | Sostituzione completa                | ReviewerWritesReviewerSigns | ASSIGNED      | 1               | 2              | ReviewerWritesReviewerSigns |
      | M2M    | Cambio contemporaneo reviewer e mode | ReviewerWritesReviewerSigns | ASSIGNED      | 1               | 2              | AdminWritesReviewerSigns    |

  # FIXME temporaneo, rimuovere
  Scenario Outline: [ASSIGN_MORE_REVIEWERS_01] Test nuovo step per ogni stato
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "PA2" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "PA2" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un "admin" di "PA2"
    And si ha una compilazione dell'analisi del rischio in modalità <mode> in stato "<stato>"
    # TODO
    Examples:
      | mode                         | stato     |
      | AdminWritesReviewerSigns     | DRAFT     |
      | ReviewerWritesReviewerSigns  | ASSIGNED  |
      | AdminWritesReviewerSigns     | SUBMITTED |
      | AdminWritesReviewerSigns     | SIGNED    |
      | ReviewerWritesReviewerSigns  | SIGNED    |
      | AdminWritesReviewerSigns     | REJECTED  |
