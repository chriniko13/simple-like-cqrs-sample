package com.chriniko.likecqrs.sample.core;

public class DocumentMappingException extends RuntimeException {

    public DocumentMappingException(Throwable error) {
        super(error);
    }
}
