package org.ukdw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ukdw.entity.ResourceEntity;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {

    Optional<ResourceEntity> findByResourceBitmask(Long permission);
    Optional<ResourceEntity> findByResourceName(String resourceName);
//    void deleteByResourceBitmask(Long resourceBitmask);
//    void deleteByResourceName(String resourceName);
}
