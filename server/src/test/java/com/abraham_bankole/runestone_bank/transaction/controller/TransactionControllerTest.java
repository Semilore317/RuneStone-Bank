package com.abraham_bankole.runestone_bank.transaction.controller;

import com.abraham_bankole.runestone_bank.common.dto.BankResponse;
import com.abraham_bankole.runestone_bank.common.utils.AccountUtils;
import com.abraham_bankole.runestone_bank.security.config.JwtTokenProvider;
import com.abraham_bankole.runestone_bank.transaction.dto.CreditDebitRequest;
import com.abraham_bankole.runestone_bank.transaction.dto.TransferRequest;
import com.abraham_bankole.runestone_bank.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = TransactionController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false) // Ignore security for pure controller tests
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    // Security context mocks
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockitoBean
    private com.abraham_bankole.runestone_bank.security.repository.TokenBlacklistRepository tokenBlacklistRepository;

    @Test
    void testCredit_ReturnsOk() throws Exception {
        CreditDebitRequest request = new CreditDebitRequest("123456", BigDecimal.valueOf(500));
        
        BankResponse expectedResponse = BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .build();
                
        when(transactionService.creditAccount(any(CreditDebitRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/transactions/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE));
    }

    @Test
    void testDebit_ReturnsOk() throws Exception {
        CreditDebitRequest request = new CreditDebitRequest("123456", BigDecimal.valueOf(100));
        
        BankResponse expectedResponse = BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                .build();
                
        when(transactionService.debitAccount(any(CreditDebitRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/transactions/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE));
    }

    @Test
    void testTransfer_ReturnsOk() throws Exception {
        TransferRequest request = TransferRequest.builder()
                .sender("111")
                .receiver("222")
                .amount(BigDecimal.valueOf(200))
                .build();
        
        BankResponse expectedResponse = BankResponse.builder()
                .responseCode(AccountUtils.TRANSACTION_SUCCESSFUL_CODE)
                .build();
                
        when(transactionService.transfer(any(TransferRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value(AccountUtils.TRANSACTION_SUCCESSFUL_CODE));
    }

    @Test
    void testHistory_ReturnsOk() throws Exception {
        when(transactionService.getTransactionHistory(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/transactions/history")
                .param("accountNumber", "123456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
