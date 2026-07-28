package it.pagopa.pn.cucumber.steps.informalNotification.utils;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequestAdditionalMessage;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewMessageRequestPrimaryMessage;
import it.pagopa.pn.cucumber.steps.informalNotification.InformalDto.InformalMessageContent;

public final class InformalMessageUtils {

    private InformalMessageUtils() {
    }

    public static NewMessageRequestPrimaryMessage buildPrimaryMessage(InformalMessageContent content) {

        return new NewMessageRequestPrimaryMessage().language(content.getLanguage()).subject(content.getSubject()).longBody(content.getLongBody()).shortBody(content.getShortBody());
    }


    public static NewMessageRequestAdditionalMessage buildAdditionalMessage(InformalMessageContent content) {

        return new NewMessageRequestAdditionalMessage().language(content.getLanguage()).subject(content.getSubject()).longBody(content.getLongBody()).shortBody(content.getShortBody());
    }
}
