package com.golf.app.golfapp.mapper;

import com.golf.app.golfapp.model.Lesson;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LessonMapper {

    List<Lesson> findAll();

    Lesson findById(Long id);

    void insert(Lesson lesson);

    void update(Lesson lesson);

    void deleteById(Long id);

    List<Lesson> findByCategory(String category);
}