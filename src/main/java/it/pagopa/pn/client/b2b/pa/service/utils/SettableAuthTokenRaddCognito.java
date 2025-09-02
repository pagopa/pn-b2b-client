package it.pagopa.pn.client.b2b.pa.service.utils;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

import java.util.HashMap;
import java.util.Map;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SettableAuthTokenRaddCognito {


    private final String raddCognitoUser1;
    private final String raddCognitoPasswordUser1;
    private final String raddCognitoClientIdUser1;
    private final String raddCognitoUser2;
    private final String raddCognitoPasswordUser2;
    private final String raddCognitoClientIdUser2;

    @Setter
    @Getter
    private String tokenCognito;

    public SettableAuthTokenRaddCognito(@Value("${pn.external.radd-cognito-user-1}") String raddCognitoUser1,
                                        @Value("${pn.external.radd-cognito-password-user-1}") String raddCognitoPasswordUser1,
                                        @Value("${pn.external.radd-cognito-clientid-user-1}") String raddCognitoClientIdUser1,
                                        @Value("${pn.external.radd-cognito-user-2}") String raddCognitoUser2,
                                        @Value("${pn.external.radd-cognito-password-user-2}") String raddCognitoPasswordUser2,
                                        @Value("${pn.external.radd-cognito-clientid-user-2}") String raddCognitoClientIdUser2
    ) {
        this.raddCognitoUser1 = raddCognitoUser1;
        this.raddCognitoPasswordUser1 = raddCognitoPasswordUser1;
        this.raddCognitoClientIdUser1 = raddCognitoClientIdUser1;
        this.raddCognitoUser2 = raddCognitoUser2;
        this.raddCognitoPasswordUser2 = raddCognitoPasswordUser2;
        this.raddCognitoClientIdUser2 = raddCognitoClientIdUser2;

    }

    private final Map<String, String> tokenCache = new HashMap<>();

    /**
     * Restituisce un token JWT Cognito generato in base all'utente scelto
     *
     * @param userIndex LETTURA_SCRITTURA = usa credenziali user1, SOLO_LETTURA = usa credenziali user2
     */
    public String generateToken(String userIndex) {
        // Se il token è già stato generato, lo restituisco subito
        if (tokenCache.containsKey(userIndex)) {
            return tokenCache.get(userIndex);
        }
        String username;
        String password;
        String clientId;
        String token;

        switch (userIndex) {
            case "LETTURA_SCRITTURA" -> {
                username = raddCognitoUser1;
                password = raddCognitoPasswordUser1;
                clientId = raddCognitoClientIdUser1;
                token = getTokenCognito(username, password, clientId);
            }
            case "SOLO_LETTURA" -> {
                username = raddCognitoUser2;
                password = raddCognitoPasswordUser2;
                clientId = raddCognitoClientIdUser2;
                token = getTokenCognito(username, password, clientId);
            }
            case "TOKEN_NON_VALIDO" -> {
                token = "eyJraWQiOiI3aEduaWtrNmNXNWNDeHQ4V2VtRnBTXC9EVUtoZUpVcGNkNlwvT3k2bmxxbTQ9IiwiYWxnIjoiUlMyNTYifQ";
            }
            default -> throw new IllegalArgumentException("Indice utente non valido: " + userIndex);
        }

        tokenCache.put(userIndex, token);
        this.tokenCognito = token;
        return token;
    }



    private String getTokenCognito(String username, String password, String clientId) {

        AuthenticatorCognito authenticator =
                new AuthenticatorCognito(username, password, clientId, Region.EU_SOUTH_1); // cambia regione se serve

        String token = authenticator.generateJwtToken();

        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Errore: il token JWT restituito è nullo o vuoto");
        }
        if (token.length() <= 10) {
            throw new IllegalStateException("Errore: il token JWT sembra troppo corto");
        }

        System.out.println("Token ottenuto correttamente: " + this.tokenCognito);

        return token;
    }
}


