Feature: Validazione campi invio notifiche b2b

  @testLite
  Scenario Outline: [B2B-PA-SEND_VALID_9] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
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

  @testLite
  Scenario Outline: [B2B-PA-SEND_VALID_10] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
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


  Scenario Outline: [B2B-PA-SEND_VALID_11] invio notifiche digitali mono destinatario con parametri tax_id errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | taxId | <taxId> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | taxId   |
      | 1000000000 |
      | 17000000000000000 |
      | FRMTTR76M06B715 |
      | FRMTTRZ6M06B715E |
      | FRMTTR76M0YB715E |
      | FRMTTR76M06B7W5E |
      #1) 10 numeri (min 11)
      #2) 17 numeri (max 16)
      #3) CF non valido (lettera finale mancante)
      #4) Lettera omocodia non contemplata (primi 2 numeri)
      #5) Lettera omocodia non contemplata (seconda serie di 2 numeri)
      #6) Lettera omocodia non contemplata (serie di 3 numeri finale)


  Scenario Outline: [B2B-PA-SEND_VALID_12] invio notifiche digitali mono destinatario con parametri denomination errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | <denomination> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination    |
      | 0_CHAR  |
      | 81_CHAR |


  Scenario Outline: [B2B-PA-SEND_VALID_13] invio notifiche digitali mono destinatario con parametri senderDenomination errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | <denomination> |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination    |
      | 0_CHAR  |
      | 81_CHAR |

  
  Scenario Outline: [B2B-PA-SEND_VALID_14] invio notifiche digitali mono destinatario con parametri abstract errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | abstract | <abstract> |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | abstract  |
      | 1025_CHAR |
