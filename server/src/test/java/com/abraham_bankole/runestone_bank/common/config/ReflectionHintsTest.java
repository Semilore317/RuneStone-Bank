package com.abraham_bankole.runestone_bank.common.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class ReflectionHintsTest {
  @Test
  void registersHibernateConnectionInfoLoggerConstructor() {
    RuntimeHints hints = new RuntimeHints();

    new ReflectionHints().registerHints(hints, getClass().getClassLoader());

    assertTrue(
        RuntimeHintsPredicates.reflection()
            .onType(TypeReference.of("org.hibernate.internal.log.ConnectionInfoLogger_$logger"))
            .withMemberCategory(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .test(hints));
  }
}
