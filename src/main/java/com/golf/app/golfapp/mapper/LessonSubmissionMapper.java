package com.golf.app.golfapp.mapper;

import com.golf.app.golfapp.model.LessonSubmission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LessonSubmissionMapper {

    List<LessonSubmission> findAll();

    LessonSubmission findById(Long id);

    void insert(LessonSubmission lessonSubmission);

    void updateFeedback(LessonSubmission lessonSubmission);
}