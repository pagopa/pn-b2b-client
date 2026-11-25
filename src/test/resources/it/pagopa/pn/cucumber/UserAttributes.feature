Feature: Attributi utente

  @userAttributes
  Scenario: [B2B-PF-TOS_1] Viene recuperato il consenso TOS e verificato che sia accepted TOS_scenario positivo
    Given Viene richiesto l'ultimo consenso di tipo "TOS"
    Then Il recupero del consenso non ha prodotto errori
    And Il consenso è accettato

  @userAttributes
  Scenario: [USER-ATTR_2] inserimento pec errato
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento della pec "test@test@fail.@"
    Then l'inserimento ha prodotto un errore con status code "400"

  @userAttributes
  Scenario: [USER-ATTR_3] inserimento telefono errato
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del numero di telefono "+0013894516888"
    Then l'inserimento ha prodotto un errore con status code "400"

  @userAttributes
  Scenario: [USER-ATTR_4] inserimento pec non da errore
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento della pec "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{|}~-@gmail.com"

  @userAttributes
  Scenario Outline: [USER-ATTR_5] inserimento pec errato 250 caratteri
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento della pec "<pec>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | pec                                                                                                                                                                                                                                                                                  |
      | emailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibilecontienemolticarattericontienemolticarattericontienemolticarattericontienemolticarattericontienemolticaratteri@gmail.com |


  @userAttributes
  Scenario Outline: [USER-ATTR_6] inserimento pec errato con caratteri speciali
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento della pec "<pec>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | pec                                                                                                          |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;,@gmail.com |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;@gmail.com  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;@gmail.com  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\@gmail.com   |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><@gmail.com    |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°>@gmail.com     |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°@gmail.com      |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]@gmail.com       |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[,@gmail.com       |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§@gmail.com         |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù@gmail.com          |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçò@gmail.com           |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéç@gmail.com            |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèé@gmail.com             |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàè@gmail.com              |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìà@gmail.com               |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ì@gmail.com                |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()@gmail.com                 |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£(@gmail.com                  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£@gmail.com                   |

  @userAttributes
  Scenario: [USER-ATTR_7] inserimento email di cortesia non da errore
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del email di cortesia "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{|}~-@gmail.com"

  @userAttributes
  Scenario Outline: [USER-ATTR_8] inserimento email di cortesia errato 250 caratteri
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del email di cortesia "<email>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | email                                                                                                                                                                                                                                                                                  |
      | emailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibilecontienemolticarattericontienemolticarattericontienemolticarattericontienemolticarattericontienemolticaratteri@gmail.com |

  @userAttributes
  Scenario Outline: [USER-ATTR_9] inserimento email di cortesia errato con caratteri speciali
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del email di cortesia "<email>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | email                                                                                                          |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;,@gmail.com |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;@gmail.com  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\;@gmail.com  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><\@gmail.com   |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°><@gmail.com    |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°>@gmail.com     |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]°@gmail.com      |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[]@gmail.com       |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§[,@gmail.com       |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù§@gmail.com         |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçòù@gmail.com          |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéçò@gmail.com           |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèéç@gmail.com            |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàèé@gmail.com             |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìàè@gmail.com              |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ìà@gmail.com               |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()ì@gmail.com                |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£()@gmail.com                 |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£(@gmail.com                  |
      | qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{}~-£@gmail.com                   |


  @userAttributes @addressBook4
  Scenario Outline: [USER-ATTR_10] inserimento telefono e recupero header lang
    Given si predispone addressbook per l'utente "Mario Gherkin"
    Given vengono rimossi eventuali recapiti presenti per l'utente
    When viene richiesto l'inserimento del numero di telefono "+393214210000", e passo la lingua selezionata dal destinatario "<lang>"
    Then l'inserimento va a buon fine e NON ha prodotto un errore
    Examples:
      | lang |
      | IT   |
      | EN   |
      | SL   |
      | DE   |
      | FR   |

  @userAttributes @addressBook4
  Scenario Outline: [USER-ATTR_11] inserimento pec e recupero header lang
    Given si predispone addressbook per l'utente "Mario Gherkin"
    Given vengono rimossi eventuali recapiti presenti per l'utente
    When viene richiesto l'inserimento della pec "test@test.it", e passo la lingua selezionata dal destinatario "<lang>"
    Then l'inserimento va a buon fine e NON ha prodotto un errore
    Examples:
      | lang |
      | IT   |
      | EN   |
      | SL   |
      | DE   |
      | FR   |

  @userAttributes @addressBook4
  Scenario Outline: [USER-ATTR_12] inserimento email di cortesia e recupero header lang
    Given si predispone addressbook per l'utente "Mario Gherkin"
    Given vengono rimossi eventuali recapiti presenti per l'utente
    When viene richiesto l'inserimento del email di cortesia "test@gmail.com", e passo la lingua selezionata dal destinatario "<lang>"
    Then l'inserimento va a buon fine e NON ha prodotto un errore
    Examples:
      | lang |
      | IT   |
      | EN   |
      | SL   |
      | DE   |
      | FR   |

  @userAttributes @useB2B @addressBook3
  Scenario Outline: [USER-ATTR_10B] inserimento telefono e recupero header lang
    Given si predispone addressbook per l'utente "CucumberSpa"
    Given vengono rimossi eventuali recapiti presenti per l'utente
    When viene richiesto l'inserimento del numero di telefono "+393214210000", e passo la lingua selezionata dal destinatario "<lang>"
    Then l'inserimento va a buon fine e NON ha prodotto un errore
    Examples:
      | lang |
      | IT   |
      | EN   |
      | SL   |
      | DE   |
      | FR   |

  @userAttributes @useB2B @addressBook3
  Scenario Outline: [USER-ATTR_11B] inserimento pec e recupero header lang
    Given si predispone addressbook per l'utente "CucumberSpa"
    Given vengono rimossi eventuali recapiti presenti per l'utente
    When viene richiesto l'inserimento della pec "test@test.it", e passo la lingua selezionata dal destinatario "<lang>"
    Then l'inserimento va a buon fine e NON ha prodotto un errore
    Examples:
      | lang |
      | IT   |
      | EN   |
      | SL   |
      | DE   |
      | FR   |

  @userAttributes @useB2B @addressBook3
  Scenario Outline: [USER-ATTR_12B] inserimento email di cortesia e recupero header lang
    Given si predispone addressbook per l'utente "CucumberSpa"
    Given vengono rimossi eventuali recapiti presenti per l'utente
    When viene richiesto l'inserimento del email di cortesia "test@gmail.com", e passo la lingua selezionata dal destinatario "<lang>"
    Then l'inserimento va a buon fine e NON ha prodotto un errore
    Examples:
      | lang |
      | IT   |
      | EN   |
      | SL   |
      | DE   |
      | FR   |