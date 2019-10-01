package com.chriniko.likecqrs.sample.post.boundary;

import com.chriniko.likecqrs.sample.error.ProcessingException;
import com.chriniko.likecqrs.sample.post.control.PostManager;
import com.chriniko.likecqrs.sample.post.domain.Post;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PostService {

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private PostManager postManager;

    public void update(Post post) {
        post.setUpdatedAt(Instant.now().toString());
        write(post);
    }

    public void save(Post post) {
        post.setId(UUID.randomUUID().toString());
        post.setCreatedAt(Instant.now().toString());
        write(post);
    }

    public void delete(String postId) {
        postManager.delete(postId);
    }

    public List<Post> findAll() {
        return postManager.findAll();
    }

    private void write(Post post) {
        postManager.save(post.getId(), map(post));
    }

    private JsonNode map(Post post) {
        try {
            return objectMapper.readTree(objectMapper.writeValueAsString(post));
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

}
