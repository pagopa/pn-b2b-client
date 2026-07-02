package it.pagopa.pn.cucumber.steps.informalNotification.datatable;

import io.cucumber.java.DataTableType;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRequestV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequest;
import it.pagopa.pn.cucumber.steps.informalNotification.mapper.InformalNotificationRequestMapper;

import java.util.Map;

public class DataTableTypeNotificationInformal {

    //todo t bonarie
    private InformalNotificationRequestMapper mapper = new InformalNotificationRequestMapper();

    //MessagesApi
    @DataTableType
    public NewMessageRequest newMessageRequest(Map<String, String> data) {
        return mapper.buildNewMessageRequest(data);
    }

    //NewInformalNotificationApi
    @DataTableType
    public InformalNotificationRequestV1 informalNotificationRequest(Map<String, String> data) {
        return mapper.buildInformalNotificationRequest(data);
    }
}
