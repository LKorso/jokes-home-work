package com.jokes.provider;

import com.jokes.model.Joke;

import java.util.List;

public interface JokesProvider {

    Joke provideJoke();

    List<Joke> provideJokes(Integer count);

}
