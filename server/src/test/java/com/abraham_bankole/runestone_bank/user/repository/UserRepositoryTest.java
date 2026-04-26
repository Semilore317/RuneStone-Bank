package com.abraham_bankole.runestone_bank.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.abraham_bankole.runestone_bank.security.entity.Role;
import com.abraham_bankole.runestone_bank.user.entity.User;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Test
  void testExistsByEmail() {
    // Arrange
    User user =
        User.builder()
            .firstName("Jane")
            .lastName("Doe")
            .email("jane@example.com")
            .accountNumber("123456789")
            .accountBalance(BigDecimal.ZERO)
            .password("pass")
            .phoneNumber("000")
            .role(Role.ROLE_USER)
            .status("ACTIVE")
            .build();
    userRepository.save(user);

    // Act & Assert
    assertTrue(userRepository.existsByEmail("jane@example.com"));
    assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
  }

  @Test
  void testFindByAccountNumber() {
    // Arrange
    User user =
        User.builder()
            .firstName("Jane")
            .lastName("Doe")
            .email("jane@example.com")
            .accountNumber("987654321")
            .accountBalance(BigDecimal.ZERO)
            .password("pass")
            .phoneNumber("000")
            .role(Role.ROLE_USER)
            .status("ACTIVE")
            .build();
    userRepository.save(user);

    // Act
    User foundUser = userRepository.findByAccountNumber("987654321");

    // Assert
    assertNotNull(foundUser);
    assertEquals("jane@example.com", foundUser.getEmail());
  }
}
