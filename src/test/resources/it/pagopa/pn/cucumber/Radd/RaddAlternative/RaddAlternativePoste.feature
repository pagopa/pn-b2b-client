Feature: Radd Alternative integrazione con Poste

# Api coinvolte:
#  /radd-net/api/v1/download/{operationType}/{operationId}:

#  /radd-net/api/v1/act/inquiry:
#  /radd-net/api/v1/act/transaction/start:
#  /radd-net/api/v1/act/transaction/complete:
#  /radd-net/api/v1/act/transaction/abort:
#  /radd-net/api/v1/aor/inquiry:
#  /radd-net/api/v1/aor/transaction/start:
#  /radd-net/api/v1/aor/transaction/complete:
#  /radd-net/api/v1/aor/transaction/abort:

#NB. Questa suite ha gli step condivisi con radd-alt, i client sono gestiti tramite annotation in RaddHooks
# Questa suite va eseguita solo in DEV, inserendo come variabile di compilazione RADD_POSTE_ENV = test o uat
  # così da richiamare gli end-point storati nel parameter store in /pn-test-e2e/raddVpcBaseUrlUat /raddVpcBaseUrlTest



  @useRaddVpce
  Scenario: [RADD_POSTE_01_3] Verifica allegato ARCAD per secondo evento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode = RECAG011B
    Given carico i dati della notifica con chiave "[RADD_POSTE_01_3]"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And L'operatore esegue il download del frontespizio del operazione "act"
    Then viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative

  @useRaddVpce
  Scenario: [RADD_POSTE_02_1_A] PF - Scansione QR code esistente associato al CF corretto, per una notifica con allegati di pagamento (Avviso PagoPA e F24)
    Given carico i dati della notifica con chiave "[RADD_POSTE_02_1_A]"
    When L'operatore scansione il qrCode per recuperare gli atti di Mario Gherkin
    Then la scansione si conclude correttamente su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 7 documenti


  @useRaddVpce
  Scenario: [RADD_POSTE_02_1_G] PF - Interruzione processo recupero atti e avvio nuovo processo su stessa notifica
    Given carico i dati della notifica con chiave "[RADD_POSTE_02_1_G]"
    And L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And la transazione viene abortita per gli "act"
    Then L'operatore scansione il qrCode per recuperare gli atti da radd alternative
    And la scansione si conclude correttamente su radd alternative

  @useRaddVpce
  Scenario: [RADD_POSTE_AOR_03_1_A] PG - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, ma inibizione stampa documenti associati alla notifica
    Given carico i dati della notifica con chiave "[RADD_POSTE_AOR_03_1_A]"
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
    Given carico i dati della notifica con chiave "[RADD_POSTE_AOR_03_1_B]"
    And la persona giuridica Gherkin Irreperibile chiede di verificare la presenza di notifiche
    And La verifica della presenza di notifiche in stato irreperibile per il cittadino si conclude correttamente su radd alternative
    And Vengono recuperati gli aar delle notifiche in stato irreperibile della persona giuridica su radd vpce
    And il recupero degli aar in stato irreperibile si conclude correttamente su radd alternative
    And viene chiusa la transazione per il recupero degli aar su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @useRaddVpce
  Scenario: [RADD_POSTE_02_1_C] PG - Scansione QR code esistente, associato al CF corretto, per una notifica con allegato di pagamento (solo F24)
    Given carico i dati della notifica con chiave "[RADD_POSTE_02_1_C]"
    When L'operatore scansione il qrCode per recuperare gli atti di CucumberSpa
    And la scansione si conclude correttamente su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 5 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative



 # **** DATA PREP ************

  @useRaddVpceDataPreparation
  Scenario: [DP_RADD_POSTE_01_3] Verifica allegato ARCAD per secondo evento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode = RECAG011B
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                         |
      | physicalAddress_address | via@OK-Giacenza-gt10_890_ZIP |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG011B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_EXTERNAL_LEGAL_FACTS.+\.zip"
    When Il cittadino Mario Gherkin come destinatario 0 mostra il QRCode "corretto"
    Then salvo i dati della notifica con chiave "[RADD_POSTE_01_3]"


  @useRaddVpceDataPreparation
  Scenario: [DP_RADD_POSTE_02_1_A] PF - Scansione QR code esistente associato al CF corretto, per una notifica con allegati di pagamento (Avviso PagoPA e F24)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo                            |
      | feePolicy          | DELIVERY_MODE                                |
      | paFee              | 0                                            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via @ok_890                   |
      | payment_pagoPaForm      | SI                            |
      | payment_f24             | PAYMENT_F24_STANDARD          |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_pagopa       | SI                            |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino Mario Gherkin come destinatario 0 mostra il QRCode "corretto"
    Then salvo i dati della notifica con chiave "[RADD_POSTE_02_1_A]"

  @useRaddVpceDataPreparation
  Scenario: [DP_RADD_POSTE_02_1_G] PF - Interruzione processo recupero atti e avvio nuovo processo su stessa notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo                            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And "Mario Cucumber" legge la notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then Il cittadino Mario Cucumber come destinatario 0 mostra il QRCode "corretto"
    Then salvo i dati della notifica con chiave "[RADD_POSTE_02_1_G]"

  @useRaddVpceDataPreparation
  Scenario: [DP_RADD_POSTE_AOR_03_1_A] PG - Visualizzazione AAR di notifiche i cui documenti sono già stati stampati, ma inibizione stampa documenti associati alla notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di milano                             |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then salvo i dati della notifica con chiave "[RADD_POSTE_AOR_03_1_A]"

  @useRaddVpceDataPreparation
  Scenario: [DP_RADD_POSTE_AOR_03_1_B] PG - Visualizzazione link AAR disponibili con consegna documenti alla PG successivi alla stampa documenti per notifiche associate al CF corretto (irreperibile totale)
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries @fail-Irreperibile_AR |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then salvo i dati della notifica con chiave "[RADD_POSTE_AOR_03_1_B]"

  @useRaddVpceDataPreparation
  Scenario: [DP_RADD_POSTE_02_1_C] PG - Scansione QR code esistente, associato al CF corretto, per una notifica con allegato di pagamento (solo F24)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber radd alternative |
      | senderDenomination | Comune di Palermo                            |
      | feePolicy          | DELIVERY_MODE                                |
      | paFee              | 0                                            |
    And destinatario CucumberSpa e:
      | payment_pagoPaForm   | NULL                 |
      | payment_f24          | PAYMENT_F24_STANDARD |
      | title_payment        | F24_STANDARD_PG      |
      | apply_cost_pagopa    | NO                   |
      | apply_cost_f24       | SI                   |
      | payment_multy_number | 1                    |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino CucumberSpa come destinatario 0 mostra il QRCode "corretto"
    Then salvo i dati della notifica con chiave "[RADD_POSTE_02_1_C]"

