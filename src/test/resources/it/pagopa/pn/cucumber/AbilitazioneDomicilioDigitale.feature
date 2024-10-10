Feature: Abilitazione domicilio digitale



  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Then vengono accettati i TOS
    And viene disabilitato il servizio SERCQ SEND per il comune di "default"

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "Alda Merini"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene disabilitato il servizio SERCQ SEND per il comune di "default"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_2] Attivazione del servizio SERCQ SEND per recapito principale e presenza del recapito legale PEC
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene verificata l'assenza di indirizzi Pec per il comune "default"
    Then vengono accettati i TOS
    And viene disabilitato il servizio SERCQ SEND per il comune di "default"

  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_2] Attivazione del servizio SERCQ SEND per recapito principale e presenza del recapito legale PEC
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene verificata l'assenza di indirizzi Pec per il comune "default"
    Then vengono accettati i TOS
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

  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_5] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ
    Given si predispone addressbook per l'utente "CucumberSpa"
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
    Then vengono accettati i TOS

  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_7] Attivazione del servizio SERCQ SEND per recapito specifico per ente  e accettazione dei TOS
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_Root"
    Then vengono accettati i TOS

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
    Then vengono accettati i TOS


  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_8] Attivazione del servizio SERCQ SEND per recapito specifico per ente
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_Root"
    And viene verificata l'assenza di indirizzi Pec per il comune "Comune_Root"
    Then vengono accettati i TOS


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
    Then vengono accettati i TOS

  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_9] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene inserito un recapito legale "example@pecSuccess.it" per il comune "Comune_2"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_Root"
    Then vengono accettati i TOS

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
    Then vengono accettati i TOS


  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_22] Attivazione del servizio SERCQ SEND per recapito specifico per più enti e presenza del recapito legale PEC princi
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_1"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_2"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_2"
    Then vengono accettati i TOS

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_59] Creazione notifica digitale con servizio SERCQ attivo e verifica cambiamento workflow della notifica
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_DOMICILE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_FEEDBACK" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_59] Creazione notifica digitale con servizio SERCQ attivo e verifica cambiamento workflow della notifica
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_DOMICILE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_FEEDBACK" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_60] Creazione notifica digitale con servizio SERCQ attivo per ente specifico e verifica workflow notifica previsto per SERCQ
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_1"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"


  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_60] Creazione notifica digitale con servizio SERCQ attivo per ente specifico e verifica workflow notifica previsto per SERCQ
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_1"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_61] Creazione notifica digitale verso utente che abbia attivato servizio SERCQ
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_1"
    Then viene verificata la presenza di 2 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                               |
      | details                | NOT_NULL                                           |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                  |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"



  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_61] Creazione notifica digitale verso utente che abbia attivato servizio SERCQ
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_1"
    And viene verificata la presenza di 2 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                               |
      | details                | NOT_NULL                                           |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                  |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_62] Creazione notifica digitale verso utente che abbia attivato servizio SERCQ
    Given si predispone addressbook per l'utente "Mario Gherkin"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And  viene verificata l'assenza di indirizzi Pec per il comune "default"
    And  viene verificata l'assenza di indirizzi Pec per il comune "Comune_1"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_2"
    And viene verificato che Sercq sia "abilitato" per il comune "Comune_2"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And ricerca ed effettua download del legalFact con la categoria "DIGITAL_DELIVERY_FAILURE"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_MANCATO_RECAPITO"
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Attestazione opponibile a terzi: mancato recapito digitale                                                 |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Gherkin                                                                                              |
      | DESTINATARIO_CODICE_FISCALE               | CLMCST42R12D969Z                                                                                           |
      | DESTINATARIO_DOMICILIO_DIGITALE           | test@fail.it                                                                                               |
      | DESTINATARIO_TIPO_DOMICILIO_DIGITALE      | Domicilio eletto presso la Pubblica Amministrazione mittente ex art.26, comma 5 lettera b del D.L. 76/2020 |



  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_79] Creazione notifica digitale con servizio SERCQ con Indirizzo speciale settato
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | testpagopa1@pec.pagopa.it  |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"

  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_79] Creazione notifica digitale con servizio SERCQ con Indirizzo speciale settato
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination            | CucumberSpa              |
      | taxId                   | 20517490320         |
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_80] Creazione notifica digitale multi destinatario con servizio SERCQ attivo per il primo destinatario e indirizzo PEC speciale per il secondo destinatario
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario CucumberSpa
    And destinatario
      | denomination    | Galileo Galilei        |
      | taxId           | GLLGLL64B15G702I       |
      | digitalDomicile | example3@pecSuccess.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "SEND_DIGITAL_PROGRESS"
    And esiste l'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS" per l'utente 1

  @sercq @addressBook1 @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_81] Creazione notifica digitale multi destinatario con servizio SERCQ attivo per il primo destinatario e indirizzo PEC di piattaforma per il secondo destinatario
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    And destinatario CucumberSpa
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "SEND_DIGITAL_PROGRESS"
    And esiste l'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS" per l'utente 1


  
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_82] Creazione notifica digitale multi destinatario con servizio SERCQ attivo per il primo destinatario e workflow analogico per il secondo destinatario
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per il comune "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "SEND_DIGITAL_PROGRESS"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" per l'utente 1