package it.pagopa.pn.interop.cucumber.steps.catalog;

import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceDescriptor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Method;
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

    @When("^l'utente legge da catalogo i?l'? ?(ultimo|vecchio) descrittore e-service (senza|con) riferimenti al(|la precedente versione del) template$")
    public void readEServiceDescriptorFromCatalogueAndCheckTemplateInfo(String descriptorQualifier, String templateRefWith, String prevTemplateVersion) {
        UUID expectedTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();
        if (descriptorQualifier.equals("vecchio")) {
            requireOldDescriptorRead();
            expectedTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getOldVersionId();
        } else {
            requireLastDescriptorRead();
            if (!prevTemplateVersion.isEmpty()) {
                expectedTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getOldVersionId();
            }
        }
        CatalogEServiceDescriptor obj = ((CatalogEServiceDescriptor)httpCallExecutor.getResponse());
        Assertions.assertNotNull(obj, "Response of e-service descriptor from catalog is null");

        boolean foundTemplateRef = false;

        try {
            Object templateRefObj;
            Method method = obj.getClass().getMethod("getTemplateRef");
            templateRefObj = method.invoke(obj);
            if (templateRefObj == null) {
                throw new NoSuchFieldException("templateRef is null");
            }

            method = templateRefObj.getClass().getMethod("getTemplateId");
            String actualTemplateId = method.invoke(templateRefObj).toString();
            Assertions.assertEquals(
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId().toString(),
                    actualTemplateId,
                    "templateId"
            );

            method = templateRefObj.getClass().getMethod("getTemplateVersionId");
            UUID actualTemplateVersionId = (UUID)method.invoke(templateRefObj);

            Assertions.assertEquals(expectedTemplateVersionId, actualTemplateVersionId, "templateVersionId");

            method = templateRefObj.getClass().getMethod("getTemplateName");
            String actualTemplateName = (String)method.invoke(templateRefObj);
            Assertions.assertEquals(
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getName(),
                    actualTemplateName,
                    "templateName"
            );

            foundTemplateRef = true;

        } catch (NoSuchMethodException e) {
        } catch (ReflectiveOperationException e) {
        }

        if (templateRefWith.equals("con")) {
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
    }
}
