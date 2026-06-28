package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    List<Post> findAll();

    Optional<Post> findById(Long id);

    void save(Post post);

    void update(Post post);

    void deleteById(Long id);
}