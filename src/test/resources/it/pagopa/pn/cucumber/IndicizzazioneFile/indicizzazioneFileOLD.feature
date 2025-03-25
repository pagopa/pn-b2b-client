# I seguenti casi di test per indicizzazione file di safe storage sono stati rimossi in seguito all'aggiornamento
# dei limiti su Parameter Store (specificati sopra ogni test). Per molti di questi casi, non è più riproducibile l'errore che
# ci si proponeva di testare, in quanto il numero di tag disponibili è inferiore a quello necessario per testare gli scenari.

Feature: Scenari per indicizzazione File safeStorage che sono stati rimossi dalla suite principale

  #MaxTagsPerRequest - valore corrente: 50, precedente: 6
  @aggiuntaTag
  @indicizzazioneSafeStorageOLD
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

  # Con l'aggiornamento dei limiti questo test non ha più senso: non si può ottenere questo errore sulla create in quanto
  # verrebbe catturato prima l'errore di maxValuesPerTagPerRequest (100) rispetto a quello di MaxValuesPerTagDocument (1000)
  @aggiuntaTag
  @indicizzazioneSafeStorageOLD
  Scenario: [INDEX_SS_CREATE_5_OLD] Create ERROR - MaxValuesPerTagDocument
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1,test2,test3,test4,test5,test6 |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxValuesPerTagDocument' reached."

  #MaxTagsPerDocument - valore corrente: 40, precedente: 2
  @aggiuntaTag
  @indicizzazioneSafeStorageOLD
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

  #MaxOperationsOnTagsPerRequest - valore corrente: 50, precedente: 4
  @aggiuntaTag
  @indicizzazioneSafeStorageOLD
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

  #MaxTagsPerDocument - valore corrente: 40, precedente: 2
  @aggiuntaTag
  @indicizzazioneSafeStorageOLD
  Scenario: [INDEX_SS_UPDATE_SINGLE_10] UpdateSingle ERROR - MaxTagsPerDocument
    Given Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1 secondo le seguenti operazioni
      | global_multivalue:test1        | SET |
      | global_singlevalue:test1       | SET |
      | pn-test~local_multivalue:test1 | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxTagsPerDocument' reached. Current value: 3. Max value: 2"

  #MaxOperationsOnTagsPerRequest - valore corrente: 50, precedente: 4
  @aggiuntaTag
  @indicizzazioneSafeStorageOLD
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
    And La response contiene uno o più errori "400.00" riportanti la dicitura "Number of tags to update exceeds maxOperationsOnTags limit" riguardanti il documento 1
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1  |
      | global_singlevalue:test6 |

  #MaxTagsPerDocument - valore corrente: 40, precedente: 2
  @aggiuntaTag
  @indicizzazioneSafeStorageOLD
  Scenario: [INDEX_SS_UPDATE_MASSIVE_12] Update Massive ERROR - MaxTagsPerDocument
    Given Vengono caricati 2 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_multivalue:test1 |
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                            | documentIndex |
      | SET       | global_singlevalue:test1       | 1             |
      | SET       | pn-test~local_multivalue:test1 | 1             |
      | SET       | global_singlevalue:test1       | 2             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori "400.00" riportanti la dicitura "Limit 'MaxTagsPerDocument' reached. Current value: 3. Max value: 2" riguardanti il documento 1
    And Il documento 2 è associato alla seguente lista di tag
      | global_multivalue:test1  |
      | global_singlevalue:test1 |

#################################################################### TODO #####################################################################

  # I seguenti due test necessiterebbero di un rework per funzionare col nuovo valore limite di MaxFileKeys (corrente: 1000, precedente: 5).
  # Tuttavia, richiedendo l'upload di 1001 documenti ad ogni run, andrebbe valutato se tenerli o se invece sono troppo onerosi.

  @aggiuntaTag
  @indicizzazioneSafeStorageTODO
    #non funzionante in seguito a modifica dei limiti (corrente: 1000, valore precedente: 5).
  Scenario: [INDEX_SS_UPDATE_SINGLE_7] UpdateSingle ERROR - MaxFileKeys
    Given esiste un limite "maxFileKeys" con valore pari a 1000
    And vengono caricati documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" in numero "PARI" a "maxFileKeys" con tag associati "global_indexed_multivalue:test"
    And Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modifica il documento 1001 secondo le seguenti operazioni
      | global_indexed_multivalue:test | SET |
    Then La chiamata genera un errore con status code 400
    And Il messaggio di errore riporta la dicitura "Limit 'MaxFileKeys' reached"

  @aggiuntaTag
  @concurrencyIndexSs
  @indicizzazioneSafeStorageTODO
  Scenario: [INDEX_SS_UPDATE_MASSIVE_10] Update Massive ERROR - MaxFileKeys
    And Vengono caricati 5 nuovi documenti di tipo "PN_NOTIFICATION_ATTACHMENTS" con tag associati
      | global_indexed_multivalue:test |
    And Viene caricato un nuovo documento di tipo "PN_NOTIFICATION_ATTACHMENTS"
    When Si modificano i documenti secondo le seguenti operazioni
      | operation | tag                            | documentIndex |
      | SET       | global_indexed_multivalue:test | 6             |
      | SET       | global_multivalue:test1        | 1             |
    Then L'update massivo va in successo con stato 200
    And La response contiene uno o più errori "400.00" riportanti la dicitura "Limit 'MaxFileKeys' reached. Current value: 6. Max value: 5" riguardanti il documento 6
    And Il documento 1 è associato alla seguente lista di tag
      | global_indexed_multivalue:test |
      | global_multivalue:test1        |