package com.acme.salarymanagement.salary;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<SalaryEntity, Long> {

    List<SalaryEntity> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
}
