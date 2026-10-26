package it.pagopa.pn.cucumber.steps.informalNotification.adapter;

import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalCommunication;

import java.io.IOException;

public interface InformalCommunicationAdapter {
    InformalSendResult sendInformalNotification(String cxId, String paName, InformalCommunication domainModel) throws IOException;
}
