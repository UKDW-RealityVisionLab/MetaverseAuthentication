package org.ukdw.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ukdw.dto.classroom.ClassroomPublicDTO;
import org.ukdw.dto.request.AddRemoveClassroomStudentRequest;
import org.ukdw.dto.request.AddRemoveClassroomTeacherRequest;
import org.ukdw.dto.request.UpdateClassroomRequest;
import org.ukdw.dto.response.ResponseWrapper;
import org.ukdw.entity.ClassroomEntity;
import org.ukdw.exception.ResourceNotFoundException;
import org.ukdw.services.implementation.ClassroomServiceImpl;
import org.ukdw.util.ClassroomMapper;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/classroom")
public class ClassroomController {

    @Autowired
    private ClassroomServiceImpl classroomServiceImpl;

    // TODO: benerin! harus pake classroomDTO biar class name nya gaboleh null
    @PostMapping
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('TEACHER,ADMIN', 3L, 511L)")
    public ResponseEntity<ClassroomEntity> createClassroom(@RequestBody ClassroomEntity classroom) {
        ClassroomEntity createdClassroom = classroomServiceImpl.createClassroom(classroom);
        return new ResponseEntity<>(createdClassroom, HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('TEACHER,ADMIN,STUDENT', 3L, 511L, 1L)")
    public ResponseEntity<?> getAllClassroom() {
        List<ClassroomEntity> classrooms = classroomServiceImpl.getAllClassroom();
        List<ClassroomPublicDTO> dtoList = classrooms.stream()
                .map(ClassroomMapper::toPublicDTO)
                .collect(Collectors.toList());

        ResponseWrapper<List<ClassroomPublicDTO>> response = new ResponseWrapper<>(HttpStatus.OK.value(), dtoList);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('TEACHER,ADMIN,STUDENT', 3L, 511L, 1L)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getClassroomById(@PathVariable Long id) {
        Optional<ClassroomEntity> classroom = classroomServiceImpl.getClassroomById(id);
        return classroom.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id:" + id));
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('TEACHER,ADMIN', 3L, 511L)")
    @PutMapping("/{id}")
    public ResponseEntity<ClassroomEntity> updateClassroom(@PathVariable Long id, @RequestBody UpdateClassroomRequest request) {
        ClassroomEntity updated = classroomServiceImpl.updateClassroom(id, request);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('TEACHER,ADMIN', 3L, 511L)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        classroomServiceImpl.deleteClassroom(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/teacher/{classroomId}")
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('TEACHER,ADMIN', 3L, 511L)")
    public ResponseEntity<?> addTeacherToClassroom(@PathVariable Long classroomId, @Valid @RequestBody AddRemoveClassroomTeacherRequest request) {
        Boolean isAddTeacher = classroomServiceImpl.addTeacherToClassroom(classroomId, request.getTeacherId());
        if(!isAddTeacher){
            ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), isAddTeacher);
            return ResponseEntity.ok(response);
        }

        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),isAddTeacher);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/teacher/{classroomId}")
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('TEACHER,ADMIN', 3L, 511L)")
    public ResponseEntity<?> removeTeacherFromClassroom(@PathVariable Long classroomId, @Valid @RequestBody AddRemoveClassroomTeacherRequest request) {
        Boolean isAddTeacher = classroomServiceImpl.removeTeacherFromClassroom(classroomId, request.getTeacherId());
        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), isAddTeacher);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/student/{classroomId}")
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('TEACHER,ADMIN', 3L, 511L)")
    public ResponseEntity<?> addStudentToClassroom(@PathVariable Long classroomId, @Valid @RequestBody AddRemoveClassroomStudentRequest request) {
        Boolean isAddStudent = classroomServiceImpl.addStudentToClassroom(classroomId, request.getStudentId());
        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),isAddStudent);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/student/{classroomId}")
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('TEACHER,ADMIN', 3L, 511L)")
    public ResponseEntity<?> removeStudentFromClassroom(@PathVariable Long classroomId, @Valid @RequestBody AddRemoveClassroomStudentRequest request) {
        Boolean isAddStudent = classroomServiceImpl.removeStudentFromClassroom(classroomId, request.getStudentId());
        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),isAddStudent);
        return ResponseEntity.ok(response);
    }
}
