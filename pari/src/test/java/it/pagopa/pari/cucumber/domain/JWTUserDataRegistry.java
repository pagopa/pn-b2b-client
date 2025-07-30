package it.pagopa.pari.cucumber.domain;

import java.util.Map;

public class JWTUserDataRegistry {

    private final Map<String, JWTUserData> jwtUserDataMap = Map.of(
            "PRODUTTORE_1", JWTUserData.builder()
                    .uid("99457865-8a65-467f-aeec-7ce9f71c361a")
                    .name("Giuseppe")
                    .familyName("Polignano")
                    .orgId("b5ae0b41-b854-414e-8295-078595ee1db4")
                    .orgRole("operatore")
                    .orgFc("00005005050")
                    .build(),
            "PRODUTTORE_2", JWTUserData.builder()
                    .uid("195da70f-d3f0-4c57-b62e-ef471348e920")
                    .name("Lorenzo")
                    .familyName("Lollo")
                    .orgId("b5ae0b41-b854-414e-8295-078595ee1db5")
                    .orgRole("operatore")
                    .orgFc("00005005051")
                    .build(),
            "INVITALIA", JWTUserData.builder()
                    .uid("195da70f-d3f0-4c57-b62e-ef471348e920")
                    .name("Lorenzo")
                    .familyName("Lollo")
                    .orgId("b5ae0b41-b854-414e-8295-078595ee1da1")
                    .orgRole("invitalia")
                    .build()
    );

    public JWTUserData getUserData(String key) {
        return jwtUserDataMap.get(key);
    }

    public Map<String, JWTUserData> getAll() {
        return jwtUserDataMap;
    }

}
