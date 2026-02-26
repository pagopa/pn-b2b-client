package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketRole;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.BucketUrl;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;

public record FileLocation(
        BucketRole bucketRole,
        BucketUrl bucketUrl,
        FilenameFormat filenameFormat
) {

    public FileLocation resolve(TokenResolver resolver) {
        return new FileLocation(
                bucketRole,
                new BucketUrl(
                        bucketUrl.base(),
                        resolvePath(bucketUrl.prefix(), resolver),
                        resolvePath(bucketUrl.key(), resolver)
                ),
                filenameFormat
        );
    }

    private static String resolvePath(String path, TokenResolver resolver) {
        if (path == null || path.isBlank()) {
            return path;
        }

        return java.util.Arrays.stream(path.split("/"))
                .map(resolver::resolve)
                .collect(java.util.stream.Collectors.joining("/"));
    }
}

