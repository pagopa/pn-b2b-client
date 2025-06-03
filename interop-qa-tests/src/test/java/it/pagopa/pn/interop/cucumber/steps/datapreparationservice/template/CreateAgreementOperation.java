package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class CreateAgreementOperation {
    private Supplier<Object> apiCaller;
    private Function<Object, UUID> resultExtractor;
}
