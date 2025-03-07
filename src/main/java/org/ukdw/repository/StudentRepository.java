package org.ukdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ukdw.entity.StudentEntity;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}
