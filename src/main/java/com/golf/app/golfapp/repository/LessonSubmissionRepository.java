package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.LessonSubmission;

import java.util.List;
import java.util.Optional;

public interface LessonSubmissionRepository {

    List<LessonSubmission> findAll();

    Optional<LessonSubmission> findById(Long id);

    void save(LessonSubmission lessonSubmission);
}