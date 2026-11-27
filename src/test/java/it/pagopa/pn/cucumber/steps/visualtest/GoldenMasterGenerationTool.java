package it.pagopa.pn.cucumber.steps.visualtest;

import it.pagopa.pn.client.b2b.pa.service.impl.TemplateEngineClientImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Renders the golden masters of all documents (or of {@code -Dvrt.documents=KEY1,KEY2}) with the Template Engine
 * reachable at {@code -Dpn.delivery.base-url} into {@code target/vrt/generated-golden}. Promotion into
 * {@code src/test/resources} is manual, after a visual review.
 *
 * <pre>mvnw test -Dtest=GoldenMasterGenerationTool -Dvrt.generateGoldenMasters=true -Dpn.delivery.base-url=http://localhost:8886</pre>
 */
@EnabledIfSystemProperty(named = "vrt.generateGoldenMasters", matches = "true")
class GoldenMasterGenerationTool {

    @Test
    void renderGoldenMasters() throws Exception {
        RestTemplate restTemplate = new RestTemplate(List.of(new ByteArrayHttpMessageConverter(),
                new ResourceHttpMessageConverter(), new StringHttpMessageConverter(),
                new MappingJackson2HttpMessageConverter()));
        String baseUrl = System.getProperty("pn.delivery.base-url", "http://localhost:8886");
        TemplateEngineGoldenRenderer renderer =
                new TemplateEngineGoldenRenderer(new TemplateEngineClientImpl(restTemplate, baseUrl));

        String selected = System.getProperty("vrt.documents", "");
        List<VisualDocument> documents = selected.isBlank()
                ? List.of(VisualDocument.values())
                : Arrays.stream(selected.split(",")).map(String::trim).map(VisualDocument::of).toList();

        for (VisualDocument document : documents) {
            Path target = GoldenMasterProvider.GENERATED_GOLDEN_ROOT.resolve(document.folder()).resolve("expected.pdf");
            Files.createDirectories(target.getParent());
            Files.write(target, renderer.render(document));
            System.out.printf("%s -> %s%n", document.key(), target.toAbsolutePath());
        }
    }
}
