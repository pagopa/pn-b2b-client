package it.pagopa.interop.purpose.service.mapper;

import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersion;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

// TODO utile in fase di sviluppo, superfluo una volta a regime; considerarne rimozione una volta stabilizzata la feature Evol. nuovi operatori.
class PurposeUpdateMapperTest {
    private final PurposeUpdateMapper mapper = Mappers.getMapper(PurposeUpdateMapper.class);

    @Test
    void copiesMetadataWithoutResendingRiskAnalysis() {
        Purpose purpose = new Purpose().title("Titolo originale").description("Descrizione")
                .isFreeOfCharge(false).freeOfChargeReason("Motivazione")
                .currentVersion(new PurposeVersion().dailyCalls(42))
                .riskAnalysisForm(new RiskAnalysisForm().version("1.0"));

        var payload = mapper.toUpdateContent(purpose);

        assertThat(payload.getTitle()).isEqualTo(purpose.getTitle());
        assertThat(payload.getDescription()).isEqualTo(purpose.getDescription());
        assertThat(payload.getIsFreeOfCharge()).isFalse();
        assertThat(payload.getFreeOfChargeReason()).isEqualTo(purpose.getFreeOfChargeReason());
        assertThat(payload.getDailyCalls()).isEqualTo(42);
        assertThat(payload.getRiskAnalysisForm()).isNull();
        payload.setTitle("Titolo modificato");
        assertThat(purpose.getTitle()).isEqualTo("Titolo originale");
    }

    @Test
    void acceptsAbsentOptionalValues() {
        var payload = mapper.toUpdateContent(new Purpose().title("Titolo").description("Descrizione")
                .isFreeOfCharge(true).currentVersion(new PurposeVersion().dailyCalls(1)));

        assertThat(payload.getFreeOfChargeReason()).isNull();
        assertThat(payload.getDailyCalls()).isEqualTo(1);
    }
}
