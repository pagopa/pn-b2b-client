Feature: Radd Alternative


  @raddAlt
  Scenario: [RADD-ALT_ACT-1] PF - Scansione QR code esistente associato al CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto" su radd alternative
    When L'operatore scansione il qrCode per recuperare gli atti della "PF"
    And la scansione si conclude correttamente su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_ACT-2] PF - Scansione QR code esistente associato al CF corretto, ma relativo a una notifica con perfezionamento > 120 giorni
    When Il cittadino "Mario Cucumber" mostra il QRCode "dopo 120gg"
    Then L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And Viene restituito un messaggio di errore "QRcode non valido" con codice di errore 1 su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_ACT-3] PF - Scansione QR code inesistente
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "inesistente" su radd alternative
    When L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    Then Viene restituito un messaggio di errore "QRcode non valido" con codice di errore 1 su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_ACT-4] PF - Scansione QR code esistente associato al CF sbagliato
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "appartenente a terzo" su radd alternative
    When L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    Then Viene restituito un messaggio di errore "CF non valido" con codice di errore 1 su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_ACT-5] PF - Scansione documenti e creazione file zip
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED

  @raddAlt
  Scenario: [RADD-ALT_ACT-6] PF - Stampa documenti disponibili associati a QR code esistente con CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When Il cittadino "Mario Cucumber" mostra il QRCode "corretto" su radd alternative
    Then L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR su radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_ACT-7] PF - Consegna documenti al cittadino successivi alla stampa
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    Then Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR su radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-8] PF - Visualizzazione in timeline nuovo evento di avvenuta consegna documenti tramite RADD (NOTIFICATION_RADD_RETRIEVED)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And la scansione si conclude correttamente su radd alternative
    When lato destinatario la notifica può essere correttamente recuperata da "Mario Cucumber" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
    Then lato desinatario "Mario Cucumber" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista

  @raddAlt
  Scenario: [RADD-ALT_ACT-9] PF - Restituzione errore - Documenti già stampati
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When Il cittadino "Mario Cucumber" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR su radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And Viene restituito un messaggio di errore "Stampa già eseguita" con codice di errore 3

  Scenario: [RADD-ALT_ACT-10] PF - Notifica annullata - Restituzione errore al tentativo di recupero documenti di notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION" e successivamente annullata
    When Il cittadino "Mario Cucumber" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And Viene restituito un messaggio di errore "notifica annullata" con codice di errore 80 su radd alternative


  Scenario: [RADD-ALT_ACT-11] PF - Restituzione errore - Documento non stampabile tra quelli disponibili nella lista dei documenti associati a QR code esistente con CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And la scansione si conclude correttamente su radd alternative
    And si inizia il processo di caricamento dei documento di identità del cittadino ma non si porta a conclusione su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR su radd alternative
    And l'operazione di download degli atti genera un errore "documenti non disponibili" con codice 99

  @raddAlt
  Scenario: [RADD-ALT_ACT-12] PG - Scansione QR code esistente associato al CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "CucumberSpa" mostra il QRCode "corretto" su radd alternative
    When L'operatore scansione il qrCode per recuperare gli atti della "PG"
    And la scansione si conclude correttamente su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-13] PG - Scansione QR code esistente associato al CF corretto, ma relativo a una notifica con perfezionamento > 120 giorni.
    When Il cittadino "CucumberSpa" mostra il QRCode "dopo 120gg"
    Then L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    And Viene restituito un messaggio di errore "QRcode non valido" con codice di errore 1 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-14] PG - Scansione QR code inesistente
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "CucumberSpa" mostra il QRCode "inesistente" su radd alternative
    When L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    Then Viene restituito un messaggio di errore "QRcode non valido" con codice di errore 1 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-15] PG - Scansione QR code esistente associato al CF sbagliato
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "CucumberSpa" mostra il QRCode "appartenente a terzo" su radd alternative
    When L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    Then Viene restituito un messaggio di errore "CF non valido" con codice di errore 1 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-16] PG - Scansione documenti e creazione file zip
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED

  @raddAlt
  Scenario: [RADD-ALT_ACT-17] PG - Stampa documenti disponibili associati a QR code esistente con CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When Il cittadino "CucumberSpa" mostra il QRCode "corretto" su radd alternative
    Then L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR su radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-18] PG - Consegna documenti al cittadino successivi alla stampa
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    Then Il cittadino "CucumberSpa" mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR su radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-19] PG - Visualizzazione in timeline nuovo evento di avvenuta consegna documenti tramite RADD (NOTIFICATION_RADD_RETRIEVED)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "CucumberSpa" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    And la scansione si conclude correttamente su radd alternative
    When lato destinatario la notifica può essere correttamente recuperata da "CucumberSpa" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
    Then lato desinatario "CucumberSpa" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista


  @raddAlt
  Scenario: [RADD-ALT_ACT-20] PG - Restituzione errore - Documenti già stampati
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "CucumberSpa" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR su radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And Il cittadino "CucumberSpa" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    And Viene restituito un messaggio di errore "Stampa già eseguita" con codice di errore 3 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-21] PG - Notifica annullata - Restituzione errore al tentativo di recupero documenti di notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLATION_REQUEST"
    And Il cittadino "CucumberSpa" mostra il QRCode "corretto" su radd alternative
    When L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    Then Viene restituito un messaggio di errore "notifica annullata" con codice di errore 80 su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_ACT-22] PG - Restituzione errore - Documento non stampabile tra quelli disponibili nella lista dei documenti associati a QR code esistente con CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "CucumberSpa" mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And la scansione si conclude correttamente su radd alternative
    And si inizia il processo di caricamento dei documento di identità del cittadino ma non si porta a conclusione su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR su radd alternative
    And l'operazione di download degli atti genera un errore "documenti non disponibili" con codice 99

  @raddAlt
  Scenario: [RADD-ALT_AOR-23] PF - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Signor casuale e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche su radd alternative
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_AOR-24] PF - Visualizzazione link AAR disponibili associati a notifica esistente in stato irreperibile con CF corretto
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Signor casuale e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche su radd alternative
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della "PF" su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_AOR-25] PF - Stampa documenti disponibili associati a notifica esistente con CF corretto, mai visualizzata
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Signor casuale e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche su radd alternative
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della "PF" su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_AOR-26] PF - Consegna documenti al cittadino successivi alla stampa
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Signor casuale e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche su radd alternative
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della "PF" su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative


  #Da capire se fattibile siccome l'aor restituisce solo notifiche irreperibili e act li serve perforza un QRcode
  @raddAlt
  Scenario: [RADD-ALT_AOR-27] PF - Restituzione errore - nessuna Notifica disponibile associata al CF
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche su radd alternative
    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99


  @raddAlt
  Scenario: [RADD-ALT_AOR-28] PF - Restituzione errore - nessuna Notifica disponibile in stato Irreperibile associata al CF corretto
    When la "PF" "Signor Generato" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99 su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_AOR-29] PF - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, ma inibizione stampa documenti associati alla notifica
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Signor casuale e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When Il cittadino Signor casuale chiede di verificare la presenza di notifiche su radd alternative
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della "PF" su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    And la transazione viene abortita per gli "aor"
    And l'operazione di abort genera un errore "La transazione risulta già completa" con codice 2


  @raddAlt
  Scenario: [RADD-ALT_AOR-30] PG - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la "PG" "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_AOR-31] PG - Visualizzazione link AAR disponibili associati a notifica esistente in stato irreperibile con CF corretto
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la "PG" "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della "PF" su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_AOR-32] PG - Stampa documenti disponibili associati a notifica esistente con CF corretto, mai visualizzata
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la "PG" "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della "PF" su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_AOR-33] PG - Consegna documenti al cittadino successivi alla stampa
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la "PG" "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della "PF" su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative


  @raddAlt
  Scenario: [RADD-ALT_AOR-34] PG - Restituzione errore - nessuna Notifica disponibile associata al CF corretto
    Given la "PG" "Signor Generato SRL" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99


  @raddAlt
  Scenario: [RADD-ALT_AOR-35] PG - Restituzione errore - nessuna Notifica disponibile in stato Irreperibile associata al CF corretto
    Given la "PG" "Signor Generato SRL" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99

  @raddAlt
  Scenario: [RADD-ALT_AOR-36] PG - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, ma inibizione stampa documenti associati alla notifica
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR|
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When la "PG" "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della "PF" su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    And la transazione viene abortita per gli "aor"
    And l'operazione di abort genera un errore "La transazione risulta già completa" con codice 2

  @raddAlt
  Scenario: [RADD-ALT_ACT-37] PA - Visualizzazione in timeline nuovo evento di avvenuta consegna documenti tramite RADD (NOTIFICATION_RADD_RETRIEVED)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Gherkin" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    When viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"

  @raddAlt
  Scenario: [RADD-ALT_ACT-38] PA - Verifica notifiche visualizzate tramite RADD: stato Avvenuto Accesso non presente in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Gherkin" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    When viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" non esista

  @raddAlt
  Scenario: [RADD-ALT_ACT-39] PF - Verifica notifiche visualizzate tramite RADD: stato Avvenuto Accesso non presente in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Gherkin" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    When viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    Then lato destinatario la notifica può essere correttamente recuperata da "Mario Gherkin" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
    And lato desinatario "Mario Gherkin" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista

  @raddAlt
  Scenario: [RADD-ALT_ACT-40] PG - Verifica notifiche visualizzate tramite RADD: stato Avvenuto Accesso non presente in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "CucumberSpa" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PG"
    When And
    Then lato destinatario la notifica può essere correttamente recuperata da "Mario Gherkin" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
    And lato desinatario "CucumberSpa" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista


 # @raddAlt @ignore MANUALE
 # Scenario: [RADD-ALT_ACT-41] PF/PG - Check conformità AAR




   #Bozza...
  @raddAlt
  Scenario Outline: [RADD-ALT_ACT-42] Scansione QR code o IUN e verifica auditlog AUD_RADD_ACTINQUIRY
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And Il cittadino "Mario Cucumber" mostra il QRCode "corretto" su radd alternative
    And L'operatore scansione il qrCode per recuperare gli atti della "PF"
    When la scansione si conclude correttamente su radd alternative
    Then viene verificato che esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log           |
      | AUD_RADD_ACTINQUIRY |

  @raddAlt
  Scenario Outline: [RADD-ALT_AOR-43] Ricerca Notifiche collegate al CF fornito dal cittadino e verifica auditlog AUD_RADD_AORINQUIRY
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When la "PF" "Mario Cucumber" chiede di verificare la presenza di notifiche
    Then viene verificato che esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log           |
      | AUD_RADD_AORINQUIRY |

  @raddAlt
  Scenario Outline: [RADD-ALT_ACT-44] Disponibilità dei documenti (Allegati Notifica, Allegati di pagamento, AOT) collegati alla notifica ricercata e verifica auditlog AUD_RADD_ACTTRAN
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When Il cittadino "Mario Cucumber" mostra il QRCode "corretto" su radd alternative
    Then L'operatore scansione il qrCode per recuperare gli atti su radd alternative per il recipientType "PF"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR su radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    Then viene verificato che esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log        |
      | AUD_RADD_ACTTRAN |

  @raddAlt
  Scenario Outline: [RADD-ALT_AOR-45] Disponibilità degli AAR collegati al CF fornito e verifica auditlog AUD_RADD_AORTRAN
    Then viene verificato che esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log        |
      | AUD_RADD_AORTRAN |

  @raddAlt
  Scenario Outline: [RADD-ALT_ACT-46] Esecuzione operazione di conferma del completamento di recupero degli atti e verifica auditlog AUD_NT_RADD_OPEN
    Then viene verificato che esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log        |
      | AUD_NT_RADD_OPEN |