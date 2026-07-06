package it.pagopa.pn.cucumber.steps.informalNotification.utils;


import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadRequest;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.externalb2bpainformal.model.InformalPreLoadResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.*;
import it.pagopa.pn.client.b2b.pa.polling.design.PnPollingFactory;
import it.pagopa.pn.client.b2b.pa.service.IPnPaB2bClient;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bExternalInformalClientImpl;
import it.pagopa.pn.cucumber.steps.pa.utilityVersions.B2bUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NotificationInformalUtilsV1 extends B2bUtils {

    private final PnPaB2bExternalInformalClientImpl externalInformalClient;

    @Autowired
    public NotificationInformalUtilsV1(ApplicationContext context, IPnPaB2bClient b2bClient, PnPaB2bExternalInformalClientImpl externalInformalClient, PnPollingFactory pollingFactory) {

        super(context, b2bClient, pollingFactory);

        this.externalInformalClient = externalInformalClient;
    }


    private Pair<String, String> preloadInformal(String resourceName, String contentType) throws IOException {

        String sha256 = computeSha256(context, resourceName);

        InformalPreLoadRequest request = new InformalPreLoadRequest().preloadIdx("0").contentType(contentType).sha256(sha256);

        InformalPreLoadResponse response = externalInformalClient.informalPresignedUploadRequest(List.of(request)).get(0);

        log.info("Informal preload resource={} sha256={} url={}", resourceName, sha256, response.getUrl());

        loadToPresigned(context, response.getUrl(), response.getSecret(), sha256, resourceName, contentType);

        return new Pair<>(response.getKey(), sha256);
    }

    // =========================================================
    // ENTRY POINT
    // =========================================================
    public InformalNotificationRequestV1 preloadAndPrepare(InformalNotificationRequestV1 request) throws IOException {

        preloadDocuments(request);
        preloadPayments(request);

        return request;
    }

    // =========================================================
    // DOCUMENTS
    // =========================================================
    private void preloadDocuments(InformalNotificationRequestV1 request) throws IOException {

        List<NotificationDocument> newDocs = new ArrayList<>();

        for (NotificationDocument doc : request.getDocuments()) {
            newDocs.add(preloadDocument(doc));
        }
        request.setDocuments(newDocs);
    }

    public NotificationDocument preloadDocument(NotificationDocument doc) throws IOException {

        Pair<String, String> preload = preloadInformal(doc.getRef().getKey(), "application/pdf");

        doc.getRef().setKey(preload.getValue1());
        doc.getRef().setVersionToken("v1");
        doc.setDigests(new NotificationAttachmentDigests().sha256(preload.getValue2()));

        return doc;
    }

    // =========================================================
    // PAYMENTS
    // =========================================================
    private void preloadPayments(InformalNotificationRequestV1 request) throws IOException {

        for (InformalNotificationRecipientV1 recipient : request.getRecipients()) {

            if (recipient.getPayments() == null) continue;

            for (InformalNotificationPaymentItem payment : recipient.getPayments()) {

                if (payment.getPagoPa() != null) {
                    payment.getPagoPa().setAttachment(preloadAttachment(payment.getPagoPa().getAttachment()));
                }
            }
        }
    }

    public NotificationPaymentAttachment preloadAttachment(NotificationPaymentAttachment attachment) throws IOException {

        if (attachment == null) return null;

        Pair<String, String> preload = preloadInformal(attachment.getRef().getKey(), "application/pdf");

        attachment.getRef().setKey(preload.getValue1());
        attachment.getRef().setVersionToken("v1");
        attachment.setDigests(new NotificationAttachmentDigests().sha256(preload.getValue2()));

        return attachment;
    }
}


