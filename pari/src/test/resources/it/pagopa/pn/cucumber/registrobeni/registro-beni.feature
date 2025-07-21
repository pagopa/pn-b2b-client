Feature: PARI - Portale registro dei beni

#  Scenario Outline: Avvenuto accesso alla piattaforma Registro Beni con utenza Produttore in seguito all’accettazione dei ToS
#    Given l'utente è un "<ruolo>" di "<ente>"
#    Given l'utente "<action>" i TOS
#    Given l'operazione viene effettuata senza errori
#
#    Examples:
#      | ruolo        | ente        | action                |
#      | A            | B           | ACCETTA               |
#      | A            | B           | RIFIUTA               |

  Scenario: Avvenuto accesso alla piattaforma Registro Beni con utenza Produttore ed accettazione dei ToS con body errato
#    Given l'utente è un "<ruolo>" di "<ente>"
#    Given l'utente "<action>" i TOS con request body errato
#    Given l'operazione produce errore
    When viene caricato il csv con dati:
      | codice_EPREL | codice_GTIN/EAN | codice_prodotto | categoria      | paese_produzione  |
      | 2405439      | 668888          | 666777666       | RANGEHOODS     | IT                |