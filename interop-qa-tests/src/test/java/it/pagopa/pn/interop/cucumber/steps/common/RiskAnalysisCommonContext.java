package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Getter
@Setter
@Component
public class RiskAnalysisCommonContext {
    UUID riskAnalysisId;
    Integer dailyCalls = 1;
    List<AssignedReviewerActorRef> assignedReviewerActors = new ArrayList<>();
    RiskAnalysisFormSeed riskAnalysisVariation;

    public record AssignedReviewerActorRef(String tenantType, String role, int index) {
    }
}
