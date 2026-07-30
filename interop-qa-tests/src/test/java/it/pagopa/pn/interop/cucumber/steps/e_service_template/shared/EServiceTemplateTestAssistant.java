package it.pagopa.pn.interop.cucumber.steps.e_service_template.shared;

import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingPredicateException;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient;
import it.pagopa.interop.e_service_template.IEServiceTemplateClient.EServiceTemplateDocumentKind;
import it.pagopa.interop.e_service_template.mapper.DescriptorAttributesMapper;
import it.pagopa.interop.e_service_template.mapper.RiskAnalysisMapper;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateDocumentInfo;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.BFFDataPreparationService;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.assertj.core.api.Assertions.fail;

/** It contains general utility functions used across all other classes.  */
@Data
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServiceTemplateTestAssistant {
    private final BFFDataPreparationService dataPreparationService;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IEServiceTemplateClient eServiceTemplateClient;
    private final IHttpExecutor httpCallExecutor;
    private final PollingService pollingService;
    private final EasyRandom easyRandom;
    private final DescriptorAttributesMapper descriptorAttributesMapper;
    private final RiskAnalysisMapper riskAnalysisMapper;

    public EServiceTemplateTestAssistant(ClientTokenConfigurator clientTokenConfigurator,
        BFFDataPreparationService dataPreparationService,
        SharedStepsContext sharedStepsContext,
        DescriptorAttributesMapper descriptorAttributesMapper,
        RiskAnalysisMapper riskAnalysisMapper) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.dataPreparationService = dataPreparationService;
        this.sharedStepsContext = sharedStepsContext;
        this.eServiceTemplateClient = clientTokenConfigurator.getEServiceTemplateClient();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.easyRandom = new EasyRandom(sharedStepsContext.getEServiceTemplateStepContext().getEasyRandomParameters());
        this.descriptorAttributesMapper = descriptorAttributesMapper;
        this.riskAnalysisMapper = riskAnalysisMapper;
    }

    public String buildEServiceTemplateName() {
        String suffix =
            + sharedStepsContext.getTestSeed()
            + "-"
            + this.sharedStepsContext.getEServiceTemplateStepContext().getTemplatesManaged().size();
        return String.format("eservice-template-%s", suffix);
    }

    public void mutateLastVersionState(EServiceTemplateVersionState desiredState) {
        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext()
            .getLastTemplateManaged();
        BiConsumer<UUID, UUID> publisher = (templateId, versionId) -> {
            this.addDocumentToEServiceTemplateVersionSuccessfully(templateId, versionId, EServiceTemplateDocumentKind.INTERFACE, 0); // perché ogni template deve avere almeno un'interfaccia
            if (Boolean.TRUE.equals(lastTemplateManaged.getAsync())) {
                this.updateLastTemplateVersionWithAsyncExchangeProperties();
                this.addDocumentToEServiceTemplateVersionSuccessfully(EServiceTemplateDocumentKind.ASYNC_EXCHANGE_CALLBACK_INTERFACE, 0); // perché ogni template async deve avere almeno un'interfaccia di callback
            }
            publishEServiceTemplate(templateId, versionId);
        };
        switch (desiredState) {
            case DRAFT -> { /* no-op: una versione appena creata è automaticamente in questo stato */ }
            case PUBLISHED -> publisher.accept(lastTemplateManaged.getId(), lastTemplateManaged.getLastVersionId());
            case SUSPENDED -> {
                publisher.accept(lastTemplateManaged.getId(), lastTemplateManaged.getLastVersionId());    // perché prima di essere sospesa deve essere pubblicata
                suspendEServiceTemplate();
            }
            case DEPRECATED -> {
                publisher.accept(lastTemplateManaged.getId(), lastTemplateManaged.getLastVersionId());
                UUID newVersionId = this.createNewVersion(lastTemplateManaged.getId());
                publisher.accept(lastTemplateManaged.getId(), newVersionId);
            }
            default -> throw new IllegalArgumentException("Stato non supportato: " + desiredState);
        }
    }

    /**
     * Aggiorna l'ultima versione del template con una configurazione async di base,
     * preservando gli attributi già presenti sulla versione corrente.
     *
     * Flusso:
     * 1) legge la versione attuale del template
     * 2) recupera/mappa gli attributes esistenti
     * 3) invia update con asyncExchangeProperties
     * 4) effettua polling finché i campi async non risultano persistiti
     */
    private void updateLastTemplateVersionWithAsyncExchangeProperties() {
        EServiceTemplateInfo lastTemplateManaged = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
        UUID eServiceTemplateId = lastTemplateManaged.getId();
        UUID eServiceTemplateVersionId = lastTemplateManaged.getLastVersionId();

        // Read della versione corrente per non perdere informazioni preesistenti (es. attributes).
        httpCallExecutor.performCall(
            () -> this.eServiceTemplateClient.getEServiceTemplateVersion(
                eServiceTemplateId,
                eServiceTemplateVersionId));

        if (httpCallExecutor.getResponseStatus().isError()) {
            throw new IllegalStateException(
                "Esito negativo non previsto durante il recupero della versione del template: "
                    + httpCallExecutor.getResponseStatus()
                    + " - "
                    + httpCallExecutor.getErrorMessage());
        }

        EServiceTemplateVersionDetails retrievedVersion = (EServiceTemplateVersionDetails) httpCallExecutor.getResponse();
        DescriptorAttributes retrievedAttributes = nonNull(retrievedVersion)
            ? retrievedVersion.getAttributes()
            : null;
        // Gli attributes vengono rimappati nel seed di update così da preservare lo stato attuale.
        EServiceTemplateAttributesSeed attributesSeed = nonNull(retrievedAttributes)
            ? this.descriptorAttributesMapper.mapAttributesToSeeds(retrievedAttributes)
            : new EServiceTemplateAttributesSeed();

        AsyncExchangeProperties asyncExchangeProperties = new AsyncExchangeProperties()
            .responseTime(100)
            .resourceAvailableTime(100)
            .maxResultSet(100)
            .confirmation(true)
            .bulk(true);

        UpdateEServiceTemplateVersionSeed seed = new UpdateEServiceTemplateVersionSeed()
            .attributes(attributesSeed)
            .voucherLifespan(6000)
            .asyncExchangeProperties(asyncExchangeProperties);

        // Update della versione con i dati async richiesti dallo scenario.
        httpCallExecutor.performCall(
            () -> this.eServiceTemplateClient.updateEServiceTemplateVersion(
                eServiceTemplateId,
                eServiceTemplateVersionId,
                seed));

        if (httpCallExecutor.getResponseStatus().isError()) {
            throw new IllegalStateException(
                "Esito negativo non previsto durante l'aggiornamento delle asyncExchangeProperties: "
                    + httpCallExecutor.getResponseStatus()
                    + " - "
                    + httpCallExecutor.getErrorMessage());
        }

        try {
            // Polling su GET versione finché la configurazione async non è effettivamente visibile.
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> this.eServiceTemplateClient.getEServiceTemplateVersion(
                        eServiceTemplateId,
                        eServiceTemplateVersionId)),
                status -> {
                    if (status == HttpStatus.NOT_FOUND) {
                        return false;
                    }
                    EServiceTemplateVersionDetails version = (EServiceTemplateVersionDetails) httpCallExecutor.getResponse();
                    AsyncExchangeProperties retrievedAsyncExchangeProperties = nonNull(version) ? version.getAsyncExchangeProperties() : null;
                    return nonNull(retrievedAsyncExchangeProperties)
                        && Integer.valueOf(100).equals(retrievedAsyncExchangeProperties.getResponseTime())
                        && Integer.valueOf(100).equals(retrievedAsyncExchangeProperties.getResourceAvailableTime())
                        && Integer.valueOf(100).equals(retrievedAsyncExchangeProperties.getMaxResultSet())
                        && retrievedAsyncExchangeProperties.getConfirmation()
                        && retrievedAsyncExchangeProperties.getBulk();
                },
                "Le asyncExchangeProperties non sono state applicate correttamente alla versione dell'e-service template"
            );
        } catch (PollingPredicateException e) {
            fail("Le asyncExchangeProperties non sono state applicate correttamente alla versione dell'e-service template: " + e.getMessage());
        }
    }

    private UUID createNewVersion(UUID templateId) {
        CreatedResource createdVersion = this.eServiceTemplateClient.createEServiceTemplateVersion(
            templateId);
        pollingService.makePolling(
            () -> this.eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(templateId, createdVersion.getId()),
            httpResponse -> httpResponse.getStatusCode().is2xxSuccessful(),
            "Non è stata rilevata la versione dell'e-service creata entro il timeout. Consultare logs HTTP per maggiori dettagli."
        );
        return createdVersion.getId();
    }

    public void publishEServiceTemplate() {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.publishEServiceTemplate(
            sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
            sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId());
    }

    public void suspendEServiceTemplate() {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        this.suspendEServiceTemplate(
            sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
            sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId());
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

    public void addDocumentToEServiceTemplateVersionSuccessfully(
        UUID templateId, UUID versionId, EServiceTemplateDocumentKind kind,
        int fileIndex
    ) {
        addDocumentToEServiceTemplateVersion(templateId, versionId, kind, fileIndex);
        checkDocumentAddedToEServiceTemplateVersion(templateId, versionId, kind);
    }

    public void addDocumentToEServiceTemplateVersion(EServiceTemplateDocumentKind kind, int fileIndex) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, fileIndex);
    }

    // TODO troppe varianti di questi metodi, standardizzarne 1 o 2 al massimo
    public void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind, int fileIndex) {
        String prettyName = buildPrettyName(kind);
        addDocumentToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, prettyName, fileIndex);
    }

    public void addDocumentToEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind, String prettyName, int fileIndex
    ) {
        String userToken = clientTokenConfigurator.getLastToken();
        Resource doc = buildResource(kind, fileIndex);
        addDocumentToEserviceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId, kind, prettyName, userToken, doc);
    }

    private Resource buildResource(EServiceTemplateDocumentKind kind, int fileIndex) {
        /* 19/03/2025 Versione precedente in cui si supponeva si potesse passare ogni genere di byte array. */
        /*String docBody = "Hello, I'm a document of type %s".formatted(kind);
        Resource doc = new ByteArrayResource(docBody.getBytes(StandardCharsets.UTF_8));*/

        String basePath = "src/main/resources/";
        String strFileIndex = fileIndex == 0 ? "" : String.valueOf(fileIndex);

        String documentPath = basePath + "dummy" + strFileIndex + ".pdf";

        boolean isRest = this.sharedStepsContext.getEServiceTemplateStepContext().getTechnology() == EServiceTechnology.REST;
        String interfacePath = basePath + "interface" + strFileIndex + (isRest ? ".yaml" : ".wsdl");

        switch (kind) {
            case DOCUMENT -> {
                return new PathResource(Path.of(documentPath));
            }
            case INTERFACE, ASYNC_EXCHANGE_CALLBACK_INTERFACE -> {
                return new PathResource(Path.of(interfacePath));
            }
            default -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                EServiceTemplateDocumentKind.class.getSimpleName(),
                kind));
        }
    }

    public UUID addDocumentToEserviceTemplateVersion(UUID eServiceTemplateId, UUID eServiceTemplateVersionId,
        EServiceTemplateDocumentKind kind, String prettyName, String userToken, Resource doc) {
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.addDocumentWithHttpInfo(
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
                UUID id = response.getBody().getId();
                sharedStepsContext.getEServiceTemplateStepContext().setLastAddedDocument(new EServiceTemplateDocumentInfo(
                    id, prettyName,
                    doc.getInputStream().readAllBytes()));
                return id;
            } catch (IOException e) {
                fail("Errore imprevisto: il body del documento costruito non restituisce correttamente un InputStream", e);
            }
        } else {
            sharedStepsContext.getEServiceTemplateStepContext().setLastAddedDocument(new EServiceTemplateDocumentInfo(null, null, null, httpCallExecutor.getErrorMessage()));
        }

        return eServiceTemplateId;
    }

    public void checkDocumentAddedToEServiceTemplateVersion(EServiceTemplateDocumentKind kind) {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();
        checkDocumentAddedToEServiceTemplateVersion(eServiceTemplateId, eServiceTemplateVersionId,
            kind);
    }

    private void checkDocumentAddedToEServiceTemplateVersion(UUID eServiceTemplateId,
        UUID eServiceTemplateVersionId, EServiceTemplateDocumentKind kind) {
        EServiceTemplateDocumentInfo lastAddedDocument = sharedStepsContext.getEServiceTemplateStepContext().getLastAddedDocument();

        if(isNotEmpty(lastAddedDocument.errorMessage())) {
            fail("Il documento non è stato aggiunto correttamente alla versione dell'e-service template. Ultimo errore noto: %s".formatted(lastAddedDocument.errorMessage()));
        }

        try {
            // controlla la coerenza con quanto contenuto nel template
            pollingService.makePolling(
                () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                    eServiceTemplateId,
                    eServiceTemplateVersionId),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        EServiceDoc doc = switch (kind) {
                            case DOCUMENT -> res.getBody().getDocs().stream().filter(d -> d.getId().equals(
                                lastAddedDocument.id())).findFirst().orElse(null);
                            case INTERFACE -> res.getBody().getInterface();
                            case ASYNC_EXCHANGE_CALLBACK_INTERFACE -> res.getBody().getAsyncExchangeCallbackInterface();
                            default -> throw new IllegalArgumentException("Unsupported %s value: %s".formatted(
                                EServiceTemplateDocumentKind.class.getSimpleName(),
                                kind));
                        };
                        return nonNull(doc) && doc.getPrettyName().equals(lastAddedDocument.prettyName());
                    }
                    return false;

                },
                "Lo stato del documento restituito dalla API GET degli e-service templates non corrisponde a quello atteso"
            );

            // controlla la coerenza del documento stesso
            pollingService.makePolling(
                () -> eServiceTemplateClient.getDocumentWithHttpInfo(
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
                        return res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody()) && Arrays.equals(
                            FileUtils.readFileToByteArray(res.getBody()), lastAddedDocument.body());
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

    public String buildPrettyName(EServiceTemplateDocumentKind kind) {
        return "e-service-template-%s-%d-%d".formatted(kind.toString(), sharedStepsContext.getTestSeed(),
            RandomUtils.insecure().randomInt(0, 1_000_000));
    }

    public void addRiskAnalysisToEServiceTemplateSuccessfully() {
        addRiskAnalysisToEServiceTemplate();
        checkRiskAnalysisAddedToEServiceTemplate();
    }

    public void addRiskAnalysisToEServiceTemplate() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        sharedStepsContext.getEServiceTemplateStepContext().setLastAddedRiskAnalysis(getEServiceRiskAnalysisSeed());
        sharedStepsContext.getEServiceTemplateStepContext().incrementLastAddedRiskAnalysisIndex();
        addRiskAnalysisToEServiceTemplate(eServiceTemplateId, sharedStepsContext.getEServiceTemplateStepContext().getLastAddedRiskAnalysis());
    }

    private EServiceTemplateRiskAnalysisSeed getEServiceRiskAnalysisSeed() {
        return getEServiceRiskAnalysisSeed(true);
    }

    public EServiceTemplateRiskAnalysisSeed getEServiceRiskAnalysisSeed(boolean completed) {
        String tenantType = sharedStepsContext.getTenantType();
        return getEServiceRiskAnalysisSeedWithType(tenantType, true);
    }

    public EServiceTemplateRiskAnalysisSeed getEServiceRiskAnalysisSeedWithType(String tenantType, boolean completed) {
        IdentityService identityService = sharedStepsContext.getIdentityService();
        RiskAnalysis riskAnalysis = this.dataPreparationService.getRiskAnalysis(tenantType, completed);
        return this.riskAnalysisMapper.mapToSeed(riskAnalysis, TenantKind.fromValue(identityService.getKind(tenantType)));
    }

    public void addRiskAnalysisToEServiceTemplate(UUID eServiceTemplateId, EServiceTemplateRiskAnalysisSeed riskAnalysisSeed) {
        String userToken = sharedStepsContext.getUserToken();
        clientTokenConfigurator.setBearerToken(userToken);
        httpCallExecutor.performCall(
            () -> eServiceTemplateClient.addRiskAnalysis(
                eServiceTemplateId,
                riskAnalysisSeed));
    }

    public void checkRiskAnalysisAddedToEServiceTemplate() {
        UUID eServiceTemplateId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        UUID eServiceTemplateVersionId = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId();

        try {
            int lastAddedRiskAnalysisIndex = sharedStepsContext.getEServiceTemplateStepContext()
                .getLastAddedRiskAnalysisIndex();
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        eServiceTemplateId,
                        eServiceTemplateVersionId),
                    ResponseEntity::getStatusCode),
                res ->
                    nonNull(res.getBody()) &&
                        this.areConsistent(
                            sharedStepsContext.getEServiceTemplateStepContext().getLastAddedRiskAnalysis(),
                            res.getBody().getEserviceTemplate().getRiskAnalysis().get(
                                lastAddedRiskAnalysisIndex)),
                "La risk analysis non è stata aggiunta correttamente all'e-service template"
            );
            ResponseEntity<EServiceTemplateVersionDetails> re = (ResponseEntity<EServiceTemplateVersionDetails>) httpCallExecutor.getResponse();
            if(re.getStatusCode().is2xxSuccessful()) {
                this.sharedStepsContext.getEServiceTemplateStepContext()
                    .setLastAddedRiskAnalysisId(re.getBody().getEserviceTemplate().getRiskAnalysis().get(lastAddedRiskAnalysisIndex).getId());
            }
        } catch (IllegalArgumentException e) { // TODO altrove è stato usato PollingPredicateException, che impedirà il catch di IllegalArgumentException, correggere
            fail("La risk analysis non è stata aggiunta correttamente all'e-service template");
        }
    }

    public EServiceTemplateAttributesSeed nextAttributesSeed() {
        return new EServiceTemplateAttributesSeed()
                .addCertifiedItem(
                        easyRandom.objects(EServiceTemplateVersionAttributeSeed.class, 3)
                                .peek(seed -> seed.setDiscreteConfig(null))
                                .toList()
                )
                .addDeclaredItem(
                        easyRandom.objects(EServiceTemplateVersionAttributeSeed.class, 3)
                                .peek(seed -> seed.setDiscreteConfig(null))
                                .toList()
                )
                .addVerifiedItem(
                        easyRandom.objects(EServiceTemplateVersionAttributeSeed.class, 3)
                                .peek(seed -> seed.setDiscreteConfig(null))
                                .toList()
                );
    }

    public boolean areConsistent(EServiceTemplateRiskAnalysisSeed lastRiskAnalysis, EServiceTemplateRiskAnalysis retrievedAnalysis) {
        /* TODO: modificare usando un assertion equals di AssertJ così da avere un log preciso in caso di errore
        *       Bisognerà introdurre un mapper per i due tipi di sopra
        *       Dopo l'assertion basterà restituire true (se non ci sono stati AssertionError può solo essere andata bene) */

        return lastRiskAnalysis.getName().equals(retrievedAnalysis.getName()) &&
            lastRiskAnalysis.getRiskAnalysisForm().getVersion().equals(retrievedAnalysis.getRiskAnalysisForm().getVersion()) &&
            lastRiskAnalysis.getRiskAnalysisForm().getAnswers().equals(retrievedAnalysis.getRiskAnalysisForm().getAnswers());

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
            templateId,
            templateVersionId);
        Predicate<ResponseEntity<EServiceTemplateVersionDetails>> pollingStopPredicate = res ->
            res.getStatusCode() != HttpStatus.NOT_FOUND && requireNonNull(
                res.getBody()).getState() == EServiceTemplateVersionState.PUBLISHED;
        mutateEServiceTemplateState(templateId, templateVersionId, templatePublisher, pollingStopPredicate);
    }

    public void suspendEServiceTemplate(UUID templateId, UUID templateVersionId) {
        Runnable templateSuspender = () -> eServiceTemplateClient.suspendEServiceTemplate(
            templateId,
            templateVersionId);
        Predicate<ResponseEntity<EServiceTemplateVersionDetails>> pollingStopPredicate = res ->
            res.getStatusCode() != HttpStatus.NOT_FOUND && requireNonNull(
                res.getBody()).getState() == EServiceTemplateVersionState.SUSPENDED;
        mutateEServiceTemplateState(templateId, templateVersionId, templateSuspender, pollingStopPredicate);
    }

    public void activateEServiceTemplate(UUID templateId, UUID templateVersionId) {
        Runnable templateActivator = () -> eServiceTemplateClient.activateEServiceTemplate(
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
                templateId,
                templateVersionId),
            pollingStopPredicate,
            "There was an error while retrieving the e-service template"
        );
    }

    public void checkEServiceTemplateVersion(Predicate<EServiceTemplateVersionDetails> versionIsAsExpected, String errorMsg) {
        try {
            pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                    () -> eServiceTemplateClient.getEServiceTemplateVersionWithHttpInfo(
                        sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId(),
                        sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getLastVersionId()),
                    ResponseEntity::getStatusCode),
                res -> {
                    if(res.getStatusCode().is2xxSuccessful() && nonNull(res.getBody())) {
                        return versionIsAsExpected.test(res.getBody());
                    }
                    return false;
                },
                errorMsg
            );
        } catch (PollingPredicateException e) {
            // TODO occorrerebbero più dettagli, sul modello di quelli dati solitamente in automatico da AssertJ
            fail(errorMsg);
        }
    }
}