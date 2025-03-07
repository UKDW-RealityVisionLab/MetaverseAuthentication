package org.ukdw.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "attendance_record")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ATTENDANCE_RECORD_SEQ")
    @SequenceGenerator(name = "ATTENDANCE_RECORD_SEQ", sequenceName = "ATTENDANCE_RECORD_SEQ", allocationSize = 1)
    private Long id;

    private Long studentId;
    private Instant attendanceTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    @JsonBackReference // Prevent recursion on the child side
    private AttendanceEntity attendance;

    public AttendanceRecord(){}

    public AttendanceRecord(
            Long studentId,
            Instant attendanceTime
    ){
        this.studentId = studentId;
        this.attendanceTime = attendanceTime;
    }

}
