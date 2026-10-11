Feature: Verifica del contenuto dei differenti tipi di legalFact prodotti nei workflow di notifiche digitali/analogiche

  @legalFact
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_1] Data una notifica analogica, si verifica l'esistenza del legalFact generato in seguito ad accettazione se sia di tipo NOTIFICA PRESA IN CARICO
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "SENDER_ACK"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_PRESA_IN_CARICO"
    #Si verifica la presenza di un insieme di CAMPI ed i corrispondenti VALUE
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Attestazione opponibile a terzi: notifica presa in carico                                                  |
      | MITTENTE                                  | Comune di palermo                                                                                          |
      | CF_MITTENTE                               | 80016350821                                                                                                |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Gherkin                                                                                              |
      | DESTINATARIO_CODICE_FISCALE               | CLMCST42R12D969Z                                                                                           |
      | DESTINATARIO_DOMICILIO_DIGITALE           | testpagopa3@pec.pagopa.it                                                                                  |
      | DESTINATARIO_TIPO_DOMICILIO_DIGITALE      | Domicilio eletto presso la Pubblica Amministrazione mittente ex art.26, comma 5 lettera b del D.L. 76/2020 |
      | DESTINATARIO_INDIRIZZO_FISICO             | Mario Gherkin Presso SCALA B VIA SENZA NOME 87100 COSENZA CS ITALIA                                        |

  @legalFact @deleghe1
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_2] Data una notifica analogica, si verifica l'esistenza del legalFact generato in seguito ad accettazione se sia di tipo NOTIFICA PRESA IN CARICO MULTIDESTINATARIO
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "SENDER_ACK"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO"
    #Se i campi per il DESTINATARIO si ripetono allora vogliamo verificare più destinatari
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Attestazione opponibile a terzi: notifica presa in carico        |
      | MITTENTE                                  | Comune di palermo                                                |
      | CF_MITTENTE                               | 80016350821                                                      |
      # PRIMO DESTINATARIO
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Gherkin                                                    |
      | DESTINATARIO_CODICE_FISCALE               | CLMCST42R12D969Z                                                 |
      | DESTINATARIO_DOMICILIO_DIGITALE           | non fornito dalla PA                                             |
      | DESTINATARIO_TIPO_DOMICILIO_DIGITALE      | non fornito dalla PA                                             |
      | DESTINATARIO_INDIRIZZO_FISICO             | Mario Gherkin Presso SCALA B VIA@OK_890 87100 COSENZA CS ITALIA  |
      # SECONDO DESTINATARIO
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Cucumber                                                   |
      | DESTINATARIO_CODICE_FISCALE               | FRMTTR76M06B715E                                                 |
      | DESTINATARIO_DOMICILIO_DIGITALE           | non fornito dalla PA                                             |
      | DESTINATARIO_TIPO_DOMICILIO_DIGITALE      | non fornito dalla PA                                             |
      | DESTINATARIO_INDIRIZZO_FISICO             | Mario Cucumber Presso SCALA B VIA@OK_890 87100 COSENZA CS ITALIA |

  @legalFact @deleghe1
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_3] Data una notifica analogica, si verifica l'esistenza del legalFact generato in seguito ad accettazione se sia di tipo NOTIFICA PRESA IN CARICO MULTIDESTINATARIO
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    And destinatario Mario Cucumber e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "SENDER_ACK"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_PRESA_IN_CARICO_MULTIDESTINATARIO"
    # Si verifica la presenza di un DESTINATARIO in una specifica posizione di ordinamento
    Then si verifica se il legalFact contiene i campi per il destinatario
      | TITLE                                     | Attestazione opponibile a terzi: notifica presa in carico        |
      | MITTENTE                                  | Comune di palermo                                                |
      | CF_MITTENTE                               | 80016350821                                                      |
      | multiDestinatarioPosition                 | 2                                                                |
      # SECONDO DESTINATARIO
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Cucumber                                                   |
      | DESTINATARIO_CODICE_FISCALE               | FRMTTR76M06B715E                                                 |
      | DESTINATARIO_DOMICILIO_DIGITALE           | non fornito dalla PA                                             |
      | DESTINATARIO_TIPO_DOMICILIO_DIGITALE      | non fornito dalla PA                                             |
      | DESTINATARIO_INDIRIZZO_FISICO             | Mario Cucumber Presso SCALA B VIA@OK_890 87100 COSENZA CS ITALIA |

  @legalFact
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_4] Data una notifica digitale, in seguito al completamento del relativo workflow si verifica l'esistenza del legalFact generato se sia di tipo NOTIFICA DIGITALE
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_SUCCESS_WORKFLOW"
    And ricerca ed effettua download del legalFact con la categoria "DIGITAL_DELIVERY"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_DIGITALE"
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Attestazione opponibile a terzi: notifica digitale                                                         |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Gherkin                                                                                              |
      | DESTINATARIO_CODICE_FISCALE               | CLMCST42R12D969Z                                                                                           |
      | DESTINATARIO_DOMICILIO_DIGITALE           | testpagopa3@pec.pagopa.it                                                                                  |
      | DESTINATARIO_TIPO_DOMICILIO_DIGITALE      | Domicilio eletto presso la Pubblica Amministrazione mittente ex art.26, comma 5 lettera b del D.L. 76/2020 |

  @legalFact
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_5] Data una notifica digitale, in seguito al completamento del relativo workflow ed a presa visione da parte del destinatario, si verifica l'esistenza del legalFact generato se sia di tipo AVVENUTO ACCESSO
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Cucumber" legge la notifica ricevuta
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And "Mario Cucumber" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO"
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Attestazione opponibile a terzi: avvenuto accesso |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Cucumber                                    |
      | DESTINATARIO_CODICE_FISCALE               | FRMTTR76M06B715E                                  |

  @legalFact @deleghe1
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_6] Data una notifica digitale, in seguito al completamento del relativo workflow ed a presa visione da parte del delegato, si verifica l'esistenza del legalFact generato se sia di tipo AVVENUTO ACCESSO DELEGATO
    Given "Mario Gherkin" rifiuta se presente la delega ricevuta "Mario Cucumber"
    Given "Mario Gherkin" viene delegato da "Mario Cucumber"
    And "Mario Gherkin" accetta la delega "Mario Cucumber"
    When viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | comune di milano            |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And la notifica può essere correttamente letta da "Mario Gherkin" con delega
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And "Mario Cucumber" richiede il download dell'attestazione opponibile "RECIPIENT_ACCESS"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_AVVENUTO_ACCESSO_DELEGATO"
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Attestazione opponibile a terzi: avvenuto accesso |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Cucumber                                    |
      | DESTINATARIO_CODICE_FISCALE               | FRMTTR76M06B715E                                  |
      | DELEGATO_NOME_COGNOME_RAGIONE_SOCIALE     | Cristoforo Colombo                                |
      | DELEGATO_CODICE_FISCALE                   | CLMCST42R12D969Z                                  |

  @legalFact
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_7] Data una notifica digitale, in seguito al completamento del relativo workflow ed a presa visione da parte del destinatario, si verifica l'esistenza del legalFact generato se sia di tipo MANCATO RECAPITO
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber |
      | senderDenomination | Comune di palermo          |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "DIGITAL_FAILURE_WORKFLOW"
    And ricerca ed effettua download del legalFact con la categoria "DIGITAL_DELIVERY_FAILURE"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_MANCATO_RECAPITO"
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Attestazione opponibile a terzi: mancato recapito digitale                                                 |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Gherkin                                                                                              |
      | DESTINATARIO_CODICE_FISCALE               | CLMCST42R12D969Z                                                                                           |
      | DESTINATARIO_DOMICILIO_DIGITALE           | test@fail.it                                                                                               |
      | DESTINATARIO_TIPO_DOMICILIO_DIGITALE      | Domicilio eletto presso la Pubblica Amministrazione mittente ex art.26, comma 5 lettera b del D.L. 76/2020 |

  @legalFact
  Scenario: [B2B_LEGAL_FACT_RIMOSSO_DA_SS_BUG_19624] Verifica che per legalFact vecchi 10 o più anni (e dunque rimossi da safeStorage) la get tramite api pubblica restituisca errore 500, mentre da api privata 410
    Given verifico che recuperando un legalFact rimosso da safeStorage, le api restituiscano l'errore corretto

  @legalFact
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_8] In seguito ad un disservizio verificatosi in piattaforma durante la creazione di una notifica, si verifica l'esistenza del legalFact generato se sia di tipo DOWNTIME
    Given vengono letti gli eventi di disservizio degli ultimi 60 giorni relativi alla "creazione notifiche"
    When viene individuato se presente l'evento più recente
    Then si effettua download della relativa attestazione opponibile e si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_DOWNTIME"

  @legalFact
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_9] In seguito ad un disservizio verificatosi in piattaforma durante la visualizzazione di una notifica, si verifica l'esistenza del legalFact generato se sia di tipo DOWNTIME
    Given vengono letti gli eventi di disservizio degli ultimi 60 giorni relativi alla "visualizzazione notifiche"
    When viene individuato se presente l'evento più recente
    Then si effettua download della relativa attestazione opponibile e si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_DOWNTIME"

  @legalFact @ignore
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_10] In seguito ad un disservizio verificatosi in piattaforma durante il workflow di una notifica, si verifica l'esistenza del legalFact generato se sia di tipo DOWNTIME
    Given vengono letti gli eventi di disservizio degli ultimi 60 giorni relativi al "workflow notifiche"
    When viene individuato se presente l'evento più recente
    Then si effettua download della relativa attestazione opponibile e si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_DOWNTIME"

  @legalFact
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_11] Data una notifica, in seguito all'annullamento da parte del mittente si verifica l'esistenza del legalFact generato se sia di tipo NOTIFICATION_CANCELLED
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber cancellazione |
      | senderDenomination | Comune di palermo                        |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    # Given imposto lo iun di SharedSteps a "EGNV-KWEV-JPTX-202610-W-1" e la pa a "Comune_Multi"
    And ricerca ed effettua download del legalFact con la categoria "NOTIFICATION_CANCELLED"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICATION_CANCELLED"
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Dichiarazione annullamento notifica sulla piattaforma SEND |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Gherkin                                              |
      | DESTINATARIO_CODICE_FISCALE               | CLMCST42R12D969Z                                           |
      | MITTENTE                                  | Comune di palermo                                          |

  @legalFact
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_12] Data una notifica analogica a destinatario irreperibile, in seguito a workflow completato si verifica l'esistenza del legalFact generato se sia di tipo ANALOG_FAILURE
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber irreperibile |
      | senderDenomination | Comune di palermo                       |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                       |
      | physicalAddress_address | Via @FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And la PA richiede il download dell'attestazione opponibile "COMPLETELY_UNREACHABLE"
    Then si verifica se il legalFact è di tipo "LEGALFACT_ANALOG_FAILURE"
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Deposito avviso di avvenuta ricezione |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Gherkin Irreperibile                  |
      | DESTINATARIO_CODICE_FISCALE               | 02455090981                           |

  @legalFact
  Scenario: [B2B-LEGALFACT_CONTENT_VERIFY_13] Data una notifica digitale a persona giuridica, in seguito ad avvenuta consegna su PEC si verifica l'esistenza del legalFact generato PEC_RECEIPT se sia di tipo NOTIFICA DIGITALE
    Given viene generata una nuova notifica
      | subject            | invio notifica GA cucumber PEC receipt |
      | senderDenomination | Comune di palermo                      |
    And destinatario GherkinSpa
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_PROGRESS"
    Then la PA richiede il download dell'attestazione opponibile "PEC_RECEIPT"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_DIGITALE"
    Then si verifica se il legalFact contiene i campi
      | TITLE                                     | Attestazione opponibile a terzi: notifica digitale |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | GherkinSpa                                         |
      | DESTINATARIO_CODICE_FISCALE               | 12666810299                                        |

  @legalFact
  Scenario: [FUTR_NOTIFICATION_AAR] Verifica del legalFact di tipo NOTIFICATION_AAR per Modello 890 standard raccomandata AR
    Given viene generata una nuova notifica
      | subject            | invio notifica raccomandata AR |
      | senderDenomination | Comune di palermo              |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "NOTIFICATION_AAR"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICATION_AAR"
    And si verifica se il legalFact contiene i campi
      | TITLE                       | Avviso di avvenuta ricezione |
      | DESTINATARIO_CODICE_FISCALE | CLMCST42R12D969Z             |
      | MITTENTE                    | Comune di palermo            |
    And si verifica se il legalFact contiene al massimo 2 pagine

  @legalFact
  Scenario: [FUTR_NOTIFICATION_AAR_RADD_ALT] Verifica del legalFact di tipo NOTIFICATION_AAR_RADD_ALT per Avviso di ricezione RADD Alternativo
    Given viene generata una nuova notifica
      | subject            | invio notifica RADD Alternativo |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "NOTIFICATION_AAR_RADD_ALT"
    Then si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICATION_AAR_RADD_ALT"
    And si verifica se il legalFact contiene i campi
      | TITLE                       | Avviso di avvenuta ricezione |
      | DESTINATARIO_CODICE_FISCALE | CLMCST42R12D969Z             |
      | MITTENTE                    | Comune di palermo            |
    And si verifica se il legalFact contiene al massimo 2 pagine

  @legalFact
  Scenario: [FUTR_ANALOG_FEEDBACK_AVAILABILITY] Verifica del legalFact di tipo ANALOG_FEEDBACK_AVAILABILITY_STATEMENT per attestazione disponibilità feedback analogico
    Given viene generata una nuova notifica
      | subject            | invio notifica feedback analogico |
      | senderDenomination | Comune di palermo                 |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "ANALOG_FEEDBACK_AVAILABILITY_STATEMENT"
    Then si verifica se il legalFact è di tipo "LEGALFACT_ANALOG_FEEDBACK_AVAILABILITY_STATEMENT"
    And si verifica se il legalFact contiene i campi
      | TITLE       | Dichiarazione data di messa a disposizione dell'esito analogico |
      | MITTENTE    | Comune di palermo                                               |
      | CF_MITTENTE | 80016350821                                                     |
    And si verifica se il legalFact contiene al massimo 2 pagine

  @legalFact
  Scenario: [FUTR_INFORMAL_ANALOG_COMMUNICATION] Verifica del legalFact di tipo INFORMAL_ANALOG_COMMUNICATION per comunicazione bonaria cartacea
    Given viene generata una nuova notifica
      | subject            | invio notifica comunicazione bonaria |
      | senderDenomination | Comune di palermo                    |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "INFORMAL_ANALOG_COMMUNICATION"
    Then si verifica se il legalFact è di tipo "LEGALFACT_INFORMAL_ANALOG_COMMUNICATION"
    And si verifica se il legalFact contiene i campi
      | TITLE                                     | COMUNICAZIONE DA PARTE DI |
      | DESTINATARIO_CODICE_FISCALE               | CLMCST42R12D969Z          |
      | MITTENTE                                  | Comune di palermo         |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Gherkin             |
    And si verifica se il legalFact contiene al massimo 2 pagine

  @legalFact
  Scenario: [FUTR_ANALOG_DELIVERY_TIMEOUT] Verifica del legalFact di tipo ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT per mancato recapito per decorrenza termini
    Given viene generata una nuova notifica
      | subject            | invio notifica decorrenza termini |
      | senderDenomination | Comune di palermo                 |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT"
    Then si verifica se il legalFact è di tipo "LEGALFACT_ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT"
    And si verifica se il legalFact contiene i campi
      | TITLE                                     | Attestazione opponibile a terzi: mancata ricezione esiti dall'addetto al recapito estero |
      | DESTINATARIO_NOME_COGNOME_RAGIONE_SOCIALE | Mario Gherkin                                                                            |
      | DESTINATARIO_CODICE_FISCALE               | CLMCST42R12D969Z                                                                         |
    And si verifica se il legalFact contiene al massimo 2 pagine