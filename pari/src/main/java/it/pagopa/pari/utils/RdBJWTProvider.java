package it.pagopa.pari.utils;

import it.pagopa.pari.registrobeni.domain.RdbRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RdBJWTProvider {
    private Map<RdbRole, String> jwtForRole = new HashMap<>();

    public String provideJWT(RdbRole role) {
        return jwtForRole.get(role);
    }

    public void storeJwt(RdbRole operator, String jwt) {
        jwtForRole.put(operator, jwt);
    }
}
