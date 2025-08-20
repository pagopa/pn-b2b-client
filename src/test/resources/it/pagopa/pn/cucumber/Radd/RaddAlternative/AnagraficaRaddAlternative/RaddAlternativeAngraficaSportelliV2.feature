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




  Scenario: [RADD_ANAGRAFICA] AUT
    Given l' utente con username "test@test.com" password "Test_Cognito_1.!" e clientId "77j22r1r812dt3vo8d4s985ap4" richiede e riceve un token valido tramite cognito



  #  *** INSERIMENTO ***

  @raddAnagraficaV2 #rif srs 1
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_1] Creazione nuova sede RADD con dati corretti
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +399858425136 |
      | radd_externalCodes    | EXT000B       |
    Then la response V2 a seguito del nuovo inserimento deve contenere i valori attesi
      | addressRow    | Via Roma, 80133 Napoli NA, Italia |
      | cap           | 80133                             |
      | province      | NA                                |
      | city          | Napoli                            |
      | description   | descrizione                       |
      | phoneNumbers  | +399858425136                     |
      | externalCodes | EXT000B                           |
    Then viene verificato che l' ultimo sportello inserito venga restituito nella lista tramite locationId
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId
#aggiungere controllo sul 200?


  @raddAnagraficaV2 #rif srs 2
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_2] Creazione nuova sede RADD con intero campo address mancante
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd       | NULL          |
      | radd_description   | descrizione   |
      | radd_phoneNumbers  | +399858425136 |
      | radd_externalCodes | EXT002A       |
    Then l'operazione ha prodotto un errore con status code "400"


  @raddAnagraficaV2 #rif srs 3-4-5-6-7-8-9
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_3] Creazione nuova sede RADD senza campi obbligatori
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | <via>           |
      | address_radd_cap      | <cap>           |
      | address_radd_province | <provincia>     |
      | address_radd_city     | <citta>         |
      | radd_description      | <descrizione>   |
      | radd_phoneNumber      | <telefono>      |
      | radd_externalCodes    | <externalCodes> |
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | via      | cap   | provincia | citta  | descrizione | telefono      | externalCodes |
      | NULL     | 80133 | NA        | NAPOLI | descrizione | +399858425136 | EXT100        |
      | via roma | NULL  | NA        | NAPOLI | descrizione | +399858425136 | EXT101        |
      | via roma | 80133 | NULL      | NAPOLI | descrizione | +399858425136 | EXT103        |
      | via roma | 80133 | NA        | NULL   | descrizione | +399858425136 | EXT104        |
      | via roma | 80133 | NA        | NAPOLI | NULL        | +399858425136 | EXT105        |
      | via roma | 80133 | NA        | NAPOLI | descrizione | NULL          | EXT106        |
      | via roma | 80133 | NA        | NAPOLI | descrizione | +399858425136 | NULL          |


  @raddAnagraficaV2 #rif srs dal 10 al 27 + dal 29 al 36
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_4] Creazione nuova sede con campi non validi
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | <via>               |
      | address_radd_cap      | <cap>               |
      | address_radd_province | <provincia>         |
      | address_radd_city     | <citta>             |
      | radd_description      | <descrizione>       |
      | radd_phoneNumber      | <telefono>          |
      | radd_externalCode     | <externalCode>      |

      | address_radd_country  | <stato>             |
      | radd_openingTime      | <aperturaSportello> |
      | radd_start_validity   | <startValidity>     |
      | radd_end_validity     | <endValidity>       |
      | radd_email            | <email>             |
      | radd_website          | <website>           |
      | radd_partner_type     | <partnertype>       |
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | via         | cap    | provincia | citta  | stato  | descrizione | telefono      | aperturaSportello             | startValidity | endValidity | externalCode  | email                | website      | partnertype |
      | Ciao@mondo# | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT400        | NULL                 | NULL         | NULL        |
      | via fiume   | 123456 | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT401        | NULL                 | NULL         | NULL        |
      | via fiume   | 2016   | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT402        | NULL                 | NULL         | NULL        |
      | via fiume   | 2016Z  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT403        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | ITA       | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT404        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | na        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT405        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | NAP       | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT406        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | 10        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT407        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | ++99858425136 | NULL                          | NULL          | NULL        | EXT408        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +3998-842-136 | NULL                          | NULL          | NULL        | EXT409        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT410        | https://exa_mple.com | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT411        | nome@dominio         | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT412        | @dominio.ext         | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | Mo-Fr 09:00-13:00,15:00-18:00 | NULL          | NULL        | EXT413        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | 08:00-1A:30,15:00-18:00       | NULL          | NULL        | EXT414        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | 1998-01-01    | NULL        | EXT415        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | 01-01-2030    | NULL        | EXT416        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | 202-01-01     | NULL        | EXT417        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | 01-01-2030  | EXT418        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | 2030-01-01    | 2029-01-01  | EXT419        | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | ĄŁĽŚŠŞSAFŤŹŽŻ | NULL                 | NULL         | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT421        | NULL                 | https://.com | NULL        |
      | via fiume   | 20161  | MI        | MILANO | ITALIA | Test        | +399858425136 | NULL                          | NULL          | NULL        | EXT421        | NULL                 | https://.com | ĄŁĽ         |


  @raddAnagraficaV2 #rif srs 37
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_5] Creazione nuova sede RADD con dati corretti
    Given Effettuo l'autenticazione per l' utente con permessi: "SOLO_LETTURA"
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +399858425136 |
      | radd_externalCodes    | EXT002D       |
    Then l'operazione ha prodotto un errore con status code "403"


  @raddAnagraficaV2 #rif srs 40
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_6] Creazione nuova sede RADD con dati corretti
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row          | via roma                |
      | address_radd_cap          | 80133                   |
      | address_radd_province     | NA                      |
      | address_radd_city         | NAPOLI                  |
      | radd_description          | descrizione             |
      | radd_phoneNumbers         | +399858425136           |
      | radd_externalCodes        | EXT0010                 |
      | address_radd_country      | ITALIA                  |
      | radd_openingTime          | mon=9:00-10:00#         |
      | radd_start_validity       | now                     |
      | radd_end_validity         | +10g                    |
      | radd_email                | test@example.com        |
      | radd_website              | https://www.example.com |
      | radd_partner_type         | CAF                     |
      | radd_appointment_required | true                    |
    And la response V2 deve avere tutti i campi valorizzati
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId


  @raddAnagraficaV2 #rif srs 29
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_7] Creazione nuova sede RADD con campo stratValidity vuoto e restituzione campo formattato correttamente
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via roma      |
      | address_radd_cap      | 80133         |
      | address_radd_province | NA            |
      | address_radd_city     | NAPOLI        |
      | radd_description      | descrizione   |
      | radd_phoneNumbers     | +399858425136 |
      | radd_externalCodes    | EXT007A       |
      | radd_start_validity   | NULL          |
    Then la response V2 deve aver restiutito in automatico startValidity odierno in formato yyyy-MM-dd
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId


# todo controllo data restituita rif srs 38-39 401 su cancellazione e modifica


  #  ***CANCELLAZIONE ***

  @raddAnagraficaV2 #rif srs
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_8] Cancellazione sportello RADD con dati corretti
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row          | via posto               |
      | address_radd_cap          | 75010                   |
      | address_radd_province     | MT                      |
      | address_radd_city         | OLIVETO LUCANO          |
      | address_radd_country      | ITALY                   |
      | radd_description          | descrizione             |
      | radd_phoneNumbers         | +39 9858425136          |
      | radd_openingTime          | mon=9:00-10:00#         |
      | radd_start_validity       | now                     |
      | radd_end_validity         | +10g                    |
      | radd_externalCodes        | EXT0011                 |
      | radd_email                | test@example.com        |
      | radd_appointment_required | true                    |
      | radd_website              | https://www.example.com |
      | radd_partnerType          | partnertype             |
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId

  @raddAnagraficaV2 #rif srs
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_11] Cancellazione sportello RADD con dati corretti
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId: "<locationId>" con errore
    Then l'operazione ha prodotto un errore con status code "403"
Examples:
    |locationId|
    |A|
    |A000000000000000000000111111111111|
    |#@#@#@|
    |""|
    |ĄŁĽŚŠŞSAFŤŹŽŻ|

  @raddAnagraficaV2 #rif srs 38
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_9] Cancellazione sportello RADD con utente abilitato a sola lettura
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row          | via posto               |
      | address_radd_cap          | 75010                   |
      | address_radd_province     | MT                      |
      | address_radd_city         | OLIVETO LUCANO          |
      | address_radd_country      | ITALY                   |
      | radd_description          | descrizione             |
      | radd_phoneNumbers         | +39 9858425136          |
      | radd_openingTime          | mon=9:00-10:00#         |
      | radd_start_validity       | now                     |
      | radd_end_validity         | +10g                    |
      | radd_externalCodes        | EXT0012                 |
      | radd_email                | test@example.com        |
      | radd_appointment_required | true                    |
      | radd_website              | https://www.example.com |
      | radd_partnerType          | partnertype             |
    Given Effettuo l'autenticazione per l' utente con permessi: "SOLO_LETTURA"
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId con errore
    Then l'operazione ha prodotto un errore con status code "403"


  Scenario: [RADD_ANAGRAFICA_CRUD_V2_4] cancellazione sportello RADD con dati corretti
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via posto        |
      | address_radd_cap      | 80017            |
      | address_radd_province | NA               |
      | address_radd_city     | MELITO DI NAPOLI |
      | address_radd_country  | ITALY            |
      | radd_start_validity   | now              |
      | radd_description      | descrizione      |
      | radd_phoneNumber      | +39 0126437425   |
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId



    # *** LETTURA ***



  @raddAnagraficaV2
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_5]
    Given Effettuo l'autenticazione per l' utente con permessi: "LETTURA_SCRITTURA"
    When viene richiesta la lista degli sportelli Radd V2 con dati:
      | radd_filter_limit   | 3    |
      | radd_filter_lastKey | NULL |
    And la response V2 deve contenere 3 items
    And la response registry V2 della lettura deve avere tutti i campi obbligatori valorizzati
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId


  Scenario: [RADD_ANAGRAFICA_CRUD_V2_5b] inserimento sportello RADD con dati corretti
    Given l' utente con username "test@test.com" password "Test_Cognito_1.!" e clientId "77j22r1r812dt3vo8d4s985ap4" richiede e riceve un token valido tramite cognito
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row          | via posto               |
      | address_radd_cap          | 75010                   |
      | address_radd_province     | MT                      |
      | address_radd_city         | OLIVETO LUCANO          |
      | address_radd_country      | ITALY                   |
      | radd_description          | descrizione             |
      | radd_phoneNumbers         | +39 9858425136          |
      | radd_openingTime          | mon=9:00-10:00#         |
      | radd_start_validity       | now                     |
      | radd_end_validity         | +10g                    |
      | radd_externalCodes        | EXT006                  |
      | radd_email                | test@example.com        |
      | radd_appointment_required | true                    |
      | radd_website              | https://www.example.com |
      | radd_partnerType          | partnertype             |
    And la response V2 contiene almeno un externalCode uguale a quello della request
    #And la response registry V2 deve avere tutti i campi obbligatori valorizzati
    Then viene cancellato lo sportello Radd V2 appena inserito tramite locationId




  #  *** MODIFICA ***



  Scenario: [RADD_ANAGRAFICA_CRUD_V2_8] modifica sportello RADD con dati corretti controllo successo modifica
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via posto      |
      | address_radd_cap      | 80014          |
      | address_radd_province | NA             |
      | address_radd_city     | VARCATURO      |
      | address_radd_country  | ITALY          |
      | radd_start_validity   | +1g            |
      | radd_description      | descrizione    |
      | radd_phoneNumber      | +39 2445356789 |
      | radd_openingTime      | tue=1:00-2:00# |

    Then viene modificato uno sportello Radd V2 con dati:
      | radd_description | descrizione modificata |
      | radd_openingTime | tue=10:00-20:00#       |
      | radd_phoneNumber | +39 9858425136         |
    And la response registry V2 deve avere tutti i campi obbligatori valorizzati
    And la response V2 a seguito del nuovo inserimento deve contenere i valori attesi
      | partnerId           | P12345           |
      #| locationId         | L67890 |
      #| locationId          | 63afd148-7e4a-4a7d-ba50-ec4d97a74322 |
      | description         | descrizione      |
      | email               | test@example.com |
      | appointmentRequired | true             |
      | externalCodes       | EXT002A          |
      | phoneNumbers        | +39 9858425136   |


  Scenario: [RADD_ANAGRAFICA_CRUD_V2_9] modifica sportello RADD con formato campi errato controllo restituzione errore
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via posto      |
      | address_radd_cap      | 80016          |
      | address_radd_province | NA             |
      | address_radd_city     | SAN ROCCO      |
      | address_radd_country  | ITALY          |
      | radd_description      | descrizione    |
      | radd_phoneNumber      | +39 2445356789 |
      | radd_openingTime      | tue=1:00-2:00# |

    Then viene modificato uno sportello Radd V2 con dati errati:
      | radd_openingTime | !!"$%&/ASgSG(£%%£%'?^\s# |
      | radd_phoneNumber | !!"$%&/(AGSS£%%£%'?^\s#  |
    And l'operazione ha prodotto un errore con status code "400"


  Scenario: [RADD_ANAGRAFICA_CRUD_V2_10] modifica sportello RADD con registryId non presente controllo restituzione errore
    When viene modificato uno sportello Radd V2 con dati errati:
      | radd_registryId | errato |
    Then l'operazione ha prodotto un errore con status code "404"


  Scenario: [RADD_ANAGRAFICA_CRUD_V2_11] modifica sportello RADD con registryId vuoto controllo restituzione errore
    When viene modificato uno sportello Radd V2 con dati errati:
      | radd_registryId | NULL |
    Then l'operazione ha prodotto un errore con status code "400"

  #questo non è stato modificato, fallisce perchè non va in 404

  Scenario: [RADD_ANAGRAFICA_CRUD_V2_12] modifica sportello RADD con uid non presente controllo restituzione errore  -- non vengono effettuati i controlli
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via posto      |
      | address_radd_cap      | 80024          |
      | address_radd_province | NA             |
      | address_radd_city     | CARDITELLO     |
      | address_radd_country  | ITALY          |
      | radd_description      | descrizione    |
      | radd_phoneNumber      | +39 2445356789 |
      | radd_openingTime      | tue=1:00-2:00# |

    Then viene modificato uno sportello Radd V2 con dati errati:
      | radd_registryId | corretto      |
      | radd_uid        | AJFSAJFOSIJFO |
    And l'operazione ha prodotto un errore con status code "404"


  Scenario: [RADD_ANAGRAFICA_CRUD_V2_13] modifica sportello RADD con uid vuoto controllo restituzione errore
    Then viene modificato uno sportello Radd V2 con dati errati:
      | radd_uid | NULL |
    And l'operazione ha prodotto un errore con status code "400"


    #  *** UTENTE PERMESSI SOLA LETTURA ***