package com.abraham_bankole.runestone_bank.common.service;

import com.abraham_bankole.runestone_bank.common.entity.Outbox;
import com.abraham_bankole.runestone_bank.common.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private OutboxService outboxService;

    @Test
    void testExportEvent_Success() throws Exception {
        Object mockEvent = new Object();
        when(objectMapper.writeValueAsString(mockEvent)).thenReturn("{\"key\":\"value\"}");

        outboxService.exportEvent("123", "User", "USER_CREATED", mockEvent);

        verify(outboxRepository, times(1)).save(any(Outbox.class));
    }

    @Test
    void testExportEvent_ThrowsExceptionOnJsonError() throws Exception {
        Object mockEvent = new Object();
        when(objectMapper.writeValueAsString(mockEvent)).thenThrow(new JsonProcessingException("Error") {});

        assertThrows(RuntimeException.class, () -> outboxService.exportEvent("123", "User", "USER_CREATED", mockEvent));
    }
}
