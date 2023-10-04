Feature: Attributi utente

  Scenario: [B2B-PF-TOS_1] Viene recuperato il consenso TOS e verificato che sia accepted TOS_scenario positivo
    Given Viene richiesto l'ultimo consenso di tipo "TOS"
    Then Il recupero del consenso non ha prodotto errori
    And Il consenso è accettato

  @ignore
  Scenario: [USER-ATTR_2] inserimento pec errato
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento della pec "test@test@fail.@"
    Then l'inserimento ha prodotto un errore con status code "400"

  @ignore
  Scenario: [USER-ATTR_3] inserimento telefono errato
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del numero di telefono "+0013894516888"
    Then l'inserimento ha prodotto un errore con status code "400"


  Scenario: [USER-ATTR_4] inserimento pec non da errore
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento della pec "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{|}~-@gmail.com"

  Scenario Outline: [USER-ATTR_5] inserimento pec errato 250 caratteri
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento della pec "<pec>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | pec                                                                                                                                                                                                                                                                                  |
      | emailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibilecontienemolticarattericontienemolticarattericontienemolticarattericontienemolticarattericontienemolticaratteri@gmail.com |


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

  Scenario: [USER-ATTR_7] inserimento email di cortesia non da errore
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del email di cortesia "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!#$%&'+/=?^_`{|}~-@gmail.com"

  Scenario Outline: [USER-ATTR_8] inserimento email di cortesia errato 250 caratteri
    Given si predispone addressbook per l'utente "Mario Cucumber"
    When viene richiesto l'inserimento del email di cortesia "<email>"
    Then l'inserimento ha prodotto un errore con status code "400"
    Examples:
      | email                                                                                                                                                                                                                                                                                  |
      | emailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibileemailchecontienemolticaratterimetterneilpiupossibilecontienemolticarattericontienemolticarattericontienemolticarattericontienemolticarattericontienemolticaratteri@gmail.com |

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