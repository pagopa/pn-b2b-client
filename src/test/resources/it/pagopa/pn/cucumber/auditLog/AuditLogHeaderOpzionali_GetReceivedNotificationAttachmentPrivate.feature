# QA-19752: alcune API private di pn-delivery e pn-delivery-push producono audit log privi di cx_id/cx_type/uid
# perche' tali header non venivano accettati in input. Un file per API/audit log (coerente con la tabella del
# ticket: getReceivedNotificationAttachmentPrivate -> AUD_NT_ATCHOPEN_RCP); ciascuna combinazione non vuota (2^3-1=7) dei tre header opzionali cx_id/cx_type/uid e'
# uno Scenario a se' stante (non una Examples di uno Scenario Outline), ognuno con la propria notifica creata al
# momento, cosi' da essere indipendente dagli altri.

@qa19752AuditHeaderOpzionali
Feature: Audit log con header opzionali cx_id/cx_type/uid - getReceivedNotificationAttachmentPrivate (AUD_NT_ATCHOPEN_RCP)

  Scenario: [QA-19752_AUD_NT_ATCHOPEN_RCP_CXID] getReceivedNotificationAttachmentPrivate - audit log AUD_NT_ATCHOPEN_RCP con header opzionali cx_id=PF-11111111-1111-1111-1111-111111111111 cx_type=assente uid=assente
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationAttachmentPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | $NULL |
      | uid    | $NULL    |
    Then verifico che l'audit log con tag "AUD_NT_ATCHOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_ATCHOPEN_RCP_CXTYPE] getReceivedNotificationAttachmentPrivate - audit log AUD_NT_ATCHOPEN_RCP con header opzionali cx_id=assente cx_type=PF uid=assente
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationAttachmentPrivate con i seguenti header opzionali
      | cxId   | $NULL   |
      | cxType | PF |
      | uid    | $NULL    |
    Then verifico che l'audit log con tag "AUD_NT_ATCHOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_ATCHOPEN_RCP_UID] getReceivedNotificationAttachmentPrivate - audit log AUD_NT_ATCHOPEN_RCP con header opzionali cx_id=assente cx_type=assente uid=22222222-2222-2222-2222-222222222222
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationAttachmentPrivate con i seguenti header opzionali
      | cxId   | $NULL   |
      | cxType | $NULL |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico che l'audit log con tag "AUD_NT_ATCHOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_ATCHOPEN_RCP_CXID_CXTYPE] getReceivedNotificationAttachmentPrivate - audit log AUD_NT_ATCHOPEN_RCP con header opzionali cx_id=PF-11111111-1111-1111-1111-111111111111 cx_type=PF uid=assente
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationAttachmentPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | PF |
      | uid    | $NULL    |
    Then verifico che l'audit log con tag "AUD_NT_ATCHOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_ATCHOPEN_RCP_CXID_UID] getReceivedNotificationAttachmentPrivate - audit log AUD_NT_ATCHOPEN_RCP con header opzionali cx_id=PF-11111111-1111-1111-1111-111111111111 cx_type=assente uid=22222222-2222-2222-2222-222222222222
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationAttachmentPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | $NULL |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico che l'audit log con tag "AUD_NT_ATCHOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_ATCHOPEN_RCP_CXTYPE_UID] getReceivedNotificationAttachmentPrivate - audit log AUD_NT_ATCHOPEN_RCP con header opzionali cx_id=assente cx_type=PF uid=22222222-2222-2222-2222-222222222222
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationAttachmentPrivate con i seguenti header opzionali
      | cxId   | $NULL   |
      | cxType | PF |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico che l'audit log con tag "AUD_NT_ATCHOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_ATCHOPEN_RCP_ALL] getReceivedNotificationAttachmentPrivate - audit log AUD_NT_ATCHOPEN_RCP con header opzionali cx_id=PF-11111111-1111-1111-1111-111111111111 cx_type=PF uid=22222222-2222-2222-2222-222222222222
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationAttachmentPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | PF |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico che l'audit log con tag "AUD_NT_ATCHOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati
