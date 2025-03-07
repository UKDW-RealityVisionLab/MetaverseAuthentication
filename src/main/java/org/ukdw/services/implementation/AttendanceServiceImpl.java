package org.ukdw.services.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ukdw.entity.AttendanceEntity;
import org.ukdw.entity.AttendanceRecord;
import org.ukdw.entity.ClassroomEntity;
import org.ukdw.exception.AccessDeniedException;
import org.ukdw.exception.RequestParameterErrorException;
import org.ukdw.exception.ResourceNotFoundException;
import org.ukdw.repository.AttendanceRecordRepository;
import org.ukdw.repository.AttendanceRepository;
import org.ukdw.repository.ClassroomRepository;
import org.ukdw.services.AttendanceService;
import org.ukdw.services.ClassroomService;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    @Lazy
    private ClassroomService classroomService;

    @Override
    @Transactional
    public AttendanceEntity createAttendance(Long classroomId, String openTimeStr, String closeTimeStr) {
        // Find the classroom by ID to associate with the attendance
        ClassroomEntity classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id " + classroomId));

        // Create the new attendance entity
        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setClassroom(classroom);

        Instant openTime = convertToInstant(openTimeStr);
        Instant closeTime = convertToInstant(closeTimeStr);

        attendance.setOpenTime(openTime);
        attendance.setCloseTime(closeTime);

        // Save and return the created attendance entity
        return attendanceRepository.save(attendance);
    }

    @Override
    public void deleteAttendance(Long attendanceId) {
        // Check if attendance exists
        AttendanceEntity attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id " + attendanceId));

        // Delete the attendance record
        attendanceRepository.delete(attendance);
    }

    @Override
    public AttendanceEntity editAttendance(Long attendanceId, String openTimeStr, String closeTimeStr) throws RequestParameterErrorException {
        // Find the existing attendance
        AttendanceEntity attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id " + attendanceId));

        // Convert string to Instant (Zulu time) and validate the format
        Instant openTime = convertToInstant(openTimeStr);
        Instant closeTime = convertToInstant(closeTimeStr);

        // Update open and close times
        attendance.setOpenTime(openTime);
        attendance.setCloseTime(closeTime);

        // Save and return the updated attendance entity
        return attendanceRepository.save(attendance);
    }

    @Override
    public List<AttendanceEntity> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    @Override
    public AttendanceEntity getAttendanceById(Long attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id " + attendanceId));
    }

    @Override
    public Optional<List<AttendanceEntity>> getAttendanceByClassroomId(Long classroomId) {
        Optional<List<AttendanceEntity>> attendanceOpt = attendanceRepository.findByClassroomId(classroomId);
        if(attendanceOpt.isEmpty()){
            throw new ResourceNotFoundException("Classroom not found with id "+ classroomId);
        }

        if(attendanceOpt.get().isEmpty()){
            throw new ResourceNotFoundException("Classroom not found with id "+ classroomId);
        }

        return attendanceRepository.findByClassroomId(classroomId);
    }

    @Override
    public void deleteAttendancesByClassroomId(Long classroomId) {
        // Fetch the classroom to ensure it exists
        classroomRepository.findById(classroomId)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with id " + classroomId));

        // Find all attendance records for the given classroom ID
        Optional<List<AttendanceEntity>> attendances = attendanceRepository.findByClassroomId(classroomId);

        // Delete all attendance records for the classroom
        if (attendances.isPresent()) {
            attendanceRepository.deleteAll(attendances.get());
        } else {
            throw new ResourceNotFoundException("No attendance records found for classroom id " + classroomId);
        }
    }

    @Override
    public void setStudentAttendance(Long attendanceId, Long studentId){
        // Find the attendance record by the given attendanceId
        AttendanceEntity attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id " + attendanceId));

        // Before set attendance, check if the student is enrolled to this classroom
        boolean isStudentEnrolled = classroomService.isStudentEnrolled(attendance.getClassroom().getId(), studentId);
        if(!isStudentEnrolled){
            throw new AccessDeniedException("studentId: " + studentId + " is not enrolled in this class");
        }

        // Check if the student has already marked attendance
        Optional<AttendanceRecord> existingRecord = attendanceRecordRepository.findByAttendanceIdAndStudentId(attendanceId, studentId);
        if (existingRecord.isPresent()) {
            throw new RequestParameterErrorException("Student has already marked attendance.");
        }

        // Create a new attendance record for the student
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setAttendance(attendance);
        attendanceRecord.setStudentId(studentId);
        attendanceRecord.setAttendanceTime(Instant.now()); // Set the current time as attendance time

        // Save the attendance record
        attendanceRecordRepository.save(attendanceRecord);
    }

    @Override
    public void deleteStudentAttendance(Long attendanceId, Long studentId){
        AttendanceEntity attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id " + attendanceId));

        // Check if the student has already marked attendance
        Optional<AttendanceRecord> existingRecord = attendanceRecordRepository.findByAttendanceIdAndStudentId(attendanceId, studentId);
        if (existingRecord.isEmpty()) {
            throw new ResourceNotFoundException("Student not found with id " + studentId);
        }

        attendanceRecordRepository.delete(existingRecord.get());

    }

    private Instant convertToInstant(String timeStr) {
        // Define the expected format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

        try {
            // Parse the string into a ZonedDateTime
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(timeStr, formatter);

            // Convert it to Instant (Zulu time, UTC)
            return zonedDateTime.toInstant();
        } catch (Exception e) {
            throw new RequestParameterErrorException("Invalid time format. Expected format: yyyy-MM-dd HH:mm:ss [Timezone]. Example: 2024-11-15 09:00:00 UTC or 2024-11-15 09:00:00 Asia/Jakarta");

        }
    }
}
