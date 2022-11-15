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

##Istruzioni aggiuntive per l'esecuzione dei test e2e
- Inserire nel file 'api-keys.properties' nel path 'pn-b2b-client/config/'
- 'pn.external.api-keys.pagopa-dev=<api_key>' sostituendo ad <api_key> l'api key corretta
- 'pn.external.api-keys.pagopa-dev-2*=<api_key>' sostituendo ad <api_key> l'api key corretta
- 'pn.external.api-keys.pagopa-GA-dev**=<api_key>' sostituendo ad <api_key> l'api key corretta
- 'pn.external.api.keys.appio.pagopa.dev=<api_key>' sostituendo ad <api_key> l'api key corretta
- NOTA* : ApiKey per una qualsiasi PA diversa dalle altre precedentemente inserite
- NOTA**: ApiKey per una qualsiasi PA NON MVP diversa dalle altre precedentemente inserite

- creare il file 'bearer-token.properties' nel path 'pn-b2b-client/config/'
- inserire nel file appena creato le stringhe: 
- 'pn.external.bearer-token-CristoforoC.pagopa=<bearer-token>' sostituendo ad <bearer-token> il bearer token corretto
- 'pn.external.bearer-token-FieramoscaE.pagopa=<bearer-token>' sostituendo ad <bearer-token> il bearer token corretto

-eseguire il run della classe nel file src/test/java/it/pagopa/pn/cucumber/CucumberB2BTest.java
  per l'esecuzione dei test e2e delle api b2b di Piattaforma Notifiche
- eseguire il run della classe nel file src/test/java/it/pagopa/pn/cucumber/CucumberDevIntegrationTest.java 

##Istruzioni aggiuntive per l'esecuzione dei test e2e in locale su più ambienti contemporaneamente
- Inserire nel file 'bearer-token.properties' nel path 'pn-b2b-client/config/'
- 'pn.external.bearer-token-CristoforoC.pagopa-svil=<bearer-token>' sostituendo ad <bearer-token> il bearer token corretto
- 'pn.external.bearer-token-FieramoscaE.pagopa-svil=<bearer-token>' sostituendo ad <bearer-token> il bearer token corretto 
- e/o
- 'pn.external.bearer-token-CristoforoC.pagopa-coll=<bearer-token>' sostituendo ad <bearer-token> il bearer token corretto
- 'pn.external.bearer-token-FieramoscaE.pagopa-coll=<bearer-token>' sostituendo ad <bearer-token> il bearer token corretto

- Inserire nel file 'api-keys.properties' nel path 'pn-b2b-client/config/'
- 'pn.external.api-keys.pagopa-svil=<api_key>' sostituendo ad <api_key> l'api key corretta
- 'pn.external.api-keys.pagopa-svil-2=<api_key>' sostituendo ad <api_key> l'api key corretta
- 'pn.external.api-keys.pagopa-GA-svil=<api_key>' sostituendo ad <api_key> l'api key corretta
- 'pn.external.api.keys.appio.pagopa.svil=<api_key>' sostituendo ad <api_key> l'api key corretta
- e/o
- 'pn.external.api-keys.pagopa-coll=<api_key>' sostituendo ad <api_key> l'api key corretta
- 'pn.external.api-keys.pagopa-coll-2=<api_key>' sostituendo ad <api_key> l'api key corretta
- 'pn.external.api-keys.pagopa-GA-coll=<api_key>' sostituendo ad <api_key> l'api key corretta
- 'pn.external.api.keys.appio.pagopa.coll=<api_key>' sostituendo ad <api_key> l'api key corretta

- Inserire nel file 'application.properties' nel path 'pn-b2b-client/config/'
- pn.execution-type=svil OPPURE coll 
- Eseguire src/test/java/it/pagopa/pn/cucumber/CucumberEnvIntegrationTest.java
- Questa classe in base al valore di 'pn.execution-type' eseguira i test su ambiente SVILL o COLL
- NOTA: in caso di assenza o valore non contemplato di 'pn.execution-type' saranno eseguiti i test su DEV