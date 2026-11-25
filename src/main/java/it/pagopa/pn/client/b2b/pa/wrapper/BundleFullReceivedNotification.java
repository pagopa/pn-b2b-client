package it.pagopa.pn.client.b2b.pa.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.pn.client.b2b.generated.openapi.clients.delivery2b.model.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BundleFullReceivedNotification {
    private String idempotenceToken;
    private String paProtocolNumber;
    private String subject;
    @JsonProperty("abstract")
    private String _abstract;
    private List<NotificationRecipientV24> recipients;
    private List<NotificationDocument> documents;
    private NotificationFeePolicy notificationFeePolicy;
    private String cancelledIun;

    private FullReceivedNotificationV28.PhysicalCommunicationTypeEnum physicalCommunicationType;

    @lombok.ToString.Exclude
    private String senderDenomination;
    private String senderTaxId;
    private String group;
    private Integer amount;
    private String paymentExpirationDate;
    private String taxonomyCode;
    private Integer paFee;
    private Integer vat;

    private FullReceivedNotificationV28.PagoPaIntModeEnum pagoPaIntMode;
    private List<String> additionalLanguages;
    private String senderPaId;
    private String iun;
    private java.lang.String sentAt;
    private String cancelledByIun;
    private Boolean documentsAvailable;
    private String version;
    private UsedServices usedServices;
    private BundleNotificationStatus notificationStatus;
    private List<TimelineElementV28> timeline;

    public enum BundleNotificationStatus {
        IN_VALIDATION("IN_VALIDATION"),
        ACCEPTED("ACCEPTED"),
        REFUSED("REFUSED"),
        DELIVERING("DELIVERING"),
        DELIVERED("DELIVERED"),
        VIEWED("VIEWED"),
        EFFECTIVE_DATE("EFFECTIVE_DATE"),
        PAID("PAID"),
        UNREACHABLE("UNREACHABLE"),
        CANCELLED("CANCELLED"),
        CANCELLATION_IN_PROGRESS("CANCELLATION_IN_PROGRESS"),
        RETURNED_TO_SENDER("RETURNED_TO_SENDER");

        private final String value;

        BundleNotificationStatus(String value) {
            this.value = value;
        }

        public static BundleNotificationStatus fromValue(String value) {
            for (BundleNotificationStatus notificationStatus : BundleNotificationStatus.values()) {
                if (notificationStatus.value.equals(value)) {
                    return notificationStatus;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

}
