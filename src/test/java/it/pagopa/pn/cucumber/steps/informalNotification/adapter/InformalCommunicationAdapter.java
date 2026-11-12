package it.pagopa.pn.cucumber.steps.informalNotification.adapter;

import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalCommunication;

public interface InformalCommunicationAdapter {

    InformalApiVersion version();

    // Converte il domain model nel model della versione API e invia la comunicazione (documenti già precaricati)
    InformalSendResult send(String cxId, InformalCommunication communication);
}
