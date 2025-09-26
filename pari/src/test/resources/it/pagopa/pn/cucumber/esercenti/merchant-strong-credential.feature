Feature: PARI - Portale Esercenti Credenziali Forti (Sede legale)

  Background:
    Given vengono generati tutti i token JWT necessari per esercenti

    #[TC-16]
    #[TC-18]
  Scenario: [TC_MERCHANT_1]
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene recuperato l'elenco dei punti vendita
    | city |  |
    Then la lista dei punti vendita è correttamente popolata
    And si recupera il dettaglio di uno specifico punto vendita

    #[TC-17]
  Scenario: [TC_MERCHANT_2]
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene recuperato l'elenco dei punti vendita
    | merchantId | 0000 |
    Then la chiamata ritorna un errore con status code: 404

    #[TC-19]
  Scenario: [TC_MERCHANT_3]
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene recuperato l'elenco dei punti vendita
      | city |  |
    Then si recupera il dettaglio di uno specifico punto vendita con i seguenti parametri:
      |pointOfSaleId| 0000 |
    Then la chiamata ritorna un errore con status code: 404
    Then si recupera il dettaglio di uno specifico punto vendita con i seguenti parametri:
      |merchantId| 0000 |
    Then la chiamata ritorna un errore con status code: 404

    #[TC-20]
  Scenario: [TC_MERCHANT_4]
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene recuperato l'elenco dei punti vendita
      | city |  |
    Then si recupera il dettaglio di uno specifico punto vendita con i seguenti parametri:
      |pointOfSaleId| 0000 |
    Then la chiamata ritorna un errore con status code: 404
    Then si recupera il dettaglio di uno specifico punto vendita con i seguenti parametri:
      |merchantId| 0000 |
    Then la chiamata ritorna un errore con status code: 404


  #[TC-20]
  Scenario Outline: [TC_MERCHANT_5]
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene censito un nuovo punto vendita con i seguenti parametri:
      | id             | <id>             |
      | type           | <type>           |
      | franchiseName  | <franchiseName>  |
      | region         | <region>         |
      | province       | <province>       |
      | city           | <city>           |
      | zipCode        | <zipCode>        |
      | address        | <address>        |
      | streetNumber   | <streetNumber>   |
      | webSite        | <webSite>        |
#      | contactEmail   | <contactEmail>   |
      | contactName    | <contactName>    |
      | contactSurname | <contactSurname> |
      | channelEmail   | <channelEmail>   |
      | channelPhone   | <channelPhone>   |
      | channelGeolink | <channelGeolink> |
      | channelWebsite | <channelWebsite> |
    Examples:
      | id                          | type     | franchiseName  | region   | province | city       | zipCode | address                                            | streetNumber | webSite                          | contactEmail          | contactName | contactSurname | channelEmail         | channelPhone | channelGeolink                     | channelWebsite              |
      | 68d13516bebd6132f487d99z    | Retail   | Test8          | Lombardia| MI       | Milano     | 20100   | Via Trieste, 65015 Montesilvano PE, Italia         | 12           | https://www.mediaworld.it/       | test.p8@prova.com     | Mario       | Rossi          | support@superstore.it | +39021234567 | https://maps.app.goo.gl/abc123      | https://channel.superstore.it|
#      | 102 | Partner  | MegaShop      | Lazio    | RM       | Roma       | 00100   | Corso Italia     | 45           | https://megashop.com        | contatti@megashop.com| Luca        | Bianchi        | help@megashop.com     | +39061234567 | https://maps.app.goo.gl/xyz789      | https://channel.megashop.com |


    #[TC-21]
  Scenario Outline: [TC_MERCHANT_6]
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene censito un nuovo punto vendita con i seguenti parametri:
      | merchantId     | 0000             |
      | id             | <id>             |
      | type           | <type>           |
      | franchiseName  | <franchiseName>  |
      | region         | <region>         |
      | province       | <province>       |
      | city           | <city>           |
      | zipCode        | <zipCode>        |
      | address        | <address>        |
      | streetNumber   | <streetNumber>   |
      | webSite        | <webSite>        |
#      | contactEmail   | <contactEmail>   |
      | contactName    | <contactName>    |
      | contactSurname | <contactSurname> |
      | channelEmail   | <channelEmail>   |
      | channelPhone   | <channelPhone>   |
      | channelGeolink | <channelGeolink> |
      | channelWebsite | <channelWebsite> |
    Then la chiamata ritorna un errore con status code: 404

    Examples:
      | id                          | type     | franchiseName  | region   | province | city       | zipCode | address                                            | streetNumber | webSite                          | contactEmail          | contactName | contactSurname | channelEmail         | channelPhone | channelGeolink                     | channelWebsite              |
      | 68d13516bebd6132f487d99z    | Retail   | Test8          | Lombardia| MI       | Milano     | 20100   | Via Trieste, 65015 Montesilvano PE, Italia         | 12           | https://www.mediaworld.it/       | test.p8@prova.com     | Mario       | Rossi          | support@superstore.it | +39021234567 | https://maps.app.goo.gl/abc123      | https://channel.superstore.it|


  #[TC-22]
  Scenario Outline: [TC_MERCHANT_7]
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene censito un nuovo punto vendita con i seguenti parametri:
      | id             | <id>             |
      | type           | <type>           |
      | franchiseName  | <franchiseName>  |
      | region         | <region>         |
      | province       | <province>       |
      | city           | <city>           |
      | zipCode        | <zipCode>        |
      | address        | <address>        |
      | streetNumber   | <streetNumber>   |
      | webSite        | <webSite>        |
#      | contactEmail   | <contactEmail>   |
      | contactName    | <contactName>    |
      | contactSurname | <contactSurname> |
      | channelEmail   | <channelEmail>   |
      | channelPhone   | <channelPhone>   |
      | channelGeolink | <channelGeolink> |
      | channelWebsite | <channelWebsite> |
    Examples:
      | id                          | type     | franchiseName  | region   | province | city       | zipCode | address                                            | streetNumber | webSite                          | contactEmail          | contactName | contactSurname | channelEmail         | channelPhone | channelGeolink                     | channelWebsite              |
      | 688cb2c22fb2709e4ba6d18d    | Retail   | Test8          | Lombardia| MI       | Milano     | 20100   | Via Trieste, 65015 Montesilvano PE, Italia         | 12           | https://www.mediaworld.it/       | test.p8@prova.com     | Mario       | Rossi          | support@superstore.it | +39021234567 | https://maps.app.goo.gl/abc123      | https://channel.superstore.it|
#      | 102 | Partner  | MegaShop      | Lazio    | RM       | Roma       | 00100   | Corso Italia     | 45           | https://megashop.com        | contatti@megashop.com| Luca        | Bianchi        | help@megashop.com     | +39061234567 | https://maps.app.goo.gl/xyz789      | https://channel.megashop.com |

  #[TC-23]
  Scenario Outline: [TC_MERCHANT_8]
    Given viene usata l'utenza: MERCHANT_ROOT
    When viene censito un nuovo punto vendita con i seguenti parametri:
      | id             | <id>             |
      | type           | <type>           |
      | franchiseName  | <franchiseName>  |
      | region         | <region>         |
      | province       | <province>       |
      | city           | <city>           |
      | zipCode        | <zipCode>        |
      | address        | <address>        |
      | streetNumber   | <streetNumber>   |
      | webSite        | <webSite>        |
#      | contactEmail   | <contactEmail>   |
      | contactName    | <contactName>    |
      | contactSurname | <contactSurname> |
      | channelEmail   | <channelEmail>   |
      | channelPhone   | <channelPhone>   |
      | channelGeolink | <channelGeolink> |
      | channelWebsite | <channelWebsite> |
    Then la chiamata ritorna un errore con status code: 404
    Examples:
      | id                          | type     | franchiseName  | region   | province | city       | zipCode | address                                            | streetNumber | webSite                          | contactEmail          | contactName | contactSurname | channelEmail         | channelPhone | channelGeolink                     | channelWebsite              |
      | 00000    | Retail   | Test8          | Lombardia| MI       | Milano     | 20100   | Via Trieste, 65015 Montesilvano PE, Italia         | 12           | https://www.mediaworld.it/       | test.p8@prova.com     | Mario       | Rossi          | support@superstore.it | +39021234567 | https://maps.app.goo.gl/abc123      | https://channel.superstore.it|
#      | 102 | Partner  | MegaShop      | Lazio    | RM       | Roma       | 00100   | Corso Italia     | 45           | https://megashop.com        | contatti@megashop.com| Luca        | Bianchi        | help@megashop.com     | +39061234567 | https://maps.app.goo.gl/xyz789      | https://channel.megashop.com |








