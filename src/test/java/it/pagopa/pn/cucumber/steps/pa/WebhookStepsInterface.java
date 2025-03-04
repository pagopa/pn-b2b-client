package it.pagopa.pn.cucumber.steps.pa;

import java.util.UUID;

public interface WebhookStepsInterface {

    void deleteStreams(String pa);

    void initializeStreamRequest(String action, String pa);

    void updateStream(UUID idStream);

    void verifyEventNotInStream();
}