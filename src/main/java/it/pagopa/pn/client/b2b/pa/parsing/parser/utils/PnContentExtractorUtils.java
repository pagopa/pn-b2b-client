package it.pagopa.pn.client.b2b.pa.parsing.parser.utils;

import it.pagopa.pn.client.b2b.pa.parsing.config.PnLegalFactTokenProperty;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class PnContentExtractorUtils {
    private PnContentExtractorUtils() {}

    public static String getFieldByToken(PnTextSlidingWindow pnTextSlidingWindow, String value) {
        if(isValueBetweenTokens(pnTextSlidingWindow.getSlidedText(), value, pnTextSlidingWindow.getTokenStart(), pnTextSlidingWindow.getTokenEnd())) {
            return value;
        }
        return null;
    }

    public static boolean isValueBetweenTokens(String text, String value, String tokenStart, String tokenEnd) {
        String checkingLeft = extractAdjacentWordByValue(text, value, tokenStart, true);
        String checkingRight = extractAdjacentWordByValue(text, value, tokenEnd, false);
        if(checkingLeft != null || checkingRight != null)
            return isExactlyContained(checkingLeft, tokenStart) && isExactlyContained(checkingRight, tokenEnd);
        return false;
    }

    public static String extractAdjacentWordByValue(String text, String value, String token, boolean isLeft) {
        int wordCount = countTokenWords(token);
        String regex = "\\s+";
        int position = text.indexOf(value);
        if(position == -1)
            return null;
        StringBuilder stringBuilder = new StringBuilder();

        if(isLeft) {
            String substring = text.substring(0, position);
            String[] words = substring.split(regex);
            for (int i = Math.max(0, words.length - wordCount * 2); i < words.length; i++) {
                stringBuilder.append(words[i]);
                stringBuilder.append(" ");
            }
        } else {
            String substring = text.substring(position+value.length());
            String[] words = substring.split(regex);
            for (int i = 0; i < Math.min(wordCount * 2, words.length); i++) {
                stringBuilder.append(words[i]);
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString().trim();
    }

    public static boolean isExactlyContained(String source, String subItem) {
        String quotedSubItem = Pattern.quote(subItem);
        String pattern = "(?<!\\S)" + quotedSubItem + "(?!\\S)";

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

    public static int countTokenWords(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0;
        }
        String[] words = input.trim().split("\\s+");
        return words.length;
    }

    public static List<Integer> concatenateValue(PnTextSlidingWindow pnTextSlidingWindow, String value, List<String> valueList, PnLegalFactTokenProperty tokenProps) {
        String text = pnTextSlidingWindow.getSlidedText();
        String tokenStart = pnTextSlidingWindow.getTokenStart();
        String tokenEnd = pnTextSlidingWindow.getTokenEnd();

        // Trovare l'indice posizionale di tokenStart, value e tokenEnd
        int pos1 = customIndexOf(text, tokenStart, 0);
        int indexValue = text.indexOf(value);
        if (pos1 == -1 || (pos1 > indexValue))
            return Collections.emptyList();

        int pos2 = text.indexOf(value, pos1 + tokenStart.length());
        if (pos2 == -1 || !text.substring(pos1 + tokenStart.length() + countControlCharacters(text.substring(pos1, pos2)), pos2).trim().isEmpty())
            return Collections.emptyList(); // Non vi sono parole tra tokenStart e value

        int pos3 = customIndexOf(text, tokenEnd, pos2 + value.length());
        if (pos3 == -1)
            return Collections.emptyList(); // Non vi sono parole tra value e tokenEnd

        // Controlla se vi sono parole tra il value ed il tokenEnd
        String betweenText = text.substring(pos2 + value.length(), pos3).trim();
        if (!betweenText.isEmpty()) {
            List<Integer> brokenValueList = cleanUpByBrokenValues(betweenText, valueList, tokenProps);
            if(!brokenValueList.isEmpty()) {
                int substituteIndex = brokenValueList.remove(0);
                String concatenatedValue = value.trim() + "\r\n" + betweenText;
                valueList.set(substituteIndex, concatenatedValue);
                brokenValueList.sort(Collections.reverseOrder());
                brokenValueList.forEach(brokenValue -> valueList.remove(brokenValue.intValue()));
            }
            return brokenValueList;
        }
        return Collections.emptyList();
    }

    public static int customIndexOf(String source, String toFind, int fromIndex) {
        if (fromIndex < 0 || fromIndex >= source.length()) {
            return -1;
        }
        // Normalizzazione delle stringhe da cui iniziare la ricerca
        String sourceFromIndex = source.substring(fromIndex);
        String normalizedSourceFromIndex = sourceFromIndex.replaceAll("[\r\n\\s]+", "");
        String normalizedToFind = toFind.replaceAll("[\r\n\\s]+", "");

        int indexInNormalized = normalizedSourceFromIndex.indexOf(normalizedToFind);
        if (indexInNormalized == -1) {
            return -1;
        }
        // Calcolo della posizione reale nella stringa originale
        int sourcePos = fromIndex;
        int matchPos = 0;

        while (sourcePos < source.length() && matchPos < indexInNormalized) {
            char currentChar = source.charAt(sourcePos);
            if (!Character.isWhitespace(currentChar) && currentChar != '\r' && currentChar != '\n') {
                matchPos++;
            }
            sourcePos++;
        }
        return sourcePos+1;
    }

    public static int countControlCharacters(String input) {
        if (input == null) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            if (Character.isISOControl(input.charAt(i))) {
                count++;
            }
        }
        return count;
    }

    public static int countDates(String text) {
        String DATE_PATTERN = "\\b\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}\\b";
        Pattern pattern = Pattern.compile(DATE_PATTERN);
        Matcher matcher = pattern.matcher(text);

        int cnt = 0;
        while (matcher.find()) {
            cnt++;
        }

        return cnt;
    }

    public static List<Integer> cleanUpByBrokenValues(String betweenText, List<String> valueList, PnLegalFactTokenProperty tokenProps) {
        List<Integer> brokenValueList = new ArrayList<>();
        String[] splitted = betweenText.split(tokenProps.getRegexCarriageNewline());
        int posToRemove = valueList.indexOf(splitted[0]);
        if (posToRemove != -1)
            brokenValueList.add(posToRemove-1);

        for(String valSplit: splitted) {
            posToRemove = valueList.indexOf(valSplit.trim());
            if (posToRemove != -1)
                brokenValueList.add(posToRemove);
        }
        return brokenValueList;
    }

    public static String cleanUpText(String text, PnLegalFactTokenProperty tokenProps) {
        String cleanedText = text.replaceAll(tokenProps.getRegexCleanupNsbp(), "");
        cleanedText = removeHash(cleanedText);
        cleanedText = normalizeLineEndings(cleanedText);
        return cleanedText.trim();
    }

    public static List<String> cleanUpList(List<String> boldValueList, PnLegalFactTokenProperty tokenProps) {
        List<String> cleanedList = removeUselessValues(boldValueList, Arrays.asList(tokenProps.getCleanupDestinatario(), tokenProps.getCleanupDelegato()));
        return removeHash(cleanedList);
    }

    public static String normalizeLineEndings(String text) {
        // Sostituisce tutte le occorrenze di '\r\n' e '\n' con '\n' temporaneamente
        text = text.replace("\r\n", "\n").replace("\r", "\n");
        // Suddivide il testo in righe
        String[] lines = text.split("\n");
        // Unisce le righe con '\r\n'
        StringBuilder normalizedText = new StringBuilder();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                normalizedText.append(line.trim()).append("\r\n"); // Rimuove spazi finali e aggiunge '\r\n'
            }
        }
        return normalizedText.toString();
    }

    public static List<String> removeUselessValues(List<String> boldValueList, List<String> toRemoveValue) {
        List<String> cleanedList = new ArrayList<>(boldValueList);
        cleanedList.removeIf(toRemoveValue::contains);
        return cleanedList;
    }

    public static String removeHash(String text) {
        // Regex per trovare stringhe di 64 o 128 caratteri
        String regex = "\\b[a-fA-F0-9]{64}\\b|\\b[a-fA-F0-9]{128}\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        // Sostituisce tutte le occorrenze che corrispondono alla regex con una stringa vuota
       return matcher.replaceAll("");
    }

    public static List<String> removeHash(List<String> valueList) {
        List<String> cleanedList = new ArrayList<>(valueList);
        // Regex per trovare stringhe di 64 o 128 caratteri
        String regex = "\\b[a-fA-F0-9]{64}\\b|\\b[a-fA-F0-9]{128}\\b";
        // Rimuove gli elementi dalla lista che corrispondono alla regex
        cleanedList.removeIf(hash -> hash.matches(regex));
        return cleanedList;
    }

    public static int countDuplicates(String text, String toSearch) {
        if (toSearch.isEmpty()) {
            return 0;
        }
        int count = 0;
        int fromIndex = 0;
        while ((fromIndex = text.indexOf(toSearch, fromIndex)) != -1) {
            count++;
            fromIndex += toSearch.length();
        }
        return count;
    }
}