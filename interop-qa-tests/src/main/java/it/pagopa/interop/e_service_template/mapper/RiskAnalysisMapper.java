package it.pagopa.interop.e_service_template.mapper;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind;
import it.pagopa.interop.purpose.domain.RiskAnalysis;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RiskAnalysisMapper {
    EServiceTemplateRiskAnalysisSeed mapToSeed(RiskAnalysis riskAnalysis, TenantKind tenantKind);
}
