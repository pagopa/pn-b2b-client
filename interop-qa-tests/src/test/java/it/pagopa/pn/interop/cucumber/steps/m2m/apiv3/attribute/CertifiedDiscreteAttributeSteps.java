package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.attribute;

import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IM2MV3CertifiedDiscreteAttributeClient;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CertifiedDiscreteAttributeSteps extends AbstractCommonSteps<CertifiedDiscreteAttribute, UUID> {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MV3CertifiedDiscreteAttributeClient client;

    public CertifiedDiscreteAttributeSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("certifiedDiscreteAttribute", clientTokenConfigurator.getM2mV3CertifiedDiscreteAttributeClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mV3CertifiedDiscreteAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
    }

    @When("viene effettuata la creazione dell'attributo certificato discreto")
    public void createCertifiedDiscreteAttribute(CertifiedDiscreteAttributeSeed payloadAttrCert) {
        CertifiedDiscreteAttribute result = client.create(payloadAttrCert);
        var attributeContext = sharedStepsContext.getAttributeCommonContext();

        List<CertifiedDiscreteAttribute> published = new ArrayList<>();
        published.add(result);

        attributeContext.setCertifiedDiscretePublished(published);
    }

    @Override
    public void bindActual(SharedStepsContext context, List<CertifiedDiscreteAttribute> actualEntities) {
        var attributeContext = context.getAttributeCommonContext();
        attributeContext.setCertifiedDiscreteActual(actualEntities);
    }

    @Override
    public List<CertifiedDiscreteAttribute> bindExpected(SharedStepsContext context) {
        var attributeContext = context.getAttributeCommonContext();
        return attributeContext.getCertifiedDiscretePublished();
    }
}
