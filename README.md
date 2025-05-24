# Spring Boot BFF with Keycloak Authentication

## Overview

This project is a Backend-for-Frontend (BFF) built with Spring Boot and secured with Keycloak.  
It provides authentication and API endpoints for a frontend client.

---

## How to Run

### Prerequisites
- Java 17+
- Maven
- Running Keycloak server with configured realm and client

### Setup Environment Variables

Create environment variables or use a `.env` file:

```bash
export KEYCLOAK_URL=https://your-keycloak-url
export KEYCLOAK_REALM=your-realm
export KEYCLOAK_CLIENT=your-client-id
export KEYCLOAK_SECRET=your-client-secret
export PORT=8081
