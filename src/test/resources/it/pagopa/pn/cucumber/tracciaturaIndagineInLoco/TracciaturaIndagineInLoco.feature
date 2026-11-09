Feature: SEND - Tracciatura dell'indagine in loco e della fonte dell'indirizzo per gli invii cartacei

  # PST - Tracciatura Indagine in loco e fonte secondo invio (Epic PN-18659)
  #
  # Dati di test e ambienti:
  # - PA abilitata al VAS -> Comune_Multi (il VAS si attiva solo con indirizzo fisico non valorizzato).
  # - PF con indirizzo su ANPR -> Utenza con Indirizzo Valido da ANPR (config/taxids-{env}.properties): in DEV e TEST
  #   ANPR è il mock, in UAT è ANPR reale (ambiente di validazione) e il codice fiscale configurato cambia di conseguenza.
  #   Nelle bonarie lo stesso codice fiscale si ottiene con taxId = ${Utenza con Indirizzo Valido da ANPR}.
  # - PG censita sul Registro Imprese -> 01113570442: il Registro Imprese (Infocamere) è mock in tutti gli ambienti.
  #

  # ====================================================================================================================
  # SCENARIO 1 - Notifiche legali con invio cartaceo
  # CASO DI TEST 1.1: Verificare che, lungo tutto il percorso di recapito cartaceo di una notifica legale, vengano tracciate
  # la fonte dell'indirizzo utilizzato e l'eventuale indagine in loco del postino.
  # ====================================================================================================================

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_A @work-item:QA-17058
  Scenario: [PAPER_SOURCE_LEGAL_01_1_A] Indirizzo verificato sui registri nazionali, destinatario persona fisica e consegna al primo tentativo
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Utenza con Indirizzo Valido da ANPR e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then il primo invio cartaceo del destinatario 0 riporta come fonte dell'indirizzo "ANPR"
    And il primo invio cartaceo del destinatario 0 non riporta l'indagine in loco
    And l'esito del primo tentativo del destinatario 0 riporta che l'indagine in loco non è stata effettuata

  # PST 1.1.B è coperta da due scenari perché manca una PG censita sul Registro Imprese (mock)
  # il cui indirizzo restituito dal VAS produca una sequenza con indirizzo individuato dal postino (es. FAIL-Discovery_AR).
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_B_1 @work-item:QA-17058
  Scenario: [PAPER_SOURCE_LEGAL_01_1_B_1] Indirizzo verificato sui registri nazionali e destinatario persona giuridica
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | PG Censito  |
      | recipientType   | PG          |
      | taxId           | 01113570442 |
      | digitalDomicile | NULL        |
      | physicalAddress | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then il primo invio cartaceo del destinatario 0 riporta come fonte dell'indirizzo "REGISTRO_IMPRESE"

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_B_2 @work-item:QA-17058
  Scenario: [PAPER_SOURCE_LEGAL_01_1_B_2] Destinatario persona giuridica, tentativo fallito e nuovo indirizzo individuato dal postino utilizzato
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito            |
      | recipientType           | PG                    |
      | taxId                   | 01113570442           |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then l'esito del primo tentativo del destinatario 0 riporta che l'indagine in loco è stata effettuata
    And il secondo invio cartaceo del destinatario 0 riporta come fonte dell'indirizzo "INDAGINE"
    And il secondo invio cartaceo del destinatario 0 riporta che l'indagine in loco è stata effettuata

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_C @work-item:QA-17058
  Scenario: [PAPER_SOURCE_LEGAL_01_1_C] Indirizzo fornito dal mittente, destinatario persona fisica, tentativo fallito e indirizzo individuato dal postino non utilizzabile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | REGISTERED_LETTER_890       |
    And destinatario Utenza con Indirizzo Valido da ANPR e:
      | digitalDomicile         | NULL                                     |
      | physicalAddress_address | Via@FAIL-DISCOVERYIRREPERIBILEBADCAP_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then il primo invio cartaceo del destinatario 0 riporta come fonte dell'indirizzo "MITTENTE"
    And l'esito del primo tentativo del destinatario 0 riporta che l'indagine in loco è stata effettuata
    And il secondo invio cartaceo del destinatario 0 riporta come fonte dell'indirizzo "ANPR"
    And il secondo invio cartaceo del destinatario 0 riporta che l'indagine in loco è stata effettuata

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_D @work-item:QA-17058
  Scenario: [PAPER_SOURCE_LEGAL_01_1_D] Destinatario persona giuridica, primo tentativo fallito senza indagine in loco e secondo invio verso il Registro Imprese
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | PG Censito               |
      | recipientType           | PG                       |
      | taxId                   | 01113570442              |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Then l'esito del primo tentativo del destinatario 0 riporta che l'indagine in loco non è stata effettuata
    And il secondo invio cartaceo del destinatario 0 riporta come fonte dell'indirizzo "REGISTRO_IMPRESE"
    And il secondo invio cartaceo del destinatario 0 riporta che l'indagine in loco non è stata effettuata

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_E @work-item:QA-17058
  Scenario: [PAPER_SOURCE_LEGAL_01_1_E] Primo tentativo fallito senza alcun indirizzo alternativo disponibile
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | FNTLCU80T25F205R         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then il primo invio cartaceo del destinatario 0 riporta come fonte dell'indirizzo "MITTENTE"
    And il secondo invio cartaceo del destinatario 0 non è presente

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_F @work-item:QA-17058
  Scenario: [PAPER_SOURCE_LEGAL_01_1_F] Notifica multidestinatario con un solo destinatario verificato sui registri nazionali
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Utenza con Indirizzo Valido da ANPR e:
      | digitalDomicile | NULL |
      | physicalAddress | NULL |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then il primo invio cartaceo del destinatario 0 riporta come fonte dell'indirizzo "ANPR"
    And il primo invio cartaceo del destinatario 1 riporta come fonte dell'indirizzo "MITTENTE"


  # ====================================================================================================================
  # CASO DI TEST 1.2: Verificare che anche la raccomandata semplice riporti la fonte dell'indirizzo utilizzato.
  # ====================================================================================================================

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_2_A @work-item:QA-17058
  Scenario: [PAPER_SOURCE_LEGAL_01_2_A] Raccomandata semplice con indirizzo fornito dal mittente
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS    |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    Then la raccomandata semplice del destinatario 0 riporta come fonte dell'indirizzo "MITTENTE"

  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_2_B @work-item:QA-17058
  Scenario: [PAPER_SOURCE_LEGAL_01_2_B] Raccomandata semplice con indirizzo verificato sui registri nazionali
    Given il test è effettuabile con API versione "V25" o superiore
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Utenza con Indirizzo Valido da ANPR e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress         | NULL         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_SIMPLE_REGISTERED_LETTER"
    Then la raccomandata semplice del destinatario 0 riporta come fonte dell'indirizzo "ANPR"

  # ====================================================================================================================
  # SCENARIO 2 - Comunicazioni bonarie con invio cartaceo
  # CASO DI TEST 2.1: Verificare che l'invio cartaceo di una comunicazione bonaria riporti la fonte dell'indirizzo utilizzato.
  # ====================================================================================================================

  @tracciaturaIndagineInLoco @PAPER_SOURCE_INFORMAL_02_1_A @work-item:QA-17058
  Scenario: [PAPER_SOURCE_INFORMAL_02_1_A] Comunicazione bonaria con indirizzo fornito dal mittente
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | MessaMora                               |
      | messageId                | ${NEW-IT}                               |
      | subject                  | Test tracciatura fonte indirizzo        |
      | recipientType            | PF                                      |
      | taxId                    | FRMTTR76M06B715E                        |
      | denomination             | Ettore Fieramosca                       |
      | email                    | suppressionlist@simulator.amazonses.com |
      | digitalDomicile          | NULL                                    |
      | physical_address_address | Via@OK_RS                               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE" della notifica bonaria
    Then l'invio cartaceo della comunicazione bonaria del destinatario 0 riporta come fonte dell'indirizzo "MITTENTE"

  @tracciaturaIndagineInLoco @PAPER_SOURCE_INFORMAL_02_1_B @work-item:QA-17058
  Scenario: [PAPER_SOURCE_INFORMAL_02_1_B] Comunicazione bonaria con indirizzo verificato sui registri nazionali per una persona fisica
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | MessaMora                               |
      | messageId        | ${NEW-IT}                               |
      | subject          | Test tracciatura fonte indirizzo        |
      | recipientType    | PF                                      |
      | taxId            | ${Utenza con Indirizzo Valido da ANPR}  |
      | denomination     | PF censito vas                          |
      | email            | suppressionlist@simulator.amazonses.com |
      | digitalDomicile  | NULL                                    |
      | physical_address | ${PHYSICAL_ADDRESS_NULL}                |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE" della notifica bonaria
    Then l'invio cartaceo della comunicazione bonaria del destinatario 0 riporta come fonte dell'indirizzo "ANPR"

  @tracciaturaIndagineInLoco @PAPER_SOURCE_INFORMAL_02_1_C @work-item:QA-17058
  Scenario: [PAPER_SOURCE_INFORMAL_02_1_C] Comunicazione bonaria con indirizzo verificato sui registri nazionali per una persona giuridica
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId       | MessaMora                        |
      | messageId        | ${NEW-IT}                        |
      | subject          | Test tracciatura fonte indirizzo |
      | recipientType    | PG                               |
      | taxId            | 01113570442                      |
      | denomination     | PG Censito VAS                   |
      | digitalDomicile  | example@FAIL-pecFirstKO.it       |
      | physical_address | ${PHYSICAL_ADDRESS_NULL}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE" della notifica bonaria
    Then l'invio cartaceo della comunicazione bonaria del destinatario 0 riporta come fonte dell'indirizzo "REGISTRO_IMPRESE"

  # ====================================================================================================================
  # SCENARIO 3 - Rilascio asincrono
  # CASO DI TEST 3.1: Verificare che le notifiche con recapito cartaceo in corso al momento del rilascio si concludano
  # correttamente.
  #
  # Non è una casistica ricorrente: le notifiche vanno preparate a mano prima di un rilascio richiesto apposta.
  # - Preparazione (@rilascioAsincronoPre): si lancia a mano TracciaturaIndagineInLocoPreRilascioTest prima del rilascio.
  #   Le spedizioni vengono fermate con una sequence senza esito finale e gli IUN annotati nel log
  #   (pn.ripresa.iun.<chiave>=<IUN>). Questi scenari non fanno parte dell'esecuzione ordinaria.
  # - Ripresa (@rilascioAsincronoPost): fa parte dell'esecuzione ordinaria. Se viene passato lo IUN di una notifica
  #   preparata (-Dpn.ripresa.iun.<chiave>=<IUN>) la spedizione viene sbloccata inviando gli esiti tramite il consolidatore
  #   e se ne verifica la conclusione; altrimenti lo scenario viene saltato.
  # ====================================================================================================================

  @tracciaturaIndagineInLoco @rilascioAsincronoPre @PAPER_SOURCE_ROLLOUT_03_1_A @work-item:QA-17058
  Scenario: [PAPER_SOURCE_ROLLOUT_03_1_A_PRE] Notifica legale con primo invio cartaceo in corso al momento del rilascio - fase 1
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL              |
      | physicalAddress_address | Via@OK_AR_BLOCKED |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018"
    Then si annota lo IUN della notifica con la chiave "PAPER_SOURCE_ROLLOUT_03_1_A"

  @tracciaturaIndagineInLoco @rilascioAsincronoPost @PAPER_SOURCE_ROLLOUT_03_1_A @work-item:QA-17058
  Scenario: [PAPER_SOURCE_ROLLOUT_03_1_A_POST] Notifica legale con primo invio cartaceo in corso al momento del rilascio - fase 2
    Given si riprende la notifica annotata con la chiave "PAPER_SOURCE_ROLLOUT_03_1_A" inviata dal "Comune_Multi"
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | deliveryFailureCause | discoveredAddress |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002D  | M01                  | Via@OK_AR         |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | attachment_1 |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002E  | Plico        |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode |
      | AR          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRN002F  |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then il primo invio cartaceo del destinatario 0 non riporta la fonte dell'indirizzo
    And l'esito del primo tentativo del destinatario 0 riporta che l'indagine in loco è stata effettuata
    And il secondo invio cartaceo del destinatario 0 riporta come fonte dell'indirizzo "INDAGINE"
    And il secondo invio cartaceo del destinatario 0 riporta che l'indagine in loco è stata effettuata

  @tracciaturaIndagineInLoco @rilascioAsincronoPre @PAPER_SOURCE_ROLLOUT_03_1_B @work-item:QA-17058
  Scenario: [PAPER_SOURCE_ROLLOUT_03_1_B_PRE] Comunicazione bonaria con invio cartaceo in corso al momento del rilascio - fase 1
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId               | MessaMora                               |
      | messageId                | ${NEW-IT}                               |
      | subject                  | Test tracciatura fonte indirizzo        |
      | recipientType            | PF                                      |
      | taxId                    | FRMTTR76M06B715E                        |
      | denomination             | Ettore Fieramosca                       |
      | email                    | suppressionlist@simulator.amazonses.com |
      | digitalDomicile          | NULL                                    |
      | physical_address_address | Via@OK_START_RS                         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_ANALOG_MESSAGE_PROGRESS" della notifica bonaria
    Then si annota lo IUN della notifica con la chiave "PAPER_SOURCE_ROLLOUT_03_1_B"

  @tracciaturaIndagineInLoco @rilascioAsincronoPost @PAPER_SOURCE_ROLLOUT_03_1_B @work-item:QA-17058
  Scenario: [PAPER_SOURCE_ROLLOUT_03_1_B_POST] Comunicazione bonaria con invio cartaceo in corso al momento del rilascio - fase 2
    Given si riprende la comunicazione bonaria annotata con la chiave "PAPER_SOURCE_ROLLOUT_03_1_B"
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode |
      | RS          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRS001C  |
    Then l'esito dell'invio cartaceo della comunicazione bonaria del destinatario 0 è presente
    And l'invio cartaceo della comunicazione bonaria del destinatario 0 non riporta la fonte dell'indirizzo

  @tracciaturaIndagineInLoco @rilascioAsincronoPre @PAPER_SOURCE_ROLLOUT_03_1_C @work-item:QA-17058
  Scenario: [PAPER_SOURCE_ROLLOUT_03_1_C_PRE] Notifica legale con secondo invio cartaceo in corso al momento del rilascio - fase 1
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                                                                                                                                                                         |
      | physicalAddress_address | Via @sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECRN002D[DISCOVERY;FAILCAUSE:M01].5s-RECRN002E[DOC:Plico;DOC:Indagine].5s-RECRN002F@discovered.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-CON018 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON018"
    Then si annota lo IUN della notifica con la chiave "PAPER_SOURCE_ROLLOUT_03_1_C"

  @tracciaturaIndagineInLoco @rilascioAsincronoPost @PAPER_SOURCE_ROLLOUT_03_1_C @work-item:QA-17058
  Scenario: [PAPER_SOURCE_ROLLOUT_03_1_C_POST] Notifica legale con secondo invio cartaceo in corso al momento del rilascio - fase 2
    Given si riprende la notifica annotata con la chiave "PAPER_SOURCE_ROLLOUT_03_1_C" inviata dal "Comune_Multi"
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001A  |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode | attachment_1 |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001B  | AR           |
    And viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode |
      | AR          | ATTEMPT_1 | PCRETRY_0 | RECINDEX_0 | RECRN001C  |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then il secondo invio cartaceo del destinatario 0 non riporta la fonte dell'indirizzo
    And il secondo invio cartaceo del destinatario 0 non riporta l'indagine in loco
    And l'esito del secondo tentativo del destinatario 0 è presente
