#Verifica tecnica senza chiamate remote: dimostra che scenari eseguiti in parallelo leggono la versione della propria suite da un contesto non condiviso.
Feature: versione dello stream definita dalla suite V28

  @streamVersionIsolationV28
  Scenario Outline: [STREAM-VERSION-ISOLATION_V28] Scenario concorrente <n> legge la versione della propria suite
    Given gli scenari di verifica della versione dello stream sono in esecuzione contemporaneamente
    Then l'hook di versione ha inizializzato il contesto prima degli hook con ordine predefinito
    And la versione dello stream definita dalla suite è "V28"
    And il contesto della versione è un'istanza propria dello scenario

    Examples:
      | n |
      | 1 |
      | 2 |
      | 3 |
      | 4 |
