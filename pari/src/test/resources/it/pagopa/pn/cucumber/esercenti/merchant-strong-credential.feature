Feature: PARI - Portale Esercenti Credenziali Forti (Sede legale)

  Background:
    Given vengono generati tutti i token JWT necessari per esercenti

    #[TC-16]
    #[TC-18]
  Scenario: [TC_MERCHANT_1] Verificare la chiamata che restituisce l'elenco dei punti vendita e il dettaglio di un punto vendita specifico
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene recuperato l'elenco dei punti vendita
    | city |  |
    Then la lista dei punti vendita è correttamente popolata
    And si recupera il dettaglio di uno specifico punto vendita

    #[TC-17]
  Scenario: [TC_MERCHANT_2] Viene invocata la chiamata che restituisce l’elenco dei punti vendita passando un merchantId errato
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene recuperato l'elenco dei punti vendita
    | merchantId | 0000 |
    Then la chiamata ritorna status code: 404

    #[TC-19]
  Scenario: [TC_MERCHANT_3] Viene invocata la chiamata che restituisce il dettaglio di un punto vendita passando un merchantId/pointOfSaleId errato
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene recuperato l'elenco dei punti vendita
      | city |  |
    Then si recupera il dettaglio di uno specifico punto vendita con i seguenti parametri:
      |pointOfSaleId| 0000 |
    Then la chiamata ritorna status code: 404
    Then si recupera il dettaglio di uno specifico punto vendita con i seguenti parametri:
      |merchantId| 0000 |
    Then la chiamata ritorna status code: 404

  #[TC-20]
  #[TC-22]
  Scenario: [TC_MERCHANT_5] Viene censito correttamente un nuovo punto vendita e poi viene provata la modifica - Caso positivo
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene censito un nuovo punto vendita con i seguenti parametri:
      | id | NULL |
      | contactName    | Mario                                          |
      | contactSurname | Rossi                                          |
    Then la chiamata ritorna status code: 200
    #SI PROVA LA MODIFICA DI UN PUNTO VENDITA
    When viene censito un nuovo punto vendita con i seguenti parametri:
      | contactSurname | Verde                                          |
    Then la chiamata ritorna status code: 200


    #[TC-21]
  Scenario: [TC_MERCHANT_6] Si prova a censire un nuovo punto vendita passando un merchantId non valido - Caso di errore
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene censito un nuovo punto vendita con i seguenti parametri:
      | merchantId     | 0000             |
    Then la chiamata ritorna status code: 404


  #[TC-23]
  Scenario: [TC_MERCHANT_8] Viene censito un nuovo punto vendita utilizzando un pointOfSaleId non valido - KO
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene censito un nuovo punto vendita con i seguenti parametri:
      | id             | 00000             |
    Then la chiamata ritorna status code: 404









