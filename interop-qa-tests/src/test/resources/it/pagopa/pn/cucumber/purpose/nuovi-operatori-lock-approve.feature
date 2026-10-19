@purpose @nuovi-operatori @ra-lock-approve
Feature: Validazione della versione dei metadati durante la firma dell'analisi del rischio

  # CDT 3.3: due reviewer distinti inviano la stessa V quasi simultaneamente.
  # Il vincitore non è predeterminato: esattamente un 204, un 409 e versione finale V+1.
  @ra-concurrent-sign
  Scenario: [RA_SIGN_CONCURRENT_01] Due approvazioni concorrenti sulla stessa versione
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And l'utente assegna con successo i valutatori 1,2 alla finalità in modalità AdminWritesReviewerSigns
    And compila l'analisi del rischio tramite endpoint generico con successo
    And l'utente invia il submit dell'analisi del rischio della finalità con successo
    When i due valutatori assegnati convalidano contemporaneamente l'analisi del rischio con la stessa versione acquisita
    Then una sola firma concorrente riesce e la versione dei metadati è incrementata di uno

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

  # CDT 3.2
  # AWRS SUBMITTED viene modificata da admin 2 (titolo);
  # RWRS ASSIGNED viene modificata da reviewer 2 (risposte RA). Firma sempre reviewer 1.
  # Le finestre UI del PST sono ricondotte alla stessa verifica API del lock:
  # V1 acquisita, mutazione valida -> V2, firma esplicita con V1 -> 409, nessuna mutazione ulteriore.
  @sad-path
  Scenario Outline: [RA_SIGN_STALE_VERSION_01] Firma con versione obsoleta dopo modifica in <mode>
    Given "PA1" ha già creato e pubblicato 1 e-service
    And "GSP" ha una richiesta di fruizione in stato "ACTIVE" per quell'e-service
    And "GSP" ha già creato 1 finalità in stato "DRAFT" per quell'eservice
    And l'utente ha ruolo "admin" nel profilo API BFF dell'ente "GSP"
    And l'utente assegna con successo i valutatori 1,2 alla finalità in modalità <mode>
    And <compilazione>
    And <invio>
    And l'utente è un "reviewer" di "GSP"
    And l'utente acquisisce la versione dei metadati della finalità da convalidare
    When l'utente è il numero 2 ad avere ruolo "<ruoloModifica>" di "GSP"
    And l'utente modifica <datoModificato> per rendere obsoleta la versione acquisita
    Then si ottiene status code 200
    And la modifica concorrente è persistita e ha incrementato la versione dei metadati
    And lo stato della compilazione dell'analisi del rischio è "<stato>"
    When l'utente è un "reviewer" di "GSP"
    And l'utente convalida l'analisi del rischio della finalità usando la versione dei metadati precedentemente acquisita
    Then si ottiene status code 409
    And il tentativo con versione obsoleta lascia invariati finalità e versione dei metadati
    And lo stato della compilazione dell'analisi del rischio è "<stato>"

    Examples:
      | mode                        | compilazione                                                                      | invio                                                                         | ruoloModifica | datoModificato | stato     |
      | AdminWritesReviewerSigns    | compila l'analisi del rischio tramite endpoint generico con successo              | l'utente invia il submit dell'analisi del rischio della finalità con successo | admin         | titolo         | SUBMITTED |
      | ReviewerWritesReviewerSigns | il valutatore assegnato compila l'analisi del rischio della finalità con successo | lo stato della compilazione dell'analisi del rischio è "ASSIGNED"             | reviewer      | risposte       | ASSIGNED  |

