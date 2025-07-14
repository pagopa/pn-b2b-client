package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class UpperAgreement {
    private UpperAgreementState state;
    private boolean suspendedByProducer;
    private boolean suspendedByConsumer;

    public static UpperAgreement from(Agreement agreement) {
        return UpperAgreement.of(
            UpperAgreementState.from(agreement.getState()),
            isTrue(agreement.getSuspendedByProducer()),
            isTrue(agreement.getSuspendedByConsumer()));
    }

    public static UpperAgreement from(it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Agreement agreement) {
        return UpperAgreement.of(
            UpperAgreementState.from(agreement.getState()),
            isTrue(agreement.getSuspendedByProducer()),
            isTrue(agreement.getSuspendedByConsumer()));
    }
}
