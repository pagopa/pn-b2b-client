@purpose @nuovi-operatori @ra-lock-approve
Feature: Validazione della versione dei metadati durante la firma dell'analisi del rischio

  # CDT 3.1 e 3.6: stesso stato SUBMITTED e attore autorizzato, varia solo il payload di firma inviato tramite client generato.
  # I casi negativi distinguono il 400 (Bad Request da schema/validazione) dal 409 (Conflict su versione non allineata).
  Scenario Outline: [RA_SIGN_VERSION_VALIDATION_01] Firma con versione metadati <versione> (<esito>)
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And l'utente assegna con successo i valutatori 1,2 alla finalità in modalità AdminWritesReviewerSigns
    And compila l'analisi del rischio tramite endpoint generico con successo
    And l'utente invia il submit dell'analisi del rischio della finalità con successo
    When l'utente è un "reviewer" di "GSP"
    And l'utente convalida l'analisi del rischio con versione metadati <versione>
    Then si ottiene status code <statusCode>
    And lo stato della finalità e i dati dell'analisi del rischio risultano coerenti con la firma con esito <esito>
    And lo stato della compilazione dell'analisi del rischio è "<statoFinale>"

    @happy-path
    Examples: CDT 3.1 - approvazione con successo con versione aggiornata (204 No Content)
      | versione | statusCode | esito    | statoFinale |
      | corrente | 204        | positivo | SIGNED      |

    @sad-path
    Examples: CDT 3.6 - robustezza BE su metadataVersion assente o non valida
      # Campo obbligatorio omesso nel body -> 400 Bad Request
      | versione | statusCode | esito    | statoFinale |
      | assente  | 400        | negativo | SUBMITTED   |
      # Valore negativo viola il vincolo OpenAPI (minimum: 0) -> 400 Bad Request
      | negativa | 400        | negativo | SUBMITTED   |
      # Valore 0 precedente/non allineato rispetto alla versione corrente (che è > 0) -> 409 Conflict
      | zero     | 409        | negativo | SUBMITTED   |
