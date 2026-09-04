package it.pagopa.pn.interop.cucumber.steps.m2m.apiv3.attribute;

import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IM2MV3CertifiedDiscreteAttributeClient;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.ICommonSteps;
import org.junit.jupiter.api.Assertions;

import java.util.*;

public class CertifiedDiscreteAttributeSteps extends AbstractCommonSteps<CertifiedDiscreteAttribute, UUID> {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MV3CertifiedDiscreteAttributeClient client;
    private final IHttpExecutor httpExecutor;

    public CertifiedDiscreteAttributeSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("certifiedDiscreteAttribute", clientTokenConfigurator.getM2mV3CertifiedDiscreteAttributeClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mV3CertifiedDiscreteAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("viene effettuata la creazione dell'attributo certificato discreto con successo")
    public void createCertifiedDiscreteAttributeSuccessfully(CertifiedDiscreteAttributeSeed payloadAttrCert) {
        createCertifiedDiscreteAttribute(payloadAttrCert);
        if(httpExecutor.getResponseStatus().isError()) {
            throw new IllegalStateException("La creazione dell'attributo certificato non ha avuto successo. Visionare logs per maggiori dettagli.");
        }
    }

    @When("viene effettuata la creazione degli attributi certificati discreti")
    public void createCertifiedDiscreteAttributes(List<CertifiedDiscreteAttributeSeed> payloadAttrCert) {
        payloadAttrCert.forEach(this::createCertifiedDiscreteAttribute);
    }

    @When("viene effettuata la creazione dell'attributo certificato discreto")
    public void createCertifiedDiscreteAttribute(CertifiedDiscreteAttributeSeed payloadAttrCert) {
        CertifiedDiscreteAttribute result = client.create(payloadAttrCert);
        sharedStepsContext.getAttributeCommonContext().getCertifiedDiscretePublished().add(result);
    }

    @When("si tenta la creazione dell'attributo certificato discreto senza passare parametri nella richiesta")
    public void createCertifiedDiscreteAttributeWithoutParameters() {
        client.tryCreationWithMissingData();
    }

    @When("l'utente tenta di recuperare un attributo certificato discreto con un l'id dell'attributo dichiarato creato, senza ottenere alcun risultato")
    public void getByIdOfDifferentAttributeKind() {
        var declaredAttributes = sharedStepsContext.getAttributeCommonContext().getDeclaredPublished();
        UUID attributeId = declaredAttributes.get(declaredAttributes.size() - 1).getId();
        CertifiedDiscreteAttribute actual = client.get(attributeId);
        Assertions.assertNull(actual);
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
