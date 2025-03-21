package it.pagopa.pn.interop.cucumber.steps.e_service_template.shared;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.assertj.core.api.Assertions.fail;

import com.google.common.io.Files;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.e_service_template.mapper.RiskAnalysisMapper;
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
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.e_service_template.shared.EServiceTemplateStepContext.EServiceTemplateDocumentInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
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
    private final RiskAnalysisMapper riskAnalysisMapper;

    public EServiceTemplateTestAssistant(ClientTokenConfigurator clientTokenConfigurator,
        DataPreparationService dataPreparationService,
        SharedStepsContext sharedStepsContext,
        EServiceTemplateStepContext templateContext,
        DescriptorAttributesMapper descriptorAttributesMapper,
        RiskAnalysisMapper riskAnalysisMapper) {
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
        this.riskAnalysisMapper = riskAnalysisMapper;
    }

    public String nextTestResourceNameSuffix() {
        int randomInt = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        return String.format("%d-%d", sharedStepsContext.getTestSeed(), randomInt);
    }

    // TODO generalizzabile in "mutateVersionState"
    public void mutateLastVersionState(EServiceTemplateVersionState desiredState) {
        Runnable publisher = () -> {
            this.addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind.INTERFACE, 0); // perché ogni template deve avere almeno un'interfaccia
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
        this.publishEServiceTemplate(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId());
    }

    public void suspendEServiceTemplate() {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.suspendEServiceTemplate(
            templateContext.getLastTemplateManaged().id(),
            templateContext.getLastTemplateManaged().lastVersionId());
    }

    /** Adds a document of the specified kind to the last managed e-service template version.
     * It then checks that the document has been correctly added to the e-service template version.
     * @param kind the kind of document to add
     * @param fileIndex index of the pre-defined file to use as document body. Index starts by 0.
     * */
    public void addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind kind, int fileIndex) {
        addDocumentToEServiceTemplateVersion(kind, fileIndex);
        checkDocumentAddedToEServiceTemplateVersion(kind);
    }

    public void addDocumentToEServiceTemplateVersion(EServiceTemplateDocumentKind kind, int fileIndex) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, fileIndex);
    }

    // TODO troppe varianti di questi metodi, standardizzarne 1 o 2 al massimo
    public void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind, int fileIndex) {
        String prettyName = buildPrettyName(kind);
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, prettyName, fileIndex);
    }

    public void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind, String prettyName, int fileIndex) {
        String userToken = sharedStepsContext.getUserToken();
        Resource doc = buildResource(kind, fileIndex);
        addDocumentToEserviceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, prettyName, userToken, doc);
    }

    private static Resource buildResource(EServiceTemplateDocumentKind kind, int fileIndex) {
        /* 19/03/2025 Versione precedente in cui si supponeva si potesse passare ogni genere di byte array. */
        /*String docBody = "Hello, I'm a document of type %s".formatted(kind);
        Resource doc = new ByteArrayResource(docBody.getBytes(StandardCharsets.UTF_8));*/

        String basePath = "src/main/resources/";
        String strFileIndex = fileIndex == 0 ? "" : String.valueOf(fileIndex);

        String documentPath = basePath + "dummy" + strFileIndex + ".pdf";
        String interfacePath = basePath + "interface" + strFileIndex + ".yaml";

        switch (kind) {
            case DOCUMENT -> {
                return new PathResource(Path.of(documentPath));
            }
            case INTERFACE -> {
                return new PathResource(Path.of(interfacePath));
            }
            default -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                EServiceTemplateDocumentKind.class.getSimpleName(),
                kind));
        }
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
        if(!httpCallExecutor.getResponseStatus().isError()) {
            ResponseEntity<CreatedResource> response = (ResponseEntity<CreatedResource>) httpCallExecutor.getResponse();
            try {
                templateContext.setLastAddedDocument(new EServiceTemplateDocumentInfo(response.getBody().getId(), prettyName,
                    doc.getInputStream().readAllBytes()));
            } catch (IOException e) {
                fail("Errore imprevisto: il body del documento costruito non restituisce correttamente un InputStream", e);
            }
        } else {
            templateContext.setLastAddedDocument(new EServiceTemplateDocumentInfo(null, null, null, httpCallExecutor.getErrorMessage()));
        }

    }

    public void checkDocumentAddedToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = templateContext.getLastTemplateManaged().id();
        UUID eServiceTemplateVersionId = templateContext.getLastTemplateManaged().lastVersionId();
        EServiceTemplateDocumentInfo lastAddedDocument = templateContext.getLastAddedDocument();

        if(isNotEmpty(lastAddedDocument.errorMessage())) {
            fail("Il documento non è stato aggiunto correttamente alla versione dell'e-service template. Ultimo errore noto: %s".formatted(lastAddedDocument.errorMessage()));
        }

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
                            case DOCUMENT -> res.getBody().getDocs().stream().filter(d -> d.getId().equals(
                                lastAddedDocument.id())).findFirst().orElse(null);
                            case INTERFACE -> res.getBody().getInterface();
                            default -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                EServiceTemplateDocumentKind.class.getSimpleName(),
                                kind));
                        };
                        return doc.getPrettyName().equals(lastAddedDocument.prettyName());
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
                    lastAddedDocument.id()),
                res -> {
                    try {
                        /* TODO 19/03/2025: per verificare che il documento sia stato caricato
                        *   correttamente viene effettuato un confronto completo byte-per-byte.
                        *   Una soluzione più efficiente potrebbe essere confrontare soltanto
                        *   dei valori di hash generati sia in fase di invio che in fase di
                        *   ricezione, aiutandosi eventualmente con due metodi supplementari che,
                        *   sia inviando che ricevendo, anziché restituire void o un
                        *   oggetto File restituiscano un'oggetto hash di qualche tipo; a quel
                        *   punto qui basterà confrontare i valori hash. */
                        return res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && Arrays.equals(Files.toByteArray(res.getBody()), lastAddedDocument.body());
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
        templateContext.setLastAddedRiskAnalysis(getEServiceRiskAnalysisSeed());
        templateContext.incrementLastAddedRiskAnalysisIndex();
        addRiskAnalysisToEServiceTemplate(eServiceTemplateId, templateContext.getLastAddedRiskAnalysis());
    }

    private EServiceRiskAnalysisSeed getEServiceRiskAnalysisSeed() {
        RiskAnalysis riskAnalysis = this.dataPreparationService.getRiskAnalysis(
            sharedStepsContext.getTenantType(), true);
        return this.riskAnalysisMapper.mapToSeed(riskAnalysis);
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
        } catch (IllegalArgumentException e) { // TODO altrove è stato usato PollingPredicateException, che impedirà il catch di IllegalArgumentException, correggere
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
        /* TODO: modificare usando un assertion equals di AssertJ così da avere un log preciso in caso di errore
        *       Bisognerà introdurre un mapper per i due tipi di sopra
        *       Dopo l'assertion basterà restituire true (se non ci sono stati AssertionError può solo essere andata bene) */

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

    public void publishEServiceTemplate(UUID templateId, UUID templateVersionId) {
        Runnable templatePublisher = () -> eServiceTemplateClient.publishEServiceTemplate(
            sharedStepsContext.getXCorrelationId(),
            templateId,
            templateVersionId);
        Predicate<ResponseEntity<EServiceTemplateVersionDetails>> pollingStopPredicate = res ->
            res.getStatusCode() != HttpStatus.NOT_FOUND && requireNonNull(
                res.getBody()).getState() == EServiceTemplateVersionState.PUBLISHED;
        mutateEServiceTemplateState(templateId, templateVersionId, templatePublisher, pollingStopPredicate);
    }

    public void suspendEServiceTemplate(UUID templateId, UUID templateVersionId) {
        Runnable templateSuspender = () -> eServiceTemplateClient.suspendEServiceTemplate(
            sharedStepsContext.getXCorrelationId(),
            templateId,
            templateVersionId);
        Predicate<ResponseEntity<EServiceTemplateVersionDetails>> pollingStopPredicate = res ->
            res.getStatusCode() != HttpStatus.NOT_FOUND && requireNonNull(
                res.getBody()).getState() == EServiceTemplateVersionState.SUSPENDED;
        mutateEServiceTemplateState(templateId, templateVersionId, templateSuspender, pollingStopPredicate);
    }

    public void activateEServiceTemplate(UUID templateId, UUID templateVersionId) {
        Runnable templateActivator = () -> eServiceTemplateClient.activateEServiceTemplate(
            sharedStepsContext.getXCorrelationId(),
            templateId,
            templateVersionId);
        Predicate<ResponseEntity<EServiceTemplateVersionDetails>> pollingStopPredicate = res ->
            res.getStatusCode() != HttpStatus.NOT_FOUND && requireNonNull(
                res.getBody()).getState() == EServiceTemplateVersionState.PUBLISHED;
        mutateEServiceTemplateState(templateId, templateVersionId, templateActivator, pollingStopPredicate);
    }

    private void mutateEServiceTemplateState(
        UUID templateId,
        UUID templateVersionId,
        Runnable templateStateMutator,
        Predicate<ResponseEntity<EServiceTemplateVersionDetails>> pollingStopPredicate)
    {
        httpCallExecutor.performCall(templateStateMutator);
        if (httpCallExecutor.getResponseStatus().isError()) {
            return;
        }
        pollingService.makePolling(
            /* NOTE: in questa chiamata NON si sta usando HttpCallExecutor perché la chiamata
             * "principale" - quella il cui esito dovrà eventualmente essere verificato dai
             * test - è quella appena effettuata, non questa, che serve solo ad attendere
             * l'effettivo mutamento di stato. */
            () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                sharedStepsContext.getXCorrelationId(),
                templateId,
                templateVersionId),
            pollingStopPredicate,
            "There was an error while retrieving the e-service template"
        );
    }
}