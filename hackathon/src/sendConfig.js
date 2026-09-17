const fs = require('node:fs');
const path = require('node:path');
const dotenv = require('dotenv');
class ConfigError extends Error {}

function validateTestConfig(env) {
    const fields = ['PN_EXTERNAL_API_KEY_GA', 'PN_EXTERNAL_BASE_URL_UPLOAD_NOTIFICATION', 'PN_EXTERNAL_BASE_URL_SENT_NOTIFICATION'];
    const missing = fields.filter(key => !env[key]?.trim());
    if (missing.length) throw new ConfigError(`Configurazione test incompleta: ${missing.join(', ')}`);
    const preloadUrl = env.PN_EXTERNAL_BASE_URL_UPLOAD_NOTIFICATION.trim();
    const requestUrl = env.PN_EXTERNAL_BASE_URL_SENT_NOTIFICATION.trim();
    for (const [value, paths] of [[preloadUrl, ['/delivery/attachments/preload']], [requestUrl, ['/delivery/v2.4/requests', '/delivery/v2.5/requests']]]) {
        let url;
        try { url = new URL(value); } catch { throw new ConfigError('URL SEND test non valido.'); }
        if (url.origin !== 'https://api.test.notifichedigitali.it' || !paths.includes(url.pathname) || url.search || url.hash || url.username || url.password) {
            throw new ConfigError('Consentiti solo endpoint SEND test: preload e requests v2.4/v2.5.');
        }
    }
    return { apiKey: env.PN_EXTERNAL_API_KEY_GA.trim(), preloadUrl, requestUrl };
}

function loadTestConfig() {
    // Carica esclusivamente test. Non dipende da NODE_ENV e non usa credenziali development.
    const local = path.join(__dirname, '..', '.env.send.test.local');
    const legacy = path.join(__dirname, '.env.test');
    const file = fs.existsSync(local) ? local : legacy;
    const env = fs.existsSync(file) ? dotenv.parse(fs.readFileSync(file)) : {};
    return validateTestConfig(env);
}

module.exports = { loadTestConfig, validateTestConfig, ConfigError };
