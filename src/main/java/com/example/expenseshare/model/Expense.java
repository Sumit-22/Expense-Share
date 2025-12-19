package com.example.expenseshare.model;

import com.example.expenseshare.enums.SplitType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "expense")
@Data
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private double amount;

    @ManyToOne
    private User paidBy;

    @ManyToOne
    private Group group;

    @Enumerated(EnumType.STRING)
    private SplitType splitType;
}