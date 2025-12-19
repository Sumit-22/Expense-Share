package com.example.expenseshare.dto;

import com.example.expenseshare.enums.SplitType;
import lombok.Data;

import java.util.Map;

@Data
public class ExpenseRequestDto {

    private Long groupId;     // kis group me expense
    private Long paidBy;      // kisne pay kiya
    private double amount;    // total amount
    private String description;

    private SplitType splitType;

    /*
     * key   = userId
     * value = amount OR percentage (based on splitType)
     */
    private Map<Long, Double> splits;
}
