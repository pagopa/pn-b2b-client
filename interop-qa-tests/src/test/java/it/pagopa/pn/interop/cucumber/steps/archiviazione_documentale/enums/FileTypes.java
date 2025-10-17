package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.enums;

public enum FileTypes {
    PDF("pdf"),
    ZIP("zip");

    private final String extension;

    FileTypes(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static FileTypes fromExtension(String ext) {
        for (FileTypes type : values()) {
            if (type.extension.equalsIgnoreCase(ext)) {
                return type;
            }
        }
        return null;
    }
}

