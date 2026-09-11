package it.pagopa.interop.eservice.service;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableHeaders;
import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.EService;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

public interface IM2MV3EserviceClient extends IM2MEserviceClient, SettableHeaders, Authenticable {

	@Data
	@Builder
	class DelegatedEServiceArchivingRequest {
		private String archivingReason;
		private Integer gracePeriodDays;
	}

	EService submitDelegatedEServiceArchiving(UUID eServiceId, DelegatedEServiceArchivingRequest body);

	EService cancelDelegatedEServiceArchiving(UUID eServiceId);

	EService approveDelegatedEServiceArchiving(UUID eServiceId);

	EService rejectDelegatedEServiceArchiving(UUID eServiceId, String rejectionReason);

}
