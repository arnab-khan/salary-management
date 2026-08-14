# **Architecture**

## **1. Project Structure**

The application will use a **feature-based structure** for both frontend and backend.

### **1.1. Frontend Structure**

```text
frontend/
└── src/app/
    ├── core/
    │   ├── auth/
    │   ├── guards/
    │   └── interceptors/
    │
    ├── features/
    │   ├── employee/
    │   │   ├── components/
    │   │   ├── models/
    │   │   └── services/
    │   │
    │   └── salary/
    │       ├── components/
    │       ├── models/
    │       └── services/
    │
    ├── shared/
    │   └── components/
    │
    ├── app.routes.ts
    └── app.config.ts
```

### **1.2. Backend Structure**

```text
backend/
└── src/main/java/.../
    ├── employee/
    │   ├── EmployeeController.java
    │   ├── EmployeeService.java
    │   ├── EmployeeRepository.java
    │   ├── EmployeeEntity.java
    │   └── dto/
    │
    ├── salary/
    │   ├── SalaryController.java
    │   ├── SalaryService.java
    │   ├── SalaryRepository.java
    │   ├── SalaryEntity.java
    │   └── dto/
    │
    ├── auth/
    │   └── SecurityConfig.java
    │
    └── common/
        └── exception/
```

## **2. System Architecture**

```mermaid
flowchart TD

    HR[HR Manager]

    subgraph FE[Angular Frontend]
        AUTH[Authentication]
        EMPF[Employee Feature]
        SALF[Salary Feature]
        SHARED[Shared Components]
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
    end

    subgraph DB[MySQL]
        ET[(employee)]
        ST[(salary)]
    end

    HR --> AUTH
    AUTH --> SECURITY

    EMPF -->|Search / Filter / Sort / Pagination| EC
    SALF -->|Update / Bulk Update / History| SC

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

    ET -->|One-to-Many| ST

    SHARED --> EMPF
    SHARED --> SALF
```

## **3. Application Flow**

* **Angular Authentication** handles the HR login flow and protected frontend routes.
* **Employee Feature** handles the employee salary table, search, filtering, sorting, and pagination.
* **Salary Feature** handles salary updates, bulk updates, and salary history.
* **Spring Security** handles session-based authentication and protects backend APIs.
* **Employee Module** handles employee-related backend operations.
* **Salary Module** handles salary-related backend operations.
* **Employee** and **Salary** have a **One-to-Many relationship** in MySQL.