const { handler } = require('./index');

const event = {
    httpMethod: "POST", 
    body: JSON.stringify({subject: "soggetto passato dinamicamente"}), 
    headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer your-test-token"  // Aggiungi il token qui
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
