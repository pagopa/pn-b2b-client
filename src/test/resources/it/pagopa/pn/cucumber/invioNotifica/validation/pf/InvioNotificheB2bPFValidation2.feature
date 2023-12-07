Feature: Validazione campi invio notifiche b2b

  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_9] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_State | <stato> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | stato                             |
      | Città d'Avorio                    |
      | Fær Øer                           |
      | São Tomé                          |
      | Hagåtña                           |
      | Zhōnghuóá Rénmín Gònghéguó        |
      | Tašād                             |
      | Jumhūriyyat Tašād                 |
      | Κύπρος                            |
      | Κυπριακή Δημοκρατία Kypros        |
      | Ittihād al-Qumur                  |
      | Chosŏn Minjujuŭi Inmin Konghwaguk |
      | Répúblique de Côte d`Ivoire       |
      | Iritriyā                          |
      | République d'Haïti                |
      | Lýðveldið Ísland                  |
      | Poblacht na hÉireann              |
      | Īrān                              |
      | Bhārat Ganarājya                  |
      | Lībiyā                            |
      | Mfùko la Malaŵi                   |
      | Moçambique                        |
      | Mūrītāniyā                        |
      | Têta Paraguái                     |
      | Česká republika                   |
      | Mālo Tuto’atasi o Sāmoa           |

  @testLite
  Scenario Outline: [B2B-PA-SEND_VALID_9_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_State | <stato> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | stato                                                                                                            |
      | Costa d'Avorio                                                                                                   |
      | Spagna                                                                                                           |
      | Italia                                                                                                           |


  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <indirizzo> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | indirizzo      |
      | via dà         |
      | via dell'adige |
      | via sull’adige |
      | via sull`adige |
      | via è          |
      | via ì          |
      | via ò          |


  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_10_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_address | <indirizzo> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | indirizzo                                    |
      | via dà via è via ì via ò                     |
      | via dell'adige via sull’adige via sull`adige |


  Scenario Outline: [B2B-PA-SEND_VALID_11] invio notifiche digitali mono destinatario con parametri tax_id errati_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | taxId | <taxId> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | taxId             |
      | 1000000000        |
      | 17000000000000000 |
      | FRMTTR76M06B715   |
      | FRMTTRZ6M06B715E  |
      | FRMTTR76M0YB715E  |
      | FRMTTR76M06B7W5E  |
      | 20517460320       |
      #1) 10 numeri (min 11)
      #2) 17 numeri (max 16)
      #3) CF non valido (lettera finale mancante)
      #4) Lettera omocodia non contemplata (primi 2 numeri)
      #5) Lettera omocodia non contemplata (seconda serie di 2 numeri)
      #6) Lettera omocodia non contemplata (serie di 3 numeri finale)
      #7) CF solo numerico

  Scenario Outline: [B2B-PA-SEND_VALID_12] invio notifiche digitali mono destinatario con parametri denomination errati_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | <denomination> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination |
      | 0_CHAR       |
      | 81_CHAR      |


  Scenario Outline: [B2B-PA-SEND_VALID_13] invio notifiche digitali mono destinatario con parametri senderDenomination errati_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | <denomination>              |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination |
      | 0_CHAR       |
      | 81_CHAR      |


  Scenario Outline: [B2B-PA-SEND_VALID_14] invio notifiche digitali mono destinatario con parametri abstract errati_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | abstract           | <abstract>                  |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | abstract  |
      | 1025_CHAR |

  Scenario: [B2B-PA-SEND_VALID_15] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And viene configurato noticeCodeAlternative uguale a noticeCode
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  @testLite
  Scenario: [B2B-PA-SEND_VALID_16] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And viene configurato noticeCodeAlternative diversi a noticeCode
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  Scenario: [B2B-PA-SEND_VALID_17] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Cucumber
    And viene configurato noticeCodeAlternative uguale a noticeCode
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  @testLite
  Scenario: [B2B-PA-SEND_VALID_18] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Cucumber
    And viene configurato noticeCodeAlternative diversi a noticeCode
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @dev
  Scenario Outline: [B2B-PA-SEND_VALID_19] invio notifiche digitali mono destinatario con physicalAddress_zip corretti scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | FRANCIA    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | <zip_code> |
      | physicalAddress_province     | Paris      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | zip_code |
      | 750077 |
      | 750077750077 |
      | 750077750077998 |

  @dev
  Scenario Outline: [B2B-PA-SEND_VALID_20] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL       |
      | physicalAddress_State        | FRANCIA    |
      | physicalAddress_municipality | Parigi     |
      | physicalAddress_zip          | <zip_code> |
      | physicalAddress_province     | Paris      |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | zip_code |
      | 7500777500779987 |
    #1) 15 max Length

  @dev
  Scenario Outline: [B2B-PA-SEND_VALID_21] invio notifiche digitali mono destinatario con physicalAddress_zip corretti scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_zip          | <zip_code> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | zip_code |
      | 87041 |
      | 87100 |

  @dev
  Scenario Outline: [B2B-PA-SEND_VALID_22] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_zip          | <zip_code> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | zip_code |
      | 7500777500779987 |

  @ignore
  Scenario: [B2B-PA-SEND_VALID_23] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_zip          | 33344 |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"


  Scenario: [B2B-PA-SEND_VALID_24] invio notifiche digitali mono destinatario con provincia non presente e Stato Italia scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | ITALIA    |
      | physicalAddress_province | NULL |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  Scenario: [B2B-PA-SEND_VALID_25] invio notifiche digitali mono destinatario con provincia non presente e Stato Estero scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | FRANCIA    |
      | physicalAddress_province | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  Scenario: [B2B-PA-SEND_VALID_26] invio notifiche digitali mono destinatario con provincia presente e Stato italia scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | ITALIA |
      | physicalAddress_province | MI      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @ignore
  Scenario: [B2B-PA-SEND_VALID_27] invio notifiche digitali mono destinatario con provincia presente e Stato estero scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | FRANCIA |
      | physicalAddress_province | MI      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @ignore
  Scenario: [B2B-PA-SEND_VALID_28] invio notifiche digitali mono destinatario con provincia presente e Stato estero scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | FRANCIA |
      | physicalAddress_province | MILANO  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

    @ignore
  Scenario: [B2B-PA-SEND_VALID_29] invio notifiche digitali mono destinatario con provincia presente e Stato estero scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | ITALIA |
      | physicalAddress_province | MILNO  |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN


  Scenario: [B2B-PA-SEND_VALID_30] invio notifiche digitali mono destinatario con provincia non presente e Stato non presente scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | NULL |
      | physicalAddress_province | NULL |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"


  Scenario: [B2B-PA-SEND_VALID_31] invio notifiche digitali mono destinatario con provincia non presente e Stato Italia scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | ITALIA    |
      | physicalAddress_province | 0_CHAR |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"


  Scenario: [B2B-PA-SEND_VALID_32] invio notifiche digitali mono destinatario con provincia  presente e Stato non presente scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_State    | NULL |
      | physicalAddress_province | MI |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN



  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_33] Invio notifica digitale con mono destinatario con denomination corretta e recupero tramite codice IUN (p.fisica)_scenario positivo

    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | <denomination>   |
      | taxId        | FRMTTR76M06B715E |

  When la notifica viene inviata dal "Comune_1"
  Then si verifica la corretta acquisizione della richiesta di invio notifica

    Examples:
      | denomination                  |
      | Cristoforo Colombo            |
      | Cristoforo Colombo 0123456789 |
      | SALVATOR DALI                 |
      | Ilaria-D'Amico/.@_            |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_34] Invio notifica digitale con mono destinatario con denomination errata scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano               |
    And destinatario
      | denomination | <denomination>   |
      | taxId        | FRMTTR76M06B715E |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination                                                 |
      | Nicolò Rossi Raffaella Carrà Salvator Dalì Bruno Nicolè dudù |
      


  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_35] Invio notifica digitale mono destinatario con physicalAddress_address e physicalAddress_addressDetails  corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:

      | physicalAddress_address        | <indirizzo> |
      | physicalAddress_addressDetails | <indirizzo> |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | indirizzo                       |
      | via dell'adige- via torino/.@_  |
      | VIA ADIGE VIA TORINO            |
      | via adige 01234 via adige 56789 |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_36] Invio notifica digitale mono destinatario con physicalAddress_municipality corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <comune> |
      | physicalAddress_zip          | 20121    |
      | physicalAddress_province     | MILANO   |
  When la notifica viene inviata dal "Comune_1"
  Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | comune             |
      | Milano '-/.@_      |
      | MILANO             |
      | MILANO 01234 56789 |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_37] Invio notifica digitale mono destinatario con physicalAddress_municipalityDetails corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | localita           |
      | Milano '-/.@_      |
      | PARIGI             |
      | MILANO 01234 56789 |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_38] Invio notifica digitale mono destinatario con physicalAddress_State corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_State | <state> |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | state              |
      | Italia '-/.@_      |
      | FRANCIA            |
      | ITALIA 01234 56789 |


  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_39] Invio notifica digitale mono destinatario con physicalAddress_zip corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <comune>   |
      | physicalAddress_zip          | <zip_code> |
      | physicalAddress_province     | <province> |
      | physicalAddress_State        | <state>    |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | zip_code   | comune | province | state   |
      | 1212_      | Paris  | Paris    | FRANCIA |
      | ZONA 1     | Paris  | Paris    | FRANCIA |
      | 0123456789 | Paris  | Paris    | FRANCIA |


  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_40] Invio notifica digitale mono destinatario con physicalAddress_province corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | Milano     |
      | physicalAddress_zip          | 20121      |
      | physicalAddress_province     | <province> |
    When la notifica viene inviata dal "Comune_1"
    Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | province       |
      | MI '-/.@_      |
      | MI             |
      | MI 01234 56789 |


  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_41] Invio notifica digitale mono destinatario con physicalAddress_address errato (p.fisica)_scenario negativo

    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_address | <indirizzo> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | indirizzo                |
      | via dà via è via ì via ò |
      | via dell ()=?*+;,!^&     |


  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_42] Invio notifica digitale mono destinatario con physicalAddress_addressDetails errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_addressDetails | <indirizzo> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | indirizzo                |
      | via dà via è via ì via ò |
      | via dell adige  ()=?*+;,!^& |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_43] Invio notifica digitale mono destinatario con physicalAddress_municipality errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <comune> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | comune                               |
      | san donà Erbè Forlì Nardò Brùsaporto |
      | san_dona  ()=?*+;,!^&                |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_44] Invio notifica digitale mono destinatario con physicalAddress_municipalityDetails errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | localita                             |
      | san donà Erbè Forlì Nardò Brùsaporto |
      | san_dona  ()=?*+;,!^&                |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_45] Invio notifica digitale mono destinatario con physicalAddress_State errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_State | <state> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | state                            |
      | Città d'Avòrio Rénmín Mùrìtaniya |
      | Citta d Avorio ()=?*+;,!^&       |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_46] Invio notifica digitale mono destinatario con physicalAddress_zip errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_zip | <zip_code> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | zip_code               |
      | dà via è via ì via ò   |
      | dell adige ()=?*+;,!^& |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_47] Invio notifica digitale mono destinatario con physicalAddress_province errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | physicalAddress_province | <province> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | province                         |
      | Città d'Avòrio Rénmín Mùrìtaniya |
      | Citta d Avorio ()=?*+;,!^&       |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_48] invio notifiche digitali mono destinatario con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province corretti scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <municipality> |
      | physicalAddress_zip          | <zip_code>     |
      | physicalAddress_province     | <province>     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | municipality     | zip_code | province |
      | SETTIMO MILANESE | 20019    | MI       |
      | Milano           | 20121    | MI       |
      | VILLAPIANA LIDO  | 87076    | CS       |

  @validation @realNormalizzatore
  Scenario Outline: [B2B-PA-SEND_VALID_49] invio notifiche digitali mono destinatario con  con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province errati scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <municipality> |
      | physicalAddress_zip          | <zip_code>     |
      | physicalAddress_province     | <province>     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Examples:
      | municipality | zip_code | province |
      | Palermo      | 20019    | MI       |
      | Milano       | 90121    | PA       |
      | Milano       | 90121    | MI       |
      | Milano       | 90121    | RM       |

