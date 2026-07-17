package it.pagopa.pn.cucumber.steps.informalNotification.context;

import lombok.Data;

@Data
public class InformalScenarioContext {

    // PA
    private String paName;
    private String currentCxId;
    private String currentGroupId;
    private String senderId;

    // destinatario
    private String recipientTaxId;
    private String recipientDenomination;

    // auth recipient
    private it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internawebrecipientinformal.model.CxTypeAuthFleet recipientCxType;

    // notifica
    private String notificationRequestId;
    private String iun;
}

