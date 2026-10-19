package it.pagopa.interop.purpose.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.RemainingDailyCallsResponse;

import java.util.UUID;

public interface IM2MV3PurposeClient extends IM2MPurposeClient, SettableHeaders, Authenticable {

    RemainingDailyCallsResponse getRemainingDailyCalls(UUID purposeId);

    void assignRiskAnalysis(UUID purposeId, RiskAnalysisAssignmentRequest payload);

    /* V3 restituisce campi che V2 non restituisce, da qui l'esigenza di avere un metodo separato da quello di V2 ereditato. */
    /** Restituisce la finalità con i campi del contratto V3, senza conversione in V2. */
    it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Purpose getPurposeV3(UUID purposeId);
}
