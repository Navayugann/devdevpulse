package com.devpulse.devpulse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void testGetAllProjectsReturnsOkAndList() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testGetProjectByIdReturnsProject() throws Exception {
        Project saved = projectRepository.save(new Project("CI/CD Pipeline", "Automation pipeline", "Planned"));

        mockMvc.perform(get("/api/projects/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("CI/CD Pipeline"))
                .andExpect(jsonPath("$.status").value("Planned"));
    }

    @Test
    void testGetProjectByIdNotFoundReturns404() throws Exception {
        mockMvc.perform(get("/api/projects/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testCreateProjectSuccessReturns201() throws Exception {
        Project newProject = new Project("Kubernetes Cluster", "DevOps orchestration", "In Progress");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Kubernetes Cluster"))
                .andExpect(jsonPath("$.status").value("In Progress"));
    }

    @Test
    void testCreateProjectValidationFailsReturns400() throws Exception {
        Project invalidProject = new Project("", "Missing name", "");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProject)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.status").exists());
    }

    @Test
    void testUpdateProjectSuccessReturns200() throws Exception {
        Project saved = projectRepository.save(new Project("Old Name", "Old Desc", "Planned"));

        Project updatePayload = new Project("Updated Name", "Updated Desc", "Completed");

        mockMvc.perform(put("/api/projects/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.status").value("Completed"));
    }

    @Test
    void testDeleteProjectSuccessReturns204() throws Exception {
        Project saved = projectRepository.save(new Project("Temporary Project", "To be deleted", "Planned"));

        mockMvc.perform(delete("/api/projects/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify it was deleted
        mockMvc.perform(get("/api/projects/" + saved.getId()))
                .andExpect(status().isNotFound());
    }
}
