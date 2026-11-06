package it.pagopa.pn.cucumber.steps.informalNotification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformalPayment {
    private String noticeCode;
    private String creditorTaxId;
    private Integer amount;
    private OffsetDateTime dueDate;
    private String attachmentKey;
    private String attachmentContentType;
}
