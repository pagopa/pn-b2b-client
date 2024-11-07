Feature: Produzione del documento che attesta l'annullamento di una notifica

  @attestatoAnnullamentoNotifica
  Scenario: [ATTESTATO_ANNULLAMENTO_1] Verifica la presenza del nuovo elemento di timeline NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED e successivamente annullata
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST"

  @attestatoAnnullamentoNotifica
  Scenario: [ATTESTATO_ANNULLAMENTO_2]