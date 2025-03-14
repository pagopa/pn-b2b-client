package it.pagopa.pn.interop.cucumber.steps.e_service_template.shared;

import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceTemplateVersionSeed;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Data
public class EServiceTemplateStepContext {
    @Mapper(componentModel = "spring")
    public interface EServiceTemplateInfoMapper {
        /* TODO 07/03/2025 overhead, se questo mapper continua a servire solo a questo bisognerebbe
         * semplicemente mutare EServiceTemplateInfo in un pojo e ricorrere ai metodi set per modificarlo   */
        @Mapping(source = "newVersionId", target = "lastVersionId")
        EServiceTemplateInfo withVersionId(EServiceTemplateInfo templateInfo, UUID newVersionId);
    }

    /* TODO 13/03/2025 i record non si stanno prestando bene come previsto, convertirli in classi
     *  POJO con Lombok e collocarle all'esterno, in un package dedicato al context in cui
     *  spostare anche questa classe */
    /** Stores data on an e-service template useful for testing */
    public record EServiceTemplateInfo(String name, String intendedTarget, String eServiceDescription, UUID id, UUID lastVersionId){}

    /** Stores data on an e-service template document useful for testing */
    public record EServiceTemplateDocumentInfo(UUID id, String prettyName, byte[] body){}

    private EServiceTemplateInfo lastTemplateManaged;
    private EServiceTemplateDocumentInfo lastAddedDocument;
    private UpdateEServiceTemplateVersionSeed lastTemplateVersionUpdateSeed;


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
    private EServiceRiskAnalysisSeed lastAddedRiskAnalysis;
    private int lastAddedRiskAnalysisIndex = -1; // -1 means no risk analysis has been added yet

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
}
