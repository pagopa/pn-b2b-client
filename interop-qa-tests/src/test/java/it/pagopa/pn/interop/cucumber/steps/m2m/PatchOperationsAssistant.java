package it.pagopa.pn.interop.cucumber.steps.m2m;

import static org.assertj.core.api.Assertions.assertThat;

import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class PatchOperationsAssistant<PATCH_REQUEST, RESOURCE, RESOURCE_ID> {
    private final ResourceMapper<PATCH_REQUEST, RESOURCE> resourceMapper;
    private final IHttpExecutor httpExecutor;
    private final DelayService delayService;
    private final ResourceContext<RESOURCE> resourceContext;
    private final String resourceSimpleName;

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di ..." */
    public void patchResource() {
        patchResource(this.buildDefaultPatchRequest());
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di ..." */
    public void patchResource(PATCH_REQUEST patchRequest) {
        RESOURCE_ID resourceId = this.getResourceId();

        RESOURCE originalResource = this.getResource(resourceId);
        resourceContext.setOriginalResource(originalResource);

        RESOURCE expectedPatchedResource = this.resourceMapper.copyResource(originalResource);
        this.resourceMapper.copyPatchRequestToResource(patchRequest, expectedPatchedResource);
        resourceContext.setExpectedResource(expectedPatchedResource);

        httpExecutor.performCall(() -> this.patchResource(resourceId, patchRequest));
        this.resourceContext.setReturnedResource((RESOURCE) httpExecutor.getResponse());
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di un ... inesistente" */
    public void patchNonExistentResource() {
        RESOURCE_ID resourceId = this.randomResourceId();
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
        assertThat(resourceContext.getReturnedResource())
            .as("Verifica che il risultato restituito dall'API PATCH su '%s' sia coerente con le modifiche effettuate", this.resourceSimpleName)
            .isEqualTo(resourceContext.getExpectedResource());
    }

    /* di solito associato a step del tipo
     * "... è stato parzialmente modificato correttamente" */
    public void checkPatchedResource() {
        delayService.delay();
        RESOURCE_ID resourceId = this.getResourceId();
        RESOURCE actualPatchedResource = this.getResource(resourceId);
        assertThat(actualPatchedResource)
            .as("Verifica che le modifiche apportate a '%s' con l'API PATCH siano state apportate correttamente", this.resourceSimpleName)
            .isEqualTo(resourceContext.getExpectedResource());
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
}