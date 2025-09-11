@upload-csv
Feature: PARI - Portale registro dei beni

  Background:
    Given vengono generati tutti i token JWT necessari

    #[TC_13]
  Scenario: [TC_ACTION_ON_PRODUCT_1]
    Given viene usata l'utenza: PRODUTTORE_2
    Given viene verificata la presenza di un prodotto escluso, se non presente viene aggiunto
    Then viene aggiunto di nuovo un prodotto già rifiutato
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.header |

    #[TC_14]
  Scenario: [TC_ACTION_ON_PRODUCT_2]
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2310946          | AWM10014586GD       | AWM10014586GD          | Lavasciuga       | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Given viene usata l'utenza: INVITALIA_L1
    Then viene contrassegnato il prodotto appena aggiunto
    Then si verifica che la risposta abbia:
      | status      | OK |
    Given viene usata l'utenza: PRODUTTORE_2
    Then si verifica che il prodotto sia marcato come: "SUPERVISED"
    Then viene caricato di nuovo lo stesso prodotto
    Then si verifica che la risposta abbia:
      | status      | OK |
    Then si verifica che il prodotto sia marcato come: "SUPERVISED"






