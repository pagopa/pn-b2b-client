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
    // risposta originale della versione API, solo a scopo di log
    private Object rawResponse;
}
