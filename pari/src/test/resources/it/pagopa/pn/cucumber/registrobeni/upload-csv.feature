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

  Scenario Outline: Avvenuto accesso alla piattaforma Registro Beni con utenza Produttore ed accettazione dei ToS con body errato
    Given viene usata l'utenza: "PRODUTTORE_1"
    Given l'utente "ACCETTA" i TOS

#    Given l'utente "<action>" i TOS con request body errato
#    Given l'operazione produce errore
    When viene caricato il csv con categoria: "<categoria>" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto  | Categoria        | Paese di Produzione  |
      | <cod_eprel>      | <cod_gtin>          | 666777666        | <categoria>      | IT                   |

    Examples:
    | cod_eprel   | cod_gtin    | categoria           |
    | 2405439     | 68888       | WASHINGMACHINES     |
    |             |             | WASHERDRIERS        |
    |             |             | OVENS               |
    |             |             | RANGEHOODS          |
    |             |             | DISHWASHERS         |
    |             |             | TUMBLEDRIERS        |
    |             |             | REFRIGERATINGAPPL   |




  Scenario: Inserimento di un nuovo file CSV con category errata
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS

    When viene caricato il csv con categoria: "ERRATA" e dati:
      | Codice EPREL | Codice GTIN/EAN        | Codice Prodotto   | Categoria           | Paese di Produzione  |
      | 2226586      | eiQINTWM149V2          | EIQINTWM149       | WASHINGMACHINES     | IT                   |
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.category |

  Scenario: Inserimento di un nuovo file con estensione errata poiché diversa da csv
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS

    When viene caricato un file NON csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL | Codice GTIN/EAN        | Codice Prodotto   | Categoria           | Paese di Produzione  |
      | 2226586      | eiQINTWM149V2          | EIQINTWM149       | WASHINGMACHINES     | IT                   |
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.extension |


  Scenario Outline: Inserimento di un nuovo file CSV non valido con alcune colonne non popolate o popolate in modo non corretto
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
    |               | eiQINTWM149V2       | EIQINTWM149       | WASHINGMACHINES     | IT        |
    | 2226586       |                     | EIQINTWM149       | WASHINGMACHINES     | IT        |
    #da rivedere il caso sotto perché la risposta va stranamente in 200 (possibile bug) --------- GIUSTO COSì non obbligatorio
    | 2226586       | eiQINTWM149V2       |                   | WASHINGMACHINES     | IT        |
    | 2226586       | eiQINTWM149V2       | EIQINTWM149       |                     | IT        |
    | 2226586       | eiQINTWM149V2       | EIQINTWM149       | WASHINGMACHINES     |           |

    | AAABBBC       | eiQINTWM149V2       | EIQINTWM149       | WASHINGMACHINES     | IT                   |
    | 22265\|86     | eiQINTWM149V2       | EIQINTWM149       | WASHINGMACHINES     | IT                   |
    | 22265è86      | eiQINTWM149V2       | EIQINTWM149       | WASHINGMACHINES     | IT                   |
    | AAAB123       | eiQINTWM149V2       | EIQINTWM149       | WASHINGMACHINES     | IT                   |

    | 2226586      | 1234567891232        | EIQINTWM149       | WASHINGMACHINES     | IT                   |
    | 2226586      | aaaaaabbbbbbcc       | EIQINTWM149       | WASHINGMACHINES     | IT                   |
    | 2226586      | aaaaaabbbbbb12c      | EIQINTWM149       | WASHINGMACHINES     | IT                   |
    | 2226586      | aaaaaabbbbbb12c      | EIQINTWM149       | WASHINGMACHINES     | IT                   |
    | 2226586      | eiQè!TWM149V2        | EIQINTWM149       | WASHINGMACHINES     | IT                   |



    | 2226586      | eiQINTWM149V2          | EIQINTè149       | WASHINGMACHINES     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINT+149       | WASHINGMACHINES     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINT!149       | WASHINGMACHINES     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINTà149       | WASHINGMACHINES     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINT()49       | WASHINGMACHINES     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINTWM14924253453453453121132334454545534532424234242EIQINTWM14924253453453453121132334454545534531       | WASHINGMACHINES     | IT                   |
    | 2226586      | eiQINTWM149V2          | EIQINTWM149       | WASHINGMACHINES     | ITA                   |
    | 2226586      | eiQINTWM149V2          | EIQINTWM149       | WASHINGMACHINES     | I%                    |

# Possibile bug perché ritorna
#  413 Request Entity Too Large
#  <html>
#       <head><title>413 Request Entity Too Large</title></head>
#   <body>
#       <center><h1>413 Request Entity Too Large</h1></center>
#       <hr><center>nginx</center>
#   </body>
#  </html>
  Scenario: Inserimento di un nuovo file csv che supera il peso massimo (>2MB)
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS
    When viene caricato un file csv di peso maggiore a quello consentito
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.extension |

  Scenario: Inserimento di un nuovo file csv che supera il numero di righe massimo (> 100 righe)
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS
    When viene caricato un file csv contente più righe di quelle accettate
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.maxrow |

  Scenario: Recupero lista dei caricamenti e prodotti precedentemente caricati
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS
    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto  | Categoria        | Paese di Produzione  |
      | 2226586          | eiQINTWM149V2       | EIQINTWM149      | WASHINGMACHINES  | IT                   |
    When vengono recuperati i prodotti precedentemente caricati
    Then si verifica che la lista di prodotti ritornata non sia nulla
    When si recupera la lista dei caricamenti effettuati dall'utenza
    Then si verifica che la lista dei caricamenti non sia nulla

  Scenario: Inserimento di un nuovo file CSV non valido (Paese errato) e contestuale recupero del report
    Given viene usata l'utenza: "PRODUTTORE_1"
#    Given l'utente "ACCETTA" i TOS
    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL        | Codice GTIN/EAN        | Codice Prodotto         | Categoria           | Paese di Produzione  |
      | 2226586             | eiQINTWM149V2          | EIQINTWM149             | WASHINGMACHINES     | I&                   |
    Then si verifica che la risposta abbia:
      | status           | KO |
      | errorKey         | product.invalid.file.report |
      | productFileId    | NOT_NULL                    |
    # Recupero del report generato:
    When viene recuperato il report di errore appena generato
    Then il report è correttamente popolato

    Scenario Outline: Si tenta di recuperare un report di errore con id non presente o non valido
      Given viene usata l'utenza: "PRODUTTORE_1"
      When si tenta di recuperare un report di errore "<productFileId>" e si ottiene status code 404
      Examples:
        | productFileId |
        | NOT_VALID     |
        | NOT_PRESENT   |



