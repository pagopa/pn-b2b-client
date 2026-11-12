Feature: Non regressione della ricezione delle adesioni degli enti da SelfCare

  # Regole di business
  # - Gli enti aderiscono a SEND tramite SelfCare, che comunica le adesioni a SEND.
  # - SEND deve restare collegato a SelfCare e ricevere le adesioni senza errori.
  # - Tra un rilascio e l'altro SEND riprende la ricezione dal punto in cui era arrivato: non rielabora le adesioni
  #   già ricevute e non le divide con altri ambienti collegati allo stesso SelfCare.
  #
  # Perimetro: SEND non può inviare adesioni di prova a SelfCare, quindi gli scenari verificano il collegamento e la
  # sua continuità tra i rilasci, ma non l'elaborazione di una nuova adesione.
  # TODO: il team di sviluppo estenderà il mock per pubblicare adesioni di prova; con il mock disponibile vanno
  # aggiunti gli scenari sull'elaborazione di una nuova adesione.

  @onboardingSelfCare @work-item:QA-18904
  Scenario: [ONBOARDING_NR_01_1] SEND riceve le adesioni degli enti da SelfCare senza errori
    Then SEND è collegato al canale con cui SelfCare comunica le adesioni degli enti
    And la ricezione delle adesioni non ha registrato errori dall'ultimo collegamento

  @onboardingSelfCare @work-item:QA-18904
  Scenario: [ONBOARDING_NR_01_2] SEND riprende la ricezione delle adesioni dal punto in cui era arrivato
    Then la ricezione delle adesioni usa la stessa sottoscrizione dei rilasci precedenti
