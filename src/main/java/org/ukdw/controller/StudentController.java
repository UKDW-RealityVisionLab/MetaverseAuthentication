package org.ukdw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ukdw.entity.StudentEntity;
import org.ukdw.services.StudentService;

import java.util.Optional;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,STUDENT', 511L, 1L)")
    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,STUDENT', 511L, 1L)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        return Optional.ofNullable(studentService.findById(id))
                .map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

//    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody StudentEntity studentEntity) {
        return new ResponseEntity<>(studentService.save(studentEntity), HttpStatus.CREATED);
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,STUDENT', 511L, 1L)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody StudentEntity studentEntity) {
        return new ResponseEntity<>(studentService.update(id, studentEntity), HttpStatus.OK);
    }

    /*@PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,STUDENT', 511L, 1L)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/
}
