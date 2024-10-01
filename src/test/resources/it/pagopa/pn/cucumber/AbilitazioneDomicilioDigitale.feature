Feature: Abilitazione domicilio digitale

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_1] Attivazione del servizio SERCQ SEND per recapito principale e accettazione dei TOS
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_2] Attivazione del servizio SERCQ SEND per recapito principale e presenza del recapito legale PEC
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificata l' assenza di pec inserite per l'utente "Mario Cucumber"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_2] Attivazione del servizio SERCQ SEND per recapito principale e presenza del recapito legale PEC
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene verificata l' assenza di pec inserite per l'utente "Lucio Anneo Seneca"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_3] Disattivazione del servizio SERCQ SEND per recapito principale
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Then viene disabilitato il servizio SERCQ SEND

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_3] Disattivazione del servizio SERCQ SEND per recapito principale
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Then viene disabilitato il servizio SERCQ SEND

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_5] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ
    Given si predispone addressbook per l'utente "Mario Cucumber"
    #Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene verificata la presenza di pec inserite per l'utente "Mario Cucumber"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_5] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene verificata la presenza di pec inserite per l'utente "Lucio Anneo Seneca"


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_6] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ, con OTP errato
    Given si predispone addressbook per l'utente "Mario Cucumber"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserito un recapito legale "example3@pecSuccess.it" con verification code "*$%&+/"
    Then viene verificata l'assenza di  indirizzi Pec per l'utente "Mario Cucumber"
    And viene verificata la presenza di Sercq attivo per l'utente "Mario Cucumber"


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_6] Inserimento indirizzo PEC come recapito principale, dopo attivazione del servizio SERCQ, con OTP errato
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserito un recapito legale "example3@pecSuccess.it" con verification code "*$%&+/"
    Then viene verificata l'assenza di  indirizzi Pec per l'utente "Lucio Anneo Seneca"
    And viene verificata la presenza di Sercq attivo per l'utente "Lucio Anneo Seneca"


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_7] Attivazione del servizio SERCQ SEND per recapito specifico per ente  e accettazione dei TOS
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_7] Attivazione del servizio SERCQ SEND per recapito specifico per ente  e accettazione dei TOS
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_8] Attivazione del servizio SERCQ SEND per recapito specifico per ente
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificata l' assenza di pec inserite per l'utente "Mario Cucumber" per il comune "Comune_Root"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_8] Attivazione del servizio SERCQ SEND per recapito specifico per ente
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificata l' assenza di pec inserite per l'utente "Mario Cucumber" per il comune "Comune_Root"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_9] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene inserito un recapito legale "example@pecSuccess.it" per il comune "Comune_2"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificata la presenza di pec inserite per l'utente "Mario Cucumber" per il comune "Comune_1"
    And viene verificata la presenza di pec inserite per l'utente "Mario Cucumber" per il comune "Comune_2"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_9] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene inserito un recapito legale "example@pecSuccess.it" per il comune "Comune_2"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_Root"
    And viene verificata la presenza di pec inserite per l'utente "Mario Cucumber" per il comune "Comune_1"
    And viene verificata la presenza di pec inserite per l'utente "Mario Cucumber" per il comune "Comune_2"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_10] Attivazione del servizio SERCQ SEND per recapito principale e inserimento della PEC come recapito specifico per ente
    Given si predispone addressbook per l'utente "Mario Cucumber"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene verificata la presenza di pec inserite per l'utente "Mario Cucumber" per il comune "Comune_1"
    #controllo su sercQ attivo?

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_10] Attivazione del servizio SERCQ SEND per recapito principale e inserimento della PEC come recapito specifico per ente
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    And viene verificata la presenza di pec inserite per l'utente "Mario Cucumber" per il comune "Comune_1"
    #controllo su sercQ attivo?

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_11] Inserimento indirizzo PEC come recapito specifico per ente, dopo attivazione del servizio SERCQ per stesso ente
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_11] Inserimento indirizzo PEC come recapito specifico per ente, dopo attivazione del servizio SERCQ per stesso ente
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_12] Inserimento indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    Then viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_12] Inserimento indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    Then viene inserito un recapito legale "example3@pecSuccess.it" per il comune "Comune_Root"

    ##TODO DA RIVEDERE
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_13] Modifica indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    Then viene inserito un recapito legale "example3@pecSuccess.it" modificando recapito esistente per il comune "Comune_Root"

     ##TODO DA RIVEDERE
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_13] Modifica indirizzo PEC come recapito specifico per ente, con PEC già associata per lo stesso ente
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    Then viene inserito un recapito legale "example3@pecSuccess.it" modificando recapito esistente per il comune "Comune_Root"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_14] Elimina indirizzo PEC come recapito specifico per ente con la presenza di una PEC già associata
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    Then viene rimossa se presente la pec di piattaforma di "Mario Cucumber"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_14] Elimina indirizzo PEC come recapito specifico per ente con la presenza di una PEC già associata
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_1"
    Then viene rimossa se presente la pec di piattaforma di "Mario Cucumber"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_15] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti per ente
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    Then viene disabilitato il servizio SERCQ SEND

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_15] Attivazione del servizio SERCQ SEND per recapito specifico per ente e presenza del recapito legale PEC specifico per enti differenti per ente
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    Then viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    Then viene disabilitato il servizio SERCQ SEND

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_16] Attivazione servizio SERCQ e aggiunta recapito di cortesia email
    Given si predispone addressbook per l'utente "Mario Cucumber"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_16] Attivazione servizio SERCQ e aggiunta recapito di cortesia email
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_22] Attivazione del servizio SERCQ SEND per recapito specifico per più enti e presenza del recapito legale PEC princi
    Given si predispone addressbook per l'utente "Mario Cucumber"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_2"
    Then vengono accettati i TOS

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_22] Attivazione del servizio SERCQ SEND per recapito specifico per più enti e presenza del recapito legale PEC princi
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_2"
    Then vengono accettati i TOS

    ##TODO DA TERMINARE
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_59] Creazione notifica digitale con servizio SERCQ attivo e verifica cambiamento workflow della notifica
    Given si predispone addressbook per l'utente "Mario Gherkin"
    And viene attivato il servizio SERCQ SEND per recapito principale
    Given viene generata una nuova notifica
      | subject            | invio notifica a Gherkin |
      | senderDenomination | Comune di milano         |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"
    And si verifica che gli elementi di timeline associati alla notifica siano 4
    And si verifica che lo stato "DELIVERING" sia assente
    Then si verifica che il timestamp dell'evento di Feedback è uguale a quello dell'evento "AAR_GENERATION"
      | NULL | NULL |

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_60] Creazione notifica digitale con servizio SERCQ attivo per ente specifico e verifica workflow notifica previsto per SERCQ
    Given si predispone addressbook per l'utente "Mario Cucumber"
    Then viene inserito un recapito legale "example3@pecSuccess.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    Then viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_60] Creazione notifica digitale con servizio SERCQ attivo per ente specifico e verifica workflow notifica previsto per SERCQ
    Given si predispone addressbook per l'utente "Gherkin spa"
    Then viene inserito un recapito legale "example3@pecSuccess.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    Then viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_61] Creazione notifica digitale verso utente che abbia attivato servizio SERCQ
    Given si predispone addressbook per l'utente "Mario Cucumber"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_1"
    Then viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                               |
      | details                | NOT_NULL                                           |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                  |

  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_61] Creazione notifica digitale verso utente che abbia attivato servizio SERCQ
    Given si predispone addressbook per l'utente "Gherkin spa"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_1"
    Then viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Gherkin spa
    And la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    And si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                               |
      | details                | NOT_NULL                                           |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                  |

#verificare mittente notifica
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF_62] Creazione notifica digitale verso utente che abbia attivato servizio SERCQ
    Given si predispone addressbook per l'utente "Mario Cucumber"
    Then viene inserito un recapito legale "test@fail.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    Then viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And ricerca ed effettua download del legalFact con la categoria "DIGITAL_DELIVERY_FAILURE"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_MANCATO_RECAPITO"
    Then si verifica se il legalFact contiene i campi
      | TITLE | Attestazione opponibile a terzi: mancato recapito digitale |

 #verificare mittente notifica
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_62] Creazione notifica digitale verso utente che abbia attivato servizio SERCQ
    Given si predispone addressbook per l'utente "Gherkin spa"
    Then viene inserito un recapito legale "test@fail.it"
    And viene attivato il servizio SERCQ SEND per il comune "Comune_1"
    Then viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_2" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And ricerca ed effettua download del legalFact con la categoria "DIGITAL_DELIVERY_FAILURE"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_MANCATO_RECAPITO"
    Then si verifica se il legalFact contiene i campi
      | TITLE | Attestazione opponibile a terzi: mancato recapito digitale


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PG_79] Creazione notifica digitale con servizio SERCQ con Indirizzo speciale settato
    Given si predispone addressbook per l'utente "Gherkin spa"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserito un recapito legale "example3@pecSuccess.it"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination            | Gherkin spa               |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"


  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_PF79] Creazione notifica digitale con servizio SERCQ con Indirizzo speciale settato
    Given si predispone addressbook per l'utente "Mario Cucumber"
    Then viene attivato il servizio SERCQ SEND per recapito principale
    And viene inserito un recapito legale "example3@pecSuccess.it"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di milano            |
    And destinatario
      | denomination            | Gherkin spa               |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile_address | testpagopa1@pec.pagopa.it |
    And vengono letti gli eventi fino allo stato della notifica "DELIVERED"





  #TODO: migliorare con un pò di assertion le chiamate e/o con verifica dell'assenza di errori
  #TODO: attenzione a mantenere l'annotation per limitare la concorrenza
  #TODO: viene usata anche sharedStep valutare se spostare gli step (creati appositamente per il test)
  @addressBook1
  Scenario: [ABILITAZIONE_DOMICILIO_DIGITALE_1] invio messaggio di cortesia - invio notifica per email per ente padre per PG
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserito un recapito legale "example2@pecSuccess.it" per il comune "Comune_Root"
    And viene inserito un recapito legale "example3@pecSuccess.it"
    And viene verificata la presenza di pec inserite per l'utente "Lucio Anneo Seneca"
    And viene verificata la presenza di 2 recapiti di cortesia inseriti per l'utente "Lucio Anneo Seneca"
    And viene rimossa se presente la pec di piattaforma di "Lucio Anneo Seneca"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_Root"
    And viene inserita l'email di cortesia "provaemail2@test.it" per il comune "Comune_Root"
