package com.chriniko.likecqrs.sample.post.control;

import com.chriniko.likecqrs.sample.core.CqrsEnvironment;
import com.chriniko.likecqrs.sample.core.Materialization;
import com.chriniko.likecqrs.sample.core.MaterializationType;
import com.chriniko.likecqrs.sample.core.ReadSide;
import com.chriniko.likecqrs.sample.error.ProcessingException;
import com.chriniko.likecqrs.sample.post.domain.Post;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Dependent
public class PostManager {

    private static final String TOPIC = "posts";

    private final CqrsEnvironment cqrsEnvironment;

    private final ObjectMapper mapper;

    @Inject
    public PostManager(@Materialization(MaterializationType.ELASTIC_SEARCH) CqrsEnvironment cqrsEnvironment, // Note: select here HEAP or ELASTIC_SEARCH
                       ObjectMapper mapper) {
        this.cqrsEnvironment = cqrsEnvironment;
        this.mapper = mapper;
        cqrsEnvironment.start();
    }

    public List<Post> findAll() {
        ReadSide readSide = cqrsEnvironment.readSide();

        Collection<JsonNode> records = readSide.findAll();

        return records
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private Post map(JsonNode document) {
        try {
            return mapper.readValue(mapper.writeValueAsString(document), Post.class);
        } catch (IOException e) {
            throw new ProcessingException(e);
        }
    }

    public void delete(String postId) {
        cqrsEnvironment
                .writeSide()
                .delete(
                        TOPIC,
                        postId)
                .join();
    }

    public void save(String key, JsonNode value) {
        cqrsEnvironment
                .writeSide()
                .insert(
                        TOPIC,
                        key,
                        value)
                .join();
    }
}
