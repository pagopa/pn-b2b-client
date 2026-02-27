package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model;

public record FileNameParts(String extension) {
    public static FileNameParts parse(String filename) {
        if (filename == null)
            throw new IllegalArgumentException("filenameFormat is null");

        for (FilenameFormat format : FilenameFormat.values()) {
            FileNameParts parts = format.match(filename);
            if (parts != null) return parts;
        }

        return null;
    }
}
