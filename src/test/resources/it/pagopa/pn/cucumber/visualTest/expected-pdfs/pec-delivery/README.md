# Golden Master PDF – PEC_DELIVERY

Questo file segnaposto indica la directory dove deve essere posizionato
il golden master PDF per il template `PEC_DELIVERY` (Avvenuta ricezione digitale).

## Come generare il golden master

1. Eseguire lo scenario con tag `@pecDelivery @smoke` che valida i campi dinamici senza confronto visivo.
2. Salvare il file scaricato come `expected.pdf` in questa cartella:
```
src/test/resources/it/pagopa/pn/cucumber/visualtest/expected-pdfs/pec-delivery/expected.pdf
```
