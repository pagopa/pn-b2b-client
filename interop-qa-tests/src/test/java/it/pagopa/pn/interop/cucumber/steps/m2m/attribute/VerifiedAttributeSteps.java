package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import io.cucumber.java.en.And;
import it.pagopa.interop.attribute.service.IM2MVerifiedAttributeClient;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.interop.attribute.service.IM2MVerifiedAttributeClient.VerifiedAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VerifiedAttributeSteps extends AbstractCommonSteps<VerifiedAttribute, UUID> {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MVerifiedAttributeClient client;

    public VerifiedAttributeSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("verifiedAttribute", clientTokenConfigurator.getM2mVerifiedAttributeClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mVerifiedAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
    }

    @And("viene effettuata la creazione dell'attributo verificato")
    public void creazioneAttributoDichiarato(VerifiedAttributeSeed payloadAttrCert) {
        VerifiedAttribute result = client.create(payloadAttrCert);
        var attributeContext = sharedStepsContext.getAttributeCommonContext();

        List<VerifiedAttribute> published = new ArrayList<>();
        published.add(result);

        attributeContext.setVerifiedPublished(published);
    }

    @Override
    public void bindActual(SharedStepsContext context, List<VerifiedAttribute> actualEntities) {
        var attributeContext = context.getAttributeCommonContext();
        attributeContext.setVerifiedActual(actualEntities);
    }

    @Override
    public List<VerifiedAttribute> bindExpected(SharedStepsContext context) {
        var attributeContext = context.getAttributeCommonContext();
        return attributeContext.getVerifiedPublished();
    }
}
