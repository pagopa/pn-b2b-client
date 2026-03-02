package it.pagopa.pn.interop.cucumber.steps.llgg;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import it.pagopa.pn.interop.cucumber.utility.FileUtils;
import org.assertj.core.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AdeguamentoLineeGuidaSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public AdeguamentoLineeGuidaSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = sharedStepsContext.getHttpCallExecutor();
    }

    @When("verifica che la versione 3.1 dell'analisi del rischio includa domande inerenti all'uso di dati personali")
    public void checkRiskAnalysis3_1() {
        this.userRequireTemplateVersionAndCheck("3.1", (risk) -> {
            List<String> questionIds = risk.getQuestions()
                    .stream()
                    .map(q -> q.getId())
                    .collect(Collectors.toList());

            Assertions.assertThat(questionIds)
                    .as("Verifica che le domande includano l'uso di dati personali e di dati personali di terze parti")
                    .contains("usesPersonalData", "usesThirdPartyPersonalData", "usesThirdPartyConfidentialData");
        });
    }

    @When("l'utente aggiunge un'analisi del rischio con un flag relativo ai dati personali impostato a {string}")
    public void addCustomRiskAnalysis(String flagCondition) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        RiskAnalysis customRiskAnalysis = getRiskAnalysis(flagCondition);

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getEServiceClient().addRiskAnalysisToEService(
                        sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                        new it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed()
                                .name(customRiskAnalysis.getName())
                                .riskAnalysisForm(customRiskAnalysis.getRiskAnalysisForm())
                )
        );
    }

    public static RiskAnalysis getRiskAnalysis(String flagPersonalData) {
        RiskAnalysis customRiskAnalysis;

        if (flagPersonalData.equals("true")) {
            customRiskAnalysis = FileUtils.readJsonAs("classpath:it/pagopa/pn/cucumber/llgg/personal-data-true-risk-analysis.json", RiskAnalysis.class);
        } else if (flagPersonalData.equals("false")) {
            customRiskAnalysis = FileUtils.readJsonAs("classpath:it/pagopa/pn/cucumber/llgg/personal-data-false-risk-analysis.json", RiskAnalysis.class);
        } else {
            throw new RuntimeException(String.format("Flag condition non riconosciuto: %s", flagPersonalData));
        }
        return customRiskAnalysis;
    }

    @When("viene settato il personalDataFlag a {string} passando un {string} inesistente")
    public void updatePersonalDataFlagAfterPublication(String flagPersonalData, String target) {
        updatePersonalDataFlagInternal(target.equalsIgnoreCase("eServiceId"), UUID.randomUUID(), flagPersonalData, false);
    }

    @When("viene settato il personalDataFlag a {string} nell'eservice appena creato")
    public void updatePersonalDataFlag(String flagPersonalData) {
        UUID eserviceId = this.sharedStepsContext.getEServicesCommonContext().getEserviceId();
        updatePersonalDataFlagInternal(true, eserviceId, flagPersonalData, false);
    }

    @When("viene settato il personalDataFlag a {string} nell'eservice template appena creato")
    public void updatePersonalDataFlagInEserviceTemplate(String flagPersonalData) {
        UUID templateId = this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged().getId();
        updatePersonalDataFlagInternal(false, templateId, flagPersonalData, false);
    }

    @When("verifica che il flagPersonalData presente nell'istanza dell'eServiceTemplate coincida con quanto specificato nel template")
    public void checkEserviceTemplateIstance() {
        EServiceTemplateInfo template = this.sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();
        UUID eServiceId = this.sharedStepsContext.getEServiceTemplateStepContext().getLastEServiceIdCreatedFromTemplate();

        if (template == null)
            throw new RuntimeException("Nessun template gestito trovato nel contesto: impossibile verificare l'istanza di eService.");
        if (eServiceId == null)
            throw new RuntimeException("Nessun eService creato a partire dal template trovato nel contesto: impossibile recuperare i dettagli.");

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(eServiceId)
        );

        Assertions.assertThat(this.httpCallExecutor.getResponseStatus().is2xxSuccessful())
                .as("La chiamata GET per i dettagli dell'eService (id=%s) non è andata a buon fine", eServiceId)
                .isTrue();

        ProducerEServiceDetails eService = (ProducerEServiceDetails) httpCallExecutor.getResponse();

        Assertions.assertThat(eService.getPersonalData())
                .as("Il flag personalData dell'eService (id=%s) non corrisponde a quello del template (id=%s)", eServiceId, template.getId())
                .isEqualTo(template.getPersonalData());
    }

    @When("verifica che il flagPersonalData presente nell'eService sia {string}")
    public void checkEserviceIstance(String flagPersonalData) {
        UUID eServiceId = this.sharedStepsContext.getEServicesCommonContext().getEserviceId();
        if (eServiceId == null)
            throw new RuntimeException("Nessun eService creato a partire dal template trovato nel contesto: impossibile recuperare i dettagli.");

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDetails(eServiceId)
        );

        Assertions.assertThat(this.httpCallExecutor.getResponseStatus().is2xxSuccessful())
                .as("La chiamata GET per i dettagli dell'eService (id=%s) non è andata a buon fine", eServiceId)
                .isTrue();

        ProducerEServiceDetails eService = (ProducerEServiceDetails) httpCallExecutor.getResponse();
        Boolean expectedFlag = flagPersonalData.equals("undefined") ? null : flagPersonalData.equalsIgnoreCase("true");

        Assertions.assertThat(eService.getPersonalData())
                .as("Il flag personalData dell'eService (id=%s) non corrisponde a (id=%s)", eServiceId, expectedFlag)
                .isEqualTo(expectedFlag);
    }

    @When("verifica che il flagPersonalData presente nell'eService con il descrittore appena creato sia {string}")
    public void checkEserviceIstanceWithDescriptor(String flagPersonalData) {
        UUID eServiceId = this.sharedStepsContext.getEServicesCommonContext().getEserviceId();
        UUID descriptorId = this.sharedStepsContext.getEServicesCommonContext().getDescriptorId();

        if (eServiceId == null) throw new RuntimeException("Nessun eServiceId trovato.");
        if (descriptorId == null) throw new RuntimeException("Nessun descriptorId trovato");

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getProducerClient().getProducerEServiceDescriptor(eServiceId, descriptorId)
        );

        Assertions.assertThat(this.httpCallExecutor.getResponseStatus().is2xxSuccessful())
                .as("La chiamata GET per i dettagli dell'eService (id=%s) e descriptor (id=%s) non è andata a buon fine", eServiceId, descriptorId)
                .isTrue();

        ProducerEServiceDescriptor descriptor = (ProducerEServiceDescriptor) httpCallExecutor.getResponse();
        Boolean expectedFlag = flagPersonalData.equals("undefined") ? null : flagPersonalData.equalsIgnoreCase("true");

        Assertions.assertThat(descriptor.getEservice().getPersonalData())
                .as("Il flag personalData dell'eService (id=%s) con descrittore (id=%s) non corrisponde a (id=%s)", eServiceId, descriptorId, expectedFlag)
                .isEqualTo(expectedFlag);
    }

    @When("verifica che il flagPersonalData presente nella nuova versione dell'eServiceTemplate sia {string}")
    public void checkEserviceTemplate(String flagPersonalData) {
        EServiceTemplateInfo lastTemplate = sharedStepsContext.getEServiceTemplateStepContext().getLastTemplateManaged();

        if (lastTemplate == null)
            throw new IllegalStateException("Nessun template gestito trovato nel contesto: impossibile verificare l'eServiceTemplate.");

        UUID eServiceTemplateId = lastTemplate.getId();
        UUID eServiceTemplateVersionId = lastTemplate.getLastVersionId();

        if (eServiceTemplateId == null)
            throw new IllegalStateException("L'ID del template è nullo: impossibile effettuare la chiamata per i dettagli.");
        if (eServiceTemplateVersionId == null)
            throw new IllegalStateException("L'ID della versione del template è nullo: impossibile effettuare la chiamata per i dettagli.");

        httpCallExecutor.performCall(
                () -> this.clientTokenConfigurator.getEServiceTemplateClient()
                        .getEServiceTemplateVersionWithHttpInfo(eServiceTemplateId, eServiceTemplateVersionId)
        );

        Assertions.assertThat(this.httpCallExecutor.getResponseStatus().is2xxSuccessful())
                .as("La chiamata GET per il template (id=%s, versione=%s) non è andata a buon fine",
                        eServiceTemplateId, eServiceTemplateVersionId)
                .isTrue();

        ResponseEntity<EServiceTemplateVersionDetails> response = (ResponseEntity<EServiceTemplateVersionDetails>) httpCallExecutor.getResponse();

        if (response == null || response.getBody() == null) {
            throw new IllegalStateException(String.format(
                    "La risposta per il template (id=%s, versione=%s) è vuota",
                    eServiceTemplateId, eServiceTemplateVersionId
            ));
        }

        EServiceTemplateVersionDetails templateVersion = response.getBody();
        Boolean expectedFlag = flagPersonalData.equals("undefined") ? null : flagPersonalData.equalsIgnoreCase("true");

        Assertions.assertThat(templateVersion.getEserviceTemplate().getPersonalData())
                .as("Il flag personalData dell'eServiceTemplate (id=%s, versione=%s) non corrisponde al valore atteso: %s",
                        eServiceTemplateId, eServiceTemplateVersionId, expectedFlag)
                .isEqualTo(expectedFlag);
    }

    @When("i {int} e-service recuperati hanno il flagPersonalData settato a {string}")
    public void searchCreatedEServices(int toSearch, String flagPersonalData) {

        Boolean personalData = flagPersonalData.equals("undefined") ? null : flagPersonalData.equalsIgnoreCase("true");

        for (int i = 0; i < toSearch; i++) {
            String query = String.format("eservice-%d-%d", i, sharedStepsContext.getTestSeed());

            httpCallExecutor.performCall(
                    () -> clientTokenConfigurator.getProducerClient()
                            .getProducerEServices(0, 30, query, null, null, personalData)
            );

            Assertions.assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful())
                    .as("La chiamata GET /producers/eservices con query='%s' non è andata a buon fine", query)
                    .isTrue();

            ProducerEServices results = (ProducerEServices) httpCallExecutor.getResponse();
            if (results == null) throw new IllegalStateException("La risposta della ricerca eServices è vuota");

            List<String> resultsName = results.getResults().stream().map(ProducerEService::getName).toList();
            Assertions.assertThat(resultsName).as(String.format("Il nome '%s' non è contenuto tra i risultati", query)).contains(query);

        }
    }

    @When("i {int} e-service template recuperati hanno il flagPersonalData settato a {string}")
    public void searchCreatedEServiceTemplates(int toSearch, String flagPersonalData) {

        Boolean personalData = flagPersonalData.equals("undefined") ? null : flagPersonalData.equalsIgnoreCase("true");

        for (int i = 0; i < toSearch; i++) {
            String query = String.format("eservice-template-%d-%d", sharedStepsContext.getTestSeed(), i);

            httpCallExecutor.performCall(
                    () -> clientTokenConfigurator.getEServiceTemplateClient().getEServiceTemplatesCatalog(0, 30, query, null, personalData)
            );

            Assertions.assertThat(httpCallExecutor.getResponseStatus().is2xxSuccessful())
                    .as("La chiamata GET /catalog/eservices/templates con query='%s' non è andata a buon fine", query)
                    .isTrue();

            CatalogEServiceTemplates results = (CatalogEServiceTemplates) httpCallExecutor.getResponse();
            if (results == null) throw new IllegalStateException("La risposta della ricerca eServices è vuota");

            List<String> resultsName = results.getResults().stream().map(CatalogEServiceTemplate::getName).toList();
            Assertions.assertThat(resultsName).as(String.format("Il nome '%s' non è contenuto tra i risultati", query)).contains(query);

        }
    }

    private void userRequireTemplateVersionAndCheck(String version, Consumer<RiskAnalysisFormConfig> action) {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());
        RiskAnalysisFormConfig riskAnalysis;

        httpCallExecutor.performCall(
                () -> clientTokenConfigurator.getPurposeApiClient().retrieveRiskAnalysisConfigurationByVersion(
                        version, sharedStepsContext.getEServicesCommonContext().getEserviceId()
                )
        );

        riskAnalysis = (RiskAnalysisFormConfig) httpCallExecutor.getResponse();
        action.accept(riskAnalysis);
    }

    private void updatePersonalDataFlagInternal(boolean isEservice, UUID id, String flagPersonalData, boolean invalidToken) {
        boolean personalData = Boolean.parseBoolean(flagPersonalData);

        httpCallExecutor.performCall(() -> {
            if (isEservice) {
                EServicePersonalDataFlagUpdateSeed seed = new EServicePersonalDataFlagUpdateSeed();
                seed.setPersonalData(personalData);

                if (invalidToken) {

                } else {
                    clientTokenConfigurator.getEServiceClient()
                            .updateEServicePersonalDataFlagAfterPublication(id, seed);
                }
            } else {
                EServiceTemplatePersonalDataFlagUpdateSeed seed = new EServiceTemplatePersonalDataFlagUpdateSeed();
                seed.setPersonalData(personalData);

                if (invalidToken) {
                    clientTokenConfigurator.getEServiceTemplateClient()
                            .updateEServiceTemplatePersonalDataFlagAfterPublicationWithInvalidToken(id, seed);
                } else {
                    clientTokenConfigurator.getEServiceTemplateClient()
                            .updateEServiceTemplatePersonalDataFlagAfterPublication(id, seed);
                }
            }
        });
    }

}
