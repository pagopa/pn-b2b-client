package it.pagopa.pn.interop.cucumber.steps.m2m.attribute;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.attribute.service.enums.AttributeRequestType;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttributeSeed;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.M2MDataPreparationService;
import org.assertj.core.api.Assertions;

import java.util.Optional;
import java.util.UUID;

public class AttributeSteps {

    private final SharedStepsContext sharedStepsContext;
    private final M2MDataPreparationService dataPreparationService;

    public AttributeSteps(SharedStepsContext sharedStepsContext,
                          M2MDataPreparationService dataPreparationService) {
        this.sharedStepsContext = sharedStepsContext;
        this.dataPreparationService = dataPreparationService;
    }

    @And("viene effettuata la creazione dell'attributo certificato")
    public void creazioneAttributoCertificato(CertifiedAttributeSeed payloadAttrCert) {

        // Esegue le creazione
        Optional<UUID> attributeId = dataPreparationService.createCertifiedAttribute(payloadAttrCert);

        // Aggiorna il contesto corrente
        sharedStepsContext.getAttributeCommonContext().setAttributeId(attributeId.orElse(null));
    }

    @When("l'utente tenta di recuperare il dettaglio dell'attributo certificato con id {attributeRequestType}")
    public void recuperoAttributoCertificato(AttributeRequestType requestType) {
        UUID inputAttributeId;

        // Determina l'ID da utilizzare in al request type
        switch (requestType) {
            case NULL_ID -> inputAttributeId = null;

            case INVALID_ID -> inputAttributeId = UUID.randomUUID();

            case VALID -> inputAttributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();

            default -> throw new IllegalStateException("Unexpected value: " + requestType);
        }

        // Effettua la chiamata per ottenere il dettaglio, se disponibile
        UUID certifiedAttributeId = dataPreparationService.getCertifiedAttribute(inputAttributeId)
                .map(CertifiedAttribute::getId)
                .orElse(null);

        // Salva l'eventuale risultato nel contesto condiviso
        sharedStepsContext.getAttributeCommonContext().setAttributeId(certifiedAttributeId);
    }

    @When("l'utente tenta di recuperare il dettaglio dell'attributo certificato")
    public void recuperoAttributoCertificato() {
        this.recuperoAttributoCertificato(AttributeRequestType.VALID);
    }

    @Then("non viene restituito il dettaglio dell'attributo certificato")
    public void dettaglioAttributoCertificatoNonVieneRestituito() {
        checkDettaglioAttributoCertificato(null, "", false);
    }

    @Then("viene restituito il dettaglio dell'attributo certificato")
    public void dettaglioAttributoCertificatoVieneRestituito(){
        UUID attributeId = sharedStepsContext.getAttributeCommonContext().getAttributeId();
        checkDettaglioAttributoCertificato(attributeId, "", true);
    }

    private void checkDettaglioAttributoCertificato(UUID expectedId, String name, boolean shouldExist) {
        UUID id = this.sharedStepsContext.getAttributeCommonContext().getAttributeId();

        if (shouldExist) {
            Assertions.assertThat(id)
                    .as("La risposta del dettaglio dell'attributo certificato \"" + name + "\" non deve essere null (ID atteso: " + expectedId + ")")
                    .isNotNull();

            Assertions.assertThat(id)
                    .as("L'ID dell'attributo nella risposta non corrisponde a quello atteso (atteso: "
                            + expectedId + ", ottenuto: " + id + ") per l'attributo certificato \"" + name + "\"")
                    .isEqualByComparingTo(expectedId);

        } else {
            Assertions.assertThat(id)
                    .as("Nessun dettaglio dell'attributo certificato doveva essere restituito, ma è stato ricevuto un oggetto con id: " + id)
                    .isNull();
        }
    }
}
