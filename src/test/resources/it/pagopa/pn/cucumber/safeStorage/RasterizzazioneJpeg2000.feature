# QA-18838: validazione di PN-21874 (pn-pdfraster, rasterizzazione di immagini JPEG2000)
Feature: Rasterizzazione di documenti con immagini JPEG2000

  # Se un'immagine JPEG2000 non viene decodificata, la rasterizzazione non va in errore ma
  # produce una pagina bianca: per questo si verifica il contenuto di ogni pagina.
  # I PDF JPEG2000_* hanno pagine prevalentemente scure.

  # Giro completo: la rasterizzazione la chiede pn-ec al ritentativo dopo CON996.
  # CAP non coperto da RADD, così il documento entra nella busta; senza avviso pagoPA,
  # che verrebbe rasterizzato anch'esso.
  @e2e @rasterJpeg2000 @work-item:QA-18838
  Scenario Outline: [SS-RASTER-JPEG2000-1] Una notifica analogica con documento JPEG2000 <tipoDocumento> arriva al recapitista rasterizzata senza pagine bianche
    Given viene generata una nuova notifica
      | subject               | notifica analogica con cucumber |
      | senderDenomination    | Comune di palermo               |
      | physicalCommunication | AR_REGISTERED_LETTER            |
      | document              | <documento>                     |
    And destinatario Mario Cucumber e:
      | digitalDomicile              | NULL                     |
      | physicalAddress_address      | Via@OK_PCRETRY_CON996_AR |
      | physicalAddress_zip          | 00010                    |
      | physicalAddress_municipality | BORGO SANTA MARIA        |
      | physicalAddress_province     | RM                       |
      | payment                      | NULL                     |
    When la notifica viene inviata tramite api b2b dal "Comune_Multi" e si attende che lo stato diventi "ACCEPTED"
    Then vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_PROGRESS" con deliveryDetailCode "CON996"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "SEND_ANALOG_FEEDBACK" con deliveryDetailCode "RECRN001C" tentativo "ATTEMPT_1"
    And vengono letti gli eventi fino all'elemento di timeline della notifica "REFINEMENT"
    And si verifica che il documento rasterizzato inviato in via cartacea al destinatario 0 non abbia pagine bianche
    Examples:
      | tipoDocumento     | documento          |
      | a colori          | DOC_JPEG2000_RGB   |
      | in scala di grigi | DOC_JPEG2000_GRIGI |
      | multipagina misto | DOC_JPEG2000_MISTO |
