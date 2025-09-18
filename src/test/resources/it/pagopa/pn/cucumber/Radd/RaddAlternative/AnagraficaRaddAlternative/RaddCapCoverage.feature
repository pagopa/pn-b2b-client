Feature: Radd Alternative Anagrafica Aggiornata Sportelli V2



  # Solo Lettura DEV -> username: "DEV-LevelTwo@test.com" , password: "DEV-LevelTwo-1234!"

 # DEV -> username: "test@test.com" , password: "Test_Cognito_1.!" , clientId/poolId: "77j22r1r812dt3vo8d4s985ap4"
 #     -> xPagopaPnCxId/parentId: "P12345"(per ora qualsiasi)

 # TEST -> username: "admin@test.pagopa.it" , password: "Admin-testcognito1" , clientId/poolId: "s8tm86rmpfgiccdo5rogmeod7"
 #     -> xPagopaPnCxId/parentId: ""

 # UAT -> username: "admin@uat.pagopa.it" , password: "Admin-uatcognito1" , clientId/poolId: "29anv4akm6uur60810ge0enn47"
 #     -> xPagopaPnCxId/parentId: ""

 # HOTFIX -> username: "admin@hotfix.pagopa.it" , password: "Admin-hotfixcognito1" , clientId/poolId: ""
 #     -> xPagopaPnCxId/parentId: ""

#todo t cap


  Scenario: [RADD_ANAGRAFICA] Testing Autenticazione con parametri
    Given l' utente con username "test@test.com" password "Test_Cognito_1.!" e clientId "77j22r1r812dt3vo8d4s985ap4" richiede e riceve un token valido tramite cognito


  #  *** INSERIMENTO ***

  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 1
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_1] Creazione nuova sede RADD con dati corretti
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma    |
      | address_radd_cap      | 80133       |
      | address_radd_province | NA          |
      | address_radd_city     | NAPOLI      |
      | radd_description      | descrizione |
      | radd_phoneNumbers     | 3201234567  |
      | radd_externalCodes    | EXT01QA     |
    Then la response V2 a seguito del nuovo inserimento deve contenere i valori attesi
      | addressRow         | Via Roma, 80133 Napoli NA, Italia |
      | cap                | 80133                             |
      | province           | NA                                |
      | city               | Napoli                            |
      | description        | descrizione                       |
      | phoneNumbers       | 3201234567                        |
      | externalCodes      | EXT01QA                           |
      | address_addressRow | via roma                          |
      | address_cap        | 80133                             |
      | address_province   | NA                                |
      | address_city       | NAPOLI                            |
    Then viene verificato che l' ultimo sportello inserito venga restituito nella lista tramite locationId


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 1
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_22] Creazione nuova sede RADD con numeri di telefono accettati
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma    |
      | address_radd_cap      | 80133       |
      | address_radd_province | NA          |
      | address_radd_city     | NAPOLI      |
      | radd_description      | descrizione |
      | radd_phoneNumbers     | <telefono>  |
      | radd_externalCodes    | EXT01QA     |
    Examples:
      | telefono       |
      | 8001234567     |
      | +39800123456   |
      | +390212345678  |
      | +3933312345678 |

  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs nd
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_26] Creazione nuova sede RADD con descrizione maggiore di 200 caratteri
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | <descrizione> |
      | radd_phoneNumbers     | 8001234567    |
      | radd_externalCodes    | EXT01QA       |
    Then l'operazione Radd V2 ha prodotto un errore con status code "400"
    Examples:
      | descrizione                                                                      |
      | "Ogni giorno porta con sé nuove opportunità, anche se spesso si nascondono dietro piccole sfide. Con pazienza e fiducia, ogni passo diventa crescita, e il percorso si riempie di significato autentico." |


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 2
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_2] Creazione nuova sede RADD con intero campo address mancante
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd       | NULL        |
      | radd_description   | descrizione |
      | radd_phoneNumbers  | 33312345678 |
      | radd_externalCodes | EXT02QA     |
    Then l'operazione Radd V2 ha prodotto un errore con status code "400"


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_21] Creazione nuova sede RADD con partenr id non valido
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    Then viene impostato un partenr Id non valido
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | via roma    |
      | address_radd_cap      | 80133       |
      | address_radd_province | NA          |
      | address_radd_city     | NAPOLI      |
      | radd_description      | descrizione |
      | radd_phoneNumbers     | 0812345678  |
      | radd_externalCodes    | EXT11QA     |
    Then l'operazione Radd V2 ha prodotto un errore con status code "400"


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 3-4-5-6-7-8-9
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_3] Creazione nuova sede RADD senza campi obbligatori
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | <via>           |
      | address_radd_cap      | <cap>           |
      | address_radd_province | <provincia>     |
      | address_radd_city     | <citta>         |
      | radd_description      | <descrizione>   |
      | radd_phoneNumbers     | <telefono>      |
      | radd_externalCodes    | <externalCodes> |
    Then l'operazione Radd V2 ha prodotto un errore con status code "400"
    Examples:
      | via      | cap   | provincia | citta  | descrizione | telefono   | externalCodes |
      | NULL     | 80133 | NA        | NAPOLI | descrizione | 3201234567 | EXT03QA       |
      | via roma | NULL  | NA        | NAPOLI | descrizione | 3201234567 | EXT03QA       |
      | via roma | 80133 | NULL      | NAPOLI | descrizione | 3201234567 | EXT03QA       |
      | via roma | 80133 | NA        | NULL   | descrizione | 3201234567 | EXT03QA       |
      | via roma | 80133 | NA        | NAPOLI | NULL        | 3201234567 | EXT03QA       |
      | via roma | 80133 | NA        | NAPOLI | descrizione | NULL       | EXT03QA       |
      | via roma | 80133 | NA        | NAPOLI | descrizione | 3201234567 | NULL          |


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs dal 11 al 28 + dal 30 al 36
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_4] Creazione nuova sede con campi non validi
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | <via>               |
      | address_radd_cap      | <cap>               |
      | address_radd_province | <provincia>         |
      | address_radd_city     | <citta>             |
      | radd_description      | <descrizione>       |
      | radd_phoneNumbers     | <telefono>          |
      | radd_externalCodes    | <externalCode>      |
      | address_radd_country  | <stato>             |
      | radd_openingTime      | <aperturaSportello> |
      | radd_start_validity   | <startValidity>     |
      | radd_end_validity     | <endValidity>       |
      | radd_email            | <email>             |
      | radd_website          | <website>           |
      | radd_partner_type     | <partnertype>       |
    Then l'operazione Radd V2 ha prodotto un errore con status code "400"
    Examples:
      | via         | cap    | provincia | citta  | stato  | descrizione | telefono       | aperturaSportello | startValidity | endValidity | externalCode | email                | website      | partnertype |
      | via fiume2  | 123456 | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA2     | NULL                 | NULL         | NULL        |
      | via fiume3  | 8014   | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA3     | NULL                 | NULL         | NULL        |
      | via fiume4  | 2016Z  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA4     | NULL                 | NULL         | NULL        |
      | via fiume5  | 20161  | ITA       | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA5     | NULL                 | NULL         | NULL        |
      | via fiume6  | 20161  | na        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA6     | NULL                 | NULL         | NULL        |
      | via fiume7  | 20161  | NAP       | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA7     | NULL                 | NULL         | NULL        |
      | via fiume8  | 20161  | 10        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA8     | NULL                 | NULL         | NULL        |
      | via fiume9  | 20161  | MI        | MILANO | ITALIA | Test        | ++99858425136  | NULL              | NULL          | NULL        | EXT04QA9     | NULL                 | NULL         | NULL        |
      | via fiume10 | 20161  | MI        | MILANO | ITALIA | Test        | 3998-842-136   | NULL              | NULL          | NULL        | EXT04QA10    | NULL                 | NULL         | NULL        |
      | via fiume11 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA11    | https://exa_mple.com | NULL         | NULL        |
      | via fiume12 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA12    | nome@dominio         | NULL         | NULL        |
      | via fiume13 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA13    | @dominio.ext         | NULL         | NULL        |
      | via fiume17 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | 01-01-2030    | NULL        | EXT04QA17    | NULL                 | NULL         | NULL        |
      | via fiume18 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | 202-01-01     | NULL        | EXT04QA18    | NULL                 | NULL         | NULL        |
      | via fiume19 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | 01-01-2030  | EXT04QA19    | NULL                 | NULL         | NULL        |
      | via fiume20 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | 2015-01-01  | EXT04QA20    | NULL                 | NULL         | NULL        |
      | via fiume24 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | 2030-01-01    | 2029-01-01  | EXT04QA14    | NULL                 | NULL         | NULL        |
      | via fiume22 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA21    | NULL                 | https://.com | NULL        |
      | via fiume23 | 20161  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA22    | NULL                 | NULL         | ĄŁĽ         |
      | via fiume26 | 201 1  | MI        | MILANO | ITALIA | Test        | 3201234567     | NULL              | NULL          | NULL        | EXT04QA23    | NULL                 | NULL         | NULL        |
      | via fiume27 | 20161  | MI        | MILANO | ITALIA | Test        | +39401234567   | NULL              | NULL          | NULL        | EXT04QA24    | NULL                 | NULL         | NULL        |
      | via fiume28 | 20161  | MI        | MILANO | ITALIA | Test        | 123456789      | NULL              | NULL          | NULL        | EXT04QA25    | NULL                 | NULL         | NULL        |
      | via fiume29 | 20161  | MI        | MILANO | ITALIA | Test        | 00112345678    | NULL              | NULL          | NULL        | EXT04QA26    | NULL                 | NULL         | NULL        |
      | via fiume30 | 20161  | MI        | MILANO | ITALIA | Test        | 00401234567    | NULL              | NULL          | NULL        | EXT04QA27    | NULL                 | NULL         | NULL        |
      | via fiume32 | 20161  | MI        | MILANO | ITALIA | Test        | 1234567890     | NULL              | NULL          | NULL        | EXT04QA29    | NULL                 | NULL         | NULL        |
      | via fiume34 | 20161  | MI        | MILANO | ITALIA | Test        | 333123456      | NULL              | NULL          | NULL        | EXT04QA31    | NULL                 | NULL         | NULL        |
      | via fiume31 | 20161  | MI        | MILANO | ITALIA | Test        | 3331234567890  | NULL              | NULL          | NULL        | EXT04QA32    | NULL                 | NULL         | NULL        |
      | via fiume35 | 20161  | MI        | MILANO | ITALIA | Test        | 999333123456   | NULL              | NULL          | NULL        | EXT04QA33    | NULL                 | NULL         | NULL        |
      | via fiume36 | 20161  | MI        | MILANO | ITALIA | Test        | 00390012345678 | NULL              | NULL          | NULL        | EXT04QA34    | NULL                 | NULL         | NULL        |
      | via fiume37 | 20161  | MI        | MILANO | ITALIA | Test        | +393           | NULL              | NULL          | NULL        | EXT04QA35    | NULL                 | NULL         | NULL        |


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 29
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_5] Creazione nuova sede RADD con campo stratValidity vuoto e restituzione campo formattato correttamente
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma       |
      | address_radd_cap      | 80133          |
      | address_radd_province | NA             |
      | address_radd_city     | NAPOLI         |
      | radd_description      | descrizione    |
      | radd_phoneNumbers     | +3933312345678 |
      | radd_externalCodes    | EXT05QA        |
      | radd_start_validity   | NULL           |
    Then la response V2 deve aver restiutito in automatico startValidity odierno in formato yyyy-MM-dd


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 37
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_6] Creazione nuova sede RADD con utente non abilitato alla scrittura
    Given Effettuo l'autenticazione per l' utente con permessi: "SOLO_LETTURA"
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | via roma       |
      | address_radd_cap      | 80133          |
      | address_radd_province | NA             |
      | address_radd_city     | NAPOLI         |
      | radd_description      | descrizione    |
      | radd_phoneNumbers     | +3933312345678 |
      | radd_externalCodes    | EXT06QA        |
    Then l'operazione ha prodotto un errore con status code "403"


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 40
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_7] Creazione nuova sede RADD con dati corretti e controllo response
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row          | via roma                |
      | address_radd_cap          | 80133                   |
      | address_radd_province     | NA                      |
      | address_radd_city         | NAPOLI                  |
      | radd_description          | descrizione             |
      | radd_phoneNumbers         | +3933312345678          |
      | radd_externalCodes        | EXT07QA                 |
      | address_radd_country      | ITALIA                  |
      | radd_openingTime          | mon=9:00-10:00#         |
      | radd_start_validity       | now                     |
      | radd_end_validity         | +10g                    |
      | radd_email                | test@example.com        |
      | radd_website              | https://www.example.com |
      | radd_partner_type         | CAF                     |
      | radd_appointment_required | true                    |
    And la response registry V2 deve avere i campi "tutti" valorizzati


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 69
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_8] Creazione nuova sede RADD con ExternalCode già esistente
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma       |
      | address_radd_cap      | 80133          |
      | address_radd_province | NA             |
      | address_radd_city     | NAPOLI         |
      | radd_description      | descrizione    |
      | radd_phoneNumbers     | +3933312345678 |
      | radd_externalCodes    | EXT09QAA       |
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | via roma       |
      | address_radd_cap      | 80133          |
      | address_radd_province | NA             |
      | address_radd_city     | NAPOLI         |
      | radd_description      | descrizione    |
      | radd_phoneNumbers     | +3933312345678 |
      | radd_externalCodes    | EXT09QAA,EXT11 |


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 70
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_23] Creazione nuova sede RADD con ExternalCode già esistente
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma       |
      | address_radd_cap      | 80133          |
      | address_radd_province | NA             |
      | address_radd_city     | NAPOLI         |
      | radd_description      | descrizione    |
      | radd_phoneNumbers     | +3933312345678 |
      | radd_externalCodes    | EXT09QAA       |
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | via roma             |
      | address_radd_cap      | 80133                |
      | address_radd_province | NA                   |
      | address_radd_city     | NAPOLI               |
      | radd_description      | descrizione          |
      | radd_phoneNumbers     | +3933312345678       |
      | radd_externalCodes    | EXT09QAA,EXT11,EXT12 |
    When viene richiesta la lista degli sportelli Radd V2 con dati:
      | radd_filter_limit   | 100  |
      | radd_filter_lastKey | NULL |
    Then cancello i registriV2 con externalCode:
      | EXT09QAA |
      | EXT09QAA |


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs ND
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_24] Creazione nuova sede RADD con ExternalCode ripetuto
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma        |
      | address_radd_cap      | 80133           |
      | address_radd_province | NA              |
      | address_radd_city     | NAPOLI          |
      | radd_description      | descrizione     |
      | radd_phoneNumbers     | +3933312345678  |
      | radd_externalCodes    | EXT2QAA,EXT2QAA |




  #  *** MODIFICA ***

  @raddAnagraficaV2 @deleteNewSite @cognito2 #rif srs 42 e 56
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_9] Modifica sportello RADD con dati corretti con verifica response
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row          | via posto               |
      | address_radd_cap          | 75010                   |
      | address_radd_province     | MT                      |
      | address_radd_city         | OLIVETO LUCANO          |
      | address_radd_country      | ITALY                   |
      | radd_description          | descrizione             |
      | radd_phoneNumbers         | +3933312345678          |
      | radd_openingTime          | mon=9:00-10:00#         |
      | radd_start_validity       | now                     |
      | radd_end_validity         | +10g                    |
      | radd_externalCodes        | EXT09QA                 |
      | radd_email                | test@example.com        |
      | radd_appointment_required | true                    |
      | radd_website              | https://www.example.com |
      | radd_partnerType          | partnertype             |
    Then viene modificato uno sportello Radd V2 con dati:
      | radd_description          | descrizione modificata     |
      | radd_openingTime          | tue=10:00-20:00#           |
      | radd_phoneNumbers         | +3933312345678             |
      | radd_email                | test@examplemodificato.com |
      | radd_end_validity         | 2030-10-10                 |
      | radd_externalCodes        | EXT09QA                    |
      | radd_appointment_required | false                      |
      | radd_website              | https://www.ex1.com        |
    And la response registry V2 deve avere i campi "obbligatori" valorizzati
    Then la response registry V2 deve avere i campi correttamente formattati
    And la response V2 a seguito del nuovo inserimento deve contenere i valori attesi
      | description         | descrizione modificata     |
      | openingTime         | tue=10:00-20:00#           |
      | phoneNumbers        | +3933312345678             |
      | email               | test@examplemodificato.com |
      | endValidity         | 2030-10-10                 |
      | externalCodes       | EXT09QA                    |
      | appointmentRequired | false                      |
      | website             | https://www.ex1.com        |


  @raddAnagraficaV2 @deleteNewSite @cognito2 #rif srs 47, 49, 50, 51, 52, 54, 55
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_10] Modifica sportello RADD con dati non conformi
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row          | via posto               |
      | address_radd_cap          | 75010                   |
      | address_radd_province     | MT                      |
      | address_radd_city         | OLIVETO LUCANO          |
      | address_radd_country      | ITALY                   |
      | radd_description          | descrizione             |
      | radd_phoneNumbers         | +390212345678           |
      | radd_openingTime          | mon=9:00-10:00#         |
      | radd_start_validity       | now                     |
      | radd_end_validity         | +10g                    |
      | radd_externalCodes        | EXT010QA                |
      | radd_email                | test@example.com        |
      | radd_appointment_required | true                    |
      | radd_website              | https://www.example.com |
      | radd_partnerType          | partnertype             |
    Then viene modificato uno sportello Radd V2 con dati errati:
      | radd_description          | <description>          |
      | radd_openingTime          | <openingTime>          |
      | radd_phoneNumbers         | <phoneNumbers>         |
      | radd_email                | <email>                |
      | radd_end_validity         | <end_validity>         |
      | radd_externalCodes        | <externalCodes>        |
      | radd_appointment_required | <appointment_required> |
      | radd_website              | <website>              |
    Then l'operazione Radd V2 ha prodotto un errore con status code "400"
    Examples:
      | description | openingTime | phoneNumbers                        | email                    | end_validity             | externalCodes | appointment_required | website     |
      | NULL        | NULL        | +390123456789,3921573273,3333333333 | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +39 012 3456789                     | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | NULL                                | NULL                     | 21-03-2024               | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | NULL                                | NULL                     | 1998-01-01               | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | NULL                                | NULL                     | 2025-01-0A               | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | NULL                                | NULL                     | 2025-01                  | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | NULL                                | NULL                     | 2025/01/01               | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | NULL                                | mail@@esempio.it         | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | NULL                                | NULL                     | NULL                     | NULL          | NULL                 | www.esempio |
      | NULL        | NULL        | !!"$%&£%'?^\s#!SG(£%%£%'            | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | NULL                                | !!"$%&/ASgSG(£%%£%'?^\s# | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | NULL                                | NULL                     | !!"$%&/ASgSG(£%%£%'?^\s# | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | ""                                  | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 1234567890                          | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +390123456789,333123456             | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 080123456789                        | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +390123456789,+394441234567         | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +390123456789,+39333123ABCD         | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 3331234567890                       | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +39-333-1234567                     | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 999333123456                        | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 00390012345678                      | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +393                                | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 333123                              | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +39333123                           | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 39 3331234567                       | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 700123456                           | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 003900123                           | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +39011234567890                     | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | (0039)3331234567                    | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +39.333.1234567                     | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +39/3331234567                      | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 0039-333-1234567                    | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | 333-123-4567                        | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +39 800123456                       | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +39 02 1234567                      | NULL                     | NULL                     | NULL          | NULL                 | NULL        |
      | NULL        | NULL        | +39 333123456789                    | NULL                     | NULL                     | NULL          | NULL                 | NULL        |


  @raddAnagraficaV2 @deleteNewSite @cognito2 #rif srs 43-44
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_11] Modifica sportello RADD con dati locationId non corretto
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row          | via posto               |
      | address_radd_cap          | 75010                   |
      | address_radd_province     | MT                      |
      | address_radd_city         | OLIVETO LUCANO          |
      | address_radd_country      | ITALY                   |
      | radd_description          | descrizione             |
      | radd_phoneNumbers         | +390212345678           |
      | radd_openingTime          | mon=9:00-10:00#         |
      | radd_start_validity       | now                     |
      | radd_end_validity         | +10g                    |
      | radd_externalCodes        | EXT11QA                 |
      | radd_email                | test@example.com        |
      | radd_appointment_required | true                    |
      | radd_website              | https://www.example.com |
      | radd_partnerType          | partnertype             |
    Then viene modificato uno sportello Radd V2 con dati errati:
      | locationId                | <locationId>               |
      | radd_description          | descrizione modificata     |
      | radd_openingTime          | tue=10:00-20:00#           |
      | radd_phoneNumbers         | +390212345678              |
      | radd_email                | test@examplemodificato.com |
      | radd_end_validity         | 2030-10-10                 |
      | radd_appointment_required | false                      |
      | radd_website              | https://www.ex1.com        |
    Then l'operazione Radd V2 ha prodotto un errore con status code "<statusCode>"
    Examples:
      | locationId                           | statusCode |
      | NULL                                 | 400        |
      | CASUALE                              | 404        |
      | 32d80697-da08-42ce-b0d3-c46e5152eda2 | 404        |


  @raddAnagraficaV2 @deleteNewSite @cognito2 #rif srs 39
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_12] Aggiornamento sportello RADD con utente abilitato a sola lettura
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row          | via posto               |
      | address_radd_cap          | 75010                   |
      | address_radd_province     | MT                      |
      | address_radd_city         | OLIVETO LUCANO          |
      | address_radd_country      | ITALY                   |
      | radd_description          | descrizione             |
      | radd_phoneNumbers         | +390212345678           |
      | radd_openingTime          | mon=9:00-10:00#         |
      | radd_start_validity       | now                     |
      | radd_end_validity         | +10g                    |
      | radd_externalCodes        | EXT12QA                 |
      | radd_email                | test@example.com        |
      | radd_appointment_required | true                    |
      | radd_website              | https://www.example.com |
      | radd_partnerType          | partnertype             |
    Given Effettuo l'autenticazione per l' utente con permessi: "SOLO_LETTURA"
    Then viene modificato uno sportello Radd V2 con dati errati:
      | radd_description          | descrizione modificata     |
      | radd_openingTime          | tue=10:00-20:00#           |
      | radd_phoneNumbers         | +390212345678              |
      | radd_email                | test@examplemodificato.com |
      | radd_end_validity         | 2030-10-10                 |
      | radd_appointment_required | false                      |
      | radd_website              | https://www.ex1.com        |
    Then l'operazione ha prodotto un errore con status code "403"
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"


  @raddAnagraficaV2 @cognito1 #rif srs 71
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_25] Modifica external code con external code già censito per altra sede
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +390212345678 |
      | radd_externalCodes    | EXT99AA       |
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +390212345678 |
      | radd_externalCodes    | EXT99AB       |
    Then viene modificato uno sportello Radd V2 con dati:
      | radd_externalCodes | EXT99AA |
    When viene richiesta la lista degli sportelli Radd V2 con dati:
      | radd_filter_limit   | 100  |
      | radd_filter_lastKey | NULL |
    Then cancello i registriV2 con externalCode:
      | EXT99AA |
      | EXT99AA |



     #  ***CANCELLAZIONE ***

  # solo per testing
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_Delete] Cancellazione sportello RADD per testing con parametri
    Given viene cancellato lo sportello Radd V2 appena inserito tramite locationId: "<locationId>" e partnerId: "<partnerId>" con errore
    Examples:
      | locationId | partnerId |
      |            |           |


  @raddAnagraficaV2 @deleteNewSite @cognito2 #rif srs 38
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_13] Cancellazione sportello RADD con utente abilitato a sola lettura
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +390212345678 |
      | radd_externalCodes    | EXT13QA       |
    Given Effettuo l'autenticazione per l' utente con permessi: "SOLO_LETTURA"
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId con errore
    Then l'operazione ha prodotto un errore con status code "403"
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"


  @raddAnagraficaV2 @deleteNewSite @cognito2 #rif srs 58
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_14] Cancellazione sportello RADD e verifica assenza in lettura
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +390212345678 |
      | radd_externalCodes    | EXT14QA       |
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId
    When viene richiesta la lista degli sportelli Radd V2 con dati:
      | radd_filter_limit   | 100  |
      | radd_filter_lastKey | NULL |
    Then verifica che il locationId oggetto della cancellazione è "ASSENTE" nella response di lettura


  @raddAnagraficaV2 @deleteNewSite @cognito2 #rif srs 59, 60
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_15] Cancellazione sportello RADD con locationId non accettato
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma        |
      | address_radd_cap      | 80133           |
      | address_radd_province | NA              |
      | address_radd_city     | NAPOLI          |
      | radd_description      | descrizione     |
      | radd_phoneNumbers     | +390212345678   |
      | radd_externalCodes    | <externalCodes> |
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId: "<locationId>" con errore
    Then l'operazione Radd V2 ha prodotto un errore con status code "<statusCode>"
    Examples:
      | locationId                           | externalCodes | statusCode |
      | ee95edab-9b74-4c46-9d69-2b6c8b3f5f82 | EXT15QA1      | 404        |
      | NULL                                 | EXT15QA2      | 400        |
      | #@#@#@                               | EXT15QA3      | 404        |
      | ĄŁĽŚŠŞSAFŤŹŽŻ                        | EXT15QA4      | 404        |


  @raddAnagraficaV2 @deleteNewSite @cognito2 #rif srs 61
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_16] Cancellazione sportello RADD da partenrId non associato al localId
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +390212345678 |
      | radd_externalCodes    | EXT16QA       |
    And viene cancellato lo sportello Radd V2 appena inserito con partnerId: "77765432555" con errore
    Then l'operazione ha prodotto un errore con status code "404"
    When viene richiesta la lista degli sportelli Radd V2 con dati:
      | radd_filter_limit   | 100  |
      | radd_filter_lastKey | NULL |
    Then verifica che il locationId oggetto della cancellazione è "PRESENTE" nella response di lettura


    # *** LETTURA ***

  @raddAnagraficaV2 @cognito1 #rif srs 63 e 68
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_17] Lettura sedi Radd con limite di impaginazione e verifica campi valorizzati
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +390212345678 |
      | radd_externalCodes    | EXT17AD       |
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +390212345678 |
      | radd_externalCodes    | EXT17AC       |
    When viene richiesta la lista degli sportelli Radd V2 con dati:
      | radd_filter_limit   | 1    |
      | radd_filter_lastKey | NULL |
    And la response V2 deve contenere 1 items
    And la response registry V2 della lettura deve avere i campi "obbligatori" valorizzati
    When viene richiesta la lista degli sportelli Radd V2 con dati:
      | radd_filter_limit   | 100  |
      | radd_filter_lastKey | NULL |
    Then cancello i registriV2 con externalCode:
      | EXT17AC |
      | EXT17AD |


  @raddAnagraficaV2 @cognito1 #rif srs 64
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_19] Lettura sedi Radd con token non valido
    Given Effettuo l'autenticazione per l' utente con permessi: "TOKEN_NON_VALIDO"
    Then viene richiesta la lista degli sportelli Radd V2 con errore
      | radd_filter_limit   | 1    |
      | radd_filter_lastKey | NULL |
    And l'operazione ha prodotto un errore con status code "403"


  @raddAnagraficaV2 @cognito1 #rif srs 65
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_20] Lettura sedi Radd con partenrId non valido
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    Then viene impostato un partenr Id non valido
    Then viene richiesta la lista degli sportelli Radd V2 con errore
      | radd_filter_limit   | 1    |
      | radd_filter_lastKey | NULL |
    And l'operazione ha prodotto un errore con status code "400"


  @raddAnagraficaV2 @deleteNewSite @cognito1 #rif srs 66 e 67
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_18] inserimento sportello RADD con dati corretti
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma            |
      | address_radd_cap      | 80133               |
      | address_radd_province | NA                  |
      | address_radd_city     | NAPOLI              |
      | radd_description      | Test QA             |
      | radd_phoneNumbers     | +390212345678       |
      | radd_externalCodes    | EXT18QA             |
      | address_radd_country  | ITALY               |
      | radd_openingTime      | tue=10:00-20:00#    |
      | radd_start_validity   | 2030-01-01          |
      | radd_end_validity     | 2030-02-01          |
      | radd_email            | teat@test.com       |
      | radd_website          | https://www.ex1.com |
      | radd_partner_type     | CAF                 |
    Then viene richiesta la lista degli sportelli Radd V2 con errore
      | radd_filter_limit   | 100  |
      | radd_filter_lastKey | NULL |
    And viene verificato che l' ultimo sportello inserito venga restituito nella lista tramite locationId
    Then la response registry V2 deve avere i campi correttamente formattati