package it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import it.pagopa.common.polling.Poller;
import it.pagopa.common.polling.PollingConfig;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.cucumber.steps.SharedSteps;
import it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.domain.CourtesyEmailLanguage;
import it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.domain.ReceivedEmail;
import it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.service.TestEmailInboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.domain.CourtesyEmailLanguage.ITALIAN_BUTTON;
import static it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.domain.CourtesyEmailLanguage.ITALIAN_HELLO;
import static it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.domain.CourtesyEmailLanguage.ITALIAN_SUBJECT;
import static it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.domain.CourtesyEmailLanguage.SEPARATOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifiche sull'email di cortesia recapitata alla casella di test (config pn.test.email.*).
 */
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class MessaggiCortesiaEmailSteps {
    private static final Duration EMAIL_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration EMAIL_POLL_INTERVAL = Duration.ofSeconds(20);

    private final SharedSteps sharedSteps;
    private final TestEmailInboxService inboxService;

    private FullSentNotificationV29 notification;
    private ReceivedEmail courtesyEmail;

    @Then("l'email di cortesia della notifica arriva nella casella di test")
    public void courtesyEmailArrives() {
        notification = sharedSteps.getSentNotificationLastVersion();
        String iun = notification.getIun();
        courtesyEmail = Poller.pollUntil(() -> findCourtesyEmail(iun), Optional::isPresent,
                        PollingConfig.of(EMAIL_TIMEOUT, EMAIL_POLL_INTERVAL),
                        last -> "Email di cortesia della notifica " + iun + " non arrivata nella casella di test")
                .orElseThrow();
    }

    @And("l'oggetto dell'email di cortesia riporta l'ente mittente e lo IUN")
    public void subjectHasSenderAndIun() {
        String italianSubject = ITALIAN_SUBJECT + " " + notification.getSenderDenomination() + " - " + notification.getIun();
        String expected = additionalLanguage()
                .map(language -> "SEND - " + language.subject() + SEPARATOR + italianSubject)
                .orElse("SEND - " + italianSubject);
        assertThat(courtesyEmail.subject()).isEqualTo(expected);
    }

    @And("il corpo dell'email di cortesia riporta l'ente mittente e lo IUN")
    public void bodyHasSenderAndIun() {
        assertThat(courtesyEmail.plainTextBody())
                .contains(notification.getSenderDenomination())
                .contains(notification.getIun());
    }

    @And("l'email di cortesia ha in allegato l'AAR della notifica")
    public void attachmentIsNotificationAar() {
        String aarKey = inboxService.expectedAarAttachmentName(notification);
        ReceivedEmail.Attachment aar = courtesyEmail.attachments().stream()
                .filter(attachment -> aarKey.equals(attachment.filename()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Allegato " + aarKey + " assente, allegati: " + courtesyEmail.attachments()));
        byte[] content = inboxService.downloadAttachment(courtesyEmail.messageId(), aar.attachmentId());
        assertThat(new String(content, 0, Math.min(content.length, 5), StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
    }

    @And("l'email di cortesia riporta i testi nella lingua aggiuntiva della notifica e in italiano")
    public void bodyIsBilingual() {
        CourtesyEmailLanguage language = additionalLanguage()
                .orElseThrow(() -> new AssertionError("La notifica " + notification.getIun() + " non ha una lingua aggiuntiva"));
        assertThat(courtesyEmail.plainTextBody())
                .contains(language.hello() + SEPARATOR + ITALIAN_HELLO)
                .contains(language.button() + SEPARATOR + ITALIAN_BUTTON);
    }

    private Optional<ReceivedEmail> findCourtesyEmail(String iun) {
        try {
            return inboxService.findBySubjectText(iun);
        } catch (TestEmailInboxService.InboxUnavailableException e) {
            // rete, 429 o 5xx da Gmail: si riprova al giro successivo, gli altri errori restano bloccanti
            log.warn("Casella di test non raggiungibile, nuovo tentativo al prossimo giro: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<CourtesyEmailLanguage> additionalLanguage() {
        List<String> languages = notification.getAdditionalLanguages();
        return languages == null || languages.isEmpty()
                ? Optional.empty()
                : Optional.of(CourtesyEmailLanguage.valueOf(languages.get(0)));
    }
}
