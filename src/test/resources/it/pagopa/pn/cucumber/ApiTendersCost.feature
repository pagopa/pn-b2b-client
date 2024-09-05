Feature: Adeguamento gestione costi analogici

  # @tendersCostMassive
  Scenario: [TENDER-COST-ANALOGIC-1] Viene effettuato un test massivo effettuando le chiamate con dati presi dal CSV
    Given vengono recuperati i valori delle richieste da file
    Then viene invocata l'api e si controlla che il risultato sia quello atteso

# @tendersCost
  Scenario: [Scenario-5] Recupero dei costi associati ad uno specifico CAP con TenderId errato
    Given viene creata una richiesta con valori di default
    When viene chiamata l'api di calcolo costi con tenderId "56ed074c-13b6-4d61-ba49-221953e6b60f"
    Then l'api ritorna status code 404

# @tendersCost
  Scenario: [Scenario-12] Recupero dei costi associati ad uno specifico cap con costo per il prodotto non presente tra i recapitisti e nel FSU
    Given viene creata una richiesta con i seguenti valori
      | product           | AR   |
      | geokey            | 100  |
      | numPages          | 3    |
      | isReversePrinter  | true |
      | pageWeight        | 80   |
    When viene chiamata l'api di calcolo costi con tenderId "NULL"
    Then l'api ritorna status code 404

# @tendersCost
  Scenario Outline: [Scenario-4] Recupero dei costi associati ad uno specifico CAP con un campo mandatory della Request body settato a  NULL
    Given viene creata una richiesta con i seguenti valori
      | product           | <PRODUCT>|
      | geokey            | <GEOKEY> |
      | numPages          | <NUMPAGES> |
      | isReversePrinter  | <ISREVERSEPRINTER>|
      | weight            | <PAGEWEIGHT>|
    When viene chiamata l'api di calcolo costi con tenderId "<TENDERID>"
    Then l'api ritorna status code 400
    Examples:
      |TENDERID    | PRODUCT   | GEOKEY     | NUMPAGES 	| ISREVERSEPRINTER 	    | PAGEWEIGHT |
      | test       | NULL      |   100      | 50 	    | false                 | 255    |
      | test       | RS        |   NULL     | 50 	    | false                 | 255    |
      | test       | RS        |   100      | NULL		| false                 | 255    |
      | test       | RS        |   100      | 50 	    | NULL                  | 255    |
      | test       | RS        |   100      | 50 	    | false                 | NULL   |

