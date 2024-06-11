package it.pagopa.pn.client.b2b.pa.parsing.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PnCorePatterns {
    //    private static final String REGEX_CLEANUP_MATCHER_PATTERN = "[^*\\s]\\r\\n";
    private static final String REGEX_CLEANUP_MATCHER_PATTERN = "[^-\\s]\\r\\n";
    private static final String STRING_CLEANUP_PATTERN = " \r\n";
    //    private static final String REGEX_CLEANUP_RETURN_NEWLINE = "\\s\\r\\n";
    private static final String REGEX_CLEANUP_RETURN_NEWLINE = "\\r\\n";
    private static final String REGEX_CLEANUP_NSBP = "\\u00a0";
    private static final String REGEX_CLEANUP_DOUBLE_WHITESPACE = "\\s{2,}";


    protected PnCorePatterns() {}

    protected static String extractPattern(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    protected static String extractDateTimePattern(Pattern pattern, String inputText) {
        Matcher matcher = pattern.matcher(inputText);
        if (matcher.find()) {
            return matcher.group(1).trim() + " " + matcher.group(2).trim();
        }
        return null;
    }

    protected static List<String> extractPatternList(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        List<String> occurencies = new ArrayList<>();
        while (matcher.find()) {
            occurencies.add(matcher.group(1).trim());
        }
        return occurencies;
    }

    protected static String formatAndCleanUp(String text) {
        Matcher matcher = Pattern.compile(REGEX_CLEANUP_MATCHER_PATTERN).matcher(text);
        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find()){
            String group = matcher.group(0);
            matcher.appendReplacement(stringBuilder, group.charAt(0) + STRING_CLEANUP_PATTERN);
        }
        matcher.appendTail(stringBuilder);
        String newText = stringBuilder.toString();
        newText = newText.replaceAll(REGEX_CLEANUP_RETURN_NEWLINE, "");
        newText = newText.replaceAll(REGEX_CLEANUP_NSBP,"");
        return newText.replaceAll(REGEX_CLEANUP_DOUBLE_WHITESPACE," ").trim();
    }
}