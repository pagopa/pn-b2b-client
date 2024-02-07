Feature: controllo costo notifiche con IVA


  Scenario: [PARTITA-IVA_V21-V23_1] Invio notifica 890 SYNC con iva inclusa controllo costo
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
    And viene verificato il costo "totale" di una notifica "890" del utente "0"


  Scenario: [PARTITA-IVA_V23-V1_1] Invio notifica 890 SYNC con la V1 controllo costo con la V23
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED con la versione "V1"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo = "100" della notifica

  Scenario: [PARTITA-IVA_V23-V1_2] Invio notifica 890 SYNC FLAT_RATE con la V1 controllo costo a 0 con la V23
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


  Scenario: [PARTITA-IVA_V1-V23_1] Invio notifica 890 SYNC con la V1 controllo costo con la V23
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

  Scenario: [PARTITA-IVA_V1-V23_2] Invio notifica 890 SYNC FLAT_RATE con la V1 controllo costo a 0 con la V23
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