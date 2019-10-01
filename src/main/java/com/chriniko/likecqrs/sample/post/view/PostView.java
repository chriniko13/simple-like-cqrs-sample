package com.chriniko.likecqrs.sample.post.view;

import com.chriniko.likecqrs.sample.infra.jsf.JsfEngine;
import com.chriniko.likecqrs.sample.infra.logging.InfoLevel;
import com.chriniko.likecqrs.sample.post.boundary.PostService;
import com.chriniko.likecqrs.sample.post.domain.Post;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

@Named
@ViewScoped
public class PostView implements Serializable {

    @Inject
    @InfoLevel
    private Consumer<String> log;

    @Inject
    private PostService postService;

    @Inject
    private JsfEngine jsfEngine;

    @Getter
    private Post newPost;

    @Getter
    @Setter
    private List<Post> posts;

    @Getter
    @Setter
    private Post selectedPost;

    @PostConstruct
    public void init() {
        newPost = new Post();
        posts = postService.findAll();
    }

    public void refresh() {
        posts = postService.findAll();
        jsfEngine.displayMessage("Posts datatable refreshed successfully!");
    }

    public void insertNewPost() {

        postService.save(newPost);

        posts = postService.findAll();

        jsfEngine.displayMessage("Post with description: " + newPost.getDescription() + " saved successfully!");
    }

    public void deleteSelectedPost() {
        if (postIsNotSelected()) {
            return;
        }

        postService.delete(selectedPost.getId());

        posts = postService.findAll();

        jsfEngine.displayMessage("Post with id: " + selectedPost.getId() + " deleted successfully!");

        selectedPost = null;
    }

    public void updateSelectedPost() {
        if (postIsNotSelected()) {
            return;
        }

        postService.update(selectedPost);

        posts = postService.findAll();

        jsfEngine.displayMessage("Post with id: " + selectedPost.getId() + " updated successfully!");

        selectedPost = null;
    }

    private boolean postIsNotSelected() {
        if (selectedPost == null) {
            jsfEngine.displayWarnMessage("Please first select a post!");
            return true;
        }
        return false;
    }

}
