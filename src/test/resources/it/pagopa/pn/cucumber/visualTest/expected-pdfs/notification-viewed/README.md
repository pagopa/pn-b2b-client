# Golden Master PDF – NOTIFICATION_VIEWED

Questo file segnaposto indica la directory dove deve essere posizionato
il golden master PDF per il template `NOTIFICATION_VIEWED` (Attestato di avvenuto accesso).

## Come generare il golden master

1. Eseguire lo scenario con tag `@notificationViewed @smoke`.
2. Salvare il file scaricato come `expected.pdf` in questa cartella:
```
src/test/resources/it/pagopa/pn/cucumber/visualtest/expected-pdfs/notification-viewed/expected.pdf
```
