const axios = require('axios');

const expectedOrgId = 'ID_ORGANIZZAZIONE_ATTESA'; 

/**
 * Verifica il token Slack e controlla che appartenga all'organizzazione specificata.
 * @param {string} token
 * @returns {Promise<void>}
 */
async function verifySlackToken(token) {
    if (!token) {
        throw new Error("Token mancante");
    }

    try {
        const response = await axios.get('https://slack.com/api/auth.test', {
            headers: {
                'Authorization': `Bearer ${token.replace("Bearer ", "")}`
            }
        });

        const slackData = response.data;
        
        if (!slackData.ok) {
            throw new Error(`Errore di autenticazione Slack: ${slackData.error}`);
        }

        if (slackData.team_id !== expectedOrgId) {
            throw new Error("Accesso negato: organizzazione non autorizzata");
        }

        console.log("Token Slack e organizzazione validati con successo.");

    } catch (error) {
        console.error("Errore durante la verifica del token Slack:", error.message);
        throw new Error("Token Slack non valido o errore di autenticazione");
    }
}

module.exports = { verifySlackToken };
