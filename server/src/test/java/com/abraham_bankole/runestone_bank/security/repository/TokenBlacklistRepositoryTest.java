package com.abraham_bankole.runestone_bank.security.repository;

import com.abraham_bankole.runestone_bank.security.entity.TokenBlacklist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class TokenBlacklistRepositoryTest {

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Test
    void testFindByToken_WhenExists() {
        TokenBlacklist token = new TokenBlacklist();
        token.setToken("my.blacklisted.token");
        tokenBlacklistRepository.save(token);

        Optional<TokenBlacklist> result = tokenBlacklistRepository.findByToken("my.blacklisted.token");
        assertTrue(result.isPresent());
    }

    @Test
    void testFindByToken_WhenDoesNotExist_ReturnsEmpty() {
        Optional<TokenBlacklist> result = tokenBlacklistRepository.findByToken("non.existent.token");
        assertFalse(result.isPresent());
    }
}
