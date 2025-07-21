Feature: segregazione delivery push

  @segregazione_delivery_push
  Scenario Outline: [SEGREGAZIONE_DP_1]
    Given vengono recuperate dal sistema 500 notifiche inviate tra <dateStart> e <dateEnd> da "Comune_Multi"
    Then confronto le timeline ottenute chiamando la nuova API e la vecchia impostando confidentialInfoRequired a <confidentialInfoRequired>
    Examples:
      | dateStart    | dateEnd      | confidentialInfoRequired |
      | "25/03/2025" | "28/06/2025" | "true"                   |
      | "25/06/2024" | "25/07/2024" | "false"                  |

  @segregazione_delivery_push
  Scenario Outline: [SEGREGAZIONE_DP_2]
    Given vengono recuperate dal sistema 500 notifiche inviate tra <dateStart> e <dateEnd> da "Comune_Multi"
    Then confronto gli status history ottenuti chiamando la nuova API e la vecchia
    Examples:
      | dateStart    | dateEnd      |
      | "25/03/2025" | "28/06/2025" |
      | "25/06/2024" | "25/07/2024" |