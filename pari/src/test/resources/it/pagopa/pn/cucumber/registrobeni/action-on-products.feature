@upload-csv
Feature: PARI - Portale registro dei beni

  Background:
    Given vengono generati tutti i token JWT necessari

  Scenario: [TC_ACTION_ON_PRODUCT]
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
    When viene caricato il csv con categoria: "COOKINGHOBS" e dati:
      | Codice GTIN/EAN     | Codice Prodotto        | Categoria           | Paese di Produzione       | Marca     | Modello    |
      | 8016361967658       | 33801999               | Piano cottura       | IT                        | Candy     | x100       |
    Then si verifica che la lista di prodotti caricati non sia nulla
    Then viene recuperata la lista prodotti di una specifica istituzione tra quelle recuperate precedentemente






