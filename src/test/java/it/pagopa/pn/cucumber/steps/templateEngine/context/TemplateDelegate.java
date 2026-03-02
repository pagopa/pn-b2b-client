package it.pagopa.pn.cucumber.steps.templateEngine.context;

import lombok.Data;

@Data
public class TemplateDelegate {
    private String denomination;
    private String taxId;

    public TemplateDelegate denomination(String denomination) {

        this.denomination = denomination;
        return this;
    }

    public TemplateDelegate taxId(String taxId) {

        this.taxId = taxId;
        return this;
    }
}
