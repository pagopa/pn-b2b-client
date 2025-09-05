package it.pagopa.pn.interop.cucumber.steps.m2m.eservice.mapper;

import it.pagopa.interop.generated.openapi.clients.m2mGateway.model.Document;
import it.pagopa.pn.interop.cucumber.steps.DocumentMetadata;
import java.time.OffsetDateTime;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class DocumentMapper {
    public abstract DocumentMetadata map(Document document);
    public abstract List<DocumentMetadata> map(List<Document> document);

    protected OffsetDateTime map(String value) {
        return OffsetDateTime.parse(value);
    }
}
