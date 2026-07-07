package it.pagopa.interop.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@PropertySource(value = "classpath:upload-files.properties")
@ConfigurationProperties(prefix = "upload")
public class UploadDocumentFilesProperties {
    private Map<String, String> files = new HashMap<>();
}


