package it.pagopa.pn.client.b2b.pa.polling.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;


@Getter
@Builder
@Setter
public class PnPollingParameter {
    private int user = 0;
    private UUID streamId;
    private PollingType pollingType;
    public enum PollingType {SLOW, RAPID, SHORT}
    private String value;
    private PnPollingPredicate pnPollingPredicate;
}