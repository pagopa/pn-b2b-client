# Golden Master PDF – SENDER_ACK

Questo file segnaposto indica la directory dove deve essere posizionato
il golden master PDF per il template `SENDER_ACK`.

## Come generare il golden master

1. Eseguire lo scenario `[VRT-02]` (tag `@smoke`) che **non** richiede il golden master
   e non esegue il confronto visivo.
2. Recuperare il PDF dal log di debug o dallo step Cucumber:
   - In `PdfVisualRegressionSteps.verificaConformitaVisiva()`, aggiungere temporaneamente
     `Files.write(Path.of("expected.pdf"), actualPdf);` per dumpare il PDF.
3. Spostare il PDF dumped in questa directory rinominandolo `expected.pdf`.
4. Rimuovere il codice temporaneo.

## Posizione richiesta

```
src/test/resources/it/pagopa/pn/cucumber/visualtest/expected-pdfs/sender-ack/expected.pdf
```

> **ATTENZIONE**: Il PDF golden master deve essere prodotto dall'ambiente di test
> **stabile** (non da un ambiente in evoluzione) per evitare falsi positivi nel
> visual diff di future esecuzioni.
