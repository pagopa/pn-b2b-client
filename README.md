##Istruzioni per l'utilizzo
- creare il file 'api-keys.properties' nel path 'pn-b2b-client/config/'
- inserire nel file appena creato la stringa 'pn.external.api-keys.pagopa-dev=<api_key>' sostituendo ad <api_key> l'api key corretta
- nel file 'config/application.properties' settare correttamente il campo 'pn.external.base-url' in funzione dell'ambiente su cui inviare la notifica
- eseguire il comando
```
    ./mvnw clean install
```
- eseguire il run del main() nel file src/main/java/it/pagopa/pn/client/b2b/pa/NewNotification.java
per invio della notifica e verifica dell'accettazione della stessa da parte di Piattaforma Notifiche
- eseguire il run della classe nel file src/main/java/it/pagopa/pn/client/b2b/pa/cucumber/test/CucumberB2BTest.java
  per l'esecuzione dei test e2e delle api b2b di Piattaforma Notifiche