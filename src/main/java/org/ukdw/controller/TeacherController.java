package org.ukdw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ukdw.dto.user.TeacherDTO;
import org.ukdw.entity.TeacherEntity;
import org.ukdw.services.TeacherService;

import java.util.Optional;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER', 511L, 3L)")
    @GetMapping
    public ResponseEntity<?> getAllTeachers() {
        return new ResponseEntity<>(teacherService.findAll(), HttpStatus.OK);
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER', 511L, 3L)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable Long id) {
        return Optional.ofNullable(teacherService.findById(id))
                .map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createTeacher(@RequestBody TeacherEntity teacherEntity) {
        return new ResponseEntity<>(teacherService.save(teacherEntity), HttpStatus.CREATED);
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER', 511L, 3L)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO teacher) {
        return new ResponseEntity<>(teacherService.update(id, teacher), HttpStatus.OK);
    }

    /*@PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER', 511L, 3L)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        teacherService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/
}
