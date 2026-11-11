package it.pagopa.pn.cucumber.steps.templateEngine.escapemod;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class HtmlEscapeTarget {
    private final String endpoint;
    private final String templateType;
    private final String operationId;
    private final String format;
    private final Map<String, String> fieldMappings;
}