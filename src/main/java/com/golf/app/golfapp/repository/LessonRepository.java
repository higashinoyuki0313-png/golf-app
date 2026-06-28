package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.Lesson;

import java.util.List;
import java.util.Optional;

public interface LessonRepository {

    List<Lesson> findAll();

    Optional<Lesson> findById(Long id);

    List<Lesson> findByCategory(String category);

    void save(Lesson lesson);

    void update(Lesson lesson);

    void deleteById(Long id);


}
