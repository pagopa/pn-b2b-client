package it.pagopa.pn.interop.cucumber.steps.authorization;

import it.pagopa.interop.conf.api_profile.ApiProfile.ApiSet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// TODO utile solo in fase di sviluppo. Considerarne rimozioni una volta implementati tutti i test di Evolutive Nuovi Operatori.
class ClientCreateStepProfileRoleTest {
    @Test
    void acceptsM2mRolesOnlyForM2m() {
        for (String role : List.of("m2m", "m2m-admin", "M2M", "M2M-ADMIN")) {
            assertThatCode(() -> ClientCreateStep.validateProfileRole(role, ApiSet.M2M)).doesNotThrowAnyException();
            assertThatThrownBy(() -> ClientCreateStep.validateProfileRole(role, ApiSet.BFF))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void acceptsBffRolesOnlyForBff() {
        for (String role : List.of("admin", "support", "api", "security", "api,security", "reviewer", "viewer")) {
            assertThatCode(() -> ClientCreateStep.validateProfileRole(role, ApiSet.BFF)).doesNotThrowAnyException();
            assertThatThrownBy(() -> ClientCreateStep.validateProfileRole(role, ApiSet.M2M))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void rejectsMissingAndUnknownRoles() {
        assertThatThrownBy(() -> ClientCreateStep.validateProfileRole(null, ApiSet.BFF)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ClientCreateStep.validateProfileRole("admin", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ClientCreateStep.validateProfileRole("unknown", ApiSet.BFF)).isInstanceOf(IllegalArgumentException.class);
    }
}
