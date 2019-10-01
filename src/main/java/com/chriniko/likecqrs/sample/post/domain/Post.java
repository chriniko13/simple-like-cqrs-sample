package com.chriniko.likecqrs.sample.post.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private String id;

    private String author;
    private String description;
    private String text;

    private String createdAt;
    private String updatedAt;
}
