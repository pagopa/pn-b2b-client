package it.pagopa.pn.cucumber.steps.templateEngine.service;

import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateEngineContextFactory;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateType;
import it.pagopa.pn.cucumber.steps.templateEngine.escapemod.HtmlEscapeExecution;
import it.pagopa.pn.cucumber.steps.templateEngine.escapemod.HtmlEscapeReflectionHandler;
import it.pagopa.pn.cucumber.steps.templateEngine.escapemod.HtmlEscapeTarget;
import it.pagopa.pn.cucumber.steps.templateEngine.escapemod.HtmlEscapeTargets;
import it.pagopa.pn.cucumber.steps.templateEngine.escapemod.HtmlEscapeUtilities;
import it.pagopa.pn.cucumber.steps.templateEngine.escapemod.HtmlEscapeValidator;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.ITemplateEngineStrategy;
import org.junit.jupiter.api.Assertions;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class HtmlEscapeService {

    private static final String DEFAULT_NOTIFICATION_TYPE = "semplice";
    private static final String DEFAULT_RECIPIENT_TYPE = "PF";

    private final Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy;
    private final TemplateEngineContextFactory contextFactory;
    private final ITemplateEngineClient templateEngineClient;

    public HtmlEscapeService(Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy,
                             TemplateEngineContextFactory contextFactory,
                             ITemplateEngineClient templateEngineClient) {
        this.templateEngineStrategy = templateEngineStrategy;
        this.contextFactory = contextFactory;
        this.templateEngineClient = templateEngineClient;
    }

    public List<HtmlEscapeExecution> executeFieldsFuzzing(String endpoint, String language, String fields) {
        HtmlEscapeTarget target = HtmlEscapeTargets.get(endpoint);

        Map<String, String> fuzzedValues = new LinkedHashMap<>();
        for (String field : HtmlEscapeUtilities.splitFields(fields)) {
            fuzzedValues.put(field, buildFuzzValue());
        }

        return List.of(executeFuzzCall(target, language, fuzzedValues));
    }

    public List<HtmlEscapeExecution> executeCorpusFuzzing(String endpoint, String language, String field) {
        HtmlEscapeTarget target = HtmlEscapeTargets.get(endpoint);
        List<HtmlEscapeExecution> executions = new ArrayList<>();

        for (String value : buildCorpus()) {
            executions.add(executeFuzzCall(target, language, Map.of(field, value)));
        }

        return executions;
    }

    public void verifyEscapingDisabled(List<HtmlEscapeExecution> executions, String format) {
        verifyExecutions(executions, format, false);
    }

    public void verifyEscapingEnabled(List<HtmlEscapeExecution> executions, String format) {
        verifyExecutions(executions, format, true);
    }

    private void verifyExecutions(List<HtmlEscapeExecution> executions, String format, boolean escapingEnabled) {
        Assertions.assertNotNull(executions, "Lista esecuzioni fuzzing nulla");
        Assertions.assertFalse(executions.isEmpty(), "Nessuna esecuzione fuzzing disponibile");

        String expectedFormat = format.toLowerCase();

        for (HtmlEscapeExecution execution : executions) {
            Assertions.assertEquals(
                    expectedFormat,
                    execution.getFormat(),
                    "Il formato dichiarato nello scenario non coincide con quello dell'endpoint "
                            + execution.getEndpoint()
            );

            String rendered = retrieveFuzzedOutput(execution);
            Assertions.assertNotNull(rendered, "Output nullo per endpoint " + execution.getEndpoint());

            String normalizedRendered = HtmlEscapeUtilities.normalizeRendered(rendered, execution.getFormat());

            for (Map.Entry<String, String> entry : execution.getRawValues().entrySet()) {
                String field = entry.getKey();
                String rawValue = HtmlEscapeUtilities.toSingleLine(entry.getValue());

                if (escapingEnabled) {
                    assertEscapingEnabled(execution, field, rawValue, normalizedRendered);
                } else {
                    assertEscapingDisabled(execution, field, rawValue, normalizedRendered);
                }
            }
        }
    }

    private void assertEscapingEnabled(HtmlEscapeExecution execution,
                                       String field,
                                       String rawValue,
                                       String rendered) {
        switch (execution.getFormat()) {
            case "pdf" ->
                    HtmlEscapeValidator.assertPdfValue(execution, field, rawValue, rendered);
            case "html" ->
                    HtmlEscapeValidator.assertHtmlValue(execution, field, rawValue, rendered);
            case "text" ->
                    HtmlEscapeValidator.assertTextValue(execution, field, rawValue, rendered);
            default ->
                    throw new IllegalArgumentException("Formato non supportato: " + execution.getFormat());
        }
    }

    private void assertEscapingDisabled(HtmlEscapeExecution execution,
                                        String field,
                                        String rawValue,
                                        String rendered) {
        switch (execution.getFormat()) {
            case "pdf" ->
                    HtmlEscapeValidator.assertPdfValueEscapingDisabled(execution, field, rawValue, rendered);
            case "html" ->
                    HtmlEscapeValidator.assertHtmlValueEscapingDisabled(execution, field, rawValue, rendered);
            case "text" ->
                    HtmlEscapeValidator.assertTextValue(execution, field, rawValue, rendered);
            default ->
                    throw new IllegalArgumentException("Formato non supportato: " + execution.getFormat());
        }
    }

    private HtmlEscapeExecution executeFuzzCall(HtmlEscapeTarget target,
                                                String language,
                                                Map<String, String> logicalValues) {
        Map<String, String> mappedValues = mapFields(target, logicalValues);

        TemplateEngineResult callResult = target.getTemplateType() != null
                ? retrieveTemplateByStrategy(target, language, mappedValues)
                : retrieveTemplateByClientReflection(target, language, mappedValues);

        Assertions.assertNotNull(
                callResult,
                "Nessuna risposta ottenuta dall'endpoint " + target.getEndpoint()
        );

        return new HtmlEscapeExecution(
                target.getEndpoint(),
                target.getFormat(),
                new LinkedHashMap<>(logicalValues),
                callResult
        );
    }

    private Map<String, String> mapFields(HtmlEscapeTarget target, Map<String, String> logicalValues) {
        Map<String, String> mappedValues = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : logicalValues.entrySet()) {
            String mappedField = target.getFieldMappings().get(entry.getKey());
            if (mappedField == null) {
                throw new IllegalArgumentException(
                        "Campo '" + entry.getKey() + "' non configurato per endpoint " + target.getEndpoint()
                );
            }
            mappedValues.put(mappedField, entry.getValue());
        }

        return mappedValues;
    }

    private TemplateEngineResult retrieveTemplateByStrategy(HtmlEscapeTarget target,
                                                            String language,
                                                            Map<String, String> mappedValues) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("context_recipientType", DEFAULT_RECIPIENT_TYPE);
        parameters.put("recipient_recipientType", DEFAULT_RECIPIENT_TYPE);
        parameters.put("recipientType", DEFAULT_RECIPIENT_TYPE);
        parameters.putAll(mappedValues);

        TemplateType templateType = TemplateType.fromValue(target.getTemplateType().toUpperCase());
        TemplateRequestContext context = contextFactory.createContext(parameters, DEFAULT_NOTIFICATION_TYPE);

        return templateEngineStrategy
                .get(templateType)
                .retrieveTemplate(language, true, context);
    }

    private TemplateEngineResult retrieveTemplateByClientReflection(HtmlEscapeTarget target,
                                                                    String language,
                                                                    Map<String, String> propertyValues) {
        Assertions.assertNotNull(templateEngineClient, "ITemplateEngineClient non disponibile");
        Method clientMethod = findClientMethod(target.getOperationId());

        try {
            Object request = HtmlEscapeReflectionHandler.createDefaultModel(
                    clientMethod.getParameterTypes()[1],
                    0,
                    new LinkedHashSet<>()
            );

            for (Map.Entry<String, String> entry : propertyValues.entrySet()) {
                HtmlEscapeReflectionHandler.setNestedProperty(
                        request,
                        entry.getKey().split("\\."),
                        0,
                        entry.getValue()
                );
            }

            Object languageValue = HtmlEscapeReflectionHandler.createLanguageValue(
                    clientMethod.getParameterTypes()[0],
                    language
            );

            Object response = clientMethod.invoke(templateEngineClient, languageValue, request);
            return HtmlEscapeReflectionHandler.toTemplateEngineResult(response);

        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            if (cause instanceof HttpClientErrorException httpClientErrorException) {
                throw httpClientErrorException;
            }
            if (cause instanceof HttpServerErrorException httpServerErrorException) {
                throw httpServerErrorException;
            }

            throw new IllegalStateException("Errore invocando " + target.getOperationId(), cause);

        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Impossibile costruire/invocare il payload per " + target.getOperationId(),
                    e
            );
        }
    }

    private String retrieveFuzzedOutput(HtmlEscapeExecution execution) {
        return switch (execution.getFormat()) {
            case "pdf" -> {
                Resource resource = execution.getResult().getTemplateFileReturned();
                Assertions.assertNotNull(
                        resource,
                        "PDF non restituito per endpoint " + execution.getEndpoint()
                );
                yield HtmlEscapeUtilities.extractPdfText(resource, execution.getEndpoint());
            }
            case "html", "text" -> {
                String text = execution.getResult().getTemplateHtmlReturned();
                Assertions.assertNotNull(
                        text,
                        "Testo/HTML non restituito per endpoint " + execution.getEndpoint()
                );
                yield text;
            }
            default ->
                    throw new IllegalArgumentException(
                            "Formato non supportato: " + execution.getFormat()
                    );
        };
    }

    private List<String> buildCorpus() {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        return List.of(
                "FUZZ_" + random + "_LT_<_END",
                "FUZZ_" + random + "_GT_>_END",
                "FUZZ_" + random + "_AMP_&_END",
                "FUZZ_" + random + "_QUOTE_\"_END",
                "FUZZ_" + random + "_APOS_'_END",
                "FUZZ_" + random + "_ALL_<>&\"'_END",
                "FUZZ_" + random + "_MARKUP_<b>X</b>_END"
        );
    }

    private String buildFuzzValue() {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "FZ_" + id + "_<b>X</b>_<>&\"'àèì$_€_END";
    }

    private Method findClientMethod(String operationId) {
        String normalizedOperation = HtmlEscapeUtilities.normalize(operationId);

        return Arrays.stream(templateEngineClient.getClass().getMethods())
                .filter(method -> method.getParameterCount() == 2)
                .filter(method -> HtmlEscapeUtilities.normalize(method.getName()).equals(normalizedOperation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Il client Template Engine non espone l'operationId '" + operationId + "'"
                ));
    }
}
