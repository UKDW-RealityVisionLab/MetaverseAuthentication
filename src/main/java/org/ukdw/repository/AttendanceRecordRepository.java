package org.ukdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ukdw.entity.AttendanceEntity;
import org.ukdw.entity.AttendanceRecord;


import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByAttendance(AttendanceEntity attendanceEntity);

    Optional<AttendanceRecord> findByAttendanceIdAndStudentId(Long attendanceId, Long studentId);

    void deleteByAttendanceIdAndStudentId(Long attendanceId, Long studentId);
}
