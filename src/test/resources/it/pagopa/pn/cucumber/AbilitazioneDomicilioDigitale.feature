Feature: Abilitazione domicilio digitale



  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Then l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    Then l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq
    And viene disabilitato il servizio SERCQ SEND per il comune di "default"


  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "Alda Merini"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene disabilitato il servizio SERCQ SEND per il comune di "default"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_2] Attivazione del servizio SERCQ SEND per recapito principale e presenza del recapito legale PEC
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene verificata l'assenza di indirizzi Pec per il comune "default"
    Then l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq
    And viene disabilitato il servizio SERCQ SEND per il comune di "default"

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_2] Attivazione del servizio SERCQ SEND per recapito principale e presenza del recapito legale PEC
    Given si predispone addressbook per l'utente "Alda Merini"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene verificata l'assenza di indirizzi Pec per il comune "default"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    Then l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq
    And viene disabilitato il servizio SERCQ SEND per il comune di "default"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_3] Disattivazione del servizio SERCQ SEND per recapito principale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per il comune "default"
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Then viene disabilitato il servizio SERCQ SEND per il comune di "default"
    And viene verificato che Sercq sia "disabilitato" per il comune "default"

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_3] Disattivazione del servizio SERCQ SEND per recapito principale
    Given si predispone addressbook per l'utente "Alda Merini"
    And viene attivato il servizio SERCQ SEND per il comune "default"
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Then viene disabilitato il servizio SERCQ SEND per il comune di "default"
    And viene verificato che Sercq sia "disabilitato" per il comune "default"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_5] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene verificato che Sercq sia "disabilitato" per il comune "default"

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_5] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ
    Given si predispone addressbook per l'utente "Alda Merini"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene verificato che Sercq sia "disabilitato" per il comune "default"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_6] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ, con OTP errato
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserito un recapito legale "example3@pecSuccess.it" con verification code errato "*$%&+/"
    And viene verificata l'assenza di indirizzi Pec per il comune "default"
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene disabilitato il servizio SERCQ SEND per il comune di "default"


  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_6] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ, con OTP errato
    Given si predispone addressbook per l'utente "Alda Merini"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserito un recapito legale "example3@pecSuccess.it" con verification code errato "*$%&+/"
    Then viene verificata l'assenza di indirizzi Pec per il comune "default"
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene disabilitato il servizio SERCQ SEND per il comune di "default"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_7] Attivazione del servizio SERCQ SEND per recapito specifico per ente  e accettazione dei TOS
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_Root"
    Then l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_7] Attivazione del servizio SERCQ SEND per recapito specifico per ente  e accettazione dei TOS
    Given si predispone addressbook per l'utente "Alda Merini"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_Root"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_8] Attivazione del servizio SERCQ SEND per recapito specifico per ente
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_Root"
    And viene verificata l'assenza di indirizzi Pec per il comune "Comune_Root"
    Then l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq


  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_8] Attivazione del servizio SERCQ SEND per recapito specifico per ente
    Given si predispone addressbook per l'utente "Alda Merini"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_Root"
    And viene verificata l'assenza di indirizzi Pec per il comune "Comune_Root"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_9] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene inserito un recapito legale "example@pecSuccess.it" per il comune "Comune_2"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_Root"
    Then l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_9] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "Alda Merini"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene inserito un recapito legale "example@pecSuccess.it" per il comune "Comune_2"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_Root"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_10] Attivazione del servizio SERCQ SEND per recapito principale e inserimento della PEC come recapito specifico per ente
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "default"

  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_10] Attivazione del servizio SERCQ SEND per recapito principale e inserimento della PEC come recapito specifico per ente
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "default"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_11] Inserimento indirizzo PEC come recapito specifico per ente, dopo attivazione del servizio SERCQ per stesso ente
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_1"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"


  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_11] Inserimento indirizzo PEC come recapito specifico per ente, dopo attivazione del servizio SERCQ per stesso ente
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_1"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_12] Inserimento indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
    Then viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"


  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_12] Inserimento indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
    Then viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_13] Modifica indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
    And viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root" con verification code errato "*$%&+/"
    And viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"


  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_13] Modifica indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
    And viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root" con verification code errato "*$%&+/"
    And viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_14] Elimina indirizzo PEC come recapito specifico per ente con la presenza di una PEC già associata
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    Then viene rimossa se presente la pec per il comune "Comune_1"
    And  viene verificata l'assenza di indirizzi Pec per il comune "Comune_1"

  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_14] Elimina indirizzo PEC come recapito specifico per ente con la presenza di una PEC già associata
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    Then viene rimossa se presente la pec per il comune "Comune_1"
    And  viene verificata l'assenza di indirizzi Pec per il comune "Comune_1"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_15] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_1"
    Then viene disabilitato il servizio SERCQ SEND per il comune di "Comune_1"
    And viene verificato che Sercq sia "disabilitato" per il comune "Comune_1"


  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_15] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_1"
    Then viene disabilitato il servizio SERCQ SEND per il comune di "Comune_1"
    And viene verificato che Sercq sia "disabilitato" per il comune "Comune_1"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_16] Attivazione servizio SERCQ e aggiunta recapito di cortesia email
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_16] Attivazione servizio SERCQ e aggiunta recapito di cortesia email
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_22] Attivazione del servizio SERCQ SEND per recapito specifico per più enti e presenza del recapito legale PEC princi
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_1"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_2"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_2"
    Then l'utente "Galileo Galilei" "ACCETTA" i tos per sercq
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq