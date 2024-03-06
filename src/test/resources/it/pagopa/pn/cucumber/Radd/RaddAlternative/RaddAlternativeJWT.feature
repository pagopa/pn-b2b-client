Feature: Radd Alternative jwt verification

  @raddAlt
  Scenario: [RADD_ALT-JWT-1] PF -  Recupero notifica con codice IUN esistente associato e JWT corretto
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    When vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    Then L'operatore usa lo IUN "corretto" per recuperare gli atti di "Mario Cucumber" da issuer "issuer_1"
    And la lettura si conclude correttamente su radd alternative
    And vengono caricati i documento di identità del cittadino su radd alternative
    Then Vengono visualizzati sia gli atti sia le attestazioni opponibili riferiti alla notifica associata all'AAR da radd alternative
    And l'operazione di download degli atti si conclude correttamente su radd alternative
    And l'operazione di download restituisce 5 documenti
    And viene conclusa la visualizzati di atti ed attestazioni della notifica su radd alternative
    And la chiusura delle transazione per il recupero degli aar non genera errori su radd alternative

  @raddAlt
  Scenario: [RADD_ALT-JWT-2] PF -  Recupero notifica con codice IUN esistente associato e JWT di un issuer non censito (verifica manuale errore dai log)
    Then L'operatore usa lo IUN "errato" per recuperare gli atti di "Mario Cucumber" da issuer "issuer_non_censito"
    And l'operazione ha prodotto un errore con status code "500"

  @raddAlt
  Scenario: [RADD_ALT-JWT-3] PF -  Recupero notifica con codice IUN esistente associato e JWT di un issuer scaduto (verifica manuale errore dai log)
    Then L'operatore usa lo IUN "errato" per recuperare gli atti di "Mario Cucumber" da issuer "issuer_scaduto"
    And l'operazione ha prodotto un errore con status code "500"

  @raddAlt
  Scenario: [RADD_ALT-JWT-4] PF -  Recupero notifica con codice IUN esistente associato e JWT di un issuer intended usage errato
    Then L'operatore usa lo IUN "errato" per recuperare gli atti di "Mario Cucumber" da issuer "issuer_dati_errati"
    And l'operazione ha prodotto un errore con status code "403"

  @raddAlt
  Scenario: [RADD_ALT-JWT-5] PF -  Recupero notifica con codice IUN esistente associato e JWT di un issuer con aud errata (verifica manuale errore dai log)
    Then L'operatore usa lo IUN "errato" per recuperare gli atti di "Mario Cucumber" da issuer "issuer_aud_errata"
    And l'operazione ha prodotto un errore con status code "500"

  @raddAlt
  Scenario: [RADD_ALT-JWT-6] PF -  Recupero notifica con codice IUN esistente associato e JWT di un issuer con private key diverso dalla JWKS (verifica manuale errore dai log)
    Then L'operatore usa lo IUN "errato" per recuperare gli atti di "Mario Cucumber" da issuer "issuer_private_diverso"
    And l'operazione ha prodotto un errore con status code "500"

  @raddAlt
  Scenario: [RADD_ALT-JWT-7] PF -  Recupero notifica con codice IUN esistente associato e JWT di un issuer con kid diverso dalla JWKS (verifica manuale errore dai log)
    Then L'operatore usa lo IUN "errato" per recuperare gli atti di "Mario Cucumber" da issuer "issuer_kid_diverso"
    And l'operazione ha prodotto un errore con status code "500"
