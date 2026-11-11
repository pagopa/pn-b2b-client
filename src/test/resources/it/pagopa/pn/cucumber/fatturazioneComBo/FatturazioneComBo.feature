Feature: Fatturazione delle comunicazioni bonarie (ComBo)

  # Regole di business (SRS "Attività propedeutiche alla fatturazione di ComBo", PN-21786)
  # - Una comunicazione bonaria è fatturabile quando il percorso del destinatario si conclude, qualunque sia l'esito,
  #   oppure quando viene rifiutata. Il costo appartiene alla comunicazione per destinatario: si fattura solo la prima
  #   conclusione di ogni destinatario; un cambio di esito successivo non ha costo e non si fattura.
  # - Alla conclusione si fattura anche l'invio cartaceo (raccomandata semplice, un solo tentativo), con il giorno e l'ora
  #   della conclusione.
  # - Rifiuto: costo della comunicazione bonaria per il numero di destinatari; per i rifiuti tecnici (ricerca dell'indirizzo
  #   non riuscita) vale la modalità configurata per le bonarie (uniforme o per destinatario in errore). Un indirizzo
  #   trovato ma non utilizzabile è un rifiuto ordinario.
  # - Il costo è interno a SEND: non è esposto all'ente. Le bonarie si fatturano separatamente dalle legali e i loro dati
  #   non vengono ancora inviati al sistema di fatturazione esterno.
  #
  # Scenari e ID seguono il PST "Attività propedeutiche alla fatturazione di ComBo"
  # (INFORMAL_INVOICING_<SCENARIO>_<NN>_<caso>_<variante>).
  #
  # Gli importi dei costi sono configurati per ambiente: se non vengono indicati all'esecuzione, si verifica che il costo
  # sia presente, positivo e identico fra timeline e fatturazione (parametri descritti negli step).
  #
  # Gli scenari non ancora eseguibili sono marcati @ignore e con il tag della dipendenza mancante:
  #   @attesaFatturazioneComBo    fatturazione delle comunicazioni bonarie non rilasciata
  #   @verificaDatiUat            dato di test da validare in UAT (destinatario per cui la ricerca dell'indirizzo non riesce, campagna)
  #
  # Comunicazioni bonarie con più destinatari: non coperte. Oggi una comunicazione bonaria ammette un solo destinatario
  # in tutti gli ambienti di test, quindi il costo di rifiuto "per numero di destinatari" e la modalità del rifiuto
  # tecnico si verificano con un solo destinatario.

  # =========================================================================================
  # SCENARIO 1 - Percorso di fatturazione standard
  # =========================================================================================

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_BASIC_01_1_A] Una comunicazione bonaria diventa fatturabile quando il suo percorso si conclude
    Given il "Comune_Multi" ha inviato una comunicazione bonaria a un destinatario raggiungibile via email
    When il percorso della comunicazione bonaria si conclude
    Then la conclusione è registrata in timeline con il costo previsto per le comunicazioni bonarie
    And la comunicazione bonaria risulta fra quelle da fatturare all'ente nel giorno della conclusione

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_BASIC_01_2_A] Una comunicazione bonaria è fatturata una sola volta anche se l'esito cambia dopo la conclusione
    Given il "Comune_Multi" ha inviato una comunicazione bonaria a un destinatario non raggiungibile via email
    And il percorso della comunicazione bonaria si è concluso
    When il destinatario legge la comunicazione bonaria e l'esito cambia
    Then il nuovo esito è registrato in timeline senza costo
    And fra le comunicazioni da fatturare resta solo la prima conclusione, con il suo costo e il suo giorno
    And il nuovo esito non genera ulteriori addebiti

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_BASIC_01_3_A] L'invio cartaceo è fatturato insieme alla comunicazione bonaria
    Given il "Comune_Multi" ha inviato una comunicazione bonaria che prevede l'invio cartaceo
    When il percorso della comunicazione bonaria si conclude
    Then la comunicazione bonaria risulta fra quelle da fatturare all'ente nel giorno della conclusione
    And l'invio cartaceo risulta da fatturare insieme alla comunicazione, nel giorno della conclusione

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @verificaDatiUat @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_BASIC_01_4_A] Una comunicazione bonaria non è fatturata prima della conclusione del suo percorso
    Given il "Comune_Multi" ha inviato una comunicazione bonaria il cui percorso prevede più tentativi di recapito
    When il destinatario riceve la comunicazione bonaria sul primo canale previsto dalla campagna
    Then la comunicazione bonaria non risulta fra quelle da fatturare finché il percorso non si conclude
    And gli eventi del percorso non riportano alcun costo

  # =========================================================================================
  # SCENARIO 2 - Fatturazione delle comunicazioni bonarie rifiutate
  # =========================================================================================

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_REFUSED_02_1_A] Una comunicazione bonaria rifiutata è fatturata con il costo di rifiuto delle bonarie
    When il "Comune_Multi" invia una comunicazione bonaria non conforme a 1 destinatario
    Then il rifiuto è registrato in timeline con un costo pari al costo della comunicazione bonaria per il numero di destinatari
    And la comunicazione rifiutata risulta fra quelle da fatturare all'ente nel giorno del rifiuto
    And non risulta da fatturare alcun invio cartaceo

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @verificaDatiUat @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_REFUSED_02_2_A] Il rifiuto tecnico per ricerca dell'indirizzo non riuscita è fatturato con la modalità prevista per le bonarie
    When il "Comune_Multi" invia una comunicazione bonaria senza indirizzo fisico a un destinatario per cui la ricerca dell'indirizzo da parte di SEND non va a buon fine
    Then il rifiuto è registrato in timeline con il costo previsto per i rifiuti tecnici delle comunicazioni bonarie
    And la comunicazione rifiutata risulta fra quelle da fatturare all'ente nel giorno del rifiuto

  # =========================================================================================
  # SCENARIO 3 - Riservatezza dei costi delle comunicazioni bonarie
  # =========================================================================================

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_PRIVACY_03_1_A] Il costo di una comunicazione bonaria conclusa non è esposto all'ente
    Given il "Comune_Multi" riceve gli aggiornamenti di stato delle comunicazioni bonarie
    And il "Comune_Multi" ha inviato una comunicazione bonaria a un destinatario raggiungibile via email
    And il percorso della comunicazione bonaria si è concluso
    When il "Comune_Multi" consulta la comunicazione bonaria e ne riceve gli aggiornamenti di stato
    Then gli eventi esposti all'ente non riportano alcun costo
    And il formato degli eventi esposti all'ente non cambia

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_PRIVACY_03_1_B] Il costo di una comunicazione bonaria rifiutata non è esposto all'ente
    Given il "Comune_Multi" riceve gli aggiornamenti di stato delle comunicazioni bonarie
    And il "Comune_Multi" ha inviato una comunicazione bonaria non conforme a 1 destinatario
    When il "Comune_Multi" consulta la comunicazione bonaria e ne riceve gli aggiornamenti di stato
    Then gli eventi esposti all'ente non riportano alcun costo
    And il formato degli eventi esposti all'ente non cambia

  # =========================================================================================
  # SCENARIO 4 - Non regressione della fatturazione delle notifiche legali
  # =========================================================================================

  @fatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_1_A] Una notifica legale visualizzata continua a essere fatturata come notifica legale
    Given il "Comune_Multi" ha inviato una notifica legale a un destinatario raggiungibile via PEC
    When il destinatario visualizza la notifica legale
    Then la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per le notifiche legali
    And la notifica legale non risulta fra le comunicazioni bonarie da fatturare

  @fatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_1_B] Una notifica legale perfezionata dopo l'invio cartaceo continua a essere fatturata come notifica legale
    Given il "Comune_Multi" ha inviato una notifica legale a un destinatario raggiungibile solo con raccomandata
    When la notifica legale si perfeziona per decorrenza termini dopo l'invio cartaceo
    Then la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per le notifiche legali
    And l'invio cartaceo risulta fra le notifiche legali da fatturare insieme alla notifica
    And la notifica legale non risulta fra le comunicazioni bonarie da fatturare

  @fatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_1_C] Una notifica legale annullata continua a essere fatturata come notifica legale
    Given il "Comune_Multi" ha inviato una notifica legale a un destinatario raggiungibile via PEC
    When il "Comune_Multi" annulla la notifica legale
    Then la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per le notifiche legali
    And la notifica legale non risulta fra le comunicazioni bonarie da fatturare

  @fatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_2_A] Il rifiuto di una notifica legale per documento non disponibile continua a essere fatturato come prima
    When il "Comune_Multi" invia una notifica legale con un documento non disponibile
    Then la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per le notifiche legali

  @fatturazioneComBo @verificaDatiUat @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_2_B] Il rifiuto tecnico di una notifica legale per ricerca dell'indirizzo non riuscita continua a essere fatturato come prima
    When il "Comune_Multi" invia una notifica legale senza indirizzo fisico a un destinatario per cui la ricerca dell'indirizzo da parte di SEND non va a buon fine
    Then la notifica legale risulta fra le notifiche legali da fatturare con il costo previsto per i rifiuti tecnici delle notifiche legali

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_3_A] Una comunicazione bonaria conclusa non entra nella fatturazione delle notifiche legali
    Given il "Comune_Multi" ha inviato una comunicazione bonaria a un destinatario raggiungibile via email
    When il percorso della comunicazione bonaria si conclude
    Then la comunicazione bonaria non risulta fra le notifiche legali da fatturare

  @fatturazioneComBo @ignore @attesaFatturazioneComBo @work-item:QA-18903
  Scenario: [INFORMAL_INVOICING_NR_04_3_B] Una comunicazione bonaria rifiutata non entra nella fatturazione delle notifiche legali
    When il "Comune_Multi" invia una comunicazione bonaria non conforme a 1 destinatario
    Then la comunicazione bonaria non risulta fra le notifiche legali da fatturare
