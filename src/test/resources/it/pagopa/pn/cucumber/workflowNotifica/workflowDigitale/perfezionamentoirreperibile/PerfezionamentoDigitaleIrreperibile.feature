Feature: Perfezionamento della notifica con destinatario irreperibile a 10g dal deposito AAR e revisione date perfezionamento digitali

  @addressBook1
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_3] Invio notifica digitale monodestinatario PF con Domicilio Digitale
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

  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_3] Invio notifica digitale monodestinatario PF con indirizzo speciale associato
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

  @mockNR
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_4] Invio notifica digitale monodestinatario PF con indirizzo semplice associato ripreso da national registry
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | recipientType   | PF               |
      | taxId           | PRVMNL80A01F205M |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che nell'elemento di timeline della notifica "PUBLIC_REGISTRY_RESPONSE" sia presente il campo Digital Address da National Registry
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"

  #25
  @addressBook1
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_5] Invio notifica digitale monodestinatario PF con Domicilio Digitale in cui il secondo tentativo va a buon fine
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "test@OK-pecFirstFailSecondSuccess.it" per il comune "Comune_1"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | KO |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline | true |
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "SUCCESSO DIGITALE"

  #26
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_6] Invio notifica digitale monodestinatario PG su recapito legale di piattaforma
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@OK-pecFirstFailSecondSuccess.it"
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
      | details_digitalAddress | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 0 |
    And viene inserito un recapito legale "example@OK-pecSuccess.it"
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | details | NOT_NULL |
      | details_responseStatus | OK |
      | details_sendingReceipts | [{"id": null, "system": null}] |
      | details_digitalAddress | {"address": "example@OK-pecSuccess.it", "type": "PEC"} |
      | details_recIndex | 0 |
      | details_digitalAddressSource | PLATFORM |
      | details_sentAttemptMade | 1 |

  #27
  @addressBook1
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_7] Invio notifica digitale monodestinatario PF con Domicilio Digitale già fallito il primo ciclo e cancella indirizzo di piattaforma
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
    And vengono rimossi eventuali recapiti presenti per l'utente
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

  #28 chiedere conferma
  @addressBook1
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_8] Invio notifica digitale monodestinatario PF con Domicilio Digitale modifica indirizzo di piattaforma con email valida
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "test@fail.it"
    And l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene inserito un recapito legale "example@pecSuccess.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

  #29
  @addressBook1
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_9] Invio notifica digitale monodestinatario indirizzo di piattaforma prima fallisce poi va con successo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "test@OK-pecFirstFailSecondSuccess.it"
    And l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene inserito un recapito legale "example@pecSuccess.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

  #30
  @addressBook1 @mockNR
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_10] Invio notifica digitale monodestinatario PF con Indirizzo di piattaforma generale su national registry con esito negativo e secondo tentativo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination    | Test digitale ok |
      | taxId           | JPCRPP78D43F165N |
      | digitalDomicile | NULL             |
      | physicalAddress_address | OK-pecFirstFailSecondSuccess.it |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    Then controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

 #31
 @addressBook1 @mockNR
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_11] Invio notifica digitale monodestinatario con utente che ha indirizzo nr generale e cambia pec in fase di invio notifica
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
      | physicalAddress_address | test@OK-pecFirstFailSecondSuccess.it |
   When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
   And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "pec@fail.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

  #32 chiedere conferma
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_12] Invio notifica digitale monodestinatario PF con Domicilio Digitale con secondo tentativo fallito
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@pecSuccess.it"
    And l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
      | physicalAddress_address | example@FAIL-pecFirstKOSecondKO.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And viene inserito un recapito legale "example@pecSuccess.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

  #33 chiedere conferma
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_13] Invio notifica digitale monodestinatario PF con Domicilio Digitale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@pecSuccess.it"
    And l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And viene inserito un recapito legale "example@pecSuccess.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

  #34 chiedere conferma
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_14] Invio notifica digitale monodestinatario PF con Domicilio Digitale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@pecSuccess.it"
    And l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And viene inserito un recapito legale "example@pecSuccess.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

  #35 chiedere conferma
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_15] Invio notifica digitale monodestinatario PF con Domicilio Digitale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@pecSuccess.it"
    And l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And viene inserito un recapito legale "example@pecSuccess.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

  #36 chiedere conferma
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_16] Invio notifica digitale monodestinatario PF con Domicilio Digitale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@pecSuccess.it"
    And l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    And viene inserito un recapito legale "example@pecSuccess.it"
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"

  @addressBook1
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_17] Invio notifica digitale monodestinatario PF con Domicilio Digitale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene generata una nuova notifica
      | subject | invio notifica con galileo |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | Galileo Galilei |
      | taxId | GLLGLL64B15G702I |
      | digitalDomicile | NULL |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si controlla con check rapidi che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "SEND_DIGITAL_FEEDBACK" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "DIGITALE"




