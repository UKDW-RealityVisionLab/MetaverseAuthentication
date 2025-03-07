package org.ukdw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.ukdw.dto.request.auth.UserPermissionRequest;
import org.ukdw.entity.GroupEntity;
import org.ukdw.entity.UserAccountEntity;
import org.ukdw.repository.GroupRepository;
import org.ukdw.repository.ResourceRepository;
import org.ukdw.repository.UserAccountRepository;
import org.ukdw.services.*;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
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
    private UserAccountService userAccountService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserGroupService userGroupService;

    @BeforeEach
    void setup(){
        userAccountRepository.deleteAll();
        resourceRepository.deleteAll();
        groupRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE groups ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE user_account ALTER COLUMN id RESTART WITH 1");

        GroupEntity group1 = new GroupEntity();
        group1.setPermission(1L);
        group1.setGroupname("TEST GROUP 1");
        groupRepository.save(group1);

        GroupEntity group2 = new GroupEntity();
        group2.setPermission(2L);
        group2.setGroupname("TEST GROUP 2");
        groupRepository.save(group2);

        UserAccountEntity user1 = new UserAccountEntity();
        user1.setEmail("test1@mail.com");
        user1.setUsername("test1");
        user1.setPassword("password");
        user1.setScope("student");
        user1.setRegNumber("T0001");
        userAccountRepository.save(user1);
        userGroupService.addUserGroup(1L, 1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllUsers() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.data.size()").value(1))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("getUserbyIdDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUserbyId(long userAccountId, int expectedStatus) throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        mockMvc.perform(get("/users/" + userAccountId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("isUserExistDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void isUserExist(long userAccountId, int expectedStatus) throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        mockMvc.perform(get("/users/exist/" + userAccountId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("addUserGroupDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addUserGroup(long userAccountId, long groupId, int expectedStatus) throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        UserPermissionRequest userPermissionRequest = new UserPermissionRequest();
        userPermissionRequest.setUserId(userAccountId);
        userPermissionRequest.setGroupId(groupId);

        String requestBody = objectMapper.writeValueAsString(userPermissionRequest);

        mockMvc.perform(post("/users/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("removeUserGroupDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void removeUserGroup(long userAccountId, long groupId, int expectedStatus) throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        UserPermissionRequest userPermissionRequest = new UserPermissionRequest();
        userPermissionRequest.setUserId(userAccountId);
        userPermissionRequest.setGroupId(groupId);

        String requestBody = objectMapper.writeValueAsString(userPermissionRequest);
        mockMvc.perform(delete("/users/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    private static Stream<Arguments> removeUserGroupDataProvider() {
        return Stream.of(
                // Successful remove user's group
                Arguments.of(1L, 1L, 200),
                // Failed remove user's group due to no group present in user
                Arguments.of(1L, 2L, 400),
                // Failed due to group id did not exist
                Arguments.of(1L, 100L, 404)
        );
    }

    private static Stream<Arguments> addUserGroupDataProvider() {
        return Stream.of(
                // Successful add user's group
                Arguments.of(1L, 2L, 200),
                // Failed to add user's group due to group present in user
                Arguments.of(1L, 1L, 400),
                // Failed due to group id did not exist
                Arguments.of(1L, 100L, 404)
        );
    }

    private static Stream<Arguments> getUserbyIdDataProvider() {
        return Stream.of(
                // Successful get user's by id
                Arguments.of(1L, 200),
                // Failed due to user id did not exist
                Arguments.of(100L, 404)
        );
    }

    private static Stream<Arguments> isUserExistDataProvider() {
        return Stream.of(
                // Successful get user's by id
                Arguments.of(1L,200),
                // Failed due to user id did not exist
                Arguments.of(10L, 404)
        );
    }
}