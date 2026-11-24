package it.pagopa.interop.maintenance;

import it.pagopa.interop.generated.openapi.clients.tenant_process.model.MaintenanceTenantUpdatePayload;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.Tenant;
import it.pagopa.interop.generated.openapi.clients.tenant_process.model.TenantKind;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    @Mapping(source = "tenant", target = "tenant")
    @Mapping(source = "currentVersion", target = "currentVersion")
    @Mapping(source = "tenantKind", target = "tenant.kind")
    @Mapping(source = "selfcareInstitutionType", target = "tenant.selfcareInstitutionType")
    MaintenanceTenantUpdatePayload mapWith(
            Tenant tenant,
            int currentVersion,
            TenantKind tenantKind,
            String selfcareInstitutionType);

    @Mapping(source = "tenant", target = "tenant")
    @Mapping(source = "currentVersion", target = "currentVersion")
    @Mapping(source = "selfcareInstitutionType", target = "tenant.selfcareInstitutionType")
    MaintenanceTenantUpdatePayload mapWith(
            Tenant tenant,
            int currentVersion,
            String selfcareInstitutionType);
}