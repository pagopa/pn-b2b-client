# Golden Master PDF – NOTIFICATION_CANCELLED

Questo file segnaposto indica la directory dove deve essere posizionato
il golden master PDF per il template `NOTIFICATION_CANCELLED` (Attestazione di annullamento notifica).

## Come generare il golden master

1. Eseguire lo scenario con tag `@notificationCancelled @smoke`.
2. Salvare il file scaricato come `expected.pdf` in questa cartella:
```
src/test/resources/it/pagopa/pn/cucumber/visualtest/expected-pdfs/notification-cancelled/expected.pdf
```
