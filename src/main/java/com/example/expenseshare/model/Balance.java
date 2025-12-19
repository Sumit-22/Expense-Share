package com.example.expenseshare.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "balance")
@Data
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User debtor;    // jisne dena hai

    @ManyToOne
    private User creditor;  // jisko milna hai

    private double amount;
}
