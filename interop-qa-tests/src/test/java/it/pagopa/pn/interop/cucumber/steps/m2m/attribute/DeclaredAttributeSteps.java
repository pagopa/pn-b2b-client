package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import io.cucumber.java.en.And;
import it.pagopa.interop.attribute.service.IM2MDeclaredAttributeClient;
import it.pagopa.interop.attribute.service.IM2MDeclaredAttributeClient.DeclaredAttribute;
import it.pagopa.interop.attribute.service.IM2MDeclaredAttributeClient.DeclaredAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeclaredAttributeSteps extends AbstractCommonSteps<DeclaredAttribute, UUID> {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MDeclaredAttributeClient client;

    public DeclaredAttributeSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("declaredAttribute", clientTokenConfigurator.getM2mDeclaredAttributeClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mDeclaredAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
    }

    @And("viene effettuata la creazione dell'attributo dichiarato")
    public void creazioneAttributoDichiarato(DeclaredAttributeSeed payloadAttrCert) {
        DeclaredAttribute result = client.create(payloadAttrCert);
        var attributeContext = sharedStepsContext.getAttributeCommonContext();

        List<DeclaredAttribute> published = new ArrayList<>();
        published.add(result);

        attributeContext.setDeclaredPublished(published);
    }

    @Override
    public void bindActual(SharedStepsContext context, List<DeclaredAttribute> actualEntities) {
        var attributeContext = context.getAttributeCommonContext();
        attributeContext.setDeclaredActual(actualEntities);
    }

    @Override
    public List<DeclaredAttribute> bindExpected(SharedStepsContext context) {
        var attributeContext = context.getAttributeCommonContext();
        return attributeContext.getDeclaredPublished();
    }
}
