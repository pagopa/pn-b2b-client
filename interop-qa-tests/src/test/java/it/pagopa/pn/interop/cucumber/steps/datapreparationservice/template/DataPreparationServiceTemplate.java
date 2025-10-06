package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import it.pagopa.interop.agreement.domain.ClientType;
import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.operation.IOperation;
import it.pagopa.interop.common.operation.SimpleOperation;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.AddConsumerDocumentOperation.AddConsumerDocumentParams;
import it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template.CreateAgreementOperation.CreateAgreementParams;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.annotation.Nullable;
import java.io.File;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
public class DataPreparationServiceTemplate {
    private final PollingService pollingService;
    private final IHttpExecutor httpCallExecutor;
    private final CommonUtils commonUtils;
    public static final String ERROR_RETRIEVING_AGREEMENT = "There was an error while retrieving the agreement by ID!";

    public DataPreparationServiceTemplate(
            IHttpExecutor httpCallExecutor,
            PollingService pollingService,
            CommonUtils commonUtils) {
        this.httpCallExecutor = httpCallExecutor;
        this.pollingService = pollingService;
        this.commonUtils = commonUtils;
    }

    public Optional<UUID> createAgreement(CreateAgreementOperation operation, UUID eServiceID, UUID descriptorId, @Nullable UUID delegationId) {
        return performOperation(SimpleOperation.of(
                () -> operation.getApiCaller().apply(CreateAgreementParams.of(eServiceID, descriptorId, delegationId)),
                Function.identity()
        ));
    }

    public UUID createAndCheckAgreement(CreateAndCheckAgreementOperation operation, UUID eServiceID, UUID descriptorId, UUID delegationId) {
        UUID agreementId = createAgreement(operation.getCreateOperation(), eServiceID, descriptorId, delegationId).orElseThrow(
                () -> new NoSuchElementException("Failed to create an agreement: result of agreement creation API is '%s'".formatted(httpCallExecutor.getResponseStatus())));
        assertValidResponse();
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> operation.getCheckerApiCaller().apply(agreementId)),
                res -> res != HttpStatus.NOT_FOUND,
                ERROR_RETRIEVING_AGREEMENT
        );
        return agreementId;
    }

    public void submitAgreement(SubmitAgreementOperation operation, UUID agreementId, UpperAgreementState expectedState) {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(() -> operation.getApiCaller().apply(agreementId)),
                HttpStatus::is2xxSuccessful,
                "There was an error while submitting the agreement!"
        );

        assertValidResponse();
        pollingService.makePolling(
                () -> operation.getCheckerApiCaller().apply(agreementId),
                res -> res.getState() == expectedState,
                ERROR_RETRIEVING_AGREEMENT
        );
    }

    public UUID createAgreementWithGivenState(CreateAgreementWithStateOperation op, UpperAgreementState agreementState, UUID eServiceID, UUID descriptorId, UUID delegationId, File doc) {
        // agreement in state DRAFT
        UUID agreementId = createAndCheckAgreement(op.getCreateAndCheckAgreementOperation(), eServiceID, descriptorId, delegationId);
        if (doc != null) addConsumerDocumentToAgreement(op.getAddConsumerDocumentOperation(), agreementId, doc);
        return switch (agreementState) {
            case DRAFT -> agreementId;
            case PENDING, ACTIVE -> {
                submitAgreement(op.getSubmitAgreementOperation(), agreementId, agreementState);
                yield agreementId;
            }
            case SUSPENDED -> {
                submitAgreement(op.getSubmitAgreementOperation(), agreementId, UpperAgreementState.ACTIVE);
                suspendAgreement(op.getSuspendAgreementOperation(), agreementId, ClientType.CONSUMER);
                yield agreementId;
            }
            case ARCHIVED -> {
                submitAgreement(op.getSubmitAgreementOperation(), agreementId, UpperAgreementState.ACTIVE);
                suspendAgreement(op.getSuspendAgreementOperation(), agreementId, ClientType.CONSUMER);
                archiveAgreement(op.getArchiveAgreementOperation(), agreementId);
                yield agreementId;
            }
            default -> throw new IllegalArgumentException("Unsupported AgreementState: " + agreementState);
        };
    }

    public void addConsumerDocumentToAgreement(AddConsumerDocumentOperation op, UUID agreementId, File doc) {
        httpCallExecutor.performCall(() -> op.getApiCaller().apply(
                AddConsumerDocumentParams.of(agreementId, doc)));
        pollingService.makePolling(
                () -> op.getCheckerApiCaller().apply(agreementId),
                res -> !op.getDocumentListExtractor().apply(res).isEmpty(),
                ERROR_RETRIEVING_AGREEMENT
        );
    }

    public void suspendAgreement(SuspendAgreementOperation op, UUID agreementId, ClientType suspendedBy) {
        httpCallExecutor.performCall(() -> op.getApiCaller().apply(agreementId));
        assertValidResponse();
        pollingService.makePolling(
                () -> op.getCheckerApiCaller().apply(agreementId),
                agreement -> agreement.getState().equals(UpperAgreementState.SUSPENDED)
                        && ClientType.PRODUCER.equals(suspendedBy) ? agreement.isSuspendedByProducer()
                        : agreement.isSuspendedByConsumer(),
                ERROR_RETRIEVING_AGREEMENT
        );
    }

    public void archiveAgreement(ArchiveAgreementOperation op, UUID agreementId) {
        httpCallExecutor.performCall(() -> op.getApiCaller().accept(agreementId));
        assertValidResponse();
        pollingService.makePolling(
                () -> op.getCheckerApiCaller().apply(agreementId),
                res -> res.getState() == UpperAgreementState.ARCHIVED,
                ERROR_RETRIEVING_AGREEMENT
        );
    }

    public Optional<UUID> createAttribute(AttributeOperation operation) {
        return performOperation(operation);
    }

    private void assertValidResponse() {
        commonUtils.assertValidResponse();
    }

    public <T, R> Optional<R> performOperation(IOperation<T, R> operation) {
        // Esegue la chiamata HTTP
        httpCallExecutor.performCall(operation.getApiCaller());

        // Recupera la risposta e l'esito della chiamata (cast se necessario)
        @SuppressWarnings("unchecked")
        T rawResponse = (T) httpCallExecutor.getResponse();
        var response = httpCallExecutor.getResponseStatus();

        // Se la risposta è positiva, estrae e restituisce il risultato
        if (response.is2xxSuccessful()) {
            return Optional.ofNullable(operation.getResultExtractor().apply(rawResponse));
        }

        // In caso di errore, loggare o gestire in altro modo se necessario
        log.warn("HTTP call failed with status: {}", response.value());
        return Optional.empty();
    }

}
