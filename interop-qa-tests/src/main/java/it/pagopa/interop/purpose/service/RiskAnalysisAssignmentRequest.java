package it.pagopa.interop.purpose.service;

import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisAssignmentSeed;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.UUID;

@Data
@Builder
public class RiskAnalysisAssignmentRequest {
    private RiskAnalysisReviewMode reviewMode;
    private List<UUID> reviewerIds;

    public RiskAnalysisAssignmentSeed toBFFRequest() {
        return new RiskAnalysisAssignmentSeed()
            .reviewMode(it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode.fromValue(reviewMode.getValue()))
            .reviewerIds(reviewerIds);
    }

    public it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RiskAnalysisAssignmentSeed toM2MRequest() {
        return new it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RiskAnalysisAssignmentSeed()
                .reviewMode(it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RiskAnalysisReviewMode.fromValue(reviewMode.getValue()))
                .reviewerIds(reviewerIds == null ? null : new LinkedHashSet<>(reviewerIds));
    }
}
