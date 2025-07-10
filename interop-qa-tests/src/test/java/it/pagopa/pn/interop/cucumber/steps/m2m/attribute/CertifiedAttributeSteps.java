package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IM2MCertifiedAttributeClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
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

    @And("viene effettuata la creazione dell'attributo certificato")
    public void creazioneAttributoCertificato(CertifiedAttributeSeed payloadAttrCert) {
        CertifiedAttribute result = client.create(payloadAttrCert);
        var attributeContext = sharedStepsContext.getAttributeCommonContext();

        List<CertifiedAttribute> published = new ArrayList<>();
        published.add(result);

        attributeContext.setCertifiedPublished(published);
    }

    @When("l'utente tenta di recuperare la lista di certifiedAttribute filtrata per nome")
    public void getFilteredList() {
        List<CertifiedAttribute> expected = sharedStepsContext.getAttributeCommonContext()
            .getCertifiedPublished();
        this.pollingService.makePolling(() -> httpExecutor.performCall(
            () -> this.client.getFilteredBy(expected.stream().map(CertifiedAttribute::getName).toList())),
            status -> status.is2xxSuccessful() && !((List<CertifiedAttribute>)httpExecutor.getResponse()).isEmpty(),
            "La lista non contiene tutti gli attributi certificati attesi. Visionare logs delle chiamate HTTP per maggiori dettagli.");
    }

    @Then("la lista ottenuta contiene l'attributo certificato creato")
    public void listCheck() {
        List<CertifiedAttribute> expected = sharedStepsContext.getAttributeCommonContext()
            .getCertifiedPublished();
        List<CertifiedAttribute> actual = ((List<CertifiedAttribute>)httpExecutor.getResponse());
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
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
