package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import org.springframework.web.client.RestClientException;

import java.io.File;

public interface ITemplateEngineClient {
    File analogDeliveryWorkflowFailureLegalFact(LanguageEnum xLanguage, AnalogDeliveryWorkflowFailureLegalFact analogDeliveryWorkflowFailureLegalFact) throws RestClientException;

    String emailbody(LanguageEnum xLanguage, MailVerificationCodeBody emailbody) throws RestClientException;

    String emailsubject(LanguageEnum xLanguage) throws RestClientException;

    File legalFactMalfunction(LanguageEnum xLanguage, MalfunctionLegalFact legalFactMalfunction) throws RestClientException;

    File notificationAAR(LanguageEnum xLanguage, NotificationAar notificationAAR) throws RestClientException;

    String notificationAARForEMAIL(LanguageEnum xLanguage, NotificationAarForEmail notificationAARForEMAIL) throws RestClientException;

    String notificationAARForPEC(LanguageEnum xLanguage, NotificationAarForPec notificationAARForPEC) throws RestClientException;

    String notificationAARForSMS(LanguageEnum xLanguage, NotificationAarForSms notificationAARForSMS) throws RestClientException;

    File notificationAARRADDalt(LanguageEnum xLanguage, NotificationAarRaddAlt notificationAARRADDalt) throws RestClientException;

    String notificationAARSubject(LanguageEnum xLanguage, NotificationAarForSubject notificationAARSubject);

    File notificationCancelledLegalFact(LanguageEnum xLanguage, NotificationCancelledLegalFact notificationCancelledLegalFact) throws RestClientException;

    File notificationReceivedLegalFact(LanguageEnum xLanguage, NotificationReceivedLegalFact notificationReceiverLegalFact) throws RestClientException;

    File notificationViewedLegalFact(LanguageEnum xLanguage, NotificationViewedLegalFact notificationViewedLegalFact) throws RestClientException;

    File pecDeliveryWorkflowLegalFact(LanguageEnum xLanguage, PecDeliveryWorkflowLegalFact pecDeliveryWorkflowLegalFact) throws RestClientException;

    String pecbody(LanguageEnum xLanguage, PecVerificationCodeBody pecbody) throws RestClientException;

    String pecbodyconfirm(LanguageEnum xLanguage) throws RestClientException;

    String pecbodyreject(LanguageEnum xLanguage) throws RestClientException;

    String pecsubject(LanguageEnum xLanguage) throws RestClientException;

    String pecsubjectconfirm(LanguageEnum xLanguage) throws RestClientException;

    String pecsubjectreject(LanguageEnum xLanguage) throws RestClientException;

    String smsbody(LanguageEnum xLanguage) throws RestClientException;
}
