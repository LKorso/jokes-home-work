package com.jokes.web;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JokesControllerTest {

    @Value("${jokes.uri.random}")
    private String singleJokeUri;

    @Autowired
    private MockMvc mvc;

    @RegisterExtension
    private static final WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void setUp(DynamicPropertyRegistry registry) {
        registry.add("jokes.uri.base", () -> "http://localhost:" + wireMockExtension.getPort());
    }

    @Test
    void jokesReturnsOneJoke() throws Exception {
        var jokeJson = "{\"type\":\"general\",\"setup\":\"How do locomotives know where they're going?\",\"punchline\":\"Lots of training\",\"id\":120}";
        configureFor(wireMockExtension.getPort());
        stubFor(WireMock.get(singleJokeUri).willReturn(okJson(jokeJson)));

        mvc.perform(get("/jokes?count=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].setup", is("How do locomotives know where they're going?")))
                .andExpect(jsonPath("$[0].punchline", is("Lots of training")));
    }
}