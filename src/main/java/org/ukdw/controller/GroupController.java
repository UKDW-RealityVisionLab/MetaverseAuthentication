package org.ukdw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ukdw.dto.group.GroupDTO;
import org.ukdw.dto.group.GroupWithResourcesDTO;
import org.ukdw.dto.request.auth.GroupPermissionRequest;
import org.ukdw.dto.response.AppsCheckPermissionResponse;
import org.ukdw.dto.response.ResponseWrapper;
import org.ukdw.entity.GroupEntity;
import org.ukdw.services.GroupService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    // GET all groups
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        ResponseWrapper<List<GroupEntity>> response = new ResponseWrapper<>(HttpStatus.OK.value(), groupService.getAllGroups());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @GetMapping("/details")
    public ResponseEntity<?> getAllGroupsWithResources(){
        ResponseWrapper<List<GroupWithResourcesDTO>> response = new ResponseWrapper<>(HttpStatus.OK.value(), groupService.getAllGroupsWithResources());
        return ResponseEntity.ok(response);
    }

    // GET a group by ID
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getGroupById(@PathVariable(value = "id") long id) {
        GroupWithResourcesDTO group = groupService.getGroupById(id);
        ResponseWrapper<GroupWithResourcesDTO> response = new ResponseWrapper<>(HttpStatus.OK.value(), group);
        return ResponseEntity.ok(response);
    }

    // POST - Create a new group
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @ResponseBody
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupDTO request) {
        GroupEntity newGroup = new GroupEntity();
        newGroup.setGroupname(request.getGroupname().trim().toUpperCase());
        if(request.getPermission().isPresent()){
            newGroup.setPermission(request.getPermission().get());
        }

        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.CREATED.value(), "Success create new group",groupService.createGroup(newGroup));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT - Update a group by ID
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateGroup(@PathVariable(value = "id") Long id, @RequestBody GroupDTO updateRequest) {
        Optional<GroupEntity> updatedGroup = groupService.updateGroup(id, updateRequest);
        if (updatedGroup.isPresent()){
            return ResponseEntity.ok(updatedGroup);
        }else{
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(HttpStatus.BAD_REQUEST.value(), String.format("id:%s not found", id), null));
        }

    }

    // DELETE - Remove a group by ID
    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @ResponseBody
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable(value = "id") Long id) {
        boolean deleted = groupService.deleteGroup(id);
        if (deleted) {
            ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), String.format("id:%s delete success", id), "");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ResponseWrapper<>(HttpStatus.NOT_FOUND.value(), String.format("id:%s not found", id), null));
        }
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @ResponseBody
    @PostMapping(value = "/permission", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addGroupPermission(@Valid @RequestBody GroupPermissionRequest request){
        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), "Success add permission to group", groupService.addGroupPermission(request.getGroupId(), request.getPermission()));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @ResponseBody
    @DeleteMapping(value = "/permission", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeGroupPermission(@Valid @RequestBody GroupPermissionRequest request){
        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), "Success remove permission from group", groupService.removeGroupPermission(request.getGroupId(), request.getPermission()));
        return ResponseEntity.ok(response);
    }
}
