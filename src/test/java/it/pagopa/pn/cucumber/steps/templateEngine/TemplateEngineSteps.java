package it.pagopa.pn.cucumber.steps.templateEngine;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateEngineContextFactory;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateRequestContext;
import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateType;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TemplateEngineSteps {

    private static final String BODY_CORRETTO = "CORRETTO";

    private final Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy;
    Map<TemplateType, List<String>> templateEngineObjectFields;
    private final TemplateEngineContextFactory contextFactory;
    private TemplateEngineResult result;

    private HttpClientErrorException templateFileException;

    public TemplateEngineSteps(Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy,
                               TemplateEngineContextFactory contextFactory) {
        this.templateEngineStrategy = templateEngineStrategy;
        this.contextFactory = contextFactory;
    }

    @When("recupero (il template)(l'oggetto) per {string} in lingua {string} con il body {string}")
    public void recuperoIlTemplatePerInLinguaConIlBody(String templateType, String language, String body) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, language, body, new HashMap<>());
    }

    @When("recupero (il template)(l'oggetto) per {string} in lingua {string}")
    public void recuperoIlTemplatePerInLingua(String templateType, String language) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, language, BODY_CORRETTO, new HashMap<>());
    }

    @When("recupero (il template)(l'oggetto) per {string} con i valori nel request body:")
    public void recuperoIlTemplateConIValoriNelRequestBody(String templateType, Map<String, String> parameters) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        retrieveTemplate(templateTypeObject, "italiana", BODY_CORRETTO, parameters);
    }

    @When("recupero (il template)(l'oggetto) per {string} con i valori nel request body errati")
    public void recuperoIlTemplateConIValoriNelRequestBodyErrati(String templateType) {
        TemplateType templateTypeObject = TemplateType.fromValue(templateType.toUpperCase());
        templateEngineObjectFields.get(templateTypeObject)
                .forEach(data -> {
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put(data, "null");
                    retrieveTemplate(templateTypeObject, "italiana", BODY_CORRETTO, parameters);
                });
    }

    private void retrieveTemplate(TemplateType templateType, String language, String body, Map<String, String> parameters) {
        try {
            TemplateRequestContext context = contextFactory.createContext(parameters);
            result = templateEngineStrategy.get(templateType).retrieveTemplate(language, body.equals(BODY_CORRETTO), context);
        } catch (HttpClientErrorException e) {
            templateFileException = e;
        }
    }

    @Then("verifico che il template è in formato {string}")
    public void verificoCheIlTemplateInFormato(String extentionFile) {
        Assertions.assertNotNull(result);
        if(extentionFile.equals(".pdf")) {
            Assertions.assertNotNull(result.getTemplateFileReturned());
            Assertions.assertTrue(result.getTemplateFileReturned().getName().endsWith(extentionFile));
        } else {
            Assertions.assertNotNull(result.getTemplateHtmlReturned());
        }
    }

    @Then("verifico che (tutte le chiamate)(la chiamata) (sia)(siano) (andata)(andate) in {string} error(.)( e che nessuna abbia ricevuto una risposta)")
    public void verificoCheLaChiamataSiaAndataInError(String errorCode) {
        Assertions.assertNull(result);
        Assertions.assertNotNull(templateFileException);
        Assertions.assertEquals(errorCode, String.valueOf(templateFileException.getRawStatusCode()));
    }

   /* public Map<TemplateType, ITemplateEngineStrategy> templateEngineStrategy() {
        Map<TemplateType, ITemplateEngineStrategy> map = new HashMap<>();
        map.put(TemplateType.AAR_PRESA_IN_CARICO, new NotificationReceiverLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.AAR_NOTIFICA_DIGITALE, new PecDeliveryWorkflowLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.AAR_AVVENUTO_ACCESSO, new NotificationViewedLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.AAR_FUNZIONAMENTO_RIPRISTINO, new LegalFactMalfunctionStrategy(templateEngineClient));
        map.put(TemplateType.AAR_ANNULLAMENTO_NOTIFICA, new NotificationCancelledLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.DEPOSITO_AVVENUTA_RICEZIONE, new AnalogDeliveryWorkflowFailureLegalFactStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_AVVENUTA_RICEZIONE, new NotificationAARStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_AVVENUTA_RICEZIONE_RADD, new NotificationAARRADDaltStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_CORTESIA_EMAIL, new NotificationAARForEMAILStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_CORTESIA_PEC, new NotificationAARForPECStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_EMAIL, new ConfirmEmailBodyStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_PEC, new ConfirmPecBodyStrategy(templateEngineClient));
        map.put(TemplateType.PEC_VALIDA, new ValidPecBodyStrategy(templateEngineClient));
        map.put(TemplateType.PEC_NON_VALIDA, new PecBodyRejectStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_CORTESIA_SMS, new NotificationAARForSMSStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_SMS, new ConfirmSmsBodyStrategy(templateEngineClient));
        map.put(TemplateType.AVVISO_CORTESIA_SMS_OBJECT, new NotificationAARSubjectStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_EMAIL_OBJECT, new ConfirmEmailBodyObjectStrategy(templateEngineClient));
        map.put(TemplateType.OTP_CONFERMA_PEC_OBJECT, new ConfirmPecBodyObjectStrategy(templateEngineClient));
        map.put(TemplateType.PEC_VALIDA_OBJECT, new ValidPecBodyObjectStrategy(templateEngineClient));
        map.put(TemplateType.PEC_NON_VALIDA_OBJECT, new PecBodyRejectObjectStrategy(templateEngineClient));
        return map;
    }*/
}
