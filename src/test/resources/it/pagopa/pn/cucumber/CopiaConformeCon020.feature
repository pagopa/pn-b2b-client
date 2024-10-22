Feature: test per la visualizzazione della copia conforme


    #@copiaConformeCon020
  Scenario: [TEST] Creazione notifica analogica mono destinatario AR di 2 pagine verso PF con SEND_ANALOG_PROGRESS con campi attesi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile_address | test@fail.it |
      | physicalAddress_address      | Via@OK_AR-CON020-7Z1P |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi ACCEPTED
    #Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"







  #@copiaConformeCon020
  Scenario: [COPIA-CONFORME-CON020-1] Creazione notifica analogica mono destinatario AR di 2 pagine verso PF con SEND_ANALOG_PROGRESS con campi attesi
    #Given si predispone addressbook per l'utente "***"
    #And vengono rimossi eventuali recapiti presenti per l'utente
    #And viene disabilitato il servizio SERCQ SEND per il comune di "default"
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                   |
      | physicalAddress_address      | Via@OK_AR-CON020-ZIP2P |
    When la notifica viene inviata tramite api b2b dal "***" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "***"


  #@copiaConformeCon020
  Scenario: [COPIA-CONFORME-CON020-2] Creazione notifica analogica mono destinatario 890 di 3 pagine 7ZIP verso PG con SEND_ANALOG_PROGRESS e documento con campi attesi
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
   # And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                   |
      | physicalAddress_address      | Via@OK_890-CON020 |
    When la notifica viene inviata tramite api b2b dal "***" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "***"


  #@copiaConformeCon020
  Scenario: [COPIA-CONFORME-CON020-3] Creazione notifica analogica mono destinatario RS di una pagina verso PF con SEND_SIMPLE_REGISTERED_LETTER_PROGRESS
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile              | NULL                   |
      | physicalAddress_address      | Via@OK_RS-CON020 |
    When la notifica viene inviata tramite api b2b dal "***" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "***"


  #@copiaConformeCon020
  Scenario: [COPIA-CONFORME-CON020-4] Creazione notifica analogica multi destinatario 890 di 3 pagine con evento  SEND_ANALOG_PROGRESS per destinatario 0 e SEND_DIGITAL_PROGRESS per destinatario 1
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di Palermo           |
    When la notifica viene inviata tramite api b2b dal "***" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "***"