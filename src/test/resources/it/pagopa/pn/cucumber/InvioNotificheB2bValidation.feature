Feature: Validazione campi invio notifiche b2b

  Scenario Outline: [B2B-PA-SEND_VALID_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | physicalAddress_municipality | <comune> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

    Examples:
      |  comune           |
      | san donà di piave |
      | Canneto sull'Oglio|
      | Erbè              |
      | Forlì             |
      | Nardò             |


  Scenario Outline: [B2B-PA-SEND_VALID_2] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

    Examples:
      |     localita       |
      | san donà di piave  |
      | Canneto sull'Oglio |
      | Erbè               |
      | Forlì              |
      | Nardò              |



  Scenario Outline: [B2B-PA-SEND_VALID_3] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
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
      | via dell'anno |
      | via è         |
      | via ì         |
      | via ò         |



  Scenario Outline: [B2B-PA-SEND_VALID_4] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.fisica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Cucumber e:
      | physicalAddress_State | <stato> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN

    Examples:
      |   stato             |
      | Città del Vaticano  |
      | Costa d'Avorio      |
      | Perù                |



  Scenario Outline: [B2B-PA-SEND_VALID_5] invio notifica con oggetto contenente caratteri speciali_scenario positivo
    Given viene generata una nuova notifica
      | subject | <name> |
      | senderDenomination | comune di milano  |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      |      name        |
      | irrinunciabilità |
      | trentatré        |
      | altresì          |
      | bistrò           |
      | più              |
      | dall'atto        |


  Scenario Outline: [B2B-PA-SEND_VALID_6] invio notifica a destinatario la cui denominazione contenente caratteri speciali_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano  |
    And destinatario
      | denomination | <denomination> |
      | taxId | FRMTTR76M06B715E |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | denomination    |
      | Nicolò Rossi    |
      | Raffaella Carrà |
      | Ilaria D'Amico  |
      | Salvator Dalì   |
      | Bruno Nicolè    |
      | dudù            |


  @SmokeTest
  Scenario Outline: [B2B-PA-SEND_VALID_7] invio notifiche digitali mono destinatario con parametri denomination errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | <denomination> |
      | taxId | FRMTTR76M06B715E |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | denomination    |
      | 0_CHAR  |
      | 81_CHAR |

  @SmokeTest
  Scenario Outline: [B2B-PA-SEND_VALID_8] invio notifiche digitali mono destinatario con parametri senderDenomination errati_scenario negativo
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

  Scenario Outline: [B2B-PA-SEND_VALID_9] invio notifiche digitali mono destinatario con parametri tax_id errati_scenario negativo
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

  Scenario Outline: [B2B-PA-SEND_VALID_10] invio notifiche digitali mono destinatario con parametri tax_id corretti_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | taxId | <taxId> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | taxId   |
      | FRMTTR76M06B715E |
      | FRMTTR76M0MB715E |
      | FRMTTR76M06B7P5E |
      #1) Lettera omocodia contemplata (primi 2 numeri)
      #2) Lettera omocodia contemplata (seconda serie di 2 numeri)
      #3) Lettera omocodia contemplata (serie di 3 numeri finale)


  Scenario Outline: [B2B-PA-SEND_VALID_11] invio notifiche digitali mono destinatario con parametri creditorTaxId errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber e:
      | payment_creditorTaxId | <creditorTaxId> |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | creditorTaxId   |
      | 1000000000 |
      | 120000000000 |
      | 11_CHAR |
      #1) 10 numeri (min 11)
      #2) 12 numeri (max 11)
      #3) 11 lettere (ammessi solo numeri)


  Scenario Outline: [B2B-PA-SEND_VALID_12] invio notifiche digitali mono destinatario con parametri senderTaxId errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | <senderTaxId> |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | senderTaxId   |
      | 1000000000 |
      | 120000000000 |
      | 11_CHAR |
      #1) 10 numeri (min 11)
      #2) 12 numeri (max 11)
      #3) 11 lettere (ammessi solo numeri)


  @SmokeTest
  Scenario Outline: [B2B-PA-SEND_VALID_13] invio notifiche digitali mono destinatario con parametri subject errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | <subject> |
      | senderDenomination | comune di milano |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | subject   |
      | 513_CHAR |

  @SmokeTest
  Scenario Outline: [B2B-PA-SEND_VALID_14] invio notifiche digitali mono destinatario con parametri abstract errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | abstract | <abstract> |
    And destinatario Mario Cucumber
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | abstract   |
      | 1025_CHAR |
