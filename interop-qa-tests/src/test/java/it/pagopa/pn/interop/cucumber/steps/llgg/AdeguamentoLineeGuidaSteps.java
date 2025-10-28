package it.pagopa.pn.interop.cucumber.steps.llgg;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AdeguamentoLineeGuidaSteps {
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final SharedStepsContext sharedStepsContext;
    private final IHttpExecutor httpCallExecutor;

    public AdeguamentoLineeGuidaSteps(ClientTokenConfigurator clientTokenConfigurator, SharedStepsContext sharedStepsContext, IHttpExecutor httpCallExecutor) {
        this.clientTokenConfigurator = clientTokenConfigurator;
        this.sharedStepsContext = sharedStepsContext;
        this.httpCallExecutor = httpCallExecutor;
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


    public void userRequireTemplateVersionAndCheck(String version, Consumer<RiskAnalysisFormConfig> action) {
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

    @When("l'utente aggiunge un'analisi del rischio custom")
    public void addCustomRiskAnalysis() {
        clientTokenConfigurator.setBearerToken(sharedStepsContext.getUserToken());

        // qui metti il tuo JSON come stringa
        String json = "{\n" +
                "    \"riskAnalysis\": [\n" +
                "        {\n" +
                "            \"createdAt\": \"2025-10-27T18:38:03.614Z\",\n" +
                "            \"id\": \"66023227-4905-48ab-83b2-949380f1eb05\",\n" +
                "            \"name\": \"prova finalita\",\n" +
                "            \"riskAnalysisForm\": {\n" +
                "                \"answers\": {\n" +
                "                    \"checkedExistenceMereCorrectnessInteropCatalogue\": [\"true\"],\n" +
                "                    \"confirmPricipleIntegrityAndDiscretion\": [\"true\"],\n" +
                "                    \"dataDownload\": [\"NO\"],\n" +
                "                    \"declarationConfirmGDPR\": [\"true\"],\n" +
                "                    \"deliveryMethod\": [\"CLEARTEXT\"],\n" +
                "                    \"doneDpia\": [\"NO\"],\n" +
                "                    \"institutionalPurpose\": [\"prova prova prova\"],\n" +
                "                    \"isRequestOnBehalfOfThirdParties\": [\"NO\"],\n" +
                "                    \"knowsDataQuantity\": [\"NO\"],\n" +
                "                    \"legalBasis\": [\"CONTRACT\"],\n" +
                "                    \"personalDataTypes\": [\"WITH_NON_IDENTIFYING_DATA\"],\n" +
                "                    \"policyProvided\": [\"YES\"],\n" +
                "                    \"policyProvidedMedium\": [\"PRINT\"],\n" +
                "                    \"purpose\": [\"INSTITUTIONAL\"],\n" +
                "                    \"purposePursuit\": [\"MERE_CORRECTNESS\"],\n" +
                "                    \"usesPersonalData\": [\"YES\"]\n" +
                "                },\n" +
                "                \"riskAnalysisId\": \"66023227-4905-48ab-83b2-949380f1eb05\",\n" +
                "                \"version\": \"3.1\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

            // leggo solo il nodo riskAnalysis[0]
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(json);
            com.fasterxml.jackson.databind.JsonNode raNode = root.path("riskAnalysis").get(0);

            // mappo in RiskAnalysis (la tua domain class)
            RiskAnalysis customRiskAnalysis = mapper.treeToValue(raNode, RiskAnalysis.class);

            // costruisco la request per l’API
            httpCallExecutor.performCall(
                    () -> clientTokenConfigurator.getEServiceClient().addRiskAnalysisToEService(
                            sharedStepsContext.getEServicesCommonContext().getEserviceId(),
                            new it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed()
                                    .name(customRiskAnalysis.getName())
                                    .riskAnalysisForm(customRiskAnalysis.getRiskAnalysisForm())
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Errore durante la creazione dell'analisi del rischio custom", e);
        }
    }

}
