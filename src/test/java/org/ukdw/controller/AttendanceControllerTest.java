package org.ukdw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.ukdw.dto.request.CreateAttendanceRequest;
import org.ukdw.dto.request.StudentAttendanceRequest;
import org.ukdw.dto.request.UpdateAttendanceRequest;
import org.ukdw.entity.AttendanceEntity;
import org.ukdw.entity.ClassroomEntity;
import org.ukdw.repository.AttendanceRecordRepository;
import org.ukdw.repository.AttendanceRepository;
import org.ukdw.repository.ClassroomRepository;
import org.ukdw.services.AttendanceService;
import org.ukdw.services.ClassroomService;
import org.ukdw.services.PrivilegeVerifierService;


import java.util.Set;
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
@TestPropertySource(properties = "eureka.client.enabled=false")
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @MockBean
    private PrivilegeVerifierService privilegeVerifierService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String testTime = "2024-11-15 09:00:00 UTC";
    private final String testEndTime = "2024-11-16 09:00:00 UTC";

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
//        classroomRepository.deleteAll();

        // Reset H2 auto-increment sequence
//        jdbcTemplate.execute("ALTER TABLE classroom ALTER COLUMN id RESTART WITH 1");

        // Reset H2 auto-increment sequence
        jdbcTemplate.execute("ALTER TABLE attendance ALTER COLUMN id RESTART WITH 1");

        when(privilegeVerifierService.hasPrivilege(eq("ADMIN,TEACHER"), anyLong(), anyLong())).thenReturn(true);
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN,TEACHER,STUDENT"), anyLong(), anyLong(), anyLong())).thenReturn(true);

        // Mock ClassroomEntity
//        ClassroomEntity classroom = new ClassroomEntity();
//        classroom.setId(1L);
//        classroom.setName("Class A");
//        classroom.setDescription("This is Class A");
//        classroom.setTahunAjaran("2023/2024");
//        classroom.setSemester("1");
//        classroom.setTeacherIds(Set.of(1L));
//        classroom.setStudentIds(Set.of(101L, 102L));
//        classroomRepository.save(classroom);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateAttendance() throws Exception {
        // Mock ClassroomEntity
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setId(1L);
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomRepository.save(classroom);

        CreateAttendanceRequest request = new CreateAttendanceRequest(1L, testTime, testEndTime);
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/attendances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("deleteAttendanceDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteAttendance(int classroomId, int expectedStatus) throws Exception {
        // Mock ClassroomEntity
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setId(1L);
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomRepository.save(classroom);

        AttendanceEntity attendance = attendanceService.createAttendance(1L, testTime, testEndTime);

        mockMvc.perform(delete("/attendances/" + classroomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testEditAttendance() throws Exception {
        // Mock ClassroomEntity
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setId(1L);
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomRepository.save(classroom);

        AttendanceEntity attendance = attendanceService.createAttendance(1L, testTime, testEndTime);

        String newTestTime = "2024-11-20 09:00:00 UTC";
        String newTestEndTime = "2024-11-22 09:00:00 UTC";
        UpdateAttendanceRequest request = new UpdateAttendanceRequest(newTestTime, newTestEndTime);

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/attendances/" + attendance.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void testGetAllAttendances() throws Exception {
        // Mock ClassroomEntity
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setId(1L);
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomRepository.save(classroom);

//        AttendanceEntity attendance = new AttendanceEntity();
//        attendance.setId(1L);
//        attendance.setClassroom(classroom);
//        attendance.setOpenTime(Instant.now());
//        attendance.setCloseTime(Instant.now().plusSeconds(60 * 60 * 24));
//        attendanceRepository.save(attendance);

        AttendanceEntity attendance = attendanceService.createAttendance(1L, testTime, testEndTime);

        mockMvc.perform(get("/attendances")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void testGetAttendanceById() throws Exception {
        // Mock ClassroomEntity
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setId(1L);
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomRepository.save(classroom);

        AttendanceEntity attendance = attendanceService.createAttendance(1L, testTime, testEndTime);

        mockMvc.perform(get("/attendances/" + attendance.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "teacher", roles = {"TEACHER"})
    void testSetStudentAttendanceRecord() throws Exception {
        ClassroomEntity classroom = new ClassroomEntity();
        classroom.setId(1L);
        classroom.setName("Class A");
        classroom.setDescription("This is Class A");
        classroom.setTahunAjaran("2023/2024");
        classroom.setSemester("1");
        classroom.setTeacherIds(Set.of(1L));
        classroom.setStudentIds(Set.of(101L, 102L));
        classroomRepository.save(classroom);

//        AttendanceEntity attendance = new AttendanceEntity();
//        attendance.setId(1L);
//        attendance.setClassroom(classroom);
//        attendance.setOpenTime(Instant.now());
//        attendance.setCloseTime(Instant.now());
//        attendanceRepository.save(attendance);
        AttendanceEntity attendance = attendanceService.createAttendance(1L, testTime, testEndTime);

        StudentAttendanceRequest request = new StudentAttendanceRequest();
        request.setStudentId(101L);
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/attendances/student/" + attendance.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - classroom id <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> deleteAttendanceDataProvider() {
        return Stream.of(
//                String className, String classDescription, String tahunAjaran, String semester, int expectedStatus
                // Successfully create classroom
                Arguments.of(1, 204),
                // Failed delete attendance because classroom doesn't exist
                Arguments.of(100, 404)
        );
    }
}
