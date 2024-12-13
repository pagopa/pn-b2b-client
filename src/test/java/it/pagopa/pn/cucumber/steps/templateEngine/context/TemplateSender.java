package it.pagopa.pn.cucumber.steps.templateEngine.context;

import lombok.Data;

@Data
public class TemplateSender {
    private String paDenomination;
    private String paTaxId;

    public TemplateSender paDenomination(String paDenomination) {

        this.paDenomination = paDenomination;
        return this;
    }

    public TemplateSender paTaxId(String paTaxId) {

        this.paTaxId = paTaxId;
        return this;
    }

}
