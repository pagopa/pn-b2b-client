package it.pagopa.pn.interop.cucumber.steps.m2m;


/* 13/08/2025 esempio di utilizzo: se un oggetto di tipo EServiceDelegationPatchOperationsAssistant
* effettua un'op. di patch, usando un contesto condiviso si ha che anche
* EServicePatchOperationsAssistant sarà in grado di effettuare un confronto tra valore atteso
* e valore restituito (visto che entrambi farebbero il check sull'entità 'EService'), permettendo
* di non dover specificare altri step specifici per l'op. di PATCH della delega,
* che farebbero sostanzialmente la stessa cosa */
public interface ResourceContext<RESOURCE> {
    RESOURCE getOriginalResource();
    void setOriginalResource(RESOURCE resource);

    RESOURCE getExpectedResource();
    void setExpectedResource(RESOURCE resource);

    RESOURCE getReturnedResource();
    void setReturnedResource(RESOURCE resource);
}