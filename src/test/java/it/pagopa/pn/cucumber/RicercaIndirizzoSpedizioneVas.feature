Feature: test per il recupero indirizzo al primo tentativo vas


  # Indirizzi recuperati dai registri - CREAZIONE notifica andata a buon fine -CONSEGNA andata a buon fine.


  @ricercaIndirizzoVas
  Scenario: [1-19_3-11-15] Invio notifica AR monodestinatario verso PF con campo address vuoto e recupero indirizzo da ANPR Vas attivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PF Censito       |
      | taxId                               | PPPPLT80A01H501V |
      | digitalDomicile_address             | test@pec.it      |
      | physicalAddress_address             | via bologna 7    |
      | physicalAddress_municipality        | Bologna          |
      | physicalAddress_municipalityDetails | NULL             |
      | at                                  | NULL             |
      | physicalAddress_addressDetails      | NULL             |
      | physicalAddress_province            | BO               |
      | physicalAddress_State               | ITALIA           |
      | physicalAddress_zip                 | 40069            |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "xxx"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"

    #And esiste l'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_RESPONSE" abbia notificationCost uguale a "null" per l'utente 0

    #STEP codice 200 del test 15
  #Call una sola volta 3
  #Call lista corretta di utenze 3
  #Response registro ANPR 3/11
  #SEND_ANALOG_FEEDBACK.responseStatus.OK 11


  @ricercaIndirizzoVas
  Scenario: [4_4] Invio notifica 890 monodestinatario verso PG con campo address vuoto e recupero indirizzo da RI Vas attivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "xxx"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
  #Call una sola volta
  #Call lista corretta di utenze
  #Response registro RI

  @ricercaIndirizzoVas
  Scenario: [5_5] Invio notifica multidestinatario AR verso PF-PG con campo address vuoto e recupero indirizzo dai registri nazionali Vas attivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PF Censito       |
      | taxId                               | PPPPLT80A01H501V |
      | digitalDomicile_address             | test@pec.it      |
      | physicalAddress_address             | via bologna 7    |
      | physicalAddress_municipality        | Bologna          |
      | physicalAddress_municipalityDetails | NULL             |
      | at                                  | NULL             |
      | physicalAddress_addressDetails      | NULL             |
      | physicalAddress_province            | BO               |
      | physicalAddress_State               | ITALIA           |
      | physicalAddress_zip                 | 40069            |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino allo stato della notifica "xxx"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details          | NOT_NULL |
      | details_recIndex | 0        |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details          | NOT_NULL |
      | details_recIndex | 1        |
  #Call una sola volta
  #Call lista corretta di utenze
  #Response registro RI/ANPR




  # Indirizzi non recuperati dai registri - CREAZIONE notifica fallita.


  @ricercaIndirizzoVas
  Scenario: [13-22_6] Invio notifica AR monodestinatario verso PF con campo address vuoto e nessun indirizzo trovato da ANPR notifica rifiutata Vas attivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PF Non Censito |
      | taxId                               | XXX            |
      | digitalDomicile_address             | test@pec.it    |
      | physicalAddress_address             | via bologna 7  |
      | physicalAddress_municipality        | Bologna        |
      | physicalAddress_municipalityDetails | NULL           |
      | at                                  | NULL           |
      | physicalAddress_addressDetails      | NULL           |
      | physicalAddress_province            | BO             |
      | physicalAddress_State               | ITALIA         |
      | physicalAddress_zip                 | 40069          |
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
  # Call una volta
  #Call lista utenze
  #Response non esista


  @ricercaIndirizzoVas
  Scenario: [14_7] Invio notifica AR monodestinatario verso PG con campo address vuoto e nessun indirizzo trovato da RI notifica rifiutata Vas attivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG Non Censito |
      | taxId                               | XXX            |
      | digitalDomicile_address             | test@pec.it    |
      | physicalAddress_address             | via bologna 7  |
      | physicalAddress_municipality        | Bologna        |
      | physicalAddress_municipalityDetails | NULL           |
      | at                                  | NULL           |
      | physicalAddress_addressDetails      | NULL           |
      | physicalAddress_province            | BO             |
      | physicalAddress_State               | ITALIA         |
      | physicalAddress_zip                 | 40069          |
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
  # Call una volta
  #Call lista utenze
  #Response non esista



 #*** ADD _8. entrambi non censiti
  @ricercaIndirizzoVas
  Scenario: [_8] Invio notifica multidestinatario AR verso PF-PG con campo address vuoto entrambi NON censiti Vas attivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PF Non Censito |
      | taxId                               | xxx            |
      | digitalDomicile_address             | test@pec.it    |
      | physicalAddress_address             | via bologna 7  |
      | physicalAddress_municipality        | Bologna        |
      | physicalAddress_municipalityDetails | NULL           |
      | at                                  | NULL           |
      | physicalAddress_addressDetails      | NULL           |
      | physicalAddress_province            | BO             |
      | physicalAddress_State               | ITALIA         |
      | physicalAddress_zip                 | 40069          |
    And destinatario
      | denomination                        | PG Non Censito |
      | taxId                               | xxx            |
      | digitalDomicile_address             | test@pec.it    |
      | physicalAddress_address             | via bologna 7  |
      | physicalAddress_municipality        | Bologna        |
      | physicalAddress_municipalityDetails | NULL           |
      | at                                  | NULL           |
      | physicalAddress_addressDetails      | NULL           |
      | physicalAddress_province            | BO             |
      | physicalAddress_State               | ITALIA         |
      | physicalAddress_zip                 | 40069          |
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"
    Then vengono letti gli eventi fino allo stato della notifica "xxx"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
  # Call una volta/lista utenze
  #Response non esista



  @ricercaIndirizzoVas
  Scenario: [15_10M] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e un solo indirizzo trovato da RI notifica rifiutata Vas attivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PF Non Censito   |
      | taxId                               | FRMTTR76M06B715E |
      | digitalDomicile_address             | test@pec.it      |
      | physicalAddress_address             | via bologna 7    |
      | physicalAddress_municipality        | Bologna          |
      | physicalAddress_municipalityDetails | NULL             |
      | at                                  | NULL             |
      | physicalAddress_addressDetails      | NULL             |
      | physicalAddress_province            | BO               |
      | physicalAddress_State               | ITALIA           |
      | physicalAddress_zip                 | 40069            |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"

# Inserimento MANUALE + VAS.

  @ricercaIndirizzoVas
  Scenario: [16_9M] Invio notifica AR multidestinatario verso PF-PG con campo address vuoto e uno compilato e indirizzo trovato da RI notifica accettata Vas attivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PF               |
      | taxId                               | PPPPLT80A01H501V |
      | digitalDomicile_address             | test@pec.it      |
      | physicalAddress_address             | via bologna 7    |
      | physicalAddress_municipality        | Bologna          |
      | physicalAddress_municipalityDetails | NULL             |
      | at                                  | NULL             |
      | physicalAddress_addressDetails      | NULL             |
      | physicalAddress_province            | BO               |
      | physicalAddress_State               | ITALIA           |
      | physicalAddress_zip                 | 40069            |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PUBLIC_REGISTRY_VALIDATION_CALL"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details          | NOT_NULL |
      | details_recIndex | 1        |
      #   Verificare corretti elementi per PF 0



  @ricercaIndirizzoVas
  Scenario: [17?] Invio notifica 890 multidestinatario verso PF-PG con campo address vuoto e uno compilato e indirizzo non trovato da RI notifica rifiutata Vas attivo
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG            |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    And destinatario
      | denomination                        | PF Non Censitop |
      | taxId                               | XXX             |
      | digitalDomicile_address             | test@pec.it     |
      | physicalAddress_address             | via bologna 7   |
      | physicalAddress_municipality        | Bologna         |
      | physicalAddress_municipalityDetails | NULL            |
      | at                                  | NULL            |
      | physicalAddress_addressDetails      | NULL            |
      | physicalAddress_province            | BO              |
      | physicalAddress_State               | ITALIA          |
      | physicalAddress_zip                 | 40069           |
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"


  #Abilitazione PA / FeatureFlag / WI-VAS-1.3 + WI-VAS-1.4 + client WI-VAS-1.5 ********************


  @ricercaIndirizzoVas
  Scenario: [20_16] Crezione notifica PA abilitata - Feature flag Attivo - Client versione precedente e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b con la versione "V24" dal "AB" e si attende che lo stato diventi "REFUSED"
# client non aggiornato



# ***ADD _17 : PA non abilitata - Feature flag Attivo - Client non aggiornato e notifica rifiutata
  @ricercaIndirizzoVas
  Scenario: [23_19] Crezione notifica PA NON abilitata - Feature flag Attivo - Client NON aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b con la versione "V24" dal "NON AB" e si attende che lo stato diventi "REFUSED"
#Client non aggiornato



# ***ADD _18 : PA non abilitata - Feature flag Attivo - Client aggiornato e notifica rifiutata
  @ricercaIndirizzoVas
  Scenario: [23_19] Crezione notifica PA NON abilitata - Feature flag Attivo - Client aggiornato e notifica rifiutata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b dal "NON AB" e si attende che lo stato diventi "REFUSED"


  @ricercaIndirizzoVas
  Scenario: [23_19] Crezione notifica PA abilitata - Feature flag Spento - Client aggiornato e notifica rifiutata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b dal "AB" e si attende che lo stato diventi "REFUSED"

  @ricercaIndirizzoVas
  Scenario: [24_20] Crezione notifica PA abilitata - Feature flag Spento - Client versione precedente e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b con la versione "V24" dal "AB" e si attende che lo stato diventi "REFUSED"

  @ricercaIndirizzoVas
  Scenario: [25_21] Crezione notifica PA non abilitata - Feature flag Spento - Client aggiornato e notifica rifiutata
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b dal "NON AB" e si attende che lo stato diventi "REFUSED"


  @ricercaIndirizzoVas
  Scenario: [26_22] Crezione notifica PA non abilitata - Feature flag Spento - Client non aggiornato e notifica rifiutata
    Given viene generata una nuova notifica con la versione "V24"
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    When la notifica viene inviata tramite api b2b con la versione "V24" dal "NON AB" e si attende che lo stato diventi "REFUSED"




    # Api lato Destinatario

  @preesitiEnabledTags
  Scenario: [] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECAG008A all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890     |
      | taxId                   | FRMTTR76M06B715E            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And attendo che gli elementi di timeline SEND_ANALOG_PROGRESS vengano ricevuti tutti
    And la notifica può essere correttamente recuperata da ""
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG008A" non è visibile


  @preesitiEnabledTags
  Scenario: [] Verifica presenza evento SEND_ANALOG_PROGRESS con il DeliveryDetailCode RECAG012 all’interno della timeline B2B ma non della timeline web
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-WO-011B       |
      | taxId                   | FRMTTR76M06B715E |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | via@OK-WO-011B   |
    When la notifica viene inviata tramite api b2b dal "" e si attende che lo stato diventi "ACCEPTED"
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT"
    And la notifica può essere correttamente recuperata da "Mario Cucumber"
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è visibile
    Then lato api l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012" è visibile
    And lato destinatario vengono letti i dettagli della notifica lato web dal destinatario "Mario Gherkin"
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012" è visibile
    Then lato destinatario dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è presente
    And lato mittente vengono letti i dettagli della notifica lato web "Comune_Multi"
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012" è visibile
    And lato mittente dal web l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG012A" non è presente












#Stream
#@cleanWebhook @addressBook1

@cleanWebhook @webhook1
  Scenario: [] Invio notifica e controllo che stream con eventType vuoto e versione da V26 contenga elemento ANALOG_WORKFLOW_RECIPIENT_DECEASED
#    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V26"
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione ""
    And si crea il nuovo stream per il "Comune_Multi" con versione ""
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "" con la versione ""


  @cleanWebhook @webhook1
  Scenario: [] Invio notifica e controllo che stream con eventType vuoto e versione da V26 contenga elemento ANALOG_WORKFLOW_RECIPIENT_DECEASED
#    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V26"
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination                        | PG Censito    |
      | taxId                               | 01113570442   |
      | digitalDomicile_address             | test@pec.it   |
      | physicalAddress_address             | via bologna 7 |
      | physicalAddress_municipality        | Bologna       |
      | physicalAddress_municipalityDetails | NULL          |
      | at                                  | NULL          |
      | physicalAddress_addressDetails      | NULL          |
      | physicalAddress_province            | BO            |
      | physicalAddress_State               | ITALIA        |
      | physicalAddress_zip                 | 40069         |
    Then la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione ""
    And si crea il nuovo stream per il "Comune_Multi" con versione ""
    Then vengono letti gli eventi dello stream del "Comune_Multi" fino all'elemento di timeline "" con la versione ""


#Sperimentazione: 27-28
#12/13/14 tentativi

  #23/24/24
  #_23 dettaglio notifica mittente - GET /delivery/v2.5/notifications/sent/${IUN} -> nessun nuovo elemento
  #_24 dettaglio notifica mittente - GET /delivery/v2.6/notifications/sent/${IUN} -> nuovi elementi
  #_25 dettaglio notifica destinatario - GET/delivery/v2.5/notifications/received/{iun}  -> nessun nuovo lemento -> token
  #_26 dettaglio notifica destinatario - GET/delivery/v2.6/notifications/received/{iun}  -> nuovi elementi

  #"Gherkin Analogic" con Piva "05722930657"

