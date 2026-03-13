Feature: Adeguamento Indirizzo ANPR (SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2644410397/Adeguamento+costruzione+indirizzo+fornito+da+ANPR)

  @AdeguamentoIndirizzoANPR_Old
  Scenario Outline: [DIRECT_CALL_TO_ANPR_OLD]
    When viene interrogato nationalRegistry per il codice fiscale "<taxId>"
    Then si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo "OLD"
    Examples:
      | taxId            |
      #toponimo=null;numeroCivico!=null;colore=1;corte!=null;scala!=null;scalaEsterna!=null
      | GNVGCM97E04L781N |
      #toponimo!=null;numeroCivico=null
      | MRNMRZ04D07L781J |
      #toponimo=null;numeroCivico=null;
      | VNNVNN99T16L781L |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera=null;metrico>0;esponente1!=null
      | PRZPLA89E02L781K |
      #toponimo!=null;numeroCivico!=null;specie=null;numero=null;lettera!=null;metrico=0;progSnc>0;scala!=null;colore=3;esponente1!=null
      | BRNBNN92S02L781R |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;progSnc>0;scala!=null;colore>5;esponente1!=null
      | LNNLNZ02L27L781Z |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=3;interno1!=null;interno2=""
      | QDRQMD99C20L781Y |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=5;interno1="";interno2!=null
      | JRIJNN05A01L781M |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=1;interno1!=null;interno2!=null
      | RZORNZ95C11L781S |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=5;isolato!=null
      | RGHLVC01H09H501K |

  @AdeguamentoIndirizzoANPR_Minimal
  Scenario Outline: [DIRECT_CALL_TO_ANPR_MINIMAL]
    When viene interrogato nationalRegistry per il codice fiscale "<taxId>"
    Then si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo "MINIMAL"
    Examples:
      | taxId            |
      #toponimo=null;numeroCivico!=null;colore=1;corte!=null;scala!=null;scalaEsterna!=null
      | GNVGCM97E04L781N |
      #toponimo!=null;numeroCivico=null
      | MRNMRZ04D07L781J |
      #toponimo=null;numeroCivico=null;
      | VNNVNN99T16L781L |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera=null;metrico>0;esponente1!=null
      | PRZPLA89E02L781K |
      #toponimo!=null;numeroCivico!=null;specie=null;numero=null;lettera!=null;metrico=0;progSnc>0;scala!=null;colore=3;esponente1!=null
      | BRNBNN92S02L781R |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;progSnc>0;scala!=null;colore>5;esponente1!=null
      | LNNLNZ02L27L781Z |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=3;interno1!=null;interno2=""
      | QDRQMD99C20L781Y |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=5;interno1="";interno2!=null
      | JRIJNN05A01L781M |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=1;interno1!=null;interno2!=null
      | RZORNZ95C11L781S |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=5;isolato!=null
      | RGHLVC01H09H501K |


  @AdeguamentoIndirizzoANPR_Full
  Scenario Outline: [DIRECT_CALL_TO_ANPR_FULL]
    When viene interrogato nationalRegistry per il codice fiscale "<taxId>"
    Then si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo "FULL"
    Examples:
      | taxId            |
      #toponimo=null;numeroCivico!=null;colore=1;corte!=null;scala!=null;scalaEsterna!=null
      | GNVGCM97E04L781N |
      #toponimo!=null;numeroCivico=null
      | MRNMRZ04D07L781J |
      #toponimo=null;numeroCivico=null;
      | VNNVNN99T16L781L |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera=null;metrico>0;esponente1!=null
      | PRZPLA89E02L781K |
      #toponimo!=null;numeroCivico!=null;specie=null;numero=null;lettera!=null;metrico=0;progSnc>0;scala!=null;colore=3;esponente1!=null
      | BRNBNN92S02L781R |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;progSnc>0;scala!=null;colore>5;esponente1!=null
      | LNNLNZ02L27L781Z |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=3;interno1!=null;interno2=""
      | QDRQMD99C20L781Y |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=5;interno1="";interno2!=null
      | JRIJNN05A01L781M |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=1;interno1!=null;interno2!=null
      | RZORNZ95C11L781S |
      #toponimo!=null;numeroCivico!=null;specie!=null;numero!=null;lettera!=null;metrico=lettera;colore=5;isolato!=null
      | RGHLVC01H09H501K |


  @AdeguamentoIndirizzoANPR_Old
  Scenario Outline: [RICERCA_INDIRIZZO_ANPR_ALGORITMO_OLD] Verifica costruzione indirizzo ANPR secondo le logiche dell'algoritmo OLD
    Given il test è effettuabile con API versione "V25" o superiore
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | <denomination> |
      | taxId           | <taxId>        |
      | recipientType   | PF             |
      | digitalDomicile | NULL           |
      | physicalAddress | NULL           |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL            |
      | details_registry        | ANPR                |
      | details_recIndex        | 0                   |
      | details_physicalAddress | "<expectedAddress>" |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_RESPONSE" esista
      | details                 | NOT_NULL            |
      | details_registry        | ANPR                |
      | details_recIndex        | 0                   |
      | details_physicalAddress | "<expectedAddress>" |
    Examples:
      | denomination  | taxId            | expectedAddress |
      #1) ha numero, lettera, esponente1, colore e interno2/espInterno2
      | Mario Uno     | VRSVLR80A01L781H | TODO_address1   |
      #2) ha progSNC, colore, numero lettera esponente per l'address e nel civicoInterno ci sono entrambi gli interni (interno1/2 ed espInterno1/2)
      | Mario Due     | RNCRNL00A03F205C | TODO_address2   |
      #3) Ha solamente metrico colore e civico interno 1
      | Mario Tre     | DLRPQL89P09D612E | TODO_address3   |
      #4) Ha tutti i campi tranne i due interni/espInterno e la scalaEsterna
      | Mario Quattro | PRSNBL81R08H501T | TODO_address4   |


  @AdeguamentoIndirizzoANPR_Minimal
  Scenario Outline: [RICERCA_INDIRIZZO_ANPR_ALGORITMO_MINIMAL] Verifica costruzione indirizzo ANPR secondo le logiche dell'algoritmo MINIMAL
    Given il test è effettuabile con API versione "V25" o superiore
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | "<denomination>" |
      | taxId           | "<taxId>"        |
      | recipientType   | PF               |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL            |
      | details_registry        | ANPR                |
      | details_recIndex        | 0                   |
      | details_physicalAddress | "<expectedAddress>" |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_RESPONSE" esista
      | details                 | NOT_NULL            |
      | details_registry        | ANPR                |
      | details_recIndex        | 0                   |
      | details_physicalAddress | "<expectedAddress>" |
    Examples:
      | denomination  | taxId            | expectedAddress |
      #1) ha numero, lettera, esponente1, colore e interno2/espInterno2
      | Mario Uno     | VRSVLR80A01L781H | TODO_address1   |
      #2) ha progSNC, colore, numero lettera esponente per l'address e nel civicoInterno ci sono entrambi gli interni (interno1/2 ed espInterno1/2)
      | Mario Due     | RNCRNL00A03F205C | TODO_address2   |
      #3) Ha solamente metrico colore e civico interno 1
      | Mario Tre     | DLRPQL89P09D612E | TODO_address3   |
      #4) Ha tutti i campi tranne i due interni/espInterno e la scalaEsterna
      | Mario Quattro | PRSNBL81R08H501T | TODO_address4   |


  @AdeguamentoIndirizzoANPR_Full
  Scenario Outline: [RICERCA_INDIRIZZO_ANPR_ALGORITMO_FULL] Verifica costruzione indirizzo ANPR secondo le logiche dell'algoritmo FULL
    Given il test è effettuabile con API versione "V25" o superiore
    Given viene generata una nuova notifica
      | subject               | invio notifica con cucumber |
      | senderDenomination    | Comune di Palermo           |
      | physicalCommunication | AR_REGISTERED_LETTER        |
    And destinatario
      | denomination    | "<denomination>" |
      | taxId           | "<taxId>"        |
      | recipientType   | PF               |
      | digitalDomicile | NULL             |
      | physicalAddress | NULL             |
    When la notifica viene inviata tramite api b2b dal "Comune_1" e si attende che lo stato diventi "ACCEPTED"
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_CALL" esista
      | loadTimeline       | true     |
      | details            | NOT_NULL |
      | details_recIndexes | [0]      |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_VALIDATION_RESPONSE" esista
      | details                 | NOT_NULL            |
      | details_registry        | ANPR                |
      | details_recIndex        | 0                   |
      | details_physicalAddress | "<expectedAddress>" |
    And viene verificato che l'elemento di timeline "PUBLIC_REGISTRY_RESPONSE" esista
      | details                 | NOT_NULL            |
      | details_registry        | ANPR                |
      | details_recIndex        | 0                   |
      | details_physicalAddress | "<expectedAddress>" |
    Examples:
      | denomination  | taxId            | expectedAddress |
      #1) ha numero, lettera, esponente1, colore e interno2/espInterno2
      | Mario Uno     | VRSVLR80A01L781H | TODO_address1   |
      #2) ha progSNC, colore, numero lettera esponente per l'address e nel civicoInterno ci sono entrambi gli interni (interno1/2 ed espInterno1/2)
      | Mario Due     | RNCRNL00A03F205C | TODO_address2   |
      #3) Ha solamente metrico colore e civico interno 1
      | Mario Tre     | DLRPQL89P09D612E | TODO_address3   |
      #4) Ha tutti i campi tranne i due interni/espInterno e la scalaEsterna
      | Mario Quattro | PRSNBL81R08H501T | TODO_address4   |