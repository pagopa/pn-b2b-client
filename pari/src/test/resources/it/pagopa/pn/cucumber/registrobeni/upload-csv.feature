@upload-csv
Feature: PARI - Portale registro dei beni

  Background:
    Given vengono generati tutti i token JWT necessari

  @produttore1 @invitalia1 @ignore
  Scenario Outline: [TC_1_TOS_OK] Avvenuto accesso alla piattaforma Registro Beni con utenza Produttore in seguito all’accettazione dei ToS
    Given viene rimossa l'accettazione dei ToS per l'utente: <utenza>
    Given viene usata l'utenza: <utenza>
    Then si verifica che i ToS NON_SONO stati accettati
    When l'utente accetta i ToS con successo
    Then si verifica che i ToS SONO stati accettati
    Examples:
      | utenza          |
      | PRODUTTORE_1    |
      | INVITALIA_L1    |

  @produttore1 @ignore
  Scenario: [TC_1_TOS_KO] La sottomissione di un csv prodotti senza l'accettazione dei ToS deve essere proibita
    Given viene usata l'utenza: PRODUTTORE_1
    Given viene rimossa l'accettazione dei ToS per l'utente: PRODUTTORE_1
    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL | Codice GTIN/EAN        | Codice Prodotto   | Categoria           | Paese di Produzione  |
      | 2226586      | eiQINTWM149V2          | EIQINTWM149       | Lavatrice           | IT                   |
    Then si verifica che la risposta abbia:
      | status      | KO |

  @produttore1
  Scenario: [TC_UPLOAD_2] Inserimento di un nuovo file CSV con category errata
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
    When viene caricato il csv con categoria: "ERRATA" e dati:
      | Codice EPREL | Codice GTIN/EAN        | Codice Prodotto   | Categoria           | Paese di Produzione  |
      | 2226586      | eiQINTWM149V2          | EIQINTWM149       | Lavatrice           | IT                   |
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.category |

  @produttore1
  Scenario: [TC_UPLOAD_3] Inserimento di un nuovo file con estensione errata poiché diversa da csv
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
    When viene caricato un file NON csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL | Codice GTIN/EAN        | Codice Prodotto   | Categoria           | Paese di Produzione  |
      | 2226586      | eiQINTWM149V2          | EIQINTWM149       | Lavatrice           | IT                   |
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.extension |

  @produttore1
  Scenario Outline: [TC_UPLOAD_4] Inserimento di un nuovo file CSV non valido con alcune colonne non popolate o popolate in modo non corretto
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
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

  @produttore1
  Scenario: [TC_UPLOAD_5] Inserimento di un nuovo file csv che supera il peso massimo (>2MB)
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
    When viene caricato un file csv di peso maggiore a quello consentito
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.maxsize |

  @produttore1
  Scenario: [TC_UPLOAD_6] Inserimento di un nuovo file csv che supera il numero di righe massimo (> 100 righe)
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
    When viene caricato un file csv contente più righe di quelle accettate
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.maxrow |

  @produttore1
  Scenario Outline: [TC_UPLOAD_7] Recupero lista dei caricamenti e prodotti precedentemente caricati
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
    When viene caricato il csv con categoria: "<categoria_csv>" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2310946          | AWM10014586GD       | <codice_prodotto>      | <categoria>      | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Then si verifica che la lista di prodotti caricati non sia nulla
    Then si verifica che nella lista dei caricamenti ne sia stato aggiunto uno nuovo
  Examples:
      | categoria_csv       | codice_prodotto     | categoria         |
      | WASHERDRIERS        | AWM10014586GD       | Lavasciuga        |
      | OVENS               |                     | Forno             |
      | RANGEHOODS          |                     | Cappa da cucina   |
      | DISHWASHERS         |                     | Lavastoviglie     |
      | TUMBLEDRYERS        |                     | Asciugatrice      |
      | REFRIGERATINGAPPL   |                     | Apparecchio di refrigerazione       |

  @produttore1
  Scenario: [TC_UPLOAD_8] Inserimento di un nuovo file CSV non valido (Paese errato) e contestuale recupero del report
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
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

  @produttore1
  Scenario Outline: [TC_UPLOAD_9] Si tenta di recuperare un report di errore con id non presente o non valido
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
    When si tenta di recuperare un report di errore "<productFileId>" e si ottiene status code <statusCode>
    Examples:
      | productFileId              | statusCode  |
      #NON PRESENTE
      | 5f2b9c8a4d3e1f6b7a9d2c4e   | 404         |
      #NON VALIDO
      | invalid_product_file       | 500         |

  @produttore1
  Scenario Outline: [TC_UPLOAD_10] Inserimento di un nuovo file CSV per Piani Cottura non valido con alcune colonne non popolate o popolate in modo non corretto
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
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

  @produttore1
  Scenario Outline: [TC_UPLOAD_11] Inserimento di un nuovo file CSV valido per Piani Cottura
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
    When viene caricato il csv con categoria: "COOKINGHOBS" e dati:
      | Codice GTIN/EAN     | Codice Prodotto        | Categoria         | Paese di Produzione       | Marca     | Modello    |
      | <codice_gtin>       | <codice_prodotto>      | <categoria>       | <paese>                   | <marca>   | <modello>  |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Examples:
      | codice_gtin         | codice_prodotto   | categoria           | paese     | marca    | modello |
      | 8016361967658       | 33801999          | Piano cottura       | IT        | Candy    | x100    |
      | 8016361967659       |                   | Piano cottura       | IT        | Candy    | x100    |

  @produttore1
  Scenario Outline: [TC_UPLOAD_12] Verifica di un nuovo file CSV valido per Cappe da cucina
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
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

  @produttore1
  Scenario Outline: [TC_UPLOAD_13] Inserimento di un nuovo file CSV con intestazione colonne errate
    Given viene usata l'utenza: PRODUTTORE_1
    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | <header_1>   | <header_2>             | <header_3>        | <header_4>          | <header_5>           |
      | 2226586      | eiQINTWM149V2          | EIQINTWM149       | Lavatrice           | IT                   |
    Then si verifica che la risposta abbia:
      | status      | KO |
      | errorKey    | product.invalid.file.header |
    Examples:
      | header_1        | header_2               | header_3           | header_4            | header_5             |
      | codice Eprel    | Codice GTIN/EAN        | Codice Prodotto    | Categoria           | Paese di Produzione  |
      | codiceEprel     | Codice GTIN/EAN        | Codice Prodotto    | Categoria           | Paese di Produzione  |
      |                 | Codice GTIN/EAN        | Codice Prodotto    | Categoria           | Paese di Produzione  |
      | Codice EPREL    | CodiceGTIN/EAN         | Codice Prodotto    | Categoria           | Paese di Produzione  |
      | Codice EPREL    |                        | Codice Prodotto    | Categoria           | Paese di Produzione  |
      | Codice EPREL    | Codice GTIN/EAN        | CodiceProdotto     | Categoria           | Paese di Produzione  |
      | Codice EPREL    | Codice GTIN/EAN        |                    | Categoria           | Paese di Produzione  |
      | Codice EPREL    | Codice GTIN/EAN        | Codice Prodotto    | Cate\goria          | Paese di Produzione  |
      | Codice EPREL    | Codice GTIN/EAN        | Codice Prodotto    |                     | Paese di Produzione  |
      | Codice EPREL    | Codice GTIN/EAN        | Codice Prodotto    | Categoria           | Paese_di_Produzione  |
      | Codice EPREL    | Codice GTIN/EAN        | Codice Prodotto    | Categoria           | PaesediProduzione    |
      | Codice EPREL    | Codice GTIN/EAN        | Codice Prodotto    | Categoria           |                      |

  @produttore1 @produttore2
  Scenario: [TC_UPLOAD_14] Un secondo Produttore prova a caricare dei prodotti che sono stati caricati precedentemente da un altro Produttore e riceve errore
    Given viene usata l'utenza: PRODUTTORE_1
    Given l'utente accetta i ToS con successo
    When viene caricato il csv con categoria: "COOKINGHOBS" e dati:
      | Codice GTIN/EAN     | Codice Prodotto        | Categoria           | Paese di Produzione       | Marca     | Modello    |
      | 8016361967658       | 33801999               | Piano cottura       | IT                        | Candy     | x100       |
    Given viene usata l'utenza: PRODUTTORE_2
    Given l'utente accetta i ToS con successo
    When viene caricato il csv con categoria: "COOKINGHOBS" e dati:
      | Codice GTIN/EAN     | Codice Prodotto        | Categoria           | Paese di Produzione       | Marca     | Modello    |
      | 8016361967658       | 33801999               | Piano cottura       | IT                        | Candy     | x100       |
    When si recupera l'ultimo caricamento effettuato dall'utenza
    Then si verifica che i prodotti non siano stati aggiunti in quanto già caricati da un produttore diverso



