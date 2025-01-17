package it.pagopa.interop.purpose.domain;

import it.pagopa.interop.purpose.exception.RiskAnalysisMapGenerationException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class RiskAnalysisDataFromJson {

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static class RiskAnalysisAttributes {
        private List<String> purpose;
        private List<String> institutionalPurpose;
        private List<String> usesPersonalData;
        private List<String> usesThirdPartyPersonalData;
        private List<String> personalDataTypes;
        private List<String> legalBasis;
        private List<String> knowsDataQuantity;
        private List<String> deliveryMethod;
        private List<String> policyProvided;
        private List<String> confirmPricipleIntegrityAndDiscretion;
        private List<String> doneDpia;
        private List<String> dataDownload;
        private List<String> purposePursuit;
        private List<String> checkedExistenceMereCorrectnessInteropCatalogue;
        private List<String> usesThirdPartyData;
        private List<String> declarationConfirmGDPR;

        public Map<String, List<String>> toMap() {
            Map<String, List<String>> map = new HashMap<>();
            for (Field field : this.getClass().getDeclaredFields()) {
                boolean originalAccessibility = field.canAccess(this);
                field.setAccessible(true);
                try {
                    List<String> value = (List<String>) field.get(this);
                    if (value != null) {
                        map.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RiskAnalysisMapGenerationException("Unable to access field: " + field.getName(), e);
                }
                field.setAccessible(originalAccessibility);
            }
            return map;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    public static class RiskAnalysisTemplate {
        private RiskAnalysisAttributes completed;
        private RiskAnalysisAttributes uncompleted;
    }

}
