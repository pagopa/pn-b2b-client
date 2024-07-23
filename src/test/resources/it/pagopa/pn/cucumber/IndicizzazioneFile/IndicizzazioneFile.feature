Feature: test preliminari indicizzazione File safeStorage

  Scenario Outline: FAILED AUTHORIZATION
    When L'utente tenta di effettuare l'operazione "<operation>" senza essere autorizzato ad accedervi
    Then La chiamata genera un errore con status code 403
    Examples:
      | operation      |
      | CREATE_FILE    |
      | GET_FILE       |
      | UPDATE_SINGLE  |
      | UPDATE_MASSIVE |
      | GET_TAGS       |
      | SEARCH_FILE    |

  @aggiuntaTag
  Scenario: UpdateSingle SUCCESS - solo operazioni SET
    Given Viene caricato un nuovo documento pdf
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test | SET |
    Then Il documento è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test |

  @aggiuntaTag
  Scenario: UpdateSingle SUCCESS - solo operazioni DELETE 1
    Given Viene caricato un nuovo documento pdf
    And Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1,test2 | SET |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1 | DELETE |
    Then Il documento è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test2 |

  @aggiuntaTag
  Scenario: UpdateSingle SUCCESS - solo operazioni DELETE 2
    Given Viene caricato un nuovo documento pdf
    And Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1  | SET |
      | global_singlevalue:test1 | SET |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1 | DELETE |
    Then Il documento è stato correttamente modificato con la seguente lista di tag
      | global_singlevalue:test1 |

  @aggiuntaTag
  Scenario: UpdateSingle SUCCESS - solo operazioni DELETE 3
    Given Viene caricato un nuovo documento pdf
    And Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1 | SET |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test2 | DELETE |
    Then Il documento è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test1 |

  @aggiuntaTag
  Scenario: UpdateSingle SUCCESS - operazioni SET+DELETE
    Given Viene caricato un nuovo documento pdf
    And Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test | SET |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test  | DELETE |
      | global_singlevalue:test | SET    |
    Then Il documento è stato correttamente modificato con la seguente lista di tag
      | global_singlevalue:test |

  @aggiuntaTag
  Scenario: UpdateSingle ERROR - Set+Delete sullo stesso tag
    Given Viene caricato un nuovo documento pdf
    And Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1 | SET |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test2 | SET    |
      | global_multivalue:test1 | DELETE |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "SET and DELETE cannot contain the same tags: [global_multivalue]"

  @aggiuntaTag
  Scenario: UpdateSingle ERROR - MaxFileKeys
    Given Vengono caricati 6 nuovi documenti pdf
    And I primi 5 documenti vengono modificati secondo le seguenti operazioni
      | global_indexed_multivalue:test | SET |
    When Si modifica il documento 6 secondo le seguenti operazioni
      | global_indexed_multivalue:test | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxFileKeys' reached. Current value: 6. Max value: 5"

  @aggiuntaTag
  Scenario: UpdateSingle ERROR - MaxOperationsOnTagsPerRequest
    Given Viene caricato un nuovo documento pdf
    And Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1 | SET |
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1          | DELETE |
      | global_indexed_multivalue:test2  | SET    |
      | global_singlevalue:test3         | SET    |
      | global_indexed_singlevalue:test4 | SET    |
      | pn-test~local_multivalue:test5   | SET    |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Number of tags to update exceeds maxOperationsOnTags limit"

  @aggiuntaTag
  Scenario: UpdateSingle ERROR - MaxValuesPerTagDocument
    Given Viene caricato un nuovo documento pdf
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1,test2,test3 | SET |
    And Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test4,test5,test6 | SET |
    Then La chiamata genera un errore con status code 400
    #And Il messaggio di errore riporta la dicitura "TODO"

  @aggiuntaTag
  Scenario: UpdateSingle ERROR - MaxTagsPerDocument
    Given Viene caricato un nuovo documento pdf
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1        | SET |
      | global_singlevalue:test1       | SET |
      | pn-test~local_multivalue:test1 | SET |
    Then La chiamata genera un errore con status code 400
    #And Il messaggio di errore riporta la dicitura ""

  @aggiuntaTag
  Scenario: UpdateSingle ERROR - MaxValuesPerTagPerRequest
    Given Viene caricato un nuovo documento pdf
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1,test2,test3,test4,test5,test6 | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Number of values for tag global_multivalue exceeds maxValues limit"

#  Scenario: Search ERROR - MaxMapValuesForSearch
#    Given Viene caricato un nuovo documento pdf
#    And Si modifica il documento 1 secondo le seguenti operazioni
#      | global_multivalue:test1,test2,test3,test4,test5 | SET |
#    When Si effettua una ricerca passando 6 filtri di ricerca
#    Then La chiamata genera un errore con status code 400
#    And Il messaggio di errore riporta la dicitura "TODO"

  @aggiuntaTag
  Scenario: GetTags SUCCESS
    Given Viene caricato un nuovo documento pdf
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1,test2,test3 | SET |
      | global_singlevalue:test1            | SET |
    Then Il documento è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test1,test2,test3 |
      | global_singlevalue:test1            |

#    Todo otteniamo 500
  Scenario: GetTags SUCCESS Empty Result
    Given Viene caricato un nuovo documento pdf
    Then Il documento è stato correttamente modificato con la seguente lista di tag
      | null |

  Scenario: Update Massive SUCCESS - solo operazioni SET
    Given Vengono caricati 2 nuovi documenti pdf
    When Si modificano i documenti secondo le seguenti operazioni
      | tag | documentIndex  | operation |
      | global_multivalue:test1 | 1 | SET |
      | global_multivalue:test2 | 2 | SET |
    Then La chiamata genera un errore con status code 200
    And Il documento 1 è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test1 |
    And Il documento 2 è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test2 |

  Scenario: Update Massive SUCCESS - solo operazioni DELETE
    Given Vengono caricati 2 nuovi documenti pdf
    And Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1,test2 | SET |
    And Si modifica il documento 2 secondo le seguenti operazioni
      | global_multivalue:test1,test2 | SET |
    When Si modificano i documenti secondo le seguenti operazioni
      | tag | documentIndex  | operation |
      | global_multivalue:test1 | 1 | DELETE |
      | global_multivalue:test2 | 2 | DELETE |
    Then La chiamata genera un errore con status code 200
    And Il documento 1 è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test2 |
    And Il documento 2 è stato correttamente modificato con la seguente lista di tag
      | global_multivalue:test1 |
    And Il documento 1 non contiene la seguente lista di tag
      | global_multivalue:test1 |
    And Il documento 2 non contiene la seguente lista di tag
      | global_multivalue:test2 |