Feature: Adeguamento gestione costi analogici


  Scenario Outline: [Scenario-5] Recupero dei costi associati ad uno specifico CAP con TenderId errato
    Given viene creata una richiesta con valori di default
    When viene chiamata l'api di calcolo costi con tenderId tenderid "<TENDERID>"
    Then l'api ritorna status code 404

    Examples:
    |TENDERID |
    |56ed074c-13b6-4d61-ba49-221953e6b60f    |


  Scenario Outline: [Scenario-6] Recupero dei costi associati ad uno specifico Stato Estero per ogni prodotto disponibile
    Given viene creata una richiesta con i seguenti valori
      | product           | <PRODUCT>|
      | geokey            | <GEOKEY> |
      | numPages          | <NUMPAGES> |
      | isReversePrinter  | <ISREVERSEPRINTER>|
      | weight            | <WEIGHT>|
    When viene chiamata l'api di calcolo costi con tenderId tenderid "<TENDERID>"
    Then l'api ritorna status code 200

    Examples:
     |TENDERID     | PRODUCT   | GEOKEY    | NUMPAGES 	| ISREVERSEPRINTER 	    | WEIGHT |
     | NULL        | AR        |   100     | 3		    | true                  | 80   |





  Scenario Outline: [Scenario-4] Recupero dei costi associati ad uno specifico CAP con un campo mandatory della Request body settato a  NULL
    Given viene creata una specifica richiesta con product "<PRODUCT>", geokey "<GEOKEY>", numpages "<NUMPAGES>", isReversePrinter "<ISREVERSEPRINTER>", weight "<WEIGHT>"
    When viene chiamata l'api di calcolo costi con tenderId tenderid "<TENDERID>"
    Then l'api ritorna status code 400

    Examples:
      |TENDERID    | PRODUCT   | GEOKEY     | NUMPAGES 	| ISREVERSEPRINTER 	    | WEIGHT |
      | test       | NULL      |   100      | 50 	    | false                 | 255    |
      | test       | RS        |   NULL     | 50 	    | false                 | 255    |
      | test       | RS        |   100      | NULL		| false                 | 255    |
      | test       | RS        |   100      | 50 	    | NULL                  | 255    |
      | test       | RS        |   100      | 50 	    | false                 | NULL   |



  Scenario : [Scenario-3] Recupero dei costi associati ad ogni CAP per ogni prodotto disponibile e per ogni scaglione
    Given vengono recuperati i valori delle richieste da file
     # Given vengono create le richieste
     # When viene chiamata l'api di calcolo costi con tenderId tenderid "<TENDERID>"
    Then l'api ritorna status code 200