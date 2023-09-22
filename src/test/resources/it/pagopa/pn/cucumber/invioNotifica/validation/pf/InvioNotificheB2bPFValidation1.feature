Feature: Validazione campi invio notifiche b2b

  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_municipality | <comune> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | comune             |
      | Cantù              |
      | san donà di piave  |
      | Canneto sull'Oglio |
      | Erbè               |
      | Forlì              |
      | Nardò              |
      | Canneto sull’Oglio |
      | Canneto sull`Oglio |
      | Fær Øer            |
      | São Tomé           |
      | Hagåtña            |


  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_1_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_municipality | <comune> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | comune                                   |
      | san donà di piave Erbè Forlì Nardò       |
      | Canneto sull'Oglio sull’Oglio sull`Oglio |
      | Fær Øer São Tomé Hagåtña                 |

  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_2] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | localita           |
      | san donà di piave  |
      | Canneto sull'Oglio |
      | Erbè               |
      | Forlì              |
      | Nardò              |
      | Canneto sull’Oglio |
      | Canneto sull`Oglio |
      | Fær Øer            |
      | São Tomé           |
      | Hagåtña            |


  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_2_LITE] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Cucumber e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | localita                                 |
      | san donà di piave Erbè Forlì Nardò       |
      | Canneto sull'Oglio sull’Oglio sull`Oglio |
      | Fær Øer São Tomé Hagåtña                 |

  Scenario Outline: [B2B-PA-SEND_VALID_3] invio notifica a destinatario la cui denominazione contenente caratteri speciali_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | <denomination>   |
      | taxId        | FRMTTR76M06B715E |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | denomination    |
      | Nicolò Rossi    |
      | Raffaella Carrà |
      | Ilaria D`Amico  |
      | Salvator Dalì   |
      | Bruno Nicolè    |
      | dudù            |
       #problema con | Ilaria D’Amico  |
       #problema con | Ilaria D'Amico  |


  @ignore
  Scenario Outline: [B2B-PA-SEND_VALID_3_LITE] invio notifica a destinatario la cui denominazione contenente caratteri speciali_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | denomination | <denomination>   |
      | taxId        | FRMTTR76M06B715E |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | denomination                                                 |
      | Nicolò Rossi Raffaella Carrà Salvator Dalì Bruno Nicolè dudù |
      | Ilaria D`Amico D’Amico D'Amico                               |

  @testLite
  Scenario Outline: [B2B-PA-SEND_VALID_4] invio notifica con oggetto contenente caratteri speciali_scenario positivo
    Given viene generata una nuova notifica
      | subject            | <name>           |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | name             |
      | irrinunciabilità |
      | trentatrrré      |
      | altresssssì      |
      | bistrrrrrrò      |
      | piiiiiiiiiù      |
      | dall'atttto      |
      | dall’atttto      |
      | dall`atttto      |


  Scenario Outline: [B2B-PA-SEND_VALID_5] invio notifiche digitali mono destinatario con parametri tax_id errati_scenario positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario
      | taxId | <taxId> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | taxId            |
      | FRMTTR76F06B715E |
      | FRRTTR76M0MB715H |


  Scenario Outline: [B2B-PA-SEND_VALID_6] invio notifiche digitali mono destinatario con parametri creditorTaxId errati_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | <creditorTaxId> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | creditorTaxId |
      | 1000000000    |
      | 120000000000  |
      | 11_CHAR       |
      #1) 10 numeri (min 11)
      #2) 12 numeri (max 11)
      #3) 11 lettere (ammessi solo numeri)


  Scenario Outline: [B2B-PA-SEND_VALID_7] invio notifiche digitali mono destinatario con parametri senderTaxId errati_scenario negativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
      | senderTaxId        | <senderTaxId>               |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | senderTaxId  |
      | 1000000000   |
      | 120000000000 |
      | 11_CHAR      |
      #1) 10 numeri (min 11)
      #2) 12 numeri (max 11)
      #3) 11 lettere (ammessi solo numeri)


  Scenario Outline: [B2B-PA-SEND_VALID_8] invio notifiche digitali mono destinatario con parametri subject errati_scenario negativo
    Given viene generata una nuova notifica
      | subject            | <subject>        |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | subject  |
      | 513_CHARRRR |
