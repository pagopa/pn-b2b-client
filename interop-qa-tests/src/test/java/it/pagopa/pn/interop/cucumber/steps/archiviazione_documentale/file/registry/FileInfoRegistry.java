package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.registry;

import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.client.model.S3BucketInfoBuilder;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums.InteropFile;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.FileInfo;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model.FileLocation;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.registry.model.FileInfoDefinition;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.validator.FileValidator;
import it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.utils.TokenResolver;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.registry.model.Definitions.definitions;


public class FileInfoRegistry {

    private final Map<InteropFile, FileInfo> registry;
    private final TokenResolver tokenResolver;

    public FileInfoRegistry(TokenResolver tokenResolver, String documentBucketBase, String documentWormBucketBase,
                            String eventBucketBase, String eventWormBucketBase,
                            String jwtDetailsBucketBase, String jwtDetailsSignedBucketBase) {
        this.tokenResolver = tokenResolver;

        this.registry = definitions(documentBucketBase, documentWormBucketBase,
                    eventBucketBase, eventWormBucketBase,
                    jwtDetailsBucketBase, jwtDetailsSignedBucketBase)
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        FileInfoDefinition::type,
                        this::buildFileInfo
                ));
    }

    public FileInfo getFileInfo(InteropFile file) {
        FileInfo template = registry.get(file);
        if (template == null) {
            throw new IllegalArgumentException("File info non trovata per: " + file);
        }

        Set<FileLocation> resolvedLocations = template.locations().stream()
                .map(location -> location.resolve(tokenResolver))
                .collect(java.util.stream.Collectors.toUnmodifiableSet());

        return new FileInfo(
                template.type(),
                template.validation(),
                resolvedLocations
        );
    }

    private FileInfo buildFileInfo(FileInfoDefinition def) {

        FileValidator validator = new FileValidator(
                tokenResolver,
                def.required(),
                def.optional()
        );

        Set<FileLocation> locations = def.locations().stream()
                .map(ld -> new FileLocation(
                        ld.role(),
                        S3BucketInfoBuilder.builder()
                                .fullPath(ld.bucketBase())
                                .build(),
                        ld.format()
                ))
                .collect(Collectors.toUnmodifiableSet());

        return new FileInfo(def.type(), validator, locations);
    }
}
