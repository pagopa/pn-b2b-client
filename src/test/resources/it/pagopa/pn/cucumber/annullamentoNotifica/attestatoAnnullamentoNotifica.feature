Feature: produzione del documento di annullamento notifica

  @attestatoAnnullamentoNotifica
  Scenario: [ATTESTATO_ANNULLAMENTO_1]
    Given viene generata una nuova notifica con la versione più recente
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V24"
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune di palermo"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST"
#    And è presente il documento che ne attesta l'annullamento

  @attestatoAnnullamentoNotifica
  Scenario: [ATTESTATO_ANNULLAMENTO_2]
    Given viene generata una nuova notifica con la versione più recente
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V24"
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune di palermo"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED_DOCUMENT_CREATION_REQUEST"


