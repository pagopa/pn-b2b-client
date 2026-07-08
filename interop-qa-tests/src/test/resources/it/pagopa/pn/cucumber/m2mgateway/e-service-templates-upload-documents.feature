@e-service-template-m2m
@e-service-template-m2m-documents
Feature: Upload documenti su versione e-service template via API M2M

  @happy-path
  Scenario: [ESERVICE_TEMPLATE_UPLOAD_01] Per una versione DRAFT di e-service template è possibile allegare tutti i file del tipo previsto dalla piattaforma
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di caricare uno alla volta il seguente insieme di documenti sulla versione dell'e-service template
      | pdf  |
      | json |
      | md   |
      | xsd  |
      | yml  |
      | yaml |
      | txt  |
      | wsdl |
    Then tutti i tentativi di caricamento sulla versione dell'e-service template hanno esito positivo

  @sad-path
  Scenario: [ESERVICE_TEMPLATE_UPLOAD_02] Per una versione DRAFT di e-service template il caricamento fallisce per tipi non ammessi o estensioni non valide
    Given l'utente è un "admin" di "PA1"
    And l'utente effettua la creazione di un e-service template in modalità erogazione in stato di DRAFT
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di caricare uno alla volta i seguenti tipi documenti sulla versione dell'e-service template, con l'estensione specificata
      | documento | estensione |
      # tipi di file non permessi, con estensione non permessa: ogni file conserva la propria estensione
      | html      | html       |
      | sh        | sh         |
      | bat       | bat        |
      | cmd       | cmd        |
      | js        | js         |
      | bash      | bash       |
      | ps1       | ps1        |
      | png       | png        |
      | docx      | docx       |
      | zip       | zip        |
      | msi       | msi        |
      | exe       | exe        |
      # tipi di file non permessi, con estensione permessa (magic byte riconoscibile)
      | png       | pdf        |
      | docx      | pdf        |
      | zip       | pdf        |
      | msi       | pdf        |
      | exe       | pdf        |
      # tipi di file permessi, con estensione non permessa
      | pdf       | exe        |
      | json      | exe        |
      | md        | exe        |
      | xsd       | exe        |
      | yml       | exe        |
      | yaml      | exe        |
      | txt       | exe        |
      | wsdl      | exe        |
      # tipi di file permessi, senza estensione
      | pdf       |            |
      # tipo di file non permesso, con estensione non permessa
      | html      | exe        |
      # tipo di file permesso, con doppia estensione non permessa
      | pdf       | pdf.exe    |
    Then tutti i tentativi di caricamento sulla versione dell'e-service template hanno esito negativo


