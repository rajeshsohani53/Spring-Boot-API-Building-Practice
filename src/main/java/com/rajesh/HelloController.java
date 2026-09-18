package com.rajesh;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rajesh.model.Student;

@RestController
public class HelloController {
	
	private List<Student> students = new ArrayList<>();	
@GetMapping("/hello")
public String sayHello()
{
	return "Hii Rajesh I'm Spring Boot";
}

@GetMapping("/students")
public List<Student> getAllStudents() {
    return students;
}

@PostMapping("/students")
public Student addStudent(@RequestBody Student student) {
    students.add(student);
    return student;
}
}
