Feature: Validazione campi invio notifiche b2b con persona giuridica

  Scenario Outline: [B2B-PA-SEND_VALID_PG_1] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.giuridica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
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
      | Canneto sull’Oglio|
      | Canneto sull`Oglio|
      | Fær Øer           |
      | São Tomé          |
      | Hagåtña           |

  Scenario Outline: [B2B-PA-SEND_VALID_PG_2] Invio notifica digitale mono destinatario e recupero tramite codice IUN (p.giuridica)_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Gherkin spa e:
      | physicalAddress_municipalityDetails | <localita> |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      |     localita      |
      | san donà di piave |
      | Canneto sull'Oglio|
      | Erbè              |
      | Forlì             |
      | Nardò             |
      | Canneto sull’Oglio|
      | Canneto sull`Oglio|
      | Fær Øer           |
      | São Tomé          |
      | Hagåtña           |

  Scenario Outline: [B2B-PA-SEND_VALID_PG_3] invio notifica a destinatario la cui denominazione contenente caratteri speciali_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano  |
    And destinatario
      | denomination | <denomination> |
      | recipientType |       PG      |
      | taxId | 15376371009           |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And la notifica può essere correttamente recuperata dal sistema tramite codice IUN
    Examples:
      | denomination    |
      | Nicolò spa      |
      | Carrà trasporti |
      | D'Amico spa     |
      | D`Amico  srl    |
      |  Dalì srls      |
      | Nicolè srls     |
      | dudù   spa      |

  Scenario Outline: [B2B-PA-SEND_VALID_PG_4] invio notifica con oggetto contenente caratteri speciali_scenario positivo
    Given viene generata una nuova notifica
      | subject | <name> |
      | senderDenomination | comune di milano  |
    And destinatario Gherkin spa
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
      | dall’atto        |
      | dall`atto        |

  Scenario Outline: [B2B-PA-SEND_VALID_PG_5] invio notifiche digitali mono destinatario con errati tax_id errati_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination  | Società Cucumber |
      | recipientType |      PG          |
      | taxId         |   <taxId>        |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | taxId   |
      | FRMTTR76F06B715E |
      | FRRTTR76M0MB715H |

  Scenario Outline: [B2B-PA-SEND_VALID_PG_6] invio notifiche digitali mono destinatario con parametri tax_id corretti_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination  | Gherkin spa |
      | recipientType |      PG     |
      | taxId         |   <taxId>   |
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | taxId   |
      | 1537637100   |
      | 153763710094 |
      | 15376A71009  |
      | 15376371001l |
      | 1537637100l  |
      #1) P.Iva numero in meno (8 cifre)
      #2) P.Iva numero in più (10 cifre)
      #3) P.Iva con lettera (8 cifre e una lettera)
      #4) P.Iva con letta (9 cifre e una lettera)
      #5) P.Iva numero in meno con lettera alla fine (8 cifre e una lettera)

  Scenario Outline: [B2B-PA-SEND_VALID_PG_7] invio notifiche digitali mono destinatario con parametri creditorTaxId errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario Gherkin spa e:
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

  Scenario Outline: [B2B-PA-SEND_VALID_PG_8] invio notifiche digitali mono destinatario con parametri senderTaxId errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | senderTaxId | <senderTaxId> |
    And destinatario Gherkin spa
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

  @SmokeTest
  Scenario Outline: [B2B-PA-SEND_VALID_PG_9] invio notifiche digitali mono destinatario con parametri subject errati_scenario negativo
    Given viene generata una nuova notifica
      | subject | <subject> |
      | senderDenomination | comune di milano |
    And destinatario Gherkin spa
    When la notifica viene inviata dal "Comune_1"
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | subject  |
      | 513_CHAR |