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

    public String getTenantBy(DelegationRole role) {
        return role == DelegationRole.DELEGATING ? delegatorTenant : delegateTenant;
    }

    public UUID getIdBy(DelegationRole role) {
        return role == DelegationRole.DELEGATING ? delegatorId : delegateId;
    }
}
