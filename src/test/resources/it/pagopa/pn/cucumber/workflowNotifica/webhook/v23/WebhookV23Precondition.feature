Feature: verifica creazione stream

  #NOTE:

  #1- Set token PA come admin senza gruppi per i Comuni Milano Verona Palermo:
  #pn.exteral.bearerToken.pa1.pagopa-dev=
  #pn.exteral.bearerToken.pa2.pagopa-dev=
  #pn.external.bearerToken.ga.pagopa-dev=

  #2- Set ApikKey dei diversi Comuni senza gruppi associati generati con il test [API-KEY_NO_GROUPS_WEBHOOK_ONLY_FOR_DEBUG]:
  #NO INTEROP
  #pn.external.api-keys.pagopa-dev-false=
  #pn.external.api-keys.pagopa-dev-2-false=
  #pn.external.api-keys.pagopa-dev-GA-false=
  #SI INTEROP
  #pn.external.api-keys.pagopa-dev-true=
  #pn.external.api-keys.pagopa-dev-2-true=
  #pn.external.api-keys.pagopa-dev-GA-true=

  #3- prima di ogni run cancellare tutti gli stream creati  in precedenza con il test [ONLY_FOR_DEBUG]

  Scenario: [ONLY_FOR_DEBUG] Cancellazione stream notifica
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V23"
    Given vengono cancellati tutti gli stream presenti del "Comune_1" con versione "V23"
    Given vengono cancellati tutti gli stream presenti del "Comune_2" con versione "V23"
    Given vengono cancellati tutti gli stream presenti del "Comune_Multi" con versione "V10"
    Given vengono cancellati tutti gli stream presenti del "Comune_1" con versione "V10"
    Given vengono cancellati tutti gli stream presenti del "Comune_2" con versione "V10"

  Scenario: [API-KEY_NO_GROUPS_WEBHOOK_ONLY_FOR_DEBUG] generazione ApiKey senza gruppo   ApiKey_scenario positivo
    Given Viene creata una nuova apiKey per il comune "Comune_1" senza gruppo
    Given Viene creata una nuova apiKey per il comune "Comune_2" senza gruppo
    Given Viene creata una nuova apiKey per il comune "Comune_Multi" senza gruppo

