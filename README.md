# IndiKore Calculator & Text Toolkit

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)]()
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)]()
[![Build Tool](https://img.shields.io/badge/Maven-3.9%2B-orange.svg)]()
[![Testing](https://img.shields.io/badge/Tests-39%20passed-success.svg)]()

A lightweight Java and Maven utility built to demonstrate an automated **Jenkins CI/CD pipeline**, version control workflows, and project planning using **GitHub Projects**.

---

## Features
- **Calculator Utility (`Calculator.java`)**:
  - Basic arithmetic: addition, subtraction, multiplication, and division (with zero-division protection).
  - Advanced operations: power calculation, factorial (with overflow detection), and prime number verification.
- **Text Processing Toolkit (`TextToolkit.java`)**:
  - String reversal, palindrome validation (ignoring non-alphanumeric chars), word counting, and title casing.
- **Automated Testing (`JUnit 5`)**:
  - 44 automated unit and integration test cases with 100% pass rate.
- **Modern Glassmorphic Web Dashboard**:
  - Live interactive web UI served on port `8080` with zero external dependencies via JDK `HttpServer`.
  - **Math Engine Tab**: interactive computation with step-by-step history log.
  - **Text Studio Tab**: live string reversal, palindrome detection radar, and word counter.
  - **CI/CD Telemetry Tab**: real-time pipeline status, uptime, latency, and `/api/health` monitoring.
- **Executable CLI & Server (`App.java`)**:
  - Packaged into an executable JAR file with manifest entry point.

---

## Quick Start (Local)

### Prerequisites
- JDK 17 or higher
- Apache Maven 3.8+

### Build & Run
```bash
# 1. Compile and execute all 44 JUnit 5 tests
mvn clean test

# 2. Package into a runnable JAR
mvn package

# 3. Launch Web Dashboard on http://localhost:8080/
java -jar target/indikore-calculator-toolkit-1.0.0.jar

# (Optional) Run in headless CLI verification mode:
java -jar target/indikore-calculator-toolkit-1.0.0.jar --cli
```

---

## Jenkins CI/CD Pipeline Architecture

The declarative [`Jenkinsfile`](./Jenkinsfile) automatically executes the following stages on every commit:

```
[Checkout] ➔ [Compile] ➔ [Run Unit Tests] ➔ [Package Artifact] ➔ [Smoke Test] ➔ [Archive JAR]
```

- **Checkout**: Pulls the latest commit from GitHub.
- **Compile**: Compiles Java source files with `mvn clean compile`.
- **Run Unit Tests**: Executes all 39 tests and collects JUnit test XML reports.
- **Package Artifact**: Builds `indikore-calculator-toolkit-1.0.0.jar`.
- **Smoke Test**: Executes the standalone JAR to verify operational readiness.
- **Archive**: Archives the generated `.jar` file in Jenkins build artifacts.
