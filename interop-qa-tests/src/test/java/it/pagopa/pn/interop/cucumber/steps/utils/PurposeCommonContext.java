package it.pagopa.pn.interop.cucumber.steps.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Getter
@Setter
@Component
public class PurposeCommonContext {
    private List<String> purposesIds = new ArrayList<>();
    private List<String> currentVersionIds = new ArrayList<>();
    private List<String> waitingForApprovalVersionIds = new ArrayList<>();
    private String purposeId;
    private String versionId;
    private String waitingForApprovalVersionId;

}
