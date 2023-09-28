Feature: Workflow analogico

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-1] Invio notifica con percorso analogico 890 con verifica elementi di timeline SEND_ANALOG_PROGRESS con legalFactId di category ANALOG_DELIVERY
  e documentType 23L, SEND_ANALOG_FEEDBACK e ANALOG_SUCCESS_WORKFLOW
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-2] Invio notifica con percorso analogico. Successo giacenza delegato 890 (OK-GiacenzaDelegato-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-PersonaAbilitata_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG002B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG002C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-PERSONAABILITATA_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-3] Invio notifica con percorso analogico 890. Successo al secondo tentativo invio 890.
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Retry_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 10    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | progressIndex | 1 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG004 |
      | details_deliveryFailureCause | F01 F02 F03 F04 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | progressIndex | 3 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-4] Invio notifica con percorso analogico. Successo invio RS (OK_RS)
    Given viene generata una nuova notifica
      | subject | notifica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 4 |
      | numCheck    | 10    |
    And si aggiunge alla sequence il controllo che "DIGITAL_FAILURE_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "DIGITAL_FAILURE_WORKFLOW"
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2 |
      | numCheck    | 2    |
    And si aggiunge alla sequence il controllo che "PREPARE_SIMPLE_REGISTERED_LETTER" esista
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@OK_RS", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER" esista
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@OK_RS", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost | 181 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-5] Invio notifica con percorso analogico. Successo invio RS (OK_RS) in cui la notifica viene visualizzata prima
  dell’evento SEND_SIMPLE_REGISTERED_LETTER
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario "Mr. UtenteQualsiasi"
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@ok_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 4 |
      | numCheck    | 10    |
    And si aggiunge alla sequence il controllo che "DIGITAL_FAILURE_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
    And viene verificata la sequence
    And la notifica può essere correttamente recuperata da "Mr. UtenteQualsiasi"
    And viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2 |
      | numCheck    | 4    |
    And si aggiunge alla sequence il controllo che "NOTIFICATION_VIEWED" esista
      | details_recIndex | 0 |
      | legalFactsIds | [{"category": "RECIPIENT_ACCESS"}] |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" non esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-6] Invio notifica con percorso analogico. Successo al secondo tentativo invio RS (OK-Retry_RS).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@OK-Retry_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 4 |
      | numCheck    | 15    |
    And si aggiunge alla sequence il controllo che "DIGITAL_FAILURE_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | legalFactsIds | [{"category": "DIGITAL_DELIVERY"}] |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And si aggiunge alla sequence il controllo che "PREPARE_SIMPLE_REGISTERED_LETTER" esista
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_RS", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER" esista
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_RS", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost | 181 |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECRS006 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-7] Invio notifica con percorso analogico. Successo giacenza lte 890 (OK-Giacenza-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA-LTE10_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-8] Invio notifica con percorso analogico. Successo giacenza gt 890 (OK-Giacenza-gt10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 3 |
      | numCheck    | 10    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011A |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA-GT10_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005C |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | loadTimeline | true |
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-9] Invio notifica con percorso analogico. Successo giacenza 890 gt 23L(OK-Giacenza-gt10-23L_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011A |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG005C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA-GT10-23L_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-10] Invio notifica con percorso analogico. Successo giacenza 890 gt 10(OK-GiacenzaDelegato-gt10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011A |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZADELEGATO-GT10_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006C |
    And viene verificata la sequence

  @e2e @fail
  Scenario: [E2E-PF_WF-ANALOG-11] Partenza workflow cartaceo se viene inviato un messaggio di cortesia
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. EmailCortesia"
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2 |
      | numCheck    | 10    |
    And si aggiunge alla sequence il controllo che "SEND_COURTESY_MESSAGE" esista
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si aggiunge alla sequence il controllo che "SCHEDULE_ANALOG_WORKFLOW" esista
      | details_recIndex | 0 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_DOMICILE" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificata la sequence
    And controlla che il timestamp di "SEND_ANALOG_DOMICILE" sia dopo quello di invio e di attesa di lettura del messaggio di cortesia
      | NULL | NULL |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-12] Invio notifica con percorso analogico. Successo giacenza delegato 890 (OK-GiacenzaDelegato-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZADELEGATO-LTE10_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-13] Invio notifica con percorso analogico. Successo giacenza delegato gt 23L 890 (OK-GiacenzaDelegato-gt10-23L_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 10    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011A |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG006C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZADELEGATO-GT10-23L_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-14] Invio notifica con percorso analogico. Fallimento giacenza gt 890 (FAIL-Giacenza-gt10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 12    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL-GIACENZA-GT10_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007C |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-15] Invio notifica con percorso analogico. Fallimento giacenza gt 23L 890 (FAIL-Giacenza-gt10-23L_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 10    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL-GIACENZA-GT10-23L_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-16] Invio notifica con percorso analogico. Compiuta giacenza 890 (OK-CompiutaGiacenza_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 12    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011A |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNAG012 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG011B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-COMPIUTAGIACENZA_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG008B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG008C |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-17] Invio notifica con percorso analogico. Successo giacenza ar (OK-Giacenza_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 12    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN003B |
      | details_attachments | [{"documentType": "AR"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN003C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-18] Successo invio raccomandata semplice
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-19] Invio notifica con percorso analogico. Fallimento giacenza lte 890 (FAIL-Giacenza-lte10_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 12    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG007C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL-GIACENZA-LTE10_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-20] Invio notifica con percorso analogico. Fallimento giacenza AR (FAIL-Giacenza_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 12    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN004B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN004C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL-GIACENZA_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-21] Fallimento invio raccomandata semplice
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890           |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN002B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN002C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificata la sequence

  @e2e @ignore
  Scenario: [E2E-PF_WF-ANALOG-22] Invio notifica con percorso analogico. Successo giacenza gt 10 AR (OK-Giacenza-gt10_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Giacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 12    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNRN012 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN003B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN003C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-GIACENZA-GT10_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-23] Invio notifica con percorso analogico. Fallimento giacenza gt 10 AR (FAIL-Giacenza-gt10_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Giacenza-gt10_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 12    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | PNRN012 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN004B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN004C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL-GIACENZA-GT10_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e @fail
  Scenario: [E2E-PF_WF-ANALOG-24] Invio notifica con percorso analogico. Fallimento compiuta giacenza AR (FAIL-CompiutaGiacenza_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-CompiutaGiacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN011 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN005B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN005C |
    And si aggiunge alla sequence il controllo che "ANALOG_FAILURE_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL-COMPIUTAGIACENZA_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-25] Invio notifica con percorso analogico. Successo seconda raccomandata AR (OK-Retry_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-Retry_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | progressIndex | 1 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN006 |
      | details_deliveryFailureCause | F01 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | progressIndex | 3 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-RETRY_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-26] Invio notifica con percorso analogico. Successo non rendicontabile AR (OK-NonRendicontabile_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-NonRendicontabile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | progressIndex | 1 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN013 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | progressIndex | 3 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-NONRENDICONTABILE_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-27] Invio notifica con percorso analogico. Successo causa forza maggiore AR (OK-CausaForzaMaggiore_AR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-CausaForzaMaggiore_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN015 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "AR"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRN001C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-CAUSAFORZAMAGGIORE_AR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-28] Invio notifica con percorso analogico. Fallimento 890 (FAIL_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003C |
      | details_deliveryFailureCause | M02 M05 M06 M07 M08 M09 |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-29] Invio notifica con percorso analogico. Fallimento irreperibile 890 (FAIL-Irreperibile_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003F |
      | details_deliveryFailureCause | M03 |
    And si aggiunge alla sequence il controllo che "ANALOG_FAILURE_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-30] Invio notifica con percorso analogico. Fallimento primo tentativo e successo secondo tentativo 890 (FAIL-Discovery_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Discovery_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Indagine"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003F |
      | details_deliveryFailureCause | M03 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG001C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_physicalAddress | {"address": "via@sequence.5s-CON080.5s-RECAG001A.5s-RECAG001B[DOC:23L].5s-RECAG001C", "municipality": "Milan", "province": "MI", "zip": "20121", "foreignState": "Italy"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-31] Invio notifica con percorso analogico. Fallimento primo tentativo e secondo tentativo 890 (FAIL-DiscoveryIrreperibile_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Indagine"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG003F |
      | details_deliveryFailureCause | M03 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG003E |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | details_deliveryDetailCode | RECAG003F |
      | details_deliveryFailureCause | M03 |
    And si aggiunge alla sequence il controllo che "ANALOG_FAILURE_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_FAILURE_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-32] Invio notifica con percorso analogico. Successo non rendicontabile 890 (OK-NonRendicontabile_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-NonRendicontabile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | progressIndex | 1 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG013 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | progressIndex | 3 |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-NONRENDICONTABILE_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-33] Invio notifica con percorso analogico. Successo causa forza maggiore 890 (OK-CausaForzaMaggiore_890).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-CausaForzaMaggiore_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 2.5 |
      | numCheck    | 8    |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG015 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001B |
      | legalFactsIds | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments | [{"documentType": "23L"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECAG001C |
    And si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK-CAUSAFORZAMAGGIORE_890", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And viene inizializzata la sequence per il controllo sulla timeline
      | numCheck    | 1    |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | details_recIndex | 0 |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-34] Invio notifica con percorso analogico. Successo RIS (OK_RIS).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@OK_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 3 |
      | numCheck    | 15    |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK_RIS", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost | 181 |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080  |
    And viene verificata la sequence

  @e2e @test
  Scenario: [E2E-PF_WF-ANALOG-35] Invio notifica con percorso analogico. Fallimento RIS (FAIL_RIS).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL_RIS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then viene inizializzata la sequence per il controllo sulla timeline
      | pollingTimeMultiplier | 3.5 |
      | numCheck    | 10    |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL_RIS", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost | 133 |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRSI004B |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRSI004C |
    And viene verificata la sequence

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-36] Invio notifica con percorso analogico. Successo RIR (OK_RIR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@OK_RIR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI001 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI002 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI003C |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | loadTimeline | true |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-37] Invio notifica con percorso analogico. Fallimento RIR (FAIL_RIR).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL_RIR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL_RIR", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI001 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI002 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI004B |
      | details_attachments | [{"documentType": "Plico"}] |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" esista
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRI004C |
    And si aggiunge alla sequence il controllo che "SCHEDULE_REFINEMENT" esista
      | details_recIndex | 0 |
    And viene schedulato il perfezionamento per decorrenza termini per il caso "ANALOG_SUCCESS_WORKFLOW"
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
    And si attende che sia presente il perfezionamento per decorrenza termini
      | details_recIndex | 0 |
    And si aggiunge alla sequence il controllo che "REFINEMENT" esista
      | loadTimeline | true |
      | details_recIndex | 0 |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-38] Invio notifica con percorso analogico. Fallimento RS (FAIL_RS).
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address | Via@FAIL_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_physicalAddress | {"address": "VIA@FAIL_RS", "municipality": "MILANO", "municipalityDetails": "MILANO", "at": "Presso", "addressDetails": "SCALA B", "province": "MI", "zip": "87100", "foreignState": "ITALIA"} |
      | details_analogCost | 181 |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | CON080  |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRS002C |
      | details_deliveryFailureCause | M07 |
    And si aggiunge alla sequence il controllo che "SEND_SIMPLE_REGISTERED_LETTER_PROGRESS" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 0 |
      | details_deliveryDetailCode | RECRS002B |
      | details_attachments | [{"documentType": "Plico"}] |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-39] Invio notifica con percorso analogico. Primo tentativo fallisce, secondo non viene eseguito.
  (sequenza FAIL-Irreperibile_AR)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "ANALOG_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And vengono verificati gli eventi precedenti in ordine
      | seq0 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "CON080", "recIndex": 0} |
      | seq1 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "RECRN002E", "recIndex": 0, "attachments": [{"documentType": "Plico"}]} |
      | seq2 | {"category": "SEND_ANALOG_FEEDBACK", "deliveryDetailCode": "RECRN002F", "recIndex": 0, "sent_attempt_made": 0, "deliveryFailureCause": "M04"} |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | progressIndex | 1 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-40] Invio notifica con percorso analogico. Primo tentativo fallisce, secondo viene eseguito va a buon fine.
  (sequenza FAIL-Discovery_AR)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Discovery_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And vengono verificati gli eventi precedenti in ordine
      | seq0 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "CON080", "recIndex": 0, "sent_attempt_made": 0} |
      | seq1 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "RECRN002E", "recIndex": 0, "sent_attempt_made": 0, "attachments": [{"documentType": "Plico"}]} |
      | seq1-parallel | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "RECRN002E", "recIndex": 0, "sent_attempt_made": 0, "attachments": [{"documentType": "Indagine"}]} |
      | seq2 | {"category": "SEND_ANALOG_FEEDBACK", "deliveryDetailCode": "RECRN002F", "recIndex": 0, "sent_attempt_made": 0, "deliveryFailureCause": "M01"} |
      | seq3 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "CON080", "recIndex": 0, "sent_attempt_made": 1} |
      | seq4 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "RECRN001B", "recIndex": 0, "sent_attempt_made": 1, "attachments": [{"documentType": "AR"}]} |
      | seq5 | {"category": "SEND_ANALOG_FEEDBACK", "deliveryDetailCode": "RECRN001C", "recIndex": 0, "sent_attempt_made": 1} |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-41] Invio notifica con percorso analogico. Primo tentativo fallisce, secondo viene eseguito e fallisce anche lui.
  (sequenza FAIL-DiscoveryIrreperibile_AR)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | AR_REGISTERED_LETTER |
    And destinatario "Mr. NoIndirizzi"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-DiscoveryIrreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "ANALOG_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And vengono verificati gli eventi precedenti in ordine
      | seq0 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "CON080", "recIndex": 0, "sent_attempt_made": 0} |
      | seq1 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "RECRN002E", "recIndex": 0, "sent_attempt_made": 0, "attachments": [{"documentType": "Plico"}]} |
      | seq1-parallel | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "RECRN002E", "recIndex": 0, "sent_attempt_made": 0, "attachments": [{"documentType": "Indagine"}]} |
      | seq2 | {"category": "SEND_ANALOG_FEEDBACK", "deliveryDetailCode": "RECRN002F", "recIndex": 0, "sent_attempt_made": 0, "deliveryFailureCause": "M01"} |
      | seq3 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "CON080", "recIndex": 0, "sent_attempt_made": 1} |
      | seq4 | {"category": "SEND_ANALOG_PROGRESS", "deliveryDetailCode": "RECRN002E", "recIndex": 0, "sent_attempt_made": 1, "attachments": [{"documentType": "Plico"}]} |
      | seq5 | {"category": "SEND_ANALOG_FEEDBACK", "deliveryDetailCode": "RECRN002F", "recIndex": 0, "sent_attempt_made": 1, "deliveryFailureCause": "M03"} |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-42] Partenza workflow cartaceo se non viene inviato un messaggio di cortesia
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. EmailCortesia"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@OK-GiacenzaDelegato-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "SEND_ANALOG_DOMICILE" esista
      | loadTimeline            | true     |
      | pollingTime             | 30000   |
      | numCheck                | 20       |
      | details_recIndex        | 0        |
      | details_sentAttemptMade | 0        |
    And si aggiunge alla sequence il controllo che "SCHEDULE_ANALOG_WORKFLOW" esista
      | NULL | NULL |
    And controlla che il timestamp di "SEND_ANALOG_DOMICILE" sia dopo quello di invio e di attesa di lettura del messaggio di cortesia
      | NULL | NULL |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-43] Invio analogico fallimento al primo tentativo e Ok al secondo recuperato da ANPR.
  (sequence FAIL-Irreperibile_AR)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. UtenteIndirizzoANPRGiusto"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And vengono verificati gli eventi precedenti in ordine
      | seq0 | {"category": "SEND_ANALOG_FEEDBACK", "recIndex": 0, "sent_attempt_made": 0, "deliveryDetailCode": "RECRN002F", "deliveryFailureCause": "M04"} |
      | seq1 | {"category": "SEND_ANALOG_FEEDBACK", "recIndex": 0, "sent_attempt_made": 1, "deliveryDetailCode": "RECAG001C"} |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-44] Invio analogico fallimento al primo tentativo e indirizzo di ANPR è uguale a quello del primo tentativo
  (sequence FAIL-Irreperibile_AR)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. UtenteIndirizzoANPRIrreperibile"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via NationalRegistries @FAIL-Irreperibile_AR 5 |
      | physicalAddress_at | [blank] |
      | physicalAddress_addressDetails | [blank] |
      | physicalAddress_zip | 20122 |
      | physicalAddress_municipality | MILANO |
      | physicalAddress_municipalityDetails | [blank] |
      | physicalAddress_province | MI |
      | physicalAddress_State | Italia |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "ANALOG_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And vengono verificati gli eventi precedenti in ordine
      | seq0 | {"category": "SEND_ANALOG_FEEDBACK", "recIndex": 0, "sent_attempt_made": 0, "deliveryDetailCode": "RECRN002F", "deliveryFailureCause": "M04"} |
      | seq1 | {"category": "PREPARE_ANALOG_DOMICILE", "recIndex": 0, "sent_attempt_made": 1} |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_PROGRESS" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |
      | progressIndex | 1 |
    And si aggiunge alla sequence il controllo che "SEND_ANALOG_FEEDBACK" non esista
      | details | NOT_NULL |
      | details_recIndex | 0 |
      | details_sentAttemptMade | 1 |

  @e2e
  Scenario: [E2E-PF_WF-ANALOG-45] Invio analogico fallimento al primo tentativo e Ok al secondo recuperato da ANPR.
  (sequence FAIL-Irreperibile_AR)
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication | REGISTERED_LETTER_890 |
    And destinatario "Mr. UtenteIndirizzoANPRIrreperibile"
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR 10 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then si aggiunge alla sequence il controllo che "ANALOG_FAILURE_WORKFLOW" esista
      | loadTimeline | true |
      | pollingTime | 30000 |
      | numCheck    | 30    |
      | details | NOT_NULL |
      | details_recIndex | 0 |
    And vengono verificati gli eventi precedenti in ordine
      | seq0 | {"category": "SEND_ANALOG_FEEDBACK", "recIndex": 0, "sent_attempt_made": 0, "deliveryDetailCode": "RECRN002F", "deliveryFailureCause": "M04"} |
      | seq1 | {"category": "PREPARE_ANALOG_DOMICILE", "recIndex": 0, "sent_attempt_made": 1 } |
      | seq2 | {"category": "SEND_ANALOG_FEEDBACK", "recIndex": 0, "sent_attempt_made": 1 } |
