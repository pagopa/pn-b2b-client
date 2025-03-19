package it.pagopa.interop.e_service_template.mapper;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RiskAnalysisMapper {
    EServiceRiskAnalysisSeed mapToSeed(RiskAnalysis riskAnalysis);
}
