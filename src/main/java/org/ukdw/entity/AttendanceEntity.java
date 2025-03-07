package org.ukdw.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "attendance")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "classroom_id", nullable = false)
//    @JsonBackReference // Prevent recursion on the back side of the relationship
    private ClassroomEntity classroom;

    @Column(name = "open_time", nullable = false)
    private Instant openTime;

    @Column(name = "close_time", nullable = false)
    private Instant closeTime;

    @OneToMany(mappedBy = "attendance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonManagedReference // Manage serialization on the parent side
    private Set<AttendanceRecord> records = new HashSet<>();

}
