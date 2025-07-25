package it.pagopa.pari.utils;

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
public class JWTUserRoleProvider {
    private Map<String, String> jwtUserRole = new HashMap<>();

    public String provideJWTRole(String role) {
        return jwtUserRole.get(role);
    }

    public void storeJwt(String operator, String jwt) {
        jwtUserRole.put(operator, jwt);
    }
}
