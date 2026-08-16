package com.acme.salarymanagement.employee.dto;

import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Currency;
import com.acme.salarymanagement.common.enums.Role;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        Role role,
        Integer experience,
        LocalDate joiningDate,
        Country country,
        BigDecimal currentSalaryAmount,
        Currency currency,
        int salaryHistoryCount
) {
}
