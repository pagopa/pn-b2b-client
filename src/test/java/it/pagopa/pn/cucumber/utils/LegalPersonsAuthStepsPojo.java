package it.pagopa.pn.cucumber.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class LegalPersonsAuthStepsPojo {

    private List<LegalPersonAuthExpectedResponseWithStatus> responseWithStatusList;
    private List<Object> userListResponse;
    private RestClientResponseException exception;

    public LegalPersonsAuthStepsPojo() {
        this.responseWithStatusList = new LinkedList<>();
        this.userListResponse = new LinkedList<>();
    }
}
