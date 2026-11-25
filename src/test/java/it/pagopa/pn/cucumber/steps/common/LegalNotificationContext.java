package it.pagopa.pn.cucumber.steps.common;

import it.pagopa.pn.client.b2b.pa.domain.Destinatario;
import lombok.Data;

@Data
public class LegalNotificationContext {
    public LegalRecipientContext recipient = new LegalRecipientContext();
    public String senderId;


    @Data
    public static class LegalRecipientContext {
        private Destinatario destinatario;
    }

}
