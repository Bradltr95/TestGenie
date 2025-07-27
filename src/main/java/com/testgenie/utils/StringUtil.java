package com.testgenie.utils;

/**
 * String utility method's used by the TestGenerator to ensure formatting is consistent for test stubs.
 */
public class StringUtil {
    /**
     * Lowercases the first character of a given string.
     */
    public static String lowercaseFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * Capitalizes the first character of a given string.
     */
    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
