package it.pagopa.pn.client.b2b.pa.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class NotificationSearchParam {

    /**
     * Valore convenzionale per {@code xPagopaPnCxType}/{@code xPagopaPnCxId}/{@code iunMatch}: indica
     * che il chiamante deve risolvere il campo nel valore reale desunto dal contesto (es. il destinatario
     * della ricerca), anziché in un valore esplicito. Un {@code null} esplicito, al contrario, deve
     * restare tale fino alla chiamata API, per simulare l'assenza di un campo obbligatorio.
     */
    public static final String ACTUAL = "ACTUAL";

    public String xPagopaPnUid;
    public String xPagopaPnCxType;
    public String xPagopaPnCxId;
    public OffsetDateTime startDate;
    public OffsetDateTime endDate;
    public String mandateId;
    public String senderId;
    public String status;
    public String subjectRegExp;
    public String iunMatch;
    public String recipientId;
    public List<String> xPagopaPnCxGroups;
    public String group;
    public Integer size = 10;
    public String nextPagesKey;
    public String communicationType;
    public String campaignId;
}
