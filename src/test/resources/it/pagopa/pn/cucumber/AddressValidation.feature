Feature: address validation feature


  Scenario Outline: [B2B_ADDRESS_VALIDATION] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | <denomination> |
      |    taxId     | CLMCST42R12D969Z |
      | physicalAddress_address | <address> |
      | at | <at> |
      | physicalAddress_addressDetails | <addressDetails> |
      | physicalAddress_zip | <zip> |
      | physicalAddress_municipality | <municipality> |
      | physicalAddress_municipalityDetails | <municipalityDetails> |
      | physicalAddress_province | <province> |
      | physicalAddress_State | <foreignState> |
    Then la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi <notificationValidationStatus>
    Examples:
      | denomination  | at    | address                   | addressDetails  | zip   | municipality        | municipalityDetails | province  | foreignState  | notificationValidationStatus  |
      | MARIO OK      | NULL  | VIA DELLA COSTITUZIONE 26 | NULL            | 20837 | MONTICELLO BRIANZA  |  NULL               | LC        | ITALIA        | ACCEPTED                      |
      | MARIO KO      | NULL  | VIA DELLA COSTITUZIONE 26 | NULL            | 20837 | MONTICELLO BRIANZA  |  NULL               | LC        | ITALIA        | REFUSED                       |