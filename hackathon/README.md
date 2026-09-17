# SEND Hackathon Bot

## Avvio

Node.js compatibile con Slack Bolt 5; sviluppo verificato con Node 24.
Configurare `hackathon/.env.local` partendo da `.env.example`: `SLACK_BOT_TOKEN`,
`SLACK_APP_TOKEN`, `SLACK_ALLOWED_USER_IDS` (ID separati da virgole).

```sh
cd hackathon/src
npm ci --ignore-scripts
npm start
```

Tenere una sola istanza attiva. Riavviare con Ctrl+C e `npm start` dopo ogni modifica.
Nell'app Slack: Socket Mode, interattività, comando `/send-notification`, scope bot
`commands`, `chat:write`, `files:read`. Reinstallare l'app dopo modifiche ai permessi.

## Configurazione SEND test

`sendConfig.js` usa `hackathon/.env.send.test.local` se presente, altrimenti il file
preesistente `hackathon/src/.env.test`. Non legge development e non dipende da NODE_ENV.
Il modello privo di credenziali è `.env.send.test.example`; i file locali sono esclusi da Git.
I vecchi `.env.test` e `.env.development` già in staging non sono stati modificati:
`.gitignore` non rimuove file già tracciati. Non includere credenziali in un commit.

Sono ammessi esclusivamente HTTPS e host `api.test.notifichedigitali.it`, con questi endpoint:

- POST `/delivery/attachments/preload`;
- POST `/delivery/v2.4/requests` oppure `/delivery/v2.5/requests`;
- GET sullo stesso endpoint configurato per lo stato.

La configurazione corrente usa v2.5; il client mantiene la versione configurata.

La configurazione esistente è stata verificata strutturalmente senza mostrare valori segreti.
La validità della chiave e la disponibilità dell'ambiente non sono state provate con invii reali.
Mittente e gruppo del form devono corrispondere all'ente di test autorizzato dalla chiave;
nessun valore fiscale viene ereditato dal vecchio template. L'autorizzazione del mittente
è verificata da SEND; il form esegue solo controlli locali di formato.

## Flusso

1. `/send-notification` apre il form con il solo ambiente TEST.
2. Compilare notifica, mittente, destinatario PF/PG, indirizzo italiano, eventuale PEC e PDF.
3. Riepilogo mostra i dati: nessuna chiamata SEND è stata ancora effettuata.
4. **Invia in TEST avvia un invio reale nell'ambiente di test.**
5. Il bot registra la lavorazione e risponde a Slack prima delle operazioni lente.
6. Recupera il file con `files.info`, scarica con autenticazione Slack (host consentiti e
   redirect controllati), controlla limite 10 MB e leggibilità PDF tramite pdf-lib.
7. Calcola SHA-256 Base64, esegue preload e upload usando il metodo restituito e gli header richiesti.
   Il `versionToken` è l'header `x-amz-version-id` della risposta upload, mai un valore fittizio.
8. Invia la richiesta nella versione configurata (v2.4/v2.5) e registra immediatamente `notificationRequestId`.
9. Interroga lo stato rispettando `retryAfter`, per circa cinque minuti, fino a ACCEPTED con IUN
   o REFUSED. Il timeout di ogni chiamata HTTP è 30 secondi; il limite non interrompe una chiamata in corso.
10. Aggiorna la modal. Se chiusa o se l'esito tarda, consultare:

```text
/send-notification stato ID_LAVORAZIONE
```

Il comando stato è privato e limitato al proprietario/workspace della lavorazione. Se l'esito
è in attesa, effettua una nuova lettura SEND. In caso di POST con risultato incerto, prova la
lettura tramite protocollo e idempotenceToken, senza reinviare la notifica.
Un errore di aggiornamento della modal non modifica l'esito SEND.

## Perimetro e conservazione

Un destinatario nazionale, un PDF, nessun pagamento, `notificationFeePolicy=FLAT_RATE`,
`pagoPaIntMode=NONE`. Senza protocollo esplicito si genera un UUID per la prova: non sostituisce
la protocollazione ufficiale della PA. Restano esclusi multidestinatario, estero, pagamenti e produzione.
La validazione locale non certifica CF/checksum, esistenza di indirizzo/PEC o tassonomia SEND.
La lettura con pdf-lib non certifica PDF/A: anche i controlli asincroni SEND possono rifiutare il file.

Le bozze prima della conferma vivono in memoria per 15 minuti (pulizia ogni minuto).
Dopo conferma il registro in `hackathon/.local/jobs`, escluso da Git e con file a permessi 0600,
conserva ID, proprietario, protocollo, stato ed esito, non il PDF né i dati del destinatario.
La chiave di deduplicazione è un hash di ambiente, codice fiscale mittente e protocollo.
Il lock è scritto prima del lavoro: due conferme, anche dopo riavvio, non riutilizzano lo stesso
protocollo. Non eliminare lock per riprovare un esito incerto: prima verificarlo con il comando stato.
Per una richiesta definitivamente fallita e da correggere, usare un nuovo protocollo di test.

Il registro resta sul disco senza cancellazione automatica e non è una coda distribuita.
Il bot locale deve restare attivo durante la lavorazione. Dopo arresto nessun job è reinviato:
una preparazione interrotta può restare PREPARING; SUBMITTING/UNCERTAIN richiedono consultazione
tramite comando stato. WAITING può essere aggiornato con lo stesso comando. Il polling automatico
non riparte al riavvio. Non eseguire più istanze dello stesso bot.
Il file caricato su Slack rimane soggetto alla conservazione di Slack.

`index.js` e `preloadDocument.js` sono il prototipo storico e non vengono eseguiti da `npm start`.
Il nuovo client evita i relativi log di payload/segreti e la mutazione del template condiviso.

## Fonti e tracciabilità — 2026-09-16

Obiettivo: immagine fornita dall'utente (comando, form, ambiente, documento, autorizzazione,
invio e identificativo/IUN). Ambiente test scelto esplicitamente dall'utente.
Non sono stati forniti SRS/PST o identificativi formali di casi.

Contratto: modelli locali generati `externalb2bpa.model.NewNotificationRequestV24`,
`NotificationRecipientV23` (destinatario di V24), `NotificationDocument`, `PreLoadResponse`,
`NotificationAttachmentBodyRef`, `NewNotificationResponse`, `NewNotificationRequestStatusResponseV24`;
API `NewNotificationApi` e `SenderReadB2BApi`. Il JSON della descrizione è `abstract`, non `_abstract`.
La generazione in `pom.xml` (`generate-external-client-b2b-pa`, Java/resttemplate) usa
`pn-delivery/docs/openapi/api-external-b2b-pa-bundle.yaml` da `refs/heads/develop`.
Snapshot locale non rigenerato; i metodi v2.4 risultano deprecati nel client generato, ma sono
supportati per compatibilità. La configurazione corrente usa v2.5: verificati anche
`NewNotificationRequestV25`, `NotificationRecipientV24` e i metodi v2.5 di invio/stato.
Il payload del form rimane compatibile; la v2.5 consente anche destinatari senza indirizzo
fisico, mentre questo form continua a richiederlo. Disponibilità live non verificata. Nessuna modifica ai modelli generati,
alle versioni Java o alle suite Cucumber.

Slack: [file_input](https://docs.slack.dev/reference/block-kit/block-elements/file-input-element/),
[file object e download](https://docs.slack.dev/reference/objects/file-object/),
[view submissions](https://docs.slack.dev/tools/bolt-js/concepts/view-submissions/).

| Requisito | Implementazione / evidenza | Stato |
| --- | --- | --- |
| Comando, form e conferma | notificationForm.js, notificationFlow.js | Form verificato live prima dell'integrazione SEND |
| Dati notifica e destinatario | Validazione e mapping v2.4 | Automatico locale, perimetro singolo destinatario |
| Solo test | sendConfig.js, validazione ambiente anche nel worker | Automatico locale |
| Documento | Recupero Slack, parsing PDF, preload e upload con versione reale | Test con HTTP simulato; integrazione live da verificare |
| Autorizzazione | Allowlist e proprietà della bozza/lavorazione | Automatico locale |
| Invio senza duplicati | Journal, lock protocollo, POST singola, token stabile | Automatico locale; non coda distribuita |
| Esito/IUN | Polling, stato, recupero ambiguo via protocollo/token | Automatico locale; accettazione live da verificare |
| Estero, pagamenti, multidestinatario | Fuori dal perimetro di questa versione | Non coperti |

## Verifica

```sh
cd hackathon/src
npm test
```

Test `node:test` con dati sintetici, PDF generati in memoria, SDK e HTTP simulati: nessun invio
remoto. Coprono form, autorizzazioni, isolamento, upload, versione, esiti, timeout ambiguo,
recupero, deduplicazione dopo riavvio e credenziali separate tra Slack/SEND/S3.
L'indice Cucumber non cambia: questi test riguardano il bot Node.js.
Non eseguiti Maven, upload remoto o notifica reale. La prova live parte solo da “Invia in TEST”.
