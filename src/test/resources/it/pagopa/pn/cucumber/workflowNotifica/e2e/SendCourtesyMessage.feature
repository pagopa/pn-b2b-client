Feature: Invio messaggi cortesia e2e


  #                                 *** Flag OFF Digitale ***


  @addressBook1 @courtesyMessage @CM_FlagOFF @cleanAddressBook #rif srs 44
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_1] Verifica successione elementi - Invio DIGITALE con email di cortesia per PF
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 999 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK"
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @addressBook2 @courtesyMessage @CM_FlagOFF @cleanAddressBook #rif srs 44
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_2] Verifica successione elementi - Invio DIGITALE con email di cortesia per PG
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica a CucumberSpa |
      | senderDenomination | Comune di palermo            |
    And destinatario
      | denomination    | CucumberSpa |
      | taxId           | 20517490320 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 999 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |

# solo UAT
  @courtesyMessage @CM_FlagOFF @uatCM #rif srs 44
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_3] Verifica successione elementi - Invio DIGITALE con APPIO di cortesia solo PF UAT
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario
      | denomination    | Matteo Rossi     |
      | taxId           | AAAAAA00A00A000C |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                 |
      | details_recIndex       | 0                                        |
      | details_digitalAddress | {"type": "APPIO", "address": "DISABLED"} |


  @courtesyMessage @addressBook3 @CM_FlagOFF @cleanAddressBook #rif srs 44
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_4] Verifica successione elementi - Invio DIGITALE con TPP di cortesia solo PF
    Given si predispone addressbook per l'utente "Mario Gherkin"
    Then l'utente "Mario Gherkin" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject            | notifica digitale con cucumber |
      | senderDenomination | Comune di palermo              |
    And destinatario
      | denomination    | Mario Gherkin    |
      | taxId           | CLMCST42R12D969Z |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                          |
      | details_recIndex       | 0                                 |
      | details_digitalAddress | {"type": "TPP", "address": "APP"} |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK"



 #                         ****  Flag OFF Analogico ***


  @addressBook1 @courtesyMessage @CM_FlagOFF @cleanAddressBook #rif srs 45
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_5] Verifica successione elementi - Invio ANALOGICO con email di cortesia per PF
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination            | Galileo Galilei  |
      | taxId                   | GLLGLL64B15G702I |
      | physicalAddress_address | Via@ok_AR        |
      | digitalDomicile         | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 999 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @addressBook2 @courtesyMessage @CM_FlagOFF @cleanAddressBook #rif srs 45
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_6] Verifica successione elementi - Invio ANALOGICO con email di cortesia per PG
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    Given viene generata una nuova notifica
      | subject            | invio notifica a Cucumber |
      | senderDenomination | Comune di palermo         |
    And destinatario
      | denomination    | Cucumber    |
      | recipientType   | PG          |
      | taxId           | 20517490320 |
      | physicalAddress | Via@ok_890  |
      | digitalDomicile | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 999 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |



  # solo UAT
  @courtesyMessage @CM_FlagOFF @uatCM #rif srs 45
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_7] Verifica successione elementi - Invio ANALOGICO con APPIO di cortesia solo PF UAT
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination            | Matteo Rossi     |
      | taxId                   | AAAAAA00A00A000C |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_AR        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                 |
      | details_recIndex       | 0                                        |
      | details_digitalAddress | {"type": "APPIO", "address": "DISABLED"} |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"


  @courtesyMessage @addressBook3 @CM_FlagOFF #rif srs 45
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_8] Verifica successione elementi - Invio ANALOGICO con TPP di cortesia solo PF
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890 |
      | taxId                   | CLMCST42R12D969Z        |
      | digitalDomicile         | NULL                    |
      | physicalAddress_address | Via@ok_AR               |
      | payment_pagoPaForm      | SI                      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                          |
      | details_recIndex       | 0                                 |
      | details_digitalAddress | {"type": "TPP", "address": "APP"} |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"


















#                                         FLAG ON - DIGITALE


# DOMICILIO DIGITALE SERCQ

  @addressBook1 @courtesyMessage @cleanAddressBook #rif srs 39
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_9] Verifica successione elementi - Invio DIGITALE con SEND, cortesia email per PF
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Then viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @addressBook2 @courtesyMessage @cleanAddressBook #rif srs 39
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_10] Verifica successione elementi - Invio DIGITALE con SEND, cortesia email per PG
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica a CucumberSpa |
      | senderDenomination | Comune di palermo            |
    And destinatario
      | denomination    | CucumberSpa |
      | taxId           | 20517490320 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @addressBook3 @courtesyMessage @cleanAddressBook @bankCourtesyMessage #rif srs 39
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_12] Verifica successione elementi - Invio DIGITALE con SEND, cortesia TPP solo PF
    Given si predispone addressbook per l'utente "Mario Gherkin"
    Then l'utente "Mario Gherkin" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject            | notifica digitale con cucumber |
      | senderDenomination | Comune di palermo              |
    And destinatario Mario Gherkin e:
      | digitalDomicile    | NULL |
      | payment_pagoPaForm | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                          |
      | details_recIndex       | 0                                 |
      | details_digitalAddress | {"type": "TPP", "address": "APP"} |


  # DOMICILIO DIGITALE PEC di PIATTAFORMA


  @addressBook1 @courtesyMessage @cleanAddressBook #rif srs 40
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_13] Verifica successione elementi - Invio DIGITALE con domicilio digitale PEC. cortesia email per PF
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @addressBook2 @courtesyMessage @cleanAddressBook #rif srs 40
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_14] Verifica successione elementi - Invio DIGITALE con domicilio digitale PEC. cortesia email per PG
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene generata una nuova notifica
      | subject            | invio notifica a CucumberSpa |
      | senderDenomination | Comune di palermo            |
    And destinatario
      | denomination    | CucumberSpa |
      | taxId           | 20517490320 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @addressBook3 @courtesyMessage @cleanAddressBook @bankCourtesyMessage #rif srs 40
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_16] Verifica successione elementi - Invio DIGITALE con domicilio digitale PEC, cortesia TPP solo PF
    Given si predispone addressbook per l'utente "Mario Gherkin"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene generata una nuova notifica
      | subject            | notifica digitale con cucumber |
      | senderDenomination | Comune di palermo              |
    And destinatario Mario Gherkin e:
      | digitalDomicile    | NULL |
      | payment_pagoPaForm | SI   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                          |
      | details_recIndex       | 0                                 |
      | details_digitalAddress | {"type": "TPP", "address": "APP"} |


 #    DOMICILIO DIGITALE NR


  #@courtesyMessage #rif srs 41
  #@courtesyMessage @cleanAddressBook
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_17] Verifica successione elementi - Invio DIGITALE con domicilio PEC da RN, cortesia email per PF
  # serve un utente censito per AddressBook che ritorni una pec dai RN
    Given si predispone addressbook per l'utente "Dino Sauro"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Dino Sauro"
    Then viene generata una nuova notifica
      | subject | invio notifica a Sauro Dino |
    And destinatario
      | denomination            | Dino Sauro                |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
      | taxId                   | DSRDNI00A01A225I          |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"

  @addressBook1 @courtesyMessage @cleanAddressBook
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_17B] Verifica successione elementi - Invio ANALOGICO con domicilio PEC da RN, cortesia email per PF
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Then viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SCHEDULE_ANALOG_WORKFLOW" sia immediatamente successivo a quello dell'evento "SEND_COURTESY_MESSAGE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |



   #rif srs 41 ottenere l'idirizzo cf@pec.it
  @uatCM @courtesyMessage
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_19] Verifica successione elementi - Invio DIGITALE con domicilio digitale PEC da RN, cortesia APPIO per PF UAT
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination            | Matteo Rossi              |
      | taxId                   | AAAAAA00A00A000C          |
      | recipientType           | PF                        |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    #And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                 |
      | details_recIndex       | 0                                        |
      | details_digitalAddress | {"type": "APPIO", "address": "DISABLED"} |



  #         RECAPITO SPECIALE

  @addressBook1 @courtesyMessage @cleanAddressBook #rif srs 42
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_21] Verifica successione elementi - Invio DIGITALE con domicilio digitale PEC SPECIALE, cortesia email per PF
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Then viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei           |
      | taxId           | GLLGLL64B15G702I          |
      | digitalDomicile | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @addressBook2 @courtesyMessage @cleanAddressBook #rif srs 42
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_22] Verifica successione elementi - Invio DIGITALE con domicilio PEC SPECIALE, cortesia email per PG
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    Then viene generata una nuova notifica
      | subject            | invio notifica a CucumberSpa |
      | senderDenomination | Comune di palermo            |
    And destinatario
      | denomination    | CucumberSpa               |
      | taxId           | 20517490320               |
      | digitalDomicile | testpagopa1@pec.pagopa.it |
      | recipientType   | PG                        |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @courtesyMessage @uatCM #rif srs 42
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_23] Verifica successione elementi - Invio DIGITALE con domicilio digitale PEC SPECIALE, cortesia APPIO per PF UAT
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination            | Matteo Rossi              |
      | taxId                   | AAAAAA00A00A000C          |
      | digitalDomicile         | testpagopa1@pec.pagopa.it |
      | physicalAddress_address | Via@ok_AR                 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                 |
      | details_recIndex       | 0                                        |
      | details_digitalAddress | {"type": "APPIO", "address": "DISABLED"} |


  @courtesyMessage @addressBook3 @bankCourtesyMessage #rif srs 42
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_24] Verifica successione elementi - Invio DIGITALE con domicilio digitale PEC SPECIALE, cortesia TPP solo PF
    Then viene generata una nuova notifica
      | subject            | notifica digitale con cucumber |
      | senderDenomination | Comune di palermo              |
    And destinatario Mario Gherkin e:
      | digitalDomicile | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                          |
      | details_recIndex       | 0                                 |
      | details_digitalAddress | {"type": "TPP", "address": "APP"} |


#           FLAG ON - ANALOGICO

  @addressBook1 @courtesyMessage @cleanAddressBook #rif srs 43
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_25] Verifica successione elementi - Invio ANALOGICO, cortesia email per PF
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination            | Galileo Galilei  |
      | taxId                   | GLLGLL64B15G702I |
      | physicalAddress_address | Via@ok_AR        |
      | digitalDomicile         | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SCHEDULE_ANALOG_WORKFLOW" sia immediatamente successivo a quello dell'evento "SEND_COURTESY_MESSAGE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @addressBook2 @courtesyMessage @cleanAddressBook #rif srs 43
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_26] Verifica successione elementi - Invio ANALOGICO, cortesia email per PG
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    Given viene generata una nuova notifica
      | subject            | invio notifica a Cucumber |
      | senderDenomination | Comune di palermo         |
    And destinatario
      | denomination    | Cucumber    |
      | recipientType   | PG          |
      | taxId           | 20517490320 |
      | physicalAddress | Via@ok_890  |
      | digitalDomicile | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SCHEDULE_ANALOG_WORKFLOW" sia immediatamente successivo a quello dell'evento "SEND_COURTESY_MESSAGE" con una differenza massima di 999 secondi
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                          |
      | details_recIndex       | 0                                                 |
      | details_digitalAddress | {"type": "EMAIL","address": "provaemail@test.it"} |


  @courtesyMessage @uatCM #rif srs 43
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_27] Verifica successione elementi - Invio ANALOGICO, cortesia APPIO per PF UAT
    Given viene generata una nuova notifica
      | subject | invio notifica a Rossi |
    And destinatario
      | denomination            | Matteo Rossi     |
      | taxId                   | AAAAAA00A00A000C |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@ok_AR        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SCHEDULE_ANALOG_WORKFLOW" sia immediatamente successivo a quello dell'evento "SEND_COURTESY_MESSAGE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                 |
      | details_recIndex       | 0                                        |
      | details_digitalAddress | {"type": "APPIO", "address": "DISABLED"} |


  @courtesyMessage @addressBook3 @bankCourtesyMessage #rif srs 43
  Scenario: [COURTESY_MESSAGE_SERCQ_F2_28] Verifica successione elementi - Invio ANALOGICO, cortesia TPP solo PF
    Given si predispone addressbook per l'utente "Mario Gherkin"
    Then l'utente "Mario Gherkin" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL      |
      | physicalAddress_address | Via@ok_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SCHEDULE_ANALOG_WORKFLOW" sia immediatamente successivo a quello dell'evento "SEND_COURTESY_MESSAGE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                          |
      | details_recIndex       | 0                                 |
      | details_digitalAddress | {"type": "TPP", "address": "APP"} |


  @e2e @addressBook1
  Scenario: [E2E-SEND_COURTESY_MESSAGE_1] invio messaggio di cortesia - invio per email
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "Comune_1"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                                |
      | details                | NOT_NULL                                            |
      | details_digitalAddress | {"address": "provaemail2@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                   |

  @e2e
  Scenario: [E2E-SEND_COURTESY_MESSAGE_2] invio messaggio di cortesia - invio per SMS
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination    | Louis Armstrong  |
      | taxId           | RMSLSO31M04Z404R |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                        |
      | details                | NOT_NULL                                    |
      | details_digitalAddress | {"address": "+393214210000", "type": "SMS"} |
      | details_recIndex       | 0                                           |

  @e2e @ignore
  Scenario: [E2E-SEND_COURTESY_MESSAGE_3] invio messaggio di cortesia - invio per AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination    | Cristoforo Colombo |
      | taxId           | CLMCST42R12D969Z   |
      | digitalDomicile | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                |
      | details                | NOT_NULL                            |
      | details_digitalAddress | {"address": "...", "type": "APPIO"} |
      | details_recIndex       | 0                                   |

  @e2e  @ignore
  Scenario: [E2E-SEND-COURTESY-MESSAGE-4] Invio notifica mono destinatario con messaggio di cortesia non configurato
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination    | Dino Sauro       |
      | taxId           | DSRDNI00A01A225I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" non esista
      | loadTimeline | true |