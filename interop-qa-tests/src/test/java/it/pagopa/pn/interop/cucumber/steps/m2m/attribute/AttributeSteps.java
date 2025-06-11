package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import io.cucumber.java.en.And;
import it.pagopa.interop.attribute.service.IM2MAttributeClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttributeSteps extends AbstractCommonSteps<CertifiedAttribute, UUID> {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MAttributeClient client;


    public AttributeSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("certifiedAttribute", clientTokenConfigurator.getM2mAttributeClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
    }

    @And("viene effettuata la creazione dell'attributo certificato")
    public void creazioneAttributoCertificato(CertifiedAttributeSeed payloadAttrCert) {
        CertifiedAttribute result = client.create(payloadAttrCert);
        var attributeContext = sharedStepsContext.getAttributeCommonContext();

        List<CertifiedAttribute> published = new ArrayList<>();
        published.add(result);

        attributeContext.setPublished(published);
    }

    @Override
    public void bindActual(SharedStepsContext context, List<CertifiedAttribute> actualEntities) {
        var attributeContext = context.getAttributeCommonContext();
        attributeContext.setActual(actualEntities);
    }

    @Override
    public List<CertifiedAttribute> bindExpected(SharedStepsContext context) {
        var attributeContext = context.getAttributeCommonContext();
        return attributeContext.getPublished();
    }
}
