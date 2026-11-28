package it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.client.b2b.pa.config.springconfig.TestEmailConfig;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.FullSentNotificationV29;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.TimelineElementCategoryV28;
import it.pagopa.pn.cucumber.steps.messaggiCortesiaEmail.domain.ReceivedEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lettura della casella email di test tramite Gmail API (OAuth 2.0, scope gmail.readonly).
 * Usa l'HttpClient del JDK e non RestTemplate: con RestTemplate a DEBUG (config/application.properties)
 * il modulo OAuth, credenziali comprese, finirebbe nei log.
 */
@Slf4j
@Component
public class TestEmailInboxService {
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GMAIL_URL = "https://gmail.googleapis.com/gmail/v1/users/me/messages";
    private static final int MAX_RESULTS = 10;
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private static final int MAX_ERROR_BODY = 300;
    private static final String SAFESTORAGE_PREFIX = "safestorage://";

    private final TestEmailConfig config;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public TestEmailInboxService(TestEmailConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();
    }

    /**
     * Ultima email arrivata alla casella di test, negli ultimi due giorni, con {@code subjectText} nell'oggetto.
     */
    public Optional<ReceivedEmail> findBySubjectText(String subjectText) {
        String accessToken = accessToken();
        String query = "to:" + config.getAddress() + " \"" + subjectText + "\" newer_than:2d";
        URI listUri = URI.create(GMAIL_URL + "?q=" + encode(query) + "&maxResults=" + MAX_RESULTS);
        JsonNode messages = get(listUri, accessToken).path("messages");
        for (JsonNode message : messages) {
            ReceivedEmail email = readMessage(message.path("id").asText(), accessToken);
            if (email.subject() != null && email.subject().contains(subjectText)) {
                log.info("Email trovata nella casella di test: id={} oggetto={}", email.messageId(), email.subject());
                return Optional.of(email);
            }
        }
        return Optional.empty();
    }

    /**
     * Nome con cui l'AAR della notifica arriva in allegato: la chiave SafeStorage di AAR_GENERATION senza prefisso.
     */
    public String expectedAarAttachmentName(FullSentNotificationV29 notification) {
        return notification.getTimeline().stream()
                .filter(element -> TimelineElementCategoryV28.AAR_GENERATION.equals(element.getCategory()))
                .filter(element -> element.getDetails() != null && element.getDetails().getGeneratedAarUrl() != null)
                .map(element -> element.getDetails().getGeneratedAarUrl())
                .findFirst()
                .orElseThrow(() -> new AssertionError("AAR_GENERATION assente nella timeline di " + notification.getIun()))
                .replace(SAFESTORAGE_PREFIX, "");
    }

    /**
     * Contenuto dell'allegato {@code attachmentId} dell'email {@code messageId}.
     */
    public byte[] downloadAttachment(String messageId, String attachmentId) {
        URI uri = URI.create(GMAIL_URL + "/" + encode(messageId) + "/attachments/" + encode(attachmentId));
        return decode(get(uri, accessToken()).path("data").asText());
    }

    private ReceivedEmail readMessage(String messageId, String accessToken) {
        URI uri = URI.create(GMAIL_URL + "/" + encode(messageId) + "?format=full");
        JsonNode payload = get(uri, accessToken).path("payload");
        String from = null;
        String subject = null;
        for (JsonNode header : payload.path("headers")) {
            String name = header.path("name").asText();
            if ("From".equalsIgnoreCase(name)) {
                from = header.path("value").asText();
            } else if ("Subject".equalsIgnoreCase(name)) {
                subject = header.path("value").asText();
            }
        }
        StringBuilder html = new StringBuilder();
        List<ReceivedEmail.Attachment> attachments = new ArrayList<>();
        collectParts(payload, html, attachments);
        return new ReceivedEmail(messageId, from, subject, html.toString(), attachments);
    }

    private void collectParts(JsonNode part, StringBuilder html, List<ReceivedEmail.Attachment> attachments) {
        String mimeType = part.path("mimeType").asText();
        JsonNode body = part.path("body");
        if (StringUtils.hasText(part.path("filename").asText()) && body.hasNonNull("attachmentId")) {
            attachments.add(new ReceivedEmail.Attachment(part.path("filename").asText(), mimeType,
                    body.path("attachmentId").asText()));
        } else if ("text/html".equalsIgnoreCase(mimeType) && body.hasNonNull("data")) {
            html.append(new String(decode(body.path("data").asText()), StandardCharsets.UTF_8));
        }
        for (JsonNode child : part.path("parts")) {
            collectParts(child, html, attachments);
        }
    }

    private String accessToken() {
        if (!StringUtils.hasText(config.getRefreshToken())) {
            throw new IllegalStateException("Credenziali della casella di test assenti: valorizzare pn.test.email.*");
        }
        Map<String, String> form = new LinkedHashMap<>();
        form.put("client_id", config.getClientId());
        form.put("client_secret", config.getClientSecret());
        form.put("refresh_token", config.getRefreshToken());
        form.put("grant_type", "refresh_token");
        HttpRequest request = HttpRequest.newBuilder(URI.create(TOKEN_URL))
                .timeout(REQUEST_TIMEOUT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(formEncode(form)))
                .build();
        return send(request).path("access_token").asText();
    }

    private JsonNode get(URI uri, String accessToken) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(REQUEST_TIMEOUT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .GET()
                .build();
        return send(request);
    }

    private JsonNode send(HttpRequest request) {
        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new InboxUnavailableException("Chiamata a " + request.uri().getHost() + " non riuscita", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Chiamata a " + request.uri().getHost() + " interrotta", e);
        }
        int status = response.statusCode();
        if (status / 100 != 2) {
            // il corpo degli errori Google non contiene token: serve per capire quota, consenso revocato ecc.
            String body = response.body() == null ? "" : response.body();
            String message = "HTTP " + status + " da " + request.uri().getHost() + request.uri().getPath() + ": "
                    + body.substring(0, Math.min(body.length(), MAX_ERROR_BODY));
            if (status == 429 || status / 100 == 5) {
                throw new InboxUnavailableException(message, null);
            }
            throw new IllegalStateException(message);
        }
        return readTree(response.body());
    }

    private JsonNode readTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Risposta Gmail API non leggibile", e);
        }
    }

    private static String formEncode(Map<String, String> form) {
        return form.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    /**
     * Codifica per URL; lo spazio diventa %20 e non +, valido sia nel path sia nella query.
     */
    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static byte[] decode(String base64Url) {
        return Base64.getUrlDecoder().decode(base64Url);
    }

    /**
     * Casella momentaneamente non raggiungibile (rete, 429, 5xx): chi fa polling può riprovare.
     */
    public static class InboxUnavailableException extends RuntimeException {
        public InboxUnavailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
