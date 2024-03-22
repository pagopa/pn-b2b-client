Feature: verifica feature aoo/uo

  @AOO_UO
  Scenario: [B2B-AOO-UO_1] Invio notifica digitale_scenario da PA figlio e non riesce a recuperare la notifica PA padre
    Given viene generata una nuova notifica
      | subject            | invio notifica multi cucumber |
      | senderDenomination | Comune di Sappada             |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then si tenta il recupero dal sistema tramite codice IUN dalla PA "Comune_Root"
    And l'operazione ha generato un errore con status code "404"

  @AOO_UO
  Scenario: [B2B-AOO-UO_2] Invio notifica digitale_scenario da PA padre e non riesce a recuperare la notifica  PA figlio
    Given viene generata una nuova notifica
      | subject            | invio notifica multi cucumber |
      | senderDenomination | Comune di milano              |
    And destinatario Mario Cucumber
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then si tenta il recupero dal sistema tramite codice IUN dalla PA "Comune_Son"
    And l'operazione ha generato un errore con status code "404"


  @AOO_UO @platformDependent
  Scenario: [B2B-AOO-UO_3] Invio ad indirizzo di piattaforma fallimento al primo tentativo, successo al ritentativo e fallimento al secondo tentativo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@OK-pecFirstFailSecondSuccess.it" per il comune "Comune_Root"
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Sappada           |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline                 | true                                                             |
      | details                      | NOT_NULL                                                         |
      | details_responseStatus       | KO                                                               |
      | details_sendingReceipts      | [{"id": null, "system": null}]                                   |
      | details_digitalAddress       | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex             | 0                                                                |
      | details_digitalAddressSource | PLATFORM                                                         |
      | details_sentAttemptMade      | 0                                                                |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details                      | NOT_NULL                                                         |
      | details_digitalAddress       | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex             | 0                                                                |
      | details_digitalAddressSource | PLATFORM                                                         |
      | details_sentAttemptMade      | 0                                                                |

  @AOO_UO @platformDependent
  Scenario: [B2B-AOO-UO_4] Invio ad indirizzo di piattaforma fallimento al primo tentativo, successo al ritentativo e fallimento al secondo tentativo
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example@OK-pecFirstFailSecondSuccess.it" per il comune "Comune_Root"
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Sappada           |
    And destinatario
      | denomination    | Lucio Anneo Seneca |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | digitalDomicile | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline                 | true                                                             |
      | details                      | NOT_NULL                                                         |
      | details_responseStatus       | KO                                                               |
      | details_sendingReceipts      | [{"id": null, "system": null}]                                   |
      | details_digitalAddress       | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex             | 0                                                                |
      | details_digitalAddressSource | PLATFORM                                                         |
      | details_sentAttemptMade      | 0                                                                |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details                      | NOT_NULL                                                         |
      | details_digitalAddress       | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex             | 0                                                                |
      | details_digitalAddressSource | PLATFORM                                                         |
      | details_sentAttemptMade      | 0                                                                |

  @AOO_UO @platformDependent
  Scenario: [B2B-AOO-UO_5] Invio ad indirizzo di piattaforma fallimento al primo tentativo, successo al ritentativo e fallimento al secondo tentativo
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@OK-pecFirstFailSecondSuccess.it" per il comune "Comune_Root"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline                 | true                                                             |
      | details                      | NOT_NULL                                                         |
      | details_responseStatus       | KO                                                               |
      | details_sendingReceipts      | [{"id": null, "system": null}]                                   |
      | details_digitalAddress       | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex             | 0                                                                |
      | details_digitalAddressSource | PLATFORM                                                         |
      | details_sentAttemptMade      | 0                                                                |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details                      | NOT_NULL                                                         |
      | details_digitalAddress       | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex             | 0                                                                |
      | details_digitalAddressSource | PLATFORM                                                         |
      | details_sentAttemptMade      | 0                                                                |

  @AOO_UO @platformDependent
  Scenario: [B2B-AOO-UO_6] Invio ad indirizzo di piattaforma fallimento al primo tentativo, successo al ritentativo e fallimento al secondo tentativo
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example@OK-pecFirstFailSecondSuccess.it" per il comune "Comune_Root"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination    | Lucio Anneo Seneca |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | digitalDomicile | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then viene verificato che l'elemento di timeline "SEND_DIGITAL_FEEDBACK" esista
      | loadTimeline                 | true                                                             |
      | details                      | NOT_NULL                                                         |
      | details_responseStatus       | KO                                                               |
      | details_sendingReceipts      | [{"id": null, "system": null}]                                   |
      | details_digitalAddress       | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex             | 0                                                                |
      | details_digitalAddressSource | PLATFORM                                                         |
      | details_sentAttemptMade      | 0                                                                |
    And viene verificato che l'elemento di timeline "SEND_DIGITAL_DOMICILE" esista
      | details                      | NOT_NULL                                                         |
      | details_digitalAddress       | {"address": "example@OK-pecFirstFailSecondSuccess.it", "type": "PEC"} |
      | details_recIndex             | 0                                                                |
      | details_digitalAddressSource | PLATFORM                                                         |
      | details_sentAttemptMade      | 0                                                                |


  @AOO_UO @platformDependent
  Scenario: [B2B-AOO-UO_7] invio messaggio di cortesia - invio notifica per email per ente padre per PG
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_Root"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination    | Lucio Anneo Seneca |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | digitalDomicile | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                               |
      | details                | NOT_NULL                                           |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                  |
    And viene cancellata l'email di cortesia per il comune "default"
    And viene cancellata l'email di cortesia per il comune "Comune_Root"

  @AOO_UO @platformDependent
  Scenario: [B2B-AOO-UO_8] invio messaggio di cortesia - invio notifica per email per ente padre
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_Root"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Root" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                               |
      | details                | NOT_NULL                                           |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                  |

  @AOO_UO @platformDependent
  Scenario: [B2B-AOO-UO_9] invio messaggio di cortesia - invio notifica per email per ente figlio per PG
    Given si predispone addressbook per l'utente "Lucio Anneo Seneca"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "default"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_Root"
    And viene generata una nuova notifica
      | subject | invio notifica con cucumber |
    And destinatario
      | denomination    | Lucio Anneo Seneca |
      | recipientType   | PG                 |
      | taxId           | 20517490320        |
      | digitalDomicile | NULL               |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                               |
      | details                | NOT_NULL                                           |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                  |
    And viene cancellata l'email di cortesia per il comune "default"
    And viene cancellata l'email di cortesia per il comune "Comune_Root"

  @AOO_UO @platformDependent
  Scenario: [B2B-AOO-UO_10] invio messaggio di cortesia - invio notifica per email per ente figlio
    Given si predispone addressbook per l'utente "Galileo Galilei"
    And viene inserito un recapito legale "example@pecSuccess.it"
    And viene inserita l'email di cortesia "provaemail@test.it" per il comune "Comune_Root"
    And viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Sappada           |
    And destinatario
      | denomination    | Galileo Galilei  |
      | taxId           | GLLGLL64B15G702I |
      | digitalDomicile | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_Son" e si attende che lo stato diventi ACCEPTED
    Then si verifica la corretta acquisizione della notifica
    And viene verificato che l'elemento di timeline "SEND_COURTESY_MESSAGE" esista
      | loadTimeline           | true                                               |
      | details                | NOT_NULL                                           |
      | details_digitalAddress | {"address": "provaemail@test.it", "type": "EMAIL"} |
      | details_recIndex       | 0                                                  |
