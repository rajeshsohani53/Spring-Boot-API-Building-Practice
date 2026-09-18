# Spring Boot REST API — Building Practice

A hands-on project where I built a REST API in Spring Boot from scratch, one concept at a time, to learn backend development the "understand why, not just how" way.

This is **Phase 1: an in-memory CRUD API with a simple HTML/JavaScript frontend.**
Phase 2 (upgraded version) rebuilds the same idea with a **MySQL database using Spring Data JPA** so data persists across restarts.

---

## What this project does

A simple Student Management REST API. You can:

- **Create** a student (POST)
- **Read** all students or one student by id (GET)
- **Update** a student (PUT)
- **Delete** a student (DELETE)

It comes with two web pages that call the API from the browser:

- `add.html` — a form to add a new student
- `list.html` — a table of all students, with per-row **Update** and **Delete** controls

---

## Tech stack

- **Java 17**
- **Spring Boot 3.x** (Spring Web)
- **Maven** (build tool)
- **Embedded Tomcat** (ships inside the app)
- **Jackson** (automatic JSON conversion)
- **HTML + vanilla JavaScript** (`fetch` API) for the frontend

> Note: Phase 1 stores data in an in-memory `List`, so data resets when the app restarts. This is fixed in Phase 2 with MySQL + JPA.

---

## API endpoints

| Operation | Method | URL | Body |
|-----------|--------|-----|------|
| Create | POST | `/students` | JSON student |
| Read all | GET | `/students` | — |
| Read one | GET | `/students/{id}` | — |
| Update | PUT | `/students/{id}` | JSON student |
| Delete | DELETE | `/students/{id}` | — |

Student JSON shape:
```json
{ "id": 1, "name": "Raj", "course": "Java Full Stack" }
```

---

## How to run

1. Make sure you have **Java 17+** installed (`java -version`).
2. Clone the repo and open it in your IDE (IntelliJ / Eclipse / STS) as a Maven project.
3. Run `DemoApiApplication.java`.
4. Wait for the console to show `Tomcat started on port(s): 8080`.
5. Open in your browser:
   - Add a student: `http://localhost:8080/add.html`
   - View all students: `http://localhost:8080/list.html`

To test the API directly with curl:
```
curl -X POST http://localhost:8080/students -H "Content-Type: application/json" -d "{\"id\":1,\"name\":\"Raj\",\"course\":\"Java Full Stack\"}"
curl http://localhost:8080/students
```

---

## What I learned (Phase 1)

- What a REST API is — resources + HTTP verbs (GET/POST/PUT/DELETE)
- **`@RestController`** and how it returns data directly as the response body
- **`@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping`** — routing by HTTP verb
- How **Jackson** auto-converts Java objects to JSON (using getters) and JSON back to objects (using setters + a no-arg constructor)
- **`@RequestBody`** (data from the request body) vs **`@PathVariable`** (data from the URL path)
- Serving static files from `src/main/resources/static/`
- The **same-origin / CORS** rule (why pages must be opened via `http://localhost:8080/`, not `file://`)
- Calling an API from the frontend with the JavaScript `fetch` API
- A real debugging lesson: always read the response **status**, don't trust a "success" label

Detailed step-by-step learning notes are in the notes file in this repo.

---

## Roadmap

- [x] **Phase 1** — In-memory CRUD REST API + HTML/JS frontend
- [ ] **Phase 2** — Persist data with **MySQL + Spring Data JPA** (entities, repositories, no more data loss on restart)

---

## Author

**Rajesh (Raj) Sohani** — Java Full Stack Developer
GitHub: [rajeshsohani53](https://github.com/rajeshsohani53)
