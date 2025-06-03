package it.pagopa.pn.interop.cucumber.steps.datapreparationservice.template;

import it.pagopa.interop.authorization.service.utils.PollingService;
import it.pagopa.interop.utils.HttpCallExecutor;
import it.pagopa.pn.interop.cucumber.utility.CommonUtils;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class DataPreparationServiceTemplate {
    private final PollingService pollingService;
    private final HttpCallExecutor httpCallExecutor;
    private final CommonUtils commonUtils;
    public static final String ERROR_RETRIEVING_AGREEMENT = "There was an error while retrieving the agreement by ID!";

    public DataPreparationServiceTemplate(
                                  HttpCallExecutor httpCallExecutor,
                                  PollingService pollingService,
                                  CommonUtils commonUtils) {
        this.httpCallExecutor = httpCallExecutor;
        this.pollingService = pollingService;
        this.commonUtils = commonUtils;
    }

    public Optional<UUID> createAgreement(CreateAgreementOperation operation) {
        httpCallExecutor.performCall(operation.getApiCaller());
        UUID result = operation.getResultExtractor().apply(httpCallExecutor.getResponse());
        return httpCallExecutor.getClientResponse().is2xxSuccessful()
            ? Optional.of(result)
            : Optional.empty();
    }

    public UUID createAndCheckAgreement(CreateAndCheckAgreementOperation operation) {
        UUID agreementId = createAgreement(operation.getCreateOperation()).orElseThrow(
            () -> new NoSuchElementException("Failed to create an agreement: result of agreement creation API is '%s'".formatted(httpCallExecutor.getClientResponse())));
        assertValidResponse();
        pollingService.makePolling(
            () ->  httpCallExecutor.performCall(() -> operation.getCheckerApiCaller().apply(agreementId)),
            res -> res != HttpStatus.NOT_FOUND,
            ERROR_RETRIEVING_AGREEMENT
        );
        return agreementId;
    }

    public void submitAgreement(SubmitAgreementOperation operation, UpperAgreementState expectedState) {
        pollingService.makePolling(
                () -> httpCallExecutor.performCall(operation.getApiCaller()),
            HttpStatus::is2xxSuccessful,
                "There was an error while submitting the agreement!"
        );

        assertValidResponse();
        pollingService.makePolling(
            operation.getCheckerApiCaller(),
                res -> operation.getStateExtractor().apply(res) == expectedState,
                ERROR_RETRIEVING_AGREEMENT
        );
    }

    private void assertValidResponse() {
        commonUtils.assertValidResponse();
    }

}
