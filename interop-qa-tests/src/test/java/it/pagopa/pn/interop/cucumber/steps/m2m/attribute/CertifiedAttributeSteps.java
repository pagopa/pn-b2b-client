package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import it.pagopa.interop.attribute.service.IM2MCertifiedAttributeClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.CertifiedDiscreteAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CertifiedAttributeSteps extends AbstractCommonSteps<CertifiedAttribute, UUID> {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MCertifiedAttributeClient client;
    private final PollingService pollingService;
    private final IHttpExecutor httpExecutor;

    public CertifiedAttributeSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("certifiedAttribute", clientTokenConfigurator.getM2mCertifiedAttributeClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mCertifiedAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.pollingService = sharedStepsContext.getPollingService();
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("viene effettuata la creazione dell'attributo certificato con successo")
    public void creazioneAttributoCertificatoConSuccesso(CertifiedAttributeSeed payloadAttrCert) {
        creazioneAttributoCertificato(payloadAttrCert);
        if(httpExecutor.getResponseStatus().isError()) {
            throw new IllegalStateException("La creazione dell'attributo certificato non ha avuto successo. Visionare logs per maggiori dettagli.");
        }
    }

    @And("viene effettuata la creazione dell'attributo certificato")
    public void creazioneAttributoCertificato(CertifiedAttributeSeed payloadAttrCert) {
        CertifiedAttribute result = client.create(payloadAttrCert);
        var attributeContext = sharedStepsContext.getAttributeCommonContext();

        List<CertifiedAttribute> published = new ArrayList<>();
        published.add(result);

        attributeContext.setCertifiedPublished(published);
    }

    @Then("la risposta contiene almeno {int} attributo certificato")
    public void listCheckCount(int expectedSize) {
        List<CertifiedAttribute> actual = sharedStepsContext.getAttributeCommonContext().getCertifiedActual();
        assertThat(actual).hasSizeGreaterThanOrEqualTo(expectedSize);
    }

    @Then("la risposta contiene esattamente i {int} attributi certificati discreti creati")
    public void listCheck(int expectedSize) {
        List<CertifiedDiscreteAttribute> published = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscretePublished();
        List<CertifiedDiscreteAttribute> actual = sharedStepsContext.getAttributeCommonContext().getCertifiedDiscreteActual();

        assertThat(actual).hasSize(expectedSize);
        assertThat(actual).hasSizeGreaterThanOrEqualTo(expectedSize);

        published.forEach(attr -> {
            assertThat(actual).anyMatch(attr::equals);
        });
    }

    @Override
    public void bindActual(SharedStepsContext context, List<CertifiedAttribute> actualEntities) {
        var attributeContext = context.getAttributeCommonContext();
        attributeContext.setCertifiedActual(actualEntities);
    }

    @Override
    public List<CertifiedAttribute> bindExpected(SharedStepsContext context) {
        var attributeContext = context.getAttributeCommonContext();
        return attributeContext.getCertifiedPublished();
    }
}
