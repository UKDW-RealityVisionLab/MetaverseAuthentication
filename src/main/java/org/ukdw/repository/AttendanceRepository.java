package org.ukdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.ukdw.entity.AttendanceEntity;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

    @Query("SELECT a FROM AttendanceEntity a WHERE a.classroom.id = :classroomId AND :now BETWEEN a.openTime AND a.closeTime")
    Optional<AttendanceEntity> findActiveAttendance(Long classroomId, LocalDateTime now);

    Optional<List<AttendanceEntity>> findByClassroomId(Long classroomId);

}
