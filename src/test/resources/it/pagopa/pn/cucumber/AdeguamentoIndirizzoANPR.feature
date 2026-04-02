Feature: Adeguamento Indirizzo ANPR (SRS: https://pagopa.atlassian.net/wiki/spaces/PN/pages/2644410397/Adeguamento+costruzione+indirizzo+fornito+da+ANPR)

  @AdeguamentoIndirizzoANPR_Old
  Scenario Outline: [DIRECT_CALL_TO_ANPR_OLD] Interrogando NationalRegistry quando l'algoritmo per il calcolo di address e addressDetail è impostato a OLD, verificare la correttezza dei dati
    When viene interrogato nationalRegistry per il codice fiscale "<taxId>"
    Then si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo OLD
    Examples:
      | taxId            |
      #toponimo=null;numeroCivico!=null;colore=1;corte!=null;scala!=null;scalaEsterna!=null
      | GNVGCM97E04L781N |
      #toponimo!=null;numeroCivico=null
      | JNOFBN86B05L781H |
      #toponimo=null;numeroCivico=null;
      | BLLBBR95D46L781R |
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
  Scenario Outline: [DIRECT_CALL_TO_ANPR_MINIMAL] Interrogando NationalRegistry quando l'algoritmo per il calcolo di address e addressDetail è impostato a MINIMAL, verificare la correttezza dei dati
    When viene interrogato nationalRegistry per il codice fiscale "<taxId>"
    Then si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo MINIMAL
    Examples:
      | taxId            |
      #toponimo=null;numeroCivico!=null;colore=1;corte!=null;scala!=null;scalaEsterna!=null
      | GNVGCM97E04L781N |
      #toponimo!=null;numeroCivico=null
      | JNOFBN86B05L781H |
      #toponimo=null;numeroCivico=null;
      | BLLBBR95D46L781R |
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
  Scenario Outline: [DIRECT_CALL_TO_ANPR_FULL] Interrogando NationalRegistry quando l'algoritmo per il calcolo di address e addressDetail è impostato a FULL, verificare la correttezza dei dati
    When viene interrogato nationalRegistry per il codice fiscale "<taxId>"
    Then si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo FULL
    Examples:
      | taxId            |
      #toponimo=null;numeroCivico!=null;colore=1;corte!=null;scala!=null;scalaEsterna!=null
      | GNVGCM97E04L781N |
      #toponimo!=null;numeroCivico=null
      | JNOFBN86B05L781H |
      #toponimo=null;numeroCivico=null;
      | BLLBBR95D46L781R |
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

  @AdeguamentoIndirizzoANPR_Old_UAT
  Scenario Outline: [DIRECT_CALL_TO_ANPR_OLD_UAT] Interrogando NationalRegistry quando l'algoritmo per il calcolo di address e addressDetail è impostato a OLD, verificare la correttezza dei dati
    When viene interrogato nationalRegistry per il codice fiscale "<taxId>"
    Then si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo OLD
    Examples:
      | taxId            |
      #toponimo.denominazione=CAVOUR;toponimo.specie=null;numero=1;lettera=null;metrico=0;progSnc=10;scala=null;colore=1;scalaEsterna=3;interno2=4;isolato=7
      | VRDLSM78B02F839R |
      #toponimo.denominazione=Po;toponimo.specie=VIA;numero=1;lettera=A;metrico=100;progSnc=10;corte=2;scala=2;colore=3;scalaEsterna=3;interno1=4;interno2=5
      | RSSMSM85E15H501L |
      | KPRSMP91H12F205O |
      | KRSJSM88S03H501A |
      #toponimo.denominazione=Fiume;toponimo.specie=Via;numero=1;lettera=A;metrico=null;progSnc=10;scala=null;colore=1;scalaEsterna=3;interno1=4;isolato=6
      | BSMGPR92R62F205X |

  @AdeguamentoIndirizzoANPR_Minimal_UAT
  Scenario Outline: [DIRECT_CALL_TO_ANPR_MINIMAL_UAT] Interrogando NationalRegistry quando l'algoritmo per il calcolo di address e addressDetail è impostato a MINIMAL, verificare la correttezza dei dati
    When viene interrogato nationalRegistry per il codice fiscale "<taxId>"
    Then si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo MINIMAL
    Examples:
      | taxId            |
      #toponimo.denominazione=CAVOUR;toponimo.specie=null;numero=1;lettera=null;metrico=0;progSnc=10;scala=null;colore=1;scalaEsterna=3;interno2=4;isolato=7
      | VRDLSM78B02F839R |
      #toponimo.denominazione=Po;toponimo.specie=VIA;numero=1;lettera=A;metrico=100;progSnc=10;corte=2;scala=2;colore=3;scalaEsterna=3;interno1=4;interno2=5
      | RSSMSM85E15H501L |
      | KPRSMP91H12F205O |
      | KRSJSM88S03H501A |
      #toponimo.denominazione=Fiume;toponimo.specie=Via;numero=1;lettera=A;metrico=null;progSnc=10;scala=null;colore=1;scalaEsterna=3;interno1=4;isolato=6
      | BSMGPR92R62F205X |

  @AdeguamentoIndirizzoANPR_Full_UAT
  Scenario Outline: [DIRECT_CALL_TO_ANPR_FULL_UAT] Interrogando NationalRegistry quando l'algoritmo per il calcolo di address e addressDetail è impostato a FULL, verificare la correttezza dei dati
    When viene interrogato nationalRegistry per il codice fiscale "<taxId>"
    Then si verifica che l'indirizzo sia correttamente formattato secondo le logiche dell'algoritmo FULL
    Examples:
      | taxId            |
      #toponimo.denominazione=CAVOUR;toponimo.specie=null;numero=1;lettera=null;metrico=0;progSnc=10;scala=null;colore=1;scalaEsterna=3;interno2=4;isolato=7
      | VRDLSM78B02F839R |
      #toponimo.denominazione=Po;toponimo.specie=VIA;numero=1;lettera=A;metrico=100;progSnc=10;corte=2;scala=2;colore=3;scalaEsterna=3;interno1=4;interno2=5
      | RSSMSM85E15H501L |
      | KPRSMP91H12F205O |
      | KRSJSM88S03H501A |
      #toponimo.denominazione=Fiume;toponimo.specie=Via;numero=1;lettera=A;metrico=null;progSnc=10;scala=null;colore=1;scalaEsterna=3;interno1=4;isolato=6
      | BSMGPR92R62F205X |