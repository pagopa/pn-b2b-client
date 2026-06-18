Feature: Modifica di un singolo aderente

  # 08/06/2026: testa una api di maintenance, le quali - al momento - non possono essere eseguite al di fuori della
  # pipeline Github.
  # Essendo la modifica di un tenant:
  #   1.  un'operazione delicata, che rischia di esporre il sistema a problemi imprevedibili qualora non
  #       venisse fatta correttamente
  #   2.  un'operazione coinvolta in pochi edge cases, che non rientra nei normali flussi di utilizzo del sistema
  # si sceglie di non includere questo test tra gli NRT.
  @debug
  @tenant-maintenance
  Scenario: [TENANT_WRONG_VERSION_1] Quando si tenta di modificare un tenant con una versione non aggiornata, si ottiene un errore
    Given l'ente "PA4" legge la propria attuale configurazione
    When si tenta di modificare il tenant "PA4" con una versione antecedente a quella corrente
    Then si ottiene status code 400
