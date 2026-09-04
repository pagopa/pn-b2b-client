package it.pagopa.pn.interop.cucumber.steps.agreement.config;

import io.cucumber.java.DataTableType;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeCertifiedDiscreteComparator;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.steps.agreement.model.EServiceAttributeSpec;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class EServiceAttributeRequestConfig {

    private final SharedStepsContext sharedStepsContext;

    @DataTableType
    public EServiceAttributeSpec toEServiceAttributeSpec(Map<String, String> row) {

        String kindValue = row.get("kind");
        AttributeKind kind = kindValue != null ? AttributeKind.fromValue(kindValue) : null;

        String codeValue = row.get("code");
        String code = (codeValue != null && !codeValue.isBlank()) ? codeValue : null;

        String groupValue = row.get("group");
        Integer group = (groupValue != null && !groupValue.isBlank()) ? Integer.valueOf(groupValue) : null;

        String comparatorValue = row.get("comparator");
        AttributeCertifiedDiscreteComparator comparator = (comparatorValue != null && !comparatorValue.isBlank()) ? AttributeCertifiedDiscreteComparator.fromValue(comparatorValue) : null;

        Integer value = null;

        if (row.get("value") != null && row.get("value").startsWith("$ATTR_CERT_DISCR_THRESHOLD")) {
            // Example formula: $ATTR_CERT_DISCR_THRESHOLD(PA1,-100)
            String formula = row.get("value");
            String param1 = formula.substring(formula.indexOf("(") + 1, formula.indexOf(","));
            Integer param2 = Integer.parseInt(formula.substring(formula.indexOf(",") + 1, formula.indexOf(")")));

            if (!sharedStepsContext.getAttributeCommonContext().getOwnerCertifiedDiscreteAttribute().equals(param1)) {
                throw new IllegalArgumentException("The attribute name in the formula does not match the attribute name in the context");
            }

            Integer discreteValue = sharedStepsContext.getAttributeCommonContext().getOwnedCertifiedDiscreteAttributes().get(0).getDiscreteValue();
            value = discreteValue + param2;
        } else {
            value = (row.get("value") != null && !row.get("value").isBlank()) ? Integer.valueOf(row.get("value")) : null;
        }

        Integer dailyCallsPerConsumer = (row.get("dailyCallsPerConsumer") != null && !row.get("dailyCallsPerConsumer").isBlank()) ? Integer.valueOf(row.get("dailyCallsPerConsumer")) : null;

        return new EServiceAttributeSpec(kind, group, code, comparator, value, dailyCallsPerConsumer);
    }
}
