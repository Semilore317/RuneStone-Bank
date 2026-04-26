package com.abraham_bankole.runestone_bank.statement.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abraham_bankole.runestone_bank.security.config.JwtTokenProvider;
import com.abraham_bankole.runestone_bank.statement.service.BankStatementService;
import com.abraham_bankole.runestone_bank.transaction.entity.Transaction;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = StatementController.class,
    excludeAutoConfiguration = {
      org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
      org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
@AutoConfigureMockMvc(addFilters = false) // Ignore security for pure controller tests
class StatementControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private BankStatementService bankStatementService;

  @MockitoBean private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

  @MockitoBean
  private com.abraham_bankole.runestone_bank.security.repository.TokenBlacklistRepository
      tokenBlacklistRepository;

  @Test
  void testGenerateBankStatement_ReturnsOk() throws Exception {
    Transaction mockTransaction = Transaction.builder().accountNumber("123").build();
    List<Transaction> list = Collections.singletonList(mockTransaction);

    when(bankStatementService.generateStatement("123", "2023-01-01", "2023-12-31"))
        .thenReturn(list);

    mockMvc
        .perform(
            get("/api/v1/bankstatement")
                .param("accountNumber", "123")
                .param("start", "2023-01-01")
                .param("end", "2023-12-31")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].accountNumber").value("123"));
  }

  @Test
  void testRequestStatementByEmail_ReturnsOk() throws Exception {
    when(bankStatementService.generateStatement("123", "2023-01-01", "2023-12-31"))
        .thenReturn(Collections.emptyList());

    mockMvc
        .perform(
            post("/api/v1/bankstatement/email")
                .param("accountNumber", "123")
                .param("start", "2023-01-01")
                .param("end", "2023-12-31")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").exists());
  }
}
