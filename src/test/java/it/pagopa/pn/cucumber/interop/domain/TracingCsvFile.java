package it.pagopa.pn.cucumber.interop.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TracingCsvFile {
    private String date;
    private String purpose_id;
    private String status;
    private int request_count;
}
