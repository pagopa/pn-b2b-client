package it.pagopa.pn.interop.cucumber.steps.catalog;

import static org.assertj.core.api.Assertions.fail;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CatalogEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Method;
import java.util.Map;
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

    @When("^l'utente legge da catalogo i?l'? ?(ultimo|vecchio) descrittore e-service (senza|con) riferimenti al template$")
    public void readEServiceDescriptorFromCatalogueAndCheckTemplateInfo(String descriptorQualifier, String templateRefWith) {
        readEServiceDescriptorFromCatalogueAndCheckTemplateInfo(descriptorQualifier, templateRefWith, Map.of());
    }

    @When("^l'utente legge da catalogo i?l'? ?(ultimo|vecchio) descrittore e-service (senza|con) riferimenti al template e dati:$")
    public void readEServiceDescriptorFromCatalogueAndCheckTemplateInfo(String descriptorQualifier, String templateRefWith, Map<String, Boolean> expectedData) {
        if (descriptorQualifier.equals("vecchio")) {
            requireOldDescriptorRead();
        } else {
            requireLastDescriptorRead();
        }
        CatalogEServiceDescriptor obj = ((CatalogEServiceDescriptor)httpCallExecutor.getResponse());
        Assertions.assertNotNull(obj, "Response of e-service descriptor from catalog is null");

        boolean foundTemplateRef = false;
        boolean expectedValueOfNewTemplateVersionAvailable =
                expectedData.getOrDefault("isNewTemplateVersionAvailable", false);

        try {
            Object templateRefObj;
            Method method = obj.getClass().getMethod("getTemplateRef");
            templateRefObj = method.invoke(obj);

            method = templateRefObj.getClass().getMethod("getTemplateId");
            String actualTemplateId = (String)method.invoke(templateRefObj);
            Assertions.assertEquals(
                    actualTemplateId,
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId().toString()
            );

            method = templateRefObj.getClass().getMethod("getTemplateVersionId");
            String actualTemplateVersionId = (String)method.invoke(templateRefObj);
            Assertions.assertEquals(
                    actualTemplateVersionId,
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId().toString()
            );

            method = templateRefObj.getClass().getMethod("getTemplateName");
            String actualTemplateName = (String)method.invoke(templateRefObj);
            Assertions.assertEquals(
                    actualTemplateName,
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getName()
            );

            method = templateRefObj.getClass().getMethod("getTemplateInterface");
            Object actualTemplateInterface = method.invoke(templateRefObj);
            Assertions.assertNotNull(actualTemplateInterface);

            EServiceDoc expectedInterface =
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getTemplateInterface();

            method = actualTemplateInterface.getClass().getMethod("getPrettyName");
            String prettyName = (String)method.invoke(actualTemplateInterface);
            Assertions.assertEquals(expectedInterface.getPrettyName(), prettyName);

            method = actualTemplateInterface.getClass().getMethod("getChecksum");
            String checksum = (String)method.invoke(actualTemplateInterface);
            Assertions.assertEquals(expectedInterface.getChecksum(), checksum);

            method = templateRefObj.getClass().getMethod("getInterfaceMetadata");
            Object actualInterfaceMetadata = method.invoke(templateRefObj);
            Assertions.assertNotNull(actualInterfaceMetadata);

            method = actualInterfaceMetadata.getClass().getMethod("getContactName");
            String contactName = (String)method.invoke(actualInterfaceMetadata);
            Assertions.assertEquals(
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getContactName(),
                    contactName
            );
            method = actualInterfaceMetadata.getClass().getMethod("getContactEmail");
            String contactEmail = (String)method.invoke(actualInterfaceMetadata);
            Assertions.assertEquals(
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getContactEmail(),
                    contactEmail
            );

            method = templateRefObj.getClass().getMethod("isNewTemplateVersionAvailable");
            Object isNewTemplateVersionAvailable = method.invoke(templateRefObj);
            Assertions.assertEquals(
                    expectedValueOfNewTemplateVersionAvailable,
                    isNewTemplateVersionAvailable
            );

            Integer expectedDailyCallsPerConsumer =
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionUpdateSeed()
                            .getDailyCallsPerConsumer();

            if (expectedDailyCallsPerConsumer != null) {
                method = templateRefObj.getClass().getMethod("getTemplateDailyCallsPerConsumer");
                Integer templateDailyCallsPerConsumer = (Integer)method.invoke(templateRefObj);
                Assertions.assertEquals(expectedDailyCallsPerConsumer, templateDailyCallsPerConsumer);
            }

            Integer expectedDailyCallsTotal =
                    sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateVersionUpdateSeed()
                            .getDailyCallsTotal();

            if (expectedDailyCallsTotal != null) {
                method = templateRefObj.getClass().getMethod("getTemplateDailyCallsTotal");
                Integer templateDailyCallsTotal = (Integer)method.invoke(templateRefObj);
                Assertions.assertEquals(expectedDailyCallsTotal, templateDailyCallsTotal);
            }

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
