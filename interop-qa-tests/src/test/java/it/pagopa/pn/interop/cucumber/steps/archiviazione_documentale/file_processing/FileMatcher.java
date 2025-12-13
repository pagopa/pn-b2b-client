package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.agreement.*;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.auth.ClientDeletedEventStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.auth.KeyDeletedEventStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.auth.KeysAddedEventStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.auth.VoucherEventStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.delegation.*;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.eservice.DescriptorEserviceUpgradedEventStrategy;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file_processing.match_strategy.purpose.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.FileType.*;

public class FileMatcher implements IFileMatcher {
    private final Map<FileType, IFileMatcher> strategies = new HashMap<>();

    public FileMatcher() {
        strategies.put(AGREEMENT_ACTIVATED, new AgreementActivatedStrategy());
        strategies.put(AGREEMENT_ACTIVATED_SIGNED, new AgreementActivatedStrategy());
        strategies.put(AGREEMENT_ACTIVATED_EVENT, new AgreementActivatedEventStrategy());
        strategies.put(AGREEMENT_ACTIVATED_EVENT_SIGNED, new AgreementActivatedEventSignedStrategy());

        strategies.put(AGREEMENT_UPGRADED, new AgreementUpgradedStrategy());
        strategies.put(AGREEMENT_UPGRADED_EVENT, new AgreementUpgradedEventStrategy());

        strategies.put(CLIENT_DELETED_EVENT, new ClientDeletedEventStrategy());

        strategies.put(KEY_DELETED_EVENT, new KeyDeletedEventStrategy());

        strategies.put(KEYS_ADDED_EVENT, new KeysAddedEventStrategy());

        strategies.put(VOUCHER_EVENT, new VoucherEventStrategy());

        strategies.put(CONSUMER_DELEGATION_APPROVED, new ConsumerDelegationApprovedStrategy());
        strategies.put(CONSUMER_DELEGATION_APPROVED_SIGNED, new ConsumerDelegationApprovedStrategy());

        strategies.put(CONSUMER_DELEGATION_APPROVED_EVENT, new ConsumerDelegationApprovedEventStrategy());
        strategies.put(CONSUMER_DELEGATION_APPROVED_EVENT_SIGNED, new ConsumerDelegationApprovedEventSignedStrategy());

        strategies.put(CONSUMER_DELEGATION_REVOKED_EVENT, new ConsumerDelegationRevokedEventStrategy());

        strategies.put(CONSUMER_DELEGATION_REVOKED, new ConsumerDelegationRevokedStrategy());

        strategies.put(PRODUCER_DELEGATION_APPROVED_EVENT, new ProducerDelegationApprovedEventStrategy());

        strategies.put(PRODUCER_DELEGATION_APPROVED, new ProducerDelegationApprovedStrategy());

        strategies.put(PRODUCER_DELEGATION_REVOKED_EVENT, new ProducerDelegationRevokedEventStrategy());

        strategies.put(PRODUCER_DELEGATION_REVOKED, new ProducerDelegationRevokedStrategy());

        strategies.put(DESCRIPTOR_ESERVICE_UPGRADED_EVENT, new DescriptorEserviceUpgradedEventStrategy());

        strategies.put(NEW_PURPOSE_VERSION_ACTIVATED, new NewPurposeVersionActivatedStrategy());

        strategies.put(PURPOSE_ACTIVATED, new PurposeActivatedStrategy());
        strategies.put(PURPOSE_ACTIVATED_SIGNED, new PurposeActivatedStrategy());

        strategies.put(PURPOSE_TEMPLATE_PUBLISHED, new PurposeTemplatePublishedStrategy());

        strategies.put(PURPOSE_UPGRADED_EVENT, new PurposeUpgradedEventStrategy());

        strategies.put(PURPOSE_VERSION_ACTIVATED, new PurposeVersionActivatedStrategy());
    }

    public boolean match(IFileMatcher.MatchingStrategySeed seed) throws IOException {
        IFileMatcher strategy = strategies.get(seed.getFileType());
        if(strategy == null) throw new RuntimeException("Unknown file type " + seed.getFileType());

        return strategy.match(seed);
    }
}

