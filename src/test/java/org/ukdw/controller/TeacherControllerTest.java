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
import org.ukdw.entity.TeacherEntity;
import org.ukdw.repository.TeacherRepository;
import org.ukdw.services.PrivilegeVerifierService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
//@TestPropertySource(locations = "classpath:application-test.properties")
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    @MockBean
    private PrivilegeVerifierService privilegeVerifierService; // Mock the PrivilegeVerifierService

    @BeforeEach
    void setUp() {
        teacherRepository.deleteAll(); // Clear the database before each test

        // Mock privilege verification
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN,TEACHER"), anyLong(), anyLong())).thenReturn(true);

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllTeachers() throws Exception {
        TeacherEntity teacher1 = new TeacherEntity(1L, "John", "Doe", "NID123", "1234567890", "Address 1", "City1", "Region1", "Country1", "12345", "Male", "Scholar1");
        TeacherEntity teacher2 = new TeacherEntity(2L, "Jane", "Doe", "NID456", "0987654321", "Address 2", "City2", "Region2", "Country2", "54321", "Female", "Scholar2");
        teacherRepository.saveAll(List.of(teacher1, teacher2));

        mockMvc.perform(get("/teachers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetTeacherById() throws Exception {
        TeacherEntity teacher = teacherRepository.save(new TeacherEntity(1L, "John", "Doe", "NID123", "1234567890", "Address 1", "City1", "Region1", "Country1", "12345", "Male", "Scholar1"));

        mockMvc.perform(get("/teachers/" + teacher.getUserId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateTeacher() throws Exception {
        TeacherEntity teacher = new TeacherEntity(1L, "John", "Doe", "NID123", "1234567890", "Address 1", "City1", "Region1", "Country1", "12345", "Male", "Scholar1");
        String requestBody = objectMapper.writeValueAsString(teacher);

        mockMvc.perform(post("/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateTeacher() throws Exception {
        TeacherEntity teacher = teacherRepository.save(new TeacherEntity(1L, "John", "Doe", "NID123", "1234567890", "Address 1", "City1", "Region1", "Country1", "12345", "Male", "Scholar1"));
        TeacherEntity updatedTeacher = new TeacherEntity(1L, "Jane", "Doe", "NID456", "0987654321", "Address 2", "City2", "Region2", "Country2", "54321", "Female", "Scholar2");
        String requestBody = objectMapper.writeValueAsString(updatedTeacher);

        mockMvc.perform(put("/teachers/" + teacher.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andDo(print());
    }
}

