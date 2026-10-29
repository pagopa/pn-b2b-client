@purpose @nuovi-operatori @evol-nuovi-operatori @ra-lock-approve
Feature: Rifiuto della firma dell'analisi del rischio per ruoli non autorizzati
  Verifica che solo i reviewer possano firmare l'analisi, rifiutando gli altri ruoli e preservando lo stato.

  # CDT 3.4: entrambe le modalità con reviewer, RA compilata e pronta per la firma.
  # SUBMITTED si applica ad AWRS; RWRS usa ASSIGNED. Versione valida acquisita nel Given
  # dall'admin proprietario per isolare l'autorizzazione dalla validazione del lock.
  # Duplicato mantenuto: RWRS/admin si sovrappone a RWRS_CONVALIDA_14_1_NON_VALUTATORE
  # in nuovi-operatori-rwrs.feature; valutarne successivamente la rimozione.
  @sad-path
  Scenario Outline: [RA_SIGN_UNAUTHORIZED_ROLE_01] Firma negata al ruolo <ruolo> in <mode>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And si ha una compilazione dell'analisi del rischio in modalità <mode> in stato "<stato>"
    And l'utente acquisisce la finalità e la versione dei metadati per la convalida
    When l'utente ha ruolo "<ruolo>" nel profilo API BFF dell'ente "GSP"
    And l'utente convalida l'analisi del rischio della finalità usando la versione dei metadati precedentemente acquisita
    Then si ottiene status code 403
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And la firma rifiutata lascia invariati la finalità e la versione dei metadati acquisite
    And lo stato della compilazione dell'analisi del rischio è "<stato>"

    Examples: AWRS pronta per la firma
      | mode                     | stato     | ruolo        |
      | AdminWritesReviewerSigns | SUBMITTED | support      |
      | AdminWritesReviewerSigns | SUBMITTED | admin        |
      | AdminWritesReviewerSigns | SUBMITTED | api          |
      | AdminWritesReviewerSigns | SUBMITTED | security     |
      | AdminWritesReviewerSigns | SUBMITTED | api,security |
      | AdminWritesReviewerSigns | SUBMITTED | viewer       |

    Examples: RWRS compilata e pronta per la firma
      | mode                        | stato    | ruolo        |
      | ReviewerWritesReviewerSigns | ASSIGNED | support      |
      | ReviewerWritesReviewerSigns | ASSIGNED | admin        |
      | ReviewerWritesReviewerSigns | ASSIGNED | api          |
      | ReviewerWritesReviewerSigns | ASSIGNED | security     |
      | ReviewerWritesReviewerSigns | ASSIGNED | api,security |
      | ReviewerWritesReviewerSigns | ASSIGNED | viewer       |