Feature: Api Service Desk

          #stato operation OK= Notifica recapitata

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_5] Invocazione del servizio UNREACHABLE per CF con sole notifiche in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | TMTCRL80A41A662H |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_7] Invocazione del servizio UNREACHABLE per CF con una sola notifica in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | TMTSFS80A01H703K |

  @CallCenterEvoluto
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
      | TMTVDM65H08L736A| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |
      | TMTSFS80A01H703K| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |
      | TMTCRL80A41A662H| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_4] Invocazione del servizio UNREACHABLE per CF senza notifiche in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 0

    Examples:
      | CF               |
      | CPNTMS85T15H703W |


  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_6] Invocazione del servizio UNREACHABLE per CF più notifiche non consegnate sia per IRR TOT che per altre motivazioni
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | FRMTTR76M06B715E |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_8] Invocazione del servizio UNREACHABLE per CF con sole notifiche presenti in stato IRR TOT con ultima tentativo di consegna >120g
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | TMTMRC66A01H703L |

  @CallCenterEvoluto
  Scenario: [API-SERVICE_DESK_UNREACHABLE_9] Invocazione del servizio UNREACHABLE per CF vuoto
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE con cf vuoto
    When viene invocato il servizio UNREACHABLE con errore
    Then il servizio risponde con errore "500"

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_10] Invocazione del servizio UNREACHABLE per CF non formalmente corretto
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               |
      | CPNTMS85T15H703WCPNTMS85T15H703W! |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_14] Invocazione del servizio CREATE_OPERATION per CF vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con cf vuoto
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
       | FULLNAME | NAMEROW2 | ADDRESS  |ADDRESSROW2 | CAP | CITY | CITY2 | PR | COUNTRY |
       | CICCIO PASTICCIO | SIGN. | Via@ok_RS | INTERNO 2| 80100 | NAPOLI | XXX | NA| ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_15] Invocazione del servizio CREATE_OPERATION per CF che non ha notifiche da consegnare per irr tot
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      |CF| FULLNAME |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP |CITY | CITY2 | PR |COUNTRY|
      |CPNTMS85T15H703W|CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX|NA|ITALIA|

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_16] Invocazione del servizio CREATE_OPERATION per CF errato
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      |CF| FULLNAME |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP |CITY | CITY2 | PR |COUNTRY|
      |CPNTMS85T15H703WCPNTMS85T15H703W!|CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX|NA|ITALIA|

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_17] Invocazione del servizio CREATE_OPERATION con indirizzo vuoto
    Given viene comunicato il nuovo indirizzo con campo indirizzo vuoto
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      |CF|
      |TMTSFS80A01H703K|

  @ignore
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
      | TMTGMN76L05A944V| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80100|NAPOLI|SOCCAVO |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_SEARCH_18_1] Invocazione del servizio CREATE_OPERATION con indirizzo errato
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
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTGMN76L05A944V| CICCIO PASTICCIO|SIGN.   |Via@FAIL-Irreperibile_AR| INTERNO 2  |80100|NAPOLI|SOCCAVO |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_19] Invocazione del servizio CREATE_OPERATION con ticket id vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "ticketid_vuoto" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF              | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| 1233443322              | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA|

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_20] Invocazione del servizio CREATE_OPERATION con ticket id non formalmente corretto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "ticketid_errato" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| 1233443322              | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_21] Invocazione del servizio CREATE_OPERATION con operation ticket id non formalmente corretto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "ticketoperationid_errato"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | TICKET_ID | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| 1233443322| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_22] Invocazione del servizio CREATE_OPERATION con coppia ticket id ed operation ticket id già usati in precedenza
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
   #TEST   | TMTSFS80A01H703K| AUTYV7JIYJ40WXC| AUT6DBGNT0      | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |
      | TMTSFS80A01H703K| AUT6B0D3CP3PGCG| AUT6CS4IU0      | CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_23] Invocazione del servizio CREATE_OPERATION inserimento richiesta corretta con creazione operation id
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTGVR80D44A783O| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_24] Invocazione del servizio UPLOAD VIDEO con operation id non esistente
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO con "<OPERATION_ID>" con errore
    Then il servizio risponde con errore "404"

    Examples:
      | OPERATION_ID|
      |abcedred|

  @CallCenterEvoluto
  Scenario: [API-SERVICE_PREUPLOAD_VIDEO_25] Invocazione del servizio UPLOAD VIDEO con operation id vuoto
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When  viene invocato il servizio UPLOAD VIDEO con operationid vuoto
    Then il servizio risponde con errore "400"

  @CallCenterEvoluto
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
      | FRMTTR76M06B715E| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |

  @CallCenterEvoluto
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

  @CallCenterEvoluto
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
      | TMTSFS80A01H703K| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

  @CallCenterEvoluto
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
      | TMTDMA00T07G273O| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |

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

  @CallCenterEvoluto
  Scenario: [API-SERVICE_SEARCH_34] Invocazione del servizio SEARCH con CF vuoto
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "CF_vuoto"
    When viene invocato il servizio SEARCH con errore
    Then il servizio risponde con errore "400"

  @CallCenterEvoluto
  Scenario: [API-SERVICE_SEARCH_35] Invocazione del servizio SEARCH con CF non formalmente corretto
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "CF_errato"
    When viene invocato il servizio SEARCH con errore
    Then il servizio risponde con errore "400"

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_SEARCH_36] Invocazione del servizio SEARCH con CF corretto
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo

    Examples:
      | CF   |
      | TMTDMA00T07G273O |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_SEARCH_37] Invocazione del servizio SEARCH con CF senza notifiche in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con lista vuota

    Examples:
      | CF   |
      | CPNTMS85T15H704W |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_SEARCH_38] Invocazione del servizio SEARCH con CF con una sola notifica reinviata per irreperibilità totale
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo

    Examples:
      | CF   |
      | TMTSFS80A01H703K |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_SEARCH_39] Invocazione del servizio SEARCH con CF con sole notifiche reinviate per irreperibilità con ultimo tentativo di consegna >120g
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>"
    When viene invocato il servizio SEARCH
    Then Il servizio SEARCH risponde con esito positivo

    Examples:
      | CF   |
      | TMTCRL80A41A662H |

  @CallCenterEvoluto
  Scenario Outline: [E2E_40] Inserimento di una nuova richista di reinvio pratiche con stato operation id OK con notifiche in irr tot con ultimo tentativo >120 gg
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
    Then Il servizio SEARCH risponde con esito positivo con uncompleted iun lo stato della consegna è "KO"


    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTMRC66A01H703L| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |


  @CallCenterEvoluto
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
    #Then Il servizio SEARCH risponde con esito positivo per lo "<IUN>" e lo stato della consegna è "OK"
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "OK"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF2>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO
    When viene invocato il servizio UPLOAD VIDEO
    Then la risposta del servizio UPLOAD VIDEO risponde con esito positivo
    Then il video viene caricato su SafeStorage
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF2>"
    When viene invocato il servizio SEARCH con delay
    #Then Il servizio SEARCH risponde con esito positivo per lo "<IUN>" e lo stato della consegna è "OK"
    Then Il servizio SEARCH risponde con esito positivo e lo stato della consegna è "OK"

    Examples:
    #  | CF1             | CF2               | IUN|FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
       | CF1             | CF2               |FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
    #  | TMTYRU80A07H703E| TMTRCC80A01A509O|WQUW-ZTGZ-HGVG-202310-W-1| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |
      | TMTYRU80A07H703E| TMTRCC80A01A509O| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |

  @ignore
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
      | FRMTTR76M06B715E| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |


    #stato operation CREATING= Operazione in attesa di caricamento del video
  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_SEARCH_42_1] Inserimento di una nuova richista di reinvio pratiche con stato operation id in CREATED
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
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTGNR83L06C351I| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |

  @CallCenterEvoluto
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
      | TMTMRA63A50L219T| CICCIO PASTICCIO|SIGN.   |Via@FAIL-Irreperibile_AR| INTERNO 2  |80121|NAPOLI|SOCCAVO |NA|ITALIA |


            #stato operation OK= Notifica recapitata
  @CallCenterEvoluto
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
      | TMTCRL80A01F205A| CLMCST42R12D969Z| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |

      #stato operation KO= Notifica in errore di spedizione o in errore di validazione
  @CallCenterEvoluto
  Scenario Outline: [E2E_50_1] CF per il quale una consegna per irreperibilità totale è andata  KO e si reinserisce nuova richiesta
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1
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
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS2>" "<ADDRESSROW2>" "<CAP2>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
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
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 0


    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESS2|ADDRESSROW2|CAP  |CAP2|CITY |CITY2|PR|COUNTRY|
      | TMTGRD80A04A662I| CICCIO PASTICCIO|SIGN.   |Via@FAIL-Irreperibile_AR|Via@ok_RS| INTERNO 2  |80100|80121 |NAPOLI|SOCCAVO |NA|ITALIA |

  @ignore
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
      | TMTGRD80A04A662I| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80100|80121 |NAPOLI|SOCCAVO |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_51] Inserimento di una nuova richista di reinvio pratiche con stato caricamento video su SafeStorage e verifica retention
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
      | TMTFNC42M05M082F| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80121|NAPOLI|SOCCAVO |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_54] Invocazione del servizio UPLOAD VIDEO con ContentType vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>"
    When viene invocato il servizio CREATE_OPERATION
    Then la risposta del servizio CREATE_OPERATION risponde con esito positivo
    And viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con ContentType vuoto
    Given viene invocato il servizio UPLOAD VIDEO con errore
    Then il servizio risponde con errore "400"

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTFNC42M05M082F| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA |


  @CallCenterEvoluto
  Scenario Outline: [API-AUTH_55] Connessione al client senza API KEY
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>" senza API Key
    When viene invocato il servizio UNREACHABLE con errore senza API Key
    Then il servizio risponde con errore "401" senza API Key
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>" senza API Key
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>" senza API Key
    When viene invocato il servizio CREATE_OPERATION senza API Key con errore
    Then il servizio risponde con errore "401" senza API Key
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO senza API Key
    When viene invocato il servizio UPLOAD VIDEO senza API Key con "<CF>" con errore
    Then il servizio risponde con errore "401" senza API Key
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>" senza API Key
    Then viene invocato il servizio SEARCH senza API Key con errore
    Then il servizio risponde con errore "401" senza API Key

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80121|NAPOLI|SOCCAVO |NA|ITALIA |

  @CallCenterEvoluto
  Scenario Outline: [API-AUTH_56] Connessione al client con api key errata
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>" con API Key errata
    When viene invocato il servizio UNREACHABLE con errore con API Key errata
    Then il servizio risponde con errore "401" con API Key errata
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>" con API Key errata
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION con "<CF>" con API Key errata
    When viene invocato il servizio CREATE_OPERATION con API Key errata con errore
    Then il servizio risponde con errore "401" con API Key errata
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con API Key errata
    When viene invocato il servizio UPLOAD VIDEO con API Key errata con "<CF>" con errore
    Then il servizio risponde con errore "401" con API Key errata
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "<CF>" con API Key errata
    Then viene invocato il servizio SEARCH con API Key errata Key con errore
    Then il servizio risponde con errore "401" con API Key errata

    Examples:
      | CF               | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTSFS80A01H703K| CICCIO PASTICCIO|SIGN.   |Via Roma| INTERNO 2  |80121|NAPOLI|SOCCAVO |NA|ITALIA |

    #Test inserito per la GA.2.1, PN 8094
  @CallCenterEvoluto
  Scenario Outline: [API-SLIPT_SPEDIZIONE_57] Creazione spedizione multipla per numero pagine allegati superiore al limite consentito per singola spedizione
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
    Then Il servizio SEARCH risponde con esito positivo con spedizione multipla e lo stato della consegna è "OK"

    Examples:
      | CF1             | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY|
      | TMTTMS92M57G793P| CICCIO PASTICCIO|SIGN.   |Via@ok_RS| INTERNO 2  |80121|NAPOLI|XXX |NA|ITALIA |

  @dpCallCenterEvoluto
  Scenario: [DP_SERVICE_DESK_UNREACHABLE_5_1] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTCRL80A41A662H |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @dpCallCenterEvoluto
  Scenario: [DP_SERVICE_DESK_UNREACHABLE_5_2] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTCRL80A41A662H |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_SERVICE_DESK_UNREACHABLE_5_3] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | FRMTTR76M06B715E |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_SERVICE_DESK_UNREACHABLE_6_1] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTDGI62C01A271W |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_SERVICE_DESK_UNREACHABLE_6_2] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_890_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTDGI62C01A271W |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_SERVICE_DESK_UNREACHABLE_6_3] Invio notifica ed attesa elemento di timeline ANALOG_SUCCESS_WORKFLOW_scenario positivo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
    And destinatario Mario Gherkin e:
      | denomination | Test OK |
      | digitalDomicile | NULL |
      | taxId | TMTDGI62C01A271W |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"

  @dpCallCenterEvoluto
  Scenario: [DP_SERVICE_DESK_UNREACHABLE_7_1] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTSFS80A01H703K |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_API-SERVICE_DESK_CREATE_OPERATION_23] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTGVR80D44A783O |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_API-SERVICE_SEARCH_18_1] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTGMN76L05A944V |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_API-SERVICE_PREUPLOAD_VIDEO_30] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTDMA00T07G273O |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"



  @dpCallCenterEvoluto
  Scenario: [DP_API-SERVICE_SEARCH_41] Notifica multi destinatario con workflow analogico con un destinatario irreperibile
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 1 |
      | taxId | TMTYRU80A07H703E |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTRCC80A01A509O |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    #And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    #And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "DIGITAL_DELIVERY_CREATION_REQUEST"
    #And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "SCHEDULE_REFINEMENT_WORKFLOW"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 0

   # And la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 1
      #Ritardare Il Perfezionamento per lo stato DELIVERED
 #   And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"

  @dpCallCenterEvoluto
  Scenario: [DP_API-SERVICE_SEARCH_42_1] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTGNR83L06C351I |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_API-E2E_46] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTVDM65H08L736A |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_API-E2E_47] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTMRA63A50L219T |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"


  @dpCallCenterEvoluto
  Scenario: [DP_E2E_48] Notifica multi destinatario con workflow analogico con un destinatario irreperibile
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario Mario Gherkin
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTCRL80A01F205A |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_DELIVERY_CREATION_REQUEST" per l'utente 0
    #And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    #And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "DIGITAL_DELIVERY_CREATION_REQUEST"
    #And vengono letti gli eventi fino allo stato della notifica "DELIVERED" per il destinatario 0 e presente l'evento "SCHEDULE_REFINEMENT_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SCHEDULE_REFINEMENT" e verifica data schedulingDate per il destinatario 0 rispetto ell'evento in timeline "SEND_DIGITAL_FEEDBACK"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW" per l'utente 0

    And la PA richiede il download dell'attestazione opponibile "DIGITAL_DELIVERY"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE" per l'utente 1
      #Ritardare Il Perfezionamento per lo stato DELIVERED
    And vengono letti gli eventi fino allo stato della notifica "EFFECTIVE_DATE"


  @dpCallCenterEvoluto
  Scenario: [DP_API-E2E_50] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTGRD80A04A662I |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_API-E2E_51] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTFNC42M05M082F |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_API-SERVICE_PREUPLOAD_VIDEO_54] Attesa elemento di timeline COMPLETELY_UNREACHABLE_fail_AR_scenario negativo
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTFNC42M05M082F |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"



  @dpCallCenterEvoluto
  Scenario: [DP_API-SLIPT_SPEDIZIONE_57_1] Creazione notifica con allegato di 26 pagine
    Given viene generata una nuova notifica
      | subject | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo |
      | physicalCommunication |  AR_REGISTERED_LETTER |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTTMS92M57G793P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b con preload allegato da 100 pagine pdf dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"

  @dpCallCenterEvoluto
  Scenario: [DP_API-SLIPT_SPEDIZIONE_57_2] Creazione notifica con allegato di 26 pagine
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination | Test AR Fail 2 |
      | taxId | TMTTMS92M57G793P |
      | digitalDomicile | NULL |
      | physicalAddress_address | Via@FAIL-Irreperibile_AR |
    When la notifica viene inviata tramite api b2b con preload allegato da 100 pagine pdf dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"