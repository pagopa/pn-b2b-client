package it.pagopa.pn.interop.cucumber.steps.e_service_template.shared;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import com.google.common.io.Files;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributes;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext.EServiceTemplateDocumentInfo;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

/** It contains general utility functions used across all other classes.  */
@Data
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServiceTemplateTestAssistant {
    // TODO 13/03/2025: almeno alcuni di questi attributi resteranno inutilizzati, rimuoverli
    private final DataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final IEServiceClient eServiceClient;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EServiceTemplateStepContext templateContext;
    private final EasyRandom easyRandom;
    private final DescriptorAttributesMapper descriptorAttributesMapper;

    public EServiceTemplateTestAssistant(ClientTokenConfigurator clientTokenConfigurator,
        DataPreparationService dataPreparationService,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateStepContext templateContext,
        DescriptorAttributesMapper descriptorAttributesMapper) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.templateContext = templateContext;
        this.easyRandom = new EasyRandom(templateContext.getEasyRandomParameters());
        this.descriptorAttributesMapper = descriptorAttributesMapper;
    }

    public String nextTestResourceNameSuffix() {
        int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        return String.format("%d-%d", sharedStepsContext.getTestSeed(), randomInt);
    }

    // TODO generalizzabile in "mutateVersionState"
    public void mutateLastVersionState(EServiceTemplateVersionState desiredState) {
        Runnable publisher = () -> {
            this.addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind.INTERFACE); // perché ogni template deve avere almeno un'interfaccia
            publishEServiceTemplate();
        };
        switch (desiredState) {
            case DRAFT -> { /* no-op: una versione appena creata è automaticamente in questo stato */ }
            case PUBLISHED -> publisher.run();
            case SUSPENDED -> {
                publisher.run();    // perché prima di essere sospesa deve essere pubblicata
                suspendEServiceTemplate();
            }
            default -> throw new IllegalArgumentException("Stato non supportato: " + desiredState);
        }
    }

    public void publishEServiceTemplate() {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.dataPreparationService.publishEServiceTemplate(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId());
    }

    public void suspendEServiceTemplate() {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.dataPreparationService.suspendEServiceTemplate(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId());
    }

    public void addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind kind) {
        addDocumentToEServiceTemplateVersion(kind);
        checkDocumentAddedToEServiceTemplateVersion(kind);
    }

    public void addDocumentToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind);
    }

    // TODO troppe varianti di questi metodi, standardizzarne 1 o 2 al massimo
    public void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind) {
        String prettyName = buildPrettyName(kind);
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, prettyName);
    }

    public void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind, String prettyName) {
        String userToken = sharedStepsContext.getUserToken();
        String docBody = "Hello, I'm a document of type %s".formatted(kind);
        Resource doc = new ByteArrayResource(docBody.getBytes(StandardCharsets.UTF_8));
        addDocumentToEserviceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, prettyName, userToken, doc);
    }

    public void addDocumentToEserviceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind, String prettyName, String userToken, Resource doc) {
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.addDocumentWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                eServiceTemplateVersionId,
                kind,
                prettyName,
                doc),

            /* TODO altrove non è stata usata questa variante del metodo che permette di conservare il codice di risposta originale,
             * modificare anche gli altri scenari così che si possa effettuare un check preciso dello status restituito
             */
            ResponseEntity::getStatusCode);

        ResponseEntity<CreatedResource> response = (ResponseEntity<CreatedResource>) httpCallExecutor.getResponse();
        try {
            templateContext.setLastAddedDocument(response.getStatusCode().is2xxSuccessful()
                ? new EServiceTemplateDocumentInfo(response.getBody().getId(), prettyName,
                doc.getInputStream().readAllBytes())
                : null);
        } catch (IOException e) {
            fail("Errore imprevisto: il body del documento costruito non restituisce correttamente un InputStream", e);
        }
    }

    public void checkDocumentAddedToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        try {
            // controlla la coerenza con quanto contenuto nel template
            pollingService.makePolling(
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceDoc doc = switch (kind) {
                            case DOCUMENT -> res.getBody().getDocs().stream().filter(d -> d.getId().equals(templateContext.getLastAddedDocument().id())).findFirst().orElse(null);
                            case INTERFACE -> res.getBody().getInterface();
                            default -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                EServiceTemplateDocumentKind.class.getSimpleName(),
                                kind));
                        };
                        return doc.getPrettyName().equals(templateContext.getLastAddedDocument().prettyName());
                    }
                    return false;

                },
                "Lo stato del documento restituito dalla API GET degli e-service templates non corrisponde a quello atteso"
            );

            // controlla la coerenza del documento stesso
            pollingService.makePolling(
                () -> eServiceTemplateClient.getDocumentWithHttpInfo(
                    sharedStepsContext.getXCorrelationId(),
                    eServiceTemplateId,
                    eServiceTemplateVersionId,
                    templateContext.getLastAddedDocument().id()),
                res -> {
                    try {
                        return res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && Files.readLines(res.getBody(), StandardCharsets.UTF_8).get(0).equals(templateContext.getLastAddedDocument().body());
                    } catch (IOException e) {
                        throw new RuntimeException("Errore nella lettura del body binario della risposta HTTP: %s".formatted(res), e);
                    }
                },
                "Lo stato del documento restituito dalla API GET dei documenti non corrisponde a quello atteso"
            );
        } catch (PollingPredicateException e) {
            // TODO altrove non si è stati così precisi nei messaggi di errore, adeguare
            fail("Il documento non è stato aggiunto correttamente alla versione dell'e-service template: " + e.getMessage());
        }
    }

    // TODO verificare in quanti posti è usato, se in 1 solo espandere inline
    public String buildPrettyName(EServiceTemplateDocumentKind kind) {
        return "e-service-template-%s-%s".formatted(kind.toString(),
            this.nextTestResourceNameSuffix());
    }

    public void addRiskAnalysisToEServiceTemplateSuccessfully() {
        addRiskAnalysisToEServiceTemplate();
        checkRiskAnalysisAddedToEServiceTemplate();
    }

    public void addRiskAnalysisToEServiceTemplate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        templateContext.setLastAddedRiskAnalysis(easyRandom.nextObject(EServiceRiskAnalysisSeed.class));
        templateContext.incrementLastAddedRiskAnalysisIndex();
        addRiskAnalysisToEServiceTemplate(eServiceTemplateId, templateContext.getLastAddedRiskAnalysis());
    }

    public void addRiskAnalysisToEServiceTemplate(UUID eServiceTemplateId, EServiceRiskAnalysisSeed riskAnalysisSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.addRiskAnalysis(
                sharedStepsContext.getXCorrelationId(),
                eServiceTemplateId,
                riskAnalysisSeed));
    }

    public void checkRiskAnalysisAddedToEServiceTemplate() {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();

        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getXCorrelationId(),
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res ->
                    nonNull(res.getBody()) &&
                        this.areConsistent(
                            templateContext.getLastAddedRiskAnalysis(),
                            res.getBody().getEserviceTemplate().getRiskAnalysis().get(templateContext.getLastAddedRiskAnalysisIndex())),
                "La risk analysis non è stata aggiunta correttamente all'e-service template"
            );
        } catch (PollingPredicateException e) {
            fail("La risk analysis non è stata aggiunta correttamente all'e-service template");
        }
    }

    public EServiceTemplateAttributesSeed nextAttributesSeed() {
        return new EServiceTemplateAttributesSeed()
            .addCertifiedItem(easyRandom.objects(EServiceTemplateVersionAttributeSeed.class, 3).toList())
            .addDeclaredItem(easyRandom.objects(EServiceTemplateVersionAttributeSeed.class, 3).toList())
            .addVerifiedItem(easyRandom.objects(EServiceTemplateVersionAttributeSeed.class, 3).toList());
    }

    public boolean areConsistent(EServiceRiskAnalysisSeed lastRiskAnalysis, EServiceRiskAnalysis retrievedAnalysis) {
        return lastRiskAnalysis.getName().equals(retrievedAnalysis.getName()) &&
            lastRiskAnalysis.getRiskAnalysisForm().equals(retrievedAnalysis.getRiskAnalysisForm());

        /* TODO retrievedAnalysis ha il campo "createdAt" che però è di tipo stringa: stando
         *  a https://stackoverflow.com/questions/49379006/what-is-the-correct-way-to-declare-a-date-in-an-openapi-swagger-file#:~:text=In%20OpenAPI%2C%20the%20date-time%20format%20is%20used%20to,a%20breakdown%3A%20Regex%20for%20this%3A%20%5Ed%7B4%7D-d%7B2%7D-d%7B2%7DTd%7B2%7D%3Ad%7B2%7D%3Ad%7B2%7DZ%24%20CODE%20%22fmt%22
         *  dovrebbe trattarsi dello standard ISO 8601; arricchire il test così da verificare anche questo dato
         */
    }

    public boolean areConsistent(UpdateEServiceTemplateVersionSeed lastUpdate, EServiceTemplateVersionDetails retrievedTemplate) {
        DescriptorAttributes descriptorAttributes = retrievedTemplate.getAttributes();
        EServiceTemplateAttributesSeed mappedAttributes = this.descriptorAttributesMapper.mapAttributesToSeeds(
            descriptorAttributes);
        return lastUpdate.getAttributes().equals(mappedAttributes) &&
            lastUpdate.getDescription().equals(retrievedTemplate.getDescription()) &&
            lastUpdate.getAgreementApprovalPolicy().equals(retrievedTemplate.getAgreementApprovalPolicy()) &&
            lastUpdate.getVoucherLifespan().equals(retrievedTemplate.getVoucherLifespan()) &&
            lastUpdate.getDailyCallsTotal().equals(retrievedTemplate.getDailyCallsTotal()) &&
            lastUpdate.getDailyCallsPerConsumer().equals(retrievedTemplate.getDailyCallsPerConsumer());
    }
}