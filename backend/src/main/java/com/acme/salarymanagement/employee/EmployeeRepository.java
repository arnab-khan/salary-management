package com.acme.salarymanagement.employee;

import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Currency;
import com.acme.salarymanagement.common.enums.Role;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    // Searches employees by optional keyword, role, experience, and country filters with pagination.
    @Query("""
                SELECT e FROM EmployeeEntity e
                WHERE (:roles IS NULL OR e.role IN :roles)
                AND (:experience IS NULL OR e.experience = :experience)
                AND (:countries IS NULL OR e.country IN :countries)
                AND (:currency IS NULL OR EXISTS (
                    SELECT 1 FROM SalaryEntity currentSalary
                    WHERE currentSalary.employee = e
                    AND currentSalary.currency = :currency
                    AND currentSalary.createdAt = (
                        SELECT MAX(latestSalary.createdAt) FROM SalaryEntity latestSalary
                        WHERE latestSalary.employee = e
                    )
                ))
                AND (
                    :keyword IS NULL OR
                    CAST(e.id AS string) LIKE CONCAT('%', :keyword, '%')
                    OR LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """)
    Page<EmployeeEntity> searchEmployees(
            String keyword,
            List<Role> roles,
            Integer experience,
            List<Country> countries,
            Currency currency,
            Pageable pageable
    );

}
