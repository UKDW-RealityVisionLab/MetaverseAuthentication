package org.ukdw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.ukdw.AppMain;
import org.ukdw.dto.request.auth.*;
import org.ukdw.entity.CustomUserDetails;
import org.ukdw.entity.GroupEntity;
import org.ukdw.entity.UserAccountEntity;
import org.ukdw.repository.GroupRepository;
import org.ukdw.repository.UserAccountRepository;
import org.ukdw.services.*;

import java.text.ParseException;
import java.util.List;

import java.net.http.HttpHeaders;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "eureka.client.enabled=false")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserGroupService userGroupService;

    @MockBean
    private PrivilegeVerifierService privilegeVerifierService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() throws ParseException {
        userAccountRepository.deleteAll();
        groupRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE groups ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE user_account ALTER COLUMN id RESTART WITH 1");

        // Mock groups
        createInitialGroups();

        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.setEmail("testUser@mail.com");
        userAccountEntity.setUsername("testUser");
        userAccountEntity.setPassword("password123!");
        userAccountEntity.setRegNumber("T101");
        userAccountEntity.setScope("STUDENT");
        userAccountService.createUserAccount(userAccountEntity);
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
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("signinTestDataProvider")
    public void testSigninEndpoint(String email, String password, int expectedStatus, String expectedMessageKey, String expectedMessageValue) throws Exception {
        // Mock for test setup
        UserAccountEntity mockUser = new UserAccountEntity();
        mockUser.setEmail("student@example.com");
        mockUser.setPassword(passwordEncoder.encode("password123"));
        userAccountRepository.save(mockUser);

        when(jwtService.generateToken(any())).thenReturn("mockToken");
        when(jwtService.generateRefreshToken(any())).thenReturn("mockRefreshToken");

        // Create the payload
        SignInRequest loginRequest = new SignInRequest(email, password);

        // Convert payload to JSON string
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        // Perform POST request and validate response
        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$." + expectedMessageKey).value(expectedMessageValue))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("signupTestDataProvider")
    public void testSignupEndpoint(String username, String email, String password, String scope, int expectedStatus, String expectedMessageKey, String expectedMessageValue) throws Exception{
        // Create the payload
        SignUpRequest signupRequest = new SignUpRequest();
        signupRequest.setUsername(username);
        signupRequest.setEmail(email);
        signupRequest.setPassword(password);
        signupRequest.setScope(scope);

//        // Convert payload to JSON string
        String requestBody = objectMapper.writeValueAsString(signupRequest);

        // Perform POST request and validate response
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$." + expectedMessageKey).value(expectedMessageValue))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("refreshTokenTestDataProvider")
    public void testRefreshTokenEndpoint(String refreshToken, int expectedStatus, String expectedMessageKey, String expectedMessageValue) throws Exception{
        UserAccountEntity userAccountEntity = new UserAccountEntity();
        userAccountEntity.setEmail("test@mail.com");
        userAccountEntity.setUsername("test1");
        userAccountEntity.setPassword("password123!");
        userAccountEntity.setRegNumber("T001");
        userAccountEntity.setScope("STUDENT");
        UserAccountEntity userAccount = userAccountService.createUserAccount(userAccountEntity);
        userGroupService.addUserGroup(userAccount.getId(), 1L);

        when(jwtService.extractUserName(any())).thenReturn("test1");
        when(jwtService.generateToken(any())).thenReturn("newMockToken");
        when(jwtService.validateRefreshToken(eq("valid.refresh.token"), any())).thenReturn(true);


        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
        refreshTokenRequestDto.setRefreshToken(refreshToken);

        // Convert payload to JSON string
        String requestBody = objectMapper.writeValueAsString(refreshTokenRequestDto);

//         Perform POST request and validate response
        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$." + expectedMessageKey).value(expectedMessageValue))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("appsCheckPermissionTestDataProvider")
    public void testAppsCheckPermissionEndpoint(String accessToken, Long featureCode, int expectedStatus, String expectedMessageKey, String expectedMessageValue) throws Exception{
        when(jwtService.extractUserName(any())).thenReturn("testUser");

        // Find the user
        Optional<UserAccountEntity> userAccountOptional = userAccountRepository.findByUsername("testUser");
        assertTrue(userAccountOptional.isPresent(), "User should exist in the database"); // Ensure user exists
        UserAccountEntity userAccountEntity = userAccountOptional.get(); // Get the entity from Optional

        // Mock UserDetails because login session doesn't get stored in Security Context
        CustomUserDetails userDetails = new CustomUserDetails(userAccountEntity);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth); // Set mock authentication in SecurityContext

        // Create the payload
        AppsCheckPermissionRequest appsCheckPermissionRequest = new AppsCheckPermissionRequest();
        appsCheckPermissionRequest.setFeatureCode(featureCode);

        // Convert payload to JSON string
        String requestBody = objectMapper.writeValueAsString(appsCheckPermissionRequest);

        // Perform POST request and validate response
        mockMvc.perform(post("/auth/apps-check-permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$." + expectedMessageKey).value(expectedMessageValue))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("testCheckPermissionEndpointDataProvider")
    public void testCheckPermissionEndpoint(String accessToken, String role, Long permission, int expectedStatus, String expectedMessageKey, String expectedMessageValue) throws Exception{
        when(jwtService.extractUserName(any())).thenReturn("testUser");

        // Find the user (user is a STUDENT)
        Optional<UserAccountEntity> userAccountOptional = userAccountRepository.findByUsername("testUser");
        assertTrue(userAccountOptional.isPresent(), "User should exist in the database"); // Ensure user exists
        UserAccountEntity userAccountEntity = userAccountOptional.get(); // Get the entity from Optional

        // Mock UserDetails because login session doesn't get stored in Security Context
        CustomUserDetails userDetails = new CustomUserDetails(userAccountEntity);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth); // Set mock authentication in SecurityContext

        // Create the payload
        CheckPermissionRequest checkPermissionRequest = new CheckPermissionRequest();
        checkPermissionRequest.setRoles(new String[]{role});
        checkPermissionRequest.setPermissions(new Long[]{permission});

        // Convert payload to JSON string
        String requestBody = objectMapper.writeValueAsString(checkPermissionRequest);

        // Perform POST request and validate response
        mockMvc.perform(post("/auth/check-permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$." + expectedMessageKey).value(expectedMessageValue))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("verifyTestDataProvider")
    void testVerifyEndpoint(String accessToken, int expectedStatus, String expectedMessageKey, String expectedMessageValue) throws Exception {
        if(accessToken.equals("mockToken")){
            when(jwtService.extractUserName(any())).thenReturn("testUser");

            Claims claims = mock(Claims.class); // Mock claims
            when(jwtService.extractAllClaims(any())).thenReturn(claims);

            // Simulate valid claims
            when(claims.get("id")).thenReturn(1.0);
            when(claims.get("role")).thenReturn("ADMIN");
            when(claims.get("permission")).thenReturn(511.0);

            when(jwtService.isTokenExpired(any())).thenReturn(false);
        } else if (accessToken.equals("expiredMockToken")) {
            when(jwtService.extractUserName(any())).thenReturn("testUser");

            Claims claims = mock(Claims.class); // Mock claims
            when(jwtService.extractAllClaims(any())).thenReturn(claims);

            // Simulate valid claims
            when(claims.get("id")).thenReturn(1.0);
            when(claims.get("role")).thenReturn("ADMIN");
            when(claims.get("permission")).thenReturn(511.0);
            when(jwtService.isTokenExpired(any())).thenReturn(true);
        } else if (accessToken.equals("invalidMockToken")){
            when(jwtService.extractUserName(any())).thenThrow(new JwtException("Invalid token format"));
        }

        // Perform POST request and validate response
        mockMvc.perform(get("/auth/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
                )
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$." + expectedMessageKey).value(expectedMessageValue))
                .andDo(print());
    }


    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - email <br>
     * - password <br>
     * - expected HTTP status <br>
     * - expected JSON response key <br>
     * - expected JSON response value
     */
    private static Stream<Arguments> signinTestDataProvider() {
        return Stream.of(
                // Successful login
                Arguments.of("student@example.com", "password123", 200, "data.accessToken", "mockToken"),
                // Invalid credentials
                Arguments.of("invaliduser", "wrongpassword", 401, "data.message", "email or password is wrong. email :invaliduser"),
                // Empty email
                Arguments.of("", "password123", 400, "data.message", "email is required"),
                // Empty password
                Arguments.of("testuser", "", 400, "data.message", "password is required"),
                // Invalid email and password
                Arguments.of("unknown", "unknown", 401, "data.message", "email or password is wrong. email :unknown")
        );
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - username (required) <br>
     * - email (required) <br>
     * - password (required)<br>
     * - scope (required: 'student' / 'teacher') <br>
     * - firstname (optional) <br>
     * - nim/nid (optional) <br>
     * - expected HTTP status <br>
     * - expected JSON response key <br>
     * - expected JSON response value
     */
    private static Stream<Arguments> signupTestDataProvider() {
        return Stream.of(
                // Successful signup student
                Arguments.of("testStudent", "testStudent@ti.ukdw.ac.id", "password123", "student", 200, "message", "Signup success"),
                // Successful signup teacher
                Arguments.of("testTeacher", "testTeacher@staff.ukdw.ac.id", "password123", "teacher", 200, "message", "Signup success"),
                // Invalid username format
                Arguments.of("!-.1", "testStudent@ti.ukdw.ac.id", "password123", "student",400, "data.message", "Username must be 5-40 characters long, start with a letter, and only contain letters, numbers, underscores, or dots. It cannot start or end with an underscore or dot."),
                // Invalid email format
                Arguments.of("testuser", "user@test", "password123", "student", 400, "data.message", "Invalid email format. Make sure it's staff / ti email account"),
                // Invalid password length
                Arguments.of("testuser", "testuser@ti.ukdw.ac.id", "aaaa", "student",400, "data.message", "Password must be between 5 and 40 characters"),
                // Invalid scope (only accept student and teacher for scope param)
                Arguments.of("testuser", "testuser@ti.ukdw.ac.id", "password123", "scope", 400, "data.message", "Scope must be either 'student' or 'teacher'.")
        );
    }

    private static Stream<Arguments> refreshTokenTestDataProvider() {
        return Stream.of(
                //success refresh token
                Arguments.of("valid.refresh.token", 200, "data", "newMockToken"),
                //refresh token invalid
                Arguments.of("invalid.refresh.token", 401, "data", "Invalid refresh token")
        );
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - access token (required) <br>
     * - required feature code <br>
     * - expected HTTP status <br>
     * - expected JSON response key <br>
     * - expected JSON response value
     */
    private static Stream<Arguments> appsCheckPermissionTestDataProvider() {
        return Stream.of(
                // success check app permission and return true (have permission to access classroom)
                Arguments.of("mockToken", 1L, 200, "data.status", "true"),

                // success check app permission and return false (doesn't have permission to access classroom)
                Arguments.of("mockToken", 3L, 200, "data.status", "false"),

                // invalid check app permission due no feature code
                Arguments.of("mockToken", null, 400, "data.message", "Feature code is required")
        );
    }


    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - access token (required) <br>
     * - role (STUDENT / TEACHER / ADMIN)<br>
     * - permission value <br>
     * - expected HTTP status <br>
     * - expected JSON response key <br>
     * - expected JSON response value
     */
    private static Stream<Arguments> testCheckPermissionEndpointDataProvider() {
        return Stream.of(
                // success check permission and return TRUE (have permission to access classroom)
                Arguments.of("mockToken", "STUDENT", 1L, 200, "status", "true"),

                // success check permission and return FALSE (have permission to access certain Endpoint restricted to be accessed only to certain group)
                Arguments.of("mockToken", "ADMIN", 511L, 200, "status", "false"),

                // invalid check app permission due no role and permission value
                Arguments.of("mockToken", null, null, 200, "status", "false")
        );
    }

    private static Stream<Arguments> verifyTestDataProvider() {
        return Stream.of(
                // Valid token case
                Arguments.of("mockToken", 200, "message", "Token is valid"),
                // Expired token case
                Arguments.of("expiredMockToken", 401, "message", "Expired token"),
                // Invalid token case
                Arguments.of("invalidMockToken", 401, "message", "Invalid token format")
        );
    }

    private void createInitialGroups() {
        // Create and save GROUP records
        GroupEntity studentGroup = new GroupEntity();
        studentGroup.setGroupname("STUDENT");
        studentGroup.setPermission(1L);
        groupRepository.save(studentGroup);

        GroupEntity teacherGroup = new GroupEntity();
        teacherGroup.setGroupname("TEACHER");
        teacherGroup.setPermission(3L);
        groupRepository.save(teacherGroup);

        GroupEntity adminGroup = new GroupEntity();
        adminGroup.setGroupname("ADMIN");
        adminGroup.setPermission(65535L);
        groupRepository.save(adminGroup);
    }
}