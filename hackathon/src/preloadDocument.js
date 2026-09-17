const dotenv = require('dotenv');
const crypto = require('crypto');
const axios = require('axios');
const fs = require('fs');
const path = require('path');
const generateRandomValue = require('./commonLogic');

const env = process.env.NODE_ENV || 'development';
dotenv.config({ path: `.env.${env}` });

// Costanti
const API_URL = process.env.PN_EXTERNAL_BASE_URL_UPLOAD_NOTIFICATION;
const API_KEY = process.env.PN_EXTERNAL_API_KEY_GA;

// lettura e codifica esadecimale del file
function computeSha256(resName) {
    const absolutePath = path.resolve(__dirname, resName);
    const fileData = fs.readFileSync(absolutePath); // Lettura sincrona del file
    const hash = crypto.createHash('sha256').update(fileData).digest('base64');
    return hash;
}

// ottenere la risposta di pre-caricamento
const getPreLoadResponse = async (sha256) => {
    const request = {
        preloadIdx: "0",
        sha256: sha256,
        contentType: "application/pdf"
    };

    const headers = {
        'x-api-key': API_KEY,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };

    try {
        const response = await axios.post(API_URL, [request], { headers });
        return response.data[0];
    } catch (error) {
        console.error('Error in getPreLoadResponse:', error);
        throw error;
    }
};

const loadToPresigned = async (url, secret, sha256, resource, resourceType, depth = 0) => {
    const absolutePath = path.resolve(__dirname, resource);
    const data = fs.readFileSync(absolutePath);
    const paramsSafeStorage = {
        headers: {
            'Content-Type': 'application/pdf',
            'x-amz-checksum-sha256': sha256,
            'Content-Length': data.byteLength,
            'x-amz-meta-secret': secret,
        },
        responseType: 'none',
    };

    console.log("headers:", paramsSafeStorage.headers);
    
    try {
        const response = await axios.put(url, data, paramsSafeStorage);
        console.log('Upload successful:', response.data);
    } catch (error) {
        if (depth >= 5) throw error;
        console.log("Upload failed, retrying..." + error);
        await new Promise(resolve => setTimeout(resolve, 2000)); // Attendi 2 secondi prima di riprovare
        console.error(`[Retry ${depth + 1}] URL: ${url}, Secret: ${secret}, SHA256: ${sha256}`);
        return loadToPresigned(url, secret, sha256, resource, resourceType, depth + 1);
    }
};

// Funzione generica di pre-caricamento
const preloadGeneric = async (resourceName) => {
    const sha256 = await computeSha256(resourceName);
    const preLoadResponse = await getPreLoadResponse(sha256);
    console.log('preload response' + preLoadResponse);
    const { key, secret, url } = preLoadResponse;

    console.log(`Attachment resourceKey=${resourceName}, sha256=${sha256}, secret=${secret}, presignedUrl=${url}`);
    await loadToPresigned(url, secret, sha256, resourceName);
    
    return { key, sha256 };
};

// Funzione per pre-caricare un documento
const preloadDocument = async (document) => {
    try {
        const absolutePath = path.resolve(__dirname, document.ref.key);
        const { key, sha256 } = await preloadGeneric(absolutePath);
        document.ref.key = key;
        document.ref.versionToken = 'v1';
        document.digests.sha256 = sha256;
        return document;
    } catch (error) {
        console.error('Error in preloadDocument:', error);
        throw new Error("Exception: " + error.message);
    }
};

// Funzione principale per caricare il documento
const uploadNotification = async (request) => {
    const newDocs = [];

    for (const doc of request.documents) {
        if (doc) {
            try {
                const preloadedDoc = await preloadDocument(doc);
                newDocs.push(preloadedDoc);
            } catch (error) {
                console.error("Error in uploadNotification:", error);
                throw new Error("Exception: " + error.message);
            }
        }
    }
    request.documents = newDocs;
    await preloadPayDocumentV23(request);
    return request;
};

// Funzione per pre-caricare i documenti di pagamento
const preloadPayDocumentV23 = async (request) => {
    console.log('paymentList preload')
    for (const recipient of request.recipients) {
        const paymentList = recipient.payments;
        console.log('paymentList preload payments ' + recipient.payments)
        if (paymentList) {
            await setAttachmentWithSleepV23(paymentList);
        }
    }
};

const setAttachmentWithSleepV23 = async (paymentList) => {
    console.log('paymentList ' + paymentList)
    for (const paymentInfo of paymentList) {
        try {
            await new Promise(resolve => setTimeout(resolve, Math.random() * 350));

            if (paymentInfo.pagoPa) {
                paymentInfo.pagoPa.noticeCode = generateRandomValue();
                paymentInfo.pagoPa.attachment = await preloadAttachment(paymentInfo.pagoPa.attachment);
            }

            if (paymentInfo.F24) {
                paymentInfo.F24.metadataAttachment = await preloadMetadataAttachment(paymentInfo.F24.metadataAttachment);
            }
        } catch (error) {
            console.error("Error in setAttachmentWithSleepV23:", error);
            throw new Error("PnB2bException: " + error.message);
        }
    }
};

const preloadAttachment = async (attachment) => {
    if (attachment) {
        try {
            const preloadAttachmentResult = await preloadGeneric(attachment.ref.key);
            attachment.ref.key = preloadAttachmentResult.key;
            attachment.ref.versionToken = 'v1';
            attachment.digests = {
                "sha256" : preloadAttachmentResult.sha256
            }
            
            return attachment;
        } catch (error) {
            console.error("Error in preloadAttachment:", error);
            throw new Error("Error in preloading attachment: " + error.message);
        }
    }
    return attachment;
};

const preloadMetadataAttachment = async (attachment) => {
    if (attachment) {
        try {
            const preloadAttachmentResult = await preloadGeneric(attachment.ref.key);
            return {
                ...attachment,
                key: preloadAttachmentResult.key,
                versionToken: 'v1',
                digests: preloadAttachmentResult.digests
            };
        } catch (error) {
            console.error("Error in preloadMetadataAttachment:", error);
            throw new Error("Error in preloading metadata attachment: " + error.message);
        }
    }
    return attachment;
};

module.exports = uploadNotification;
