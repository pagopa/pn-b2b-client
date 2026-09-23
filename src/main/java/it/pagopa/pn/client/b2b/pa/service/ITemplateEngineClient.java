package it.pagopa.pn.client.b2b.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.*;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClientException;

public interface ITemplateEngineClient {
    Resource analogDeliveryWorkflowFailureLegalFact(LanguageEnum xLanguage, AnalogDeliveryWorkflowFailureLegalFact analogDeliveryWorkflowFailureLegalFact) throws RestClientException;

    String emailbody(LanguageEnum xLanguage, MailVerificationCodeBody emailbody) throws RestClientException;

    String emailsubject(LanguageEnum xLanguage) throws RestClientException;

    Resource legalFactMalfunction(LanguageEnum xLanguage, MalfunctionLegalFact legalFactMalfunction) throws RestClientException;

    Resource notificationAAR(LanguageEnum xLanguage, NotificationAar notificationAAR) throws RestClientException;

    String notificationAARForEMAILDigital(LanguageEnum xLanguage, NotificationAarForEmailDigital notificationAARForEMAIL) throws RestClientException;

    String notificationAARForEMAILAnalog(LanguageEnum xLanguage, NotificationAarForEmailAnalog notificationAARForEMAIL) throws RestClientException;

    String notificationAARForPEC(LanguageEnum xLanguage, NotificationAarForPec notificationAARForPEC) throws RestClientException;

    String notificationAARForSMSDigital(LanguageEnum xLanguage, NotificationAarForSmsDigital notificationAARForSMS) throws RestClientException;

    String notificationAARForSMSAnalog(LanguageEnum xLanguage, NotificationAarForSmsAnalog notificationAARForSMS) throws RestClientException;

    Resource notificationAARRADDalt(LanguageEnum xLanguage, NotificationAarRaddAlt notificationAARRADDalt) throws RestClientException;

    String notificationAARSubject(LanguageEnum xLanguage, NotificationAarForSubject notificationAARSubject);

    Resource notificationCancelledLegalFact(LanguageEnum xLanguage, NotificationCancelledLegalFact notificationCancelledLegalFact) throws RestClientException;

    Resource notificationReceivedLegalFact(LanguageEnum xLanguage, NotificationReceivedLegalFact notificationReceiverLegalFact) throws RestClientException;

    Resource notificationViewedLegalFact(LanguageEnum xLanguage, NotificationViewedLegalFact notificationViewedLegalFact) throws RestClientException;

    Resource pecDeliveryWorkflowLegalFact(LanguageEnum xLanguage, PecDeliveryWorkflowLegalFact pecDeliveryWorkflowLegalFact) throws RestClientException;

    String pecbody(LanguageEnum xLanguage, PecVerificationCodeBody pecbody) throws RestClientException;

    String pecbodyconfirm(LanguageEnum xLanguage) throws RestClientException;

    String pecbodyreject(LanguageEnum xLanguage) throws RestClientException;

    String pecsubject(LanguageEnum xLanguage) throws RestClientException;

    String pecsubjectconfirm(LanguageEnum xLanguage) throws RestClientException;

    String pecsubjectreject(LanguageEnum xLanguage) throws RestClientException;

    String smsbody(LanguageEnum xLanguage) throws RestClientException;

    Resource informalAnalogCommunication(LanguageEnum xLanguage, InformalCommunication informalCommunication) throws RestClientException;

    String informalEmailCommunicationBody(LanguageEnum xLanguage, InformalCommunication informalCommunication) throws RestClientException;

    String informalEmailCommunicationSubject(LanguageEnum xLanguage, InformalEmailCommunicationSubject informalEmailCommunicationSubject) throws RestClientException;

    String informalPecCommunicationBody(LanguageEnum xLanguage, InformalCommunication informalCommunication) throws RestClientException;

    String informalPecCommunicationSubject(LanguageEnum xLanguage, InformalEmailCommunicationSubject informalEmailCommunicationSubject);

    String informalIoCommunication(LanguageEnum xLanguage, InformalCommunication informalCommunication) throws RestClientException;

    String informalSmsCommunication(LanguageEnum xLanguage, InformalSmsCommunication informalSmsCommunication) throws RestClientException;
}
