Feature: Radd Alternative Anagrafica Sportelli

  # [ADEG-RADD-VAL-FIELD-1]
  @raddAnagrafica @raddAlternativeCsv @puliziaSportelliCsv
  Scenario: [RADD_ANAGRAFICA_CSV_1] caricamento CSV con 2 sportelli
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city    | address_radd_country | radd_description | radd_phoneNumber | radd_geoLocation_latitudine | radd_geoLocation_longitudine | radd_openingTime | radd_start_validity | radd_end_validity | radd_capacity | radd_externalCode |
      | via posto        | 30022            | VE                    | CEGGIA               | ITALIA               | test sportelli   | 01-5410951       | 45.0000                     | 42.2412                      | lun=9:00-10:00#  | now                 | +10g              | 10            | testRadd          |
      | vai posto2       | 30023            | VE                    | CONCORDIA SAGITTARIA | ITALIA               | test sportelli   | 01-5245951       | 11.0000                     | 32.1245                      | lun=9:00-10:00#  | now                 | +10g              | 22            | testRadd          |
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   |
      | 0     | ACCEPTED |
      | 1     | ACCEPTED |

  @raddAnagrafica @raddAlternativeCsv @puliziaSportelliCsv
  Scenario: [RADD_ANAGRAFICA_CSV_2] caricamento 2 volte stesso checksum del CSV
    When viene cambiato raddista con "STANDARD"
    Then viene caricato il csv 2 volte con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city | address_radd_country | radd_description | radd_phoneNumber |
      | via posto        | 30020            | VE                    | PONTE CREPALDO    | ITALIA               | test sportelli   | 01-5245951       |
      | vai posto2       | 30020            | VE                    | PORTEGRANDI       | ITALIA               | test sportelli   | 01-5245951       |
    And l'operazione ha prodotto un errore con status code "409"
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   |
      | 0     | ACCEPTED |
      | 1     | ACCEPTED |

  @raddAnagrafica @raddAlternativeCsv @puliziaSportelliCsv
  Scenario: [RADD_ANAGRAFICA_CSV_3] caricamento 2 CSV con il primo CSV con un record in stato PENDING
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city | address_radd_country | radd_description | radd_phoneNumber |
      | via posto        | 74022            | TA                    | FRAGAGNANO        | ITALIA               | test sportelli   | 01-5245951       |
      | vai posto2       | 74025            | TA                    | MARINA DI GINOSA  | ITALIA               | test sportelli   | 01-5245951       |
    Then viene controllato lo stato di caricamento del csv a "PENDING"
    Then viene caricato il csv con formatto "corretto" con restituzione errore con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city | address_radd_country | radd_description | radd_phoneNumber |
      | via posto3       | 74022            | TA                    | FRAGAGNANO        | ITALIA               | test sportelli   | 01-5245951       |
      | vai posto4       | 74025            | TA                    | MARINA DI GINOSA  | ITALIA               | test sportelli   | 01-5245951       |
    And l'operazione ha prodotto un errore con status code "400"
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   |
      | 0     | ACCEPTED |
      | 1     | ACCEPTED |

  #[RADD_ANAGRAFICA_CSV_STATO_2]
  @raddAnagrafica @raddAlternativeCsv @puliziaSportelliCsv
  Scenario: [RADD_ANAGRAFICA_CSV_STATO_1] caricamento CSV verifica stato PENDING
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city | address_radd_country | radd_description | radd_phoneNumber |
      | via posto        | 75010            | MT                    | ALIANO            | ITALIA               | test sportelli   | 01-5245951       |
      | vai posto2       | 75010            | MT                    | CALCIANO          | ITALIA               | test sportelli   | 01-5245951       |
    Then viene controllato lo stato di caricamento del csv a "PENDING"
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   |
      | 0     | ACCEPTED |
      | 1     | ACCEPTED |

  @raddAnagrafica @raddAlternativeCsv
  Scenario: [RADD_ANAGRAFICA_CSV_STATO_3] caricamento CSV con righa malformata verifica stato a REJECTED
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con formatto "errato" con restituzione errore con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city | address_radd_country |
      | via posto        | 75010            | MT                    | GARAGUSO          | ITALIA               |
      | vai posto2       | 75010            | MT                    | GORGOGLIONE       | ITALIA               |
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che il sportello sia in stato "REJECTED"

  @raddAnagrafica @raddAlternativeCsv
  Scenario: [RADD_ANAGRAFICA_CSV_STATO_4] caricamento CSV con campi a null dove c'è obbligatorietà verifica stato a REJECTED
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city | address_radd_country | radd_geoLocation_latitudine | radd_geoLocation_longitudine | radd_description | radd_phoneNumber |
      | NULL             | NULL             | NULL                  | NULL              | NULL                 | NULL                        | 45.0000                      |  cuccumella      | 01-5245951       |
      | NULL             | 75010            | NULL                  | NULL              | NULL                 | 42.0000                     | NULL                         |  cipicipi        | 01-5245951       |
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   |
      | 0     | REJECTED |
      | 1     | REJECTED |

  @raddAnagrafica @raddAlternativeCsv
  Scenario: [RADD_ANAGRAFICA_CSV_STATO_5] caricamento CSV con formato campi errato verifica stato a REJECTED e messaggio di errore
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city | address_radd_country | radd_description | radd_phoneNumber | radd_geoLocation_latitudine | radd_geoLocation_longitudine | radd_openingTime | radd_start_validity | radd_end_validity | radd_capacity | radd_externalCode |
      | ĄŁĽŚŠŞŤŹŽż       | ĄŁĽŚŠŞŤŹŽż       | ĄŁĽŚŠŞŤŹŽż            | ĄŁĽŚŠŞŤŹŽż        | ĄŁĽŚAFŠŞŤŹŽż         | ĄŁĽŚFAŠŞŤŹŽż     | ĄŁĽŚŠŞAFŤŹŽż     | ĄŁĽŚŠŞAFSŤŹŽż               | ĄŁĽŚŠŞŤŹŽż                   | ĄŁĽŚŠŞŤŹŽż       | formato errato      | formato errato    | ĄŁĽŚŠŞŤŹŽż    | ĄŁĽŚŠŞŤŹŽż        |
      | ĄŁ43ŞŤŹŽż        | ĄŁ43ŞŤŹŽż        | ĄŁĽŚWERŹŽż            | 53teŞŤŹŽż         | ĄŁĽFAŚŠŞŤŹŽż         | ĄŁĽŚŠŞŤFŹŽż      | ĄŁĽŚŠŞŤŹŽż       | ĄŁĽŚŠAŞŤŹŽż                 | ĄŁĽŚŠŞŤŹŽż                   | ĄŁĽŚŠŞŤŹŽż       | formato errato      | formato errato    | ĄŁĽŚŠŞŤŹŽż    | ĄŁĽŚŠŞŤŹŽż        |
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   |
      | 0     | REJECTED |
      | 1     | REJECTED |

  @raddAnagrafica
  Scenario Outline: [RADD_ANAGRAFICA_CSV_STATO_6] caricamento CSV verifica stato con requestId non esistente
    When viene eseguita la richiesta per controllo dello stato di caricamento del csv con restituzione errore
      | radd_requestId | <requestId> |
    Then l'operazione ha prodotto un errore con status code "<errore>"
    Examples:
      | requestId | errore |
      | nonEsiste | 404    |
      | NULL      | 400    |

  @raddAnagrafica @raddAlternativeCsv
  Scenario Outline: [ADEG-RADD-VAL-FIELD-2] caricamento CSV contiene solo il campo description o il campo telefono vuoto
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city    | address_radd_country | radd_description  | radd_phoneNumber  | radd_geoLocation_latitudine | radd_geoLocation_longitudine | radd_openingTime | radd_start_validity | radd_end_validity | radd_capacity | radd_externalCode |
      | via posto        | 30022            | VE                    | CEGGIA               | ITALIA               | <raddDescription> | <raddPhoneNumber> | 45.0000                     | 42.2412                      | lun=9:00-10:00#  | now                 | +10g              | 10            | testRadd          |
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   | errorMessage   |
      | 0     | REJECTED | <errorMessage> |
    Examples:
      | raddDescription | raddPhoneNumber | errorMessage                          |
      | null            | 01-5245951      | Il campo descrizione è obbligatorio   |
      | test sportelli  | null            | Il campo telefono è obbligatorio      |

  @raddAnagrafica @raddAlternativeCsv @puliziaSportelliCsv
  Scenario: [ADEG-RADD-VAL-FIELD-3] caricamento CSV contiene solo il campo telefono compilato erroneamente
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city    | address_radd_country | radd_description | radd_phoneNumber  | radd_geoLocation_latitudine | radd_geoLocation_longitudine | radd_openingTime | radd_start_validity | radd_end_validity | radd_capacity | radd_externalCode |
      | via posto        | 30022            | VE                    | CEGGIA               | ITALIA               | test sportelli   | ĄŁĽŚŠŞŤŹŽż              | 45.0000                     | 42.2412                      | lun=9:00-10:00#  | now                 | +10g              | 10            | testRadd          |
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   | errorMessage                                       |
      | 0     | REJECTED | Il campo telefono non rispetta il formato definito |

  @raddAnagrafica @raddAlternativeCsv @puliziaSportelliCsv
  Scenario: [ADEG-RADD-VAL-FIELD-4] caricamento CSV con uno sportello con campi telefono e descrizione vuoti, uno sportello con telefono compilato correttamente e uno sportello compilato correttamente
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city    | address_radd_country | radd_description | radd_phoneNumber  | radd_geoLocation_latitudine | radd_geoLocation_longitudine | radd_openingTime | radd_start_validity | radd_end_validity | radd_capacity | radd_externalCode |
      | via posto        | 30022            | VE                    | CEGGIA               | ITALIA               | NULL             | NULL              | 45.0000                     | 42.2412                      | lun=9:00-10:00#  | now                 | +10g              | 10            | testRadd          |
      | via posto2       | 30022            | VE                    | GORGOGLIONE          | ITALIA               | test sportelli   | error             | 45.0000                     | 42.2412                      | lun=9:00-10:00#  | now                 | +10g              | 10            | testRadd          |
      | via posto3       | 30023            | VE                    | CONCORDIA SAGITTARIA | ITALIA               | test sportelli   | 01-5245951        | 45.0000                     | 42.2412                      | lun=9:00-10:00#  | now                 | +10g              | 10            | testRadd          |
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   | errorMessage                                                          |
      | 0     | REJECTED | Il campo descrizione è obbligatorio, Il campo telefono è obbligatorio |
      | 1     | REJECTED | Il campo telefono non rispetta il formato definito                    |
      | 2     | ACCEPTED | null                                                                  |

  @raddAnagrafica @raddAlternativeCsv @puliziaSportelliCsv
  Scenario: [RADD_ANAGRAFICA_CSV_LISTA_1] caricamento CSV verifica il ricevimento della lista dei sportelli RADD
    When viene cambiato raddista con "STANDARD"
    When viene caricato il csv con dati:
      | address_radd_row | address_radd_cap | address_radd_province | address_radd_city | radd_description | radd_phoneNumber  |
      | via posto        | 75010            | MT                    | GROTTOLE          | test sportelli   | 01-5245951        |
      | via ceggia       | 75010            | MT                    | MIGLIONICO        | test sportelli   | 01-5245951        |
    Then viene controllato lo stato di caricamento del csv a "DONE"
    Then viene richiesta la lista degli sportelli caricati dal csv:
      | radd_requestId    | corretto |
      | radd_filter_limit | 10       |
    Then si controlla che gli sportelli inseriti siano nello status giusto:
      | index | status   |
      | 0     | ACCEPTED |
      | 1     | ACCEPTED |

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CSV_LISTA_2] caricamento CSV verifica il ricevimento errore nella richesta della lista dei sporetelli con requestId non presente
    When viene richiesta la lista degli sportelli caricati dal csv con dati errati:
      | radd_requestId    | 077bdd84-0000-0e0a-8200-ed7124ea8338 |
      | radd_filter_limit | 10                                   |
    Then  controllo che venga restituito vuoto perchè non presente

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CSV_LISTA_3] caricamento CSV verifica il ricevimento errore nella richesta della lista dei sporetelli con requestId vuoto
    When viene richiesta la lista degli sportelli caricati dal csv con dati errati:
      | radd_requestId    | NULL |
      | radd_filter_limit | 10   |
    Then l'operazione ha prodotto un errore con status code "400"

  @raddAnagrafica @puliziaSportelli
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

  @raddAnagrafica
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_2] inserimento sportello RADD senza campi obbligatori
    When viene generato uno sportello Radd con restituzione errore con dati:
      | address_radd_row      | <via>           |
      | address_radd_cap      | <cap>           |
      | address_radd_province | <provincia>     |
      | address_radd_city     | <citta>         |
      | radd_description      | <descrizione>   |
      | radd_phoneNumber      | <telefono>      |
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | via       | cap   | provincia | citta  | descrizione | telefono       |
      | NULL      | 20161 | MI        | MILANO | descrizione | +39 9858425136 |
      | via posto | NULL  | MI        | MILANO | descrizione | +39 9858425136 |
      | via posto | 20161 | NULL      | MILANO | descrizione | +39 9858425136 |
      | via posto | 20161 | MI        | NULL   | descrizione | +39 9858425136 |
      | via posto | 20161 | MI        | MILANO | NULL        | +39 9858425136 |
      | via posto | 20161 | MI        | NULL   | descrizione | NULL           |

  @raddAnagrafica
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_3] inserimento sportello RADD con formato campi errato
    When viene generato uno sportello Radd con restituzione errore con dati:
      | address_radd_row             | <via>                |
      | address_radd_cap             | <cap>                |
      | address_radd_province        | <provincia>          |
      | address_radd_city            | <citta>              |
      | address_radd_country         | <stato>              |
      | radd_description             | <descrizione>        |
      | radd_phoneNumber             | <telefono>           |
      | radd_geoLocation_latitudine  | <latitudine>         |
      | radd_geoLocation_longitudine | <longitudine>        |
      | radd_openingTime             | <apperturaSportello> |
      | radd_start_validity          | <startValidity>      |
      | radd_end_validity            | <endValidity>        |
      | radd_capacity                | <capacity>           |
      | radd_externalCode            | <externalCode>       |
    Then l'operazione ha prodotto un errore con status code "400"
    Examples:
      | via       | cap   | provincia | citta  | stato  | descrizione | telefono          | latitudine       | longitudine       | apperturaSportello | startValidity     | endValidity       | capacity         | externalCode |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | ĄŁĽŚŠŞŤŹŽŻASFą˛łľ | 45.0000          | 45.0000           | NULL               | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | ERROR             | 45.0000          | 45.0000           | NULL               | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | ĄŁĽŚŠŞAFŤŹŽŻą˛łľ | 45.0000           | NULL               | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | ĄŁĽŚŠŞŤASFŹŽŻą˛łľ | NULL               | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | 45.0000           | ĄŁĽŚŠŞSAFŤŹŽŻą˛łľ  | NULL              | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | 45.0000           | NULL               | ĄŁĽŚŠŞŤŹASFŽŻą˛łľ | NULL              | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | 45.0000           | NULL               | NULL              | ĄŁĽŚŠGAfŞŤŹŽŻą˛łľ | NULL             | NULL         |
      | via posto | 20161 | MI        | MILANO | ITALIA | NULL        | NULL              | 45.0000          | 45.0000           | NULL               | NULL              | NULL              | ĄŁĽŚAFŠŞŤŹŽŻą˛łľ | NULL         |

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_4] inserimento sportello RADD con endValidity minore della startValidity
    When viene generato uno sportello Radd con restituzione errore con dati:
      | address_radd_row             | via posto   |
      | address_radd_cap             | 75010       |
      | address_radd_province        | MT          |
      | address_radd_city            | PESCHIERA   |
      | address_radd_country         | ITALY       |
      | radd_description             | descrizione |
      | radd_geoLocation_latitudine  | 15.0000     |
      | radd_geoLocation_longitudine | 67.0000     |
      | radd_openingTime             | NULL        |
      | radd_start_validity          | now         |
      | radd_end_validity            | -20g        |
      | radd_phoneNumber             | 01-5245951  |
    Then l'operazione ha prodotto un errore con status code "400" con messaggio di errore "La data di fine validità non può essere precedente alla data di inizio validità"

  @raddAnagrafica @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_5] inserimento sportello RADD con endValidity a null (controllo manuale che sportello sia utilizzabile )
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto       |
      | address_radd_cap      | 75010           |
      | address_radd_province | MT              |
      | address_radd_city     | SAN MAURO FORTE |
      | address_radd_country  | ITALY           |
      | radd_start_validity   | now             |
      | radd_end_validity     | NULL            |
      | radd_phoneNumber      | 01-5245951      |
      | radd_description      | test sportelli  |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL            |
      | physicalAddress_address      | Via@ok_890      |
      | physicalAddress_municipality | SAN MAURO FORTE |
      | physicalAddress_zip          | 75010           |
      | physicalAddress_province     | MT              |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 755 e il peso di 10 nei details del'elemento di timeline letto

  @raddAnagrafica @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_6] inserimento sportello RADD con startValidity avanti di giorni (controllo manuale che il sportello non sia attivo)
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto   |
      | address_radd_cap      | 80014       |
      | address_radd_province | NA          |
      | address_radd_city     | LAGO PATRIA |
      | address_radd_country  | ITALY       |
      | radd_start_validity   | +10g        |
      | radd_end_validity     | NULL        |
      | radd_phoneNumber      | 01-5245951  |
      | radd_description      | descrizione |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL        |
      | physicalAddress_address      | Via@ok_890  |
      | physicalAddress_municipality | LAGO PATRIA |
      | physicalAddress_zip          | 80014       |
      | physicalAddress_province     | NA          |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 761 e il peso di 20 nei details del'elemento di timeline letto

  @raddAnagrafica @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_7] modifica sportello RADD con dati corretti controllo successo modifica
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto      |
      | address_radd_cap      | 80014          |
      | address_radd_province | NA             |
      | address_radd_city     | VARCATURO      |
      | address_radd_country  | ITALY          |
      | radd_start_validity   | +1g            |
      | radd_description      | descrizione    |
      | radd_phoneNumber      | +39 2445356789 |
      | radd_openingTime      | tue=1:00-2:00# |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene modificato uno sportello Radd con dati:
      | radd_description | descrizione modificata |
      | radd_openingTime | tue=10:00-20:00#       |
      | radd_phoneNumber | +39 9858425136         |

  # questo test non fa quello per cui è stato creato.
  # TODO modificarlo per controllare se effettivamente i campi rimangono uguali
  # non ho trovato un metodo che mi ritorni i dati dello dopo la modifica
  @raddAnagrafica @puliziaSportelli @ignore
  Scenario: [RADD_ANAGRAFICA_CRUD_8] modifica sportello RADD con campi vuoto dove non obbligatorio controllo successo modifica
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto        |
      | address_radd_cap      | 80016            |
      | address_radd_province | NA               |
      | address_radd_city     | MARANO DI NAPOLI |
      | address_radd_country  | ITALY            |
      | radd_description      | descrizione      |
      | radd_phoneNumber      | +39 2445356789   |
      | radd_openingTime      | tue=1:00-2:00#   |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene modificato uno sportello Radd con dati:
      | radd_description | NULL |
      | radd_openingTime | NULL |
      | radd_phoneNumber | NULL |

  @raddAnagrafica @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_9] modifica sportello RADD con formato campi errato controllo restituzione errore
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto      |
      | address_radd_cap      | 80016          |
      | address_radd_province | NA             |
      | address_radd_city     | SAN ROCCO      |
      | address_radd_country  | ITALY          |
      | radd_description      | descrizione    |
      | radd_phoneNumber      | +39 2445356789 |
      | radd_openingTime      | tue=1:00-2:00# |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene modificato uno sportello Radd con dati errati:
      | radd_openingTime | !!"$%&/ASgSG(£%%£%'?^\s# |
      | radd_phoneNumber | !!"$%&/(AGSS£%%£%'?^\s#  |
    And l'operazione ha prodotto un errore con status code "400"

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_10] modifica sportello RADD con registryId non presente controllo restituzione errore
    When viene modificato uno sportello Radd con dati errati:
      | radd_registryId | errato |
    Then l'operazione ha prodotto un errore con status code "404"

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_11] modifica sportello RADD con registryId vuoto controllo restituzione errore
    When viene modificato uno sportello Radd con dati errati:
      | radd_registryId | NULL |
    Then l'operazione ha prodotto un errore con status code "400"

  #questo non è stato modificato, fallisce perchè non va in 404
  @raddAnagrafica @ignore
  Scenario: [RADD_ANAGRAFICA_CRUD_12] modifica sportello RADD con uid non presente controllo restituzione errore  -- non vengono effettuati i controlli
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto      |
      | address_radd_cap      | 80024          |
      | address_radd_province | NA             |
      | address_radd_city     | CARDITELLO     |
      | address_radd_country  | ITALY          |
      | radd_description      | descrizione    |
      | radd_phoneNumber      | +39 2445356789 |
      | radd_openingTime      | tue=1:00-2:00# |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene modificato uno sportello Radd con dati errati:
      | radd_registryId | corretto      |
      | radd_uid        | AJFSAJFOSIJFO |
    And l'operazione ha prodotto un errore con status code "404"

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_13] modifica sportello RADD con uid vuoto controllo restituzione errore
    Then viene modificato uno sportello Radd con dati errati:
      | radd_uid | NULL |
    And l'operazione ha prodotto un errore con status code "400"

  @raddAnagrafica @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_14] modifica sportello RADD con dati corretti ma modifica da diverso operatore RADD
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto           |
      | address_radd_cap      | 80016               |
      | address_radd_province | NA                  |
      | address_radd_city     | TORRE PISCITELLI    |
      | address_radd_country  | ITALY               |
      | radd_description      | descrizione         |
      | radd_phoneNumber      | +39 0126437425      |
      | radd_openingTime      | mon=10:00-13:00#    |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    And viene cambiato raddista con "STANDARD"
    Then viene modificato uno sportello Radd con dati errati:
      | radd_description | descrizione modificata |
      | radd_openingTime | tue=10:00-20:00#       |
      | radd_phoneNumber | +39 0126437425         |
    And l'operazione ha prodotto un errore con status code "404"
    And viene cambiato raddista con "UPLOADER"

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_15] cancellazione sportello RADD con dati corretti
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto           |
      | address_radd_cap      | 80017               |
      | address_radd_province | NA                  |
      | address_radd_city     | MELITO DI NAPOLI    |
      | address_radd_country  | ITALY               |
      | radd_start_validity   | now                 |
      | radd_description      | descrizione         |
      | radd_phoneNumber      | +39 0126437425      |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene cancellato uno sportello Radd con dati:
      | radd_end_validity | now |

  @raddAnagrafica @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_16] cancellazione sportello RADD con endDate < endValidity dello sportello
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto           |
      | address_radd_cap      | 80018               |
      | address_radd_province | NA                  |
      | address_radd_city     | MUGNANO DI NAPOLI   |
      | address_radd_country  | ITALY               |
      | radd_start_validity   | now                 |
      | radd_end_validity     | +10g                |
      | radd_description      | descrizione         |
      | radd_phoneNumber      | +39 0126437425      |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene cancellato uno sportello Radd con dati:
      | radd_end_validity | -10g |


  @raddAnagrafica @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_17] cancellazione sportello RADD con endDate > endValidity dello sportello controllo sportello ancora aperto
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto           |
      | address_radd_cap      | 80019               |
      | address_radd_province | NA                  |
      | address_radd_city     | QUALIANO            |
      | address_radd_country  | ITALY               |
      | radd_start_validity   | now                 |
      | radd_end_validity     | +1g                 |
      | radd_description      | descrizione         |
      | radd_phoneNumber      | 0126437425          |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    And viene cancellato uno sportello Radd con dati:
      | radd_end_validity | +10g |
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL       |
      | physicalAddress_address      | Via@ok_890 |
      | physicalAddress_municipality | QUALIANO   |
      | physicalAddress_zip          | 80019      |
      | physicalAddress_province     | NA         |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    And viene verificato il costo di 755 e il peso di 10 nei details del'elemento di timeline letto


  @raddAnagrafica  @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_18] cancellazione sportello RADD con registryId non presente nella lista degli sportelli
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto           |
      | address_radd_cap      | 80020               |
      | address_radd_province | NA                  |
      | address_radd_city     | CASAVATORE          |
      | address_radd_country  | ITALY               |
      | radd_description      | descrizione         |
      | radd_phoneNumber      | +39 0126437425      |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene cancellato uno sportello Radd con dati errati:
      | radd_registryId | 0_CHAR |
    And l'operazione ha prodotto un errore con status code "400"

  @raddAnagrafica @puliziaSportelli
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_19] cancellazione sportello RADD con controllo campi obbligatori
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | random         |
      | address_radd_cap      | 80020          |
      | address_radd_province | NA             |
      | address_radd_city     | CASAVATORE     |
      | address_radd_country  | ITALY          |
      | radd_phoneNumber      | 01-5245951     |
      | radd_description      | test sportelli |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene cancellato uno sportello Radd con dati errati:
      | radd_end_validity | <endValidity> |
      | radd_registryId   | <registryId>  |
      | radd_uid          | <uid>         |
    And l'operazione ha prodotto un errore con status code "400"
    Examples:
      | endValidity | registryId | uid      |
      | corretto    | corretto   | NULL     |
      | corretto    | NULL       | corretto |
      | NULL        | corretto   | corretto |


  @raddAnagrafica @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_20] cancellazione sportello RADD con dati corretti da diverso operatore RADD
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto   |
      | address_radd_cap      | 80020       |
      | address_radd_province | NA          |
      | address_radd_city     | CRISPANO    |
      | address_radd_country  | ITALY       |
      | radd_phoneNumber      | 01-5245951  |
      | radd_description      | descrizione |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene cambiato raddista con "STANDARD"
    Then viene cancellato uno sportello Radd con dati errati:
      | radd_end_validity | now |
    And l'operazione ha prodotto un errore con status code "404"
    Then viene cambiato raddista con "UPLOADER"


  @raddAnagrafica @puliziaSportelli
  Scenario: [RADD_ANAGRAFICA_CRUD_21] ricevimento lista sportelli del operatore con dati corretti
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto    |
      | address_radd_cap      | 80020        |
      | address_radd_province | NA           |
      | address_radd_city     | FRATTAMINORE |
      | address_radd_country  | ITALY        |
      | radd_externalCode     | testRadd     |
      | radd_phoneNumber      | 01-5245951   |
      | radd_description      | descrizione  |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene richiesta la lista degli sportelli con dati:
      | radd_filter_limit     | 10           |
      | radd_filter_lastKey   | NULL         |
      | address_radd_cap      | 80020        |
      | address_radd_province | NA           |
      | address_radd_city     | FRATTAMINORE |
      | radd_externalCode     | testRadd     |
      | radd_phoneNumber      | 01-5245951   |
      | radd_description      | descrizione  |
    And viene effettuato il controllo se la richiesta ha trovato dei sportelli


  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_22] ricevimento lista sportelli del operatore con limit a null controllo default
    When viene generato uno sportello Radd con dati:
      | address_radd_row      | via posto      |
      | address_radd_cap      | 80020          |
      | address_radd_province | NA             |
      | address_radd_city     | CRISPANO       |
      | address_radd_country  | ITALY          |
      | radd_phoneNumber      | 01-5245951     |
      | radd_description      | test sportelli |
    Then si controlla che il sportello sia in stato "ACCEPTED"
    Then viene richiesta la lista degli sportelli con dati:
      | radd_filter_limit     | NULL           |
      | radd_filter_lastKey   | NULL           |
      | address_radd_cap      | NULL           |
      | address_radd_province | NULL           |
      | address_radd_city     | NULL           |
      | radd_externalCode     | testRadd       |
      | radd_phoneNumber      | 01-5245951     |
      | radd_description      | test sportelli |
    And viene contrallato il numero di sportelli trovati sia uguale a 10

  @raddAnagrafica
  Scenario Outline: [RADD_ANAGRAFICA_CRUD_23] ricevimento lista sportelli del operatore tramite filtro
    Then viene richiesta la lista degli sportelli con dati:
      | radd_filter_limit     | 10             |
      | radd_filter_lastKey   | NULL           |
      | address_radd_cap      | <cap>          |
      | address_radd_province | <provincia>    |
      | address_radd_city     | <citta>        |
      | radd_externalCode     | <externalCode> |
      | radd_phoneNumber      | 01-5245951     |
      | radd_description      | test sportelli |
    Then viene effettuato il controllo se la richiesta ha trovato dei sportelli
    Examples:
      | cap   | provincia | citta          | externalCode |
      | 75010 | NULL      | NULL           | NULL         |
      | NULL  | MT        | NULL           | NULL         |
      | NULL  | NULL      | OLIVETO LUCANO | NULL         |
      | NULL  | NULL      | NULL           | testRadd     |

  @raddAnagrafica
  Scenario: [RADD_ANAGRAFICA_CRUD_24] ricevimento lista vuota dei sportelli del operatore con filtro con valore non presente
    When viene richiesta la lista degli sportelli con dati:
      | radd_filter_limit     | 10                  |
      | radd_filter_lastKey   | NULL                |
      | address_radd_cap      | NULL                |
      | address_radd_province | NULL                |
      | address_radd_city     | NULL                |
      | radd_externalCode     | valore non presente |
    Then viene effettuato il controllo se la richiesta abbia dato lista vuota
