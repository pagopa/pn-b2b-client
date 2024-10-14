package it.pagopa.pn.cucumber.utils;

import it.pagopa.pn.client.b2b.generated.openapi.clients.generate.model.externalregistry.privateapi.PgUser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class LegalPersonsAuthStepsPojo {

    private List<LegalPersonAuthExpectedResponseWithStatus> responseWithStatusList;
    private List<PgUser> userListResponse;
    private RestClientResponseException exception;

    public LegalPersonsAuthStepsPojo() {
        this.responseWithStatusList = new LinkedList<>();
        this.userListResponse = new LinkedList<>();
    }

    public void addUserToList(PgUser user) {
        userListResponse.add(user);
    }
}
