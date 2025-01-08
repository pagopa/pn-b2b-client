package it.pagopa.pn.interop.cucumber.steps;

import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.agreement.domain.EServiceDescriptor;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.purpose.RiskAnalysisDataInitializer;
import it.pagopa.interop.purpose.domain.*;
import it.pagopa.interop.purpose.service.IPurposeApiClient;
import it.pagopa.interop.tenant.service.ITenantsApi;
import it.pagopa.interop.utils.HttpCallExecutor;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import static it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode.RECEIVE;

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
    private final CommonUtils commonUtils;
    private final HttpCallExecutor httpCallExecutor;
    private final RiskAnalysisDataInitializer riskAnalysisDataInitializer;
    private final SharedStepsContext sharedStepsContext;

    static {
        DEFAULT_CLIENT_SEED.setName(String.format("client %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
        DEFAULT_CLIENT_SEED.setDescription("Descrizione client");
        DEFAULT_CLIENT_SEED.setMembers(List.of());
    }

    public DataPreparationService(IAuthorizationClient authorizationClient,
                                  IAgreementClient agreementClient,
                                  IAttributeApiClient attributeApiClient,
                                  ITenantsApi tenantsApi,
                                  IEServiceClient eServiceClient,
                                  IProducerClient producerClient,
                                  IPurposeApiClient purposeApiClient,
                                  HttpCallExecutor httpCallExecutor,
                                  CommonUtils commonUtils,
                                  RiskAnalysisDataInitializer riskAnalysisDataInitializer,
                                  SharedStepsContext sharedStepsContext) {
        this.authorizationClient = authorizationClient;
        this.agreementClient = agreementClient;
        this.attributeApiClient = attributeApiClient;
        this.tenantsApi = tenantsApi;
        this.eServiceClient = eServiceClient;
        this.producerClient = producerClient;
        this.purposeApiClient = purposeApiClient;
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
        this.riskAnalysisDataInitializer = riskAnalysisDataInitializer;
        this.sharedStepsContext = sharedStepsContext;
    }

    public UUID createClient(String clientKind, ClientSeed partialClientSeed) {
        ClientSeed mergedClientSeed = merge(DEFAULT_CLIENT_SEED, partialClientSeed);
        if ("CONSUMER".equals(clientKind)) {
            httpCallExecutor.performCall(() -> authorizationClient.createConsumerClient(sharedStepsContext.getXCorrelationId(), mergedClientSeed));
        } else {
            httpCallExecutor.performCall(() -> authorizationClient.createApiClient(sharedStepsContext.getXCorrelationId(), mergedClientSeed));
        }
        assertValidResponse();
        UUID clientId = ((CreatedResource) httpCallExecutor.getResponse()).getId();
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClient(sharedStepsContext.getXCorrelationId(), clientId)),
                res -> res != HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
        );
        return clientId;
    }

    public void addMemberToClient(UUID clientId, UUID userId) {
        InlineObject2 inlineObject = new InlineObject2().addUserIdsItem(userId);
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.addUsersToClient(sharedStepsContext.getXCorrelationId(), clientId, inlineObject)),
                res -> !res.is5xxServerError(),
                "Failed to add a user to the client!"
        );
        assertValidResponse();
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClientUsers(sharedStepsContext.getXCorrelationId(), clientId)),
                res -> ((List<CompactUser>) httpCallExecutor.getResponse()).stream().anyMatch(user -> user.getUserId().equals(userId)),
                "Failed to retrieve the client users list!"
        );
    }

    public void addPurposeToClient(UUID clientId, UUID purposeId) {
        PurposeAdditionDetailsSeed purposeAdditionDetailsSeed = new PurposeAdditionDetailsSeed().purposeId(purposeId);
        httpCallExecutor.performCall(() -> authorizationClient.addClientPurpose(sharedStepsContext.getXCorrelationId(), clientId, purposeAdditionDetailsSeed));
        assertValidResponse();

        commonUtils.makePolling(
                () -> authorizationClient.getClient(sharedStepsContext.getXCorrelationId(), clientId),
                res -> res.getPurposes().stream().anyMatch(purp -> purp.getPurposeId().equals(purposeId)),
                "Failed to add a purpose to the client!"
        );
    }

    public void archivePurpose(UUID purposeId, UUID versionId) {
        httpCallExecutor.performCall(() ->
                purposeApiClient.archivePurposeVersion(sharedStepsContext.getXCorrelationId(), purposeId, versionId)
        );
        assertValidResponse();
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> purposeApiClient.getPurpose(sharedStepsContext.getXCorrelationId(), purposeId)),
                res -> ((Purpose) httpCallExecutor.getResponse()).getCurrentVersion() != null
                        ? ((Purpose) httpCallExecutor.getResponse()).getCurrentVersion().getState().getValue().equals(PurposeVersionState.ARCHIVED.getValue())
                        : Boolean.FALSE,
                "There was an error while retrieving the purpose!"
        );
    }

    public String addPublicKeyToClient(UUID clientId, KeySeed keySeed) {
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.createKeys(sharedStepsContext.getXCorrelationId(), clientId, List.of(keySeed))),
                res -> res != HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create a new key!"
        );
        assertValidResponse();
        AtomicReference<Optional<String>> keyFound = new AtomicReference<>(Optional.empty());

        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.getClientKeys(sharedStepsContext.getXCorrelationId(), clientId, null)),
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
        // agreement in state DRAFT
        UUID agreementId = createAgreement(eServiceID, descriptorId);
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

    public UUID createAgreement(UUID eServiceID, UUID descriptorId) {
        httpCallExecutor.performCall(() -> agreementClient.createAgreement(sharedStepsContext.getXCorrelationId(), new AgreementPayload().eserviceId(eServiceID).descriptorId(descriptorId)));
        assertValidResponse();
        UUID agreementId = ((CreatedResource) httpCallExecutor.getResponse()).getId();
        commonUtils.makePolling(
                () ->  httpCallExecutor.performCall(() -> agreementClient.getAgreementById(sharedStepsContext.getXCorrelationId(), agreementId)),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the agreement by ID!"
        );
        return agreementId;
    }

    public void submitAgreement(UUID agreementId, AgreementState expectedState) {
        httpCallExecutor.performCall(() -> agreementClient.submitAgreement(sharedStepsContext.getXCorrelationId(), agreementId, new AgreementSubmissionPayload()));
        assertValidResponse();
        commonUtils.makePolling(
                () -> agreementClient.getAgreementById(sharedStepsContext.getXCorrelationId(), agreementId),
                res -> res.getState() == expectedState,
                "There was an error while retrieving the agreement by ID!"
        );
    }

    public void suspendAgreement(UUID agreementId, ClientType suspendedBy) {
        httpCallExecutor.performCall(() -> agreementClient.suspendAgreement(sharedStepsContext.getXCorrelationId(), agreementId));
        assertValidResponse();
        commonUtils.makePolling(
                () -> agreementClient.getAgreementById(sharedStepsContext.getXCorrelationId(), agreementId),
                res -> res.getState().equals(AgreementState.SUSPENDED)
                    && ClientType.PRODUCER.equals(suspendedBy) ? res.getSuspendedByProducer() : res.getSuspendedByConsumer(),
                "There was an error while retrieving the agreement by ID!"
        );

    }

    public void archiveAgreement(UUID agreementId) {
        httpCallExecutor.performCall(() -> agreementClient.archiveAgreement(sharedStepsContext.getXCorrelationId(), agreementId));
        assertValidResponse();
        commonUtils.makePolling(
                () -> agreementClient.getAgreementById(sharedStepsContext.getXCorrelationId(), agreementId),
                res -> res.getState() == AgreementState.ARCHIVED,
                "There was an error while retrieving the agreement by ID!"
        );
    }

    public void addConsumerDocumentToAgreement(UUID agreementId, File doc) {
        httpCallExecutor.performCall(
                () -> agreementClient.addAgreementConsumerDocument(sharedStepsContext.getXCorrelationId(), agreementId, "documento-test-qa.pdf", "documento-test-qa", new FileSystemResource(doc)));
        commonUtils.makePolling(
                () -> agreementClient.getAgreementById(sharedStepsContext.getXCorrelationId(), agreementId),
                res -> res.getConsumerDocuments().size() > 0,
                "There was an error while retrieving the agreement by ID!"
        );
    }

    public UUID createAttribute(AttributeKind attributeKind, String name) {
        String actualName = name != null ? null : String.format("new_attribute_%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        switch (attributeKind) {
            case CERTIFIED -> httpCallExecutor.performCall(() -> attributeApiClient.createCertifiedAttribute(sharedStepsContext.getXCorrelationId(), new CertifiedAttributeSeed().description("description_test").name(actualName)));
            case VERIFIED -> httpCallExecutor.performCall(() -> attributeApiClient.createVerifiedAttribute(sharedStepsContext.getXCorrelationId(), new AttributeSeed().description("description_test").name(actualName)));
            case DECLARED -> httpCallExecutor.performCall(() -> attributeApiClient.createDeclaredAttribute(sharedStepsContext.getXCorrelationId(), new AttributeSeed().description("description_test").name(actualName)));
            default -> throw new IllegalArgumentException("Invalid attributeKind: " + attributeKind);
        }
        assertValidResponse();

        commonUtils.makePolling(
                () -> attributeApiClient.getAttributes(sharedStepsContext.getXCorrelationId(), 1, 0, List.of(attributeKind), actualName, null),
                res -> res.getResults().size() > 0,
                "There was an error while retrieving the attributes"
        );
        return ((Attribute) httpCallExecutor.getResponse()).getId();
    }

    public void assignCertifiedAttributeToTenant(UUID tenantId, UUID attributeId) {
        httpCallExecutor.performCall(
                () -> tenantsApi.addCertifiedAttribute(tenantId, new CertifiedTenantAttributeSeed().id(attributeId)));
        assertValidResponse();

        commonUtils.makePolling(
                () -> tenantsApi.getCertifiedAttributes(tenantId),
                res -> res.getAttributes().stream().anyMatch(attr -> attr.getId().equals(attributeId)),
                "There was an error while retrieving the attributes"
        );
    }

    public EServiceDescriptor createEServiceAndDraftDescriptor(EServiceSeed partialEserviceSeed, UpdateEServiceDescriptorSeed partialDescriptorSeed) {
        EServiceSeed DEFAULT_ESERVICE_SEED = new EServiceSeed()
                .name(String.format("e-service %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)))
                .description("Descrizione e-service")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER);
        EServiceSeed eServiceSeed = merge(DEFAULT_ESERVICE_SEED, partialEserviceSeed);

        httpCallExecutor.performCall(() -> eServiceClient.createEService(sharedStepsContext.getXCorrelationId(), eServiceSeed));
        assertValidResponse();
        UUID eserviceId = ((CreatedEServiceDescriptor)httpCallExecutor.getResponse()).getId();
        UUID descriptorId = ((CreatedEServiceDescriptor)httpCallExecutor.getResponse()).getDescriptorId();

        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> producerClient.getProducerEServiceDescriptor(sharedStepsContext.getXCorrelationId(), eserviceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the producer e-service descriptor"
        );

        updateDraftDescriptor(eserviceId, descriptorId, partialDescriptorSeed);
        return new EServiceDescriptor(eserviceId, descriptorId);
    }

    public void updateDraftDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed partialDescriptorSeed) {
        ProducerEServiceDescriptor descriptor = producerClient.getProducerEServiceDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId);
        UpdateEServiceDescriptorSeed currentDescriptorSeed = new UpdateEServiceDescriptorSeed()
                .agreementApprovalPolicy(descriptor.getAgreementApprovalPolicy())
                .attributes(new DescriptorAttributesSeed().addCertifiedItem(List.of()).addDeclaredItem(List.of()).addVerifiedItem(List.of()))
                .dailyCallsPerConsumer(descriptor.getDailyCallsPerConsumer())
                .dailyCallsTotal(descriptor.getDailyCallsTotal())
                .audience(descriptor.getAudience())
                .voucherLifespan(descriptor.getVoucherLifespan());

        UpdateEServiceDescriptorSeed descriptorSeed = mergeDescriptorSeed(currentDescriptorSeed, partialDescriptorSeed)
                .dailyCallsPerConsumer(50).dailyCallsTotal(1000).audience(List.of("pagopa.it"));

        httpCallExecutor.performCall(() -> eServiceClient.updateDraftDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId, descriptorSeed));
        assertValidResponse();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        };
    }

    public Map<String, Object> bringDescriptorToGivenState(UUID eServiceId, UUID descriptorId, EServiceDescriptorState descriptorState, boolean withDocument) {
        // 1 add document to descriptor
        UUID documentId = null;
        Map<String, Object> result = new HashMap<>();
        if (withDocument) documentId = addDocumentToDescriptor(eServiceId, descriptorId);
        result.put("descriptorId", descriptorId);
        result.put("documentId", documentId);

        if (descriptorState == EServiceDescriptorState.DRAFT) return result;

        // 2. Add interface to descriptor
        addInterfaceToDescriptor(eServiceId, descriptorId);

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
            UUID agreementId = createAgreement(eServiceId, descriptorId);
            submitAgreement(agreementId, AgreementState.ACTIVE);
        }

        // Create another DRAFT descriptor
        UUID secondDescriptorId = createNextDraftDescriptor(eServiceId);

        // Add interface to secondDescriptor
        addInterfaceToDescriptor(eServiceId, secondDescriptorId);

        // Publish secondDescriptor
        publishDescriptor(eServiceId, secondDescriptorId);

        // Check until the first descriptor is in desired state
        commonUtils.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId),
                res -> res.getState() == descriptorState,
                "There was an error while retrieving the producer e-service descriptor"
        );
        return result;
    }

    public UUID addDocumentToDescriptor(UUID eServiceId, UUID descriptorId) {
        String prettyName = String.format("Documento_test_qa-%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        Resource resource = createBlobFile("dummy.pdf", "documento-test-qa.pdf");

        httpCallExecutor.performCall(() -> eServiceClient.createEServiceDocument(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId, "DOCUMENT", prettyName, resource));
        assertValidResponse();
        UUID documentId = ((CreatedResource) httpCallExecutor.getResponse()).getId();

        commonUtils.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId),
                res -> res.getDocs().stream().anyMatch(doc -> doc.getPrettyName().equals(prettyName)),
                "There was an error while retrieving the producer e-service descriptor"
        );
        return documentId;
    }

    public void addInterfaceToDescriptor(UUID eServiceId, UUID descriptorId) {
        Resource resource = createBlobFile("interface.yaml", "interface.yaml");
        httpCallExecutor.performCall(() -> eServiceClient.createEServiceDocument(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId, "INTERFACE", "Interfaccia", resource));
        assertValidResponse();

        commonUtils.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId),
                res -> res.getInterface() != null,
                "There was an error while retrieving the producer e-service descriptor"
        );

    }

    public void publishDescriptor(UUID eServiceId, UUID descriptorId) {
        updateDraftDescriptor(eServiceId, descriptorId, new UpdateEServiceDescriptorSeed().audience(List.of("pagopa.it")));
        httpCallExecutor.performCall(() -> eServiceClient.publishDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId));
        assertValidResponse();
        commonUtils.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId),
                res -> res.getState() == EServiceDescriptorState.PUBLISHED,
                "There was an error while retrieving the producer e-service descriptor"
        );
    }

    public void suspendDescriptor(UUID eServiceId, UUID descriptorId) {
        httpCallExecutor.performCall(() -> eServiceClient.suspendDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId));
        assertValidResponse();
        commonUtils.makePolling(
                () -> producerClient.getProducerEServiceDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId),
                res -> res.getState() == EServiceDescriptorState.SUSPENDED,
                "There was an error while retrieving the producer e-service descriptor"
        );
    }

    public UUID createNextDraftDescriptor(UUID eServiceId) {
        httpCallExecutor.performCall(() -> eServiceClient.createDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId));
        assertValidResponse();
        UUID descriptorId = ((CreatedResource) httpCallExecutor.getResponse()).getId();
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> producerClient.getProducerEServiceDescriptor(sharedStepsContext.getXCorrelationId(), eServiceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the producer e-service descriptor"
        );
        return descriptorId;
    }

    public RiskAnalysis getRiskAnalysis(String tenantType, boolean completed) {
        String templateType = (tenantType.equals("PA1") || tenantType.equals("PA2")) ? "PA" : "Privato/GSP";
        String templateStatus = (completed) ? "completed" : "uncompleted";
        RiskAnalysisDataFromJson.RiskAnalysisTemplate riskAnalysisTemplate = riskAnalysisDataInitializer.getRiskAnalysisData().get(templateType);
        RiskAnalysisDataFromJson.RiskAnalysisAttributes riskAnalysisAttributes = (completed) ? riskAnalysisTemplate.getCompleted() : riskAnalysisTemplate.getUncompleted();
        httpCallExecutor.performCall(() -> purposeApiClient.retrieveLatestRiskAnalysisConfiguration(sharedStepsContext.getXCorrelationId()));
        assertValidResponse();
        String version = ((RiskAnalysisFormConfig) httpCallExecutor.getResponse()).getVersion();
        return new RiskAnalysis("finalità test", new RiskAnalysisFormSeed().version(version).answers(riskAnalysisAttributes.toMap()));
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
            httpCallExecutor.performCall(() -> purposeApiClient.createPurposeForReceiveEservice(sharedStepsContext.getXCorrelationId(), purposeEServiceSeed));
        }
        else {
            // For modes other than RECEIVE, build a PurposeSeed
            PurposeSeed purposeSeed = new PurposeSeed();
            purposeSeed.setTitle(title);
            purposeSeed.setDescription(description);
            purposeSeed.setIsFreeOfCharge(isFreeOfCharge);
            purposeSeed.setFreeOfChargeReason(freeOfChargeReason);
            purposeSeed.setDailyCalls(dailyCalls);

            // Add data from the payload
            purposeSeed.setEserviceId(teServiceMode.getEserviceId());
            purposeSeed.setConsumerId(teServiceMode.getConsumerId());
            purposeSeed.setRiskAnalysisForm(teServiceMode.getRiskAnalysisFormSeed());
            httpCallExecutor.performCall(() -> purposeApiClient.createPurpose(sharedStepsContext.getXCorrelationId(), purposeSeed));
        }
        assertValidResponse();
        UUID purposeId = ((CreatedResource) httpCallExecutor.getResponse()).getId();
        AtomicReference<UUID> currentVersion = new AtomicReference<>();
        AtomicReference<UUID> waitingForApprovalVersionId = new AtomicReference<>();

        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> purposeApiClient.getPurpose(sharedStepsContext.getXCorrelationId(), purposeId)),
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
                "There was an error while retrieving the purpose!"
        );

        if (purposeState == PurposeVersionState.DRAFT) {
            sharedStepsContext.getPurposeCommonContext().setPurposeId(String.valueOf(purposeId));
            sharedStepsContext.getPurposeCommonContext().setVersionId(String.valueOf(currentVersion));
            return;
        }
        // 2. Activate the purpose version
        httpCallExecutor.performCall(() -> purposeApiClient.activatePurposeVersion(sharedStepsContext.getXCorrelationId(), purposeId, currentVersion.get()));
        assertValidResponse();
        PurposeVersionResource activatePurposeResponse = ((PurposeVersionResource) httpCallExecutor.getResponse());

        // 3. If the state required is WAITING_FOR_APPROVAL, we need to wait until the purpose version is in that state and return the purposeId
        if (purposeState == PurposeVersionState.WAITING_FOR_APPROVAL) {
            commonUtils.makePolling(
                    () -> purposeApiClient.getPurpose(sharedStepsContext.getXCorrelationId(), purposeId),
                    res -> {
                        if (Optional.ofNullable(res.getWaitingForApprovalVersion()).map(PurposeVersion::getId).isPresent()) {
                            waitingForApprovalVersionId.set(res.getWaitingForApprovalVersion().getId());
                        }
                        return PurposeVersionState.WAITING_FOR_APPROVAL == Optional.ofNullable(res.getWaitingForApprovalVersion())
                                .map(PurposeVersion::getState).orElse(null);
                    },
                    "There was an error while retrieving the purpose!"
            );
            sharedStepsContext.getPurposeCommonContext().setPurposeId(String.valueOf(purposeId));
            sharedStepsContext.getPurposeCommonContext().setWaitingForApprovalVersionId(String.valueOf(waitingForApprovalVersionId.get()));
            return;
        }

        commonUtils.makePolling(
                () -> purposeApiClient.getPurpose(sharedStepsContext.getXCorrelationId(), purposeId),
                res -> {
                    if (PurposeVersionState.ACTIVE == Optional.ofNullable(res.getCurrentVersion()).map(PurposeVersion::getState).orElse(null)) {
                        currentVersion.set(res.getCurrentVersion().getId());
                        return true;
                    }
                    return false;
                },
                "There was an error while retrieving the purpose!"
        );

        // 4. If the state required is SUSPENDED call the endpoint to suspend the purpose version
        if (purposeState == PurposeVersionState.SUSPENDED) {
            httpCallExecutor.performCall(() -> purposeApiClient.suspendPurposeVersion(sharedStepsContext.getXCorrelationId(), purposeId, currentVersion.get()));
            assertValidResponse();
            PurposeVersionResource suspendPurposeResponse = ((PurposeVersionResource) httpCallExecutor.getResponse());
            commonUtils.makePolling(
                    () -> purposeApiClient.getPurpose(sharedStepsContext.getXCorrelationId(), purposeId),
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
            httpCallExecutor.performCall(() -> purposeApiClient.archivePurposeVersion(sharedStepsContext.getXCorrelationId(), purposeId, currentVersion.get()));
            assertValidResponse();
            PurposeVersionResource suspendPurposeResponse = ((PurposeVersionResource) httpCallExecutor.getResponse());
            commonUtils.makePolling(
                    () -> purposeApiClient.getPurpose(sharedStepsContext.getXCorrelationId(), purposeId),
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
        return;
    }

    public void rejectPurposeVersion(UUID purposeId, UUID versionId) {
        httpCallExecutor.performCall(() -> purposeApiClient.rejectPurposeVersion(sharedStepsContext.getXCorrelationId(), purposeId, versionId, new RejectPurposeVersionPayload().rejectionReason("Testing QA purposes")));
        assertValidResponse();

        commonUtils.makePolling(
                () -> purposeApiClient.getPurpose(sharedStepsContext.getXCorrelationId(), purposeId),
                res -> {
                    Optional<PurposeVersionState> versionState = res.getVersions().stream().filter(v -> v.getId().equals(versionId)).map(PurposeVersion::getState).findFirst();
                    return versionState.isPresent() && versionState.get().equals(PurposeVersionState.REJECTED);
                },
                "There was an error while retrieving the purpose!"
        );


    }

    private Resource createBlobFile(String filePathToRead, String fileNameToCreate) {
        Path filePath = Paths.get("src/main/resources/interface.yaml");
        byte[] fileContent = null;
        File file = null;
        try {
            fileContent = Files.readAllBytes(filePath);
            Path newFilePath = Paths.get(fileNameToCreate);
            Files.write(newFilePath, fileContent);
            file = newFilePath.toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileSystemResource(filePath);
    }

    private void assertValidResponse() {
        Assertions.assertFalse(httpCallExecutor.getClientResponse().isError(),
                "Something went wrong: " + httpCallExecutor.getClientResponse().getReasonPhrase());
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
}
