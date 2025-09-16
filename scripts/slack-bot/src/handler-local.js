const { handler } = require('./index');

const event = {
    httpMethod: "POST", 
    body: JSON.stringify({subject: "soggetto passato dinamicamente", recipients: [{ taxId:"GLLGLL64B15G702I" }]}),
    headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer your-test-token"
    },

};

(async () => {
    try {
        const result = await handler(event); 
        console.log("Risultato:", result);   
    } catch (error) {
        console.error("Errore:", error);     
    }
})();
