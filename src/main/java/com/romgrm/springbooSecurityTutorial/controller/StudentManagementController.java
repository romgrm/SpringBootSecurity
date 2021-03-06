package com.romgrm.springbooSecurityTutorial.controller;

import com.romgrm.springbooSecurityTutorial.models.Student;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {
    private static final List<Student> STUDENTS = Arrays.asList(
            new Student(1, "Gréaume Romain"),
            new Student(2, "Quellec Marie"),
            new Student(3, "Pierre-Paul Jacques")
    );


    // GET
    @GetMapping
    public List<Student> getAllStudents(){return STUDENTS;}

    // CREATE
    @PostMapping
    public void registerStudent(@RequestBody Student student){
        System.out.println(student + "created");
    }

    //DELETE
    @DeleteMapping(path = "{studentId}")
    public void deleteStudent(@PathVariable("studentId") Integer studentId){
        System.out.println(studentId + "deleted");
    }

    // UPDATE
    @PutMapping(path = "{studentId}")
    public void updateStudent(@PathVariable("studentId") Integer studentId,@PathVariable("student") Student student){
        System.out.println(String.format("%s %s", studentId, student));
    }
}
