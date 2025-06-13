package it.pagopa.interop.common.client;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.common.enums.EntityIdType;
import it.pagopa.interop.utils.HttpCallExecutor;

import java.util.List;

public interface IClient<E,K> extends SettableBearerToken {
   E get(K id);
   List<E> getAll();

   K getId(E entity);
   K generateId(EntityIdType entityIdType);
   void setHttpCallExecutor(HttpCallExecutor httpCallExecutor);
}
