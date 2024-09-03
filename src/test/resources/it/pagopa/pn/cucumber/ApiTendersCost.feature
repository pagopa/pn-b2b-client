
Feature: Api Tenders Cost



3 massi
Descrizione:
Verificare tramite API il recupero dei costi associati ad uno specifico CAP per ogni prodotto disponibile ( AR, RS, 890)  e per ogni scaglione.
Precondizioni:
Le tabelle di DynamoDB (<pn-PaperChannelTender>
<pn-PaperChannelGeokey>
<pn-PaperChannelDeliveryDriver>
<pn-PaperChannelCost>) sono state correttamente create
Step:
Invocare API POST /paper-channel-private/v2/tenders/{tenderId}/cost/calculate
Inserire un valido ${TenderId}
Compilare correttamente la Request body
Dati del test:
NA
Risultato atteso:
Il sistema restituisce il costo calcolato della spedizione arrotondato alla seconda cifra decimale
Status Code: 200
positivo
Automation Only
Test massivo
Work item 4.2.4
------------------------------------------------------------------------------------------------------------------------------------------------

4
Descrizione:
Verificare tramite API il recupero dei costi associati ad uno specifico CAP con un campo mandatory della Request body settato a  NULL
Precondizioni: Le tabelle di DynamoDB (<pn-PaperChannelTender>
<pn-PaperChannelGeokey>
<pn-PaperChannelDeliveryDriver>
<pn-PaperChannelCost>) sono state correttamente create
Step:
Invocare API POST /paper-channel-private/v2/tenders/{tenderId}/cost/calculate
Inserire un valido ${TenderId}
Settare uno o più campi del Request body a NULL
Dati del test:
NA
Risultato atteso:
Verrà restituito errore con Status Code: 400
negativo
Automation Only
Il test verrà eseguito su un singolo CAP
Work item 4.2.4



@apiTendersCost
Scenario Outline: [API-***] Invocazione del servizio con TenderId valorizzato correttamente ma senza Lot
Given come operatore devo accedere al costo di una gara con tenderId "<TENDERID>" product  "<PRODUCT>" e con zone "<ZONE>" lot "<LOT>" deliveryDriverId "<DELIVERYDRIVERID>"
Then il servizio risponde con errore "400"

Examples:
| TENDERID  | PRODUCT  | ZONE     | LOT 		| DELIVERYDRIVERID 		|
| **** 		| NULL     |   NULL   | NULL		| NULL                 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"?????","detail":"must not be null"}]}

----------------------------------------------------------------------------------------------------------------------------------------


5
Descrizione:
Verificare tramite API il recupero dei costi associati ad uno specifico CAP con ${TenderId} errato
Precondizioni: Le tabelle di DynamoDB (<pn-PaperChannelTender>
<pn-PaperChannelGeokey>
<pn-PaperChannelDeliveryDriver>
<pn-PaperChannelCost>) sono state correttamente create
Step:
Invocare API POST /paper-channel-private/v2/tenders/{tenderId}/cost/calculate
Utilizzare un ${TenderId} inesistente
Compilare correttamente la Request body
Dati del test:
NA
Risultato atteso:
Verrà restituito errore con Status Code: 404
negativo
Automation Only
Il test verrà eseguito su un singolo CAP
Work item 4.2.4



@apiTendersCost
Scenario Outline: [API-*****] Invocazione del servizio con TenderId errato
Given come operatore devo accedere al costo di una gara di cui conosco l’identificativo (TenderId) "<TENDERID>"
Then il servizio risponde con errore "404"

Examples:
| TENDERID      |
| ********* 	|
    #404 Not Found









-------------------------------------------------------------------------------------------------------------------------------------------------------
6 ?
Descrizione:
Verificare tramite API il recupero dei costi associati ad uno specifico Stato Estero per ogni prodotto disponibile (RIR, RIS)  e per ogni scaglione.
Precondizioni:
Le tabelle di DynamoDB (<pn-PaperChannelTender>
<pn-PaperChannelGeokey>
<pn-PaperChannelDeliveryDriver>
<pn-PaperChannelCost>) sono state correttamente create
Step:
Invocare API POST /paper-channel-private/v2/tenders/{tenderId}/cost/calculate
Inserire un valido ${TenderId}
Compilare correttamente la Request body
Dati del test:
NA
Risultato atteso:
Il sistema restituisce il costo calcolato della spedizione arrotondato alla seconda cifra decimale
Status Code: 200
positivo
Automation Only
Questo Test dovrà essere implementato per un solo Stato Estero per zona (1,2,3)
E' opportuno implementare questa tipologia di test?
Work item 4.2.4



-------------------------------------------------------------------------------------------------------------------------------------------------------------------------
7 (ripwetizione del 4?)
Descrizione:
Verificare tramite API il recupero dei costi associati ad uno specifico Stato Estero con uno campo  mandatory della Request body settato a  NULL
Precondizioni: Le tabelle di DynamoDB (<pn-PaperChannelTender>
<pn-PaperChannelGeokey>
<pn-PaperChannelDeliveryDriver>
<pn-PaperChannelCost>) sono state correttamente create
Step:
Invocare API POST /paper-channel-private/v2/tenders/{tenderId}/cost/calculate
Inserire un valido ${TenderId}
Settare uno o più campi del Request body a NULL
Dati del test:
NA
Risultato atteso:
Verrà restituito errore con Status Code: 400
negativo
Automation Only
Il test verrà eseguito su un singolo Stato Estero
Work item 4.2.4


@apiTendersCost
Scenario Outline: [API-***] Invocazione del servizio con TenderId valorizzato correttamente ma senza ????
Given come operatore devo accedere al costo di una gara con tenderId "<TENDERID>" product  "<PRODUCT>" e con zone "<ZONE>" lot "<LOT>" deliveryDriverId "<DELIVERYDRIVERID>"
Then il servizio risponde con errore "400"

Examples:
| TENDERID    | PRODUCT | ZONE | LOT 		| DELIVERYDRIVERID 		|
| **** 		| NULL     |   NULL   | NULL		| NULL                 |
    #errors":[{"code":"PN_GENERIC_INVALIDPARAMETER","element":"?????","detail":"must not be null"}]}








-------------------------------------------------------------------------------------------------------------------

8 (ripetizione del 5? )
Descrizione:
Verificare tramite API il recupero dei costi associati ad uno specifico Stato Estero con ${TenderId} errato
Precondizioni: Le tabelle di DynamoDB (<pn-PaperChannelTender>
<pn-PaperChannelGeokey>
<pn-PaperChannelDeliveryDriver>
<pn-PaperChannelCost>) sono state correttamente create
Step:
Invocare API POST /paper-channel-private/v2/tenders/{tenderId}/cost/calculate
Utilizzare un ${TenderId} inesistente
Compilare correttamente la Request body
Dati del test:
NA
Risultato atteso:
Verrà restituito errore con Status Code: 404
negativo
Automation Only
Il test verrà eseguito su un singolo Stato Estero
Work item 4.2.4



@apiTendersCost
Scenario Outline: [API-*****] Invocazione del servizio con TenderId errato
Given come operatore devo accedere al costo di una gara di cui conosco l’identificativo (TenderId) "<TENDERID>"
Then il servizio risponde con errore "404"

Examples:
| TENDERID      |
| ********* 	|
 #404 Not Found





----------------------------------------------------------------------------------------------


Descrizione: Invocare l’API  per il recupero di tutte le gare
Precondizioni: Le tabelle di DynamoDB (<pn-PaperChannelTender>
<pn-PaperChannelGeokey>
<pn-PaperChannelDeliveryDriver>
<pn-PaperChannelCost>) sono state correttamente create
Step:
Invocare l’API GET /paper-channel-private/v2/tenders
Dati del test:
NA
Risultato atteso:
Il sistema restituisce tutte le gare
Status Code: 200
Positivo
Automation Only



#***.** Come operatore devo accedere all’elenco di tutte le Gare ...
@apiTendersCost
Scenario: [API-*****] Invocazione del servizio e verifica restituzione array di oggetti con ***
Given l'operatore richiede l'elenco di tutte le gare
Then Il servizio risponde con esito positivo con la lista delle gare