@m2m-agreements
@m2m-agreements-documents
@document_upload
@document
@document-type-check
Feature: Upload documenti consumer su agreement via API M2M

  @happy-path
  Scenario: [AGREEMENT_UPLOAD_01] Per un agreement in stato DRAFT è possibile allegare tutti i file del tipo previsto dalla piattaforma
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di caricare uno alla volta il seguente insieme di documenti sulla richiesta di fruizione
      | pdf  |
      | json |
      | md   |
      | xsd  |
      | yml  |
      | yaml |
      | txt  |
      | wsdl |
    Then tutti i tentativi di caricamento sulla richiesta di fruizione hanno esito positivo

  @sad-path
  Scenario: [AGREEMENT_UPLOAD_02] Per un agreement in stato DRAFT il caricamento fallisce per tipi non ammessi o estensioni non valide
    Given l'utente è un "admin" di "PA1"
    And "PA2" ha già creato un e-service in stato "PUBLISHED" con approvazione "MANUAL"
    And "PA1" ha una richiesta di fruizione in stato "DRAFT" per quell'e-service
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di caricare uno alla volta i seguenti tipi documenti sulla richiesta di fruizione, con l'estensione specificata
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
    Then tutti i tentativi di caricamento sulla richiesta di fruizione hanno esito negativo


