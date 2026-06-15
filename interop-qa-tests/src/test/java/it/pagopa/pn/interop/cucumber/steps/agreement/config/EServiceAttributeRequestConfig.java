package it.pagopa.pn.interop.cucumber.steps.agreement.config;

import io.cucumber.java.DataTableType;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeCertifiedDiscreteComparator;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeKind;
import it.pagopa.pn.interop.cucumber.steps.agreement.model.EServiceAttributeSpec;
import java.util.Map;

public class EServiceAttributeRequestConfig {

    @DataTableType
    public EServiceAttributeSpec toEServiceAttributeSpec(Map<String, String> row) {

        String kindValue = row.get("kind");
        AttributeKind kind = kindValue != null ? AttributeKind.fromValue(kindValue) : null;

        String groupValue = row.get("group");
        Integer group = (groupValue != null && !groupValue.isBlank()) ? Integer.valueOf(groupValue) : null;

        String comparatorValue = row.get("comparator");
        AttributeCertifiedDiscreteComparator comparator = (comparatorValue != null && !comparatorValue.isBlank()) ? AttributeCertifiedDiscreteComparator.fromValue(comparatorValue) : null;

        Integer value = (row.get("value") != null && !row.get("value").isBlank()) ? Integer.valueOf(row.get("value")) : null;

        Integer dailyCallsPerConsumer = (row.get("dailyCallsPerConsumer") != null && !row.get("dailyCallsPerConsumer").isBlank()) ? Integer.valueOf(row.get("dailyCallsPerConsumer")) : null;

        return new EServiceAttributeSpec(kind, group, comparator, value, dailyCallsPerConsumer);
    }
}
