package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class ArchiveAgreementOperation {
    private Consumer<UUID> apiCaller;
    private Function<UUID, UpperAgreement> checkerApiCaller;
}
