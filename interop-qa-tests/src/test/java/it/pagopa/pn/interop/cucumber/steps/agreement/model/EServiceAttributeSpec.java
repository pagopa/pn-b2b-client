package it.pagopa.pn.interop.cucumber.steps.agreement.model;

import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeCertifiedDiscreteComparator;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import lombok.Value;

@Value
public class EServiceAttributeSpec {
    AttributeKind kind;
    Integer group;
    AttributeCertifiedDiscreteComparator comparator;
    Integer value;
    Integer dailyCallsPerConsumer;
}
