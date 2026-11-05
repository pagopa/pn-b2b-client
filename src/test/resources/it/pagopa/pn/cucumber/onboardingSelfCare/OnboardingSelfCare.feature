Feature: Non regressione della ricezione delle adesioni degli enti da SelfCare

  # Regole di business
  # - Gli enti aderiscono a SEND tramite SelfCare, che comunica le adesioni a SEND.
  # - SEND deve restare collegato a SelfCare e ricevere le adesioni senza errori.
  # - Tra un rilascio e l'altro SEND riprende la ricezione dal punto in cui era arrivato: non rielabora le adesioni
  #   già ricevute e non le divide con altri ambienti collegati allo stesso SelfCare.
  # - La ricezione dei pagamenti pagoPA (PN-20853) convive con quella delle adesioni: un problema sui pagamenti non
  #   deve interrompere le adesioni.
  # - Gli enti che hanno già aderito restano registrati e l'assistenza continua a trovarli.
  #
  # Perimetro: SEND non può inviare adesioni di prova a SelfCare, quindi gli scenari verificano il collegamento,
  # la sua continuità tra i rilasci, l'isolamento dai pagamenti e gli enti già aderenti, ma non l'elaborazione di
  # una nuova adesione.
  #
  # Gli scenari valgono prima e dopo i rilasci (PN-20853, PN-22132) e in qualunque ambiente.
  # Gli scenari non ancora eseguibili sono marcati @ignore e con i tag delle dipendenze mancanti:
  #   @attesaKafkaMock     mock del Nodo pagoPA di pn-external-channels non rilasciato (branch feature/kafka-tests)
  #   @attesaDlqPagamenti  DLQ dei pagamenti di pn-kafka-bridge non ancora rilasciata

  # =========================================================================================
  # SCENARIO 1 - Collegamento a SelfCare
  # =========================================================================================

  @onboardingSelfCare
  Scenario: [ONBOARDING_NR_01_1] SEND riceve le adesioni degli enti da SelfCare senza errori
    Then SEND è collegato al canale con cui SelfCare comunica le adesioni degli enti
    And la ricezione delle adesioni non ha registrato errori dall'ultimo collegamento

  @onboardingSelfCare
  Scenario: [ONBOARDING_NR_01_2] SEND riprende la ricezione delle adesioni dal punto in cui era arrivato
    Then la ricezione delle adesioni usa la stessa sottoscrizione dei rilasci precedenti

  # =========================================================================================
  # SCENARIO 2 - Convivenza con la ricezione dei pagamenti
  # =========================================================================================

  @onboardingSelfCare @ignore @attesaKafkaMock @attesaDlqPagamenti
  Scenario: [ONBOARDING_NR_02_1] Un evento di pagamento malformato non interrompe la ricezione delle adesioni
    Given SEND sta ricevendo le adesioni degli enti da SelfCare
    When pagoPA pubblica un evento di pagamento con "data di pagamento mancante"
    Then l'evento malformato viene scartato nella coda degli eventi di pagamento malformati
    And la ricezione delle adesioni prosegue senza interruzioni né errori

  # =========================================================================================
  # SCENARIO 3 - Enti già aderenti
  # =========================================================================================

  @onboardingSelfCare
  Scenario Outline: [ONBOARDING_NR_03_1] Un ente già aderente resta registrato e visibile all'assistenza: <ente>
    Then l'ente "<ente>" risulta aderente a SEND con il proprio codice fiscale
    And l'operatore dell'assistenza trova l'ente "<ente>" cercandolo per denominazione

    Examples:
      | ente         |
      | Comune_1     |
      | Comune_2     |
      | Comune_Multi |
