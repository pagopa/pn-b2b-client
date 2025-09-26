package it.pagopa.pn.client.b2b.pa.service.utils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;

import java.util.Map;

public class AuthenticatorCognito {

    private final String username;
    private final String password;
    private final String clientId;
    private final CognitoIdentityProviderClient client;

    public AuthenticatorCognito (String username, String password, String clientId, Region region) {
        if (username == null || password == null || clientId == null) {
            throw new IllegalArgumentException("Username, password e clientId sono obbligatori");
        }
        this.username = username;
        this.password = password;
        this.clientId = clientId;

        this.client = CognitoIdentityProviderClient.builder()
                .region(region) // es: Region.EU_CENTRAL_1
                .build();
    }

    /**
     * Esegue login con USER_PASSWORD_AUTH e restituisce un JWT
     */
    public String generateJwtToken() {
        try {
            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .clientId(clientId)
                    .authParameters(Map.of(
                            "USERNAME", username,
                            "PASSWORD", password
                    ))
                    .build();

            InitiateAuthResponse response = client.initiateAuth(authRequest);

            if (response.authenticationResult() == null ||
                    response.authenticationResult().idToken() == null) {
                throw new RuntimeException("Token JWT non restituito da Cognito");
            }

            return response.authenticationResult().idToken();

        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Errore durante l'autenticazione Cognito: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}

