package it.pagopa.pn.interop.cucumber.steps.m2m;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.enums.M2MRole;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.conf.api_profile.ApiProfile;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class PatchOperationsAssistant<PATCH_REQUEST, RESOURCE, RESOURCE_ID> {

    private final ResourceMapper<PATCH_REQUEST, RESOURCE> resourceMapper;
    private final IHttpExecutor httpExecutor;
    private final DelayService delayService;
    private final ResourceContext<RESOURCE> resourceContext;
    private final ClientTokenConfigurator tokenConfigurator;
    private final String resourceSimpleName;

    /* DEV. NOTE 25 02 2026: in controtendenza rispetto alla best practice, si opta per la tecnica field injection
     * invece che di constructor injection, per evitare di dover aggiornare tutte le classi concrete, nell'ottica
     * che questo sistema sarà migliorato con refactor futuri */
    @Autowired
    private SharedStepsContext sharedStepsContext;

    @Autowired
    private ApiProfile apiProfile;

    @Autowired
    private M2MAuthSteps m2mAuthSteps;

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di ..." */
    public void patchResource() {
        patchResource(this.buildDefaultPatchRequest());
    }


    public void patchResource(PATCH_REQUEST patchRequest) {
        this.patchResource(patchRequest, null, null);
    }

    public void patchResource(String patchTenant, M2MRole m2mRole) {
        this.patchResource(this.buildDefaultPatchRequest(), patchTenant, m2mRole);
    }

    public void patchResource(PATCH_REQUEST patchRequest, String patchTenant, M2MRole role) {
        Runnable authProcedure = () -> {
            if (patchTenant != null && role != null) m2mAuthSteps.authenticateM2MUser("admin", patchTenant, role);
        };
        this.patchResource(patchRequest, authProcedure);
    }

    public void patchResource(PATCH_REQUEST patchRequest, Runnable patchAuth) {
        String previousBearerToken = tokenConfigurator.getLastToken();
        Auth previuousAuth = sharedStepsContext.getAuth();

        RESOURCE_ID resourceId = this.getResourceId();
        RESOURCE originalResource = this.getResource(resourceId);
        resourceContext.setOriginalResource(originalResource);

        RESOURCE expectedPatchedResource = this.resourceMapper.copyResource(originalResource);
        this.resourceMapper.copyPatchRequestToResource(patchRequest, expectedPatchedResource);
        resourceContext.setExpectedResource(expectedPatchedResource);

        // Auth m2m
        patchAuth.run();
        httpExecutor.performCall(() -> this.patchResource(resourceId, patchRequest));
        this.resourceContext.setReturnedResource((RESOURCE) httpExecutor.getResponse());

        tokenConfigurator.setBearerToken(previousBearerToken);
        tokenConfigurator.setAuth(previuousAuth);
        sharedStepsContext.setAuth(previuousAuth);
    }

    public void patchResourceWithInvalidToken(PATCH_REQUEST patchRequest) {
        this.patchResource(patchRequest, () -> m2mAuthSteps.setExpiredM2MAuth());
    }

    public void patchResourceWithInvalidToken() {
        this.patchResource(this.buildDefaultPatchRequest(), () -> m2mAuthSteps.setExpiredM2MAuth());
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di un ... inesistente" */
    public void patchNonExistentResource() {
        RESOURCE_ID resourceId = this.randomResourceId();
        PATCH_REQUEST patchRequest = buildDefaultPatchRequest();
        httpExecutor.performCall(() -> this.patchResource(resourceId, patchRequest));
    }

    public void patchNonSpecifiedResource() {
        RESOURCE_ID resourceId = this.emptyResourceId();
        PATCH_REQUEST patchRequest = buildDefaultPatchRequest();
        httpExecutor.performCall(() -> this.patchResource(resourceId, patchRequest));
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di ... senza apportare cambiamenti" */
    public void patchResourceWithSameInfo() {
        RESOURCE_ID resourceId = this.getResourceId();

        RESOURCE actualResource = this.getResource(resourceId);
        resourceContext.setOriginalResource(actualResource);

        PATCH_REQUEST patchRequest = resourceMapper.mapResourceToPatchRequest(actualResource);
        httpExecutor.performCall(() -> this.patchResource(resourceId, patchRequest));
        this.resourceContext.setReturnedResource((RESOURCE) httpExecutor.getResponse());
    }

    /* di solito associato a step del tipo
     * "il ... restituito è coerente con le modifiche effettuate" */
    public void checkPatchOperationResult() {
        delayService.delay();
        assertImpl(
                resourceContext.getReturnedResource(),
                resourceContext.getExpectedResource(),
                "Verifica che il risultato restituito dall'API PATCH su '%s' sia coerente con le modifiche effettuate".formatted(this.resourceSimpleName));
    }

    /* di solito associato a step del tipo
     * "... è stato parzialmente modificato correttamente" */
    public void checkPatchedResource() {
        delayService.delay();
        RESOURCE_ID resourceId = this.getResourceId();
        RESOURCE actualPatchedResource = this.getResource(resourceId);
        assertImpl(
                actualPatchedResource,
                resourceContext.getExpectedResource(),
                "Verifica che le modifiche apportate a '%s' con l'API PATCH siano state apportate correttamente".formatted(this.resourceSimpleName));
    }

    protected void assertImpl(RESOURCE actual, RESOURCE expected, String assertDescription) {
        assertThat(actual).as(assertDescription).isEqualTo(expected);
    }

    /* di solito associato a step del tipo
     * "... non ha subito modifiche" */
    public void checkUnpatchedResource() {
        delayService.delay();
        RESOURCE_ID resourceId = this.getResourceId();
        RESOURCE actualPatchedResource = this.getResource(resourceId);
        assertThat(actualPatchedResource)
                .as("Verifica che non siano state apportate modifiche a '%s'", this.resourceSimpleName)
                .isEqualTo(resourceContext.getOriginalResource());
    }

    protected abstract RESOURCE_ID getResourceId();

    protected abstract RESOURCE getResource(RESOURCE_ID resourceId);

    protected abstract PATCH_REQUEST buildDefaultPatchRequest();

    protected abstract RESOURCE patchResource(RESOURCE_ID resourceId, PATCH_REQUEST patchRequest);

    protected abstract RESOURCE_ID randomResourceId();

    protected RESOURCE_ID emptyResourceId() {
        return null;
    }
}