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

## Next up
- **Part 5:** POST endpoint (accept data from the client).
- **Part 6:** full CRUD with an in-memory list.
- **Part 7:** connect to MySQL with Spring Data JPA.
