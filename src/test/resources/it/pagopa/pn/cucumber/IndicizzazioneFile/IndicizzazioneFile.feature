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
  Scenario: [INDEX_SS_CREATE_2] Create - ERROR Trasformazione
    Given Viene caricato un nuovo documento di tipo "PN_LEGAL_FACTS_ST" con tag associati
      | global_multivalue:test1 |
    Then Il documento 1 è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test1 |
    And Il documento 1 è correttamente formato con la seguente lista di tag
      | global_multivalue:test1 |
    And La chiamata genera un errore con status code 404
    And Il messaggio di errore riporta la dicitura "Document is missing from bucket"

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_3] Create ERROR - MaxTagsPerRequest
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2           |
      | global_indexed_multivalue:test1,test2   |
      | global_singlevalue:test1                |
      | global_indexed_singlevalue:test1        |
      | pn-test~local_multivalue:test1,test2    |
      | pn-test~local_singlevalue:test1         |
      | pn-test~local_indexed_singlevalue:test1 |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxTagsPerRequest' reached"

  @testIndexSS
  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_4] Create ERROR - MaxFileKeys
    Given Vengono caricati 5 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    And I primi 5 documenti vengono modificati secondo le seguenti operazioni
      | global_indexed_multivalue:test | SET |
    When Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_multivalue:test |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxFileKeys' reached. Current value: 6. Max value: 5"

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_5] Create ERROR - MaxValuesPerTagDocument
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2,test3,test4,test5,test6 |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxValuesPerTagDocument' reached."

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_6] Create ERROR - MaxTagsPerDocument
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2         |
      | global_indexed_multivalue:test1,test2 |
      | global_singlevalue:test1              |
      | global_indexed_singlevalue:test1      |
      | pn-test~local_multivalue:test1,test2  |
      | pn-test~local_singlevalue:test1       |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxTagsPerDocument' reached"

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_7] Create ERROR - MaxValuesPerTagPerRequest
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2,test3,test4,test5,test6, test7 |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxValuesPerTagPerRequest' reached"

  @uatIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_CREATE_7.2] Create ERROR - MaxValuesPerTagPerRequest
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con un tag avente 101 valori associati
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
  Scenario: [INDEX_SS_UPDATE_SINGLE_2] UpdateSingle SUCCESS - solo operazioni DELETE (PARZIALE)
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test2 | DELETE |
    Then Il documento 1 è associato alla seguente lista di tag
      | global_multivalue:test1 |

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

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_7] UpdateSingle ERROR - MaxFileKeys
    Given Vengono caricati 6 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    And I primi 5 documenti vengono modificati secondo le seguenti operazioni
      | global_indexed_multivalue:test | SET |
    When Si modifica il documento 6 secondo le seguenti operazioni
      | global_indexed_multivalue:test | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxFileKeys' reached. Current value: 6. Max value: 5"

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_8] UpdateSingle ERROR - MaxOperationsOnTagsPerRequest
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1          | DELETE |
      | global_indexed_multivalue:test2  | SET    |
      | global_singlevalue:test3         | SET    |
      | global_indexed_singlevalue:test4 | SET    |
      | pn-test~local_multivalue:test5   | SET    |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Number of tags to update exceeds maxOperationsOnTags limit"

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_9] UpdateSingle ERROR - MaxValuesPerTagDocument
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2,test3 |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test4,test5,test6 | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxValuesPerTagDocument' reached. Current value: 6. Max value: 5"

  @uatIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_9.1] UpdateSingle ERROR - MaxValuesPerTagDocument
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    And gli si associano 1000 valori diversi a un singolo tag
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1001 | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxValuesPerTagDocument' reached. Current value: 1001. Max value: 1000"

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_10] UpdateSingle ERROR - MaxTagsPerDocument
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1        | SET |
      | global_singlevalue:test1       | SET |
      | pn-test~local_multivalue:test1 | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxTagsPerDocument' reached. Current value: 3. Max value: 2"

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_11] UpdateSingle ERROR - MaxValuesPerTagPerRequest
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1,test2,test3,test4,test5,test6, test7 | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Number of values for tag global_multivalue exceeds maxValues limit"

  @uatIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_SINGLE_11.2] UpdateSingle ERROR - MaxValuesPerTagPerRequest
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1 associando 101 valori a un singolo tag
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
    And La response contiene uno o più errori riportanti la dicitura "SET and DELETE cannot contain the same tags: [global_multivalue]" riguardanti il documento 1
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1  |
      | global_singlevalue:test1 |

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_8] Update Massive ERROR - MaxFileKeysUpdateMassivePerRequest
    Given Vengono caricati 6 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                     | documentIndex |
      | SET       | global_multivalue:test1 | 1             |
      | SET       | global_multivalue:test1 | 2             |
      | SET       | global_multivalue:test1 | 3             |
      | SET       | global_multivalue:test1 | 4             |
      | SET       | global_multivalue:test1 | 5             |
      | SET       | global_multivalue:test1 | 6             |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Number of documents to update exceeds MaxFileKeysUpdateMassivePerRequest limit."

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_9] Update Massive ERROR - MaxOperationsOnTagsPerRequest
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1 |
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                              | documentIndex |
      | DELETE    | global_multivalue:test1          | 1             |
      | SET       | global_indexed_multivalue:test2  | 1             |
      | SET       | global_singlevalue:test3         | 1             |
      | SET       | global_indexed_singlevalue:test4 | 1             |
      | SET       | pn-test~local_multivalue:test5   | 1             |
      | SET       | global_singlevalue:test6         | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori riportanti la dicitura "Number of tags to update exceeds maxOperationsOnTags limit" riguardanti il documento 1
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1  |
      | global_singlevalue:test6 |

  @testIndexSS
  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_10] Update Massive ERROR - MaxFileKeys
    Given Sul DB non è presente nessun documento con associato il tag "global_indexed_multivalue:test"
    And Vengono caricati 5 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_multivalue:test |
    And Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                            | documentIndex |
      | SET       | global_indexed_multivalue:test | 6             |
      | SET       | global_multivalue:test1        | 1             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori riportanti la dicitura "Limit 'MaxFileKeys' reached. Current value: 6. Max value: 5" riguardanti il documento 6
    And Il documento 1 è associato alla seguente lista di tag
      | global_indexed_multivalue:test |
      | global_multivalue:test1        |

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_11] Update Massive ERROR - MaxValuesPerTagDocument
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2,test3,test4 |
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                           | documentIndex |
      | SET       | global_multivalue:test5       | 1             |
      | SET       | global_multivalue:test5,test6 | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori riportanti la dicitura "Limit 'MaxValuesPerTagDocument' reached. Current value: 6. Max value: 5" riguardanti il documento 2
    And Il documento 1 è associato alla seguente lista di tag
      | global_multivalue:test1,test2,test3,test4,test5 |

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_12] Update Massive ERROR - MaxTagsPerDocument
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1 |
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                            | documentIndex |
      | SET       | global_singlevalue:test1       | 1             |
      | SET       | pn-test~local_multivalue:test1 | 1             |
      | SET       | global_singlevalue:test1       | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori riportanti la dicitura "Limit 'MaxTagsPerDocument' reached. Current value: 3. Max value: 2" riguardanti il documento 1
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1  |
      | global_singlevalue:test1 |

  @testIndexSS
  @aggiuntaTag
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_UPDATE_MASSIVE_13] Update Massive ERROR - MaxValuesPerTagPerRequest
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                                                         | documentIndex |
      | SET       | global_multivalue:test1,test2,test3,test4,test5,test6,test7 | 1             |
      | SET       | global_multivalue:test1                                     | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori riportanti la dicitura "Number of values for tag global_multivalue exceeds maxValues limit" riguardanti il documento 1
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1 |

  ########################################################### SEARCH FILE-KEY ###################################################################

  @testIndexSS
  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorage
  Scenario: [INDEX_SS_SEARCH_1] SEARCH ERROR - MaxMapValuesForSearch
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_multivalue:test1,test2 |
      | global_indexed_singlevalue:test1      |
    When Vengono ricercate con logica "" le fileKey aventi i seguenti tag
      | global_indexed_multivalue:testEmpty         |
      | global_multivalue:testEmpty                 |
      | global_indexed_singlevalue:testEmpty        |
      | global_singlevalue:testEmpty                |
      | pn-test~local_indexed_multivalue:testEmpty  |
      | pn-test~local_indexed_singlevalue:testEmpty |
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
  Scenario: [INDEX_SS_SEARCH_6] SEARCH SUCCESS: multipli parametri tag (logic or)
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