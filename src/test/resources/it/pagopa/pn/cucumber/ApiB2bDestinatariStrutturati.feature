Feature: Api b2b per destinatari strutturati

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-1] Viene invocata l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di tutti gli enti
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "CucumberSpa"
    Given "Mario Gherkin" viene delegato da "CucumberSpa"
    And "Mario Gherkin" accetta la delega "CucumberSpa"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-2] Viene invocata l’API di creazione delega da delegante PG a delegato PG con l’accesso alle notifiche da parte di tutti gli enti
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    Given "GherkinSrl" viene delegato da "CucumberSpa"
    And "GherkinSrl" accetta la delega "CucumberSpa"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "GherkinSrl" con delega

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-3] Viene invocata l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di enti specifici
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "Mario Gherkin" viene delegato da "CucumberSpa" per comune "Comune_1"
    And "Mario Gherkin" accetta la delega "CucumberSpa"
    Given viene generata una nuova notifica
      | subject            | invio notifica CucumberSpa |
      | senderDenomination | Comune di Milano          |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-4] Viene invocata l’API di creazione delega da delegante PG a delegato PG con l’accesso alle notifiche da parte di enti specifici
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "GherkinSrl" viene delegato da "CucumberSpa" per comune "Comune_1"
    And "GherkinSrl" accetta la delega "CucumberSpa"
    Given viene generata una nuova notifica
      | subject            | invio notifica CucumberSpa |
      | senderDenomination | Comune di Milano          |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "GherkinSrl" con delega

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario Outline: [Scenario-5-6-9-10-11-12] Viene invocata l’API di creazione delega da delegante PG a delegato PF con campi non conformi
    And viene creata una delega con i seguenti parametri errati:
      | delegator | CucumberSpa |
      | delegate  | <DELEGATE> |
    Then si verifica che lo status code sia: 400
    Examples:
      | DELEGATE |
      | EMPTY_FISCAL_CODE |
      | INVALID_FISCAL_CODE |
      | EMPTY_FIRST_NAME |
      | EMPTY_LAST_NAME |
      | FIRST_NAME_NOT_VALID |
      | LAST_NAME_NOT_VALID |
#      | EMPTY_DISPLAY_NAME |
      | DISPLAY_NAME_NOT_VALID |


  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-8] Viene invocata l’API di creazione delega da delegante PG a delegato PF con data di fine delega antecedente a quella di inizio
    And viene creata una delega con i seguenti parametri errati:
      | delegator | CucumberSpa    |
      | dateFrom  | TODAY         |
      | dateTo    | PAST_DATE     |
    Then si verifica che lo status code sia: 400

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-7] Viene invocata l’API di creazione delega da delegante PG a delegato PF con data di fine delega formalmente non valida
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "CucumberSpa"
    And viene creata una delega con i seguenti parametri errati:
      | delegator | CucumberSpa      |
      | dateFrom  | TODAY           |
      | dateTo    | INVALID_FORMAT  |
    Then si verifica che lo status code sia: 400

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-13] Viene invocata l’API di creazione delega da delegante PG a se stesso
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "CucumberSpa"
    Given "CucumberSpa" viene delegato da "CucumberSpa"

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-14] Viene invocata l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di tutti gli enti
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "Mario Gherkin" viene delegato da "CucumberSpa"
    And "Mario Gherkin" accetta la delega "CucumberSpa"
    And "Mario Gherkin" viene delegato da "CucumberSpa"
    Then si verifica che lo status code sia: 409

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [B2B_MANDATE_DEST_20] associazione ad un gruppo di una delega già accetta da parte di un delegato persona giuridica
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "GherkinSrl" viene delegato da "CucumberSpa"
    Then "GherkinSrl" accetta la delega "CucumberSpa" associando un gruppo

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [B2B_MANDATE_DEST_21] associazione ad un gruppo di una delega già accetta da parte di un delegato persona giuridica
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "GherkinSrl" viene delegato da "CucumberSpa"
    And "GherkinSrl" accetta la delega "CucumberSpa"
#    Then "CucumberSpa" visualizza le deleghe
    And come amministratore "CucumberSpa" associa alla delega il primo gruppo disponibile attivo per il delegato "GherkinSrl"
    And come delegante "CucumberSpa" l'associazione a gruppi sulla delega di "GherkinSrl"

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-24] Viene invocata l’API di visualizzazione elenco deleghe ricevute dai deleganti
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    And "GherkinSrl" viene delegato da "CucumberSpa"
    And "GherkinSrl" accetta la delega "CucumberSpa"
    Then il delegato "GherkinSrl" visualizza le deleghe a suo carico

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-25] Viene invocata l’API di rifiuto di una delega da parte di un delegato prima dell'accettazione
    Given "Mario Gherkin" viene delegato da "CucumberSpa"
    Then "Mario Gherkin" rifiuta se presente la delega ricevuta "CucumberSpa"

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-26] Viene invocata l’API di rifiuto di una delega da parte di un delegato dopo l'accettazione
    Given "Mario Gherkin" viene delegato da "CucumberSpa"
    And "Mario Gherkin" accetta la delega "CucumberSpa"
    Then "Mario Gherkin" rifiuta se presente la delega ricevuta "CucumberSpa"

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-27] Viene invocata l’API di ricerca delegante tramite CF
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    Given "GherkinSrl" viene delegato da "CucumberSpa"
    And "GherkinSrl" accetta la delega "CucumberSpa"
    Then il delegato "GherkinSrl" visualizza le deleghe da parte di un delegante con CF: "20517490320"

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-29] Viene invocata l’API di ricerca delegante tramite stato della delega
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    Given "GherkinSrl" viene delegato da "CucumberSpa"
    Then il delegato "CucumberSpa" visualizza le deleghe da parte di "" in stato "PENDING"

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-30] Viene invocata l’API di ricerca delegante con codice fiscale errato
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    Given "GherkinSrl" viene delegato da "CucumberSpa"
    And il delegato "GherkinSrl" visualizza le deleghe da parte di "Utente errato" in stato ""
    Then si verifica che lo status code sia: 400

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [Scenario-32] Viene invocata l’API di ricerca delegante con codice fiscale errato
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "CucumberSpa"
    Given "GherkinSrl" viene delegato da "CucumberSpa"
    And il delegato "GherkinSrl" visualizza le deleghe da parte di "" in stato "PENDINGSSS"
    Then si verifica che lo status code sia: 400

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [B2B_MANDATE_DEST_34] Invocare l’API di visualizzazione dell’elenco delle notifiche
    Given "GherkinSrl" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate     | 01/01/2030 |
      | endDate       | 01/10/2022 |
    And si verifica che lo status code sia: 500
    And "GherkinSrl" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | status        | ACCEPTED   |
    And "GherkinSrl" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | iunMatch      |  VDKD-YVDR-XXXX-202409-X-9  |
    And Si verifica che il numero di notifiche restituite nella pagina sia 0

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [B2B_MANDATE_DEST_35] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere correttamente recuperato da "CucumberSpa" con delega

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [B2B_MANDATE_DEST_36] Invocare l’API di visualizzazione dell’elenco delle notifiche con delega
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Then "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      |  |  |
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | size          |    -1      |
    And si verifica che lo status code sia: 400
    #size = -1 -> size = null
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | status        | ACCEPTED   |
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | subjectRegExp | adsdasdasdasdasdasdasdasdasdasdasdas   |
    And Si verifica che il numero di notifiche restituite nella pagina sia 0
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | iunMatch      |  VDKD-YVDR-XXXX-202409-X-9  |
    And Si verifica che il numero di notifiche restituite nella pagina sia 0

  @useB2B @deleghe1 @b2bDestinatariStrutturati
  Scenario: [B2B_MANDATE_DEST_37] Invio notifica digitale mono destinatario e recupero allegato pagopa_scenario positivo
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl e:
      | payment_pagoPaForm | SI               |
      | payment_f24        | PAYMENT_F24_FLAT |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then l'allegato "PAGOPA" può essere correttamente recuperato da "CucumberSpa" con delega

  @userAttributes  @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-DEST-USER-ATTR_2] inserimento pec errato
    Given si predispone addressbook per l'utente "CucumberSpa"
    When viene richiesto l'inserimento della pec "test@test@fail.@"
    Then l'inserimento ha prodotto un errore con status code "400"

  @userAttributes  @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-DEST-USER-ATTR_3] inserimento telefono errato
    Given si predispone addressbook per l'utente "CucumberSpa"
    When viene richiesto l'inserimento del numero di telefono "+0013894516888"
    Then l'inserimento ha prodotto un errore con status code "400"

  @userAttributes  @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-DEST-USER-ATTR_4] inserimento pec non da errore
    Given si predispone addressbook per l'utente "CucumberSpa"
    When viene richiesto l'inserimento della pec "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{|}~-@gmail.com"

  @userAttributes  @useB2B @b2bDestinatariStrutturati
  Scenario Outline: [B2B-DEST-USER-ATTR_5] inserimento pec errato 250 caratteri
    Given si predispone addressbook per l'utente "CucumberSpa"
    When viene richiesto l'inserimento della pec "<pec>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | pec                                                                                                                                                                                                                                                                                  |
      | emailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibilecontienemolticarattericontienemolticarattericontienemolticarattericontienemolticarattericontienemolticaratteri@gmail.com |

  @userAttributes  @useB2B @b2bDestinatariStrutturati
  Scenario Outline: [B2B-DEST-USER-ATTR_6] inserimento pec errato con caratteri speciali
    Given si predispone addressbook per l'utente "CucumberSpa"
    When viene richiesto l'inserimento della pec "<pec>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | pec                                                                                                          |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;,@gmail.com |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;@gmail.com  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;@gmail.com  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\@gmail.com   |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><@gmail.com    |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°>@gmail.com     |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°@gmail.com      |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]@gmail.com       |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[,@gmail.com       |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§@gmail.com         |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù@gmail.com          |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçò@gmail.com           |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéç@gmail.com            |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèé@gmail.com             |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàè@gmail.com              |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìà@gmail.com               |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ì@gmail.com                |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()@gmail.com                 |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£(@gmail.com                  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£@gmail.com                   |

  @userAttributes  @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-DEST-USER-ATTR_7] inserimento email di cortesia non da errore
    Given si predispone addressbook per l'utente "GherkinSrl"
    When viene richiesto l'inserimento del email di cortesia "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{|}~-@gmail.com"

  @userAttributes  @useB2B @b2bDestinatariStrutturati
  Scenario Outline: [B2B-DEST-USER-ATTR_8] inserimento email di cortesia errato 250 caratteri
    Given si predispone addressbook per l'utente "CucumberSpa"
    When viene richiesto l'inserimento del email di cortesia "<email>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | email                                                                                                                                                                                                                                                                                  |
      | emailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibilecontienemolticarattericontienemolticarattericontienemolticarattericontienemolticarattericontienemolticaratteri@gmail.com |

  @userAttributes  @useB2B @b2bDestinatariStrutturati
  Scenario Outline: [B2B-DEST-USER-ATTR_9] inserimento email di cortesia errato con caratteri speciali
    Given si predispone addressbook per l'utente "CucumberSpa"
    When viene richiesto l'inserimento del email di cortesia "<email>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | email                                                                                                          |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;,@gmail.com |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;@gmail.com  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ì@gmail.com                |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()@gmail.com                 |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£(@gmail.com                  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£@gmail.com                   |

  @addressBook1 @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-DEST-USER-ATTR_10] invio messaggio di cortesia - invio notifica per email per ente padre per PG
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene verificata la presenza di qualunque tipo di recapito inserito per l'utente "Lucio Anneo Seneca"
    And viene rimossa se presente la pec di piattaforma di "Lucio Anneo Seneca"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_Root"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "Comune_Root"
    And viene verificata la presenza di 2 recapiti di cortesia inseriti per l'utente "Lucio Anneo Seneca"
    And viene cancellata l'email di cortesia per il comune "Comune_Root"

  @legalFact @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-RECIPIENT_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then "CucumberSpa" richiede il download dell'attestazione opponibile "SENDER_ACK"

  @legalFact @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-RECIPIENT_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then "CucumberSpa" richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @legalFact @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-RECIPIENT_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then "CucumberSpa" richiede il download dell'attestazione opponibile "PEC_RECEIPT"

  @legalFact @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-RECIPIENT_LEGALFACT_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "CucumberSpa" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then "CucumberSpa" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @useB2B @b2bDestinatariStrutturati
  Scenario: [B2B-DEST-DELIVERY_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "CucumberSpa"
