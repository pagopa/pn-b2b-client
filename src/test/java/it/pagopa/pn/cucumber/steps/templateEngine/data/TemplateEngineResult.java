package it.pagopa.pn.cucumber.steps.templateEngine.data;

import lombok.Data;
import org.springframework.core.io.Resource;

import java.io.File;

@Data
public class TemplateEngineResult {
    private Resource templateFileReturned;
    private String templateHtmlReturned;

    public TemplateEngineResult(Resource templateFileReturned) {
        this.templateFileReturned = templateFileReturned;
    }

    public TemplateEngineResult(String templateHtmlReturned) {
        this.templateHtmlReturned = templateHtmlReturned;
    }
}
