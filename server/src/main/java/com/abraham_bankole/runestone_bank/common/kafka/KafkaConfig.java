package com.abraham_bankole.runestone_bank.common.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
  @Bean
  public NewTopic userRegisteredTopic() {
    return TopicBuilder.name(KafkaTopics.USER_REGISTERED).partitions(1).replicas(1).build();
  }

  @Bean
  public NewTopic userLoginTopic() {
    return TopicBuilder.name(KafkaTopics.USER_LOGIN).partitions(1).replicas(1).build();
  }

  @Bean
  public NewTopic transactionCompletedTopic() {
    return TopicBuilder.name(KafkaTopics.TRANSACTION_COMPLETED).partitions(1).replicas(1).build();
  }

  @Bean
  public NewTopic statementReadyTopic() {
    return TopicBuilder.name(KafkaTopics.STATEMENT_READY).partitions(1).replicas(1).build();
  }
}
