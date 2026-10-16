@purpose @nuovi-operatori
Feature: Cambio modalità di revisione e gestione dei dati dell'analisi del rischio

  # CDT 1.2 e 1.3: cambio modalità e conservazione/reset dati tramite BFF.
  # CDT 7.1: tutte le sei direzioni tramite M2M, con gli stati iniziali applicabili.
  # Popup di conferma e verifiche frontend fuori perimetro.
  # Reviewer 1 mantenuto tra mode 2 e 3; nessun reviewer in mode 1.
  # Decisione di copertura: il PST elenca le transizioni 2 -> 1 e 2 -> 3,
  # non un CDT distinto per ogni stato. Le espandiamo deliberatamente su DRAFT,
  # SUBMITTED e REJECTED (2 direzioni x 3 stati = 6 Examples per canale BFF/M2M)
  # per verificare il comportamento in tutti gli stati iniziali applicabili.
  # SIGNED escluso dalla precondizione non firmata; ASSIGNED appartiene a mode 3.
  Scenario Outline: [RA_REVIEW_MODE_CHANGE_01] <apiSet> - <modeIniziale> (<statoIniziale>) verso <modeFinale>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente è un amministratore del profilo API BFF dell'ente "GSP"
    And si ha una compilazione dell'analisi del rischio in modalità <modeIniziale> in stato "<statoIniziale>"
    When l'utente è un amministratore del profilo API <apiSet> dell'ente "GSP"
    And l'utente tenta di assegnare alla finalità i seguenti valutatori: <reviewerFinali>, in modalità <modeFinale>
    Then si ottiene status code 204
    And l'utente è un "admin" di "GSP"
    And alla finalità risultano associati esclusivamente i seguenti valutatori: <reviewerFinali>, in modalità <modeFinale>
    And ai valutatori assegnati risulta correttamente assegnata la finalità
    And i dati RA dopo il cambio modalità risultano <effettoRA>
    And i valutatori rimossi rispetto all'assegnazione iniziale non vedono più la finalità nelle assegnazioni

    Examples: BFF - CDT 1.2 e 1.3
      | apiSet | modeIniziale                | statoIniziale | modeFinale                  | effettoRA  | reviewerFinali |
      | BFF    | AdminWritesAdminSigns       | DRAFT         | AdminWritesReviewerSigns    | conservati | 1              |
      | BFF    | AdminWritesAdminSigns       | DRAFT         | ReviewerWritesReviewerSigns | azzerati   | 1              |
      | BFF    | AdminWritesReviewerSigns    | DRAFT         | AdminWritesAdminSigns       | conservati | nessuno        |
      | BFF    | AdminWritesReviewerSigns    | SUBMITTED     | AdminWritesAdminSigns       | conservati | nessuno        |
      | BFF    | AdminWritesReviewerSigns    | REJECTED      | AdminWritesAdminSigns       | conservati | nessuno        |
      | BFF    | AdminWritesReviewerSigns    | DRAFT         | ReviewerWritesReviewerSigns | azzerati   | 1              |
      | BFF    | AdminWritesReviewerSigns    | SUBMITTED     | ReviewerWritesReviewerSigns | azzerati   | 1              |
      | BFF    | AdminWritesReviewerSigns    | REJECTED      | ReviewerWritesReviewerSigns | azzerati   | 1              |
      | BFF    | ReviewerWritesReviewerSigns | ASSIGNED      | AdminWritesAdminSigns       | azzerati   | nessuno        |
      | BFF    | ReviewerWritesReviewerSigns | ASSIGNED      | AdminWritesReviewerSigns    | azzerati   | 1              |

    @m2m-ra-review
    Examples: M2M - CDT 7.1 cambi modalità
      | apiSet | modeIniziale                | statoIniziale | modeFinale                  | effettoRA  | reviewerFinali |
      | M2M    | AdminWritesAdminSigns       | DRAFT         | AdminWritesReviewerSigns    | conservati | 1              |
      | M2M    | AdminWritesAdminSigns       | DRAFT         | ReviewerWritesReviewerSigns | azzerati   | 1              |
      | M2M    | AdminWritesReviewerSigns    | DRAFT         | AdminWritesAdminSigns       | conservati | nessuno        |
      | M2M    | AdminWritesReviewerSigns    | SUBMITTED     | AdminWritesAdminSigns       | conservati | nessuno        |
      | M2M    | AdminWritesReviewerSigns    | REJECTED      | AdminWritesAdminSigns       | conservati | nessuno        |
      | M2M    | AdminWritesReviewerSigns    | DRAFT         | ReviewerWritesReviewerSigns | azzerati   | 1              |
      | M2M    | AdminWritesReviewerSigns    | SUBMITTED     | ReviewerWritesReviewerSigns | azzerati   | 1              |
      | M2M    | AdminWritesReviewerSigns    | REJECTED      | ReviewerWritesReviewerSigns | azzerati   | 1              |
      | M2M    | ReviewerWritesReviewerSigns | ASSIGNED      | AdminWritesAdminSigns       | azzerati   | nessuno        |
      | M2M    | ReviewerWritesReviewerSigns | ASSIGNED      | AdminWritesReviewerSigns    | azzerati   | 1              |

  # CDT 7.1: vere prime assegnazioni esplicite M2M, senza assign nel Given.
  @m2m-ra-review
  Scenario Outline: [M2M_RA_FIRST_MODE_01] Prima assegnazione in modalità <mode>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    When l'utente è un amministratore del profilo API M2M dell'ente "GSP"
    And l'utente tenta di assegnare alla finalità i seguenti valutatori: <reviewerFinali>, in modalità <mode>
    Then si ottiene status code 204
    And l'utente è un "admin" di "GSP"
    And alla finalità risultano associati esclusivamente i seguenti valutatori: <reviewerFinali>, in modalità <mode>
    And ai valutatori assegnati risulta correttamente assegnata la finalità

    Examples:
      | mode                        | reviewerFinali |
      | AdminWritesAdminSigns       | nessuno        |
      | AdminWritesReviewerSigns    | 1              |
      | ReviewerWritesReviewerSigns | 1              |



