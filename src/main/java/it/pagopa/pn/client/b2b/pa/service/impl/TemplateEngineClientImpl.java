package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.api.TemplateApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.*;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TemplateEngineClientImpl implements ITemplateEngineClient {
    private final String basePath;
    private RestTemplate restTemplate;
    private TemplateApi templateApi;

    public TemplateEngineClientImpl(RestTemplate restTemplate, @Value("${pn.template.engine.base-path}") String basePath) {
        this.restTemplate = restTemplate;
        this.basePath = basePath;
        this.templateApi = new TemplateApi(apiClient());
    }

    private ApiClient apiClient() {
        ApiClient apiClient = new ApiClient(this.restTemplate);
        apiClient.setBasePath(basePath);
        return apiClient;
    }

    @Override
    public File analogDeliveryWorkflowFailureLegalFact(LanguageEnum xLanguage, AnalogDeliveryWorkflowFailureLegalFact analogDeliveryWorkflowFailureLegalFact) throws RestClientException {
        return templateApi.analogDeliveryWorkflowFailureLegalFact(xLanguage, analogDeliveryWorkflowFailureLegalFact);
    }

    @Override
    public String emailbody(LanguageEnum xLanguage, Emailbody emailbody) throws RestClientException {
        return templateApi.mailVerificationCodeBody(xLanguage, emailbody);
    }

    @Override
    public String emailsubject(LanguageEnum xLanguage) throws RestClientException {
        return templateApi.mailVerificationCodeSubject(xLanguage);
    }

    @Override
    public File legalFactMalfunction(LanguageEnum xLanguage, LegalFactMalfunction legalFactMalfunction) throws RestClientException {
        return templateApi.malfunctionLegalFact(xLanguage, legalFactMalfunction);
    }

    @Override
    public File notificationAAR(LanguageEnum xLanguage, NotificationAAR notificationAAR) throws RestClientException {
        return templateApi.notificationAAR(xLanguage, notificationAAR);
    }

    @Override
    public String notificationAARForEMAIL(LanguageEnum xLanguage, NotificationAARForEMAIL notificationAARForEMAIL) throws RestClientException {
        return templateApi.notificationAARForEMAIL(xLanguage, notificationAARForEMAIL);
    }

    @Override
    public String notificationAARForPEC(LanguageEnum xLanguage, NotificationAARForPEC notificationAARForPEC) throws RestClientException {
        return templateApi.notificationAARForPEC(xLanguage, notificationAARForPEC);
    }

    @Override
    public String notificationAARForSMS(LanguageEnum xLanguage, NotificationAARForSMS notificationAARForSMS) throws RestClientException {
        return templateApi.notificationAARForSMS(xLanguage, notificationAARForSMS);
    }

    @Override
    public File notificationAARRADDalt(LanguageEnum xLanguage, NotificationAARRADDalt notificationAARRADDalt) throws RestClientException {
        return templateApi.notificationAARRADDalt(xLanguage, notificationAARRADDalt);
    }

    @Override
    public String notificationAARSubject(LanguageEnum xLanguage, NotificationAARSubject notificationAARSubject) throws RestClientException {
        return templateApi.notificationAARSubject(xLanguage, notificationAARSubject);
    }

    @Override
    public File notificationCancelledLegalFact(LanguageEnum xLanguage, NotificationCancelledLegalFact notificationCancelledLegalFact) throws RestClientException {
        return templateApi.notificationCancelledLegalFact(xLanguage, notificationCancelledLegalFact);
    }

    @Override
    public File notificationReceivedLegalFact(LanguageEnum xLanguage, NotificationReceiverLegalFact notificationReceiverLegalFact) throws RestClientException {
        return templateApi.notificationReceivedLegalFact(xLanguage, notificationReceiverLegalFact);
    }

    @Override
    public File notificationViewedLegalFact(LanguageEnum xLanguage, NotificationViewedLegalFact notificationViewedLegalFact) throws RestClientException {
        return templateApi.notificationViewedLegalFact(xLanguage, notificationViewedLegalFact);
    }

    @Override
    public File pecDeliveryWorkflowLegalFact(LanguageEnum xLanguage, PecDeliveryWorkflowLegalFact pecDeliveryWorkflowLegalFact) throws RestClientException {
        return templateApi.pecDeliveryWorkflowLegalFact(xLanguage, pecDeliveryWorkflowLegalFact);
    }

    @Override
    public String pecbody(LanguageEnum xLanguage, Pecbody pecbody) throws RestClientException {
        return templateApi.pecVerificationCodeBody(xLanguage, pecbody);
    }

    @Override
    public String pecbodyconfirm(LanguageEnum xLanguage, Pecbody pecbody) throws RestClientException {
        return templateApi.pecValidationContactsSuccessBody(xLanguage, pecbody);
    }

    @Override
    public String pecbodyreject(LanguageEnum xLanguage) throws RestClientException {
        return templateApi.pecValidationContactsRejectBody(xLanguage);
    }

    @Override
    public String pecsubject(LanguageEnum xLanguage) throws RestClientException {
        return templateApi.pecVerificationCodeSubject(xLanguage);
    }

    @Override
    public String pecsubjectconfirm(LanguageEnum xLanguage) throws RestClientException {
        return templateApi.pecValidationContactsSuccessSubject(xLanguage);
    }

    @Override
    public String pecsubjectreject(LanguageEnum xLanguage) throws RestClientException {
        return templateApi.pecValidationContactsRejectSubject(xLanguage);
    }

    @Override
    public String smsbody(LanguageEnum xLanguage) throws RestClientException {
        return templateApi.smsVerificationCodeBody(xLanguage);
    }
}
