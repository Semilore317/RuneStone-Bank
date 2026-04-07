package com.abraham_bankole.runestone_bank.common.service;

import com.abraham_bankole.runestone_bank.common.entity.Outbox;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class OutboxService {
    private final ObjectMapper objectMapper;

    public OutboxService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void exportEvent(String aggregateId, String aggregateType, String eventType, Object event){
        try{
            String payload = objectMapper.writeValueAsString(event);
            Outbox outbox = Outbox.builder()
                    .aggregateId(aggregateId)
                    .aggregateType(aggregateType)
                    .eventType(eventType)
                    .payload(payload)
                    .build();

            // save to outbox using repository class
            
        }catch (JsonProcessingException exception){
            throw new RuntimeException("Error serializing event", exception);
        }
    }
}
