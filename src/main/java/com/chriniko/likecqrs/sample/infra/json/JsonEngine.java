package com.chriniko.likecqrs.sample.infra.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@ApplicationScoped
public class JsonEngine {

    @Singleton
    @Produces
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
