const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '..', '.env.local') });
const { App } = require('@slack/bolt');
const { registerNotificationFlow } = require('./notificationFlow');
const { loadTestConfig, ConfigError } = require('./sendConfig');
const { createSendClient } = require('./sendClient');
const { createJobStore } = require('./sendJobs');
let startupStage = 'configurazione Slack';

async function main() {
    const required = ['SLACK_BOT_TOKEN', 'SLACK_APP_TOKEN', 'SLACK_ALLOWED_USER_IDS'];
    const missing = required.filter(name => !process.env[name]?.trim());
    if (missing.length) {
        throw new ConfigError(`Compilare hackathon/.env.local: ${missing.join(', ')}`);
    }
    const allowedUsers = new Set(process.env.SLACK_ALLOWED_USER_IDS.split(',').map(id => id.trim()).filter(Boolean));
    if (!allowedUsers.size) throw new ConfigError('Configurare almeno un utente Slack autorizzato.');
    startupStage = 'configurazione SEND test';
    const sendConfig = loadTestConfig();
    startupStage = 'registro locale';
    const jobs = createJobStore(path.join(__dirname, '..', '.local', 'jobs'));
    startupStage = 'connessione Slack';
    // Non registrare payload Slack, token o dati del form nei log.
    const logger = {
        debug() {}, info() {}, warn() { console.warn('Avviso connessione Slack.'); },
        error() { console.error('Errore Slack: verificare connessione, token e permessi.'); },
        setLevel() {}, getLevel() { return 'error'; }, setName() {},
    };
    const app = new App({
        token: process.env.SLACK_BOT_TOKEN,
        appToken: process.env.SLACK_APP_TOKEN,
        socketMode: true,
        logger,
    });

    const sendService = createSendClient(sendConfig, process.env.SLACK_BOT_TOKEN);
    const dispose = registerNotificationFlow(app, allowedUsers, { sendService, jobs });
    app.error(async () => logger.error());
    await app.start();
    console.log('Bot connesso. Invio SEND TEST abilitato solo tramite il pulsante “Invia in TEST”.');
    for (const signal of ['SIGINT', 'SIGTERM']) {
        process.once(signal, async () => { dispose(); await app.stop(); });
    }
}

main().catch(error => {
    const safeCodes = new Set(['invalid_auth', 'not_authed', 'token_revoked', 'token_expired', 'missing_scope',
        'not_allowed_token_type', 'account_inactive', 'ENOTFOUND', 'ECONNREFUSED', 'ETIMEDOUT', 'EACCES', 'EPERM']);
    const code = error?.data?.error || error?.code || error?.original?.code;
    const detail = error instanceof ConfigError ? error.message
        : safeCodes.has(code) ? `Codice: ${code}.` : 'Controllare configurazione e connessione; dettagli sensibili omessi.';
    console.error(`Avvio non riuscito [${startupStage}]: ${detail}`);
    process.exitCode = 1;
});
