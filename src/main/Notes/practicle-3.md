# Spring Boot REST API — Learning Notes

My hands-on practice building a simple REST API in Spring Boot, one part at a time.

---

## Part 1 — What an API is (concept)

- An API is like a **waiter** in a restaurant: the client (browser/app) is the customer, the server (my code + DB) is the kitchen, and the API is the waiter carrying requests in and responses out.
- An API = a set of rules for how one program asks another program for something.
- **REST** is a style: everything is a **resource** (student, product, booking), and I act on resources using **HTTP methods**.

| Action | HTTP method | CRUD |
|--------|-------------|------|
| Get data | GET | Read |
| Create data | POST | Create |
| Update data | PUT | Update |
| Delete data | DELETE | Delete |

- Request/response loop: client sends request to a URL + method → server runs code → server returns JSON + a status code (200 OK, 201 Created, 404 Not Found).
- **One-liner:** A REST API exposes resources over HTTP, where the URL identifies the resource and the HTTP method says what to do with it, and data flows as JSON.

---

## Part 2 — Project setup

- Generated the project from **https://start.spring.io** (Spring Initializr).
- Settings: Maven, Java, Spring Boot 3.x, Group `com.rajesh`, Artifact `demoApi`, Jar, Java 17.
- **Key dependency: Spring Web** (`spring-boot-starter-web`) — this gives REST support + an embedded Tomcat server.
- **Mistake I fixed:** my first `pom.xml` had plain `spring-boot-starter` (no web). Without `-web` there is no Tomcat and no API. Fixed by changing it to `spring-boot-starter-web`, then reloaded Maven.

### Main class
```java
@SpringBootApplication
public class DemoApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApiApplication.class, args);
    }
}
```

- **`@SpringBootApplication`** = 3 annotations in one: `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`.
- `SpringApplication.run(...)` boots the embedded Tomcat server.
- Difference from old JSP/Servlet projects: the Tomcat server ships **inside** the app (embedded), instead of deploying a WAR to an external Tomcat.

### Result
- App runs → console shows **"Tomcat started on port(s): 8080"**.
- Visiting `http://localhost:8080` shows a **Whitelabel Error Page (404)** — this is SUCCESS: the server is alive, it just has no endpoint mapped yet.

---

## Part 3 — First GET endpoint

### Code
```java
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Raj, your API is working!";
    }
}
```

- Controller must be in the same package as the main class (or a sub-package) so `@ComponentScan` finds it.
- Restart the app (Spring Boot does not auto-reload by default).
- Visit `http://localhost:8080/hello` → shows **"Hello Raj, your API is working!"** — my first working endpoint.

### Key concepts (interview-ready)

**`@RestController`** (class-level):
- Marks the whole CLASS as a REST request handler (not "an endpoint" — the endpoint is the method).
- Combines `@Controller` + `@ResponseBody`. The `@ResponseBody` part is what sends the return value directly as the HTTP response body instead of resolving to a JSP/view.
- It is the ENTRY POINT of the backend (where a request enters), not something that "calls" the backend.
- Returns whatever the method returns:
  - return a **String** → plain text (like `/hello`)
  - return an **object** → Spring auto-converts to **JSON** (via Jackson) — see Part 4.

**`@GetMapping("/hello")`** (method-level):
- Routing: "when a GET request hits `/hello`, run this method."
- `/hello` is the address clients use. Changing it moves the endpoint.

### How routing works
Client sends a request → Spring matches the URL + HTTP method to the right method → runs that method → returns the response. One `@RestController` class can hold many endpoints (different URLs/methods).

---

## Part 4 — Return real JSON with a model class

### Model class (POJO)
```java
public class Student {
    private int id;
    private String name;
    private String course;

    public Student(int id, String name, String course) {
        this.id = id;
        this.name = name;
        this.course = course;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCourse() { return course; }
}
```

### Endpoint returning an object
```java
@GetMapping("/student")
public Student getStudent() {
    return new Student(1, "Raj", "Java Full Stack");
}
```
- Visit `http://localhost:8080/student` → `{"id":1,"name":"Raj","course":"Java Full Stack"}` — real JSON, generated automatically from the Java object.
- Returning `List<Student>` → produces a JSON array `[{...},{...}]`. Jackson handles collections too.

### Key concept — how Spring makes JSON
- **Jackson** is Spring Boot's built-in JSON library (ships with `spring-boot-starter-web`, no config needed).
- When a `@RestController` method returns an **object**, Jackson serializes it to JSON by **calling the getters** (`getName()` → `"name"` field).
- **No getters = empty JSON `{}`** — Jackson can't see inside the object without them. (Real gotcha.)

### BIG insight — `toString()` vs Jackson (two separate worlds)
- `Student@1a2b3c` is the default `Object.toString()` output = `ClassName@hashCodeInHex` (shown when you override nothing).
- BUT in a REST controller, **`toString()` is NEVER called.** Overriding it makes **zero difference** to the JSON.
- Two completely separate mechanisms:

| Situation | What is used | Output |
|-----------|--------------|--------|
| `System.out.println(student)` | `toString()` | `Student@1a2b3c` (or my override) |
| `return student;` from `@RestController` | **Jackson + getters** | `{"id":1,"name":"Raj",...}` |

- **Proof:** override `toString()` to return `"I AM A STUDENT"` → JSON is still `{"id":1,...}` (toString ignored). Delete a getter → that field disappears from JSON (getters drive it).
- **Precise answer:** "It's not about `toString()`. In plain Java, printing the object calls default `Object.toString()` → `Student@hashcode`. In a REST controller, Spring passes the object to Jackson, which serializes to JSON by reading the getters; `toString()` is never involved."

**Remember:** `toString()` = text-printing world. Jackson + getters = JSON serialization world. Keep them separate.

---

## Part 5 — POST endpoint (client sends data)

### Storage (in memory, for now)
```java
private List<Student> students = new ArrayList<>();
```
- Lives in memory → resets on every app restart. MySQL fixes this in Part 7.

### Student class needs setters + no-arg constructor now
```java
public Student() { }   // no-arg constructor — Jackson needs it to create an empty object
// ... existing constructor + getters ...
public void setId(int id) { this.id = id; }
public void setName(String name) { this.name = name; }
public void setCourse(String course) { this.course = course; }
```

### POST + GET endpoints
```java
@PostMapping("/students")
public Student addStudent(@RequestBody Student student) {
    students.add(student);
    return student;
}

@GetMapping("/students")
public List<Student> getAllStudents() {
    return students;
}
```

### Key concepts (interview-ready)

**`@PostMapping("/students")`**
- Routes POST requests for `/students` to this method — used to create/save a new student.
- Differs from `@GetMapping` only in the HTTP method it listens for: GET reads, POST sends data.
- **Same URL can serve GET and POST.** Spring routes by the **HTTP method (verb), NOT by whether data was sent.** `GET /students` and `POST /students` are two distinct endpoints.

**`@RequestBody Student student`**
- Takes the JSON in the request **body** and tells Jackson to deserialize it into a `Student` object, passed in as the parameter.
- It does ONLY the JSON→object conversion. It does NOT save anything — `students.add(student)` is separate logic I wrote. `@RequestBody` never touches storage/DB.

### Getters vs setters — the symmetry
- **Getters write JSON OUT** (object → JSON, Part 4).
- **Setters + no-arg constructor read JSON IN** (JSON → object, Part 5).
- Jackson runs BOTH directions: JS object → JSON string → Java object (in), and Java object → JSON (out).

### Testing a POST
- A browser address bar can only send **GET**. POST needs a tool.
- Options: **curl** (built into Windows 10), VS Code REST Client extension, or Postman.
- curl example (Windows — escape inner quotes with `\"`):
```
curl -X POST http://localhost:8080/students -H "Content-Type: application/json" -d "{\"id\":1,\"name\":\"Raj\",\"course\":\"Java Full Stack\"}"
```
- The `Content-Type: application/json` header is REQUIRED, or you get a **415** error.

---

## Part 5b — Simple frontend (HTML + JavaScript)

Built two pages to hit the API from a real UI instead of curl/Postman.

### Where the files MUST live
```
src/main/resources/static/add.html
src/main/resources/static/list.html
```
- Spring Boot automatically serves anything in `src/main/resources/static/`.
- A file there becomes `http://localhost:8080/add.html`.

### CORS / same-origin — the big rule
- **Open pages via `http://localhost:8080/add.html`, NOT by double-clicking (`file:///C:/add.html`).**
- If the page and API are on the **same origin** (both `localhost:8080`), the browser lets JavaScript call the API freely.
- Different origins (`file://`, or a page on another port) → browser **blocks** the call with a **CORS error**.
- **Rule:** if the address bar starts with `file://` it's WRONG. It must start with `http://localhost:8080/`.

### The client-side fetch (POST)
```javascript
const response = await fetch("/students", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(student)
});
```
- `fetch("/students", ...)` → JS sends an HTTP request to the API (relative URL works because same-origin).
- `method: "POST"` → makes Spring route to `addStudent()` (verb decides the endpoint — same rule as backend).
- `headers Content-Type: application/json` → tells server the body is JSON (else 415).
- `body: JSON.stringify(student)` → JS object → JSON string to travel over the network. Server reverses it via `@RequestBody` + Jackson.

### The client-side fetch (GET)
```javascript
const response = await fetch("/students");   // no method = GET by default
const students = await response.json();      // parse JSON array into a JS array
```

### DEBUGGING LESSON — read the response, not the label
- Hit a bug: page said **"Saved successfully"** but the JSON body was actually:
```json
{ "timestamp": "...", "status": 404, "error": "Not Found", "path": "/students" }
```
- Cause: the `@PostMapping("/students")` endpoint didn't exist yet / app not restarted → 404. The frontend was ahead of the backend.
- Lesson: **always read the `status` field**, don't trust a "success" label. 404 = endpoint not found, 200 = OK, 201 = created, 400/415 = bad request body.
- My JS flaw: it should check `response.ok` before saying "saved" (improvement to make later).
- Fix that worked: make sure the POST/GET endpoints + storage list exist in `HelloController`, `Student` has setters + no-arg constructor, then **restart the app**.

---

## Next up
- **Part 6:** full CRUD (add PUT to update, DELETE to remove, GET by id) + fix the JS to check `response.ok`.
- **Part 7:** connect to MySQL with Spring Data JPA (real persistence — no more data loss on restart).
