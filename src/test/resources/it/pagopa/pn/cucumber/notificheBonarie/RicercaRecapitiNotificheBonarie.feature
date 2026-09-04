Feature: Ricerca dei recapiti digitali per una notifica bonaria.


# ***********************************************
# **** 1 - Invio di una notifica bonaria tramite canali digitali PEC
# ***********************************************


 #pec piattaforma con e senza speciale

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_1_A] Come ente mittente invio una notifica bonaria verso PF specificando una pec speciale e il servizio utilizzerà quella di piattaforma
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente

  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_1_A] Come ente mittente invio una notifica bonaria verso PG specificando una pec speciale e il servizio utilizzerà quella di piattaforma
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | CucumberSpa              |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_1_B] Come ente mittente invio una notifica bonaria verso PF NON specificando una pec speciale e il servizio utilizzerà quella di piattaforma
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_1_B] Come ente mittente invio una notifica bonaria verso PG NON specificando una pec speciale e il servizio utilizzerà quella di piattaforma
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente


#ok registri naz

  @informalNotificationsSearchDigitalAddress @mockNR
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_2] Come ente mittente invio una notifica bonaria verso PF senza pec speciale ne di piattaforma, il serizio utilizzerà quella generale
    #tos v3 xx ???
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | DRCMRA80A01H501L   |
      | denomination    | Utenza ok mock     |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | xx      |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | xx       |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | GENERAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | xx      |
    And si attende che venga prodotto l'elemento "PUBLIC_REGISTRY_CALL" della notifica bonaria


  @informalNotificationsSearchDigitalAddress @mockNR
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_2] Come ente mittente invio una notifica bonaria verso PG senza pec speciale ne di piattaforma, il serizio utilizzerà quella generale
    #tos v3 xx ???
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 70472431207        |
      | denomination    | Utenza ok mock     |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | xx      |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | xx       |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | GENERAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | xx      |
    And si attende che venga prodotto l'elemento "PUBLIC_REGISTRY_CALL" della notifica bonaria


#no registri

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_3] Come ente mittente invio una notifica bonaria verso PF senza pec speciale ne di piattaforma, il serizio cerchera la generale con esito negativo, il canale pec sarà saltato
    #tos v3 xx ???
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | xx      |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | xx       |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | GENERAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | xx      |
    And si attende che venga prodotto l'elemento "PUBLIC_REGISTRY_CALL" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | PEC |


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_3] Come ente mittente invio una notifica bonaria verso PG senza pec speciale ne di piattaforma, il serizio cerchera la generale con esito negativo, il canale pec sarà saltato
    #tos v3 xx ???
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | xx      |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | xx       |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | GENERAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | xx      |
    And si attende che venga prodotto l'elemento "PUBLIC_REGISTRY_CALL" della notifica bonaria
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | PEC |

#sercQ

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_4] Come ente mittente invio una notifica bonaria verso PF con pec speciale e il servizio utilizzerà sercQ
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
      # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xxx | EMAIL |
# progress e feedback per caso 8 xxx non ci sarà sercQ
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | PEC |
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-managerxxx" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_SEND_EMAIL_COURTESY |

  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_4] Come ente mittente invio una notifica bonaria verso PG con pec speciale e il servizio utilizzerà sercQ
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    #And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"
    #And viene verificato che Sercq sia "disabilitato" come indirizzo di "default"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" come indirizzo di "default"
    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | CucumberSpa              |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xxx | EMAIL |
# progress e feedback per caso 8 xxx ci sarà serc?
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "disabilitato" come indirizzo di "default"
    And verifico la presenza di un audit log su "/aws/ecs/pn-workflow-managerxxx" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | iun    | auto             |
      | param1 | AUD_COM_SEND_EMAIL_COURTESY |

#solo speciale

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_5] Come ente mittente invio una notifica bonaria verso PF con solo pec speciale e il servizio utilizzerà la pec speciale
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | false   |

  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_5] Come ente mittente invio una notifica bonaria verso PG con solo pec speciale e il servizio utilizzerà la pec speciale
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | CucumberSpa              |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | false   |



# ***********************************************
# **** 2 - Invio del messaggio di cortesia a seguito di utilizzo del canale digitale SercQ
# ***********************************************

 #SMS ON

  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_02_1_A] Come ente mittente invio una notifica bonaria verso PF tramite sercQ e CON sms censito. Il servizio invierà un messaggio di cortesia email ed SMS

  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_02_1_A] Come ente mittente invio una notifica bonaria verso PG tramite sercQ e CON sms censito. Il servizio invierà un messaggio di cortesia email ed SMS

  @informalNotificationsSearchDigitalAddress @addressBook1 #@informalNotificSmsON
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_02_1_B] Come ente mittente invio una notifica bonaria verso PF tramite sercQ e ASSENZA di un telefono censito. Il servizio invierà un messaggio di cortesia solo su email
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
      # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
      | phone_number    | +3900000                 |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xxx | EMAIL |
    And non è presente l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xxx | SMS|
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"


  @informalNotificationsSearchDigitalAddress @addressBook2 #@informalNotificSmsON
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_02_1_B] Come ente mittente invio una notifica bonaria verso PG tramite sercQ e ASSENZA di un telefono censito. Il servizio invierà un messaggio di cortesia solo su email
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    #And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"
    #And viene verificato che Sercq sia "disabilitato" come indirizzo di "default"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" come indirizzo di "default"
      # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | CucumberSpa              |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
      | phone_number    | +3900000                 |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xxx | EMAIL |
    And non è presente l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xxx | SMS|
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"


 #SMS OFF

  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_02_2_A] Come ente mittente invio una notifica bonaria verso PF tramite sercQ e CON sms censito. Il servizio invierà un messaggio di cortesia solo su email

  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_02_2_A] Come ente mittente invio una notifica bonaria verso PG tramite sercQ e CON sms censito. Il servizio invierà un messaggio di cortesia solo su email

  @informalNotificationsSearchDigitalAddress @addressBook1 #@informalNotificSmsOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_02_2_B] Come ente mittente invio una notifica bonaria verso PF tramite sercQ e ASSENZA di un telefono censito. Il servizio invierà un messaggio di cortesia solo su email
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
      # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | +3900000           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xxx | EMAIL |
    And non è presente l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xxx | SMS|
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"


  @informalNotificationsSearchDigitalAddress @addressBook2 #@informalNotificSmsOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_02_2_B] Come ente mittente invio una notifica bonaria verso PG tramite sercQ e ASSENZA di un telefono censito. Il servizio invierà un messaggio di cortesia solo su email
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    #And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"
    #And viene verificato che Sercq sia "disabilitato" come indirizzo di "default"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" come indirizzo di "default"
      # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | +3900000           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xx | EMAIL |
    And non è presente l'elemento "SEND_COURTESY_MESSAGE" della notifica bonaria con dettagli
      | details_xx | SMS|
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"


# ***********************************************
# **** 5 - Nuova versione dei TOS per attivazione ricerca recapito digitale per le notifiche bonarie
# ***********************************************

 #speciale+piattaforma=speciale

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_A] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF con pec speciale e pec di piattaforma, il servizio utilizzerà la pec speciale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | false   |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_A] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG con pec speciale e pec di piattaforma, il servizio utilizzerà la pec speciale
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | CucumberSpa              |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | false   |
    And vengono rimossi eventuali recapiti presenti per l'utente


#!speciale-piattaforma=skip

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_B] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF SENZA pec speciale e pec di piattaforma, il servizio skippa il canale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | false   |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | PEC |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_B] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG SENZA pec speciale e pec di piattaforma, il servizio skippa il canale
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    #NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | false   |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | PEC |
    And vengono rimossi eventuali recapiti presenti per l'utente


#!speciale+sercQ=skip

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_C] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF SENZA pec speciale e sercq attivo, il servizio skippa il canale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
      # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | false   |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | PEC |
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_C] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG SENZA pec speciale e sercq attivo, il servizio skippa il canale
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    #And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"
    #And viene verificato che Sercq sia "disabilitato" come indirizzo di "default"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" come indirizzo di "default"
    # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | false   |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | PEC |
    And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "disabilitato" come indirizzo di "default"


 #speciale+sercq=speciale

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_D] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF con sercq attivo e pec speciale, il servizio utilizzerà la pec speciale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
      # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | false   |
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_D] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG con sercq attivo e pec di speciale, il servizio utilizzerà la pec speciale
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    #And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"
    #And viene verificato che Sercq sia "disabilitato" come indirizzo di "default"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" come indirizzo di "default"
    # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | CucumberSpa              |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | false   |
    And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "disabilitato" come indirizzo di "default"

 #!speciale+!piatt=skip

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_05_1_E] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PF senza pec speciale ne recapiti di piattaforma, il servizio skippa il canale
    # NO tos v3 xx
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | false   |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
    # xxx chiamtaa a NR?
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | PEC |


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_05_1_E] Come ente mittente in mancanza dell accettazione dei tos, invio una notifica bonaria verso PG senza pec speciale ne recapiti di piattaforma, il servizio skippa il canale
    # NO tos v3 xx
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | false   |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | false    |
      # xxx chiamtaa a NR?
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | PEC |



# ***********************************************
# **** 6 - Invio di una notifica bonaria tramite canale Email
# ***********************************************


  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_06_1_A] Come ente mittente invio una notifica bonaria verso PF SENZA email speciale e CON email di piattaforma. Il servizio utilizza l'email di piattaforma.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL    |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_06_1_A] Come ente mittente invio una notifica bonaria verso PG SENZA email speciale e CON email di piattaforma. Il servizio utilizza l'email di piattaforma.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
	    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL    |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_06_1_B] Come ente mittente invio una notifica bonaria verso PF SENZA email speciale e SENZA email di piattaforma. Il servizio skippa il canale email.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL    |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_06_1_B] Come ente mittente invio una notifica bonaria verso PG SENZA email speciale e SENZA email di piattaforma. Il servizio skippa il canale email.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "CucumberSpa"
	    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL    |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |


  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_06_1_C] Come ente mittente invio una notifica bonaria verso PF CON email speciale e SENZA email di piattaforma. Il servizio utilizza l'email speciale.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                      |
      | messageId       | ${NEW-IT}               |
      | subject         | Test Serch Contact      |
      | recipientType   | PF                      |
      | taxId           | GLLGLL64B15G702I        |
      | denomination    | GALILEO GALILEI         |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL    |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_06_1_C] Come ente mittente invio una notifica bonaria verso PG CON email speciale e SENZA email di piattaforma. Il servizio utilizza l'email speciale.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "CucumberSpa"
	    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                      |
      | messageId       | ${NEW-IT}               |
      | subject         | Test Serch Contact      |
      | recipientType   | PG                      |
      | taxId           | 20517490320             |
      | denomination    | CucumberSpa             |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL    |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |


  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_06_1_D] Come ente mittente invio una notifica bonaria verso PF CON email speciale e CON email di piattaforma. Il servizio utilizza l'email di piattaforma.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                      |
      | messageId       | ${NEW-IT}               |
      | subject         | Test Serch Contact      |
      | recipientType   | PF                      |
      | taxId           | GLLGLL64B15G702I        |
      | denomination    | GALILEO GALILEI         |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL    |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_06_1_D] Come ente mittente invio una notifica bonaria verso PG CON email speciale e CON email di piattaforma. Il servizio utilizza l'email di piattaforma.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL    |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente



# ***********************************************
# **** 7 - Invio di una notifica bonaria tramite canale SMS
# ***********************************************

  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_07_1_A] Come ente mittente invio una notifica bonaria verso PF SENZA n di telefono speciale e CON n di telefono di piattaforma. Il servizio utilizzerà il numero presente in piattaforma.

  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_07_1_A] Come ente mittente invio una notifica bonaria verso PG SENZA n di telefono speciale e CON n di telefono di piattaforma. Il servizio utilizzerà il numero presente in piattaforma.

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_07_1_B] Come ente mittente invio una notifica bonaria verso PF SENZA n di telefono speciale e SENZA n di telefono di piattaforma. Il servizio skippa il canale sms.
	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | SMS |


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_07_1_B] Come ente mittente invio una notifica bonaria verso PG SENZA n di telefono speciale e SENZA n di telefono di piattaforma. Il servizio skippa il canale sms.
	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | SMS |


  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_07_1_C] Come ente mittente invio una notifica bonaria verso PF CON n di telefono speciale e SENZA n di telefono di piattaforma. Il servizio utilizza il nimero speciale.
	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | +3900000           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |


  @informalNotificationsSearchDigitalAddress @addressBook2
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_07_1_C] Come ente mittente invio una notifica bonaria verso PG CON n di telefono speciale e SENZA n di telefono di piattaforma. Il servizio utilizza il numero speciale.
	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | +3900000           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |


  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_07_1_D] Come ente mittente invio una notifica bonaria verso PF CON n di telefono speciale e CON n di telefono di piattaforma. Il servizio utilizzerà il numero presente in piattaforma.

  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_07_1_D] Come ente mittente invio una notifica bonaria verso PG CON n di telefono speciale e CON n di telefono di piattaforma. Il servizio utilizzerà il numero presente in piattaforma.


# ***********************************************
# **** 8 - Workflow di una notifica bonaria inviata tramite SercQ
# ***********************************************

  #implementati in [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_01_4]
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_08_1_A] Come ente mittente invio una notifica bonaria verso PF con sercQ attivo. A seguito di un successo di invio si controlla la correttezza della timeline.

  #implementati in [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_01_4]
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_08_1_A] Come ente mittente invio una notifica bonaria verso PG con sercQ attivo. A seguito di un successo di invio si controlla la correttezza della timeline.

  #occorrono sequence sul ko di sercQ
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_08_1_B] Come ente mittente invio una notifica bonaria verso PF con sercQ attivo. A seguito di un ko sull'invio si controlla la correttezza della timeline.

  #occorrono sequence sul ko di sercQ
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_08_1_B] Come ente mittente invio una notifica bonaria verso PG con sercQ attivo. A seguito di un ko sull'invio si controlla la correttezza della timeline.


# ***********************************************
# **** 9 - Rimozione del canale digitale dalla piattaforma a seguito del suo recupero.
# ***********************************************

  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_09_1_A] Come ente mittente invio una notifica bonaria verso PF SENZA pec speciale e con pec di piattaforma, il destinatario rimuove la pec ma il flusso non varia il percorso.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente
# xxx controllo prograss feedback
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | PEC |


  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_09_1_B] Come ente mittente invio una notifica bonaria verso PF CON pec speciale e con pec di piattaforma, il destinatario rimuove la pec ma il flusso non varia il percorso.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |
    And vengono rimossi eventuali recapiti presenti per l'utente
# xxx controllo prograss feedback
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | PEC |


  @informalNotificationsSearchDigitalAddress @addressBook1
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_09_1_C] Come ente mittente invio una notifica bonaria verso PF CON pec speciale e con pec di piattaforma, il destinatario rimuove la pec ma il flusso non varia il percorso.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
      # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | false    |
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"
#controlli xxx
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | PEC |


# ***********************************************
# **** Verifica del Comportamento del servizio con Feature-flag: OFF
# ***********************************************

  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_1_A] Come ente mittente invio una notifica bonaria verso PF CON PEC speciale e CON RECAPITO di piattaforma. TOS ACCETTATI. Il servizio utilizza la pec speciale.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |
    And vengono rimossi eventuali recapiti presenti per l'utente
    # xxx controllo specifico
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | PEC |


  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_1_A] Come ente mittente invio una notifica bonaria verso PG CON PEC speciale e CON RECAPITO di piattaforma. TOS ACCETTATI. Il servizio utilizza la pec speciale.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | CucumberSpa              |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | PEC     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |
    And vengono rimossi eventuali recapiti presenti per l'utente
# xxx controllo specifico
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel            | PEC |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | PEC |


  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_1_B] Come ente mittente invio una notifica bonaria verso PF SENZA PEC speciale e CON RECAPITO di piattaforma. TOS ACCETTATI. Il servizio non validerà la notifica.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "xxx"
    And vengono rimossi eventuali recapiti presenti per l'utente
    # xxx controllo specifico


  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_1_B] Come ente mittente invio una notifica bonaria verso PG SENZA PEC speciale e CON RECAPITO di piattaforma. TOS ACCETTATI. Il servizio non validerà la notifica.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "xxx"
    And vengono rimossi eventuali recapiti presenti per l'utente
# xxx controllo specifico


  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_1_C] Come ente mittente invio una notifica bonaria verso PF SENZA PEC speciale e CON RECAPITO di piattaforma. TOS RIFIUTATI. Il servizio non validerà la notifica.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    #When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "xxx"
    #xxx verificare


  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_1_C] Come ente mittente invio una notifica bonaria verso PG SENZA PEC speciale e CON RECAPITO di piattaforma. TOS RIFIUTATI. Il servizio non validerà la notifica.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "xxx"


  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_1_D] Come ente mittente invio una notifica bonaria verso PF CON PEC speciale e CON RECAPITO di piattaforma. TOS RIFIUTATI. Il servizio non validerà la notifica.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    # NO tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PF                       |
      | taxId           | GLLGLL64B15G702I         |
      | denomination    | GALILEO GALILEI          |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
        #When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "xxx"
    #xxx verificare
    And vengono rimossi eventuali recapiti presenti per l'utente
    # xxx controllo specifico


  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_1_D] Come ente mittente invio una notifica bonaria verso PG CON PEC speciale e CON RECAPITO di piattaforma. TOS RIFIUTATI. Il servizio non validerà la notifica.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                       |
      | messageId       | ${NEW-IT}                |
      | subject         | Test Serch Contact       |
      | recipientType   | PG                       |
      | taxId           | 20517490320              |
      | denomination    | CucumberSpa              |
      | email           | NULL                     |
      | digitalDomicile | example@OK-pecSuccess.it |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "REFUSED"
    Then la notifica bonaria è stata rifiutata per l'errore: "xxx"
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_2_A] Come ente mittente invio una notifica bonaria verso PF SENZA EMAIL speciale e CON EMAIL di piattaforma. Il servizio skippa il canale email
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_2_A] Come ente mittente invio una notifica bonaria verso PG SENZA EMAIL speciale e CON EMAIL di piattaforma. Il servizio skippa il canale email
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
	    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_2_B] Come ente mittente invio una notifica bonaria verso PF SENZA EMAIL speciale e SENZA EMAIL di piattaforma. Il servizio skippa il canale email
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |



  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_2_B] Come ente mittente invio una notifica bonaria verso PG SENZA EMAIL speciale e SENZA EMAIL di piattaforma. Il servizio skippa il canale email
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "CucumberSpa"
	    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | EMAIL |



  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_2_C] Come ente mittente invio una notifica bonaria verso PF CON EMAIL speciale e SENZA EMAIL di piattaforma. Il servizio utilizza l'email speciale.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                      |
      | messageId       | ${NEW-IT}               |
      | subject         | Test Serch Contact      |
      | recipientType   | PF                      |
      | taxId           | GLLGLL64B15G702I        |
      | denomination    | GALILEO GALILEI         |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |


  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_2_C] Come ente mittente invio una notifica bonaria verso PG CON EMAIL speciale e SENZA EMAIL di piattaforma. Il servizio utilizza l'email speciale.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "CucumberSpa"
	    #tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                      |
      | messageId       | ${NEW-IT}               |
      | subject         | Test Serch Contact      |
      | recipientType   | PG                      |
      | taxId           | 20517490320             |
      | denomination    | CucumberSpa             |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |


  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_2_C] Come ente mittente invio una notifica bonaria verso PF CON EMAIL speciale e CON EMAIL di piattaforma. Il servizio utilizza l'email speciale.
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                      |
      | messageId       | ${NEW-IT}               |
      | subject         | Test Serch Contact      |
      | recipientType   | PF                      |
      | taxId           | GLLGLL64B15G702I        |
      | denomination    | GALILEO GALILEI         |
      | email           | tullio.test@virgilio.it |
      | digitalDomicile | NULL                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |
    And vengono rimossi eventuali recapiti presenti per l'utente


  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_2_C] Come ente mittente invio una notifica bonaria verso PG CON EMAIL speciale e CON EMAIL di piattaforma. Il servizio utilizza l'email speciale.
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    	        # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | digitalDomicile | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | EMAIL   |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | true    |
      | details_isTosAccepted        | true    |
    And vengono rimossi eventuali recapiti presenti per l'utente


  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_3_A] Come ente mittente invio una notifica bonaria verso PF SENZA SMS speciale e CON SMS di piattaforma. Il servizio skippa il canale SMS

  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_3_A] Come ente mittente invio una notifica bonaria verso PG SENZA SMS speciale e CON SMS di piattaforma. Il servizio skippa il canale SMS


  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_3_B] Come ente mittente invio una notifica bonaria verso PF SENZA SMS speciale e SENZA SMS di piattaforma. Il servizio skippa il canale SMS
     # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | SMS |


  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_3_B] Come ente mittente invio una notifica bonaria verso PG SENZA SMS speciale e SENZA SMS di piattaforma. Il servizio skippa il canale SMS
 # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | NULL               |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | false    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS     |
      | details_digitalAddressSource | SPECIAL |
      | details_isAvailable          | false   |
      | details_isTosAccepted        | true    |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_SKIP" della notifica bonaria con dettagli
      | details_channel | SMS |


  @informalNotificationsSearchDigitalAddress @addressBook1 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_3_C] Come ente mittente invio una notifica bonaria verso PF CON SMS speciale e SENZA SMS di piattaforma. Il servizio utilizza l'SMS speciale.
 # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PF                 |
      | taxId           | GLLGLL64B15G702I   |
      | denomination    | GALILEO GALILEI    |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | +3900000           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | flase    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |


  @informalNotificationsSearchDigitalAddress @addressBook2 @informalNSearchDigitalAddressFlagOFF
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_3_C] Come ente mittente invio una notifica bonaria verso PG CON SMS speciale e SENZA SMS di piattaforma. Il servizio utilizza l'SMS speciale.
  # tos v3 xx
    Then l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | xx                 |
      | messageId       | ${NEW-IT}          |
      | subject         | Test Serch Contact |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | denomination    | CucumberSpa        |
      | email           | NULL               |
      | digitalDomicile | NULL               |
      | phone_number    | +3900000           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | flase    |
      | details_isTosAccepted        | true     |
    And si attende che venga prodotto l'elemento "GET_ADDRESS" della notifica bonaria con dettagli
      | details_channel              | SMS      |
      | details_digitalAddressSource | PLATFORM |
      | details_isAvailable          | true     |
      | details_isTosAccepted        | true     |


  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PF_04_3_C] Come ente mittente invio una notifica bonaria verso PF CON SMS speciale e CON SMS di piattaforma. Il servizio utilizza l'SMS speciale.

  #non potendo automatizzare l'accettazione dei tos per sms il test sarà manuale
  Scenario: [NOTIFICHE_BONARIE_SERCH_CONTACT_PG_04_3_C] Come ente mittente invio una notifica bonaria verso PG CON SMS speciale e CON SMS di piattaforma. Il servizio utilizza l'SMS speciale.