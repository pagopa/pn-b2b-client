Feature: verifica audit-log su delivery

  @NotificationDetailsError @work-item:QA-18841
  Scenario: [NOTIFICHE_LEGALI_AUDIT_LOG_400_ERROR] Invio una notifica legale con external-registries spento e controllo che venga generato un audito log di errore con status code 502 quando viene chiamata l'api di dettaglio notifica
    Given viene generata una nuova notifica
      | subject            | invio notifica delega temporanea |
      | senderDenomination | comune di Palermo                |
      | document           | SI                               |
      | payment_f24        | PAYMENT_F24_FLAT                 |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then viene restituito uno status code 502 in accesso al dettaglio notifica tramite API per le notifiche legali
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | error | ----- |

  @NotificationDetailsError @work-item:QA-18841
  Scenario: [NOTIFICHE_BONARIE_AUDIT_LOG_400_ERROR] Invio una notifica bonaria con external-registries spento e controllo che venga generato un audito log di errore con status code 502 quando viene chiamata l'api di dettaglio notifica
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId    | FattOrd           |
      | recipientType | PF                |
      | taxId         | FRMTTR76M06B715E  |
      | denomination  | Ettore Fieramosca |
      | messageId     | ${NEW-IT}         |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    Then viene restituito uno status code 502 in accesso al dettaglio notifica tramite API per le notifiche bonarie
    And verifico la presenza di un audit log su "/aws/ecs/pn-delivery" negli ultimi 20 minuti riportante i seguenti dati nel messaggio
      | error | ----- |

