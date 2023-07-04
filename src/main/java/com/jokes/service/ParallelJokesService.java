package com.jokes.service;

import com.jokes.model.Joke;
import com.jokes.provider.JokesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
// TODO rename service
public class ParallelJokesService implements JokesService {

    @Value("${jokes.default.count}")
    private int defaultCount;

    private final JokesProvider jokesProvider;

    @Autowired
    public ParallelJokesService(JokesProvider jokesProvider) {
        this.jokesProvider = jokesProvider;
    }

    @Override
    public List<Joke> load(Integer count) {
        count = count == null ? defaultCount : count;

        if (count == 1) {
            return List.of(jokesProvider.provideJoke());
        }

        return jokesProvider.provideJokes(count);
    }
}
