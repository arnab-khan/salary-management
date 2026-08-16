package com.acme.salarymanagement.employee;

import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Role;
import com.acme.salarymanagement.employee.dto.EmployeeResponse;
import com.acme.salarymanagement.salary.SalaryEntity;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployees(
            String keyword,
            List<Role> roles,
            Integer experience,
            List<Country> countries,
            Pageable pageable) {
        Page<EmployeeEntity> employees = employeeRepository.searchEmployees(keyword, roles, experience, countries,
                pageable);
        return employees.map(this::toResponse);
    }

    private EmployeeResponse toResponse(EmployeeEntity employee) {
        SalaryEntity currentSalary = employee.getSalaries().stream()
                .max(Comparator.comparing(SalaryEntity::getCreatedAt))
                .orElse(null);

        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getRole(),
                employee.getExperience(),
                employee.getJoiningDate(),
                employee.getCountry(),
                currentSalary == null ? null : currentSalary.getAmount(),
                currentSalary == null ? null : currentSalary.getCurrency(),
                employee.getSalaries().size());
    }
}
