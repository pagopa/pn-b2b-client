package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class AddConsumerDocumentOperation {
    @Data
    @AllArgsConstructor(staticName = "of")
    public static class AddConsumerDocumentParams {
        private UUID agreementId;
        private File doc;
    }

    private Function<AddConsumerDocumentParams, ResponseEntity<Void>> apiCaller;
    private Function<UUID, UpperAgreement> checkerApiCaller;
    private Function<Object, List<?>> documentListExtractor;
}
