# 🎬 MovieFlix API

A secure and scalable RESTful API built with **Spring Boot** for managing movie data, authentication, and file uploads.

---

## 📌 Project Overview

**MovieFlix API** is designed to manage movie information with full CRUD operations, authentication, and file handling support.

### 🎯 Objectives
- Provide RESTful APIs for movie management
- Support image/file upload
- Secure endpoints using JWT Authentication
- Implement Exception Handling
- Support Pagination & Sorting
- Serve as backend for frontend applications

---

## 🚀 Features

| # | Feature | Description | Status |
|--|--------|------------|--------|
| 1 | Movie Management | CRUD operations for movies | ✅ |
| 2 | Pagination & Sorting | Filter by title, date, rating | ✅ |
| 3 | File Upload | Upload images/documents | ✅ |
| 4 | Exception Handling | Handle errors properly | ✅ |
| 5 | Authentication | Secure API with JWT | ✅ |

---

## 🔐 Authentication API

**Base URL**  http://localhost:8080/api/v1/auth


| Method | Endpoint | Description | Auth |
|--------|----------|------------|------|
| POST | /register | Register user | ❌ |
| POST | /login | Login & get JWT | ❌ |

---

## 🎥 Movie API

**Base URL** http://localhost:8080/api/v1/movie


| Method | Endpoint | Description | Auth |
|--------|----------|------------|------|
| GET | /all | Get all movies | ✅ ADMIN |
| GET | /{id} | Get movie by ID | ✅ ADMIN |
| GET | /allMoviesPage | Pagination | ✅ ADMIN |
| GET | /allMoviesPageSort | Pagination + Sort | ✅ ADMIN |
| POST | /add-movie | Create movie | ✅ ADMIN |
| PUT | /update/{id} | Update movie | ✅ ADMIN |
| DELETE | /delete/{id} | Delete movie | ✅ ADMIN |

---

## 📁 File Upload API

**Base URL** http://localhost:8080/file


| Method | Endpoint | Description | Auth |
|--------|----------|------------|------|
| POST | /upload | Upload file | ❌ |
| GET | /{fileName} | Get file | ❌ |

---

## 🔐 Security & Authentication

### 🔄 Authentication Flow
1. User logs in via `/auth/login`
2. Server validates credentials
3. JWT token is generated
4. Token returned to client

### 🔁 Authorized Requests
Client must include: Authorization: Bearer <jwt-token>


---

## 👥 Role-Based Access Control (RBAC)

| Role | Permission |
|------|------------|
| ADMIN | Full CRUD access |
| USER | Read-only access |

---

## 🛡️ Security Implementation

- Spring Security for authentication & authorization
- JWT (stateless authentication)
- Role-based access using `@PreAuthorize`
- Password encryption using **BCrypt**

---

## 🧠 Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- MySQL

---

## 📌 Author

**Yan Pich**  
Backend Developer | Java Spring Boot
