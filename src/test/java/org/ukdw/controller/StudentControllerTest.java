package org.ukdw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.ukdw.entity.StudentEntity;
import org.ukdw.repository.StudentRepository;
import org.ukdw.services.PrivilegeVerifierService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "eureka.client.enabled=false")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @MockBean
    private PrivilegeVerifierService privilegeVerifierService; // Mock the PrivilegeVerifierService

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll(); // Clear the database before each test

        // Mock privilege verification
        when(privilegeVerifierService.hasPrivilege(anyString(), anyLong(), anyLong())).thenReturn(true);

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllStudents() throws Exception {
        StudentEntity student1 = new StudentEntity();
        student1.setUserId(1);
        student1.setFirstName("John");
        StudentEntity student2 = new StudentEntity();
        student2.setUserId(2);
        student2.setFirstName("Jane");
        studentRepository.saveAll(List.of(student1, student2));

        mockMvc.perform(get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetStudentById() throws Exception {
        StudentEntity student1 = new StudentEntity();
        student1.setUserId(1);
        student1.setFirstName("John");
        StudentEntity student2 = new StudentEntity();
        student2.setUserId(2);
        student2.setFirstName("Jane");
        studentRepository.saveAll(List.of(student1, student2));

        mockMvc.perform(get("/students/" + student1.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateStudent() throws Exception {
        StudentEntity student1 = new StudentEntity();
        student1.setUserId(1);
        student1.setFirstName("Susan");
        String requestBody = objectMapper.writeValueAsString(student1);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Susan"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateStudent() throws Exception {
        StudentEntity student1 = new StudentEntity();
        student1.setUserId(1);
        student1.setFirstName("Susan");
        studentRepository.save(student1);

        StudentEntity updatedStudent = new StudentEntity();
        updatedStudent.setFirstName("Jane");

        String requestBody = objectMapper.writeValueAsString(updatedStudent);

        mockMvc.perform(put("/students/" + student1.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andDo(print());
    }
}
