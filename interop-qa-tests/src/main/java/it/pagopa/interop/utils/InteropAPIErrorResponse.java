package it.pagopa.interop.utils;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class InteropAPIErrorResponse {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InteropAPIError {
        private String code;
        private String detail;
    }

    private UUID correlationId;
    private List<InteropAPIError> errors;
    private String status;
    private String detail;
    private String title;
    private String type;
}
