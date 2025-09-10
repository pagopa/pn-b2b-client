@upload-csv
Feature: PARI - Portale registro dei beni

  Background:
    Given vengono generati tutti i token JWT necessari

  Scenario: [TC_ACTION_ON_PRODUCT_1]
    Given viene usata l'utenza: PRODUTTORE_2
    Given viene verificata la presenza di un prodotto escluso, se non presente viene aggiunto
    Then viene aggiunto di nuovo un prodotto già rifiutato
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.header |

  Scenario: [TC_ACTION_ON_PRODUCT_2]
    Given viene usata l'utenza: PRODUTTORE_2
    Given viene verificata la presenza di un prodotto escluso, se non presente viene aggiunto
    Then viene aggiunto di nuovo un prodotto già rifiutato
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.header |






