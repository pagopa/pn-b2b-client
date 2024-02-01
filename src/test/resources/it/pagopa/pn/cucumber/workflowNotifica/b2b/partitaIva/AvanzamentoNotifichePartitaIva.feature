Feature: controllo costo notifiche con IVA


  Scenario: [PARTITA-IVA_CONTROLLO-COSTO_1] Invio notifica RS con iva inclusa controllo costo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | vat                | 10                          |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo "totale" della notifica del utente "0" di una notifica "RS"


  Scenario: [PARTITA-IVA_CONTROLLO-COSTO_2] Invio notifica RIS con iva inclusa controllo costo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | vat                | 10                          |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | physicalAddress_State        | FRANCIA      |
      | physicalAddress_municipality | Parigi       |
      | physicalAddress_zip          | ZONE_1       |
      | physicalAddress_province     | Paris        |
      | digitalDomicile_address      | test@fail.it |
      | physicalAddress_address      | Via@ok_RIS   |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    And viene verificato il costo "totale" della notifica del utente "0" di una notifica "RIS"


  Scenario: [PARTITA-IVA_CONTROLLO-COSTO_3] Invio notifica 890 con iva inclusa controllo costo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | vat                | 10                          |
      | paFee              | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo "totale" della notifica del utente "0" di una notifica "890"


  Scenario: [PARTITA-IVA_CONTROLLO-COSTO_4] Invio notifica AR con iva inclusa controllo costo
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di milano            |
      | feePolicy             | DELIVERY_MODE               |
      | physicalCommunication | AR_REGISTERED_LETTER        |
      | vat                   | 10                          |
      | paFee                 | 100                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo "totale" della notifica del utente "0" di una notifica "AR"


  Scenario: [PARTITA-IVA_CONTROLLO-COSTO_3] Invio notifica 890 ASYNC con iva inclusa controllo costo
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | vat                | 10                          |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL        |
      | physicalAddress_address | via@ok_890  |
      | payment_creditorTaxId   | 77777777777 |
      | payment_pagoPaForm      | SI          |
      | payment_f24             | NULL        |
      | apply_cost_pagopa       | SI          |
      | payment_multy_number    | 1           |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED per controllo GPD
    And viene aggiunto il costo della notifica totale
    Then  lettura amount posizione debitoria per la notifica corrente di "Mario Gherkin"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then  lettura amount posizione debitoria per la notifica corrente di "Mario Gherkin"
    And viene effettuato il controllo del amount di GPD con il costo totale della notifica con iva inclusa
    Then viene cancellata la posizione debitoria di "Mario Gherkin"


  Scenario: [PARTITA-IVA_CONTROLLO-COSTO_3] Invio notifica RS ASYNC con iva inclusa controllo costo
    Given viene creata una nuova richiesta per istanziare una nuova posizione debitoria per l'ente creditore "77777777777" e amount "100" per "Mario Gherkin" con CF "CLMCST42R12D969Z"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | pagoPaIntMode      | ASYNC                       |
      | vat                | 10                          |
      | paFee              | 10                          |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | payment_creditorTaxId   | 77777777777  |
      | payment_pagoPaForm      | SI           |
      | payment_f24             | NULL         |
      | apply_cost_pagopa       | SI           |
      | payment_multy_number    | 1            |
    And al destinatario viene associato lo iuv creato mediante partita debitoria per "Mario Gherkin" alla posizione 0
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED per controllo GPD
    And viene aggiunto il costo della notifica totale
    Then  lettura amount posizione debitoria per la notifica corrente di "Mario Gherkin"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    Then  lettura amount posizione debitoria per la notifica corrente di "Mario Gherkin"
    And viene effettuato il controllo del amount di GPD con il costo totale della notifica con iva inclusa
    Then viene cancellata la posizione debitoria di "Mario Gherkin"