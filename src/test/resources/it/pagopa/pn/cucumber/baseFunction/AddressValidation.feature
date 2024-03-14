Feature: address validation feature


  @testNormalizzatore
  Scenario Outline: [B2B_ADDRESS_VALIDATION] Invio notifica digitale ed attesa stato ACCEPTED_scenario positivo
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination | <denomination> |
      |    taxId     | CLMCST42R12D969Z |
      | physicalAddress_address | <address> |
      | at | <at> |
      | physicalAddress_addressDetails | <addressDetails> |
      | physicalAddress_zip | <zip> |
      | physicalAddress_municipality | <municipality> |
      | physicalAddress_municipalityDetails | <municipalityDetails> |
      | physicalAddress_province | <province> |
      | physicalAddress_State | <foreignState> |
    Then la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi <notificationValidationStatus>
    Examples:
      | denomination           | at           | address                                             | addressDetails  | zip     | municipality                  | municipalityDetails | province  | foreignState  | notificationValidationStatus  |
      | TEST_NORMALIZZATORE_01 | NULL         | Via Rodolfo de Novà 1                               | NULL            | 00173   | roma                          | NULL                | RM        | italia        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_02 | NULL         | VIA FIUME N° 16/A                                   | NULL            | 97013   | COMISO                        | NULL                | RG        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_03 | NULL         | EPASA-ITAC                                          | NULL            | NULL    | marotta@cert.epasa-itaco      | NULL                | it        | NULL          | REFUSED                       |
      | TEST_NORMALIZZATORE_04 | NULL         | VIA DON DINO BIANCARDI,60                           | NULL            | 46030   | SUSTINENTE                    | NULL                | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_05 | NULL         | VIA REPUBBLICA,57                                   | NULL            | 20843   | VERANO B                      | NULL                | ZA        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_06 | NULL         | VIA LAMARMORA 40/A                                  | NULL            | 20122   | NON IDENTIFICATO              | NULL                | XX        | NULL          | REFUSED                       |
      | TEST_NORMALIZZATORE_07 | NULL         | REVERE                                              | NULL            | 46036   | NULL                          | NULL                | MN        | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_08 | NULL         | 00120 CITTA DEL VATICANO VA                         | NULL            | NULL    | NULL                          | NULL                | VA        | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_09 | NULL         | VIA BRUSA 53                                        | NULL            | 10149   | TORINO                        | NULL                | TO        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_10 | NULL         | VIA A GIMMA                                         | NULL            | 70121   | BARI                          | NULL                | BA        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_11 | NULL         | VILLA POMA                                          | NULL            | 46036   | BORGO MANTOVANO               | NULL                | MN        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_12 | NULL         | VIA�FORNO�17                                        | NULL            | 38054   | PRIMIERO�S�MARTINO�CATN       | NULL                | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_13 | NULL         | SOTTOTENETE CAPUTO, 1                               | NULL            | 70056   | MOLFETTA                      | NULL                | BA        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_14 | NULL         | 0                                                   | NULL            | 185     | ROMA                          | NULL                | RM        | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_15 | NULL         | VIA PALAZZO 3 17 TORRE FARO                         | NULL            | 98164   | MESSINA                       | NULL                | ME        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_16 | NULL         | COMMESSAGGIO                                        | NULL            | 46018   | COMMESSAGGIO                  | NULL                | MN        | NULL          | REFUSED                       |
      | TEST_NORMALIZZATORE_17 | NULL         | AJAYI ESTHER                                        | NULL            | NULL    | antitratta.bologna@pec.net    | NULL                | NULL      | ITALIA        | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_18 | NULL         | VIA BRUSA 53                                        | NULL            | 10149   | TORINO                        | NULL                | TO        | ITA           | ACCEPTED                      |
      | TEST_NORMALIZZATORE_19 | NULL         | VIA A GIMMA                                         | NULL            | 70121   | BARI                          | NULL                | BA        | ITALY         | ACCEPTED                      |
      | TEST_NORMALIZZATORE_20 | NULL         | VIA�FORNO�17                                        | NULL            | 38054   | PRIMIERO�S�MARTINO�CATN       | NULL                | NULL      | ITALIA        | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_21 | NULL         | SOTTOTENETE CAPUTO, 1                               | NULL            | 70056   | MOLFETTA                      | NULL                | BA        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_22 | NULL         | 0                                                   | NULL            | 185     | ROMA                          | NULL                | RM        | ITALIA        | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_23 | NULL         | VIA PALAZZO 3 17 TORRE FARO                         | NULL            | 98164   | MESSINA                       | NULL                | ME        | italia        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_24 | NULL         | COMMESSAGGIO                                        | NULL            | 46018   | COMMESSAGGIO                  | NULL                | MN        | ita           | REFUSED                       |
      | TEST_NORMALIZZATORE_25 | NULL         | VIA ANDREA MANTEGNA,15/17                           | NULL            | 46042   | CASTEL GOFFREDO               | NULL                | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_26 | NULL         | VIA ANDREA MANTEGNA 15                              | NULL            | 46042   | CASTEL GOFFREDO               | NULL                | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_27 | NULL         | VIA ANDREA MANTEGNA 15 INTERNO 3                    | NULL            | 46042   | CASTEL GOFFREDO               | NULL                | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_28 | NULL         | VIA ANDREA MANTEGNA 15 INTERNO 3                    | INTERNO 3       | 46042   | CASTEL GOFFREDO               | NULL                | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_29 | NULL         | VIA ANDREA MANTEGNA 15 INTERNO 3                    | INTERNO 4       | 46042   | CASTEL GOFFREDO               | NULL                | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_30 | NULL         | VIA DALMAZIA 2                                      | NULL            | 21014   | LAVENO-MOMBELLO LAVENO MOMBEL | NULL                | VA        | ITALIA        | REFUSED                       |
      | TEST_NORMALIZZATORE_31 | NULL         | VIA SETTETERMINI 33                                 | NULL            | 21100   | SANFERMO DI VARESE            | NULL                | VA        | ITALIA        | REFUSED                       |
      | TEST_NORMALIZZATORE_32 | NULL         | VIA DANTE ALIGHIERI                                 | NULL            | NULL    | BORGO VIRGILIO                | NULL                | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_33 | NULL         | VIA DANTE ALIGHIERI                                 | NULL            | NULL    | BORGO VIRGILIO                | VIRGILIO            | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_34 | NULL         | VIA DANTE ALIGHIERI                                 | NULL            | NULL    | BORGO VIRGILIO                | BORGOFORTE          | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_35 | NULL         | VIA DANTE ALIGHIERI 3 VIRGILIO                      | NULL            | NULL    | BORGO VIRGILIO                | NULL                | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_36 | NULL         | VIA DANTE ALIGHIERI 5 BORGOFORTE                    | NULL            | NULL    | BORGO VIRGILIO                | NULL                | MN        | ITALIA        | ACCEPTED                      |
      | TEST_NORMALIZZATORE_37 | NULL         | STRADA VICINALE DEL MONTE-LAGO SAN VITO N 7 C       | NULL            | 70014   | CONVERSANO                    | NULL                | BA        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_38 | NULL         | REGIONE PRELE STRADALE MOIRANO 56                   | NULL            | 15011   | ACQUI TERME                   | NULL                | AL        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_39 | NULL         | VIA B CROCE 6/7 VIA F DA BAXILIO                    | NULL            | 15057   | TORTONA                       | NULL                | AL        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_40 | NULL         | TRAVERSA AL N 58 STRADA DETTA DELLA MARINA N 22 IN  | NULL            | 70126   | BARI                          | NULL                | BA        | NULL          | ACCEPTED                      |
      | TEST_NORMALIZZATORE_41 | NULL         | VIA PIPPO                                           | NULL            | NULL    | ROMA                          | NULL                | RM        | NULL          | REFUSED                       |
      | TEST_NORMALIZZATORE_42 | NULL         | VIA PIPPO                                           | NULL            | 20121   | ROMA                          | NULL                | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_43 | NULL         | VIA PIPPO                                           | NULL            | NULL    | ROMA                          | NULL                | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_44 | NULL         | VIA PIPPO 2 LIDO DI OSTIA                           | NULL            | 198     | ROMA                          | NULL                | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_45 | NULL         | VIA PIPPO                                           | NULL            | 198     | ROMA                          | LIDO DI OSTIA       | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_46 | NULL         | VIA PIPPO                                           | INTERNO 3       | 198     | ROMA                          | NULL                | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_47 | NULL         | VIA MOLINI 2                                        | NULL            | 37069   | VILLAFRANCA                   | NULL                | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_48 | NULL         | VIA MOLINI 2                                        | NULL            | 37062   | VILLAFRANCA                   | NULL                | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_49 | NULL         | VIA MOLINI 2                                        | NULL            | 37062   | DOSSOBUONO                    | NULL                | NULL      | NULL          | HTTP_ERROR                    |
      | TEST_NORMALIZZATORE_50 | NULL         | VIA EURIALO N^47 IN.6                               | NULL            | 00100   | ROMA                          | NULL                | RM        | italia        | ACCEPTED                      |



  @testNormalizzatore
  Scenario Outline: [B2B_ADDRESS_VALIDATION_2] Controllo physicalAddress con caratteri ISOLatin1 e limite a 44 - PN-10272
    Given viene generata una nuova notifica
      | subject | invio notifica con cucumber |
      | senderDenomination | Comune di milano |
    And destinatario
      | denomination                        | <denomination>        |
      | taxId                               | CLMCST42R12D969Z      |
      | digitalDomicile                     | NULL                  |
      | physicalAddress_address             | <address>             |
      | at                                  | <at>                  |
      | physicalAddress_addressDetails      | <addressDetails>      |
      | physicalAddress_zip                 | <zip>                 |
      | physicalAddress_municipality        | <municipality>        |
      | physicalAddress_municipalityDetails | <municipalityDetails> |
      | physicalAddress_province            | <province>            |
      | physicalAddress_State               | <foreignState>        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK"
    Examples:
      | denomination        | address                                                      | addressDetails | zip                                                                  | municipality                                        | municipalityDetails          | province                                    | foreignState |
      | TEST_VALIDAZIONE_01 | 10, RUE DES CANNIERS-QUARTIER ANCHES BT. C02 MARSIGLIA       | 0_CHAR         | 83310                                                                | COGOLIN                                             | 0_CHAR                       | MARSIGLIA                                   | FRANCIA      |
      | TEST_VALIDAZIONE_02 | 1 RUE HENRY BARBUSSE - BAT 1 ENT 2 LOGT 1979                 | 0_CHAR         | 13090                                                                | AIX EN PROVENCE                                     | 0_CHAR                       | FRA                                         | FRANCIA      |
      | TEST_VALIDAZIONE_03 | 1 36 MOO.9, BAAN IM AMPOND, TAWEEWATTANA RD., SALATHAMMASOP, | 0_CHAR         | 10700                                                                | BANGKOK                                             | 0_CHAR                       | 0_CHAR                                      | THAILANDIA   |
      | TEST_VALIDAZIONE_04 | CALLE MARIA MARROQUI 28 INT. 310, COL. CENTRO                | 0_CHAR         | 06050                                                                | CITTA' DEL MESSICO                                  | 0_CHAR                       | 0_CHAR                                      | MESSICO      |
      | TEST_VALIDAZIONE_05 | CALLE CADETE SISA C/NACUNDAY - ZONA NORTE 350                | 0_CHAR         | 2300                                                                 | FERNANDO DE LA MORA                                 | 0_CHAR                       | PRY                                         | PARAGUAY     |
      | TEST_VALIDAZIONE_06 | VIA SANDRO PERTINI GIA' VLE A. MORO TRAV.II NICOLO 22        | 0_CHAR         | 0_CHAR                                                               | REGGIO DI CALABRIA                                  | 0_CHAR                       | RC                                          | ITALIA       |
      | TEST_VALIDAZIONE_07 | TRAVERSA AL N 58 STRADA DETTA DELLA MARINA 26                | 0_CHAR         | 70126                                                                | BARI                                                | TORRE A MARE                 | BA                                          | ITALIA       |
      | TEST_VALIDAZIONE_08 | STRADA STATALE N.7 TER  LECCE - CAMPI SALENTINA 1C.P.        | 0_CHAR         | 73100                                                                | LECCE                                               | 0_CHAR                       | LE                                          | ITALIA       |
      | TEST_VALIDAZIONE_09 | S}~T~R~ADæA }~æ} N.7 pæaæris                                 | 0_CHAR         | 73100                                                                | S}~T~R~ADæA }~æ} N.7 pæaæris                        | 0_CHAR                       | LE                                          | FRANCIA      |
      | TEST_VALIDAZIONE_10 | S}~T~R~ADæA }~æ} N.7 pæaæris                                 | 0_CHAR         | 73100                                                                | S}~T~R~ADæA }~æ} N.7 pæaæris                        | 0_CHAR                       | S}~T~R~ADæA }~æ} N.7 pæaæris                | FRANCIA      |
      | TEST_VALIDAZIONE_11 | S}~T~R~ADæA }~æ} N.7 pæaæris                                 | 0_CHAR         | 73100                                                                | LECCE                                               | S}~T~R~ADæA }~æ} N.7 pæaæris | LE                                          | FRANCIA      |
      | TEST_VALIDAZIONE_12 | STRADA }~æ} N.7 TER  LECCE - CAMPI SALENTINA 1C.P.           | 0_CHAR         | 73100                                                                | LECCE                                               | 0_CHAR                       | LE                                          | ITALIA       |
      | TEST_VALIDAZIONE_13 | 10, RUE DES CANNIERS-QUARTIER ANCHES BT. C02 MARSIGLIA       | 0_CHAR         | 83310                                                                | COGOLIN                                             | 0_CHAR                       | MARSIGLIA prova provincia lungaaaaaaaaaaaaa | FRANCIA      |
      | TEST_VALIDAZIONE_14 | 10, RUE DES CANNIERS-QUARTIER ANCHES BT. C02 MARSIGLIA       | 0_CHAR         | 83310                                                                | COGOLIN prova comune lunghissimoooooooooooooooooooo | 0_CHAR                       | MA                                          | FRANCIA      |
      | TEST_VALIDAZIONE_15 | 10, RUE DES CANNIERS-QUARTIER ANCHES BT. C02 MARSIGLIA       | 0_CHAR         | 83310000000000000000000000000000000000000000000000000000000000000000 | COGOLIN                                             | 0_CHAR                       | MARSIGLIA                                   | FRANCIA      |


  @testNormalizzatore
  Scenario Outline: [B2B_ADDRESS_VALIDATION_3] invio notifiche controllo arrivo RIR - PN-10273
    Given viene generata una nuova notifica
      | subject            | invio notifica con cucumber |
      | senderDenomination | Comune di palermo           |
    And destinatario Mario Gherkin e:
      | digitalDomicile                     | NULL                  |
      | physicalAddress_address             | <address>             |
      | at                                  | <at>                  |
      | physicalAddress_addressDetails      | <addressDetails>      |
      | physicalAddress_zip                 | <zip>                 |
      | physicalAddress_municipality        | <municipality>        |
      | physicalAddress_municipalityDetails | <municipalityDetails> |
      | physicalAddress_province            | <province>            |
      | physicalAddress_State               | <foreignState>        |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi ACCEPTED
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRI003C"
    Examples:
      | address            | at   | addressDetails | zip    | municipality | municipalityDetails | province | foreignState |
      | VIA DELLA POSTA    | NULL | NULL           | 0_CHAR | VATICANO     | NULL                | RM       | ITALIA       |
      | OTTAVA GUALDARIA 1 | NULL | NULL           | NULL   | DOMAGNANO    | NULL                | NULL     | SAN MARINO   |

