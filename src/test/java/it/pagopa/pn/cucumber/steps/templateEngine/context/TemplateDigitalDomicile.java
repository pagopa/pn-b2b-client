package it.pagopa.pn.cucumber.steps.templateEngine.context;

import lombok.Data;

@Data
public class TemplateDigitalDomicile {
    private String address;

    public TemplateDigitalDomicile address(String address) {

        this.address = address;
        return this;
    }
}
