Feature: verifica audit-log su delivery

  Scenario: [AUDIT_LOG_400_ERROR]Invio una notifica con external-registries spento e controllo che venga generato un audito log di errore con status code 502 piuttosto che 400
    Given viene generata una nuova notifica
      | subject            | invio notifica pn-er spento |
      | senderDenomination | comune di Palermo           |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | error | ----- |

