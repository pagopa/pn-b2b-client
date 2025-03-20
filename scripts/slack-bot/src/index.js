const dotenv = require('dotenv');
const https = require('https');
const fs = require('fs');
const axios = require('axios');
const path = require('path');
const uploadNotification = require('./preloadDocument');
const generateRandomValue = require('./commonLogic');
const { verifySlackToken } = require('./slackAuth');

const env = process.env.NODE_ENV || 'development';
dotenv.config({ path: `.env.${env}` });

const defaultRequestPath = path.join(__dirname, 'default-request.json');
const defaultRequest = JSON.parse(fs.readFileSync(defaultRequestPath, 'utf8'));
const API_KEY = process.env.PN_EXTERNAL_API_KEY_GA;

const NEW_NOTIFICATION_URL = process.env.PN_EXTERNAL_BASE_URL_SENT_NOTIFICATION;

const BEARER_TOKEN_PA_1 = 'token'

/**
 * Creazione della notifica in base a dei parametri forniti e restituzione dello IUN appena creato.
 * @param {object} event
 * @returns {string}
 */
exports.handler = async (event) => {
    console.log("Contollo accesso ai test di compilazione");
    const token = event.headers?.Authorization || event.headers?.authorization;
    
    try {
        params = JSON.parse(event.body); 
        let defaultParams = defaultRequest;
        const updateParams = setDefaultValues(defaultParams, params);

        const finalParams = await uploadNotification(updateParams);
             //.catch(err => console.error('Failed to preload:', err));
        console.log('request finale: ' + JSON.stringify(finalParams, null, 2))

        const data = await sentNotification(finalParams);
        console.log('data' + data.body)
    } catch (error) {
        console.log(error)
        return {
            statusCode: 400,
            body: JSON.stringify({ message: "Body della richiesta non valido" })
        };
    }

};

const setDefaultValues = (defaults, params) => {
return {
        ...defaults,
        paProtocolNumber: generateRandomValue(),
        group: process.env.SENDER_GROUP_ID,
        ...params,
        recipients: params.recipients ? params.recipients.map((recipient, index) => ({
            ...defaults.recipients[index],
            ...recipient
        })) : defaults.recipients
    };
    /*return {
        ...defaults,
        paProtocolNumber: generateRandomValue(),
        group: process.env.SENDER_GROUP_ID,
        ...params
    };*/
};

const checkParameter = (params) => {
    const requiredFields = ['notificationType', 'parametro2']; 

    for (const field of requiredFields) {
        if (!params[field]) {
            throw new Error(`Il campo "${field}" è obbligatorio.`);
        }
    }
};

const sentNotification = async (params) => {
    console.log(params);
    
    const paramsSafeStorage = {
        headers: {
            'Accept': 'application/json',
            'x-api-key': API_KEY 
        }
    };

        console.log('new not header: ' + paramsSafeStorage.headers)
    
    try {
        const response = await axios.post(NEW_NOTIFICATION_URL, params, paramsSafeStorage);
        console.log('Risposta:', response.data); 
        return response.data;  
    } catch (error) {
        console.error('Errore durante la richiesta:', error);
        throw error;  
    }
}
