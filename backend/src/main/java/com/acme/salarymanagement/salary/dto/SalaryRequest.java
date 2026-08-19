package com.acme.salarymanagement.salary.dto;

import com.acme.salarymanagement.common.enums.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SalaryRequest(
        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}
