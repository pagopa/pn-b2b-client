package it.pagopa.pn.interop.cucumber.steps;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Document {
    private DocumentMetadata metadata;
    private Resource resource;

    /** Constructs the {@link Document} instance, prioritizing the information contained in
     * {@link Resource}. Ex: the "name" field of {@link DocumentMetadata} is filled with the
     * name of the <i>resource</i> object. */
    public static Document of(DocumentMetadata metadata, Resource resource) {
        Document document = new Document(metadata, resource);
        document.getMetadata().setName(resource.getFilename());
        return document;
    }
}
