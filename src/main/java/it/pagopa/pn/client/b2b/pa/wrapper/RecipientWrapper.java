package it.pagopa.pn.client.b2b.pa.wrapper;

import it.pagopa.pn.client.b2b.generated.openapi.clients.external.generate.model.external.bff.recipient.digitaladdresses.BffUserAddress;
import it.pagopa.pn.client.b2b.pa.generated.openapi.clients.internaladdressbook.model.UserAddresses;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class RecipientWrapper {

   @Getter
   @Setter
   private UserAddresses b2bUserAddress;

   @Getter
   @Setter
   private List<BffUserAddress> bffUserAddress;



}
