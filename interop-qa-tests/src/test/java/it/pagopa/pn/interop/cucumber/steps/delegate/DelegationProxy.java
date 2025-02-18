package it.pagopa.pn.interop.cucumber.steps.delegate;

import it.pagopa.pn.interop.cucumber.steps.common.DelegationCommonContext;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

/** Proxy to manage the delegation-related data in the context. */
public interface DelegationProxy {
    UUID getDelegationId();
    void setDelegationId(UUID delegationId);

    /** Factory method to create a DelegationIdProxy for the main delegation.  */
    static DelegationProxy ofMainDelegation(DelegationCommonContext delegationContext) {
        return new DelegationProxyMainImpl(delegationContext);
    }

    /** Factory method to create a DelegationIdProxy for the auxiliary delegation.  */
    static DelegationProxy ofAuxDelegation(DelegationCommonContext delegationContext) {
        return new DelegationProxyAuxImpl(delegationContext);
    }

    @Data
    @AllArgsConstructor
    class DelegationProxyMainImpl implements DelegationProxy {

        private DelegationCommonContext delegationCommonContext;

        public UUID getDelegationId() {
            return delegationCommonContext.getDelegationId();
        }

        public void setDelegationId(UUID delegationId) {
            delegationCommonContext.setDelegationId(delegationId);
        }
    }


    @Data
    @AllArgsConstructor
    class DelegationProxyAuxImpl implements DelegationProxy {

        private DelegationCommonContext delegationCommonContext;

        public UUID getDelegationId() {
            return delegationCommonContext.getAuxDelegationId();
        }

        public void setDelegationId(UUID delegationId) {
            delegationCommonContext.setAuxDelegationId(delegationId);
        }
    }
}