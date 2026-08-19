package com.acme.salarymanagement.salary;

import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;
import com.acme.salarymanagement.salary.dto.SalaryRequest;
import com.acme.salarymanagement.salary.dto.SalaryResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    public SalaryService(SalaryRepository salaryRepository, EmployeeRepository employeeRepository) {
        this.salaryRepository = salaryRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<SalaryResponse> getEmployeeSalaryHistory(Long employeeId) {
        return salaryRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SalaryResponse addEmployeeSalary(Long employeeId, SalaryRequest request) {
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        SalaryEntity salary = new SalaryEntity();
        salary.setEmployee(employee);
        salary.setAmount(request.amount());
        salary.setCurrency(request.currency());

        return toResponse(salaryRepository.save(salary));
    }

    private SalaryResponse toResponse(SalaryEntity salary) {
        return new SalaryResponse(
                salary.getId(),
                salary.getAmount(),
                salary.getCurrency(),
                salary.getCreatedAt());
    }
}
