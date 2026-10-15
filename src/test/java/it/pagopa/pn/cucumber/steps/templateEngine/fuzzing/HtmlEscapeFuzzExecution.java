package it.pagopa.pn.cucumber.steps.templateEngine.fuzzing;

import it.pagopa.pn.cucumber.steps.templateEngine.data.TemplateEngineResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class HtmlEscapeFuzzExecution {

    private final String endpoint;
    private final String format;
    private final Map<String, String> rawValues;
    private final TemplateEngineResult result;
}