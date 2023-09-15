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
      | FRMTTR76M06B715E |

  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_6] Invocazione del servizio UNREACHABLE per CF più notifiche non consegnate sia per IRR TOT che per altre motivazioni
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | XXXCCC87B12H702E |

  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_7] Invocazione del servizio UNREACHABLE per CF con una sola notifica in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | XXXCCC87B12H702E |

  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_8] Invocazione del servizio UNREACHABLE per CF con sole notifiche presenti in stato IRR TOT con ultima tentativo di consegna >120g
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 0

    Examples:
      | CF   |
      | XXXCCC87B12H702E |

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
    When viene invocato il servizio CREATE_OPERATION
    Then il servizio risponde con errore "XXX"

    Examples:
      |CF| FULLNAME |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP |CITY | CITY2 | PR |COUNTRY|
      |CPNTMS85T15H703W|CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX|NA|ITALIA|

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_16] Invocazione del servizio CREATE_OPERATION per CF errato
    Given viene comunicato il nuovo indirizzo con campo indirizzo vuoto
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      |CF|
      |CPNTMS85T15H703WCPNTMS85T15H703W!|

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_19] Invocazione del servizio CREATE_OPERATION con ticket id vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "ticketid_vuoto" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF              | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| 1233443322              | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA|

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_20] Invocazione del servizio CREATE_OPERATION con ticket id non formalmente corretto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "ticketid_errato" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| 1233443322              | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_21] Invocazione del servizio CREATE_OPERATION con operation ticket id non formalmente corretto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "ticketoperationid_errato"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | TICKET_ID | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| 1233443322| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_22] Invocazione del servizio CREATE_OPERATION con coppia ticket id ed operation ticket id già usati in precedenza
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| 11| 11           | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA                    |

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_23] Invocazione del servizio CREATE_OPERATION inserimento richiesta corretta con creazione operation id
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_24] Invocazione del servizio UPLOAD VIDEO con operation id non esistente
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO con "<OPERATION_ID>" con errore
    Then il servizio risponde con errore "400"

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
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

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
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_28] Invocazione del servizio UPLOAD VIDEO con sha256 errato
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con sha256 errato
    When viene invocato il servizio UPLOAD VIDEO con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

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
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_32] Invocazione del servizio UPLOAD VIDEO con formato non consentito
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con formato non corretto
    When viene invocato il servizio UPLOAD VIDEO
    Then il video viene caricato su SafeStorage

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

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
      | XXXCCC87B12H702E |

  Scenario Outline: [API-SERVICE_SEARCH_37] Invocazione del servizio SEARCH con CF senza notifiche in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con lista vuota

    Examples:
      | CF   |
      | XXXCCC87B12H702EE |

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
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

    #stato operation VALIDATION= Operazione in fase di validazione di indirizzo e allegati
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
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |


        #stato operation OK= Notifica recapitata
  Scenario Outline: [API-SERVICE_SEARCH_46] Inserimento di una nuova richista di reinvio pratiche con stato operation id OK
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
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "OK"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |


            #stato operation KO= Notifica in errore di spedizione o in errore di validazione
  Scenario Outline: [API-SERVICE_SEARCH_47] Inserimento di una nuova richista di reinvio pratiche con stato operation id KO
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
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "KO"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@FAIL-Irreperibile_AR| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

            #stato operation OK= Notifica recapitata
  Scenario Outline: [API-SERVICE_SEARCH_48] Inserimento di una nuova richista di reinvio pratiche con stato operation id OK, multidestinario
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
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "OK"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |



  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_XX1] Invocazione del servizio UPLOAD VIDEO con ContentType vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con ContentType vuoto
    Given viene invocato il servizio UPLOAD VIDEO con errore
    Then il servizio risponde con errore "500"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | XXXCCC87B12H702E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |


