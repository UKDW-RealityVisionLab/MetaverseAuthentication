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
import org.ukdw.dto.ResourceDTO;
import org.ukdw.repository.ResourceRepository;
import org.ukdw.services.PrivilegeVerifierService;
import org.ukdw.services.ResourceService;

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
@TestPropertySource(locations = "classpath:application.yml")
class ResourceControllerTest {

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
    private ResourceService resourceService;

    @BeforeEach
    void setup(){
        resourceRepository.deleteAll();
        // Reset H2 auto-increment sequence
        jdbcTemplate.execute("ALTER TABLE resource ALTER COLUMN id RESTART WITH 1");
    }

    @ParameterizedTest
    @MethodSource("createResourceDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createResource(String resourceName, Long resourceShift, int expectedStatus) throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        ResourceDTO resource = new ResourceDTO();
        resource.setResourceName(resourceName);
        resource.setResourceShift(resourceShift);

        String requestBody = objectMapper.writeValueAsString(resource);
        mockMvc.perform(post("/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllResourcesAsAdmin() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        ResourceDTO resource = new ResourceDTO();
        resource.setResourceName("TEST_RESOURCE_1");
        resource.setResourceShift(1L);
        resourceService.createResource(resource);

        ResourceDTO resource1 = new ResourceDTO();
        resource1.setResourceName("TEST_RESOURCE_2");
        resource1.setResourceShift(2L);
        resourceService.createResource(resource1);

        mockMvc.perform(get("/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.size()").value(2))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "student", roles = {"STUDENT"})
    void shouldFailWhenGetAllResourcesAsStudent() throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("STUDENT"), anyLong())).thenReturn(false);

        ResourceDTO resource = new ResourceDTO();
        resource.setResourceName("TEST_RESOURCE");
        resource.setResourceShift(1L);
        resourceService.createResource(resource);

        mockMvc.perform(get("/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(403))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("getResourceByIdDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getResourceById(int resourceId, int expectedStatus) throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        ResourceDTO resource = new ResourceDTO();
        resource.setResourceName("TEST_RESOURCE");
        resource.setResourceShift(1L);
        resourceService.createResource(resource);

        mockMvc.perform(get("/resources/"+resourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("deleteResourceByIdDataProvider")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteResourceById(int resourceId, int expectedStatus) throws Exception {
        // Mocking check privilege
        when(privilegeVerifierService.hasPrivilege(eq("ADMIN"), anyLong())).thenReturn(true);

        ResourceDTO resource = new ResourceDTO();
        resource.setResourceName("TEST_RESOURCE");
        resource.setResourceShift(1L);
        resourceService.createResource(resource);

        mockMvc.perform(delete("/resources/"+resourceId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(expectedStatus))
                .andDo(print());
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - resource name <br>
     * - resource bitshift <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> createResourceDataProvider() {
        return Stream.of(
                // Successfully create resource
                Arguments.of("TEST_RESOURCE", 1L, 201),
                // Failed create resource because no resource name
                Arguments.of(null, 1L, 400),
                // Failed create resource because no resource bitshift
                Arguments.of("TEST_RESOURCE", null, 400)
        );
    }


    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - resource id <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> getResourceByIdDataProvider() {
        return Stream.of(
                // Successfully get resource by id
                Arguments.of(1, 200),
                // Failed get resource by id
                Arguments.of(100, 404)
        );
    }

    /**
     * Data provider for parameterized test cases. <br>
     * Each entry contains: <br>
     * - resource id <br>
     * - expected HTTP status code <br>
     */
    private static Stream<Arguments> deleteResourceByIdDataProvider() {
        return Stream.of(
                // Successfully get resource by id
                Arguments.of(1, 204),
                // Failed get resource by id
                Arguments.of(100, 404)
        );
    }
}