package com.tolpp.android.util;

import com.tolpp.android.constant.WordConstant;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomUtil {
    private static final String alphaSmallCase = "abcdefghijklmnopqrstuvxyz";
    private static final String alphaCapitalCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String numeric = "1234567890";
    private static final String alphaNumeric = numeric + alphaSmallCase + alphaCapitalCase;
    private static final String hexadecimal = numeric + "ABCDEF";
    private static final Random random = new Random();

    private static final List<String> existingPhaSearchRuleIds = Arrays.asList("5e73d89a39b269233f4ceca8",
            "5e7baf764c17ee8b4d590738"
    );

    public static String randomAlphaCapital(int length) {
        return randomString(alphaCapitalCase, length);
    }

    public static String randomAlphaSmall(int length) {
        return randomString(alphaSmallCase, length);
    }

    public static String randomAlpha(int length) {
        return randomString(alphaSmallCase + alphaCapitalCase, length);
    }

    public static String randomHex(int length) {
        return randomString(hexadecimal, length);
    }

    public static String randomAlphaNumeric(int length) {
        return randomString(alphaNumeric, length);
    }

    public static String randomNumeric(int length) {
        return randomString(numeric, length);
    }

    public static String randomString(String charSet, int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (charSet.length() * Math.random());
            sb.append(charSet.charAt(index));
        }

        return sb.toString();
    }

    public static String randomWord() {
        int randomIndex = (int) (WordConstant.commonWords.length * Math.random());
        return WordConstant.commonWords[randomIndex];
    }

    public static String randomSentence(int wordCount, String separator, boolean capitalize) {
        if (wordCount == 0) return "";
        StringBuilder sentenceBuilder = new StringBuilder();

        for (int i = 0; i < wordCount; i++) {
            if (i != 0) {
                sentenceBuilder.append(separator);
            }
            String word = randomWord();
            if (capitalize) {
                word = StringUtils.capitalize(word);
            }
            sentenceBuilder.append(word);
        }

        return sentenceBuilder.toString();
    }

    public static byte[] getRandomBytes(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }
}
