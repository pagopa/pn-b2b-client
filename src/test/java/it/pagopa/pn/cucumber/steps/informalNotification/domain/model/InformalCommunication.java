package it.pagopa.pn.cucumber.steps.informalNotification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformalCommunication {
    private String campaignId;
    private String senderDenomination;
    private String paProtocolNumber;
    private String idempotenceToken;
    private String subject;
    private String group;
    @Builder.Default
    private List<InformalRecipient> recipients = new ArrayList<>();
    @Builder.Default
    private List<InformalDocument> documents = new ArrayList<>();
}
