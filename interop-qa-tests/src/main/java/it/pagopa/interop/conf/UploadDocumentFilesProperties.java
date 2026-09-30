package it.pagopa.interop.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
@Setter
@Component
@PropertySource(value = "classpath:upload-files.properties")
@ConfigurationProperties(prefix = "upload")
public class UploadDocumentFilesProperties {
    private Map<String, String> files = new HashMap<>();

    public Map<String, Resource> asNormalizedResourceMap() {
        Map<String, Resource> resources = new LinkedHashMap<>();
        files.forEach((fileType, path) -> resources.put(normalize(fileType), new ClassPathResource(path)));
        return Collections.unmodifiableMap(resources);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}


