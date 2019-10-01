package com.chriniko.likecqrs.sample.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.CompletableFuture;

public interface WriteSide {

    CompletableFuture<Long> delete(String collectionName, String key);

    CompletableFuture<Long> insert(String collectionName, String key, JsonNode value);
}
