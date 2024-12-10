package it.pagopa.pn.interop.cucumber.steps.purpose.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PurposeCommonContext {
    private List<String> purposesIds = new ArrayList<>();
    private List<String> currentVersionIds = new ArrayList<>();
    private List<String> waitingForApprovalVersionIds = new ArrayList<>();
    private String purposeId;
    private String versionId;
    private String waitingForApprovalVersionId;

}
