Feature: segregazione delivery push

  Scenario Outline: [SEGREGAZIONE_DP_1]
    Given vengono recuperate dal sistema 200 notifiche inviate tra <dateStart> e <dateEnd> da "Comune_Multi"
    Then confronto le timeline ottenute chiamando la nuova API e la vecchia impostando confidentialInfoRequired a <confidentialInfoRequired>
    Examples:
      | dateStart    | dateEnd      | confidentialInfoRequired |
      | "25/03/2025" | "28/06/2025" | "true"                   |

  Scenario Outline: [SEGREGAZIONE_DP_2]
    Given vengono recuperate dal sistema 200 notifiche inviate tra <dateStart> e <dateEnd> da "Comune_Multi"
    Then confronto gli status history ottenuti chiamando la nuova API e la vecchia
    Examples:
      | dateStart    | dateEnd      |
      | "25/03/2025" | "28/06/2025" |

  Scenario: [PROVA_2_tentativo]
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di milano                |
      | physicalCommunication | AR_REGISTERED_LETTER            |
    And destinatario
      | denomination            | Giovanna D'Arco  |
      | taxId                   | DRCGNN12A46A326K |
      | digitalDomicile         | NULL             |
      | physicalAddress_address | Via@fail_AR      |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"