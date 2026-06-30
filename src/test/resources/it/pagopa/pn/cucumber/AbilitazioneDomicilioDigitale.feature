Feature: Abilitazione domicilio digitale


  @sercq @addressBook2 @sercqTos #bug fixing
  Scenario: [ABILITAZIONE_DOMICILIO_TOS_ACCETTATI] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "disabilitato" come indirizzo di "default"
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

    And viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" come indirizzo di "default"
    And viene disabilitato il servizio SERCQ SEND come indirizzo di "default"


  @sercq @addressBook2 @sercqTos #bug fixing
  Scenario: [ABILITAZIONE_DOMICILIO_TOS_ACCETTATI_2] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And viene disabilitato il servizio SERCQ SEND per la PA "Comune_Root"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_Root"

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

    And viene attivato il servizio SERCQ SEND per la PA "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_Root"
    And viene disabilitato il servizio SERCQ SEND per la PA "Comune_Root"

  @sercq @addressBook2 @sercqTos #bug fixing
  Scenario: [ABILITAZIONE_DOMICILIO_TOS_NON_ACCETTATI] Attivazione del servizio SERCQ SEND per recapito principale e NON accettazione dei TOS
    Given si predispone addressbook per l'utente "CucumberSpa"
#    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And viene disabilitato il servizio SERCQ SEND per la PA "default"
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

    And vengono rimossi eventuali recapiti presenti per l'utente
    Then l'utente "CucumberSpa" "NON ACCETTA" i tos per sercq
    And viene attivato il servizio SERCQ SEND per recapito "default" con errore
    Then l'operazione ha prodotto un errore con status code "400"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "default"

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"

    And viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "default"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_2] Attivazione del servizio SERCQ SEND per recapito principale e presenza del recapito legale PEC
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    Then viene attivato il servizio SERCQ SEND come indirizzo di "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene verificata l'assenza di indirizzi Pec per il comune "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "default"

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_2] Attivazione del servizio SERCQ SEND per recapito principale e presenza del recapito legale PEC
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    Then l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"

    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene verificata l'assenza di indirizzi Pec per il comune "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "default"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_3] Disattivazione del servizio SERCQ SEND per recapito principale
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"

    And viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    Then viene disabilitato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_1"

    And viene attivato il servizio SERCQ SEND per la PA "Comune_2"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_2"
    Then viene disabilitato il servizio SERCQ SEND per la PA "Comune_2"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_2"

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_3] Disattivazione del servizio SERCQ SEND per recapito principale
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"

    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Then viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"

    And viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    Then viene disabilitato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_1"

    And viene attivato il servizio SERCQ SEND per la PA "Comune_2"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_2"
    Then viene disabilitato il servizio SERCQ SEND per la PA "Comune_2"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_2"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_5] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_5] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"

    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_6] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ, con OTP errato
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    Then viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene inserito un recapito legale "example3@pecSuccess.it" con verification code errato "*$%&+/"
    And viene verificata l'assenza di indirizzi Pec per il comune "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "default"


  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_6] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ, con OTP errato
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene inserito un recapito legale "example3@pecSuccess.it" con verification code errato "*$%&+/"
    Then viene verificata l'assenza di indirizzi Pec per il comune "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "default"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_7] Attivazione del servizio SERCQ SEND per recapito specifico per ente  e accettazione dei TOS
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_Root"

  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_7] Attivazione del servizio SERCQ SEND per recapito specifico per ente  e accettazione dei TOS
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_Root"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_8] Attivazione del servizio SERCQ SEND per recapito specifico per ente
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_Root"
    And viene verificata l'assenza di indirizzi Pec per il comune "Comune_Root"


  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_8] Attivazione del servizio SERCQ SEND per recapito specifico per ente
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_Root"
    And viene verificata l'assenza di indirizzi Pec per il comune "Comune_Root"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_9] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene inserito un recapito legale "example@pecSuccess.it" per il comune "Comune_2"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"
    Then viene attivato il servizio SERCQ SEND per la PA "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"


  @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_9] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i tos per sercq
    And l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"
    And viene inserito un recapito legale "example4@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene inserito un recapito legale "example@pecSuccess.it" per il comune "Comune_2"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"
    Then viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_2"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"
    Then viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_2"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"
    Then viene attivato il servizio SERCQ SEND per la PA "Comune_Root"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_11] Inserimento indirizzo PEC come recapito specifico per ente, dopo attivazione del servizio SERCQ per stesso ente
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_1"


  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_11] Inserimento indirizzo PEC come recapito specifico per ente, dopo attivazione del servizio SERCQ per stesso ente
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_1"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_1"

#  @sercq @addressBook1
#  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_12] Inserimento indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
#    Given si predispone addressbook per l'utente "Galileo Galilei"
#    And vengono rimossi eventuali recapiti presenti per l'utente
#    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
#    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
#    And viene inserito un recapito legale "example@pecSuccess.it"
#    And viene controllato che siano presenti pec verificate inserite per il comune "default"
#    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
#    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
#    Then viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root"
#    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"


#  @sercq @addressBook2
#  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_12] Inserimento indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
#    Given si predispone addressbook per l'utente "CucumberSpa"
#    And vengono rimossi eventuali recapiti presenti per l'utente
#    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
#   Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
#    And viene inserito un recapito legale "example@pecSuccess.it"
#    And viene controllato che siano presenti pec verificate inserite per il comune "default"
#    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
#    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"
#    Then viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root"
#    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"


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
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    Then viene disabilitato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"


  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_15] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    Then viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    Then viene disabilitato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_1"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"


  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_16] Attivazione servizio SERCQ e aggiunta recapito di cortesia email
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

  @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_16] Attivazione servizio SERCQ e aggiunta recapito di cortesia email
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene inserita l'email di cortesia "provaemail1@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

#  @sercq @addressBook1
#  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_22] Attivazione del servizio SERCQ SEND per recapito specifico per più enti e presenza del recapito legale PEC princi
#    Given si predispone addressbook per l'utente "Galileo Galilei"
#    Then l'utente "Galileo Galilei" "ACCETTA" i tos per sercq v2
#    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
#    And vengono rimossi eventuali recapiti presenti per l'utente
#   And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
#   Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
#    Then viene attivato il servizio SERCQ SEND per la PA "default"
#    And viene verificato che Sercq sia "abilitato" per la PA "default"
#    And viene attivato il servizio SERCQ SEND per la PA "Comune_1"
#    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
#    And viene attivato il servizio SERCQ SEND per la PA "Comune_2"
#    And viene verificato che Sercq sia "abilitato" per la PA "Comune_2"
#
#
#
#  @sercq @addressBook2
#  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_22] Attivazione del servizio SERCQ SEND per recapito specifico per più enti e presenza del recapito legale PEC princi
#    Given si predispone addressbook per l'utente "CucumberSpa"
#    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
#    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
#    And vengono rimossi eventuali recapiti presenti per l'utente
#    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
#    Then viene attivato il servizio SERCQ SEND per recapito principale
#    And viene verificato che Sercq sia "abilitato" per il comune "default"
#    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
#    And viene verificato che Sercq sia "abilitato" per il comune "Comune_1"
#    And viene attivato il servizio SERCQ SEND per il comune "Comune_2"
#    And viene verificato che Sercq sia "abilitato" per il comune "Comune_2"


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_59] Creazione notifica digitale con servizio SERCQ attivo e verifica cambiamento workflow della notifica
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
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
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_DOMICILE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_FEEDBACK" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi

  @sercq @precondition @addressBook1 @webhook1 @cleanWebhook @ignoreHotfixTemp #temp
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_WEBHOOK_V10] Creazione di un nuovo stream con versione V10 e controllo che SERCQ non è presente
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    And si predispone 1 nuovo stream denominato "stream-test" con eventType "TIMELINE" con versione "V10"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream per il "Comune_1" con versione "V10"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "ACCEPTED" con la versione "V10"
    And si verifica la "ASSENZA" di SERCQ con la versione "V10"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  @sercq @addressBook1 @webhookV23 @precondition @cleanWebhook @webhook2 @ignoreHotfixTemp #temp
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_WEBHOOK_V23] Creazione di un nuovo stream con versione V23 e controllo che SERCQ è presente
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    And si predispone 1 nuovo stream denominato "stream-test23" con eventType "TIMELINE" con versione "V23"
    And Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    And viene impostata l'apikey appena generata
    And viene aggiornata la apiKey utilizzata per gli stream
    And si crea il nuovo stream con versione "V23" per il "Comune_1" con un gruppo disponibile "NO_GROUPS"
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi dello stream del "Comune_1" fino allo stato "ACCEPTED" con la versione "V23"
    And vengono letti gli eventi dello stream del "Comune_1" fino all'elemento di timeline "SEND_DIGITAL_FEEDBACK" con la versione "V23"
    And si verifica la "PRESENZA" di SERCQ con la versione "V23"
    And viene modificato lo stato dell'apiKey in "BLOCK"
    And l'apiKey viene cancellata

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_59] Creazione notifica digitale con servizio SERCQ attivo e verifica cambiamento workflow della notifica
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_DOMICILE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_FEEDBACK" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_60] Creazione notifica digitale con servizio SERCQ attivo per ente specifico e verifica workflow notifica previsto per SERCQ
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"


  @sercq @addressBook2 @ignoreHotfixTemp #temp
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_60] Creazione notifica digitale con servizio SERCQ attivo per ente specifico e verifica workflow notifica previsto per SERCQ
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_1"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_61] Creazione notifica digitale verso utente che abbia attivato servizio SERCQ
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
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
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
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
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_1"
    And viene verificata la presenza di 2 recapiti di cortesia inseriti per l'utente "CucumberSpa"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario CucumberSpa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                               |
      | details                | NOT_NULL                                           |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                  |
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"

  @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_61_79] Creazione notifica digitale con servizio SERCQ con Indirizzo speciale settato
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
#    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
#    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    And viene inserita l'email di cortesia "provaemail1@test.it" per il comune "Comune_1"
    And viene verificata la presenza di 2 recapiti di cortesia inseriti per l'utente "Galileo Galilei"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei           |
      | taxId           | GLLGLL64B15G702I          |
      | digitalDomicile | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And si verifica la corretta acquisizione della notifica
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                                |
      | details                | NOT_NULL                                            |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                   |
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_DOMICILE" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi
    And viene verificato che il timestamp dell'evento "SEND_DIGITAL_FEEDBACK" sia immediatamente successivo a quello dell'evento "AAR_GENERATION" con una differenza massima di 60 secondi


  @sercq @addressBook4
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_62] Creazione notifica digitale verso utente che abbia attivato servizio SERCQ
    Given si predispone addressbook per l'utente "Mario Gherkin"
    Then l'utente "Mario Gherkin" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "Comune_1"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_1"
    And l'utente "Mario Gherkin" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Mario Gherkin"
    And  viene verificata l'assenza di indirizzi Pec per il comune "default"
    And  viene verificata l'assenza di indirizzi Pec per il comune "Comune_1"
    And viene attivato il servizio SERCQ SEND per la PA "Comune_2"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_2"
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
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
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a Galileo Galilei |
    And destinatario
      | denomination    | Galileo Galilei           |
      | taxId           | GLLGLL64B15G702I          |
      | digitalDomicile | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_79] Creazione notifica digitale con servizio SERCQ con Indirizzo speciale settato
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i tos per sercq
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination            | CucumberSpa               |
      | taxId                   | 20517490320               |
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then Viene verificato che non sia arrivato un evento di "SEND_DIGITAL_PROGRESS"


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_80] Creazione notifica digitale multi destinatario con servizio SERCQ attivo per il primo destinatario e indirizzo PEC speciale per il secondo destinatario
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario CucumberSpa
    And destinatario
      | denomination    | Galileo Galilei        |
      | taxId           | GLLGLL64B15G702I       |
      | digitalDomicile | example3@pecSuccess.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "SEND_DIGITAL_PROGRESS"
    And esiste l'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS" per l'utente 1

  ##TODO Analizzare il comportamento della doppia annotazione
  #@addressBook1 @addressBook2 @ignoreHotfixTemp #temp
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_81] Creazione notifica digitale multi destinatario con servizio SERCQ attivo per il primo destinatario e indirizzo PEC di piattaforma per il secondo destinatario
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
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
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "SEND_DIGITAL_PROGRESS"
    And esiste l'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS" per l'utente 1


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_82] Creazione notifica digitale multi destinatario con servizio SERCQ attivo per il primo destinatario e workflow analogico per il secondo destinatario
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galileii" controlla l'accettazione "positiva" dei tos per sercq v2
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    Given viene generata una nuova notifica
      | subject | invio notifica a CucumberSpa |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    And destinatario Gherkin Analogic e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi e verifico che l'utente 0 non abbia associato un evento "SEND_DIGITAL_PROGRESS"
    And esiste l'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" per l'utente 1



    #    SERCQ FASE 2

  @sercqF2 @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_1] Attivazione SercQ principale con email di cortesia attiva
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq v2

    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"

    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"

  @sercqF2 @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_2] Attivazione SercQ per ente con email di cortesia attiva e pec principale attiva
    Given si predispone addressbook per l'utente "Alda Merini"
    Then l'utente "Alda Merini" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Alda Merini" controlla l'accettazione "positiva" dei tos per sercq v2

    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Alda Merini"

    And viene attivato il servizio SERCQ SEND per la PA "Comune_2"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_2"
  #  1. Pec per ente specifico o generale?

  @sercqF2 @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_3] Attivazione pec principale con email di cortesia non attiva
    Given si predispone addressbook per l'utente "Alda Merini"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Alda Merini"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"

  @sercqF2 @sercq @addressBook3
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_4] Attivazione pec per ente con email di cortesia non attiva
    Given si predispone addressbook per l'utente "Alda Merini"
    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Alda Merini"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"

  @sercqF2 @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_5] Negata attivazione di SercQ principale con email di cortesia non attiva
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2

    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    And viene attivato il servizio SERCQ SEND per recapito "default" con errore
    Then l'operazione ha prodotto un errore con status code "400"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"


  @sercqF2 @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_6] Negata attivazione di SercQ per ente con email di cortesia non attiva e pec principale attiva
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2

    And vengono rimossi eventuali recapiti presenti per l'utente
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"

    And viene attivato il servizio SERCQ SEND per recapito "Comune_Root" con errore
    Then l'operazione ha prodotto un errore con status code "400"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_Root"


  @sercqF2 @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_7] Disattivazione email di cortesia con SercQ principale disattivato
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2

    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"
    And viene disabilitato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "disabilitato" per la PA "default"

    And viene cancellata l'email di cortesia per il comune "default"
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"


  @sercqF2 @sercq @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_8] Disattivazione email di cortesia con SercQ per ente disattivato e pec principale attiva
    Given si predispone addressbook per l'utente "Galileo Galilei"
    Then l'utente "Galileo Galilei" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "Galileo Galilei" controlla l'accettazione "positiva" dei tos per sercq v2

    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_2"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

    And viene attivato il servizio SERCQ SEND per la PA "Comune_2"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_2"
    And viene disabilitato il servizio SERCQ SEND per la PA "Comune_2"
    And viene verificato che Sercq sia "disabilitato" per la PA "Comune_2"

    And viene cancellata l'email di cortesia per il comune "default"
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "Galileo Galilei"

  @sercqF2 @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_9] Disattivazione email di cortesia con pec principale attiva
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    And viene cancellata l'email di cortesia per il comune "default"
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "CucumberSpa"

  @sercqF2 @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_10] Disattivazione email di cortesia con pec per ente attiva
    Given si predispone addressbook per l'utente "CucumberSpa"
    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_Root"

    And viene cancellata l'email di cortesia per il comune "default"
    Then viene verificata la presenza di 0 recapiti di cortesia inseriti per l'utente "CucumberSpa"

  @sercqF2 @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_11] Negata disattivazione email cortesia con SercQ principale attivo
    Given si predispone addressbook per l'utente "CucumberSpa"

    And vengono rimossi eventuali recapiti presenti per l'utente

    Then l'utente "CucumberSpa" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq v2

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"

    And viene cancellata l'email di cortesia per il comune "default"
    Then l'operazione ha prodotto un errore con status code "400"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

  @sercqF2 @sercq @addressBook2
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_12] Negata disattivazione email cortesia con SercQ per ente attivo
    Given si predispone addressbook per l'utente "CucumberSpa"

    And vengono rimossi eventuali recapiti presenti per l'utente

    Then l'utente "CucumberSpa" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq v2

    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene controllato che siano presenti pec verificate inserite per il comune "default"

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

    And viene attivato il servizio SERCQ SEND per la PA "Comune_2"
    And viene verificato che Sercq sia "abilitato" per la PA "Comune_2"

    And viene cancellata l'email di cortesia per il comune "default"
    Then l'operazione ha prodotto un errore con status code "400"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

  #@sercqF2 @sercq @addressBook2  # Lo sviluppo di questa fix è rimandato a data da definirsi
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_F2_13] Negata disattivazione email cortesia con SercQ per ente attivo
    Given si predispone addressbook per l'utente "CucumberSpa"
    Then l'utente "CucumberSpa" "ACCETTA" i termini di servizio di tipo: TOS_SERCQ
    And l'utente "CucumberSpa" controlla l'accettazione "positiva" dei tos per sercq v2

    And vengono rimossi eventuali recapiti presenti per l'utente

    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"

    And viene attivato il servizio SERCQ SEND per la PA "default"
    And viene verificato che Sercq sia "abilitato" per la PA "default"

    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_2"
    And viene controllato che siano presenti pec verificate inserite per il comune "Comune_2"

    And viene cancellata l'email di cortesia per il comune "default"
    Then l'operazione ha prodotto un errore con status code "400"
    Then viene verificata la presenza di 1 recapiti di cortesia inseriti per l'utente "CucumberSpa"