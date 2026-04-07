package com.abraham_bankole.runestone_bank.common.kafka;

public class KafkaTopics {
    private KafkaTopics() {}

    public static final String USER_REGISTERED = "user.registered";
    public static final String USER_LOGIN = "user.login";
    public static final String TRANSACTION_COMPLETED = "transaction.completed";
    public static final String STATEMENT_READY = "statement.ready";
}
