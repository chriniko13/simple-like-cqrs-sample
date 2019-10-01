package com.chriniko.likecqrs.sample.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Dependent
@Named("heap")
public class HeapReadSide implements ReadSide {

    private final ConcurrentHashMap<String, JsonNode> documentsById;

    @Inject
    public HeapReadSide() {
        documentsById = new ConcurrentHashMap<>();
    }

    @Override
    public String indexName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String type() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<JsonNode> findAll() {
        return new ArrayList<>(documentsById.values()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void upsert(long timestamp, String key, JsonNode payload) {
        if (NullNode.getInstance().equals(payload) || payload == null) {
            documentsById.remove(key);
        } else {
            documentsById.put(key, payload);
        }
    }

}
