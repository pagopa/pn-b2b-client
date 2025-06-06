package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class CreateAgreementOperation {
    @Data
    @AllArgsConstructor(staticName = "of")
    public static class CreateAgreementParams {
        private UUID eServiceID;
        private UUID descriptorId;
        @Nullable private UUID delegationId;
    }

    private Function<CreateAgreementParams, UUID> apiCaller;
}
