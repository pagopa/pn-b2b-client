Feature: API Service Desk

  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_4] Invocazione del servizio UNREACHABLE per CF senza notifiche in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 0

    Examples:
      | CF               |
      | XXXCCC87B12H702E |

  Scenario Outline: [API-SERVICE_DESK_UNREACHABLE_5] Invocazione del servizio UNREACHABLE per CF con sole notifiche in stato IRR TOT
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "<CF>"
    When viene invocato il servizio UNREACHABLE
    Then la risposta del servizio UNREACHABLE è 1

    Examples:
      | CF   |
      | XXXCCC87B12H702E |

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

  Scenario : [API-SERVICE_DESK_UNREACHABLE_9] Invocazione del servizio UNREACHABLE per CF vuoto
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "CF_vuoto"
    When viene invocato il servizio UNREACHABLE
    Then il servizio risponde con errore "503"

  Scenario : [API-SERVICE_DESK_UNREACHABLE_10] Invocazione del servizio UNREACHABLE per CF non formalmente corretto
    Given viene creata una nuova richiesta per invocare il servizio UNREACHABLE per il "CF_errato"
    When viene invocato il servizio UNREACHABLE
    Then il servizio risponde con errore "503"

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_14] Invocazione del servizio CREATE_OPERATION per CF vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION
    Then il servizio risponde con errore "503"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY
      | null | 1233443322| 1233443322              | CICCIO PASTICCIO|SIGN.   |VIA ROMA| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_15] Invocazione del servizio CREATE_OPERATION per CF che non ha notifiche da consegnare per irr tot
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION
    Then il servizio risponde con errore "503"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY
      | XXXCCC87B12H702E | 1233443322| 1233443322              | CICCIO PASTICCIO|SIGN.   |VIA ROMA| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_16] Invocazione del servizio CREATE_OPERATION per CF errato
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION
    Then il servizio risponde con errore "503"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY
      | CPNTM@85T15H703W | 1233443322| 1233443322              | CICCIO PASTICCIO|SIGN.   |VIA ROMA| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_17] Invocazione del servizio CREATE_OPERATION con indirizzo vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION
    Then il servizio risponde con errore "503"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY
      | CPNTM@85T15H703W | 1233443322| 1233443322              | CICCIO PASTICCIO|SIGN.   |null| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA


  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_19] Invocazione del servizio CREATE_OPERATION con ticket id vuoto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION
    Then il servizio risponde con errore "503"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY
      | XXXCCC87B12H702E| null| 1233443322              | CICCIO PASTICCIO|SIGN.   |VIA ROMA 12| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA


  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_20] Invocazione del servizio CREATE_OPERATION con ticket id non formalmente corretto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION
    Then il servizio risponde con errore "503"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY
      | XXXCCC87B12H702E| 123344332@| 1233443322              | CICCIO PASTICCIO|SIGN.   |VIA ROMA 12| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA


  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_21] Invocazione del servizio CREATE_OPERATION con operation ticket id non formalmente corretto
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION
    Then il servizio risponde con errore "503"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY
      | XXXCCC87B12H702E| 1233443322| 123344332@              | CICCIO PASTICCIO|SIGN.   |VIA ROMA 12| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA

  Scenario Outline: [API-SERVICE_DESK_CREATE_OPERATION_22] Invocazione del servizio CREATE_OPERATION con coppia ticket id ed operation ticket id già usati in precedenza
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION
    Then il servizio risponde con errore "401"

    Examples:
      | CF               | TICKET_ID | OPERATION_TICKED_ID     | FULLNAME        |NAMEROW2|ADDRESS  |ADDRESSROW2|CAP  |CITY |CITY2|PR|COUNTRY
      | XXXCCC87B12H702E| 1233443322| 1233443322              | CICCIO PASTICCIO|SIGN.   |VIA ROMA 12| INTERNO 2  |80100|NAPOLI|XXX |NA|ITALIA


  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_24] Invocazione del servizio UPLOAD VIDEO con operation id non esistente
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con "<PRELOADIDX>" "<SHA256>" "<CONTENT_TYPE>"
    When viene invocato il servizio UPLOAD VIDEO con "<OPERATION_ID>"
    Then il servizio risponde con errore "401"

    Examples:
      | PRELOADIDX  | SHA256        | CONTENT_TYPE     | OPERATION_ID
      | 12344333    | 123adasdadadad| application/octet-stream       | asd2332


  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_25] Invocazione del servizio UPLOAD VIDEO con operation id vuoto
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con "<PRELOADIDX>" "<SHA256>" "<CONTENT_TYPE>"
    When viene invocato il servizio UPLOAD VIDEO con "<OPERATION_ID>"
    Then il servizio risponde con errore "401"

    Examples:
      | PRELOADIDX  | SHA256        | CONTENT_TYPE     | OPERATION_ID
      | 12344333    | 123adasdadadad| application/octet-stream       | null

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_26] Invocazione del servizio UPLOAD VIDEO con sha256 vuoto
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con "<PRELOADIDX>" "<SHA256>" "<CONTENT_TYPE>"
    When viene invocato il servizio UPLOAD VIDEO con "<OPERATION_ID>"
    Then il servizio risponde con errore "401"

    Examples:
      | PRELOADIDX  | SHA256        | CONTENT_TYPE     | OPERATION_ID
      | 12344333    | null| application/octet-stream       | asd2332


  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_27] Invocazione del servizio UPLOAD VIDEO con preloadidx vuoto
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con "<PRELOADIDX>" "<SHA256>" "<CONTENT_TYPE>"
    When viene invocato il servizio UPLOAD VIDEO con "<OPERATION_ID>"
    Then il servizio risponde con errore "401"

    Examples:
      | PRELOADIDX  | SHA256        | CONTENT_TYPE     | OPERATION_ID
      | null    | 123adasdadadad| application/octet-stream       | asd2332

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_27] Invocazione del servizio UPLOAD VIDEO con preloadidx vuoto
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con "<PRELOADIDX>" "<SHA256>" "<CONTENT_TYPE>"
    When viene invocato il servizio UPLOAD VIDEO con "<OPERATION_ID>"
    Then il servizio risponde con errore "401"

    Examples:
      | PRELOADIDX  | SHA256        | CONTENT_TYPE     | OPERATION_ID
      | null    | 123adasdadadad| application/octet-stream       | asd2332

  Scenario Outline: [API-SERVICE_PREUPLOAD_VIDEO_30] Invocazione del servizio UPLOAD VIDEO con esito positivo
    Given viene comunicato il nuovo indirizzo con "<FULLNAME>" "<NAMEROW2>" "<ADDRESS>" "<ADDRESSROW2>" "<CAP>" "<CITY>" "<CITY2>" "<PR>" "<COUNTRY>"
    Given viene creata una nuova richiesta per invocare il servizio CREATE_OPERATION per con "<CF>" "<TICKET_ID>" "<OPERATION_TICKED_ID>"
    When viene invocato il servizio CREATE_OPERATION
    Given viene creata una nuova richiesta per invocare il servizio UPLOAD VIDEO con "<PRELOADIDX>" "<SHA256>" "<CONTENT_TYPE>"
    When viene invocato il servizio UPLOAD VIDEO
    Then il servizio risponde con errore "401"

    Examples:
      | PRELOADIDX  | SHA256        | CONTENT_TYPE     | OPERATION_ID
      | 12344333    | null          | application/octet-stream       | asd2332


  Scenario : [API-SERVICE_SEARCH_30] Invocazione del servizio SEARCH con CF vuoto
    Given viene creata una nuova richiesta per invocare il servizio SEARCH per il "CF_vuoto"
    When viene invocato il servizio SEARCH
    Then il servizio risponde con errore "401"













