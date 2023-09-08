Feature: Validazione campi invio notifiche b2b persona giuridica

  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_PG_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.giuridica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | physicalAddress_State | <stato> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      |   stato                          |
      | Città d'Avorio                   |
      | Fær Øer                          |
      | São Tomé                         |
      | Hagåtña                          |
      | Zhōnghuóá Rénmín Gònghéguó       |
      | Tašād                            |
      | Jumhūriyyat Tašād                |
      | Κύπρος                           |
      | Κυπριακή Δημοκρατία Kypros       |
      | Ittihād al-Qumur                 |
      | Chosŏn Minjujuŭi Inmin Konghwaguk|
      | Répúblique de Côte d`Ivoire      |
      | Iritriyā                         |
      | République d'Haïti               |
      | Lýðveldið Ísland                 |
      | Poblacht na hÉireann             |
      | Īrān                             |
      | Bhārat Ganarājya                 |
      | Lībiyā                           |
      | Mfùko la Malaŵi                  |
      | Moçambique                       |
      | Mūrītāniyā                       |
      | Têta Paraguái                    |
      | Česká republika                  |
      | Mālo Tuto’atasi o Sāmoa          |

  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_PG_11] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.giuridica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | physicalAddress_address | <indirizzo> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      |   indirizzo   |
      | via dà        |
      | via dell'adige|
      | via sull’adige|
      | via sull`adige|
      | via è         |
      | via ì         |
      | via ò         |


  Scenario Outline: [B2B-PA-SEND_VALID_PG_12] invio notifiche digitali mono destinatario con parametri denomination errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | <denomination> |
      | recipientType |      PG       |
      | taxId         |  15376371009  |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination    |
      | 0_CHAR  |
      | 81_CHAR |


  Scenario Outline: [B2B-PA-SEND_VALID_PG_13] invio notifiche digitali mono destinatario con parametri senderDenomination errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | <denomination> |
    And destinatario Gherkin spa
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination    |
      | 0_CHAR  |
      | 81_CHAR |


  Scenario Outline: [B2B-PA-SEND_VALID_PG_14] invio notifiche digitali mono destinatario con parametri abstract errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | abstract | <abstract> |
    And destinatario Gherkin spa
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | abstract  |
      | 1025_CHAR |

  Scenario: [B2B-PA-SEND_VALID_PG-15] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa
    And viene configurato noticeCodeAlternative uguale a noticeCode
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"

  @testLite
  Scenario: [B2B-PA-SEND_VALID_PG_16] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa
    And viene configurato noticeCodeAlternative diversi a noticeCode
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  Scenario: [B2B-PA-SEND_VALID_PG-17] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Gherkin spa
    And viene configurato noticeCodeAlternative uguale a noticeCode
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"

  @testLite
  Scenario: [B2B-PA-SEND_VALID_PG_18] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative diversi_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Gherkin spa
    And viene configurato noticeCodeAlternative diversi a noticeCode
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

  @testLite
  Scenario Outline: [B2B-PA-SEND_VALID_PG_19] invio notifiche digitali mono destinatario con physicalAddress_zip corretti scenario positivo
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
      | 750077750077 |

  Scenario Outline: [B2B-PA-SEND_VALID_PG_20] invio notifiche digitali mono destinatario con physicalAddress_zip non corretti scenario negativo
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

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_1] Invio notifica digitale con multi destinatario corretto e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination  | <denominationPF> |
      | recipientType | PF               |
      | taxId         | FRMTTR76M06B715E |
    And destinatario
      | denomination  | <denominationPG> |
      | recipientType | PG               |
      | taxId         | 15376371009      |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | denominationPF                | denominationPG   |
      | Cristoforo Colombo            | srl azienda      |
      | Cristoforo Colombo 0123456789 | 0123456789 spa   |
      | SALVATOR DALI                 | SRL AZIENDA      |
      | Ilaria-D'Amico/.              | l'azienda- C.R.L |

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_2] Invio notifica digitale con multi destinatario corretto e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And destinatario
      | denomination  | <denomination> |
      | recipientType | PG             |
      | taxId         | 15376371009    |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination             |
      | àzìèndà òù               |
      | srl_Nicola&Rossi@ ;:,?!" |

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_3] Invio notifica digitale multi destinatario con physicalAddress_address corretto (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_address             | <indirizzo> |
      | physicalAddress_address_details     | <indirizzo> |
      | physicalAddress_municipality        | <comune>    |
      | physicalAddress_municipalityDetails | <localita>  |
      | physicalAddress_State               | <state>     |
      | physicalAddress_zip                 | <zip_code>  |
      | physicalAddress_province            | <province>  |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | indirizzo                       | comune             | localita            | state               | zip_code   | province       |
      | via dell'adige- via torino/.    | Milano '-/.        | Milano '-/.         | Italia '-/.         | 20019 '-/. | mi '-/.        |
      | VIA ADIGE  VIA TORINO           | PARIGI             | PARIGI              | FRANCIA             | ZONA 1     | PARIS          |
      | via adige 01234 via adige 56789 | Milano 01234 56789 | Milano  01234 56789 | ITALIA  01234 56789 | 20121      | MI 01234 56789 |

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_4] Invio notifica digitale multi destinatario con physicalAddress_address errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_address | <indirizzo> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | indirizzo                |
      | via dà via è via ì via ò |
      | via dell`adige_?^,       |

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_5] Invio notifica digitale multi destinatario con physicalAddress_address_details errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_address_details | <indirizzo> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | indirizzo                |
      | via dà via è via ì via ò |
      | via dell`adige_?^,       |

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_6] Invio notifica digitale multi destinatario con physicalAddress_municipality errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_municipality | <comune> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | comune                               |
      | san donà Erbè Forlì Nardò Brùsaporto |
      | san_dona`?^,"                        |

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_7] Invio notifica digitale multi destinatario con physicalAddress_municipalityDetails errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | localita                             |
      | san donà Erbè Forlì Nardò Brùsaporto |
      | san_dona`?^,"                        |

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_8] Invio notifica digitale multi destinatario con physicalAddress_State errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_State | <state> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | state                            |
      | Città d'Avòrio Rénmín Mùrìtaniya |
      | Citta_d`Avorio?^,"               |

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_9] Invio notifica digitale multi destinatario con physicalAddress_zip errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_zip | <zip_code> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | zip_code                 |
      | via dà via è via ì via ò |
      | via dell`adige_?^,       |

  @7621
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_10] Invio notifica digitale multi destinatario con physicalAddress_province errato (p.fisica)_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_province | <province> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | province                         |
      | Città d'Avòrio Rénmín Mùrìtaniya |
      | Citta_d`Avorio?^,"               |

  @7632
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_11] invio notifiche digitali multi destinatario con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province corretti scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile                     | NULL           |
      | physicalAddress_municipalityDetails | <municipality> |
      | physicalAddress_zip                 | <zip_code>     |
      | physicalAddress_province            | <province>     |
    And destinatario Gherkin spa e:
      | digitalDomicile              | NULL           |
      | physicalAddress_municipality | <municipality> |
      | physicalAddress_zip          | <zip_code>     |
      | physicalAddress_province     | <province>     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | municipality | zip_code | province |
      | Milano       | 20019    | MI       |
      | Milano       | 20121    | MI       |
      | Milano       | 20162    | MI       |

  @7632
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_12] invio notifiche digitali multi destinatario con  con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province errati scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin
    And destinatario Gherkin spa e:
      | digitalDomicile                     | NULL           |
      | physicalAddress_municipalityDetails | <municipality> |
      | physicalAddress_zip                 | <zip_code>     |
      | physicalAddress_province            | <province>     |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | municipality | zip_code | province |
      | Palermo      | 20019    | MI       |
      | Milano       | 90121    | PA       |
      | Milano       | 90121    | MI       |
      | Milano       | 90121    | RM       |
