package com.chriniko.likecqrs.sample.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;

public interface ReadSide {

    String indexName();

    String type();

    Collection<JsonNode> findAll();

    void upsert(long timestamp, String key, JsonNode payload);
}
