Feature: Radd Alternative

# FLUSSO ACT su PF con QrCode - successfully cases

  # [RADD-ALT_ACT-5], [RADD-ALT_ACT-6], [RADD-ALT_ACT-7], [RADD-ALT_ACT-8],
  # [RADD-ALT_ACT-37], [RADD-ALT_ACT-38], [RADD-ALT_ACT-39]
  # Assert:
  # stato Avvenuto Accesso non presente in timeline sia lato destinatario che lato PA
  @raddAlt @authFleet @zip
  Scenario Outline: [RADD-ALT_ACT-1] PF/PA - stampa e consegna documenti disponibili associati - visualizzazione in timeline avvenuta consegna tramite RADD (NOTIFICATION_RADD_RETRIEVED)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo |
    And destinatario
      | denomination           | <CITIZEN>  |
      | taxId                  | <CF>       |
      | digitalDomicile_address| testpagopa3@pec.pagopa.it |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "<CITIZEN>" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti di "<CITIZEN>"
    Then la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And lato destinatario la notifica può essere correttamente recuperata da "<CITIZEN>" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
    And lato desinatario "<CITIZEN>" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista
    Examples:
      | CITIZEN       | CF               |
      | Mario Cucumber | FRMTTR76M06B715E |
      | Mario Gherkin  | CLMCST42R12D969Z |

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-55] PF - Scansione QR code esistente associato al CF corretto, per una notifica con allegati di pagamento (Avviso PagoPA e F24)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via @ok_890                   |
      | payment_pagoPaForm      | SI                            |
      | payment_f24             | PAYMENT_F24_STANDARD          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | SI                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "Mario Gherkin" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti di "Mario Gherkin"
    Then la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 6 documenti

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-56] PF - Scansione QR code esistente associato al CF corretto, per una notifica con allegato di pagamento (solo F24)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via @ok_890                   |
      | payment_pagoPaForm      | NULL                          |
      | payment_f24             | PAYMENT_F24_STANDARD          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | NO                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti di "Mario Cucumber"
    Then la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 5 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  #[RADD-ALT_ACT-81]
  @raddAlt
  Scenario: [RADD-ALT_ACT-80] PF - Stampa documenti disponibili associati a QR code esistente conCF corretto su notifica analogica 890: verifica restituzione link e elemento di timeline contenente la ricevuta di postalizzazione in formato zip
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL           |
      | physicalAddress_address | Via@OK_890_ZIP |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B"
    And viene effettuato un controllo sul type zip attachment di "ATTACHMENTS" per l'elemento di timeline "SEND_ANALOG_PROGRESS" con DOC "23L"
      | loadTimeline               | true      |
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG001B |
    When Il cittadino "Mario Gherkin" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative

  #[RADD-ALT_ACT-84]
  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-83] PF - Stampa documenti disponibili associati a QR code esistente con CF corretto su notifica analogica AR: verifica restituzione link alla ricevuta di postalizzazione (in formato pdf)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL          |
      | physicalAddress_address | Via@OK_AR_ZIP |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001B"
    And viene effettuato un controllo sul type zip attachment di "ATTACHMENTS" per l'elemento di timeline "SEND_ANALOG_PROGRESS" con DOC "AR"
      | loadTimeline               | true      |
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECRN001B |
    And Il cittadino "Mario Gherkin" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    Then la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative

  @raddAlt @zip @uatEnvCondition
  Scenario: [RADD-ALT_ACT-85] Inserimento notifica indirizzata a PG con sequence OK_AR_ZIP  - verifica presenza elemento di timeline contenente la ricevuta di postalizzazione in formato zip
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL          |
      | physicalAddress_address | Via@OK_AR_ZIP |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN001B"
    And viene effettuato un controllo sul type zip attachment di "ATTACHMENTS" per l'elemento di timeline "SEND_ANALOG_PROGRESS" con DOC "AR"
      | loadTimeline               | true      |
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECRN001B |

  #Notifica gia letta
  #[RADD-ALT_ACT-86]
  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-90] PF - Recupero documenti allegati alla notifica: verifica presenza attributi (url, needAuth, categoria file)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Cucumber" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    Then la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And si verifica se il file richiede l'autenticazione
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative

# FLUSSO ACT su PF con QrCode - error cases

  @raddAlt
  Scenario: [RADD-ALT_ACT-2] PF - Scansione QR code esistente associato al CF corretto, ma relativo a una notifica con perfezionamento > 120 giorni
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "dopo 120gg"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti genera un errore "documenti non disponibili" con codice 4 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-3] PF - Scansione QR code inesistente
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "inesistente"
    When L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    Then Viene restituito un messaggio di errore "QRcode non valido" con codice di errore 10 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-4] PF - Scansione QR code esistente associato al CF sbagliato
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo                            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "Mario Gherkin" come destinatario 0 mostra il QRCode "appartenente a terzo"
    When L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    Then Viene restituito un messaggio di errore "CF non valido" con codice di errore 10 su radd alternative

  #da verificare se il mesaggio dovrebbe essere - notifica annullata dalla PA
  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-10] PF - Notifica annullata - Restituzione errore al tentativo di recupero documenti di notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And Viene restituito un messaggio di errore "Questa notifica è stata annullata dall’ente mittente" con codice di errore 80 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-11] PF - Restituzione errore - Documento non stampabile tra quelli disponibili nella lista dei documenti associati a QR code esistente con CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And si inizia il processo di caricamento dei documento di identità del cittadino ma non si porta a conclusione su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti genera un errore "documenti non disponibili" con codice 4 su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-88] PF - Interruzione processo recupero atti e avvio nuovo processo su stessa notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Cucumber" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And la transazione viene abortita per gli "act"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    Then viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative

# FLUSSO ACT su PG con QrCode - successfully cases

  #[RADD-ALT_ACT-12], [RADD-ALT_ACT-16], [RADD-ALT_ACT-40]
  #
  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-17] PG - Scansione QR code esistente scansione documenti creazione file zip e stampa documenti disponibili con CF corretto: stato Avvenuto Accesso non presente in timeline
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "CucumberSpa" come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    Then lato destinatario la notifica può essere correttamente recuperata da "CucumberSpa" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
    And lato desinatario "CucumberSpa" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-60] PG - Scansione QR code esistente, associato al CF corretto, per una notifica con allegati di pagamento (Avviso PagoPA e F24)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm   | SI                   |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | title_payment        | F24_STANDARD_PG      |
      | apply_cost_pagopa    | SI                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "CucumberSpa" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti di "CucumberSpa"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 6 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-61] PG - Scansione QR code esistente, associato al CF corretto, per una notifica con allegato di pagamento (solo F24)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm   | NULL                 |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | title_payment        | F24_STANDARD_PG      |
      | apply_cost_pagopa    | NO                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "CucumberSpa" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti di "CucumberSpa"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 5 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  # notifica gia letta
  # [RADD-ALT_ACT-87]
  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-91] PG - Stampa documenti disponibili associati a QR code esistente con CF corretto la cui notifica è in stato avvenuto accesso: verifica presenza attributi (url, needAuth, categoria file)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "CucumberSpa" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then Il cittadino "CucumberSpa" come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And si verifica se il file richiede l'autenticazione
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative

  @raddAlt @uatEnvCondition @con020 @con020OldTestSuccessful
    # TODO la sequenza OK_890_ZIP sembra non produrre un evento CON020. IUN: DHAU-TZTU-GEYH-202411-L-1
  Scenario: [RADD-ALT_ACT-82] Inserimento notifica indirizzata a PG con sequence OK_890_ZIP  - verifica presenza elemento di timeline contenente la ricevuta di postalizzazione in formato zip
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL           |
      | physicalAddress_address | Via@OK_890_ZIP |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG001B"
    And viene effettuato un controllo sul type zip attachment di "ATTACHMENTS" per l'elemento di timeline "SEND_ANALOG_PROGRESS" con DOC "23L"
      | loadTimeline               | true      |
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG001B |

# FLUSSO ACT su PG con QrCode - error cases

  @raddAlt
  Scenario: [RADD-ALT_ACT-13] PG - Scansione QR code esistente associato al CF corretto, ma relativo a una notifica con perfezionamento > 120 giorni.
    When Il cittadino "CucumberSpa" come destinatario 0 mostra il QRCode "dopo 120gg"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti genera un errore "documenti non disponibili" con codice 4 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-14] PG - Scansione QR code inesistente
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "CucumberSpa" come destinatario 0 mostra il QRCode "inesistente"
    When L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    Then Viene restituito un messaggio di errore "QRcode non valido" con codice di errore 10 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-15] PG - Scansione QR code esistente associato al CF sbagliato
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "Gherkin Irreperibile" come destinatario 0 mostra il QRCode "appartenente a terzo"
    When L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    Then Viene restituito un messaggio di errore "CF non valido" con codice di errore 10 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-22] PG - Restituzione errore - Documento non stampabile tra quelli disponibili nella lista dei documenti associati a QR code esistente con CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "CucumberSpa" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And si inizia il processo di caricamento dei documento di identità del cittadino ma non si porta a conclusione su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti genera un errore "documenti non disponibili" con codice 4 su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-89] PG - Interruzione processo recupero atti e avvio nuovo processo su stessa notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "CucumberSpa" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then Il cittadino "CucumberSpa" come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And la transazione viene abortita per gli "act"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    Then viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

# FLUSSO ACT per multidestinatario PF/PG

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-59] PF/PG - Scansione QR code esistente, associato al CF corretto, per una notifica multi destinatario con allegati di pagamento (Avvisi PagoPA e F24)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24          | PAYMENT_F24_STANDARD          |
      | title_payment        | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm   | SI                   |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | title_payment        | F24_STANDARD_PG      |
      | apply_cost_pagopa    | SI                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE" per l'utente 1
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT" per l'utente 1
    And  Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti di "Mario Cucumber"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 6 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    Then  Il cittadino "CucumberSpa" come destinatario 1 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti di "CucumberSpa"
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 6 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative

# FLUSSO ACT su PF con IUN - successfully cases

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-48] PF - Recupero notifica con codice IUN esistente associato al CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber"
    Then la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    When viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-57] PF -  Recupero notifica con allegato di pagamento (solo Avviso PagoPA)  con codice IUN esistente associato a CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via @ok_890 |
      | payment_pagoPaForm      | SI          |
      | payment_f24             | NULL        |
      | apply_cost_pagopa       | SI          |
      | apply_cost_f24          | NO          |
      | payment_multy_number    | 1           |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber"
    And la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 5 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-58] PF - Recupero notifica con allegati di pagamento (due o più Avvisi PagoPA e due o più F24) con codice IUN esistente associato a CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via @ok_890                   |
      | payment_pagoPaForm      | SI                            |
      | payment_f24             | PAYMENT_F24_STANDARD          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | SI                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 2                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber"
    And la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 8 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

# FLUSSO ACT su PF con IUN - error cases

  @raddAlt
  Scenario: [RADD-ALT_ACT-47] PF - Restituzione errore - Recupero notifica solo con CF corretto
    When L'operatore usa lo IUN "non esitente" per recuperare gli atti di "Mario Cucumber" con restituzione errore
    Then l'operazione ha prodotto un errore con status code "400"

  @raddAlt
  Scenario: [RADD-ALT_ACT-49] PF - Recupero notifica con codice IUN errato associato a CF corretto
    When L'operatore usa lo IUN "errato" per recuperare gli atti di "Mario Cucumber"
    Then Viene restituito un messaggio di errore "input non valido" con codice di errore 10 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-50] PF -  Recupero notifica con codice IUN esistente associato a CF sbagliato
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Gherkin"
    Then Viene restituito un messaggio di errore "input non valido" con codice di errore 10 su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-95] PF - Visualizzazione documenti di notifica con una PF con messaggio di corstesia e molti F24 controllo restituzione statusCode 2 con retry - PN-10916
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo                            |
      | feePolicy          | DELIVERY_MODE                                |
      | paFee              | 0                                            |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via @ok_890                   |
      | payment_pagoPaForm      | SI                            |
      | payment_f24             | PAYMENT_F24_STANDARD          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | SI                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 15                            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si controlla con check rapidi che lo stato diventi ACCEPTED
    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber"
    Then la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative senza ritentativi
    And l'operazione di download degli atti genera un errore "documento non disponibile per il download" con codice 2 su radd alternative

# FLUSSO ACT su PG con IUN - successfully cases

  #[RADD-ALT_ACT-19], RADD-ALT_ACT-52]
  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-18] PG - Consegna documenti al cittadino successivi alla stampa
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And L'operatore usa lo IUN "corretto" per recuperare gli atti di "CucumberSpa"
    And la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    When vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    Then lato destinatario la notifica può essere correttamente recuperata da "CucumberSpa" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
    And lato desinatario "CucumberSpa" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista

  @raddAlt
  Scenario: [RADD-ALT_ACT-62] PG -  Recupero notifica con allegato di pagamento (solo Avviso PagoPA)  con codice IUN esistente associato a CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm   | SI   |
      | payment_f24          | NULL |
      | apply_cost_pagopa    | SI   |
      | apply_cost_f24       | NO   |
      | payment_multy_number | 1    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then L'operatore usa lo IUN "corretto" per recuperare gli atti di "CucumberSpa"
    And la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 5 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-63] PG - Recupero notifica con allegati di pagamento (due o più Avvisi PagoPA e due o più F24) con codice IUN esistente associato a CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm   | SI                   |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | title_payment        | F24_STANDARD_PG      |
      | apply_cost_pagopa    | SI                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 2                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then L'operatore usa lo IUN "corretto" per recuperare gli atti di "CucumberSpa"
    And la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 8 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

# FLUSSO ACT su PG con IUN - error cases

#  test non piu valido per feature stampa multipla
#
#  @raddAlt @zip
#  Scenario: [RADD-ALT_ACT-20] PG - Restituzione errore - Documenti già stampati
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative  |
#      | senderDenomination | Comune di Palermo           |
#    And destinatario CucumberSpa
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "CucumberSpa"
#    Then la lettura si conclude correttamente su radd alternative
#    # And vengono caricati i documento di identità del cittadino su radd alternative
#    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
#    And l'operazione di download degli atti si conclude correttamente su radd alternative
#    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
#    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "CucumberSpa"
#    And Viene restituito un messaggio di errore "Stampa già eseguita" con codice di errore 3 su radd alternative

  #da verificare se il mesaggio dovrebbe essere - notifica annullata dalla PA
  @raddAlt
  Scenario: [RADD-ALT_ACT-21] PG - Notifica annullata - Restituzione errore al tentativo di recupero documenti di notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And la notifica può essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "CucumberSpa"
    Then Viene restituito un messaggio di errore "Questa notifica è stata annullata dall’ente mittente" con codice di errore 80 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-51] PG - Restituzione errore - Recupero notifica solo con CF corretto
    When L'operatore usa lo IUN "non esitente" per recuperare gli atti di "CucumberSpa" con restituzione errore
    Then l'operazione ha prodotto un errore con status code "400"

  @raddAlt
  Scenario: [RADD-ALT_ACT-53] PG - Recupero notifica con codice IUN errato associato a CF corretto
    When L'operatore usa lo IUN "errato" per recuperare gli atti di "CucumberSpa"
    Then Viene restituito un messaggio di errore "input non valido" con codice di errore 10 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_ACT-54] PG -  Recupero notifica con codice IUN esistente associato a CF sbagliato
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "Gherkin Irreperibile"
    Then Viene restituito un messaggio di errore "input non valido" con codice di errore 10 su radd alternative

# FLUSSO AOR su PF con QrCode - successfully cases

  #[RADD-ALT_AOR-24], [RADD-ALT_AOR-25], [RADD-ALT_AOR-26], [RADD-ALT_AOR-26_1]
  @raddAlt @zip
  Scenario: [RADD-ALT_AOR-23] PF - Visualizzazione link AAR disponibili con consegna documenti al cittadino successivi alla stampa documenti per notifiche associate al CF corretto (irreperibile totale)
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative


  @raddAlt @authFleet
  Scenario: [RADD-ALT_AOR-64] PF - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale) con allegato Avviso PagoPA e F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR                      |
      | payment_pagoPaForm      | SI                                            |
      | payment_f24             | PAYMENT_F24_STANDARD                          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z                 |
      | apply_cost_pagopa       | SI                                            |
      | apply_cost_f24          | SI                                            |
      | payment_multy_number    | 1                                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor casuale" chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente e vengono restituiti 2 aar su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_AOR-67] PF - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale) con allegati due o più Avvisi PagoPA e due o più F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR  |
      | payment_pagoPaForm      | SI                                            |
      | payment_f24             | PAYMENT_F24_STANDARD                          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z                 |
      | apply_cost_pagopa       | SI                                            |
      | apply_cost_f24          | SI                                            |
      | payment_multy_number    | 2                                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona fisica "Signor casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente e vengono restituiti 2 aar su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_AOR-65] PF - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale) con allegato F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR  |
      | payment_pagoPaForm      | NULL                                          |
      | payment_f24             | PAYMENT_F24_STANDARD                          |
      | title_payment           | F24_STANDARD_FRMTTR76M06B715E                 |
      | apply_cost_pagopa       | NO                                            |
      | apply_cost_f24          | SI                                            |
      | payment_multy_number    | 1                                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona fisica "Signor casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente e vengono restituiti 2 aar su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_AOR-66] PF - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale) con allegato un Avviso PagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR  |
      | payment_pagoPaForm      | SI                                            |
      | payment_f24             | NULL                                          |
      | apply_cost_pagopa       | SI                                            |
      | apply_cost_f24          | NO                                            |
      | payment_multy_number    | 1                                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona fisica "Signor casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente e vengono restituiti 2 aar su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

# FLUSSO AOR su PF con QrCode - error cases

  @raddAlt
  Scenario: [RADD-ALT_AOR-27] PF - Restituzione errore - nessuna Notifica disponibile associata al CF
    When la persona fisica "Signor Generato" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99 su radd alternative

  @raddAlt
  Scenario: [RADD-ALT_AOR-28] PF - Restituzione errore - nessuna Notifica disponibile in stato Irreperibile associata al CF corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di milano            |
    And destinatario Signor casuale
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When la persona fisica "Signor Casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99 su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_AOR-29] PF - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, ma inibizione stampa documenti associati alla notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di milano            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor Casuale" chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    And la transazione viene abortita per gli "aor"
    And l'operazione di abort genera un errore "La transazione risulta già completa" con codice 2 su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_AOR-92] PF - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, ma inibizione 2 volte
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di milano            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona fisica "Signor Casuale" chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And la transazione viene abortita per gli "aor"
    And la transazione viene abortita per gli "aor"
    And l'operazione di abort genera un errore "La transazione risulta annullata" con codice 99 su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_AOR-93] PF - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, inibizione poi complete
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di milano            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona fisica "Signor Casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And la transazione viene abortita per gli "aor"
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar ha generato l'errore "La transazione risulta annullata" con statusCode 2 su radd alternative

## FLUSSO AOR su PG con QrCode - successfully cases

  #[RADD-ALT_AOR-32] [RADD-ALT_AOR-33] [RADD-ALT_AOR-31]
  @raddAltManuale @zip
  Scenario: [RADD-ALT_AOR-30] PG - Visualizzazione link AAR disponibili con consegna documenti alla PG successivi alla stampa documenti per notifiche associate al CF corretto (irreperibile totale)
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la persona giuridica "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono recuperati gli aar delle notifiche in stato irreperibile della persona giuridica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAltManuale
  Scenario: [RADD-ALT_AOR-68] PG - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale) con allegato Avviso PagoPA e F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR  |
      | payment_pagoPaForm      | SI                                            |
      | payment_f24             | PAYMENT_F24_STANDARD                          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z                 |
      | apply_cost_pagopa       | SI                                            |
      | apply_cost_f24          | SI                                            |
      | payment_multy_number    | 1                                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona giuridica "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAltManuale
  Scenario: [RADD-ALT_AOR-69] PG - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale) con allegato F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR  |
      | payment_pagoPaForm      | NULL                                          |
      | payment_f24             | PAYMENT_F24_STANDARD                          |
      | title_payment           | F24_STANDARD_FRMTTR76M06B715E                 |
      | apply_cost_pagopa       | NO                                            |
      | apply_cost_f24          | SI                                            |
      | payment_multy_number    | 1                                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona giuridica "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAltManuale
  Scenario: [RADD-ALT_AOR-70] PG - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale) con allegato un Avviso PagoPA
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR  |
      | payment_pagoPaForm      | SI                                            |
      | payment_f24             | NULL                                          |
      | apply_cost_pagopa       | SI                                            |
      | apply_cost_f24          | NO                                            |
      | payment_multy_number    | 1                                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona giuridica "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAltManuale
  Scenario: [RADD-ALT_AOR-71] PG - Notifiche Disponibili associate al CF corretto fornito dal destinatario (irreperibile totale) con allegati due o più Avvisi PagoPA e due o più F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR  |
      | payment_pagoPaForm      | SI                                            |
      | payment_f24             | PAYMENT_F24_STANDARD                          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z                 |
      | apply_cost_pagopa       | SI                                            |
      | apply_cost_f24          | SI                                            |
      | payment_multy_number    | 2                                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona giuridica "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

# FLUSSO AOR su PG con QrCode - error cases

  @raddAlt
  Scenario: [RADD-ALT_AOR-34] PG - Restituzione errore - nessuna Notifica disponibile associata al CF corretto
    Given la persona giuridica "cucumberspa" chiede di verificare la presenza di notifiche

  @raddAlt
  Scenario: [RADD-ALT_AOR-35] PG - Restituzione errore - nessuna Notifica disponibile in stato Irreperibile associata al CF corretto
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Given la persona giuridica "cucumberspa" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99 su radd alternative

  @raddAltManuale @zip
  Scenario: [RADD-ALT_AOR-36] PG - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, ma inibizione stampa documenti associati alla notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di milano            |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona giuridica "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona giuridica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    And la transazione viene abortita per gli "aor"
    And l'operazione di abort genera un errore "La transazione risulta già completa" con codice 2 su radd alternative

# FLUSSO AOR per Multidestinatario  PF/PG

  @raddAltManuale @zip
  Scenario: [RADD-ALT_AOR-72] PF/PG - Notifica multi destinatario disponibile associata al CF corretto fornito dal destinatario (irreperibile totale) con allegati Avvisi PagoPA e F24
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR  |
      | payment_pagoPaForm      | SI                                            |
      | payment_f24             | PAYMENT_F24_STANDARD                          |
      | title_payment           | F24_STANDARD_CASUALE                          |
      | apply_cost_pagopa       | SI                                            |
      | apply_cost_f24          | SI                                            |
      | payment_multy_number    | 1                                             |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                                          |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR  |
      | payment_pagoPaForm      | SI                                            |
      | payment_f24             | PAYMENT_F24_STANDARD                          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z                 |
      | apply_cost_pagopa       | SI                                            |
      | apply_cost_f24          | SI                                            |
      | payment_multy_number    | 1                                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 0
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 1
    When la persona fisica "Signor Casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente e vengono restituiti 2 aar su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    When la persona giuridica "Gherkin Irreperibile" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

# ACT TRANSACTION

#  scenario gia coperto sulle nuove feature...
#
#  @raddAlt @zip
#  Scenario: [RADD-ALT_ACT-75] PF -  Start di una ACT transaction con stesso operationId da cxId diversi - ricezione OK
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative  |
#      | senderDenomination | Comune di Palermo           |
#    And destinatario Mario Cucumber
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
#    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
#    And la scansione si conclude correttamente su radd alternative
#    And vengono caricati i documento di identità del cittadino su radd alternative
#    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
#    And l'operazione di download degli atti si conclude correttamente su radd alternative
#    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR con lo stesso operationId dal raddista "issuer_2"
#    And l'operazione di download degli atti si conclude correttamente su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-73] PF -  Start di una ACT transaction con stesso operationId - ricezione Errore
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "Mario Cucumber" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti genera un errore "Transazione già esistente o con stato completed o aborted" con codice 5 su radd alternative

  # AOR TRANSACTION

  @raddAlt @zip
  Scenario: [RADD-ALT_AOR-74] PF -  Start di una AOR transaction con stesso operationId - ricezione Errore
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona fisica "Signor casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar genera un errore "Transazione già esistente o con stato completed o aborted" con codice 5 su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_AOR-74_1] PF -  Start di una AOR transaction con stessa Request
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona fisica "Signor casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica 2 volte su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_AOR-76] PF -  Start di una AOR transaction con stesso operationId da cxId diversi - ricezione OK
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona fisica "Signor casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    #capire se si puo creare un altro Uploader o metterlo a standard
    #Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica con lo stesso operationId dal raddista "issuer_2"
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative

  @raddAlt @ignore
  Scenario: [RADD-ALT_AOR-77] PF -  Start di una AOR transaction su notifica irreperibile perfezionata > 120gg - Ricezione errore RetryAfter
    When la persona fisica "Mario Cucumber" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative

# Frontespizio cases

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-78] PF - Verifica restituzione al cittadino del documento Frontespizio (nome e cognome del destinatario) come primo documento del plico
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario Signor casuale e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    When la persona fisica "Signor Casuale" chiede di verificare la presenza di notifiche
    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisica su radd alternative
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And L'operatore esegue il download del frontespizio del operazione "aor"
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-79] PG - Verifica restituzione al cittadino del documento Frontespizio (ragione sociale dell'impresa destinataria) come primo documento del plico
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative  |
      | senderDenomination | Comune di Palermo           |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    When Il cittadino "CucumberSpa" come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And L'operatore esegue il download del frontespizio del operazione "act"
    Then viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative

  @raddAlt @zip
  Scenario: [RADD-ALT_ACT-94] PF - Verifica restituzione errore al download del documento Frontespizio con attachmentId non esistente - PN-11259
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo                            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    When L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber"
    And la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    Then L'operatore esegue il download del frontespizio del operazione "act" con attachmentId "nonEsistente"
    And l'operazione ha prodotto un errore con status code "400"

# AUDIT cases

  @raddAlt @raddAltLog
  Scenario: [RADD-ALT_AUDIT_LOG-96] Scansione QR code o IUN e verifica auditlog AUD_RADD_ACTTRAN
    When viene verificato che esiste un audit log "AUD_RADD_ACTTRAN" in "10y"
    Then viene verificato che esiste un audit log "AUD_RADD_ACTTRAN" con messaggio "[AUD_RADD_ACTTRAN] FAILURE"
    Then viene verificato che esiste un audit log "AUD_RADD_ACTTRAN" senza messaggio con "error null"

  @raddAlt @raddAltLog
  Scenario Outline: [RADD-ALT_AUDIT_LOG-97] Scansione QR code o IUN e verifica auditlog AUD_RADD_ACTTRAN
    When viene verificato che esiste un audit log "<audit-log>" in "10y"
    Then viene verificato che esiste un audit log "<audit-log>" con messaggio "operationId="
    Then viene verificato che esiste un audit log "<audit-log>" senza messaggio con "operationId=null"
    Examples:
      | audit-log        |
      | AUD_RADD_ACTTRAN |
      | AUD_RADD_AORTRAN |

  #Copre scenari tutti i scenari di test 43-44-45-46
  @raddAlt @raddAltLog
  Scenario Outline: [RADD-ALT_AUDIT_LOG-42] Scansione QR code o IUN e verifica auditlog AUD_RADD_ACTINQUIRY
    Then viene verificato che esiste un audit log "<audit-log>" in "10y"
    Examples:
      | audit-log           |
      | AUD_RADD_ACTINQUIRY |
      | AUD_RADD_AORINQUIRY |
      | AUD_RADD_ACTTRAN    |
      | AUD_RADD_AORTRAN    |
      | AUD_NT_RADD_OPEN    |

# @raddAlt @ignore MANUALE
# Scenario: [RADD-ALT_ACT-41] PF/PG - Check conformità AAR

  @raddAlt
  Scenario Outline: [RADD-ALT_ACT-19] PF/PA - stampa e consegna documenti disponibili associati - visualizzazione in timeline avvenuta consegna tramite RADD (NOTIFICATION_RADD_RETRIEVED) e recupero timeline dal cruscotto assistenza (Hotfix PN-12886)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo |
    And destinatario
      | denomination           | <CITIZEN>  |
      | taxId                  | <CF>       |
      | digitalDomicile_address| testpagopa3@pec.pagopa.it |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino "<CITIZEN>" come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti di "<CITIZEN>"
    Then la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_RADD_RETRIEVED"
    And viene verificato che l'elemento di timeline "NOTIFICATION_VIEWED" non esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And lato destinatario la notifica può essere correttamente recuperata da "<CITIZEN>" e verifica presenza dell'evento di timeline NOTIFICATION_RADD_RETRIEVED
    And lato desinatario "<CITIZEN>" viene verificato che l'elemento di timeline NOTIFICATION_VIEWED non esista
    And invocazione servizio per recupero timeline notifica con iun
    And Il servizio risponde correttamente
    Examples:
      | CITIZEN       | CF               |
      | Mario Cucumber | FRMTTR76M06B715E |





