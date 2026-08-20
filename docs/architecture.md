# **Architecture**

## **1. Project Structure**

The application uses a **feature-based structure** for both frontend and backend.

### **1.1. Frontend Structure**

```text
frontend/
`-- src/app/
    |-- core/
    |   |-- auth/
    |   |   |-- guards/
    |   |   |-- pages/
    |   |   `-- services/
    |   `-- interceptors/
    |
    |-- features/
    |   |-- employee/
    |   |   |-- components/
    |   |   |-- pages/
    |   |   `-- services/
    |   |
    |   `-- salary/
    |       |-- components/
    |       `-- services/
    |
    |-- shared/
    |   `-- interfaces/
    |
    |-- app.routes.ts
    `-- app.config.ts
```

### **1.2. Backend Structure**

```text
backend/
`-- src/main/java/com/acme/salarymanagement/
    |-- auth/
    |   |-- AuthController.java
    |   |-- AuthService.java
    |   |-- SecurityConfig.java
    |   `-- dto/
    |
    |-- common/
    |   |-- EnumController.java
    |   |-- dto/
    |   `-- enums/
    |
    |-- employee/
    |   |-- EmployeeController.java
    |   |-- EmployeeService.java
    |   |-- EmployeeRepository.java
    |   |-- EmployeeEntity.java
    |   `-- dto/
    |
    |-- salary/
    |   |-- SalaryController.java
    |   |-- SalaryService.java
    |   |-- SalaryRepository.java
    |   |-- SalaryEntity.java
    |   `-- dto/
    |
    `-- seed/
        `-- DataSeeder.java
```

## **2. System Architecture**

```mermaid
flowchart TD

    HR[HR Manager]

    subgraph FE[Angular Frontend]
        AUTH[Authentication]
        EMPF[Employee Feature]
        SALF[Salary Feature]
        SHARED[Shared Interfaces]
    end

    subgraph BE[Spring Boot Backend]
        SECURITY[Spring Security<br/>Session Authentication]

        subgraph EMP[Employee Module]
            EC[Controller]
            ED[DTO & Validation]
            ES[Service]
            ER[Repository]
            EE[Employee Entity]
        end

        subgraph SAL[Salary Module]
            SC[Controller]
            SD[DTO & Validation]
            SS[Service]
            SR[Repository]
            SE[Salary Entity]
        end

        subgraph COMMON[Common Module]
            ENUMS[Enums API]
        end

        SEED[Data Seeder]
    end

    subgraph DB[MySQL]
        ET[(employee)]
        ST[(salary)]
    end

    HR --> AUTH
    AUTH --> SECURITY

    EMPF -->|Search / Filter / Sort / Pagination| EC
    SALF -->|Update / History| SC
    EMPF -->|Role / Country / Currency Options| ENUMS

    SECURITY --> EC
    SECURITY --> SC

    EC --> ED
    ED --> ES
    ES --> ER
    ER --> EE
    EE --> ET

    SC --> SD
    SD --> SS
    SS --> SR
    SR --> SE
    SE --> ST

    SEED --> ET
    SEED --> ST

    ET -->|One-to-Many| ST

    SHARED --> EMPF
    SHARED --> SALF
```

## **3. Application Flow**

* **Angular Authentication** handles the HR login flow and protected frontend routes.
* **Employee Feature** handles the employee salary table, search, filtering, sorting, and pagination.
* **Salary Feature** handles individual salary updates and salary history.
* **Shared Interfaces** define frontend response shapes used across features.
* **Spring Security** handles session-based authentication and protects backend APIs.
* **Employee Module** handles employee-related backend operations.
* **Salary Module** handles salary-related backend operations.
* **Common Module** exposes role, country, and currency option data.
* **Data Seeder** creates employee and salary data during startup when employee data is empty.
* **Employee** and **Salary** have a **One-to-Many relationship** in MySQL.