package org.ukdw.services;



import org.ukdw.entity.AttendanceEntity;

import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    public AttendanceEntity createAttendance(Long classroomId, String openTimeStr, String closeTimeStr);
    public void deleteAttendance(Long attendanceId);
    public AttendanceEntity editAttendance(Long attendanceId, String openTimeStr, String closeTimeStr);
    public List<AttendanceEntity> getAllAttendances();
    public AttendanceEntity getAttendanceById(Long attendanceId);
    public Optional<List<AttendanceEntity>> getAttendanceByClassroomId(Long classroomId);
    public void deleteAttendancesByClassroomId(Long classroomId);
    public void setStudentAttendance(Long attendanceId, Long studentId);
    public void deleteStudentAttendance(Long attendanceId, Long studentId);


}
