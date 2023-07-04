package com.jokes.provider;

public class RandomJokesException extends RuntimeException {

    public RandomJokesException(String message) {
        super(message);
    }

    public RandomJokesException(String message, Throwable cause) {
        super(message, cause);
    }

    public RandomJokesException(Throwable cause) {
        super(cause);
    }
}
