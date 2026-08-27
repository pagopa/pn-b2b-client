package it.pagopa.pn.interop.cucumber.steps.catalog;

import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

@Slf4j
public class DescriptorReadConsumerSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public DescriptorReadConsumerSteps(ClientTokenConfigurator clientTokenConfigurator,
                                       SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("l'utente fruitore richiede la lettura di quel descrittore")
    public void requireLastDescriptorRead() {
        requireDescriptorRead(sharedStepsContext.getEServicesCommonContext().getDescriptorId());
    }

    @When("l'utente fruitore richiede la lettura del vecchio descrittore")
    public void requireOldDescriptorRead() {
        requireDescriptorRead(sharedStepsContext.getEServicesCommonContext().getOldDescriptorId());
    }

    public void requireDescriptorRead(UUID descriptorId) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().getCatalogEServiceDescriptor(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        descriptorId
                )
        );
    }

    @When("^l'utente fruitore richiede la lettura dell?'? ?(ultimo|vecchio) descrittore e(| non) trova riferimenti al template$")
    public void requireDescriptorReadAndCheckInfoTemplate(String descriptorQualifier, String templateRefNot) {
        if (descriptorQualifier.equals("vecchio")) {
            requireOldDescriptorRead();
        } else {
            requireLastDescriptorRead();
        }
        CatalogEServiceDescriptor obj = ((CatalogEServiceDescriptor)httpCallExecutor.getResponse());
        Assertions.assertNotNull(obj, "Response of e-service descriptor from catalog is null");
        boolean foundTemplateRef = false;
        try {
            obj.getClass().getMethod("getTemplateRef");
            foundTemplateRef = true;
        } catch (NoSuchMethodException e) {}

        if (templateRefNot.isEmpty()) {
            if (foundTemplateRef) {
                log.info("Found template reference as expected");
            } else {
                fail("Not found template reference");
            }
        } else {
            if (foundTemplateRef) {
                fail("Found template reference");
            } else {
                log.info("Not found template reference as expected");
            }
        }
        // TODO rimuovere questa riga di log di controllo temporaneo dello stato
        log.info("Stato descrittore: " + obj.getState().toString());
    }
}
