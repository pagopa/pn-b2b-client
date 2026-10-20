package it.pagopa.pn.cucumber.steps.informalNotification.adapter;

import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.InformalNotificationRequestV1;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NewInformalNotificationResponse;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internalb2bpainformal.model.NotificationAttachmentDigests;
import it.pagopa.pn.client.b2b.pa.service.impl.PnPaB2bInternalInformalClientImpl;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalCommunication;
import it.pagopa.pn.cucumber.steps.informalNotification.domain.model.InformalDocument;
import it.pagopa.pn.cucumber.steps.informalNotification.utils.NotificationInformalUtilsV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InformalCommunicationV1Adapter implements InformalCommunicationAdapter {

    private final InformalCommunicationV1Mapper mapper;
    private final NotificationInformalUtilsV1 notificationInformalUtilsV1;
    private final PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl;

    @Autowired
    public InformalCommunicationV1Adapter(NotificationInformalUtilsV1 notificationInformalUtilsV1,
                                          PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl) {
        this(InformalCommunicationV1Mapper.INSTANCE, notificationInformalUtilsV1, pnPaB2bInternalInformalClientImpl);
    }

    public InformalCommunicationV1Adapter(InformalCommunicationV1Mapper mapper,
                                          NotificationInformalUtilsV1 notificationInformalUtilsV1,
                                          PnPaB2bInternalInformalClientImpl pnPaB2bInternalInformalClientImpl) {
        this.mapper = mapper != null ? mapper : InformalCommunicationV1Mapper.INSTANCE;
        this.notificationInformalUtilsV1 = notificationInformalUtilsV1;
        this.pnPaB2bInternalInformalClientImpl = pnPaB2bInternalInformalClientImpl;
    }

    @Override
    public InformalSendResult sendInformalNotification(String cxId, String paName, InformalCommunication domainModel) throws IOException {
        InformalNotificationRequestV1 requestV1 = mapper.toV1(domainModel);

        // 1. Identifica chiavi non valide impostate dai test (es. per testare la validazione dell'API)
        // e imposta temporaneamente una risorsa valida di classpath per far completare con successo il preload S3
        Map<Integer, String> invalidKeyOverrides = new HashMap<>();
        if (requestV1.getDocuments() != null) {
            for (int i = 0; i < requestV1.getDocuments().size(); i++) {
                var doc = requestV1.getDocuments().get(i);
                if (doc.getRef() != null && doc.getRef().getKey() != null) {
                    String key = doc.getRef().getKey();
                    if (key.startsWith("PN_NOTIFICATION_ATTACHMENT") || !key.startsWith("classpath:")) {
                        invalidKeyOverrides.put(i, key);
                        doc.getRef().setKey("classpath:/sample_1pg.pdf");
                    }
                }
            }
        }

        // 2. Preload documents and payments for v1
        requestV1 = notificationInformalUtilsV1.preloadAndPrepare(requestV1, paName);

        // 3. Ripristina le chiavi non valide dopo il preload
        invalidKeyOverrides.forEach((idx, overrideKey) -> {
            if (idx < requestV1.getDocuments().size() && requestV1.getDocuments().get(idx).getRef() != null) {
                requestV1.getDocuments().get(idx).getRef().setKey(overrideKey);
            }
        });

        // 4. Riapplica eventuali altri override di test (contentType, sha256, duplicazione chiavi)
        if (domainModel.getDocuments() != null && requestV1.getDocuments() != null) {
            for (int i = 0; i < domainModel.getDocuments().size() && i < requestV1.getDocuments().size(); i++) {
                InformalDocument domainDoc = domainModel.getDocuments().get(i);
                var v1Doc = requestV1.getDocuments().get(i);
                if (domainDoc.getContentType() != null && !domainDoc.getContentType().equalsIgnoreCase("application/pdf")) {
                    v1Doc.setContentType(domainDoc.getContentType());
                }
                if (domainDoc.getSha256() != null) {
                    v1Doc.setDigests(new NotificationAttachmentDigests().sha256(domainDoc.getSha256()));
                }
            }
            if (domainModel.getDocuments().size() >= 2 && requestV1.getDocuments().size() >= 2) {
                String key0 = domainModel.getDocuments().get(0).getKey();
                String key1 = domainModel.getDocuments().get(1).getKey();
                if (key0 != null && key0.equals(key1)) {
                    requestV1.getDocuments().get(1).getRef().setKey(requestV1.getDocuments().get(0).getRef().getKey());
                }
            }
        }

        log.info("Sending InformalNotification V1 payload for cxId={}: {}", cxId, requestV1);
        NewInformalNotificationResponse response = pnPaB2bInternalInformalClientImpl.sendNewInformalNotificationV1(cxId, requestV1);

        return InformalSendResult.builder()
                .notificationRequestId(response != null ? response.getNotificationRequestId() : null)
                .status("ACCEPTED")
                .rawResponse(response)
                .build();
    }

    public InformalNotificationRequestV1 mapToV1(InformalCommunication domainModel) {
        return mapper.toV1(domainModel);
    }
}