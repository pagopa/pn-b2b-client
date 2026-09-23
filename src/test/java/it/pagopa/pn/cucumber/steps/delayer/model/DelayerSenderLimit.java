package it.pagopa.pn.cucumber.steps.delayer.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"pk", "deliveryDate", "weeklyEstimate"})
@ToString
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

    public DelayerSenderLimit(DelayerSenderLimit original) {
        this.pk = original.pk;
        this.deliveryDate = original.deliveryDate;
        this.weeklyEstimate = original.weeklyEstimate;
        this.monthlyEstimate = original.monthlyEstimate;
        this.originalEstimate = original.originalEstimate;
        this.paId = original.paId;
        this.productType = original.productType;
        this.province = original.province;
    }
}
