package com.jokes.web;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebMvcTest(JokesController.class)
class JokesControllerTest {

    @Value("${random.jokes.uri.random}")
    private String singleJokeUri;

    @Autowired
    private MockMvc mvc;

    @RegisterExtension
    private static final WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void setUp(DynamicPropertyRegistry registry) {
        registry.add("random.jokes.uri.base", () -> "http://localhost:" + wireMockExtension.getPort());
    }

    @Test
    void jokesReturnsOneJoke() throws Exception {
        var jokeJson = "{\"type\":\"general\",\"setup\":\"How do locomotives know where they're going?\",\"punchline\":\"Lots of training\",\"id\":120}";
        configureFor(wireMockExtension.getPort());
        stubFor(WireMock.get(singleJokeUri).willReturn(okJson(jokeJson)));

        mvc.perform(get("/jokes?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$setup").value("How do locomotives know where they're going?"))
                .andExpect(jsonPath("punchline").value("Lots of training"));
    }
}