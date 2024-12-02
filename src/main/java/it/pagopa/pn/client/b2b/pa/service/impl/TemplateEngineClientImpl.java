package it.pagopa.pn.client.b2b.pa.service.impl;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.ApiClient;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.api.TemplateApi;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
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

    public TemplateEngineClientImpl(RestTemplate restTemplate, @Value("${pn.delivery.base-url}") String basePath) {
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
    public String emailbody(LanguageEnum xLanguage, MailVerificationCodeBody emailbody) throws RestClientException {
        return templateApi.mailVerificationCodeBody(xLanguage, emailbody);
    }

    @Override
    public String emailsubject(LanguageEnum xLanguage) throws RestClientException {
        return templateApi.mailVerificationCodeSubject(xLanguage);
    }

    @Override
    public File legalFactMalfunction(LanguageEnum xLanguage, MalfunctionLegalFact legalFactMalfunction) throws RestClientException {
        return templateApi.malfunctionLegalFact(xLanguage, legalFactMalfunction);
    }

    @Override
    public File notificationAAR(LanguageEnum xLanguage, NotificationAar notificationAAR) throws RestClientException {
        return templateApi.notificationAar(xLanguage, notificationAAR);
    }

    @Override
    public String notificationAARForEMAIL(LanguageEnum xLanguage, NotificationAarForEmail notificationAARForEMAIL) throws RestClientException {
        return templateApi.notificationAarForEmail(xLanguage, notificationAARForEMAIL);
    }

    @Override
    public String notificationAARForPEC(LanguageEnum xLanguage, NotificationAarForPec notificationAARForPEC) throws RestClientException {
        return templateApi.notificationAarForPec(xLanguage, notificationAARForPEC);
    }

    @Override
    public String notificationAARForSMS(LanguageEnum xLanguage, NotificationAarForSms notificationAARForSMS) throws RestClientException {
        return templateApi.notificationAarForSms(xLanguage, notificationAARForSMS);
    }

    @Override
    public File notificationAARRADDalt(LanguageEnum xLanguage, NotificationAarRaddAlt notificationAARRADDalt) throws RestClientException {
        return templateApi.notificationAarRaddAlt(xLanguage, notificationAARRADDalt);
    }

    @Override
    public String notificationAARSubject(LanguageEnum xLanguage, NotificationAarForSubject notificationAARSubject) throws RestClientException {
        return templateApi.notificationAarForSubject(xLanguage, notificationAARSubject);
    }

    @Override
    public File notificationCancelledLegalFact(LanguageEnum xLanguage, NotificationCancelledLegalFact notificationCancelledLegalFact) throws RestClientException {
        return templateApi.notificationCancelledLegalFact(xLanguage, notificationCancelledLegalFact);
    }

    @Override
    public File notificationReceivedLegalFact(LanguageEnum xLanguage, NotificationReceivedLegalFact notificationReceiverLegalFact) throws RestClientException {
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
    public String pecbody(LanguageEnum xLanguage, PecVerificationCodeBody pecbody) throws RestClientException {
        return templateApi.pecVerificationCodeBody(xLanguage, pecbody);
    }

    @Override
    public String pecbodyconfirm(LanguageEnum xLanguage) throws RestClientException {
        return templateApi.pecValidationContactsSuccessBody(xLanguage);
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
