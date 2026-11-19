package it.pagopa.pn.client.b2b.pa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class TemplateEngineConfigBean {

    @Bean
    public TemplateEngineMessageConfigs templateEngineConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream yamlStream = new ClassPathResource("config/template-engine.yml").getInputStream()) {
            return mapper.readValue(yamlStream, TemplateEngineMessageConfigs.class);
        }
    }
}
