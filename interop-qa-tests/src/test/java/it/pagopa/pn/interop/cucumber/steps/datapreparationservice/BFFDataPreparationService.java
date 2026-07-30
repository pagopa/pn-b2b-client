package it.pagopa.pn.interop.cucumber.steps.datapreparationservice;

import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.authorization.service.ClientAdminConfig;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.RiskAnalysisDataInitializer;
import it.pagopa.interop.purpose.domain.CreatedEserviceVersion;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.domain.RiskAnalysisDataFromJson;
import it.pagopa.interop.purpose.domain.TEServiceMode;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.Document;
import it.pagopa.pn.interop.cucumber.steps.DocumentMetadata;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.*;
import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import static it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState.PUBLISHED;
import static it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.RECEIVE;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNullElse;
import static org.apache.commons.collections4.IterableUtils.size;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BFFDataPreparationService {
    @Data
    @Builder
    public static class MutateDescriptorResult {
        private UUID descriptorId;
        private UUID interfaceId;
        private UUID callbackInterfaceId;
        private List<DocumentMetadata> documentsMetadata;

        @Nullable
        public UUID getDocumentId(int index) {
            return size(documentsMetadata) > index ? documentsMetadata.get(0).getId() : null;
        }
    }

    private static final ClientSeed DEFAULT_CLIENT_SEED = new ClientSeed();
    private final IAuthorizationClient authorizationClient;
    private final IAgreementClient agreementClient;
    private final IAttributeApiClient attributeApiClient;
    private final ITenantsApi tenantsApi;
    private final IEServiceClient eServiceClient;
    private final IProducerClient producerClient;
    private final IPurposeApiClient purposeApiClient;
    private final PollingService pollingService;
    private final IHttpExecutor httpCallExecutor;
    private final RiskAnalysisDataInitializer riskAnalysisDataInitializer;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;
    private final BlobFileCreator blobFileCreator;
    private final it.pagopa.interop.authorization.service.DataPreparationService mainDataPrepService;
    private final DataPreparationServiceTemplate template;
    private final DelayService delayService;

    public static final String ERROR_RETRIEVING_AGREEMENT = "There was an error while retrieving the agreement by ID!";
    public static final String ERROR_RETRIEVING_PRODUCER_DESCRIPTOR = "There was an error while retrieving the producer e-service descriptor";
    public static final String ERROR_RETRIEVING_PURPOSE = "There was an error while retrieving the purpose!";
    public static final String DESCRIPTION_TEST = "description_test";

    static {
        DEFAULT_CLIENT_SEED.setName(String.format("client %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
        DEFAULT_CLIENT_SEED.setDescription("Descrizione client");
        DEFAULT_CLIENT_SEED.setMembers(List.of());
    }

    public BFFDataPreparationService(
            ClientTokenConfigurator clientTokenConfigurator,
            RiskAnalysisDataInitializer riskAnalysisDataInitializer,
            SharedStepsContext sharedStepsContext,
            BlobFileCreator blobFileCreator,
            CommonUtils commonUtils,
            it.pagopa.interop.authorization.service.DataPreparationService mainDataPrepService,
            DelayService delayService) {
        this.authorizationClient = clientTokenConfigurator.getAuthorizationClient();
        this.agreementClient = clientTokenConfigurator.getAgreementClient();
        this.attributeApiClient = clientTokenConfigurator.getAttributeApiClient();
        this.tenantsApi = clientTokenConfigurator.getTenantsApi();
        this.eServiceClient = clientTokenConfigurator.getEServiceClient();
        this.producerClient = clientTokenConfigurator.getProducerClient();
        this.purposeApiClient = clientTokenConfigurator.getPurposeApiClient();
        this.sharedStepsContext = sharedStepsContext;
        this.blobFileCreator = blobFileCreator;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.riskAnalysisDataInitializer = riskAnalysisDataInitializer;
        this.commonUtils = commonUtils;

        this.mainDataPrepService = mainDataPrepService;
        this.mainDataPrepService.setAuthorizationClient(this.authorizationClient);
        this.mainDataPrepService.setHttpCallExecutor(httpCallExecutor);

        this.template = new DataPreparationServiceTemplate(
                this.httpCallExecutor,
                this.pollingService,
                this.commonUtils
        );

        this.delayService = delayService;
    }

    public UUID createClient(String clientKind, ClientSeed partialClientSeed) {
        return this.mainDataPrepService.createClient(clientKind, partialClientSeed);
    }

    public void addMemberToClient(UUID clientId, UUID userId) {
        this.mainDataPrepService.addMemberToClient(clientId, userId);
    }

    public void addPurposeToClient(UUID clientId, UUID purposeId) {
        PurposeAdditionDetailsSeed purposeAdditionDetailsSeed = new PurposeAdditionDetailsSeed().purposeId(purposeId);
        httpCallExecutor.performCall(() -> authorizationClient.addClientPurpose(clientId, purposeAdditionDetailsSeed));
        assertValidResponse();

        pollingService.makePolling(
                () -> authorizationClient.getClient(clientId),
                res -> res.getPurposes().stream().anyMatch(purp -> purp.getPurposeId().equals(purposeId)),
                "Failed to add a purpose to the client!"
        );
    }

    public void archivePurpose(UUID purposeId, UUID versionId) {
        httpCallExecutor.performCall(() ->
                purposeApiClient.archivePurposeVersion(purposeId, versionId)
        );
        assertValidResponse();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> purposeApiClient.getPurpose(purposeId)),
                res -> ((Purpose) httpCallExecutor.getResponse()).getCurrentVersion() != null
                        ? ((Purpose) httpCallExecutor.getResponse()).getCurrentVersion().getState().getValue().equals(PurposeVersionState.ARCHIVED.getValue())
                        : Boolean.FALSE,
                ERROR_RETRIEVING_PURPOSE
        );
    }

    public void suspendPurpose(UUID purposeId, UUID versionId, ClientType checkSuspendedBy) {
        httpCallExecutor.performCall(() ->
                purposeApiClient.suspendPurposeVersion(purposeId, versionId)
        );
        assertValidResponse();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> purposeApiClient.getPurpose(purposeId)),
                res -> {
                    Purpose purpose = ((Purpose) httpCallExecutor.getResponse());
                    boolean isSuspended = purpose.getCurrentVersion().getState() == PurposeVersionState.SUSPENDED;
                    if (checkSuspendedBy == null) return isSuspended;
                    else if (checkSuspendedBy == ClientType.CONSUMER) {
                        isSuspended = isTrue(purpose.getSuspendedByConsumer());
                    }
                    else {
                        isSuspended = isTrue(purpose.getSuspendedByProducer());
                    }
                    return isSuspended;
                },
                ERROR_RETRIEVING_PURPOSE
        );
    }

    public String addPublicKeyToClient(UUID clientId, KeySeed keySeed) {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.createKeys(clientId, List.of(keySeed))),
                res -> res != HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create a new key!"
        );
        assertValidResponse();
        AtomicReference<Optional<String>> keyFound = new AtomicReference<>(Optional.empty());

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.getClientKeys(clientId, 0, 50, null)),
                res -> {
                    keyFound.set(((PublicKeys) httpCallExecutor.getResponse()).getKeys().stream()
                            .filter(ks -> ks.getName().equals(keySeed.getName()))
                            .map(PublicKey::getKeyId).findAny());
                    return keyFound.get().isPresent();
                },
                "There was an error while retrieving the client keys!"
        );
        return keyFound.get().isPresent() ? keyFound.get().get() : null;
    }

    public UUID createAgreementWithGivenState(AgreementState agreementState, UUID eServiceID, UUID descriptorId, File doc) {
        return createAgreementWithGivenState(agreementState, eServiceID, descriptorId, null, doc);
    }

    public UUID createAgreementWithGivenState(AgreementState agreementState, UUID eServiceID, UUID descriptorId, UUID delegationId, File doc) {
        CreateAgreementWithStateOperation op = CreateAgreementWithStateOperation.builder()
            .createAndCheckAgreementOperation(buildCreateAndCheckAgreementOperation())
            .submitAgreementOperation(buildSubmitAgreementOperation())
            .suspendAgreementOperation(buildSuspendAgreementOperation())
            .archiveAgreementOperation(buildArchiveAgreementOperation())
            .addConsumerDocumentOperation(buildAddConsumerDocumentOperation())
            .build();
        return template.createAgreementWithGivenState(op, UpperAgreementState.from(agreementState), eServiceID, descriptorId, delegationId, doc);
    }

    public Map<String, UUID> createAgreementWithGivenStateAndDocument(AgreementState agreementState, UUID eserviceId, UUID descriptorId) {
        try {
            Resource doc = blobFileCreator.createBlobFile("src/main/resources/dummy.pdf", "documento-test-qa.pdf");
            UUID agreementId = createAgreementWithGivenState(agreementState, eserviceId, descriptorId, null, doc.getFile());
            Agreement agreement = agreementClient.getAgreementById(agreementId);
            UUID documentId = agreement.getConsumerDocuments().get(0).getId();
            return Map.of("agreementId", agreementId, "documentId", documentId);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public UUID createAndCheckAgreement(UUID eServiceID, UUID descriptorId) {
        return createAndCheckAgreement(eServiceID, descriptorId, null);
    }

    public Optional<UUID> createAgreement(UUID eServiceID, UUID descriptorId, @Nullable UUID delegationId) {
        CreateAgreementOperation operation = buildCreateAgreementOperation();
        return template.createAgreement(operation, eServiceID, descriptorId, delegationId);
    }

    private CreateAgreementOperation buildCreateAgreementOperation() {
        return CreateAgreementOperation.of(
            params -> agreementClient.createAgreement(new AgreementPayload()
                .eserviceId(params.getEServiceID())
                .descriptorId(params.getDescriptorId())
                .delegationId(params.getDelegationId())
            ).getId()
        );
    }

    public UUID createAndCheckAgreement(UUID eServiceID, UUID descriptorId, UUID delegationId) {
        CreateAndCheckAgreementOperation operation = buildCreateAndCheckAgreementOperation();
        return template.createAndCheckAgreement(operation, eServiceID, descriptorId, delegationId);
    }

    private CreateAndCheckAgreementOperation buildCreateAndCheckAgreementOperation() {
        return CreateAndCheckAgreementOperation.of(
            buildCreateAgreementOperation(),
            agreementClient::getAgreementById
        );
    }

    public void submitAgreement(UUID agreementId, AgreementState expectedState) {
        SubmitAgreementOperation operation = buildSubmitAgreementOperation();
        template.submitAgreement(operation, agreementId, UpperAgreementState.from(expectedState));
    }

    private SubmitAgreementOperation buildSubmitAgreementOperation() {
        return SubmitAgreementOperation.of(
            agrId -> UpperAgreement.from(
                agreementClient.submitAgreement(agrId, new AgreementSubmissionPayload())),
            agrId -> UpperAgreement.from(agreementClient.getAgreementById(agrId)));
    }

    public void suspendAgreement(UUID agreementId, ClientType suspendedBy) {
        SuspendAgreementOperation op = buildSuspendAgreementOperation();
        template.suspendAgreement(op, agreementId, suspendedBy);
    }

    private SuspendAgreementOperation buildSuspendAgreementOperation() {
        return SuspendAgreementOperation.builder()
            .apiCaller(id -> UpperAgreement.from(agreementClient.suspendAgreement(id)))
            .checkerApiCaller(id -> UpperAgreement.from(agreementClient.getAgreementById(id)))
            .build();
    }

    public void archiveAgreement(UUID agreementId) {
        ArchiveAgreementOperation op = buildArchiveAgreementOperation();
        template.archiveAgreement(op, agreementId);
    }

    private ArchiveAgreementOperation buildArchiveAgreementOperation() {
        return ArchiveAgreementOperation.builder()
            .apiCaller(agreementClient::archiveAgreement)
            .checkerApiCaller(id -> UpperAgreement.from(agreementClient.getAgreementById(id)))
            .build();
    }

    public void addConsumerDocumentToAgreement(UUID agreementId, File doc) {
        AddConsumerDocumentOperation op = buildAddConsumerDocumentOperation();
        template.addConsumerDocumentToAgreement(op, agreementId, doc);
    }

    private AddConsumerDocumentOperation buildAddConsumerDocumentOperation() {
        return AddConsumerDocumentOperation.builder()
            .apiCaller(params -> agreementClient.addAgreementConsumerDocument(
                params.getAgreementId(),
                "documento-test-qa.pdf",
                "documento-test-qa",
                new FileSystemResource(params.getDoc())))
            .checkerApiCaller(id -> UpperAgreement.from(agreementClient.getAgreementById(id)))
            .documentListExtractor(res -> ((UpperAgreement) res).getConsumerDocuments())
            .build();
    }

    public Attribute createAttribute(AttributeKind attributeKind) {
        return this.createAttribute(attributeKind, null);
    }

    public Attribute createAttribute(AttributeKind attributeKind, String name) {
        String actualName = name == null ? String.format("new_attribute_%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)) : name;
        switch (attributeKind) {
            case CERTIFIED ->
                    httpCallExecutor.performCall(() -> attributeApiClient.createCertifiedAttribute(new CertifiedAttributeSeed().description(DESCRIPTION_TEST).name(actualName)));
            case VERIFIED ->
                    httpCallExecutor.performCall(() -> attributeApiClient.createVerifiedAttribute(new AttributeSeed().description(DESCRIPTION_TEST).name(actualName)));
            case DECLARED ->
                    httpCallExecutor.performCall(() -> attributeApiClient.createDeclaredAttribute(new AttributeSeed().description(DESCRIPTION_TEST).name(actualName)));
            default -> throw new IllegalArgumentException("Invalid attributeKind: " + attributeKind);
        }
        assertValidResponse();

        pollingService.makePolling(
                () -> attributeApiClient.getAttributes(1, 0, List.of(attributeKind), actualName, null),
                res -> !res.getResults().isEmpty(),
                "There was an error while retrieving the attributes"
        );
        return ((Attribute) httpCallExecutor.getResponse());
    }

    public void declareDeclaredAttribute(UUID tenantId, UUID attributeId) {
        httpCallExecutor.performCall(() -> tenantsApi.addDeclaredAttribute(new DeclaredTenantAttributeSeed().id(attributeId)));
        assertValidResponse();
        pollingService.makePolling(
                () -> tenantsApi.getDeclaredAttributes(tenantId),
                res -> res.getAttributes().stream().anyMatch(attr -> attr.getId().equals(attributeId)),
                String.format("Declared attribute with id: %s not found!", attributeId)
        );

    }

    public void assignCertifiedAttributeToTenant(UUID tenantId, UUID attributeId) {
        httpCallExecutor.performCall(
                () -> tenantsApi.addCertifiedAttribute(tenantId, new CertifiedTenantAttributeSeed().id(attributeId)));
        assertValidResponse();

        pollingService.makePolling(
                () -> tenantsApi.getCertifiedAttributes(tenantId),
                res -> res.getAttributes().stream().anyMatch(attr -> attr.getId().equals(attributeId)),
                "There was an error while retrieving the attributes"
        );
    }

    public void assignDeclaredAttributeToTenant(UUID tenantId, UUID attributeId) {
        httpCallExecutor.performCall(
            () -> tenantsApi.addDeclaredAttribute(new DeclaredTenantAttributeSeed().id(attributeId)));
        assertValidResponse();

        // FIXME 26/03/2025 momentaneamente disabilitato a causa dell'imprevisto contenuto della
        //  risposta, la quale è vuota quando non dovrebbe. Non impattante sull'attuale parco test.
        /*pollingService.makePolling(
            () -> tenantsApi.getDeclaredAttributes(xCorrelationId, tenantId),
            res -> res.getAttributes().stream().anyMatch(attr -> attr.getId().equals(attributeId)),
            "There was an error while retrieving the attributes"
        );*/
    }

    public void assignVerifiedAttributeToTenant(UUID tenantId, UUID verifierId, UUID attributeId, UUID agreementId, String expirationDate) {
        httpCallExecutor.performCall(
                () -> tenantsApi.verifyVerifiedAttribute(tenantId,
                        new VerifiedTenantAttributeSeed().id(attributeId).agreementId(agreementId).expirationDate(expirationDate)));
        assertValidResponse();

        pollingService.makePolling(
                () -> tenantsApi.getVerifiedAttributes(tenantId),
                res -> res.getAttributes().stream()
                        .filter(attr -> attr.getId().equals(attributeId))
                        .anyMatch(attr -> attr.getVerifiedBy().stream().anyMatch(tenantVerifier -> tenantVerifier.getId().equals(verifierId))
                        && attr.getRevokedBy().stream().noneMatch(tenantRevoker -> tenantRevoker.getId().equals(verifierId))),
                String.format("Verified attribute with id: %s not found!", attributeId)
        );
    }

    public EServiceDescriptor createEServiceInState(EServiceSeed partialEserviceSeed, UpdateEServiceDescriptorSeed partialDescriptorSeed, @Nonnull EServiceState eServiceState) {
        if(EServiceState.ARCHIVED.equals(eServiceState)) {
            /* NOTE 09/03/2026: il passaggio dell'e-service (non di un suo descriptor, dell'intero e-service)
             * in stato ARCHIVED non è al momento supportato (rif. https://pagopaspa.slack.com/archives/C06D24MANNN/p1772816415479329).
             * Quando sarà supportato, si prevede di sostituire il lancio dell'eccezione con l'implementazione effettiva.  */
            throw new UnsupportedOperationException("L'archiviazione di un e-service nella sua interezza non è al momento supportata dalla piattaforma Interop");
        } else {
            EServiceDescriptor eServiceDescriptor = this.createEServiceAndDraftDescriptor(partialEserviceSeed, partialDescriptorSeed);
            this.bringDescriptorToGivenState(eServiceDescriptor.getEServiceId(),
                    eServiceDescriptor.getDescriptorId(), EServiceDescriptorState.valueOf(
                            eServiceState.toString()), false);
            return eServiceDescriptor;
        }
    }

    public EServiceDescriptor createEServiceAndDraftDescriptor(EServiceSeed partialEserviceSeed, UpdateEServiceDescriptorSeed partialDescriptorSeed) {
        EServiceSeed defaultEserviceSeed = new EServiceSeed()
                .name(String.format("e-service %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)))
                .description("Descrizione e-service")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .isConsumerDelegable(false)
                .isClientAccessDelegable(false)
                .personalData(false);
        EServiceSeed eServiceSeed = merge(defaultEserviceSeed, partialEserviceSeed);

        httpCallExecutor.performCall(() -> eServiceClient.createEService(eServiceSeed));
        assertValidResponse();
        UUID eserviceId = ((CreatedEServiceDescriptor) httpCallExecutor.getResponse()).getId();
        UUID descriptorId = ((CreatedEServiceDescriptor) httpCallExecutor.getResponse()).getDescriptorId();

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> producerClient.getProducerEServiceDescriptor(eserviceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );

        ProducerEServiceDescriptor producerEServiceDescriptor = (ProducerEServiceDescriptor) httpCallExecutor.getResponse();
        sharedStepsContext.getEServicesCommonContext().setName(producerEServiceDescriptor.getEservice().getName());
        sharedStepsContext.getEServicesCommonContext().setDescription(producerEServiceDescriptor.getEservice().getDescription());

        updateDraftDescriptor(eserviceId, descriptorId, partialDescriptorSeed);
        return new EServiceDescriptor(eserviceId, descriptorId);
    }

    public EServiceDescriptor createEServiceAndDraftDescriptorSpecifyingConsumerDelegationFlags(EServiceSeed partialEserviceSeed, UpdateEServiceDescriptorSeed partialDescriptorSeed, Boolean isConsumerDelegable, Boolean isClientAccessDelegable) {
        EServiceSeed defaultEserviceSeed = new EServiceSeed()
                .name(String.format("e-service %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)))
                .description("Descrizione e-service")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .isConsumerDelegable(isConsumerDelegable)
                .isClientAccessDelegable(isClientAccessDelegable)
                .personalData(false);
        EServiceSeed eServiceSeed = merge(defaultEserviceSeed, partialEserviceSeed);

        httpCallExecutor.performCall(() -> eServiceClient.createEService(eServiceSeed));
        assertValidResponse();
        UUID eserviceId = ((CreatedEServiceDescriptor) httpCallExecutor.getResponse()).getId();
        UUID descriptorId = ((CreatedEServiceDescriptor) httpCallExecutor.getResponse()).getDescriptorId();

        HttpStatus eServiceDetailsStatus = pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> producerClient.getProducerEServiceDetails(eserviceId)),
                status -> {
                    if(status.isError()) return false;

                    ProducerEServiceDetails eServiceDetails = (ProducerEServiceDetails) httpCallExecutor.getResponse();
                    return eServiceDetails.getIsConsumerDelegable() != null && eServiceDetails.getIsConsumerDelegable().equals(isConsumerDelegable) &&
                            eServiceDetails.getIsClientAccessDelegable() != null && eServiceDetails.getIsClientAccessDelegable().equals(isClientAccessDelegable);
                },
                "Impossibile aggiornare i flag di delega dell'e-service"
        );

        if (eServiceDetailsStatus.is2xxSuccessful() && httpCallExecutor.getResponse() instanceof ProducerEServiceDetails eServiceDetails) {
            sharedStepsContext.getEServicesCommonContext().setIsConsumerDelegable(eServiceDetails.getIsConsumerDelegable());
            sharedStepsContext.getEServicesCommonContext().setIsClientAccessDelegable(eServiceDetails.getIsClientAccessDelegable());
        }

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> producerClient.getProducerEServiceDescriptor(eserviceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );

        updateDraftDescriptor(eserviceId, descriptorId, partialDescriptorSeed);
        return new EServiceDescriptor(eserviceId, descriptorId);
    }

    public EServiceDescriptor createEServiceAndDraftDescriptorWithCustomPersonalData(EServiceSeed partialEserviceSeed, UpdateEServiceDescriptorSeed partialDescriptorSeed, Boolean personalData) {
        EServiceSeed defaultEserviceSeed = new EServiceSeed()
                .name(String.format("e-service %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)))
                .description("Descrizione e-service")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .isConsumerDelegable(false)
                .isClientAccessDelegable(false);
        EServiceSeed eServiceSeed = merge(defaultEserviceSeed, partialEserviceSeed);
        eServiceSeed.setPersonalData(personalData);

        httpCallExecutor.performCall(() -> eServiceClient.createEService(eServiceSeed));
        assertValidResponse();
        UUID eserviceId = ((CreatedEServiceDescriptor) httpCallExecutor.getResponse()).getId();
        UUID descriptorId = ((CreatedEServiceDescriptor) httpCallExecutor.getResponse()).getDescriptorId();

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> producerClient.getProducerEServiceDescriptor(eserviceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );

        updateDraftDescriptor(eserviceId, descriptorId, partialDescriptorSeed);
        return new EServiceDescriptor(eserviceId, descriptorId);
    }

    public void updateDraftDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed partialDescriptorSeed) {
        ProducerEServiceDescriptor descriptor = producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId);

        DescriptorAttributesSeed descriptorAttributesSeed = new DescriptorAttributesSeed();
        descriptorAttributesSeed.setCertified(
                sharedStepsContext.getAttributeCommonContext().mapAttributes(descriptor.getAttributes().getCertified())
        );
        descriptorAttributesSeed.setDeclared(
                sharedStepsContext.getAttributeCommonContext().mapAttributes(descriptor.getAttributes().getDeclared())
        );
        descriptorAttributesSeed.setVerified(
                sharedStepsContext.getAttributeCommonContext().mapAttributes(descriptor.getAttributes().getVerified())
        );

        UpdateEServiceDescriptorSeed currentDescriptorSeed = new UpdateEServiceDescriptorSeed()
                .agreementApprovalPolicy(descriptor.getAgreementApprovalPolicy())
                .attributes(
                        descriptorAttributesSeed
                )
                .dailyCallsPerConsumer(descriptor.getDailyCallsPerConsumer())
                .dailyCallsTotal(descriptor.getDailyCallsTotal())
                .audience(descriptor.getAudience())
                .voucherLifespan(descriptor.getVoucherLifespan());

        UpdateEServiceDescriptorSeed descriptorSeed = mergeDescriptorSeed(currentDescriptorSeed, partialDescriptorSeed)
            .audience(List.of("pagopa.it"));

        httpCallExecutor.performCall(() -> eServiceClient.updateDraftDescriptor(eServiceId, descriptorId, descriptorSeed));
        assertValidResponse();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("Error while sleeping: {}", e.getMessage());
        }
    }

    public void updateTemplateInstanceDraftDescriptor(UUID eServiceId, UUID descriptorId) {
        updateTemplateInstanceDraftDescriptor(eServiceId, descriptorId, false);
    }

    public void updateTemplateInstanceDraftDescriptor(UUID eServiceId, UUID descriptorId, boolean isAsync) {
        UpdateEServiceDescriptorTemplateInstanceSeed seed = new UpdateEServiceDescriptorTemplateInstanceSeed()
            .dailyCallsPerConsumer(10)
            .dailyCallsTotal(100)
            .addAudienceItem("some audience item")
            .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC);

        if (isAsync) {
            AsyncExchangePropertiesInstanceSeed asyncSeed = new AsyncExchangePropertiesInstanceSeed();
            asyncSeed.setResponseTime(100);
            asyncSeed.setResourceAvailableTime(100);
            asyncSeed.setMaxResultSet(100);
            seed.setAsyncExchangeProperties(asyncSeed);
        }

        httpCallExecutor.performCall(() -> eServiceClient.updateDraftDescriptorTemplateInstanceWithHttpInfo(eServiceId, descriptorId, seed));
        assertValidResponse();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("Error while sleeping: {}", e.getMessage());
        }
    }

    public MutateDescriptorResult bringDescriptorToGivenState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState descriptorState, boolean withDocument) {
        return bringDescriptorToGivenState(eServiceId, descriptorId, descriptorState, withDocument ? 1 : 0, null, null, false);
    }

    public MutateDescriptorResult bringDescriptorToGivenState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState descriptorState, boolean withDocument, boolean addCallbackInterface) {
        return bringDescriptorToGivenState(eServiceId, descriptorId, descriptorState, withDocument ? 1 : 0, null, null, addCallbackInterface);
    }

    public MutateDescriptorResult bringDescriptorToGivenState(
        UUID eServiceId,
        UUID descriptorId,
        EServiceDescriptorState descriptorState,
        int documents,
        @Nullable String documentNamePrefix,
        @Nullable String documentPrettyNamePrefix,
        @Nullable Boolean addCallbackInterface
    ) {
        MutateDescriptorResult.MutateDescriptorResultBuilder resultBuilder = MutateDescriptorResult.builder();

        // 1 add document to descriptor
        String namePrefix = requireNonNullElse(documentNamePrefix, "Document QA test name");
        String prettyNamePrefix = requireNonNullElse(documentPrettyNamePrefix, "Document QA test pretty name");
        UUID uuid = UUID.randomUUID();
        List<DocumentMetadata> documentsMetadata = addDocumentsToResource(uuid, documents, namePrefix, prettyNamePrefix,
            (prettyName, resource) -> {
                UUID docId = addDocumentToDescriptor(eServiceId, descriptorId, prettyName,
                    resource);

                // 08/09/2025: si è osservato che aggiunte in rapida successione possono generare
                // risposte 500, si aggiunge un delay per scongiurarlo
                delayService.delay();

                return docId;
            }
        ).stream().map(Document::getMetadata).toList();

        resultBuilder.descriptorId(descriptorId);
        resultBuilder.documentsMetadata(documentsMetadata);

        if (descriptorState == EServiceDescriptorState.DRAFT) return resultBuilder.build();

        // 2. Add interface to descriptor
        UUID interfaceId = addInterfaceToDescriptor(eServiceId, descriptorId);
        resultBuilder.interfaceId(interfaceId);

        // 2.1. Add callback interface to descriptor
        if (addCallbackInterface != null && addCallbackInterface) {
            UUID callbackInterfaceId = addCallbackInterfaceToDescriptor(eServiceId, descriptorId);
            resultBuilder.callbackInterfaceId(callbackInterfaceId);
        }

        // 3. Publish Descriptor
        publishDescriptor(eServiceId, descriptorId);
        if (descriptorState == EServiceDescriptorState.PUBLISHED) return resultBuilder.build();

        // 4. Suspend Descriptor
        if (descriptorState == EServiceDescriptorState.SUSPENDED) {
            suspendDescriptor(eServiceId, descriptorId);
            return resultBuilder.build();
        }

        if (descriptorState == EServiceDescriptorState.DEPRECATED) {
            // Optional. Create an agreement
            UUID agreementId = createAndCheckAgreement(eServiceId, descriptorId);
            submitAgreement(agreementId, AgreementState.ACTIVE);
        }

        // Create another DRAFT descriptor
        UUID secondDescriptorId = createNextDraftDescriptor(eServiceId);

        // Add interface to secondDescriptor
        addInterfaceToDescriptor(eServiceId, secondDescriptorId);

        // Add callback interface to secondDescriptor
        if (addCallbackInterface != null && addCallbackInterface) {
            addCallbackInterfaceToDescriptor(eServiceId, secondDescriptorId);
        }

        // Publish secondDescriptor
        publishDescriptor(eServiceId, secondDescriptorId);

        // Check until the first descriptor is in desired state
        pollingService.makePolling(
            () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
            res -> res.getState() == descriptorState,
            ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
        return resultBuilder.build();
    }

    /* Tentativo di generalizzare l'upload di documenti. Al momento funziona con e-service e
     * e-services template. */
    public List<Document> addDocumentsToResource(UUID uuid, int documentsQt,
        String namePrefix, String prettyNamePrefix, BiFunction<String, Resource, UUID> documentUploader) {
        List<Document> documents = new ArrayList<>();
        for(int i = 0; i < documentsQt; i++) {
            delayService.delayForSeconds(1);
            String documentContent = """
                Random document QA test - %s - %d""".formatted(uuid, i);
            int documentIndex = i + 1;
            Resource tempFileResource = blobFileCreator.createBlobTempFileWithExtension(
                namePrefix + documentIndex + " - ", "txt", documentContent.getBytes());
            String prettyName = prettyNamePrefix + " - " + documentIndex;

            UUID documentId = documentUploader.apply(prettyName, tempFileResource);
            DocumentMetadata metadata = DocumentMetadata.builder()
                .id(documentId)
                .name(tempFileResource.getFilename())
                .prettyName(prettyName)
                .createdAt(OffsetDateTime.now())
                .build();
            documents.add(Document.of(metadata, tempFileResource));
        }

        return documents;
    }

    public Map<String, Object> bringTemplateInstanceDescriptorToGivenState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState descriptorState, boolean withDocument) {
        // 1 add document to descriptor
        UUID documentId = null;
        Map<String, Object> result = new HashMap<>();
        if (withDocument) documentId = addDocumentToDescriptor(eServiceId, descriptorId, null);
        result.put("descriptorId", descriptorId);
        result.put("documentId", documentId);

        if (descriptorState == EServiceDescriptorState.DRAFT) return result;

        // 2. Add interface to descriptor
        interpolateInterfaceToDescriptor(eServiceId, descriptorId);

        // 3. Publish Descriptor
        publishTemplateInstanceDescriptor(eServiceId, descriptorId);
        if (descriptorState == EServiceDescriptorState.PUBLISHED) return result;

        // 4. Suspend Descriptor
        if (descriptorState == EServiceDescriptorState.SUSPENDED) {
            suspendDescriptor(eServiceId, descriptorId);
            return result;
        }

        if (descriptorState == EServiceDescriptorState.DEPRECATED) {
            // Optional. Create an agreement
            UUID agreementId = createAndCheckAgreement(eServiceId, descriptorId);
            submitAgreement(agreementId, AgreementState.ACTIVE);
        }

        // Create another DRAFT descriptor
        UUID secondDescriptorId = createNextDraftDescriptor(eServiceId);

        // Add interface to secondDescriptor
        interpolateInterfaceToDescriptor(eServiceId, secondDescriptorId);

        // Publish secondDescriptor
        publishTemplateInstanceDescriptor(eServiceId, secondDescriptorId);

        // Check until the first descriptor is in desired state
        pollingService.makePolling(
            () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
            res -> res.getState() == descriptorState,
            "There was an error while retrieving the producer e-service descriptor"
        );
        return result;
    }

    public UUID addDocumentToDescriptor(UUID eServiceId, UUID descriptorId, String name) {
        String prettyName = (name == null) ? String.format("Documento_test_qa-%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)) : name;
        Resource resource;
        if (prettyName.equals("test 2")) {
            resource = blobFileCreator.createBlobFile("src/main/resources/interface1.yaml", "documento-test2-qa.pdf");
        } else {
            resource = blobFileCreator.createBlobFile("src/main/resources/origin-interface.yaml", "documento-test-qa.pdf");
        }
        return addDocumentToDescriptor(eServiceId, descriptorId, name, resource);
    }

    public UUID addDocumentToDescriptor(UUID eServiceId, UUID descriptorId, String name, Resource resource) {
        String prettyName = (name == null) ? String.format("Documento_test_qa-%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)) : name;

        httpCallExecutor.performCall(() -> eServiceClient.createEServiceDocument(eServiceId,
            descriptorId, "DOCUMENT", prettyName, resource));
        assertValidResponse();
        UUID documentId = ((CreatedResource) httpCallExecutor.getResponse()).getId();

        pollingService.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
                res -> res.getDocs().stream().anyMatch(doc -> doc.getPrettyName().equals(prettyName)),
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
        return documentId;
    }

    public UUID addInterfaceToDescriptor(UUID eServiceId, UUID descriptorId) {
        Resource resource = blobFileCreator.createBlobFile("src/main/resources/origin-interface.yaml", "interface.yaml");
        httpCallExecutor.performCall(() -> eServiceClient.createEServiceDocument(eServiceId, descriptorId, "INTERFACE", "Interfaccia", resource));
        assertValidResponse();

        pollingService.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
                res -> res.getInterface() != null,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );

        return ((CreatedResource) httpCallExecutor.getResponse()).getId();
    }

    public UUID addCallbackInterfaceToDescriptor(UUID eServiceId, UUID descriptorId) {
        Resource resource = blobFileCreator.createBlobFile("src/main/resources/origin-interface.yaml", "interface.yaml");
        httpCallExecutor.performCall(() -> eServiceClient.createEServiceDocument(eServiceId, descriptorId, "ASYNC_EXCHANGE_CALLBACK_INTERFACE", "Interfaccia Callback", resource));
        assertValidResponse();

        pollingService.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
                res -> res.getInterface() != null,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );

        return ((CreatedResource) httpCallExecutor.getResponse()).getId();
    }

    public void interpolateInterfaceToDescriptor(UUID eServiceId, UUID descriptorId) {
        TemplateInstanceInterfaceServerUrlSeed serverUrl =
            new TemplateInstanceInterfaceServerUrlSeed().url(URI.create("http://www.some.url.it"));

        TemplateInstanceInterfaceRESTSeed seed = new TemplateInstanceInterfaceRESTSeed()
            .contactName("Some contact name")
            .contactEmail("some@contact-email.it")
            .addServerUrlsItem(serverUrl);
        httpCallExecutor.performCall(() -> eServiceClient.addEServiceTemplateInstanceInterfaceRestWithHttpInfo(eServiceId, descriptorId, seed));
        assertValidResponse();

        pollingService.makePolling(
            () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
            res -> res.getInterface() != null,
            "There was an error while retrieving the producer e-service descriptor"
        );
    }

    public void publishDescriptor(UUID eServiceId, UUID descriptorId) {
        updateDraftDescriptor(eServiceId, descriptorId,
            new UpdateEServiceDescriptorSeed().audience(List.of("pagopa.it")));
        httpCallExecutor.performCall(
            () -> eServiceClient.publishDescriptor(
                eServiceId, descriptorId));
        assertValidResponse();
        pollingService.makePolling(
            () -> producerClient.getProducerEServiceDescriptor(
                eServiceId, descriptorId),
            res -> res.getState() == EServiceDescriptorState.PUBLISHED,
            ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
    }

    public void publishTemplateInstanceDescriptor(UUID eServiceId, UUID descriptorId) {
        updateTemplateInstanceDraftDescriptor(eServiceId, descriptorId);
        httpCallExecutor.performCall(() -> eServiceClient.publishDescriptor(eServiceId, descriptorId));
        assertValidResponse();
        pollingService.makePolling(
            () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
            res -> res.getState() == EServiceDescriptorState.PUBLISHED,
            ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
    }

    public CreatedResource approveDelegatedEServiceDescriptor(UUID eServiceId, UUID descriptorId) {
        httpCallExecutor.performCall(() -> eServiceClient.approveDelegatedEServiceDescriptor(eServiceId, descriptorId));
        assertValidResponse();
        pollingService.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
                res -> res.getState() == PUBLISHED,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
        return (CreatedResource) httpCallExecutor.getResponse();
    }

    public void suspendDescriptor(UUID eServiceId, UUID descriptorId) {
        httpCallExecutor.performCall(() -> eServiceClient.suspendDescriptor(eServiceId, descriptorId));
        assertValidResponse();
        pollingService.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
                res -> res.getState() == EServiceDescriptorState.SUSPENDED,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
    }

    public UUID createNextDraftDescriptor(UUID eServiceId) {
        httpCallExecutor.performCall(() -> eServiceClient.createDescriptor(eServiceId));
        assertValidResponse();
        UUID descriptorId = ((CreatedResource) httpCallExecutor.getResponse()).getId();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
        return descriptorId;
    }

    public RiskAnalysis getRiskAnalysis(String tenantType, boolean completed) {
        String templateType = tenantType.startsWith("PA") ? "PA" : "Privato/GSP";
        RiskAnalysisDataFromJson.RiskAnalysisTemplate riskAnalysisTemplate = riskAnalysisDataInitializer.getRiskAnalysisData().get(templateType);
        RiskAnalysisDataFromJson.RiskAnalysisAttributes riskAnalysisAttributes = (completed) ? riskAnalysisTemplate.getCompleted() : riskAnalysisTemplate.getUncompleted();
        httpCallExecutor.performCall(purposeApiClient::retrieveLatestRiskAnalysisConfiguration);
        assertValidResponse();
        String version = ((RiskAnalysisFormConfig) httpCallExecutor.getResponse()).getVersion();
        return new RiskAnalysis(String.format("finalità_test_%d", new Random().nextInt()), new RiskAnalysisFormSeed().version(version).answers(riskAnalysisAttributes.toMap()));
    }

    public CreatedEserviceVersion createPurposeWithGivenState(int testSeed, EServiceMode eServiceMode, PurposeVersionState purposeState, TEServiceMode teServiceMode) {
        return createPurposeWithGivenState(testSeed, eServiceMode, purposeState, teServiceMode, null);
    }

    public CreatedEserviceVersion createPurposeWithGivenState(int testSeed, EServiceMode eServiceMode, PurposeVersionState purposeState, TEServiceMode teServiceMode, DelegationRef delegationRef) {
        // 1. Define default values
        String title = String.format("purpose title - QA - %d - %d", testSeed, ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        String description = "description of the purpose - QA";
        boolean isFreeOfCharge = true;
        String freeOfChargeReason = "free of charge - QA";
        int dailyCalls = purposeState == PurposeVersionState.WAITING_FOR_APPROVAL ? 51 : 1;

        // 1. Check which mode the eservice is and call the correct endpoint
        if (eServiceMode == RECEIVE) {
            // For RECEIVE mode, build a PurposeEServiceSeed
            PurposeEServiceSeed purposeEServiceSeed = new PurposeEServiceSeed();
            purposeEServiceSeed.setTitle(title);
            purposeEServiceSeed.setDescription(description);
            purposeEServiceSeed.setIsFreeOfCharge(isFreeOfCharge);
            purposeEServiceSeed.setFreeOfChargeReason(freeOfChargeReason);
            purposeEServiceSeed.setDailyCalls(dailyCalls);

            // Add data from the payload
            purposeEServiceSeed.setEserviceId(teServiceMode.getEserviceId());
            purposeEServiceSeed.setConsumerId(teServiceMode.getConsumerId());
            purposeEServiceSeed.setRiskAnalysisId(teServiceMode.getRiskAnalysisId());
            httpCallExecutor.performCall(() -> purposeApiClient.createPurposeForReceiveEservice(purposeEServiceSeed));
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                sharedStepsContext.getPurposeCommonContext().addCreatedPurposeEService(purposeEServiceSeed);
            }
        } else {
            // For modes other than RECEIVE, build a PurposeSeed
            PurposeSeed purposeSeed = new PurposeSeed();
            purposeSeed.setTitle(teServiceMode.getTitle() != null ? teServiceMode.getTitle() : title);
            purposeSeed.setDescription(description);
            purposeSeed.setIsFreeOfCharge(isFreeOfCharge);
            purposeSeed.setFreeOfChargeReason(freeOfChargeReason);
            purposeSeed.setDailyCalls(dailyCalls);

            // Add data from the payload
            purposeSeed.setEserviceId(teServiceMode.getEserviceId());
            purposeSeed.setConsumerId(teServiceMode.getConsumerId());
            purposeSeed.setRiskAnalysisForm(teServiceMode.getRiskAnalysisFormSeed());
            httpCallExecutor.performCall(() -> purposeApiClient.createPurpose(purposeSeed));
            if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
                sharedStepsContext.getPurposeCommonContext().addCreatedPurpose(purposeSeed);
            }
        }
        assertValidResponse();
        UUID purposeId = ((CreatedResource) httpCallExecutor.getResponse()).getId();
        AtomicReference<UUID> currentVersion = new AtomicReference<>();
        AtomicReference<UUID> waitingForApprovalVersionId = new AtomicReference<>();

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeApiClient.getPurpose(purposeId)),
                res -> {
                    if (res == HttpStatus.OK) {
                        UUID id = Optional.ofNullable((Purpose) httpCallExecutor.getResponse())
                                .map(Purpose::getCurrentVersion)
                                .map(PurposeVersion::getId)
                                .orElse(null);
                        currentVersion.set(id);
                        return true;
                    }
                    return false;
                },
                ERROR_RETRIEVING_PURPOSE
        );

        if (purposeState == PurposeVersionState.DRAFT) {
            sharedStepsContext.getPurposeCommonContext().setPurposeId(String.valueOf(purposeId));
            sharedStepsContext.getPurposeCommonContext().setVersionId(String.valueOf(currentVersion));
            return CreatedEserviceVersion.builder()
                    .purposeId(purposeId)
                    .currentVersionId(currentVersion.get())
                    .build();
        }
        // 2. Activate the purpose version
        httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(purposeId, currentVersion.get(), delegationRef));
        assertValidResponse();

        // 3. If the state required is WAITING_FOR_APPROVAL, we need to wait until the purpose version is in that state and return the purposeId
        if (purposeState == PurposeVersionState.WAITING_FOR_APPROVAL) {
            pollingService.makePolling(
                    () -> purposeApiClient.getPurpose(purposeId),
                    res -> {
                        if (Optional.ofNullable(res.getWaitingForApprovalVersion()).map(PurposeVersion::getId).isPresent()) {
                            waitingForApprovalVersionId.set(res.getWaitingForApprovalVersion().getId());
                        }
                        return PurposeVersionState.WAITING_FOR_APPROVAL == Optional.ofNullable(res.getWaitingForApprovalVersion())
                                .map(PurposeVersion::getState).orElse(null);
                    },
                    ERROR_RETRIEVING_PURPOSE
            );
            sharedStepsContext.getPurposeCommonContext().setPurposeId(String.valueOf(purposeId));
            sharedStepsContext.getPurposeCommonContext().setWaitingForApprovalVersionId(String.valueOf(waitingForApprovalVersionId.get()));
            return CreatedEserviceVersion.builder()
                    .purposeId(purposeId)
                    .waitingForApprovalVersionId(waitingForApprovalVersionId.get())
                    .build();
        }

        pollingService.makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                res -> {
                    if (PurposeVersionState.ACTIVE == Optional.ofNullable(res.getCurrentVersion()).map(PurposeVersion::getState).orElse(null)) {
                        currentVersion.set(res.getCurrentVersion().getId());
                        return true;
                    }
                    return false;
                },
                ERROR_RETRIEVING_PURPOSE
        );

        // 4. If the state required is SUSPENDED call the endpoint to suspend the purpose version
        if (purposeState == PurposeVersionState.SUSPENDED) {
            httpCallExecutor.performCall(() -> purposeApiClient.suspendPurposeVersion(purposeId, currentVersion.get()));
            assertValidResponse();
            pollingService.makePolling(
                    () -> purposeApiClient.getPurpose(purposeId),
                    res -> {
                        if (PurposeVersionState.SUSPENDED == Optional.ofNullable(res.getCurrentVersion()).map(PurposeVersion::getState).orElse(null)) {
                            currentVersion.set(res.getCurrentVersion().getId());
                            return true;
                        }
                        return false;
                    },
                    "There was an error while suspending the purpose!"
            );
        }
        // 5. If the state required is ARCHIVED call the endpoint to archive the purpose version
        if (purposeState == PurposeVersionState.ARCHIVED) {
            httpCallExecutor.performCall(() -> purposeApiClient.archivePurposeVersion(purposeId, currentVersion.get()));
            assertValidResponse();
            pollingService.makePolling(
                    () -> purposeApiClient.getPurpose(purposeId),
                    res -> {
                        if (PurposeVersionState.ARCHIVED == Optional.ofNullable(res.getCurrentVersion()).map(PurposeVersion::getState).orElse(null)) {
                            currentVersion.set(res.getCurrentVersion().getId());
                            return true;
                        }
                        return false;
                    },
                    "There was an error while archiving the purpose!"
            );
        }
        sharedStepsContext.getPurposeCommonContext().setPurposeId(String.valueOf(purposeId));
        sharedStepsContext.getPurposeCommonContext().setVersionId(String.valueOf(currentVersion.get()));

        return CreatedEserviceVersion.builder()
                .purposeId(purposeId)
                .currentVersionId(currentVersion.get())
                .build();
    }

    public CreatedEserviceVersion createNewPurposeVersion(UUID purposeId, PurposeVersionSeed purposeVersionSeed) {
        httpCallExecutor.performCall(
                () -> purposeApiClient.createPurposeVersion(
                        UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId()), purposeVersionSeed)
        );
        AtomicReference<UUID> currentVersionId = new AtomicReference<>();
        AtomicReference<UUID> waitingForApprovalVersionId = new AtomicReference<>();

        assertValidResponse();
        boolean shouldWaitForApproval = purposeVersionSeed.getDailyCalls() > 50;

        pollingService.makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                res -> {
                    currentVersionId.set(res.getCurrentVersion().getId());
                    if (shouldWaitForApproval) {
                        waitingForApprovalVersionId.set(res.getWaitingForApprovalVersion().getId());
                        return res.getWaitingForApprovalVersion().getState().getValue().equals("WAITING_FOR_APPROVAL");
                    }
                    return res.getCurrentVersion().getDailyCalls() == purposeVersionSeed.getDailyCalls();
                },
                "Ther was an error while creating the new purpose version!"
        );
        return CreatedEserviceVersion.builder()
                .purposeId(purposeId)
                .currentVersionId(currentVersionId.get())
                .waitingForApprovalVersionId(waitingForApprovalVersionId.get())
                .build();
    }

    public void rejectPurposeVersion(UUID purposeId, UUID versionId) {
        httpCallExecutor.performCall(() -> purposeApiClient.rejectPurposeVersion(purposeId, versionId, new RejectPurposeVersionPayload().rejectionReason("Testing QA purposes")));
        assertValidResponse();

        pollingService.makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                res -> {
                    Optional<PurposeVersionState> versionState = res.getVersions().stream().filter(v -> v.getId().equals(versionId)).map(PurposeVersion::getState).findFirst();
                    return versionState.isPresent() && versionState.get().equals(PurposeVersionState.REJECTED);
                },
                ERROR_RETRIEVING_PURPOSE
        );
    }

    public void rejectAgreement(UUID agreementId) {
        httpCallExecutor.performCall(() -> agreementClient.rejectAgreement(agreementId,
                new AgreementRejectionPayload().reason("Agreement rejected during QA")));
        assertValidResponse();

        pollingService.makePolling(
                () -> agreementClient.getAgreementById(agreementId),
                res -> res.getState().equals(AgreementState.REJECTED),
                "There was an error while rejecting the agreement"
        );
    }

    public void approveAgreement(UUID agreementId, DelegationRef delegationRef) {
        httpCallExecutor.performCall(() -> agreementClient.approveAgreement(agreementId, delegationRef));
        assertValidResponse();
        pollingService.makePolling(
            () -> agreementClient.getAgreementById(agreementId),
            res -> res.getState() == AgreementState.ACTIVE,
            "There was an error while approving the agreement"
        );
    }

    public void unsuspendAgreement(UUID agreementId, ClientType reactivatedBy, DelegationRef delegationRef) {
        httpCallExecutor.performCall(() -> agreementClient.unsuspendAgreement(agreementId, delegationRef));
        assertValidResponse();
        pollingService.makePolling(
            () -> agreementClient.getAgreementById(agreementId),
            res -> {
                AgreementState state = res.getState();
                boolean isActive = (state == AgreementState.ACTIVE);
                if (reactivatedBy != null) {
                    isActive = (reactivatedBy == ClientType.CONSUMER)
                        ? isNotTrue(res.getSuspendedByConsumer())
                        : isNotTrue(res.getSuspendedByProducer());
                }
                return isActive;
            },
            "There was an error while unsuspending the agreement"
        );
    }

    public void revokeCertifiedAttributeToTenant(UUID tenantId, UUID attributeId) {
        httpCallExecutor.performCall(() -> tenantsApi.revokeCertifiedAttribute(tenantId, attributeId));
        assertValidResponse();
        pollingService.makePolling(
                () -> tenantsApi.getCertifiedAttributes(tenantId),
                res -> res.getAttributes().stream().anyMatch(attr -> attr.getId().equals(attributeId) && attr.getRevocationTimestamp() != null),
                "There was an error while revoking the certified attribute!"
        );
    }

    public void revokeVerifiedAttributeToTenant(UUID tenantId, UUID attributeId, UUID agreementId, UUID revokerId) {
        httpCallExecutor.performCall(() -> tenantsApi.revokeVerifiedAttribute(tenantId, attributeId, agreementId));
        assertValidResponse();
        pollingService.makePolling(
            () -> tenantsApi.getVerifiedAttributes(tenantId),
            res -> res.getAttributes().stream().anyMatch(
                attr -> attr.getId().equals(attributeId) &&
                                    attr.getRevokedBy().stream().anyMatch(tenantRevoker -> tenantRevoker.getId().equals(revokerId))),
            "There was an error while revoking the certified attribute!"
        );
    }

    public void revokeDeclaredAttributeToTenant(UUID tenantId, UUID attributeId) {
        httpCallExecutor.performCall(() -> tenantsApi.revokeDeclaredAttribute(attributeId));
        assertValidResponse();
        pollingService.makePolling(
            () -> tenantsApi.getDeclaredAttributes(tenantId),
            res -> res.getAttributes().stream().anyMatch(attr -> attr.getId().equals(attributeId) && attr.getRevocationTimestamp() != null),
            "There was an error while revoking the certified attribute!"
        );
    }

    public void upgradeAgreement(UUID agreementId) {
        httpCallExecutor.performCall(() -> agreementClient.upgradeAgreement(agreementId));
        assertValidResponse();
        Agreement response = (Agreement) httpCallExecutor.getResponse();
        UUID newAgreementId = response.getId();

        pollingService.makePolling(
            () -> agreementClient.getAgreementById(newAgreementId),
            Objects::nonNull,
            ERROR_RETRIEVING_AGREEMENT
        );

        sharedStepsContext.setAgreementId(newAgreementId);
    }

    public void deleteClientKeyById(UUID clientId, String keyId) {
        httpCallExecutor.performCall(() -> authorizationClient.deleteClientKeyById(clientId, keyId));
        assertValidResponse();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClientKeyById(clientId, keyId)),
                res -> res == HttpStatus.NOT_FOUND,
                "There was an error while deleting the client key!"
        );
    }

    public void deletePurposeFromClient(UUID clientId, UUID purposeId) {
        httpCallExecutor.performCall(() -> authorizationClient.removeClientPurpose(clientId, purposeId));
        assertValidResponse();

        pollingService.makePolling(
                () -> authorizationClient.getClient(clientId),
                res -> !res.getPurposes().stream().anyMatch(p -> p.getPurposeId().equals(purposeId)),
                "There was an error while deleting the client purpose!"
        );
    }

    public void deleteClient(UUID clientId) {
        httpCallExecutor.performCall(() -> authorizationClient.deleteClient(clientId));
        assertValidResponse();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClient(clientId)),
                res -> res == HttpStatus.NOT_FOUND,
                "There was an error while deleting the client!"
        );
    }

    public void activateDescriptor(UUID eServiceId, UUID descriptorId) {
        httpCallExecutor.performCall(() -> eServiceClient.activateDescriptor(eServiceId, descriptorId));
        assertValidResponse();
        pollingService.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
                res -> res.getState() == EServiceDescriptorState.PUBLISHED || res.getState() == EServiceDescriptorState.DEPRECATED,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
    }

    public void waitRiskAnalysisDocument() {
        pollingService.makePolling(
                () -> purposeApiClient.getPurpose(UUID.fromString(sharedStepsContext.getPurposeCommonContext().getPurposeId())),
                purpose -> purpose.getIsDocumentReady(),
                "Risk analysis document is not ready",
                30,
                30_000
        );
    }

    public UUID addRiskAnalysisToEService(UUID eServiceId, EServiceRiskAnalysisSeed eServiceRiskAnalysisSeed) {
        httpCallExecutor.performCall(() -> eServiceClient.addRiskAnalysisToEService(eServiceId, eServiceRiskAnalysisSeed));
        assertValidResponse();

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> producerClient.getProducerEServiceDetails(eServiceId)
                ),
                res -> Optional.ofNullable((ProducerEServiceDetails) httpCallExecutor.getResponse())
                        .map(ProducerEServiceDetails::getRiskAnalysis)
                        .filter(list -> !list.isEmpty())
                        .map(list -> list.get(0))
                        .map(EServiceRiskAnalysis::getId)
                        .isPresent(),
                "Risk analysis not found!"
        );
        return ((ProducerEServiceDetails) httpCallExecutor.getResponse()).getRiskAnalysis().get(0).getId();
    }

    public void addEmailToTenant(UUID tenantId, MailSeed mailSeed) {
        httpCallExecutor.performCall(
                () -> tenantsApi.addTenantMailWithHttpInfo(tenantId, mailSeed.kind(MailKind.CONTACT_EMAIL)),
                ResponseEntity::getStatusCode);
        assertValidResponse();

        pollingService.makePolling(
                () -> tenantsApi.getTenant(tenantId),
                res -> res.getContactMail().getAddress().equals(mailSeed.getAddress()),
                "The desired email was not added to tenant!"
        );
    }

    private void assertValidResponse() {
        Assertions.assertFalse(httpCallExecutor.getResponseStatus().isError(),
                "Something went wrong: " + httpCallExecutor.getResponseStatus().getReasonPhrase());
    }

    private ClientSeed merge(ClientSeed defaultClientSeed, ClientSeed partialClientSeed) {
        ClientSeed clientSeed = new ClientSeed();
        clientSeed.setMembers(useOrDefault(partialClientSeed.getMembers(), defaultClientSeed.getMembers()));
        clientSeed.setDescription(useOrDefault(partialClientSeed.getDescription(), defaultClientSeed.getDescription()));
        clientSeed.setName(useOrDefault(partialClientSeed.getName(), defaultClientSeed.getName()));
        return clientSeed;
    }

    private EServiceSeed merge(EServiceSeed defaultClientSeed, EServiceSeed partialClientSeed) {
        EServiceSeed eServiceSeed = new EServiceSeed();
        eServiceSeed.setName(useOrDefault(partialClientSeed.getName(), defaultClientSeed.getName()));
        eServiceSeed.setDescription(useOrDefault(partialClientSeed.getDescription(), defaultClientSeed.getDescription()));
        eServiceSeed.setTechnology(useOrDefault(partialClientSeed.getTechnology(), defaultClientSeed.getTechnology()));
        eServiceSeed.setMode(useOrDefault(partialClientSeed.getMode(), defaultClientSeed.getMode()));
        eServiceSeed.setIsConsumerDelegable(useOrDefault(partialClientSeed.getIsConsumerDelegable(), defaultClientSeed.getIsConsumerDelegable()));
        eServiceSeed.setIsClientAccessDelegable(useOrDefault(partialClientSeed.getIsClientAccessDelegable(), defaultClientSeed.getIsClientAccessDelegable()));
        eServiceSeed.setPersonalData(useOrDefault(partialClientSeed.getPersonalData(), defaultClientSeed.getPersonalData()));
        eServiceSeed.setAsyncExchange(useOrDefault(partialClientSeed.getAsyncExchange(), defaultClientSeed.getAsyncExchange()));
        return eServiceSeed;
    }

    private UpdateEServiceDescriptorSeed mergeDescriptorSeed(UpdateEServiceDescriptorSeed defaultDescriptorSeed, UpdateEServiceDescriptorSeed partialDescriptorSeed) {
        UpdateEServiceDescriptorSeed descriptorSeed = new UpdateEServiceDescriptorSeed();
        descriptorSeed.setAttributes(useOrDefault(partialDescriptorSeed.getAttributes(), defaultDescriptorSeed.getAttributes()));
        descriptorSeed.setDescription(useOrDefault(partialDescriptorSeed.getDescription(), defaultDescriptorSeed.getDescription()));
        descriptorSeed.setAudience(useOrDefault(partialDescriptorSeed.getAudience(), defaultDescriptorSeed.getAudience()));
        descriptorSeed.setVoucherLifespan(useOrDefault(partialDescriptorSeed.getVoucherLifespan(), defaultDescriptorSeed.getVoucherLifespan()));
        descriptorSeed.setAgreementApprovalPolicy(useOrDefault(partialDescriptorSeed.getAgreementApprovalPolicy(), defaultDescriptorSeed.getAgreementApprovalPolicy()));
        descriptorSeed.setDailyCallsTotal(useOrDefault(partialDescriptorSeed.getDailyCallsTotal(), defaultDescriptorSeed.getDailyCallsTotal()));
        descriptorSeed.setDailyCallsPerConsumer(useOrDefault(partialDescriptorSeed.getDailyCallsPerConsumer(), defaultDescriptorSeed.getDailyCallsPerConsumer()));
        descriptorSeed.setAsyncExchangeProperties(useOrDefault(partialDescriptorSeed.getAsyncExchangeProperties(), defaultDescriptorSeed.getAsyncExchangeProperties()));
        return descriptorSeed;
    }

    private <T> T useOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public void deletePurposeVersion(UUID purposeId, UUID waitingForApprovalVersionId) {
        httpCallExecutor.performCall(() -> this.purposeApiClient.deletePurposeVersion(purposeId, waitingForApprovalVersionId));
        assertValidResponse();
        pollingService.makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                res -> isNull(res) || res.getVersions().stream().noneMatch(v -> v.getId().equals(waitingForApprovalVersionId)),
                "There was an error while deleting the purpose version!"
        );
    }

    public void activatePurposeVersion(UUID purposeId, UUID waitingForApprovalVersionId) {
        activatePurposeVersion(purposeId, waitingForApprovalVersionId, null);
    }

    public void activatePurposeVersion(UUID purposeId, UUID waitingForApprovalVersionId, ClientType checkNotSuspendedBy) {
        httpCallExecutor.performCall(() -> this.purposeApiClient.activatePurposeVersion(purposeId, waitingForApprovalVersionId));
        assertValidResponse();
        pollingService.makePolling(
                () -> purposeApiClient.getPurpose(purposeId),
                res -> {
                    Optional<PurposeVersion> oVersionId = res.getVersions().stream()
                        .filter(v -> v.getId().equals(waitingForApprovalVersionId)).findAny();
                    boolean isActive = oVersionId.map(purposeVersion -> purposeVersion.getState()
                        .equals(PurposeVersionState.ACTIVE)).orElse(false);
                    if (isNull(checkNotSuspendedBy)) {
                        return isActive;
                    }
                    return switch (checkNotSuspendedBy) {
                        case CONSUMER -> Boolean.FALSE.equals(res.getSuspendedByConsumer());
                        case PRODUCER -> Boolean.FALSE.equals(res.getSuspendedByProducer());
                        default -> throw new IllegalArgumentException(
                            "Unexpected value: " + checkNotSuspendedBy);
                    };
                },
                "There was an error while activating the purpose version!"
        );
    }

    public void editClientAdmin(UUID clientId, ClientAdminConfig adminConfig) {
        this.mainDataPrepService.editClientAdmin(clientId, adminConfig);
    }
}
