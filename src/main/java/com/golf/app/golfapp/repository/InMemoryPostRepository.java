package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryPostRepository implements PostRepository {

    private final List<Post> posts = new ArrayList<>();

    public InMemoryPostRepository() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setTitle("ドライバー改善");
        post1.setContent("アドレスを見直しましょう");
        post1.setCategory("distance");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("アプローチ練習");
        post2.setContent("距離感を意識しましょう");
        post2.setCategory("shortgame");

        posts.add(post1);
        posts.add(post2);
    }

    @Override
    public List<Post> findAll() {
        return posts;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst();
    }

    @Override
    public void save(Post post) {
        posts.add(post);
    }

    @Override
    public void update(Post post){
        findById(post.getId()).ifPresent(existingPost -> {
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());
            existingPost.setImage(post.getImage());
            existingPost.setCause(post.getCause());
            existingPost.setImprovement(post.getImprovement());
            existingPost.setPractice(post.getPractice());
            existingPost.setCategory(post.getCategory());
        });

    }

    @Override
    public void deleteById(Long id){
        posts.removeIf(post -> post.getId().equals(id));
    }
}