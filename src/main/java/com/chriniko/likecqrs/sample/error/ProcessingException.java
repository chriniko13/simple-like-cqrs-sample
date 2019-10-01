package com.chriniko.likecqrs.sample.error;

public class ProcessingException extends RuntimeException {

    public ProcessingException(Throwable error) {
        super(error);
    }

}
