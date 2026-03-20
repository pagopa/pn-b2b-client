package it.pagopa.pn.interop.cucumber.steps.m2m.purpose.assistant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Purpose;
import it.pagopa.interop.purpose.service.IM2MPurposeClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.PurposeCommonContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.PatchOperationsAssistant;
import it.pagopa.pn.interop.cucumber.steps.m2m.ResourceMapper;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class PurposeGenericPatchOperationsAssistant<PATCH_REQUEST> extends PatchOperationsAssistant<PATCH_REQUEST, Purpose, UUID> {
    protected final PurposeCommonContext context;
    protected final IM2MPurposeClient client;

    public PurposeGenericPatchOperationsAssistant(
        ResourceMapper<PATCH_REQUEST, Purpose> resourceMapper,
        SharedStepsContext sharedStepsContext,
        IM2MPurposeClient client,
        PurposePatchContext patchContext,
        ClientTokenConfigurator tokenConfigurator
    ) {
        super(
            resourceMapper,
            sharedStepsContext.getHttpCallExecutor(),
            sharedStepsContext.getDelayService(),
            patchContext,
            tokenConfigurator,
            "purpose");
        this.context = sharedStepsContext.getPurposeCommonContext();
        this.client = client;
    }

    @Override
    protected UUID getResourceId() {
        return this.context.getPurposeIdAsUUID();
    }

    @Override
    protected Purpose getResource(UUID uuid) {
        return client.getPurpose(uuid);
    }

    @Override
    protected UUID randomResourceId() {
        return UUID.randomUUID();
    }

    @Override
    public void patchResource(PATCH_REQUEST patchRequest, String patchTenant, M2MRole role) {
        this.context.setUpdateTime(OffsetDateTime.now());
        super.patchResource(patchRequest, patchTenant, role);
    }

    @Override
    protected void assertImpl(Purpose actual, Purpose expected, String assertDescription) {
        assertSoftly(softly -> {
            softly.assertThat(actual)
                .as(assertDescription)
                .usingRecursiveComparison()
                .ignoringFields("updatedAt", "currentVersion.updatedAt", "currentVersion.dailyCalls")
                .isEqualTo(expected);

            softly.assertThat(actual.getCurrentVersion().getDailyCalls())
                .as("Verifica che l'attributo 'dailyCalls' sia coerente con le modifiche effettuate")
                .isNotNull()
                .isEqualTo(expected.getCurrentVersion().getDailyCalls());

            softly.assertThat(OffsetDateTime.parse(actual.getUpdatedAt()))
                .as("Verifica timestamp di modifica della finalità restituita")
                .isNotNull()
                .isCloseTo(this.context.getUpdateTime(), within(15, SECONDS));
        });
    }
}