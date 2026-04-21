# 🧾 Camunda BPM Invoice Approval App

A full-stack invoice approval workflow application built with Camunda BPM 7.20, Spring Boot 3, and PostgreSQL. Invoices are submitted via REST API and routed through an automated approval workflow managed by the Camunda process engine.

---

## 🛠 Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Spring Boot | 3.2.5 | Application framework |
| Camunda BPM | 7.20.0 | Embedded process engine |
| Spring Data JPA | Latest | ORM / DB access |
| PostgreSQL | 15 | Database |
| Lombok | Latest | Boilerplate reduction |
| Maven | 3.9.x | Build tool |
| Docker | Latest | PostgreSQL container |

---

## 🏗 Architecture Overview

```
REST Client
    │
    ▼
InvoiceController        ← REST API layer (/api/invoices)
    │
    ▼
InvoiceService           ← Business logic + starts Camunda process
    │
    ▼
Camunda Process Engine   ← Manages workflow state
    │
    ├── ValidateInvoiceDelegate   ← Validates invoice data
    ├── ApproveInvoiceDelegate    ← Handles approval
    └── RejectInvoiceDelegate     ← Handles rejection
    │
    ▼
PostgreSQL Database      ← Stores invoices + Camunda tables
```

---

## 📁 Project Structure

```
camunda-invoice-app/
├── pom.xml
└── src/
    └── main/
        ├── java/com/yourname/invoiceapp/
        │   ├── InvoiceAppApplication.java
        │   ├── controller/
        │   │   └── InvoiceController.java
        │   ├── service/
        │   │   └── InvoiceService.java
        │   ├── repository/
        │   │   └── InvoiceRepository.java
        │   ├── entity/
        │   │   └── Invoice.java
        │   └── delegate/
        │       ├── ValidateInvoiceDelegate.java
        │       ├── ApproveInvoiceDelegate.java
        │       └── RejectInvoiceDelegate.java
        └── resources/
            ├── application.properties
            ├── bpmn/
            │   └── invoice-approval.bpmn
            └── META-INF/
                └── processes.xml
```

---

## ✅ Prerequisites

| Tool | Download |
|---|---|
| Java 17 | https://adoptium.net |
| Maven 3.9.x | https://maven.apache.org/download.cgi |
| Docker Desktop | https://www.docker.com/products/docker-desktop |
| Git | https://git-scm.com |

### Verify installations
```powershell
java -version
mvn -version
docker --version
git --version
```

---

## 🚀 Getting Started

### 1. Clone the repository
```powershell
git clone https://github.com/yourusername/camunda-invoice-app.git
cd camunda-invoice-app
```

### 2. Start PostgreSQL with Docker
```powershell
docker run --name camunda-postgres `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=postgres `
  -e POSTGRES_DB=invoicedb `
  -p 5432:5432 `
  -d postgres:15
```

### 3. Build the project
```powershell
mvn package -DskipTests
```

### 4. Run the app
```powershell
mvn spring-boot:run
```

App starts on → **http://localhost:8080**

### 5. After PC reboot, restart the database
```powershell
docker start camunda-postgres
```

---

## 📡 API Reference

Base URL: `http://localhost:8080/api/invoices`

### Endpoints

| Method | URL | Description |
|---|---|---|
| POST | `/api/invoices` | Create and submit an invoice |
| GET | `/api/invoices` | Get all invoices |
| GET | `/api/invoices/{id}` | Get invoice by ID |
| POST | `/api/invoices/{id}/approve` | Approve an invoice |
| POST | `/api/invoices/{id}/reject` | Reject an invoice |

### Create Invoice — Request Body
```json
{
  "invoiceNumber": "INV-001",
  "vendorName": "Acme Corp",
  "amount": 1500.00
}
```

### Create Invoice — Response
```json
{
  "id": 1,
  "invoiceNumber": "INV-001",
  "vendorName": "Acme Corp",
  "amount": 1500.00,
  "status": "PENDING_APPROVAL",
  "processInstanceId": "abc123",
  "createdAt": "2024-01-15T10:30:00"
}
```

### PowerShell test commands
```powershell
# Create an invoice
Invoke-WebRequest -Uri "http://localhost:8080/api/invoices" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"invoiceNumber":"INV-001","vendorName":"Acme Corp","amount":1500.00}'

# Get all invoices
Invoke-WebRequest -Uri "http://localhost:8080/api/invoices" -Method GET

# Approve invoice with ID 1
Invoke-WebRequest -Uri "http://localhost:8080/api/invoices/1/approve" -Method POST

# Reject invoice with ID 1
Invoke-WebRequest -Uri "http://localhost:8080/api/invoices/1/reject" -Method POST
```

---

## 🔄 Workflow Process

```
[Start] → [Validate Invoice] → {Amount > 0?}
                                    │
                    ┌───── YES ──────┴───── NO ─────┐
                    ▼                                ▼
           [User Task: Review]            [Reject Invoice]
                    │                                │
                    ▼                                ▼
           [Approve Invoice]                      [End]
                    │
                    ▼
                  [End]
```

### Invoice Status Flow
```
CREATED → PENDING_APPROVAL → APPROVED
                           → REJECTED
```

### Delegates

| Class | Trigger | Action |
|---|---|---|
| `ValidateInvoiceDelegate` | Auto on process start | Checks amount > 0 |
| `ApproveInvoiceDelegate` | After user approves | Sets status to APPROVED |
| `RejectInvoiceDelegate` | When validation fails | Sets status to REJECTED |

---

## 🖥 Camunda Web Tools

| Tool | URL | Login |
|---|---|---|
| Cockpit | http://localhost:8080/camunda/app/cockpit | admin / admin |
| Tasklist | http://localhost:8080/camunda/app/tasklist | admin / admin |
| Admin | http://localhost:8080/camunda/app/admin | admin / admin |
| REST API | http://localhost:8080/engine-rest | — |

---

## 🗄 Database

### Connect directly
```powershell
docker exec -it camunda-postgres psql -U postgres -d invoicedb
```

### Useful queries
```sql
-- All invoices
SELECT * FROM invoices;

-- Active process instances
SELECT * FROM act_ru_execution;

-- Process history
SELECT * FROM act_hi_procinst;
```

### Camunda auto-created table groups
| Prefix | Description |
|---|---|
| `ACT_RE_*` | Repository — process definitions |
| `ACT_RU_*` | Runtime — active instances |
| `ACT_HI_*` | History — completed processes |
| `ACT_GE_*` | General — resources |

---

## 🔧 Troubleshooting

### `mvn` not recognized
```powershell
# Run PowerShell as Administrator
choco install maven -y
# Then close and reopen PowerShell
```

### PostgreSQL connection refused
```powershell
docker start camunda-postgres
```

### Port 8080 already in use
```powershell
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### VS Code red underlines on Java files
```powershell
mvn dependency:resolve
```
Then: `Ctrl+Shift+P` → `Java: Clean Java Language Server Workspace` → **Restart and delete**

### Camunda tables not created
Ensure `application.properties` contains:
```properties
camunda.bpm.database.schema-update=true
spring.jpa.hibernate.ddl-auto=update
```

### Process not deploying
Ensure these files exist:
```
src/main/resources/META-INF/processes.xml
src/main/resources/bpmn/invoice-approval.bpmn
```

---

## 📝 License

This project is for educational purposes.

---

## 👤 Author

**Your Name**
GitHub: [Ojasva Shrivastava](https://github.com/haveafreakyside28-alt)
