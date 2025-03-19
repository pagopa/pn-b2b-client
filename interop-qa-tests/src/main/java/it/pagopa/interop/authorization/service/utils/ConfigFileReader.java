package it.pagopa.interop.authorization.service.utils;

import it.pagopa.interop.authorization.domain.Tenant;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class ConfigFileReader {
    private final List<Tenant> tenantList;

    public ConfigFileReader() {
        this.tenantList = readProperty();
    }

    private List<Tenant> readProperty() {
        String filePath = "config/tenants-ids.yaml";
        List<Tenant> tenantList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Yaml yaml = new Yaml(new Constructor(Tenant.class));
            yaml.loadAll(reader).forEach(i -> tenantList.add((Tenant) i));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return tenantList;
    }

}
