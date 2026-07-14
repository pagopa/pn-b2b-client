@bff-notification
Feature: API Notifiche - verifica notifiche in-app messaggio e deep link (generato da excel)

  # FALLISCE: si aggiunge una chiave al client e-service, ma la notifica non menziona 'e-service'
  # Inoltre le doppie virgolette attorno al nome del client sono attese ma non presenti
  Scenario: [Notifica chiave aggiunta a client e-service] Viene aggiunta una nuova chiave ad un client e-service
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    When l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    Ti informiamo che è stata aggiunta una nuova chiave e-service al client "$CONTEXT(clientName)".
    """

  # TODO: ottengo la notifica: La chiave lmtL5gHMQaIbtsLDWxV35b7EUIkR0HoazemQJr9Q-u4 è stata rimossa
  # dal portachiavi erogatore pkname-1379444475. Assicurati che l'operatività non sia compromessa.
  # Per rimuovere una chiave di e-service, giusto dal portafoglio erogatore si può fare. Come generare la notifica?
  Scenario: [Notifica chiave rimossa da client e-service] Viene rimossa una chiave da un client e-service
    Given l'utente è un "admin" di "PA1"
    And esiste un producer keychain con nome "PKC1" e con descrizione "DESC_PKC1"
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And viene associato l'utente "%actual" alla producer keychain "%actual"
    And l'utente crea una nuova chiave di tipo "RSA" all'interno del producer-keychains con:
      | key    | name   | alg    | use    | keychainId |
      | %valid | %valid | %valid | %valid | %actual    |
    And l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    When viene eliminata la producer-key con keychainId "%actual", kid "%actual"

    Then admin di "PA1" ha ricevuto la notifica in-app contenente il link API_E_SERVICE
    """
    L'utente $CONTEXT(producerName) ha rimosso una chiave di e-service dal client "CONTEXT(clientName)".
    Assicurati che l'operatività non sia compromessa.
    """
