package it.pagopa.pn.cucumber.steps.templateEngine.context;

import lombok.Data;

import java.util.List;

@Data
public class TemplateNotification {
    private String iun;
    private String subject;
    private TemplateSender sender;
    private List<TemplateRecipient> recipients;

    public TemplateNotification iun(String iun) {

        this.iun = iun;
        return this;
    }

    public TemplateNotification subject(String subject) {

        this.subject = subject;
        return this;
    }

    public TemplateNotification sender(TemplateSender sender) {

        this.sender = sender;
        return this;
    }

    public TemplateNotification recipients(List<TemplateRecipient> recipients) {

        this.recipients = recipients;
        return this;
    }
}
