package it.pagopa.pn.interop.cucumber.steps.purplose_template;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeTemplateWithCompactCreator;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class PurposeTemplateSteps {

    private final IPurposeTemplateClient purposeTemplateClient;

    private CreatedResource createdTemplate;

    private HttpStatusCodeException error;

    private final SharedStepsContext sharedStepsContext;

    private final ClientTokenConfigurator clientTokenConfigurator;

    private final IdentityService identityService;

    private final IHttpExecutor httpCallExecutor;

    public PurposeTemplateSteps(IPurposeTemplateClient purposeTemplateClient,
                                ClientTokenConfigurator clientTokenConfigurator,
                                SharedStepsContext sharedStepsContext) {
        this.purposeTemplateClient = purposeTemplateClient;
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("viene creato un nuovo purpose template")
    public void createPurposeTemplate() {
        PurposeTemplateSeed request = new PurposeTemplateSeed();
        request.setPurposeTitle("TEST CREATE PURPOSE TEMPLATE");
        request.setPurposeDescription("test");

        boolean success = false;
        CreatedResource response = null;
        try {
            response = purposeTemplateClient.createPurposeTemplate(request);
            success = true;
            this.createdTemplate = response;
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
        if (success) {
            assertThat(createdTemplate).as("").isNotNull();
        }
    }

    @When("si effettua la get del purpose template {string}")
    public void getPurposeTemplate(String ptType) {
        UUID ptId;
        switch (ptType.toUpperCase()) {
            case "CREATO" -> ptId = createdTemplate.getId();
            case "INESISTENTE" -> ptId = UUID.randomUUID();
            default -> throw new IllegalArgumentException("Invalid purposeTemplateId type");
        }
        boolean success = false;
        try {
            PurposeTemplateWithCompactCreator purposeTemplateWithCompactCreator = purposeTemplateClient.getPurposeTemplate(ptId);
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
        if (success) {
            assertThat(createdTemplate).as("").isNotNull();
        }
    }
}
