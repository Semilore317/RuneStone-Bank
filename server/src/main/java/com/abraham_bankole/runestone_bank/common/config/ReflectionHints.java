package com.abraham_bankole.runestone_bank.common.config;

import org.jetbrains.annotations.Nullable;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

// GraalVM vs Spring-Boot reflections workaround
public class ReflectionHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {

    }
}
