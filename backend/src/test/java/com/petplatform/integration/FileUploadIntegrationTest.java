package com.petplatform.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petplatform.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileUploadIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("login user uploads image and file is stored locally")
    void uploadShouldStoreImageLocally() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "fake-image-content".getBytes()
        );

        MvcResult result = mockMvc.perform(multipart("/api/v1/files/upload")
                        .file(file)
                        .with(currentUser(2L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.file_name").value("avatar.png"))
                .andExpect(jsonPath("$.data.content_type").value("image/png"))
                .andReturn();

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        String url = (String) data.get("url");

        assertThat(url).startsWith("/uploads/");
        String[] pathSegments = url.substring("/uploads/".length()).split("/");
        Path savedFile = Path.of("target", "test-uploads", pathSegments);
        assertThat(savedFile).exists();
    }

    @Test
    @DisplayName("reject unsupported upload content type")
    void uploadShouldRejectUnsupportedContentType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "note.txt",
                "text/plain",
                "plain-text".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/files/upload")
                        .file(file)
                        .with(currentUser(2L)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(10001))
                .andExpect(jsonPath("$.message").value("仅支持 jpg、png、webp、gif 图片"));
    }
}
