package com.example.expenseshare.service;

import com.example.expenseshare.dto.ExpenseRequestDto;
import com.example.expenseshare.enums.SplitType;
import com.example.expenseshare.model.Expense;
import com.example.expenseshare.model.ExpenseSplit;
import com.example.expenseshare.model.Group;
import com.example.expenseshare.model.User;
import com.example.expenseshare.repository.ExpenseRepository;
import com.example.expenseshare.repository.ExpenseSplitRepository;
import com.example.expenseshare.repository.GroupRepository;
import com.example.expenseshare.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExpenseService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    public ExpenseService(UserRepository userRepository,
                          GroupRepository groupRepository,
                          ExpenseRepository expenseRepository,
                          ExpenseSplitRepository expenseSplitRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
    }

    public void addExpense(ExpenseRequestDto dto) {

        // 1️⃣ Fetch payer & group
        User paidBy = userRepository.findById(dto.getPaidBy())
                .orElseThrow(() -> new RuntimeException("PaidBy user not found"));

        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // 2️⃣ Save Expense
        Expense expense = new Expense();
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setPaidBy(paidBy);
        expense.setGroup(group);
        expense.setSplitType(dto.getSplitType());

        expense = expenseRepository.save(expense);

        // 3️⃣ Split Logic
        if (dto.getSplitType() == SplitType.EQUAL) {
            handleEqualSplit(dto, expense);
        } else if (dto.getSplitType() == SplitType.EXACT) {
            handleExactSplit(dto, expense);
        } else if (dto.getSplitType() == SplitType.PERCENT) {
            handlePercentSplit(dto, expense);
        }
    }

    // ================= SPLIT METHODS =================

    private void handleEqualSplit(ExpenseRequestDto dto, Expense expense) {
        int totalUsers = dto.getSplits().size();
        double perUserAmount = dto.getAmount() / totalUsers;

        for (Long userId : dto.getSplits().keySet()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmount(perUserAmount);

            expenseSplitRepository.save(split);
        }
    }

    private void handleExactSplit(ExpenseRequestDto dto, Expense expense) {
        for (Map.Entry<Long, Double> entry : dto.getSplits().entrySet()) {
            User user = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmount(entry.getValue());

            expenseSplitRepository.save(split);
        }
    }

    private void handlePercentSplit(ExpenseRequestDto dto, Expense expense) {
        for (Map.Entry<Long, Double> entry : dto.getSplits().entrySet()) {
            User user = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            double amount = dto.getAmount() * entry.getValue() / 100;

            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmount(amount);

            expenseSplitRepository.save(split);
        }
    }
}
