package it.pagopa.pn.client.b2b.pa.polling.dto;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.payment.BffPaymentInfoItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@ToString(callSuper = true)
@Getter
@Setter
public class PnPollingResponsePaymentInfo extends PnPollingResponse {
    private List<BffPaymentInfoItem> paymentInfoResponse;
    private Integer amount;
}
