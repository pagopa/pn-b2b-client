@action-on-products
Feature: PARI - Portale registro dei beni

  Background:
    Given vengono generati tutti i token JWT necessari

    #[TC_13]
  #[TC_57]
  @produttore2 @invitalia1
  Scenario: [TC_ACTION_ON_PRODUCT_1] Viene caricato un prodotto, escluso e poi caricato di nuovo
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2431696          | CP1012SA0GR       | CP1012SA0GR          | Lavasciuga       | IT                   |
    Given viene usata l'utenza: INVITALIA_L1
    And viene escluso il prodotto appena aggiunto
    Then si verifica che il prodotto sia marcato come: "REJECTED"
    Given viene usata l'utenza: PRODUTTORE_2
    Then viene caricato di nuovo lo stesso prodotto
    Then si verifica che la risposta abbia:
      | status      | OK |
    Then si verifica che il prodotto sia marcato come: "UPLOADED"

    #[TC_14]
    #[TC_54]
  @produttore2 @invitalia1 @invitalia2
  Scenario: [TC_ACTION_ON_PRODUCT_2] Si verifica che ci sia il corretto numero di motivazioni a seguito delle operazioni di contrassegnazione / esclusione
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2413074          | CP1210WA0ES       | CP1210WA0ES          | Lavasciuga       | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Given viene usata l'utenza: INVITALIA_L1
    Then viene contrassegnato il prodotto appena aggiunto
    #si verifica che tutti gli operatori Invitalia possono leggere la motivazione del prodotto contrassegnato
    And viene recuperata la lista iniziale di motivazioni
    And si verifica che ci siano 1 motivazioni a seguito delle operazioni di contrassegnazione fatte da INVITALIA_L1
    And viene usata l'utenza: INVITALIA_L2
    And si verifica che ci siano 1 motivazioni a seguito delle operazioni di contrassegnazione fatte da INVITALIA_L1
    #lato produttore si verifica che il prodotto sia marcato come contrassegnato e che il ricarimento dello stesso non ne cambia lo stato
    Given viene usata l'utenza: PRODUTTORE_2
    Then si verifica che il prodotto sia marcato come: "SUPERVISED"
    Then viene caricato di nuovo lo stesso prodotto
    Then si verifica che la risposta abbia:
      | status      | OK |
    # [TC_58]
    # Recupero del report generato:
    And si verifica che il report dell'ultimo prodotto aggiunto contenga la descrizione: "Il prodotto è sottoposto alle verifiche previste"
    Then il report è correttamente popolato
    Then si verifica che il prodotto sia marcato come: "SUPERVISED"
    #lato INVITALIA escludo il prodotto dopo la contrassegnazione
    Given viene usata l'utenza: INVITALIA_L1
    When viene escluso il prodotto appena aggiunto
    Then si verifica che ci siano 2 motivazioni a seguito delle operazioni di contrassegnazione fatte da INVITALIA_L1
    Then si verifica che il prodotto sia marcato come: "REJECTED"


  #[TC_56]
  @produttore2 @invitalia1 @invitalia2
  Scenario: [TC_ACTION_ON_PRODUCT_3] Si verifica che lato INVITALIA le vecchie motivazioni non siano più visibili quando un prodotto viene caricato di nuovo dal produttore
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2219972           | WAN2827NPL       | WAN2827NPL          | Lavatrice        | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Given viene usata l'utenza: INVITALIA_L1
    Then si verifica che il prodotto sia marcato come: "UPLOADED"
    And viene escluso il prodotto appena aggiunto
    And si verifica che ci siano 1 motivazioni a seguito delle operazioni di esclusione fatte da INVITALIA_L1
    Given viene usata l'utenza: PRODUTTORE_2
    Then viene caricato di nuovo lo stesso prodotto
    Then si verifica che la risposta abbia:
      | status      | OK |
    Given viene usata l'utenza: INVITALIA_L1
    And si verifica che ci siano 0 motivazioni a seguito delle operazioni di esclusione fatte da INVITALIA_L1
    Given viene usata l'utenza: INVITALIA_L2
    And si verifica che ci siano 0 motivazioni a seguito delle operazioni di esclusione fatte da INVITALIA_L1

      #[TC_60]
  @produttore2 @invitalia1
  Scenario: [TC_ACTION_ON_PRODUCT_3_B] Viene escluso un prodotto e poi si prova ad iniziare l'iter di approvazione di un prodotto
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2474773          | W1D2A854ADPS       | W1D2A854ADPS          | Lavasciuga       | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Given viene usata l'utenza: INVITALIA_L1
    And viene escluso il prodotto appena aggiunto
    Then si verifica che il prodotto sia marcato come: "REJECTED"
    And viene iniziato l'iter di approvazione del prodotto
    Then si verifica che il prodotto sia marcato come: "REJECTED"

          #[TC_61]
  @produttore2 @invitalia2
  Scenario: [TC_ACTION_ON_PRODUCT_4] L2 o un produttore non possono iniziare la fase di approvazione di un prodotto che deve partire da L1
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2474773          | W1D2A854ADPS       | W1D2A854ADPS          | Lavasciuga       | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Given viene usata l'utenza: INVITALIA_L2
    And viene iniziato l'iter di approvazione del prodotto
    Then si verifica che la chiamata abbia ritornato uno status code: 403
    Given viene usata l'utenza: PRODUTTORE_2
    And viene iniziato l'iter di approvazione del prodotto
    Then si verifica che la chiamata abbia ritornato uno status code: 403

      #[TC_62]
  @produttore2 @invitalia1 @invitalia2
  Scenario: [TC_ACTION_ON_PRODUCT_5] L'avvio del iter di approvazione del prodotto cambio lo stato in "WAIT_APPROVED" che è però visibile soltanto lato INVITALIA
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 1589211           | WF5V843BWSIT       | WF5V843BWSIT          | Lavatrice        | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Then si verifica che il prodotto sia marcato come: "UPLOADED"
    Given viene usata l'utenza: INVITALIA_L1
    And viene iniziato l'iter di approvazione del prodotto
    Then si verifica che il prodotto sia marcato come: "WAIT_APPROVED"
    Given viene usata l'utenza: PRODUTTORE_2
    Then si verifica che il prodotto sia marcato come: "UPLOADED"
    Given viene usata l'utenza: INVITALIA_L2
    And viene ripristinato il prodotto appena aggiunto da L2
    Then si verifica che il prodotto sia marcato come: "UPLOADED"

  @produttore2 @invitalia1 @invitalia2
  Scenario: [TC_ACTION_ON_PRODUCT_6] L'approvazione di un prodotto da parte di un operatore L2 che non è stato ancora approvato da L1 non ne cambia lo stato
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2423603          | F2R5FG6J       | F2R5FG6J          | Lavasciuga       | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Then si verifica che il prodotto sia marcato come: "UPLOADED"
    Given viene usata l'utenza: INVITALIA_L2
    And viene approvato il prodotto appena aggiunto
    Then si verifica che il prodotto sia marcato come: "UPLOADED"
    # La chiamata dell'API /approved non è permessa ad un operatore L1
    Given viene usata l'utenza: INVITALIA_L1
    And viene approvato il prodotto appena aggiunto
    Then si verifica che la chiamata abbia ritornato uno status code: 403

  @produttore2 @invitalia1 @invitalia2
  Scenario: [TC_ACTION_ON_PRODUCT_7] L'API di esclusione di un prodotto non può essere chiamata da un ente Invitalia L2.
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHINGMACHINES" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 1589211           | WF5V843BWSIT       | WF5V843BWSIT          | Lavatrice        | IT                   |
    Then si verifica che la risposta abbia:
      | status           | OK |
    Then si verifica che il prodotto sia marcato come: "UPLOADED"
    Given viene usata l'utenza: INVITALIA_L2
    And viene escluso il prodotto appena aggiunto
    Then si verifica che la chiamata abbia ritornato uno status code: 403
    # Si verifica che lo stato non è cambiato
    Then si verifica che il prodotto sia marcato come: "UPLOADED"
    # Si verifica che un operatore L2 non può nemmeno escludere un prodotto segnato come da revisionare
    Given viene usata l'utenza: INVITALIA_L1
    Then viene contrassegnato il prodotto appena aggiunto
    Then si verifica che il prodotto sia marcato come: "SUPERVISED"
    Given viene usata l'utenza: INVITALIA_L2
    And viene escluso il prodotto appena aggiunto
    Then si verifica che la chiamata abbia ritornato uno status code: 403
    Given viene usata l'utenza: INVITALIA_L1
    And viene escluso il prodotto appena aggiunto

    #bug https://pagopa.atlassian.net/browse/RDB-321
  @produttore2 @invitalia1
  Scenario: [TC_ACTION_ON_PRODUCT_8] L'API di esclusione di un prodotto ritorna un KO se i prodotti non sono nello stesso stato.
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2195173          | WD90DG6G94BB       | WD90DG6G94BB          | Lavasciuga       | IT                   |
      | 2195172           | WD90DG6G94BK       | WD90DG6G94BK          | Lavasciuga        | IT                   |
    Given viene usata l'utenza: INVITALIA_L1
    And viene contrassegnato il prodotto con codice eprel: "2195173"
    And viene escluso il prodotto appena aggiunto
    Then si verifica che l'operazione di aggiornamento ritorni i seguenti valori:
      | status      | KO |
      | errorKey    | product.invalid.update.mixedStatus |
    Then si verifica che lo stato del prodotto con codice eprel: "2195173" sia: "SUPERVISED"
    Then si verifica che lo stato del prodotto con codice eprel: "2195172" sia: "UPLOADED"
    And viene escluso il prodotto con codice eprel: "2195173"
    And viene escluso il prodotto con codice eprel: "2195172"

  #bug https://pagopa.atlassian.net/browse/RDB-321
  @produttore2 @invitalia1
  Scenario: [TC_ACTION_ON_PRODUCT_8] L'API di contrassegnazione di un prodotto ritorna un KO se i prodotti non sono nello stesso stato.
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2195173          | WD90DG6G94BB       | WD90DG6G94BB          | Lavasciuga       | IT                   |
      | 2195172           | WD90DG6G94BK       | WD90DG6G94BK          | Lavasciuga        | IT                   |
    Given viene usata l'utenza: INVITALIA_L1
    And viene escluso il prodotto con codice eprel: "2195173"
    And viene contrassegnato il prodotto appena aggiunto
    Then si verifica che l'operazione di aggiornamento ritorni i seguenti valori:
      | status      | KO |
      | errorKey    | product.invalid.update.mixedStatus |
    Then si verifica che lo stato del prodotto con codice eprel: "2195173" sia: "REJECTED"
    Then si verifica che lo stato del prodotto con codice eprel: "2195172" sia: "UPLOADED"
    And viene escluso il prodotto con codice eprel: "2195173"
    And viene escluso il prodotto con codice eprel: "2195172"

  @produttore2 @invitalia1 @invitalia2
  Scenario: [TC_ACTION_ON_PRODUCT_9] Un prodotto non può essere contrassegnato da un operatore Invitalia L2
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2195173          | WD90DG6G94BB       | WD90DG6G94BB          | Lavasciuga       | IT                   |
    Given viene usata l'utenza: INVITALIA_L1
    And viene contrassegnato il prodotto appena aggiunto
    Given viene usata l'utenza: INVITALIA_L2
    And viene contrassegnato il prodotto appena aggiunto
    Then si verifica che la chiamata abbia ritornato uno status code: 403
    Then si verifica che lo stato del prodotto con codice eprel: "2195173" sia: "SUPERVISED"
    Given viene usata l'utenza: INVITALIA_L1
    And viene escluso il prodotto appena aggiunto
    Then si verifica che lo stato del prodotto con codice eprel: "2195173" sia: "REJECTED"

  @produttore2 @invitalia1 @invitalia2
  Scenario: [TC_ACTION_ON_PRODUCT_10] Un prodotto portato in WAIT_APPROVED da L1 non può più essere escluso da L1 e quindi lo stato non cambia
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "WASHERDRIERS" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria        | Paese di Produzione  |
      | 2195173          | WD90DG6G94BB       | WD90DG6G94BB          | Lavasciuga       | IT                   |
    Given viene usata l'utenza: INVITALIA_L1
    And viene iniziato l'iter di approvazione del prodotto
    And viene escluso il prodotto appena aggiunto
    Then si verifica che il prodotto sia marcato come: "WAIT_APPROVED"
   # si ripristina lo stato del prodotto
    Given viene usata l'utenza: INVITALIA_L2
    And viene ripristinato il prodotto appena aggiunto da L2
    Then si verifica che il prodotto sia marcato come: "UPLOADED"


  @produttore2 @invitalia1
  Scenario: [TC_ACTION_ON_PRODUCT_11] Un utente INVITALIA_L1 esclude più prodotto insieme e si verifica che per ogni prodotto ci sia una sola motivazione
    Given viene usata l'utenza: PRODUTTORE_2
    When viene caricato il csv con categoria: "REFRIGERATINGAPPL" e dati:
      | Codice EPREL     | Codice GTIN/EAN     | Codice Prodotto        | Categoria                           | Paese di Produzione  |
      | 2390281          | KG36NVIAG           | KG36NVIAG              | Apparecchio di refrigerazione       | IT                   |
      | 2312227          | KGN36VIDB           | KGN36VIDB              | Apparecchio di refrigerazione       | IT                   |
    Given viene usata l'utenza: INVITALIA_L1
    And vengono esclusi i prodotti appena aggiunti
    Then si verifica che lo stato del prodotto con codice eprel: "2390281" sia: "REJECTED"
    Then si verifica che lo stato del prodotto con codice eprel: "2312227" sia: "REJECTED"
    Then si verifica che per il prodotto "2390281" ci sia 1 motivazione
    Then si verifica che per il prodotto "2312227" ci sia 1 motivazione
