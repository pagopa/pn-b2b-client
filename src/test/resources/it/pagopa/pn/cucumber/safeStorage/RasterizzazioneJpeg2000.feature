# QA-18838: validazione di PN-21874 (pn-pdfraster, rasterizzazione di immagini JPEG2000)
Feature: Rasterizzazione su SafeStorage di documenti con immagini JPEG2000

  # Il documento viene caricato con il tipo usato da pn-ec per i documenti da stampare
  # rasterizzati (PN_PAPER_ATTACHMENT): SafeStorage lo rende disponibile solo dopo la
  # trasformazione eseguita da pn-pdfraster. Il percorso non dipende dagli allegati inseriti
  # nella busta cartacea, che variano con la configurazione dell'ambiente.
  #
  # Se un'immagine JPEG2000 non viene decodificata, la rasterizzazione non va in errore ma
  # produce una pagina bianca: per questo si verifica il contenuto di ogni pagina.
  # I PDF JPEG2000_* sono generati da scripts/jpeg2000-fixtures/generate_jpeg2000_fixtures.py
  # e hanno pagine prevalentemente scure.

  @e2e @rasterJpeg2000
  Scenario Outline: [SS-RASTER-JPEG2000-1] Un documento con immagini JPEG2000 <tipoDocumento> caricato per la stampa viene rasterizzato senza pagine bianche
    Given il client SafeStorage "pn-test" carica il documento "<documento>" di tipo "PN_PAPER_ATTACHMENT"
    When si attende che il documento caricato venga rasterizzato
    Then il documento rasterizzato non ha pagine bianche
    Examples:
      | tipoDocumento     | documento          |
      | a colori          | JPEG2000_RGB.pdf   |
      | in scala di grigi | JPEG2000_GRIGI.pdf |
      | multipagina misto | JPEG2000_MISTO.pdf |
