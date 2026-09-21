package it.pagopa.pn.interop.cucumber.utility;

import org.springframework.core.io.Resource;

import java.io.IOException;

public final class ResourceUtils {

    private ResourceUtils() {
    }

    public static String extractUploadPath(Resource resource) {
        try {
            return resource.getFile().toPath().toAbsolutePath().normalize().toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to resolve uploaded document path", e);
        }
    }
}
