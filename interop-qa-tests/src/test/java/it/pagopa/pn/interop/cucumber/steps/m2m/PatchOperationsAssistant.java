package it.pagopa.pn.interop.cucumber.steps.m2m;

import it.pagopa.interop.authorization.domain.Auth;
import it.pagopa.interop.authorization.domain.dpop.DpopHeaderPolicy;
import it.pagopa.interop.authorization.service.DPoPTokenService;
import it.pagopa.interop.authorization.service.utils.JWTUtils;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.JsonParseException;
import it.pagopa.interop.config.springconfig.springconfig.ApiProfile;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class PatchOperationsAssistant<PATCH_REQUEST, RESOURCE, RESOURCE_ID> {
    record TokenAuthInfo(String tenant, UUID cliendId) {};

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

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di ..." */
    public void patchResource() {
        patchResource(this.buildDefaultPatchRequest());
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di ..." */
    public void patchResource(String patchToken) {
        patchResource(this.buildDefaultPatchRequest(), patchToken);
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di ..." */
    public void patchResource(PATCH_REQUEST patchRequest) {
        String actualToken = tokenConfigurator.getLastToken();
        this.patchResource(patchRequest, actualToken, actualToken);
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica di ... con token non valido" */
    public void patchResourceWithInvalidToken() {
        this.patchResource(this.buildDefaultPatchRequest(), M2MAuthSteps.INVALID_AUTH_TOKEN);
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica di ... con token non valido" */
    public void patchResourceWithInvalidToken(PATCH_REQUEST patchRequest) {
        this.patchResource(patchRequest, M2MAuthSteps.INVALID_AUTH_TOKEN);
    }

    /* di solito associato a step del tipo
     * "l'utente tenta di effettuare la modifica parziale di ..." */
    public void patchResource(PATCH_REQUEST patchRequest, String patchToken) {
        String actualToken = tokenConfigurator.getLastToken();
        this.patchResource(patchRequest, actualToken, patchToken);
    }

    /**
     * Perform a PATCH operation and set the context for future checks.
     * To do this, a GET operation is first performed on the current version of the
     * resource.
     * @param patchRequest parameters needed to perform the PATCH request
     * @param getToken auth token used in GET operation
     * @param patchToken auth token used in PATCH operation
     */
    public void patchResource(PATCH_REQUEST patchRequest, String getToken, String patchToken) {
        String previousAuthToken = tokenConfigurator.getLastToken();

        RESOURCE_ID resourceId = this.getResourceId();

        tokenConfigurator.setBearerToken(getToken);
        eventuallySetAuth(getToken);
        RESOURCE originalResource = this.getResource(resourceId);
        resourceContext.setOriginalResource(originalResource);

        RESOURCE expectedPatchedResource = this.resourceMapper.copyResource(originalResource);
        this.resourceMapper.copyPatchRequestToResource(patchRequest, expectedPatchedResource);
        resourceContext.setExpectedResource(expectedPatchedResource);

        tokenConfigurator.setBearerToken(patchToken);
        eventuallySetAuth(patchToken);
        httpExecutor.performCall(() -> this.patchResource(resourceId, patchRequest));
        this.resourceContext.setReturnedResource((RESOURCE) httpExecutor.getResponse());

        tokenConfigurator.setBearerToken(previousAuthToken);
        eventuallySetAuth(previousAuthToken);
    }

    private void eventuallySetAuth(String token) {
        try {
            TokenAuthInfo authInfo = extractTokenAuthInfo(token);
            if (ApiProfile.ApiM2MVersion.V3.equals(apiProfile.getApiM2MVersion()) && authInfo != null) {

                DPoPTokenService.PreparedClient preparedClient = sharedStepsContext.getIdentityService().getPreparedClient(authInfo.cliendId());
                sharedStepsContext.getClientCommonContext().addClient(preparedClient);

                Auth auth = Auth.of(DpopHeaderPolicy.of(DpopHeaderPolicy.Mode.NORMAL), authInfo.cliendId().toString(), authInfo.tenant(), "ADMIN", preparedClient.keyPair().getKeyPair());
                tokenConfigurator.setAuth(auth);
                sharedStepsContext.setAuth(auth);
            }
        } catch (JsonParseException e) {
            Auth auth = sharedStepsContext.getAuth();
            auth.getDpopHeaderPolicy().setMode(DpopHeaderPolicy.Mode.INVALID_AUTH);
            tokenConfigurator.setAuth(auth);
        }
    }

    @Nullable
    private TokenAuthInfo extractTokenAuthInfo(String token) {
        try {
            Map<String, Object> jwtPayload = JWTUtils.decodeJwtPayload(token);
            Object oClientId = getValue(jwtPayload, "client_id");
            if(oClientId == null) {
                return null;
            }

            UUID clientId = UUID.fromString(oClientId.toString());

            Object oOrganizationId = getValue(jwtPayload, "organizationId");
            UUID organizationId = UUID.fromString(oOrganizationId.toString());
            String tenant = sharedStepsContext.getIdentityService().getTenant(organizationId);

            return new TokenAuthInfo(tenant, clientId);
        } catch (JsonParseException e) {
            // Al momento siamo in questo caso SOLO se passiamo un token deliberatamente non valido
            throw e;
        }
    }

    @Nullable
    private static Object getValue(Map<String, Object> jwtPayload, String fieldKey) {
        Object oClientId = jwtPayload.get(fieldKey);
        //requireNonNull(oClientId, "Not found expected field %s in token payload".formatted(fieldKey));
        return oClientId;
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
}