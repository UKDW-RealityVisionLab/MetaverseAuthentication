package org.ukdw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ukdw.dto.request.CreateAttendanceRequest;
import org.ukdw.dto.request.StudentAttendanceRequest;
import org.ukdw.dto.request.UpdateAttendanceRequest;
import org.ukdw.entity.AttendanceEntity;
import org.ukdw.exception.ResourceNotFoundException;
import org.ukdw.services.implementation.AttendanceServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/attendances")
public class AttendanceController {

    @Autowired
    private AttendanceServiceImpl attendanceServiceImpl;

    // Create attendance
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER', 511L, 3L)")
    @PostMapping()
    public ResponseEntity<AttendanceEntity> createAttendance(
            @RequestBody CreateAttendanceRequest request) {
        AttendanceEntity attendance = attendanceServiceImpl.createAttendance(request.getClassroomId(), request.getOpenTime(), request.getCloseTime());
        return ResponseEntity.ok(attendance);
    }

    // Delete attendance
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER', 511L, 3L)")
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId) {
        attendanceServiceImpl.deleteAttendance(attendanceId);
        return ResponseEntity.noContent().build();
    }

    // Edit attendance
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER', 511L, 3L)")
    @PutMapping("/{attendanceId}")
    public ResponseEntity<AttendanceEntity> editAttendance(
            @PathVariable Long attendanceId,
            @RequestBody UpdateAttendanceRequest request) {
        AttendanceEntity updatedAttendance = attendanceServiceImpl.editAttendance(attendanceId, request.getOpenTime(), request.getCloseTime());
        return ResponseEntity.ok(updatedAttendance);
    }

    // Get all attendance records
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER,STUDENT', 511L, 3L, 1L)")
    @GetMapping
    public ResponseEntity<List<AttendanceEntity>> getAllAttendances() {
        List<AttendanceEntity> attendances = attendanceServiceImpl.getAllAttendances();
        return ResponseEntity.ok(attendances);
    }

    // Get attendance by ID
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER,STUDENT', 511L, 3L, 1L)")
    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceEntity> getAttendanceById(@PathVariable Long attendanceId) {
        AttendanceEntity attendance = attendanceServiceImpl.getAttendanceById(attendanceId);
        return ResponseEntity.ok(attendance);
    }

    // Get attendance by classroom ID
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER,STUDENT', 511L, 3L, 1L)")
    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<?> getAttendanceByClassroomId(@PathVariable Long classroomId) {
        Optional<List<AttendanceEntity>> attendances = attendanceServiceImpl.getAttendanceByClassroomId(classroomId);
        return attendances.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id " + classroomId));
    }

    @PostMapping("/student/{attendanceId}")
    public ResponseEntity<?> setStudentAttendanceRecord(
            @PathVariable Long attendanceId,
            @RequestBody StudentAttendanceRequest request
    ){
        attendanceServiceImpl.setStudentAttendance(attendanceId, request.getStudentId());
        return ResponseEntity.ok("Success");
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN,TEACHER', 511L, 3L)")
    @DeleteMapping("/student/{attendanceId}")
    public ResponseEntity<?> deleteStudentAttendanceRecord(
            @PathVariable Long attendanceId,
            @RequestBody StudentAttendanceRequest request
    ){
        attendanceServiceImpl.deleteStudentAttendance(attendanceId, request.getStudentId());
        return ResponseEntity.ok("Success");
    }
}