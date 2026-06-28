package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.LessonSubmission;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryLessonSubmissionRepository
        implements LessonSubmissionRepository {

    public final List<LessonSubmission> lessonSubmissions =
           new ArrayList<>();

    @Override
    public List<LessonSubmission> findAll() {
        return lessonSubmissions;
    }

    @Override
    public Optional<LessonSubmission> findById(Long id) {
        return lessonSubmissions.stream()
                .filter(submission -> submission.getId().equals(id))
                .findFirst();
    }

    @Override
    public void save(LessonSubmission lessonSubmission) {
        lessonSubmissions.add(lessonSubmission);
    }
}