package samples;

import java.util.Optional;

/**
 * A tiny string helper utility class that introduces:
 *
 * Conditionals (if)
 *
 * Exception throwing
 *
 * String parameters
 *
 * Non-primitive return types (Optional)
 */
public class StringUtils {

    public boolean isPalindrome(String input) {
        if (input == null) return false;
        String reversed = new StringBuilder(input).reverse().toString();
        return input.equalsIgnoreCase(reversed);
    }

    public Optional<String> capitalize(String word) {
        if (word == null || word.isBlank()) return Optional.empty();
        return Optional.of(Character.toUpperCase(word.charAt(0)) + word.substring(1));
    }
}