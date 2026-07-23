Feature: test preliminari indicizzazione File safeStorage

  @indicizzazioneSafeStorage
  Scenario Outline: [INDEX_SS_FAIL_AUTH] FAILED AUTHORIZATION - client fittizio e client reale
    When Il client "<client>" tenta di effettuare l'operazione "<operation>" senza essere autorizzato ad accedervi
    Then La chiamata genera un errore con status code 403
    Examples:
      | operation      | client                  |
      | CREATE_FILE    | api-key-non-autorizzata |
      | GET_FILE       | api-key-non-autorizzata |
      | UPDATE_SINGLE  | api-key-non-autorizzata |
      | UPDATE_MASSIVE | api-key-non-autorizzata |
      | GET_TAGS       | api-key-non-autorizzata |
      | SEARCH_FILE    | api-key-non-autorizzata |
      | CREATE_FILE    | internal                |
      | GET_FILE       | internal                |
      | UPDATE_SINGLE  | internal                |
      | UPDATE_MASSIVE | internal                |
      | GET_TAGS       | internal                |
      | SEARCH_FILE    | internal                |

  ########################################################### GET FILE ###################################################################

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_GET_FILE_1] GetFile - SUCCESS
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
      | global_singlevalue:test1      |
    Then Il documento 1 è correttamente formato con la seguente lista di tag
      | global_multivalue:test1,test2 |
      | global_singlevalue:test1      |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  #local_multivalue e local_singlevalue sono tag locali, registrati con il prefisso pn-test~ su pn-SS-IndexingConfiguration
  Scenario: [INDEX_SS_GET_FILE_1_LOCAL] GetFile - SUCCESS (tag locali)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_multivalue:test1,test2 |
      | local_singlevalue:test1      |
    Then Il documento 1 è correttamente formato con la seguente lista di tag
      | local_multivalue:test1,test2 |
      | local_singlevalue:test1      |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  #recIndex è un tag locale, registrato senza il prefisso pn-test~ su pn-SS-IndexingConfiguration
  Scenario: [INDEX_SS_GET_FILE_1_LOCAL_2] GetFile - SUCCESS (tag locali)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | recIndex:test1 |
    Then Il documento 1 è correttamente formato con la seguente lista di tag
      | recIndex:test1 |

  ########################################################### GET TAGS ###################################################################

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_GET_TAGS_1] GetTags SUCCESS
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1,test2,test3 | SET |
      | global_singlevalue:test1            | SET |
    Then Il documento 1 è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test1,test2,test3 |
      | global_singlevalue:test1            |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_GET_TAGS_1_LOCAL] GetTags SUCCESS
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1 secondo le seguenti operazioni
      | pn-test~local_multivalue:test1,test2,test3 | SET |
      | pn-test~local_singlevalue:test1            | SET |
    Then Il documento 1 è stato correttamente modificato con la seguente lista di tag
      | local_multivalue:test1,test2,test3 |
      | local_singlevalue:test1            |

  @pn-16132
  @indicizzazioneSafeStorage
  Scenario: [INDEX_EMPTY_FILE] GetTags SUCCESS
    Given viene caricato un nuovo pdf di 0 byte
    And La chiamata genera un errore con status code 422
    And Il messaggio di errore riporta la dicitura "Empty or invalid file"

  @pn-16132
  @indicizzazioneSafeStorage
  Scenario: [INDEX_EMPTY_FILE] GetTags SUCCESS
    Given viene caricato un nuovo pdf di 0 byte
    And La chiamata genera un errore con status code 422
    And Il messaggio di errore riporta la dicitura "Empty or invalid file"

  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_GET_TAGS_2] GetTags SUCCESS Empty Result
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    Then Il documento 1 è stato correttamente modificato con la seguente lista di tag
      | null |

  ########################################################### CREATE FILE ###################################################################

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_1] Create - SUCCESS
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
      | global_singlevalue:test1      |
    Then Il documento 1 è associato alla seguente lista di tag
      | global_multivalue:test1,test2 |
      | global_singlevalue:test1      |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_1_LOCAL] Create - SUCCESS
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_multivalue:test1,test2 |
      | local_singlevalue:test1      |
    Then Il documento 1 è associato alla seguente lista di tag
      | local_multivalue:test1,test2 |
      | local_singlevalue:test1      |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [VALIDATION_BUG_20667] Creazione con successo di un documento con document type avante un nome libero, che non inizia per forza con "PN_"
    Given Viene caricato un nuovo documento di tipo "DOCUMENT_WITH_NO_PN_PREFIX" con tag associati
      | local_multivalue:test1,test2 |
      | local_singlevalue:test1      |
    Then Il documento 1 è associato alla seguente lista di tag
      | local_multivalue:test1,test2 |
      | local_singlevalue:test1      |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_WITH_DATE] Create - SUCCESS (tag contenente data)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | tagWithDate |
    Then Il documento 1 è associato alla seguente lista di tag
      | tagWithDate |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_2] Create - ERROR Trasformazione
    Given Viene caricato un nuovo documento di tipo "PN_LEGAL_FACTS_ST" con tag associati
      | global_multivalue:test1 |
    Then Il documento 1 è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test1 |
    And Il documento 1 è correttamente formato con la seguente lista di tag
      | global_multivalue:test1 |
    And La chiamata genera un errore con status code 404
    And Il messaggio di errore riporta la dicitura "Document is missing from bucket"


  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_4] Create ERROR - MaxFileKeys
    Given esiste un limite "maxFileKeys" con valore pari a 1000
    And vengono caricati documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" in numero "PARI" a "maxFileKeys" con tag associati "global_indexed_singlevalue:test"
    When vengono caricati documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" in numero "PARI" a "1" con tag associati "global_indexed_singlevalue:test"
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxFileKeys' reached"


  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_7] Create ERROR - MaxValuesPerTagPerRequest
    Given esiste un limite "maxValuesPerTagPerRequest" con valore pari a 100
    When vengono caricati documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" in numero "PARI" a "1" con associato il tag "global_multivalue" avente 101 valori diversi
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxValuesPerTagPerRequest' reached"

  ########################################################### UPDATE SINGLE ###################################################################

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_1] UpdateSingle SUCCESS - solo operazioni SET
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test | SET |
    Then Il documento 1 è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_1_LOCAL] UpdateSingle SUCCESS - solo operazioni SET
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1 secondo le seguenti operazioni
      | pn-test~local_multivalue:test | SET |
    Then Il documento 1 è stato correttamente modificato con la seguente lista di tag
      | local_multivalue:test |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_2] UpdateSingle SUCCESS - solo operazioni DELETE (PARZIALE)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test2 | DELETE |
    Then Il documento 1 è associato alla seguente lista di tag
      | global_multivalue:test1 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_2_LOCAL] UpdateSingle SUCCESS - solo operazioni DELETE (PARZIALE)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_multivalue:test1,test2 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | pn-test~local_multivalue:test2 | DELETE |
    Then Il documento 1 è associato alla seguente lista di tag
      | local_multivalue:test1 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_3] UpdateSingle SUCCESS - solo operazioni DELETE (TOTALE)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1,test2 | DELETE |
    Then Il documento 1 è associato alla seguente lista di tag
      | null |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_3_LOCAL] UpdateSingle SUCCESS - solo operazioni DELETE (TOTALE)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_multivalue:test1,test2 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | pn-test~local_multivalue:test1,test2 | DELETE |
    Then Il documento 1 è associato alla seguente lista di tag
      | null |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_4] UpdateSingle SUCCESS - solo operazioni DELETE (ININFLUENTE)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test2 | DELETE |
    Then Il documento 1 è associato alla seguente lista di tag
      | global_multivalue:test1 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_5] UpdateSingle SUCCESS - operazioni SET+DELETE
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test  | DELETE |
      | global_singlevalue:test | SET    |
    Then Il documento 1 è associato alla seguente lista di tag
      | global_singlevalue:test |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_6] UpdateSingle ERROR - Set+Delete sullo stesso tag
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test2 | SET    |
      | global_multivalue:test1 | DELETE |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "SET and DELETE cannot contain the same tags: [global_multivalue]"

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_6_LOCAL] UpdateSingle ERROR - Set+Delete sullo stesso tag
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_multivalue:test1 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | pn-test~local_multivalue:test2 | SET    |
      | pn-test~local_multivalue:test1 | DELETE |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "SET and DELETE cannot contain the same tags: [pn-test~local_multivalue]"

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_9] UpdateSingle ERROR - MaxValuesPerTagDocument
    Given esiste un limite "maxValuesPerTagDocument" con valore pari a 1000
    And esiste un limite "maxValuesPerTagPerRequest" con valore pari a 100
    And Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    And il documento viene aggiornato aggiungendo "maxValuesPerTagPerRequest" valori per volta al tag "global_multivalue", fino a raggiungere il limite di "maxValuesPerTagDocument"
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:testOltreLimite | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxValuesPerTagDocument' reached"

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_11] UpdateSingle ERROR - MaxValuesPerTagPerRequest
    Given esiste un limite "maxValuesPerTagPerRequest" con valore pari a 100
    And Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1 associando valori a un singolo tag in numero "SUPERIORE" a "maxValuesPerTagPerRequest"
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Number of values for tag global_multivalue exceeds maxValues limit"

  ########################################################### UPDATE MASSIVE ###################################################################

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_1] Update Massive SUCCESS - solo operazioni SET
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modificano i documenti secondo le seguenti operazioni
      | tag                      | documentIndex | operation |
      | global_multivalue:test1  | 1             | SET       |
      | global_singlevalue:test1 | 1             | SET       |
      | global_multivalue:test2  | 2             | SET       |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | global_multivalue:test1  |
      | global_singlevalue:test1 |
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test2 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_1_LOCAL] Update Massive SUCCESS - solo operazioni SET
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modificano i documenti secondo le seguenti operazioni
      | tag                             | documentIndex | operation |
      | pn-test~local_multivalue:test1  | 1             | SET       |
      | pn-test~local_singlevalue:test1 | 1             | SET       |
      | pn-test~local_multivalue:test2  | 2             | SET       |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | local_multivalue:test1  |
      | local_singlevalue:test1 |
    And Il documento 2 è associato alla seguente lista di tag
      | local_multivalue:test2 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_2] Update Massive SUCCESS - solo operazioni DELETE (PARZIALE)
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
    When Si modificano i documenti secondo le seguenti operazioni
      | tag                     | documentIndex | operation |
      | global_multivalue:test1 | 1             | DELETE    |
      | global_multivalue:test2 | 2             | DELETE    |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | global_multivalue:test2 |
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1 |
    And Il documento 1 non contiene la seguente lista di tag
      | global_multivalue:test1 |
    And Il documento 2 non contiene la seguente lista di tag
      | global_multivalue:test2 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_2_LOCAL] Update Massive SUCCESS - solo operazioni DELETE (PARZIALE)
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | pn-test~local_multivalue:test1,test2 |
    When Si modificano i documenti secondo le seguenti operazioni
      | tag                            | documentIndex | operation |
      | pn-test~local_multivalue:test1 | 1             | DELETE    |
      | pn-test~local_multivalue:test2 | 2             | DELETE    |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | local_multivalue:test2 |
    And Il documento 2 è associato alla seguente lista di tag
      | local_multivalue:test1 |
    And Il documento 1 non contiene la seguente lista di tag
      | local_multivalue:test1 |
    And Il documento 2 non contiene la seguente lista di tag
      | local_multivalue:test2 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_3] Update Massive SUCCESS - solo operazioni DELETE (TOTALE)
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
      | global_singlevalue:test1      |
    When Si modificano i documenti secondo le seguenti operazioni
      | tag                           | documentIndex | operation |
      | global_multivalue:test1,test2 | 1             | DELETE    |
      | global_multivalue:test1,test2 | 2             | DELETE    |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | global_singlevalue:test1 |
    And Il documento 2 è associato alla seguente lista di tag
      | global_singlevalue:test1 |
    And Il documento 1 non contiene la seguente lista di tag
      | global_multivalue:test1,test2 |
    And Il documento 2 non contiene la seguente lista di tag
      | global_multivalue:test1,test2 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_3_LOCAL] Update Massive SUCCESS - solo operazioni DELETE (TOTALE)
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_multivalue:test1,test2 |
      | local_singlevalue:test1      |
    When Si modificano i documenti secondo le seguenti operazioni
      | tag                                  | documentIndex | operation |
      | pn-test~local_multivalue:test1,test2 | 1             | DELETE    |
      | pn-test~local_multivalue:test1,test2 | 2             | DELETE    |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | local_singlevalue:test1 |
    And Il documento 2 è associato alla seguente lista di tag
      | local_singlevalue:test1 |
    And Il documento 1 non contiene la seguente lista di tag
      | local_multivalue:test1,test2 |
    And Il documento 2 non contiene la seguente lista di tag
      | local_multivalue:test1,test2 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_4] Update Massive SUCCESS - solo operazioni DELETE (ININFLUENTE)
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
    When Si modificano i documenti secondo le seguenti operazioni
      | tag                     | documentIndex | operation |
      | global_multivalue:test3 | 1             | DELETE    |
      | global_multivalue:test3 | 2             | DELETE    |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | global_multivalue:test1,test2 |
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1,test2 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_4_LOCAL] Update Massive SUCCESS - solo operazioni DELETE (ININFLUENTE)
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_multivalue:test1,test2 |
    When Si modificano i documenti secondo le seguenti operazioni
      | tag                            | documentIndex | operation |
      | pn-test~local_multivalue:test3 | 1             | DELETE    |
      | pn-test~local_multivalue:test3 | 2             | DELETE    |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | local_multivalue:test1,test2 |
    And Il documento 2 è associato alla seguente lista di tag
      | local_multivalue:test1,test2 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_5] Update Massive SUCCESS - operazioni SET+DELETE
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
      | global_singlevalue:test1      |
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                     | documentIndex |
      | DELETE    | global_multivalue:test2 | 1             |
      | DELETE    | global_multivalue:test2 | 2             |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | global_multivalue:test1  |
      | global_singlevalue:test1 |
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1  |
      | global_singlevalue:test1 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_5_LOCAL] Update Massive SUCCESS - operazioni SET+DELETE
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_multivalue:test1,test2 |
      | local_singlevalue:test1      |
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                            | documentIndex |
      | DELETE    | pn-test~local_multivalue:test2 | 1             |
      | DELETE    | pn-test~local_multivalue:test2 | 2             |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | local_multivalue:test1  |
      | local_singlevalue:test1 |
    And Il documento 2 è associato alla seguente lista di tag
      | local_multivalue:test1  |
      | local_singlevalue:test1 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_6] Update Massive ERROR - File key ripetuta
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
      | global_singlevalue:test1      |
    When La request presenta una ripetizione della stessa fileKey
      | operation | tag                      | documentIndex |
      | SET       | global_multivalue:test3  | 1             |
      | DELETE    | global_singlevalue:test1 | 1             |
      | DELETE    | global_multivalue:test2  | 2             |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Duplicate fileKey found:"

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_7] Update Massive ERROR - Set+Delete sullo stesso tag
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
      | global_singlevalue:test1      |
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                     | documentIndex |
      | SET       | global_multivalue:test3 | 1             |
      | DELETE    | global_multivalue:test2 | 1             |
      | DELETE    | global_multivalue:test2 | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori "400.00" riportanti la dicitura "SET and DELETE cannot contain the same tags: [global_multivalue]" riguardanti il documento 1
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1  |
      | global_singlevalue:test1 |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_8] Update Massive ERROR - MaxFileKeysUpdateMassivePerRequest
    Given esiste un limite "maxFileKeysUpdateMassivePerRequest" con valore pari a 100
    And vengono caricati documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" in numero "SUPERIORE" a "maxFileKeysUpdateMassivePerRequest"
    When tali documenti vengono modificati simultaneamente associando a ciascuno il tag "global_multivalue"
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Number of documents to update exceeds MaxFileKeysUpdateMassivePerRequest limit."

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_11] Update Massive ERROR - MaxValuesPerTagDocument
    Given esiste un limite "maxValuesPerTagDocument" con valore pari a 1000
    And esiste un limite "maxValuesPerTagPerRequest" con valore pari a 100
    And Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    And i documenti vengono aggiornati aggiungendo "maxValuesPerTagPerRequest" valori per volta al tag "global_multivalue", fino a raggiungere il limite di "maxValuesPerTagDocument"
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                               | documentIndex |
      | SET       | global_multivalue:testOltreLimite | 1             |
      | SET       | global_singlevalue:test1          | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori "400.00" riportanti la dicitura "Limit 'MaxValuesPerTagDocument' reached" riguardanti il documento 1

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_13] Update Massive ERROR - MaxValuesPerTagPerRequest
    Given esiste un limite "maxValuesPerTagPerRequest" con valore pari a 100
    And Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When i documenti vengono modificati associando al primo il tag "global_multivalue" con un numero di valori "SUPERIORE" a "maxValuesPerTagPerRequest", mentre al secondo un solo valore
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori "400.00" riportanti la dicitura "Number of values for tag global_multivalue exceeds maxValues limit" riguardanti il documento 1

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_14] Update Massive ERROR - tutte le filekeys inesistenti
    Given si prova a fare l'update di 2 documenti inesistenti secondo le seguenti operazioni
      | operation | tag                      | documentIndex |
      | SET       | global_singlevalue:test1 | 1             |
      | SET       | global_singlevalue:test2 | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori "500.00" riportanti la dicitura "Document key not present in DB" riguardanti il documento 1
    And La response contiene uno o più errori "500.00" riportanti la dicitura "Document key not present in DB" riguardanti il documento 2

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_15] Update Massive ERROR - due filekeys esistenti e due filekeys inesistenti
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When si prova a fare l'update dei documenti creati e di 2 documenti inesistenti secondo le seguenti operazioni
      | tag                      | documentIndex | operation |
      | global_singlevalue:test1 | 1             | SET       |
      | global_singlevalue:test2 | 2             | SET       |
      | global_singlevalue:test3 | 3             | SET       |
      | global_singlevalue:test4 | 4             | SET       |
    Then L'update massivo va in successo con stato 200
    And Il documento 1 è associato alla seguente lista di tag
      | global_singlevalue:test1 |
    And Il documento 2 è associato alla seguente lista di tag
      | global_singlevalue:test2 |
    And La response contiene uno o più errori "500.00" riportanti la dicitura "Document key not present in DB" riguardanti il documento 3
    And La response contiene uno o più errori "500.00" riportanti la dicitura "Document key not present in DB" riguardanti il documento 4

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_16] Update Massive ERROR - tag inesistente
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                                  | documentIndex |
      | SET       | global_singlevalue:test1             | 1             |
      | SET       | global_singlevalue_inesistente:test2 | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori "400.00" riportanti la dicitura "Tag 'global_singlevalue_inesistente' not found in the indexing configuration" riguardanti il documento 2
    And Il documento 1 è associato alla seguente lista di tag
      | global_singlevalue:test1 |
    And Il documento 2 è associato alla seguente lista di tag
      | null |

  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_17] Update Massive ERROR - tutti i tag inesistenti
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                                  | documentIndex |
      | SET       | global_singlevalue_inesistente:test1 | 1             |
      | SET       | global_singlevalue_inesistente:test2 | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori "400.00" riportanti la dicitura "Tag 'global_singlevalue_inesistente' not found in the indexing configuration" riguardanti il documento 1
    And La response contiene uno o più errori "400.00" riportanti la dicitura "Tag 'global_singlevalue_inesistente' not found in the indexing configuration" riguardanti il documento 2
    And Il documento 1 è associato alla seguente lista di tag
      | null |
    And Il documento 2 è associato alla seguente lista di tag
      | null |

  ########################################################### SEARCH FILE-KEY ###################################################################

  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_SEARCH_1] SEARCH ERROR - MaxMapValuesForSearch
    Given esiste un limite "maxMapValuesForSearch" con valore pari a 10
    When Vengono ricercate con logica "" delle fileKey impostando come filtro di ricerca un numero di tags "SUPERIORE" a "maxMapValuesForSearch"
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxMapValuesForSearch' reached"

  @aggiuntaTag
    @concurrencyIndexSs
    @indicizzazioneSafeStorage
  Scenario Outline: [INDEX_SS_SEARCH_2] SEARCH SUCCESS: Empty Result
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_multivalue:test1,test2 |
      | global_indexed_singlevalue:test1      |
    When Vengono ricercate con logica "<logic>" le fileKey aventi i seguenti tag
      | global_indexed_multivalue:testEmpty  |
      | global_indexed_singlevalue:testEmpty |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | null |
    Examples:
      | logic |
      | and   |
      | or    |
      |       |

  @aggiuntaTag
    @concurrencyIndexSs
    @indicizzazioneSafeStorage
  Scenario Outline: [INDEX_SS_SEARCH_2_LOCAL] SEARCH SUCCESS: Empty Result
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_indexed_multivalue:test1,test2 |
      | local_indexed_singlevalue:test1      |
    When Vengono ricercate con logica "<logic>" le fileKey aventi i seguenti tag
      | local_indexed_multivalue:testEmpty  |
      | local_indexed_singlevalue:testEmpty |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | null |
    Examples:
      | logic |
      | and   |
      | or    |
      |       |


  @aggiuntaTag
    @concurrencyIndexSs
    @indicizzazioneSafeStorage
  Scenario Outline: [INDEX_SS_SEARCH_3] SEARCH ERROR: 0 parametri tag
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_multivalue:test1,test2 |
      | global_indexed_singlevalue:test1      |
    When Vengono ricercate con logica "<logic>" le fileKey aventi i seguenti tag
      | null |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "No tag params provided. At least one tag key-value pair is required"
    Examples:
      | logic |
      | and   |
      | or    |
      |       |

  @aggiuntaTag
    @concurrencyIndexSs
    @indicizzazioneSafeStorage
  Scenario Outline: [INDEX_SS_SEARCH_4] SEARCH SUCCESS: 1 parametro tag
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_multivalue:testSearch1param1,testSearch1param2 |
      | global_indexed_singlevalue:testSearch1param1                  |
    When Vengono ricercate con logica "<logic>" le fileKey aventi i seguenti tag
      | global_indexed_multivalue:testSearch1param1 |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | 1 |
      | 2 |
    Examples:
      | logic |
      | and   |
      | or    |
      |       |

  @aggiuntaTag
    @concurrencyIndexSs
    @indicizzazioneSafeStorage
  Scenario Outline: [INDEX_SS_SEARCH_5] SEARCH SUCCESS: multipli parametri tag (logic and o null)
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_multivalue:testMultipleParam1,testMultipleParam2 |
      | global_indexed_singlevalue:testMultipleParam1                   |
    When Vengono ricercate con logica "<logic>" le fileKey aventi i seguenti tag
      | global_indexed_multivalue:testMultipleParam1  |
      | global_indexed_singlevalue:testMultipleParam1 |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | 1 |
      | 2 |
    Examples:
      | logic |
      | and   |
      |       |

  @aggiuntaTag
    @concurrencyIndexSs
    @indicizzazioneSafeStorage
  Scenario Outline: [INDEX_SS_SEARCH_5_LOCAL] SEARCH SUCCESS: multipli parametri tag (logic and o null)
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_indexed_multivalue:testMultipleParam1,testMultipleParam2 |
      | local_indexed_singlevalue:testMultipleParam1                   |
    When Vengono ricercate con logica "<logic>" le fileKey aventi i seguenti tag
      | pn-test~local_indexed_multivalue:testMultipleParam1  |
      | pn-test~local_indexed_singlevalue:testMultipleParam1 |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | 1 |
      | 2 |
    Examples:
      | logic |
      | and   |
      |       |

  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_SEARCH_6] SEARCH SUCCESS: multipli parametri tag globali (logic or)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_multivalue:testOrParam1,testOrParam2 |
    And Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_singlevalue:testOrParam1 |
    When Vengono ricercate con logica "or" le fileKey aventi i seguenti tag
      | global_indexed_multivalue:testOrParam1  |
      | global_indexed_singlevalue:testOrParam1 |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | 1 |
      | 2 |

  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_SEARCH_6_LOCAL] SEARCH SUCCESS: multipli parametri tag locali (logic or)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_indexed_multivalue:testOrParam1,testOrParam2 |
    And Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_indexed_singlevalue:testOrParam1 |
    When Vengono ricercate con logica "or" le fileKey aventi i seguenti tag
      | pn-test~local_indexed_multivalue:testOrParam1  |
      | pn-test~local_indexed_singlevalue:testOrParam1 |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | 1 |
      | 2 |

  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_SEARCH_6_LOCAL_2] SEARCH SUCCESS: multipli parametri tag (uno locale, uno globale) (logic or)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | local_indexed_singlevalue:testOrParam1 |
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_singlevalue:testOrParam1 |
    When Vengono ricercate con logica "or" le fileKey aventi i seguenti tag
      | local_indexed_singlevalue:testOrParam1  |
      | global_indexed_singlevalue:testOrParam1 |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | 1 |
      | 2 |

  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_SEARCH_7] SEARCH FAIL: la ricerca di un tag globale NON indicizzato non deve produrre risultati (logic or)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_singlevalue:testOrParam1 |
    When Vengono ricercate con logica "or" le fileKey aventi i seguenti tag
      | global_singlevalue:testOrParam1 |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | null |

  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_SEARCH_7_LOCAL] SEARCH FAIL: la ricerca di un tag locale NON indicizzato non deve produrre risultati (logic or)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | recIndex:testOrParam1 |
    When Vengono ricercate con logica "or" le fileKey aventi i seguenti tag
      | recIndex:testOrParam1 |
    Then Il risultato della search contiene le fileKey relative ai seguenti documenti
      | null |