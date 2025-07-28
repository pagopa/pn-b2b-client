@upload-csv
Feature: PARI - Portale registro dei beni

  Background:
    Given vengono generati tutti i token JWT necessari

#  Scenario Outline: Avvenuto accesso alla piattaforma Registro Beni con utenza Produttore in seguito all’accettazione dei ToS
#    Given l'utente è un "<ruolo>" di "<ente>"
#    Given l'utente "<action>" i TOS
#    Given l'operazione viene effettuata senza errori
#
#    Examples:
#      | ruolo        | ente        | action                |
#      | A            | B           | ACCETTA               |
#      | A            | B           | RIFIUTA               |

#  Scenario Outline: [TC_1] Avvenuto accesso alla piattaforma Registro Beni con utenza Produttore ed accettazione dei ToS con body errato
#    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS

#    Given l'utente "<action>" i TOS con request body errato
#    Given l'operazione produce errore
#    When viene caricato il csv con categoria: "<categoria>" e dati:
#      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto  | Categoria        | Paese di Produzione  |
#      | <cod_eprel>      | <cod_gtin>          | 666777666        | <categoria>      | IT                   |
#
#    Examples:
#    | cod_eprel   | cod_gtin    | categoria           |
#    | 2405439     | 68888       | Lavatrice           |
#    |             |             | WASHERDRIERS        |
#    |             |             | OVENS               |
#    |             |             | RANGEHOODS          |
#    |             |             | DISHWASHERS         |
#    |             |             | TUMBLEDRIERS        |
#    |             |             | REFRIGERATINGAPPL   |




  Scenario: [TC_2] Inserimento di un nuovo file CSV con category errata
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS

    When viene caricato il csv con categoria: "ERRATA" e dati:
      | Codice EPREL | Codice GTIN/EAN        | Codice Prodotto   | Categoria           | Paese di Produzione  |
      | 2226586      | eiQINTWM149V2          | EIQINTWM149       | Lavatrice           | IT                   |
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.category |

  Scenario: [TC-3] Inserimento di un nuovo file con estensione errata poiché diversa da csv
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS

    When viene caricato un file NON csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL | Codice GTIN/EAN        | Codice Prodotto   | Categoria           | Paese di Produzione  |
      | 2226586      | eiQINTWM149V2          | EIQINTWM149       | Lavatrice           | IT                   |
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.extension |


  Scenario Outline: [TC-4] Inserimento di un nuovo file CSV non valido con alcune colonne non popolate o popolate in modo non corretto
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS

    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL        | Codice GTIN/EAN        | Codice Prodotto         | Categoria           | Paese di Produzione       |
      | <codice_eprel>      | <codice_gtin>          | <codice_prodotto>       | <categoria>         | <paese>                   |
    Then si verifica che la risposta abbia:
      | status           | KO |
      | errorKey         | product.invalid.file.report |
      | productFileId    | NOT_NULL                    |
    Examples:
    | codice_eprel  | codice_gtin         | codice_prodotto   | categoria           | paese     |
    |               | eiQINTWM149V2       | EIQINTWM149       | Lavatrice           | IT        |
    | 2226586       |                     | EIQINTWM149       | Lavatrice           | IT        |
    | 2226586       | eiQINTWM149V2       | EIQINTWM149       |                     | IT        |
    | 2226586       | eiQINTWM149V2       | EIQINTWM149       | Lavatrice           |           |
    | AAABBBC       | eiQINTWM149V2       | EIQINTWM149       | Lavatrice     | IT                   |
    | 22265\|86     | eiQINTWM149V2       | EIQINTWM149       | Lavatrice     | IT                   |
    | 22265è86      | eiQINTWM149V2       | EIQINTWM149       | Lavatrice     | IT                   |
    | AAAB123       | eiQINTWM149V2       | EIQINTWM149       | Lavatrice     | IT                   |
    | 2226586      | 123456789123210      | EIQINTWM149       | Lavatrice     | IT                   |
    | 2226586      | aaaaaabbbbbbccc      | EIQINTWM149       | Lavatrice     | IT                   |
    | 2226586      | aaaaaabbbbbb12c      | EIQINTWM149       | Lavatrice     | IT                   |
    | 2226586      | aaaaaabbbbbb12c      | EIQINTWM149       | Lavatrice     | IT                   |
    | 2226586      | eiQè!TWM149V2        | EIQINTWM149       | Lavatrice     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINTè149       | Lavatrice     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINT+149       | Lavatrice     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINT!149       | Lavatrice     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINTà149       | Lavatrice     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINT()49       | Lavatrice     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINTWM14924253453453453121132334454545534532424234242EIQINTWM14924253453453453121132334454545534531       | Lavatrice     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINTWM149       | Lavatrice     | ITA                   |
    | 2226586      | eiQINTWM149V2          | EIQINTWM149       | Lavatrice     | I%                    |
    | 2226586      | eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM11          | EIQINTWM149       | Lavatrice     | IT                    |
    | 2226586      | eiQINTWM149V2          | EIQINTWM149       | LAV\|ATRIC&S     | IT                    |


# Possibile bug perché ritorna
#  413 Request Entity Too Large
#  <html>
#       <head><title>413 Request Entity Too Large</title></head>
#   <body>
#       <center><h1>413 Request Entity Too Large</h1></center>
#       <hr><center>nginx</center>
#   </body>
#  </html>
  Scenario: [TC-5] Inserimento di un nuovo file csv che supera il peso massimo (>2MB)
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS
    When viene caricato un file csv di peso maggiore a quello consentito
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.extension |

  Scenario: [TC-6] Inserimento di un nuovo file csv che supera il numero di righe massimo (> 100 righe)
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS
    When viene caricato un file csv contente più righe di quelle accettate
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.maxrow |

  Scenario Outline: [TC-7] Recupero lista dei caricamenti e prodotti precedentemente caricati
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS
    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto  | Categoria        | Paese di Produzione  |
      | 2226586          | eiQINTWM149V2       | <codice_prodotto>      | Lavatrice        | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    When vengono recuperati i prodotti precedentemente caricati
    Then si verifica che la lista di prodotti ritornata non sia nulla
    When si recupera la lista dei caricamenti effettuati dall'utenza
    Then si verifica che la lista dei caricamenti non sia nulla

  Examples:
      | codice_prodotto     |
      | EIQINTWM149         |
      |                     |

  Scenario: [TC-8] Inserimento di un nuovo file CSV non valido (Paese errato) e contestuale recupero del report
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS
    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL        | Codice GTIN/EAN        | Codice Prodotto         | Categoria           | Paese di Produzione  |
      | 2226586             | eiQINTWM149V2          | EIQINTWM149             | Lavatrice           | I&                   |
    Then si verifica che la risposta abbia:
      | status           | KO |
      | errorKey         | product.invalid.file.report |
      | productFileId    | NOT_NULL                    |
    # Recupero del report generato:
    When viene recuperato il report di errore appena generato
    Then il report è correttamente popolato

    Scenario Outline: [TC-9] Si tenta di recuperare un report di errore con id non presente o non valido
      Given viene usata l'utenza: "PRODUTTORE_1"
      When si tenta di recuperare un report di errore "<productFileId>" e si ottiene status code 404
      Examples:
        | productFileId |
        | NOT_VALID     |
        | NOT_PRESENT   |

  Scenario Outline: [TC-10] Inserimento di un nuovo file CSV per Piani Cottura non valido con alcune colonne non popolate o popolate in modo non corretto
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS

    When viene caricato il csv con categoria: "COOKINGHOBS" e dati:
      | Codice GTIN/EAN     | Codice Prodotto        | Categoria         | Paese di Produzione       | Marca     | Modello    |
      | <codice_gtin>       | <codice_prodotto>      | <categoria>       | <paese>                   | <marca>   | <modello>  |
    Then si verifica che la risposta abbia:
      | status           | KO |
      | errorKey         | product.invalid.file.report |
      | productFileId    | NOT_NULL                    |
    Examples:
       | codice_gtin         | codice_prodotto   | categoria           | paese     | marca    | modello |
       |                     | 33801999          | Piano cottura       | IT        | Candy    | x100    |
       | 8016361967656       | 33801999338019993380199933801999338019993380199933801999338019993380199933801999338019993380199933801999          | Piano cottura         | IT        | Candy    | x100    |
       | 8016361967656       | 33801999          |                     | IT        | Candy    | x100    |
       | 8016361967656       | 33801999          | Piano cottura       |           | Candy    | x100    |
       | 8016361967656       | 33801999          | Piano cottura       | IT        |          | x100    |
       | 8016361967656       | 33801999          | Piano cottura       | IT        | Candy    |         |
       | 801636196765655     | 33801999          | Piano cottura       | IT        | Candy    | x100    |
       | 8016361967656       | 33801999          | Lavatrice           | IT        | Candy    | x100    |
       | 8016361967656       | 33801999          | Piano cottura       | IT        | CandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandyCandy    | x100    |
       | 8016361967656       | 33801999          | Piano cottura       | IT        | Candy    | x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x100x1000    |
       | 8016361967656       | 33801999          | Piano cottura       | ITA       | Candy    | x100   |
       | 8016361967656       | 33801999          | Piano cottura       | I&        | Candy    | x100    |
       | 8016361967656       | 3380%999          | Piano cottura       | IT        | Candy    | x100    |
       | 8016361967656       | 33801/99          | Piano cottura       | IT        | Candy    | x100    |
       | 8016361967656       | 33801,99          | Piano cottura       | IT        | Candy    | x100    |
       | 8016361967656       | 33801^99          | Piano cottura       | IT        | Candy    | x100    |
       | 8016361967656       | 33801è99          | Piano cottura       | IT        | Candy    | x100    |
       | 8016361967656       | 33801199          | COOKINGHOBS         | IT        | Candy    | x100    |

  Scenario Outline: [TC-11] Inserimento di un nuovo file CSV valido per Piani Cottura
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS

    When viene caricato il csv con categoria: "COOKINGHOBS" e dati:
      | Codice GTIN/EAN     | Codice Prodotto        | Categoria         | Paese di Produzione       | Marca     | Modello    |
      | <codice_gtin>       | <codice_prodotto>      | <categoria>       | <paese>                   | <marca>   | <modello>  |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Examples:
      | codice_gtin         | codice_prodotto   | categoria           | paese     | marca    | modello |
      | 8016361967658       | 33801999          | Piano cottura       | IT        | Candy    | x100    |
      | 8016361967659       |                   | Piano cottura       | IT        | Candy    | x100    |


  Scenario Outline: [TC-12] Verifica di un nuovo file CSV valido per Cappe da cucina
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS

    When viene verificato il csv con categoria: "RANGEHOODS" e dati:
      | Codice EPREL        | Codice GTIN/EAN        | Codice Prodotto         | Categoria           | Paese di Produzione       |
      | <codice_eprel>      | <codice_gtin>          | <codice_prodotto>       | <categoria>         | <paese>                   |
    Then si verifica che la risposta abbia:
      | status           | KO |
      | errorKey         | product.invalid.file.report |
      | productFileId    | NOT_NULL                    |
    Examples:
      | codice_eprel  | codice_gtin         | codice_prodotto   | categoria           | paese     |
      |                | eiQINTWM149V2      | HWF90Elica       | Cappa da cucina     | IT        |
      | 1059484       |                     | HWF90Elica       | Cappa da cucina     | IT        |
      | 1059484       | eiQINTWM149V2       | HWF90Elica       | Cappa da cucina     |           |

      | AAABBBC       | eiQINTWM149V2       | HWF90Elica       | Cappa da cucina     | IT        |
      | 22265\|86     | eiQINTWM149V2       | HWF90Elica       | Cappa da cucina     | IT        |
      | 22265è86      | eiQINTWM149V2       | HWF90Elica       | Cappa da cucina     | IT        |
      | AAAB123       | eiQINTWM149V2       | HWF90Elica       | Cappa da cucina     | IT        |
      | 1059484       | 123456789123234     | HWF90Elica       | Cappa da cucina     | IT        |
      | 1059484       | aaaaaabbbbbbccc     | HWF90Elica       | Cappa da cucina     | IT        |
      | 1059484       | aaaaaabbbbbb12c     | HWF90Elica       | Cappa da cucina     | IT        |
      | 1059484       | aaaaaabbbbbb12c     | HWF90Elica       | Cappa da cucina     | IT        |
      | 1059484       | eiQè!TWM149V2       | HWF90Elica       | Cappa da cucina     | IT        |

      | 2226586       | eiQINTWM149V2       | HWF90ElicaHWF90ElicaHWF90ElicaHWF90ElicaHWF90ElicaHWF90ElicaHWF90ElicaHWF90ElicaHWF90ElicaHWF90ElicaHWF90Elica       | Cappa da cucina     | IT        |
      | 2226586       | eiQINTWM149V2       | HWF90Elica       | Cappa da cucina     | ITA     |
      | 2226586       | eiQINTWM149V2       | HWF90Elica       | Cappa da cucina     | I%      |
      | 2226586       | eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM149V2eiQINTWM11          | EIQINTWM149       | Cappa da cucina     | IT          |
      | 2226586       | eiQINTWM149V2       | HWF90Elica       | Cappa\|Cucina&S     | IT      |






