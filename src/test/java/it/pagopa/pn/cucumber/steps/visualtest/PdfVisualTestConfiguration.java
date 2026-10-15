package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.common.pdf.visualtest.DocumentTemplateRegistry;
import it.pagopa.pn.cucumber.steps.visualtest.templates.AnalogDeliveryWorkflowTimeoutLegalFactTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.AnalogFeedbackAvailabilityStatementTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.AnalogFailureTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.InformalAnalogCommunicationTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.MalfunctionTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.NotificationAarRaddAltTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.NotificationAarTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.NotificationCancelledTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.NotificationViewedTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.PecDeliveryTemplate;
import it.pagopa.pn.cucumber.steps.visualtest.templates.SenderAckTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione Spring che registra il {@link DocumentTemplateRegistry} come bean
 * nel contesto del modulo {@code pn-b2bclient}.
 *
 * <p>Tutti gli 11 template di documenti PDF previsti sono costruiti e registrati qui.</p>
 * <ol>
 *   <li>{@link SenderAckTemplate} (SENDER_ACK)</li>
 *   <li>{@link PecDeliveryTemplate} (PEC_DELIVERY)</li>
 *   <li>{@link NotificationViewedTemplate} (NOTIFICATION_VIEWED)</li>
 *   <li>{@link AnalogFailureTemplate} (ANALOG_FAILURE)</li>
 *   <li>{@link NotificationCancelledTemplate} (NOTIFICATION_CANCELLED)</li>
 *   <li>{@link MalfunctionTemplate} (MALFUNCTION)</li>
 *   <li>{@link AnalogDeliveryWorkflowTimeoutLegalFactTemplate} (ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT)</li>
 *   <li>{@link NotificationAarTemplate} (NOTIFICATION_AAR)</li>
 *   <li>{@link NotificationAarRaddAltTemplate} (NOTIFICATION_AAR_RADD_ALT)</li>
 *   <li>{@link AnalogFeedbackAvailabilityStatementTemplate} (ANALOG_FEEDBACK_AVAILABILITY_STATEMENT)</li>
 *   <li>{@link InformalAnalogCommunicationTemplate} (INFORMAL_ANALOG_COMMUNICATION)</li>
 * </ol>
 *
 * <p>Il registry è {@code @Bean} singleton nel contesto Spring: non ha stato condiviso
 * statico e non interferisce con registry di altri moduli.</p>
 */
@Configuration
public class PdfVisualTestConfiguration {

    /**
     * Registro di tutti gli 11 template PDF disponibili nel modulo {@code pn-b2bclient}.
     */
    @Bean
    public DocumentTemplateRegistry documentTemplateRegistry() {
        return DocumentTemplateRegistry.builder()
                .register(SenderAckTemplate.build())
                .register(PecDeliveryTemplate.build())
                .register(NotificationViewedTemplate.build())
                .register(AnalogFailureTemplate.build())
                .register(NotificationCancelledTemplate.build())
                .register(MalfunctionTemplate.build())
                .register(AnalogDeliveryWorkflowTimeoutLegalFactTemplate.build())
                .register(NotificationAarTemplate.build())
                .register(NotificationAarRaddAltTemplate.build())
                .register(AnalogFeedbackAvailabilityStatementTemplate.build())
                .register(InformalAnalogCommunicationTemplate.build())
                .build();
    }
}
