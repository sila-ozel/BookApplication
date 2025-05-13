# 🛡️ Secure Login System with Role-Based Access

This is a full-stack login and registration system built using **Spring Boot (Java)** for the backend and **React.js** for the frontend. It includes authentication via JWT, role-based access control (User/Admin), secure password storage, and password history tracking.

---

## 📌 Features

- 🔐 **JWT-Based Authentication**: Protects secure endpoints using JSON Web Tokens.
- 👥 **Role-Based Access Control**: Users can register as either `ROLE_USER` or `ROLE_ADMIN`.
- 📝 **Password Policy Enforcement**:
  - Must contain at least one digit.
  - Must contain at least one uppercase letter.
- 📚 **Password History Tracking**:
  - The last 3 passwords are stored to prevent reuse.
- ⏰ **Time Slot Configuration**:
  - Admins can define configurable values like time limits.
- 🚫 **Change Password on First Login**:
  - Users can be flagged to change passwords upon their first login.

---

## 🧪 Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security
- JPA / Hibernate
- H2 / MySQL (configurable)
- JWT for secure authentication

### Frontend
- React.js
- Axios for HTTP calls
- React Router for navigation

---

## 🚀 Running the Application

### Prerequisites
- Java 17+
- Node.js 16+
- Maven

### Backend Setup

```bash
cd backend
mvn clean install
mvn spring-boot:run
