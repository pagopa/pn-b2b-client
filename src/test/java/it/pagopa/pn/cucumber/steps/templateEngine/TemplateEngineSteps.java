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
import it.pagopa.pn.cucumber.steps.templateEngine.fuzzing.HtmlEscapeFuzzExecution;
import it.pagopa.pn.cucumber.steps.templateEngine.fuzzing.HtmlEscapeFuzzTarget;
import it.pagopa.pn.cucumber.steps.templateEngine.fuzzing.HtmlEscapeFuzzTargets;
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
    private final List<HtmlEscapeFuzzExecution> htmlEscapeFuzzExecutions = new ArrayList<>();

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
            String retrievedText = extractPdfText(resource, "template corrente");
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
                Assertions.assertTrue(result.retrieveFormattedText().endsWith(fieldValue + " PagoPA S.p.A. società per azioni con socio unico capitale sociale di euro 1000000 interamente versato sede legale in Roma, Piazza Colonna 370, CAP 00187 n. di iscrizione a Registro Imprese di Roma, CF e P.IVA 15376371009"));
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
    // HTML ESCAPE FUZZING
    // -------------------------------------------------------------------------

    @When("eseguo il fuzzing HTML escaping sull'endpoint {string} in lingua {string} sui campi {string}")
    public void eseguoIlFuzzingHtmlEscapingSullEndpoint(String endpoint, String language, String fields) {
        HtmlEscapeFuzzTarget target = HtmlEscapeFuzzTargets.get(endpoint);
        htmlEscapeFuzzExecutions.clear();

        Map<String, String> fuzzedValues = new LinkedHashMap<>();
        for (String field : splitFields(fields)) {
            fuzzedValues.put(field, buildPdfFuzzValue());
        }

        htmlEscapeFuzzExecutions.add(executeFuzzCall(target, language, fuzzedValues));
    }

    /**
     * Esegue sul medesimo campo il corpus minimo richiesto usando marker univoci,
     * così le asserzioni negative non collidono con i caratteri strutturali del template HTML.
     * Copre i cinque caratteri da escapare, una combinazione completa e un payload markup-like.
     */
    @When("eseguo il corpus di fuzzing HTML escaping sull'endpoint {string} in lingua {string} sul campo {string}")
    public void eseguoIlCorpusDiFuzzingHtmlEscaping(String endpoint, String language, String field) {
        HtmlEscapeFuzzTarget target = HtmlEscapeFuzzTargets.get(endpoint);
        htmlEscapeFuzzExecutions.clear();

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
            htmlEscapeFuzzExecutions.add(executeFuzzCall(target, language, Map.of(field, value)));
        }
    }

    @Then("verifico che tutti i valori fuzzed siano correttamente rappresentati nel template di tipo {string}")
    public void verificoCheTuttiIValoriFuzzedSianoEscapati(String format) {

        Assertions.assertFalse(
                htmlEscapeFuzzExecutions.isEmpty(),
                "Nessuna esecuzione fuzzing disponibile"
        );

        String expectedFormat = format.toLowerCase();

        for (HtmlEscapeFuzzExecution execution : htmlEscapeFuzzExecutions) {

            Assertions.assertEquals(
                    expectedFormat,
                    execution.getFormat(),
                    "Il formato dichiarato nello scenario non coincide con quello dell'endpoint "
                            + execution.getEndpoint()
            );

            String rendered = retrieveFuzzedOutput(execution);

            Assertions.assertNotNull(rendered,
                    "Output nullo per endpoint " + execution.getEndpoint());

            String normalizedRendered = normalizeRendered(rendered, execution.getFormat());

            for (Map.Entry<String, String> entry : execution.getRawValues().entrySet()) {

                String field = entry.getKey();
                String rawValue = toSingleLine(entry.getValue());

                switch (execution.getFormat()) {

                    case "pdf" ->
                            assertPdfValue(
                                    execution,
                                    field,
                                    rawValue,
                                    normalizedRendered
                            );

                    case "html" ->
                            assertHtmlValue(
                                    execution,
                                    field,
                                    rawValue,
                                    normalizedRendered
                            );

                    case "text" ->
                            assertTextValue(
                                    execution,
                                    field,
                                    rawValue,
                                    normalizedRendered
                            );

                    default ->
                            throw new IllegalArgumentException(
                                    "Formato non supportato: " + execution.getFormat()
                            );
                }
            }
        }
    }

    private void assertPdfValue(HtmlEscapeFuzzExecution execution, String field, String rawValue, String rendered) {
        assertThat(rendered)
                .as(
                        "Il PDF dell'endpoint %s non contiene il valore letterale del campo %s. Atteso: %s",
                        execution.getEndpoint(),
                        field,
                        rawValue
                )
                .contains(rawValue);

        String escapedValue = toSingleLine(escapeExpected(rawValue));

        if (!escapedValue.equals(rawValue)) {
            assertThat(rendered)
                    .as(
                            "Il PDF dell'endpoint %s contiene entity HTML non renderizzate per il campo %s",
                            execution.getEndpoint(),
                            field
                    )
                    .doesNotContain(escapedValue);
        }
    }

    private void assertHtmlValue(HtmlEscapeFuzzExecution execution, String field, String rawValue, String rendered) {
        String escapedValue = toSingleLine(escapeExpected(rawValue));
        assertThat(rendered)
                .as(
                        "Escaping HTML errato per endpoint %s, campo %s. Atteso: %s",
                        execution.getEndpoint(),
                        field,
                        escapedValue
                )
                .contains(escapedValue);

        if (isUniqueFuzzValue(rawValue)) {
            assertThat(rendered)
                    .as(
                            "Il valore non escapato del campo %s e' presente nell'HTML dell'endpoint %s",
                            field,
                            execution.getEndpoint()
                    )
                    .doesNotContain(rawValue);
        }
    }

    private boolean isUniqueFuzzValue(String value) {
        return value != null
                && (value.startsWith("FZ_") || value.startsWith("FUZZ_"));
    }

    private void assertTextValue(
            HtmlEscapeFuzzExecution execution,
            String field,
            String rawValue,
            String rendered
    ) {

        assertThat(rendered)
                .as(
                        "Il template testuale dell'endpoint %s non contiene il valore del campo %s. Atteso: %s",
                        execution.getEndpoint(),
                        field,
                        rawValue
                )
                .contains(rawValue);
    }

    private String normalizeRendered(String rendered, String format) {
        return switch (format) {
            case "html" -> normalizeHtmlRendered(rendered);
            case "pdf", "text" -> toSingleLine(rendered);
            default -> throw new IllegalArgumentException(
                    "Formato non supportato: " + format
            );
        };
    }

    private String normalizeHtmlRendered(String value) {
        if (value == null) {
            return null;
        }

        return toSingleLine(value)
                .replace("&#xE0;", "à")
                .replace("&#xE8;", "è")
                .replace("&#xEC;", "ì")
                .replace("&#x20AC;", "€");
    }

    private String toSingleLine(String value) {
        if (value == null) {
            return null;
        }

        return value.replaceAll("\\R\\s*", "");
    }

    private HtmlEscapeFuzzExecution executeFuzzCall(HtmlEscapeFuzzTarget target, String language, Map<String, String> logicalValues) {
        resetFuzzCallState();

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
        return new HtmlEscapeFuzzExecution(target.getEndpoint(), target.getFormat(), new LinkedHashMap<>(logicalValues), callResult);
    }

    /**
     * I quattro endpoint presenti nell'OpenAPI ma non ancora mappati da TemplateType/strategy
     * vengono richiamati tramite il client gia' iniettato. La reflection evita di introdurre
     * nuove strategy o modificare configurazioni esistenti solo per i test di escaping.
     */
    private TemplateEngineResult retrieveTemplateByClientReflection(HtmlEscapeFuzzTarget target, String language,
                                                                    Map<String, String> propertyValues) {
        Assertions.assertNotNull(templateEngineClient, "ITemplateEngineClient non disponibile");
        Method clientMethod = findClientMethod(target.getOperationId());

        try {
            Object request = createDefaultModel(clientMethod.getParameterTypes()[1], 0, new LinkedHashSet<>());
            for (Map.Entry<String, String> entry : propertyValues.entrySet()) {
                setNestedProperty(request, entry.getKey().split("\\."), 0, entry.getValue());
            }

            Object languageValue = createLanguageValue(clientMethod.getParameterTypes()[0], language);
            Object response = clientMethod.invoke(templateEngineClient, languageValue, request);
            return toTemplateEngineResult(response);
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

    private Method findClientMethod(String operationId) {
        String normalizedOperation = normalize(operationId);
        return Arrays.stream(templateEngineClient.getClass().getMethods())
                .filter(method -> method.getParameterCount() == 2)
                .filter(method -> normalize(method.getName()).equals(normalizedOperation))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Il client Template Engine non espone l'operationId '" + operationId + "'"));
    }

    private Object createLanguageValue(Class<?> languageType, String language) {
        String enumValue = switch (language.toUpperCase()) {
            case "ITALIANA" -> "IT";
            case "TEDESCA" -> "DE";
            case "SLOVENA" -> "SL";
            case "FRANCESE" -> "FR";
            case "INGLESE" -> "EN";
            default -> throw new IllegalArgumentException("Lingua non valida: " + language);
        };

        if (languageType == String.class) {
            return enumValue;
        }
        if (languageType.isEnum()) {
            return Arrays.stream(languageType.getEnumConstants())
                    .filter(value -> ((Enum<?>) value).name().equalsIgnoreCase(enumValue)
                            || value.toString().equalsIgnoreCase(enumValue))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Valore lingua " + enumValue + " non disponibile su " + languageType.getName()));
        }
        throw new IllegalStateException("Tipo lingua non supportato: " + languageType.getName());
    }

    private TemplateEngineResult toTemplateEngineResult(Object response) throws ReflectiveOperationException {
        if (response instanceof Resource resource) {
            return new TemplateEngineResult(resource);
        }
        if (response instanceof String text) {
            return new TemplateEngineResult(text);
        }
        if (response == null) {
            return null;
        }

        // Compatibilita' con eventuali wrapper tipo ResponseEntity senza introdurre dipendenze nuove.
        try {
            Method getBody = response.getClass().getMethod("getBody");
            return toTemplateEngineResult(getBody.invoke(response));
        } catch (NoSuchMethodException ignored) {
            throw new IllegalStateException("Tipo risposta non gestito: " + response.getClass().getName());
        }
    }

    private Object createDefaultModel(Class<?> type, int depth, Set<Class<?>> branch) throws ReflectiveOperationException {
        if (depth > 5 || branch.contains(type)) {
            return null;
        }

        Object simpleValue = defaultSimpleValue(type, type.getSimpleName());
        if (simpleValue != null) {
            return simpleValue;
        }

        Object model = type.getDeclaredConstructor().newInstance();
        Set<Class<?>> nextBranch = new LinkedHashSet<>(branch);
        nextBranch.add(type);

        for (Method setter : type.getMethods()) {
            if (!setter.getName().startsWith("set") || setter.getParameterCount() != 1) {
                continue;
            }

            String property = decapitalize(setter.getName().substring(3));
            if ("additionalProperties".equals(property)) {
                continue;
            }

            Object value = defaultValueForProperty(setter.getParameterTypes()[0],
                    setter.getGenericParameterTypes()[0], property, depth + 1, nextBranch);
            if (value != null) {
                setter.invoke(model, value);
            }
        }
        return model;
    }

    private Object defaultValueForProperty(Class<?> type, Type genericType, String property,
                                           int depth, Set<Class<?>> branch) throws ReflectiveOperationException {
        if (type == String.class) {
            return defaultStringValue(property);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.TRUE;
        }
        if (type == int.class || type == Integer.class) {
            return 1;
        }
        if (type == long.class || type == Long.class) {
            return 1L;
        }
        if (type == double.class || type == Double.class) {
            return 1D;
        }
        if (type == float.class || type == Float.class) {
            return 1F;
        }
        if (type == BigDecimal.class) {
            return BigDecimal.ONE;
        }
        if (type == BigInteger.class) {
            return BigInteger.ONE;
        }
        if (type == URI.class) {
            return URI.create("https://example.org");
        }
        if (type == OffsetDateTime.class) {
            return OffsetDateTime.parse("2025-05-20T10:15:30Z");
        }
        if (type == LocalDateTime.class) {
            return LocalDateTime.parse("2025-05-20T10:15:30");
        }
        if (type == LocalDate.class) {
            return LocalDate.parse("2025-05-20");
        }
        if (type == Instant.class) {
            return Instant.parse("2025-05-20T10:15:30Z");
        }
        if (type == UUID.class) {
            return UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        }
        if (type.isEnum()) {
            return selectEnumDefault(type, property);
        }
        if (Collection.class.isAssignableFrom(type)) {
            Object item = createCollectionItem(genericType, property, depth, branch);
            if (item == null) {
                return null;
            }
            if (Set.class.isAssignableFrom(type)) {
                return Set.of(item);
            }
            return List.of(item);
        }
        if (Map.class.isAssignableFrom(type)) {
            return Map.of();
        }

        return createDefaultModel(type, depth, branch);
    }

    private Object createCollectionItem(Type genericType, String property,
                                        int depth, Set<Class<?>> branch) throws ReflectiveOperationException {
        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return null;
        }
        Type itemType = parameterizedType.getActualTypeArguments()[0];
        if (!(itemType instanceof Class<?> itemClass)) {
            return null;
        }
        return defaultValueForProperty(itemClass, itemClass, property, depth, branch);
    }

    private Object defaultSimpleValue(Class<?> type, String property) {
        if (type == String.class) {
            return defaultStringValue(property);
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.TRUE;
        }
        if (type == int.class || type == Integer.class) {
            return 1;
        }
        if (type == long.class || type == Long.class) {
            return 1L;
        }
        return null;
    }

    private Object selectEnumDefault(Class<?> enumType, String property) {
        List<String> preferredNames = new ArrayList<>();
        String normalizedProperty = normalize(property);
        if (normalizedProperty.contains("recipienttype")) {
            preferredNames.add("PF");
        }
        if (normalizedProperty.contains("status")) {
            preferredNames.add("SUCCESS");
        }
        if (normalizedProperty.equals("type")) {
            preferredNames.add("PEC");
            preferredNames.add("DIGITAL");
        }
        preferredNames.add("IT");

        Object[] constants = enumType.getEnumConstants();
        for (String preferredName : preferredNames) {
            for (Object constant : constants) {
                if (((Enum<?>) constant).name().equalsIgnoreCase(preferredName)
                        || constant.toString().equalsIgnoreCase(preferredName)) {
                    return constant;
                }
            }
        }
        return constants.length == 0 ? null : constants[0];
    }

    private String defaultStringValue(String property) {
        String normalizedProperty = normalize(property);
        if (normalizedProperty.contains("iun")) {
            return "UTGP-ZRHR-XDNQ-202505-Q-1";
        }
        if (normalizedProperty.contains("taxid")) {
            return "RSSMRA80A01H501U";
        }
        if (normalizedProperty.contains("url") || normalizedProperty.contains("link")) {
            return "https://example.org";
        }
        if (normalizedProperty.contains("date") || normalizedProperty.contains("time")
                || normalizedProperty.equals("when")) {
            return "2025-05-20T10:15:30Z";
        }
        if (normalizedProperty.contains("digest")) {
            return "TEST_digest_allegato";
        }
        if (normalizedProperty.contains("recipienttype")) {
            return "PF";
        }
        if (normalizedProperty.equals("type")) {
            return "PEC";
        }
        if (normalizedProperty.contains("status")) {
            return "SUCCESS";
        }
        if (normalizedProperty.contains("phone")) {
            return "0612345678";
        }
        if (normalizedProperty.contains("verificationcode")) {
            return "12345";
        }
        return property + "_value";
    }

    private void setNestedProperty(Object current, String[] path, int index, String value)
            throws ReflectiveOperationException {
        if (current == null) {
            throw new IllegalStateException("Impossibile valorizzare il path " + String.join(".", path));
        }

        if (current instanceof Collection<?> collection) {
            if (collection.isEmpty()) {
                throw new IllegalStateException("Collection vuota nel path " + String.join(".", path));
            }
            for (Object item : collection) {
                setNestedProperty(item, path, index, value);
            }
            return;
        }

        String property = path[index];
        if (index == path.length - 1) {
            Method setter = findSetter(current.getClass(), property);
            setter.invoke(current, convertStringValue(value, setter.getParameterTypes()[0]));
            return;
        }

        Method getter = findGetter(current.getClass(), property);
        Object nested = getter.invoke(current);
        if (nested == null) {
            Method setter = findSetter(current.getClass(), property);
            nested = defaultValueForProperty(setter.getParameterTypes()[0], setter.getGenericParameterTypes()[0],
                    property, 1, new LinkedHashSet<>());
            setter.invoke(current, nested);
        }
        setNestedProperty(nested, path, index + 1, value);
    }

    private Method findGetter(Class<?> type, String property) {
        String normalizedProperty = normalize(property);
        return Arrays.stream(type.getMethods())
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> method.getName().startsWith("get") || method.getName().startsWith("is"))
                .filter(method -> {
                    String name = method.getName().startsWith("get")
                            ? method.getName().substring(3)
                            : method.getName().substring(2);
                    return normalize(name).equals(normalizedProperty);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Getter non trovato per " + type.getSimpleName() + "." + property));
    }

    private Method findSetter(Class<?> type, String property) {
        String normalizedProperty = normalize(property);
        return Arrays.stream(type.getMethods())
                .filter(method -> method.getName().startsWith("set") && method.getParameterCount() == 1)
                .filter(method -> normalize(method.getName().substring(3)).equals(normalizedProperty))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Setter non trovato per " + type.getSimpleName() + "." + property));
    }

    private Object convertStringValue(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        }
        if (targetType.isEnum()) {
            return Arrays.stream(targetType.getEnumConstants())
                    .filter(constant -> ((Enum<?>) constant).name().equalsIgnoreCase(value)
                            || constant.toString().equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Valore enum non valido " + value + " per " + targetType.getName()));
        }
        throw new IllegalStateException("Il fuzzing HTML e' previsto su campi testuali, trovato " + targetType.getName());
    }

    private String retrieveFuzzedOutput(HtmlEscapeFuzzExecution execution) {
        return switch (execution.getFormat()) {
            case "pdf" -> {
                Resource resource = execution.getResult().getTemplateFileReturned();
                Assertions.assertNotNull(
                        resource,
                        "PDF non restituito per endpoint " + execution.getEndpoint()
                );
                yield extractPdfText(resource, execution.getEndpoint());
            }
            case "html", "text" -> {
                String text = execution.getResult().getTemplateHtmlReturned();
                Assertions.assertNotNull(
                        text,
                        "Testo/HTML non restituito per endpoint " + execution.getEndpoint()
                );
                yield text;
            }
            default -> throw new IllegalArgumentException(
                    "Formato non supportato: " + execution.getFormat()
            );
        };
    }

    private String extractPdfText(Resource resource, String endpoint) {
        try (InputStream inputStream = resource.getInputStream()) {
            return PDFUtility.extractText(inputStream.readAllBytes());
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException(
                    "Impossibile estrarre il testo del PDF per " + endpoint,
                    e
            );
        }
    }

    private void resetFuzzCallState() {
        result = null;
        templateFileException = null;
        templateServerException = null;
        templateFileExceptions.clear();
    }

    private List<String> splitFields(String fields) {
        return Arrays.stream(fields.split(","))
                .map(String::trim)
                .filter(field -> !field.isBlank())
                .toList();
    }

    private String buildFuzzValue() {
        String id = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);

        return "FUZZ_" + id
                + "_START_"
                + "<b>bold</b>_"
                + "<span class=\"test\">span</span>_"
                + "<script>alert('x')</script>_"
                + "<>&\"'_"
                + "{}[]()_${value}_#{value}_"
                + "\\n_\\t_\\\\_"
                + "&lt;_&amp;_&#39;_"
                + "àèéìòù_€_"
                + "END";
    }

    private String buildPdfFuzzValue() {
        String id = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);

        return "FZ_" + id + "_<b>X</b>_<>&\"'àèì$_€_END";
    }

    /**
     * Expected calcolato indipendentemente da FreeMarker: il test non deve usare lo stesso
     * escaper della produzione, altrimenti un errore comune a implementazione e test passerebbe inosservato.
     */
    private String escapeExpected(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
    }

    private static String decapitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

}
