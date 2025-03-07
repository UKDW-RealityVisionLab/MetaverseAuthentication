package org.ukdw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ukdw.dto.ResourceDTO;
import org.ukdw.entity.ResourceEntity;
import org.ukdw.services.ResourceService;

import java.util.List;

@RestController()
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    // Create a new resource
    @PostMapping
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    public ResponseEntity<ResourceEntity> createResource(@Valid @RequestBody ResourceDTO resource) {
        ResourceEntity createdResource = resourceService.createResource(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResource);
    }

    // Get all resources
    @GetMapping
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    public ResponseEntity<List<ResourceEntity>> getAllResources() {
        List<ResourceEntity> resources = resourceService.getAllResources();
        return ResponseEntity.ok(resources);
    }

    // Get resource by ID
    @GetMapping("/{id}")
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    public ResponseEntity<ResourceEntity> getResourceById(@PathVariable Long id) {
        ResourceEntity resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(resource);
    }

    // Delete resource by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    public ResponseEntity<Void> deleteResourceById(@PathVariable Long id) {
        resourceService.deleteResourceById(id);
        return ResponseEntity.noContent().build();
    }

}
