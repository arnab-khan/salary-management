package com.acme.salarymanagement.seed;

import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Currency;
import com.acme.salarymanagement.common.enums.Role;
import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;
import com.acme.salarymanagement.salary.SalaryEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {

        // Run seed only when employee data does not already exist.
        if (employeeRepository.count() > 0) {
            return;
        }

        List<EmployeeEntity> employees = new ArrayList<>();

        Role[] roles = Role.values();
        Country[] countries = Country.values();
        Random random = new Random();

        for (int i = 1; i <= 10_000; i++) {

            EmployeeEntity employee = new EmployeeEntity();

            // Create employee profile data.
            employee.setName("Employee " + i);
            employee.setEmail("employee" + i + "@acme.com");
            employee.setRole(roles[random.nextInt(roles.length)]);
            employee.setExperience(random.nextInt(15) + 1);
            employee.setJoiningDate(
                    LocalDate.now().minusDays(random.nextInt(3650))
            );
            Country country = countries[random.nextInt(countries.length)];
            employee.setCountry(country);

            Currency currency = Currency.fromCountry(country);

            // Distribute salary history in 10% buckets.
            // 10% get no history, 10% get 1 history record, and so on up to 9 history records.
            int historyRecordCount = random.nextInt(10);
            int salaryRecordCount = historyRecordCount + 1;
            int salaryAmount = 300000 + random.nextInt(1200000);
            long employeeAgeInDays = Math.max(1, LocalDate.now().toEpochDay() - employee.getJoiningDate().toEpochDay());
            long salaryIntervalInDays = Math.max(1, employeeAgeInDays / salaryRecordCount);

            for (int salaryIndex = 1; salaryIndex <= salaryRecordCount; salaryIndex++) {
                SalaryEntity salary = new SalaryEntity();

                // Create salary records. Multiple records represent salary history.
                salary.setEmployee(employee);
                salary.setAmount(BigDecimal.valueOf(salaryAmount));
                salary.setCurrency(currency);
                salary.setCreatedAt(
                        randomSalaryDate(employee.getJoiningDate(), salaryIntervalInDays, salaryIndex, random)
                );

                employee.getSalaries().add(salary);

                // Increase the next salary by a random amount.
                salaryAmount += 25000 + random.nextInt(150000);
            }

            employees.add(employee);
        }

        employeeRepository.saveAll(employees);
    }

    private Instant randomSalaryDate(
            LocalDate joiningDate,
            long salaryIntervalInDays,
            int salaryIndex,
            Random random
    ) {
        long startOffset = salaryIntervalInDays * (salaryIndex - 1);
        long randomOffset = random.nextLong(salaryIntervalInDays);

        return joiningDate
                .plusDays(startOffset + randomOffset)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
    }
}
