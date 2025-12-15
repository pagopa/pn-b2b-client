package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.BucketRole;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.FileValidator;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record FileInfo(InteropFile type, FileValidator validation, Set<FileLocation> locations) {
    public Optional<FileLocation> locationFor(BucketRole role) {

        List<FileLocation> matches = locations.stream()
                .filter(l -> l.bucketRole() == role)
                .toList();

        if (matches.size() > 1) {
            throw new IllegalStateException(
                    "More than one DocumentLocation found for bucketRole " + role +
                            " in DocumentType " + type
            );
        }

        return matches.stream().findFirst();
    }
}
