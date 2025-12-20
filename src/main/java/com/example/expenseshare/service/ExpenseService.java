package com.example.expenseshare.service;

import com.example.expenseshare.dto.ExpenseRequestDto;
import com.example.expenseshare.enums.SplitType;
import com.example.expenseshare.model.*;
import com.example.expenseshare.repository.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExpenseService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final BalanceRepository balanceRepository;

    public ExpenseService(UserRepository userRepository,
                          GroupRepository groupRepository,
                          ExpenseRepository expenseRepository,
                          ExpenseSplitRepository expenseSplitRepository,
                          BalanceRepository balanceRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.balanceRepository = balanceRepository;
    }



    public void addExpense(ExpenseRequestDto dto) {

        User paidBy = userRepository.findById(dto.getPaidBy())
                .orElseThrow(() -> new RuntimeException("PaidBy user not found"));

        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Expense expense = new Expense();
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setPaidBy(paidBy);
        expense.setGroup(group);
        expense.setSplitType(dto.getSplitType());

        expense = expenseRepository.save(expense);

        if (dto.getSplitType() == SplitType.EQUAL) {
            handleEqualSplit(dto, expense);
        } else if (dto.getSplitType() == SplitType.EXACT) {
            handleExactSplit(dto, expense);
        } else {
            handlePercentSplit(dto, expense);
        }
    }

    private void handleEqualSplit(ExpenseRequestDto dto, Expense expense) {
        int totalUsers = dto.getSplits().size();
        double perUserAmount = dto.getAmount() / totalUsers;
        User paidBy = expense.getPaidBy();

        for (Long userId : dto.getSplits().keySet()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmount(perUserAmount);
            expenseSplitRepository.save(split);

            updateBalance(user, paidBy, perUserAmount);
        }
    }

    private void handleExactSplit(ExpenseRequestDto dto, Expense expense) {
        User paidBy = expense.getPaidBy();

        for (Map.Entry<Long, Double> entry : dto.getSplits().entrySet()) {
            User user = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmount(entry.getValue());
            expenseSplitRepository.save(split);

            updateBalance(user, paidBy, entry.getValue());
        }
    }

    private void handlePercentSplit(ExpenseRequestDto dto, Expense expense) {
        User paidBy = expense.getPaidBy();

        for (Map.Entry<Long, Double> entry : dto.getSplits().entrySet()) {
            User user = userRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            double amount = dto.getAmount() * entry.getValue() / 100;

            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(expense);
            split.setUser(user);
            split.setAmount(amount);
            expenseSplitRepository.save(split);

            updateBalance(user, paidBy, amount);
        }
    }

    // ================= BALANCE LOGIC =================

    private void updateBalance(User debtor, User creditor, double amount) {

        if (debtor.getId().equals(creditor.getId())) return;

        var reverse = balanceRepository.findByDebtorAndCreditor(creditor, debtor);

        if (reverse.isPresent()) {
            Balance rev = reverse.get();

            if (rev.getAmount() > amount) {
                rev.setAmount(rev.getAmount() - amount);
                balanceRepository.save(rev);
            } else if (rev.getAmount() < amount) {
                balanceRepository.delete(rev);
                saveOrUpdate(debtor, creditor, amount - rev.getAmount());
            } else {
                balanceRepository.delete(rev);
            }
        } else {
            saveOrUpdate(debtor, creditor, amount);
        }
    }

    private void saveOrUpdate(User debtor, User creditor, double amount) {
        Balance balance = balanceRepository
                .findByDebtorAndCreditor(debtor, creditor)
                .orElse(new Balance());

        balance.setDebtor(debtor);
        balance.setCreditor(creditor);
        balance.setAmount(balance.getAmount() + amount);

        balanceRepository.save(balance);
    }
}
