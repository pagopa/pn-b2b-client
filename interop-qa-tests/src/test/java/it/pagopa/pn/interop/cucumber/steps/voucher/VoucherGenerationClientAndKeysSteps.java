package it.pagopa.pn.interop.cucumber.steps.voucher;

import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.domain.KeyPairPEM;
import it.pagopa.interop.authorization.service.utils.IdentityService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.interop.purpose.domain.TEServiceMode;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.DataPreparationService;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import java.security.KeyPair;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class VoucherGenerationClientAndKeysSteps {

    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final DataPreparationService dataPreparationService;

    public VoucherGenerationClientAndKeysSteps(ClientTokenConfigurator clientTokenConfigurator,
        SharedStepsContext sharedStepsContext,
        DataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
    }

    @Given("{string} rimuove quella nuova chiave dal client")
    public void removeNewKeyFromClient(String clientName) {
        removeKeyFromClient(clientName, sharedStepsContext.getClientCommonContext().getNewKeyId());
    }

    @Given("{string} rimuove quella chiave dal client")
    public void removeKeyFromClient(String clientName) {
        removeKeyFromClient(clientName, sharedStepsContext.getClientCommonContext().getKeyId());
    }

    private void removeKeyFromClient(String clientName, String keyId) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(clientName, null));
        dataPreparationService.deleteClientKeyById(
            sharedStepsContext.getClientCommonContext().getFirstClient(),
            keyId
        );
    }

    @Given("{string} rimuove quella nuova finalità dal client")
    public void removeNewPurposeFromClient(String clientName) {
        removePurposeFromClient(clientName,
            sharedStepsContext.getPurposeCommonContext().getNewPurposeId());
    }

    @Given("{string} rimuove quella finalità dal client")
    public void removePurposeFromClient(String clientName) {
        removePurposeFromClient(clientName,
            sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID());
    }

    private void removePurposeFromClient(String clientName, UUID newPurposeId) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(clientName, null));
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        dataPreparationService.deletePurposeFromClient(
            clientId,
            newPurposeId
        );
    }

    @Given("{string} cancella quel client")
    public void deleteClient(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        dataPreparationService.deleteClient(clientId);
    }

    @Given("un {string} di {string} ha aggiunto una nuova chiave pubblica al client")
    public void addNewPublicKeyToClient(String role, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, role));
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        String publicKey = KeyPairGeneratorUtil.createBase64PublicKey("RSA", 2048);

        String newKeyId = dataPreparationService.addPublicKeyToClient(
            clientId,
            KeyPairGeneratorUtil.createKeySeed(publicKey, sharedStepsContext.getTestSeed()).get(0)
        );

        sharedStepsContext.getClientCommonContext().setNewKeyId(newKeyId);
    }

    @Given("{string} ha già creato una nuova chiave pubblica senza associarla al client")
    public void createNewPublicKeyWithoutClientAssociation(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));

        String keyType = "RSA";
        KeyPair keyPair = KeyPairGeneratorUtil.createKeyPair(keyType, 2048);
        KeyPairPEM keyPairPEM = KeyPairGeneratorUtil.keyPairToPEM(keyPair);
        sharedStepsContext.getClientCommonContext().setClientPublicKey(keyPairPEM.getPublicKey());
        sharedStepsContext.getClientCommonContext().setClientPublicKeyAsObj(keyPair.getPublic());
        sharedStepsContext.getClientCommonContext().setClientPrivateKey(keyPairPEM.getPrivateKey());
        sharedStepsContext.getClientCommonContext().setClientPrivateKeyAsObj(keyPair.getPrivate());
        sharedStepsContext.getClientCommonContext().setKeyType(keyType);
        sharedStepsContext.getClientCommonContext().setKeyId(UUID.randomUUID().toString());
    }

    @Given("{string} ha già creato una nuova finalità attiva per quell'eservice")
    public void createNewActivePurposeForEservice(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID consumerId = identityService.getOrganizationId(tenantType);
        UUID eserviceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();
        RiskAnalysis riskAnalysis = dataPreparationService.getRiskAnalysis(tenantType, true);
        dataPreparationService.createPurposeWithGivenState(
            sharedStepsContext.getTestSeed(),
            EServiceMode.DELIVER,
            PurposeVersionState.ACTIVE,
            TEServiceMode.builder()
                .eserviceId(eserviceId)
                .consumerId(consumerId)
                .riskAnalysisFormSeed(riskAnalysis.getRiskAnalysisForm())
                .build()
        );

        UUID purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID();
        sharedStepsContext.getPurposeCommonContext().setNewPurposeId(purposeId);
    }

    @Given("{string} ha già associato quella nuova finalità a quel client")
    public void associateNewPurposeToClient(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID clientId = sharedStepsContext.getClientCommonContext().getFirstClient();
        UUID newPurposeId = sharedStepsContext.getPurposeCommonContext().getNewPurposeId();

        dataPreparationService.addPurposeToClient(clientId, newPurposeId);
    }

    @Given("{string} ha già creato 1 nuovo client {string}")
    public void createNewClient(String tenantType, String clientKind) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID newClientId = dataPreparationService.createClient(
            clientKind,
            new ClientSeed().name(
                // second param is a random int
                String.format("client-%d-%d", sharedStepsContext.getTestSeed(),  getRandomInt())
            )
        );

        /* Codice originale pre-porting:
         *      this.newClientId = await dataPreparationService.createClient(...)
         * al posto di introdurre in contesto "newClientId" si considera l'ultimo client immesso
         * */
        sharedStepsContext.getClientCommonContext().addClient(newClientId);
    }

    private static int getRandomInt() {
        return ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
    }

    @Given("{string} ha già inserito l'utente con ruolo {string} come membro di quel nuovo client")
    public void addMemberToClient(String tenantType, String roleOfMemberToAdd) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID newClientId = sharedStepsContext.getClientCommonContext().getLastClient();
        UUID clientMemberUserId = identityService.getUserId(tenantType, roleOfMemberToAdd);

        dataPreparationService.addMemberToClient(newClientId, clientMemberUserId);
    }

    @Given("un {string} di {string} ha caricato una chiave pubblica nel nuovo client")
    public void addPublicKeyToNewClient(String role, String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, role));
        UUID newClientId = sharedStepsContext.getClientCommonContext().getLastClient();
        String keyType = "RSA";
        KeyPairPEM keyPair = KeyPairGeneratorUtil.createKeyPairPEM(keyType, 2048);
        sharedStepsContext.getClientCommonContext().setNewClientPublicKey(keyPair.getPublicKey());
        sharedStepsContext.getClientCommonContext().setNewClientPrivateKey(keyPair.getPrivateKey());

        sharedStepsContext.getClientCommonContext().setKeyType(keyType);

        String keyId = dataPreparationService.addPublicKeyToClient(
            newClientId,
            KeyPairGeneratorUtil.createKeySeed(
                KeyPairGeneratorUtil.keyToBase64(keyPair.getPublicKey(), true),
                sharedStepsContext.getTestSeed()).get(0)
        );

        sharedStepsContext.getClientCommonContext().setKeyId(keyId);
    }

    @Given("{string} ha già associato la finalità al nuovo client")
    public void associatePurposeToNewClient(String tenantType) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenantType, null));
        UUID newClientId = sharedStepsContext.getClientCommonContext().getLastClient();
        UUID purposeId = sharedStepsContext.getPurposeCommonContext().getPurposeIdAsUUID();

        dataPreparationService.addPurposeToClient(newClientId, purposeId);
    }
}
