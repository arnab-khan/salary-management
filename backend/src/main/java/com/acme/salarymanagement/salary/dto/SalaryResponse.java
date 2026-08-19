package com.acme.salarymanagement.salary.dto;

import com.acme.salarymanagement.common.enums.Currency;
import java.math.BigDecimal;
import java.time.Instant;

public record SalaryResponse(
        Long id,
        BigDecimal amount,
        Currency currency,
        Instant createdAt
) {
}
