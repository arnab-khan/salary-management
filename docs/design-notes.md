# **Design Notes**

## **1. Technology Choices**

* **Frontend:** Angular 22.1.0
* **Backend:** Java 21 with Spring Boot 4.1.0
* **Database:** MySQL
* **Database Access:** Spring Data JPA with Hibernate (managed by Spring Boot 4.1.0)
* **UI Component Library:** Angular Material 22.1.2
* **CSS Framework:** Bootstrap 5.3.8

## **2. Data Model**

The application will use two main tables: **Employee** and **Salary**.

### **2.1. Employee**

Fields:

* `id` - Primary key and Employee ID displayed in the UI.
* `name`
* `email` - Unique.
* `role`
* `experience`
* `joining_date`
* `country`

### **2.2. Salary**

Fields:

* `id` - Primary key.
* `employee_id` - Foreign key referencing `employee.id`.
* `amount`
* `currency`
* `created_at`

**Relationship:** Employee and Salary have a **One-to-Many relationship**. One employee can have multiple salary records.

Each salary update creates a new Salary record instead of overwriting the existing record. The latest record is considered the employee's current salary, while previous records provide salary history.

## **3. Database Indexes**

### **Employee**

* `role` - For filtering.
* `experience` - For filtering and sorting.
* `country` - For filtering.
* `joining_date` - For sorting.

### **Salary**

* `(employee_id, created_at)` - Composite index for efficiently finding an employee's latest salary and salary history.

## **4. Predefined Data**

Role, Country, and Currency will use predefined values maintained using **Java Enums**.

## **5. Data Seeding**

A seed script will generate **10,000 employee records** along with their initial salary records.

The seed process will run during application startup **only when employee data does not already exist**, preventing duplicate records on subsequent application restarts.

Seed data will use the predefined `Role`, `Country`, and `Currency` enums to generate consistent data.

## **6. Authentication**

The application will use a predefined HR Manager account.

* Authentication will be handled using **Spring Security with session-based authentication**.
* HR username/email and password will be provided through **environment variables** in the deployed environment instead of being stored in the source code.
* After successful login, the authenticated session will be maintained using an **HTTP-only cookie**.
* Salary management APIs will be accessible only to the authenticated HR Manager.