package it.pagopa.pn.interop.cucumber.steps;

import static it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.RECEIVE;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.bff.model.Agreement;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementRejectionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementState;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CertifiedTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.CompactUser;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DeclaredTenantAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTechnology;
import it.pagopa.interop.generated.openapi.clients.bff.model.InlineObject4;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.MailKind;
import it.pagopa.interop.generated.openapi.clients.bff.model.MailSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDetails;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKey;
import it.pagopa.interop.generated.openapi.clients.bff.model.PublicKeys;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeEServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectPurposeVersionPayload;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.VerifiedTenantAttributeSeed;
import it.pagopa.interop.purpose.RiskAnalysisDataInitializer;
import it.pagopa.interop.purpose.domain.CreatedEserviceVersion;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.domain.RiskAnalysisDataFromJson;
import it.pagopa.interop.purpose.domain.TEServiceMode;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

import it.pagopa.pn.interop.cucumber.utility.BlobFileCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataPreparationService {
    private static final ClientSeed DEFAULT_CLIENT_SEED = new ClientSeed();
    private final IAuthorizationClient authorizationClient;
    private final IAgreementClient agreementClient;
    private final IAttributeApiClient attributeApiClient;
    private final ITenantsApi tenantsApi;
    private final IEServiceClient eServiceClient;
    private final IProducerClient producerClient;
    private final IPurposeApiClient purposeApiClient;
    private final PollingService pollingService;
    private final HttpCallExecutor httpCallExecutor;
    private final RiskAnalysisDataInitializer riskAnalysisDataInitializer;
    private final SharedStepsContext sharedStepsContext;
    private final CommonUtils commonUtils;
    private final BlobFileCreator blobFileCreator;
    public static final String ERROR_RETRIEVING_AGREEMENT = "There was an error while retrieving the agreement by ID!";
    public static final String ERROR_RETRIEVING_PRODUCER_DESCRIPTOR = "There was an error while retrieving the producer e-service descriptor";
    public static final String ERROR_RETRIEVING_PURPOSE = "There was an error while retrieving the purpose!";
    public static final String DESCRIPTION_TEST = "description_test";

    
    static {
        DEFAULT_CLIENT_SEED.setName(String.format("client %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
        DEFAULT_CLIENT_SEED.setDescription("Descrizione client");
        DEFAULT_CLIENT_SEED.setMembers(List.of());
    }

    public DataPreparationService(ClientTokenConfigurator clientTokenConfigurator,
                                  RiskAnalysisDataInitializer riskAnalysisDataInitializer,
                                  SharedStepsContext sharedStepsContext,
                                  BlobFileCreator blobFileCreator,
                                  CommonUtils commonUtils) {
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
    }

    public UUID createClient(String clientKind, ClientSeed partialClientSeed) {
        ClientSeed mergedClientSeed = merge(DEFAULT_CLIENT_SEED, partialClientSeed);
        if ("CONSUMER".equals(clientKind)) {
            httpCallExecutor.performCall(() -> authorizationClient.createConsumerClient(mergedClientSeed));
        } else {
            httpCallExecutor.performCall(() -> authorizationClient.createApiClient(mergedClientSeed));
        }
        assertValidResponse();
        UUID clientId = ((CreatedResource) httpCallExecutor.getResponse()).getId();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClient(clientId)),
                res -> res != HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
        );
        return clientId;
    }

    public void addMemberToClient(UUID clientId, UUID userId) {
        InlineObject4 inlineObject = new InlineObject4().addUserIdsItem(userId);
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.addUsersToClient(clientId, inlineObject)),
                res -> !res.is5xxServerError(),
                "Failed to add a user to the client!"
        );
        assertValidResponse();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClientUsers(clientId)),
                res -> Optional.ofNullable(httpCallExecutor.getResponse())
                        .map(obj -> (List<CompactUser>) obj)
                        .orElse(List.of())
                        .stream()
                        .anyMatch(user -> user.getUserId().equals(userId)),
                "Failed to retrieve the client users list!"
        );
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
                        isSuspended = purpose.getSuspendedByConsumer();
                    }
                    else {
                        isSuspended = purpose.getSuspendedByProducer();
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
        // agreement in state DRAFT
        UUID agreementId = createAndCheckAgreement(eServiceID, descriptorId, delegationId);
        if (doc != null) addConsumerDocumentToAgreement(agreementId, doc);
        return switch (agreementState) {
            case DRAFT -> agreementId;
            case PENDING, ACTIVE -> {
                submitAgreement(agreementId, agreementState);
                yield agreementId;
            }
            case SUSPENDED -> {
                submitAgreement(agreementId, AgreementState.ACTIVE);
                suspendAgreement(agreementId, ClientType.CONSUMER);
                yield agreementId;
            }
            case ARCHIVED -> {
                submitAgreement(agreementId, AgreementState.ACTIVE);
                suspendAgreement(agreementId, ClientType.CONSUMER);
                archiveAgreement(agreementId);
                yield agreementId;
            }
            default -> throw new IllegalArgumentException("Unsupported AgreementState: " + agreementState);
        };
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
        httpCallExecutor.performCall(() -> agreementClient.createAgreement(
            new AgreementPayload().eserviceId(eServiceID).descriptorId(descriptorId).delegationId(delegationId)));
        return httpCallExecutor.getClientResponse().is2xxSuccessful()
            ? Optional.of(((CreatedResource) httpCallExecutor.getResponse()).getId())
            : Optional.empty();
    }

    public UUID createAndCheckAgreement(UUID eServiceID, UUID descriptorId, UUID delegationId) {
        UUID agreementId = createAgreement(eServiceID, descriptorId, delegationId).orElseThrow(
            () -> new NoSuchElementException("Failed to create an agreement: result of agreement creation API is '%s'".formatted(httpCallExecutor.getClientResponse())));
        assertValidResponse();
        pollingService.makePolling(
            () ->  httpCallExecutor.performCall(() -> agreementClient.getAgreementById(agreementId)),
            res -> res != HttpStatus.NOT_FOUND,
            ERROR_RETRIEVING_AGREEMENT
        );
        return agreementId;
    }

    public void submitAgreement(UUID agreementId, AgreementState expectedState) {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> agreementClient.submitAgreement(agreementId, new AgreementSubmissionPayload())),
                res -> res.is2xxSuccessful(),
                "There was an error while submitting the agreement!"
        );

        assertValidResponse();
        pollingService.makePolling(
                () -> agreementClient.getAgreementById(agreementId),
                res -> res.getState() == expectedState,
                ERROR_RETRIEVING_AGREEMENT
        );
    }

    public void suspendAgreement(UUID agreementId, ClientType suspendedBy) {
        httpCallExecutor.performCall(() -> agreementClient.suspendAgreement(agreementId));
        assertValidResponse();
        pollingService.makePolling(
                () -> agreementClient.getAgreementById(agreementId),
                res -> isTrue(res.getState().equals(AgreementState.SUSPENDED)
                    && ClientType.PRODUCER.equals(suspendedBy) ? res.getSuspendedByProducer()
                    : res.getSuspendedByConsumer()),
                ERROR_RETRIEVING_AGREEMENT
        );

    }

    public void archiveAgreement(UUID agreementId) {
        httpCallExecutor.performCall(() -> agreementClient.archiveAgreement(agreementId));
        assertValidResponse();
        pollingService.makePolling(
                () -> agreementClient.getAgreementById(agreementId),
                res -> res.getState() == AgreementState.ARCHIVED,
                ERROR_RETRIEVING_AGREEMENT
        );
    }

    public void addConsumerDocumentToAgreement(UUID agreementId, File doc) {
        httpCallExecutor.performCall(
                () -> agreementClient.addAgreementConsumerDocument(agreementId, "documento-test-qa.pdf", "documento-test-qa", new FileSystemResource(doc)));
        pollingService.makePolling(
                () -> agreementClient.getAgreementById(agreementId),
                res -> !res.getConsumerDocuments().isEmpty(),
                ERROR_RETRIEVING_AGREEMENT
        );
    }

    public UUID createAttribute(AttributeKind attributeKind, String name) {
        String actualName = name == null ? String.format("new_attribute_%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)) : name;
        switch (attributeKind) {
            case CERTIFIED -> httpCallExecutor.performCall(() -> attributeApiClient.createCertifiedAttribute(new CertifiedAttributeSeed().description(DESCRIPTION_TEST).name(actualName)));
            case VERIFIED -> httpCallExecutor.performCall(() -> attributeApiClient.createVerifiedAttribute(new AttributeSeed().description(DESCRIPTION_TEST).name(actualName)));
            case DECLARED -> httpCallExecutor.performCall(() -> attributeApiClient.createDeclaredAttribute(new AttributeSeed().description(DESCRIPTION_TEST).name(actualName)));
            default -> throw new IllegalArgumentException("Invalid attributeKind: " + attributeKind);
        }
        assertValidResponse();

        pollingService.makePolling(
                () -> attributeApiClient.getAttributes(1, 0, List.of(attributeKind), actualName, null),
                res -> !res.getResults().isEmpty(),
                "There was an error while retrieving the attributes"
        );
        return ((Attribute) httpCallExecutor.getResponse()).getId();
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

    public void assignVerifiedAttributeToTenant(UUID tenantId, UUID verifierId, UUID attributeId, UUID agreementId, String expirationDate  ) {
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

    public EServiceDescriptor createEServiceAndDraftDescriptor(EServiceSeed partialEserviceSeed, UpdateEServiceDescriptorSeed partialDescriptorSeed) {
        EServiceSeed defaultEserviceSeed = new EServiceSeed()
                .name(String.format("e-service %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)))
                .description("Descrizione e-service")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .isConsumerDelegable(false)
                .isClientAccessDelegable(false);
        EServiceSeed eServiceSeed = merge(defaultEserviceSeed, partialEserviceSeed);

        httpCallExecutor.performCall(() -> eServiceClient.createEService(eServiceSeed));
        assertValidResponse();
        UUID eserviceId = ((CreatedEServiceDescriptor)httpCallExecutor.getResponse()).getId();
        UUID descriptorId = ((CreatedEServiceDescriptor)httpCallExecutor.getResponse()).getDescriptorId();

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
        UpdateEServiceDescriptorSeed currentDescriptorSeed = new UpdateEServiceDescriptorSeed()
                .agreementApprovalPolicy(descriptor.getAgreementApprovalPolicy())
                .attributes(
                        new DescriptorAttributesSeed()
                                .addCertifiedItem(descriptor.getAttributes().getCertified().stream()
                                        .flatMap(List::stream).
                                        map(attr -> new DescriptorAttributeSeed()
                                                .id(attr.getId())
                                                .explicitAttributeVerification(attr.getExplicitAttributeVerification()))
                                        .toList())
                                .addDeclaredItem(descriptor.getAttributes().getDeclared().stream()
                                        .flatMap(List::stream).
                                        map(attr -> new DescriptorAttributeSeed()
                                                .id(attr.getId())
                                                .explicitAttributeVerification(attr.getExplicitAttributeVerification()))
                                        .toList())
                                .addVerifiedItem(descriptor.getAttributes().getVerified().stream()
                                        .flatMap(List::stream).
                                        map(attr -> new DescriptorAttributeSeed()
                                                .id(attr.getId())
                                                .explicitAttributeVerification(attr.getExplicitAttributeVerification()))
                                        .toList())
                )
                .dailyCallsPerConsumer(descriptor.getDailyCallsPerConsumer())
                .dailyCallsTotal(descriptor.getDailyCallsTotal())
                .audience(descriptor.getAudience())
                .voucherLifespan(descriptor.getVoucherLifespan());

        UpdateEServiceDescriptorSeed descriptorSeed = mergeDescriptorSeed(currentDescriptorSeed, partialDescriptorSeed)
                .dailyCallsPerConsumer(50).dailyCallsTotal(1000).audience(List.of("pagopa.it"));

        httpCallExecutor.performCall(() -> eServiceClient.updateDraftDescriptor(eServiceId, descriptorId, descriptorSeed));
        assertValidResponse();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("Error while sleeping: {}", e.getMessage());
        }
    }

    public Map<String, UUID> bringDescriptorToGivenState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState descriptorState, boolean withDocument) {
        // 1 add document to descriptor
        UUID documentId = null;
        Map<String, UUID> result = new HashMap<>();
        if (withDocument) documentId = addDocumentToDescriptor(eServiceId, descriptorId, null);
        result.put("descriptorId", descriptorId);
        result.put("documentId", documentId);

        if (descriptorState == EServiceDescriptorState.DRAFT) return result;

        // 2. Add interface to descriptor
        UUID interfaceId = addInterfaceToDescriptor(eServiceId, descriptorId);
        result.put("interfaceId", interfaceId);

        // 3. Publish Descriptor
        publishDescriptor(eServiceId, descriptorId);
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
        addInterfaceToDescriptor(eServiceId, secondDescriptorId);

        // Publish secondDescriptor
        publishDescriptor(eServiceId, secondDescriptorId);

        // Check until the first descriptor is in desired state
        pollingService.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
                res -> res.getState() == descriptorState,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
        return result;
    }

    public UUID addDocumentToDescriptor(UUID eServiceId, UUID descriptorId, String name) {
        String prettyName = (name == null) ? String.format("Documento_test_qa-%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)) : name;
        Resource resource = blobFileCreator.createBlobFile("src/main/resources/origin-interface.yaml", "documento-test-qa.pdf");

        httpCallExecutor.performCall(() -> eServiceClient.createEServiceDocument(eServiceId, descriptorId, "DOCUMENT", prettyName, resource));
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

    public void publishDescriptor(UUID eServiceId, UUID descriptorId) {
        updateDraftDescriptor(eServiceId, descriptorId, new UpdateEServiceDescriptorSeed().audience(List.of("pagopa.it")));
        httpCallExecutor.performCall(() -> eServiceClient.publishDescriptor(eServiceId, descriptorId));
        assertValidResponse();
        pollingService.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(eServiceId, descriptorId),
                res -> res.getState() == EServiceDescriptorState.PUBLISHED,
                ERROR_RETRIEVING_PRODUCER_DESCRIPTOR
        );
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
        String templateType = (tenantType.equals("PA1") || tenantType.equals("PA2")) ? "PA" : "Privato/GSP";
        RiskAnalysisDataFromJson.RiskAnalysisTemplate riskAnalysisTemplate = riskAnalysisDataInitializer.getRiskAnalysisData().get(templateType);
        RiskAnalysisDataFromJson.RiskAnalysisAttributes riskAnalysisAttributes = (completed) ? riskAnalysisTemplate.getCompleted() : riskAnalysisTemplate.getUncompleted();
        httpCallExecutor.performCall(() -> purposeApiClient.retrieveLatestRiskAnalysisConfiguration());
        assertValidResponse();
        String version = ((RiskAnalysisFormConfig) httpCallExecutor.getResponse()).getVersion();
        return new RiskAnalysis(String.format("finalità_test_%d", new Random().nextInt()), new RiskAnalysisFormSeed().version(version).answers(riskAnalysisAttributes.toMap()));
    }

    public void createPurposeWithGivenState(int testSeed, EServiceMode eServiceMode, PurposeVersionState purposeState, TEServiceMode teServiceMode) {
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
        }
        else {
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
            return ;
        }
        // 2. Activate the purpose version
        httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(purposeId, currentVersion.get()));
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
            return;
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

    public void activateAgreement(UUID agreementId, ClientType reactivatedBy) {
        httpCallExecutor.performCall(() -> agreementClient.activateAgreement(agreementId));
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
            "There was an error while activating the agreement"
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

    public UUID upgradeAgreement(UUID agreementId) {
        httpCallExecutor.performCall(() -> agreementClient.upgradeAgreement(agreementId));
        assertValidResponse();
        Agreement response = (Agreement) httpCallExecutor.getResponse();
        UUID newAgreementId = response.getId();

        pollingService.makePolling(
            () -> agreementClient.getAgreementById(newAgreementId),
            Objects::nonNull,
            ERROR_RETRIEVING_AGREEMENT
        );

        return newAgreementId;
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

    public UUID addRiskAnalysisToEService(UUID eServiceId, EServiceRiskAnalysisSeed eServiceRiskAnalysisSeed) {
        httpCallExecutor.performCall(() -> eServiceClient.addRiskAnalysisToEService(eServiceId, eServiceRiskAnalysisSeed));
        assertValidResponse();

        pollingService.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> producerClient.getProducerEServiceDetails(eServiceId)
                ),
                res -> Optional.ofNullable((ProducerEServiceDetails) httpCallExecutor.getResponse())
                        .map(ProducerEServiceDetails::getRiskAnalysis)
                        .map(list -> list.get(0))
                        .map(EServiceRiskAnalysis::getId)
                        .isPresent(),
                "Risk analysis not found!"
        );
        return ((ProducerEServiceDetails) httpCallExecutor.getResponse()).getRiskAnalysis().get(0).getId();
    }

    public void addEmailToTenant(UUID tenantId, MailSeed mailSeed) {
        httpCallExecutor.performCall(() -> tenantsApi.addTenantMail(tenantId, mailSeed.kind(MailKind.CONTACT_EMAIL)));
        assertValidResponse();

        pollingService.makePolling(
                () -> tenantsApi.getTenant(tenantId),
                res -> res.getContactMail().getAddress().equals(mailSeed.getAddress()),
                "The desired email was not added to tenant!"
        );
    }

    private void assertValidResponse() {
        commonUtils.assertValidResponse();
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
        return eServiceSeed;
    }

    private UpdateEServiceDescriptorSeed mergeDescriptorSeed(UpdateEServiceDescriptorSeed defaultDescriptorSeed, UpdateEServiceDescriptorSeed partialDescriptorSeed) {
        UpdateEServiceDescriptorSeed descriptorSeed = new UpdateEServiceDescriptorSeed();
        descriptorSeed.setAttributes(useOrDefault(partialDescriptorSeed.getAttributes(), defaultDescriptorSeed.getAttributes()));
        descriptorSeed.setDescription(useOrDefault(partialDescriptorSeed.getDescription(), defaultDescriptorSeed.getDescription()));
        descriptorSeed.setAudience(useOrDefault(partialDescriptorSeed.getAudience(), defaultDescriptorSeed.getAudience()));
        descriptorSeed.setVoucherLifespan(useOrDefault(partialDescriptorSeed.getVoucherLifespan(), defaultDescriptorSeed.getVoucherLifespan()));
        descriptorSeed.setAgreementApprovalPolicy(useOrDefault(partialDescriptorSeed.getAgreementApprovalPolicy(), defaultDescriptorSeed.getAgreementApprovalPolicy()));
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
}
