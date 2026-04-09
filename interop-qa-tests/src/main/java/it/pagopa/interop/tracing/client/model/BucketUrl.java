package it.pagopa.interop.tracing.client.model;

public record BucketUrl(String base, String prefix, String key) {

    public String fullPath() {
        StringBuilder sb = new StringBuilder();

        if (base != null && !base.isBlank()) {
            sb.append(trimTrailingSlash(base));
        }

        if (prefix != null && !prefix.isBlank()) {
            sb.append("/").append(trimSlashes(prefix));
        }

        if (key != null && !key.isBlank() && !key.equals(prefix)) {
            sb.append("/").append(trimLeadingSlash(key));
        }

        return sb.toString();
    }

    private static String trimTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private static String trimLeadingSlash(String value) {
        return value.startsWith("/") ? value.substring(1) : value;
    }

    private static String trimSlashes(String value) {
        return trimTrailingSlash(trimLeadingSlash(value));
    }
}
