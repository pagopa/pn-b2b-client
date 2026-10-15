package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Getter
@Setter
@Component
public class RiskAnalysisCommonContext {
    UUID riskAnalysisId;
    Integer dailyCalls = 1;
    List<AssignedReviewerActorRef> assignedReviewerActors = new ArrayList<>();
    List<AssignedReviewerActorRef> initialReviewerActors;
    RiskAnalysisFormSeed riskAnalysisVariation;
    RiskAnalysisSnapshot initialRiskAnalysis;

    public void rememberInitialReviewers() {
        initialReviewerActors = List.copyOf(assignedReviewerActors);
    }

    public void rememberRiskAnalysis(UUID purposeId, RiskAnalysisForm form) {
        Map<String, List<String>> answers = new LinkedHashMap<>();
        form.getAnswers().forEach((key, values) -> answers.put(key, values == null ? null
                : Collections.unmodifiableList(new ArrayList<>(values))));
        initialRiskAnalysis = new RiskAnalysisSnapshot(purposeId, form.getVersion(),
                Collections.unmodifiableMap(answers));
    }

    public record RiskAnalysisSnapshot(UUID purposeId, String version, Map<String, List<String>> answers) {
    }

    public record AssignedReviewerActorRef(String tenantType, String role, int index) {
    }
}
