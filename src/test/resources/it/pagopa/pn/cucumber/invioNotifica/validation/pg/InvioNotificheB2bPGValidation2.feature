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

  @SmokeTest
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

  @SmokeTest
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

  @SmokeTest
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
