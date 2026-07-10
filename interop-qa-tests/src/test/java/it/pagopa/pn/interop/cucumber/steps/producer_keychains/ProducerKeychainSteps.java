package it.pagopa.pn.interop.cucumber.steps.producer_keychains;

import com.nimbusds.jose.jwk.KeyType;
import io.cucumber.java.en.And;
import io.jsonwebtoken.lang.Assert;
import it.pagopa.interop.authorization.domain.KeyPairDecorator;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.KeyPairGeneratorUtil;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceAdditionDetailsSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychain;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import it.pagopa.interop.producerkeychain.ProducerKeychainClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.m2m.common.AbstractCommonSteps;
import it.pagopa.pn.interop.cucumber.utility.delay_service.DelayService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class ProducerKeychainSteps extends AbstractCommonSteps<ProducerKeychain, UUID> {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final IdentityService identityService;
    private final ProducerKeychainClient producerKeychainClient;
    private final DelayService delayService;

    protected ProducerKeychainSteps(SharedStepsContext context,
        ClientTokenConfigurator clientTokenConfigurator,
        DelayService delayService) {
        super("producerKeychain", clientTokenConfigurator.getProducerKeychainClient(), context);
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.identityService = context.getIdentityService();
        this.producerKeychainClient = clientTokenConfigurator.getProducerKeychainClient();
        this.delayService = delayService;
    }

    @And("l'utente {string} di {string} crea un portachiavi erogatore")
    public void createProducerKeychain(String ruolo, String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, ruolo));
        UUID userId = identityService.getUserId(tenant, ruolo);
        String entityId = RandomStringUtils.insecure().nextNumeric(5);
        UUID producerKeychainId = producerKeychainClient.create(new ProducerKeychainSeed()
            .name("portachiavi-erogatore-" + entityId)
            .description("Descrizione portachiavi erogatore " + entityId)
            .addMembersItem(userId)
        );
        getContext().getProducerKeychainCommonContext().addProducerKeychainId(producerKeychainId);
        clientTokenConfigurator.setBearerToken(getContext().getUserToken());
    }

    @And("l'utente {string} di {string} crea un portachiavi erogatore con successo")
    public void successfullyCreateProducerKeychain(String ruolo, String tenant) {
        createProducerKeychain(ruolo, tenant);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, ruolo));
        getContext().getPollingService().makePolling(
            () -> producerKeychainClient.get(getContext().getProducerKeychainCommonContext().getFirstProducerKeychainId()),
            res -> {
                Assert.notNull(res);
                getContext().getProducerKeychainCommonContext().setKeychainName(res.getName());
                return true;
            },
            "La creazione del portachiavi erogatore non ha avuto successo"
        );
        clientTokenConfigurator.setBearerToken(getContext().getUserToken());
    }

    @And("l'utente {string} di {string} associa il portachiavi erogatore all'e-service")
    public void linkProducerKeychainToEService(String ruolo, String tenant) {
        UUID producerKeychainId = getContext().getProducerKeychainCommonContext().getFirstProducerKeychainId();
        UUID eServiceId = getContext().getEServicesCommonContext().getEserviceId();
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, ruolo));
        producerKeychainClient.linkEService(
            producerKeychainId,
            new EServiceAdditionDetailsSeed().eserviceId(eServiceId));
        clientTokenConfigurator.setBearerToken(getContext().getUserToken());
    }

    @And("l'utente {string} di {string} aggiunge l'utente {string} di {string} al portachiavi erogatore")
    public void addUserToProducerKeychain(String ruolo, String tenant, String ruoloUserToAdd, String tenantUserToAdd) {
        UUID producerKeychainId = getContext().getProducerKeychainCommonContext().getFirstProducerKeychainId();
        UUID userId = identityService.getUserId(tenantUserToAdd, ruoloUserToAdd);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, ruolo));
        producerKeychainClient.addUserToProducerKeychain(
            producerKeychainId,
            userId);
        clientTokenConfigurator.setBearerToken(getContext().getUserToken());
        delayService.delay();
    }

    @And("l'utente {string} di {string} associa il portachiavi erogatore all'e-service con successo")
    public void successfullyLinkProducerKeychainToEService(String ruolo, String tenant) {
        linkProducerKeychainToEService(ruolo, tenant);
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, ruolo));
        getContext().getPollingService().makePolling(
            () -> producerKeychainClient.get(getContext().getProducerKeychainCommonContext().getFirstProducerKeychainId()),
            res -> !IterableUtils.isEmpty(res.getEservices()),
            "L'associazione del portachiavi erogatore all'e-service non ha avuto successo"
        );
        clientTokenConfigurator.setBearerToken(getContext().getUserToken());
    }

    @And("l'utente {string} di {string} aggiunge una chiave al portachiavi erogatore")
    public void createProducerKey(String ruolo, String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, ruolo));

        String keyType = "RSA";
        KeyPairDecorator keyPair = KeyPairDecorator.of(keyType, 2048);

        KeySeed keySeed = buildKeySeed(keyPair, keyType);
        UUID producerKeychainId = getContext().getProducerKeychainCommonContext()
            .getFirstProducerKeychainId();
        producerKeychainClient.createProducerKey(producerKeychainId, keySeed);
        getContext().getProducerKeychainCommonContext().addProducerKeyPair(keyPair);
        getContext().getProducerKeychainCommonContext().setProducerKeyName(keySeed.getName());
        clientTokenConfigurator.setBearerToken(getContext().getUserToken());
        delayService.delay();
    }

    @And("l'utente {string} di {string} rimuove tutte le chiavi dal portachiavi erogatore")
    public void removeProducerKey(String ruolo, String tenant) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, ruolo));
        UUID producerKeychainId = getContext().getProducerKeychainCommonContext()
            .getFirstProducerKeychainId();

        delayService.delay();
        producerKeychainClient.getProducerKeysIds(producerKeychainId).forEach(
            key -> producerKeychainClient.deleteProducerKey(producerKeychainId, key));

        clientTokenConfigurator.setBearerToken(getContext().getUserToken());
    }

    @And("l'utente {string} di {string} rimuove l'utente {string} dal portachiavi erogatore")
    public void successfullyRemoveUserFromKeychain(String ruolo, String tenant, String roleToRemove) {
        clientTokenConfigurator.setBearerToken(identityService.getToken(tenant, ruolo));
        UUID producerKeychainId = getContext().getProducerKeychainCommonContext()
                .getFirstProducerKeychainId();
        UUID userIdToRemove = identityService.getUserId(tenant, roleToRemove);
        producerKeychainClient.removeUserFromKeychain(producerKeychainId, userIdToRemove);
        clientTokenConfigurator.setBearerToken(getContext().getUserToken());
    }

    private static KeySeed buildKeySeed(KeyPairDecorator keyPair, String keyType) {
        String encodedPublicKey = keyPair.getDelimitedPublicKeyBase64();
        return KeyPairGeneratorUtil.createKeySeed(encodedPublicKey, KeyType.parse(keyType)).get(0);
    }

    @Override
    public void bindActual(SharedStepsContext context, List<ProducerKeychain> actualEntities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ProducerKeychain> bindExpected(SharedStepsContext context) {
        throw new UnsupportedOperationException();
    }
}
