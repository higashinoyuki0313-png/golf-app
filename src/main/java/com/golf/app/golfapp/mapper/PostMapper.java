package com.golf.app.golfapp.mapper;

import com.golf.app.golfapp.model.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {

    List<Post> findAll();

    Post findById(Long id);

    void insert(Post post);

    void update(Post post);

    void deleteById(Long id);
}