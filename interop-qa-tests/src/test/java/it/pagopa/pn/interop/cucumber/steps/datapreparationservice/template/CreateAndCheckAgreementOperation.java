package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import java.util.UUID;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class CreateAndCheckAgreementOperation {
    private CreateAgreementOperation createOperation;
    private Function<UUID, Object> checkerApiCaller;
}
