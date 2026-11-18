package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.PdfDocumentTemplate;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV28;
import it.pagopa.pn.cucumber.steps.visualtest.templates.AnalogDeliveryWorkflowTimeoutLegalFactTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.AnalogFailureTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.AnalogFeedbackAvailabilityStatementTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.InformalAnalogCommunicationTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.MalfunctionTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.NotificationAarRaddAltTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.NotificationAarTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.NotificationCancelledTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.NotificationViewedTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.PecDeliveryTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.SenderAckTemplate;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;

/** Documents under visual regression: how each one is retrieved from SEND and which template describes it. */
public enum VisualDocument {

    SENDER_ACK(SenderAckTemplate::build,
            Source.legalFact(TimelineElementCategoryV28.REQUEST_ACCEPTED, "SENDER_ACK")),
    PEC_DELIVERY(PecDeliveryTemplate::build,
            Source.legalFact(TimelineElementCategoryV28.DIGITAL_SUCCESS_WORKFLOW, "DIGITAL_DELIVERY")),
    NOTIFICATION_VIEWED(NotificationViewedTemplate::build,
            Source.legalFact(TimelineElementCategoryV28.NOTIFICATION_VIEWED, "RECIPIENT_ACCESS")),
    ANALOG_FAILURE(AnalogFailureTemplate::build,
            Source.legalFact(TimelineElementCategoryV28.COMPLETELY_UNREACHABLE, "ANALOG_FAILURE_DELIVERY")),
    NOTIFICATION_CANCELLED(NotificationCancelledTemplate::build,
            Source.legalFact(TimelineElementCategoryV28.NOTIFICATION_CANCELLED, "NOTIFICATION_CANCELLED")),
    MALFUNCTION(MalfunctionTemplate::build, Source.downtime()),
    ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT(AnalogDeliveryWorkflowTimeoutLegalFactTemplate::build,
            Source.hiddenLegalFact("SEND_ANALOG_TIMEOUT_CREATION_REQUEST")),
    NOTIFICATION_AAR(NotificationAarTemplate::build, Source.aar("PN_AAR")),
    NOTIFICATION_AAR_RADD_ALT(NotificationAarRaddAltTemplate::build, Source.aar("PN_AAR")),
    ANALOG_FEEDBACK_AVAILABILITY_STATEMENT(AnalogFeedbackAvailabilityStatementTemplate::build,
            Source.notRetrievable("produced only by an operations script, never by a notification workflow")),
    INFORMAL_ANALOG_COMMUNICATION(InformalAnalogCommunicationTemplate::build,
            Source.notRetrievable("cover page stored in SafeStorage without a download API"));

    private final Supplier<PdfDocumentTemplate> template;
    private final Source source;

    VisualDocument(Supplier<PdfDocumentTemplate> template, Source source) {
        this.template = template;
        this.source = source;
    }

    public String key() {
        return name();
    }

    public String folder() {
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    public PdfDocumentTemplate template() {
        return template.get();
    }

    public Source source() {
        return source;
    }

    public static VisualDocument of(String key) {
        return Arrays.stream(values()).filter(d -> d.name().equals(key)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Documento VRT sconosciuto: " + key));
    }

    /** Where SEND exposes the document. */
    public record Source(Kind kind, TimelineElementCategoryV28 timelineCategory, String legalFactCategory,
                         String keyPrefix) {

        /**
         * NOT_RETRIEVABLE: golden master only, keyPrefix holds the reason. HIDDEN_LEGAL_FACT: the timeline element is
         * not exposed by B2B, keyPrefix is its category and the legal fact id is read from pn-Timelines; the file is
         * then downloaded through the usual B2B legal fact API.
         */
        public enum Kind { TIMELINE_LEGAL_FACT, HIDDEN_LEGAL_FACT, AAR, DOWNTIME, NOT_RETRIEVABLE }

        static Source legalFact(TimelineElementCategoryV28 timelineCategory, String legalFactCategory) {
            return new Source(Kind.TIMELINE_LEGAL_FACT, timelineCategory, legalFactCategory, null);
        }

        static Source hiddenLegalFact(String timelineCategory) {
            return new Source(Kind.HIDDEN_LEGAL_FACT, null, null, timelineCategory);
        }

        static Source aar(String keyPrefix) {
            return new Source(Kind.AAR, TimelineElementCategoryV28.AAR_GENERATION, null, keyPrefix);
        }

        static Source downtime() {
            return new Source(Kind.DOWNTIME, null, null, null);
        }

        static Source notRetrievable(String reason) {
            return new Source(Kind.NOT_RETRIEVABLE, null, null, reason);
        }
    }
}
