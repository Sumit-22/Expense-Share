package com.example.expenseshare.controller;

import com.example.expenseshare.model.Balance;
import com.example.expenseshare.repository.BalanceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/balances")
public class BalanceController {

    private final BalanceRepository balanceRepository;

    public BalanceController(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @GetMapping
    public List<Balance> getAllBalances() {
        return balanceRepository.findAll();
    }
}
