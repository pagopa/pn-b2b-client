# QA-19752: alcune API private di pn-delivery e pn-delivery-push producono audit log privi di cx_id/cx_type/uid
# perche' tali header non venivano accettati in input. Un file per API/audit log (coerente con la tabella del
# ticket: searchNotificationsPrivate -> AUD_NT_SEARCH_SND); ciascuna combinazione non vuota (2^3-1=7) dei tre header opzionali cx_id/cx_type/uid e'
# uno Scenario a se' stante (non una Examples di uno Scenario Outline), ognuno con la propria notifica creata al
# momento, cosi' da essere indipendente dagli altri.

@auditLogHeaderOpzionali
Feature: Audit log con header opzionali cx_id/cx_type/uid - searchNotificationsPrivate (AUD_NT_SEARCH_SND)

  @work-item:QA-18882
  Scenario: [QA-19752_AUD_NT_SEARCH_SND_CXID] searchNotificationsPrivate: quando in richiesta è presente solo cx_id (assenti cx_type e uid), l'audit log AUD_NT_SEARCH_SND riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata searchNotificationsPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | $NULL |
      | uid    | $NULL    |
    Then verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | tag | AUD_NT_SEARCH_SND |
      | cx_id | PF-11111111-1111-1111-1111-111111111111 |
      | exclude | cx_type, uid |

  @work-item:QA-18882
  Scenario: [QA-19752_AUD_NT_SEARCH_SND_CXTYPE] searchNotificationsPrivate: quando in richiesta è presente solo cx_type (assenti cx_id e uid), l'audit log AUD_NT_SEARCH_SND riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata searchNotificationsPrivate con i seguenti header opzionali
      | cxId   | $NULL   |
      | cxType | PF |
      | uid    | $NULL    |
    Then verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | tag | AUD_NT_SEARCH_SND |
      | cx_type | PF |
      | exclude | cx_id, uid |

  @work-item:QA-18882
  Scenario: [QA-19752_AUD_NT_SEARCH_SND_UID] searchNotificationsPrivate: quando in richiesta è presente solo uid (assenti cx_id e cx_type), l'audit log AUD_NT_SEARCH_SND riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata searchNotificationsPrivate con i seguenti header opzionali
      | cxId   | $NULL   |
      | cxType | $NULL |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | tag | AUD_NT_SEARCH_SND |
      | uid | 22222222-2222-2222-2222-222222222222 |
      | exclude | cx_id, cx_type |

  @work-item:QA-18882
  Scenario: [QA-19752_AUD_NT_SEARCH_SND_CXID_CXTYPE] searchNotificationsPrivate: quando in richiesta sono presenti solo cx_id e cx_type (assente uid), l'audit log AUD_NT_SEARCH_SND riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata searchNotificationsPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | PF |
      | uid    | $NULL    |
    Then verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | tag | AUD_NT_SEARCH_SND |
      | cx_id | PF-11111111-1111-1111-1111-111111111111 |
      | cx_type | PF |
      | exclude | uid |

  @work-item:QA-18882
  Scenario: [QA-19752_AUD_NT_SEARCH_SND_CXID_UID] searchNotificationsPrivate: quando in richiesta sono presenti solo cx_id e uid (assente cx_type), l'audit log AUD_NT_SEARCH_SND riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata searchNotificationsPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | $NULL |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | tag | AUD_NT_SEARCH_SND |
      | cx_id | PF-11111111-1111-1111-1111-111111111111 |
      | uid | 22222222-2222-2222-2222-222222222222 |
      | exclude | cx_type |

  @work-item:QA-18882
  Scenario: [QA-19752_AUD_NT_SEARCH_SND_CXTYPE_UID] searchNotificationsPrivate: quando in richiesta sono presenti solo cx_type e uid (assente cx_id), l'audit log AUD_NT_SEARCH_SND riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata searchNotificationsPrivate con i seguenti header opzionali
      | cxId   | $NULL   |
      | cxType | PF |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | tag | AUD_NT_SEARCH_SND |
      | cx_type | PF |
      | uid | 22222222-2222-2222-2222-222222222222 |
      | exclude | cx_id |

  @work-item:QA-18882
  Scenario: [QA-19752_AUD_NT_SEARCH_SND_ALL] searchNotificationsPrivate: quando la richiesta include tutti e tre gli header opzionali cx_id, cx_type e uid, l'audit log AUD_NT_SEARCH_SND riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata searchNotificationsPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | PF |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | tag | AUD_NT_SEARCH_SND |
      | cx_id | PF-11111111-1111-1111-1111-111111111111 |
      | cx_type | PF |
      | uid | 22222222-2222-2222-2222-222222222222 |
