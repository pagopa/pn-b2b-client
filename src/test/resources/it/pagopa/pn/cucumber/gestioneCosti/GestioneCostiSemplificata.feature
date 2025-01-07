Feature: Test sul processo di fruizione e aggiornamento costi gara

#  Test massivo con recupero dati da file csv contenuto nel path: src/main/resources/TEST_massivo_costi.csv
#  NOTA: Il csv da passare in input deve essere rinominato in: TEST_massivo_costi.csv
#  e il tenderID da usare nello step è l'id contenuto nel nome del csv originale
  @gestioneCostiSemplificata
  Scenario: [COST-ANALOGIC-SUCCESS-1] Viene effettuato un test massivo effettuando le chiamate con dati presi dal CSV
    Given vengono recuperati i valori delle richieste da file
    Then viene invocata l'api e si controlla che il risultato sia quello atteso per la gara con tenderId "20241206"

 @gestioneCostiSemplificata
  Scenario: [COST-ANALOGIC-FAILURE-1] Recupero dei costi associati ad uno specifico CAP con TenderId errato
    Given viene creata una richiesta con valori di default
    When viene chiamata l'api di calcolo costi con tenderId "aaaa-bbbb-cccc"
    Then l'api ritorna status code 404

 #   NOTA: Il tenderId corretto da utilizzare va recuperato nella tabella pn-PaperChannelTender
 @gestioneCostiSemplificata
  Scenario: [COST-ANALOGIC-FAILURE-2] Recupero dei costi associati ad uno specifico CAP con costo per il prodotto non presente tra i recapitisti e nel FSU
    Given viene creata una richiesta con i seguenti valori
      | product           | RIS   |
      | geokey            | 00102 |
      | numSides          | 3     |
      | isReversePrinter  | true  |
      | pageWeight        | 80    |
    When viene chiamata l'api di calcolo costi con tenderId "20241206"
    Then l'api ritorna status code 404

 @gestioneCostiSemplificata
  Scenario Outline: [COST-ANALOGIC-FAILURE-3] Recupero dei costi associati ad uno specifico CAP con un campo mandatory della Request body settato a  NULL
    Given viene creata una richiesta con i seguenti valori
      | product           | <PRODUCT>|
      | geokey            | <GEOKEY> |
      | numSides          | <NUMSIDES> |
      | isReversePrinter  | <ISREVERSEPRINTER>  |
    When viene chiamata l'api di calcolo costi con tenderId "20241206"
    Then l'api ritorna status code 400
    Examples:
      | PRODUCT    | GEOKEY     | NUMSIDES 	| ISREVERSEPRINTER    |
      | NULL       |   00102    | 5 	    | false               |
      | 890        |   NULL     | 5 	    | false               |
      | 890        |   00102    | NULL		| false               |
      | 890        |   00102    | 5 	    | NULL                |

