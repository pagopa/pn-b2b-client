package it.pagopa.interop.attribute.model;


import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeCertifiedDiscreteComparator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscreteAttribute extends it.pagopa.interop.generated.openapi.clients.bff.model.Attribute {

    private Integer destinationGroup;
    private AttributeCertifiedDiscreteComparator attributeCertifiedDiscreteComparator;
    private Integer discreteValue;
}
