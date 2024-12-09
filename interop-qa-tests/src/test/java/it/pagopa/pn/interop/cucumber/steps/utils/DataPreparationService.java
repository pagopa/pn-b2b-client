package it.pagopa.pn.interop.cucumber.steps.utils;

import io.cucumber.java.it.Ma;
import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.agreement.service.IEServiceClient;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.authorization.service.IProducerClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.tenant.service.ITenantsApi;
import org.junit.jupiter.api.Assertions;
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

public class DataPreparationService {
    private static final ClientSeed DEFAULT_CLIENT_SEED = new ClientSeed();
    private IAuthorizationClient authorizationClient;
    private IAgreementClient agreementClient;
    private IAttributeApiClient attributeApiClient;
    private ITenantsApi tenantsApi;
    private IEServiceClient eServiceClient;
    private IProducerClient producerClient;
    private CommonUtils commonUtils;
    private HttpCallExecutor httpCallExecutor;

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
                                  HttpCallExecutor httpCallExecutor,
                                  CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.agreementClient = agreementClient;
        this.attributeApiClient = attributeApiClient;
        this.tenantsApi = tenantsApi;
        this.eServiceClient = eServiceClient;
        this.producerClient = producerClient;
        this.httpCallExecutor = httpCallExecutor;
        this.commonUtils = commonUtils;
    }

    public UUID createClient(String clientKind, UUID clientId, ClientSeed partialClientSeed) {
        ClientSeed mergedClientSeed = merge(DEFAULT_CLIENT_SEED, partialClientSeed);
        if ((clientKind == "CONSUMER")) {
            httpCallExecutor.performCall(() -> authorizationClient.createConsumerClient("", mergedClientSeed));
        } else {
            httpCallExecutor.performCall(() -> authorizationClient.createApiClient("", mergedClientSeed));
        }
        assertValidResponse();
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClient("", clientId)),
                res -> res != HttpStatus.NOT_FOUND,
                "Failed to retrieve the client!"
        );
        return clientId;
    }

    public void addMemberToClient(UUID clientId, UUID userId) {
        InlineObject2 inlineObject = new InlineObject2().addUserIdsItem(userId);
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.addUsersToClient("", clientId, inlineObject)),
                res -> !res.is5xxServerError(),
                "Failed to add a user to the client!"
        );
        assertValidResponse();
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> authorizationClient.getClientUsers("", clientId)),
                res -> ((List<CompactUser>) httpCallExecutor.getResponse()).stream().anyMatch(user -> user.getUserId() == userId),
                "Failed to retrieve the client users list!"
        );
    }

    public void addPurposeToClient(UUID clientId, UUID purposeId) {
        PurposeAdditionDetailsSeed purposeAdditionDetailsSeed = new PurposeAdditionDetailsSeed().purposeId(purposeId);
        httpCallExecutor.performCall(() -> authorizationClient.addClientPurpose("", clientId, purposeAdditionDetailsSeed));
        assertValidResponse();

        commonUtils.makePolling(
                () -> authorizationClient.getClient("", clientId),
                res -> ((Client) httpCallExecutor.getResponse()).getPurposes().stream().anyMatch(purp -> purp.getPurposeId() == purposeId),
                "Failed to add a purpose to the client!"
        );
    }
    public String addPublicKeyToClient(UUID clientId, KeySeed keySeed) {
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.createKeys("", clientId, KeyPairGeneratorUtil.createKeySeed(KeyUse.SIG, "RS256", KeyPairGeneratorUtil.createBase64PublicKey("RSA", 2048)))),
                res -> res != HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to create a new key!"
        );
        assertValidResponse();
        AtomicReference<Optional<String>> keyFound = new AtomicReference<>(Optional.empty());

        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(
                        () -> authorizationClient.getClientKeys("", clientId, null)),
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
        if (doc != null) {
            addConsumerDocumentToAgreement(agreementId, doc);
        }
        if ("DRAFT".equals(agreementState)) {
            return agreementId;
        }
        submitAgreement(agreementId, agreementState == AgreementState.PENDING ? agreementState : AgreementState.ACTIVE);

        // agreement in state ACTIVE o PENDING
        if (AgreementState.ACTIVE.equals(agreementState) || AgreementState.PENDING.equals(agreementState)) {
            return agreementId;
        }

        // agreement in state SUSPENDED
        suspendAgreement(agreementId, ClientType.CONSUMER);

        if(agreementState.equals(AgreementState.SUSPENDED)) {
            return agreementId;
        }

        // agreement in state ARCHIVED
        if(agreementState.equals(AgreementState.ARCHIVED)) {
            archiveAgreement(agreementId);
        }
        //TODO da verificare
        return agreementId;
    }

    public UUID createAgreement(UUID eServiceID, UUID descriptorId) {
        httpCallExecutor.performCall(
                () -> agreementClient.createAgreement("", new AgreementPayload().eserviceId(eServiceID).descriptorId(descriptorId)));
        assertValidResponse();
        UUID agreementId = ((CreatedResource) httpCallExecutor.getResponse()).getId();

        commonUtils.makePolling(
                () ->  httpCallExecutor.performCall(() -> agreementClient.getAgreementById("", agreementId)),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the agreement by ID!"
        );
        return agreementId;
    }

    public void submitAgreement(UUID agreementId, AgreementState expectedState) {
        httpCallExecutor.performCall(
                () -> agreementClient.submitAgreement("", agreementId, new AgreementSubmissionPayload())
        );
        assertValidResponse();

        commonUtils.makePolling(
                () -> agreementClient.getAgreementById("", agreementId),
                res -> res.getState() == expectedState,
                "There was an error while retrieving the agreement by ID!"
        );
    }

    public void suspendAgreement(UUID agreementId, ClientType suspendedBy) {
        httpCallExecutor.performCall(
                () -> agreementClient.suspendAgreement("", agreementId));
        assertValidResponse();

        commonUtils.makePolling(
                () -> agreementClient.getAgreementById("", agreementId),
                res -> res.getState().equals(AgreementState.SUSPENDED)
                    && ClientType.PRODUCER.equals(suspendedBy) ? res.getSuspendedByProducer() : res.getSuspendedByConsumer(),
                "There was an error while retrieving the agreement by ID!"
        );

    }

    public void archiveAgreement(UUID agreementId) {
        httpCallExecutor.performCall(
                () -> agreementClient.archiveAgreement("", agreementId));
        assertValidResponse();

        commonUtils.makePolling(
                () -> agreementClient.getAgreementById("", agreementId),
                res -> res.getState() == AgreementState.ARCHIVED,
                "There was an error while retrieving the agreement by ID!"
        );
    }

    public void addConsumerDocumentToAgreement(UUID agreementId, File doc) {
        httpCallExecutor.performCall(
                () -> agreementClient.addAgreementConsumerDocument("", agreementId, "documento-test-qa.pdf", "documento-test-qa", new FileSystemResource(doc)));

        commonUtils.makePolling(
                () -> agreementClient.getAgreementById("", agreementId),
                res -> res.getConsumerDocuments().size() > 0,
                "There was an error while retrieving the agreement by ID!"
        );
    }

    public UUID createAttribute(AttributeKind attributeKind, String name) {
        String actualName = name != null ? null : String.format("new_attribute_%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        switch (attributeKind) {
            case CERTIFIED -> httpCallExecutor.performCall(() -> attributeApiClient.createCertifiedAttribute("", new CertifiedAttributeSeed().description("description_test").name(actualName)));
            case VERIFIED -> httpCallExecutor.performCall(() -> attributeApiClient.createVerifiedAttribute("", new AttributeSeed().description("description_test").name(actualName)));
            case DECLARED -> httpCallExecutor.performCall(() -> attributeApiClient.createDeclaredAttribute("", new AttributeSeed().description("description_test").name(actualName)));
            default -> throw new IllegalArgumentException("Invalid attributeKind: " + attributeKind);
        }
        assertValidResponse();

        commonUtils.makePolling(
                () -> attributeApiClient.getAttributes("", 1, 0, List.of(attributeKind), actualName, null),
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

    public Map<String, UUID> createEServiceAndDraftDescriptor(EServiceSeed partialEserviceSeed, UpdateEServiceDescriptorSeed partialDescriptorSeed) {
        EServiceSeed DEFAULT_ESERVICE_SEED = new EServiceSeed()
                .name(String.format("e-service %d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)))
                .description("Descrizione e-service")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER);
        EServiceSeed eServiceSeed = merge(DEFAULT_ESERVICE_SEED, partialEserviceSeed);

        httpCallExecutor.performCall(() -> eServiceClient.createEService("", eServiceSeed));
        assertValidResponse();
        UUID eserviceId = ((CreatedEServiceDescriptor)httpCallExecutor.getResponse()).getId();
        UUID descriptorId = ((CreatedEServiceDescriptor)httpCallExecutor.getResponse()).getDescriptorId();

        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> producerClient.getProducerEServiceDescriptor("", eserviceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the producer e-service descriptor"
        );

        updateDraftDescriptor(eserviceId, descriptorId, partialDescriptorSeed);
        return Map.of("eserviceId", eserviceId, "descriptorId", descriptorId);
    }

    public void updateDraftDescriptor(UUID eServiceId, UUID descriptorId, UpdateEServiceDescriptorSeed partialDescriptorSeed) {
        ProducerEServiceDescriptor descriptor = producerClient.getProducerEServiceDescriptor("", eServiceId, descriptorId);

        UpdateEServiceDescriptorSeed currentDescriptorSeed = new UpdateEServiceDescriptorSeed()
                .agreementApprovalPolicy(descriptor.getAgreementApprovalPolicy())
                .attributes(new DescriptorAttributesSeed().addCertifiedItem(List.of()).addDeclaredItem(List.of()).addVerifiedItem(List.of()))
                .dailyCallsPerConsumer(descriptor.getDailyCallsPerConsumer())
                .dailyCallsTotal(descriptor.getDailyCallsTotal())
                .audience(descriptor.getAudience())
                .voucherLifespan(descriptor.getVoucherLifespan());

        UpdateEServiceDescriptorSeed descriptorSeed = mergeDescriptorSeed(currentDescriptorSeed, partialDescriptorSeed)
                .dailyCallsPerConsumer(50).dailyCallsTotal(1000).audience(List.of("pagopa.it"));

        httpCallExecutor.performCall(() -> eServiceClient.updateDraftDescriptor("", eServiceId, descriptorId, descriptorSeed));
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
        result = Map.of("descriptorId", descriptorId, "documentId", documentId);
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
                () -> producerClient.getProducerEServiceDescriptor("", eServiceId, descriptorId),
                res -> res.getState() == descriptorState,
                "There was an error while retrieving the producer e-service descriptor"
        );
        return result;
    }

    public UUID addDocumentToDescriptor(UUID eServiceId, UUID descriptorId) {
        String prettyName = String.format("Documento_test_qa-%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE));
        Resource resource = createBlobFile("dummy.pdf", "documento-test-qa.pdf");

        httpCallExecutor.performCall(() -> eServiceClient.createEServiceDocument("", eServiceId, descriptorId, "DOCUMENT", prettyName, resource));
        assertValidResponse();
        UUID documentId = ((CreatedResource) httpCallExecutor.getResponse()).getId();

        commonUtils.makePolling(
                () -> producerClient.getProducerEServiceDescriptor("", eServiceId, descriptorId),
                res -> res.getDocs().stream().anyMatch(doc -> doc.getPrettyName().equals(prettyName)),
                "There was an error while retrieving the producer e-service descriptor"
        );
        return documentId;
    }

    public void addInterfaceToDescriptor(UUID eServiceId, UUID descriptorId) {
        Resource resource = createBlobFile("interface.yaml", "interface.yaml");
        httpCallExecutor.performCall(() -> eServiceClient.createEServiceDocument("", eServiceId, descriptorId, "INTERFACE", "Interfaccia", resource));
        assertValidResponse();

        commonUtils.makePolling(
                () -> producerClient.getProducerEServiceDescriptor("", eServiceId, descriptorId),
                res -> res.getInterface() != null,
                "There was an error while retrieving the producer e-service descriptor"
        );

    }

    public void publishDescriptor(UUID eServiceId, UUID descriptorId) {
        updateDraftDescriptor(eServiceId, descriptorId, new UpdateEServiceDescriptorSeed().audience(List.of("pagopa.it")));
        httpCallExecutor.performCall(() -> eServiceClient.publishDescriptor("", eServiceId, descriptorId));
        assertValidResponse();
        commonUtils.makePolling(
                () -> producerClient.getProducerEServiceDescriptor("", eServiceId, descriptorId),
                res -> res.getState() == EServiceDescriptorState.PUBLISHED,
                "There was an error while retrieving the producer e-service descriptor"
        );
    }

    public void suspendDescriptor(UUID eServiceId, UUID descriptorId) {
        httpCallExecutor.performCall(() -> eServiceClient.suspendDescriptor("", eServiceId, descriptorId));
        assertValidResponse();
        commonUtils.makePolling(
                () -> producerClient.getProducerEServiceDescriptor("", eServiceId, descriptorId),
                res -> res.getState() == EServiceDescriptorState.SUSPENDED,
                "There was an error while retrieving the producer e-service descriptor"
        );
    }

    public UUID createNextDraftDescriptor(UUID eServiceId) {
        httpCallExecutor.performCall(() -> eServiceClient.createDescriptor("", eServiceId));
        assertValidResponse();
        UUID descriptorId = ((CreatedResource) httpCallExecutor.getResponse()).getId();
        commonUtils.makePolling(
                () -> httpCallExecutor.performCall(() -> producerClient.getProducerEServiceDescriptor("", eServiceId, descriptorId)),
                res -> res != HttpStatus.NOT_FOUND,
                "There was an error while retrieving the producer e-service descriptor"
        );
        return descriptorId;
    }

    private Resource createBlobFile(String filePathToRead, String fileNameToCreate) {
        Path filePath = Paths.get(String.format("classpath:%s", filePathToRead));
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
