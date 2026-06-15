Feature: Radd Alternative integrazione con Poste

# Api testate:
#  /radd-net/api/v1/download/{operationType}/{operationId}:   01_1/01_2/01_3

#  /radd-net/api/v1/act/inquiry:                              03A/03B/04A/04B
#  /radd-net/api/v1/act/transaction/start:                    coperto
#  /radd-net/api/v1/act/transaction/complete:                 c operto
#  /radd-net/api/v1/act/transaction/abort:                    05A/05B

#  /radd-net/api/v1/aor/inquiry:                              coperto
#  /radd-net/api/v1/aor/transaction/start:                    07/coperto
#  /radd-net/api/v1/aor/transaction/complete:                 coperto
#  /radd-net/api/v1/aor/transaction/abort:                    06A/06B



  @useRaddVpce #XWUP-TDER-TZQW-202606-R-1
  Scenario: [RADD_POSTE_01_3] Verifica allegato ARCAD per secondo evento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode = RECAG011B
#    Given viene generata una nuova notifica
#      | subject            | notifica analogica con cucumber |
#      | senderDenomination | Comune di palermo               |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile         | NULL                         |
#      | physicalAddress_address | via@OK-Giacenza-gt10_890_ZIP |
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
#      | details                    | NOT_NULL                    |
#      | details_recIndex           | 0                           |
#      | details_deliveryDetailCode | RECAG011B                   |
#      | details_sentAttemptMade    | 0                           |
#      | details_attachments        | [{"documentType": "ARCAD"}] |
#    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_EXTERNAL_LEGAL_FACTS.+\.zip"
    Given imposto lo iun di SharedSteps a "XWUP-TDER-TZQW-202606-R-1" e la pa a "Comune_Multi"
    Then Imposto il cf "CLMCST42R12D969Z" e recipient type "CF" e qrCode "WFdVUC1UREVSLVRaUVctMjAyNjA2LVItMV9QRi1hNmMxMzUwZC0xZDY5LTQyMDktOGJmOC0zMWRlNThjNzlkNmVfYTEzOTZjOTYtMmQ4ZC00NjAzLTlkZWYtNjAwYjM5ZmY3OGZm"
    #When Il cittadino Mario Gherkin come destinatario 0 mostra il QRCode "corretto"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And L'operatore esegue il download del frontespizio del operazione "act"
    Then viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative


 #   ACT

  @useRaddVpce #XVZM-TJME-KGWV-202606-V-1 ok
  Scenario: [RADD_POSTE_02_1_A] PF - Scansione QR code esistente associato al CF corretto, per una notifica con allegati di pagamento (Avviso PagoPA e F24)
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative |
#      | senderDenomination | Comune di Palermo                            |
#      | feePolicy          | DELIVERY_MODE                                |
#      | paFee              | 0                                            |
#    And destinatario Mario Gherkin e:
#      | digitalDomicile         | NULL                          |
#      | physicalAddress_address | Via @ok_890                   |
#      | payment_pagoPaForm      | SI                            |
#      | payment_f24             | PAYMENT_F24_STANDARD          |
#      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
#      | apply_cost_pagopa       | SI                            |
#      | apply_cost_f24          | SI                            |
#      | payment_multy_number    | 1                             |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Given imposto lo iun di SharedSteps a "XVZM-TJME-KGWV-202606-V-1" e la pa a "Comune_Multi"
    And Imposto il cf "CLMCST42R12D969Z" e recipient type "PF" e qrCode "WFZaTS1USk1FLUtHV1YtMjAyNjA2LVYtMV9QRi1hNmMxMzUwZC0xZDY5LTQyMDktOGJmOC0zMWRlNThjNzlkNmVfZjcwZjgzMmYtZDkzYy00ZGYxLWI0MDktMTg5ZGVmYmY4NTkz"
    #And Il cittadino Mario Gherkin come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti di Mario Gherkin
    Then la scansione si conclude correttamente su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 7 documenti


  @useRaddVpce #ok JYAY-GYDM-LWVL-202606-H-1
  Scenario: [RADD_POSTE_02_1_G] PF - Interruzione processo recupero atti e avvio nuovo processo su stessa notifica
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative |
#      | senderDenomination | Comune di Palermo                            |
#    And destinatario Mario Cucumber
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "Mario Cucumber" legge la notifica
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Given imposto lo iun di SharedSteps a "JYAY-GYDM-LWVL-202606-H-1" e la pa a "Comune_Multi"
    #Then Imposto il cf "FRMTTR76M06B715E" e recipient type "PF"
    Then Imposto il cf "FRMTTR76M06B715E" e recipient type "PF" e qrCode "SllBWS1HWURNLUxXVkwtMjAyNjA2LUgtMV9QRi00ZmM3NWRmMy0wOTEzLTQwN2UtYmRhYS1lNTAzMjk3MDhiN2RfMjRmZGRhYmItNTg2Yy00YjRiLWJlNjEtNjU3N2QxNzU0ZTE4"
    #Then Il cittadino Mario Cucumber come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And la transazione viene abortita per gli "act"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    Then viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative


   @useRaddVpce #ok UENX-YTMZ-KRVH-202606-J-1
  Scenario: [RADD_POSTE_AOR_03_1_A] PG - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, ma inibizione stampa documenti associati alla notifica
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative |
#      | senderDenomination | Comune di milano                             |
#    And destinatario Gherkin Irreperibile e:
#      | digitalDomicile         | NULL                                         |
#      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Given imposto lo iun di SharedSteps a "UENX-YTMZ-KRVH-202606-J-1" e la pa a "Comune_Multi"
    Then Imposto il cf "02455090981" e recipient type "PG"
    When la persona giuridica Gherkin Irreperibile chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona giuridica su radd vpce
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
    And la transazione viene abortita per gli "aor"
    And l'operazione di abort genera un errore "La transazione risulta già completa" con codice 2 su radd alternative


  @useRaddVpce
  Scenario: [RADD_POSTE_AOR_03_1_B] PG - Visualizzazione link AAR disponibili con consegna documenti alla PG successivi alla stampa documenti per notifiche associate al CF corretto (irreperibile totale)
#    Given viene generata una nuova notifica
#      | subject               | notifica analogica con cucumber |
#      | senderDenomination    | Comune di palermo               |
#      | physicalCommunication | AR_REGISTERED_LETTER            |
#    And destinatario Gherkin Irreperibile e:
#      | digitalDomicile         | NULL                                         |
#      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Given imposto lo iun di SharedSteps a "RQGX-KTUX-MVKD-202606-Q-1" e la pa a "Comune_Multi"
    Then Imposto il cf "02455090981" e recipient type "PG"
    And la persona giuridica Gherkin Irreperibile chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And Vengono recuperati gli aar delle notifiche in stato irreperibile della persona giuridica su radd vpce
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @useRaddVpce #LTMP-WEQR-NGZK-202606-H-1
  Scenario: [RADD_POSTE_02_1_C] PG - Scansione QR code esistente, associato al CF corretto, per una notifica con allegato di pagamento (solo F24)
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative |
#      | senderDenomination | Comune di Palermo                            |
#      | feePolicy          | DELIVERY_MODE                                |
#      | paFee              | 0                                            |
#    And destinatario CucumberSpa e:
#      | payment_pagoPaForm   | NULL                 |
#      | payment_f24          | PAYMENT_F24_STANDARD |
#      | title_payment        | F24_STANDARD_PG      |
#      | apply_cost_pagopa    | NO                   |
#      | apply_cost_f24       | SI                   |
#      | payment_multy_number | 1                    |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Given imposto lo iun di SharedSteps a "LTMP-WEQR-NGZK-202606-H-1" e la pa a "Comune_Multi"
    Then Imposto il cf "20517490320" e recipient type "PG" e qrCode "TFRNUC1XRVFSLU5HWkstMjAyNjA2LUgtMV9QRy1iMDVkZTc3Ny04MGM2LTQ1NDktYTA1NC1kOGRmZGExMzljNjJfNzk4ZmZjOTgtODhmNC00MTU5LWI2ODAtNGFkOWQ4NTg1ODdh"
    And Il cittadino CucumberSpa come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti di CucumberSpa
    And la scansione si conclude correttamente su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 5 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative


    # Errore su tentativo di upload dei documenti.
  #@useRaddVpce
  Scenario: [RADD_POSTE_02_1_I] PG - Come Operatore Radd con accesso da privatelink ricevo errore nel tentativo di effettuare l'upload dei documenti.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo                            |
    And destinatario CucumberSpa
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Il cittadino CucumberSpa come destinatario 0 mostra il QRCode "corretto"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    And non viene concesso l'upload documento via VPCE
    #ERRORE 500 todo t radd

  #  Scenario: [RADD_POSTE_AOR_03_1_D] PG - Restituzione errore - nessuna Notifica disponibile in stato Irreperibile associata al CF corretto
#    Given viene generata una nuova notifica
#      | subject               | notifica analogica con cucumber |
#      | senderDenomination    | Comune di palermo               |
#      | physicalCommunication | AR_REGISTERED_LETTER            |
#    And destinatario CucumberSpa
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    Given la persona giuridica CucumberSpa chiede di verificare la presenza di notifiche
#    Then La verifica della presenza di notifiche in stato irreperibile genera un errore "Non ci sono notifiche non consegnate per questo codice fiscale" con codice 99 su radd alternative



  #  @useRaddVpce #LTMP-WEQR-NGZK-202606-H-1
#  Scenario: [RADD_POSTE_01_2] PF - Verifica restituzione al cittadino del documento Frontespizio (nome e cognome del destinatario) come primo documento del plico
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber radd alternative |
#      | senderDenomination    | Comune di Palermo                            |
#      | physicalCommunication | AR_REGISTERED_LETTER                         |
#    And destinatario Signor Casuale e:
#      | digitalDomicile         | NULL                                         |
#      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
#    When la persona fisica Signor Casuale chiede di verificare la presenza di notifiche
#    Then La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
#    Then Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisicagiuridica su radd vpce
#    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
#    And L'operatore esegue il download del frontespizio del operazione "aor"
#    And viene chiusa la transazione per il recupero degli aar su radd alternative
#    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  #@useRaddVpce
#  Scenario: [RADD_POSTE_01_1] PG - Verifica restituzione al cittadino del documento Frontespizio (ragione sociale dell'impresa destinataria) come primo documento del plico
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative |
#      | senderDenomination | Comune di Palermo                            |
#    And destinatario CucumberSpa
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    When Il cittadino CucumberSpa come destinatario 0 mostra il QRCode "corretto"
#    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
#    And la scansione si conclude correttamente su radd alternative
#    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
#    And l'operazione di download degli atti si conclude correttamente su radd alternative
#    And L'operatore esegue il download del frontespizio del operazione "act"
#    Then viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative



#  @useRaddVpce #TUWG-LAHT-RNQM-202606-Y-1
#  Scenario: [RADD_POSTE_02_1_F] PF - Scansione QR code esistente associato al CF corretto, per una notifica con allegato di pagamento (solo F24)
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative |
#      | senderDenomination | Comune di Palermo                            |
#      | feePolicy          | DELIVERY_MODE                                |
#      | paFee              | 0                                            |
#    And destinatario Mario Cucumber e:
#      | digitalDomicile         | NULL                          |
#      | physicalAddress_address | Via @ok_890                   |
#      | payment_pagoPaForm      | NULL                          |
#      | payment_f24             | PAYMENT_F24_STANDARD          |
#      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
#      | apply_cost_pagopa       | NO                            |
#      | apply_cost_f24          | SI                            |
#      | payment_multy_number    | 1                             |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And Il cittadino Mario Cucumber come destinatario 0 mostra il QRCode "corretto"
#    When L'operatore scansione il qrCode per recuperare gli atti di Mario Cucumber
#    Then la scansione si conclude correttamente su radd alternative
#    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
#    And l'operazione di download degli atti si conclude correttamente su radd alternative
#    And l'operazione di download restituisce 6 documenti
#    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
#    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

#  @useRaddVpce #EDLT-LPQZ-WLQR-202606-H-1
#  Scenario: [RADD_POSTE_02_1_E] PF -  Recupero notifica con allegato di pagamento (solo Avviso PagoPA)  con codice IUN esistente associato a CF corretto
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative |
#      | senderDenomination | Comune di Palermo                            |
#      | feePolicy          | DELIVERY_MODE                                |
#      | paFee              | 0                                            |
#    And destinatario Mario Cucumber e:
#      | digitalDomicile         | NULL        |
#      | physicalAddress_address | Via @ok_890 |
#      | payment_pagoPaForm      | SI          |
#      | payment_f24             | NULL        |
#      | apply_cost_pagopa       | SI          |
#      | apply_cost_f24          | NO          |
#      | payment_multy_number    | 1           |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    Then L'operatore usa lo IUN "corretto" per recuperare gli atti di Mario Cucumber
#    And la lettura si conclude correttamente su radd alternative
#    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
#    And l'operazione di download degli atti si conclude correttamente su radd alternative
#    And l'operazione di download restituisce 6 documenti
#    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
#    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

#  @useRaddVpce
#  Scenario: [RADD_POSTE_02_1_D] PG -  Recupero notifica con allegato di pagamento (solo Avviso PagoPA)  con codice IUN esistente associato a CF corretto
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative |
#      | senderDenomination | Comune di Palermo                            |
#      | feePolicy          | DELIVERY_MODE                                |
#      | paFee              | 0                                            |
#    And destinatario CucumberSpa e:
#      | payment_pagoPaForm   | SI   |
#      | payment_f24          | NULL |
#      | apply_cost_pagopa    | SI   |
#      | apply_cost_f24       | NO   |
#      | payment_multy_number | 1    |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    Then L'operatore usa lo IUN "corretto" per recuperare gli atti di CucumberSpa
#    And la lettura si conclude correttamente su radd alternative
#    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
#    And l'operazione di download degli atti si conclude correttamente su radd alternative
#    And l'operazione di download restituisce 5 documenti
#    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
#    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  #  @useRaddVpce #VZJN-JKQN-MVRM-202606-P-1
#  Scenario: [RADD_POSTE_02_1_H] PG - Interruzione processo recupero atti e avvio nuovo processo su stessa notifica
#    Given viene generata una nuova notifica
#      | subject            | invio notifica con cucumber radd alternative |
#      | senderDenomination | Comune di Palermo                            |
#    And destinatario CucumberSpa
#    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
#    And "CucumberSpa" legge la notifica
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
#    Then Il cittadino CucumberSpa come destinatario 0 mostra il QRCode "corretto"
#    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
#    And la scansione si conclude correttamente su radd alternative
#    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
#    And la transazione viene abortita per gli "act"
#    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
#    And la scansione si conclude correttamente su radd alternative
#    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
#    And l'operazione di download degli atti si conclude correttamente su radd alternative
#    Then viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
#    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative


#   AOR
#  #@useRaddVpce
#  Scenario: [RADD_POSTE_AOR_03_1_C] PF - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, ma inibizione stampa documenti associati alla notifica
#    Given viene generata una nuova notifica
#      | subject               | invio notifica con cucumber radd alternative |
#      | senderDenomination    | Comune di milano                             |
#      | physicalCommunication | AR_REGISTERED_LETTER                         |
#    And destinatario Signor Casuale e:
#      | digitalDomicile         | NULL                                         |
#      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
#    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
#    And la persona fisica Signor Casuale chiede di verificare la presenza di notifiche
#    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
#    And Vengono recuperati gli aar delle notifiche in stato irreperibile della persona fisicagiuridica su radd vpce
#    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
#    And viene chiusa la transazione per il recupero degli aar su radd alternative
#    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative
#    And la transazione viene abortita per gli "aor"
#    And l'operazione di abort genera un errore "La transazione risulta già completa" con codice 2 su radd alternative