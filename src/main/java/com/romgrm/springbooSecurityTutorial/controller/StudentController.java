package com.romgrm.springbooSecurityTutorial.controller;

import com.romgrm.springbooSecurityTutorial.models.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/students")
public class StudentController {

    /*Création de la data*/
    private static final List<Student> STUDENTS = Arrays.asList(
            new Student(1, "Gréaume Romain"),
            new Student(2, "Quellec Marie"),
            new Student(3, "Pierre-Paul Jacques")
    );

    /*On créer une request Get by Id qui va nous retourner un tableau de type Students
    Ce tableau sera "stream" c-a-d que la data va être enveloppée afin de subir différentes opérations
    Ensuite on va "filtrer" le tableau en regardant si l'id entré en paramètre correspond à un Id dans
    le tableau existant. Si oui, on retourne le premier et le stream est terminé. Si non, retourne moi
    une exception*/
    @GetMapping(path = "{studentId}")
    public Student getStudentById(@PathVariable("studentId") Integer studentId){
        return STUDENTS.stream()
                .filter(item -> studentId.equals(item.getStudentId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Student" + studentId + "doesn't exist !"));
    }
}
