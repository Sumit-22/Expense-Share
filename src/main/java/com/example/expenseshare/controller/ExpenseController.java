package com.example.expenseshare.controller;

import com.example.expenseshare.dto.ExpenseRequestDto;
import com.example.expenseshare.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<String> addExpense(@RequestBody ExpenseRequestDto dto) {
        expenseService.addExpense(dto);
        return ResponseEntity.ok("Expense added successfully");
    }
}
