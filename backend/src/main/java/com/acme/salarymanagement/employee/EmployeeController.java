package com.acme.salarymanagement.employee;

import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Role;
import com.acme.salarymanagement.employee.dto.EmployeeResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // API URL: GET http://localhost:8080/api/employees?keyword=Arnab&roles=SOFTWARE_ENGINEER,QA_ENGINEER&countries=INDIA,CANADA&page=0&size=10&sort=name,asc
    @GetMapping
    public Page<EmployeeResponse> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Role> roles,
            @RequestParam(required = false) Integer experience,
            @RequestParam(required = false) List<Country> countries,
            Pageable pageable
    ) {
        return employeeService.getEmployees(keyword, roles, experience, countries, pageable);
    }
}
