Feature: test per la bs di radd


  Scenario: [BS-ACT-1] invio notifica BS-ACT-1
    Given viene generata una nuova notifica
      | subject            | BS-ACT-1_CAF-CNA|
      | abstract           | NULL            |
      | senderDenomination | PagoPa S.p.A.   |
      | senderTaxId        | 15376371009     |
      | document           | DOC_BS          |
      | taxonomyCode       | 070101P         |
    And destinatario
      | denomination                      | Nome Destinatario           |
      | taxId                             | FRMTTR76M06B715E            |
      | digitalDomicile_address           | test@pec.it                 |
      | physicalAddress_address           | via bologna 7               |
      | physicalAddress_municipality      | Bologna                     |
      |physicalAddress_municipalityDetails| NULL                        |
      | at                                | NULL                        |
      |physicalAddress_addressDetails     | NULL                        |
      |physicalAddress_province           | BO                          |
      |physicalAddress_State              | ITALIA                      |
      |physicalAddress_zip                | 40069                       |
      |payment_creditorTaxId              | 15376371009                 |
      |payment_pagoPaForm                 | SI                          |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED


  Scenario: [BS-AOR-1] invio notifica BS-AOR-1
    Given viene generata una nuova notifica
      | subject            | BS-AOR-1_CAF-CNA|
      | abstract           | NULL            |
      | senderDenomination | PagoPa S.p.A.   |
      | senderTaxId        | 15376371009     |
      | document           | DOC_BS          |
      | taxonomyCode       | 070101P         |
    And destinatario
      | denomination                      | Nome Destinatario           |
      | taxId                             | FRMTTR76M06B715E            |
      | digitalDomicile_address           | test@pec.opibo.it           |
      | physicalAddress_address           | via bologna 7               |
      | physicalAddress_municipality      | Bologna                     |
      |physicalAddress_municipalityDetails| NULL                        |
      | at                                | NULL                        |
      |physicalAddress_addressDetails     | NULL                        |
      |physicalAddress_province           | BO                          |
      |physicalAddress_State              | ITALIA                      |
      |physicalAddress_zip                | 40069                       |
      |payment_creditorTaxId              | 15376371009                 |
      |payment                            | NULL                          |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED




  Scenario: [BS-ACT-2] invio notifica BS-ACT-2
    Given viene generata una nuova notifica
      | subject            | BS-ACT-2_CAF-CNA|
      | abstract           | NULL            |
      | senderDenomination | PagoPa S.p.A.   |
      | senderTaxId        | 15376371009     |
      | document           | DOC_BS;DOC_BS2  |
      | taxonomyCode       | 070101P         |
    And destinatario
      | denomination                      | Nome Destinatario           |
      | taxId                             | FRMTTR76M06B715E            |
      | digitalDomicile_address           | test@pec.it                 |
      | physicalAddress_address           | via roma 7                  |
      | physicalAddress_municipality      | Roma                        |
      |physicalAddress_municipalityDetails| NULL                        |
      | at                                | NULL                        |
      |physicalAddress_addressDetails     | NULL                        |
      |physicalAddress_province           | RM                          |
      |physicalAddress_State              | ITALIA                      |
      |physicalAddress_zip                | 00198                       |
      |payment_creditorTaxId              | 15376371009                 |
      |payment_pagoPaForm                 | SI                          |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED


  Scenario: [BS-AOR-2] invio notifica BS-AOR-2
    Given viene generata una nuova notifica
      | subject            | BS-AOR-2_CAF-CNA|
      | abstract           | NULL            |
      | senderDenomination | PagoPa S.p.A.   |
      | senderTaxId        | 15376371009     |
      | document           | DOC_BS          |
      | taxonomyCode       | 070101P         |
    And destinatario
      | denomination                      | Nome Destinatario           |
      | taxId                             | FRMTTR76M06B715E            |
      | digitalDomicile_address           | test@pec.it                 |
      | physicalAddress_address           | via roma 7                  |
      | physicalAddress_municipality      | Roma                        |
      |physicalAddress_municipalityDetails| NULL                        |
      | at                                | NULL                        |
      |physicalAddress_addressDetails     | NULL                        |
      |physicalAddress_province           | RM                          |
      |physicalAddress_State              | ITALIA                      |
      |physicalAddress_zip                | 00198                       |
      |payment_creditorTaxId              | 15376371009                 |
      |payment                            | NULL                        |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED



  Scenario: [BS-ACT-3] invio notifica BS-ACT-3
    Given viene generata una nuova notifica
      | subject            | BS-ACT-3_CAF-CNA|
      | abstract           | NULL            |
      | senderDenomination | PagoPa S.p.A.   |
      | senderTaxId        | 15376371009     |
      | document           | DOC_BS          |
      | feePolicy          | DELIVERY_MODE   |
      | taxonomyCode       | 070101P         |
    And destinatario
      | denomination                      | PagoPA S.p.A                |
      | taxId                             | 15376371009                 |
      | digitalDomicile                   | NULL                        |
      | recipientType                     | PG                          |
      | physicalAddress_address           | Piazza Colonna 370          |
      | physicalAddress_municipality      | Roma                        |
      |physicalAddress_municipalityDetails| NULL                        |
      | at                                | NULL                        |
      |physicalAddress_addressDetails     | NULL                        |
      |physicalAddress_province           | RM                          |
      |physicalAddress_State              | ITALIA                      |
      |physicalAddress_zip                | 00187                       |
      |payment_creditorTaxId              | 15376371009                 |
      |payment_pagoPaForm                 | NO                          |
      | payment_f24                       | PAYMENT_F24_STANDARD_PG     |
      | apply_cost_f24                    | SI                          |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED





    Scenario: Cancellazione notifica
      Given viene cancellata la notifica con IUN "lll"
