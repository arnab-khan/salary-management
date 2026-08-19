# **Salary Management System Requirements**

## **1. Goal**

Build employee salary management software for an organization with 10,000 employees.

## **2. Scope & Features**

### **2.1. Employee Salary Table**

HR Manager can view employee salary information in a paginated table.

The table will contain:

* Employee ID
* Name
* Email
* Role
* Experience (Years)
* Joining Date
* Country
* Current Salary
* Currency
* Actions (Update Salary / View Salary History)

### **2.2. Table Features**

The table will support:

* **Search** by Employee ID, Name, and Email.
* **Filter** by Role, Experience, Country, and Currency.
* **Sort** by Name, Experience, and Joining Date. Current Salary can be sorted when a specific Currency is selected.
* **Pagination** to efficiently handle 10,000 employees.

### **2.3. Salary Management**

HR Manager can:

* View an employee's current annual salary.
* Update salary for an individual employee.
* View an employee's salary change history.
* Salary changes will be stored as historical records without overwriting previous salary information.

### **2.4. HR Login**

HR Manager can log in using a predefined email/username and password. Only the authenticated HR Manager can access salary management screens.

### **2.5. Seed Data**

A seed script will generate **10,000 employees with salary data** across different roles, experience levels, countries, joining dates, salaries, and currencies.

## **3. Deliberately Out of Scope**

* **Employee Management:** Adding or deleting employees is excluded because the application focuses on salary management for the existing employee dataset.
* **Payroll & Payments:** The system manages salary data, not salary payments or paid/pending payment status.
* **Tax & Deductions:** Country-specific payroll calculations are excluded.
* **Currency Conversion:** Salaries are maintained in their original currencies.
* **Employee Self-Service:** The application is designed for the HR Manager.
* **Multi-tenancy:** The system is designed for a single organization (ACME).