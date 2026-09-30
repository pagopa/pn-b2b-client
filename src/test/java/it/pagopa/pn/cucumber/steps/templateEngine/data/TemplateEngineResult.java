package it.pagopa.pn.cucumber.steps.templateEngine.data;

import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class TemplateEngineResult {
    private Resource templateFileReturned;
    private String templateHtmlReturned;

    private String fileTextRetrieved;

    public TemplateEngineResult(Resource templateFileReturned) {
        this.templateFileReturned = templateFileReturned;
    }

    public TemplateEngineResult(String templateHtmlReturned) {
        this.templateHtmlReturned = templateHtmlReturned;
        this.fileTextRetrieved = templateHtmlReturned;
    }

    public String retrieveFormattedText() {
        if (fileTextRetrieved == null) return "";

        String text = fileTextRetrieved;

        text = text.replaceAll("<[^>]*>", " ");

        return text
                .replaceAll("\\r\\n", " ")
                .replaceAll("\\n", " ")
                .replaceAll("\\r", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
    }
