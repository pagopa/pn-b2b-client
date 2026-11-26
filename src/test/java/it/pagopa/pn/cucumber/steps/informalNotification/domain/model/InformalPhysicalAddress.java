package it.pagopa.pn.cucumber.steps.informalNotification.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformalPhysicalAddress {
    private String address;
    private String addressDetails;
    private String zip;
    private String municipality;
    private String province;
    private String foreignState;
    private String at;
}
