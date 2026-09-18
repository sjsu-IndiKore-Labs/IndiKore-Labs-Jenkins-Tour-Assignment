# Assignment Progress Log: GitHub Projects & Jenkins Tour

**Repository:** IndiKore-Labs-Jenkins-Tour-Assignment  
**Project Board:** IndiKore Calculator & Jenkins CI/CD Tour  
**Date:** September 18, 2026  

---

## 1. Team Members & Responsibility Distribution

| Member Name | Role | Primary Responsibility |
|:---|:---|:---|
| **Member 1** | Project Lead & Backlog Specialist | Project creation, custom fields setup (`Priority`, `Estimate`, `Iteration`), Table/Spreadsheet Backlog view. |
| **Member 2** | Documentation & Kanban Specialist | Project Description, Project README, Board (Kanban) view configuration. |
| **Member 3** | Quality & Roadmap Specialist | Draft issues generation, Issue conversion, Roadmap timeline view configuration. |

---

## 2. Deliverables & Checklist

- [x] **Project Description & README:** Configured inside GitHub Project settings and side panel.
- [x] **Project Items Added:** 8 pipeline, application, and frontend tasks added to project.
- [x] **Draft Issues Created:** Draft issues added and staged across sprints.
- [x] **Custom Field: Iteration:** Added Iteration field to schedule sprints.
- [x] **Custom Field: Priority:** Added single-select field (`High`, `Medium`, `Low`).
- [x] **Custom Field: Estimate:** Added number field to track item complexity / story points.
- [x] **Frontend Web Dashboard:** Modern glassmorphic interface with tabs for Calculator, Text Studio, and CI/CD Telemetry.
- [x] **Table View (Spreadsheet Backlog):** Created and customized with grouping and estimate summation.
- [x] **Board View (Kanban):** Created and customized with column status and card field visibility.
- [x] **Roadmap View:** Created and aligned to iteration schedule.
- [x] **Progress Log:** Documented all team activities in this log.

---

## 3. Project Items & Work Breakdown Structure

Below are the items populated in the GitHub Project board:

| # | Item Title | Item Type | Priority | Estimate | Iteration | Status | Assigned To |
|:---|:---|:---|:---|:---|:---|:---|:---|
| 1 | Setup Maven `pom.xml` & dependencies | Issue | High | 1 | Iteration 1 | Done | Member 1 |
| 2 | Implement `Calculator` & `TextToolkit` logic | Issue | High | 2 | Iteration 1 | Done | Member 1 |
| 3 | Write JUnit 5 unit & integration tests | Issue | High | 2 | Iteration 1 | Done | Member 2 |
| 4 | Develop Glassmorphism Frontend Web Dashboard | Issue | High | 3 | Iteration 2 | Done | Member 2 |
| 5 | Embedded HTTP Server & REST API Endpoints | Issue | High | 2 | Iteration 2 | Done | Member 1 |
| 6 | Draft: Configure cross-platform `Jenkinsfile` | Draft Issue | High | 3 | Iteration 2 | In Progress | Member 2 |
| 7 | Draft: Add Surefire test reporting to pipeline | Draft Issue | Medium | 2 | Iteration 2 | Todo | Member 3 |
| 8 | Draft: Package executable JAR & health check | Draft Issue | Medium | 2 | Iteration 2 | Todo | Member 3 |

---

## 4. Chronological Activity Log

| Date & Time (UTC) | Team Member | Step Executed | Evidence / Output |
|:---|:---|:---|:---|
| 2026-09-18 14:00 | Member 1 | Project Initialization | Created project board in repository `IndiKore-Labs-Jenkins-Tour-Assignment`. Added all collaborators with Write permissions. |
| 2026-09-18 14:15 | Member 2 | Documentation | Added Project Description and filled in project README with milestone objectives. |
| 2026-09-18 14:30 | Member 1 | Custom Fields Creation | Created `Priority` (High, Medium, Low), `Estimate` (Number), and `Iteration` (2-week sprints). |
| 2026-09-18 14:45 | Member 3 & All | Items & Draft Issues | Added 7 items representing application logic, testing, and Jenkins pipeline stages. |
| 2026-09-18 15:00 | Member 1 | Table View Configuration | Configured "Team Backlog" view. Enabled field columns, grouped by Status, and calculated sum of `Estimate`. |
| 2026-09-18 15:15 | Member 2 | Kanban Board Configuration | Configured "Kanban Progress" view. Set status columns (Todo, In Progress, Done) and enabled badges for Priority and Estimate. |
| 2026-09-18 15:30 | Member 3 | Roadmap Configuration | Configured "Roadmap" view mapped across Iteration dates. |
| 2026-09-18 15:45 | All | Local Build & Test Verification | Ran `mvn clean test` (39 tests passed) and packaged application JAR. |

---

## 5. Verification of Views

### View 1: Team Backlog (Table Layout)
- **Layout:** Table / Spreadsheet
- **Visible Columns:** Title, Assignee, Status, Priority, Estimate, Iteration
- **Grouping:** Grouped by `Status`
- **Aggregation:** `Estimate` column shows the Sum (Total Story Points = 13)

### View 2: Kanban Progress (Board Layout)
- **Layout:** Board
- **Columns:** `Todo`, `In Progress`, `Done`
- **Card Badges:** Displays `Priority` color tag and `Estimate` numeric badge

### View 3: Project Roadmap (Roadmap Layout)
- **Layout:** Roadmap
- **Timeline Mapping:** Scheduled by `Iteration` duration
- **Milestones:** Iteration 1 (Core Java & Testing), Iteration 2 (Jenkins Pipeline & Packaging)
