package it.pagopa.pn.interop.cucumber.steps.utils;

import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.agreement.service.IAgreementClient;
import it.pagopa.interop.attribute.service.IAttributeApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.authorization.service.IAuthorizationClient;
import it.pagopa.interop.authorization.service.utils.CommonUtils;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.tenant.service.ITenantsApi;
import org.junit.jupiter.api.Assertions;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class DataPreparationService {
    private static final ClientSeed DEFAULT_CLIENT_SEED = new ClientSeed();
    private IAuthorizationClient authorizationClient;
    private IAgreementClient agreementClient;
    private IAttributeApiClient attributeApiClient;
    private ITenantsApi tenantsApi;
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
                                  HttpCallExecutor httpCallExecutor,
                                  CommonUtils commonUtils) {
        this.authorizationClient = authorizationClient;
        this.agreementClient = agreementClient;
        this.attributeApiClient = attributeApiClient;
        this.tenantsApi = tenantsApi;
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

    private <T> T useOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
