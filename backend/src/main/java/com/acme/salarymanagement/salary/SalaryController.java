package com.acme.salarymanagement.salary;

import com.acme.salarymanagement.salary.dto.SalaryRequest;
import com.acme.salarymanagement.salary.dto.SalaryResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping("/employee/{employeeId}")
    public List<SalaryResponse> getEmployeeSalaryHistory(@PathVariable Long employeeId) {
        return salaryService.getEmployeeSalaryHistory(employeeId);
    }

    @PostMapping("/employee/{employeeId}")
    public SalaryResponse addEmployeeSalary(
            @PathVariable Long employeeId,
            @Valid @RequestBody SalaryRequest request
    ) {
        return salaryService.addEmployeeSalary(employeeId, request);
    }
}
