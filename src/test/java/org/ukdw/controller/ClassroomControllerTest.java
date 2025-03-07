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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.ukdw.dto.request.AddRemoveClassroomStudentRequest;
import org.ukdw.dto.request.AddRemoveClassroomTeacherRequest;
import org.ukdw.dto.request.FindUserByIdRequest;
import org.ukdw.dto.request.UpdateClassroomRequest;
import org.ukdw.dto.response.FindUserByIdResponse;
import org.ukdw.entity.ClassroomEntity;
import org.ukdw.entity.UserAccountEntity;
import org.ukdw.repository.AttendanceRecordRepository;
import org.ukdw.repository.AttendanceRepository;
import org.ukdw.repository.ClassroomRepository;
import org.ukdw.repository.UserAccountRepository;
import org.ukdw.services.AttendanceService;
import org.ukdw.services.ClassroomService;
import org.ukdw.services.PrivilegeVerifierService;
import org.ukdw.services.implementation.ClassroomServiceImpl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource(properties = "eureka.client.enabled=false")
@TestPropertySource(locations = "classpath:application.yml")
class ClassroomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ClassroomServiceImpl classroomService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @MockBean
    private PrivilegeVerifierService privilegeVerifierService;

    @MockBean
    private HttpServletRequest httpServletRequest;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String testTime = "2024-11-15 09:00:00 UTC";
    private final String testEndTime = "2024-11-16 09:00:00 UTC";

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        classroomRepository.deleteAll();

        // Reset H2 auto-increment sequence
        jdbcTemplate.execute("ALTER TABLE classroom ALTER COLUMN id RESTART WITH 1");

        when(privilegeVerifierService.hasPrivilege(eq("TEACHER,ADMIN"), anyLong(), anyLong())).thenReturn(true);
        when(privilegeVerifierService.hasPrivilege(eq("TEACHER,ADMIN,STUDENT"), anyLong(), anyLong(), anyLong())).thenReturn(true);
    }

    @ParameterizedTest
    @MethodSource("testCreateClassroomDataProvider")
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void testCreateClassroom(String className, String classDescription, String tahunAjaran, String semester, int expectedStatus) throws Exception {
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName(className);
        classroom.setDescription(classDescription);
        classroom.setTahunAjaran(tahunAjaran);
        classroom.setSemester(semester);

        String requestBody = objectMapper.writeValueAsString(classroom);

        mockMvc.perform(post("/classroom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void getAllClassroom() throws Exception {
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        ClassroomEntity classroom2 = new ClassroomEntity();
        classroom2.setName("Class B");
        classroom2.setDescription("This is Class B");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom2);

        mockMvc.perform(get("/classroom")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andDo(print());
    }


    @ParameterizedTest
    @MethodSource("getClassroomByIdDataProvider")
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void getClassroomById(Long classroomId, int expectedStatus, String expectedJsonKey, String expectedJsonValue) throws Exception {
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        ClassroomEntity classroom2 = new ClassroomEntity();
        classroom2.setName("Class B");
        classroom2.setDescription("This is Class B");
        classroom2.setTahunAjaran("2023/2024");
        classroom2.setSemester("1");
        classroom2.setTeacherIds(Set.of(1L));
        classroom2.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom2);

        mockMvc.perform(get("/classroom/" + classroomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$." + expectedJsonKey).value(expectedJsonValue))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateClassroom() throws Exception {
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        UpdateClassroomRequest updatedClassroom = new UpdateClassroomRequest();
        updatedClassroom.setName("Class C");

        String requestBody = objectMapper.writeValueAsString(updatedClassroom);

        mockMvc.perform(put("/classroom/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Class C"))
                .andDo(print());

    }

    @ParameterizedTest
    @MethodSource("deleteClassroomDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteClassroom(int classroomId, int expectedStatus) throws Exception {
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        mockMvc.perform(delete("/classroom/" + classroomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    /*@Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addTeacherToClassroom() throws Exception{
        // Mock classroom
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        /// Mock the response for checking if the user exists
        FindUserByIdRequest findUserByIdRequest = new FindUserByIdRequest(2L);
        FindUserByIdResponse userResponse = new FindUserByIdResponse();
        userResponse.setUserExist(true); // Simulate that the user exists

        // Mock the behavior of UserClient's findUserExistbyId method
        when(userClient.findUserExistbyId(eq(findUserByIdRequest), any(HttpServletRequest.class)))
                .thenReturn(userResponse); // Return the mocked response when the user exists

        AddRemoveClassroomTeacherRequest addTeacherRequest = new AddRemoveClassroomTeacherRequest();
        addTeacherRequest.setTeacherId(2L);

        String requestBody = objectMapper.writeValueAsString(addTeacherRequest);

        mockMvc.perform(post("/classroom/teacher/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }*/

    @ParameterizedTest
    @MethodSource("addTeacherToClassroomDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addTeacherToClassroom(Long teacherId, int expectedStatus, Boolean teacherUserExist) throws Exception {
        // Mock classroom
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L)); // Initially only teacher with ID 1L
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        // Mock the response for checking if the user exists
//        FindUserByIdRequest findUserByIdRequest = new FindUserByIdRequest(teacherId);
//        FindUserByIdResponse userResponse = new FindUserByIdResponse();
//        userResponse.setUserExist(teacherUserExist); // Simulate that the user exists

        // Mock the behavior of UserClient's findUserExistbyId method
//        when(classroomService.isUserExist(teacherId))
//                .thenReturn(teacherUserExist);
//        boolean testing = classroomService.isUserExist(teacherId);
//        when(userClient.findUserExistbyId(eq(findUserByIdRequest), any(HttpServletRequest.class)))
//                .thenReturn(userResponse); // Return the mocked response when the user exists

        AddRemoveClassroomTeacherRequest addTeacherRequest = new AddRemoveClassroomTeacherRequest();
        addTeacherRequest.setTeacherId(teacherId);

        String requestBody = objectMapper.writeValueAsString(addTeacherRequest);

        mockMvc.perform(post("/classroom/teacher/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus)) // Assert the expected status
                .andDo(print());
    }

    /*@Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void removeTeacherFromClassroom() throws Exception {
        // Mock classroom
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L,2L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        AddRemoveClassroomTeacherRequest addTeacherRequest = new AddRemoveClassroomTeacherRequest();
        addTeacherRequest.setTeacherId(2L);

        String requestBody = objectMapper.writeValueAsString(addTeacherRequest);

        mockMvc.perform(delete("/classroom/teacher/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());

    }*/

    @ParameterizedTest
    @MethodSource("removeTeacherFromClassroomDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void removeTeacherFromClassroom(Long teacherId, int expectedStatus) throws Exception {
        // Mock classroom
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L, 2L)); // Initial teacher set contains teacher with ID 1L and 2L
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        AddRemoveClassroomTeacherRequest addTeacherRequest = new AddRemoveClassroomTeacherRequest();
        addTeacherRequest.setTeacherId(teacherId);

        String requestBody = objectMapper.writeValueAsString(addTeacherRequest);

        mockMvc.perform(delete("/classroom/teacher/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus)) // Assert the expected status
                .andDo(print());
    }

    /*@Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addStudentToClassroom() throws Exception{
        // Mock classroom
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        /// Mock the response for checking if the user exists
        FindUserByIdRequest findUserByIdRequest = new FindUserByIdRequest(103L);
        FindUserByIdResponse userResponse = new FindUserByIdResponse();
        userResponse.setUserExist(true); // Simulate that the user exists

        // Mock the behavior of UserClient's findUserExistbyId method
        when(userClient.findUserExistbyId(eq(findUserByIdRequest), any(HttpServletRequest.class)))
                .thenReturn(userResponse); // Return the mocked response when the user exists

        AddRemoveClassroomStudentRequest addTeacherRequest = new AddRemoveClassroomStudentRequest();
        addTeacherRequest.setStudentId(103L);

        String requestBody = objectMapper.writeValueAsString(addTeacherRequest);

        mockMvc.perform(post("/classroom/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }*/

    @ParameterizedTest
    @MethodSource("addStudentToClassroomDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addStudentToClassroom(Long studentId, int expectedStatus) throws Exception {
        // Mock classroom
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        // Mock the response for checking if the student exists
        FindUserByIdRequest findUserByIdRequest = new FindUserByIdRequest(studentId);
        FindUserByIdResponse userResponse = new FindUserByIdResponse();
        userResponse.setUserExist(true); // Simulate that the student exists

        // Mock the behavior of UserClient's findUserExistbyId method
//        when(userClient.findUserExistbyId(eq(findUserByIdRequest), any(HttpServletRequest.class)))
//                .thenReturn(userResponse); // Return the mocked response when the student exists

        AddRemoveClassroomStudentRequest addStudentRequest = new AddRemoveClassroomStudentRequest();
        addStudentRequest.setStudentId(studentId);

        String requestBody = objectMapper.writeValueAsString(addStudentRequest);

        mockMvc.perform(post("/classroom/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus)) // Assert the expected status
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("removeStudentFromClassroomDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void removeStudentFromClassroom(Long studentId, int expectedStatus) throws Exception {
        // Mock classroom
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomService.createClassroom(classroom);

        AddRemoveClassroomStudentRequest addTeacherRequest = new AddRemoveClassroomStudentRequest();
        addTeacherRequest.setStudentId(studentId);

        String requestBody = objectMapper.writeValueAsString(addTeacherRequest);

        mockMvc.perform(delete("/classroom/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - student id <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> removeStudentFromClassroomDataProvider() {
        return Stream.of(
                // Successful login
                Arguments.of(102L, 200),
                // Student doesn't exist in classroom
                Arguments.of(103L, 400)
        );
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - student id <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> addStudentToClassroomDataProvider() {
        return Stream.of(
                // Successful addition of student to classroom
                Arguments.of(1L, 200),
                // Student already exists in classroom (or other failure cases)
                Arguments.of(101L, 400)
        );
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - teacher id <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> removeTeacherFromClassroomDataProvider() {
        return Stream.of(
                // Successfully remove teacher with ID 2L
                Arguments.of(2L, 200),
                // Try to remove teacher that does not exist in classroom (ID 3L)
                Arguments.of(3L, 400)
        );
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - teacher id <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> addTeacherToClassroomDataProvider() {
        return Stream.of(
                // Successfully add teacher with ID 2L
                Arguments.of(2L, 200, true),
                // Try to add teacher that already exists in classroom (ID 1L)
                Arguments.of(1L, 400, true),
                // Try to add teacher with ID 310L who doesn't exist
                Arguments.of(310L, 404, false)   // Assuming 400 for non-existent user
        );
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - classroom id <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> getClassroomByIdDataProvider() {
        return Stream.of(
                // Successfully get classroom id
                Arguments.of(1L, 200, "name", "Class A"),
                // Classroom id not found
                Arguments.of(100L, 404, "message", "Classroom not found with id:100")
        );
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - class name <br>
     * - class description <br>
     * - tahun ajaran (format: YYYY/YYYY) <br>
     * - semester (format string) <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> testCreateClassroomDataProvider() {
        return Stream.of(
                // Successfully create classroom
                Arguments.of("Class C", "This is Class C", "2023/2024", "1", 201)

                // Todo: ini diubah kalau sudah bikin create classroomDTO
                // Failed create classroom because classroom name is null
//                Arguments.of(null, "This is Class C", "2023/2024", "1", 200)
        );
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - classroom id <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> deleteClassroomDataProvider() {
        return Stream.of(
//                String className, String classDescription, String tahunAjaran, String semester, int expectedStatus
                // Successfully create classroom
                Arguments.of(1, 204),
                // Failed delete classroom because classroom doesn't exist
                Arguments.of(100, 404)
        );
    }
}