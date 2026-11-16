package it.pagopa.pn.cucumber.steps.pa.service;

import it.pagopa.pn.client.b2b.pa.service.IPnSafeStoragePrivateClient;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.AdditionalFileTagsGetResponse;
import it.pagopa.pn.client.web.generated.openapi.clients.safeStorage.model.AdditionalFileTagsUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lettura e modifica dei tag associati a un documento su SafeStorage.
 */
@Service
@RequiredArgsConstructor
public class DocumentTagsService {

    private final IPnSafeStoragePrivateClient safeStoragePrivateClient;

    public Map<String, List<String>> getTags(String fileKey) {
        return Optional.ofNullable(safeStoragePrivateClient.additionalFileTagsGet(fileKey))
                .map(AdditionalFileTagsGetResponse::getTags)
                .orElse(Map.of());
    }

    public void addTagValue(String fileKey, String tag, String value) {
        AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
        request.putSETItem(tag, List.of(value));
        safeStoragePrivateClient.additionalFileTagsUpdate(fileKey, request);
    }

    public void removeTagValue(String fileKey, String tag, String value) {
        AdditionalFileTagsUpdateRequest request = new AdditionalFileTagsUpdateRequest();
        request.putDELETEItem(tag, List.of(value));
        safeStoragePrivateClient.additionalFileTagsUpdate(fileKey, request);
    }
}
