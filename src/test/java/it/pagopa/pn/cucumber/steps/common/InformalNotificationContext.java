package it.pagopa.pn.cucumber.steps.common;

import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import lombok.Data;

@Data
public class InformalNotificationContext {
    public InformalRecipientContext recipient = new InformalRecipientContext();
    public String senderId;
    public String groupId;
    public String iun;


    @Data
    public static class InformalRecipientContext {
        private Destinatario destinatario;
    }

}
