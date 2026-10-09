Feature: Visual Regression Testing dei documenti PDF prodotti da SEND

  # ─────────────────────────────────────────────────────────────────────────
  # Template: SENDER_ACK – Attestazione notifica presa in carico
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @senderAck
  Scenario: [VRT-01] Verifica visual regression del SENDER_ACK – singolo destinatario digitale
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT SENDER_ACK |
      | senderDenomination | Comune di palermo             |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "SENDER_ACK"
    Then si verifica la conformità visiva del PDF con il template "SENDER_ACK"

  @visualTest @senderAck @smoke
  Scenario: [VRT-02] Verifica solo campi dinamici del SENDER_ACK (senza golden master – smoke)
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT SENDER_ACK smoke |
      | senderDenomination | Comune di palermo                   |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "SENDER_ACK"
    Then si verificano i campi dinamici del PDF con il template "SENDER_ACK"

  @visualTest @senderAck @smoke
  Scenario: [VRT-02b] Verifica i campi del SENDER_ACK per il destinatario 1
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT SENDER_ACK destinatario |
      | senderDenomination | Comune di palermo                          |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REQUEST_ACCEPTED"
    And ricerca ed effettua download del legalFact con la categoria "SENDER_ACK"
    And si verificano i campi dinamici del PDF con il template "SENDER_ACK"
    Then per il destinatario 1 del PDF si verificano i seguenti campi
      | campo                            | valore        |
      | Nome e Cognome / Ragione Sociale | Mario Gherkin |

  # Per una notifica multidestinatario basta ripetere lo step cambiando l'indice del
  # destinatario: ciascuna etichetta ("campo") si ripete una volta per blocco e lo step
  # prende automaticamente la sua N-esima occorrenza nel documento.
  #
  # Then per il destinatario 2 del PDF si verificano i seguenti campi
  #   | campo                             | valore            |
  #   | Nome e Cognome / Ragione Sociale  | Luigi Cucumber    |
  #   | Codice Fiscale                    | RSSMRA80A01H501U  |
  #   | Domicilio digitale                | luigi@pec.it      |

  # ─────────────────────────────────────────────────────────────────────────
  # Template: PEC_DELIVERY – Avvenuta ricezione digitale
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @pecDelivery
  Scenario: [VRT-03] Verifica visual regression del PEC_DELIVERY – workflow digitale completato
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT PEC_DELIVERY |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And ricerca ed effettua download del legalFact con la categoria "DIGITAL_DELIVERY"
    Then si verifica la conformità visiva del PDF con il template "PEC_DELIVERY"

  @visualTest @pecDelivery @smoke
  Scenario: [VRT-03-SMOKE] Verifica solo campi dinamici del PEC_DELIVERY (senza golden master – smoke)
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT PEC_DELIVERY smoke |
      | senderDenomination | Comune di palermo                     |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_DOMICILE"
    And ricerca ed effettua download del legalFact con la categoria "DIGITAL_DELIVERY"
    Then si verificano i campi dinamici del PDF con il template "PEC_DELIVERY"

  # ─────────────────────────────────────────────────────────────────────────
  # Template: NOTIFICATION_VIEWED – Attestato avvenuto accesso
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @notificationViewed
  Scenario: [VRT-04] Verifica visual regression del NOTIFICATION_VIEWED – accesso PF
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT NOTIFICATION_VIEWED |
      | senderDenomination | Comune di palermo                      |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And "Mario Gherkin" legge la notifica ricevuta
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    And ricerca ed effettua download del legalFact con la categoria "RECIPIENT_ACCESS"
    Then si verifica la conformità visiva del PDF con il template "NOTIFICATION_VIEWED"

  @visualTest @notificationViewed @smoke
  Scenario: [VRT-04-SMOKE] Verifica solo campi dinamici del NOTIFICATION_VIEWED (senza golden master – smoke)
#    Given viene generata una nuova notifica
#      | subject            | invio notifica VRT NOTIFICATION_VIEWED smoke |
#      | senderDenomination | Comune di palermo                            |
#    And destinatario Mario Gherkin
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And "Mario Gherkin" legge la notifica ricevuta
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_VIEWED"
    Given imposto lo iun di SharedSteps a "PLVK-HADX-VHKG-202610-Q-1" e la pa a "Comune_Multi"
    And ricerca ed effettua download del legalFact con la categoria "RECIPIENT_ACCESS"
    Then si verificano i campi dinamici del PDF con il template "NOTIFICATION_VIEWED"

  # ─────────────────────────────────────────────────────────────────────────
  # Template: ANALOG_FAILURE – Attestato mancato recapito analogico
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @analogFailure
  Scenario: [VRT-05] Verifica visual regression del ANALOG_FAILURE – mancato recapito 890
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT ANALOG_FAILURE |
      | senderDenomination | Comune di palermo                 |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                       |
      | physicalAddress_address | Via @FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    And ricerca ed effettua download del legalFact con la categoria "COMPLETELY_UNREACHABLE"
    Then si verifica la conformità visiva del PDF con il template "ANALOG_FAILURE"

  @visualTest @analogFailure @smoke
  Scenario: [VRT-05-SMOKE] Verifica solo campi dinamici del ANALOG_FAILURE (senza golden master – smoke)
#    Given viene generata una nuova notifica
#      | subject            | invio notifica VRT ANALOG_FAILURE smoke |
#      | senderDenomination | Comune di palermo                       |
#    And destinatario Gherkin Irreperibile e:
#      | digitalDomicile         | NULL                       |
#      | physicalAddress_address | Via @FAIL-Irreperibile_890 |
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Given imposto lo iun di SharedSteps a "UYWA-QKVN-XZNQ-202610-M-1" e la pa a "Comune_Multi"
    And la PA richiede il download dell'attestazione opponibile "COMPLETELY_UNREACHABLE"
    Then si verificano i campi dinamici del PDF con il template "ANALOG_FAILURE"

  # ─────────────────────────────────────────────────────────────────────────
  # Template: NOTIFICATION_CANCELLED – Attestato annullamento
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @notificationCancelled
  Scenario: [VRT-06] Verifica visual regression del NOTIFICATION_CANCELLED
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT NOTIFICATION_CANCELLED |
      | senderDenomination | Comune di palermo                         |
    And destinatario Mario Gherkin
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    And ricerca ed effettua download del legalFact con la categoria "NOTIFICATION_CANCELLED"
    Then si verifica la conformità visiva del PDF con il template "NOTIFICATION_CANCELLED"

  @visualTest @notificationCancelled @smoke
  Scenario: [VRT-06-SMOKE] Verifica solo campi dinamici del NOTIFICATION_CANCELLED (senza golden master – smoke)
#    Given viene generata una nuova notifica
#      | subject            | invio notifica VRT NOTIFICATION_CANCELLED smoke |
#      | senderDenomination | Comune di palermo                               |
#    And destinatario Mario Gherkin
#    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
#    And la notifica "può" essere annullata dal sistema tramite codice IUN dal comune "Comune_Multi"
#    And vengono letti gli eventi fino all'elemento di timeline della notifica "NOTIFICATION_CANCELLED"
    Given imposto lo iun di SharedSteps a "EGNV-KWEV-JPTX-202610-W-1" e la pa a "Comune_Multi"
    And ricerca ed effettua download del legalFact con la categoria "NOTIFICATION_CANCELLED"
    Then si verificano i campi dinamici del PDF con il template "NOTIFICATION_CANCELLED"

  # ─────────────────────────────────────────────────────────────────────────
  # Template: MALFUNCTION – Attestazione malfunzionamento e ripristino
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @malfunction
  Scenario: [VRT-07] Verifica visual regression del MALFUNCTION
    Given vengono letti gli eventi di disservizio degli ultimi 60 giorni relativi alla "creazione notifiche"
    When viene individuato se presente l'evento più recente
    Then si effettua download della relativa attestazione opponibile e si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_DOWNTIME"
    And si verifica la conformità visiva del PDF con il template "MALFUNCTION"

  @visualTest @malfunction @smoke
  Scenario: [VRT-07-SMOKE] Verifica solo campi dinamici del MALFUNCTION (senza golden master – smoke)
    Given vengono letti gli eventi di disservizio degli ultimi 60 giorni relativi alla "creazione notifiche"
    When viene individuato se presente l'evento più recente
    Then si effettua download della relativa attestazione opponibile e si verifica se il legalFact è di tipo "LEGALFACT_NOTIFICA_DOWNTIME"
    And si verificano i campi dinamici del PDF con il template "MALFUNCTION"

  # ─────────────────────────────────────────────────────────────────────────
  # Template: ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @analogDeliveryWorkflowTimeout
  Scenario: [VRT-08] Verifica visual regression del ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT ANALOG_TIMEOUT |
      | senderDenomination | Comune di palermo                 |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                       |
      | physicalAddress_address | Via @FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And ricerca ed effettua download del legalFact con la categoria "ANALOG_FAILURE_WORKFLOW" con DetailCode "TIMEOUT"
    Then si verifica la conformità visiva del PDF con il template "ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT"

  @visualTest @analogDeliveryWorkflowTimeout @smoke
  Scenario: [VRT-08-SMOKE] Verifica solo campi dinamici del ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT (smoke)
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT ANALOG_TIMEOUT smoke |
      | senderDenomination | Comune di palermo                       |
    And destinatario Gherkin Irreperibile e:
      | digitalDomicile         | NULL                       |
      | physicalAddress_address | Via @FAIL-Irreperibile_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_FAILURE_WORKFLOW"
    And ricerca ed effettua download del legalFact con la categoria "ANALOG_FAILURE_WORKFLOW" con DetailCode "TIMEOUT"
    Then si verificano i campi dinamici del PDF con il template "ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT"

  # ─────────────────────────────────────────────────────────────────────────
  # Template: NOTIFICATION_AAR – Avviso di Avvenuta Ricezione standard
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @notificationAar
  Scenario: [VRT-09] Verifica visual regression del NOTIFICATION_AAR
    #TODO: cambiare, genera AAR RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT NOTIFICATION_AAR |
      | senderDenomination | Comune di palermo                   |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR"
    Then si verifica la conformità visiva del PDF con il template "NOTIFICATION_AAR"

  @visualTest @notificationAar @smoke
  Scenario: [VRT-09-SMOKE] Verifica solo campi dinamici del NOTIFICATION_AAR (smoke)
    #TODO: cambiare, genera AAR RADD
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT NOTIFICATION_AAR smoke |
      | senderDenomination | Comune di palermo                         |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR"
    Then si verificano i campi dinamici del PDF con il template "NOTIFICATION_AAR"

  # ─────────────────────────────────────────────────────────────────────────
  # Template: NOTIFICATION_AAR_RADD_ALT – Avviso di Avvenuta Ricezione RADD Alt
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @notificationAarRaddAlt
  Scenario: [VRT-10] Verifica visual regression del NOTIFICATION_AAR_RADD_ALT
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT NOTIFICATION_AAR_RADD_ALT |
      | senderDenomination | Comune di palermo                            |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"
    Then si verifica la conformità visiva del PDF con il template "NOTIFICATION_AAR_RADD_ALT"

  @visualTest @notificationAarRaddAlt @smoke
  Scenario: [VRT-10-SMOKE] Verifica solo campi dinamici del NOTIFICATION_AAR_RADD_ALT (smoke)
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT NOTIFICATION_AAR_RADD_ALT smoke |
      | senderDenomination | Comune di palermo                                  |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "AAR_GENERATION"
    Then download attestazione opponibile AAR e controllo del contenuto del file per verificare se il tipo è "AAR RADD"
    Then si verificano i campi dinamici del PDF con il template "NOTIFICATION_AAR_RADD_ALT"

  # ─────────────────────────────────────────────────────────────────────────
  # Template: ANALOG_FEEDBACK_AVAILABILITY_STATEMENT
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @analogFeedbackAvailabilityStatement
  Scenario: [VRT-11] Verifica visual regression del ANALOG_FEEDBACK_AVAILABILITY_STATEMENT
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT ANALOG_FEEDBACK |
      | senderDenomination | Comune di palermo                  |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And ricerca ed effettua download del legalFact con la categoria "ANALOG_FEEDBACK_STATEMENT"
    Then si verifica la conformità visiva del PDF con il template "ANALOG_FEEDBACK_AVAILABILITY_STATEMENT"

  @visualTest @analogFeedbackAvailabilityStatement @smoke
  Scenario: [VRT-11-SMOKE] Verifica solo campi dinamici del ANALOG_FEEDBACK_AVAILABILITY_STATEMENT (smoke)
    Given viene generata una nuova notifica
      | subject            | invio notifica VRT ANALOG_FEEDBACK smoke |
      | senderDenomination | Comune di palermo                        |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL       |
      | physicalAddress_address | Via@ok_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    And ricerca ed effettua download del legalFact con la categoria "ANALOG_FEEDBACK_STATEMENT"
    Then si verificano i campi dinamici del PDF con il template "ANALOG_FEEDBACK_AVAILABILITY_STATEMENT"

  # ─────────────────────────────────────────────────────────────────────────
  # Template: INFORMAL_ANALOG_COMMUNICATION – Comunicazione bonaria cartacea
  # ─────────────────────────────────────────────────────────────────────────

  @visualTest @informalAnalogCommunication
  Scenario: [VRT-12] Verifica visual regression del INFORMAL_ANALOG_COMMUNICATION
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                           |
      | messageId       | ${NEW-IT}                         |
      | subject         | Test workflow                     |
      | recipientType   | PF                                |
      | taxId           | FRMTTR76M06B715E                  |
      | denomination    | Ettore Fieramosca                 |
      | email           | complaint@simulator.amazonses.com |
      | digitalDomicile | NULL                              |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M006  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And ricerca ed effettua download del legalFact con la categoria "INFORMAL_ANALOG_COMMUNICATION"
    Then si verifica la conformità visiva del PDF con il template "INFORMAL_ANALOG_COMMUNICATION"

  @visualTest @informalAnalogCommunication @smoke
  Scenario: [VRT-12-SMOKE] Verifica solo campi dinamici del INFORMAL_ANALOG_COMMUNICATION (smoke)
    Given l'ente mittente "Comune_Multi" compila una notifica bonaria con i seguenti dati:
      | campaignId      | FattOrd                           |
      | messageId       | ${NEW-IT}                         |
      | subject         | Test workflow                     |
      | recipientType   | PF                                |
      | taxId           | FRMTTR76M06B715E                  |
      | denomination    | Ettore Fieramosca                 |
      | email           | complaint@simulator.amazonses.com |
      | digitalDomicile | NULL                              |
    When viene inviata una nuova notifica bonaria e si attende che vada in stato "ACCEPTED"
    And si attende che venga prodotto l'elemento "SEND_DIGITAL_MESSAGE_FEEDBACK" della notifica bonaria con dettagli
      | details_channel            | EMAIL |
      | details_deliveryDetailCode | M006  |
    And si attende che la notifica bonaria passi in stato "COMPLETED_REACHED"
    And ricerca ed effettua download del legalFact con la categoria "INFORMAL_ANALOG_COMMUNICATION"
    Then si verificano i campi dinamici del PDF con il template "INFORMAL_ANALOG_COMMUNICATION"
