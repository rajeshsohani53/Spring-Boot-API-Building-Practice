package com.rajesh;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
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

//get student by id 
@GetMapping("/students/{id}")
public Student getStudentById(@PathVariable int id)
{
	for(Student s:students)
	{
		if(s.getId()==id)
		{
			return s;
		}
	}
	return null;
}


@DeleteMapping("/students/{id}")
public String deletStudent(@PathVariable int id)
{
	students.removeIf(s->s.getId()==id);
	return "Deleted student with id " + id;
	
}


//update student 
@PutMapping("/students/{id}")
public Student updateStudent(@PathVariable int id, @RequestBody Student update)
{
	for(Student s:students)
	{
		if(s.getId()==id)
		{
			s.setName(update.getName());
			s.setCourse(update.getCourse());
			return s;
		}
	}
	return null;
}

}
