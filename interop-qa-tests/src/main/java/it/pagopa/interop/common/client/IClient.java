package it.pagopa.interop.common.client;

import it.pagopa.interop.authorization.service.utils.SettableBearerToken;
import it.pagopa.interop.common.IHttpExecutor;
import it.pagopa.interop.common.enums.EntityIdType;

import java.util.List;

public interface IClient<E, K> extends SettableBearerToken {
    E get(K id);

    List<E> getAll();

    List<E> getPage(int page, int size);

    K getId(E entity);

    K generateId(EntityIdType entityIdType);

    void setHttpCallExecutor(IHttpExecutor httpCallExecutor);
}
