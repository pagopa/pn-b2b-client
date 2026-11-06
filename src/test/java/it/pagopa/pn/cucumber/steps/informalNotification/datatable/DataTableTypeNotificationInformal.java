package it.pagopa.pn.cucumber.steps.informalNotification.datatable;

import io.cucumber.java.DataTableType;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequest;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalCommunication;
import it.pagopa.pn.cucumber.steps.informalNotification.mapper.InformalNotificationRequestMapper;

import java.util.Map;

public class DataTableTypeNotificationInformal {

    private InformalNotificationRequestMapper mapper = new InformalNotificationRequestMapper();

    //MessagesApi
    @DataTableType
    public NewMessageRequest newMessageRequest(Map<String, String> data) {
        return mapper.buildNewMessageRequest(data);
    }

    //NewInformalNotificationApi
    @DataTableType
    public InformalCommunication informalNotificationRequest(Map<String, String> data) {
        return mapper.buildInformalNotificationRequest(data);
    }
}
