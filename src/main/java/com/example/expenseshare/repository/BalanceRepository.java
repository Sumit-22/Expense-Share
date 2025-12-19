package com.example.expenseshare.repository;

import com.example.expenseshare.model.Balance;
import com.example.expenseshare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByDebtorAndCreditor(User debtor, User creditor);
    List<Balance> findByDebtorOrCreditor(User debtor, User creditor);
}
