package com.abraham_bankole.runestone_bank.security.service.impl;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.common.service.OutboxService;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import com.abraham_bankole.runestone_bank.security.config.JwtTokenProvider;
import com.abraham_bankole.runestone_bank.security.entity.TokenBlacklist;
import com.abraham_bankole.runestone_bank.security.repository.TokenBlacklistRepository;
import com.abraham_bankole.runestone_bank.user.dto.LoginDto;
import com.abraham_bankole.runestone_bank.user.entity.User;
import com.abraham_bankole.runestone_bank.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutboxService outboxService;

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testLogin_Success() {
        LoginDto loginDto = new LoginDto("test@test.com", "password123");
        Authentication authentication = mock(Authentication.class);
        
        User mockUser = User.builder()
                .firstName("Test")
                .lastName("Account")
                .email("test@test.com")
                .accountNumber("123456")
                .accountBalance(BigDecimal.valueOf(100))
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mocked.jwt.token");

        BankResponse response = authService.login(loginDto);

        assertEquals(AccountUtils.LOGIN_SUCCESS_CODE, response.getResponseCode());
        assertEquals("mocked.jwt.token", response.getJwt());
        verify(outboxService, times(1)).exportEvent(anyString(), anyString(), anyString(), any());
    }

    @Test
    void testLogin_UserNotFoundThrowsException() {
        LoginDto loginDto = new LoginDto("test@test.com", "password123");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(loginDto));
    }

    @Test
    void testLogout_Success() {
        String authHeader = "Bearer eyJhbGciOiJIUzI1NiJ9";

        BankResponse response = authService.logout(authHeader);

        assertEquals("200", response.getResponseCode());
        assertEquals("Logout successful", response.getResponseMessage());
        verify(tokenBlacklistRepository, times(1)).save(any(TokenBlacklist.class));
    }

    @Test
    void testLogout_MalformedHeader_IgnoresToken() {
        String authHeader = "InvalidTokenFormat";

        BankResponse response = authService.logout(authHeader);

        assertEquals("200", response.getResponseCode());
        verify(tokenBlacklistRepository, never()).save(any(TokenBlacklist.class));
    }
}
