package it.pagopa.pn.client.b2b.pa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class TemplateEngineConfigBean {

    @Bean
    public TemplateEngineMessageConfigs templateEngineConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File yamlFile = new File("config/template-engine.yml");
        return mapper.readValue(yamlFile, TemplateEngineMessageConfigs.class);
    }
}
