package it.pagopa.pn.cucumber.steps.templateEngine;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.common.util.PDFUtility;
import it.pagopa.pn.client.b2b.pa.exception.IllegalConfigurationException;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateEngineContextFactory;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateType;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.ITemplateEngineStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private TemplateEngineResult result;

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
        parameters.put("context_recipientType", recipientType); // todo t mc.
        parameters.put("recipient_recipientType", recipientType); // todo t mc.
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
        if(extentionFile.equals(".pdf")) {
            Assertions.assertNotNull(result.getTemplateFileReturned());
            Assertions.assertTrue(isValidPdf(result.getTemplateFileReturned()));
        } else if (extentionFile.equals("html")){
            Assertions.assertNotNull(result.getTemplateHtmlReturned());
            Assertions.assertTrue(result.getTemplateHtmlReturned().contains("<html"));
        } else if (extentionFile.equals("text")) {
            Assertions.assertNotNull(result.getTemplateHtmlReturned());
            Assertions.assertFalse(result.getTemplateHtmlReturned().contains("<html"));
        }
    }

    public boolean isValidPdf(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            String retrievedText = PDFUtility.extractText(is.readAllBytes());
            if (retrievedText == null) {
                return false;
            }
            result.setFileTextRetrieved(retrievedText);
            return !retrievedText.isBlank();
        } catch (IOException | RuntimeException e) {
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
}