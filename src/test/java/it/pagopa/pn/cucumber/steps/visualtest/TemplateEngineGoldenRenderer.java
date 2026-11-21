package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AnalogDeliveryWorkflowFailureLegalFact;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AnalogDeliveryWorkflowTimeoutLegalFact;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.AnalogFeedbackAvailabilityStatement;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.InformalCommunication;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.LanguageEnum;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.MalfunctionLegalFact;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAar;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationAarRaddAlt;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationCancelledLegalFact;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationReceivedLegalFact;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.NotificationViewedLegalFact;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.PecDeliveryWorkflowLegalFact;
import it.pagopa.pn.client.b2b.pa.service.ITemplateEngineClient;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/** Renders a document with the Template Engine from the {@code templateEngine} section of its fixture. */
public class TemplateEngineGoldenRenderer {

    private static final LanguageEnum LANGUAGE = LanguageEnum.IT;

    private final ITemplateEngineClient client;

    public TemplateEngineGoldenRenderer(ITemplateEngineClient client) {
        this.client = client;
    }

    public byte[] render(VisualDocument document) {
        VisualFixture fixture = VisualFixture.load(document);
        Resource pdf = switch (document) {
            case SENDER_ACK -> client.notificationReceivedLegalFact(LANGUAGE,
                    fixture.templateEngineRequest(NotificationReceivedLegalFact.class));
            case PEC_DELIVERY -> client.pecDeliveryWorkflowLegalFact(LANGUAGE,
                    fixture.templateEngineRequest(PecDeliveryWorkflowLegalFact.class));
            case NOTIFICATION_VIEWED -> client.notificationViewedLegalFact(LANGUAGE,
                    fixture.templateEngineRequest(NotificationViewedLegalFact.class));
            case ANALOG_FAILURE -> client.analogDeliveryWorkflowFailureLegalFact(LANGUAGE,
                    fixture.templateEngineRequest(AnalogDeliveryWorkflowFailureLegalFact.class));
            case NOTIFICATION_CANCELLED -> client.notificationCancelledLegalFact(LANGUAGE,
                    fixture.templateEngineRequest(NotificationCancelledLegalFact.class));
            case MALFUNCTION -> client.legalFactMalfunction(LANGUAGE,
                    fixture.templateEngineRequest(MalfunctionLegalFact.class));
            case ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT ->client.analogDeliveryWorkflowTimeoutLegalFact(LANGUAGE,
                    fixture.templateEngineRequest(AnalogDeliveryWorkflowTimeoutLegalFact.class));
            case NOTIFICATION_AAR -> client.notificationAAR(LANGUAGE,
                    fixture.templateEngineRequest(NotificationAar.class));
            case NOTIFICATION_AAR_RADD_ALT -> client.notificationAARRADDalt(LANGUAGE,
                    fixture.templateEngineRequest(NotificationAarRaddAlt.class));
            case ANALOG_FEEDBACK_AVAILABILITY_STATEMENT -> client.analogFeedbackAvailabilityStatement(LANGUAGE,
                    fixture.templateEngineRequest(AnalogFeedbackAvailabilityStatement.class));
            case INFORMAL_ANALOG_COMMUNICATION -> client.informalAnalogCommunication(LANGUAGE,
                    fixture.templateEngineRequest(InformalCommunication.class));
        };
        if (pdf == null) {
            throw new IllegalStateException("Il Template Engine non ha restituito il PDF di " + document.key());
        }
        try (InputStream is = pdf.getInputStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("PDF del Template Engine non leggibile per " + document.key(), e);
        }
    }
}
