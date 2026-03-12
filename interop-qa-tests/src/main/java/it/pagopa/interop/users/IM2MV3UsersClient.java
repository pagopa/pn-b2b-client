package it.pagopa.interop.users;

import it.pagopa.interop.authorization.service.utils.Authenticable;
import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.common.SettableHttpCallExecutor;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.User;
import it.pagopa.interop.generated.openapi.clients.m2mGatewayV3.model.Users;
import java.util.List;
import java.util.UUID;

public interface IM2MV3UsersClient extends Authenticable, SettableHttpCallExecutor {
	User getUser(UUID userId);

	Users getUsers(Integer limit, Integer offset, List<String> roles);
}