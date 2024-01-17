Feature: invio notifiche b2b con analisi documenti allegati

  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_1] notifica con un allegato di pagamento inviata da PA “abilitata” (Esito: refined)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_2] notifica con 3 documenti inviata da PA “abilitata” (Esito: refined)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_2_PG;DOC_2_PG;DOC_2_PG  |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_3] notifica con un documento con più di 5 pagine da PA “abilitata” (Esito: refined)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_6_PG                    |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_4] da PA non elencata in abilitazione -  documento senza allegati di pagamento e un solo documento di 3 pagine (Esito: refined)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_3_PG                    |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_5] da PA non elencata in abilitazione -  documento senza allegati di pagamento e solo due soli documenti uno di una pagina e uno di due pagine (Esito: refined)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_1_PG;DOC_2_PG           |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_6] da PA non elencata in abilitazione -  un allegato di pagamento e un documento di due pagine (Esito: refused)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_2_PG                    |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm | SI |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_7] da PA non elencata in abilitazione -  nessuno allegato di pagamento e un documento di 8 pagine (Esito: refused)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_8_PG                    |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_8] da PA non elencata in abilitazione -  nessuno allegato di pagamento e tre documenti di 2 pagine ognuno (Esito: refused)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_2_PG;DOC_2_PG;DOC_2_PG  |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    Then la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED

  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_9] da PA non elencata in abilitazione -  nessun allegato di pagamento e due documenti: uno da 3 pagine e uno da 1 pagina inviata da PA “disabilitata” (Esito: refused)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_3_PG;DOC_1_PG           |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    Then la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_10] da PA non elencata in abilitazione -  nessuno allegato di pagamento e 2 documenti di 2 pagine ognuno (Esito: refined)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_2_PG;DOC_2_PG           |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_11] da PA non elencata in abilitazione -  nessuno allegato di pagamento e un documento di 4 pagine (Esito: refined)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_4_PG                    |
    And destinatario Mario Cucumber e:
      | payment | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_12] da PA non elencata in abilitazione -  con allegato di pagamento e F24 e un documento di 8 pagine (Esito: refused)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
      | document           | DOC_8_PG                    |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24          | PAYMENT_F24_SIMPLIFIED          |
      | title_payment        | F24_SIMPLIFIED_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_13] da PA non elencata in abilitazione -  senza allegato di pagamento e F24 e un documento di 8 pagine (Esito: refused)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
      | document           | DOC_8_PG                    |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NOALLEGATO                    |
      | payment_f24          | PAYMENT_F24_SIMPLIFIED          |
      | title_payment        | F24_SIMPLIFIED_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_14] da PA non elencata in abilitazione -  senza allegato di pagamento e F24 e un documento di 8 pagine (Esito: refused)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
      | document           | DOC_8_PG                    |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NOALLEGATO                    |
      | payment_f24          | PAYMENT_F24_SIMPLIFIED          |
      | title_payment        | F24_SIMPLIFIED_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_15] da PA non elencata in abilitazione -  con allegato di pagamento e F24 e un documento di 4 pagine (Esito: refused)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
      | document           | DOC_4_PG                    |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | SI                            |
      | payment_f24          | PAYMENT_F24_SIMPLIFIED          |
      | title_payment        | F24_SIMPLIFIED_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED

  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_16] da PA non elencata in abilitazione -  senza allegato di pagamento e F24 e un documento di 4 pagine (Esito: refused)
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
      | document           | DOC_4_PG                    |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NOALLEGATO                    |
      | payment_f24          | PAYMENT_F24_SIMPLIFIED        |
      | title_payment        | F24_SIMPLIFIED_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED

#---------------------------PN-9589--------------------------------
  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_17] da PA non elencata in abilitazione - con avviso PagoPA    senza allegato di pagamento e un documento di 3 pagine (Esito: refined) PN-9589
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_3_PG                    |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NOALLEGATO |
      | apply_cost_pagopa    | SI         |
      | payment_multy_number | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_18] da PA non elencata in abilitazione - con avviso PagoPA  senza allegato di pagamento e un documento di 4 pagine (Esito: refined) PN-9589
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_4_PG                    |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NOALLEGATO |
      | apply_cost_pagopa    | SI         |
      | payment_multy_number | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_19] da PA non elencata in abilitazione - con avviso PagoPA  senza allegato di pagamento e 2 documenti di 2 pagine ciascuno (Esito: refined) PN-9589
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_2_PG;DOC_2_PG           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NOALLEGATO |
      | apply_cost_pagopa    | SI         |
      | payment_multy_number | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_20] da PA non elencata in abilitazione - con avviso PagoPA  senza allegato di pagamento e un documento di 5 pagine (Esito: refused) PN-9589
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_5_PG                    |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NOALLEGATO |
      | apply_cost_pagopa    | SI         |
      | payment_multy_number | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED

  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_21] da PA non elencata in abilitazione - con avviso PagoPA  senza allegato di pagamento e 2 documenti uno di 2 pagine e uno di 3 pagine (Esito: refused) PN-9589
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | document           | DOC_2_PG;DOC_3_PG           |
      | feePolicy          | DELIVERY_MODE               |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NOALLEGATO |
      | apply_cost_pagopa    | SI         |
      | payment_multy_number | 1          |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED


  @20Grammi
  Scenario: [B2B-PA-SEND_PRELOAD_22] da PA non elencata in abilitazione -  senza allegato di pagamento e F24 e un documento di 2 pagine (Esito: refused) PN-9589
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
      | document           | DOC_2_PG                    |
    And destinatario Mario Cucumber e:
      | payment_pagoPaForm   | NOALLEGATO                    |
      | payment_f24          | PAYMENT_F24_SIMPLIFIED        |
      | title_payment        | F24_SIMPLIFIED_CLMCST42R12D969Z |
      | apply_cost_pagopa    | SI                            |
      | apply_cost_f24       | SI                            |
      | payment_multy_number | 1                             |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi REFUSED
    Then verifica che la notifica inviata tramite api b2b dal "Comune_2" non diventi ACCEPTED


    


