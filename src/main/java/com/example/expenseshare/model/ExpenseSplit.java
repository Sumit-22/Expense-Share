package com.example.expenseshare.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "expense_split")
@Data
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Expense expense;

    @ManyToOne
    private User user;

    private double amount;
}