package com.abraham_bankole.runestone_bank.common.repository;

import com.abraham_bankole.runestone_bank.common.entity.Outbox;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, UUID> {}
