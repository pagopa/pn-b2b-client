package it.pagopa.pari.registrobeni.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class StatusChangeChronology {
    private String username;
    private String role;
    private String motivation;
    private String updateDate;
    private String currentStatus;
    private String targetStatus;
}
