package it.pagopa.pn.interop.cucumber.steps.notification;

import io.cucumber.java.en.When;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfigUpdateSeed;
import it.pagopa.interop.notification.NotificationConfigClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

@Slf4j
public class NotificationConfigSteps {

    private enum NotificationConfigOp { READ, UPDATE, UNKNOWN }
    private enum ConfigTarget { USER, TENANT }

    private final NotificationConfigClient configClient;

    private TenantNotificationConfig actualTenantNotificationConfig;
    private UserNotificationConfig actualUserNotificationConfig;
    private TenantNotificationConfig expectedTenantNotificationConfig;
    private UserNotificationConfig expectedUserNotificationConfig;

    public NotificationConfigSteps(SharedStepsContext sharedStepsContext, ClientTokenConfigurator clientTokenConfigurator) {
        this.configClient = (NotificationConfigClient) clientTokenConfigurator.getNotificationConfigClient();
        this.configClient.setHttpCallExecutor(sharedStepsContext.getHttpCallExecutor());
    }

    @When("si tenta di {word} la configurazione delle notifiche per {word}")
    public void crudConfiguration(String rawOp, String t) {
        ConfigTarget target = parseConfigTarget(t);
        handleConfigOperation(rawOp, null, target);
    }

    @When("si tenta di {word} la configurazione delle notifiche per {word} specificando un valore {entityIdType}")
    public void crudConfiguration(String rawOp, String t, EntityIdType entityIdType) {
        ConfigTarget target = parseConfigTarget(t);
        handleConfigOperation(rawOp, entityIdType, target);
    }

    @When("la configurazione delle notifiche per {word} {word} restituita")
    public void checkReadConfiguration(String target, String assertion) {
        ConfigTarget configTarget = parseConfigTarget(target);
        boolean exists = parseExistenceToken(assertion);

        Object config = (configTarget == ConfigTarget.USER)
                ? actualUserNotificationConfig
                : actualTenantNotificationConfig;

        if (exists) {
            Assertions.assertThat(config)
                    .as("La configurazione deve essere presente")
                    .isNotNull();
        } else {
            Assertions.assertThat(config)
                    .as("La configurazione non deve essere presente")
                    .isNull();
        }
    }

    @When("modifica {word} applicata")
    public void checkUpdateConfiguration(String assertion) {

        final boolean isTenantFlow = actualTenantNotificationConfig != null;

        // expected: quello impostato dallo step di update (tenant o user)
        Object expected = isTenantFlow ? expectedTenantNotificationConfig : expectedUserNotificationConfig;

        // fallback: se non ho un expected "esplicito", considero come expected la baseline (prima dell'update)
        if (expected == null) {
            expected = isTenantFlow ? actualTenantNotificationConfig : actualUserNotificationConfig;
        }

        final Object expectedFinal = expected; // effectively final per la lambda

        Object actual = PollingService.makePolling(
                () -> isTenantFlow
                        ? configClient.getTenantConfig()
                        : configClient.getUserConfig(),
                resp -> java.util.Objects.equals(resp, expectedFinal),
                "Actual ed expected devono coincidere (ma non coincidono entro il polling)",
                30,
                1000
        );


        Assertions.assertThat(actual)
                .as("Actual ed expected devono coincidere")
                .isEqualTo(expectedFinal);
    }

    private void handleConfigOperation(String rawOp, EntityIdType entityIdType, ConfigTarget target) {
        NotificationConfigOp op = parseOp(rawOp);

        UserNotificationConfigUpdateSeed userSeed = new UserNotificationConfigUpdateSeed();
        TenantNotificationConfigUpdateSeed tenantSeed = new TenantNotificationConfigUpdateSeed();

        if (entityIdType != null) {
            // gestione casi di seed "malformato / mancante"
            if (entityIdType == EntityIdType.NON_EXISTENT_ID) {
                userSeed.setEmailConfig(null);
                tenantSeed.setEnabled(null);
            } else if (entityIdType == EntityIdType.INVALID_ID) {
                userSeed = null;
                tenantSeed = null;
            } else {
                throw new IllegalArgumentException("EntityIdType non gestito: " + entityIdType);
            }
        } else {
            // toggle su configurazione attuale (se presente)
            if (actualUserNotificationConfig != null) {
                userSeed.setInAppNotificationPreference(actualUserNotificationConfig.getInAppNotificationPreference());
                userSeed.setEmailNotificationPreference(!actualUserNotificationConfig.getEmailNotificationPreference());
                userSeed.setEmailDigestPreference(actualUserNotificationConfig.getEmailDigestPreference());
                userSeed.setInAppConfig(actualUserNotificationConfig.getInAppConfig());
                userSeed.setEmailConfig(actualUserNotificationConfig.getEmailConfig());
            }
            if (actualTenantNotificationConfig != null) {
                tenantSeed.setEnabled(!actualTenantNotificationConfig.getEnabled());
            }
        }

        switch (op) {
            case READ -> onConfigRead(target);
            case UPDATE -> onConfigUpdate(target == ConfigTarget.USER ? userSeed : tenantSeed, target, entityIdType != null);
            default -> onUnknown(rawOp);
        }
    }

    private void onConfigRead(ConfigTarget target) {
        switch (target) {
            case USER -> {
                try{
                    actualUserNotificationConfig = configClient.getUserConfig();
                }catch (IllegalStateException e){
                    log.warn(e.getMessage());
                }
            }
            case TENANT -> {
                try{
                    actualTenantNotificationConfig = configClient.getTenantConfig();
                }catch (IllegalStateException e){
                    log.warn(e.getMessage());
                }
            }
        }
    }

    private void onConfigUpdate(Object seed, ConfigTarget target, boolean isInvalid) {
        switch (target) {
            case USER -> {
                UserNotificationConfigUpdateSeed userSeed = (UserNotificationConfigUpdateSeed) seed;

                try{
                    expectedUserNotificationConfig = configClient.getUserConfig();
                }catch (IllegalStateException e){
                    log.warn(e.getMessage());
                }

                configClient.updateUserNotificationConfig(userSeed);

                if (userSeed != null && expectedUserNotificationConfig != null &&!isInvalid) {
                    expectedUserNotificationConfig.setEmailNotificationPreference(userSeed.getEmailNotificationPreference());
                }
            }
            case TENANT -> {
                TenantNotificationConfigUpdateSeed tenantSeed = (TenantNotificationConfigUpdateSeed) seed;

                try{
                    expectedTenantNotificationConfig = configClient.getTenantConfig();
                }catch (IllegalStateException e){
                    log.warn(e.getMessage());
                }

                configClient.updateTenantNotificationConfig(tenantSeed);

                if (tenantSeed != null && expectedTenantNotificationConfig != null && !isInvalid) {
                    expectedTenantNotificationConfig.setEnabled(tenantSeed.getEnabled());
                }
            }
        }
    }

    private NotificationConfigOp parseOp(String op) {
        if (op == null) return NotificationConfigOp.UNKNOWN;

        return switch (op.toLowerCase()) {
            case "recuperare" -> NotificationConfigOp.READ;
            case "modificare" -> NotificationConfigOp.UPDATE;
            default -> NotificationConfigOp.UNKNOWN;
        };
    }

    private ConfigTarget parseConfigTarget(String t) {
        if (t == null) {
            throw new IllegalArgumentException("Target configurazione nullo");
        }
        return switch (t.toLowerCase()) {
            case "user" -> ConfigTarget.USER;
            case "tenant" -> ConfigTarget.TENANT;
            default -> throw new IllegalArgumentException("Target configurazione non riconosciuto: " + t);
        };
    }

    private void onUnknown(String rawOp) {throw new IllegalArgumentException("Operazione non riconosciuta: " + rawOp);}

    private boolean parseExistenceToken(String assertion) {
        if (!"viene".equals(assertion) && !"non".equals(assertion)) {
            throw new IllegalArgumentException("Token non riconosciuto: " + assertion);
        }
        return "viene".equals(assertion);
    }
}
