package it.pagopa.pn.cucumber.steps.templateEngine.data;

import lombok.Data;
import org.jsoup.Jsoup;
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

    /**
     * Estrae il testo effettivamente visibile dall'HTML (tag, stili e commenti esclusi,
     * entity HTML decodificate, spazi normalizzati) tramite un parser HTML, cosi' da poter
     * confrontare il contenuto di un template con il testo atteso indipendentemente dal markup.
     */
    public String retrieveVisibleText() {
        if (templateHtmlReturned == null) return "";
        return Jsoup.parse(templateHtmlReturned).body().text();
    }
    }
