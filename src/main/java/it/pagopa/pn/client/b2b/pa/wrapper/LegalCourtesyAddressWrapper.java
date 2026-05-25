package it.pagopa.pn.client.b2b.pa.wrapper;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.LegalAddressType;
import lombok.Data;
import lombok.Getter;

@Data
public class LegalCourtesyAddressWrapper {

    @Getter
    public enum ChannelType {
        EMAIL("EMAIL"),
        SMS("SMS"),
        PEC("PEC"),
        APPIO("APPIO"),
        SERCQ("SERCQ"),
        SERCQ_SEND("SERCQ_SEND");

        private String value;

        ChannelType(String value) {
            this.value = value;
        }
    }

    private LegalAddressType addressType;

    private String recipientId;

    private String senderId;

    private String senderName;

    private ChannelType channelType;

    @lombok.ToString.Exclude
    private String value;
    private String requestId;
    private Boolean codeValid;
    private Boolean pecValid;

}
