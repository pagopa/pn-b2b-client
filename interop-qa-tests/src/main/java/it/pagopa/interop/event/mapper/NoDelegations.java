package it.pagopa.interop.event.mapper;

import org.mapstruct.Mapping;

@Mapping(target = "producerDelegationId", ignore = true)
@Mapping(target = "consumerDelegationId", ignore = true)
public @interface NoDelegations {

}
