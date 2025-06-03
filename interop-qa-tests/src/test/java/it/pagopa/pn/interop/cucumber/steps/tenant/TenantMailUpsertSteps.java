package it.pagopa.pn.interop.cucumber.steps.tenant;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.generated.openapi.clients.bff.model.Mail;
import it.pagopa.interop.generated.openapi.clients.bff.model.MailKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.MailSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class TenantMailUpsertSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final BFFDataPreparationService dataPreparationService;
    private final IdentityService identityService;
    private final HttpCallExecutor httpCallExecutor;
    private String email;

    public TenantMailUpsertSteps(ClientTokenConfigurator clientTokenConfigurator,
                                 SharedStepsContext sharedStepsContext,
                                 BFFDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @Given("{string} ha già inserito una mail di contatto")
    public void addContactEmail(String tenantType) {
        email = String.format("%dtest@pagopa.it", new Random().nextInt());
        UUID tenantId = identityService.getOrganizationId(tenantType);
        httpCallExecutor.performCall(
                () -> dataPreparationService.addEmailToTenant(tenantId, new MailSeed().address(email).description("test description"))
        );
    }

    @When("l'utente richiede una operazione di aggiunta di una mail di contatto con description")
    public void addContactEmailWithDescription() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().addTenantMail(tenantId,
                        new MailSeed().kind(MailKind.CONTACT_EMAIL).address(String.format("%s@pagopa.it", sharedStepsContext.getTestSeed())).description("test description")
                )
        );
    }

    @When("l'utente richiede una operazione di aggiunta di una mail di contatto senza description")
    public void addContactEmailWithoutDescription() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        email = String.format("%d%d@pagopa.it", sharedStepsContext.getTestSeed(), new Random().nextInt());
        UUID tenantId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        httpCallExecutor.performCall(
                () -> dataPreparationService.addEmailToTenant(tenantId, new MailSeed().kind(MailKind.CONTACT_EMAIL).address(email))
        );
    }

    @When("l'utente richiede una operazione di aggiornamento della mail di contatto senza description")
    public void updateContactEmailWithoutDescription() {
        addContactEmailWithoutDescription();
    }

    @When("l'utente richiede una operazione di aggiunta della stessa mail di contatto già inserita")
    public void addSameContactEmailAlreadyPresent() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        UUID tenantId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getTenantsApi().addTenantMail(tenantId, new MailSeed().kind(MailKind.CONTACT_EMAIL).address(email))
        );
    }

    @Then("si ottiene status code {int} e la mail è stata aggiornata e non aggiunta")
    public void verifyStatusCodeAndMailAdded(int statusCode) {
        UUID tenantId = identityService.getOrganizationId(sharedStepsContext.getTenantType());
        sharedStepsContext.getPollingService().makePolling(
                () -> clientTokenConfigurator.getTenantsApi().getTenant(tenantId),
                res -> Optional.ofNullable(res.getContactMail()).map(Mail::getAddress).filter(address -> address.equals(email)).isPresent(),
                "Tenant with desired email address not found!"
        );
        Assertions.assertEquals(statusCode, httpCallExecutor.getClientResponse().value());
    }

    @Then("aspetta che si aggiorni il readmodel")
    public void waitReadModelUpdate() throws InterruptedException {
        Thread.sleep(3000);
    }
}
