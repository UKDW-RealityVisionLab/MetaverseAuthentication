package org.ukdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ukdw.entity.ClassroomEntity;

@Repository
public interface ClassroomRepository extends JpaRepository<ClassroomEntity, Long> {
}
