package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IM2MVerifiedAttributeClient;
import it.pagopa.interop.attribute.service.IM2MVerifiedAttributeClient.VerifiedAttributeSeed;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VerifiedAttributeSteps extends AbstractCommonSteps<VerifiedAttribute, UUID> {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MVerifiedAttributeClient client;
    private final IHttpExecutor httpExecutor;

    public VerifiedAttributeSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("verifiedAttribute", clientTokenConfigurator.getM2mVerifiedAttributeClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mVerifiedAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @And("viene effettuata la creazione dell'attributo verificato")
    public void creazioneAttributoVerificato(VerifiedAttributeSeed payloadAttrCert) {
        VerifiedAttribute result = client.create(payloadAttrCert);
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
        httpExecutor.performCall(() -> client.get(attributeId));
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
