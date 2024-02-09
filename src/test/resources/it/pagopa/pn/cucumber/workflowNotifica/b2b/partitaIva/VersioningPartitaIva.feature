Feature: controllo costo notifiche con IVA

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_1] Invio notifica 890 SYNC DELIVERY_MODE con la V21 con iva inclusa controllo costo
    Given viene generata una nuova notifica V21
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | payment_pagoPaForm      | SI         |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V21"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" V21
    And viene verificato il costo "parziale" di una notifica "890" del utente "0"
    And viene verificato il costo "totale" di una notifica "890" del utente "0"


  #messo in ignore fino a rilascio fix arrotondamento costi async
  @partitaIva @ignore
  Scenario: [PARTITA-IVA_V21-V23_2] Invio notifica 890 ASYNC DELIVERY_MODE con la V21 con iva inclusa controllo costo con V23
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica V21
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | vat                | 10                          |
      | paFee              | 10                          |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@ok_890  |
      | payment_creditorTaxId   | 77777777777 |
      | payment_pagoPaForm      | SI          |
      | apply_cost_pagopa       | SI          |
      | payment_multy_number    | 1           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V21"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" V21
    Then  lettura amount posizione debitoria per la notifica corrente di "Mario Gherkin"
    And viene effettuato il controllo del amount di GPD con il costo "parziale" della notifica con iva inclusa
    Then viene cancellata la posizione debitoria di "Mario Gherkin"

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_3] Invio notifica 890 SYNC DELIVERY_MODE con 1 F24 con la V21 con iva inclusa controllo costo
    Given viene generata una nuova notifica V21
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 10                          |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via@ok_890                    |
      | payment_f24             | PAYMENT_F24_STANDARD_0        |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | payment_pagoPaForm      | SI                            |
      | apply_cost_f24          | SI                            |
      | apply_cost_pagopa       | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V21"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" V21
    Then viene verificato il costo "parziale" di una notifica "890" del utente "0"
    And viene verificato il costo "totale" di una notifica "890" del utente "0"

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_4] Invio notifica 890 ASYNC DELIVERY_MODE con la V21 con campo iva non compilato controllo restituzione errore
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica V21
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | vat                | NULL                        |
      | paFee              | 10                          |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@ok_890  |
      | payment_creditorTaxId   | 77777777777 |
      | payment_pagoPaForm      | SI          |
      | apply_cost_pagopa       | SI          |
      | payment_multy_number    | 1           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata dal "Comune_Multi" dalla "V21"
    Then l'operazione ha prodotto un errore con status code "400"
    Then viene cancellata la posizione debitoria di "Mario Gherkin"

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_5] Invio notifica 890 SYNC DELIVERY_MODE con 1 F24  con la V21 con campo iva non compilato controllo restituzione errore
    Given viene generata una nuova notifica V21
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | NULL                        |
      | paFee                 | 10                          |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via@ok_890                    |
      | payment_f24             | PAYMENT_F24_STANDARD_0        |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata dal "Comune_Multi" dalla "V21"
    Then l'operazione ha prodotto un errore con status code "400"

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_6] Invio notifica 890 ASYNC DELIVERY_MODE con la V21 con campo paFee non compilato controllo restituzione errore
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica V21
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | vat                | 10                          |
      | paFee              | NULL                        |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | Via@ok_890  |
      | payment_creditorTaxId   | 77777777777 |
      | payment_pagoPaForm      | SI          |
      | apply_cost_pagopa       | SI          |
      | payment_multy_number    | 1           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata dal "Comune_Multi" dalla "V21"
    Then l'operazione ha prodotto un errore con status code "400"
    Then viene cancellata la posizione debitoria di "Mario Gherkin"

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_7] Invio notifica 890 SYNC DELIVERY_MODE con 1 F24 con la V21 con campo paFee non compilato controllo restituzione errore
    Given viene generata una nuova notifica V21
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | NULL                        |
      | paFee                 | 10                          |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL                          |
      | physicalAddress_address | Via@ok_890                    |
      | payment_f24             | PAYMENT_F24_STANDARD_0        |
      | title_payment           | F24_STANDARD_CLMCST42R12D969Z |
      | apply_cost_f24          | SI                            |
      | payment_multy_number    | 1                             |
    When la notifica viene inviata dal "Comune_Multi" dalla "V21"
    Then l'operazione ha prodotto un errore con status code "400"

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_8] Invio notifica 890 SYNC DELIVERY_MODE con la V21 con campo paFee non compilato controllo default
    Given viene generata una nuova notifica V21
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 11                          |
      | paFee                 | NULL                        |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V21"
    Then viene verificato che il campo "paFee" sia valorizzato a 100

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_9] Invio notifica 890 SYNC DELIVERY_MODE con la V21 con campo vat non compilato controllo default
    Given viene generata una nuova notifica V21
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | NULL                        |
      | paFee                 | 20                          |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
      | apply_cost_pagopa       | SI         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V21"
    Then viene verificato che il campo "vat" sia valorizzato a 22

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_10] Invio notifica 890 SYNC FLAT_RATE con la V21 con campo vat non compilato controllo default
    Given viene generata una nuova notifica V21
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | FLAT_RATE                   |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | NULL                        |
      | paFee                 | 10                          |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V21"
    Then viene verificato che il campo "vat" sia valorizzato a 22

  @partitaIva
  Scenario: [PARTITA-IVA_V21-V23_11] Invio notifica 890 SYNC FLAT_RATE con la V21 con campo paFee non compilato controllo default
    Given viene generata una nuova notifica V21
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | FLAT_RATE                   |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 12                          |
      | paFee                 | NULL                        |
    And destinatario Mario Gherkin V21 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V21"
    Then viene verificato che il campo "vat" sia valorizzato a 22

  @partitaIva
  Scenario: [PARTITA-IVA_V1-V23_1] Invio notifica 890 SYNC DELIVERY_MODE con la V23 controllo costo con la V1
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then vengono verificati costo = "963" e data di perfezionamento della notifica "V1"

  @partitaIva
  Scenario: [PARTITA-IVA_V1-V23_2] Invio notifica 890 SYNC FLAT_RATE con la V1 controllo costo a 0 con la V23
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | FLAT_RATE                   |
      | physicalCommunication | REGISTERED_LETTER_890       |
      | vat                   | 10                          |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then vengono verificati costo = "0" e data di perfezionamento della notifica "V1"

  @partitaIva
  Scenario: [PARTITA-IVA_V1-V23_3] Invio notifica 890 SYNC DELIVERY_MODE con la V1 controllo costo con la V23
    Given viene generata una nuova notifica V1
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Gherkin V1 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V1"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo "parziale" di una notifica "890" del utente "0"
    And viene verificato il costo "totale" di una notifica "890" del utente "0"


  @partitaIva
  Scenario: [PARTITA-IVA_V1-V23_4] Invio notifica 890 SYNC FLAT_RATE con la V1 controllo costo a 0 con la V23
    Given viene generata una nuova notifica V1
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | FLAT_RATE                   |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Mario Gherkin V1 e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V1"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo "parziale" di una notifica "890" del utente "0"
    And viene verificato il costo "totale" di una notifica "890" del utente "0"
