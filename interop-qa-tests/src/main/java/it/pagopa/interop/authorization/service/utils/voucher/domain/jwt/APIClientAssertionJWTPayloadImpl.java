package it.pagopa.interop.authorization.service.utils.voucher.domain.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class APIClientAssertionJWTPayloadImpl extends ClientAssertionJWTPayloadImpl {

}
