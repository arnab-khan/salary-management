package com.acme.salarymanagement.salary;

import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.common.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "salary", indexes = {
                @Index(name = "idx_salary_employee_created_at", columnList = "employee_id, created_at")
})
public class SalaryEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false) // Each salary record belongs to one employee.
        @JoinColumn(name = "employee_id", nullable = false) // Foreign key to employee.id.
        private EmployeeEntity employee;

        @Column(nullable = false, precision = 19, scale = 2, updatable = false)
        private BigDecimal amount; // Salary amount with up to 2 decimal places and maximum 19 total digits.

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, updatable = false)
        private Currency currency;

        @Column(name = "created_at", nullable = false, updatable = false)
        private Instant createdAt;

        @PrePersist // Uses the current time only when createdAt was not provided explicitly.
        public void prePersist() {
                if (createdAt == null) {
                        createdAt = Instant.now();
                }
        }
}
