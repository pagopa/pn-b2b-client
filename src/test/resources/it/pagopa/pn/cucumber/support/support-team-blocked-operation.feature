@backstageEnti
Feature: Verifica operazioni consentite e non consentite per il ruolo di supporto

  Scenario Outline: [SUPPORTO_1] Verifica che un ente con ruolo di supporto possa effettuare operazioni a lui consentite
    When Il team di supporto effettua l'operazione di: "<api>"
    Then il ruolo supporto ha accesso all'API e riceve una risposta valida
    Examples:
      | api                         |
      | RICERCA_TUTTE_LE_NOTIFICHE  |
      | DETTAGLIO_NOTIFICA          |
      | RECUPERO_DOCUMENTI_NOTIFICA |
      | RECUPERO_ALLEGATI_PAGAMENTO |
      | VISUALIZZA_DASHBOARD        |

  Scenario Outline: [SUPPORTO_2] Verifica che un ente con ruolo di supporto non possa effettuare operazioni a lui non consentite
    When Il team di supporto effettua l'operazione di: "<api>"
    Then il ruolo supporto non ha accesso all'API e riceve un errore di autorizzazione
    Examples:
      | api                    |
      | INVIO_NUOVA_NOTIFICA   |
      | CAMBIO_LINGUA          |
      | CANCELLAZIONE_NOTIFICA |
      | RECUPERA_API_KEYS      |
      | CREA_API_KEY           |
      | CAMBIA_STATO_API_KEY   |
      | CANCELLA_API_KEY       |
