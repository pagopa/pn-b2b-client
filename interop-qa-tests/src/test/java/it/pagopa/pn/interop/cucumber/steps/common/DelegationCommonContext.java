package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.pn.interop.cucumber.steps.delegate.DelegationRole;
import java.util.UUID;
import lombok.Data;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DelegationCommonContext {
    private UUID delegationId;
    private UUID delegatorId;
    private UUID delegateId;

    private String delegatorTenant;
    private String delegateTenant;

    /* 06/02/2025 useful in those test cases where it is necessary to have a third-party
     * delegation ID in addition to the one between delegate and delegator */
    private UUID auxDelegationId;

    public String getTenantBy(DelegationRole role) {
        return role == DelegationRole.DELEGATING ? delegatorTenant : delegateTenant;
    }

    public UUID getIdBy(DelegationRole role) {
        return role == DelegationRole.DELEGATING ? delegatorId : delegateId;
    }
}
