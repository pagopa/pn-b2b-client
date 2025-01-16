Feature:Perfezionamento della notifica con destinatario irreperibile a 10g dal deposito AAR e revisione date perfezionamento digitali

  @perfezionamentoIrreperibile
  Scenario Outline: [PERFEZIONAMENTO_IRREPERIBILE_1] Notifica analogica AR per PF nel caso in cui il 1° e il 2° tentativo di consegna falliscono controllo delle tempistiche tra SCHEDULE_REFINEMENT_WORKFLOW e ANALOG_FAILURE_WORKFLOW
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | <SEQUENCE>               |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "ANALOG_FAILURE_WORKFLOW" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "ERRORE ANALOGICO"
    Examples:
    | SEQUENCE                        |
    | Via@FAIL-Irreperibile_AR        |
    | Via@FAIL_IndirizzoInesistenteAR |

  @perfezionamentoIrreperibile
  Scenario: [PERFEZIONAMENTO_IRREPERIBILE_2] Notifica analogica AR per PF nel caso in cui il 1° tentativo fallisse e non fosse possibile individuare un altro indirizzo valido per effettuare un 2° tentativo il sistema procederà a schedulare il perfezionamento - controllo delle tempistiche tra SCHEDULE_REFINEMENT_WORKFLOW e ANALOG_FAILURE_WORKFLOW
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Test AR Fail 2           |
      | taxId                   | DVNLRD52D15M059P         |
      | digitalDomicile         | NULL                     |
      | physicalAddress_address | Via@FAIL_IndirizzoInesistenteAR |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    And vengono letti gli eventi fino all'elemento di timeline della notifica "COMPLETELY_UNREACHABLE"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And controllo che le tempistiche di arrivo tra l elemento "ANALOG_FAILURE_WORKFLOW" e l'elemento "SCHEDULE_REFINEMENT_WORKFLOW" siano corrette per la notifica "ERRORE ANALOGICO"
