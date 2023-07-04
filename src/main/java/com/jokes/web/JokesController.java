package com.jokes.web;

import com.jokes.model.Joke;
import com.jokes.service.JokesService;
import jakarta.validation.constraints.Max;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class JokesController {

    private final JokesService jokesService;

    @Autowired
    public JokesController(JokesService jokesService) {
        this.jokesService = jokesService;
    }

    @GetMapping("/jokes")
    public List<Joke> jokes(@PathParam("count") @Max(value = 100, message = "Max number of jokes at a time is 100") Integer count) {
        return jokesService.load(count);
    }
}
