package it.pagopa.pn.cucumber.steps.templateEngine;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.util.PDFUtility;
import it.pagopa.pn.client.b2b.pa.exception.IllegalConfigurationException;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateEngineContextFactory;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateType;
import it.pagopa.pn.cucumber.steps.templateEngine.escapemod.*;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.ITemplateEngineStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TemplateEngineSteps {

    private static final String BODY_CORRETTO = "CORRETTO";

    private final Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy;
    private final Map<TemplateType, List<String>> templateEngineObjectFields;
    private final TemplateEngineContextFactory contextFactory;

    @Autowired
    private ITemplateEngineClient templateEngineClient;

    private TemplateEngineResult result;
    private final List<HtmlEscapeExecution> HtmlEscapeExecutions = new ArrayList<>();

    private HttpClientErrorException templateFileException;
    private HttpServerErrorException templateServerException;
    private List<HttpStatusCodeException> templateFileExceptions = new ArrayList<>();
    private String recipientType = "PF";

    @Value("${spring.profiles.active}")
    private String runProfile;

    public TemplateEngineSteps(Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy,
                               TemplateEngineContextFactory contextFactory, Map<TemplateType, List<String>> templateEngineObjectFields) {
        this.templateEngineStrategy = templateEngineStrategy;
        this.contextFactory = contextFactory;
        this.templateEngineObjectFields = templateEngineObjectFields;
    }
    @When("recupero (il template)(l'oggetto) per {string} in lingua {string} con il body {string}")
    public void recuperoIlTemplatePerInLinguaConIlBody(String templateType, String language, String body) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, language, body, "semplice", new HashMap<>());
    }

    @When("recupero (il template)(l'oggetto) per {string} di tipo {string} in lingua {string}")
    public void recuperoIlTemplatePerInLingua(String templateType, String notificationType, String language) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("context_recipientType", recipientType); // todo t mc.
        parameters.put("recipient_recipientType", recipientType); // todo t mc.
        retrieveTemplate(templateTypeObject, language, BODY_CORRETTO, notificationType, parameters);
    }

    @When("recupero (il template)(l'oggetto) per {string} in lingua {string}")
    public void recuperoIlTemplatePerInLingua(String templateType, String language) {
        recuperoIlTemplatePerInLingua(templateType, "semplice", language);
    }
    @When("recupero (il template)(l'oggetto) per {string} in lingua {string} con recipient Type {string}")
    public void recuperoIlTemplatePerInLinguaRecType(String templateType, String language, String recipientType) {
        this.recipientType = recipientType;
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("context_recipientType", recipientType);
        parameters.put("recipient_recipientType", recipientType);
        parameters.put("recipientType", recipientType);
        retrieveTemplate(templateTypeObject, language, BODY_CORRETTO, "semplice", parameters);
    }

    @When("recupero (il template)(l'oggetto) per {string} con i valori nel request body:")
    public void recuperoIlTemplateConIValoriNelRequestBody(String templateType, Map<String, String> parameters) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, "italiana", BODY_CORRETTO, "semplice", parameters);
    }

    @When("recupero (il template)(l'oggetto) per {string} con i valori nel request body errati")
    public void recuperoIlTemplateConIValoriNelRequestBodyErrati(String templateType) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        templateEngineObjectFields.get(templateTypeObject)
                .forEach(data -> {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put(data, "null");
                    retrieveTemplate(templateTypeObject, "italiana", BODY_CORRETTO, "semplice", parameters);
                });
    }

    private void retrieveTemplate(TemplateType templateType, String language, String body, String notificationTpe, Map<String, String> parameters) {
        try {
            TemplateRequestContext context = contextFactory.createContext(parameters, notificationTpe);
            result = templateEngineStrategy.get(templateType).retrieveTemplate(language, body.equals(BODY_CORRETTO), context);
        } catch ( HttpClientErrorException e) {
            templateFileException = e;
            templateFileExceptions.add(e);
        } catch (HttpServerErrorException e) {
            templateServerException = e;
            templateFileExceptions.add(e);
        }
    }

    @Then("verifico che il template è in formato {string}")
    public void verificoCheIlTemplateInFormato(String extentionFile) {
        Assertions.assertNull(templateFileException);
        Assertions.assertNotNull(result);
        switch (extentionFile) {
            case ".pdf" -> {
                Assertions.assertNotNull(result.getTemplateFileReturned());
                Assertions.assertTrue(isValidPdf(result.getTemplateFileReturned()));
            }
            case "html" -> {
                Assertions.assertNotNull(result.getTemplateHtmlReturned());
                Assertions.assertTrue(result.getTemplateHtmlReturned().contains("<html"));
            }
            case "text" -> {
                Assertions.assertNotNull(result.getTemplateHtmlReturned());
                Assertions.assertFalse(result.getTemplateHtmlReturned().contains("<html"));
            }
            default -> Assertions.fail("Formato template non supportato: " + extentionFile);
        }
    }

    public boolean isValidPdf(Resource resource) {
        try {
            String retrievedText = HtmlEscapeUtilities.extractPdfText(resource, "template corrente");
            if (retrievedText == null) {
                return false;
            }
            result.setFileTextRetrieved(retrievedText);
            return !retrievedText.isBlank();
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Then("verifico che (tutte le chiamate)(la chiamata) (sia)(siano) (andata)(andate) in {string} error(.)( e che nessuna abbia ricevuto una risposta)")
    public void verificoCheLaChiamataSiaAndataInError(String errorCode) {
        Assertions.assertNull(result);
        if (errorCode.equals("400")) {
            Assertions.assertNotNull(templateFileException);
            Assertions.assertNotNull(templateFileExceptions);
            Assertions.assertEquals(errorCode, String.valueOf(templateFileException.getRawStatusCode()));
        } else if (errorCode.equals("500")) {
            Assertions.assertNotNull(templateServerException);
        } else throw new IllegalArgumentException("no error map on the test system.");
        templateFileExceptions.forEach(data -> Assertions.assertEquals(errorCode, String.valueOf(data.getRawStatusCode())));
    }

    private int countOccurrences(String regex) {
        Matcher matcher = Pattern.compile(regex)
                .matcher(result.retrieveFormattedText());

        int count = 0;
        while (matcher.find()) {
            count++;
        }

        return count;
    }

    private List<String> getTextsToRetrieve(String language, TemplateType templateType, String recipientType) {
        List<String> retrievedTexts = templateEngineStrategy.get(templateType).getTextsToCheckLanguage(language, recipientType);
        List<String> texts = new ArrayList<>();
        for (String retrievedText : retrievedTexts) {
            texts.add(retrievedText.replace("{%profile}", runProfile));
        }
        return texts;
    }

    @And("controllo che nel file {string} contenga il (campo)(testo) {string} valorizzato (a)(con) {string}")
    public void controlloCheNelFileContengaIlCampoValirizzatoA(String fileType, String field, String fieldValue) {
        if (fileType.equals("pdf")) {
            Assertions.assertNotNull(result.getFileTextRetrieved());
            if (field.equals("finale")) {
                Assertions.assertTrue(result.retrieveFormattedText().endsWith(fieldValue + " PagoPA S.p.A. società per azioni capitale sociale di euro 1,000,000 interamente versato sede legale in Roma, Piazza Colonna 370, CAP 00187 n. di iscrizione a Registro Imprese di Roma, CF e P.IVA 15376371009"));
            } else if (field.equals("delegato")) {
                Assertions.assertTrue(result.retrieveFormattedText().contains("il " + fieldValue + " ha avuto accesso ai documenti informatici oggetto di notifica"));
            } else {
                Assertions.assertTrue(result.retrieveFormattedText().contains(field + " " + fieldValue), "il PDF non contiene il campo: " + field + ", valorizzato a " + fieldValue);
            }
        } else {
            throw new IllegalArgumentException("no valid file to check");
        }
    }

    @And("controllo che per il template {string} il file {string} sia in lingua {string}")
    public void controlloChePerIlTemplateIlFilePerUnaNotificaIlTestoSiaInLingua(String templateType, String fileType, String languange) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        List<String> textsToFind = getTextsToRetrieve(languange, templateTypeObject, recipientType);
        String[] textsToFindArray = textsToFind.toArray(new String[0]);
        if (fileType.equals("pdf")) {
            assertThat(result.getFileTextRetrieved()).isNotNull();
            assertThat(result.retrieveFormattedText())
                    .as("Checking if formatted text contains all of: " + textsToFind)
                    .contains(textsToFindArray);
        } else {
            assertThat(result.getTemplateHtmlReturned()).isNotNull();
            assertThat(result.getTemplateHtmlReturned())
                    .as("Checking if formatted text contains all of: " + textsToFind)
                    .contains(textsToFindArray);
        }
    }

    @And("controllo che la notifica {string} abbia i giusti campi valorizzati")
    public void controlloCheLaNotificaAbbiaIGiustiCampiValorizzati(String notificationType) {
        switch (notificationType) {
            case "monodestinatario" ->
                    Assertions.assertEquals(1, countOccurrences("Nome e(?: string)? Cognome(?: /(?: string)? Ragione Sociale)?"));
            case "multidestinatario" ->
                    Assertions.assertEquals(2, countOccurrences("Nome e(?: string)? Cognome(?: /(?: string)? Ragione Sociale)?"));
            case "singolo allegato" ->
                    Assertions.assertEquals(1, countOccurrences("TEST_digest_allegato"));
            case "piu allegati" ->
                    Assertions.assertEquals(2, countOccurrences("TEST_digest_allegato"));
            default ->
                    throw new IllegalConfigurationException("Invalid notification type: " + notificationType);
        }
    }

    @And("il corpo del messaggio non contiene il testo {string}")
    public void checkMessageNotContains(String message) {
        assertMessageContent(false, message);
    }

    @And("il corpo del messaggio contiene il testo {string}")
    public void checkMessageContains(String message) {
        assertMessageContent(true, message);
    }

    public void assertMessageContent(boolean contains, String message) {
        Assertions.assertNotNull(result.getFileTextRetrieved(), "Nessun testo recuperato");
        String formattedText = result.retrieveFormattedText();
        if (contains) {
            Assertions.assertTrue(formattedText.contains(message),
                    "Il corpo del messaggio non contiene il testo atteso: " + message);
        } else {
            Assertions.assertFalse(formattedText.contains(message),
                    "Il corpo del messaggio contiene il testo non atteso: " + message);
        }
    }

    // -------------------------------------------------------------------------
    // HTML ESCAPE
    // -------------------------------------------------------------------------

    @When("eseguo HTML escaping sull'endpoint {string} in lingua {string} sui campi {string}")
    public void eseguoIlFuzzingHtmlEscapingSullEndpoint(String endpoint, String language, String fields) {
        HtmlEscapeTarget target = HtmlEscapeTargets.get(endpoint);
        HtmlEscapeExecutions.clear();

        Map<String, String> fuzzedValues = new LinkedHashMap<>();
        for (String field : HtmlEscapeUtilities.splitFields(fields)) {
            fuzzedValues.put(field, buildPdfFuzzValue());
        }

        HtmlEscapeExecutions.add(executeFuzzCall(target, language, fuzzedValues));
    }

    @Then("verifico che tutti i valori siano rappresentati con HTML escaping disabilitato nel template di tipo {string}")
    public void verificoCheTuttiIValoriFuzzedSianoRappresentatiSenzaEscaping(String format) {

        Assertions.assertFalse(HtmlEscapeExecutions.isEmpty(), "Nessuna esecuzione fuzzing disponibile");
        String expectedFormat = format.toLowerCase();
        for (HtmlEscapeExecution execution : HtmlEscapeExecutions) {
            Assertions.assertEquals(expectedFormat, execution.getFormat(),
                    "Il formato dichiarato nello scenario non coincide con quello dell'endpoint "
                            + execution.getEndpoint());

            String rendered = retrieveFuzzedOutput(execution);
            Assertions.assertNotNull(rendered,"Output nullo per endpoint " + execution.getEndpoint());
            String normalizedRendered = HtmlEscapeUtilities.normalizeRendered(rendered, execution.getFormat());
            for (Map.Entry<String, String> entry : execution.getRawValues().entrySet()) {
                String field = entry.getKey();
                String rawValue = HtmlEscapeUtilities.toSingleLine(entry.getValue());
                switch (execution.getFormat()) {
                    case "pdf" -> HtmlEscapeValidator.assertPdfValueEscapingDisabled(execution, field, rawValue, normalizedRendered);

                    case "html" ->
                            HtmlEscapeValidator.assertHtmlValueEscapingDisabled(
                                    execution,
                                    field,
                                    rawValue,
                                    normalizedRendered
                            );

                    case "text" ->
                            HtmlEscapeValidator.assertTextValue(
                                    execution,
                                    field,
                                    rawValue,
                                    normalizedRendered
                            );

                    default ->
                            throw new IllegalArgumentException(
                                    "Formato non supportato: "
                                            + execution.getFormat()
                            );
                }
            }
        }
    }

    @When("eseguo il corpus di HTML escaping sull'endpoint {string} in lingua {string} sul campo {string}")
    public void eseguoIlCorpusDiFuzzingHtmlEscaping(String endpoint, String language, String field) {
        HtmlEscapeTarget target = HtmlEscapeTargets.get(endpoint);
        HtmlEscapeExecutions.clear();

        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        List<String> corpus = List.of(
                "FUZZ_" + random + "_LT_<_END",
                "FUZZ_" + random + "_GT_>_END",
                "FUZZ_" + random + "_AMP_&_END",
                "FUZZ_" + random + "_QUOTE_\"_END",
                "FUZZ_" + random + "_APOS_'_END",
                "FUZZ_" + random + "_ALL_<>&\"'_END",
                "FUZZ_" + random + "_MARKUP_<b>X</b>_END"
        );

        for (String value : corpus) {
            HtmlEscapeExecutions.add(executeFuzzCall(target, language, Map.of(field, value)));
        }
    }

    @Then("verifico che tutti i valori siano correttamente rappresentati nel template di tipo {string}")
    public void verificoCheTuttiIValoriFuzzedSianoEscapati(String format) {

        Assertions.assertFalse(HtmlEscapeExecutions.isEmpty(),"Nessuna esecuzione fuzzing disponibile");
        String expectedFormat = format.toLowerCase();
        for (HtmlEscapeExecution execution : HtmlEscapeExecutions) {
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

                switch (execution.getFormat()) {

                    case "pdf" -> HtmlEscapeValidator.assertPdfValue(execution, field, rawValue, normalizedRendered);
                    case "html" -> HtmlEscapeValidator.assertHtmlValue(execution, field, rawValue, normalizedRendered);
                    case "text" -> HtmlEscapeValidator.assertTextValue(execution, field, rawValue, normalizedRendered);
                    default -> throw new IllegalArgumentException("Formato non supportato: " + execution.getFormat());
                }
            }
        }
    }

    private HtmlEscapeExecution executeFuzzCall(HtmlEscapeTarget target, String language, Map<String, String> logicalValues) {
        result = null;
        templateFileException = null;
        templateServerException = null;
        templateFileExceptions.clear();

        Map<String, String> mappedValues = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : logicalValues.entrySet()) {
            String mappedField = target.getFieldMappings().get(entry.getKey());
            if (mappedField == null) {
                throw new IllegalArgumentException("Campo '" + entry.getKey() + "' non configurato per endpoint " + target.getEndpoint());
            }
            mappedValues.put(mappedField, entry.getValue());
        }

        TemplateEngineResult callResult;
        if (target.getTemplateType() != null) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("context_recipientType", "PF");
            parameters.put("recipient_recipientType", "PF");
            parameters.put("recipientType", "PF");
            parameters.putAll(mappedValues);

            TemplateType templateTypeObject = TemplateType.fromValue(target.getTemplateType().toUpperCase());
            retrieveTemplate(templateTypeObject, language, BODY_CORRETTO, "semplice", parameters);

            if (templateFileException != null) {
                throw templateFileException;
            }
            if (templateServerException != null) {
                throw templateServerException;
            }
            callResult = result;
        } else {
            callResult = retrieveTemplateByClientReflection(target, language, mappedValues);
            result = callResult;
        }

        Assertions.assertNotNull(callResult, "Nessuna risposta ottenuta dall'endpoint " + target.getEndpoint());
        return new HtmlEscapeExecution(target.getEndpoint(), target.getFormat(), new LinkedHashMap<>(logicalValues), callResult);
    }

    private TemplateEngineResult retrieveTemplateByClientReflection(HtmlEscapeTarget target, String language,
                                                                    Map<String, String> propertyValues) {
        Assertions.assertNotNull(templateEngineClient, "ITemplateEngineClient non disponibile");
        Method clientMethod = findClientMethod(target.getOperationId());

        try {
            Object request = HtmlEscapeReflectionHandler.createDefaultModel(clientMethod.getParameterTypes()[1], 0, new LinkedHashSet<>());
            for (Map.Entry<String, String> entry : propertyValues.entrySet()) {
                HtmlEscapeReflectionHandler.setNestedProperty(request, entry.getKey().split("\\."), 0, entry.getValue());
            }

            Object languageValue = HtmlEscapeReflectionHandler.createLanguageValue(clientMethod.getParameterTypes()[0], language);
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
            throw new IllegalStateException("Impossibile costruire/invocare il payload per " + target.getOperationId(), e);
        }
    }

    private String retrieveFuzzedOutput(HtmlEscapeExecution execution) {
        return switch (execution.getFormat()) {
            case "pdf" -> {
                Resource resource = execution.getResult().getTemplateFileReturned();
                Assertions.assertNotNull(resource, "PDF non restituito per endpoint " + execution.getEndpoint());
                yield HtmlEscapeUtilities.extractPdfText(resource, execution.getEndpoint());
            }
            case "html", "text" -> {
                String text = execution.getResult().getTemplateHtmlReturned();
                Assertions.assertNotNull(text, "Testo/HTML non restituito per endpoint " + execution.getEndpoint());
                yield text;
            }
            default -> throw new IllegalArgumentException(
                    "Formato non supportato: " + execution.getFormat()
            );
        };
    }

    private String buildPdfFuzzValue() {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "FZ_" + id + "_<b>X</b>_<>&\"'àèì$_€_END";
    }

    private  Method findClientMethod(String operationId) {
        String normalizedOperation = HtmlEscapeUtilities.normalize(operationId);
        return Arrays.stream(templateEngineClient.getClass().getMethods())
                .filter(method -> method.getParameterCount() == 2)
                .filter(method -> HtmlEscapeUtilities.normalize(method.getName()).equals(normalizedOperation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Il client Template Engine non espone l'operationId '" + operationId + "'"));
    }

}
