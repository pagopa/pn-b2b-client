package it.pagopa.interop.purpose.service.mapper;

import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeUpdateContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Conversioni dei dati della finalità nei payload di aggiornamento BFF. */
@Mapper(componentModel = "spring")
public interface PurposeUpdateMapper {

    /**
     * Copia i dati modificabili, ricavando il carico dalla versione corrente.
     * Non reinvia il form RA: il payload è destinato a modificare i metadati,
     * anche quando il workflow non consente modifiche al form (es. SUBMITTED).
     */
    @Mapping(target = "dailyCalls", source = "currentVersion.dailyCalls")
    @Mapping(target = "riskAnalysisForm", ignore = true)
    PurposeUpdateContent toUpdateContent(Purpose purpose);
}
