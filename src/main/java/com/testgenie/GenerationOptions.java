package com.testgenie;

import java.util.Set;

/**
 * <p>
 * GenerationOptions manages the flags used to control which code generation
 * features are enabled or ignored.
 * </p>
 * <p>
 * This class uses two static sets of flags:
 * - 'flags': a set of enabled flags.
 * - 'ignoreFlags': a set of flags to be ignored even if enabled.
 * </p>
 * <p>
 * Usage:
 * - When checking if a feature should be generated for a given flag,
 *   call shouldGenerate(flag).
 * </p>
 */
public class GenerationOptions {
    // Set of enabled flags for generation
    private Set<String> flags;
    // Set of flags to ignore (even if present in 'flags')
    private Set<String> ignoreFlags;

    /**
     * @param flags        a set of flags that enable certain generation features
     * @param ignoreFlags  a set of flags to exclude from generation,
     *                     even if present in 'flags'
     */
    public GenerationOptions(Set<String> flags, Set<String> ignoreFlags) {
        this.flags = flags;
        this.ignoreFlags = ignoreFlags;
    }

    /**
     * <p>Determines if generation should proceed for the given flag.</p>
     * <p>
     * Logic:
     * - If 'flags' is empty, allow all flags unless ignored.
     * - If 'flags' is not empty, only allow flags present in 'flags'.
     * - Always skip flags present in 'ignoreFlags'.
     * </p>
     * @param flag the flag to check
     * @return true if generation should occur for this flag, false otherwise
     */
    public boolean shouldGenerate(String flag) {
        return (flags.isEmpty() || flags.contains(flag)) && (ignoreFlags.isEmpty() || !ignoreFlags.contains(flag));
    }
}