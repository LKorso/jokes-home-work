package com.jokes.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jokes.model.Joke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Service
public class RandomJokesProvider implements JokesProvider {

    @Value("${jokes.uri.base}")
    private String baseUrl;
    @Value("${jokes.uri.random}")
    private String singleJokeUrl;
    @Value("${jokes.uri.ten}")
    private String tenJokesUrl;

    private final HttpClient webClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public RandomJokesProvider(HttpClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Joke provideJoke() {
        var request = HttpRequest.newBuilder(URI.create(baseUrl + singleJokeUrl)).GET().build();
        try {
            var response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                return objectMapper.readerFor(Joke.class).readValue(response.body());
            } else {
                throw new RandomJokesException(
                        String.format(
                                "Failed to get response from: %s , with status code: %d and response body: %s",
                                singleJokeUrl, response.statusCode(), response.body()
                        )
                );
            }
        } catch (IOException | InterruptedException e) {
            throw new RandomJokesException(String.format("Failed to get response from: %s", singleJokeUrl), e);
        }
    }

    @Override
    public List<Joke> provideJokes(Integer count) {
        var numberOfTasks = defineNumberOfTasks(count);
        return IntStream.rangeClosed(1, numberOfTasks)
                .parallel()
                .mapToObj(i -> CompletableFuture.supplyAsync(this::provideTenJokes))
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .limit(count)
                .toList();
    }

    private List<Joke> provideTenJokes() {
        var request = HttpRequest.newBuilder(URI.create(baseUrl + tenJokesUrl)).GET().build();
        try {
            var response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                return objectMapper.readerForListOf(Joke.class).readValue(response.body());
            } else {
                throw new RandomJokesException(
                        String.format(
                                "Failed to get response from: %s , with status code: %d and response body: %s",
                                tenJokesUrl, response.statusCode(), response.body()
                        )
                );
            }
        } catch (IOException | InterruptedException e) {
            throw new RandomJokesException(String.format("Failed to get response from: %s", tenJokesUrl), e);
        }
    }

    private int defineNumberOfTasks(Integer count) {
        var result = count / 10;
        if (count % 10 > 0) {
            result += 1;
        }
        return result;
    }
}
