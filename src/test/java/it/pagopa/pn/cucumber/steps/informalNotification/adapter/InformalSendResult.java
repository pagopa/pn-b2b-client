package it.pagopa.pn.cucumber.steps.informalNotification.adapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformalSendResult {
    private String notificationRequestId;
    private String status;
    private Object rawResponse;
}
