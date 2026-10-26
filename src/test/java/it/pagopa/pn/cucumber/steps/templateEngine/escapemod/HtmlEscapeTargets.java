package it.pagopa.pn.cucumber.steps.templateEngine.escapemod;

import java.util.LinkedHashMap;
import java.util.Map;

public final class HtmlEscapeTargets {

    private static final String ENDPOINT_PREFIX =
            "/templates-engine-private/v1/templates/";

    private static final Map<String, HtmlEscapeTarget> TARGETS = createTargets();

    private HtmlEscapeTargets() {
    }

    public static HtmlEscapeTarget get(String endpoint) {
        String normalizedEndpoint = endpoint.trim().toLowerCase();

        if (normalizedEndpoint.startsWith(ENDPOINT_PREFIX)) {
            normalizedEndpoint = normalizedEndpoint.substring(ENDPOINT_PREFIX.length());
        }

        HtmlEscapeTarget target = TARGETS.get(normalizedEndpoint);

        if (target == null) {
            throw new IllegalArgumentException(
                    "Endpoint non configurato per HTML escape fuzzing: " + endpoint
            );
        }

        return target;
    }

    private static Map<String, HtmlEscapeTarget> createTargets() {
        Map<String, HtmlEscapeTarget> targets = new LinkedHashMap<>();

        targets.put(
                "notification-received-legal-fact",
                new HtmlEscapeTarget(
                        "notification-received-legal-fact",
                        "ATTESTAZIONE OPPONIBILE A TERZI DI NOTIFICA PRESA IN CARICO",
                        null,
                        "pdf",
                        fieldMappings(
                                "subject", "context_subject",
                                "notification.sender.paDenomination", "notification_sender_paDenomination",
                                "notification.recipients.denomination", "notification_recipient_denomination",
                                "notification.recipients.physicalAddressAndDenomination", "notification_recipient_physicalAddress",
                                "notification.recipients.digitalDomicile.address", "notification_recipient_digitalDomicile_address"
                        )
                )
        );

        targets.put(
                "pec-delivery-workflow-legal-fact",
                new HtmlEscapeTarget(
                        "pec-delivery-workflow-legal-fact",
                        "ATTESTAZIONE OPPONIBILE A TERZI DI NOTIFICA DIGITALE",
                        null,
                        "pdf",
                        fieldMappings(
                                "deliveries.denomination", "delivery_denomination",
                                "deliveries.address", "delivery_address"
                        )
                )
        );

        targets.put(
                "notification-viewed-legal-fact",
                new HtmlEscapeTarget(
                        "notification-viewed-legal-fact",
                        "ATTESTAZIONE OPPONIBILE A TERZI DI AVVENUTO ACCESSO",
                        null,
                        "pdf",
                        fieldMappings(
                                "recipient.denomination", "recipient_denomination",
                                "delegate.denomination", "delegate_denomination"
                        )
                )
        );

        targets.put(
                "notification-cancelled-legal-fact",
                new HtmlEscapeTarget(
                        "notification-cancelled-legal-fact",
                        "DICHIARAZIONE DI ANNULLAMENTO NOTIFICA",
                        null,
                        "pdf",
                        fieldMappings(
                                "notification.sender.paDenomination", "notification_sender_paDenomination",
                                "notification.recipients.denomination", "notification_recipient_denomination"
                        )
                )
        );

        targets.put(
                "analog-delivery-workflow-failure-legal-fact",
                new HtmlEscapeTarget(
                        "analog-delivery-workflow-failure-legal-fact",
                        "DEPOSITO DI AVVENUTA RICEZIONE",
                        null,
                        "pdf",
                        fieldMappings(
                                "recipient.denomination", "recipient_denomination"
                        )
                )
        );

        targets.put(
                "analog-delivery-workflow-timeout-legal-fact",
                new HtmlEscapeTarget(
                        "analog-delivery-workflow-timeout-legal-fact",
                        null,
                        "analogDeliveryWorkflowTimeoutLegalFact",
                        "pdf",
                        fieldMappings(
                                "recipient.denomination", "recipient.denomination",
                                "recipient.physicalAddress", "recipient.physicalAddress"
                        )
                )
        );

        targets.put(
                "notification-aar-radd-alt",
                new HtmlEscapeTarget(
                        "notification-aar-radd-alt",
                        "AVVISO DI AVVENUTA RICEZIONE RADD",
                        null,
                        "pdf",
                        fieldMappings(
                                "notification.subject", "notification_subject",
                                "notification.sender.paDenomination", "notification_sender_paDenomination",
                                "recipient.denomination", "recipient_denomination"
                        )
                )
        );

        targets.put(
                "notification-aar",
                new HtmlEscapeTarget(
                        "notification-aar",
                        "AVVISO DI AVVENUTA RICEZIONE",
                        null,
                        "pdf",
                        fieldMappings(
                                "notification.subject", "notification_subject",
                                "notification.sender.paDenomination", "notification_sender_paDenomination"
                        )
                )
        );

        targets.put(
                "analog-feedback-availability-statement",
                new HtmlEscapeTarget(
                        "analog-feedback-availability-statement",
                        null,
                        "analogFeedbackAvailabilityStatement",
                        "pdf",
                        fieldMappings(
                                "senderDenomination", "senderDenomination"
                        )
                )
        );

        targets.put(
                "notification-aar-for-email",
                new HtmlEscapeTarget(
                        "notification-aar-for-email",
                        "AVVISO DI CORTESIA EMAIL",
                        null,
                        "html",
                        fieldMappings(
                                "notification.sender.paDenomination", "notification_sender_paDenomination"
                        )
                )
        );

        targets.put(
                "notification-aar-for-email-digital",
                new HtmlEscapeTarget(
                        "notification-aar-for-email-digital",
                        "AVVISO DI CORTESIA EMAIL DIGITALE",
                        null,
                        "html",
                        fieldMappings(
                                "notification.sender.paDenomination", "notification_sender_paDenomination"
                        )
                )
        );

        targets.put(
                "notification-aar-for-pec",
                new HtmlEscapeTarget(
                        "notification-aar-for-pec",
                        "AVVISO DI CORTESIA PEC",
                        null,
                        "html",
                        fieldMappings(
                                "notification.subject", "notification_subject",
                                "notification.sender.paDenomination", "notification_sender_paDenomination"
                        )
                )
        );

        targets.put(
                "notification-cce-for-email",
                new HtmlEscapeTarget(
                        "notification-cce-for-email",
                        null,
                        "notificationCceForEmail",
                        "html",
                        fieldMappings(
                                "denomination", "denomination"
                        )
                )
        );

        Map<String, String> informalMappings = fieldMappings(
                "subject", "subject",
                "sender.denomination", "sender_denomination",
                "sender.service", "sender_service",
                "recipient.denomination", "recipient_denomination"
        );

        targets.put(
                "informal/analog-communication",
                new HtmlEscapeTarget(
                        "informal/analog-communication",
                        "COMUNICAZIONE BONARIA POSTA CARTACEA",
                        null,
                        "pdf",
                        informalMappings
                )
        );

        targets.put(
                "informal/email-communication-body",
                new HtmlEscapeTarget(
                        "informal/email-communication-body",
                        "EMAIL BODY COMUNICAZIONE BONARIA",
                        null,
                        "html",
                        informalMappings
                )
        );

        targets.put(
                "informal/pec-communication-body",
                new HtmlEscapeTarget(
                        "informal/pec-communication-body",
                        "PEC BODY COMUNICAZIONE BONARIA",
                        null,
                        "html",
                        informalMappings
                )
        );

        targets.put(
                "informal/io-communication",
                new HtmlEscapeTarget(
                        "informal/io-communication",
                        "IO COMUNICAZIONE BONARIA",
                        null,
                        "text",
                        informalMappings
                )
        );

        targets.put(
                "informal/courtesy-email-communication-body",
                new HtmlEscapeTarget(
                        "informal/courtesy-email-communication-body",
                        null,
                        "courtesyEmailCommunicationBody",
                        "html",
                        fieldMappings(
                                "subject", "subject",
                                "sender.denomination", "sender.denomination",
                                "sender.service", "sender.service",
                                "recipient.denomination", "recipient.denomination"
                        )
                )
        );

        return targets;
    }

    private static Map<String, String> fieldMappings(String... values) {
        Map<String, String> mappings = new LinkedHashMap<>();

        for (int i = 0; i < values.length; i += 2) {
            mappings.put(values[i], values[i + 1]);
        }

        return mappings;
    }
}