Feature: Messaggi di cortesia email recapitati alla casella di test

  @courtesyEmail @courtesyEmailNrt @addressBook1 @cleanAddressBook @work-item:QA-19117 @work-item:QA-19118 @work-item:QA-19119
  Scenario: [MESSAGGI_CORTESIA_EMAIL_1] Notifica bilingue a PF con domicilio digitale SEND: email di cortesia recapitata alla casella di test
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "send.qa.cortesia@gmail.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject             | invio notifica a Galileo Galilei |
      | senderDenomination  | Comune di milano                 |
      | additionalLanguages | DE                               |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                                   |
      | details_recIndex       | 0                                                          |
      | details_digitalAddress | {"type": "EMAIL","address": "send.qa.cortesia@gmail.com"} |
    Then l'email di cortesia della notifica arriva nella casella di test
    And l'oggetto dell'email di cortesia riporta l'ente mittente e lo IUN
    And il corpo dell'email di cortesia riporta l'ente mittente e lo IUN
    And l'email di cortesia riporta i testi nella lingua aggiuntiva della notifica e in italiano
    And l'email di cortesia ha in allegato l'AAR della notifica

  @courtesyEmail @courtesyEmailNrt @addressBook2 @cleanAddressBook @work-item:QA-19117 @work-item:QA-19118 @work-item:QA-19119
  Scenario: [MESSAGGI_CORTESIA_EMAIL_2] Notifica bilingue a PG con recapito speciale PEC: email di cortesia recapitata alla casella di test
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "send.qa.cortesia@gmail.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    Given viene generata una nuova notifica
      | subject             | invio notifica a CucumberSpa |
      | senderDenomination  | Comune di milano             |
      | additionalLanguages | FR                           |
    And destinatario
      | denomination            | CucumberSpa               |
      | taxId                   | 20517490320               |
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
      | recipientType           | PG                        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | details                | NOT_NULL                                                   |
      | details_recIndex       | 0                                                          |
      | details_digitalAddress | {"type": "EMAIL","address": "send.qa.cortesia@gmail.com"} |
    Then l'email di cortesia della notifica arriva nella casella di test
    And l'oggetto dell'email di cortesia riporta l'ente mittente e lo IUN
    And il corpo dell'email di cortesia riporta l'ente mittente e lo IUN
    And l'email di cortesia riporta i testi nella lingua aggiuntiva della notifica e in italiano
    And l'email di cortesia ha in allegato l'AAR della notifica

  @courtesyEmail @addressBook2 @cleanAddressBook @work-item:QA-19119
  Scenario: [MESSAGGI_CORTESIA_EMAIL_3] Notifica digitale a PG con domicilio digitale SEND: SEND_COURTESY_MESSAGE dopo SEND_DIGITAL_DOMICILE
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "send.qa.cortesia@gmail.com" per il comune "default"
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    Then l'email di cortesia della notifica arriva nella casella di test

  @courtesyEmail @addressBook1 @cleanAddressBook @work-item:QA-19119
  Scenario: [MESSAGGI_CORTESIA_EMAIL_4] Notifica digitale a PF con domicilio PEC della piattaforma: SEND_COURTESY_MESSAGE dopo SEND_DIGITAL_DOMICILE
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "send.qa.cortesia@gmail.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    Then l'email di cortesia della notifica arriva nella casella di test

  @courtesyEmail @addressBook2 @cleanAddressBook @work-item:QA-19119
  Scenario: [MESSAGGI_CORTESIA_EMAIL_5] Notifica digitale a PG con domicilio PEC della piattaforma: SEND_COURTESY_MESSAGE dopo SEND_DIGITAL_DOMICILE
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "send.qa.cortesia@gmail.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica a CucumberSpa |
      | senderDenomination | Comune di palermo            |
    And destinatario
      | denomination    | CucumberSpa |
      | taxId           | 20517490320 |
      | digitalDomicile | NULL        |
      | recipientType   | PG          |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    Then l'email di cortesia della notifica arriva nella casella di test

  @courtesyEmail @addressBook1 @cleanAddressBook @work-item:QA-19119
  Scenario: [MESSAGGI_CORTESIA_EMAIL_6] Notifica digitale a PF con recapito speciale PEC: SEND_COURTESY_MESSAGE dopo SEND_DIGITAL_DOMICILE
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "send.qa.cortesia@gmail.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination            | Galileo Galilei           |
      | taxId                   | GLLGLL64B15G702I          |
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And viene verificato che il timestamp dell'evento "SEND_COURTESY_MESSAGE" sia immediatamente successivo a quello dell'evento "SEND_DIGITAL_DOMICILE" con una differenza massima di 999 secondi
    Then l'email di cortesia della notifica arriva nella casella di test

  @courtesyEmail @addressBook1 @cleanAddressBook @work-item:QA-19119
  Scenario: [MESSAGGI_CORTESIA_EMAIL_7] Notifica analogica a PF senza domicilio digitale: SEND_COURTESY_MESSAGE prima di SCHEDULE_ANALOG_WORKFLOW
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "send.qa.cortesia@gmail.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination            | Galileo Galilei  |
      | taxId                   | GLLGLL64B15G702I |
      | physicalAddress_address | Via@ok_AR        |
      | digitalDomicile         | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"
    And viene verificato che il timestamp dell'evento "SCHEDULE_ANALOG_WORKFLOW" sia immediatamente successivo a quello dell'evento "SEND_COURTESY_MESSAGE" con una differenza massima di 999 secondi
    Then l'email di cortesia della notifica arriva nella casella di test

  @courtesyEmail @addressBook2 @cleanAddressBook @work-item:QA-19119
  Scenario: [MESSAGGI_CORTESIA_EMAIL_8] Notifica analogica a PG senza domicilio digitale: SEND_COURTESY_MESSAGE prima di SCHEDULE_ANALOG_WORKFLOW
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "send.qa.cortesia@gmail.com" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    Given viene generata una nuova notifica
      | subject            | invio notifica a CucumberSpa |
      | senderDenomination | Comune di palermo            |
    And destinatario
      | denomination            | CucumberSpa |
      | recipientType           | PG          |
      | taxId                   | 20517490320 |
      | physicalAddress_address | Via@ok_890  |
      | digitalDomicile         | NULL        |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_COURTESY_MESSAGE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_ANALOG_WORKFLOW"
    And viene verificato che il timestamp dell'evento "SCHEDULE_ANALOG_WORKFLOW" sia immediatamente successivo a quello dell'evento "SEND_COURTESY_MESSAGE" con una differenza massima di 999 secondi
    Then l'email di cortesia della notifica arriva nella casella di test
