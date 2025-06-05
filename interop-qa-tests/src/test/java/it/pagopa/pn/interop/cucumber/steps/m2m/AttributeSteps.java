package it.pagopa.pn.interop.cucumber.steps.m2m;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.IM2MAttributeClient;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpStatus;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class AttributeSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IdentityService identityService;
    private final M2MDataPreparationService dataPreparationService;
    private IM2MAttributeClient attributeClient;
    private final HttpCallExecutor httpCallExecutor;
    private final PollingService pollingService;

    private static final String AUTO_GENERATE_ATTRIBUTE_CODE_TOKEN = "GENERATE_AUTO";
    private static final String INVALID_ATTRIBUTE_NAME_TOKEN = "invalid";

    public AttributeSteps(ClientTokenConfigurator clientTokenConfigurator,
                          SharedStepsContext sharedStepsContext,
                          M2MDataPreparationService dataPreparationService) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.dataPreparationService = dataPreparationService;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.pollingService = sharedStepsContext.getPollingService();
        this.attributeClient = clientTokenConfigurator.getM2mAttributeClient();
    }

    @And("viene effettuata la creazione dell'attributo certificato:")
    public void creazioneAttributoCertificato(CertifiedAttributeSeed payloadAttrCert) {

        // Genera il nome se necessario
        String actualName = payloadAttrCert.getName() == null ? String.format("new_attribute_%d", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)) : payloadAttrCert.getName();
        payloadAttrCert.setName(actualName);

        // Genera automaticamente il campo "code" se necessario
        if (payloadAttrCert.getCode().equals(AUTO_GENERATE_ATTRIBUTE_CODE_TOKEN))
            payloadAttrCert.setCode(generateUniqueAttributeCode());

        // Esegue le creazione
        UUID attributeId = dataPreparationService.createCertifiedAttribute(payloadAttrCert)
                .orElseThrow(() -> new IllegalStateException("Certified attribute creation failed"));

        // Aggiorna il contesto corrente
        sharedStepsContext.getAttributeCommonContext().setAttributeId(attributeId);
    }

    @When("l'utente tenta di recuperare il dettaglio dell'attributo certificato {string}")
    public void recuperoAttributoCertificato(String name) {
        // Recupera l'identificativo dell'attributo dal contesto condiviso
        final UUID attributeId = name.equals(INVALID_ATTRIBUTE_NAME_TOKEN)
                ? null
                : this.sharedStepsContext.getAttributeCommonContext()
                .getAttributeId();

        // Esegue la chiamata e verifica che non sia fallita
        HttpStatus clientResponse = httpCallExecutor.performCall(() ->
                attributeClient.getCertifiedAttribute(attributeId)
        );

        if (clientResponse.isError()) {
            Assertions.fail("Fallita la richiesta di recupero del dettaglio dell'attributo certificato \"" + name + "\" con ID: "
                    + attributeId + " — HTTP Status: " + clientResponse);
        }

    }

    @Then("viene restituito il dettaglio dell'attributo certificato {string}")
    public void checkDettaglioAttributoCertificato(String name) {
        UUID attributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();
        checkDettaglioAttributoCertificato(attributeId, name, true);
    }

    @Then("non viene restituito il dettaglio dell'attributo certificato {string}")
    public void dettaglioAttributoCertificatoNonVieneRestituito(String name) {
        checkDettaglioAttributoCertificato(null, name, false);
    }

    private void checkDettaglioAttributoCertificato(UUID expectedId, String name, boolean shouldExist) {
        CertifiedAttribute response = (CertifiedAttribute) httpCallExecutor.getResponse();

        if (shouldExist) {
            Assertions.assertThat(response)
                    .as("La risposta del dettaglio dell'attributo certificato \"" + name + "\" non deve essere null (ID atteso: " + expectedId + ")")
                    .isNotNull();

            Assertions.assertThat(response.getId())
                    .as("L'ID dell'attributo nella risposta non corrisponde a quello atteso (atteso: "
                            + expectedId + ", ottenuto: " + response.getId() + ") per l'attributo certificato \"" + name + "\"")
                    .isEqualByComparingTo(expectedId);

        } else {
            Assertions.assertThat(response)
                    .as("Nessun dettaglio dell'attributo certificato doveva essere restituito, ma è stato ricevuto un oggetto: " + response)
                    .isNull();
        }
    }


    private String generateUniqueAttributeCode() {
        final String prefix = "unique_code";
        long timestamp = System.currentTimeMillis();
        return prefix + "_" + timestamp;
    }

}
