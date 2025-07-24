package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IM2MDeclaredAttributeClient;
import it.pagopa.interop.attribute.service.IM2MDeclaredAttributeClient.DeclaredAttributeSeed;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeclaredAttributeSteps extends AbstractCommonSteps<DeclaredAttribute, UUID> {

    private final SharedStepsContext sharedStepsContext;
    private final IM2MDeclaredAttributeClient client;
    private final IHttpExecutor httpExecutor;

    public DeclaredAttributeSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        super("declaredAttribute", clientTokenConfigurator.getM2mDeclaredAttributeClient(), sharedStepsContext);
        this.client = clientTokenConfigurator.getM2mDeclaredAttributeClient();
        this.sharedStepsContext = sharedStepsContext;
        this.client.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
        this.httpExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @And("viene effettuata la creazione dell'attributo dichiarato")
    public void creazioneAttributoDichiarato(DeclaredAttributeSeed payloadAttrCert) {
        DeclaredAttribute result = client.create(payloadAttrCert);
        var attributeContext = sharedStepsContext.getAttributeCommonContext();

        List<DeclaredAttribute> published = new ArrayList<>();
        published.add(result);

        attributeContext.setDeclaredPublished(published);
    }

    // NOTA 22/07/2025: questo step funziona qualora la creazione dell'attributo dichiarato
    // sia stata fatta attraverso API BFF
    @When("l'utente tenta di recuperare l'attributo dichiarato creato")
    public void recuperaAttributoDichiarato() {
        UUID attributeId = this.sharedStepsContext.getAttributeCommonContext().getAttributeId();
        httpExecutor.performCall(() -> client.get(attributeId));
    }

    @Then("l'attributo dichiarato è stato creato correttamente")
    public void verificaAttributoDichiarato() {
        List<DeclaredAttribute> declaredPublished = sharedStepsContext.getAttributeCommonContext()
            .getDeclaredPublished();

        DeclaredAttribute actual = (DeclaredAttribute) httpExecutor.getResponse();
        DeclaredAttribute expected = declaredPublished.get(declaredPublished.size() - 1);
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
