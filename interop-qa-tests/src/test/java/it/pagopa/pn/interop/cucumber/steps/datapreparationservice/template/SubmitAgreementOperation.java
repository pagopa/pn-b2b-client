package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class SubmitAgreementOperation {
    private Function<UUID, UpperAgreement> apiCaller;
    private Function<UUID, UpperAgreement> checkerApiCaller;
}
