package it.pagopa.pn.cucumber.steps.delayer.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DelayerSenderLimit {
    private String pk;
    private String deliveryDate;
    private int weeklyEstimate;
    private int monthlyEstimate;
    private int originalEstimate;
    private String paId;
    private String productType;
    private String province;

    public DelayerSenderLimit(JsonNode node) {
        this.pk = node.path("pk").asText();
        this.deliveryDate = node.path("deliveryDate").asText();
        this.weeklyEstimate = node.path("weeklyEstimate").asInt();
        this.monthlyEstimate = node.path("monthlyEstimate").asInt();
        this.originalEstimate = node.path("originalEstimate").asInt();
        this.paId = node.path("paId").asText();
        this.productType = node.path("productType").asText();
        this.province = node.path("province").asText();
    }
}
