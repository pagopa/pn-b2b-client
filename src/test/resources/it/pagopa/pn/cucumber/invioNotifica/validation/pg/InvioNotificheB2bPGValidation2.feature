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

  @validation
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

  @validation
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

  @validation
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

  Scenario: [B2B-PA-SEND_VALID_PG-17] invio notifiche digitali mono destinatario con noticeCode e noticeCodeAlternative uguali_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Gherkin spa
    And viene configurato noticeCodeAlternative uguale a noticeCode
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"


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

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_1] Invio notifica digitale con multi destinatario corretto e recupero tramite codice IUN scenario positivo
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
    When la notifica viene inviata dal "Comune_Multi"
    Then si verifica la corretta acquisizione della richiesta di invio notifica
    Examples:
      | denominationPF                | denominationPG      |
      | Cristoforo Colombo            | srl azienda         |
      | Cristoforo Colombo 0123456789 | 0123456789 spa      |
      | SALVATOR DALI                 | SRL AZIENDA         |
      | Ilaria-D'Amico/.@_            | l'azienda-@_ /C.R.L |


  @validation @uat
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_8] Invio notifica digitale mono destinatario con physicalAddress_province corretto scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_province     | <province> |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Examples:
      | province       |
      | mi '-/.@_      |
      | MI             |
      | MI 01234 56789 |


  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_9] Invio notifica digitale multi destinatario con physicalAddress_address errato scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_address | <indirizzo> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | indirizzo                                                    |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |


  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_11] Invio notifica digitale multi destinatario con physicalAddress_municipality errato scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_municipality | <comune> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | comune                                                       |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_12] Invio notifica digitale multi destinatario con physicalAddress_municipalityDetails errato scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | localita                            |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂ       |
      | via ĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_13] Invio notifica digitale multi destinatario con physicalAddress_State errato scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_State | <state> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | state                                                        |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_14] Invio notifica digitale multi destinatario con physicalAddress_zip errato scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_zip | <zip_code> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | zip_code                                                     |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_15] Invio notifica digitale multi destinatario con physicalAddress_province errato scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber
    And destinatario Gherkin spa e:
      | physicalAddress_province | <province> |
    When la notifica viene inviata dal "Comune_Multi"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | province                                                     |
      | via ĄŁĽŚŠŞŤŹŽŻą˛łľśˇšşťź˝žżŔĂĹĆČĘĚĎĐŃŇŐŘŮŰŢŕăĺćčęěďđńňőřůűţ˙ |

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_16] invio notifiche digitali multi destinatario con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province corretti scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | physicalAddress_municipality | <municipality> |
      | physicalAddress_zip          | <zip_code>     |
      | physicalAddress_province     | <province>     |
    And destinatario Gherkin spa e:
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

  @validation
  Scenario Outline: [B2B-PA-SEND_VALID_PF_PG_17] invio notifiche digitali multi destinatario con  con physicalAddress_zip, physicalAddress_municipality e physicalAddress_province errati scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin
    And destinatario Gherkin spa e:
      | physicalAddress_municipality | <municipality> |
      | physicalAddress_zip          | <zip_code>     |
      | physicalAddress_province     | <province>     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi REFUSED
    Examples:
      | municipality | zip_code | province |
      | Palermo      | 20019    | MI       |
      | Milano       | 90121    | PA       |
      | MILANO       | 90121    | MI       |
      | Milano       | 90121    | RM       |
