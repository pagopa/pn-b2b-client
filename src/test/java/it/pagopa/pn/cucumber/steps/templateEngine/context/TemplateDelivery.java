package it.pagopa.pn.cucumber.steps.templateEngine.context;

import lombok.Data;

@Data
public class TemplateDelivery {
    private String denomination;
    private String taxId;
    private String address;
    private String type;
    private String addressSource;
    private String responseDate;
    private Boolean ok;

    public TemplateDelivery denomination(String denomination) {

        this.denomination = denomination;
        return this;
    }

    public TemplateDelivery taxId(String taxId) {

        this.taxId = taxId;
        return this;
    }

    public TemplateDelivery address(String address) {

        this.address = address;
        return this;
    }

    public TemplateDelivery type(String type) {

        this.type = type;
        return this;
    }

    public TemplateDelivery addressSource(String addressSource) {

        this.addressSource = addressSource;
        return this;
    }

    public TemplateDelivery responseDate(String responseDate) {

        this.responseDate = responseDate;
        return this;
    }

    public TemplateDelivery ok(Boolean ok) {

        this.ok = ok;
        return this;
    }

}
