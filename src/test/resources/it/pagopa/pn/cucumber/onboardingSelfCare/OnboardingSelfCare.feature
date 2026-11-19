Feature: Non regressione della ricezione delle adesioni degli enti da SelfCare

  # Regole di business
  # - Gli enti aderiscono a SEND tramite SelfCare, che comunica le adesioni a SEND.
  # - SEND deve restare collegato a SelfCare e ricevere le adesioni senza errori.
  # - Tra un rilascio e l'altro SEND riprende la ricezione dal punto in cui era arrivato: non rielabora le adesioni
  #   già ricevute e non le divide con altri ambienti collegati allo stesso SelfCare.
  #
  # Perimetro: NR_01 verifica il collegamento a SelfCare e la sua continuità tra i rilasci; NR_02 l'elaborazione di
  # un'adesione simulata. Ogni scenario NR_02 censisce un ente di test nuovo, che resta tra gli enti aderenti
  # dell'ambiente.
  #
  # Blocchi attuali (2026-11-12); gli scenari bloccati sono @ignore:
  # - in nessun ambiente SEND riceve le adesioni simulate, perché le legge solo dal canale reale di SelfCare: NR_02

  @onboardingSelfCare @work-item:QA-18904
  Scenario: [ONBOARDING_NR_01_1] SEND riceve le adesioni degli enti da SelfCare senza errori
    Then SEND è collegato al canale con cui SelfCare comunica le adesioni degli enti
    And la ricezione delle adesioni non ha registrato errori dall'ultimo collegamento

  @onboardingSelfCare @work-item:QA-18904
  Scenario: [ONBOARDING_NR_01_2] SEND riprende la ricezione delle adesioni dal punto in cui era arrivato
    Then la ricezione delle adesioni usa la stessa sottoscrizione dei rilasci precedenti

  @onboardingSelfCare @ignore @work-item:QA-18904
  Scenario: [ONBOARDING_NR_02_1] Un ente che aderisce a SEND tramite SelfCare viene censito tra gli enti aderenti
    Given SelfCare comunica l'adesione a SEND di un nuovo ente
    Then SEND censisce l'ente tra gli enti aderenti con i dati comunicati da SelfCare

  @onboardingSelfCare @ignore @work-item:QA-18904
  Scenario: [ONBOARDING_NR_02_2] La sospensione di un'adesione comunicata da SelfCare aggiorna l'ente aderente
    Given SelfCare comunica l'adesione a SEND di un nuovo ente
    And SEND censisce l'ente tra gli enti aderenti con i dati comunicati da SelfCare
    When SelfCare comunica la sospensione dell'adesione dell'ente
    Then l'adesione dell'ente risulta sospesa

  @onboardingSelfCare @ignore @work-item:QA-18904
  Scenario: [ONBOARDING_NR_02_3] L'adesione di un ente a un prodotto diverso da SEND viene ignorata
    Given SelfCare comunica l'adesione di un nuovo ente a un prodotto diverso da SEND
    Then SEND non censisce l'ente tra gli enti aderenti
