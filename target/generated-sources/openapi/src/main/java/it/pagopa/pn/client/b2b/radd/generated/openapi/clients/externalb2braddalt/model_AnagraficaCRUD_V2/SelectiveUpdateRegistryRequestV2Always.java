package it.pagopa.pn.client.b2b.radd.generated.openapi.clients.externalb2braddalt.model_AnagraficaCRUD_V2;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class SelectiveUpdateRegistryRequestV2Always extends SelectiveUpdateRegistryRequestV2 {
    public SelectiveUpdateRegistryRequestV2Always(SelectiveUpdateRegistryRequestV2 base) {
        this.setAddress(base.getAddress());
        this.setDescription(base.getDescription());
        this.setPhoneNumbers(base.getPhoneNumbers());
        this.setExternalCodes(base.getExternalCodes());
    }

    public SelectiveUpdateRegistryRequestV2Always(){};
}