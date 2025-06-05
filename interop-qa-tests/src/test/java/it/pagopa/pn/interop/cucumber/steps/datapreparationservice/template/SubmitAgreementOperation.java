package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class SubmitAgreementOperation implements ICreateOperation<Object, UpperAgreementState>{
    private Supplier<Object> apiCaller;
    private Supplier<Object> checkerApiCaller;
    private Function<Object, UpperAgreementState> resultExtractor;
}
