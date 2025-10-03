package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
public class CreateAgreementWithStateOperation {
    private CreateAndCheckAgreementOperation createAndCheckAgreementOperation;
    private AddConsumerDocumentOperation addConsumerDocumentOperation;
    private SubmitAgreementOperation submitAgreementOperation;
    private SuspendAgreementOperation suspendAgreementOperation;
    private ArchiveAgreementOperation archiveAgreementOperation;
}
