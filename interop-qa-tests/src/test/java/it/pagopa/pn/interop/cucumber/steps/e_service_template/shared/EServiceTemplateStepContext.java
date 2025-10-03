package it.pagopa.pn.interop.cucumber.steps.e_service_template.shared;

import it.pagopa.interop.generated.openapi.clients.bff.model.CompactDescriptor;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateAttributesSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateDocumentInfo;
import it.pagopa.pn.interop.cucumber.steps.common.EServiceTemplateInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.IterableUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Getter
@Setter
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EServiceTemplateStepContext {
    @Mapper(componentModel = "spring")
    public interface EServiceTemplateInfoMapper {
        /* TODO 07/03/2025 overhead, se questo mapper continua a servire solo a questo bisognerebbe
         *  semplicemente mutare EServiceTemplateInfo in un pojo e ricorrere ai metodi set per modificarlo   */
        @Mapping(source = "newVersionId", target = "lastVersionId")
        EServiceTemplateInfo withVersionId(EServiceTemplateInfo templateInfo, UUID newVersionId);
    }

    private List<EServiceTemplateInfo> templatesManaged = new ArrayList<>();
    private EServiceTemplateDocumentInfo lastAddedDocument;
    private UpdateEServiceTemplateVersionSeed lastTemplateVersionUpdateSeed;
    private EServiceTemplateAttributesSeed lastTemplateVersionAttributesSeed;

    // TODO si somigliano troppo, sceglierne uno
    private UUID lastEServiceIdCreatedFromTemplate;
    private CreatedResource lastEServiceCreatedFromTemplate;

    // TODO si somigliano troppo, sceglierne uno
    private CompactDescriptor lastEServiceDescriptorCreatedFromTemplate;
    private UUID lastEServiceDescriptorIdCreatedFromTemplate;

    private DescriptorAttributesSeed lastDescriptorAttributesSeed;
    private String lastEServiceNameCreatedFromTemplate;

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

    public int incrementLastAddedRiskAnalysisIndex() {
        return ++lastAddedRiskAnalysisIndex;
    }

    public EServiceTemplateInfo getLastTemplateManaged() {
        return lastOf(templatesManaged);
    }

    public void addTemplateManaged(EServiceTemplateInfo templateInfo) {
        this.templatesManaged.add(templateInfo);
    }

    private <T> T lastOf(List<T> list) {
        return IterableUtils.isEmpty(list) ? null : list.get(list.size() - 1);
    }
}
