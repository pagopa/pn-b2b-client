package it.pagopa.pn.cucumber.steps.pa.service;

import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.model.mock.pec.PaperEngageRequest;
import it.pagopa.pn.client.b2b.generated.openapi.clients.externalchannels.model.mock.pec.ReceivedMessage;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpa.model.NotificationDocument;
import it.pagopa.pn.client.b2b.pa.service.impl.PnExternalChannelsServiceClientImpl;
import it.pagopa.pn.cucumber.steps.pa.domain.DocumentTransformation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Recupera le fileKey SafeStorage dei documenti di una notifica e dei documenti trasformati
 * ricevuti dal recapitista simulato.
 */
@Service
@RequiredArgsConstructor
public class TransformedDocumentService {

    private static final String SAFE_STORAGE_PREFIX = "safestorage://";

    private final PnExternalChannelsServiceClientImpl externalChannelsClient;

    public List<String> getDocumentKeys(List<NotificationDocument> documents) {
        return Optional.ofNullable(documents).orElse(List.of()).stream()
                .map(document -> toFileKey(document.getRef().getKey()))
                .toList();
    }

    /**
     * Restituisce, senza duplicati, le fileKey dei documenti trasformati presenti nelle richieste cartacee
     * ricevute dal recapitista per il destinatario.
     */
    public List<String> getTransformedDocumentKeys(String iun, Integer destinatario, DocumentTransformation transformation) {
        List<ReceivedMessage> messages = externalChannelsClient.getReceivedMessagesAnalogico(iun, destinatario);
        return Optional.ofNullable(messages).orElse(List.of()).stream()
                .map(ReceivedMessage::getPaperEngageRequest)
                .filter(Objects::nonNull)
                .map(PaperEngageRequest::getAttachments)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(attachment -> toFileKey(attachment.getUri()))
                .filter(fileKey -> fileKey != null && fileKey.startsWith(transformation.getFileKeyPrefix()))
                .distinct()
                .toList();
    }

    private static String toFileKey(String uri) {
        if (uri == null) {
            return null;
        }
        String fileKey = uri.startsWith(SAFE_STORAGE_PREFIX) ? uri.substring(SAFE_STORAGE_PREFIX.length()) : uri;
        int queryStart = fileKey.indexOf('?');
        return queryStart >= 0 ? fileKey.substring(0, queryStart) : fileKey;
    }
}
