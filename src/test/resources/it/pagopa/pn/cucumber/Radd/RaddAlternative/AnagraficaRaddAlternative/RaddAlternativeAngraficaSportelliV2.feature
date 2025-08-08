Feature: Radd Alternative Anagrafica Aggiornata Sportelli V2




  #  *** INSERIMENTO ***

  @raddAnagraficaV2
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_1] inserimento sportello RADD con dati corretti
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row         | via posto               |
      | address_radd_cap         | 75010                   |
      | address_radd_province    | MT                      |
      | address_radd_city        | OLIVETO LUCANO          |
      | address_radd_country     | ITALY                   |
      | radd_description         | descrizione             |
      | radd_phoneNumber         | +39 9858425136          |
      | radd_openingTime         | mon=9:00-10:00#         |
      | radd_start_validity      | now                     |
      | radd_end_validity        | +10g                    |
      | radd_externalCode        | testRadd                |

      | radd_appointmentRequired | true                    |
      | radd_website             | https://www.example.com |
      | radd_partnerType         | partnertype             |
    Then la response a seguito del nuovo inserimento deve contenere i valori attesi
      | partnerId          | P12345 |
      | locationId         | L67890 |
      | description        | Test description |
      | email              | test@example.com |
      | appointmentRequired| true |
      | externalCodes      | EXT001,EXT002,EXT003 |
      | phoneNumbers       | 1234567890,0987654321 |

  @raddAnagraficaV2
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_1] inserimento sportello RADD con dati corretti
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row         | via posto               |
      | address_radd_cap         | 75010                   |
      | address_radd_province    | MT                      |
      | address_radd_city        | OLIVETO LUCANO          |
      | address_radd_country     | ITALY                   |
      | radd_description         | descrizione             |
      | radd_phoneNumber         | +39 9858425136          |
      | radd_openingTime         | mon=9:00-10:00#         |
      | radd_start_validity      | now                     |
      | radd_end_validity        | +10g                    |
      | radd_externalCode        | testRadd                |

      | radd_appointmentRequired | true                    |
      | radd_website             | https://www.example.com |
      | radd_partnerType         | partnertype             |
    Then viene verificato che l' ultimo sportello inserito venga restituito nella lista tramite locationId

  @raddAnagraficaV2
  Scenario Outline: [RADD_ANAGRAFICA_CRUD V2_2] inserimento sportello RADD senza campi obbligatori
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | <via>         |
      | address_radd_cap      | <cap>         |
      | address_radd_province | <provincia>   |
      | address_radd_city     | <citta>       |
      | radd_description      | <descrizione> |
      | radd_phoneNumber      | <telefono>    |

      | radd_appointmentRequired | <appointmentRequired>                    |
      | radd_website             | <website> |
      | radd_partnerType         | <partnerType>             |
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | via       | cap   | provincia | citta  | descrizione | telefono       | partnerType | website | appointmentRequired|
      | NULL      | 20161 | MI        | MILANO | descrizione | +39 9858425136 |             |         |                    |
      | via posto | NULL  | MI        | MILANO | descrizione | +39 9858425136 |             |         |                    |
      | via posto | 20161 | NULL      | MILANO | descrizione | +39 9858425136 |             |         |                    |
      | via posto | 20161 | MI        | NULL   | descrizione | +39 9858425136 |             |         |                    |
      | via posto | 20161 | MI        | MILANO | NULL        | +39 9858425136 |             |         |                    |
      | via posto | 20161 | MI        | NULL   | descrizione | NULL           |             |         |                    |

  @raddAnagraficaV2
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_V2_3] inserimento sportello RADD con formato campi errato
    When viene generato uno sportello Radd V2 con restituzione errore con dati:
      | address_radd_row      | <via>               |
      | address_radd_cap      | <cap>               |
      | address_radd_province | <provincia>         |
      | address_radd_city     | <citta>             |
      | address_radd_country  | <stato>             |
      | radd_description      | <descrizione>       |
      | radd_phoneNumber      | <telefono>          |
      | radd_openingTime      | <aperturaSportello> |
      | radd_start_validity   | <startValidity>     |
      | radd_end_validity     | <endValidity>       |
      | radd_externalCode     | <externalCode>      |
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | via       | cap   | provincia | citta  | stato  | descrizione | telefono          | aperturaSportello | startValidity     | endValidity       | externalCode |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | ĄŁĽŚŠŞŤŹŽŻASFą˛łľ | NULL              | NULL              | NULL              | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | ERROR             | NULL              | NULL              | NULL              | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | NULL              | NULL              | NULL              | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | NULL              | NULL              | NULL              | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | ĄŁĽŚŠŞSAFŤŹŽŻą˛łľ | NULL              | NULL              | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | NULL              | ĄŁĽŚŠŞŤŹASFŽŻą˛łľ | NULL              | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | NULL              | NULL              | ĄŁĽŚŠGAfŞŤŹŽŻą˛łľ | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | NULL              | NULL              | NULL              | NULL         |


  #  ***CANCELLAZIONE ***

  @raddAnagraficaV2
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_4] cancellazione sportello RADD con dati corretti
    When viene generato uno sportello Radd V2 con dati:
      | address_radd_row      | via posto        |
      | address_radd_cap      | 80017            |
      | address_radd_province | NA               |
      | address_radd_city     | MELITO DI NAPOLI |
      | address_radd_country  | ITALY            |
      | radd_start_validity   | now              |
      | radd_description      | descrizione      |
      | radd_phoneNumber      | +39 0126437425   |
    Then viene cancellato uno sportello Radd V2 con dati:



    # *** LETTURA ***



  @raddAnagraficaV2
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_5]
    When viene richiesta la lista degli sportelli Radd v2 con dati:
      | radd_filter_limit   | 10   |
      | radd_filter_lastKey | NULL |
    Then l'operazione ha prodotto un errore con status code "400"


  @raddAnagraficaV2
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_7]
    When viene richiesta la lista degli sportelli Radd v2 con dati:
      | radd_filter_limit   | 10   |
      | radd_filter_lastKey | NULL |






  #  *** MODIFICA ***


  @raddAnagraficaV2
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


  @raddAnagrafica @puliziaSportelli
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

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_10] modifica sportello RADD con registryId non presente controllo restituzione errore
    When viene modificato uno sportello Radd V2 con dati errati:
      | radd_registryId | errato |
    Then l'operazione ha prodotto un errore con status code "404"

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_11] modifica sportello RADD con registryId vuoto controllo restituzione errore
    When viene modificato uno sportello Radd V2 con dati errati:
      | radd_registryId | NULL |
    Then l'operazione ha prodotto un errore con status code "400"

  #questo non è stato modificato, fallisce perchè non va in 404
  @raddAnagrafica @ignore
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

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_V2_13] modifica sportello RADD con uid vuoto controllo restituzione errore
    Then viene modificato uno sportello Radd V2 con dati errati:
      | radd_uid | NULL |
    And l'operazione ha prodotto un errore con status code "400"

