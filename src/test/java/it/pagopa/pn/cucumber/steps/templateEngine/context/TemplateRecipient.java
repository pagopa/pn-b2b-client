package it.pagopa.pn.cucumber.steps.templateEngine.context;

import lombok.Data;

@Data
public class TemplateRecipient {
    private String denomination;
    private String taxId;
    private String recipientType;
    private TemplateDigitalDomicile digitalDomicile;
    private String physicalAddressAndDenomination;

    public TemplateRecipient denomination(String denomination) {

        this.denomination = denomination;
        return this;
    }

    public TemplateRecipient taxId(String taxId) {

        this.taxId = taxId;
        return this;
    }

    public TemplateRecipient recipientType(String recipientType) {

        this.recipientType = recipientType;
        return this;
    }

    public TemplateRecipient digitalDomicile(TemplateDigitalDomicile digitalDomicile) {

        this.digitalDomicile = digitalDomicile;
        return this;
    }

    public TemplateRecipient physicalAddressAndDenomination(String physicalAddressAndDenomination) {

        this.physicalAddressAndDenomination = physicalAddressAndDenomination;
        return this;
    }
}
