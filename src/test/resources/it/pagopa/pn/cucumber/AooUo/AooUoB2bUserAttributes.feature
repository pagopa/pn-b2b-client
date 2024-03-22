Feature: verifica feature aoo/uo con userAttributes

  @AOO_UO
  Scenario: [USER-ATTR_10] inserimento email di cortesia per ente figlio
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del email di cortesia "provaemail@test.it" per il comune "Comune_Son"
    And  l'inserimento ha prodotto un errore con status code "400"


  @AOO_UO
  Scenario: [USER-ATTR_10_PG] inserimento email di cortesia a PG ente figlio
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    When viene richiesto l'inserimento del email di cortesia "provaemail@test.it" per il comune "Comune_Son"
    And  l'inserimento ha prodotto un errore con status code "400"


  @AOO_UO
  Scenario: [USER-ATTR_11] inserimento numero di telefono ente figlio
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del numero di telefono "provaemail@test.it" per il comune "Comune_Son"
    And  l'inserimento ha prodotto un errore con status code "400"


  @AOO_UO
  Scenario: [USER-ATTR_11_PG] inserimento numero di telefono a PG per ente figlio- fallimento
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    When viene richiesto l'inserimento del numero di telefono "provaemail@test.it" per il comune "Comune_Son"
    And  l'inserimento ha prodotto un errore con status code "400"

  @AOO_UO
  Scenario: [USER-ATTR_12] inserimento recapito legale per ente figlio- fallimento
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento della pec "provaemail@test.it" per il comune "Comune_Son"
    And  l'inserimento ha prodotto un errore con status code "400"


  @AOO_UO
  Scenario: [USER-ATTR_12_PG] inserimento recapito legale a PG per ente figlio- fallimento
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    When viene richiesto l'inserimento della pec "provaemail@test.it" per il comune "Comune_Son"
    And  l'inserimento ha prodotto un errore con status code "400"

