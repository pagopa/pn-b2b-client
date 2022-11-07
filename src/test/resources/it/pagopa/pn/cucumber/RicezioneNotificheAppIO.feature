Feature: recupero notifiche tramite api AppIO b2b

  @SmokeTest
  Scenario: [B2B-PA-APP-IO_1] Invio notifica con api b2b e recupero tramite AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere recuperata tramite AppIO

  @SmokeTest
  Scenario: [B2B-PA-APP-IO_2] Invio notifica con api b2b paProtocolNumber e idemPotenceToken e recupero tramite AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
      | idempotenceToken | AME2E3626070001.1  |
    And destinatario
      | denomination | Mario Cucumber |
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And la notifica può essere recuperata tramite AppIO
    And viene generata una nuova notifica con uguale paProtocolNumber e idempotenceToken "AME2E3626070001.2"
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere recuperata tramite AppIO

  @SmokeTest
  Scenario: [B2B-PA-APP-IO_3] Invio notifica con api b2b uguale creditorTaxId e diverso codice avviso recupero tramite AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | comune di milano |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
      | payment_creditorTaxId | 77777777777 |
    And la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And la notifica può essere recuperata tramite AppIO
    And viene generata una nuova notifica con uguale codice fiscale del creditore e diverso codice avviso
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then la notifica può essere recuperata tramite AppIO

  @SmokeTest
  Scenario: [B2B-PA-APP-IO_4] Invio notifica con api b2b e recupero documento notificato con AppIO
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | FRMTTR76M06B715E |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    Then il documento notificato può essere recuperata tramite AppIO

  @SmokeTest
  Scenario: [B2B-PA-APP-IO_5] Invio notifica con api b2b e tentativo lettura da altro utente (non delegato)_scenario negativo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
      | senderTaxId | 01199250158 |
    And destinatario
      | denomination | Mario Cucumber |
      | taxId | CLMCST42R12D969Z |
    When la notifica viene inviata tramite api b2b e si attende che lo stato diventi ACCEPTED
    And si tenta il recupero della notifica tramite AppIO
    Then il tentativo di recupero con appIO ha prodotto un errore con status code "500"


