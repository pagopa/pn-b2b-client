package it.pagopa.pn.interop.cucumber.steps.m2m.common;

import it.pagopa.interop.common.enums.AssertCheckType;
import it.pagopa.interop.common.enums.EntityIdType;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface ICommonSteps {
    void verifyByHttpStatus(int expectedStatusCode);
    void getBy(Map<String,String> filters);
    void getAll();
    void getPage(int page, int size);
    void getByFirstExpectedId();
    void getByIdType(EntityIdType entityIdType);
    void exsist(String presence);
    // TODO: valutare l'aggiunta di un metodo getAll(Map params) in modo da soddisfare anche altri casi (vedi EserviceDescriptorStep)
}
