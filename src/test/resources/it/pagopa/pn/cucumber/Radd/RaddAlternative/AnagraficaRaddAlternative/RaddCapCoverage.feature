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


  #Tebelle di riferimento: pn-AttachmentsConfig e pn-RaddCoverage

  Scenario: [RADD_ANAGRAFICA] Testing Autenticazione con parametri
    Given l' utente con username "test@test.com" password "Test_Cognito_1.!" e clientId "77j22r1r812dt3vo8d4s985ap4" richiede e riceve un token valido tramite cognito


 #                *** Validazione Api Verifica da file ***


    ##  Test massivo con recupero dati da file csv contenuto nel path: src/main/resources/TEST-cap-localita.csv
    ##  NOTA: Il csv da passare in input deve essere rinominato in: TEST-cap-localita.csv
    ##  Viene generato un file di report in: src/main/resources/output/risultati_copertura.csv
  Scenario: [RADD_API_COPERTURA_CAP_VALIDATION] Creazione report di coperture cap radd da file csv
    Given setto la data per la quale voglio verificare la copertura al "$TODAY()"
    Then leggo il file csv con cap e localita ed effettuo chiamate light e complete con report



  #                 *** CREAZIONE ***


  @capCoverageRadd @cognito1
  Scenario Outline: [RADD_API_COPERTURA_CAP_CREAZIONE_1] Creazione nuova copertura Radd tutti i campi compilati
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd
    And la response deve contenere la località e il cap "<cap>" attesi
    Examples:
      | cap   | locality | cadastralCode | province |
      | 00100 | Roma     | H501          | RM       |

  @capCoverageRadd @cognito1
  Scenario Outline: [RADD_API_COPERTURA_CAP_CREAZIONE_A] Creazione nuova copertura Radd tutti i campi compilati token non valido
    Given Effettuo l'autenticazione per l' utente con permessi: "TOKEN_NON_VALIDO"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd con Errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "403"
    Examples:
      | cap   | locality | cadastralCode | province |
      | 00100 | Roma     | H501          | RM       |

  @capCoverageRadd @cognito1
  Scenario Outline: [RADD_API_COPERTURA_CAP_CREAZIONE_2] Creazione nuova copertura Radd solo campi obbligatori
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd
    And la response deve contenere la località e il cap "<cap>" attesi
    Examples:
      | cap   | locality | cadastralCode | province |
      | 00100 | Roma     | $NULL         | $NULL    |


  @capCoverageRadd @cognito1
  Scenario Outline: [RADD_API_COPERTURA_CAP_CREAZIONE_3] Errore Creazione nuova copertura Radd campi inesistenti o vuoti
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd con Errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "400"
    Examples:
      | cap    | cadastralCode | province |
      | 80A00  | H501          | RM       |
      | 80#00  | H501          | RM       |
      | 001001 | H501          | RM       |
      | 0 0100 | H5011         | RM       |
      | $NULL  | H5011         | RM       |
      | 00100  | H5011         | RM       |
      | 00100  | H50 1         | RM       |
      | 00100  | H5            | RM       |
      | 00100  | H501          | R M      |
      | 00100  | H501          | RMA      |
      | 00100  | H501          | R        |


  @capCoverageRadd @cognito1
  Scenario Outline: [RADD_API_COPERTURA_CAP_CREAZIONE_4] Errore Creazione nuova copertura Radd locality vuoto
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd:
      | cap   | locality   | cadastralCode   | province   |
      | <cap> | <locality> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd con Errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "400"
    Examples:
      | cap   | locality | cadastralCode | province |
      | 80100 | $NULL    | H501          | RM       |


  @capCoverageRadd @cognito1
  Scenario Outline: [RADD_API_COPERTURA_CAP_CREAZIONE_5] Errore Creazione nuova copertura Radd utente solo lettura
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "SOLO_LETTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | locality   | cadastralCode   | province   |
      | <cap> | <locality> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd con Errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "403"
    Examples:
      | cap   | cadastralCode | province |
      | 80100 | H501          | RM       |

  @capCoverageRadd @cognito1
  Scenario: [RADD_API_COPERTURA_CAP_CREAZIONE_6] Errore Creazione nuova copertura Radd sede già creata
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 80100 | H501          | RM       |
    And creo una nuova copertura Radd
    And la response deve contenere la località e il cap "80100" attesi
    And creo una nuova copertura Radd con Errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "409"

  @capCoverageRadd @cognito1
  Scenario: [RADD_API_COPERTURA_CAP_CREAZIONE_6B] Errore Creazione nuova copertura Radd sede già creata
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 80100 | $NULL         | $NULL    |
    And creo una nuova copertura Radd
    And la response deve contenere la località e il cap "80100" attesi
    And creo una nuova copertura Radd con Errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "409"



#                       *** MODIFICA ***

  @capCoverageRadd @cognito2 #rif 15A-15B-16
  Scenario Outline: [RADD_API_COPERTURA_CAP_MODIFICA_15A] Modifica copertura Radd tutti i campi compilati
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 80100 | H501          | RM       |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd
    Examples:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity   |
      | 80100 | /        | H501          | RM       | $DATE_ADD(-1D) | $DATE_ADD(1Y) |
      | 80100 | /        | H501          | MI       | $DATE_ADD(-1D) | $DATE_ADD(1Y) |
      | 80100 | /        | $NULL         | $NULL    | $DATE_ADD(-1D) | $DATE_ADD(1Y) |
      | 80100 | /        | $NULL         | $NULL    | $NULL          | $NULL         |
      | 80100 | /        | H501          | RM       | $NULL          | $DATE_ADD(1Y) |
      | 80100 | /        | H501          | RM       | $DATE_ADD(-1D) | $NULL         |

  @capCoverageRadd @cognito2
  Scenario Outline: [RADD_API_COPERTURA_CAP_MODIFICA_17] Errore Modifica copertura Radd  campi obbligatori inesistenti,
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd con errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "<statusCode>"
    Examples:
      | cap    | locality | cadastralCode | province | startValidity  | endValidity    | statusCode |
      | 77777  | Roma     | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(1Y)  | 404        |
      | 00100  | Q        | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(1Y)  | 404        |
      | 001001 | Roma     | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(-1M) | 400        |
      | AAAAA  | Roma     | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(-1M) | 400        |
      | 0      | Q        | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(-1M) | 400        |
      | #      | Roma     | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(-1M) | 400        |
      | #      | Roma     | $NULL         | $NULL    | $NULL          | $NULL          | 400        |


  @capCoverageRadd @cognito2
  Scenario Outline: [RADD_API_COPERTURA_CAP_MODIFICA_18] Errore Modifica copertura Radd  campi opzionali inesistenti
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 80100 | H501          | RM       |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd con errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "400"
    Examples:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity       |
#      | 80100 | /        | WWWW          | RM       | 2025-10-01    | 2035-12-31  |
#      | 80100 | /        | H501          | WW       | 2025-10-01    | 2035-12-31  |
      | 80100 | /        | H501          | RM       | 2025-10-011    | ${$DATE_ADD(10Y)} |
      | 80100 | /        | H501          | RM       | 01-10-2025     | $DATE_ADD(10Y)    |
      | 80100 | /        | H501          | 1        | $DATE_ADD(-1Y) | $DATE_ADD(10Y)    |
      | 80100 | /        | H5 01         | RM       | $DATE_ADD(-1Y) | $DATE_ADD(10Y)    |
      | 80100 | /        | H501          | #        | $DATE_ADD(-1Y) | $DATE_ADD(10Y)    |
      | 80100 | /        | H501          | RM       | $DATE_ADD(-1Y) | #                 |
      | 80100 | /        | H             | RM       | $DATE_ADD(-1Y) | $DATE_ADD(10Y)    |
      | 80100 | $NULL    | H501          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(10Y)    |
      | $NULL | /        | H501          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(10Y)    |
      | 80100 | /        | H501          | RM       | $DATE_ADD(10Y) | $DATE_ADD(-1Y)    |
  #campi opzionali inesistenti
  #cap e locality vuoti
  #startValidity maggiore di una endValidity
  #endValidity minore di una startValidity

  @capCoverageRadd @cognito2
  Scenario: [RADD_API_COPERTURA_CAP_MODIFICA_23] Errore modifica copertura Radd con endValidity non futura
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 80100 | H501          | RM       |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity | endValidity    |
      | 80100 | /        | H501          | RM       | $NULL         | $DATE_ADD(-1Y) |
    And invoco l'API di aggiornamento copertura cap Radd con errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "400"

  @capCoverageRadd @cognito2
  Scenario: [RADD_API_COPERTURA_CAP_MODIFICA_22] Errore Modifica copertura Radd utente solo lettura
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "SOLO_LETTURA"
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity | endValidity   |
      | 00100 | Roma     | H502          | RM       | $TODAY()      | $DATE_ADD(1Y) |
    And invoco l'API di aggiornamento copertura cap Radd con errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "403"



#                    *** VERIFICA ***


  @capCoverageRadd @cognito2
  Scenario: [RADD_API_COPERTURA_CAP_VERIFICA_7] Verifica copertura Radd con tutti i campi compilati - light mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 00100 | H502          | RM       |
    And creo una nuova copertura Radd
    And la response deve contenere la località e il cap "00100" attesi
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    |
      | 00100 | /        | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(10Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr | country |
      | saluti   | via ciao   | via ecco    | 00100 | /    | RO    | RM | IT      |
    And invoco l'API di verifica copertura cap Radd Light mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"

  @capCoverageRadd @cognito2
  Scenario: [RADD_API_COPERTURA_CAP_VERIFICA_8] Verifica copertura Radd con tutti i campi compilati - complete mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 00100 | H502          | RM       |
    And creo una nuova copertura Radd
    And la response deve contenere la località e il cap "00100" attesi
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    |
      | 00100 | /        | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(10Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr | country |
      | saluti   | via ciao   | via ecco    | 00100 | /    | RO    | RM | IT      |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"

  @capCoverageRadd @cognito2
  Scenario: [RADD_API_COPERTURA_CAP_VERIFICA_9A] Verifica copertura Radd campi obbligatori - light mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 00100 | H502          | RM       |
    And creo una nuova copertura Radd
    And la response deve contenere la località e il cap "00100" attesi
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    |
      | 00100 | /        | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(10Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 00100 | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Light mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"


  @capCoverageRadd @cognito2
  Scenario: [RADD_API_COPERTURA_CAP_VERIFICA_9B] Verifica copertura Radd campi obbligatori - complete mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 00100 | H502          | RM       |
    And creo una nuova copertura Radd
    And la response deve contenere la località e il cap "00100" attesi
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    |
      | 00100 | /        | H502          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(10Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 00100 | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"


  @capCoverageRadd @cognito2 #rif 10 - 12
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_10] Errore Verifica copertura Radd cap e city inesistenti - complete/light mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2    | addressRow | addressRow2 | cap     | city     | city2 | pr | country |
      | Mario Rossi | Via Roma 1 | $NULL       | "<cap>" | "<city>" | $NULL | RM | IT      |
    And invoco l'API di verifica copertura cap Radd Complete mode con errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "400"
    And invoco l'API di verifica copertura cap Radd Light mode con errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "400"
    Examples:
      | cap   | city  |
      | A12   | /     |
      | 00100 | 123   |
      | $NULL | /     |
      | 00100 | $NULL |
      | 00 00 | /     |

  @capCoverageRadd @cognito2
  Scenario: [RADD_API_COPERTURA_CAP_VERIFICA_13] Errore Verifica copertura Radd search_mode vuoto
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 00100 | 123  | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd mode: NULL con errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "400"

  @capCoverageRadd @cognito2
  Scenario: [RADD_API_COPERTURA_CAP_VERIFICA_B] Verifica copertura Radd campi obbligatori - complete mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 00100 | H502          | RM       |
    And creo una nuova copertura Radd
    And la response deve contenere la località e il cap "00100" attesi
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    |
      | 00100 | /        | H502          | RM       | $DATE_ADD(10Y) | $DATE_ADD(11Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 00100 | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "NON_COPERTO"

    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    |
      | 00100 | /        | H502          | RM       | $DATE_ADD(10Y) | $DATE_ADD(10Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 00100 | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "NON_COPERTO"

    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    |
      | 00100 | /        | H502          | RM       | $DATE_ADD(-5Y) | $DATE_ADD(10Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 00100 | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"

    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity | endValidity   |
      | 00100 | /        | H502          | RM       | $DATE_ADD(1D) | $DATE_ADD(1D) |
    And invoco l'API di aggiornamento copertura cap Radd

    Then Effettuo l'autenticazione copertura cap per l' utente con permessi: "SOLO_LETTURA"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 00100 | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "NON_COPERTO"




##  Test massivo con recupero dati da file csv contenuto nel path: src/main/resources/TEST-cop-cap-radd.csv
##  NOTA: Il csv da passare in input deve essere rinominato in: TEST-cop-cap-radd.csv
  Scenario: [RADD_API_COPERTURA_CAP_INSERIMENTO_csv] Inserimento coperture da csv a tabella pn-RaddCoverage
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then inserisco i dati di copertura dal CSV nel database




##  Test massivo con recupero dati da file csv contenuto nel path: src/main/resources/TEST-cop-cap-radd.csv
##  NOTA: Il csv da passare in input deve essere rinominato in: TEST-cop-cap-radd.csv
  Scenario: [RADD_API_COPERTURA_CAP_VERIFICA_csv] Confronto coperture da csv
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then leggo il file csv e salvo cap, localita e stato copertura
    And verifico che lo stato della copertura sia coerente tra file e database



  # Solo per testing
  Scenario: [RADD_API_ONLY_TESTING] Verifica copertura Radd vedi da tabella : pn-RaddCoverage
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"

    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city    | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 00100 | BF-APJJ | $NULL | $NULL | $NULL   |

    Then setto la data per la quale voglio verificare la copertura al "$TODAY()"

    And invoco l'API di verifica copertura cap Radd Light mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"

    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"




# valutare:

#  cap coperto e cap non coperto -> validità con complete e light -> complete : atteso coperto - cambio date - complete
#  cap coperto e cap non coperto -> validità con complete e light -> date limite
#  cap coperto e cap non coperto -> validità con complete e light -> un cap senza end-validity, senza start validity
#

#cap senza end validity- poi con end validiy - nel range, fuori range




   #rif srs 1  #non è possibile implementare in quanto il parametro passato è LocalDate e quindi non è posssibile compilarlo in maniera non corretta
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_14] Verifica copertura con data non corretta con Light e Complete mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 80100 | H501          | RM       |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    |
      | 80100 | /        | H501          | RM       | $DATE_ADD(-1D) | $DATE_ADD(10Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto la data per la quale voglio verificare la copertura al "<search-date>"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 00100 | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode con errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "400"
    And invoco l'API di verifica copertura cap Radd Light mode con errore
    And l'operazione di copertura Radd ha prodotto un errore con status code "400"
    Examples:
      | search-date |
      | 2035-1-01   |
      | 20 35-01-01 |
      | 2035-01-#1  |
      | 01-01-2023  |
      | 2035-90-01  |
      | 203501-01   |
      | 2PP5-01-01  |
      | 2035-01 01  |
      | 2035-01-011 |
      | 2035-01-1   |


  @capCoverageRadd @cognito1 #rif srs 2
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_15L] Verifica copertura con Search-data interna al range con light mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto la data per la quale voglio verificare la copertura al "<search-date>"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | 80100 | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Light mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"
    Examples:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    | search-date     |
      | 80100 | /        | H501          | MI       | $DATE_ADD(-5Y) | $DATE_ADD(1Y)  | $DATE_ADD(-13M) |
      | 80100 | /        | H501          | MI       | $DATE_ADD(-1M) | $DATE_ADD(1Y)  | $TODAY()        |
      | 80100 | /        | H501          | MI       | $DATE_ADD(10Y) | $DATE_ADD(12Y) | $DATE_ADD(11Y)  |


  @capCoverageRadd @cognito2 #rif srs 3
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_16L] Verifica copertura con search-date esterna al range con Light mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto la data per la quale voglio verificare la copertura al "<search-date>"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | <cap> | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Light mode
    And per i dati forniti si verifica che lo stato di copertura sia "NON_COPERTO"
    Examples:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    | search-date     |
      | 90101 | /        | H501          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(1Y)  | $DATE_ADD(-13M) |
      | 90102 | /        | H501          | MI       | $DATE_ADD(10Y) | $DATE_ADD(15Y) | $TODAY()        |


  @capCoverageRadd @cognito1 #rif srs 4
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_17L] Verifica copertura con search-date uguale a start-validity e end-validity light mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto la data per la quale voglio verificare la copertura al "<search-date>"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | <cap> | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Light mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"
    Examples:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity   | search-date    |
      | 80100 | /        | H501          | RM       | $TODAY()       | $DATE_ADD(1Y) | $TODAY()       |
      | 80100 | /        | H501          | RM       | $DATE_ADD(1Y)  | $DATE_ADD(2Y) | $DATE_ADD(2Y)  |
      | 80100 | /        | H501          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(1Y) | $DATE_ADD(-1Y) |


  @capCoverageRadd @cognito2 #rif srs 5
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_18L] Verifica copertura con search-date nel range ma senza end-validity light mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto la data per la quale voglio verificare la copertura al "<search-date>"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | <cap> | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Light mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"
    Examples:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity | search-date     |
      | 80100 | /        | H501          | RM       | $DATE_ADD(-1Y) | $NULL       | $DATE_ADD(-11M) |
      | 80100 | /        | H501          | MI       | $DATE_ADD(10Y) | $NULL       | $DATE_ADD(11Y)  |
      | 80100 | /        | H501          | RM       | $TODAY()       | $NULL       | $TODAY()        |


  @capCoverageRadd @cognito1 #rif srs 2
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_15C] Verifica copertura con Search-data interna al range complete mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode | province |
      | 12121 | H501          | NA       |
    And creo una nuova copertura Radd con Errore
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | /        | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto la data per la quale voglio verificare la copertura al "<search-date>"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | <cap> | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode   | province   | startValidity   | endValidity     |
      | <cap> | /        | <cadastralCode> | <province> | $DATE_ADD(100Y) | $DATE_ADD(100Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Examples:
      | cap   | cadastralCode | province | startValidity  | endValidity    | search-date    |
      | 12121 | H501          | NA       | $DATE_ADD(-1Y) | $DATE_ADD(1Y)  | $DATE_ADD(1M)  |
      | 12121 | H501          | NA       | $DATE_ADD(-1Y) | $DATE_ADD(1Y)  | $TODAY()       |
      | 12121 | H501          | NA       | $DATE_ADD(10Y) | $DATE_ADD(12Y) | $DATE_ADD(11Y) |

  @capCoverageRadd @cognito1 #rif srs 3
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_16C] Verifica copertura con search-date esterna al range  Complete mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto la data per la quale voglio verificare la copertura al "<search-date>"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | <cap> | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "NON_COPERTO"
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality | cadastralCode   | province   | startValidity   | endValidity     |
      | <cap> | /        | <cadastralCode> | <province> | $DATE_ADD(100Y) | $DATE_ADD(100Y) |
    And invoco l'API di aggiornamento copertura cap Radd
    Examples:
      | cap   | locality | cadastralCode | province | startValidity | endValidity   | search-date    |
      | 12120 | /        | H501          | RM       | $TODAY()      | $DATE_ADD(1Y) | $DATE_ADD(-1Y) |
      | 12120 | /        | H501          | RM       | $DATE_ADD(1Y) | $DATE_ADD(2Y) | $TODAY()       |


  @capCoverageRadd @cognito1 #rif srs 4
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_17C ] Verifica copertura con search-date uguale a start-validity e end-validity light e complete mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto la data per la quale voglio verificare la copertura al "<search-date>"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | <cap> | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"
    Examples:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity    | search-date    |
      | 12123 | /        | H501          | RM       | $DATE_ADD(-1Y) | $DATE_ADD(1Y)  | $DATE_ADD(-1Y) |
      | 12123 | /        | H501          | RM       | $DATE_ADD(10Y) | $DATE_ADD(11Y) | $DATE_ADD(11Y) |
      | 12123 | /        | H501          | RM       | $TODAY()       | $DATE_ADD(1Y)  | $TODAY()       |

  @capCoverageRadd @cognito1 #rif srs 5
  Scenario Outline: [RADD_API_COPERTURA_CAP_VERIFICA_18C] Verifica copertura con search-date nel range ma senza end-validity  complete mode
    Given Effettuo l'autenticazione copertura cap per l' utente con permessi: "LETTURA_SCRITTURA"
    Then setto i dati per creare una nuova copertura Radd con locality random:
      | cap   | cadastralCode   | province   |
      | <cap> | <cadastralCode> | <province> |
    And creo una nuova copertura Radd
    And setto i dati per aggiornare una copertura Radd:
      | cap   | locality   | cadastralCode   | province   | startValidity   | endValidity   |
      | <cap> | <locality> | <cadastralCode> | <province> | <startValidity> | <endValidity> |
    And invoco l'API di aggiornamento copertura cap Radd
    Then setto la data per la quale voglio verificare la copertura al "<search-date>"
    Then setto i dati per verificare la copertura Radd:
      | nameRow2 | addressRow | addressRow2 | cap   | city | city2 | pr    | country |
      | $NULL    | $NULL      | $NULL       | <cap> | /    | $NULL | $NULL | $NULL   |
    And invoco l'API di verifica copertura cap Radd Complete mode
    And per i dati forniti si verifica che lo stato di copertura sia "COPERTO"
    Examples:
      | cap   | locality | cadastralCode | province | startValidity  | endValidity | search-date    |
      | 12125 | /        | H501          | RM       | $DATE_ADD(-5Y) | $NULL       | $DATE_ADD(-4Y) |
      | 12125 | /        | H501          | RM       | $DATE_ADD(10Y) | $NULL       | $DATE_ADD(11Y) |
      | 12125 | /        | H501          | RM       | $TODAY()       | $NULL       | $TODAY()       |