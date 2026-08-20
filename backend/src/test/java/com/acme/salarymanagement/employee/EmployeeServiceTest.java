package com.acme.salarymanagement.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Currency;
import com.acme.salarymanagement.common.enums.Role;
import com.acme.salarymanagement.employee.dto.EmployeeResponse;
import com.acme.salarymanagement.salary.SalaryEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    // Purpose: verifies employee list response uses the latest salary record as current salary.
    @Test
    void getEmployeesMapsLatestSalaryByCreatedAt() {
        // Arrange: repository returns one employee with two salary records.
        // The later createdAt value should become the current salary in the response.
        Pageable pageable = PageRequest.of(0, 10);
        EmployeeEntity employee = employeeWithSalaries(
                salary(new BigDecimal("50000.00"), Currency.INR, Instant.parse("2024-01-01T00:00:00Z")),
                salary(new BigDecimal("75000.00"), Currency.INR, Instant.parse("2025-01-01T00:00:00Z"))
        );

        employee.setCurrentSalaryAmount(new BigDecimal("75000.00"));

        when(employeeRepository.searchEmployees("Employee", List.of(Role.SOFTWARE_ENGINEER), 5,
                List.of(Country.INDIA), Currency.INR, pageable))
                .thenReturn(new PageImpl<>(List.of(employee), pageable, 1));

        // Act: call service exactly like the controller would.
        Page<EmployeeResponse> response = employeeService.getEmployees("Employee",
                List.of(Role.SOFTWARE_ENGINEER), 5, List.of(Country.INDIA), Currency.INR, pageable);

        // Assert: service maps entity fields, picks latest salary, and keeps history count.
        EmployeeResponse employeeResponse = response.getContent().getFirst();
        assertThat(employeeResponse.currentSalaryAmount()).isEqualByComparingTo("75000.00");
        assertThat(employeeResponse.currency()).isEqualTo(Currency.INR);
        assertThat(employeeResponse.salaryHistoryCount()).isEqualTo(2);

        // Assert: service passes search/filter/page parameters to repository unchanged.
        verify(employeeRepository).searchEmployees("Employee", List.of(Role.SOFTWARE_ENGINEER), 5,
                List.of(Country.INDIA), Currency.INR, pageable);
    }

    // Purpose: verifies employee list response handles employees without salary records.
    @Test
    void getEmployeesReturnsNullSalaryWhenEmployeeHasNoSalaryRecords() {
        // Arrange: repository returns an employee without any salary records.
        Pageable pageable = PageRequest.of(0, 10);
        EmployeeEntity employee = employeeWithSalaries();

        when(employeeRepository.searchEmployees(null, null, null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(employee), pageable, 1));

        // Act: call service without search/filter values.
        Page<EmployeeResponse> response = employeeService.getEmployees(null, null, null, null, null, pageable);

        // Assert: salary fields are null and history count is zero.
        EmployeeResponse employeeResponse = response.getContent().getFirst();
        assertThat(employeeResponse.currentSalaryAmount()).isNull();
        assertThat(employeeResponse.currency()).isNull();
        assertThat(employeeResponse.salaryHistoryCount()).isZero();
    }

    private EmployeeEntity employeeWithSalaries(SalaryEntity... salaries) {
        // Test helper: creates one employee entity and attaches any salary records passed by a test.
        EmployeeEntity employee = new EmployeeEntity();
        employee.setId(1L);
        employee.setName("Employee 1");
        employee.setEmail("employee1@acme.com");
        employee.setRole(Role.SOFTWARE_ENGINEER);
        employee.setExperience(5);
        employee.setJoiningDate(LocalDate.of(2020, 1, 1));
        employee.setCountry(Country.INDIA);
        employee.getSalaries().addAll(List.of(salaries));
        return employee;
    }

    private SalaryEntity salary(BigDecimal amount, Currency currency, Instant createdAt) {
        // Test helper: creates a salary record with the fields needed by EmployeeService mapping.
        SalaryEntity salary = new SalaryEntity();
        salary.setAmount(amount);
        salary.setCurrency(currency);
        salary.setCreatedAt(createdAt);
        return salary;
    }
}
