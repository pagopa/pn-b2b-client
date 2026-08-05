Feature: avanzamento notifiche b2b con workflow cartaceo gestione giacenza atto giudiziario 890

  @giacenza890Simplified @fixCadArcad
  Scenario: [B2B_GIACENZA_890_WI1.1_1] Consegnata atto in Giacenza prima dei 10 giorni.
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-lte10_890     |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@OK-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_deliveryDetailCode | CON080   |
      | details_recIndex           | 0        |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_deliveryDetailCode | RECAG010 |
      | details_recIndex           | 0        |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_deliveryDetailCode | RECAG011A |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL |
      | details_deliveryDetailCode | RECAG012 |
      | details_recIndex           | 0        |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_deliveryDetailCode | CON080   |
      | details_recIndex           | 0        |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_deliveryDetailCode | RECAG005B                   |
      | details_recIndex           | 0                           |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_deliveryDetailCode | RECAG005B                 |
      | details_recIndex           | 0                         |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_deliveryDetailCode | RECAG005C |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
    #And si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT
  #"@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.30s-RECAG012.5s-RECAG005A.5s-RECAG005B[DOC:ARCAD;DOC:23L].5s-RECAG005C"

    #SEND_ANALOG_PROGRESS
  #CON080 - Stampato ed Imbustato.

  #SEND_ANALOG_PROGRESS nn sono OK e ne KO
  #inesito RECAG010 - Atto Giudiziario (890)
  #RECRS010 - Raccomandata Semplice (RS)
  #RECRN010 - Raccomandata con ricevuta di ritorno (AR)
  #RECAG012 - ""

  #SEND_ANALOG_FEEDBACK
  #PNAG012 - Perfezionamento giudiziario della notifica - Il destinatario non ha ritirato la raccomandata 890 numero {{numero raccomandata}} presso il punto di giacenza entro 10 giorni. SEND_ANALOG_FEEDBACK

    #SEND_ANALOG_PROGRESS
  #RECAG005A - Consegnato presso Punti di Giacenza - pre-esito
  #RECAG006A - Consegna a persona abilitata presso Punti di Giacenza - pre-esito
  #RECAG007A - Mancata consegna presso Punti di Giacenza - pre-esito
  #RECAG008A - Compiuta giacenza - pre-esito

  #SEND_ANALOG_PROGRESS
  #RECAG005B - Consegnato presso Punti di Giacenza In Dematerializzazione - 23L**
  #RECAG006B - Consegna a persona delegata presso Punti di Giacenza In Dematerializzazione - 23L**
  #RECAG007B - Mancata consegna presso Punti di Giacenza In Dematerializzazione - C'è un nuovo documento allegato 23L o plico
  #RECAG008B - Compiuta giacenza In Dematerializzazione -  Plico -C'è un nuovo documento allegato  plico
  #RECAG011B - Dematerializzazione 23L

  #SEND_ANALOG_FEEDBACK | SEND_ANALOG_PROGRESS
  #RECAG005C -- Consegnato presso Punti di Giacenza -La raccomandata 890 numero {{numero raccomandata}} è stata ritirata presso il punto di giacenza.
  #RECAG006C -- Consegna a persona delegata presso Punti di Giacenza -La raccomandata 890 numero {{numero raccomandata}} è stata ritirata da una persona delegata presso il punto di giacenza
  #RECAG007C -- Mancata consegna presso Punti di Giacenza -Il destinatario ha rifiutato il ritiro della raccomandata 890 numero {{numero raccomandata}} presso il punto di giacenza.
  #RECAG008C -- Compiuta giacenza. Il destinatario non ha ritirato la raccomandata 890 numero {{numero raccomandata}} presso il punto di giacenza entro 6 mesi.


  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI1.1_2] Consegnato in Giacenza dopo i 10 giorni. In questo scenario viene simulato il perfezionamento dell’atto al 10° giorno.
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-gt10_890     |
      | taxId                   | CLMCST42R12D969Z         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | via@OK-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                      |
      | details_recIndex           | 0                                                                                                                                                                                                             |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                      |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                             |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@OK-GIACENZA-GT10_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                            |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG011B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | NULL | NULL |
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD;DOC:23L].60s-RECAG005A.5s-RECAG005C"


  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI1.1_3] Consegnato in Giacenza dopo i 10 giorni. In questo scenario viene simulato il perfezionamento dell’atto al 10° giorno
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-gt10-23L_890     |
      | taxId                   | CLMCST42R12D969Z             |
      | digitalDomicile         | NULL                         |
      | physicalAddress_address | via@OK-Giacenza-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    #[vecchio]  And si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                          |
      | details_recIndex           | 0                                                                                                                                                                                                                 |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                          |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                                 |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@OK-GIACENZA-GT10-23L_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                                |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG005B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005C |
      | details_sentAttemptMade    | 0         |
    #[si tenta di non valorizzare il secondo parametro] And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista

  #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD].5s-RECAG005A.5s-RECAG005B[DOC:23L].5s-RECAG005C"

  @giacenza890Simplified @esposizioneCadArcad
  Scenario: [B2B_GIACENZA_890_WI1.1_4] Consegnato a persona delegata in Giacenza prima dei 10 giorni.
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-GiacenzaDelegato-lte10_890     |
      | taxId                   | CLMCST42R12D969Z                  |
      | digitalDomicile         | NULL                              |
      | physicalAddress_address | via@OK-GiacenzaDelegato-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    #[vecchio]  And si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                               |
      | details_recIndex           | 0                                                                                                                                                                                                                      |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                               |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                                      |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@OK-GIACENZADELEGATO-LTE10_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                                     |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG006B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                          |
      | details_recIndex           | 0                                 |
      | details_deliveryDetailCode | RECAG006B                         |
      | details_sentAttemptMade    | 0                                 |
      | legalFactsIds              | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments        | [{"documentType": "ARCAD"}]       |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG006C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | NULL | NULL |
  #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.30s-RECAG012.5s-RECAG006A.5s-RECAG006B[DOC:ARCAD;DOC:23L].5s-RECAG006C"


  @giacenza890Simplified @esposizioneCadArcad
  Scenario: [B2B_GIACENZA_890_WI1.1_5] Consegnato a persona delegata in Giacenza dopo dei 10 giorni. In questo scenario viene simulato il perfezionamento dell’atto al 10° giorno
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-GiacenzaDelegato-gt10_890     |
      | taxId                   | CLMCST42R12D969Z                 |
      | digitalDomicile         | NULL                             |
      | physicalAddress_address | via@OK-GiacenzaDelegato-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    #[vecchio] And si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                              |
      | details_recIndex           | 0                                                                                                                                                                                                                     |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                              |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                                     |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@OK-GIACENZADELEGATO-GT10_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                                    |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG011B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                          |
      | details_recIndex           | 0                                 |
      | details_deliveryDetailCode | RECAG011B                         |
      | details_sentAttemptMade    | 0                                 |
      | legalFactsIds              | [{"category": "ANALOG_DELIVERY"}] |
      | details_attachments        | [{"documentType": "ARCAD"}]       |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG006C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | NULL | NULL |
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD;DOC:23L].60s-RECAG006A.5s-RECAG006C"


  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI1.1_6] Consegnato a persona delegata in Giacenza dopo dei 10 giorni. In questo scenario viene simulato il perfezionamento dell’atto al 10° giorno
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-GiacenzaDelegato-gt10-23L_890     |
      | taxId                   | CLMCST42R12D969Z                     |
      | digitalDomicile         | NULL                                 |
      | physicalAddress_address | via@OK-GiacenzaDelegato-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    # [vecchio] And si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                                  |
      | details_recIndex           | 0                                                                                                                                                                                                                         |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                                  |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                                         |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@OK-GIACENZADELEGATO-GT10-23L_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                                        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG006B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG006C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | NULL | NULL |
  #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD].5s-RECAG006A.5s-RECAG006B[DOC:23L].5s-RECAG006C"


  @giacenza890Simplified @fixCadArcad
  Scenario: [B2B_GIACENZA_890_WI1.1_7] Mancata Consegna in Giacenza prima dei 10 giorni.
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | FAIL-Giacenza-lte10_890     |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@FAIL-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_deliveryDetailCode | CON080   |
      | details_recIndex           | 0        |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_deliveryDetailCode | RECAG010 |
      | details_recIndex           | 0        |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_deliveryDetailCode | RECAG011A |
      | details_recIndex           | 0         |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_deliveryDetailCode | RECAG007B                   |
      | details_recIndex           | 0                           |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_deliveryDetailCode | RECAG007B                   |
      | details_recIndex           | 0                           |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG012 |
      | details_sentAttemptMade    | 0        |
  #"@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.30s-RECAG012.5s-RECAG007A.5s-RECAG007B[DOC:ARCAD;DOC:Plico].5s-RECAG007C"

  @giacenza890Simplified  @esposizioneCadArcad
  Scenario: [B2B_GIACENZA_890_WI1.1_8] Mancata Consegna in Giacenza dopo i 10 giorni. In questo scenario viene simulato il perfezionamento dell’atto al 10° giorno.
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | FAIL-Giacenza-gt10_890     |
      | taxId                   | CLMCST42R12D969Z           |
      | digitalDomicile         | NULL                       |
      | physicalAddress_address | via@FAIL-Giacenza-gt10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                        |
      | details_recIndex           | 0                                                                                                                                                                                                               |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                        |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                               |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@FAIL-GIACENZA-GT10_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                              |
    # TODO come fa lo status a essere OK e non KO se la sequenza è di tipo fail?
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG011B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG011B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG007B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG007C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | NULL | NULL |
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD;DOC:23L].60s-RECAG007A.5s-RECAG007B[DOC:Plico].5s-RECAG007C"

  @giacenza890Simplified  @esposizioneCadArcad
  Scenario: [B2B_GIACENZA_890_WI1.1_82] Invio notifica analogica ed attesa elemento di timeline SEND_ANALOG_FEEDBACK e secondo controllo per recepire campo foreignState tramite recupero indirizzo da Registro Imprese.
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
    And destinatario
      | denomination            | Vita Nova Sas                |
      | taxId                   | 12666810299                  |
      | digitalDomicile         | NULL                         |
      | physicalAddress_address | via@FAIL-Giacenza-gt10_890   |
      | recipientType           | PG                           |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                        |
      | details_recIndex           | 0                                                                                                                                                                                                               |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                        |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                               |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@FAIL-GIACENZA-GT10_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                              |
    And viene verificato che nell'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" siano configurati i campi municipalityDetails e foreignState

  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI1.1_9] Mancata Consegna in Giacenza dopo i 10 giorni. In questo scenario viene simulato il perfezionamento dell’atto al 10° giorno.
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | FAIL-Giacenza-gt10-23L_890     |
      | taxId                   | CLMCST42R12D969Z               |
      | digitalDomicile         | NULL                           |
      | physicalAddress_address | via@FAIL-Giacenza-gt10-23L_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG010"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011A"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "23L" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007B" e verifica tipo DOC "Plico" tentativo "ATTEMPT_0"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG007C"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "ANALOG_SUCCESS_WORKFLOW"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
  #"@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD].5s-RECAG007A.5s-RECAG007B[DOC:23L;DOC:Plico].5s-RECAG007C"


  @giacenza890Simplified @esposizioneCadArcad
  Scenario: [B2B_GIACENZA_890_WI1.1_10] Compiuta Giacenza. In questo scenario viene simulato il perfezionamento dell’atto al 10° giorno
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-CompiutaGiacenza_890     |
      | taxId                   | CLMCST42R12D969Z            |
      | digitalDomicile         | NULL                        |
      | physicalAddress_address | via@OK-CompiutaGiacenza_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    #[vecchio]And si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                         |
      | details_recIndex           | 0                                                                                                                                                                                                                |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                         |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                                |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@OK-COMPIUTAGIACENZA_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                               |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG011B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG011B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG008B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG008C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | NULL | NULL |
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD;DOC:23L].60s-RECAG008A.5s-RECAG008B[DOC:Plico].5s-RECAG008C"

  @esposizioneCadArcad
  Scenario: [B2B_GIACENZA_890_WI1.1_10_ALLEGATI] Compiuta Giacenza. Verifica degli allegati CAD, 23L
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-GiacenzaCAD-lte10_890     |
      | taxId                   | CLMCST42R12D969Z             |
      | digitalDomicile         | NULL                         |
      | physicalAddress_address | via@OK-GiacenzaCAD-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    #[vecchio]And si verifica che il timestamp dell'elemento di timeline della notifica SEND_ANALOG_FEEDBACK con deliveryDetailCode RECAG012 sia uguale al timestamp di REFINEMENT
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                          |
      | details_recIndex           | 0                                                                                                                                                                                                                 |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                          |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                                 |
      | details_physicalAddress    | {"at": "Presso", "address": "VIA@OK-GIACENZACAD-LTE10_890", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                                |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG005B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG005B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "CAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | details | NOT_NULL |
    #"@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.5s-RECAG012.5s-RECAG005A.5s-RECAG005B[DOC:CAD;DOC:23L].5s-RECAG005C"

  @esposizioneCadArcad
  Scenario: [ARCAD_FULL_DIGITAL_1] Verifica allegato ARCAD per secondo evento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode = RECAG011B
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                         |
      | physicalAddress_address | via@OK-Giacenza-gt10_890_ZIP |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG012 |
      | details_sentAttemptMade    | 0        |
      | details_responseStatus     | OK       |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG011B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_EXTERNAL_LEGAL_FACTS.+\.zip"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG011B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_EXTERNAL_LEGAL_FACTS.+\.zip"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG005C |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | details | NOT_NULL |
    #"@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.60s-RECAG012.5s-RECAG011B[DOC:ARCAD#Z3;DOC:23L#Z5].60s-RECAG005A.5s-RECAG005C"

  @giacenza890Simplified @fixCadArcad
  Scenario: [B2B_GIACENZA_890_WI1.1_11] Attesa elemento di timeline REFINEMENT con physicalAddress OK-WO-011B (TEST TECNICO)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL           |
      | physicalAddress_address | via@OK-WO-011B |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_deliveryDetailCode | RECAG011B                   |
      | details_recIndex           | 0                           |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "ARCAD"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_deliveryDetailCode | RECAG011B                 |
      | details_recIndex           | 0                         |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details_deliveryDetailCode | RECAG012 |
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_sentAttemptMade    | 0        |
    #"@sequence.5s-RECAG011B[DOC:ARCAD].5s-RECAG011B[DOC:23L].5m-RECAG012"

  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI1.1_12]  Invio notifica con percorso analogico (OK-GiacenzaDelegato-lte10_890_redrive)  per verificare evento fuori sequenza che produce un redrive automatico di paper channel
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL                                   |
      | physicalAddress_address | @OK-GiacenzaDelegato-lte10_890_redrive |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_DOMICILE"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG006B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                    | NOT_NULL                                                                                                                                                                                                                    |
      | details_recIndex           | 0                                                                                                                                                                                                                           |
      | details_deliveryDetailCode | RECAG012                                                                                                                                                                                                                    |
      | details_sentAttemptMade    | 0                                                                                                                                                                                                                           |
      | details_physicalAddress    | {"at": "Presso", "address": "@OK-GIACENZADELEGATO-LTE10_890_REDRIVE", "addressDetails": "SCALA B", "zip": "87100", "municipality": "COSENZA", "municipalityDetails": "COSENZA", "province": "CS", "foreignState": "ITALIA"} |
      | details_responseStatus     | OK                                                                                                                                                                                                                          |
    And viene verificato che l'elemento di timeline "ANALOG_SUCCESS_WORKFLOW" esista
      | loadTimeline | true |
    #"sequenceName": "OK-GiacenzaDelegato-lte10_890_redrive", "sequence": "@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.30s-RECAG006A.5s-RECAG006B[DOC:ARCAD;DOC:23L].60s-RECAG006C.60s-RECAG012"
    #Risultato atteso: l’evento fuori ordine viene inserito nella tabella degli errori e recuperato automaticamente da paper channel all’arrivo dell’evento RECAG012

  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI1.1_13]  Invio notifica con percorso analogico (FAIL-Giacenza-gt10_890_no_recag012)  per verificare che paper channel calcoli la data di perfezionamento e invii il PNAG012 come feedback poichè oltre i 10 giorni
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | PF non censito                      |
      | taxId                   | STTSGT90A01H501J                    |
      | digitalDomicile         | NULL                                |
      | physicalAddress_address | @FAIL-Giacenza-gt10_890_no_recag012 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON080   |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | CON020   |
      | details_sentAttemptMade    | 0        |
    And abbia anche un valore per il campo "details_attachments[0]_url" compatibile con l'espressione regolare ".+PN_PRINTED.+\.pdf"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | RECAG010 |
      | details_sentAttemptMade    | 0        |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL  |
      | details_recIndex           | 0         |
      | details_deliveryDetailCode | RECAG011A |
      | details_sentAttemptMade    | 0         |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                  |
      | details_recIndex           | 0                         |
      | details_deliveryDetailCode | RECAG011B                 |
      | details_sentAttemptMade    | 0                         |
      | details_attachments        | [{"documentType": "23L"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_PROGRESS" esista
      | details                    | NOT_NULL                    |
      | details_recIndex           | 0                           |
      | details_deliveryDetailCode | RECAG007B                   |
      | details_sentAttemptMade    | 0                           |
      | details_attachments        | [{"documentType": "Plico"}] |
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | loadTimeline               | true     |
      | details                    | NOT_NULL |
      | details_recIndex           | 0        |
      | details_deliveryDetailCode | PNAG012  |
      | details_sentAttemptMade    | 0        |
    #"@sequence.5s-CON080.5s-CON020[DOC:7ZIP;PAGES:3].5s-RECAG010.5s-RECAG011A.60s-RECAG011B[DOC:ARCAD;DOC:23L].60s-RECAG007A.5s-RECAG007B[DOC:Plico].5s-RECAG007C"

 # SEND_ANALOG_FEEDBACK------------------
  #deliveryDetailCode: RECRN002C

  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI1.1_14] Non Attesa elemento di timeline SEND_ANALOG_FEEDBACK con physicalAddress OK-NO012-lte10 (Scenario negativo)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL               |
      | physicalAddress_address | via@OK-NO012-lte10 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG010"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011A"
    #And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG005B" e verifica tipo DOC "23L"
    And viene controllato che l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" non esiste
    #"sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.35s-RECAG005A.5s-RECAG005B[DOC:ARCAD;DOC:23L].5s-RECAG005C"

  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI1.1_15] Non Attesa elemento di timeline SEND_ANALOG_FEEDBACK con physicalAddress OK-NO012-gt10 (Scenario negativo)
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario Mario Gherkin e:
      | digitalDomicile         | NULL              |
      | physicalAddress_address | via@OK-NO012-gt10 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG010"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG011A"
    And viene controllato che l'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" non esiste
    #"sequence": "@sequence.5s-CON080.5s-RECAG010.5s-RECAG011A.65s-RECAG011B[DOC:ARCAD].60s-RECAG005A.5s-RECAG005B[DOC:23L].5s-RECAG005C"


  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI2.2_16] Invio notifica con sequence @OK-Giacenza-lte10_890 ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECAG010
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza-gt10_890      |
      | taxId                   | CLMCST42R12D969Z          |
      | digitalDomicile         | NULL                      |
      | physicalAddress_address | via@OK-Giacenza-lte10_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECAG010"

  @giacenza890Simplified @ignore
  Scenario: [B2B_GIACENZA_890_WI2.2_17] Invio notifica con sequence @OK-Giacenza_RS ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECRS010
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | OK-Giacenza_RS     |
      | taxId                   | CLMCST42R12D969Z   |
      | digitalDomicile         | NULL               |
      | physicalAddress_address | via@OK-Giacenza_RS |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON080"
  #"sequence": "@sequence.5s-CON080.5s-RECRS010.5s-RECRS011.5s-RECRN003A.5s-RECRN003B[DOC:AR].5s-RECRN003C"

  @giacenza890Simplified
  Scenario: [B2B_GIACENZA_890_WI2.2_18] Invio notifica con sequence @OK-WO-Giacenza_AR ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECRN010
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | OK-WO-Giacenza_AR     |
      | taxId                   | CLMCST42R12D969Z      |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | via@OK-WO-Giacenza_AR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN010"

  @giacenza890Simplified @ignore
  Scenario: [B2B_GIACENZA_890_WIX.X_19] Invio notifica con sequence @OK-WO-Giacenza_AR ed attesa elemento di timeline SEND_ANALOG_PROGRESS con deliveryDetailCode RECRS010
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | FAIL-Giacenza-Missing_11A_890     |
      | taxId                   | CLMCST42R12D969Z                  |
      | digitalDomicile         | NULL                              |
      | physicalAddress_address | via@FAIL-Giacenza-Missing_11A_890 |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRS011"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECAG012"


  @giacenza890Simplified @ignore
  Scenario: [B2B_GIACENZA_890_WIX.X_20] Invio notifica con sequence @OK-Giacenza_890_refine_before_switch ed attesa elemento di timeline REFINEMENT
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | FAIL-Giacenza-Missing_11A_890         |
      | taxId                   | CLMCST42R12D969Z                      |
      | digitalDomicile         | NULL                                  |
      | physicalAddress_address | @OK-Giacenza_890_refine_before_switch |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @giacenza890Simplified @ignore
  Scenario: [B2B_GIACENZA_890_WIX.X_21] Invio notifica con sequence @OK-Giacenza_890_refine_after_switch ed attesa elemento di timeline REFINEMENT
    Given viene generata una nuova notifica
      | subject            | notifica analogica con cucumber |
      | senderDenomination | Comune di palermo               |
    And destinatario
      | denomination            | FAIL-Giacenza-Missing_11A_890        |
      | taxId                   | CLMCST42R12D969Z                     |
      | digitalDomicile         | NULL                                 |
      | physicalAddress_address | @OK-Giacenza_890_refine_after_switch |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"

  @perfezionamentoAR @workflowAnalogico
  Scenario Outline: [B2B_PERFEZIONAMENTO_AR_1] Verifica che il deliveryDetailCode del feedback sia PNRN012 con timestamp pari a RECRN010+10gg quando lo scarto tra RECRN010 e il secondo evento è superiore a 10 giorni
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | userTest         |
      | taxId                   | CLMCST42R12D969Z |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | <sequenceName>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN010"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<expectedDeliveryDetailCode>"
    And lo scarto temporale tra "RECRN010" e "<expectedDeliveryDetailCode>" è superiore a <intervallo>
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "PNRN012"
    #i 10 giorni (per ovvie ragioni di tempistiche) in fase di test diventano 1 minuto
    And lo scarto temporale tra "RECRN010" e "PNRN012" è pari a 1 minuti
    Examples:
      | sequenceName                 | expectedDeliveryDetailCode | intervallo |
      #per i seguenti due casi, i 10 giorni diventano 1 minuto (parametro: RefinementDuration)
      | Via@OK-Giacenza-gt10_AR      | RECRN003A                  | 1 minuti   |
      | Via@FAIL-Giacenza-gt10_AR    | RECRN004A                  | 1 minuti   |
      #in questo caso, i 30 giorni diventano 80 secondi (parametro: CompiutaGiacenzaArDuration)
      | Via@FAIL-CompiutaGiacenza_AR | RECRN005A                  | 80 secondi |

  @perfezionamentoAR @workflowAnalogico
  Scenario Outline: [B2B_PERFEZIONAMENTO_AR_2] Verifica che quando l'intervallo tra RECRN010 e l'expectedDeliveryDetailCode è inferiore a 10gg, venga generato un SEND_ANALOG_FEEDBACK con deliveryDetailCode = expectedFeedbackDeliveryDetailCode
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | userTest         |
      | taxId                   | CLMCST42R12D969Z |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | <sequenceName>   |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "RECRN010"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "<expectedDeliveryDetailCode>"
    And lo scarto temporale tra "RECRN010" e "<expectedDeliveryDetailCode>" è inferiore a 1 minuti
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "<expectedFeedbackDeliveryDetailCode>"
    Examples:
      | sequenceName         | expectedDeliveryDetailCode | expectedFeedbackDeliveryDetailCode |
      #per i seguenti due casi, i 10 giorni diventano 1 minuto (parametro: RefinementDuration)
      | Via@OK-Giacenza_AR   | RECRN003A                  | RECRN003C                          |
      | Via@FAIL-Giacenza_AR | RECRN004A                  | RECRN004C                          |


