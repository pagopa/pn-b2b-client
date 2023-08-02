## Istruzioni per l'esecuzione
- creare o modificare il file 'codiciFiscaliGenerati.txt' inserendo all'interno uno o più elementi
- di cui è richiesta la generazione del token.
-
- Esempio di JSON valido (con i 3 esempi di token generabili)
- {
    "entries": [
        {
        "taxId": "codice_fiscale_PF",
        "tokenType": "PF"
        },
        {
        "taxId": "p_iva_PG",
        "tokenType": "PG"
        },
        {
        "taxId": "codice_fiscale_PA",
        "tokenType": "PA",
        "paName": "milano"
        }
    ]
- }

- lanciare lo script:  

- profilo sso: 
- node index.js env_name lambdaName
-
- switch-role:
- node index.js env_name lambdaName profile_name role_arn 