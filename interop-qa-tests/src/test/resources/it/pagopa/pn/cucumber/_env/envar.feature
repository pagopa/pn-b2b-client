# FIXME 14/05/2025: non si tratta di una vera e propria feature, quanto un semplice
#   strumento di diagnostica per cercare di risolvere il problema che sta impedendo il completamento
#   delle pipeline su GitHub. E' temporaneo, rimuovere una volta risolto.
@envvar
Feature: Variabili di ambiente
  Scenario: Stampa tutte variabili di ambiente
    Then stampa tutte le variabili di ambiente
    And stampa tutte le system properties

