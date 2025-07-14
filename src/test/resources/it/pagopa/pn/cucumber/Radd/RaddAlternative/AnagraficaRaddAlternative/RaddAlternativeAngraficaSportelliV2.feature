Feature: Radd Alternative Anagrafica Aggiornata Sportelli



  @raddAnagraficaV2
  Scenario: [RADD_ANAGRAFICA_CRUD_1] inserimento sportello RADD con dati corretti
    When viene generato uno sportello Radd con dati:
      | address_radd_row             | via posto       |
      | address_radd_cap             | 75010           |
      | address_radd_province        | MT              |
      | address_radd_city            | OLIVETO LUCANO  |
      | address_radd_country         | ITALY           |
      | radd_description             | descrizione     |
      | radd_phoneNumber             | +39 9858425136  |
      | radd_geoLocation_latitudine  | 12.0000         |
      | radd_geoLocation_longitudine | 95.0001         |
      | radd_openingTime             | mon=9:00-10:00# |
      | radd_start_validity          | now             |
      | radd_end_validity            | +10g            |
      | radd_externalCode            | testRadd        |
      | radd_capacity                | 100             |
    Then si controlla che il sportello sia in stato "ACCEPTED"

  @raddAnagraficaV2
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_2] inserimento sportello RADD senza campi obbligatori
    When viene generato uno sportello Radd con restituzione errore con dati:
      | address_radd_row      | <via>         |
      | address_radd_cap      | <cap>         |
      | address_radd_province | <provincia>   |
      | address_radd_city     | <citta>       |
      | radd_description      | <descrizione> |
      | radd_phoneNumber      | <telefono>    |
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | via       | cap   | provincia | citta  | descrizione | telefono       |
      | NULL      | 20161 | MI        | MILANO | descrizione | +39 9858425136 |
      | via posto | NULL  | MI        | MILANO | descrizione | +39 9858425136 |
      | via posto | 20161 | NULL      | MILANO | descrizione | +39 9858425136 |
      | via posto | 20161 | MI        | NULL   | descrizione | +39 9858425136 |
      | via posto | 20161 | MI        | MILANO | NULL        | +39 9858425136 |
      | via posto | 20161 | MI        | NULL   | descrizione | NULL           |

  @raddAnagraficaV2
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_3] inserimento sportello RADD con formato campi errato
    When viene generato uno sportello Radd con restituzione errore con dati:
      | address_radd_row             | <via>               |
      | address_radd_cap             | <cap>               |
      | address_radd_province        | <provincia>         |
      | address_radd_city            | <citta>             |
      | address_radd_country         | <stato>             |
      | radd_description             | <descrizione>       |
      | radd_phoneNumber             | <telefono>          |
      | radd_geoLocation_latitudine  | <latitudine>        |
      | radd_geoLocation_longitudine | <longitudine>       |
      | radd_openingTime             | <aperturaSportello> |
      | radd_start_validity          | <startValidity>     |
      | radd_end_validity            | <endValidity>       |
      | radd_capacity                | <capacity>          |
      | radd_externalCode            | <externalCode>      |
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | via       | cap   | provincia | citta  | stato  | descrizione | telefono          | latitudine       | longitudine       | aperturaSportello | startValidity     | endValidity       | capacity         | externalCode |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | ĄŁĽŚŠŞŤŹŽŻASFą˛łľ | 45.0000          | 45.0000           | NULL              | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | ERROR             | 45.0000          | 45.0000           | NULL              | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | ĄŁĽŚŠŞAFŤŹŽŻą˛łľ | 45.0000           | NULL              | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | ĄŁĽŚŠŞŤASFŹŽŻą˛łľ | NULL              | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | 45.0000           | ĄŁĽŚŠŞSAFŤŹŽŻą˛łľ | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | 45.0000           | NULL              | ĄŁĽŚŠŞŤŹASFŽŻą˛łľ | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | 45.0000           | NULL              | NULL              | ĄŁĽŚŠGAfŞŤŹŽŻą˛łľ | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | 45.0000           | NULL              | NULL              | NULL              | ĄŁĽŚAFŠŞŤŹŽŻą˛łľ | NULL         |

