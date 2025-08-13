package it.pagopa.pn.interop.cucumber.steps.m2m;

import static org.assertj.core.api.Assertions.assertThat;

import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PatchOperationsAssistant<PATCH_REQUEST, RESOURCE, RESOURCE_ID> {
    private final ResourceMapper<PATCH_REQUEST, RESOURCE> resourceMapper;
    private final IHttpExecutor httpExecutor;
    private final DelayService delayService;
    private final String resourceSimpleName;

    private RESOURCE originalResource;
    private RESOURCE expectedPatchedResource;

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di ..." */
    public void patchResource() {
        RESOURCE_ID resourceId = this.getResourceId();
        PATCH_REQUEST patchRequest = this.buildPatchRequest();
        this.originalResource = this.getResource(resourceId);
        this.expectedPatchedResource = this.resourceMapper.copyResource(originalResource);
        this.resourceMapper.copyPatchRequestToResource(patchRequest, this.expectedPatchedResource);
        httpExecutor.performCall(() -> this.patchResource(resourceId, patchRequest));
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di un ... inesistente" */
    public void patchNonExistentResource() {
        RESOURCE_ID resourceId = this.randomResourceId();
        PATCH_REQUEST patchRequest = buildPatchRequest();
        httpExecutor.performCall(() -> this.patchResource(resourceId, patchRequest));
    }

    /* di solito associato a step del tipo
     * "... è stato parzialmente modificato correttamente" */
    public void checkPatchedResource() {
        delayService.delay();
        RESOURCE_ID resourceId = this.getResourceId();
        RESOURCE actualPatchedResource = this.getResource(resourceId);
        assertThat(actualPatchedResource)
            .as("Verifica che le modifiche apportate a '%s' con l'API PATCH siano state apportate correttamente", this.resourceSimpleName)
            .isEqualTo(this.expectedPatchedResource);
    }

    /* di solito associato a step del tipo
     * "... non ha subito modifiche" */
    public void checkUnpatchedResource() {
        delayService.delay();
        RESOURCE_ID resourceId = this.getResourceId();
        RESOURCE actualPatchedResource = this.getResource(resourceId);
        assertThat(actualPatchedResource)
            .as("Verifica che non siano state apportate modifiche a '%s'", this.resourceSimpleName)
            .isEqualTo(this.originalResource);
    }

    protected abstract RESOURCE_ID getResourceId();

    protected abstract RESOURCE getResource(RESOURCE_ID resourceId);

    protected abstract PATCH_REQUEST buildPatchRequest();

    protected abstract void patchResource(RESOURCE_ID resourceId, PATCH_REQUEST patchRequest);

    protected abstract RESOURCE_ID randomResourceId();
}