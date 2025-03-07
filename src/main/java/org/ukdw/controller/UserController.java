package org.ukdw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.ukdw.dto.request.auth.GroupPermissionRequest;
import org.ukdw.dto.request.auth.SignUpRequest;
import org.ukdw.dto.request.auth.UserPermissionRequest;
import org.ukdw.dto.response.ResponseWrapper;
import org.ukdw.entity.UserAccountEntity;
import org.ukdw.repository.UserAccountRepository;
import org.ukdw.services.UserAccountService;
import org.ukdw.services.UserGroupService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserGroupService userGroupService;
    private final UserAccountRepository userAccountRepository;

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers(){
        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), userAccountRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserbyId( @PathVariable(value = "id") Long id){
        Optional<UserAccountEntity> user = userAccountRepository.findById(id);
        if(user.isEmpty()){
            ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.NOT_FOUND.value(), "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(response);
        }else{
            ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), user.get());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping(value = "exist/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> isUserExist(@PathVariable(value = "id") Long id){

        Optional<UserAccountEntity> user = userAccountRepository.findById(id);
        if(user.isEmpty()){
            ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.NOT_FOUND.value(), "User not found", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(response);
        }else{
            ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), "User exist", true);
            return ResponseEntity.ok(response);
        }
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @ResponseBody
    @PostMapping(value = "/permission", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addUserGroup(@Valid @RequestBody UserPermissionRequest request){
        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), "Success adding group to user", userGroupService.addUserGroup(request.getUserId(), request.getGroupId()));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@privilegeVerifierService.hasPrivilege('ADMIN', 511L)")
    @ResponseBody
    @DeleteMapping(value = "/permission", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeUserGroup(@Valid @RequestBody UserPermissionRequest request){
        ResponseWrapper<?> response = new ResponseWrapper<>(HttpStatus.OK.value(), "Success removed group from user",userGroupService.removeUserGroup(request.getUserId(), request.getGroupId()));
        return ResponseEntity.ok(response);
    }
}
