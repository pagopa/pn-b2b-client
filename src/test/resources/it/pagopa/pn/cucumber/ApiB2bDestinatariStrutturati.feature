Feature: Api b2b per destinatari strutturati

  @useB2B @deleghe1
  Scenario: [Scenario-1] Viene invocata l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di tutti gli enti
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "Mario Gherkin" viene delegato da "GherkinSrl"
    And "Mario Gherkin" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "Mario Gherkin" con delega

  @useB2B @deleghe1
  Scenario: [Scenario-2] Viene invocata l’API di creazione delega da delegante PG a delegato PG con l’accesso alle notifiche da parte di tutti gli enti
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega

  @useB2B @deleghe1
  Scenario: [Scenario-3] Viene invocata l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di enti specifici
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "Mario Gherkin" viene delegato da "GherkinSrl" per comune "Comune_1"
    And "Mario Gherkin" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GherkinSrl |
      | senderDenomination | Comune di Milano          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED

  @useB2B @deleghe1
  Scenario: [Scenario-4] Viene invocata l’API di creazione delega da delegante PG a delegato PG con l’accesso alle notifiche da parte di enti specifici
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl" per comune "Comune_1"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Given viene generata una nuova notifica
      | subject            | invio notifica GherkinSrl |
      | senderDenomination | Comune di Milano          |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente letta da "CucumberSpa" con delega

  @useB2B @deleghe1
  Scenario Outline: [Scenario-5-6-9-10-11-12] Viene invocata l’API di creazione delega da delegante PG a delegato PF con campi non conformi
    Given viene creato un user con i seguenti valori:
      | displayName | <DISPLAYNAME> |
      | firstName   | <FIRSTNAME>   |
      | lastName    | <LASTNAME>    |
      | fiscalCode  | <FISCALCODE>  |
    And "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "Mario Gherkin" viene delegato da "GherkinSrl"
    Then si verifica che lo status code sia: 400
    Examples:
      | DISPLAYNAME                                                                  | FIRSTNAME                                                                    | LASTNAME                                                                     | FISCALCODE        |
      | Mario Gherkin                                                                | Mario                                                                        | Gherkin                                                                      | " "               |
      | Mario Gherkin                                                                | Mario                                                                        | Gherkin                                                                      | CLMC0T42R12D969Z5 |
      | Mario Gherkin                                                                | ""                                                                           | Gherkin                                                                      | CLMCST42R12D969Z  |
      | Mario Gherkin                                                                | Mario                                                                        | ""                                                                           | CLMCST42R12D969Z  |
      | Mario Gherkin                                                                | ""                                                                           | ""                                                                           | CLMCST42R12D969Z  |
      | Mario Gherkin                                                                | Mario                                                                        | PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP | CLMCST42R12D969Z  |
      | Mario Gherkin                                                                | PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP | Gherkin                                                                      | CLMCST42R12D969Z  |
      | ""                                                                           | Mario                                                                        | Gherkin                                                                      | CLMCST42R12D969Z  |
      | PippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoPippoP | Mario                                                                        | Gherkin                                                                      | CLMCST42R12D969Z  |


  @useB2B @deleghe1
  Scenario: [Scenario-8] Viene invocata l’API di creazione delega da delegante PG a delegato PF con data di fine delega antecedente a quella di inizio
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "Mario Gherkin" viene delegato da "GherkinSrl" con data di fine delega antecedente a quella di inizio
    Then si verifica che lo status code sia: 400

  @useB2B @deleghe1
  Scenario: [Scenario-7] Viene invocata l’API di creazione delega da delegante PG a delegato PF con data di fine delega antecedente a quella di inizio
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "Mario Gherkin" viene delegato da "GherkinSrl" con data di fine delega antecedente a quella di inizio
    Then si verifica che lo status code sia: 400

  @useB2B @deleghe1
  Scenario: [Scenario-13] Viene invocata l’API di creazione delega da delegante PG a se stesso
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "GherkinSrl" viene delegato da "GherkinSrl"

  @useB2B @deleghe1
  Scenario: [Scenario-14] Viene invocata l’API di creazione delega da delegante PG a delegato PF con l’accesso alle notifiche da parte di tutti gli enti
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "Mario Gherkin" viene delegato da "GherkinSrl"
    And "Mario Gherkin" accetta la delega "GherkinSrl"
    And "Mario Gherkin" viene delegato da "GherkinSrl"
    Then si verifica che lo status code sia: 400

  @useB2B @deleghe1
  Scenario: [B2B_MANDATE_DEST_20] associazione ad un gruppo di una delega già accetta da parte di un delegato persona giuridica
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    Then "CucumberSpa" accetta la delega "GherkinSrl" associando un gruppo

  @useB2B @deleghe1
  Scenario: [B2B_MANDATE_DEST_21] associazione ad un gruppo di una delega già accetta da parte di un delegato persona giuridica
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Then "GherkinSrl" visualizza le deleghe
    And come amministratore "GherkinSrl" associa alla delega il primo gruppo disponibile attivo per il delegato "CucumberSpa"
    And come delegante "GherkinSrl" l'associazione a gruppi sulla delega di "CucumberSpa"

  @useB2B @deleghe1
  Scenario: [Scenario-24] Viene invocata l’API di visualizzazione elenco deleghe ricevute dai deleganti
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Then il delegato "CucumberSpa" visualizza le deleghe a suo carico

  @useB2B @deleghe1
  Scenario: [Scenario-25] Viene invocata l’API di rifiuto di una delega da parte di un delegato prima dell'accettazione
    Given "Mario Gherkin" viene delegato da "GherkinSrl"
    Then "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"

  @useB2B @deleghe1
  Scenario: [Scenario-26] Viene invocata l’API di rifiuto di una delega da parte di un delegato dopo l'accettazione
    Given "Mario Gherkin" viene delegato da "GherkinSrl"
    And "Mario Gherkin" accetta la delega "GherkinSrl"
    Then "Mario Gherkin" rifiuta se presente la delega ricevuta "GherkinSrl"

  @useB2B @deleghe1
  Scenario: [Scenario-27] Viene invocata l’API di ricerca delegante tramite CF
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Then il delegato "CucumberSpa" visualizza le deleghe da parte di un delegante con CF: "12666810299"

  @useB2B @deleghe1
  Scenario: [Scenario-29] Viene invocata l’API di ricerca delegante tramite stato della delega
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    Then il delegato "CucumberSpa" visualizza le deleghe da parte di "" in stato "PENDING"

  @useB2B @deleghe1
  Scenario: [Scenario-30] Viene invocata l’API di ricerca delegante con codice fiscale errato
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And il delegato "CucumberSpa" visualizza le deleghe da parte di "Utente errato" in stato ""
    Then si verifica che lo status code sia: 400

  @useB2B @deleghe1
  Scenario: [Scenario-32] Viene invocata l’API di ricerca delegante con codice fiscale errato
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    Given "CucumberSpa" viene delegato da "GherkinSrl"
    And il delegato "CucumberSpa" visualizza le deleghe da parte di "" in stato "PENDINGSSS"
    Then si verifica che lo status code sia: 400

  @useB2B @deleghe1
  Scenario: [B2B_MANDATE_DEST_34] Invocare l’API di visualizzazione dell’elenco delle notifiche
    Then "CucumberSpa" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      |  |  |
    And "CucumberSpa" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate     | 01/01/2030 |
      | endDate       | 01/10/2022 |
    And si verifica che lo status code sia: 400
    And "CucumberSpa" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | status        | ACCEPTED   |
    And "CucumberSpa" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | subjectRegExp | adsdasdasdasdasdasdasdasdasdasdasdas   |
    And Si verifica che il numero di notifiche restituite nella pagina sia 0
    And "CucumberSpa" visualizza l'elenco delle notifiche per comune "Comune_Multi"
      | startDate     | 01/01/2022 |
      | endDate       | 01/10/2030 |
      | iunMatch      |  VDKD-YVDR-XXXX-202409-X-9  |
    And Si verifica che il numero di notifiche restituite nella pagina sia 0

  @useB2B @deleghe1
  Scenario: [B2B_MANDATE_DEST_35] Invio notifica digitale mono destinatario e recupero documento notificato_scenario positivo
    Given "GherkinSrl" rifiuta se presente la delega ricevuta "Mario Cucumber"
    And "GherkinSrl" viene delegato da "Mario Cucumber"
    And "GherkinSrl" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere correttamente recuperato da "GherkinSrl" con delega

  @useB2B @deleghe1
  Scenario: [B2B_MANDATE_DEST_36] Invocare l’API di visualizzazione dell’elenco delle notifiche con delega
    Given "CucumberSpa" rifiuta se presente la delega ricevuta "GherkinSrl"
    And "CucumberSpa" viene delegato da "GherkinSrl"
    And "CucumberSpa" accetta la delega "GherkinSrl"
    Then "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      |  |  |
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "GherkinSrl" per comune "Comune_Multi"
      | startDate     | 01/01/2030 |
      | endDate       | 01/10/2022 |
    And si verifica che lo status code sia: 400
    And "CucumberSpa" visualizza l'elenco delle notifiche del delegante "DeleganteErrato" per comune "Comune_Multi"
      |  |  |
    And si verifica che lo status code sia: 400
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

  @useB2B @deleghe1
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

  @userAttributes  @useB2B
  Scenario: [B2B-DEST-USER-ATTR_2] inserimento pec errato
    Given si predispone addressbook per l'utente "GherkinSrl"
    When viene richiesto l'inserimento della pec "test@test@fail.@"
    Then l'inserimento ha prodotto un errore con status code "400"

  @userAttributes  @useB2B
  Scenario: [B2B-DEST-USER-ATTR_3] inserimento telefono errato
    Given si predispone addressbook per l'utente "GherkinSrl"
    When viene richiesto l'inserimento del numero di telefono "+0013894516888"
    Then l'inserimento ha prodotto un errore con status code "400"

  @userAttributes  @useB2B
  Scenario: [B2B-DEST-USER-ATTR_4] inserimento pec non da errore
    Given si predispone addressbook per l'utente "GherkinSrl"
    When viene richiesto l'inserimento della pec "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{|}~-@gmail.com"

  @userAttributes  @useB2B
  Scenario Outline: [B2B-DEST-USER-ATTR_5] inserimento pec errato 250 caratteri
    Given si predispone addressbook per l'utente "GherkinSrl"
    When viene richiesto l'inserimento della pec "<pec>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | pec                                                                                                                                                                                                                                                                                  |
      | emailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibilecontienemolticarattericontienemolticarattericontienemolticarattericontienemolticarattericontienemolticaratteri@gmail.com |

  @userAttributes  @useB2B
  Scenario Outline: [B2B-DEST-USER-ATTR_6] inserimento pec errato con caratteri speciali
    Given si predispone addressbook per l'utente "GherkinSrl"
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

  @userAttributes  @useB2B
  Scenario: [B2B-DEST-USER-ATTR_7] inserimento email di cortesia non da errore
    Given si predispone addressbook per l'utente "GherkinSrl"
    When viene richiesto l'inserimento del email di cortesia "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{|}~-@gmail.com"

  @userAttributes  @useB2B
  Scenario Outline: [B2B-DEST-USER-ATTR_8] inserimento email di cortesia errato 250 caratteri
    Given si predispone addressbook per l'utente "GherkinSrl"
    When viene richiesto l'inserimento del email di cortesia "<email>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | email                                                                                                                                                                                                                                                                                  |
      | emailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibilecontienemolticarattericontienemolticarattericontienemolticarattericontienemolticarattericontienemolticaratteri@gmail.com |

  @userAttributes  @useB2B
  Scenario Outline: [B2B-DEST-USER-ATTR_9] inserimento email di cortesia errato con caratteri speciali
    Given si predispone addressbook per l'utente "GherkinSrl"
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

  @addressBook1
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
    And viene verificata la presenza di 4 recapiti di cortesia inseriti per l'utente "Lucio Anneo Seneca"
    And viene cancellata l'email di cortesia per il comune "Comune_Root"

  @legalFact @useB2B
  Scenario: [B2B-RECIPIENT_LEGALFACT_1] Invio notifica e download atto opponibile SENDER_ACK_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    Then "GherkinSrl" richiede il download dell'attestazione opponibile "SENDER_ACK"

  @legalFact @useB2B
  Scenario: [B2B-RECIPIENT_LEGALFACT_2] Invio notifica e download atto opponibile DIGITAL_DELIVERY_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    Then "GherkinSrl" richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"

  @legalFact @useB2B
  Scenario: [B2B-RECIPIENT_LEGALFACT_3] Invio notifica e download atto opponibile PEC_RECEIPT_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then "GherkinSrl" richiede il download dell'attestazione opponibile "PEC_RECEIPT"

  @legalFact @useB2B
  Scenario: [B2B-RECIPIENT_LEGALFACT_4] Invio notifica e download atto opponibile RECIPIENT_ACCESS_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And "GherkinSrl" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Then "GherkinSrl" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"

  @useB2B
  Scenario: [B2B-DEST-DELIVERY_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN API WEB_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario GherkinSrl
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere correttamente recuperata da "GherkinSrl"
