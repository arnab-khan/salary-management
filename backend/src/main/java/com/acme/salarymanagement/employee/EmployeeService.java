package com.acme.salarymanagement.employee;

import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Role;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Page<EmployeeEntity> getEmployees(
            String keyword,
            List<Role> roles,
            Integer experience,
            List<Country> countries,
            Pageable pageable
    ) {
        return employeeRepository.searchEmployees(keyword, roles, experience, countries, pageable);
    }
}
