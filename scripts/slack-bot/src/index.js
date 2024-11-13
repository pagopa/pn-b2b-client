const https = require('https');
const fs = require('fs');
const axios = require('axios');
const path = require('path');
const uploadNotification = require('./preloadDocument');
const { verifySlackToken } = require('./slackAuth');

const defaultRequestPath = path.join(__dirname, 'default-request.json');
const defaultRequest = JSON.parse(fs.readFileSync(defaultRequestPath, 'utf8'));
const API_KEY = '68c854bc-a234-4f08-b738-d632814c84cc';
const NEW_NOTIFICATION_URL = 'https://api.dev.notifichedigitali.it/delivery/v2.4/requests';

const BEARER_TOKEN_PA_1 = 'token'

/**
 * Creazione della notifica in base a dei parametri forniti e restituzione dello IUN appena creato.
 * @param {object} event
 * @returns {string}
 */
exports.handler = async (event) => {
    console.log("Contollo accesso ai test di compilazione");
    const token = event.headers?.Authorization || event.headers?.authorization;
    //console.log('token' + token);
    
    try {
        params = JSON.parse(event.body); 
        let defaultParams = defaultRequest;
        const updateParams = setDefaultValues(defaultParams, params);
        const finalParams = await uploadNotification(updateParams);
             //.catch(err => console.error('Failed to preload:', err));
        console.log('request finale: ' + finalParams)

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
        ...params                      
    };
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
