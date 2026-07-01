#Questo test è diverso da client-key-read. L'endpoint da testare qui è: /clients/{clientId}/encoded/keys/{keyId}
@client
Feature: Lettura di una chiave pubblica contenuta in un client
  Tutti gli utenti autenticati possono recuperare le informazioni di una chiave pubblica contenuta in un client 

  @happy-path
  @nrt-minimal
  Scenario Outline: [CLIENT_KEY_READ_01] Un utente con sufficienti permessi (admin); appartenente all'ente che ha creato il client; il quale utente è membro del client; nel quale client c'è una chiave pubblica; richiede la lettura del contenuto della chiave. L'operazione va a buon fine
    Given l'utente è un "admin" di "<ente>"
    Given "<ente>" ha già creato 1 client "CONSUMER"
    Given "<ente>" ha già inserito l'utente con ruolo "admin" come membro di quel client
    Given un "admin" di "<ente>" ha caricato una chiave pubblica in quel client
    When l'utente richiede la lettura del contenuto della chiave pubblica
    Then si ottiene status code 200
    Examples:
      | ente    |
      | GSP     |
      | PA1     |
      | Privato |

  @nrt-minimal
  @wait_for_fix
  Scenario Outline: [CLIENT_KEY_READ_02] Un utente di qualsiasi ruolo; appartenente all'ente che ha creato il client; il quale utente non è membro del client; nel quale client c'è una chiave pubblica; richiede la lettura del contenuto della chiave. L'operazione va a buon fine solo per admin e support
    Given l'utente è un "<ruolo>" di "PA2"
    Given "PA2" ha già creato 1 client "CONSUMER"
    Given "PA2" ha già inserito l'utente con ruolo "<ruoloCaricatore>" come membro di quel client
    Given un "<ruoloCaricatore>" di "PA2" ha caricato una chiave pubblica in quel client
    When l'utente richiede la lettura del contenuto della chiave pubblica
    Then si ottiene status code <statusCode>

    @happy-path
    Examples:
      | ruolo        | ruoloCaricatore | statusCode |
      | admin        | security        |        200 |
      | support      | security        |        200 |

    @sad-path
    Examples:
      | ruolo        | ruoloCaricatore | statusCode |
      | api          | security        |        403 |
      | security     | admin           |        403 |
      | api,security | admin           |        403 |

    @sad-path
    @nuovi-operatori-update
    Examples:
      | ruolo        | ruoloCaricatore | statusCode |
      | reviewer     | admin           |        403 |
      | viewer       | admin           |        403 |

  @happy-path
  @nrt-minimal
  Scenario: [CLIENT_KEY_READ_03] Un utente con permessi security; appartenente all'ente che ha creato il client; il quale utente è membro del client; nel quale client c'è una chiave pubblica; la quale chiave è stata caricata dall’utente stesso; richiede la lettura del contenuto della chiave. L'operazione va a buon fine
    Given l'utente è un "security" di "PA1"
    Given "PA1" ha già creato 1 client "CONSUMER"
    Given "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    Given un "security" di "PA1" ha caricato una chiave pubblica in quel client
    When l'utente richiede la lettura del contenuto della chiave pubblica
    Then si ottiene status code 200

  @happy-path
  @nrt-minimal
  Scenario: [CLIENT_KEY_READ_04] Un utente con permessi security; appartenente all'ente che ha creato il client; il quale utente è membro del client; nel quale client c'è una chiave pubblica; la quale chiave non è stata caricata dall’utente stesso; richiede la lettura del contenuto della chiave. L'operazione va a buon fine
    When "PA1" ha già creato 1 client "CONSUMER"
    And "PA1" ha già inserito l'utente con ruolo "security" come membro di quel client
    And "PA1" ha già inserito l'utente con ruolo "admin" come membro di quel client
    And un "admin" di "PA1" ha caricato una chiave pubblica in quel client
    When l'utente è un "security" di "PA1"
    And l'utente richiede la lettura del contenuto della chiave pubblica
    Then si ottiene status code 200
