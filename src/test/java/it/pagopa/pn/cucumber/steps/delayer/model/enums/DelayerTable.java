package it.pagopa.pn.cucumber.steps.delayer.model.enums;

public enum DelayerTable {

    DelayerPaperDelivery("pn-DelayerPaperDelivery"),
    PaperDeliveryDriverCapacities("pn-PaperDeliveryDriverCapacities"),
    PaperDeliveryCounters("pn-PaperDeliveryCounters"),
    PaperDeliveryDriverUsedCapacities("pn-PaperDeliveryDriverUsedCapacities"),
    PaperDeliverySenderLimit("pn-PaperDeliverySenderLimit"),
    PaperDeliveryUsedSenderLimit("pn-PaperDeliveryUsedSenderLimit"),
    PaperDeliveryDriverCapacitiesMock("pn-PaperDeliveryDriverCapacitiesMock"),
    DelayerPaperDeliveryJsonView("pn_delayer_paper_delivery_json_view");
    public final String tableName;

    DelayerTable(String tableName) {
        this.tableName = tableName;
    }

    public String toString() {
        return tableName;
    }

}
