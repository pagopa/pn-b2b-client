Feature: Controllo percorso notifiche mediante verifica del contenuto del legalFact

  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_1] Data una notifica digitale, si verifica l'esistenza del legalFact generato in seguito ad accettazione se sia di tipo NOTIFICA PRESA IN CARICO
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "SENDER_ACK"
#    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_PRESA_IN_CARICO" e contiene il campo "TITLE" con value "Attestazione opponibile a terzi: notifica presa in carico"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_PRESA_IN_CARICO"
    Then si verifica se il legalFact contiene il campo "TITLE" con value "Attestazione opponibile a terzi: notifica presa in carico"
    Then si verifica se il legalFact contiene il campo "DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Mario Gherkin"

  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_2] Data una notifica digitale, in seguito al completamento del relativo workflow si verifica l'esistenza del legalFact generato se sia di tipo NOTIFICA DIGITALE
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And ricerca ed effettua download del legalFact con la categoria "DIGITAL_DELIVERY"
#    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_DIGITALE" e contiene il campo "TITLE" con value "Attestazione opponibile a terzi: notifica digitale"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_DIGITALE"
    Then si verifica se il legalFact contiene il campo "TITLE" con value "Attestazione opponibile a terzi: notifica digitale"
    Then si verifica se il legalFact contiene il campo "DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Mario Gherkin"

  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_3] Data una notifica digitale, in seguito al completamento del relativo workflow ed a presa visione da parte del destinatario, si verifica l'esistenza del legalFact generato se sia di tipo AVVENUTO ACCESSO DESTINATARIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Gherkin" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And ricerca ed effettua download del legalFact con la categoria "RECIPIENT_ACCESS"
#    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO" e contiene il campo "DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Mario Gherkin"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO"
    Then si verifica se il legalFact contiene il campo "TITLE" con value "Attestazione opponibile a terzi: avvenuto accesso"
    Then si verifica se il legalFact contiene il campo "DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Mario Gherkin"

  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_4] Data una notifica digitale, in seguito al completamento del relativo workflow ed a presa visione da parte del delegato, si verifica l'esistenza del legalFact generato se sia di tipo AVVENUTO ACCESSO DELEGATO
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And "Mario Cucumber" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"
#    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO" e contiene il campo "DELEGATO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Cristoforo Colombo"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO"
    Then si verifica se il legalFact contiene il campo "TITLE" con value "Attestazione opponibile a terzi: avvenuto accesso"
    Then si verifica se il legalFact contiene il campo "DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Mario Cucumber"
    Then si verifica se il legalFact contiene il campo "DELEGATO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Cristoforo Colombo"

  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_5] Data una notifica digitale, in seguito al completamento del relativo workflow ed a presa visione da parte del destinatario, si verifica l'esistenza del legalFact generato se sia di tipo AVVENUTO ACCESSO
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "Mario Cucumber" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And "Mario Cucumber" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"
#    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO" e contiene il campo "DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Mario Cucumber"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO"
    Then si verifica se il legalFact contiene il campo "TITLE" con value "Attestazione opponibile a terzi: avvenuto accesso"
    Then si verifica se il legalFact contiene il campo "DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Mario Cucumber"

  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_6] Data una notifica digitale, in seguito al completamento del relativo workflow ed a presa visione da parte del destinatario, si verifica l'esistenza del legalFact generato se sia di tipo MANCATO RECAPITO
    Given viene generata una nuova notifica
      | subject | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And ricerca ed effettua download del legalFact con la categoria "DIGITAL_DELIVERY_FAILURE"
#    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_MANCATO_RECAPITO" e contiene il campo "DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Mario Gherkin"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_MANCATO_RECAPITO"
    Then si verifica se il legalFact contiene il campo "TITLE" con value "Attestazione opponibile a terzi: mancato recapito digitale"
    Then si verifica se il legalFact contiene il campo "DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE" con value "Mario Gherkin"

#  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_7] Data una notifica digitale, in seguito ad un disservizio verificatosi durante la creazione di una notifica, si verifica l'esistenza del legalFact generato se sia di tipo DOWNTIME


#  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_8] Data una notifica digitale, in seguito ad un disservizio verificatosi durante la visualizzazione di una notifica, si verifica l'esistenza del legalFact generato se sia di tipo DOWNTIME


#  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_9] Data una notifica digitale, in seguito ad un disservizio verificatosi durante il workflow di una notifica, si verifica l'esistenza del legalFact generato se sia di tipo DOWNTIME

