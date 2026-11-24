package it.pagopa.pn.interop.cucumber.steps.e_service_template.instance;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.DataTableType;
import it.pagopa.interop.generated.openapi.clients.bff.model.TemplateInstanceInterfaceRESTSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TemplateInstanceInterfaceSOAPSeed;
import it.pagopa.pn.interop.cucumber.utility.StepParser;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EServiceTemplateInstanceInterfaceRequestConfig {

    @DataTableType
    public TemplateInstanceInterfaceRESTSeed toTemplateInstanceInterfaceRESTSeed(DataTable dataTable) {
        return populateSeedFromDataTable(new TemplateInstanceInterfaceRESTSeed(), toFieldValueMap(dataTable));
    }

    @DataTableType
    public TemplateInstanceInterfaceSOAPSeed toTemplateInstanceInterfaceSOAPSeed(DataTable dataTable) {
        return populateSeedFromDataTable(new TemplateInstanceInterfaceSOAPSeed(), toFieldValueMap(dataTable));
    }

    private Map<String, String> toFieldValueMap(DataTable dataTable) {
        List<List<String>> rows = dataTable.cells();
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> mapped = new LinkedHashMap<>();
        for (List<String> row : rows) {
            if (row == null || row.size() < 2) {
                continue;
            }

            String key = row.get(0) == null ? "" : row.get(0).trim();
            if (key.isEmpty() || "field".equalsIgnoreCase(key)) {
                continue;
            }

            mapped.put(key, row.get(1));
        }

        return mapped;
    }

    private <T> T populateSeedFromDataTable(T seed, Map<String, String> data) {
        BeanWrapper wrapper = new BeanWrapperImpl(seed);
        wrapper.setAutoGrowNestedPaths(true);
        wrapper.setConversionService(new DefaultConversionService());

        for (Map.Entry<String, String> entry : data.entrySet()) {
            try {
                wrapper.setPropertyValue(entry.getKey(), StepParser.nullOrBlankOrValue(entry.getValue()));
            } catch (BeansException ex) {
                throw new IllegalArgumentException("Campo non valido o valore non convertibile: " + entry.getKey(), ex);
            }
        }

        return seed;
    }
}

