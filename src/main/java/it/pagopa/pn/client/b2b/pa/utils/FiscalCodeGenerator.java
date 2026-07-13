package it.pagopa.pn.client.b2b.pa.utils;

import java.util.HashMap;
import java.util.Map;

public class FiscalCodeGenerator {
    private static final Map<Character, Integer> ALPHA_DISPARI_MAP = new HashMap<>();
    private static final Map<Character, Integer> ALPHA_PARI_MAP = new HashMap<>();
    private static final char[] HEX_TO_LETTER = {'B', 'C', 'D', 'F', 'G', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'Z', 'A'};
    private static final char[] caratteriLista = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};


    static {
        ALPHA_DISPARI_MAP.put('0', 1);
        ALPHA_DISPARI_MAP.put('1', 0);
        ALPHA_DISPARI_MAP.put('2', 5);
        ALPHA_DISPARI_MAP.put('3', 7);
        ALPHA_DISPARI_MAP.put('4', 9);
        ALPHA_DISPARI_MAP.put('5', 13);
        ALPHA_DISPARI_MAP.put('6', 15);
        ALPHA_DISPARI_MAP.put('7', 17);
        ALPHA_DISPARI_MAP.put('8', 19);
        ALPHA_DISPARI_MAP.put('9', 21);
        ALPHA_DISPARI_MAP.put('A', 1);
        ALPHA_DISPARI_MAP.put('B', 0);
        ALPHA_DISPARI_MAP.put('C', 5);
        ALPHA_DISPARI_MAP.put('D', 7);
        ALPHA_DISPARI_MAP.put('E', 9);
        ALPHA_DISPARI_MAP.put('F', 13);
        ALPHA_DISPARI_MAP.put('G', 15);
        ALPHA_DISPARI_MAP.put('H', 17);
        ALPHA_DISPARI_MAP.put('I', 19);
        ALPHA_DISPARI_MAP.put('J', 21);
        ALPHA_DISPARI_MAP.put('K', 2);
        ALPHA_DISPARI_MAP.put('L', 4);
        ALPHA_DISPARI_MAP.put('M', 18);
        ALPHA_DISPARI_MAP.put('N', 20);
        ALPHA_DISPARI_MAP.put('O', 11);
        ALPHA_DISPARI_MAP.put('P', 3);
        ALPHA_DISPARI_MAP.put('Q', 6);
        ALPHA_DISPARI_MAP.put('R', 8);
        ALPHA_DISPARI_MAP.put('S', 12);
        ALPHA_DISPARI_MAP.put('T', 14);
        ALPHA_DISPARI_MAP.put('U', 16);
        ALPHA_DISPARI_MAP.put('V', 10);
        ALPHA_DISPARI_MAP.put('W', 22);
        ALPHA_DISPARI_MAP.put('X', 25);
        ALPHA_DISPARI_MAP.put('Y', 24);
        ALPHA_DISPARI_MAP.put('Z', 23);

        ALPHA_PARI_MAP.put('0', 0);
        ALPHA_PARI_MAP.put('1', 1);
        ALPHA_PARI_MAP.put('2', 2);
        ALPHA_PARI_MAP.put('3', 3);
        ALPHA_PARI_MAP.put('4', 4);
        ALPHA_PARI_MAP.put('5', 5);
        ALPHA_PARI_MAP.put('6', 6);
        ALPHA_PARI_MAP.put('7', 7);
        ALPHA_PARI_MAP.put('8', 8);
        ALPHA_PARI_MAP.put('9', 9);
        ALPHA_PARI_MAP.put('A', 0);
        ALPHA_PARI_MAP.put('B', 1);
        ALPHA_PARI_MAP.put('C', 2);
        ALPHA_PARI_MAP.put('D', 3);
        ALPHA_PARI_MAP.put('E', 4);
        ALPHA_PARI_MAP.put('F', 5);
        ALPHA_PARI_MAP.put('G', 6);
        ALPHA_PARI_MAP.put('H', 7);
        ALPHA_PARI_MAP.put('I', 8);
        ALPHA_PARI_MAP.put('J', 9);
        ALPHA_PARI_MAP.put('K', 10);
        ALPHA_PARI_MAP.put('L', 11);
        ALPHA_PARI_MAP.put('M', 12);
        ALPHA_PARI_MAP.put('N', 13);
        ALPHA_PARI_MAP.put('O', 14);
        ALPHA_PARI_MAP.put('P', 15);
        ALPHA_PARI_MAP.put('Q', 16);
        ALPHA_PARI_MAP.put('R', 17);
        ALPHA_PARI_MAP.put('S', 18);
        ALPHA_PARI_MAP.put('T', 19);
        ALPHA_PARI_MAP.put('U', 20);
        ALPHA_PARI_MAP.put('V', 21);
        ALPHA_PARI_MAP.put('W', 22);
        ALPHA_PARI_MAP.put('X', 23);
        ALPHA_PARI_MAP.put('Y', 24);
        ALPHA_PARI_MAP.put('Z', 25);
    }

    private static char generaCarattereControllo(String cf) {
        int sommaDispari = 0;
        int sommaPari = 0;

        for (int i = 0; i < cf.length(); i++) {
            char currentChar = cf.charAt(i);
            if (i % 2 != 1) {
                sommaDispari += ALPHA_DISPARI_MAP.getOrDefault(currentChar, 0);
            } else {
                sommaPari += ALPHA_PARI_MAP.getOrDefault(currentChar, 0);
            }
        }
        int sommaControllo = (sommaDispari + sommaPari) % 26;

        return caratteriLista[sommaControllo];
    }

    public static String generateCF(long iter) {
        int _1Num = Long.valueOf(iter % 16).intValue();
        int _2Num = Long.valueOf((iter / 16) % 16).intValue();
        int _3Num = Long.valueOf((iter / (int) Math.pow(16, 2)) % 16).intValue();
        int _4Num = Long.valueOf((iter / (int) Math.pow(16, 3)) % 16).intValue();
        int _5Num = Long.valueOf((iter / (int) Math.pow(16, 4)) % 16).intValue();
        int _6Num = Long.valueOf((iter / (int) Math.pow(16, 5)) % 16).intValue();

        char _1Char = 'X'; //Elemento essenziale per evitare che il mock di NR restituisca un valore.
        char _2Char = HEX_TO_LETTER[_2Num];
        char _3Char = HEX_TO_LETTER[_3Num];
        char _4Char = HEX_TO_LETTER[_4Num];
        char _5Char = HEX_TO_LETTER[_5Num];
        char _6Char = HEX_TO_LETTER[_6Num];

        String cfPrefix = "" + _1Char + _2Char + _3Char + _4Char + _5Char + _6Char + "20T25A944";

        char cc = generaCarattereControllo(cfPrefix);
        return cfPrefix + cc;
    }


}
