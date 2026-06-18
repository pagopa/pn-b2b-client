package it.pagopa.pn.interop.cucumber.steps.notification;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import it.pagopa.interop.authorization.domain.Tenant;
import it.pagopa.interop.authorization.service.utils.ConfigFileReader;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.generated.openapi.clients.bff.model.Notification;
import it.pagopa.interop.generated.openapi.clients.bff.model.NotificationConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantNotificationConfigUpdateSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.UserNotificationConfigUpdateSeed;
import it.pagopa.interop.notification.NotificationClientImpl;
import it.pagopa.interop.notification.NotificationConfigClient;
import it.pagopa.pn.interop.cucumber.steps.ClientTokenConfigurator;
import it.pagopa.pn.interop.cucumber.steps.SharedStepsContext;
import it.pagopa.pn.interop.cucumber.utility.FeatureLifecycleManager;
import it.pagopa.pn.interop.cucumber.utility.functionalint.Task;
import it.pagopa.pn.interop.cucumber.utility.functionalint.ThrowingConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

public class NotificationStepsConfig {
    @Data
    @AllArgsConstructor
    @Builder
    static class GlobalNotificationConfig {
        private TenantNotificationConfigUpdateSeed tenantConfig;
        private UserNotificationConfigUpdateSeed userConfig;
    }

    enum ConfigStrategy {PER_ROLE, NO_CONFIG, ALL_BUT_ESERVICE_STATE_CHANGED}

    private final NotificationClientImpl notificationClient;
    private final NotificationConfigClient notificationConfigClient;
    private final ClientTokenConfigurator clientTokenConfigurator;
    private final FeatureLifecycleManager notificationTestsManager;
    private final SharedStepsContext sharedStepsContext;
    private final ConfigFileReader configFileReader;

    private static final Map<String, GlobalNotificationConfig> rolesNotificationConfig;
    private static final GlobalNotificationConfig noNotificationConfig;
    private static final GlobalNotificationConfig adminConfigAllButEserviceStateChanged;

    // TODO 13/01/2026 sostituibile con file di configurazione esterno
    static {
        GlobalNotificationConfig adminConfig = GlobalNotificationConfig.builder()
                .tenantConfig(new TenantNotificationConfigUpdateSeed().enabled(true))
                .userConfig(new UserNotificationConfigUpdateSeed()
                        .emailNotificationPreference(false)
                        .inAppNotificationPreference(true)
                        .emailDigestPreference(true)
                        .emailConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(false)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(false)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        )
                        .inAppConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(true)
                                .agreementSuspendedUnsuspendedToProducer(true)
                                .clientAddedRemovedToProducer(true)
                                .purposeStatusChangedToProducer(true)
                                .templateStatusChangedToProducer(true)
                                .purposeQuotaAdjustmentRequestToProducer(true)
                                .eserviceStateChangedToProducer(true)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(true)
                                .eserviceStateChangedToConsumer(true)
                                .agreementActivatedRejectedToConsumer(true)
                                .purposeActivatedRejectedToConsumer(true)
                                .purposeSuspendedUnsuspendedToConsumer(true)
                                .purposeOverQuotaStateToConsumer(true)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(true)
                                .eserviceTemplateNameChangedToInstantiator(true)
                                .eserviceTemplateStatusChangedToInstantiator(true)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(true)
                                .eserviceNewVersionSubmittedToDelegator(true)
                                .eserviceNewVersionApprovedRejectedToDelegate(true)
                                .delegationSubmittedRevokedToDelegate(true)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(true)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(true)
                        ))
                .build();

        // TODO 13 01 2026 da tarare durante i test
        GlobalNotificationConfig supportConfig = GlobalNotificationConfig.builder()
                .tenantConfig(new TenantNotificationConfigUpdateSeed().enabled(true))
                .userConfig(new UserNotificationConfigUpdateSeed()
                        .emailNotificationPreference(false)
                        .inAppNotificationPreference(true)
                        .emailDigestPreference(true)
                        .emailConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(false)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(false)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        )
                        .inAppConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(true)
                                .agreementSuspendedUnsuspendedToProducer(true)
                                .clientAddedRemovedToProducer(true)
                                .purposeStatusChangedToProducer(true)
                                .templateStatusChangedToProducer(true)
                                .purposeQuotaAdjustmentRequestToProducer(true)
                                .eserviceStateChangedToProducer(true)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(true)
                                .eserviceStateChangedToConsumer(true)
                                .agreementActivatedRejectedToConsumer(true)
                                .purposeActivatedRejectedToConsumer(true)
                                .purposeSuspendedUnsuspendedToConsumer(true)
                                .purposeOverQuotaStateToConsumer(true)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(true)
                                .eserviceTemplateNameChangedToInstantiator(true)
                                .eserviceTemplateStatusChangedToInstantiator(true)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(true)
                                .eserviceNewVersionSubmittedToDelegator(true)
                                .eserviceNewVersionApprovedRejectedToDelegate(true)
                                .delegationSubmittedRevokedToDelegate(true)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(true)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(true)
                        ))
                .build();

        // TODO 13 01 2026 da tarare durante i test
        GlobalNotificationConfig securityConfig = GlobalNotificationConfig.builder()
                .userConfig(new UserNotificationConfigUpdateSeed()
                        .emailNotificationPreference(false)
                        .inAppNotificationPreference(true)
                        .emailDigestPreference(true)
                        .emailConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(false)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(false)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        )
                        .inAppConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(false)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(true)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        ))
                .build();

        // TODO 13 01 2026 da tarare durante i test
        GlobalNotificationConfig apiConfig = GlobalNotificationConfig.builder()
                .userConfig(new UserNotificationConfigUpdateSeed()
                        .emailNotificationPreference(false)
                        .inAppNotificationPreference(true)
                        .emailDigestPreference(false)
                        .emailConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(false)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(false)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        )
                        .inAppConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(true)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(false)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        ))
                .build();

        // TODO 13 01 2026 da tarare durante i test
        GlobalNotificationConfig apiSecurityConfig = GlobalNotificationConfig.builder()
                .userConfig(new UserNotificationConfigUpdateSeed()
                        .emailNotificationPreference(false)
                        .inAppNotificationPreference(true)
                        .emailDigestPreference(false)
                        .emailConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(false)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(false)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        )
                        .inAppConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(true)
                                .agreementSuspendedUnsuspendedToProducer(true)
                                .clientAddedRemovedToProducer(true)
                                .purposeStatusChangedToProducer(true)
                                .templateStatusChangedToProducer(true)
                                .purposeQuotaAdjustmentRequestToProducer(true)
                                .eserviceStateChangedToProducer(true)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(true)
                                .eserviceStateChangedToConsumer(true)
                                .agreementActivatedRejectedToConsumer(true)
                                .purposeActivatedRejectedToConsumer(true)
                                .purposeSuspendedUnsuspendedToConsumer(true)
                                .purposeOverQuotaStateToConsumer(true)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(true)
                                .eserviceTemplateNameChangedToInstantiator(true)
                                .eserviceTemplateStatusChangedToInstantiator(true)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(true)
                                .eserviceNewVersionSubmittedToDelegator(true)
                                .eserviceNewVersionApprovedRejectedToDelegate(true)
                                .delegationSubmittedRevokedToDelegate(true)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(true)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(true)
                        ))
                .build();

        noNotificationConfig = GlobalNotificationConfig.builder()
                .tenantConfig(new TenantNotificationConfigUpdateSeed().enabled(false))
                .userConfig(new UserNotificationConfigUpdateSeed()
                        .emailNotificationPreference(false)
                        .inAppNotificationPreference(false)
                        .emailDigestPreference(false)
                        .emailConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(false)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(false)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        )
                        .inAppConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(false)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(false)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        ))
                .build();

        adminConfigAllButEserviceStateChanged = GlobalNotificationConfig.builder()
                .tenantConfig(new TenantNotificationConfigUpdateSeed().enabled(true))
                .userConfig(new UserNotificationConfigUpdateSeed()
                        .emailNotificationPreference(false)
                        .inAppNotificationPreference(true)
                        .emailDigestPreference(true)
                        .emailConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(false)
                                .agreementSuspendedUnsuspendedToProducer(false)
                                .clientAddedRemovedToProducer(false)
                                .purposeStatusChangedToProducer(false)
                                .templateStatusChangedToProducer(false)
                                .purposeQuotaAdjustmentRequestToProducer(false)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(false)
                                .eserviceStateChangedToConsumer(false)
                                .agreementActivatedRejectedToConsumer(false)
                                .purposeActivatedRejectedToConsumer(false)
                                .purposeSuspendedUnsuspendedToConsumer(false)
                                .purposeOverQuotaStateToConsumer(false)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(false)
                                .eserviceTemplateNameChangedToInstantiator(false)
                                .eserviceTemplateStatusChangedToInstantiator(false)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(false)
                                .eserviceNewVersionSubmittedToDelegator(false)
                                .eserviceNewVersionApprovedRejectedToDelegate(false)
                                .delegationSubmittedRevokedToDelegate(false)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(false)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(false)
                        )
                        .inAppConfig(new NotificationConfig()
                                // Producer
                                .agreementManagementToProducer(true)
                                .agreementSuspendedUnsuspendedToProducer(true)
                                .clientAddedRemovedToProducer(true)
                                .purposeStatusChangedToProducer(true)
                                .templateStatusChangedToProducer(true)
                                .purposeQuotaAdjustmentRequestToProducer(true)
                                .eserviceStateChangedToProducer(false)

                                // Consumer
                                .agreementSuspendedUnsuspendedToConsumer(true)
                                .eserviceStateChangedToConsumer(true)
                                .agreementActivatedRejectedToConsumer(true)
                                .purposeActivatedRejectedToConsumer(true)
                                .purposeSuspendedUnsuspendedToConsumer(true)
                                .purposeOverQuotaStateToConsumer(true)

                                // Instantiator
                                .newEserviceTemplateVersionToInstantiator(true)
                                .eserviceTemplateNameChangedToInstantiator(true)
                                .eserviceTemplateStatusChangedToInstantiator(true)

                                // Delegator/Delegate
                                .delegationApprovedRejectedToDelegator(true)
                                .eserviceNewVersionSubmittedToDelegator(true)
                                .eserviceNewVersionApprovedRejectedToDelegate(true)
                                .delegationSubmittedRevokedToDelegate(true)

                                // Altri
                                .certifiedVerifiedAttributeAssignedRevokedToAssignee(true)
                                .clientKeyAndProducerKeychainKeyAddedDeletedToClientUsers(true)
                        ))
                .build();

        rolesNotificationConfig = Map.of(
                "admin", adminConfig,
                "support", supportConfig,
                "security", securityConfig,
                "api", apiConfig,
                "api,security", apiSecurityConfig
        );
    }

    public NotificationStepsConfig(
            SharedStepsContext sharedStepsContext,
            ClientTokenConfigurator clientTokenConfigurator,
            ConfigFileReader configFileReader,
            @Qualifier("notificationFeatureLifecycleManager") FeatureLifecycleManager notificationTestsManager
    ) {
        this.clientTokenConfigurator = clientTokenConfigurator;

        // necessario ricorrere all'impl. concreta per usare il suo HttpCallExecutor
        this.notificationClient = (NotificationClientImpl) clientTokenConfigurator.getNotificationClient();
        this.notificationConfigClient = (NotificationConfigClient) clientTokenConfigurator.getNotificationConfigClient();
        this.notificationTestsManager = notificationTestsManager;
        this.sharedStepsContext = sharedStepsContext;
        this.configFileReader = configFileReader;
    }

    @Before("@bff-notification and not @disable-notifications-hooks")
    public void switchOnInAppNotification() throws Exception {
        // TODO 13 01 2026 si intende ridurre la lista durante i test attraverso sperimentazione,
        //  fino a che ogni ruolo permesso avrà la sua corretta configurazione e non ci sarà più bisogno di escluderne qualcuno
        List<String> excludedRoles = rolesNotificationConfig.keySet().stream()
                .filter(role -> !List.of("admin").contains(role))
                .toList();
        this.configNotificationTests(excludedRoles, this.notificationTestsManager::before, ConfigStrategy.PER_ROLE);
    }

// Spegnere ed eliminare le notifiche da pochi o tutti gli utenti può determinare un disturbo di altri test in QA
//
//    @After("@bff-notification and not @disable-notifications-hooks")
//    public void switchOffInAppNotification() throws Exception {
//        // TODO 13 01 2026 si intende ridurre la lista durante i test attraverso sperimentazione,
//        //  fino a che ogni ruolo permesso avrà la sua configurazione e non ci sarà più bisogno di escluderne qualcuno
//        List<String> excludedRoles = rolesNotificationConfig.keySet().stream()
//                .filter(role -> !List.of("admin").contains(role))
//                .toList();
//        this.configNotificationTests(excludedRoles, this.notificationTestsManager::after, ConfigStrategy.NO_CONFIG);
//    }
//
//    // NOTE 13 01 2026 Potrebbero presentarsi problemi di race conditions (non andrebbero cancellate le notifiche se un altro test è in corso)
//    @After("@bff-notification and not @disable-notifications-hooks")
//    public void deleteAllNotifications() throws Exception {
//        PollingService pollingService = this.sharedStepsContext.getPollingService();
//        IHttpExecutor notificationExecutor = this.notificationClient.getHttpCallExecutor();
//        IHttpExecutor executor = this.sharedStepsContext.getHttpCallExecutor();
//        applyTaskForEveryUser(List.of("support"), role -> {
//            List<Notification> notifications = pollingService.makePolling(
//                    this.notificationClient::getAll,
//                    res -> notificationExecutor.getResponseStatus().is2xxSuccessful(),
//                    "Reperimento notifiche fallito");
//
//            while (!notifications.isEmpty()) {
//                List<UUID> notificationsIds = notifications.stream().map(Notification::getId).toList();
//
//                /* TODO 12/01/2026 per bypassare nel breve termine una problematica di sviluppo sono
//                 * utilizzati due executors distinti. Correggere usandone uno solo appena possibile. */
//                pollingService.makePolling(
//                        () -> executor.performCall(() -> this.notificationClient.deleteAll(notificationsIds)),
//                        HttpStatus::is2xxSuccessful,
//                        "Eliminazione notifiche fallita");
//                notifications = pollingService.makePolling(
//                        this.notificationClient::getAll,
//                        res -> notificationExecutor.getResponseStatus().is2xxSuccessful(),
//                        "Reperimento notifiche fallito");
//            }
//        });
//    }

    private void configNotificationTests(List<String> excludedRoles,
                                         ThrowingConsumer<Task> hook, ConfigStrategy configStrategy
    ) throws Exception {
        PollingService pollingService = this.sharedStepsContext.getPollingService();
        IHttpExecutor configExecutor = this.notificationConfigClient.getHttpCallExecutor();
        hook.accept(() -> applyTaskForEveryUser(excludedRoles, role -> {
            GlobalNotificationConfig config = configStrategy == ConfigStrategy.NO_CONFIG ? noNotificationConfig : rolesNotificationConfig.get(role);
            if (config.getUserConfig() != null) {
                pollingService.makePolling(
                        () -> {
                            this.notificationConfigClient.updateUserNotificationConfig(
                                    config.getUserConfig());
                            return null;
                        },
                        res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                        "Configurazione notifiche user fallita");
            }

            if (config.getTenantConfig() != null) {
                pollingService.makePolling(
                        () -> {
                            this.notificationConfigClient.updateTenantNotificationConfig(config.getTenantConfig());
                            return null;
                        },
                        res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                        "Configurazione notifiche tenant fallita");
            }
        }));
    }

    private void configureNotificationsToUser(List<String> excludedRoles, ConfigStrategy configStrategy) throws Exception {
        PollingService pollingService = this.sharedStepsContext.getPollingService();
        IHttpExecutor configExecutor = this.notificationConfigClient.getHttpCallExecutor();
        applyTaskForEveryUser(excludedRoles, role -> {
            GlobalNotificationConfig config = switch (configStrategy) {
                case NO_CONFIG -> noNotificationConfig;
                case ALL_BUT_ESERVICE_STATE_CHANGED -> adminConfigAllButEserviceStateChanged;
                default -> rolesNotificationConfig.get(role);
            };
            if (config.getUserConfig() != null) {
                pollingService.makePolling(
                        () -> {
                            this.notificationConfigClient.updateUserNotificationConfig(
                                    config.getUserConfig());
                            return null;
                        },
                        res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                        "Configurazione notifiche user fallita");
            }
            if (config.getTenantConfig() != null) {
                pollingService.makePolling(
                        () -> {
                            this.notificationConfigClient.updateTenantNotificationConfig(config.getTenantConfig());
                            return null;
                        },
                        res -> configExecutor.getResponseStatus().is2xxSuccessful(),
                        "Configurazione notifiche tenant fallita");
            }
        });
    }

    // TODO generalizzabile in utility separata
    private void applyTaskForEveryUser(List<String> excludedRoles, ThrowingConsumer<String> taskPerRole) throws Exception {
        List<Tenant> tenantList = this.configFileReader.getTenantList();
        for (Tenant tenant : tenantList) {
            // FIXME scorciatoia temporanea per interrogare solo PA1 e PA2
            if (!tenant.getName().equals("PA1") && !tenant.getName().equals("PA2")) continue;
            Map<String, List<String>> rolesCopy = new HashMap<>(tenant.getUserRoles());
            Set<Entry<String, List<String>>> roles = rolesCopy.entrySet();
            for (Entry<String, List<String>> roleEntry : roles) {
                String role = roleEntry.getKey();
                List<String> users = roleEntry.getValue();
                for (int i = 0; i < users.size() && !excludedRoles.contains(role); i++) {
                    String token = this.sharedStepsContext.getIdentityService()
                            .getToken(tenant.getName(), role, i);
                    this.clientTokenConfigurator.setBearerToken(token);
                    taskPerRole.accept(role);
                }
            }
        }
    }

    @Given("l'utente attiva le notifiche tranne il cambio di stato dell'e-service per l'erogatore")
    public void userTurnOnNotificationButEserviceStateChanged() throws Exception {
        List<String> excludedRoles = rolesNotificationConfig.keySet().stream()
                .filter(role -> !List.of("admin").contains(role))
                .toList();
        this.configureNotificationsToUser(excludedRoles, ConfigStrategy.ALL_BUT_ESERVICE_STATE_CHANGED);
    }
}
