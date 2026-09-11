# Analisi conflitto `jackson-databind` in `interop-qa-tests`

## Contesto

Nel progetto `interop-qa-tests` sono presenti piu percorsi transitive verso `com.fasterxml.jackson.core:jackson-databind`, con versioni diverse:

- `2.20.0` (via `net.masterthought:cucumber-reporting:5.10.2`)
- `2.17.2` (via `com.amazonaws:aws-java-sdk-dynamodb:1.12.792`)
- `2.12.7.1` (via `io.jsonwebtoken:jjwt-jackson:0.12.6`)
- `2.12.6.1` (via `org.springframework.boot:spring-boot-starter-json:2.5.12`)

## Domanda chiave: perche viene selezionata `2.20.0`?

Nel dependency tree verbose osservato, Maven marca le altre versioni come:

`omitted for conflict with 2.20.0`

Questo indica che, in quella risoluzione concreta dell'albero, l'artifact vincente e `2.20.0`.

## Regola Maven (importante precisazione)

La regola generale di Maven **non** e semplicemente "vince sempre la versione piu alta".

In sintesi:

1. Maven usa la mediazione "nearest definition" (percorso piu corto nell'albero).
2. A parita di distanza, conta anche l'ordine di risoluzione/dichiarazione.
3. `dependencyManagement` puo forzare la versione finale e rendere il comportamento deterministico.

Quindi il motivo reale del problema non e solo la presenza di `2.20.0`, ma il fatto che la versione finale e lasciata alla mediazione transitiva.

## Dove sta l'opacità

L'opacita nasce quando `jackson-databind` non e dichiarata in modo esplicito e centralizzato:

- la versione effettiva dipende dall'albero transitivo del momento;
- una modifica apparentemente innocua (upgrade di una libreria terza) puo cambiare la versione vincente;
- capire "chi vince" richiede ogni volta leggere `dependency:tree -Dverbose`.

## Come rendere il comportamento chiaro (senza decidere ora una patch)

Opzioni tecniche da valutare:

1. Gestire `jackson-databind` (e idealmente tutta la famiglia Jackson) in `dependencyManagement` del progetto.
2. Usare `jackson-bom` per allineare `databind`, `core`, `annotations`, `datatype-*`.
3. Aggiungere `maven-enforcer-plugin` (`dependencyConvergence` e/o `requireUpperBoundDeps`) per far fallire build in caso di conflitti non desiderati.

## Comandi utili di verifica

```powershell
cd "C:\Users\vmassaro\Devel\PagoPa\SEND\pn-b2b-client\pn-b2b-client\interop-qa-tests"
mvn dependency:tree -Dverbose=true | Select-String "jackson-databind" -Context 3,1
```

```powershell
cd "C:\Users\vmassaro\Devel\PagoPa\SEND\pn-b2b-client\pn-b2b-client\interop-qa-tests"
mvn dependency:tree | Select-String "cucumber-reporting|jackson-databind|jjwt-jackson|aws-java-sdk-dynamodb"
```

## Note di perimetro

- `interop-qa-tests` e un progetto Maven indipendente (non aggregato dal `pom.xml` root via sezione `<modules>`).
- Le conclusioni sopra descrivono la risoluzione osservata in `interop-qa-tests`.

---
**Data**: 10/09/2026  
**Stato**: Analisi del problema e criteri di verifica


