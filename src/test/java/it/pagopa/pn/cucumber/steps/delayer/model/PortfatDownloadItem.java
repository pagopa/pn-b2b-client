package it.pagopa.pn.cucumber.steps.delayer.model;

import lombok.Data;

@Data //todo t portfat valutare se tenere
public class PortfatDownloadItem {

    private String fileName;
    private String status;

    private String monthSent;   // YYYY-MM
    private String orderMonth;  // YYYY-MM

}
