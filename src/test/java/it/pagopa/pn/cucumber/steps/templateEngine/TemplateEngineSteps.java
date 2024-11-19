package it.pagopa.pn.cucumber.steps.templateEngine;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import it.pagopa.pn.cucumber.steps.templateEngine.strategies.ITemplateEngineStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.util.Map;

@Slf4j
public class TemplateEngineSteps {
    private File templateFileReturned;
    private String templateHtmlReturned;

    private Map<String, ITemplateEngineStrategy> templateEngineStrategy;
    private TemplateEngineResult result;

    private HttpClientErrorException templateFileException;

    private ITemplateEngineClient templateEngineClient;

    public TemplateEngineSteps(ITemplateEngineClient templateEngineClient, Map<String, ITemplateEngineStrategy> templateEngineStrategy) {
        this.templateEngineClient = templateEngineClient;
        this.templateEngineStrategy = templateEngineStrategy;
    }

    @When("recupero (il template)(l'oggetto) per {string} in lingua {string}")
    public void scaricoIlTemplateInLingua(String templateType, String language) {
        try {
            result = templateEngineStrategy.get(templateType.toUpperCase()).retrieveTemplate(language);
            //retrieveTemplateFile(templateType, language);
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
        /*if(extentionFile.equals(".pdf")) {
            Assertions.assertNotNull(templateFileReturned);
            Assertions.assertTrue(templateFileReturned.getName().endsWith(extentionFile));
        } else {
            Assertions.assertNotNull(templateHtmlReturned);
        }*/
    }

    @Then("verifico che la chiamata sia andata in {string} error")
    public void verificoCheLaChiamataSiaAndataInError(String errorCode) {
        Assertions.assertNotNull(templateFileException);
        Assertions.assertEquals(errorCode, templateFileException.getStatusText());
    }

    private void retrieveTemplateFile(String templateType, String language) {
         switch (templateType.toUpperCase()) {
            case "ATTESTAZIONE OPPONIBILE A TERZI DI NOTIFICA PRESA IN CARICO" -> {
                NotificationReceiverLegalFact legalFact = new NotificationReceiverLegalFact();
                templateFileReturned = templateEngineClient.notificationReceivedLegalFact(selectLanguage(language), legalFact);
            }
            case "ATTESTAZIONE OPPONIBILE A TERZI DI NOTIFICA DIGITALE" -> {
                PecDeliveryWorkflowLegalFact legalFact = new PecDeliveryWorkflowLegalFact();
                templateFileReturned = templateEngineClient.pecDeliveryWorkflowLegalFact(selectLanguage(language), legalFact);
            }
            case "ATTESTAZIONE OPPONIBILE A TERZI DI AVVENUTO ACCESSO" -> {
                NotificationViewedLegalFact legalFact = new  NotificationViewedLegalFact();
                templateFileReturned = templateEngineClient.notificationViewedLegalFact(selectLanguage(language), legalFact);
            }
            case "ATTESTAZIONE OPPONIBILE A TERZI DI MALFUNZIONAMENTO E RIPRISTINO" -> {
                LegalFactMalfunction legalFact = new  LegalFactMalfunction();
                templateFileReturned = templateEngineClient.legalFactMalfunction(selectLanguage(language), legalFact);
            }
            case "DICHIARAZIONE DI ANNULLAMENTO NOTIFICA" -> {
                NotificationCancelledLegalFact legalFact = new  NotificationCancelledLegalFact();
                templateFileReturned = templateEngineClient.notificationCancelledLegalFact(selectLanguage(language), legalFact);
            }
            case "DEPOSITO DI AVVENUTA RICEZIONE" -> {
                AnalogDeliveryWorkflowFailureLegalFact legalFact = new  AnalogDeliveryWorkflowFailureLegalFact();
                templateFileReturned = templateEngineClient. analogDeliveryWorkflowFailureLegalFact(selectLanguage(language), legalFact);
            }
            case "AVVISO DI AVVENUTA RICEZIONE" -> {
                NotificationAAR legalFact = new  NotificationAAR();
                templateFileReturned = templateEngineClient. notificationAAR(selectLanguage(language), legalFact);
            }
            case "AVVISO DI AVVENUTA RICEZIONE RADD" -> {
                NotificationAARRADDalt legalFact = new  NotificationAARRADDalt();
                templateFileReturned = templateEngineClient. notificationAARRADDalt(selectLanguage(language), legalFact);
            }
            case "AVVISO DI CORTESIA EMAIL" -> {
                NotificationAARForEMAIL legalFact = new  NotificationAARForEMAIL();
                 templateHtmlReturned = templateEngineClient.notificationAARForEMAIL(selectLanguage(language), legalFact);
            }
            case "AVVISO DI CORTESIA PEC" -> {
                NotificationAARForPEC legalFact = new  NotificationAARForPEC();
                 templateHtmlReturned = templateEngineClient.notificationAARForPEC(selectLanguage(language), legalFact);
            }
            case "OTP DI CONFERMA EMAIL" -> {
                Emailbody body = new  Emailbody();
                 templateHtmlReturned = templateEngineClient.emailbody(selectLanguage(language), body);
            }
            case "OTP DI CONFERMA PEC" -> {
                Pecbody body = new Pecbody();
                templateHtmlReturned = templateEngineClient.pecbody(selectLanguage(language), body);
            }
            case "PEC VALIDA" -> {
                Pecbody body = new Pecbody();
                templateHtmlReturned = templateEngineClient.pecbodyconfirm(selectLanguage(language), body);
            }
            case "PEC NON VALIDA" -> {
                templateHtmlReturned = templateEngineClient.pecbodyreject(selectLanguage(language));
            }
            case "AVVISO DI CORTESIA SMS" -> {
                NotificationAARForSMS notificationAARForSMS = new NotificationAARForSMS();
                templateHtmlReturned = templateEngineClient.notificationAARForSMS(selectLanguage(language), notificationAARForSMS);
            }
            case "OTP DI CONFERMA SMS" -> {
                templateHtmlReturned = templateEngineClient.smsbody(selectLanguage(language));
            }
            case "AVVISO DI CORTESIA PER L’SMS" -> {
                NotificationAARSubject subject = new NotificationAARSubject();
                templateHtmlReturned = templateEngineClient.notificationAARSubject(selectLanguage(language), subject);
            }
            default -> throw new IllegalArgumentException();
        };
    }

    LanguageEnum selectLanguage(String language) {
        return switch (language.toUpperCase()) {
            case "ITALIANA" -> LanguageEnum.IT;
            case "TEDESCA" -> LanguageEnum.DE;
            case "SLOVENA" -> LanguageEnum.SL;
            case "FRANCESE" -> LanguageEnum.FR;
            case "NULL" -> null;
            default -> throw new IllegalArgumentException();
        };
    }

}
