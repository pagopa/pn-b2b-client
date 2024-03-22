Feature: Radd fsu

  @radd @testSuiteDiProvaTemporanea
  Scenario: [B2B_RADD_DOC-UP-1] verifica document upload senza aver passato bundleId
    Given vengono caricati i documento di identità del cittadino senza "bundleId"
    Then il caricamente ha prodotto une errore http 400

  @radd
  Scenario: [B2B_RADD_DOC-UP-2] verifica document upload senza aver passato contentType
    Given vengono caricati i documento di identità del cittadino senza "contentType"
    Then il caricamente ha prodotto une errore http 500

  @radd
  Scenario: [B2B_RADD_DOC-UP-3] verifica document upload senza aver passato checksum
    Given vengono caricati i documento di identità del cittadino senza "checksum"
    Then il caricamente ha prodotto une errore http 400

  @radd
  Scenario: [B2B_RADD_ACT-1] verifica errore inquiry qrCode malformato
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "malformato"
    When L'operatore scansione il qrCode per recuperare gli atti
    Then Viene restituito un messaggio di errore "QRcode non valido" con codice di errore 1

  @radd
  Scenario: [B2B_RADD_ACT-2] verifica errore inquiry qrCode inesistente
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "inesistente"
    When L'operatore scansione il qrCode per recuperare gli atti
    Then Viene restituito un messaggio di errore "QRcode non valido" con codice di errore 1

  @radd
  Scenario: [B2B_RADD_ACT-3] inquiry su QRcode esistente ma associato al CF sbagliato
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Gherkin" mostra il QRCode "appartenente a terzo"
    When L'operatore scansione il qrCode per recuperare gli atti
    Then Viene restituito un messaggio di errore "CF non valido" con codice di errore 1

  @radd
  Scenario: [B2B_RADD_ACT-5] inquiry su QRcode esistente associato al corretto codice fiscale
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    And l'operazione di download degli atti si conclude correttamente
    And viene conclusa la visualizzati di atti ed attestazioni della notifica

  @radd
  Scenario: [B2B_RADD_ACT-6] start transaction act senza effettuare upload documenti
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And si inizia il processo di caricamento dei documento di identità del cittadino ma non si porta a conclusione
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    And l'operazione di download degli atti genera un errore "documenti non disponibili" con codice 99

  @radd
  Scenario: [B2B_RADD_ACT-7] start transaction utilizzando l'operation id di una transazione precedente con iun diverso
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    And l'operazione di download degli atti si conclude correttamente
    And viene conclusa la visualizzati di atti ed attestazioni della notifica
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    When Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR utilizzando il precedente operationId
    Then l'operazione di download degli atti genera un errore "transazione già esistente o con stato completed o aborted" con codice 99

  @radd #TODO: al momento viene accettato il comportamento
  Scenario: [B2B_RADD_ACT-8] start transaction utilizzando i documenti associati alla transazione di altro cittadino
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Gherkin" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    And l'operazione di download degli atti si conclude correttamente
    And viene conclusa la visualizzati di atti ed attestazioni della notifica
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    When Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    Then l'operazione di download degli atti si conclude correttamente

  @radd
  Scenario: [B2B_RADD_ACT-9] inquiry con lo stesso IUN della transazioni precendete
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    And l'operazione di download degli atti si conclude correttamente
    And viene conclusa la visualizzati di atti ed attestazioni della notifica
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti
    And Viene restituito un messaggio di errore "Stampa già eseguita" con codice di errore 3

  @radd
  Scenario: [B2B_RADD_ACT-10] start transaction utilizzando l'operation id di una transazione precedente con lo stesso IUN
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    And l'operazione di download degli atti si conclude correttamente
    And viene conclusa la visualizzati di atti ed attestazioni della notifica
    And vengono caricati i documento di identità del cittadino
    When Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR utilizzando il precedente operationId
    Then l'operazione di download degli atti genera un errore "stampa già eseguita" con codice 99

  @radd
  Scenario: [B2B_RADD_ACT-11] start transaction utilizzando il qr code di una transazione precedente con lo stesso IUN
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    And l'operazione di download degli atti si conclude correttamente
    And viene conclusa la visualizzati di atti ed attestazioni della notifica
    And vengono caricati i documento di identità del cittadino
    When Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    Then l'operazione di download degli atti genera un errore "Stampa già eseguita" con codice 99

  @radd
  Scenario: [B2B_RADD_ACT-12] Abort su transaction già abortita
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    Then la transazione viene abortita
    And la transazione viene abortita
    And l'operazione di abort genera un errore "La transazione risulta annullata" con codice 99

  @radd
  Scenario: [B2B_RADD_ACT-13] Abort su transaction già abortita
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    Then la transazione viene abortita
    And viene conclusa la visualizzati di atti ed attestazioni della notifica
    And la chiusura delle transazione per il recupero degli aar ha generato l'errore "La transazione risulta annullata" con statusCode 2

  @radd
  Scenario: [B2B_RADD_ACT-14] Abort su transaction già abortita
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti
    And la scansione si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR
    And viene conclusa la visualizzati di atti ed attestazioni della notifica
    And la transazione viene abortita
    And l'operazione di abort genera un errore "La transazione risulta già completa" con codice 2


  @radd
  Scenario: [B2B_RADD_AOR-1] inquiry per cittadino con nessuna notifica in stato irreperibile
    Given Il cittadino "signor generato" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99

  @radd @bugNoto
  Scenario: [B2B_RADD_AOR-2] inquiry per cittadino con molte notifiche in stato irreperibile
    Given Il cittadino "DVNLRD52D15M059P" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente


  @radd
  Scenario: [B2B_RADD_AOR-3] inquiry per cittadino con notifiche in stato irreperibile
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Signor casuale e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente

  @radd
  Scenario: [B2B_RADD_AOR-4] recupero atti per cittadino con notifiche in stato irreperibile
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Signor casuale e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile
    And il recupero degli aar in stato irreperibile si conclude correttamente
    And viene chiusa la transazione per il recupero degli aar
    And la chiusura delle transazione per il recupero degli aar non genera errori

  @radd
  Scenario: [B2B_RADD_AOR-5] start transaction aor senza effettuare upload documenti
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Signor casuale e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente
    And si inizia il processo di caricamento dei documento di identità del cittadino ma non si porta a conclusione
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile
    And il recupero degli aar genera un errore "Documenti non disponibili" con codice 99

  @radd
  Scenario: [B2B_RADD_AOR-6] aor per cittadino con 49 notifiche in stato irreperibile
    Given vengono inviate 49 notifiche per l'utente Signor casuale con il "Comune_Multi" e si aspetta fino allo stato COMPLETELY_UNREACHABLE
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente
    And vengono caricati i documento di identità del cittadino
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile
    And il recupero degli aar in stato irreperibile si conclude correttamente e vengono restituiti 49 aar
    And viene chiusa la transazione per il recupero degli aar
    And la chiusura delle transazione per il recupero degli aar non genera errori

  @radd
  Scenario: [B2B_RADD_AOR-7] inquiry per cittadino con nessuna notifica in stato irreperibile e notifica consegnata
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Signor casuale
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99


