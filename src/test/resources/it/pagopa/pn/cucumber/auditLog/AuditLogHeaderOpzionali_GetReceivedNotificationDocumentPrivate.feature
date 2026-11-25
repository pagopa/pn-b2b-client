# QA-19752: alcune API private di pn-delivery e pn-delivery-push producono audit log privi di cx_id/cx_type/uid
# perche' tali header non venivano accettati in input. Un file per API/audit log (coerente con la tabella del
# ticket: getReceivedNotificationDocumentPrivate -> AUD_NT_DOCOPEN_RCP); ciascuna combinazione non vuota (2^3-1=7) dei tre header opzionali cx_id/cx_type/uid e'
# uno Scenario a se' stante (non una Examples di uno Scenario Outline), ognuno con la propria notifica creata al
# momento, cosi' da essere indipendente dagli altri.

@qa19752AuditHeaderOpzionali
Feature: Audit log con header opzionali cx_id/cx_type/uid - getReceivedNotificationDocumentPrivate (AUD_NT_DOCOPEN_RCP)

  Scenario: [QA-19752_AUD_NT_DOCOPEN_RCP_CXID] getReceivedNotificationDocumentPrivate: quando in richiesta è presente solo cx_id (assenti cx_type e uid), l'audit log AUD_NT_DOCOPEN_RCP riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationDocumentPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | $NULL |
      | uid    | $NULL    |
    Then verifico che l'audit log con tag "AUD_NT_DOCOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_DOCOPEN_RCP_CXTYPE] getReceivedNotificationDocumentPrivate: quando in richiesta è presente solo cx_type (assenti cx_id e uid), l'audit log AUD_NT_DOCOPEN_RCP riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationDocumentPrivate con i seguenti header opzionali
      | cxId   | $NULL   |
      | cxType | PF |
      | uid    | $NULL    |
    Then verifico che l'audit log con tag "AUD_NT_DOCOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_DOCOPEN_RCP_UID] getReceivedNotificationDocumentPrivate: quando in richiesta è presente solo uid (assenti cx_id e cx_type), l'audit log AUD_NT_DOCOPEN_RCP riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationDocumentPrivate con i seguenti header opzionali
      | cxId   | $NULL   |
      | cxType | $NULL |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico che l'audit log con tag "AUD_NT_DOCOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_DOCOPEN_RCP_CXID_CXTYPE] getReceivedNotificationDocumentPrivate: quando in richiesta sono presenti solo cx_id e cx_type (assente uid), l'audit log AUD_NT_DOCOPEN_RCP riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationDocumentPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | PF |
      | uid    | $NULL    |
    Then verifico che l'audit log con tag "AUD_NT_DOCOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_DOCOPEN_RCP_CXID_UID] getReceivedNotificationDocumentPrivate: quando in richiesta sono presenti solo cx_id e uid (assente cx_type), l'audit log AUD_NT_DOCOPEN_RCP riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationDocumentPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | $NULL |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico che l'audit log con tag "AUD_NT_DOCOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_DOCOPEN_RCP_CXTYPE_UID] getReceivedNotificationDocumentPrivate: quando in richiesta sono presenti solo cx_type e uid (assente cx_id), l'audit log AUD_NT_DOCOPEN_RCP riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationDocumentPrivate con i seguenti header opzionali
      | cxId   | $NULL   |
      | cxType | PF |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico che l'audit log con tag "AUD_NT_DOCOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  Scenario: [QA-19752_AUD_NT_DOCOPEN_RCP_ALL] getReceivedNotificationDocumentPrivate: quando la richiesta include tutti e tre gli header opzionali cx_id, cx_type e uid, l'audit log AUD_NT_DOCOPEN_RCP riporta esattamente gli header passati, nient'altro
    Given viene generata una nuova notifica
      | subject            | QA-19752 audit log header opzionali |
      | senderDenomination | Comune di Palermo                   |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    When viene invocata l'API privata getReceivedNotificationDocumentPrivate con i seguenti header opzionali
      | cxId   | PF-11111111-1111-1111-1111-111111111111   |
      | cxType | PF |
      | uid    | 22222222-2222-2222-2222-222222222222    |
    Then verifico che l'audit log con tag "AUD_NT_DOCOPEN_RCP" su "/aws/ecs/pn-delivery" riporti solo gli header opzionali effettivamente passati

  # PN-21832: startTransaction pubblica di pn-radd-alt (PR #388) propaga uid/cx_id/cx_type nella chiamata interna a
  # getReceivedNotificationDocumentPrivate su pn-delivery. Per RADD cx_id/cx_type derivano dal bearer token usato,
  # quindi si verifica solo che siano valorizzati, non un valore specifico.
  Scenario: [PN-21832_DOCOPEN_RCP_RADD] Un operatore RADD avvia il ritiro di un atto e ne scarica il documento notificato: pn-delivery registra nel suo audit log chi ha fatto la richiesta
    Given viene generata una nuova notifica
      | subject            | PN-21832 audit log radd startTransaction |
      | senderDenomination | Comune di Palermo                        |
    And destinatario
      | denomination            | Mario Cucumber            |
      | taxId                   | FRMTTR76M06B715E          |
      | digitalDomicile_address | testpagopa3@pec.pagopa.it |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And Il cittadino Mario Cucumber come destinatario 0 mostra il QRCode "corretto"
    When L'operatore scansione il qrCode per recuperare gli atti di Mario Cucumber
    Then la scansione si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    And Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And verifico che l'audit log con tag "AUD_NT_DOCOPEN_RCP" su "/aws/ecs/pn-delivery" riporti l'identità del chiamante, ossia i campi cx_id, cx_type e uid valorizzati
