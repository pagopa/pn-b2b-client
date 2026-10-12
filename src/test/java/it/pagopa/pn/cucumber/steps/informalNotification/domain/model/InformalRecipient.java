package it.pagopa.pn.cucumber.steps.informalNotification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformalRecipient {
    private RecipientType recipientType;
    private UUID messageId;
    private String taxId;
    private String denomination;
    private String digitalDomicile;
    private String phoneNumber;
    private String email;
    private InformalPhysicalAddress physicalAddress;
    @Builder.Default
    private List<InformalPayment> payments = new ArrayList<>();
    @Builder.Default
    private List<String> additionalLanguages = new ArrayList<>();
}
