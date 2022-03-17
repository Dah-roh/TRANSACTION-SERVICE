package com.reloadly_task.transactionservice.repository;

import com.reloadly_task.transactionservice.model.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long> {

    Optional<Transactions> findAllByUserId(Long userId);
    Transactions findTopByUserId(Long userId);

    Optional<Transactions> findByUserId(Long id);
}
