Feature: Abilitazione domicilio digitale

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS - PF
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS - PG
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_3] Disattivazione del servizio SERCQ SEND per recapito principale - PF
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Then viene disabilitato il servizio SERCQ SEND


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_3] Disattivazione del servizio SERCQ SEND per recapito principale - PG
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Then viene disabilitato il servizio SERCQ SEND

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_7] Attivazione del servizio SERCQ SEND per recapito specifico per ente  e accettazione dei TOS -  PF
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_7] Attivazione del servizio SERCQ SEND per recapito specifico per ente  e accettazione dei TOS -  PG
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    Then vengono accettati i TOS


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_2] Attivazione del servizio SERCQ SEND per recapito principale e presenza del recapito legale PEC DA PF
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    Then viene attivato il servizio SERCQ SEND come recapito principale
    And viene verificata l' assenza di pec inserite per l'utente "Lucio Anneo Seneca"
    Then Viene richiesto l'ultimo consenso di tipo "TOS"
    And Il recupero del consenso non ha prodotto errori
    And Il consenso è accettato

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_5] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ DA PF
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    Then viene attivato il servizio SERCQ SEND come recapito principale
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene verificata la presenza di pec inserite per l'utente "Lucio Anneo Seneca"


  #TODO vedere i tos
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_6] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ, con OTP errato
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    Then viene attivato il servizio SERCQ SEND come recapito principale


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_8] Attivazione del servizio SERCQ SEND per recapito specifico per ente per PF
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    #And viene cancellata la pec per il comune "Comune_Root"
    And viene verificata l' assenza di pec inserite per l'utente "Lucio Anneo Seneca" per il comune "Comune_Root"
    Then Viene richiesto l'ultimo consenso di tipo "TOS"
    And Il recupero del consenso non ha prodotto errori
    And Il consenso è accettato


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_9] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti per ente per PF
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene inserito un recapito legale "example@pecSuccess.it" per il comune "Comune_2"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificata la presenza di pec inserite per l'utente "Lucio Anneo Seneca" per il comune "Comune_1"
    And viene verificata la presenza di pec inserite per l'utente "Lucio Anneo Seneca" per il comune "Comune_2"
    Then Viene richiesto l'ultimo consenso di tipo "TOS"
    And Il recupero del consenso non ha prodotto errori
    And Il consenso è accettato


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_10] Attivazione del servizio SERCQ SEND per recapito principale e inserimento della PEC come recapito specifico per ente per ente per PF
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    Then viene attivato il servizio SERCQ SEND come recapito principale
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene verificata la presenza di pec inserite per l'utente "Lucio Anneo Seneca" per il comune "Comune_1"
    #controllo su sercQ attivo?


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_11] Inserimento indirizzo PEC come recapito specifico per ente, dopo attivazione del servizio SERCQ per stesso ente da PF
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"


    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene inserito un recapito legale "example@pecSuccess.it" per il comune "Comune_2"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificata la presenza di pec inserite per l'utente "Lucio Anneo Seneca" per il comune "Comune_1"
    And viene verificata la presenza di pec inserite per l'utente "Lucio Anneo Seneca" per il comune "Comune_2"
    Then Viene richiesto l'ultimo consenso di tipo "TOS"
    And Il recupero del consenso non ha prodotto errori
    And Il consenso è accettato







#TODO: migliorare con un pò di assertion le chiamate e/o con verifica dell'assenza di errori
  #TODO: attenzione a mantenere l'annotation per limitare la concorrenza
  #TODO: viene usata anche sharedStep valutare se spostare gli step (creati appositamente per il test)
  @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_1] invio messaggio di cortesia - invio notifica per email per ente padre per PG
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"

    And viene inserito un recapito legale "example@pecSuccess.it"


    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene verificata la presenza di pec inserite per l'utente "Lucio Anneo Seneca"
    And viene verificata la presenza di recapiti di cortesia inseriti per l'utente "Lucio Anneo Seneca"
    And viene rimossa se presente la pec di piattaforma di "Lucio Anneo Seneca"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_Root"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "Comune_Root"
    And viene verificata la presenza di recapiti di cortesia inseriti per l'utente "Lucio Anneo Seneca"
    And viene verificata la presenza di recapiti di cortesia inseriti per l'utente "Lucio Anneo Seneca"
    And viene cancellata l'email di cortesia per il comune "Comune_Root"