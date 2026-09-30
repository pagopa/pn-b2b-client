package it.pagopa.interop.purpose.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RemainingDailyCallsResponse;

import java.util.UUID;

public interface IM2MV3PurposeClient extends IM2MPurposeClient, SettableHeaders, Authenticable {

    RemainingDailyCallsResponse getRemainingDailyCalls(UUID purposeId);
}
