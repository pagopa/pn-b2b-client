package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.DeclaredAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.VerifiedAttribute;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AttributeCommonContext {
    UUID attributeId;
    List<List<UUID>> requiredCertifiedAttributes = new ArrayList<>();
    List<List<UUID>> requiredDeclaredAttributes = new ArrayList<>();
    List<List<UUID>> requiredVerifiedAttributes = new ArrayList<>();
    String attributeConsumerTenant;

    List<Attribute> createdAttributes = new ArrayList<>();
    List<Attribute> declaredAttributes = new ArrayList<>();

    public Attribute getLastCreatedAttribute() {
        return lastOf(createdAttributes);
    }

    public void addCreatedAttribute(Attribute attribute) {
        this.attributeId = attribute.getId();
        this.createdAttributes.add(attribute);
    }

    public void addDeclaredAttribute(Attribute attribute) {
        this.attributeId = attribute.getId();
        this.declaredAttributes.add(attribute);
    }


    //--M2M--
    List<CertifiedAttribute> certifiedPublished = new ArrayList<>();
    List<CertifiedAttribute> certifiedActual = new ArrayList<>();

    List<DeclaredAttribute> declaredPublished = new ArrayList<>();
    List<DeclaredAttribute> declaredActual = new ArrayList<>();

    List<VerifiedAttribute> verifiedPublished = new ArrayList<>();
    List<VerifiedAttribute> verifiedActual = new ArrayList<>();

    private <T> T lastOf(List<T> list) {
        return IterableUtils.isEmpty(list) ? null : list.get(list.size() - 1);
    }
}
