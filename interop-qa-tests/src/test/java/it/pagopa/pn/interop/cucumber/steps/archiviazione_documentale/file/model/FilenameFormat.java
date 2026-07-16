package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FilenameFormat {

    NDJSON_LOG(Pattern.compile("^([0-9]{8})_([0-9]{6})_([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})\\.ndjson$")) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            return new FileNameParts(
                    extractExtension(filename)
            );
        }
    },

    PDF_SIGNED_DOC(Pattern.compile("^INTEROP_([A-Z_]+)-([a-f0-9]{32})-signed\\.pdf$")) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            return new FileNameParts(
                    extractExtension(filename)
            );
        }
    },

    PDF_DOC(Pattern.compile("^([0-9]{14})_([^.]+)(?:\\.[^.]+)+$")) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            return new FileNameParts(
                    extractExtension(filename)
            );
        }
    },

    AGREEMENT_CONTRACT_PDF(Pattern.compile("^([0-9a-fA-F-]{36})_([0-9a-fA-F-]{36})_(\\d{14})_([a-zA-Z0-9_]+)(?:\\.[^.]+)+$")) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            return new FileNameParts(
                    extractExtension(filename)  // pdf
            );
        }
    },

    EVENT_SIGNED_LOG(Pattern.compile("^INTEROP_([^.-]+-[0-9a-fA-F]{32}-signed)(?:\\.[^.]+)+$")) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            return new FileNameParts(
                    extractExtension(filename)
            );
        }
    },

    EVENT_LOG(Pattern.compile("^events_(\\d{8})_(\\d{6})_([0-9a-fA-F-]+)(?:\\.[^.]+)+$")) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            return new FileNameParts(
                    extractExtension(filename)
            );
        }
    };

    private final Pattern pattern;


    FilenameFormat(Pattern pattern) {
        this.pattern = pattern;
    }

    abstract FileNameParts extract(Matcher m, String filename);

    public FileNameParts match(String filename) {
        Matcher m = pattern.matcher(filename);
        return m.matches() ? extract(m, filename) : null;
    }

    private static String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx + 1) : null;
    }
}
