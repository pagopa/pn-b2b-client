package it.pagopa.pn.cucumber.steps.templateEngine.data;

import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateDelegate;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateDelivery;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateNotification;
import it.pagopa.pn.cucumber.steps.templateEngine.context.TemplateRecipient;
import lombok.Data;

import java.util.List;

@Data
public class TemplateRequestContext {
    private String sendDate;
    private TemplateNotification notification;
    private String subject;
    private String iun;
    private String physicalAddressAndDenomination;
    private List<String> digests;
    private String endWorkflowDate;
    private String endWorkflowStatus;
    private String endWorkflowTime;
    private List<TemplateDelivery> deliveries;
    private String when;
    private TemplateRecipient recipient;
    private TemplateDelegate delegate;
    private String startDate;
    private String timeReferenceStartDate;
    private String endDate;
    private String timeReferenceEndDate;
    private String notificationCancelledDate;
    private String qrCodeQuickAccessLink;
    private String piattaformaNotificheURL;
    private String piattaformaNotificheURLLabel;
    private String perfezionamentoURL;
    private String perfezionamentoURLLabel;
    private String sendURL;
    private String sendURLLAbel;
    private String raddPhoneNumber;
    private String pnFaqSendURL;
    private String sendLogoLink;
    private String logoBase64;
    private String recipientType;
    private String verificationCode;
    private String recipients;
}
