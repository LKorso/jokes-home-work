package com.jokes.service;

import com.jokes.model.Joke;

import java.util.List;

public interface JokesService {

    List<Joke> load(Integer count);

}
