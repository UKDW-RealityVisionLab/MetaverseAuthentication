package org.ukdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ukdw.entity.TeacherEntity;


public interface TeacherRepository extends JpaRepository<TeacherEntity, Long> {
}
