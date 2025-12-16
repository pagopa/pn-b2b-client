package it.pagopa.pn.interop.cucumber.utility.model;

import lombok.Value;

import java.util.Set;

@Value
public class PdfWordMatchResult {
    Set<String> found;
    Set<String> missing;

    public boolean allFound() {
        return missing.isEmpty();
    }
}