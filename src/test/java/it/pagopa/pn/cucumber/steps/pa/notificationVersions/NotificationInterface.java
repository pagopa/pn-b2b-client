package it.pagopa.pn.cucumber.steps.pa.notificationVersions;

import java.util.Map;

public interface NotificationInterface {

    void setNotificationRequest(Map<String, String> data);

    void addRecipitentToNotification(String destinatario, Map<String, String> data);

    void sendNotification(String status, int wait);
//
//    void destinatarioMarioCucumber();
//
//    void destinatarioMarioCucumberParam(Map<String, String> data);
//
//    void destinatarioMarioGherkin();
//
//    void destinatarioMarioGherkinParam(Map<String, String> data);
//
//    void destinatarioGherkinSpa();
//
//    void destinatarioGherkinSpaParam(Map<String, String> data);
//
//    void destinatarioGherkinSrl();
//
//    void destinatarioGherkinSrlParam(Map<String, String> data);
//
//    void destinatarioCucumberSpa();
//
//    void destinatarioCucumberSpaParam(Map<String, String> data);
//
//    void destinatarioCucumberSrl();
//
//    void destinatarioCucumberSrlParam(Map<String, String> data);
//
//    void destinatarioSignorCasuale();
//
//    void destinatarioSignorCasualeParam(Map<String, String> data);
//
//    void destinatarioGherkinAnalogicParam(Map<String, String> data);
//
//    void destinatarioCucumberAnalogicParam(Map<String, String> data);
//
//    void destinatarioGherkinIrreperibileParam(Map<String, String> data);
//
//    void destinatarioCucumberSociety();
//
//    void destinatarioCucumberSocietyParam(Map<String, String> data);
//
//    void destinatarioCristoforoColombo();


}
