Feature: SEND - Tracciatura dell'indagine in loco e della fonte dell'indirizzo per gli invii cartacei

  # PST - Tracciatura Indagine in loco e fonte secondo invio (Epic PN-18659)
  #
  # Dati di test e ambienti:
  # - PA abilitata al VAS -> Comune_Multi (il VAS si attiva solo con indirizzo fisico non valorizzato).
  # - PF con indirizzo su ANPR -> Utenza con Indirizzo Valido da ANPR (config/taxids-{env}.properties): in DEV e TEST
  #   ANPR è il mock, in UAT è ANPR reale (ambiente di validazione) e il codice fiscale configurato cambia di conseguenza.
  #   Nelle bonarie lo stesso codice fiscale si ottiene con taxId = ${Utenza con Indirizzo Valido da ANPR}.
  # - PG censita sul Registro Imprese -> 01113570442: il Registro Imprese (Infocamere) è mock in tutti gli ambienti.
  # - Tutti gli scenari sono eseguibili in DEV, TEST e UAT.
  #

  # ====================================================================================================================
  # SCENARIO 1 - Notifiche legali con invio cartaceo
  # CASO DI TEST 1.1: Verificare che, lungo tutto il percorso di recapito cartaceo di una notifica legale, vengano tracciate
  # la fonte dell'indirizzo utilizzato e l'eventuale indagine in loco del postino.
  # ====================================================================================================================

  # PST 1.1.A - Indirizzo verificato sui registri nazionali, destinatario persona fisica e consegna al primo tentativo.
  # Il VAS recupera l'indirizzo da ANPR: il primo invio deve riportare la fonte ANPR. Nessuna indagine in loco.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_A
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
  # Questo scenario verifica la fonte del primo invio: indirizzo recuperato dal VAS sul Registro Imprese.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_B_1
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

  # PST 1.1.B.2 - Verifica l'indagine in loco e la fonte del secondo invio per una persona giuridica.
  # Il postino individua un nuovo indirizzo che viene utilizzato (sequenza FAIL-Discovery_AR): indagine effettuata sull'esito
  # del primo tentativo e secondo invio con fonte INDAGINE. Per l'SRS la fonte del secondo invio non dipende da quella del primo.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_B_2
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

  # PST 1.1.C - Indirizzo del mittente, persona fisica, tentativo fallito e indirizzo individuato dal postino non utilizzabile.
  # La sequenza BadCAP_890 restituisce un indirizzo del postino con CAP non valido: non supera la normalizzazione e per il
  # secondo tentativo si ricorre ad ANPR.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_C
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

  # PST 1.1.D - Destinatario persona giuridica, primo tentativo fallito senza indagine in loco del postino (sequenza FAIL-Irreperibile_AR).
  # Il secondo invio parte verso l'indirizzo dei registri nazionali: per una persona giuridica la fonte è REGISTRO_IMPRESE.
  # Come per 1.1.C si attende l'esito del primo tentativo: le verifiche sul secondo invio attendono che l'elemento sia prodotto.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_D
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

  # PST 1.1.E - Primo tentativo fallito e nessun indirizzo alternativo disponibile (né dal postino né da ANPR).
  # La notifica si conclude come oggi con destinatario irreperibile, senza secondo invio.
  # FNTLCU80T25F205R non ha indirizzo né sul mock ANPR né su ANPR reale (UAT: D00, COMPLETELY_UNREACHABLE).
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_E
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

  # PST 1.1.F - Notifica con più destinatari, di cui solo uno verificato sui registri nazionali.
  # Ogni destinatario riporta la propria fonte: ANPR per il destinatario 0 (VAS), MITTENTE per il destinatario 1.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_1_F
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

  # PST 1.2.A - Raccomandata semplice di una notifica legale con indirizzo fornito dal mittente.
  # Il domicilio digitale non funzionante fa fallire il recapito digitale e parte la raccomandata semplice.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_2_A
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

  # PST 1.2.B - Raccomandata semplice di una notifica legale con indirizzo verificato sui registri nazionali.
  # Il VAS si attiva per ogni destinatario senza indirizzo fisico, indipendentemente dal domicilio digitale.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_LEGAL_01_2_B
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

  # PST 2.1.A - Comunicazione bonaria con canale cartaceo e indirizzo fornito dal mittente.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_INFORMAL_02_1_A
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

  # PST 2.1.B - Comunicazione bonaria con indirizzo verificato sui registri nazionali, destinatario persona fisica.
  # Il codice fiscale è risolto per ambiente come per le notifiche legali (Utenza con Indirizzo Valido da ANPR).
  @tracciaturaIndagineInLoco @PAPER_SOURCE_INFORMAL_02_1_B
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

  # PST 2.1.C - Comunicazione bonaria con indirizzo verificato sui registri nazionali, destinatario persona giuridica.
  @tracciaturaIndagineInLoco @PAPER_SOURCE_INFORMAL_02_1_C
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
  # Il mock ferma la notifica solo durante una spedizione: il passaggio tra l'esito di un tentativo e l'invio successivo
  # avviene subito all'interno di SEND e non è riproducibile a cavallo del rilascio.
  # ====================================================================================================================

  # PST 3.1.A - Notifica legale con primo invio cartaceo in corso al rilascio, che fallisce dopo il rilascio con indagine in loco.
  # OK_AR_BLOCKED si ferma dopo l'affidamento al recapitista (CON018): il primo invio è preparato prima del rilascio.
  @tracciaturaIndagineInLoco @rilascioAsincronoPre @PAPER_SOURCE_ROLLOUT_03_1_A
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

  # Gli esiti inviati riproducono il primo tentativo di FAIL-Discovery_AR (con il solo allegato Plico, come nei test di
  # correzione timeline): il postino individua un nuovo indirizzo con una sequence di consegna (Via@OK_AR), che viene
  # utilizzato per il secondo invio.
  @tracciaturaIndagineInLoco @rilascioAsincronoPost @PAPER_SOURCE_ROLLOUT_03_1_A
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

  # PST 3.1.B - Comunicazione bonaria con invio cartaceo in corso al rilascio.
  # OK_START_RS si ferma dopo l'affidamento al recapitista (CON020): l'invio è preparato prima del rilascio.
  @tracciaturaIndagineInLoco @rilascioAsincronoPre @PAPER_SOURCE_ROLLOUT_03_1_B
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

  # L'esito inviato completa la sequenza OK_RS (CON080, CON020, RECRS001C).
  @tracciaturaIndagineInLoco @rilascioAsincronoPost @PAPER_SOURCE_ROLLOUT_03_1_B
  Scenario: [PAPER_SOURCE_ROLLOUT_03_1_B_POST] Comunicazione bonaria con invio cartaceo in corso al momento del rilascio - fase 2
    Given si riprende la comunicazione bonaria annotata con la chiave "PAPER_SOURCE_ROLLOUT_03_1_B"
    When viene invocato il consolidatore con i seguenti dati:
      | productType | attemptId | pcRetry   | recIndex   | statusCode |
      | RS          | ATTEMPT_0 | PCRETRY_0 | RECINDEX_0 | RECRS001C  |
    Then l'esito dell'invio cartaceo della comunicazione bonaria del destinatario 0 è presente
    And l'invio cartaceo della comunicazione bonaria del destinatario 0 non riporta la fonte dell'indirizzo

  # PST 3.1.C - Notifica legale con secondo invio cartaceo in corso al rilascio.
  # Il primo tentativo fallisce prima del rilascio con un indirizzo individuato dal postino (@discovered) la cui sequence si
  # ferma dopo l'affidamento al recapitista (CON018): anche il secondo invio è preparato prima del rilascio.
  @tracciaturaIndagineInLoco @rilascioAsincronoPre @PAPER_SOURCE_ROLLOUT_03_1_C
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

  # Gli esiti inviati completano il secondo tentativo con una consegna (RECRN001A, RECRN001B con ricevuta AR, RECRN001C).
  @tracciaturaIndagineInLoco @rilascioAsincronoPost @PAPER_SOURCE_ROLLOUT_03_1_C
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
