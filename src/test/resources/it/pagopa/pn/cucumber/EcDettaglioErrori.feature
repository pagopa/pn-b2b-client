Feature: Real External channell dettagli errori riportati in EcRichiesteMetadati



    #**********************
    #******** PEC *********
    #**********************

  @realExtrnalChannel @detailsErrorExChannel @addressBook1
  Scenario: [PN_EC_DETTAGLIO_ERRORI_PEC_01_1_A] Come ente mittente invio una notifica che riceverà errore sulla pec, i dettagli saranno riportati nei log di externalchannel
    Given si predispone addressbook per l'utente "Mario Gherkin"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    And viene inserita l'email di cortesia "tullio.test@virgilio.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | Mario Gherkin        |
      | taxId                   | CLMCST42R12D969Z     |
      | digitalDomicile_address | -indirizzo@gmail.com |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "KO"
    #And vengono rimossi eventuali recapiti presenti per l'utente
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |


  @realExtrnalChannel @detailsErrorExChannel @addressBook2
  Scenario: [PN_EC_DETTAGLIO_ERRORI_PEC_01_1_B] Come ente mittente invio una notifica che riceverà errore sulla pec di piattaforma, i dettagli saranno riportati nei log di externalchannel
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "tullio.test@virgilio.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "-indirizzo@gmail.com"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "KO"
    #And vengono rimossi eventuali recapiti presenti per l'utente
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |


    #***********************
    #******** EMAIL ********
    #***********************

  @realExtrnalChannel @detailsErrorExChannel @addressBook1
  Scenario: [PN_EC_DETTAGLIO_ERRORI_EMAIL_02_1_A] Come ente mittente invio una notifica che riceverà errore sulla email, i dettagli saranno riportati nei log di externalchannel
    Given si predispone addressbook per l'utente "Mario Gherkin"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    And viene inserita l'email di cortesia "bounce@simulator.amazonses.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    Given viene generata una nuova notifica
      | subject            | notifica digitale con cucumber |
      | senderDenomination | Comune di palermo              |
    And destinatario
      | denomination            | Mario Gherkin        |
      | taxId                   | CLMCST42R12D969Z     |
      | digitalDomicile_address | -indirizzo@gmail.com |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono rimossi eventuali recapiti presenti per l'utente
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "" con responseStatus "KO"
    #And vengono rimossi eventuali recapiti presenti per l'utente
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |


  @realExtrnalChannel @detailsErrorExChannel @addressBook2
  Scenario: [PN_EC_DETTAGLIO_ERRORI_EMAIL_02_1_B] Come ente mittente invio una notifica che riceverà errore sulla email, i dettagli saranno riportati nei log di externalchannel
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "suppressionlist@simulator.amazonses.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Given viene generata una nuova notifica
      | subject            | notifica digitale con cucumber |
      | senderDenomination | Comune di palermo              |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "" con responseStatus "KO"
    #And vengono rimossi eventuali recapiti presenti per l'utente
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |


  @realExtrnalChannel @detailsErrorExChannel @addressBook1
  Scenario: [PN_EC_DETTAGLIO_ERRORI_EMAIL_02_1_C] Come ente mittente invio una notifica che riceverà errore sulla email, i dettagli saranno riportati nei log di externalchannel
    Given si predispone addressbook per l'utente "Mario Gherkin"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    And viene inserita l'email di cortesia "complaint@simulator.amazonses.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination    | Mario Gherkin    |
      | taxId           | CLMCST42R12D969Z |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "" con responseStatus "KO"
    #And vengono rimossi eventuali recapiti presenti per l'utente
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |



    #***************************
    #******** ANALOGICO ********
    #***************************

  @realExtrnalChannel @detailsErrorExChannel @addressBook2
  Scenario: [PN_EC_DETTAGLIO_ERRORI_ANALOGICO_03_1] Come ente mittente invio una notifica che riceverà errore sull invio analogico, i dettagli saranno riportati nei log di externalchannel
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "tullio.test@virgilio.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "" con responseStatus "KO"
    #And vengono rimossi eventuali recapiti presenti per l'utente
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |


    #***************************
    #******** COMBO PEC ********
    #***************************

  @realExtrnalChannel @detailsErrorExChannel @informaldetailsErrorExChannel
  Scenario: [PN_EC_DETTAGLIO_ERRORI_ANALOGICO_01_1_INFORMAL] Come ente mittente invio una notifica che riceverà errore sull invio pec, i dettagli saranno riportati nei log di externalchannel
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd              |
      | messageId       | ${NEW-IT}            |
      | subject         | Test workflow        |
      | recipientType   | PG                   |
      | taxId           | 20517490320          |
      | denomination    | Acme spa             |
      | email           | NULL                 |
      | digitalDomicile | -indirizzo@gmail.com |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel        | PEC |
      | details_responseStatus | KO  |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |


    #***************************
    #******** COMBO SMS ********
    #***************************

  @realExtrnalChannel @detailsErrorExChannel @informaldetailsErrorExChannel
  Scenario: [PN_EC_DETTAGLIO_ERRORI_ANALOGICO_02_1_INFORMAL_A] Come ente mittente invio una notifica che riceverà errore sull invio sms, i dettagli saranno riportati nei log di externalchannel
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | Reminder                       |
      | messageId       | ${NEW-IT}                      |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
      | phone_number    | todo                        |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | KO    |
      | details_channel        | EMAIL |
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_responseStatus | KO  |
      | details_channel        | SMS |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |


      #****************************
      #******** COMBO EMAIL ********
      #*****************************

  @realExtrnalChannel @detailsErrorExChannel @informaldetailsErrorExChannel
  Scenario: [PN_EC_DETTAGLIO_ERRORI_ANALOGICO_02_1_INFORMAL_B] Come ente mittente invio una notifica che riceverà errore sull invio email, i dettagli saranno riportati nei log di externalchannel
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                                 |
      | messageId       | ${NEW-IT}                               |
      | subject         | Test workflow                           |
      | recipientType   | PF                                      |
      | taxId           | FRMTTR76M06B715E                        |
      | denomination    | Ettore Fieramosca                       |
      | email           | suppressionlist@simulator.amazonses.com |
      | digitalDomicile | NULL                                    |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_PROGRESS" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M003  |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |


  @realExtrnalChannel @detailsErrorExChannel @informaldetailsErrorExChannel
  Scenario: [PN_EC_DETTAGLIO_ERRORI_ANALOGICO__02_1_INFORMAL_C] Come ente mittente invio una notifica che riceverà errore sull invio email, i dettagli saranno riportati nei log di externalchannel
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                        |
      | messageId       | ${NEW-IT}                      |
      | subject         | Test workflow                  |
      | recipientType   | PF                             |
      | taxId           | FRMTTR76M06B715E               |
      | denomination    | Ettore Fieramosca              |
      | email           | bounce@simulator.amazonses.com |
      | digitalDomicile | NULL                           |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M005  |
#    And verifico la presenza di un audit log su "/aws/ecs/pn-external-channel" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
#      | iun    | auto |
#      | param1 | todo |
