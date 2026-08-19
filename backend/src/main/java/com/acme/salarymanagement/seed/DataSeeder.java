package com.acme.salarymanagement.seed;

import com.acme.salarymanagement.common.enums.Country;
import com.acme.salarymanagement.common.enums.Currency;
import com.acme.salarymanagement.common.enums.Role;
import com.acme.salarymanagement.employee.EmployeeEntity;
import com.acme.salarymanagement.employee.EmployeeRepository;
import com.acme.salarymanagement.salary.SalaryEntity;

import java.math.BigDecimal;
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

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        for (int i = 1; i <= 10_000; i++) {

            EmployeeEntity employee = new EmployeeEntity();

            // Create employee profile data.
            employee.setName("Employee " + i);
            employee.setEmail("employee" + i + "@acme.com");
            employee.setRole(
                    roles[random.nextInt(roles.length)]
            );
            employee.setExperience(
                    random.nextInt(15) + 1
            );

            /*
             * Generate joining dates far enough in the past
             * so every employee can support up to 10 unique
             * salary-history years before the current year.
             *
             * Example in 2026:
             * joining dates will be around 2007-2016.
             */
            LocalDate latestJoiningDate =
                    LocalDate.of(currentYear - 10, 12, 31);

            LocalDate earliestJoiningDate =
                    latestJoiningDate.minusYears(9);

            long joiningDateRange =
                    latestJoiningDate.toEpochDay()
                            - earliestJoiningDate.toEpochDay()
                            + 1;

            LocalDate joiningDate =
                    earliestJoiningDate.plusDays(
                            random.nextLong(joiningDateRange)
                    );

            employee.setJoiningDate(joiningDate);

            Country country =
                    countries[random.nextInt(countries.length)];

            employee.setCountry(country);

            // Currency is derived from employee country.
            Currency currency =
                    Currency.fromCountry(country);

            /*
             * Pure random salary record count:
             *
             * 1 record  -> 10% chance
             * 2 records -> 10% chance
             * ...
             * 10 records -> 10% chance
             */
            int salaryRecordCount =
                    random.nextInt(10) + 1;

            int salaryAmount =
                    300000 + random.nextInt(1200000);

            /*
             * Spread the selected number of salary records
             * across unique past years.
             *
             * Example:
             * employee joined in 2012
             * salaryRecordCount = 4
             *
             * Records:
             * 2012
             * 2013
             * 2014
             * 2015
             */
            for (
                    int salaryIndex = 0;
                    salaryIndex < salaryRecordCount;
                    salaryIndex++
            ) {

                SalaryEntity salary =
                        new SalaryEntity();

                salary.setEmployee(employee);

                salary.setAmount(
                        BigDecimal.valueOf(salaryAmount)
                );

                salary.setCurrency(currency);

                LocalDate salaryDate =
                        joiningDate.plusYears(salaryIndex);

                /*
                 * Defensive check:
                 * seeded salary must never use current year.
                 */
                if (salaryDate.getYear() >= currentYear) {
                    break;
                }

                salary.setCreatedAt(
                        salaryDate
                                .atStartOfDay()
                                .toInstant(ZoneOffset.UTC)
                );

                employee.getSalaries().add(salary);

                // Increase salary for next history record.
                salaryAmount +=
                        25000 + random.nextInt(150000);
            }

            employees.add(employee);
        }

        // Salary records are persisted through Employee cascade.
        employeeRepository.saveAll(employees);
    }
}