package com.acme.salarymanagement.salary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acme.salarymanagement.common.enums.Currency;
import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;
import com.acme.salarymanagement.salary.dto.SalaryRequest;
import com.acme.salarymanagement.salary.dto.SalaryResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private SalaryService salaryService;

    // Purpose: verifies salary history is returned newest first using repository ordering.
    @Test
    void getEmployeeSalaryHistoryMapsSalaryRecords() {
        // Arrange: repository returns salary records for the employee.
        Long employeeId = 1L;
        SalaryEntity latestSalary = salary(2L, new BigDecimal("75000.00"), Currency.INR,
                Instant.parse("2025-01-01T00:00:00Z"));
        SalaryEntity previousSalary = salary(1L, new BigDecimal("50000.00"), Currency.INR,
                Instant.parse("2024-01-01T00:00:00Z"));

        when(salaryRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId))
                .thenReturn(List.of(latestSalary, previousSalary));

        // Act: fetch salary history through the service.
        List<SalaryResponse> response = salaryService.getEmployeeSalaryHistory(employeeId);

        // Assert: service maps salary records in the same order returned by the repository.
        assertThat(response).hasSize(2);
        assertThat(response.getFirst().id()).isEqualTo(2L);
        assertThat(response.getFirst().amount()).isEqualByComparingTo("75000.00");
        assertThat(response.getFirst().currency()).isEqualTo(Currency.INR);
        assertThat(response.getFirst().createdAt()).isEqualTo(Instant.parse("2025-01-01T00:00:00Z"));

        verify(salaryRepository).findByEmployeeIdOrderByCreatedAtDesc(employeeId);
    }

    // Purpose: verifies adding a salary creates a new salary record for the requested employee.
    @Test
    void addEmployeeSalarySavesSalaryForEmployee() {
        // Arrange: employee exists and repository returns the saved salary record.
        Long employeeId = 1L;
        EmployeeEntity employee = new EmployeeEntity();
        employee.setId(employeeId);
        SalaryRequest request = new SalaryRequest(new BigDecimal("85000.00"), Currency.USD);
        SalaryEntity savedSalary = salary(3L, request.amount(), request.currency(),
                Instant.parse("2025-06-01T00:00:00Z"));
        savedSalary.setEmployee(employee);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(salaryRepository.save(any(SalaryEntity.class))).thenReturn(savedSalary);

        // Act: add salary through the service.
        SalaryResponse response = salaryService.addEmployeeSalary(employeeId, request);

        // Assert: service returns the saved salary response.
        assertThat(response.id()).isEqualTo(3L);
        assertThat(response.amount()).isEqualByComparingTo("85000.00");
        assertThat(response.currency()).isEqualTo(Currency.USD);
        assertThat(response.createdAt()).isEqualTo(Instant.parse("2025-06-01T00:00:00Z"));

        verify(employeeRepository).findById(employeeId);
        verify(salaryRepository).save(any(SalaryEntity.class));
    }

    // Purpose: verifies salary updates fail clearly when the employee does not exist.
    @Test
    void addEmployeeSalaryThrowsNotFoundWhenEmployeeDoesNotExist() {
        // Arrange: requested employee id is missing.
        Long employeeId = 99L;
        SalaryRequest request = new SalaryRequest(new BigDecimal("85000.00"), Currency.USD);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act + Assert: service returns a 404 API error.
        assertThatThrownBy(() -> salaryService.addEmployeeSalary(employeeId, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseStatusException.getReason()).isEqualTo("Employee not found");
                });

        verify(employeeRepository).findById(employeeId);
    }

    private SalaryEntity salary(Long id, BigDecimal amount, Currency currency, Instant createdAt) {
        // Test helper: creates a salary record with fields used by SalaryService response mapping.
        SalaryEntity salary = new SalaryEntity();
        salary.setId(id);
        salary.setAmount(amount);
        salary.setCurrency(currency);
        salary.setCreatedAt(createdAt);
        return salary;
    }
}
