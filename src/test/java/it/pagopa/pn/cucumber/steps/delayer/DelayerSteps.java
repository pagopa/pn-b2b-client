package it.pagopa.pn.cucumber.steps.delayer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import it.pagopa.pn.cucumber.utils.LambdaInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.*;
import java.util.stream.Collectors;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class DelayerSteps {

    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String LAMBDA_NAME = "arn:aws:lambda:eu-south-1:830192246553:function:pn-testDelayerLambda";
    private List<JsonNode> lastResult;

    @Given("il CSV {string} è importato nella tabella di test tramite lambda {string}")
    public void importa_csv_tramite_lambda(String csvName, String lambdaArn) {
        String payload = """
        {
          "operationType": "IMPORT_DATA",
          "parameters": []
        }
        """;
        String result = lambdaInvoker.invokeMyLambda(lambdaArn, payload);
        log.info("Importazione CSV [{}]: {}", csvName, result);
    }

    @When("viene eseguito l'algoritmo tramite lambda")
    public void esegui_algoritmo() {
        String payload = """
        {
          "operationType": "RUN_ALGORITHM",
          "parameters": []
        }
        """;
        String result = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
        log.info("Esecuzione algoritmo: {}", result);
    }

    @Then("le notifiche del requestId {string} sono elaborate in ordine di priorità:")
    public void verifica_ordine_priorita(String requestId, io.cucumber.datatable.DataTable expectedOrder) throws Exception {
        String payload = String.format("""
        {
          "operationType": "GET_BY_REQUEST_ID",
          "parameters": [ "%s" ]
        }
        """, requestId);

        String lambdaResult = lambdaInvoker.invokeMyLambda(LAMBDA_NAME, payload);
        lastResult = Arrays.asList(objectMapper.readValue(lambdaResult, JsonNode[].class));

        List<JsonNode> rs = new ArrayList<>();
        List<JsonNode> secondiTentativi = new ArrayList<>();
        List<JsonNode> altri = new ArrayList<>();

        for (JsonNode node : lastResult) {
            String tipo = node.path("productType").asText();
            int attempt = node.path("attempt").asInt();
            if ("RS".equals(tipo)) {
                rs.add(node);
            } else if (attempt == 1) {
                secondiTentativi.add(node);
            } else {
                altri.add(node);
            }
        }

        assertOrdinati(rs, "prepareRequestDate", "RS");
        assertOrdinati(secondiTentativi, "prepareRequestDate", "SECONDO_TENTATIVO");
        assertOrdinati(altri, "notificationSentAt", "ALTRO");
    }

    private void assertOrdinati(List<JsonNode> list, String campo, String label) {
        List<String> valori = list.stream()
                .map(n -> n.path(campo).asText())
                .collect(Collectors.toList());

        List<String> ordinati = new ArrayList<>(valori);
        Collections.sort(ordinati);

        Assertions.assertEquals(ordinati, valori,
                String.format("La categoria %s non è ordinata per %s", label, campo));
    }
}
