package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.agreement.service.IM2MTenantClient;
import it.pagopa.interop.attribute.service.IM2MVerifiedAttributeClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.*;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class VerifiedAttributeSteps extends AbstractCommonSteps<VerifiedAttribute, UUID> {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MVerifiedAttributeClient verifiedAttributeClient;
    private final IM2MTenantClient tenantClient;
    private final IHttpExecutor httpExecutor;
    private final DelayService delayService;

    public VerifiedAttributeSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("verifiedAttribute", clientTokenConfigurator.getM2mVerifiedAttributeClient(), sharedStepsContext);
        this.verifiedAttributeClient = clientTokenConfigurator.getM2mVerifiedAttributeClient();
        this.tenantClient = clientTokenConfigurator.getM2mTenantClient();
        this.sharedStepsContext = sharedStepsContext;
        this.verifiedAttributeClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
        this.delayService = sharedStepsContext.getDelayService();
    }

    @And("viene effettuata la creazione dell'attributo verificato")
    public void creazioneAttributoVerificato(VerifiedAttributeSeed payloadAttrCert) {
        VerifiedAttribute result = verifiedAttributeClient.create(payloadAttrCert);
        var attributeContext = sharedStepsContext.getAttributeCommonContext();

        List<VerifiedAttribute> published = new ArrayList<>();
        published.add(result);

        attributeContext.setVerifiedPublished(published);
    }

    // NOTA 22/07/2025: questo step funziona qualora la creazione dell'attributo verificato
    // sia stata fatta attraverso API BFF
    @When("l'utente tenta di recuperare l'attributo verificato creato")
    public void recuperaAttributoVerificato() {
        UUID attributeId = this.sharedStepsContext.getAttributeCommonContext().getAttributeId();
        httpExecutor.performCall(() -> verifiedAttributeClient.get(attributeId));
    }

    @Then("l'attributo verificato è stato creato correttamente")
    public void verificaAttributoVerificato() {
        List<VerifiedAttribute> verifiedPublished = sharedStepsContext.getAttributeCommonContext()
            .getVerifiedPublished();

        VerifiedAttribute actual = (VerifiedAttribute) httpExecutor.getResponse();
        VerifiedAttribute expected = verifiedPublished.get(verifiedPublished.size() - 1);
        assertSoftly(softly -> {
            OffsetDateTime expectedCreationDate = OffsetDateTime.parse(expected.getCreatedAt());
            OffsetDateTime actualCreationDate = OffsetDateTime.parse(actual.getCreatedAt());
            softly.assertThat(actualCreationDate)
                .as("Verifica che la data di creazione risultante sia ragionevolmente"
                    + " vicina a quella con cui l'attributo è stato effettivamente creato")
                .isCloseTo(expectedCreationDate, within(10, SECONDS));

            actual.setCreatedAt(null);
            expected.setCreatedAt(null);
            softly.assertThat(actual)
                .as("Verifica che le informazioni dell'attributo risultante siano"
                    + " coerenti con quelle specificate in fase di creazione")
                .isEqualTo(expected);
        });
    }

    @When("l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo")
    public void recuperaVerifiers() {
        delayService.delay();
        String tenant = sharedStepsContext.getTenantType();
        UUID organizationId = sharedStepsContext.getIdentityService().getOrganizationId(tenant);
        UUID verifiedAttributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();

        recuperaVerifiers(organizationId, verifiedAttributeId);
    }

    @When("l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo all'ente {string}")
    public void recuperaVerifiers(String tenant) {
        delayService.delay();
        UUID organizationId = sharedStepsContext.getIdentityService().getOrganizationId(tenant);
        UUID verifiedAttributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();

        recuperaVerifiers(organizationId, verifiedAttributeId);
    }

    @When("l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo indicando un ente inesistente")
    public void recuperaVerifiersEnteInesistente() {
        UUID organizationId = UUID.randomUUID();
        UUID verifiedAttributeId = UUID.randomUUID();
        recuperaVerifiers(organizationId, verifiedAttributeId);
    }

    @When("l'utente tenta di recuperare la lista di enti che hanno verificato l'attributo indicando un attributo inesistente")
    public void recuperaVerifiersAttributoInesistente() {
        String tenant = sharedStepsContext.getTenantType();
        UUID organizationId = sharedStepsContext.getIdentityService().getOrganizationId(tenant);
        UUID verifiedAttributeId = UUID.randomUUID();
        recuperaVerifiers(organizationId, verifiedAttributeId);
    }

    private void recuperaVerifiers(UUID organizationId, UUID verifiedAttributeId) {
        httpExecutor.performCall(() -> tenantClient.getVerifiers(organizationId, verifiedAttributeId));
    }


    @Then("la lista degli enti che hanno verificato l'attributo è")
    public void verificaVerifiers(List<String> tenants) {
        IdentityService identityService = sharedStepsContext.getIdentityService();
        List<UUID> expectedVerifiers = tenants.stream().map(identityService::getOrganizationId).toList();
        List<UUID> actualVerifiers = ((TenantVerifiedAttributeVerifiers) httpExecutor.getResponse()).getResults().stream()
            .map(TenantVerifiedAttributeVerifier::getId)
            .toList();

        assertThat(actualVerifiers)
            .as("Verifica che gli enti verificatori attesi siano quelli effettivamente restituiti")
            .containsExactlyInAnyOrderElementsOf(expectedVerifiers);
    }

    @When("l'utente tenta di recuperare la lista di enti che hanno revocato l'attributo")
    public void recuperaRevokers() {
        delayService.delay();
        String tenant = sharedStepsContext.getTenantType();
        UUID organizationId = sharedStepsContext.getIdentityService().getOrganizationId(tenant);
        UUID verifiedAttributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();

        recuperaRevokers(organizationId, verifiedAttributeId);
    }

    @When("l'utente tenta di recuperare la lista di enti che hanno revocato l'attributo all'ente {string}")
    public void recuperaRevokers(String tenant) {
        delayService.delay();
        UUID organizationId = sharedStepsContext.getIdentityService().getOrganizationId(tenant);
        UUID verifiedAttributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();

        recuperaRevokers(organizationId, verifiedAttributeId);
    }

    @When("l'utente tenta di recuperare la lista di enti che hanno revocato l'attributo indicando un ente inesistente")
    public void recuperaRevokersEnteInesistente() {
        UUID organizationId = UUID.randomUUID();
        UUID verifiedAttributeId = UUID.randomUUID();
        recuperaRevokers(organizationId, verifiedAttributeId);
    }

    @When("l'utente tenta di recuperare la lista di enti che hanno revocato l'attributo indicando un attributo inesistente")
    public void recuperaRevokersAttributoInesistente() {
        String tenant = sharedStepsContext.getTenantType();
        UUID organizationId = sharedStepsContext.getIdentityService().getOrganizationId(tenant);
        UUID verifiedAttributeId = UUID.randomUUID();
        recuperaRevokers(organizationId, verifiedAttributeId);
    }

    private void recuperaRevokers(UUID organizationId, UUID verifiedAttributeId) {
        httpExecutor.performCall(() -> tenantClient.getRevokers(organizationId, verifiedAttributeId));
    }


    @Then("la lista degli enti che hanno revocato l'attributo è")
    public void verificaRevokers(List<String> tenants) {
        IdentityService identityService = sharedStepsContext.getIdentityService();
        List<UUID> expectedRevokers = tenants.stream().map(identityService::getOrganizationId).toList();
        List<UUID> actualRevokers = ((TenantVerifiedAttributeRevokers) httpExecutor.getResponse()).getResults().stream()
            .map(TenantVerifiedAttributeRevoker::getId)
            .toList();

        assertThat(actualRevokers)
            .as("Verifica che gli enti revocatori attesi siano quelli effettivamente restituiti")
            .containsExactlyInAnyOrderElementsOf(expectedRevokers);
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
