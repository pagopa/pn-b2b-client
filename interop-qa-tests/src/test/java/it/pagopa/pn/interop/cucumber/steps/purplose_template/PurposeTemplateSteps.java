package it.pagopa.pn.interop.cucumber.steps.purplose_template;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.identity.IdentityService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.service.IPurposeTemplateClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class PurposeTemplateSteps {

    private final IPurposeTemplateClient purposeTemplateClient;

    private CreatedResource createdTemplate;

    private PurposeTemplateWithCompactCreator purposeTemplateWithCompactCreator;

    private PurposeTemplate purposeTemplate;

    private HttpStatusCodeException error;

    private List<CompactPurposeTemplateEService> linkedEServices;

    private final SharedStepsContext sharedStepsContext;

    private final ClientTokenConfigurator clientTokenConfigurator;

    private final IdentityService identityService;

    private final IHttpExecutor httpCallExecutor;

    public PurposeTemplateSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        this.sharedStepsContext = sharedStepsContext;
        this.identityService = sharedStepsContext.getIdentityService();
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.purposeTemplateClient = clientTokenConfigurator.getPurposeTemplateClient();
    }

    //Il seguente metodo crea un purpose template in stato draft
    @When("viene creato un nuovo purpose template")
    public void createPurposeTemplate() {
        PurposeTemplateSeed request = new PurposeTemplateSeed();
        request.setPurposeTitle(getTitleWithDate());
        request.setPurposeDescription("test");

        boolean success = false;
        CreatedResource response = null;
        try {
            response = purposeTemplateClient.createPurposeTemplate(request);
            success = true;
            this.createdTemplate = response;
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
        if (success) {
            assertThat(createdTemplate).as("Il purpose template creato non dev'essere null").isNotNull();
        }
    }

    private String getTitleWithDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);
        return "purpose_template_" + formattedDate;
    }

    @When("si effettua la get del purpose template {exists}")
    public void getPurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();

        httpCallExecutor.performCall(() -> purposeTemplateClient.getPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplateWithCompactCreator = (PurposeTemplateWithCompactCreator) httpCallExecutor.getResponse();
            assertThat(purposeTemplateWithCompactCreator).as("Il risultato della get del purpose con id" + createdTemplate.getId() + " non dev'essere null").isNotNull();
        }
    }

    @When("si effettua la get by creator di tutti i purpose template in stato {string}")
    public void getAllPurposeTemplatesByCreator(String status) {
        List<PurposeTemplateState> state;
        switch (status.toUpperCase()) {
            case "ANY" -> state = null;
            case "ACTIVE" -> state = List.of(PurposeTemplateState.ACTIVE);
            case "DRAFT" -> state = List.of(PurposeTemplateState.DRAFT);
            case "SUSPENDED" -> state = List.of(PurposeTemplateState.SUSPENDED);
            case "ARCHIVED" -> state = List.of(PurposeTemplateState.ARCHIVED);
            default -> throw new IllegalArgumentException("Invalid PurposeTemplateState: " + status);
        }
        httpCallExecutor.performCall(() -> purposeTemplateClient.getCreatorPurposeTemplates(1, 10, null, null, state));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            CreatorPurposeTemplates creatorPurposeTemplates = (CreatorPurposeTemplates) httpCallExecutor.getResponse();
            assertThat(creatorPurposeTemplates).as("Il risultato della get dei purpose template by Creator" + createdTemplate.getId() + " non dev'essere null").isNotNull();
        }
    }

    @When("si effettua la get di tutti i purpose template con titolo {string}")
    public void todo(String title) {
        String titleFilter = title.equalsIgnoreCase("ANY") ? null : title;
        boolean success = false;
        try {
            CatalogPurposeTemplates catalogPurposeTemplates = purposeTemplateClient.getCatalogPurposeTemplates(1, 10, titleFilter, null, null, null, true);
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
        if (success) {
        }
    }

    @When("si aggiorna il purpose template {exists}")
    public void updatePurposeTemplate(boolean exists) {
        PurposeTemplateSeed updateRequest = new PurposeTemplateSeed();
        updateRequest.setPurposeTitle("updated_" + getTitleWithDate());
        updateRequest.setPurposeDescription("Updated description");

        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();
        boolean success = false;
        PurposeTemplate updatedPurposeTemplate = null;
        try {
            updatedPurposeTemplate = purposeTemplateClient.updatePurposeTemplate(ptId, updateRequest);
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
        if (success) {
            assertThat(updatedPurposeTemplate).as("Il template aggiornato non dev'essere null").isNotNull();
            assertThat(updatedPurposeTemplate.getPurposeDescription()).as("La descrizione deve risultare aggiornata").contains("Updated description");
            assertThat(updatedPurposeTemplate.getPurposeTitle()).as("Il titolo deve risultare aggiornato").contains("Updated title");
        } else {
            assertThat(updatedPurposeTemplate).as("Il template aggiornato dev'essere null").isNull();
            assertThat(error).as("L'operazione di update deve aver generato un errore").isNotNull();
        }
    }

    @When("si cancella il purpose template {exists}")
    public void deletePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();
        boolean success = false;
        try {
            purposeTemplateClient.deletePurposeTemplate(ptId);
            success = true;
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
        if (exists) {
            PurposeTemplateWithCompactCreator pt = getPurposeTemplateById(ptId);
            if (success) {
                assertThat(pt).as("Dopo la delete (OK), il result della get del purpose template con id " + ptId + " dovrebbe essere null").isNull();
            } else {
                assertThat(pt).as("Dopo la delete (KO), il result della get del purpose template con id " + ptId + " non dovrebbe essere null").isNotNull();
            }
        }
    }

    public PurposeTemplateWithCompactCreator getPurposeTemplateById(UUID purposeTemplateId) {
        PurposeTemplateWithCompactCreator pt = null;
        try {
            pt = purposeTemplateClient.getPurposeTemplate(purposeTemplateId);
            this.purposeTemplateWithCompactCreator = pt;
        } catch (HttpStatusCodeException e) {
            log.info("PurposeTemplate with id " + purposeTemplateId + " not found");
        }
        return pt;
    }

    @And("il purpose template {exists} viene associato all'e-service")
    public void linkPurposeTemplateToEservice(boolean exists) {
        assertThat(sharedStepsContext.getEServicesCommonContext().getEserviceId()).as("L'id dell'eService creato non dev'essere null").isNotNull();
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();

        InlineObject2 o2 = new InlineObject2();
        o2.setEserviceId(eServiceId);

        boolean success = false;
        try {
            purposeTemplateClient.linkEServiceToPurposeTemplate(ptId, o2);
            success = true;
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
        if (exists) {
            EServiceDescriptorsPurposeTemplate esDescriptorsPt = purposeTemplateClient.getPurposeTemplateEServices(ptId, 1, 10, null, null);
            assertThat(esDescriptorsPt).as("L'output della get degli e-service associati non dev'essere null").isNotNull();
            assertThat(esDescriptorsPt.getResults()).as("Il result dell'output della get degli e-service associati non dev'essere null").isNotNull();
            List<EServiceDescriptorPurposeTemplateWithCompactEServiceAndDescriptor> resultList = esDescriptorsPt.getResults();
            CompactPurposeTemplateEService linkedEService = resultList.stream().filter(x -> x.getEservice().getId().equals(eServiceId)).findFirst().orElse(null).getEservice();
            if (success) {
                assertThat(linkedEService).as("L'e-service con id " + eServiceId + " non risulta associato al purpose template con id " + ptId).isNotNull();
            } else {
                assertThat(linkedEService).as("L'e-service con id " + eServiceId + " risulta associato al purpose template con id " + ptId).isNull();
            }
        }
    }


    @Then("si effettua la get degli e-service associati al purpose template {exists}")
    public void getPurposeTemplateEservices(boolean exists) {
        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();
        EServiceDescriptorsPurposeTemplate esDescriptorsPt = null;
        boolean success = false;
        try {
            esDescriptorsPt = purposeTemplateClient.getPurposeTemplateEServices(ptId, 1, 10, null, null);
            success = true;
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
        if (success) {
            assertThat(esDescriptorsPt).as("L'output della get degli e-service associati non dev'essere null").isNotNull();
            assertThat(esDescriptorsPt.getResults()).as("Il result dell'output della get degli e-service associati non dev'essere null").isNotNull();
            List<EServiceDescriptorPurposeTemplateWithCompactEServiceAndDescriptor> resultList = esDescriptorsPt.getResults();
            linkedEServices = resultList.stream().map(x -> x.getEservice()).toList();
            checkEServicesList(true);
        }
    }

    @When("la lista di e-service associati {contains} l'e-service atteso")
    public void checkEServicesList(boolean contains) {
        assertThat(sharedStepsContext.getEServicesCommonContext().getEserviceId()).as("L'id dell'eService creato non dev'essere null").isNotNull();
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        assertThat(linkedEServices).as("La lista di e-services associati al purpose template non dev'essere null").isNotNull();
        assertThat(linkedEServices).asList().as("La lista di e-services associati al purpose template non dev'essere null").isNotEmpty();

        CompactPurposeTemplateEService found = linkedEServices.stream().filter(x -> x.getId().equals(eServiceId)).findFirst().orElse(null);
        if (contains) {
            assertThat(found).as("La lista di e-services associati dovrebbe contenere l-es con id: " + eServiceId).isNotNull();
        } else {
            assertThat(found).as("La lista di e-services associati non dovrebbe contenere l-es con id: " + eServiceId).isNull();
        }
    }

    @And("il purpose template {exists} viene disassociato dall'e-service")
    public void unlinkPurposeTemplateToEservice(boolean exists) {
        assertThat(sharedStepsContext.getEServicesCommonContext().getEserviceId()).as("L'id dell'eService creato non dev'essere null").isNotNull();
        UUID eServiceId = sharedStepsContext.getEServicesCommonContext().getEserviceId();

        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();

        InlineObject3 o3 = new InlineObject3();
        o3.setEserviceId(eServiceId);

        boolean success = false;
        try {
            purposeTemplateClient.unlinkEServiceToPurposeTemplate(ptId, o3);
            success = true;
        } catch (HttpStatusCodeException e) {
            this.error = e;
        }
        if (exists) {
            EServiceDescriptorsPurposeTemplate esDescriptorsPt = purposeTemplateClient.getPurposeTemplateEServices(ptId, 1, 10, null, null);
            assertThat(esDescriptorsPt).as("L'output della get degli e-service associati non dev'essere null").isNotNull();
            assertThat(esDescriptorsPt.getResults()).as("Il result dell'output della get degli e-service associati non dev'essere null").isNotNull();
            List<EServiceDescriptorPurposeTemplateWithCompactEServiceAndDescriptor> resultList = esDescriptorsPt.getResults();
            CompactPurposeTemplateEService linkedEService = resultList.stream().filter(x -> x.getEservice().getId().equals(eServiceId)).findFirst().orElse(null).getEservice();
            if (success) {
                assertThat(linkedEService).as("L'e-service con id " + eServiceId + " risulta ancora associato al purpose template con id " + ptId).isNull();
            } else {
                assertThat(linkedEService).as("L'e-service con id " + eServiceId + "non risulta più associato al purpose template con id " + ptId).isNotNull();
            }
        }
    }

    @When("si aggiorna il purpose template {exists} che è in stato {string}")
    public void updatePurposeTemplateInStatus(boolean exists, String status) {
        PurposeTemplateWithCompactCreator pt = purposeTemplateClient.getPurposeTemplate(createdTemplate.getId());
        assertThat(pt.getState().getValue()).as("TODO MATTEO").isEqualToIgnoringCase(status);
        updatePurposeTemplate(exists);
    }

    @And("il purpose template {exists} viene spostato in stato {ptState}")
    public void changePurposeTemplateState(boolean exists, PurposeTemplateState ptState) {
        switch (ptState) {
            case DRAFT -> System.out.println("TODO");
            case ACTIVE -> activatePurposeTemplate(exists);
            case SUSPENDED -> suspendPurposeTemplate(exists);
            case ARCHIVED -> System.out.println("TODO archive");
        }
    }

    @And("il purpose template {exists} viene riattivato")
    public void reactivateSuspendedPurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.unsuspendPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il purpose template restituito non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getState()).as("Il purpose template non risulta attivo").equals(PurposeTemplateState.ACTIVE);
        }
    }

    private void activatePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.publishPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il purpose template restituito non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getState()).as("Il purpose template non risulta attivo").equals(PurposeTemplateState.ACTIVE);
        }
    }

    private void suspendPurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.suspendPurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il purpose template restituito non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getState()).as("Il purpose template non risulta sospeso").equals(PurposeTemplateState.SUSPENDED);
        }
    }

    private void archivePurposeTemplate(boolean exists) {
        UUID ptId = exists ? createdTemplate.getId() : UUID.randomUUID();
        httpCallExecutor.performCall(() -> purposeTemplateClient.archivePurposeTemplate(ptId));
        if (httpCallExecutor.getResponseStatus().is2xxSuccessful()) {
            purposeTemplate = (PurposeTemplate) httpCallExecutor.getResponse();
            assertThat(purposeTemplate).as("Il purpose template restituito non dev'essere null").isNotNull();
            assertThat(purposeTemplate.getState()).as("Il purpose template non risulta archiviato").equals(PurposeTemplateState.ARCHIVED);
        }
    }

    @And("viene creato un nuovo purpose template in stato {ptState}")
    public void vieneCreatoUnNuovoPurposeTemplateInStato(PurposeTemplateState ptState) {
        createPurposeTemplate();
        switch (ptState) {
            case ACTIVE -> activatePurposeTemplate(true);
            case SUSPENDED -> {
                activatePurposeTemplate(true);
                suspendPurposeTemplate(true);
            }
            case ARCHIVED -> {
                activatePurposeTemplate(true);
                suspendPurposeTemplate(true);
                archivePurposeTemplate(true);
            }
        }
    }
}
