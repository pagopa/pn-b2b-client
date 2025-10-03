package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.CertifiedAttribute;
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

    //--M2M--
    List<CertifiedAttribute> published = new ArrayList<>();
    List<CertifiedAttribute> actual = new ArrayList<>();

    List<Attribute> createdAttributes = new ArrayList<>();

    public Attribute getLastCreatedAttribute() {
        return lastOf(createdAttributes);
    }

    public void addCreatedAttribute(Attribute attribute) {
        this.attributeId = attribute.getId();
        this.createdAttributes.add(attribute);
    }

    private <T> T lastOf(List<T> list) {
        return IterableUtils.isEmpty(list) ? null : list.get(list.size() - 1);
    }
}
