package it.pagopa.pn.interop.cucumber.steps.delegate;

import it.pagopa.pn.interop.cucumber.steps.common.DelegationCommonContext;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Proxy to manage the delegation-related data in the context. */
public interface DelegationProxy {
    UUID getDelegationId();
    void setDelegationId(UUID delegationId);

    UUID getDelegatorId();
    void setDelegatorId(UUID delegatorId);

    UUID getDelegateId();
    void setDelegateId(UUID delegatorId);

    /** Factory method to create a DelegationIdProxy for the main delegation.  */
    static DelegationProxy ofMainDelegation(DelegationCommonContext delegationContext) {
        return new DelegationProxyMainImpl(delegationContext);
    }

    /** Factory method to create a DelegationIdProxy for the auxiliary delegation.  */
    static DelegationProxy ofAuxDelegation(DelegationCommonContext delegationContext) {
        return new DelegationProxyAuxImpl(delegationContext);
    }

    @Data
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    abstract class AbstractDelegationProxy implements DelegationProxy {
        protected DelegationCommonContext delegationCommonContext;

        public UUID getDelegatorId() {
            return this.delegationCommonContext.getDelegatorId();
        }

        public void setDelegatorId(UUID delegatorId) {
            this.delegationCommonContext.setDelegatorId(delegatorId);
        }

        public UUID getDelegateId() {
            return this.delegationCommonContext.getDelegateId();
        }

        public void setDelegateId(UUID delegatorId) {
            this.delegationCommonContext.setDelegateId(delegatorId);
        }
    }

    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    class DelegationProxyMainImpl extends AbstractDelegationProxy {
        public DelegationProxyMainImpl(DelegationCommonContext delegationCommonContext) {
            super(delegationCommonContext);
        }

        public UUID getDelegationId() {
            return delegationCommonContext.getDelegationId();
        }

        public void setDelegationId(UUID delegationId) {
            delegationCommonContext.setDelegationId(delegationId);
        }
    }


    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    class DelegationProxyAuxImpl extends AbstractDelegationProxy {
        public DelegationProxyAuxImpl(DelegationCommonContext delegationCommonContext) {
            super(delegationCommonContext);
        }

        public UUID getDelegationId() {
            return delegationCommonContext.getAuxDelegationId();
        }

        public void setDelegationId(UUID delegationId) {
            delegationCommonContext.setAuxDelegationId(delegationId);
        }
    }
}