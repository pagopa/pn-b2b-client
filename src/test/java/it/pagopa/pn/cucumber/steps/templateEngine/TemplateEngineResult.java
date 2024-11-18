package it.pagopa.pn.cucumber.steps.templateEngine;

import lombok.Data;

import java.io.File;

@Data
public class TemplateEngineResult {
    private File templateFileReturned;
    private String templateHtmlReturned;

    public TemplateEngineResult(File templateFileReturned) {
        this.templateFileReturned = templateFileReturned;
    }

    public TemplateEngineResult(String templateHtmlReturned) {
        this.templateHtmlReturned = templateHtmlReturned;
    }
}
