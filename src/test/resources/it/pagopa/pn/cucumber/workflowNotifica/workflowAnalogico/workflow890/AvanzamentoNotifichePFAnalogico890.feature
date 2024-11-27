Feature: avanzamento notifiche b2b con workflow cartaceo 890

  Background:
    Given viene rimossa se presente la pec di piattaforma di "Mario Gherkin"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_1] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico @con020 @con020success @con020exp
  Scenario: [B2B_TIMELINE_ANALOG_890_2] Attesa elemento di timeline SEND_ANALOG_FEEDBACK_fail_890_scenario negativo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL         |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con responseStatus "OK"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON020 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_3] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-Discovery_890_scenario positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                   |
      | physicalAddress_address | Via@fail-Discovery_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @workflowAnalogico
  Scenario: [B2B-TIMELINE_HOTFIX-BUG-PEC_3] ordinamento non rispettato nella costruzione della richiesta di postalizzazione
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | document           | DOC_1_PG; DOC_2_PG          |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                 |
      | physicalAddress_address | Via@ok_890           |
      | payment_f24             | PAYMENT_F24_FLAT     |
      | title_payment           | F24_STANDARD_GHERKIN |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And si verifica il contenuto degli attachments da inviare in via cartacea al destinatario 0 con 5 allegati
    And si verifica che il 1 documento arrivato sia di tipo "AAR"
    And si verifica che il 2 documento arrivato sia di tipo "ATTO"
    And si verifica che il 3 documento arrivato sia di tipo "ATTO"
    And si verifica che il 4 documento arrivato sia di tipo "ATTO"
    And si verifica che il 5 documento arrivato sia di tipo "ATTO"

  @workflowAnalogico
  Scenario: [B2B-TIMELINE_HOTFIX-BUG-PEC_4] inserimento notifica analogica con 120 F24 STANDARD DELIVERY_MODE e controllo coerenza degli allegati cartacei.
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                 |
      | physicalAddress_address | Via@ok_890           |
      | payment_pagoPaForm      | SI                   |
      | payment_f24             | PAYMENT_F24_FLAT     |
      | title_payment           | F24_STANDARD_GHERKIN |
      | payment_multy_number    | 20                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si verifica il contenuto degli attachments da inviare in via cartacea al destinatario 0 con 42 allegati
    And si verifica che il contenuto degli attachments da inviare in via cartacea abbia 20 attachment di tipo "F24"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                 |
      | physicalAddress_address | Via@ok_AR            |
      | payment_pagoPaForm      | SI                   |
      | payment_f24             | PAYMENT_F24_FLAT     |
      | title_payment           | F24_STANDARD_GHERKIN |
      | payment_multy_number    | 20                   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    Then si verifica il contenuto degli attachments da inviare in via cartacea al destinatario 0 con 42 allegati
    And si verifica che il contenuto degli attachments da inviare in via cartacea abbia 20 attachment di tipo "F24"

  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_4] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Test 890 ok      |
      | taxId                   | DVNLRD52D15M059P |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_890       |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_5] Attesa elemento di timeline SEND_ANALOG_FEEDBACK e verifica campo SEND_ANALOG_FEEDBACK positivo
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL         |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And viene verificato il campo sendRequestId dell' evento di timeline "SEND_ANALOG_FEEDBACK"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_6] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campi municipalityDetails e foreignState positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Cucumber Analogic e:
      | digitalDomicile         | NULL         |
      | physicalAddress_address | Via@fail_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato che nell'elemento di timeline della notifica "SEND_ANALOG_DOMICILE" siano configurati i campi municipalityDetails e foreignState


  Scenario: [B2B_TIMELINE_ANALOG_890_7] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_DOMICILE e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il campo serviceLevel dell' evento di timeline "SEND_ANALOG_DOMICILE" sia valorizzato con "REGISTERED_LETTER_890"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_8] Invio notifica digitale ed attesa elemento di timeline PREPARE_ANALOG_DOMICILE e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE"
    And viene verificato il campo serviceLevel dell' evento di timeline "PREPARE_ANALOG_DOMICILE" sia valorizzato con "REGISTERED_LETTER_890"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_9] Invio notifica digitale ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e controllo campo serviceLevel positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And viene verificato il campo serviceLevel dell' evento di timeline "SEND_ANALOG_FEEDBACK" sia valorizzato con "REGISTERED_LETTER_890"


  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_10] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | DVNLRD52D15M059P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_11] Invio notifica ed attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890_NR negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination            | Test 890 Fail 2                              |
      | taxId                   | DVNLRD52D15M059P                             |
      | digitalDomicile         | NULL                                         |
      | physicalAddress_address | Via NationalRegistries@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @workflowAnalogico @con020 @con020success
  Scenario: [B2B_TIMELINE_ANALOG_890_12] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN015 890 momentaneamente non rendicontabile positivo PN-6079
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | denomination | OK-CausaForzaMaggiore_890 |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@OK-CausaForzaMaggiore_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG001C"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON080 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON020 |
      | details_sentAttemptMade | 0 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG015 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "23L"}] |
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG015[FAILCAUSE:C01].5s-RECAG001A.5s-RECAG001B[DOC:23L].5s-RECAG001C"


  @workflowAnalogico @con020 @con020success @con020ToCheck
  Scenario: [B2B_TIMELINE_ANALOG_890_13] Invio notifica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK con deliveryDetailCode RECRN013 890 momentaneamente non rendicontabile positivo PN-6079
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | denomination | OK-NonRendicontabile_890 |
      | digitalDomicile | NULL |
      | physicalAddress_address | via@OK-NonRendicontabile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG001C"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON080 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON020 |
      | details_sentAttemptMade | 0 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG013 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "23L"}] |
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG013@retry.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG001A.5s-RECAG001B[DOC:23L].5s-RECAG001C"


  @workflowAnalogico @con020 @con020success
  Scenario: [B2B_TIMELINE_ANALOG_890_14] Attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_FAIL-DiscoveryIrreperibile_890_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON080 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON020 |
      | details_sentAttemptMade | 0 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "Indagine"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG003F |
      | details_sentAttemptMade | 0 |
      | details_deliveryFailureCause | M03 |
      | details_physicalAddress | {"address": "VIA@FAIL-DISCOVERYIRREPERIBILE_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
      | details_responseStatus | KO |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON080 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON020 |
      | details_sentAttemptMade | 1 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | details_sentAttemptMade | 1 |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG003F |
      | details_sentAttemptMade | 1 |
      | details_deliveryFailureCause | M03 |
      | details_physicalAddress | {"address": "via@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG003D[FAILCAUSE:M03].5s-RECAG003E[DOC:Plico].5s-RECAG003F", "municipality": "Milano", "municipalityDetails": null, "at": null, "addressDetails": null, "province": "MI", "zip": "20121", "foreignState": "Italia"} |
      | details_responseStatus | KO |
    # TODO 21/11/2024 questo è il physicalAddress restituito dalla pittaforma; verificare che sia effettivamente il risultato atteso
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG003D[DISCOVERY;FAILCAUSE:M03].5s-RECAG003E[DOC:Plico;DOC:Indagine].5s-RECAG003F@discovered.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG003D[FAILCAUSE:M03].5s-RECAG003E[DOC:Plico].5s-RECAG003F"

  @workflowAnalogico
  Scenario: [B2B_TIMELINE_ANALOG_890_15] Attesa elemento di timeline REFINEMENT con physicalAddress OK-WO-011B
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL           |
      | physicalAddress_address | via@OK-WO-011B |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"


  @workflowAnalogico @con020 @con020success
  Scenario: [B2B_TIMELINE_ANALOG_890_16] Attesa elemento di timeline REFINEMENT con physicalAddress OK-REC008_890 - PN-9929
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL              |
      | physicalAddress_address | Via@OK-REC008_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON020 |
      | details_sentAttemptMade | 0 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG008B |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG008C |
      | details_sentAttemptMade | 0 |
    # "@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.5s-RECAG008A.5s-RECAG008B[DOC:ARCAD;DOC:23L].5s-RECAG012.5s-RECAG008C"

  @workflowAnalogico @uatEnvCondition @con020 @con020success
  Scenario: [B2B_TIMELINE_ANALOG_890_17]  PA mittente: invio notifica analogica FAIL-DiscoveryIrreperibileBadCAP_890 - PN-10146
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                                     |
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibileBadCAP_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG001C" tentativo "ATTEMPT_1"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON080 |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | CON020 |
      | details_sentAttemptMade | 0 |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "Indagine"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | details_sentAttemptMade | 0 |
      | details_attachments | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG003F |
      | details_sentAttemptMade | 0 |
      | details_deliveryFailureCause | M03 |
      | details_physicalAddress | {"address": "VIA@FAIL-DISCOVERYIRREPERIBILEBADCAP_890", "municipality": "COSENZA", "municipalityDetails": "", "at": "Presso", "addressDetails": "SCALA B", "province": "CS", "zip": "87100", "foreignState": "ITALIA"} |
      | details_responseStatus | KO |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | details_sentAttemptMade | 1 |
      | details_attachments | [{"documentType": "23L"}] |