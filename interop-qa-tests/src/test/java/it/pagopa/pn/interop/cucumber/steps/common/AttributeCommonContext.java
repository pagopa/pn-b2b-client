package it.pagopa.pn.interop.cucumber.steps.common;

import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
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

    public Attribute getLastCreatedAttribute() {
        return lastOf(createdAttributes);
    }

    public void addCreatedAttribute(Attribute attribute) {
        this.attributeId = attribute.getId();
        this.createdAttributes.add(attribute);
    }

    public void addDeclaredAttribute(Attribute attribute) {
        this.attributeId = attribute.getId();
        this.createdAttributes.add(attribute);
    }

    public void addVerifiedAttribute(Attribute attribute) {
        this.attributeId = attribute.getId();
        this.createdAttributes.add(attribute);
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

    //--Helpers--
    public List<List<DescriptorAttributeSeed>> mapAttributes(List<List<DescriptorAttribute>> attributes) {
        if (attributes == null) {
            return new ArrayList<>();
        }
        List<List<DescriptorAttributeSeed>> seeds = new ArrayList<>();
        for (List<DescriptorAttribute> group : attributes) {
            List<DescriptorAttributeSeed> groupSeed = new ArrayList<>();
            for (DescriptorAttribute attr : group) {
                DescriptorAttributeSeed seed = new DescriptorAttributeSeed()
                        .id(attr.getId())
                        .explicitAttributeVerification(attr.getExplicitAttributeVerification());
                groupSeed.add(seed);
            }
            seeds.add(groupSeed);
        }
        return seeds;
    }
}
