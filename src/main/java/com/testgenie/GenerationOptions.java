package com.testgenie;

import java.util.HashSet;
import java.util.Set;

public class GenerationOptions {
    private static Set<String> flags = new HashSet<>();
    private static Set<String> ignoreFlags = new HashSet<>();

    public GenerationOptions(Set<String> flags, Set<String> ignoreFlags) {
        GenerationOptions.flags = flags;
        GenerationOptions.ignoreFlags = ignoreFlags;
    }

    public boolean shouldGenerate(String flag) {
        return (flags.isEmpty() || flags.contains(flag)) && (ignoreFlags.isEmpty() || !ignoreFlags.contains(flag));
    }
}