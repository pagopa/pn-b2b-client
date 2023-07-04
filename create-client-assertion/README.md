# Esempio di lancio:

1. Recarsi alla root del progetto e buildare il progetto con il comando:
   - ```shell
     ./mvnw clean package
     ```
     (ambiente Unix)
   - ```shell 
     mvnw.cmd clean package
     ```
     (ambiente Windows)
   
   Verrà creato il JAR nella cartella target all'interno della root del progetto.
2. Eseguire il JAR con le options opportune:
    ```shell
    java -jar create-client-assertion-1.0-SNAPSHOT-jar-with-dependencies.jar --kid=$kid \
    --alg=RS256 \
    --typ=JWT \
    --issuer=$iss \
    --subject=$iss \
    --audience=$audience \
    --purposeId=$purposeId \
    --keyPath=<path to private key>
   ```

In alternativa è possibile da IDE avviare direttamente la classe `it.pagopa.pdnd.CreateClientAssertionApp` (sempre con le options opportune)
