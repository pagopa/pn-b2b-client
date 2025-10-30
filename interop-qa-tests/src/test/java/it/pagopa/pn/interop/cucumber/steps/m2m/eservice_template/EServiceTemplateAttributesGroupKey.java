package it.pagopa.pn.interop.cucumber.steps.m2m.eservice_template;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EServiceTemplateAttributesGroupKey extends EServiceTemplateAttributesKey {
    protected int groupIndex;

    private EServiceTemplateAttributesGroupKey copyFrom(EServiceTemplateAttributesKey key) {
        this.templateId = key.getTemplateId();
        this.versionId = key.getVersionId();
        return this;
    }

    @Override
    public EServiceTemplateAttributesGroupKey withTemplateId(UUID templateId) {
        return copyFrom(super.withTemplateId(templateId));
    }

    @Override
    public EServiceTemplateAttributesGroupKey withVersionId(UUID versionId) {
        return copyFrom(super.withVersionId(versionId));
    }

    public EServiceTemplateAttributesGroupKey withGroupIndex(int groupIndex) {
        EServiceTemplateAttributesGroupKey out = copyFrom(this);
        out.groupIndex = groupIndex;
        return out;
    }
}
