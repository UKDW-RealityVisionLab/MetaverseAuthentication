package org.ukdw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ukdw.dto.ResourceDTO;
import org.ukdw.entity.ResourceEntity;
import org.ukdw.exception.ResourceNotFoundException;
import org.ukdw.repository.ResourceRepository;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public List<ResourceEntity> getAllResources(){
        return resourceRepository.findAll();
    }

    public ResourceEntity getResourceById(Long resourceId){
        Optional<ResourceEntity> resourceOpt = resourceRepository.findById(resourceId);
        if(resourceOpt.isEmpty()){
            throw new ResourceNotFoundException("Resource id: "+ resourceId + " not found");
        }

        return resourceOpt.get();
    }

    public ResourceEntity createResource(ResourceDTO newResource){
        ResourceEntity resource = new ResourceEntity();
        resource.setResourceName(newResource.getResourceName().toUpperCase().trim());
        Long resBitmask = calculateBitmask(newResource.getResourceShift());
        resource.setResourceBitmask(resBitmask);

        return resourceRepository.save(resource);
    }

    public boolean deleteResourceById(Long resourceId){
        resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource id: " + resourceId + " not found"));

        resourceRepository.deleteById(resourceId);
        return true;
    }

    private Long calculateBitmask(Long bitmaskShift) {
        return (long) Math.pow(2, bitmaskShift);
    }

    /**
     * Load resources based on the permission bitmask dynamically from the database.
     * @param permission The bitmask representing the permissions.
     * @return A map of permission values to their respective names from the database.
     */
    public Map<Long, String> loadResourceNames(long permission) {
        List<ResourceEntity> allResources = resourceRepository.findAll();
        Map<Long, String> resources = new TreeMap<>();
        for (ResourceEntity resource : allResources) {
            long resourceBitmask = resource.getResourceBitmask();
            if ((permission & resourceBitmask) == resourceBitmask) {
                resources.put(resourceBitmask, resource.getResourceName());
            }
        }
        return resources;
    }

}
