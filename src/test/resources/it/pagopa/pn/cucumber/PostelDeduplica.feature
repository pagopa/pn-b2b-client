Feature: PostelDeduplica

  @posteDeduplica
  Scenario: [POSTEL_DEDUPLICA] Viene effettuato un secondo tentativo di consegna analogica allo stesso indirizzo della prima consegna analogica
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
      | feePolicy          | DELIVERY_MODE               |
      | paFee              | 0                           |
    And destinatario
      | denomination            | Test deduplica postel |
      | taxId                   | PRMRGG74T16H501R      |
      | digitalDomicile         | NULL                  |
      | physicalAddress_address | Via@fail_AR           |
    And la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_DIGITAL_FEEDBACK" con responseStatus "KO" al tentativo "ATTEMPT_0"
    And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" esista
      | details                      | NOT_NULL  |
      | details_deliveryFailureCause | M05       |
      | details_deliveryDetailCode   | RECRN002C |
    And vengono letti gli eventi fino all'elemento di timeline della notifica "PREPARE_ANALOG_DOMICILE" al tentativo "ATTEMPT_1"
    And verifico che su DynamoDB è presente in paperRequestError l'elemento "PREPARE_ANALOG_DOMICILE" con errorCode "PNADDR003"


    #And viene verificato che l'elemento di timeline "SEND_ANALOG_FEEDBACK" non esista


