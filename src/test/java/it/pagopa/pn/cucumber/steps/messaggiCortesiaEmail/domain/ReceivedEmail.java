package it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.domain;

import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Email letta dalla casella di test: oggetto, corpo HTML decodificato e allegati (solo metadati).
 */
public record ReceivedEmail(String messageId, String from, String subject, String htmlBody, List<Attachment> attachments) {

    /**
     * Testo del corpo senza tag HTML, con le entità decodificate e gli spazi compattati.
     */
    public String plainTextBody() {
        return HtmlUtils.htmlUnescape(htmlBody.replaceAll("<[^>]+>", " ")).replaceAll("[\\s\\u00A0]+", " ");
    }

    public record Attachment(String filename, String mimeType, String attachmentId) {
    }
}
