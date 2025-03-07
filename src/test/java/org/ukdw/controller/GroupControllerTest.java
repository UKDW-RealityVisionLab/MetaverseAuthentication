package org.ukdw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.ukdw.dto.ResourceDTO;
import org.ukdw.dto.group.GroupDTO;
import org.ukdw.dto.request.auth.GroupPermissionRequest;
import org.ukdw.entity.GroupEntity;
import org.ukdw.repository.GroupRepository;
import org.ukdw.repository.ResourceRepository;
import org.ukdw.repository.UserAccountRepository;
import org.ukdw.services.GroupService;
import org.ukdw.services.PrivilegeVerifierService;
import org.ukdw.services.ResourceService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.yml")
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PrivilegeVerifierService privilegeVerifierService;

    @MockBean
    private HttpServletRequest httpServletRequest;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private GroupService groupService;

    @BeforeEach
    void setup(){
        userAccountRepository.deleteAll();
        resourceRepository.deleteAll();
        groupRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE groups ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllGroups() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        GroupEntity group1 = new GroupEntity();
        group1.setGroupname("Group Test 1");
        group1.setPermission(1L);
        groupService.createGroup(group1);

        GroupEntity group2 = new GroupEntity();
        group2.setGroupname("Group Test 2");
        group2.setPermission(2L);
        groupService.createGroup(group2);

        mockMvc.perform(get("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllGroupsWithResources() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        GroupEntity group1 = new GroupEntity();
        group1.setGroupname("Group Test 1");
        group1.setPermission(1L);
        groupService.createGroup(group1);

        GroupEntity group2 = new GroupEntity();
        group2.setGroupname("Group Test 2");
        group2.setPermission(2L);
        groupService.createGroup(group2);

        ResourceDTO resource = new ResourceDTO();
        resource.setResourceName("TEST_RESOURCE_1");
        resource.setResourceShift(0L);
        resourceService.createResource(resource);

        ResourceDTO resource1 = new ResourceDTO();
        resource1.setResourceName("TEST_RESOURCE_2");
        resource1.setResourceShift(1L);
        resourceService.createResource(resource1);

        mockMvc.perform(get("/groups/details")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getGroupById() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        GroupEntity group1 = new GroupEntity();
        group1.setGroupname("Group Test 1");
        group1.setPermission(1L);
        groupService.createGroup(group1);

        GroupEntity group2 = new GroupEntity();
        group2.setGroupname("Group Test 2");
        group2.setPermission(2L);
        groupService.createGroup(group2);

        ResourceDTO resource = new ResourceDTO();
        resource.setResourceName("TEST_RESOURCE_1");
        resource.setResourceShift(0L);
        resourceService.createResource(resource);

        mockMvc.perform(get("/groups/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createGroup() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        GroupDTO group = new GroupDTO();
        group.setGroupname("Test Group Name");
        group.setPermission(Optional.of(1L));

        String requestBody = objectMapper.writeValueAsString(group);
        mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().is(201))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateGroup() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        GroupEntity group1 = new GroupEntity();
        group1.setGroupname("Group Test 1");
        group1.setPermission(1L);
        groupService.createGroup(group1);

        GroupDTO group = new GroupDTO();
        group.setGroupname("Test Group Name");
        group.setPermission(Optional.of(1L));

        String requestBody = objectMapper.writeValueAsString(group);
        mockMvc.perform(put("/groups/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.groupname").value(group.getGroupname().toUpperCase()))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteGroup() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        GroupEntity group1 = new GroupEntity();
        group1.setGroupname("Group Test 1");
        group1.setPermission(1L);
        groupService.createGroup(group1);

        mockMvc.perform(delete("/groups/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addGroupPermission() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        GroupEntity group1 = new GroupEntity();
        group1.setGroupname("Group Test 1");
        group1.setPermission(1L);
        groupService.createGroup(group1);

        GroupPermissionRequest request = new GroupPermissionRequest();
        request.setGroupId(1L);
        request.setPermission(2L);

        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/groups/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().is(200))
                .andDo(print());

        // Fetch the group by ID
        Optional<GroupEntity> group = groupRepository.findById(1L);

        // Assertions
        assertTrue(group.isPresent(), "Group with ID 1 should exist in the repository.");
        assertEquals(3L, group.get().getPermission(), "Permission should be updated to 3 (1 | 2).");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void removeGroupPermission() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        // Create a group with permissions
        GroupEntity group = new GroupEntity();
        group.setGroupname("Group Test");
        group.setPermission(3L); // Initial permission is 3 (binary 011)
        groupService.createGroup(group);

        // Prepare request to remove permission
        GroupPermissionRequest request = new GroupPermissionRequest();
        request.setGroupId(1L);
        request.setPermission(2L); // Removing permission 2 (binary 010)

        String requestBody = objectMapper.writeValueAsString(request);

        // Perform POST request to remove permission
        mockMvc.perform(delete("/groups/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(200))
                .andDo(print());

        // Fetch the group by ID
        Optional<GroupEntity> updatedGroup = groupRepository.findById(1L);

        // Assertions
        assertTrue(updatedGroup.isPresent(), "Group with ID 1 should exist in the repository.");
        assertEquals(1L, updatedGroup.get().getPermission(),
                "Permission should be updated to 1 (3 & ~2 = 1).");
    }
}