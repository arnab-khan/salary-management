package com.acme.salarymanagement.employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Role;
import com.acme.salarymanagement.salary.SalaryEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "employee", indexes = {
                @Index(name = "idx_employee_role", columnList = "role"),
                @Index(name = "idx_employee_experience", columnList = "experience"),
                @Index(name = "idx_employee_country", columnList = "country"),
                @Index(name = "idx_employee_joining_date", columnList = "joining_date")
})
public class EmployeeEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false, unique = true)
        private String email;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        @Column(nullable = false)
        private Integer experience; // Total professional experience in completed years.

        @Column(name = "joining_date", nullable = false)
        private LocalDate joiningDate; // Stores only the employee's joining date, without time or timezone.

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Country country;

        @OneToMany(mappedBy = "employee", // SalaryEntity owns the relationship using its "employee" field
                        cascade = CascadeType.ALL, // If operations performed on the parent Employee are propagated to
                                                   // its Salary children. For example, deleting the employee can also
                                                   // delete all of that employee’s salary rows.
                        orphanRemoval = false, // Removing a Salary from the employee's salaries list will not delete
                                              // that Salary record from the database.
                        fetch = FetchType.LAZY // Loads salary records only when they are accessed
        )
        private List<SalaryEntity> salaries = new ArrayList<>();
}
