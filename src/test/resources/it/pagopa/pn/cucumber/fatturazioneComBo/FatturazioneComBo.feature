@fatturazioneComBo
Feature: Fatturazione delle comunicazioni bonarie (ComBo)

  # Scope: registrazione per la fatturazione delle comunicazioni bonarie concluse o rifiutate, riservatezza del loro costo
  # verso l'ente e non regressione della fatturazione delle notifiche legali (PST "Attività propedeutiche alla
  # fatturazione di ComBo", PN-21786).
  #
  # Gli importi dei costi sono configurati per ambiente: se non vengono passati all'esecuzione si verifica che il costo
  # sia presente, positivo e uguale fra timeline e fatturazione.
  #
  # Limiti non coperti:
  # - Comunicazioni bonarie con più destinatari: oggi gli ambienti ne ammettono uno solo, quindi il costo di rifiuto per
  #   numero di destinatari e la modalità del rifiuto tecnico (uniforme o per destinatario in errore) sono verificati
  #   con un solo destinatario.
  # - Fuori perimetro: fatturazione a fine campagna, tariffe differenziate, invio dei dati al sistema di fatturazione
  #   esterno, conclusione per timeout dell'invio analogico.
  #
  # Rifiuti tecnici (INFORMAL_INVOICING_REFUSED_02_2_A, INFORMAL_INVOICING_NR_04_2_B): il destinatario per cui la ricerca
  # dell'indirizzo non va a buon fine è "PF Errore 500" del mock dei registri, quindi gli scenari richiedono che SEND usi
  # il mock. Il destinatario si può cambiare all'esecuzione con pn.combo-invoicing.address-search-error.{denomination,
  # tax-id,recipient-type} (o con le variabili d'ambiente PN_COMBO_INVOICING_ADDRESS_SEARCH_ERROR_*); per una PG senza
  # PEC va indicata anche una campagna senza PEC per le PG (pn.combo-invoicing.address-search-error.campaign-id).

  # Scenario 1 - Percorso di fatturazione standard

  @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_BASIC_01_1_A] Una comunicazione bonaria diventa fatturabile quando il suo percorso si conclude
    Given il "Comune_Multi" ha inviato una comunicazione bonaria a un destinatario raggiungibile via email
    When il percorso della comunicazione bonaria si conclude
    Then la conclusione è registrata in timeline con il costo previsto per le comunicazioni bonarie
    And la comunicazione bonaria risulta fra quelle da fatturare all'ente nel giorno della conclusione

  @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_BASIC_01_2_A] Una comunicazione bonaria è fatturata una sola volta anche se l'esito cambia dopo la conclusione
    Given il "Comune_Multi" ha inviato una comunicazione bonaria a un destinatario non raggiungibile via email
    And il percorso della comunicazione bonaria si è concluso
    When il destinatario legge la comunicazione bonaria e l'esito cambia
    Then il nuovo esito è registrato in timeline senza costo
    And fra le comunicazioni da fatturare resta solo la prima conclusione, con il suo costo e il suo giorno
    And il nuovo esito non genera ulteriori addebiti

  @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_BASIC_01_3_A] L'invio cartaceo è fatturato insieme alla comunicazione bonaria
    Given il "Comune_Multi" ha inviato una comunicazione bonaria che prevede l'invio cartaceo
    When il percorso della comunicazione bonaria si conclude
    Then la comunicazione bonaria risulta fra quelle da fatturare all'ente nel giorno della conclusione
    And l'invio cartaceo risulta da fatturare insieme alla comunicazione, nel giorno della conclusione

  # Scenario 2 - Fatturazione delle comunicazioni bonarie rifiutate

  @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_REFUSED_02_1_A] Una comunicazione bonaria rifiutata è fatturata con il costo di rifiuto delle bonarie
    When il "Comune_Multi" invia una comunicazione bonaria non conforme a 1 destinatario
    Then il rifiuto è registrato in timeline con un costo pari al costo della comunicazione bonaria per il numero di destinatari
    And la comunicazione rifiutata risulta fra quelle da fatturare all'ente nel giorno del rifiuto
    And non risulta da fatturare alcun invio cartaceo

  @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_REFUSED_02_2_A] Il rifiuto tecnico per ricerca dell'indirizzo non riuscita è fatturato con la modalità prevista per le bonarie
    When il "Comune_Multi" invia una comunicazione bonaria senza indirizzo fisico a un destinatario per cui la ricerca dell'indirizzo da parte di SEND non va a buon fine
    Then il rifiuto è registrato in timeline con il costo previsto per i rifiuti tecnici delle comunicazioni bonarie
    And la comunicazione rifiutata risulta fra quelle da fatturare all'ente nel giorno del rifiuto

  # Scenario 3 - Riservatezza dei costi delle comunicazioni bonarie

  @work-item:QA-18903 @webhook3
  Scenario: [INFORMAL_INVOICING_PRIVACY_03_1_A] Il costo di una comunicazione bonaria conclusa non è esposto all'ente
    Given il "Comune_Multi" riceve gli aggiornamenti di stato delle comunicazioni bonarie
    And il "Comune_Multi" ha inviato una comunicazione bonaria a un destinatario raggiungibile via email
    And il percorso della comunicazione bonaria si è concluso
    When il "Comune_Multi" consulta la comunicazione bonaria e ne riceve gli aggiornamenti di stato
    Then gli eventi esposti all'ente non riportano alcun costo
    And il formato degli eventi esposti all'ente non cambia

  @work-item:QA-18903 @webhook3
  Scenario: [INFORMAL_INVOICING_PRIVACY_03_1_B] Il costo di una comunicazione bonaria rifiutata non è esposto all'ente
    Given il "Comune_Multi" riceve gli aggiornamenti di stato delle comunicazioni bonarie
    And il "Comune_Multi" ha inviato una comunicazione bonaria non conforme a 1 destinatario
    When il "Comune_Multi" consulta la comunicazione bonaria e ne riceve gli aggiornamenti di stato
    Then gli eventi esposti all'ente non riportano alcun costo
    And il formato degli eventi esposti all'ente non cambia

  # Scenario 4 - Non regressione della fatturazione delle notifiche legali

  @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_1_C] Una notifica legale annullata continua a essere fatturata come notifica legale
    Given il "Comune_Multi" ha inviato una notifica legale a un destinatario raggiungibile via PEC
    When il "Comune_Multi" annulla la notifica legale
    Then la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per le notifiche legali

  @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_2_A] Il rifiuto di una notifica legale per documento non disponibile continua a essere fatturato come prima
    When il "Comune_Multi" invia una notifica legale con un documento non disponibile
    Then la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per le notifiche legali
    And la notifica legale non risulta fra le comunicazioni bonarie da fatturare

  @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_2_B] Il rifiuto tecnico di una notifica legale per ricerca dell'indirizzo non riuscita continua a essere fatturato come prima
    When il "Comune_Multi" invia una notifica legale senza indirizzo fisico a un destinatario per cui la ricerca dell'indirizzo da parte di SEND non va a buon fine
    Then la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per i rifiuti tecnici delle notifiche legali

  @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_3_B] Una comunicazione bonaria rifiutata non entra nella fatturazione delle notifiche legali
    When il "Comune_Multi" invia una comunicazione bonaria non conforme a 1 destinatario
    Then la comunicazione bonaria non risulta fra le notifiche legali da fatturare
