package it.pagopa.pn.client.b2b.pa.polling.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;


@Getter
@Builder
@Setter
public class PnPollingParameter {
    private int user;
    private int deepCount;
    private UUID streamId;
    private String value;
    private String lastEventId;
    private PollingType pollingType;
    public enum PollingType {SLOW, RAPID, SHORT}
    private PnPollingPredicate pnPollingPredicate;
    private PnPollingWebhook pnPollingWebhook;
}