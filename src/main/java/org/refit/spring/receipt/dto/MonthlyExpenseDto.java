package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyExpenseDto {
    private Long userId;
    private Long thisMonthExpense;
    private Long lastMonthExpense;
}
