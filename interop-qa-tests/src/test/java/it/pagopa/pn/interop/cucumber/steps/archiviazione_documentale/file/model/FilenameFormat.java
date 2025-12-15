package it.pagopa.pn.interop.cucumber.steps.archiviazione_documentale.file.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FilenameFormat {

    PDF_SIGNED_DOC(Pattern.compile("^(?:([0-9]{14})_)?INTEROP_([^.]+)(?:\\.[^.]+)+$"), true) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            return new FileNameParts(
                    m.group(1),
                    m.group(2),
                    extractExtension(filename)
            );
        }
    },

    PDF_DOC(Pattern.compile("^([0-9]{14})_([^.]+)(?:\\.[^.]+)+$"), true) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            return new FileNameParts(
                    m.group(1),
                    m.group(2),
                    extractExtension(filename)
            );
        }
    },

    EVENT_SIGNED_LOG(Pattern.compile("^INTEROP_([^.-]+-[0-9a-fA-F]{32}-signed)(?:\\.[^.]+)+$"), false) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            return new FileNameParts(
                    null,
                    m.group(1),
                    extractExtension(filename)
            );
        }
    },

    EVENT_LOG(Pattern.compile("^events_(\\d{8})_(\\d{6})_([0-9a-fA-F-]+)(?:\\.[^.]+)+$"), true) {
        @Override
        FileNameParts extract(Matcher m, String filename) {
            String timestamp = m.group(1) + m.group(2);
            String baseName = "events_" + m.group(1) + "_" + m.group(2) + "_" + m.group(3);

            return new FileNameParts(
                    timestamp,
                    baseName,
                    extractExtension(filename)
            );
        }
    };

    private final Pattern pattern;
    private final boolean hasTimestamp;

    FilenameFormat(Pattern pattern, boolean hasTimestamp) {
        this.pattern = pattern;
        this.hasTimestamp = hasTimestamp;
    }

    abstract FileNameParts extract(Matcher m, String filename);

    public FileNameParts match(String filename) {
        Matcher m = pattern.matcher(filename);
        return m.matches() ? extract(m, filename) : null;
    }

    public boolean hasTimestamp() {
        return hasTimestamp;
    }

    private static String extractExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx + 1) : null;
    }
}
