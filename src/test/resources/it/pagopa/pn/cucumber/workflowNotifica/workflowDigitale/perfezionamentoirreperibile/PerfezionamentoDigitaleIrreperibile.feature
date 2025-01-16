Feature: Perfezionamento della notifica con destinatario irreperibile a 10g dal deposito AAR e revisione date perfezionamento digitali

  #19
  @addressBook1 @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_3] Invio notifica digitale monodestinatario PF con SERCQ attivo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "SERCQ" digitalAddressSource "PLATFORM" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"

    #21
  @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_4] Invio notifica digitale monodestinatario PF con indirizzo speciale associato
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "PEC" digitalAddressSource "SPECIAL" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"

    #23
  @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_5] Invio notifica digitale monodestinatario PF con indirizzo generale associato
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PF               |
      | taxId           | PRVMNL80A01F205M |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "PEC" digitalAddressSource "GENERAL" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"

  #25
  @addressBook1 @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_6] Invio notifica digitale monodestinatario PF con indirizzo di piattaforma che fallisce al primo tentativo - il secondo tentativo con successo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "test@OK-pecFirstFailSecondSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "PEC" digitalAddressSource "PLATFORM" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"

  #26
  @addressBook1 @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_7] Invio notifica digitale monodestinatario PF su recapito di piattaforma che fallisce al primo tentativo e poi cambia email fallendo di nuovo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "test@OK-pecFirstFailSecondSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene inserito un recapito legale "pec@FAIL.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "PEC" digitalAddressSource "PLATFORM" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" con response status "KO" con la "PEC" "pec@FAIL.it"

  #27
  @addressBook1 @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_8] Invio notifica digitale monodestinatario PF su recapito di piattaforma che fallisce al primo tentativo e poi cancella tutti i recapiti
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "test@OK-pecFirstFailSecondSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And vengono rimossi eventuali recapiti presenti per l'utente
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "PEC" digitalAddressSource "PLATFORM" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"

  #28
  @addressBook1 @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_9] Invio notifica digitale monodestinatario PF su recapito di piattaforma che fallisce al primo tentativo e poi cambia email valida - va con successo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@FAIL-pecFirstKO.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene inserito un recapito legale "example@pecSuccess.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "PEC" digitalAddressSource "PLATFORM" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" con response status "OK" con la "PEC" "example@pecSuccess.it"

  #29
  @addressBook1 @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_10] Invio notifica digitale monodestinatario indirizzo di piattaforma prima fallisce poi va con successo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "test@OK-pecFirstFailSecondSuccess.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene inserito un recapito legale "example@pecSuccess.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "PEC" digitalAddressSource "PLATFORM" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" con response status "OK" con la "PEC" "example@pecSuccess.it"

  #30 ritentativo di NR
  @perfezionamentoIrreperibile @ignore
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_11] Invio notifica digitale monodestinatario PF con Indirizzo generale con esito negativo e secondo tentativo
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_recIndex | 0 |
      | details_digitalAddressSource | GENERAL |
      | details_retryNumber | 0 |
      | details_sentAttemptMade | 0 |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "PEC" digitalAddressSource "GENERAL" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"

  #35
  @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_12] Invio notifica digitale monodestinatario PF con Domicilio Digitale
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Cristoforo Colombo |
      | taxId | CLMCST42R12D969Z |
      | digitalDomicile_address | test@OK-pecFirstFailSecondSuccess.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "test@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | SPECIAL |
      | details_sentAttemptMade | 0 |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" con address type "PEC" digitalAddressSource "SPECIAL" in "OK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"

  @addressBook1 @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_13] Invio notifica digitale monodestinatario PF con indirizzo generale che fallisce al primo e al secondo tentativo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@FAIL-pecFirstKOSecondKO.it"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@FAIL-pecFirstKOSecondKO.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And esiste l'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW" per l'utente 0
    And controllo che le tempistiche di arrivo tra l elemento "DIGITAL_DELIVERY_CREATION_REQUEST" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "ERRORE DIGITALE"
