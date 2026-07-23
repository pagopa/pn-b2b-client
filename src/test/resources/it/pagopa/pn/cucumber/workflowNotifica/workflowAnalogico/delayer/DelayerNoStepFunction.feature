# Test Delayer senza Step Function (lookup capacity, cleanup).
# Sequenziali e rapidi. Ordine: TC14 (delete) → TC10 → TC4.B.

Feature: Delayer — test senza Step Function

  @delayerNoSf @delayer14
  Scenario Outline: [DELAYER-TC14] Verifica che la pulizia delle tabelle target rimuova completamente i dati di test
    Given vengono puliti i dati dalle tabelle target
    And il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
      | seed          | quantita | deliveryWeek |
      | tcZeroDriver_ | 15       | 2025-12-29   |
    And il CSV <csv> è importato da S3 nella pn-DelayerPaperDelivery tramite lambda di test
    When vengono puliti i dati dalle tabelle target
    Then non devono esistere record in pn-DelayerPaperDelivery per la deliveryDate "2025-12-29"
    And non deve esistere capacità usata alla deliveryDate "2025-12-29"
      | unifiedDeliveryDriverId |
      | Poste~80125             |
    And non devono esistere contatori per la deliveryDate "2025-12-29"
    And non devono esistere limiti mittente per la deliveryDate "2025-12-29" e pk "unknow~RS~P10"
    Examples:
      | csv                | TOT |
      | "tcZeroDriver.csv" | 15  |

  # Per il driver: zeroDriver è stata modificata la capacity a 10 per il periodo 2025-12-29T00:00:00.000Z - 2026-01-04T23:59:59.999Z
  # si verifica che la capacity ritornata per quella settiamana sia esattamente quella attesa: 10
  @delayerNoSf @delayer10
  Scenario Outline: [DELAYER-TC10] A seguito di un aggiornamento della capacity per il driver: zeroDriver verifica che la capacità ritornata sia esattamente quella attesa: 10.
    Given vengono puliti i dati dalle tabelle target
    Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
      | seed          | quantita | deliveryWeek |
      | tcZeroDriver_ | 15       | 2025-12-29   |
    And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
      | senderId       | comparative | limit |
      | unknow~RS~P10  | esattamente | 0     |
      | unknow~AR~P10  | esattamente | 0     |
      | unknow~890~P10 | esattamente | 0     |
    And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
      | unifiedDeliveryDriverId | comparative | limit |
      | zeroDriverP10~P10       | esattamente | 10    |
      | zeroDriverP10~CAP1_P10  | esattamente | 10    |
    And si verifica che il limite settimanale utilizzato dai recapitisti (unifiedDeliveryDriver-geoKey) sia:
      | unifiedDeliveryDriverId | comparative | limit |
      | zeroDriverP10~P10       | esattamente | 10    |
      | zeroDriverP10~CAP1_P10  | esattamente | 10    |
    Examples:
      | csv                | TOT |
      | "tcZeroDriver.csv" | 15  |

  # Per il driver: zeroDriver è stata modificata la capacity a 10 per il periodo 2025-12-29T00:00:00.000Z - 2026-01-04T23:59:59.999Z
  # si verifica che la capacity ritornata per una settimana diversa da quella modificata precedentemente sia quella di default: 0.
  @delayerNoSf @delayer11
  Scenario Outline: [DELAYER-TC4.B] Verifica la gestione di una capacity driver nulla
    Given vengono puliti i dati dalle tabelle target
    Given il CSV <csv> contiene <TOT> notifiche distribuite tra i seguenti test case:
      | seed          | quantita |
      | tcZeroDriver_ | 15       |
    And si presuppone che il limite mittente settimanale (paId-product_type-province) sia:
      | senderId       | comparative | limit |
      | unknow~RS~P10  | esattamente | 0     |
      | unknow~AR~P10  | esattamente | 0     |
      | unknow~890~P10 | esattamente | 0     |
    And si presume che il limite settimanale dei recapitisti (unifiedDeliveryDriver-geoKey) sia:
      | unifiedDeliveryDriverId | comparative | limit |
      | zeroDriverP10~P10       | esattamente | 0     |
      | zeroDriverP10~CAP1_P10  | esattamente | 0     |
    And si verifica che il limite settimanale utilizzato dai recapitisti (unifiedDeliveryDriver-geoKey) sia:
      | unifiedDeliveryDriverId | comparative | limit |
      | zeroDriverP10~P10       | esattamente | 0     |
      | zeroDriverP10~CAP1_P10  | esattamente | 0     |
    Examples:
      | csv                | TOT |
      | "tcZeroDriver.csv" | 15  |
