package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templates_engine.model.*;
import org.springframework.web.client.RestClientException;

import java.io.File;

public interface ITemplateEngineClient {
    File analogDeliveryWorkflowFailureLegalFact(LanguageEnum xLanguage, AnalogDeliveryWorkflowFailureLegalFact analogDeliveryWorkflowFailureLegalFact) throws RestClientException;

    String emailbody(LanguageEnum xLanguage, MailVerificationCodeBody emailbody) throws RestClientException;

    String emailsubject(LanguageEnum xLanguage) throws RestClientException;

    File legalFactMalfunction(LanguageEnum xLanguage, MalfunctionLegalFact legalFactMalfunction) throws RestClientException;

    File notificationAAR(LanguageEnum xLanguage, NotificationAAR notificationAAR) throws RestClientException;

    String notificationAARForEMAIL(LanguageEnum xLanguage, NotificationAARForEMAIL notificationAARForEMAIL) throws RestClientException;

    String notificationAARForPEC(LanguageEnum xLanguage, NotificationAARForPEC notificationAARForPEC) throws RestClientException;

    String notificationAARForSMS(LanguageEnum xLanguage, NotificationAARForSMS notificationAARForSMS) throws RestClientException;

    File notificationAARRADDalt(LanguageEnum xLanguage, NotificationAARRADDalt notificationAARRADDalt) throws RestClientException;

    String notificationAARSubject(LanguageEnum xLanguage, NotificationAARForSubject notificationAARSubject);

    File notificationCancelledLegalFact(LanguageEnum xLanguage, NotificationCancelledLegalFact notificationCancelledLegalFact) throws RestClientException;

    File notificationReceivedLegalFact(LanguageEnum xLanguage, NotificationReceiverLegalFact notificationReceiverLegalFact) throws RestClientException;

    File notificationViewedLegalFact(LanguageEnum xLanguage, NotificationViewedLegalFact notificationViewedLegalFact) throws RestClientException;

    File pecDeliveryWorkflowLegalFact(LanguageEnum xLanguage, PecDeliveryWorkflowLegalFact pecDeliveryWorkflowLegalFact) throws RestClientException;

    String pecbody(LanguageEnum xLanguage, PecVerificationCodeBody pecbody) throws RestClientException;

    String pecbodyconfirm(LanguageEnum xLanguage, PecValidationContactsSuccessBody pecbody) throws RestClientException;

    String pecbodyreject(LanguageEnum xLanguage) throws RestClientException;

    String pecsubject(LanguageEnum xLanguage) throws RestClientException;

    String pecsubjectconfirm(LanguageEnum xLanguage) throws RestClientException;

    String pecsubjectreject(LanguageEnum xLanguage) throws RestClientException;

    String smsbody(LanguageEnum xLanguage) throws RestClientException;
}
