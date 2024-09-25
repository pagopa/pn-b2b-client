Feature: Abilitazione domicilio digitale

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS -  PF
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Then vengono accettati i TOS



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