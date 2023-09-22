Feature: Api Service Desk

  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_4] Invocazione del servizio UNREACHABLE per CF senza notifiche in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 0

    Examples:
      | CF               |
      | CPNTMS85T15H703W |

  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_5] Invocazione del servizio UNREACHABLE per CF con sole notifiche in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | MNTMRA03M71C615V |

  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_6] Invocazione del servizio UNREACHABLE per CF più notifiche non consegnate sia per IRR TOT che per altre motivazioni
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | FRMTTR76M06B715E |

  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_7] Invocazione del servizio UNREACHABLE per CF con una sola notifica in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | TMTSFS80A01H703K |

    @ignore
  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_8] Invocazione del servizio UNREACHABLE per CF con sole notifiche presenti in stato IRR TOT con ultima tentativo di consegna >120g
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | TMTMRC66A01H703L |

  Scenario: [API-SERVICE_DESK_UNREACHABLE_9] Invocazione del servizio UNREACHABLE per CF vuoto
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE con cf vuoto
    When viene invocato il servizio UNREACHABLE con errore
    Then il servizio risponde con errore "500"

  Scenario Outline: : [API-SERVICE_DESK_UNREACHABLE_10] Invocazione del servizio UNREACHABLE per CF non formalmente corretto
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               |
      | CPNTMS85T15H703WCPNTMS85T15H703W! |

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_14] Invocazione del servizio CREATE_OPERATION per CF vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con cf vuoto
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
       | FULLNAME | NAMEROW2 | ADDRESS  |ADDRESSROW2 | CAP | CITY | CITY2 | PR | COUNTRY |
       | CICCIO PASTICCIO | SIGN. | Via@ok_RS | INTERNO 2| 80100 | NAPOLI | XXX | NA| ITALIA |

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_15] Invocazione del servizio CREATE_OPERATION per CF che non ha notifiche da consegnare per irr tot
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      |CF| FULLNAME |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP |CITY | CITY2 | PR |COUNTRY|
      |CPNTMS85T15H703W|CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX|NA|ITALIA|

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_16] Invocazione del servizio CREATE_OPERATION per CF errato
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      |CF| FULLNAME |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP |CITY | CITY2 | PR |COUNTRY|
      |CPNTMS85T15H703WCPNTMS85T15H703W!|CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX|NA|ITALIA|

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_17] Invocazione del servizio CREATE_OPERATION con indirizzo vuoto
    Given viene comunicato il nuovo indirizzo con campo indirizzo vuoto
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      |CF|
      |TMTSFS80A01H703K|


  Scenario Outline: [API-SERVICE_SEARCH_18] Invocazione del servizio CREATE_OPERATION con indirizzo errato
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH con delay
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "KO"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80100|NAPOLI|SOCCAVO |NA|ITALIA |


  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_19] Invocazione del servizio CREATE_OPERATION con ticket id vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "ticketid_vuoto" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF              | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| 1233443322              | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA|

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_20] Invocazione del servizio CREATE_OPERATION con ticket id non formalmente corretto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "ticketid_errato" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| 1233443322              | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_21] Invocazione del servizio CREATE_OPERATION con operation ticket id non formalmente corretto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "ticketoperationid_errato"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | TICKET_ID | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| 1233443322| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_22] Invocazione del servizio CREATE_OPERATION con coppia ticket id ed operation ticket id già usati in precedenza
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| AUTYV7JIYJ40WXC| AUT6DBGNT0      | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_23] Invocazione del servizio CREATE_OPERATION inserimento richiesta corretta con creazione operation id
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | FRMTTR76M06B715E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_24] Invocazione del servizio UPLOAD VIDEO con operation id non esistente
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO con "<OPERATION_ID>" con errore
    Then il servizio risponde con errore "404"

    Examples:
      | OPERATION_ID|
      |abcedred|

  Scenario: [API-SERVICE_PREUPLOAD_VIDEO_25] Invocazione del servizio UPLOAD VIDEO con operation id vuoto
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When  viene invocato il servizio UPLOAD VIDEO con operationid vuoto
    Then il servizio risponde con errore "400"

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_26] Invocazione del servizio UPLOAD VIDEO con sha256 vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con sha256 vuoto
    When viene invocato il servizio UPLOAD VIDEO con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | FRMTTR76M06B715E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_27] Invocazione del servizio UPLOAD VIDEO con preloadidx vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con preloadIdx vuoto
    Given viene invocato il servizio UPLOAD VIDEO con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | FRMTTR76M06B715E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_28] Invocazione del servizio UPLOAD VIDEO con sha256 errato
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con sha256 errato
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | FRMTTR76M06B715E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  @ignore
  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_29] Invocazione del servizio UPLOAD VIDEO con preloadidx errato
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con preloadIdx vuoto
    Given viene invocato il servizio UPLOAD VIDEO con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_30] Invocazione del servizio UPLOAD VIDEO con esito positivo
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  @ignore
  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_32] Invocazione del servizio UPLOAD VIDEO con formato non consentito
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then il video viene caricato su SafeStorage

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |

  @ignore
  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_33] Invocazione del servizio UPLOAD VIDEO con url scaduta
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    And il video viene caricato su SafeStorage con url scaduta
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | MNTMRA03M71C615V| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |


  Scenario: [API-SERVICE_SEARCH_34] Invocazione del servizio SEARCH con CF vuoto
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "CF_vuoto"
    When viene invocato il servizio SEARCH con errore
    Then il servizio risponde con errore "400"

  Scenario: [API-SERVICE_SEARCH_35] Invocazione del servizio SEARCH con CF non formalmente corretto
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "CF_errato"
    When viene invocato il servizio SEARCH con errore
    Then il servizio risponde con errore "400"

  Scenario Outline: [API-SERVICE_SEARCH_36] Invocazione del servizio SEARCH con CF corretto
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo

    Examples:
      | CF   |
      | FRMTTR76M06B715E |

  Scenario Outline: [API-SERVICE_SEARCH_37] Invocazione del servizio SEARCH con CF senza notifiche in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con lista vuota

    Examples:
      | CF   |
      | CPNTMS85T15H703W |

  Scenario Outline: [API-SERVICE_SEARCH_38] Invocazione del servizio SEARCH con CF con una sola notifica reinviata per irreperibilità totale
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo

    Examples:
      | CF   |
      | TMTSFS80A01H703K |

  Scenario Outline: [API-SERVICE_SEARCH_39] Invocazione del servizio SEARCH con CF con sole notifiche reinviate per irreperibilità con ultimo tentativo di consegna >120g
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo

    Examples:
      | CF   |
      | FRMTTR76M06B715E |


  Scenario Outline: [API-SERVICE_SEARCH_41] Invocazione del servizio SEARCH con CF con sole notifiche reinviate per irreperibilità totale- notifica multidestinatario
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF1>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF1>"
    When viene invocato il servizio SEARCH con delay
    Then Il servizio SEARCH risponde con esito positivo per lo "<IUN>" e lo stato della consegna è "OK"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF2>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF2>"
    When viene invocato il servizio SEARCH con delay
    Then Il servizio SEARCH risponde con esito positivo per lo "<IUN>" e lo stato della consegna è "OK"

    Examples:
      | CF1             | CF2               | IUN|FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTYRU80A07H703E| TMTRCC80A01A509O|QAQN-LJXG-YTNA-202309-Q-1  | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |


    #stato operation CREATING= Operazione in attesa di caricamento del video
  Scenario Outline: [API-SERVICE_SEARCH_42] Inserimento di una nuova richista di reinvio pratiche con stato operation id in CREATED
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "CREATING"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | MNTMRA03M71C615V| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

    #stato operation VALIDATION= Operazione in fase di validazione di indirizzo e allegati
  @ignore
  Scenario Outline: [API-SERVICE_SEARCH_43] Inserimento di una nuova richista di reinvio pratiche con stato operation id in CREATED
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "CREATING"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80121|NAPOLI|SOCCAVO |NA|ITALIA |


        #stato operation OK= Notifica recapitata
  Scenario Outline: [E2E_46] Inserimento di una nuova richista di reinvio pratiche con stato operation id OK
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH con delay
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "OK"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | MNTMRA03M71C615V| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [E2E_47] Invocazione del servizio CREATE_OPERATION con nuovo tantativo consegna non recapitata(KO)
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH con delay
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "KO"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80100|NAPOLI|SOCCAVO |NA|ITALIA |


            #stato operation OK= Notifica recapitata
  Scenario Outline: [E2E_48] Inserimento di una nuova richista di reinvio pratiche con stato operation id OK per uno dei multidestinatari
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF1>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF1>"
    When viene invocato il servizio SEARCH con delay
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "OK"
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF2>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con lista vuota

    Examples:
      | CF1             | CF2               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTMRZ69A10H501C| TMTLCU69R68L483M| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |


      #stato operation KO= Notifica in errore di spedizione o in errore di validazione
  Scenario Outline: [E2E_50] CF per il quale una consegna per irreperibilità totale è andata  KO e si reinserisce nuova richiesta
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH con delay
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "KO"
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP2>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH con delay
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "OK"


    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CAP2|CITY |CITY2|PR|COUNTRY|
      | TMTVCN80A01H501P| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80100|80121 |NAPOLI|SOCCAVO |NA|ITALIA |


  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_XX1] Invocazione del servizio UPLOAD VIDEO con ContentType vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con ContentType vuoto
    Given viene invocato il servizio UPLOAD VIDEO con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | FRMTTR76M06B715E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |


  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_XX2] Inserimento di una nuova richista di reinvio pratiche con stato caricamento video su SafeStorage e verifica retention
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Then viene effettuato un controllo sulla durata della retention

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80121|NAPOLI|SOCCAVO |NA|ITALIA |


