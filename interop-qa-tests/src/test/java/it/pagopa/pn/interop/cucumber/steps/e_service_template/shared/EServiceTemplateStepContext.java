package it.pagopa.pn.interop.cucumber.steps.e_service_template.shared;

import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import it.pagopa.pn.interop.cucumber.steps.DocumentMetadata;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateDocumentInfo;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.IterableUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Getter
@Setter
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServiceTemplateStepContext {
    private String producerName;
    private List<EServiceTemplateInfo> templatesManaged = new ArrayList<>();
    private EServiceTemplateDocumentInfo lastAddedDocument;
    private UpdateEServiceTemplateVersionSeed lastTemplateVersionUpdateSeed;
    private EServiceTemplateAttributesSeed lastTemplateVersionAttributesSeed;

    // TODO si somigliano troppo, sceglierne uno
    private UUID lastEServiceIdCreatedFromTemplate;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<CreatedResource> eServiceCreatedFromTemplates = new ArrayList<>();

    // TODO si somigliano troppo, sceglierne uno
    private CompactDescriptor lastEServiceDescriptorCreatedFromTemplate;
    private UUID lastEServiceDescriptorIdCreatedFromTemplate;

    private DescriptorAttributesSeed lastDescriptorAttributesSeed;
    private String lastEServiceNameCreatedFromTemplate;
    private String lastUsedEServiceTemplateNameSeed;

    private final EasyRandomParameters easyRandomParameters = new EasyRandomParameters()
        .seed(123L)
        .objectPoolSize(20)
        .randomizationDepth(5)
        .charset(StandardCharsets.UTF_8)
        .stringLengthRange(5, 30)
        .collectionSizeRange(1, 10)
        .scanClasspathForConcreteTypes(true)
        .overrideDefaultInitialization(true)
        .ignoreRandomizationErrors(false)
        .randomize(
            EServiceTemplateStepContext::isAnswersFieldInRiskAnalysisFormSeed,
            EServiceTemplateStepContext::randomAnswers);

    // TODO verificare che non si possano incapsulare in un unico oggetto
    private EServiceTemplateRiskAnalysisSeed lastAddedRiskAnalysis;
    private int lastAddedRiskAnalysisIndex = -1; // -1 means no risk analysis has been added yet
    private UUID lastAddedRiskAnalysisId;

    private List<DocumentMetadata> documentsMetadata;

    private int groupId; // id dell'ultimo gruppo di attributi creato
    private List<UUID> certifiedAttributesIds = new ArrayList<>();
    private List<UUID> declaredAttributesIds = new ArrayList<>();
    private List<UUID> verifiedAttributesIds = new ArrayList<>();

    private List<UUID> removedCertifiedAttributesIds = new ArrayList<>();
    private List<UUID> removedDeclaredAttributesIds = new ArrayList<>();
    private List<UUID> removedVerifiedAttributesIds = new ArrayList<>();

    private String modifiedTemplateName;

    private static boolean isAnswersFieldInRiskAnalysisFormSeed(Field field) {
        return field.getName().equals("answers") && field.getDeclaringClass().equals(
            RiskAnalysisFormSeed.class);
    }

    private static Map<String, List<String>> randomAnswers() {
        int mapCapacity = 10;
        EasyRandom easyRandom = new EasyRandom();
        Map<String, List<String>> map = new HashMap<>(mapCapacity);
        for (int i = 0; i < mapCapacity; i++) {
            map.put(
                easyRandom.nextObject(String.class),
                easyRandom.objects(String.class, 5).toList());
        }

        return map;
    }

    public CreatedResource getLastEServiceCreatedFromTemplate() {
        return lastOf(eServiceCreatedFromTemplates);
    }

    public void setLastEServiceCreatedFromTemplate(CreatedResource eServiceCreatedFromTemplate) {
        this.eServiceCreatedFromTemplates.add(eServiceCreatedFromTemplate);
    }

    public CreatedResource getEServiceCreatedFromTemplateWithIndex(int indexFromLast) {
        if (this.eServiceCreatedFromTemplates.size() < indexFromLast) return null;
        return this.eServiceCreatedFromTemplates.get(this.eServiceCreatedFromTemplates.size() - (indexFromLast + 1));
    }

    public int incrementLastAddedRiskAnalysisIndex() {
        return ++lastAddedRiskAnalysisIndex;
    }

    public EServiceTemplateInfo getLastTemplateManaged() {
        return lastOf(templatesManaged);
    }

    public void addTemplateManaged(EServiceTemplateInfo templateInfo) {
        this.templatesManaged.add(templateInfo);
    }

    public void addCertifiedAttributes(List<UUID> attributesIds) {
        this.certifiedAttributesIds.addAll(attributesIds);
    }

    public void addDeclaredAttributes(List<UUID> attributesIds) {
        this.declaredAttributesIds.addAll(attributesIds);
    }

    public void addVerifiedAttributes(List<UUID> attributesIds) {
        this.verifiedAttributesIds.addAll(attributesIds);
    }

    private <T> T lastOf(List<T> list) {
        return IterableUtils.isEmpty(list) ? null : list.get(list.size() - 1);
    }
}
