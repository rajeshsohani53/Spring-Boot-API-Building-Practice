package com.rajesh;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rajesh.model.Student;

@RestController
public class HelloController {
@GetMapping("/hello")
public String sayHello()
{
	return "Hii Rajesh I'm Spring Boot";
}

@GetMapping("/student")
public Student getStudent()
{
	return new Student(1, "Rajesh", "java full stack");
}
}
