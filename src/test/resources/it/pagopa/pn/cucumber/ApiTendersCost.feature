Feature: Adeguamento gestione costi analogici

#  Scenario 3
  Scenario: [TENDER-COST-ANALOGIC-1] Viene effettuato un test massivo effettuando le chiamate con dati presi dal CSV
    Given vengono recuperati i valori delle richieste da file
    Then viene invocata l'api e si controlla che il risultato sia quello atteso

#  Scenario 4
  Scenario Outline: [Scenario-4] Recupero dei costi associati ad uno specifico CAP con un campo mandatory della Request body settato a  NULL
    Given viene creata una richiesta con i seguenti valori
      | product           | <PRODUCT>|
      | geokey            | <GEOKEY> |
      | numPages          | <NUMPAGES> |
      | isReversePrinter  | <ISREVERSEPRINTER>|
      | weight            | <WEIGHT>|
    When viene chiamata l'api di calcolo costi con tenderId "<TENDERID>"
    Then l'api ritorna status code 400
    Examples:
      |TENDERID    | PRODUCT   | GEOKEY     | NUMPAGES 	| ISREVERSEPRINTER 	    | WEIGHT |
      | test       | NULL      |   100      | 50 	    | false                 | 255    |
      | test       | RS        |   NULL     | 50 	    | false                 | 255    |
      | test       | RS        |   100      | NULL		| false                 | 255    |
      | test       | RS        |   100      | 50 	    | NULL                  | 255    |
      | test       | RS        |   100      | 50 	    | false                 | NULL   |

  #  Scenario 5
  Scenario: [Scenario-5] Recupero dei costi associati ad uno specifico CAP con TenderId errato
    Given viene creata una richiesta con valori di default
    When viene chiamata l'api di calcolo costi con tenderId "56ed074c-13b6-4d61-ba49-221953e6b60f"
    Then l'api ritorna status code 404

  #  Scenario 6
  Scenario Outline: [Scenario-6] Recupero dei costi associati ad uno specifico Stato Estero per ogni prodotto disponibile
    Given viene creata una richiesta con i seguenti valori
      | product           | <PRODUCT>|
      | geokey            | <GEOKEY> |
      | numPages          | <NUMPAGES> |
      | isReversePrinter  | <ISREVERSEPRINTER>|
      | weight            | <WEIGHT>|
    When viene chiamata l'api di calcolo costi con tenderId "<TENDERID>"
    Then l'api ritorna status code 200
    Examples:
      |TENDERID     | PRODUCT   | GEOKEY    | NUMPAGES 	| ISREVERSEPRINTER 	    | WEIGHT |
      | NULL        | AR        |   100     | 3		    | true                  | 80   |



