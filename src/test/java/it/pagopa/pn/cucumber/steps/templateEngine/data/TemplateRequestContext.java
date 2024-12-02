package it.pagopa.pn.cucumber.steps.templateEngine.data;

import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.Delegate;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.Delivery;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.Notification;
import it.pagopa.pn.client.b2b.generated.openapi.clients.templatesengine.model.Recipient;
import lombok.Data;

import java.util.List;

@Data
public class TemplateRequestContext {
    private String sendDate;
    private Notification notification;
    private String subject;
    private String iun;
    private String physicalAddressAndDenomination;
    private List<String> digests;
    private String endWorkflowDate;
    private String endWorkflowStatus;
    private String endWorkflowTime;
    private List<Delivery> deliveries;
    private String when;
    private Recipient recipient;
    private Delegate delegate;
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

}
