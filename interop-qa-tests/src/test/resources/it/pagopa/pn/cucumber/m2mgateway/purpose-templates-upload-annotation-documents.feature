@m2m-purpose-templates
@m2m-purpose-templates-annotation-documents
Feature: Upload documenti di annotazione risk analysis su purpose template

  # TODO 08/07/2026 al momento questa API sembra accettare solo documenti di tipo PDF. Quando la
    # feature sarà in QA verificarne la correttezza, eventualmente anche consultando il portale.
  @happy-path
  Scenario: [PURPOSE_TEMPLATE_ANNOTATION_UPLOAD_01] Per un purpose template in stato DRAFT e' possibile allegare tutti i file del tipo previsto dalla piattaforma
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    And viene aggiunta un'annotazione con testo entro i 2000 caratteri ad una risposta esistente del purpose template
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di caricare uno alla volta il seguente insieme di documenti sulla risk analysis del purpose template
      | pdf  |
      | json |
      | md   |
      | xsd  |
      | yml  |
      | yaml |
      | txt  |
      | wsdl |
    Then tutti i tentativi di caricamento sulla risk analysis del purpose template hanno esito positivo

  @sad-path
  Scenario: [PURPOSE_TEMPLATE_ANNOTATION_UPLOAD_02] Per un purpose template in stato DRAFT il caricamento fallisce per tipi non ammessi o estensioni non valide
    Given l'utente è un "admin" di "PA1"
    And viene creato un nuovo purpose template
    And viene creata una risposta di analisi del rischio "ENTRO I LIMITI CONSENTITI FREE TEXT" per il purpose template creato
    When l'utente è un "admin" di "PA1" con ruolo M2M m2m-admin
    And l'utente tenta di caricare uno alla volta i seguenti tipi documenti sulla risk analysis del purpose template, con l'estensione specificata
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
    Then tutti i tentativi di caricamento sulla risk analysis del purpose template hanno esito negativo


